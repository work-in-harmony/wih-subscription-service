package com.elu.wihsubscriptionservice.service;

import com.elu.wihsubscriptionservice.dto.RequestDto;
import com.elu.wihsubscriptionservice.dto.ResponseDto;
import com.elu.wihsubscriptionservice.modal.PlanInterval;
import com.elu.wihsubscriptionservice.modal.PlanStatus;
import com.elu.wihsubscriptionservice.modal.Subscription;
import com.elu.wihsubscriptionservice.repo.SubscriptionRepo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

@Service
public class PaymentServiceImpls implements PaymentService {

    private SubscriptionRepo  subscriptionRepo;

    public PaymentServiceImpls(SubscriptionRepo subscriptionRepo) {
        this.subscriptionRepo = subscriptionRepo;
    }

    @Override
    public ResponseEntity<ResponseDto> savePaymentDetails(RequestDto requestDto) {
        try {
            // Current date
            Date startDate = new Date();

            // Calculate end date based on plan interval
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(startDate);

            if (requestDto.getPlanInterval() == PlanInterval.MONTHLY) {
                calendar.add(Calendar.MONTH, 1); // +1 month
            } else if (requestDto.getPlanInterval() == PlanInterval.YEARLY) {
                calendar.add(Calendar.YEAR, 1); // +1 year
            }

            Date endDate = calendar.getTime();

            // Create subscription object
            Subscription x = Subscription.builder()
                    .id(UUID.randomUUID())
                    .organisationId(null)
                    .amount(requestDto.getAmount())
                    .transactionId(requestDto.getTransactionId())
                    .address(requestDto.getAddress())
                    .userEmail(requestDto.getEmail())
                    .planType(requestDto.getPlanType())
                    .planInterval(requestDto.getPlanInterval())
                    .planStatus(PlanStatus.ACTIVE)
                    .startDate(startDate)
                    .endDate(endDate)
                    .createdAt(new Date())
                    .updatedAt(new Date())
                    .build();

            // Save subscription
            subscriptionRepo.save(x);

            // Prepare response
            ResponseDto responseDto = ResponseDto.builder()
                    .message("Subscription saved successfully")
                    .status("SUCCESS")
                    .code("200")
                    .success(true)
                    .email(requestDto.getEmail())
                    .amount(requestDto.getAmount())
                    .transactionId(requestDto.getTransactionId())
                    .build();

            return ResponseEntity.ok(responseDto);

        } catch (Exception e) {
            e.printStackTrace();

            ResponseDto responseDto = ResponseDto.builder()
                    .message("Failed to save subscription: " + e.getMessage())
                    .status("ERROR")
                    .code("500")
                    .success(false)
                    .build();

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDto);
        }
    }

}
