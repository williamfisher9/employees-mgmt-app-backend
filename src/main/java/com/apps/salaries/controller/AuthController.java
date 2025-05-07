package com.apps.salaries.controller;

import com.apps.salaries.dto.LoginRequestDTO;
import com.apps.salaries.dto.ResponseMessage;
import com.apps.salaries.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
public class AuthController {
    @Autowired
    private AuthService authService;

    @RequestMapping(value = "/public/auth/login", method = RequestMethod.POST)
    public ResponseEntity<ResponseMessage> authenticateUser(@RequestBody LoginRequestDTO loginRequestDTO){
        ResponseMessage responseMessage = authService.authenticateUser(loginRequestDTO);
        return new ResponseEntity<>(responseMessage, HttpStatus.valueOf(responseMessage.getStatus()));
    }
}
