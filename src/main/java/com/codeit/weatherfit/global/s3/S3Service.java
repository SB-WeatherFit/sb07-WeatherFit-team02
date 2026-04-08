package com.codeit.weatherfit.global.s3;

public interface S3Service {
    String put(byte[] bytes, String fileName);
    String put(String fileName, String contentType, byte[] bytes);
    String delete(String fileName);
    String getUrl(String key);
}