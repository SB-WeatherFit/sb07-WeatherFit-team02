package com.codeit.weatherfit.global.s3.exception;

import com.codeit.weatherfit.global.exception.ErrorCode;

public class S3UploadException extends S3Exception {
    public S3UploadException(String filename) {
        super(ErrorCode.FILE_UPLOAD_FAILURE);
        this.getDetails().put("filename", filename);
    }

    public S3UploadException(String fileName, String message) {
        super(ErrorCode.FILE_UPLOAD_FAILURE);
        this.getDetails().put("filename", fileName);
        this.getDetails().put("message", message);
    }
}
