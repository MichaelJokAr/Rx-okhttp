package com.github.jokar.rx_okhttp;

import android.content.Context;

import java.util.concurrent.TimeUnit;

import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;

/**
 * Create by JokAr. on 2019/4/18.
 */
public class NetworkManager {
    private static volatile NetworkManager instance;
    private OkHttpClient mOkHttpClient;

    public NetworkManager(Context context) {
        mOkHttpClient = getClient(context);
    }

    private static OkHttpClient getClient(Context context) {

        return new OkHttpClient.Builder()
                .connectionPool(new ConnectionPool(8, 10,
                        TimeUnit.MINUTES))
                .readTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .build();
    }

    public static NetworkManager get(Context context) {
        if (instance == null) {
            synchronized (NetworkManager.class) {
                if (instance == null) {
                    instance = new NetworkManager(context.getApplicationContext());
                }
            }
        }
        return instance;
    }

    public GET get() {
        return new GET(mOkHttpClient);
    }

    public POST post() {
        return new POST(mOkHttpClient);
    }
}
