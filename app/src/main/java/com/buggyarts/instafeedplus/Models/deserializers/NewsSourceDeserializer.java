package com.buggyarts.instafeedplus.Models.deserializers;

import com.buggyarts.instafeedplus.Models.news.NewsSource;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class NewsSourceDeserializer implements JsonDeserializer<NewsSource> {


    @Override
    public NewsSource deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        NewsSource newsSource = new NewsSource();

        final JsonObject jsonObject = json.getAsJsonObject();

        if (jsonObject.get("id") != null) {
            final String id = jsonObject.get("id").getAsString();
            newsSource.setId(id);
        }

        if (jsonObject.get("name") != null) {
            final String name = jsonObject.get("name").getAsString();
            newsSource.setName(name);
        }

        return newsSource;
    }
}
