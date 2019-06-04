package com.github.jokar.rx_okhttp;

/**
 * Create by JokAr. on 2019-06-04.
 */
public class JsonException extends RuntimeException {

    public JsonException(String json) {
        super("can't analysis json for string \n" + json);
    }

    public JsonException(String json, RuntimeException e) {
        super("can't analysis json for string \n" + json + "\n"
                + e.getMessage());
    }

    public JsonException(String json, Exception e) {
        super("can't analysis json for string \n" + json + "\n"
                + e.getMessage());
    }
}
