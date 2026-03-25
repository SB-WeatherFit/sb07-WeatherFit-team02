package com.codeit.weatherfit.domain.clothes.exception;

import com.codeit.weatherfit.global.exception.ErrorCode;

public class ClothesNotFoundException extends ClothesException {
    public ClothesNotFoundException(ErrorCode errorCode) { super (errorCode); }
}
