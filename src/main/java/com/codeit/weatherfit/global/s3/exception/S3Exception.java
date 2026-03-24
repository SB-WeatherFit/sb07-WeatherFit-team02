package com.codeit.weatherfit.global.s3.exception;

import com.codeit.weatherfit.global.exception.ErrorCode;
import com.codeit.weatherfit.global.exception.WeatherFitException;

public class S3Exception extends WeatherFitException {
    public S3Exception(ErrorCode errorCode) {
        super(errorCode);
    }
}
