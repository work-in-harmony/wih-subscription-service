package com.elu.wihsubscriptionservice.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ResponseDto {

        private String message;
        private String status;
        private String code;
        private boolean userExist;
        private boolean success;
        private String email;
        private String amount;
        private String transactionId;

}
