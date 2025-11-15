package com.elu.wihsubscriptionservice.openFeign;

import com.elu.wihsubscriptionservice.openFeign.dto.ResponseDto;
import com.elu.wihsubscriptionservice.openFeign.dto.UserRequestDto;
import com.elu.wihsubscriptionservice.openFeign.dto.UserResponseDto;
import org.apache.catalina.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "Wih-Auth-Service", url = "http://localhost:8010")
public interface UserClient {

    @PostMapping("/open-feign/user/register-user")
    public ResponseEntity<ResponseDto> registerUser(@RequestBody UserRequestDto request);

}
