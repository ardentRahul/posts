package com.example.demo.Network;

import com.example.demo.Model.DemoData;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GetPosts {

    @GET("v2/59ac28a9100000ce0bf9c236")
    Call<DemoData> getPosts(@Query("page") int pageNo);
}
