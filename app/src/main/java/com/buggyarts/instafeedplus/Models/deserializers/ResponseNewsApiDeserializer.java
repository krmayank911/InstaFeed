package com.buggyarts.instafeedplus.Models.deserializers;

import com.buggyarts.instafeedplus.Models.news.NewsArticle;
import com.buggyarts.instafeedplus.Models.news.ResponseNewsApi;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class ResponseNewsApiDeserializer implements JsonDeserializer<ResponseNewsApi> {


    @Override
    public ResponseNewsApi deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)throws JsonParseException {

        ResponseNewsApi responseNewsApi =new ResponseNewsApi();
        final JsonObject jsonObject = json.getAsJsonObject();

        if (jsonObject.get("status") != null) {
            final String status = jsonObject.get("status").getAsString();
            responseNewsApi.setStatus(status);
        }

        if (jsonObject.get("totalResults") != null){
            final Integer totalResults = jsonObject.get("totalResults").getAsInt();
            responseNewsApi.setTotalResults(totalResults);
        }

        if (jsonObject.has("articles")) {
            if (jsonObject.get("articles") != null) {
                if (jsonObject.get("articles").isJsonArray()) {
                    Type newsArticleType = new TypeToken<ArrayList<NewsArticle>>() {
                    }.getType();
                    ArrayList<NewsArticle> newsArticles = context.deserialize(jsonObject.get("articles"), newsArticleType);
                    responseNewsApi.setNewsArticles(newsArticles);
                }
            }
        }

        return responseNewsApi;
    }
}
