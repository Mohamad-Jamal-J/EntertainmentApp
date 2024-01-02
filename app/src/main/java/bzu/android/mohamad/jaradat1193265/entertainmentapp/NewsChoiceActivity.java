package bzu.android.mohamad.jaradat1193265.entertainmentapp;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;


//this activity lets the user to decide on which way they prefer to use the api
// the fast and easy way? they get directed to the Latestnews activity where they can se news form channels
// during the 48 hours, without the need to specify a query

// or to the specific activity where they can enter the query they are looking for

// therefore no life cycle methods were needed
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