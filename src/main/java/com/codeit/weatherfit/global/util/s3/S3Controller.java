package com.codeit.weatherfit.global.util.s3;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/s3")
@RequiredArgsConstructor
public class S3Controller {

    private final LogS3Service logS3Service;

    @PostMapping
    public ResponseEntity.BodyBuilder addS3(){

        logS3Service.uploadLogFile();
        return ResponseEntity.status(HttpStatus.CREATED);
    }
}
