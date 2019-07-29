package com.github.jokar.rx_okhttp;

/**
 * Create by JokAr. on 2019-06-04.
 */
public class JsonException extends RuntimeException {
    private String json;
    private String path;
    private String params;

    public JsonException(String path, String params, String json) {
        super("can't analysis json"
                + "\npath: " + path
                + "\nparams: " + params
                + "\nresult value: " + json);
        this.json = json;
        this.path = path;
        this.params = params;
    }

    public JsonException(String path, String params, String json, RuntimeException e) {
        super("can't analysis json"
                + "\npath: " + path
                + "\nparams: " + params
                + "\nresult value: " + json);
        this.json = json;
        this.path = path;
        this.params = params;
    }

    public JsonException(String path, String params, String json, Exception e) {
        super("can't analysis json"
                + "\npath: " + path
                + "\nparams: " + params
                + "\nresult value: " + json);
        this.json = json;
        this.path = path;
        this.params = params;
    }

    public String getJson() {
        return json;
    }

    public String getPath() {
        return path;
    }

    public String getParams() {
        return params;
    }
}
