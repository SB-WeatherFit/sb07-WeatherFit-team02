package com.codeit.weatherfit.global.s3.exception;

import com.codeit.weatherfit.global.exception.ErrorCode;

public class S3UrlException extends S3Exception {
    public S3UrlException(String key) {
        super(ErrorCode.FILE_URL_FAILURE);
        this.getDetails().put("key", key);
    }
}