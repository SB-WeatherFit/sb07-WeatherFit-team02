package com.codeit.weatherfit.domain.clothes.exception;

import com.codeit.weatherfit.global.exception.ErrorCode;

public class ClothesAttributeDefInUseException extends ClothesException {
    public ClothesAttributeDefInUseException(ErrorCode errorCode) { super (errorCode); }
}
