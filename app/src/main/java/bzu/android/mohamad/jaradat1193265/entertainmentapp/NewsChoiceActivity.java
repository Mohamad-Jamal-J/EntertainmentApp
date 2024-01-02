package bzu.android.mohamad.jaradat1193265.entertainmentapp;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class NewsChoiceActivity extends AppCompatActivity {
    CardView searchCardView;
    CardView latestCardView;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice_news);
        searchCardView = findViewById(R.id.searchForNewsCardView);
        latestCardView = findViewById(R.id.latestNewsCardView);

        searchCardView.setOnClickListener(action->{
            Intent intent = new Intent(this, SpecificNewsActivity.class);
            startActivity(intent);
        });

        latestCardView.setOnClickListener(action->{
            Intent intent = new Intent(this, LatestNewsActivity.class);
            startActivity(intent);
        });
    }
}