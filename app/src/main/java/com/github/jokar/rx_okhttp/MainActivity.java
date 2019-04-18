package com.github.jokar.rx_okhttp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.alibaba.fastjson.TypeReference;

import io.reactivex.functions.Consumer;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        NetworkManager.get(getApplicationContext())
                .get()
                .<ContentEntity>getAsync("https://news-at.zhihu.com/api/4/news/3892357",
                        new TypeReference<ContentEntity>() {
                        }.getType())
                .subscribe(new Consumer<ContentEntity>() {
                    @Override
                    public void accept(ContentEntity contentEntity) {
                        Log.d(TAG, contentEntity.toString());
                    }
                });
    }
}
