package bzu.android.mohamad.jaradat1193265.entertainmentapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class SpecificNewsActivity extends AppCompatActivity {

    protected final static String LATEST_QUERY_ARRAY_KEY = "LATEST_QUERY_ARRAY";
    protected static List<News> newsList;
    protected static List<String> queryList;
    RequestQueue requestQueue;
    EditText searchBar;
    ImageButton searchButton;
    Spinner categorySpinner;
    Spinner channelSpinner;
    Button clearQueryButton;
    public SharedPreferences sharedPreferences;
    public SharedPreferences.Editor sharedPreferencesEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specific_news);

        setupSharedPreferences();
        requestQueue = Volley.newRequestQueue(this);
        newsList = new ArrayList<>();
        hookLayouts();


        setAdapter(categorySpinner,News.CATEGORIES);
        setAdapter(channelSpinner,News.CHANNELS);


        searchButton.setOnClickListener(this::onSearchListener);
        clearQueryButton.setOnClickListener(action->emptyQuery());
    }

    // this was implemented to make sure the query is saved in case the app crashed or the user by mistake left the page
     // in any case or error.
    @Override
    protected void onStop() {
        super.onStop();
        updateQueryList();
        putInSharedReferences(LATEST_QUERY_ARRAY_KEY, queryList);
    }
// retrieving the saved query from when the user left, so they can continue form there
    @Override
    protected void onResume() {
        super.onResume();
        loadLastQuery();
    }

    // this method links the views in the xml file with the backened variables
    private void hookLayouts(){
        searchBar = findViewById(R.id.searchBar);
        searchButton = findViewById(R.id.searchButton);
        categorySpinner= findViewById(R.id.categorySpinner);
        channelSpinner = findViewById(R.id.channelSpinner);
        clearQueryButton = findViewById(R.id.clearQueryButton);
    }
    // this methods sets up the shared preferences and make them ready to use
    public void setupSharedPreferences() {
        sharedPreferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        sharedPreferencesEditor = sharedPreferences.edit();
    }
    // this method is used to convert a given list to json format and save it in the preferences
    public void putInSharedReferences(String KEY,List<String> list){
        Gson GSON = new Gson();

        String JSON_STRING = GSON.toJson(list);
        sharedPreferencesEditor.putString(KEY, JSON_STRING);
        sharedPreferencesEditor.apply();
    }
    // this method is used in the onresume to load the last query
    private void loadLastQuery(){
       Gson GSON = new Gson();
       String JSON_STRING = sharedPreferences.getString(LATEST_QUERY_ARRAY_KEY,null);
       Type objectType = new TypeToken<ArrayList<String>>() {}.getType();
       queryList = GSON.fromJson(JSON_STRING,objectType);
       fillViewsFromQueryList();
    }
// this method fills the views with the given last query, alos used in the onresume with load query
    private void fillViewsFromQueryList(){
        if (queryList !=null) {
            searchBar.setText(queryList.get(0));
            String channel = queryList.get(2);
            String category = queryList.get(1);
            if (channel != null) {
                int position = ((ArrayAdapter<String>) channelSpinner.getAdapter()).getPosition(channel);
                channelSpinner.setSelection(position);
            }
            if (category!= null) {
                int position = ((ArrayAdapter<String>) categorySpinner.getAdapter()).getPosition(category);
                categorySpinner.setSelection(position);
            }
        }else
          queryList = new ArrayList<>();
    }
    // this method is used in the onstop to assist it to accomplish the life cycle needed
    private void updateQueryList(){

        String query = searchBar.getText().toString();
        String selectedCategory = categorySpinner.getSelectedItem().toString();
        String selectedChannel = channelSpinner.getSelectedItem().toString();

        if (queryList !=null) {
            queryList.clear();
            queryList.add(query);
            queryList.add(selectedCategory);
            queryList.add(selectedChannel);
        }
    }

    // this method set the entries in the vies to their default (empties them)
    private void emptyQuery(){
        if (queryList!=null) {
           queryList.clear();
           searchBar.setText("");
           channelSpinner.setSelection(0);
           categorySpinner.setSelection(0);
        }else
           Toast.makeText(this,"Already Cleared", Toast.LENGTH_SHORT).show();
    }

    // this method sets the adapter for a given spinner, it assign the list given to fill the choices in the spinner

    private void setAdapter( Spinner spinner, List<String> list){
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    // this method starts when the search button is clicked, it does the necessary modifications
    // on the entries by the user and then creates the correct URL and calls the api
    private void onSearchListener(View view){

        String query = searchBar.getText().toString();
        String selectedCategory = categorySpinner.getSelectedItem().toString();
        String selectedChannel = channelSpinner.getSelectedItem().toString();

        queryList.clear();
        queryList.add(query.toLowerCase());
        queryList.add(selectedCategory.toLowerCase());
        queryList.add(selectedChannel);


        if(query.trim().isEmpty()
                && selectedCategory.equalsIgnoreCase("any")
                && selectedChannel.equalsIgnoreCase("any")){
            Toast.makeText(this,"At least one information is needed",Toast.LENGTH_LONG).show();
            return;
        }
        if (query.length()>=500) {//query too long (500>)
            Toast.makeText(this,"Query is Too Long",Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            String channel = !selectedChannel.equalsIgnoreCase("any")? "&domain="+selectedChannel:"";
            String category = !selectedCategory.equalsIgnoreCase("any")? "&category="+selectedCategory:"";
            query = URLEncoder.encode(query, "UTF-8");
            query = !query.isEmpty()? "&qInTitle="+query : "";
            String API_KEY = "pub_35494cda00bc2d3b0a3a005f503ad2b636b76";
            String baseUrl = "https://newsdata.io/api/1/news?language=en&size=10";
            String url = baseUrl+query+channel+category+"&apikey="+API_KEY;
            Log.d("TAG12", "searchBarListener: "+url);
            getNews(url);
        } catch (UnsupportedEncodingException e) {
            Toast.makeText(this,"Query Can't be Encoded",Toast.LENGTH_LONG).show();
        }
    }
    // this is the method responsible for calling the volley and fetch the fata from the api
    // i'm limited to 10 results per page, and about 200 api calls in a day and 20 api calls per 15 minutes
    // the result coming back is 10 news of the query given in titles of news
    // based on the category and/ or channel selected

    private void getNews(String url) {
        if (url==null)
            return;
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
                                Log.d("Volley", "Response "+i+": " + title + " content " + content + " date" + publishDate);
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
    // this method starts the view news activity to show the result retrieved from the api
    private void viewNews(){
        Gson GSON = new Gson();
        String JSON_STRING = GSON.toJson(newsList);
        newsList.clear();
        Intent intent = new Intent(this, ViewNewsActivity.class);
        intent.putExtra(News.NEWS_LIST_KEY, JSON_STRING);
        JSON_STRING = GSON.toJson(queryList);
        intent.putExtra(LATEST_QUERY_ARRAY_KEY,JSON_STRING);
        startActivity(intent);
    }
}