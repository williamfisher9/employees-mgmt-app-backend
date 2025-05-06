package com.apps.salaries.service;

import com.apps.salaries.exception.CustomResponse;

public interface WpsPdfGenerationService {

    public CustomResponse generatePdfFile() throws Exception;

}
