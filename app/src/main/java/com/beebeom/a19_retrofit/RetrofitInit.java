package com.beebeom.a19_retrofit;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
//레트로핏 객체 초기화
public class RetrofitInit {
    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://apis.data.go.kr/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    WeatherServer server = retrofit.create(WeatherServer.class);
}
