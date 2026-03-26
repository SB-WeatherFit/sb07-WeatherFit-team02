package com.codeit.weatherfit.domain.clothes.exception;

import com.codeit.weatherfit.global.exception.ErrorCode;

public class ClothesExtractionException extends ClothesException {
    public ClothesExtractionException(ErrorCode errorCode) { super (errorCode); }
}
