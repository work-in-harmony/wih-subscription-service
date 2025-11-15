package com.elu.wihsubscriptionservice.dto;


import com.elu.wihsubscriptionservice.modal.PlanInterval;
import com.elu.wihsubscriptionservice.modal.PlanType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class RequestDto {

    public String email;
    public String amount;
    public String transactionId;
    public AddressDto address;
    public PlanType  planType;
    public PlanInterval planInterval;
}
