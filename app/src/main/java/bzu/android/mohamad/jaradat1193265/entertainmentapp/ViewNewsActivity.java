package bzu.android.mohamad.jaradat1193265.entertainmentapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ViewNewsActivity extends AppCompatActivity {
    List<News> newsList;
    List<News> selectedNewsList;
    List<News> savedNewsList;
    ListView newsListView;
    List<String> queryList;
    ImageButton editButton;
    Button saveNewsListButton;
    ArrayAdapter<News> newsListAdapter;
    boolean editModeOn;
    public SharedPreferences sharedPreferences;
    public SharedPreferences.Editor sharedPreferencesEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_news);

        loadNewsListFromIntent();
        setupSharedPreferences();

        selectedNewsList = new ArrayList<>();
        editModeOn =false;

        hookLayouts();
        setLayoutsListeners();
        loadSavedNewsListFromPreferences();

        if (newsList.size()==0){
            Intent intent = getIntent();
            Gson GSON = new Gson();
            String JSON_STRING = intent.getStringExtra(SpecificNewsActivity.LATEST_QUERY_ARRAY);
            Type objectType = new TypeToken<ArrayList<String>>() {}.getType();
            queryList = GSON.fromJson(JSON_STRING,objectType);

            if (queryList !=null && !queryList.isEmpty())
                showEmptyNews(this, queryList.get(0), queryList.get(1), queryList.get(2));
            else
                Toast.makeText(this,"Something went wrong",Toast.LENGTH_LONG).show();
        }else
            setListViewAdapter(newsList,android.R.layout.simple_list_item_1);

    }
    public void setupSharedPreferences() {
        sharedPreferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        sharedPreferencesEditor = sharedPreferences.edit();
    }
    public void putInSharedReferences(String KEY,List<News> list){
        Gson GSON = new Gson();

        String JSON_STRING = GSON.toJson(list);
        sharedPreferencesEditor.putString(KEY, JSON_STRING);
        sharedPreferencesEditor.apply();
    }

    private void hookLayouts() {
        newsListView = findViewById(R.id.newsList);
        editButton  = findViewById(R.id.editButton);
        saveNewsListButton =findViewById(R.id.saveNewsList);
    }
    private void loadNewsListFromIntent(){
        Intent intent = getIntent();

        String JSON_STRING = intent.getStringExtra(News.NEWS_LIST_KEY);

        Gson GSON = new Gson();

        Type objectType = new TypeToken<ArrayList<News>>(){}.getType();
        newsList = GSON.fromJson(JSON_STRING,objectType);

        if (newsList==null)
            newsList = new ArrayList<>();
    }
    private void loadSavedNewsListFromPreferences(){
        Gson GSON = new Gson();

        String JSON_STRING = sharedPreferences.getString(News.SAVED_NEWS_LIST,null);
        Type objectType = new TypeToken<ArrayList<News>>(){}.getType();
        savedNewsList = GSON.fromJson(JSON_STRING,objectType);

        if (savedNewsList==null)
            savedNewsList = new ArrayList<>();
    }

    // this method will set the array adapter and it's listeners based on a given
    // list and the preferred layout (choice mode). which is responsible for providing the
    // multiple functionalities of single & long click listeners on items in the given list based on
    // the current mode (edit mode on/off).
    private void setListViewAdapter(List<News> list, int choiceMode) {
        newsListAdapter = new ArrayAdapter<>(this, choiceMode, list);

        AdapterView.OnItemClickListener itemClickListener =
                (parent, view, position, id) -> oneClickAdapterListener(list, view, position);

        newsListView.setOnItemClickListener(itemClickListener);
        newsListView.setAdapter(newsListAdapter);
    }

    //this method manages multiple and single item choices for the adapter
    // when edit mode is on, the user can select multiple items simply by clicking on them.
    // when it's off, a simple dialog will be displayed to show the full details of the selected news
    private void oneClickAdapterListener(List<News> list, View view, int position) {
        News currentNews = list.get(position);

        if (editModeOn){
            CheckedTextView checkedTextView = (CheckedTextView) view;
            if (checkedTextView.isChecked())
                selectedNewsList.add(currentNews);
            else
                selectedNewsList.remove(currentNews);
        }else
            showCurrentNews(this, currentNews);
    }

    // this method contains all the click listeners for the views in activity_main.xml file
    private void setLayoutsListeners() {
        editButton.setOnClickListener(this::editModeClickListener);
        saveNewsListButton.setOnClickListener(this::saveNewsOnClickListener);
    }

    // this method will save the news to shared preferences
    private void saveNewsOnClickListener(View action) {
      if (editModeOn)
          saveNews(selectedNewsList);
      else
          saveNews(newsList);
    }
    // this method will save the news to shared preferences based on whether edit mode is on or off
    private void saveNews(List<News> list) {
        int numOfSavedNews=0;
        for (News currentNews: list)
            if (!savedNewsList.contains(currentNews)) {
                savedNewsList.add(currentNews);
                numOfSavedNews++;
            }
        String message = numOfSavedNews+" New Items Saved";

        Toast.makeText(this,message,Toast.LENGTH_LONG).show();
        newsListView.clearChoices();
        newsListAdapter.notifyDataSetChanged();
        putInSharedReferences(News.SAVED_NEWS_LIST, savedNewsList);
    }

    // this method will toggle the edit mode on/off and as a result
    // it changes the listview layout mode to multiple or simple.
    private void editModeClickListener(View view) {
        editModeOn=!editModeOn;
        changeEditIcon();
        if (editModeOn){
            newsListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
            setListViewAdapter(newsList, android.R.layout.simple_list_item_multiple_choice);
        }else {
            newsListView.clearChoices();
            newsListView.setChoiceMode(ListView.CHOICE_MODE_NONE);
            setListViewAdapter(newsList, android.R.layout.simple_list_item_1);
            selectedNewsList.clear();
        }
    }
    private void changeEditIcon(){
        Drawable drawable;
        if (editModeOn)
            drawable = ContextCompat.getDrawable(this, R.drawable.image_bg_on);
        else
            drawable = ContextCompat.getDrawable(this, R.drawable.image_bg);
        editButton.setBackground(drawable);
    }
    private void showCurrentNews(Context context, News currentNews) {
        if (currentNews==null){
            Toast.makeText(this, "Empty Item", Toast.LENGTH_SHORT).show();
            return;
        }

        String title = currentNews.getTitle();
        String publishDate = currentNews.getPublishDate();
        String content = currentNews.getContent();


        View view = LayoutInflater.from(context).inflate(R.layout.alert_dialog_custom, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(view);
        final AlertDialog newsDialog = alertDialogBuilder.create();

        TextView newsTitleTextView = view.findViewById(R.id.newsTitleTextView);
        TextView publishDateTextview = view.findViewById(R.id.date);
        TextView newsContentTextView = view.findViewById(R.id.newsContentTextView);
        Button closeButton = view.findViewById(R.id.closeButton);
        Button saveButton = view.findViewById(R.id.saveButton);


        newsTitleTextView.setText( (title!=null)? title:"Empty Title");
        publishDateTextview.setText((publishDate!=null)? publishDate:"No Date Provided");
        newsContentTextView.setText((content!=null)? content:"No Description Provided");


        saveButton.setOnClickListener(action->{
            String message;
            if (!savedNewsList.contains(currentNews)) {
                savedNewsList.add(currentNews);
                message = "Saved";
            }else
                message = "Already saved";
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            putInSharedReferences(News.SAVED_NEWS_LIST,savedNewsList);
            newsDialog.dismiss();
        });

        closeButton.setOnClickListener(action-> newsDialog.dismiss());

        newsDialog.show();
    }

    private void showEmptyNews(Context context, String query, String category, String channel) {
        View view = LayoutInflater.from(context).inflate(R.layout.alert_dialog_empty_custom, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(view);
        final AlertDialog newsDialog = alertDialogBuilder.create();

        TextView queryTextView = view.findViewById(R.id.queryTextView);
        TextView channelTextview = view.findViewById(R.id.channelTextView);
        TextView categoryTextView = view.findViewById(R.id.categoryTextView);
        Button closeButton = view.findViewById(R.id.goBackButton);


        query = queryTextView.getText().toString() +" "+ (query.isEmpty()? "-Empty-":query);
        channel = channelTextview.getText().toString() +" "+channel;
        category = categoryTextView.getText().toString() +" "+category;
        queryTextView.setText(query);
        channelTextview.setText(channel);
        categoryTextView.setText(category);

        closeButton.setOnClickListener(action-> {
            newsDialog.dismiss();
            finish(); // i don't want the empty activity to stay in the background

        });

        newsDialog.show();
    }



}