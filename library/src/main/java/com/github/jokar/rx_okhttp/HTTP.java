package com.github.jokar.rx_okhttp;

import java.lang.reflect.Type;

import io.reactivex.Observable;
import io.reactivex.plugins.RxJavaPlugins;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Create by JokAr. on 2019/4/18.
 */
public class HTTP {
    private final OkHttpClient mOkHttpClient;

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
    public <T> ResultObservable<T> adapt(Request request, boolean async,
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
}
