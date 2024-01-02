package bzu.android.mohamad.jaradat1193265.entertainmentapp;


import androidx.annotation.NonNull;
import java.util.Arrays;
import java.util.List;

public class News {
    protected final static List<String> CATEGORIES = Arrays.asList("any", "business", "crime", "domestic", "education",
            "entertainment", "environment", "food", "health", "other" ,"politics",
            "science", "sports", "technology", "top", "tourism", "world");
    protected final static List<String> CHANNELS = Arrays.asList("any","bbc","nytimes","aljazeera", "cnn","rt","foxnews");
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

    @NonNull
    @Override
    public String toString() {
        return title;
    }
}
