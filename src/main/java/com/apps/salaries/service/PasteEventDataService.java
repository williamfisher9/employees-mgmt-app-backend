package com.apps.salaries.service;

import com.apps.salaries.dto.PasteEventDataObject;
import org.springframework.http.ResponseEntity;

public interface PasteEventDataService {

    public ResponseEntity<?> handleMinistriesPasteEventObject(PasteEventDataObject pasteEventDataObject);

    public ResponseEntity<?> handleSimplifiedPasteEventObject(PasteEventDataObject pasteEventDataObject);

    public ResponseEntity<?> handleWpsPasteEventObject(PasteEventDataObject pasteEventDataObject);

}
