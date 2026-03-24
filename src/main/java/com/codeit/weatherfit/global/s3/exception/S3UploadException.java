package com.codeit.weatherfit.global.s3.exception;

import com.codeit.weatherfit.global.exception.ErrorCode;

public class S3UploadException extends S3Exception {
    public S3UploadException(String originalFilename) {
        super(ErrorCode.FILE_UPLOAD_FAILURE);
    }
}
