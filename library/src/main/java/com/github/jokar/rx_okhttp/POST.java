package com.github.jokar.rx_okhttp;

import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * post请求
 * Create by JokAr. on 2019/4/18.
 */
public class POST extends HTTP {

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
        okhttp3.FormBody.Builder formEncodingBuilder = new okhttp3.FormBody.Builder();
        if (BodyParams != null) {
            Iterator<String> iterator = BodyParams.keySet().iterator();
            String key = "";
            while (iterator.hasNext()) {
                key = iterator.next();
                formEncodingBuilder.add(key, BodyParams.get(key));
            }
        }
        body = formEncodingBuilder.build();
        return body;

    }
}
