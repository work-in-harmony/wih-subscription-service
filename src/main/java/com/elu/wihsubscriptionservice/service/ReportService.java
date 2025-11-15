package com.elu.wihsubscriptionservice.service;


import com.elu.wihsubscriptionservice.modal.PlanType;
import com.elu.wihsubscriptionservice.openFeign.SalesReportDto;
import com.elu.wihsubscriptionservice.repo.SubscriptionRepo;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.ConditionalOperators;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;


import com.elu.wihsubscriptionservice.modal.Subscription;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.aggregation.*;
@Service
public class ReportService {
    private final SubscriptionRepo subscriptionRepo;
    private final MongoTemplate mongoTemplate; // Inject MongoTemplate

    public ReportService(SubscriptionRepo repository,
                         MongoTemplate mongoTemplate) {
        this.subscriptionRepo = repository;
        this.mongoTemplate = mongoTemplate;
    }

    public List<SalesReportDto> getSubscriptionSalesReport(Date start, Date end, String groupBy) {

        // 1. Determine the date format string based on the groupBy parameter
        String dateFormat;
        switch (groupBy.toUpperCase()) {
            case "DAY":
                dateFormat = "%Y-%m-%d";
                break;
            case "YEAR":
                dateFormat = "%Y";
                break;
            case "MONTH":
            default:
                dateFormat = "%Y-%m";
                break;
        }

        // 2. Define Aggregation Stages

        // Stage 1: $match
        // Filter documents to be within the specified date range
        MatchOperation matchStage = match(Criteria.where("startDate").gte(start).lte(end));

        // Stage 2: $project
        // **MODIFIED HERE**
        // We no longer use the conditional logic.
        // We just pass along the 'planType', create the 'period',
        // and convert the 'amount' string to a double named 'sales'.
        ProjectionOperation projectStage1 = project("planType")
                .and("startDate").dateAsFormattedString(dateFormat).as("period")
                .and(ConvertOperators.ToDouble.toDouble("$amount")).as("sales");

        // Stage 3: $group
        // Group by both the new 'period' and the 'planType' to sum sales for each
        // This stage remains the same, but it now sums the 'sales' field from projectStage1
        GroupOperation groupStage1 = group("period", "planType")
                .sum("sales").as("totalSales")
                .count().as("count");

        // Stage 4: $group (Pivot)
        // This stage is unchanged. It pivots the data.
        GroupOperation groupStage2 = group("_id.period")
                .sum(
                        ConditionalOperators.when(Criteria.where("_id.planType").is(PlanType.BASIC))
                                .thenValueOf("$totalSales").otherwise(0)
                ).as("basicSales")
                .sum(
                        ConditionalOperators.when(Criteria.where("_id.planType").is(PlanType.PRO))
                                .thenValueOf("$totalSales").otherwise(0)
                ).as("proSales")
                .sum(
                        ConditionalOperators.when(Criteria.where("_id.planType").is(PlanType.BASIC))
                                .thenValueOf("$count").otherwise(0)
                ).as("basicCount")
                .sum(
                        ConditionalOperators.when(Criteria.where("_id.planType").is(PlanType.PRO))
                                .thenValueOf("$count").otherwise(0)
                ).as("proCount");

        // Stage 5: $project
        // This stage is unchanged.
        ProjectionOperation projectStage2 = project()
                .and("_id").as("period")
                .and("basicSales").as("basicSales")
                .and("proSales").as("proSales")
                .and("basicCount").as("basicCount")
                .and("proCount").as("proCount")
                .and("basicSales").plus("proSales").as("totalSales"); // Calculate total

        // Stage 6: $sort
        // This stage is unchanged.
        SortOperation sortStage = sort(Sort.Direction.ASC, "period");

        // 3. Build and Execute the Aggregation
        Aggregation aggregation = newAggregation(
                matchStage,
                projectStage1,
                groupStage1,
                groupStage2,
                projectStage2,
                sortStage
        );

        // Execute the aggregation
        AggregationResults<SalesReportDto> results = mongoTemplate.aggregate(
                aggregation, Subscription.class, SalesReportDto.class
        );

        return results.getMappedResults();
    }

    public List<Object> getSalesReport(Date start, Date end) {
        return subscriptionRepo.findMonthlySalesReport(start, end);
    }
}
