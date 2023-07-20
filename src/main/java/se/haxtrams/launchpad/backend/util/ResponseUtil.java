package se.haxtrams.launchpad.backend.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseUtil {
    public static ResponseEntity<String> createSimpleResponse(HttpStatus httpStatus) {
        return ResponseEntity.status(httpStatus).body(httpStatus.name());
    }
}
