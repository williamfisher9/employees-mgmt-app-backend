package com.apps.salaryfilegenerator.service;

import com.apps.salaryfilegenerator.exception.CustomResponse;

public interface WpsPdfGenerationService {

    public CustomResponse generatePdfFile() throws Exception;

}
