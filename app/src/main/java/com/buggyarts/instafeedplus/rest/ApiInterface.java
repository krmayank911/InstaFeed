package com.buggyarts.instafeedplus.rest;

import com.buggyarts.instafeedplus.Models.news.ResponseNewsApi;
import com.buggyarts.instafeedplus.Models.news.ResponseSources;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInterface {

    @GET("top-headlines")
    Call<ResponseNewsApi> getNewsArticles(@Query("apiKey") String apiKey,
                                          @Query("country") String country,
                                          @Query("pageSize") int pageSize,
                                          @Query("page") int page);

    @GET("top-headlines")
    Call<ResponseNewsApi> getNewsArticlesByCategory(@Query("apiKey") String apiKey,
                                                    @Query("country") String country,
                                                    @Query("category") String category,
                                                    @Query("pageSize") int pageSize,
                                                    @Query("page") int page);

    @GET("top-headlines")
    Call<ResponseNewsApi> getNewsArticlesBySources(@Query("apiKey") String apiKey,
                                                   @Query("sources") String sources,
                                                   @Query("pageSize") int pageSize,
                                                   @Query("page") int page);

    @GET("top-headlines")
    Call<ResponseNewsApi> searchNewsArticles(@Query("apiKey") String apiKey,
                                             @Query("q") String q);

    @GET("everything")
    Call<ResponseNewsApi> searchEveryThing(@Query("apiKey") String apiKey,
                                           @Query("q") String q,
                                           @Query("page") Integer page);

    @GET("everything")
    Call<ResponseNewsApi> searchEveryThingWithParams(@Query("apiKey") String apiKey,
                                                     @Query("q") String q,
                                                     @Query("page") Integer page,
                                                     @Query("language") String language,
                                                     @Query("sortBy") String sortBy,
                                                     @Query("to") String to,
                                                     @Query("from") String from);

    @GET("sources")
    Call<ResponseSources> getAllSources(@Query("apiKey") String apiKey);

    @GET("sources")
    Call<ResponseSources> getSourcesForCountry(@Query("apiKey") String apiKey,
                                               @Query("country") String country,
                                               @Query("language") String language);

    @GET("sources")
    Call<ResponseSources> getSourcesForCategory(@Query("apiKey") String apiKey,
                                               @Query("category") String category,
                                               @Query("language") String language);

}
