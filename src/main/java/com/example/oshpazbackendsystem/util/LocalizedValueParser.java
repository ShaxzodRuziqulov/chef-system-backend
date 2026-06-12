package com.example.oshpazbackendsystem.util;

import com.example.oshpazbackendsystem.entity.enums.DifficultyLevel;
import com.example.oshpazbackendsystem.entity.enums.MeasurementUnit;

import java.util.Map;

/**
 * Excel importda foydalanuvchi o'z tilida yozgan qiymatlarni
 * backend enum larga o'giradi.
 *
 * Qo'llab-quvvatlanadi:
 *  - O'zbek:  oson, o'rtacha, qiyin | gramm, dona, litr ...
 *  - Rus:     лёгкий, средний, сложный | грамм, штук, литр ...
 *  - Ingliz:  easy, medium, hard | gram, piece, liter ...  (enum nomi ham)
 */
public final class LocalizedValueParser {

    private LocalizedValueParser() {}

    // ── DifficultyLevel ──────────────────────────────────────────────────────

    private static final Map<String, DifficultyLevel> DIFFICULTY_MAP = Map.ofEntries(
        // Uzbekcha
        Map.entry("oson",      DifficultyLevel.EASY),
        Map.entry("ortacha",   DifficultyLevel.MEDIUM),
        Map.entry("o'rtacha",  DifficultyLevel.MEDIUM),
        Map.entry("qiyin",     DifficultyLevel.HARD),
        // Ruscha
        Map.entry("лёгкий",    DifficultyLevel.EASY),
        Map.entry("легкий",    DifficultyLevel.EASY),
        Map.entry("лёгко",     DifficultyLevel.EASY),
        Map.entry("средний",   DifficultyLevel.MEDIUM),
        Map.entry("средне",    DifficultyLevel.MEDIUM),
        Map.entry("сложный",   DifficultyLevel.HARD),
        Map.entry("сложно",    DifficultyLevel.HARD),
        // Inglizcha (so'z ko'rinishida)
        Map.entry("easy",      DifficultyLevel.EASY),
        Map.entry("medium",    DifficultyLevel.MEDIUM),
        Map.entry("normal",    DifficultyLevel.MEDIUM),
        Map.entry("hard",      DifficultyLevel.HARD),
        Map.entry("difficult", DifficultyLevel.HARD)
    );

    public static DifficultyLevel parseDifficulty(String raw) {
        if (raw == null || raw.isBlank()) return DifficultyLevel.MEDIUM;
        String key = raw.trim().toLowerCase();
        // Avval to'g'ridan-to'g'ri enum nomi (EASY, MEDIUM, HARD)
        try { return DifficultyLevel.valueOf(raw.trim().toUpperCase()); }
        catch (IllegalArgumentException ignored) {}
        // Keyin lokalizatsiya map
        return DIFFICULTY_MAP.getOrDefault(key, DifficultyLevel.MEDIUM);
    }

    // ── MeasurementUnit ──────────────────────────────────────────────────────

    private static final Map<String, MeasurementUnit> UNIT_MAP = Map.ofEntries(
        // GRAM
        Map.entry("gramm",          MeasurementUnit.GRAM),
        Map.entry("gram",           MeasurementUnit.GRAM),
        Map.entry("g",              MeasurementUnit.GRAM),
        Map.entry("г",              MeasurementUnit.GRAM),
        Map.entry("грамм",          MeasurementUnit.GRAM),
        Map.entry("гр",             MeasurementUnit.GRAM),
        // KILOGRAM
        Map.entry("kilogramm",      MeasurementUnit.KILOGRAM),
        Map.entry("kilogram",       MeasurementUnit.KILOGRAM),
        Map.entry("kg",             MeasurementUnit.KILOGRAM),
        Map.entry("кг",             MeasurementUnit.KILOGRAM),
        Map.entry("килограмм",      MeasurementUnit.KILOGRAM),
        Map.entry("кило",           MeasurementUnit.KILOGRAM),
        // MILLILITER
        Map.entry("millilitr",      MeasurementUnit.MILLILITER),
        Map.entry("milliliter",     MeasurementUnit.MILLILITER),
        Map.entry("ml",             MeasurementUnit.MILLILITER),
        Map.entry("мл",             MeasurementUnit.MILLILITER),
        Map.entry("миллилитр",      MeasurementUnit.MILLILITER),
        // LITER
        Map.entry("litr",           MeasurementUnit.LITER),
        Map.entry("liter",          MeasurementUnit.LITER),
        Map.entry("l",              MeasurementUnit.LITER),
        Map.entry("л",              MeasurementUnit.LITER),
        Map.entry("литр",           MeasurementUnit.LITER),
        // CUP
        Map.entry("stakan",         MeasurementUnit.CUP),
        Map.entry("cup",            MeasurementUnit.CUP),
        Map.entry("стакан",         MeasurementUnit.CUP),
        // TABLESPOON
        Map.entry("osh qoshiq",     MeasurementUnit.TABLESPOON),
        Map.entry("tablespoon",     MeasurementUnit.TABLESPOON),
        Map.entry("tbsp",           MeasurementUnit.TABLESPOON),
        Map.entry("ст.л",           MeasurementUnit.TABLESPOON),
        Map.entry("ст.л.",          MeasurementUnit.TABLESPOON),
        Map.entry("столовая ложка", MeasurementUnit.TABLESPOON),
        // TEASPOON
        Map.entry("choy qoshiq",    MeasurementUnit.TEASPOON),
        Map.entry("teaspoon",       MeasurementUnit.TEASPOON),
        Map.entry("tsp",            MeasurementUnit.TEASPOON),
        Map.entry("ч.л",            MeasurementUnit.TEASPOON),
        Map.entry("ч.л.",           MeasurementUnit.TEASPOON),
        Map.entry("чайная ложка",   MeasurementUnit.TEASPOON),
        // PIECE
        Map.entry("dona",           MeasurementUnit.PIECE),
        Map.entry("piece",          MeasurementUnit.PIECE),
        Map.entry("pcs",            MeasurementUnit.PIECE),
        Map.entry("шт",             MeasurementUnit.PIECE),
        Map.entry("штук",           MeasurementUnit.PIECE),
        Map.entry("штука",          MeasurementUnit.PIECE),
        // BUNCH
        Map.entry("dasta",          MeasurementUnit.BUNCH),
        Map.entry("bunch",          MeasurementUnit.BUNCH),
        Map.entry("пучок",          MeasurementUnit.BUNCH),
        // PINCH
        Map.entry("chimdim",        MeasurementUnit.PINCH),
        Map.entry("pinch",          MeasurementUnit.PINCH),
        Map.entry("щепотка",        MeasurementUnit.PINCH),
        Map.entry("щепоть",         MeasurementUnit.PINCH),
        // SLICE
        Map.entry("bo'lak",         MeasurementUnit.SLICE),
        Map.entry("bolak",          MeasurementUnit.SLICE),
        Map.entry("slice",          MeasurementUnit.SLICE),
        Map.entry("ломтик",         MeasurementUnit.SLICE),
        // TO_TASTE
        Map.entry("mazaga",         MeasurementUnit.TO_TASTE),
        Map.entry("maza",           MeasurementUnit.TO_TASTE),
        Map.entry("по вкусу",       MeasurementUnit.TO_TASTE),
        Map.entry("повкусу",        MeasurementUnit.TO_TASTE),
        Map.entry("to taste",       MeasurementUnit.TO_TASTE),
        Map.entry("to_taste",       MeasurementUnit.TO_TASTE)
    );

    public static MeasurementUnit parseUnit(String raw) {
        if (raw == null || raw.isBlank()) return MeasurementUnit.PIECE;
        String key = raw.trim().toLowerCase();
        // Avval to'g'ridan-to'g'ri enum nomi (GRAM, KILOGRAM ...)
        try { return MeasurementUnit.valueOf(raw.trim().toUpperCase()); }
        catch (IllegalArgumentException ignored) {}
        // Keyin lokalizatsiya map
        return UNIT_MAP.getOrDefault(key, MeasurementUnit.PIECE);
    }

    // ── Template uchun lokalizatsiya ─────────────────────────────────────────

    public static String difficultyHint(String lang) {
        return switch (lang.toLowerCase()) {
            case "ru" -> "oson (лёгкий) | o'rtacha (средний) | qiyin (сложный)";
            case "en" -> "easy | medium | hard";
            default   -> "oson | o'rtacha | qiyin";
        };
    }

    public static String unitHint(String lang) {
        return switch (lang.toLowerCase()) {
            case "ru" -> "gramm (грамм) | kg (кг) | ml (мл) | litr (л) | stakan | osh qoshiq (ст.л) | choy qoshiq (ч.л) | dona (шт) | dasta (пучок) | chimdim (щепотка) | bo'lak (ломтик) | mazaga (по вкусу)";
            case "en" -> "gram | kg | ml | liter | cup | tablespoon | teaspoon | piece | bunch | pinch | slice | to_taste";
            default   -> "gramm | kg | ml | litr | stakan | osh qoshiq | choy qoshiq | dona | dasta | chimdim | bo'lak | mazaga";
        };
    }

    public static String difficultyExampleValue(String lang) {
        return switch (lang.toLowerCase()) {
            case "ru" -> "средний";
            case "en" -> "medium";
            default   -> "o'rtacha";
        };
    }
}
