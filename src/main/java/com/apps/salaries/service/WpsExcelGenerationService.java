package com.apps.salaries.service;

import com.apps.salaries.exception.CustomResponse;

import java.io.IOException;

public interface WpsExcelGenerationService {

    public CustomResponse generateExcelFile() throws IOException;

}
