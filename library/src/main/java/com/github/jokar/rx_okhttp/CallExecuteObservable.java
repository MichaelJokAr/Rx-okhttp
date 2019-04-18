package com.github.jokar.rx_okhttp;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.plugins.RxJavaPlugins;
import okhttp3.Call;
import okhttp3.Response;

/**
 * 同步请求
 * Create by JokAr. on 2019/4/18.
 */
public class CallExecuteObservable extends Observable<Response> {
    private final Call mCall;

    public CallExecuteObservable(Call call) {
        mCall = call;
    }

    @Override
    protected void subscribeActual(Observer<? super Response> observer) {
        CallDisposable disposable = new CallDisposable(mCall);
        observer.onSubscribe(disposable);
        if (disposable.isDisposed()) {
            return;
        }
        //
        boolean terminated = false;
        try {

            Response execute = mCall.execute();
            if (!disposable.isDisposed()) {
                observer.onNext(execute);
            }

            //
            if (!disposable.isDisposed()) {
                terminated = true;
                observer.onComplete();
            }
        } catch (Throwable throwable) {
            if (terminated) {
                RxJavaPlugins.onError(throwable);
            } else {
                try {
                    observer.onError(throwable);
                } catch (Exception e) {
                    RxJavaPlugins.onError(e);
                }
            }
        }
    }

    private static final class CallDisposable implements Disposable {
        private final Call call;
        private volatile boolean disposed;

        public CallDisposable(Call call) {
            this.call = call;
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
    }
}
