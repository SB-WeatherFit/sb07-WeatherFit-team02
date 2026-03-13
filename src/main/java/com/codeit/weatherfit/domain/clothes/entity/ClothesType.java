package com.codeit.weatherfit.domain.clothes.entity;

public enum ClothesType {

        TOP("상의"),
    BOTTOM("하의"),
    DRESS("원피스"),
    OUTER("아우터"),
    UNDERWEAR("속옷"),
    ACCESSORY("악세사리"),
    SHOES("신발"),
    SOCKS("양말"),
    HAT("모자"),
    BAG("가방"),
    SCARF("스카프"),
    ETC("기타");

        private final String description;
    ClothesType(String description) {
        this.description = description;
    }


}
