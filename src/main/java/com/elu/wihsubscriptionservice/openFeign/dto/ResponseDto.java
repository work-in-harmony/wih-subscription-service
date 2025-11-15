package com.elu.wihsubscriptionservice.openFeign.dto;



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
    private Boolean registered;
    private Boolean subscribed;
    private boolean success;
}
