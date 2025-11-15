package com.elu.wihsubscriptionservice.service;

import com.elu.wihsubscriptionservice.dto.RequestDto;
import com.elu.wihsubscriptionservice.dto.ResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface PaymentService {

    public ResponseEntity<ResponseDto> savePaymentDetails(RequestDto requestDto);
}
