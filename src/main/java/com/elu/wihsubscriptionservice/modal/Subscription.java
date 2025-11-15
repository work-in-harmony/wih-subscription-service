package com.elu.wihsubscriptionservice.modal;

import com.elu.wihsubscriptionservice.dto.AddressDto;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
@Builder
@Document(collection = "Subscription")
public class Subscription {
    @Id
    private UUID id;
    private UUID organisationId;
    private String amount;
    private String transactionId;
    private AddressDto address;
    private String userEmail;
    private PlanType planType;
    private PlanInterval planInterval;
    private PlanStatus planStatus;
    private Date  startDate;
    private Date endDate;
    private Date createdAt;
    private Date updatedAt;
}
