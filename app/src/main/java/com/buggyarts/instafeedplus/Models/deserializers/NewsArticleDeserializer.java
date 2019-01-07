package com.buggyarts.instafeedplus.Models.deserializers;

import com.buggyarts.instafeedplus.Models.news.NewsArticle;
import com.buggyarts.instafeedplus.Models.news.NewsSource;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class NewsArticleDeserializer implements JsonDeserializer<NewsArticle> {


    @Override
    public NewsArticle deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        NewsArticle newsArticle = new NewsArticle();

        final JsonObject jsonObject = json.getAsJsonObject();

        if (jsonObject.has("author")) {
            if (jsonObject.get("author") != null) {
                final String author = jsonObject.get("author").getAsString();
                newsArticle.setAuthor(author);
            }
        }

        if (jsonObject.get("title") != null) {
            final String title = jsonObject.get("title").getAsString();
            newsArticle.setTitle(title);
        }

        if (jsonObject.get("description") != null) {
            final String description = jsonObject.get("description").getAsString();
            newsArticle.setDescription(description);
        }

        if (jsonObject.get("url") != null) {
            final String url = jsonObject.get("url").getAsString();
            newsArticle.setUrl(url);
        }

        if (jsonObject.get("urlToImage") != null) {
            final String urlToImage = jsonObject.get("urlToImage").getAsString();
            newsArticle.setThumbnail_url(urlToImage);
        }

        if (jsonObject.get("publishedAt") != null) {
            final String publishedAt = jsonObject.get("publishedAt").getAsString();
            newsArticle.setPublishedAt(publishedAt);
        }

        if (jsonObject.get("content") != null) {
            final String content = jsonObject.get("content").getAsString();
            newsArticle.setContent(content);
        }

        if (jsonObject.get("source")!=null) {
            if (!jsonObject.get("source").isJsonArray()) {
                NewsSource newsSource = context.deserialize(jsonObject.get("source"), NewsSource.class);
                newsArticle.setNewsSource(newsSource);
            }
        }

        return newsArticle;
    }
}
