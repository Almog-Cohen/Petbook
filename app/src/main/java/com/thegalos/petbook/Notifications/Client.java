package com.thegalos.petbook.Notifications;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Client {

    private  static Retrofit retrofit=null;

//      public static Gson gson = new GsonBuilder()
//            .setLenient()
//            .create();

    public static Retrofit getClient(String url){
        if (retrofit==null){
            retrofit=new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

        }
        return retrofit;
    }
}
