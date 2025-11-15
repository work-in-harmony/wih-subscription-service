package com.elu.wihsubscriptionservice.openFeign;


import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SalesReportDto {
    // This will be the date string, e.g., "2025-10-28", "2025-10", or "2025"
    private String period;

    // Sales data for BASIC plan
    private double basicSales;
    private long basicCount;

    // Sales data for PRO plan
    private double proSales;
    private long proCount;

    // Total sales for the period
    private double totalSales;
}