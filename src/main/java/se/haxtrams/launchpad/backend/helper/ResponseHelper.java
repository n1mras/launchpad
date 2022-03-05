package se.haxtrams.launchpad.backend.helper;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseHelper {
    public static ResponseEntity<String> createSimpleResponse(HttpStatus httpStatus) {
        return ResponseEntity.status(httpStatus).body(httpStatus.name());
    }

}
