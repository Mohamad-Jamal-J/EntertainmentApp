package bzu.android.mohamad.jaradat1193265.entertainmentapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class APIChoiceActivity extends AppCompatActivity {
    TextView pageLabel;
    CardView newsCardView;
    CardView entertainmentCardView;
    CardView savedNewsCardView;
    Button logoutButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice_api);

        String userName =  getIntent().getStringExtra("username");
        String welcome = "Welcome";
        if (userName!=null)
            welcome+=": "+userName;
        pageLabel = findViewById(R.id.welcomeLabel);
        pageLabel.setText(welcome);
        newsCardView = findViewById(R.id.newsCardView);
        entertainmentCardView = findViewById(R.id.entertainmentCardView);
        savedNewsCardView = findViewById(R.id.savedNewsCardView);
        logoutButton = findViewById(R.id.logoutButton);


        newsCardView.setOnClickListener(action->{
            Intent intent = new Intent(this, NewsChoiceActivity.class);
            startActivity(intent);
        });

        entertainmentCardView.setOnClickListener(action->{
            Intent intent = new Intent(this, FunActivity.class);
            startActivity(intent);
        });


        savedNewsCardView.setOnClickListener(action->{
            Intent intent = new Intent(this, ViewSavedNewsActivity.class);
            startActivity(intent);
        });

        logoutButton.setOnClickListener(action->{
            finish();
        });

    }}


