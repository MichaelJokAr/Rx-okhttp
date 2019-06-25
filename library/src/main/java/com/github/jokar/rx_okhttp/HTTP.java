package com.github.jokar.rx_okhttp;

import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.plugins.RxJavaPlugins;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Create by JokAr. on 2019/4/18.
 */
public class HTTP<T extends HTTP> {
    private final OkHttpClient mOkHttpClient;
    private Map<String, String> mQueryMap;
    private Map<String, String> mBodyMap;
    private Map<String, String> mHeader;
    public HTTP(OkHttpClient okHttpClient) {
        mOkHttpClient = okHttpClient;
    }

    /**
     * 构建 ResultObservable
     *
     * @param request 请求
     * @param async   是否是异步请求
     * @param type    返回类型
     * @param <T>
     * @return
     */
    protected <T> ResultObservable<T> adapt(Request request, boolean async,
                                            Type type) {
        // 将Request封装为Call
        Call call = mOkHttpClient.newCall(request);
        // 转rx
        Observable<Response> responseObservable = async
                ? new CallEnqueueObservable(call)
                : new CallExecuteObservable(call);

        return new ResultObservable<>(responseObservable, type);
    }

    public <T> Observable<T> rxAdapter(Observable<T> source) {
        return RxJavaPlugins.onAssembly(source);
    }

    /**
     * 添加查询参数
     *
     * @param key
     * @param value
     * @return
     */
    public T addQueryParam(String key, String value) {
        if (mQueryMap == null) {
            mQueryMap = new LinkedHashMap<>();
        }
        if (key != null && key.length() > 0 && value != null) {
            mQueryMap.put(key, value);
        }

        return (T) this;
    }

    /**
     * 添加body参数
     *
     * @param key
     * @param value
     * @return
     */
    public T addBodyParam(String key, String value) {
        if (mBodyMap == null) {
            mBodyMap = new LinkedHashMap<>();
        }
        if (key != null && key.length() > 0 && value != null) {
            mBodyMap.put(key, value);
        }
        return (T) this;
    }

    /**
     * 添加头
     *
     * @param key
     * @param value
     * @return
     */
    public T addHeader(String key, String value) {
        if (mHeader == null) {
            mHeader = new LinkedHashMap<>();
        }
        if (key != null && key.length() > 0 && value != null) {
            mHeader.put(key, value);
        }
        return (T) this;
    }

    public Map<String, String> getQueryMap() {
        return mQueryMap;
    }

    public Map<String, String> getBodyMap() {
        return mBodyMap;
    }

    public Map<String, String> getHeader() {
        return mHeader;
    }
}
