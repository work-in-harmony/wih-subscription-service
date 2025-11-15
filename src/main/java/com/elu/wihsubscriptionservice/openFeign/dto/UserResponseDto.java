package com.elu.wihsubscriptionservice.openFeign.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserResponseDto {

    private String email;
    private String message;
    private Boolean status;

}
