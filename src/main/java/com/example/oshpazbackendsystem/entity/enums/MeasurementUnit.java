package com.example.oshpazbackendsystem.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MeasurementUnit {

    // Og'irlik / Weight
    GRAM      ("g",              "г",          "g",        1000, "KILOGRAM", true ),
    KILOGRAM  ("kg",             "кг",         "kg",       null,  null,      true ),

    // Hajm / Volume
    MILLILITER("ml",             "мл",         "ml",       1000, "LITER",   true ),
    LITER     ("l",              "л",          "l",        null,  null,      true ),
    CUP       ("stakan",         "стакан",     "cup",      null,  null,      true ),
    TABLESPOON("osh q.",         "ст. л.",     "tbsp",     null,  null,      true ),
    TEASPOON  ("choy q.",        "ч. л.",      "tsp",      null,  null,      true ),

    // Son / Count
    PIECE     ("dona",           "шт.",        "pcs",      null,  null,      true ),
    BUNCH     ("bog'lam",        "пучок",      "bunch",    null,  null,      true ),
    PINCH     ("chimdim",        "щепотка",    "pinch",    null,  null,      true ),
    SLICE     ("bo'lak",         "ломтик",     "slice",    null,  null,      true ),

    // Boshqa / Other  — son ko'rsatilmaydi
    TO_TASTE  ("ta'bga qarab",   "по вкусу",   "to taste", null,  null,      false);

    /** O'zbek tili */
    private final String nameUz;
    /** Rus tili */
    private final String nameRu;
    /** Ingliz tili */
    private final String nameEng;
    /**
     * Avtomatik konvertatsiya chegarasi.
     * Masalan: GRAM → 1000 bo'lsa KILOGRAM ga o'tkaziladi.
     * null = konvertatsiya yo'q.
     */
    private final Integer convertAt;
    /**
     * Konvertatsiya qilinadigan birlik nomi (enum name()).
     * null = konvertatsiya yo'q.
     */
    private final String convertTo;
    /**
     * false bo'lsa miqdor (son) ko'rsatilmaydi.
     * Masalan TO_TASTE → faqat "ta'mga qarab" (2 ta'mga qarab emas).
     */
    private final boolean showAmount;
}
