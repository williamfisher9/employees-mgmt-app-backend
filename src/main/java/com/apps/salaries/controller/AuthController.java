package com.apps.salaries.controller;

import com.apps.salaries.dto.LoginRequestDTO;
import com.apps.salaries.dto.ResponseMessage;
import com.apps.salaries.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@CrossOrigin
public class AuthController {
    @Autowired
    private AuthService authService;

    @Value("${payment.files.directory}")
    private String paymentFilesDirectory;

    @RequestMapping(value = "/public/auth/login", method = RequestMethod.POST)
    public ResponseEntity<ResponseMessage> authenticateUser(@RequestBody LoginRequestDTO loginRequestDTO){
        ResponseMessage responseMessage = authService.authenticateUser(loginRequestDTO);
        return new ResponseEntity<>(responseMessage, HttpStatus.valueOf(responseMessage.getStatus()));
    }

    @RequestMapping(value = "/public/download/{paymentFileName}", method = RequestMethod.GET)
    public ResponseEntity<Resource>
        downloadPaymentFile(@PathVariable("paymentFileName") String paymentFileName){

        try {
            Path filePath = Paths.get(paymentFilesDirectory).resolve(paymentFileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if(resource.exists()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
