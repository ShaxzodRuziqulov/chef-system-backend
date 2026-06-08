package com.example.oshpazbackendsystem.service;

import com.example.oshpazbackendsystem.dto.BulkImportResultDto;
import com.example.oshpazbackendsystem.dto.BulkImportResultDto.RowResult;
import com.example.oshpazbackendsystem.dto.RecipeCreateRequest;
import com.example.oshpazbackendsystem.dto.RecipeIngredientRequest;
import com.example.oshpazbackendsystem.dto.RecipeStepRequest;
import com.example.oshpazbackendsystem.dto.RecipeUpdateRequest;
import com.example.oshpazbackendsystem.entity.Recipe;
import com.example.oshpazbackendsystem.entity.Ingredient;
import com.example.oshpazbackendsystem.entity.enums.DifficultyLevel;
import com.example.oshpazbackendsystem.entity.enums.MeasurementUnit;
import com.example.oshpazbackendsystem.repository.CategoryRepository;
import com.example.oshpazbackendsystem.repository.IngredientRepository;
import com.example.oshpazbackendsystem.repository.RecipeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BulkImportService {

    private final RecipeService recipeService;
    private final CategoryRepository categoryRepository;
    private final IngredientRepository ingredientRepository;
    private final RecipeRepository recipeRepository;

    // ── Columns ─────────────────────────────────────────────────────────
    // 0:title_uz  1:title_ru  2:title_eng  3:description  4:category
    // 5:difficulty  6:prep_time  7:cook_time  8:servings
    // 9:ingredients  10:steps

    /**
     * @param mode "SKIP" — mavjud retseptni o'tkazib yuborish (default)
     *             "UPDATE" — mavjud retseptni yangilash
     */
    public BulkImportResultDto importFromExcel(MultipartFile file, String mode) throws IOException {
        List<RowResult> results = new ArrayList<>();
        boolean doUpdate = "UPDATE".equalsIgnoreCase(mode);
        int successCount = 0;
        int updatedCount = 0;
        int skippedCount = 0;
        int failedCount  = 0;

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            int lastRow = sheet.getLastRowNum();

            for (int i = 1; i <= lastRow; i++) {
                Row row = sheet.getRow(i);
                if (row == null || isRowEmpty(row)) continue;

                String titleUz = cellStr(row, 0);

                // Mavjudligini tekshirish
                java.util.Optional<Recipe> existing = titleUz != null
                        ? recipeRepository.findByTitleUzIgnoreCaseAndDeletedFalse(titleUz.trim())
                        : java.util.Optional.empty();

                if (existing.isPresent()) {
                    if (doUpdate) {
                        // UPDATE rejimi — mavjud retseptni yangilaymiz
                        try {
                            RecipeUpdateRequest updateReq = buildUpdateRequest(row);
                            recipeService.update(existing.get().getId(), updateReq);
                            updatedCount++;
                            results.add(RowResult.builder()
                                    .row(i + 1).status("UPDATED").titleUz(titleUz).build());
                        } catch (Exception e) {
                            failedCount++;
                            log.warn("Bulk update row {} xatosi: {}", i + 1, e.getMessage());
                            results.add(RowResult.builder()
                                    .row(i + 1).status("ERROR").titleUz(titleUz).error(e.getMessage()).build());
                        }
                    } else {
                        // SKIP rejimi — o'tkazib yuboramiz
                        skippedCount++;
                        log.info("Bulk import row {} o'tkazib yuborildi (dublikat): {}", i + 1, titleUz);
                        results.add(RowResult.builder()
                                .row(i + 1).status("SKIPPED").titleUz(titleUz)
                                .error("Bunday nomli retsept allaqachon mavjud").build());
                    }
                } else {
                    // Yangi retsept yaratamiz
                    try {
                        RecipeCreateRequest req = buildRequest(row);
                        recipeService.create(req);
                        successCount++;
                        results.add(RowResult.builder()
                                .row(i + 1).status("SUCCESS").titleUz(titleUz).build());
                    } catch (Exception e) {
                        failedCount++;
                        log.warn("Bulk import row {} xatosi: {}", i + 1, e.getMessage());
                        results.add(RowResult.builder()
                                .row(i + 1).status("ERROR").titleUz(titleUz).error(e.getMessage()).build());
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

    private RecipeCreateRequest buildRequest(Row row) {
        RecipeCreateRequest req = new RecipeCreateRequest();

        req.setTitleUz(require(cellStr(row, 0), "title_uz bo'sh bo'lmasligi kerak"));
        req.setTitleRu(cellStr(row, 1));
        req.setTitleEng(cellStr(row, 2));
        req.setDescription(cellStr(row, 3));

        // Category (nameUz bo'yicha qidiramiz)
        String catName = cellStr(row, 4);
        if (catName != null && !catName.isBlank()) {
            categoryRepository.findByNameUzIgnoreCase(catName.trim())
                    .ifPresent(c -> req.setCategoryId(c.getId()));
        }

        // Difficulty
        String diff = cellStr(row, 5);
        if (diff != null && !diff.isBlank()) {
            try { req.setDifficultyLevel(DifficultyLevel.valueOf(diff.trim().toUpperCase())); }
            catch (IllegalArgumentException ignored) { req.setDifficultyLevel(DifficultyLevel.MEDIUM); }
        } else {
            req.setDifficultyLevel(DifficultyLevel.MEDIUM);
        }

        req.setPrepTimeMinutes(cellInt(row, 6, 10));
        req.setCookTimeMinutes(cellInt(row, 7, 0));
        req.setServings(cellInt(row, 8, 4));
        req.setVisible(true);

        // Ingredients: "Guruch:500:GRAM;Go'sht:300:GRAM"
        String ingStr = cellStr(row, 9);
        if (ingStr != null && !ingStr.isBlank()) {
            req.setIngredients(parseIngredients(ingStr));
        }

        // Steps: "Guruchni yuving|Go'shtni qizing|Aralashtirib qo'ying"
        String stepsStr = cellStr(row, 10);
        if (stepsStr != null && !stepsStr.isBlank()) {
            req.setSteps(parseSteps(stepsStr));
        }

        return req;
    }

    private RecipeUpdateRequest buildUpdateRequest(Row row) {
        RecipeUpdateRequest req = new RecipeUpdateRequest();
        req.setTitleUz(cellStr(row, 0));
        req.setTitleRu(cellStr(row, 1));
        req.setTitleEng(cellStr(row, 2));
        req.setDescription(cellStr(row, 3));

        String catName = cellStr(row, 4);
        if (catName != null && !catName.isBlank()) {
            categoryRepository.findByNameUzIgnoreCase(catName.trim())
                    .ifPresent(c -> req.setCategoryId(c.getId()));
        }

        String diff = cellStr(row, 5);
        if (diff != null && !diff.isBlank()) {
            try { req.setDifficultyLevel(DifficultyLevel.valueOf(diff.trim().toUpperCase())); }
            catch (IllegalArgumentException ignored) {}
        }

        req.setPrepTimeMinutes(cellInt(row, 6, 10));
        req.setCookTimeMinutes(cellInt(row, 7, 0));
        req.setServings(cellInt(row, 8, 4));
        req.setVisible(true);

        String ingStr = cellStr(row, 9);
        if (ingStr != null && !ingStr.isBlank()) {
            req.setIngredients(parseIngredients(ingStr));
        }

        String stepsStr = cellStr(row, 10);
        if (stepsStr != null && !stepsStr.isBlank()) {
            req.setSteps(parseSteps(stepsStr));
        }

        return req;
    }

    private List<RecipeIngredientRequest> parseIngredients(String raw) {
        List<RecipeIngredientRequest> list = new ArrayList<>();
        String[] parts = raw.split(";");
        for (int i = 0; i < parts.length; i++) {
            String part = parts[i].trim();
            if (part.isBlank()) continue;
            String[] tokens = part.split(":");
            if (tokens.length < 1) continue;

            String ingName  = tokens[0].trim();
            double amount   = tokens.length > 1 ? parseDouble(tokens[1].trim(), 1.0) : 1.0;
            MeasurementUnit unit = tokens.length > 2
                    ? parseUnit(tokens[2].trim())
                    : MeasurementUnit.PIECE;

            // Ingredient topish yoki yaratish
            Ingredient ingredient = ingredientRepository
                    .findByNameUzIgnoreCase(ingName)
                    .orElseGet(() -> {
                        Ingredient newIng = new Ingredient();
                        newIng.setNameUz(ingName);
                        newIng.setNameRu(ingName);
                        newIng.setNameEng(ingName);
                        newIng.setDefaultUnit(unit);
                        return ingredientRepository.save(newIng);
                    });

            RecipeIngredientRequest ingReq = new RecipeIngredientRequest();
            ingReq.setIngredientId(ingredient.getId());
            ingReq.setAmount(amount);
            ingReq.setUnit(unit);
            ingReq.setOrderIndex(i);
            list.add(ingReq);
        }
        return list;
    }

    private List<RecipeStepRequest> parseSteps(String raw) {
        List<RecipeStepRequest> list = new ArrayList<>();
        String[] parts = raw.split("\\|");
        for (int i = 0; i < parts.length; i++) {
            String instruction = parts[i].trim();
            if (instruction.isBlank()) continue;
            RecipeStepRequest stepReq = new RecipeStepRequest();
            stepReq.setStepNumber(i + 1);
            stepReq.setInstruction(instruction);
            list.add(stepReq);
        }
        return list;
    }

    // ── Template yaratish ────────────────────────────────────────────
    public byte[] generateTemplate() throws IOException {
        try (Workbook wb = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = wb.createSheet("Retseptlar");

            // Header style
            CellStyle headerStyle = wb.createCellStyle();
            Font font = wb.createFont();
            font.setBold(true);
            headerStyle.setFont(font);
            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            String[] headers = {
                "title_uz *", "title_ru", "title_eng", "description",
                "category", "difficulty", "prep_time *", "cook_time *", "servings *",
                "ingredients", "steps"
            };

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
                sheet.setColumnWidth(i, 5000);
            }

            // Example row
            Row example = sheet.createRow(1);
            example.createCell(0).setCellValue("Osh");
            example.createCell(1).setCellValue("Плов");
            example.createCell(2).setCellValue("Plov");
            example.createCell(3).setCellValue("Milliy taomimiz");
            example.createCell(4).setCellValue("Asosiy taomlar");
            example.createCell(5).setCellValue("MEDIUM");
            example.createCell(6).setCellValue(20);
            example.createCell(7).setCellValue(60);
            example.createCell(8).setCellValue(4);
            example.createCell(9).setCellValue("Guruch:500:GRAM;Sabzi:300:GRAM;Piyoz:2:PIECE;Go'sht:400:GRAM");
            example.createCell(10).setCellValue("Guruch va sabzini tozalab oling|Piyozni yog'da qovuring|Hammasini qo'shib pishiring");

            // Hint sheet
            Sheet hint = wb.createSheet("Yo'riqnoma");
            String[][] hints = {
                {"difficulty", "EASY, MEDIUM, HARD"},
                {"ingredients", "Format: nom:miqdor:birlik;nom:miqdor:birlik"},
                {"units", "GRAM, KILOGRAM, MILLILITER, LITER, CUP, TABLESPOON, TEASPOON, PIECE, BUNCH, PINCH, SLICE, TO_TASTE"},
                {"steps", "Bosqichlarni | bilan ajrating"},
                {"* belgisi", "Majburiy maydon"},
            };
            for (int i = 0; i < hints.length; i++) {
                Row r = hint.createRow(i);
                r.createCell(0).setCellValue(hints[i][0]);
                r.createCell(1).setCellValue(hints[i][1]);
            }
            hint.setColumnWidth(0, 4000);
            hint.setColumnWidth(1, 15000);

            wb.write(out);
            return out.toByteArray();
        }
    }

    // ── Helpers ──────────────────────────────────────────────────────
    private String cellStr(Row row, int col) {
        Cell cell = row.getCell(col);
        if (cell == null) return null;
        return switch (cell.getCellType()) {
            case STRING  -> cell.getStringCellValue().trim();
            case NUMERIC -> String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default      -> null;
        };
    }

    private Integer cellInt(Row row, int col, int defaultVal) {
        Cell cell = row.getCell(col);
        if (cell == null) return defaultVal;
        if (cell.getCellType() == CellType.NUMERIC) return (int) cell.getNumericCellValue();
        try { return Integer.parseInt(cellStr(row, col)); }
        catch (Exception e) { return defaultVal; }
    }

    private boolean isRowEmpty(Row row) {
        String title = cellStr(row, 0);
        return title == null || title.isBlank();
    }

    private String require(String val, String msg) {
        if (val == null || val.isBlank()) throw new IllegalArgumentException(msg);
        return val;
    }

    private double parseDouble(String s, double def) {
        try { return Double.parseDouble(s); }
        catch (Exception e) { return def; }
    }

    private MeasurementUnit parseUnit(String s) {
        try { return MeasurementUnit.valueOf(s.toUpperCase()); }
        catch (Exception e) { return MeasurementUnit.PIECE; }
    }
}
