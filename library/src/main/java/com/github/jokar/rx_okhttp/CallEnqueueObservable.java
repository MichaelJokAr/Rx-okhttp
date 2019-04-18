package com.github.jokar.rx_okhttp;

import java.io.IOException;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.CompositeException;
import io.reactivex.plugins.RxJavaPlugins;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 异步请求
 * Create by JokAr. on 2019/4/18.
 */
public class CallEnqueueObservable extends Observable<Response> {

    private final Call mCall;

    public CallEnqueueObservable(Call call) {
        mCall = call;
    }

    @Override
    protected void subscribeActual(Observer<? super Response> observer) {
        CallCallback callCallback = new CallCallback(mCall, observer);
        observer.onSubscribe(callCallback);
        if (!callCallback.isDisposed()) {
            mCall.enqueue(callCallback);
        }
    }

    private static final class CallCallback implements Disposable, Callback {
        private final Call call;
        private final Observer<? super Response> observer;
        boolean terminated = false;
        private volatile boolean disposed;

        public CallCallback(Call call, Observer<? super Response> observer) {
            this.call = call;
            this.observer = observer;

        }

        @Override
        public void dispose() {
            disposed = true;
            call.cancel();
        }

        @Override
        public boolean isDisposed() {
            return disposed;
        }

        @Override
        public void onFailure(Call call, IOException e) {
            if (call.isCanceled()) {
                return;
            }
            try {
                observer.onError(e);
            } catch (Throwable inner) {
                RxJavaPlugins.onError(inner);
            }
        }

        @Override
        public void onResponse(Call call, Response response) {
            if (disposed) {
                return;
            }
            try {
                observer.onNext(response);

                if (!disposed) {
                    terminated = true;
                    observer.onComplete();
                }

            } catch (Exception e) {
                if (terminated) {
                    RxJavaPlugins.onError(e);
                } else if (!disposed) {
                    try {
                        observer.onError(e);
                    } catch (Throwable inner) {
                        RxJavaPlugins.onError(new CompositeException(e, inner));
                    }
                }
            }
        }
    }
}
