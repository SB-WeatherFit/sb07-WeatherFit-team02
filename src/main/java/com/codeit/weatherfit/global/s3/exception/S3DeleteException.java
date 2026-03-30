package com.codeit.weatherfit.global.s3.exception;

import com.codeit.weatherfit.global.exception.ErrorCode;

public class S3DeleteException extends S3Exception {
    public S3DeleteException(String fileName) {
        super(ErrorCode.FILE_DELETE_FAILURE);
        this.getDetails().put("fileName", fileName);
    }
}