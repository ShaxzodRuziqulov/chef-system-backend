package com.example.oshpazbackendsystem.dto.response;

import com.example.oshpazbackendsystem.entity.enums.PlanStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MealPlanResponse {

    private Long id;

    // Foydalanuvchi
    private UUID userId;
    private String userFullName;

    // Reja
    private String name;
    private LocalDate weekStartDate;
    private LocalDate weekEndDate;
    private PlanStatus status;
    private String notes;

    // Haftalik jadval
    private List<MealPlanEntryResponse> entries;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
