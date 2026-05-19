package com.example.oshpazbackendsystem.dto.response;

import com.example.oshpazbackendsystem.entity.enums.MeasurementUnit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MeasurementUnitDto {

    /** Enum nomi — GRAM, KILOGRAM, MILLILITER ... */
    private String value;

    private String nameUz;
    private String nameRu;
    private String nameEng;

    /**
     * Avtomatik konvertatsiya chegarasi.
     * Masalan: GRAM → convertAt=1000, convertTo=KILOGRAM
     * null bo'lsa konvertatsiya yo'q.
     */
    private Integer convertAt;
    private String  convertTo;

    /**
     * false bo'lsa miqdor (son) ko'rsatilmaydi.
     * Masalan TO_TASTE → faqat "ta'mga qarab".
     */
    private boolean showAmount;

    public static MeasurementUnitDto from(MeasurementUnit unit) {
        return MeasurementUnitDto.builder()
                .value(unit.name())
                .nameUz(unit.getNameUz())
                .nameRu(unit.getNameRu())
                .nameEng(unit.getNameEng())
                .convertAt(unit.getConvertAt())
                .convertTo(unit.getConvertTo())
                .showAmount(unit.isShowAmount())
                .build();
    }
}
