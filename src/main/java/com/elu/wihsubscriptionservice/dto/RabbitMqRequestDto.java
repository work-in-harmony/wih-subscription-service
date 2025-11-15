package com.elu.wihsubscriptionservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class RabbitMqRequestDto {

    private String email;
    private String transcationId;
    private String amount;

}
