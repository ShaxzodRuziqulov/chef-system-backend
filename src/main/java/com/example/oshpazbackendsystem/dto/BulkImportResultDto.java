package com.example.oshpazbackendsystem.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class BulkImportResultDto {

    private int totalRows;
    private int successCount;
    private int failedCount;
    private List<RowResult> results;

    @Data
    @Builder
    public static class RowResult {
        private int row;
        private String status;   // "SUCCESS" | "ERROR"
        private String titleUz;
        private String error;
    }
}
