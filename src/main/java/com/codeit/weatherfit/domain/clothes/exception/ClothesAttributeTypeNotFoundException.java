package com.codeit.weatherfit.domain.clothes.exception;

import com.codeit.weatherfit.global.exception.ErrorCode;

public class ClothesAttributeTypeNotFoundException extends ClothesException {
    public ClothesAttributeTypeNotFoundException(ErrorCode errorCode) { super (errorCode); }
}
