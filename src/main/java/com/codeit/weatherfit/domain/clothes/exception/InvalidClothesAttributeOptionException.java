package com.codeit.weatherfit.domain.clothes.exception;

import com.codeit.weatherfit.global.exception.ErrorCode;

public class InvalidClothesAttributeOptionException extends ClothesException {
    public InvalidClothesAttributeOptionException(ErrorCode errorCode) { super (errorCode); }
}
