package com.apps.salaryfilegenerator.service;

import com.apps.salaryfilegenerator.exception.CustomResponse;

public interface SimplifiedPdfGenerationService {

    public CustomResponse generatePdfFile() throws Exception;

}
