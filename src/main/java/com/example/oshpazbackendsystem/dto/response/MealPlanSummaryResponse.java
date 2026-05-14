package com.example.oshpazbackendsystem.dto.response;

import com.example.oshpazbackendsystem.entity.enums.PlanStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MealPlanSummaryResponse {

    private Long id;
    private String name;
    private LocalDate weekStartDate;
    private LocalDate weekEndDate;
    private PlanStatus status;
    private int entryCount;     // Jami nechta ovqat rejalashtirilgan
    private LocalDateTime createdAt;
}
