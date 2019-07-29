package com.github.jokar.rx_okhttp;


import java.lang.reflect.Type;
import java.nio.charset.Charset;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.CompositeException;
import io.reactivex.plugins.RxJavaPlugins;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;

/**
 * 解析数据，返回泛型数据
 * Create by JokAr. on 2019/4/18.
 */
public final class ResultObservable<T> extends Observable<T> {
    private final Observable<Response> upstream;
    private final Type mType;
    private Request mRequest;

    public ResultObservable(Observable<Response> upstream, Type type,
                            Request request) {
        this.upstream = upstream;
        mType = type;
        mRequest = request;
    }

    @Override
    protected void subscribeActual(Observer<? super T> observer) {
        upstream.subscribe(new ResultObserver(observer, mType, mRequest));
    }

    private static class ResultObserver<R> implements Observer<Response> {
        private final Observer<? super R> observer;
        private final Type type;
        private boolean terminated;
        private Request request;

        public ResultObserver(Observer<? super R> observer, Type type, Request request) {
            this.observer = observer;
            this.type = type;
            this.request = request;
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

                        if (r != null) {
                            observer.onNext(r);
                        } else {
                            //解析失败
                            terminated = true;
                            observer.onError(new JsonException(getUrl(), getParams(), string));
                        }
                    } catch (NoClassDefFoundError error) {
                        //没有导包。使用fastjson解析
                        analysisForFastjson(string);
                    } catch (Exception e) {
                        //解析失败
                        terminated = true;
                        try {
                            observer.onError(new JsonException(getUrl(), getParams(), string, e));
                        } catch (Exception inner) {
                            RxJavaPlugins.onError(new JsonException(getUrl(), getParams(), string,
                                    new CompositeException(e, inner)));
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
                response.close();
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
                if (r != null) {
                    observer.onNext(r);
                } else {
                    //解析失败
                    terminated = true;
                    observer.onError(new JsonException(getUrl(), getParams(), string));
                }
            } catch (NoClassDefFoundError noClassDefFoundError) {
                //解析失败,返回string
                observer.onNext((R) string);
            } catch (Exception inner) {
                //解析失败
                terminated = true;
                try {
                    observer.onError(new JsonException(getUrl(), getParams(), string, inner));
                } catch (Exception inner2) {
                    RxJavaPlugins.onError(new JsonException(getUrl(), getParams(), string,
                            new CompositeException(inner, inner2)));
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

        /**
         * 请求链接
         *
         * @return
         */
        private String getUrl() {
            return request.url().toString();
        }

        /**
         * 请求参数
         *
         * @return
         */
        private String getParams() {
            RequestBody requestBody = request.body();
            try {
                if ("POST".equals(request.method()) && requestBody.contentLength() > 0) {
                    StringBuilder sb = new StringBuilder();
                    Buffer buffer = new Buffer();
                    requestBody.writeTo(buffer);
                    Charset charset = Charset.forName("UTF-8");
                    MediaType contentType = requestBody.contentType();
                    if (contentType != null) {
                        charset = contentType.charset(Charset.forName("UTF-8"));
                    }
                    sb.append(buffer.readString(charset));
                    return sb.toString();
                }
            } catch (Exception e) {

            }
            return "";
        }
    }
}
