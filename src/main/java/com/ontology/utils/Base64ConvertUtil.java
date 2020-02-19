package com.ontology.utils;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

/**
 * Base64 tool
 */
public class Base64ConvertUtil {

    public static String encode(String str) throws UnsupportedEncodingException {
        byte[] encodeBytes = Base64.getEncoder().encode(str.getBytes("utf-8"));
        return new String(encodeBytes);
    }

    public static String encode(byte[] src) throws UnsupportedEncodingException {
        byte[] encodeBytes = Base64.getEncoder().encode(src);
        return new String(encodeBytes);
    }

    public static String decode(String str) throws UnsupportedEncodingException {
        byte[] decodeBytes = Base64.getDecoder().decode(str.getBytes("utf-8"));
        return new String(decodeBytes);
    }

    public static byte[] decodeAndReturnBytes(String str) throws UnsupportedEncodingException {
        byte[] decodeBytes = Base64.getDecoder().decode(str.getBytes("utf-8"));
        return decodeBytes;
    }

}
