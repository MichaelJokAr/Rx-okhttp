package com.github.jokar.rx_okhttp;


import java.lang.reflect.Type;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.CompositeException;
import io.reactivex.plugins.RxJavaPlugins;
import okhttp3.Response;

/**
 * 解析数据，返回泛型数据
 * Create by JokAr. on 2019/4/18.
 */
public final class ResultObservable<T> extends Observable<T> {
    private final Observable<Response> upstream;
    private final Type mType;

    public ResultObservable(Observable<Response> upstream, Type type) {
        this.upstream = upstream;
        mType = type;
    }

    @Override
    protected void subscribeActual(Observer<? super T> observer) {
        upstream.subscribe(new ResultObserver(observer, mType));
    }

    private static class ResultObserver<R> implements Observer<Response> {
        private final Observer<? super R> observer;
        private final Type type;
        private boolean terminated;

        public ResultObserver(Observer<? super R> observer, Type type) {
            this.observer = observer;
            this.type = type;
        }

        @Override
        public void onSubscribe(Disposable disposable) {
            observer.onSubscribe(disposable);
        }

        @Override
        public void onNext(Response response) {
            if (response.isSuccessful()) {
                try {
                    String string = response.body().string();
                    if (type == null) {
                        //解析失败,返回string
                        observer.onNext((R) string);
                        response.close();
                        return;
                    }
                    try {
                        //先使用gson解析
                        com.google.gson.Gson gson = new com.google.gson.Gson();
                        R r = gson.fromJson(string, type);
                        gson = null;
                        observer.onNext(r);
                    } catch (NoClassDefFoundError error) {
                        //没有导包。使用fastjson解析
                        analysisForFastjson(string);
                    } catch (Exception e) {
                        //解析失败
                        terminated = true;
                        try {
                            observer.onError(e);
                        } catch (Exception inner) {
                            RxJavaPlugins.onError(new CompositeException(e, inner));
                        }
                    }
                } catch (Exception e) {
                    terminated = true;
                    try {
                        observer.onError(e);
                    } catch (Exception inner) {
                        RxJavaPlugins.onError(new CompositeException(e, inner));
                    }
                } finally {
                    response.close();
                }
            } else {
                //没有请求成功
                terminated = true;
                Throwable t = new HttpException(response);
                try {
                    observer.onError(t);
                } catch (Exception inner) {
                    RxJavaPlugins.onError(new CompositeException(t, inner));
                }
            }
        }

        /**
         * 使用fastjson解析
         *
         * @param string
         */
        private void analysisForFastjson(String string) {
            try {
                R r = com.alibaba.fastjson.JSONObject.parseObject(string, type);
                observer.onNext(r);
            } catch (NoClassDefFoundError noClassDefFoundError) {
                //解析失败,返回string
                observer.onNext((R) string);
            } catch (Exception inner) {
                //解析失败
                terminated = true;
                try {
                    observer.onError(inner);
                } catch (Exception inner2) {
                    RxJavaPlugins.onError(new CompositeException(inner, inner2));
                }
            }
        }

        @Override
        public void onError(Throwable throwable) {
            if (!terminated) {
                observer.onError(throwable);
            } else {
                Throwable broken = new AssertionError(
                        "This should never happen! Report as a bug with the full stacktrace.");
                //noinspection UnnecessaryInitCause Two-arg AssertionError constructor is 1.7+ only.
                broken.initCause(throwable);
                RxJavaPlugins.onError(broken);
            }
        }

        @Override
        public void onComplete() {
            if (!terminated) {
                observer.onComplete();
            }
        }
    }
}
