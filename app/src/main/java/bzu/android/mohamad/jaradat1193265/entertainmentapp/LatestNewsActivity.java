package bzu.android.mohamad.jaradat1193265.entertainmentapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LatestNewsActivity extends AppCompatActivity {

    protected static List<News> newsList;
    RequestQueue requestQueue;
    Map<CardView,Integer> channelsMap;
    CardView bbcCardView;
    CardView nyTimesCardView;
    CardView aljazeeraCardView;
    CardView cnnCardView;
    CardView russiaTodayCardView;
    CardView foxnewsCardView;

    // In this activity i believe the lifecycle methods aren't really necessary
    // so i believe onCreate is enough.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_latest_news);

        requestQueue = Volley.newRequestQueue(this);
        channelsMap = new HashMap<>();
        newsList = new ArrayList<>();

        hookLayouts();
        initializeMap();
        setActionsForViews();
    }
    private void hookLayouts(){
        bbcCardView = findViewById(R.id.bbcCardView);
        nyTimesCardView = findViewById(R.id.nyTimesCardView);
        aljazeeraCardView = findViewById(R.id.aljazeeeraCardView);
        cnnCardView = findViewById(R.id.cnnCardView);
        russiaTodayCardView = findViewById(R.id.russiaTodayCardView);
        foxnewsCardView = findViewById(R.id.foxnewsCardView);
    }
    private void initializeMap(){
        channelsMap.put(bbcCardView,1);
        channelsMap.put(nyTimesCardView,2);
        channelsMap.put(aljazeeraCardView,3);
        channelsMap.put(cnnCardView,4);
        channelsMap.put(russiaTodayCardView,5);
        channelsMap.put(foxnewsCardView,6);
    }
    private void setActionsForViews(){
        bbcCardView.setOnClickListener(this::onActionListener);
        nyTimesCardView.setOnClickListener(this::onActionListener);
        aljazeeraCardView.setOnClickListener(this::onActionListener);
        cnnCardView.setOnClickListener(this::onActionListener);
        russiaTodayCardView.setOnClickListener(this::onActionListener);
        foxnewsCardView.setOnClickListener(this::onActionListener);
    }
    private void onActionListener(View cardView){
        if (cardView != null && channelsMap.containsKey(cardView)) {
            int index = channelsMap.get(cardView);
            String channel = News.CHANNELS.get(index);
            getNews(channel);
        }
    }
    private void getNews(String channel) {
        if (channel==null)
            return;
        String API_KEY = "pub_35494cda00bc2d3b0a3a005f503ad2b636b76";

        //last 48 hours    news endpoint   10 news for my free plan
        String url = "https://newsdata.io/api/1/news?language=en&size=10&domain="+channel+"&apikey="+API_KEY;

        try{
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url, null,
                response -> {
                    try {
                        String status = response.getString("status");
                        if (status.equalsIgnoreCase("success")) {
                            JSONArray results = response.getJSONArray("results");
                            int length = Math.min(results.length(), 10);
                            for (int i = 0; i < length; i++) {
                                JSONObject newsObject = results.getJSONObject(i);
                                String title = newsObject.getString("title");
                                String content = newsObject.getString("content");
                                String publishDate = newsObject.getString("pubDate");
                                String source = newsObject.getString("source_id");
                                News news = new News(title,content,publishDate, source);
                                newsList.add(news);
                                Log.d("Volley", "Response "+i+": " + title + " content " + content + " date" + publishDate+ " channel" + source);
                            }
                            viewNews();
                        }else{
                            Log.d("Volley", "status "+status);
                        }
                    }catch (JSONException e){
                        Log.d("Volley", "Exception: " +e);
                    }
                },
                error -> {
                    Log.e("Volley", "Request error: " + error.toString());
                }
            );
            requestQueue.add(jsonObjectRequest);

        }catch (Exception error){
            Log.d("Volley", "Exception: "+error);
        }
    }
    private void viewNews(){
        Gson GSON = new Gson();
        String JSON_STRING = GSON.toJson(newsList);
        newsList.clear();
        Intent intent = new Intent(this, ViewNewsActivity.class);
        intent.putExtra(News.NEWS_LIST_KEY, JSON_STRING);
        startActivity(intent);
    }
}