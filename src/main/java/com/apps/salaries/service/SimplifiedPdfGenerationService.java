package com.apps.salaries.service;

import com.apps.salaries.exception.CustomResponse;

public interface SimplifiedPdfGenerationService {

    public CustomResponse generatePdfFile() throws Exception;

}
