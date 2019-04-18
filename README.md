## Rx-okhttp
模仿retrofit简单封装okhttp为rxjava。没有过多的代码，使用简单可延伸


### 导入


### 返回数据解析方式
- gson（默认首先使用）
- fastjson

只要导入这两个包任何一个就可以

### 使用
- 创建[GET](/library/src/main/java/com/github/jokar/rx_okhttp/GET.java)变量（见[NetworkManager](/app/src/main/java/com/github/jokar/rx_okhttp/NetworkManager.java)类）

- 调用get方法
    ```
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
    ```