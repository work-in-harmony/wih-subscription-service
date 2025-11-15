package com.elu.wihsubscriptionservice.controller;


import com.elu.wihsubscriptionservice.openFeign.SalesReportDto;
import com.elu.wihsubscriptionservice.service.ReportService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/subscription/report")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/sales-graph")
    public List<SalesReportDto> getSalesGraphReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date endDate,
            @RequestParam(defaultValue = "MONTH") String groupBy) {

        return reportService.getSubscriptionSalesReport(startDate, endDate, groupBy);
    }

    @GetMapping("/sales")
    public List<Object> getSalesReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date endDate) {
        return reportService.getSalesReport(startDate, endDate);
    }
}
