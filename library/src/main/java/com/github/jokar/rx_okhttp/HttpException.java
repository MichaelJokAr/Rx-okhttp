package com.github.jokar.rx_okhttp;

import io.reactivex.annotations.Nullable;
import okhttp3.Response;

import static com.github.jokar.rx_okhttp.Preconditions.checkNotNull;

/**
 * Create by JokAr. on 2019/4/18.
 */
public class HttpException extends RuntimeException {
    private final int code;
    private final String message;
    private final transient Response response;

    public HttpException(Response response) {
        super(getMessage(response));
        this.code = response.code();
        this.message = response.message();
        this.response = response;
    }

    private static String getMessage(Response response) {
        checkNotNull(response, "response == null");
        return "HTTP " + response.code() + " " + response.message();
    }

    /**
     * HTTP status code.
     */
    public int code() {
        return code;
    }

    /**
     * HTTP status message.
     */
    public String message() {
        return message;
    }

    /**
     * The full HTTP response. This may be null if the exception was serialized.
     */
    public @Nullable
    Response response() {
        return response;
    }
}
