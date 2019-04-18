package com.github.jokar.rx_okhttp;

import java.lang.reflect.Type;

import io.reactivex.Observable;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * get请求
 * Create by JokAr. on 2019/4/18.
 */
public class GET extends HTTP {

    public GET(OkHttpClient okHttpClient) {
        super(okHttpClient);
    }

    /**
     * 同步请求
     *
     * @param url  地址
     * @param type 返回参数类型
     * @param <T>  返回参数类型
     * @return
     */
    public <T> Observable<T> get(String url, Type type) {
        ResultObservable<T> observable = adapt(type, url, false);
        return rxAdapter(observable);
    }

    /**
     * 异步请求
     *
     * @param url  地址
     * @param type 返回参数类型
     * @param <T>  返回参数类型
     * @return
     */
    public <T> Observable<T> getAsync(String url, Type type) {
        ResultObservable<T> observable = adapt(type, url, true);
        return rxAdapter(observable);
    }

    private <T> ResultObservable<T> adapt(Type type, String url, boolean async) {
        // 构造Request
        Request.Builder builder = new Request.Builder();
        Request request = builder.get().url(url).build();

        return adapt(request, async, type);
    }
}
