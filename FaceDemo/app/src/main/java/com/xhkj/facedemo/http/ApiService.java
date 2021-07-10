package com.xhkj.facedemo.http;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface ApiService {

    @Headers({"Content-Type: application/json"})
    @POST("/")
    Call<ResponseBody> getResponse();

    // 这个是得到 json字符串, 返回ResponseBody
    @Headers({"Content-Type: application/json"})
    @POST("/")
    Call<ResponseBody> getResponseResult(@Body RequestBody requestBody);
}
