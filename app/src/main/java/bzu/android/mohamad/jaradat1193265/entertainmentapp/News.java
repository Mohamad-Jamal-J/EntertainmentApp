package bzu.android.mohamad.jaradat1193265.entertainmentapp;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Arrays;
import java.util.List;

public class News implements Comparable<News>{
    protected final static List<String> CATEGORIES = Arrays.asList("ANY", "BUSINESS", "CRIME", "DOMESTIC", "EDUCATION",
            "ENTERTAINMENT", "ENVIRONMENT", "FOOD", "HEALTH", "OTHER" ,"POLITICS",
            "SCIENCE", "SPORTS", "TECHNOLOGY", "TOP", "TOURISM", "WORLD");
    protected final static List<String> CHANNELS = Arrays.asList("ANY","BBC","NYTIMES","ALJAZEERA", "CNN","RT","FOXNEWS");
    protected final static String NEWS_LIST_KEY = "NEWS_LIST_KEY";
    protected final static String SAVED_NEWS_LIST = "SAVED_NEWS_LIST";

    String title;
    String content;
    String publishDate;
    String channel;

    public News(String title, String content, String publishDate,String channel) {
        setTitle(title);
        setContent(content);
        setPublishDate(publishDate);
        setChannel(channel);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        if (title.equalsIgnoreCase("null"))
            this.title = "No Title Was Provided";
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
        if (content.equalsIgnoreCase("null"))
            this.content = "No Content Were Provided (through API)";
    }

    public String getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
        if (publishDate.equalsIgnoreCase("null"))
            this.publishDate = "No Date Was Provided";
    }
    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
        if (channel.equalsIgnoreCase("null"))
            this.channel = "Channel not specified";
    }

    @Override
    public int compareTo(News o) {
        int titleCompare = this.title.compareToIgnoreCase(o.title);
        if (titleCompare != 0) {
            return titleCompare;
        }
        int contentCompare = this.content.compareToIgnoreCase(o.content);
        if (contentCompare != 0) {
            return contentCompare;
        }
        int channelCompare = this.channel.compareToIgnoreCase(o.channel);
        if (channelCompare != 0) {
            return contentCompare;
        }
        return this.publishDate.compareToIgnoreCase(o.publishDate);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof News)
            return this.compareTo((News) obj) == 0;
        return false;
    }

    @NonNull
    @Override
    public String toString() {
        return title;
    }

}
