package com.apps.salaries.service;

import com.apps.salaries.dto.LoginRequestDTO;
import com.apps.salaries.dto.ResponseMessage;
import org.springframework.stereotype.Service;

@Service
public interface AuthService {
    ResponseMessage authenticateUser(LoginRequestDTO loginRequestDTO);
    ResponseMessage getUserById(Long id);
    ResponseMessage getUserByUsername(String username);
    ResponseMessage getUsers();
}
