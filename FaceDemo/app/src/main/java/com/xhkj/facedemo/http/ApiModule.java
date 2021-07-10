package com.xhkj.facedemo.http;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * created by ThinkPad on 2019/12/6
 * Describe
 */

public class ApiModule {

    private static String API_1 = "https://www.baidu.com";
    private static String API_2 = "https://github.com";

    private static final String BASE_URL = API_1;

    private static ApiModule apiModule;
    private ApiService apiService;

    public static ApiModule getInstance(){
        return apiModule == null ? apiModule = new ApiModule() :apiModule;
    }

    private ApiModule(){
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS) // 设置连接超时时间
                .readTimeout(60, TimeUnit.SECONDS) // 设置读取超时时间
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient)
                .build();

        apiService = retrofit.create(ApiService.class);
    }

    public ApiService getApiService(){
        return apiService;
    }

}
