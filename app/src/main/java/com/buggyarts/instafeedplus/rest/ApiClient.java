package com.buggyarts.instafeedplus.rest;

import com.buggyarts.instafeedplus.Models.deserializers.ResponseNewsApiDeserializer;
import com.buggyarts.instafeedplus.Models.news.ResponseNewsApi;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    public static final String BASE_URL = "https://newsapi.org/v2/";

    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {

            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(ResponseNewsApi.class, new ResponseNewsApiDeserializer());
//            gsonBuilder.registerTypeAdapter(ResponseSources.class, new ResponseSourcesDeserializer());
//            gsonBuilder.registerTypeAdapter(NewsArticle.class, new NewsArticleDeserializer());
//            gsonBuilder.registerTypeAdapter(NewsSource.class, new NewsSourceDeserializer());


            gsonBuilder.setLenient();
            Gson myGson = gsonBuilder.create();

            final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create(myGson))
                    .build();
        }
        return retrofit;
    }

}
