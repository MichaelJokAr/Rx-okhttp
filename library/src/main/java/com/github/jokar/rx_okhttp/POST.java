package com.github.jokar.rx_okhttp;

import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import io.reactivex.Observable;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * post请求
 * Create by JokAr. on 2019/4/18.
 */
public class POST extends HTTP {
    private MediaType mType;
    private Map<String, String> mHeader;
    public POST(OkHttpClient okHttpClient) {
        super(okHttpClient);
    }

    /**
     * 同步请求
     *
     * @param type       返回参数类型
     * @param url        地址
     * @param bodyParams 请求参数
     * @param <T>
     * @return
     */
    public <T> Observable<T> post(Type type, String url,
                                  Map<String, String> bodyParams) {
        ResultObservable<T> observable = adapt(type, url, bodyParams, false);
        return rxAdapter(observable);
    }

    /**
     * 添加头
     *
     * @param key
     * @param value
     * @return
     */
    public POST addHeader(String key, String value) {
        if (mHeader == null) {
            mHeader = new LinkedHashMap<>();
        }
        mHeader.put(key, value);
        return this;
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
     * @param bodyParams 请求参数
     * @param <T>
     * @return
     */
    public <T> Observable<T> postAsync(Type type, String url,
                                       Map<String, String> bodyParams) {

        ResultObservable<T> observable = adapt(type, url, bodyParams, true);
        return rxAdapter(observable);
    }

    /**
     * 构建 ResultObservable
     *
     * @param type       返回类型
     * @param url        地址
     * @param bodyParams 参数
     * @param async      是否是异步请求
     * @param <T>
     * @return
     */
    private <T> ResultObservable<T> adapt(Type type, String url,
                                          Map<String, String> bodyParams, boolean async) {
        //1构造RequestBody
        RequestBody body = setRequestBody(bodyParams);


        //2 构造Request
        Request.Builder requestBuilder = new Request.Builder();
        //添加header
        if (mHeader != null && !mHeader.isEmpty()) {
            Set<Map.Entry<String, String>> entries = mHeader.entrySet();
            Iterator<Map.Entry<String, String>> iterator = entries.iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> entry = iterator.next();
                requestBuilder.addHeader(entry.getKey(), entry.getValue());
            }
        }
        Request request = requestBuilder.post(body).url(url).build();

        return adapt(request, async, type);
    }

    /**
     * 构建请求参数
     *
     * @param BodyParams
     * @return
     */
    private RequestBody setRequestBody(Map<String, String> BodyParams) {
        RequestBody body = null;
        MultipartBody.Builder formEncodingBuilder = new MultipartBody.Builder();
        if (BodyParams != null) {
            Iterator<String> iterator = BodyParams.keySet().iterator();
            String key = "";
            while (iterator.hasNext()) {
                key = iterator.next();
                formEncodingBuilder.addFormDataPart(key, BodyParams.get(key));
            }
        }
        //设置类型
        if (mType != null) {
            formEncodingBuilder.setType(mType);
        }
        body = formEncodingBuilder
                .build();
        return body;

    }
}
