package com.buggyarts.instafeedplus.Models.deserializers;

import com.buggyarts.instafeedplus.Models.news.ResponseSources;
import com.buggyarts.instafeedplus.utils.Source;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class ResponseSourcesDeserializer implements JsonDeserializer<ResponseSources> {


    @Override
    public ResponseSources deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)throws JsonParseException {

        ResponseSources responseSources =new ResponseSources();
        final JsonObject jsonObject = json.getAsJsonObject();

        if (jsonObject.get("status") != null) {
            final String status = jsonObject.get("status").getAsString();
            responseSources.setStatus(status);
        }

        if (jsonObject.has("sources")) {
            if (jsonObject.get("sources") != null) {
                if (jsonObject.get("sources").isJsonArray()) {
                    Type sourceType = new TypeToken<ArrayList<Source>>() {
                    }.getType();
                    ArrayList<Source> sources = context.deserialize(jsonObject.get("sources"), sourceType);
                    responseSources.setSources(sources);
                }
            }
        }

        return responseSources;
    }
}
