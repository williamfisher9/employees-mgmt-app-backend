package com.apps.salaryfilegenerator.service;

import com.apps.salaryfilegenerator.exception.CustomResponse;

import java.io.IOException;

public interface MinistriesExcelGenerationService {

    public CustomResponse generateExcelFile() throws IOException;

}
