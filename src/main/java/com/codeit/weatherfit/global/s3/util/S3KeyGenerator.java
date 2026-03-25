package com.codeit.weatherfit.global.s3.util;

public class S3KeyGenerator {
    public static String generateKey(String originalFilename) {
        return System.currentTimeMillis() + "_" + originalFilename;
    }
}
