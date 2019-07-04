package com.github.jokar.rx_okhttp;

import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import io.reactivex.Observable;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * post请求
 * Create by JokAr. on 2019/4/18.
 */
public class POST extends HTTP<POST> {
    private MediaType mType;

    public POST(OkHttpClient okHttpClient) {
        super(okHttpClient);
    }

    /**
     * 同步请求
     *
     * @param type       返回参数类型
     * @param url        地址
     * @param <T>
     * @return
     */
    public <T> Observable<T> post(Type type, String url) {
        ResultObservable<T> observable = adapt(type, url, setRequestBody(), false);
        return rxAdapter(observable);
    }


    /**
     * content-type 同步json请求
     *
     * @param type
     * @param url
     * @param json
     * @param <T>
     * @return
     */
    public <T> Observable<T> post(Type type, String url, String json) {
        ResultObservable<T> observable = adapt(type, url, jsonRequestBody(json),
                false);
        return rxAdapter(observable);
    }

    /**
     * content-type 异步json请求
     *
     * @param type 返回参数类型
     * @param url  地址
     * @param json
     * @param <T>
     * @return
     */
    public <T> Observable<T> postAsync(Type type, String url, String json) {

        ResultObservable<T> observable = adapt(type, url, jsonRequestBody(json),
                true);
        return rxAdapter(observable);
    }

    /**
     * 返回json body
     *
     * @param json
     * @return
     */
    private RequestBody jsonRequestBody(String json) {
        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        return RequestBody.create(JSON, json);
    }
    /**
     * 设置类型
     *
     * @param type
     * @return
     */
    public POST setType(MediaType type) {
        mType = type;
        return this;
    }
    /**
     * 异步请求
     *
     * @param type       返回参数类型
     * @param url        地址
     * @param <T>
     * @return
     */
    public <T> Observable<T> postAsync(Type type, String url) {

        ResultObservable<T> observable = adapt(type, url, setRequestBody(), true);
        return rxAdapter(observable);
    }

    /**
     * 构建 ResultObservable
     *
     * @param type       返回类型
     * @param url        地址
     * @param async      是否是异步请求
     * @param <T>
     * @return
     */
    private <T> ResultObservable<T> adapt(Type type, String url, RequestBody body,
                                          boolean async) {
        //构造httpBuilder
        HttpUrl.Builder httpBuilder = HttpUrl.parse(url).newBuilder();
        //2 构造Request
        Request.Builder requestBuilder = new Request.Builder();
        //添加queryParam
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
                requestBuilder.addHeader(entry.getKey(), entry.getValue());
            }
        }
        Request request = requestBuilder.post(body).url(httpBuilder.build()).build();

        return adapt(request, async, type);
    }

    /**
     * 构建请求参数
     *
     * @return
     */
    private RequestBody setRequestBody() {
        MultipartBody.Builder multipartBodyBuilder = new MultipartBody.Builder();
        if (getBodyMap() != null) {
            Iterator<String> iterator = getBodyMap().keySet().iterator();
            String key = "";
            while (iterator.hasNext()) {
                key = iterator.next();
                if (key != null && getBodyMap().get(key) != null) {
                    multipartBodyBuilder.addFormDataPart(key, getBodyMap().get(key));
                }
            }
        }
        //设置类型
        if (mType != null) {
            multipartBodyBuilder.setType(mType);
        }
        return multipartBodyBuilder.build();

    }
}
