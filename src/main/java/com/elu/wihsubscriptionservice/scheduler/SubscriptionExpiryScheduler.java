package com.elu.wihsubscriptionservice.scheduler;


import com.elu.wihsubscriptionservice.modal.PlanStatus;
import com.elu.wihsubscriptionservice.modal.Subscription;
import com.elu.wihsubscriptionservice.repo.SubscriptionRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SubscriptionExpiryScheduler {

    private final SubscriptionRepo subscriptionRepository;

    // 🕐 Runs every day at midnight (00:00)
    @Scheduled(cron = "0 0 0 * * *")
    public void expireOldSubscriptions() {
        Date now = new Date();
        List<Subscription> activeSubs = subscriptionRepository.findByPlanStatus(PlanStatus.ACTIVE);

        for (Subscription sub : activeSubs) {
            if (sub.getEndDate() != null && sub.getEndDate().before(now)) {
                sub.setPlanStatus(PlanStatus.EXPIRED);
                sub.setUpdatedAt(now);
                subscriptionRepository.save(sub);
                System.out.println("✅ Subscription expired for user: " + sub.getUserEmail());
            }
        }
    }
}
