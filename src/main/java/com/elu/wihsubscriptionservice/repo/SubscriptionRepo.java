package com.elu.wihsubscriptionservice.repo;

import com.elu.wihsubscriptionservice.modal.PlanStatus;
import com.elu.wihsubscriptionservice.modal.Subscription;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Date;
import java.util.List;

public interface SubscriptionRepo extends MongoRepository<Subscription, String> {

    @Aggregation(pipeline = {
            "{ $match: { startDate: { $gte: ?0, $lte: ?1 } } }",
            "{ $group: { _id: { $month: '$startDate' }, totalSales: { $sum: { $toDouble: '$amount' } }, count: { $sum: 1 } } }",
            "{ $sort: { '_id': 1 } }"
    })
    List<Object> findMonthlySalesReport(Date start, Date end);

    List<Subscription> findByPlanStatus(PlanStatus planStatus);
}
