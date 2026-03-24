package com.codeit.weatherfit.global.s3.exception;

import com.codeit.weatherfit.global.exception.ErrorCode;

public class S3StorageUploadException extends S3Exception {
    public S3StorageUploadException(String originalFilename) {
        super(ErrorCode.FILE_UPLOAD_FAILURE);
    }
}
