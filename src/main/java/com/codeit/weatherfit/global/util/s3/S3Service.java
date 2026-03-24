package com.codeit.weatherfit.global.util.s3;

public interface S3Service {
    public String put(byte[] bytes,String fileName);
    public String delete(String fileName);
    public byte[] get (String fileName);
}
