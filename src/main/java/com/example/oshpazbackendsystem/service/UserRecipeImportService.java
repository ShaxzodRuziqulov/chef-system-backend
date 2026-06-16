package com.example.oshpazbackendsystem.service;

import com.example.oshpazbackendsystem.dto.*;
import com.example.oshpazbackendsystem.dto.BulkImportResultDto.RowResult;
import com.example.oshpazbackendsystem.entity.*;
import com.example.oshpazbackendsystem.entity.enums.DifficultyLevel;
import com.example.oshpazbackendsystem.entity.enums.MeasurementUnit;
import com.example.oshpazbackendsystem.entity.enums.Role;
import com.example.oshpazbackendsystem.repository.CategoryRepository;
import com.example.oshpazbackendsystem.repository.IngredientRepository;
import com.example.oshpazbackendsystem.repository.RecipeRepository;
import com.example.oshpazbackendsystem.repository.TagRepository;
import com.example.oshpazbackendsystem.service.security.CurrentUserService;
import com.example.oshpazbackendsystem.util.LocalizedValueParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.Hibernate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 3-varaqli Excel format: Sheet1=Retseptlar, Sheet2=Ingredientlar, Sheet3=Bosqichlar.
 * <p>
 * Sheet 1 ustunlari (18 ta):
 * 0=title_uz*    1=title_ru   2=title_eng   3=tavsif     4=kategoriya
 * 5=qiyinlik     6=prep*      7=cook*        8=porsiya*   9=teglar
 * 10=youtube_url 11=kaloriya  12=oqsil_g    13=yog_g     14=uglevodlar_g
 * 15=tolalar_g   16=shakar_g  17=natriy_mg
 * <p>
 * Import:  har bir satr alohida tranzaksiyada (RecipeService.create/update orqali).
 * Export:  @Transactional(readOnly=true) — lazy kolleksiyalar ochiladi.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserRecipeImportService {

    private final RecipeService recipeService;
    private final CurrentUserService currentUserService;
    private final CategoryRepository categoryRepository;
    private final IngredientRepository ingredientRepository;
    private final RecipeRepository recipeRepository;
    private final TagRepository tagRepository;

    // ─────────────────────────────────────────────────────────────────────────
    // Import
    // ─────────────────────────────────────────────────────────────────────────

    public BulkImportResultDto importFromExcel(MultipartFile file) throws IOException {
        return importFromExcel(file, "SKIP");
    }

    public BulkImportResultDto importFromExcel(MultipartFile file, String mode) throws IOException {

        boolean doUpdate = "UPDATE".equalsIgnoreCase(mode);

        if (doUpdate) {
            User caller = currentUserService.getCurrentUser();
            if (caller.getRole() != Role.ADMIN)
                throw new AccessDeniedException("mode=UPDATE faqat ADMIN uchun ruxsat etilgan");
        }

        List<RowResult> results = new ArrayList<>();
        int successCount = 0, updatedCount = 0, skippedCount = 0, failedCount = 0;

        try (Workbook wb = new XSSFWorkbook(file.getInputStream())) {

            if (wb.getNumberOfSheets() < 1)
                throw new IllegalArgumentException("Excel fayl kamida 1 ta varaq bo'lishi kerak");

            Sheet recipeSheet = wb.getSheetAt(0);

            Map<String, List<RecipeIngredientRequest>> ingredientsMap =
                    wb.getNumberOfSheets() >= 2 ? readIngredients(wb.getSheetAt(1)) : Collections.emptyMap();

            Map<String, List<RecipeStepRequest>> stepsMap =
                    wb.getNumberOfSheets() >= 3 ? readSteps(wb.getSheetAt(2)) : Collections.emptyMap();

            int lastRow = recipeSheet.getLastRowNum();

            for (int i = 1; i <= lastRow; i++) {
                Row row = recipeSheet.getRow(i);
                if (row == null || isRowEmpty(row)) continue;

                String titleUz = cellStr(row, 0);
                if (titleUz == null || titleUz.isBlank()) continue;

                var existing = recipeRepository.findFirstByTitleUzIgnoreCaseAndDeletedFalse(titleUz.trim());

                if (existing.isPresent()) {
                    if (doUpdate) {
                        try {
                            recipeService.update(existing.get().getId(), buildUpdateRequest(row, ingredientsMap, stepsMap));
                            updatedCount++;
                            results.add(RowResult.builder().row(i + 1).status("UPDATED").titleUz(titleUz).build());
                        } catch (Exception e) {
                            failedCount++;
                            log.warn("Import UPDATE row {} xatosi: {}", i + 1, e.getMessage());
                            results.add(RowResult.builder().row(i + 1).status("ERROR").titleUz(titleUz).error(e.getMessage()).build());
                        }
                    } else {
                        skippedCount++;
                        results.add(RowResult.builder().row(i + 1).status("SKIPPED").titleUz(titleUz)
                                .error("Bunday nomli retsept allaqachon mavjud").build());
                    }
                } else {
                    try {
                        recipeService.create(buildRequest(row, ingredientsMap, stepsMap));
                        successCount++;
                        results.add(RowResult.builder().row(i + 1).status("SUCCESS").titleUz(titleUz).build());
                    } catch (Exception e) {
                        failedCount++;
                        log.warn("Import CREATE row {} xatosi: {}", i + 1, e.getMessage());
                        results.add(RowResult.builder().row(i + 1).status("ERROR").titleUz(titleUz).error(e.getMessage()).build());
                    }
                }
            }
        }

        return BulkImportResultDto.builder()
                .totalRows(successCount + updatedCount + skippedCount + failedCount)
                .successCount(successCount)
                .updatedCount(updatedCount)
                .skippedCount(skippedCount)
                .failedCount(failedCount)
                .results(results)
                .build();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Export
    // ─────────────────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public byte[] exportToExcel() throws IOException {
        User caller = currentUserService.getCurrentUser();
        boolean isAdmin = caller.getRole() == Role.ADMIN;

        List<Recipe> recipes = isAdmin
                ? recipeRepository.findAllByDeletedFalseOrderByCreatedAtAsc()
                : recipeRepository.findAllByAuthorIdAndDeletedFalse(caller.getId());

        for (Recipe r : recipes) {
            Hibernate.initialize(r.getIngredients());
            r.getIngredients().forEach(ri -> Hibernate.initialize(ri.getIngredient()));
            Hibernate.initialize(r.getSteps());
            Hibernate.initialize(r.getTags());
            if (r.getCategory() != null) Hibernate.initialize(r.getCategory());
            if (r.getNutritionalInfo() != null) Hibernate.initialize(r.getNutritionalInfo());
        }

        return buildExportWorkbook(recipes);
    }

    private byte[] buildExportWorkbook(List<Recipe> recipes) throws IOException {
        try (Workbook wb = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            CellStyle hdr = makeHeaderStyle(wb);
            CellStyle data = makeDataStyle(wb);

            // ── Sheet 1: Retseptlar ─────────────────────────────────────────
            Sheet s1 = wb.createSheet("Retseptlar");
            writeHeader(s1, new String[]{
                    "title_uz", "title_ru", "title_eng", "tavsif",
                    "kategoriya", "qiyinlik", "tayyorgarlik_min", "pishirish_min", "porsiya", "teglar",
                    "youtube_url",
                    "kaloriya_kcal", "oqsil_g", "yog_g", "uglevodlar_g", "tolalar_g", "shakar_g", "natriy_mg"
            }, hdr);
            setWidths(s1, new int[]{
                    5500, 5000, 5000, 9000, 4000, 3500, 4500, 4000, 3200, 5000,
                    8000,
                    3800, 3200, 3200, 4000, 3500, 3500, 3500
            });

            int r1 = 1;
            for (Recipe r : recipes) {
                Row row = s1.createRow(r1++);
                String cat = r.getCategory() != null ? nvl(r.getCategory().getNameUz()) : "";
                String tags = r.getTags().stream()
                        .map(Tag::getNameUz).filter(Objects::nonNull)
                        .sorted().collect(Collectors.joining(", "));
                String diff = r.getDifficultyLevel() != null ? r.getDifficultyLevel().name() : "";
                NutritionalInfo ni = r.getNutritionalInfo();
                styleRow(row, data,
                        nvl(r.getTitleUz()), nvl(r.getTitleRu()), nvl(r.getTitleEng()),
                        nvl(r.getDescription()), cat, diff,
                        String.valueOf(r.getPrepTimeMinutes()),
                        String.valueOf(r.getCookTimeMinutes()),
                        String.valueOf(r.getServings()),
                        tags,
                        nvl(r.getVideoUrl()),
                        ni != null ? fmtNum(ni.getCaloriesPerServing()) : "",
                        ni != null ? fmtNum(ni.getProteinGrams()) : "",
                        ni != null ? fmtNum(ni.getFatGrams()) : "",
                        ni != null ? fmtNum(ni.getCarbohydrateGrams()) : "",
                        ni != null ? fmtNum(ni.getFiberGrams()) : "",
                        ni != null ? fmtNum(ni.getSugarGrams()) : "",
                        ni != null ? fmtNum(ni.getSodiumMg()) : "");
            }

            // ── Sheet 2: Ingredientlar ──────────────────────────────────────
            Sheet s2 = wb.createSheet("Ingredientlar");
            writeHeader(s2, new String[]{"retsept_nomi", "ingredient_nomi", "miqdor", "birlik"}, hdr);
            setWidths(s2, new int[]{5500, 5000, 3500, 4000});

            int r2 = 1;
            for (Recipe r : recipes) {
                for (RecipeIngredient ri : r.getIngredients()) {
                    Row row = s2.createRow(r2++);
                    double amt = ri.getAmount() != null ? ri.getAmount() : 0.0;
                    String amtS = (amt == Math.floor(amt)) ? String.valueOf((long) amt) : String.valueOf(amt);
                    styleRow(row, data,
                            nvl(r.getTitleUz()),
                            ri.getIngredient() != null ? nvl(ri.getIngredient().getNameUz()) : "",
                            amtS,
                            ri.getUnit() != null ? ri.getUnit().name() : "");
                }
            }

            // ── Sheet 3: Bosqichlar ─────────────────────────────────────────
            Sheet s3 = wb.createSheet("Bosqichlar");
            writeHeader(s3, new String[]{"retsept_nomi", "tartib_raqami", "ko'rsatma"}, hdr);
            setWidths(s3, new int[]{5500, 4000, 14000});

            int r3 = 1;
            for (Recipe r : recipes) {
                for (RecipeStep step : r.getSteps()) {
                    Row row = s3.createRow(r3++);
                    styleRow(row, data,
                            nvl(r.getTitleUz()),
                            String.valueOf(step.getStepNumber()),
                            nvl(step.getInstruction()));
                }
            }

            wb.write(out);
            return out.toByteArray();
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // buildRequest / buildUpdateRequest
    //
    // Sheet 1 ustunlari:
    //   0=title_uz*  1=title_ru  2=title_eng  3=tavsif  4=kategoriya
    //   5=qiyinlik    6=prep*     7=cook*       8=porsiya*  9=teglar
    //  10=youtube_url 11=kaloriya 12=oqsil_g  13=yog_g   14=uglevodlar_g
    //  15=tolalar_g 16=shakar_g 17=natriy_mg
    // ─────────────────────────────────────────────────────────────────────────

    private RecipeCreateRequest buildRequest(Row row,
                                             Map<String, List<RecipeIngredientRequest>> ingredientsMap,
                                             Map<String, List<RecipeStepRequest>> stepsMap) {
        RecipeCreateRequest req = new RecipeCreateRequest();

        String titleUz = require(cellStr(row, 0), "title_uz bo'sh bo'lmasligi kerak");
        req.setTitleUz(titleUz);
        req.setTitleRu(cellStr(row, 1));
        req.setTitleEng(cellStr(row, 2));
        req.setDescription(cellStr(row, 3));

        String catName = cellStr(row, 4);
        if (catName != null && !catName.isBlank())
            categoryRepository.findByNameUzIgnoreCase(catName.trim())
                    .ifPresent(c -> req.setCategoryId(c.getId()));

        req.setDifficultyLevel(parseDifficulty(cellStr(row, 5)));
        req.setPrepTimeMinutes(cellInt(row, 6, 10));
        req.setCookTimeMinutes(cellInt(row, 7, 0));
        req.setServings(cellInt(row, 8, 4));
        req.setTagIds(resolveTagIds(cellStr(row, 9)));
        req.setVideoUrl(cellStr(row, 10));
        req.setNutritionalInfo(readNutrition(row, 11));
        req.setVisible(true);

        req.setIngredients(ingredientsMap.getOrDefault(titleUz.trim().toLowerCase(), List.of()));
        req.setSteps(stepsMap.getOrDefault(titleUz.trim().toLowerCase(), List.of()));
        return req;
    }

    private RecipeUpdateRequest buildUpdateRequest(Row row,
                                                   Map<String, List<RecipeIngredientRequest>> ingredientsMap,
                                                   Map<String, List<RecipeStepRequest>> stepsMap) {
        RecipeUpdateRequest req = new RecipeUpdateRequest();

        String titleUz = cellStr(row, 0);
        req.setTitleUz(titleUz);
        req.setTitleRu(cellStr(row, 1));
        req.setTitleEng(cellStr(row, 2));
        req.setDescription(cellStr(row, 3));

        String catName = cellStr(row, 4);
        if (catName != null && !catName.isBlank())
            categoryRepository.findByNameUzIgnoreCase(catName.trim())
                    .ifPresent(c -> req.setCategoryId(c.getId()));

        String diff = cellStr(row, 5);
        if (diff != null && !diff.isBlank()) req.setDifficultyLevel(parseDifficulty(diff));

        req.setPrepTimeMinutes(cellInt(row, 6, 10));
        req.setCookTimeMinutes(cellInt(row, 7, 0));
        req.setServings(cellInt(row, 8, 4));
        req.setTagIds(resolveTagIds(cellStr(row, 9)));
        req.setVideoUrl(cellStr(row, 10));
        req.setNutritionalInfo(readNutrition(row, 11));
        req.setVisible(true);

        if (titleUz != null) {
            List<RecipeIngredientRequest> ings = ingredientsMap.get(titleUz.trim().toLowerCase());
            if (ings != null) req.setIngredients(ings);
            List<RecipeStepRequest> steps = stepsMap.get(titleUz.trim().toLowerCase());
            if (steps != null) req.setSteps(steps);
        }
        return req;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Sheet 2: Ingredientlar  (0=retsept_nomi | 1=ingredient | 2=miqdor | 3=birlik)
    // ─────────────────────────────────────────────────────────────────────────

    private Map<String, List<RecipeIngredientRequest>> readIngredients(Sheet sheet) {
        Map<String, List<RecipeIngredientRequest>> map = new LinkedHashMap<>();
        int lastRow = sheet.getLastRowNum();
        for (int i = 1; i <= lastRow; i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;

            String recipeName = cellStr(row, 0);
            String ingName = cellStr(row, 1);
            if (recipeName == null || recipeName.isBlank() || ingName == null || ingName.isBlank()) continue;

            double amount = cellDouble(row, 2, 1.0);
            MeasurementUnit unit = parseUnit(cellStr(row, 3));

            Ingredient ingredient = ingredientRepository.findByNameUzIgnoreCase(ingName.trim())
                    .orElseGet(() -> {
                        Ingredient newIng = new Ingredient();
                        newIng.setNameUz(ingName.trim());
                        newIng.setDefaultUnit(unit);
                        return ingredientRepository.save(newIng);
                    });

            RecipeIngredientRequest ingReq = new RecipeIngredientRequest();
            ingReq.setIngredientId(ingredient.getId());
            ingReq.setAmount(amount);
            ingReq.setUnit(unit);
            ingReq.setOrderIndex(map.getOrDefault(recipeName.trim().toLowerCase(), List.of()).size());
            map.computeIfAbsent(recipeName.trim().toLowerCase(), k -> new ArrayList<>()).add(ingReq);
        }
        return map;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Sheet 3: Bosqichlar  (0=retsept_nomi | 1=tartib | 2=ko'rsatma)
    // ─────────────────────────────────────────────────────────────────────────

    private Map<String, List<RecipeStepRequest>> readSteps(Sheet sheet) {
        Map<String, List<RecipeStepRequest>> map = new LinkedHashMap<>();
        int lastRow = sheet.getLastRowNum();
        for (int i = 1; i <= lastRow; i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;

            String recipeName = cellStr(row, 0);
            String instruction = cellStr(row, 2);
            if (recipeName == null || recipeName.isBlank() || instruction == null || instruction.isBlank()) continue;

            int stepNum = cellInt(row, 1, map.getOrDefault(recipeName.trim().toLowerCase(), List.of()).size() + 1);
            RecipeStepRequest stepReq = new RecipeStepRequest();
            stepReq.setStepNumber(stepNum);
            stepReq.setInstruction(instruction.trim());
            map.computeIfAbsent(recipeName.trim().toLowerCase(), k -> new ArrayList<>()).add(stepReq);
        }
        return map;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Template generatsiya
    // ─────────────────────────────────────────────────────────────────────────

    public byte[] generateTemplate() throws IOException {
        return generateTemplate("uz");
    }

    public byte[] generateTemplate(String lang) throws IOException {
        String l = (lang == null || lang.isBlank()) ? "uz" : lang.trim().toLowerCase();

        try (Workbook wb = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            CellStyle headerStyle = makeHeaderStyle(wb);
            CellStyle exampleStyle = makeExampleStyle(wb);
            CellStyle bold = makeBoldStyle(wb);

            String sheetRecipes, sheetIngr, sheetSteps, sheetGuide;
            String[] h1, h2, h3;
            String recipeNameUz, recipeNameRu, recipeNameEng, recipeDesc, diffVal, tagExample, videoExample;
            String[][] ingrRows, stepRows, guideRows;

            if ("ru".equals(l)) {
                sheetRecipes = "Рецепты";
                sheetIngr = "Ингредиенты";
                sheetSteps = "Шаги";
                sheetGuide = "Инструкция";

                h1 = new String[]{
                        "Название (UZ) *", "Название (RU)", "Название (EN)",
                        "Описание", "Категория", "Сложность",
                        "Подготовка (мин) *", "Готовка (мин) *", "Порции *", "Теги",
                        "YouTube URL",
                        "Калории (ккал)", "Белки (г)", "Жиры (г)", "Углеводы (г)",
                        "Клетчатка (г)", "Сахар (г)", "Натрий (мг)"};
                h2 = new String[]{"Название рецепта *", "Ингредиент *", "Количество *", "Единица *"};
                h3 = new String[]{"Название рецепта *", "Порядковый номер *", "Инструкция *"};

                recipeNameUz = "Osh (Plov)";
                recipeNameRu = "Плов";
                recipeNameEng = "Uzbek Plov";
                recipeDesc = "Национальное блюдо Узбекистана";
                diffVal = "средний";
                tagExample = "Asosiy taom, Guruchli";
                videoExample = "";

                ingrRows = new String[][]{
                        {recipeNameUz, "Рис", "1", "килограмм"},
                        {recipeNameUz, "Морковь", "800", "грамм"},
                        {recipeNameUz, "Лук", "3", "штук"},
                        {recipeNameUz, "Мясо", "800", "грамм"},
                };
                stepRows = new String[][]{
                        {recipeNameUz, "1", "Замочите рис в тёплой подсоленной воде на 1-2 часа"},
                        {recipeNameUz, "2", "Разогрейте масло в казане до 200°C и обжарьте лук до золотистого цвета"},
                        {recipeNameUz, "3", "Добавьте мясо и обжарьте со всех сторон"},
                        {recipeNameUz, "4", "Выложите морковь, приготовьте зирвак, затем добавьте рис"},
                };
                guideRows = new String[][]{
                        {"ПОЛЕ", "ПОЯСНЕНИЕ"},
                        {"Название рецепта", "Одинаково во всех трёх листах (регистр неважен)"},
                        {"Сложность", "лёгкий | средний | сложный"},
                        {"Единица", "грамм | кг | мл | литр | стакан | ст.л | ч.л | штук | пучок | щепотка | ломтик | по вкусу"},
                        {"Категория", "Существующее UZ-название категории"},
                        {"Теги", "UZ-названия через запятую: Asosiy taom, Sho'rva"},
                        {"YouTube URL", "Ссылка на YouTube-видео (необязательно). Пример: https://youtu.be/xxxxx"},
                        {"Питательность", "Все поля необязательны. Если оставить пустым — не сохранится"},
                        {"* — обязательное", "Не оставляйте пустым"},
                };

            } else if ("en".equals(l)) {
                sheetRecipes = "Recipes";
                sheetIngr = "Ingredients";
                sheetSteps = "Steps";
                sheetGuide = "Guide";

                h1 = new String[]{
                        "Title (UZ) *", "Title (RU)", "Title (EN)",
                        "Description", "Category", "Difficulty",
                        "Prep (min) *", "Cook (min) *", "Servings *", "Tags",
                        "YouTube URL",
                        "Calories (kcal)", "Protein (g)", "Fat (g)", "Carbs (g)",
                        "Fiber (g)", "Sugar (g)", "Sodium (mg)"};
                h2 = new String[]{"Recipe name *", "Ingredient *", "Amount *", "Unit *"};
                h3 = new String[]{"Recipe name *", "Step number *", "Instruction *"};

                recipeNameUz = "Osh (Plov)";
                recipeNameRu = "Плов";
                recipeNameEng = "Uzbek Plov";
                recipeDesc = "National dish of Uzbekistan";
                diffVal = "medium";
                tagExample = "Asosiy taom, Guruchli";
                videoExample = "";

                ingrRows = new String[][]{
                        {recipeNameUz, "Rice", "1", "kilogram"},
                        {recipeNameUz, "Carrot", "800", "gram"},
                        {recipeNameUz, "Onion", "3", "piece"},
                        {recipeNameUz, "Meat", "800", "gram"},
                };
                stepRows = new String[][]{
                        {recipeNameUz, "1", "Soak the rice in warm salted water for 1-2 hours"},
                        {recipeNameUz, "2", "Heat oil in a kazan to 200°C and fry onions until golden"},
                        {recipeNameUz, "3", "Add meat and brown on all sides"},
                        {recipeNameUz, "4", "Add carrots, cook the zirvak, then add the rice"},
                };
                guideRows = new String[][]{
                        {"FIELD", "DESCRIPTION"},
                        {"Recipe name", "Same in all three sheets (case-insensitive)"},
                        {"Difficulty", "easy | medium | hard"},
                        {"Unit", "gram | kg | ml | liter | cup | tablespoon | teaspoon | piece | bunch | pinch | slice | to_taste"},
                        {"Category", "Existing UZ category name"},
                        {"Tags", "Existing UZ tag names, comma-separated: Asosiy taom, Sho'rva"},
                        {"YouTube URL", "YouTube video link (optional). Example: https://youtu.be/xxxxx"},
                        {"Nutrition", "All fields optional. Leave empty to skip"},
                        {"* = required", "Do not leave empty"},
                };

            } else { // uz (default)
                sheetRecipes = "Retseptlar";
                sheetIngr = "Ingredientlar";
                sheetSteps = "Bosqichlar";
                sheetGuide = "Yo'riqnoma";

                h1 = new String[]{
                        "Nomi (UZ) *", "Nomi (RU)", "Nomi (EN)",
                        "Tavsif", "Kategoriya", "Qiyinlik",
                        "Tayyorgarlik (min) *", "Pishirish (min) *", "Porsiya *", "Teglar",
                        "YouTube URL",
                        "Kaloriya (kcal)", "Oqsil (g)", "Yog' (g)", "Uglevodlar (g)",
                        "Tolalar (g)", "Shakar (g)", "Natriy (mg)"};
                h2 = new String[]{"Retsept nomi *", "Ingredient nomi *", "Miqdor *", "Birlik *"};
                h3 = new String[]{"Retsept nomi *", "Tartib raqami *", "Ko'rsatma *"};

                recipeNameUz = "Osh (Plov)";
                recipeNameRu = "Плов";
                recipeNameEng = "Uzbek Plov";
                recipeDesc = "O'zbekistonning milliy taomi";
                diffVal = "o'rtacha";
                tagExample = "Asosiy taom, Guruchli";
                videoExample = "";

                ingrRows = new String[][]{
                        {recipeNameUz, "Guruch", "1", "kilogramm"},
                        {recipeNameUz, "Sabzi", "800", "gramm"},
                        {recipeNameUz, "Piyoz", "3", "dona"},
                        {recipeNameUz, "Go'sht", "800", "gramm"},
                };
                stepRows = new String[][]{
                        {recipeNameUz, "1", "Guruchni iliq sho'r suvda 1-2 soat iviting"},
                        {recipeNameUz, "2", "Qozonda yog'ni 200°C ga qiziting va piyozni to'q oltin rangga qovuring"},
                        {recipeNameUz, "3", "Go'shtni solib har tomonini qovorib oling"},
                        {recipeNameUz, "4", "Sabzini to'shing va zirvakni pishiring, keyin guruchni soling"},
                };
                guideRows = new String[][]{
                        {"MAYDON", "IZOH"},
                        {"Retsept nomi", "Har uch varaqda ham bir xil yozilsin (harf katta-kichikligi muhim emas)"},
                        {"Qiyinlik", "oson | o'rtacha | qiyin"},
                        {"Birlik", "gramm | kg | ml | litr | stakan | osh qoshiq | choy qoshiq | dona | dasta | chimdim | bo'lak | mazaga"},
                        {"Kategoriya", "Mavjud kategoriyaning UZ nomini kiriting"},
                        {"Teglar", "Mavjud teglarning UZ nomini vergul bilan: Asosiy taom, Sho'rva"},
                        {"YouTube URL", "YouTube video havolasi (ixtiyoriy). Misol: https://youtu.be/xxxxx"},
                        {"Ozuqa", "Barcha maydonlar ixtiyoriy. Bo'sh qolsa saqlanmaydi"},
                        {"* belgisi", "Majburiy maydon — bo'sh qoldirilmasin"},
                };
            }

            // ── Sheet 1 ───────────────────────────────────────────────────────
            Sheet s1 = wb.createSheet(sheetRecipes);
            writeHeader(s1, h1, headerStyle);
            setWidths(s1, new int[]{
                    5500, 5000, 5000, 9000, 4000, 3500, 4500, 4000, 3200, 5000,
                    8000,
                    3800, 3200, 3200, 4000, 3500, 3500, 3500
            });
            styleRow(s1.createRow(1), exampleStyle,
                    recipeNameUz, recipeNameRu, recipeNameEng, recipeDesc,
                    "Asosiy taomlar", diffVal, "40", "90", "8", tagExample,
                    videoExample,
                    "560", "18", "22", "65", "3", "2", "420");

            // ── Sheet 2 ───────────────────────────────────────────────────────
            Sheet s2 = wb.createSheet(sheetIngr);
            writeHeader(s2, h2, headerStyle);
            setWidths(s2, new int[]{6000, 5000, 3500, 4000});
            writeRows(s2, ingrRows, exampleStyle);

            // ── Sheet 3 ───────────────────────────────────────────────────────
            Sheet s3 = wb.createSheet(sheetSteps);
            writeHeader(s3, h3, headerStyle);
            setWidths(s3, new int[]{6000, 4000, 14000});
            writeRows(s3, stepRows, exampleStyle);

            // ── Sheet 4: Yo'riqnoma ───────────────────────────────────────────
            Sheet guide = wb.createSheet(sheetGuide);
            for (int i = 0; i < guideRows.length; i++) {
                Row r = guide.createRow(i);
                for (int j = 0; j < guideRows[i].length; j++) {
                    Cell c = r.createCell(j);
                    c.setCellValue(guideRows[i][j]);
                    if (i == 0) c.setCellStyle(bold);
                }
            }
            guide.setColumnWidth(0, 5500);
            guide.setColumnWidth(1, 18000);

            wb.write(out);
            return out.toByteArray();
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Yordamchi metodlar
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Columns startCol..startCol+6 dan NutritionalInfoRequest o'qiydi.
     * Agar barcha 7 ta katak bo'sh bo'lsa null qaytaradi.
     */
    private NutritionalInfoRequest readNutrition(Row row, int startCol) {
        Double cal = cellDoubleOrNull(row, startCol);
        Double prot = cellDoubleOrNull(row, startCol + 1);
        Double fat = cellDoubleOrNull(row, startCol + 2);
        Double carb = cellDoubleOrNull(row, startCol + 3);
        Double fib = cellDoubleOrNull(row, startCol + 4);
        Double sug = cellDoubleOrNull(row, startCol + 5);
        Double sod = cellDoubleOrNull(row, startCol + 6);

        if (cal == null && prot == null && fat == null && carb == null
                && fib == null && sug == null && sod == null) return null;

        NutritionalInfoRequest n = new NutritionalInfoRequest();
        n.setCaloriesPerServing(cal);
        n.setProteinGrams(prot);
        n.setFatGrams(fat);
        n.setCarbohydrateGrams(carb);
        n.setFiberGrams(fib);
        n.setSugarGrams(sug);
        n.setSodiumMg(sod);
        return n;
    }

    private Set<Long> resolveTagIds(String raw) {
        if (raw == null || raw.isBlank()) return Set.of();
        Set<Long> ids = new HashSet<>();
        for (String name : raw.split(",")) {
            String trimmed = name.trim();
            if (!trimmed.isBlank())
                tagRepository.findByNameUzIgnoreCase(trimmed).ifPresent(t -> ids.add(t.getId()));
        }
        return ids;
    }

    private void writeRows(Sheet sheet, String[][] rows, CellStyle style) {
        for (int i = 0; i < rows.length; i++) {
            Row r = sheet.createRow(i + 1);
            for (int j = 0; j < rows[i].length; j++) {
                Cell c = r.createCell(j);
                c.setCellValue(rows[i][j]);
                c.setCellStyle(style);
            }
        }
    }

    private CellStyle makeHeaderStyle(Workbook wb) {
        CellStyle s = wb.createCellStyle();
        Font f = wb.createFont();
        f.setBold(true);
        s.setFont(f);
        s.setFillForegroundColor(IndexedColors.LIGHT_TURQUOISE.getIndex());
        s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return s;
    }

    private CellStyle makeExampleStyle(Workbook wb) {
        CellStyle s = wb.createCellStyle();
        s.setFillForegroundColor(IndexedColors.LEMON_CHIFFON.getIndex());
        s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return s;
    }

    private CellStyle makeDataStyle(Workbook wb) {
        return wb.createCellStyle();
    }

    private CellStyle makeBoldStyle(Workbook wb) {
        CellStyle s = wb.createCellStyle();
        Font f = wb.createFont();
        f.setBold(true);
        s.setFont(f);
        return s;
    }

    private void writeHeader(Sheet sheet, String[] headers, CellStyle style) {
        Row row = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell c = row.createCell(i);
            c.setCellValue(headers[i]);
            c.setCellStyle(style);
        }
    }

    private void styleRow(Row row, CellStyle style, String... values) {
        for (int i = 0; i < values.length; i++) {
            Cell c = row.createCell(i);
            c.setCellValue(values[i]);
            c.setCellStyle(style);
        }
    }

    private void setWidths(Sheet sheet, int[] widths) {
        for (int i = 0; i < widths.length; i++) sheet.setColumnWidth(i, widths[i]);
    }

    // ── Cell helpers ──────────────────────────────────────────────────────────

    private String cellStr(Row row, int col) {
        Cell cell = row.getCell(col);
        if (cell == null) return null;
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> null;
        };
    }

    private Integer cellInt(Row row, int col, int def) {
        Cell cell = row.getCell(col);
        if (cell == null) return def;
        if (cell.getCellType() == CellType.NUMERIC) return (int) cell.getNumericCellValue();
        try {
            return Integer.parseInt(cellStr(row, col));
        } catch (Exception e) {
            return def;
        }
    }

    private double cellDouble(Row row, int col, double def) {
        Cell cell = row.getCell(col);
        if (cell == null) return def;
        if (cell.getCellType() == CellType.NUMERIC) return cell.getNumericCellValue();
        try {
            return Double.parseDouble(cellStr(row, col));
        } catch (Exception e) {
            return def;
        }
    }

    /**
     * Katak bo'sh yoki mavjud bo'lmasa null qaytaradi (ozuqa uchun).
     */
    private Double cellDoubleOrNull(Row row, int col) {
        Cell cell = row.getCell(col);
        if (cell == null || cell.getCellType() == CellType.BLANK) return null;
        if (cell.getCellType() == CellType.NUMERIC) return cell.getNumericCellValue();
        String s = cellStr(row, col);
        if (s == null || s.isBlank()) return null;
        try {
            return Double.parseDouble(s);
        } catch (Exception e) {
            return null;
        }
    }

    private boolean isRowEmpty(Row row) {
        String title = cellStr(row, 0);
        return title == null || title.isBlank();
    }

    private String require(String val, String msg) {
        if (val == null || val.isBlank()) throw new IllegalArgumentException(msg);
        return val;
    }

    private String nvl(String v) {
        return v != null ? v : "";
    }

    /**
     * Double ni toza formatda string ga o'giradi (ortiqcha kasr yo'q).
     */
    private String fmtNum(Double v) {
        if (v == null) return "";
        return (v == Math.floor(v) && !Double.isInfinite(v))
                ? String.valueOf((long) (double) v)
                : String.format("%.1f", v);
    }

    private DifficultyLevel parseDifficulty(String s) {
        return LocalizedValueParser.parseDifficulty(s);
    }

    private MeasurementUnit parseUnit(String s) {
        return LocalizedValueParser.parseUnit(s);
    }
}
