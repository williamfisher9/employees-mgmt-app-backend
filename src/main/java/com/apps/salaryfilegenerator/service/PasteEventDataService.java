package com.apps.salaryfilegenerator.service;

import com.apps.salaryfilegenerator.dto.PasteEventDataObject;
import org.springframework.http.ResponseEntity;

public interface PasteEventDataService {

    public ResponseEntity<?> handleMinistriesPasteEventObject(PasteEventDataObject pasteEventDataObject);

    public ResponseEntity<?> handleSimplifiedPasteEventObject(PasteEventDataObject pasteEventDataObject);

    public ResponseEntity<?> handleWpsPasteEventObject(PasteEventDataObject pasteEventDataObject);

}
