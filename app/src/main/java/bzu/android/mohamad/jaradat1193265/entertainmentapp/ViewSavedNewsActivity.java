package bzu.android.mohamad.jaradat1193265.entertainmentapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
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

public class ViewSavedNewsActivity extends AppCompatActivity {
    List<News> selectedNewsList;
    List<News> savedNewsList;
    ListView newsListView;
    ImageButton editButton;
    Button deleteSaveNewsButton;
    ArrayAdapter<News> newsListAdapter;
    AlertDialog alertDialog;
    boolean editModeOn;
    public SharedPreferences sharedPreferences;
    public SharedPreferences.Editor sharedPreferencesEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_news);

        selectedNewsList = new ArrayList<>();
        editModeOn =false;


        setupSharedPreferences();
        loadSavedNewsListFromPreferences();
        hookLayouts();
        setLayoutsListeners();

        if (savedNewsList.size()==0){
            setContentView(R.layout.activity_view_saved_news);
        }else
            setListViewAdapter(savedNewsList,android.R.layout.simple_list_item_1);

    }

    @Override
    protected void onStop() {
        super.onStop();
        if(alertDialog!=null && alertDialog.isShowing())
            alertDialog.dismiss();
    }

    private void loadSavedNewsListFromPreferences(){
        Gson GSON = new Gson();

        String JSON_STRING = sharedPreferences.getString(News.SAVED_NEWS_LIST_KEY,null);
        Type objectType = new TypeToken<ArrayList<News>>(){}.getType();
        savedNewsList = GSON.fromJson(JSON_STRING,objectType);

        if (savedNewsList==null)
            savedNewsList = new ArrayList<>();
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
        deleteSaveNewsButton =findViewById(R.id.saveNewsList);
        deleteSaveNewsButton.setText("Delete saved News");
        deleteSaveNewsButton.setBackgroundColor(Color.parseColor("#77CD4940"));

    }
    private void setLayoutsListeners() {
        editButton.setOnClickListener(this::editModeClickListener);
        deleteSaveNewsButton.setOnClickListener(this::deleteNewsOnClickListener);
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
    // this method will toggle the edit mode on/off and as a result
    // it changes the listview layout mode to multiple or simple.
    private void editModeClickListener(View view) {
        editModeOn=!editModeOn;
        changeEditIcon();
        if (editModeOn){
            newsListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
            setListViewAdapter(savedNewsList, android.R.layout.simple_list_item_multiple_choice);
        }else {
            newsListView.clearChoices();
            newsListView.setChoiceMode(ListView.CHOICE_MODE_NONE);
            setListViewAdapter(savedNewsList, android.R.layout.simple_list_item_1);
            selectedNewsList.clear();
        }
    }
    // this method will change the background of the editing switch to give the user a better ux
    // about which mode they are using now
    private void changeEditIcon(){
        Drawable drawable;
        if (editModeOn)
            drawable = ContextCompat.getDrawable(this, R.drawable.image_bg_on);
        else
            drawable = ContextCompat.getDrawable(this, R.drawable.image_bg);
        editButton.setBackground(drawable);
    }
    // this method shows a custom alert dialog to explore the full content of the selected saved item news
    // the users will also have the option to save the news they are reading
    private void showCurrentNews(Context context, News currentNews) {
        if (currentNews==null){
            Toast.makeText(this, "Empty Item", Toast.LENGTH_SHORT).show();
            return;
        }

        String title = currentNews.getTitle();
        String publishDate = currentNews.getPublishDate();
        String content = currentNews.getContent();
        String channel = currentNews.getChannel();


        View view = LayoutInflater.from(context).inflate(R.layout.alert_dialog_custom, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(view);
        alertDialog = alertDialogBuilder.create();

        TextView newsTitleTextView = view.findViewById(R.id.newsTitleTextView);
        TextView publishDateTextview = view.findViewById(R.id.date);
        TextView newsContentTextView = view.findViewById(R.id.newsContentTextView);
        Button closeButton = view.findViewById(R.id.closeButton);
        Button deleteButton = view.findViewById(R.id.saveButton);
        deleteButton.setText("Remove");

        String channelAndDate = ((channel!=null)?  channel:"Unknown Source")+", "+ ((publishDate!=null)? publishDate:"No Date Provided");
        newsTitleTextView.setText( (title!=null)? title:"Empty Title");
        publishDateTextview.setText(channelAndDate);
        newsContentTextView.setText((content!=null)? content:"No Description Provided");


        deleteButton.setOnClickListener(action->{
            String message;
            if (savedNewsList.contains(currentNews)) {
                savedNewsList.remove(currentNews);
                newsListAdapter.notifyDataSetChanged();
                putInSharedReferences(News.SAVED_NEWS_LIST_KEY,savedNewsList);
                message = "Deleted";
            }else
                message = "Already removed";
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            alertDialog.dismiss();
        });

        closeButton.setOnClickListener(action-> alertDialog.dismiss());

        alertDialog.show();
    }
    // this method decides and assigns which method should be assigned to the listener of the button
    // based on the current mode (editing on/off)
    private void deleteNewsOnClickListener(View action) {
        if (editModeOn)
            deleteSelectedNews(selectedNewsList);
        else
            showWarningDialog();
    }
    // this method deletes the selected news in the saved list
    private void deleteSelectedNews(List<News> list) {
        int numOfDeletedNews=0;
        for (News currentNews: list)
            if (savedNewsList.contains(currentNews)) {
                savedNewsList.remove(currentNews);
                newsListAdapter.notifyDataSetChanged();
                numOfDeletedNews++;
            }
        String message = numOfDeletedNews+" Old Items Deleted";

        Toast.makeText(this,message,Toast.LENGTH_LONG).show();
        newsListView.clearChoices();
        putInSharedReferences(News.SAVED_NEWS_LIST_KEY, savedNewsList);
    }
    // this method shows a dialog to warn the user about the danger of deleting all the saved data
    // it's a last resort to cancel on the operation, in case the user clicked by mistake on it
    private void showWarningDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Warning")
                .setMessage("You're about to delete all of your saved news");

        builder.setPositiveButton("YES DELETE",

                (positiveDialog, id)->{
                    savedNewsList.clear();
                    newsListAdapter.notifyDataSetChanged();
                    putInSharedReferences(News.SAVED_NEWS_LIST_KEY,savedNewsList);
                    positiveDialog.dismiss();
                });

        builder.setNegativeButton("CANCEL",
                (negativeDialog,id)->negativeDialog.dismiss());

        alertDialog = builder.create();
        alertDialog.show();
    }
}