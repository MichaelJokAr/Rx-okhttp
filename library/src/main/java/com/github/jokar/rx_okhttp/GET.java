package com.github.jokar.rx_okhttp;

import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import io.reactivex.Observable;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * get请求
 * Create by JokAr. on 2019/4/18.
 */
public class GET extends HTTP<GET> {

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
        //构造httpBuilder
        HttpUrl.Builder httpBuilder = HttpUrl.parse(url).newBuilder();
        //添加请求参数
        if (getQueryMap() != null && !getQueryMap().isEmpty()) {
            Iterator<Map.Entry<String, String>> iterator = getQueryMap().entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> entry = iterator.next();
                httpBuilder.addQueryParameter(entry.getKey(), entry.getValue());
            }
        }
        //添加header
        if (getHeader() != null && !getHeader().isEmpty()) {
            Set<Map.Entry<String, String>> entries = getHeader().entrySet();
            Iterator<Map.Entry<String, String>> iterator = entries.iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> entry = iterator.next();
                builder.addHeader(entry.getKey(), entry.getValue());
            }
        }
        Request request = builder.get().url(httpBuilder.build()).build();

        return adapt(request, async, type);
    }
}
