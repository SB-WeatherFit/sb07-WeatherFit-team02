package com.codeit.weatherfit.global.s3.util;

import java.util.UUID;

public class S3KeyGenerator {
    public static String generateKey(String originalFilename) {
        return "images/" + UUID.randomUUID() + "_" + originalFilename;
    }
}
