package com.codeit.weatherfit.domain.clothes.exception;

import com.codeit.weatherfit.global.exception.ErrorCode;

public class ClothesAttributeValueMissingException extends ClothesException {
    public ClothesAttributeValueMissingException(ErrorCode errorCode) { super (errorCode); }
}
