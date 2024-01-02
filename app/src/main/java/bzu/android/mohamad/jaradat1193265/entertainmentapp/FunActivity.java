package bzu.android.mohamad.jaradat1193265.entertainmentapp;

import androidx.appcompat.app.AppCompatActivity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import java.util.HashMap;
import java.util.Map;

public class FunActivity extends AppCompatActivity {
    TextView textView;
    Button button;
    protected SharedPreferences sharedPreferences;
    protected SharedPreferences.Editor sharedPreferencesEditor;
    private static final String JOKE_API_URL = "https://icanhazdadjoke.com/";
    protected final static String JOKE_KEY = "JOKE_KEY";
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fun);
        setupSharedPreferences();

        textView = findViewById(R.id.textView);
        button = findViewById(R.id.button);
        requestQueue = Volley.newRequestQueue(this);

        button.setOnClickListener(action->generateJoke());
    }
    //save the last joke the user saw in order to fetch it again when they're back
    @Override
    protected void onStop() {
        super.onStop();
        sharedPreferencesEditor.putString(JOKE_KEY,textView.getText().toString());
        sharedPreferencesEditor.apply();
    }

    // load the saved joke in the onstop call back method
    @Override
    protected void onResume() {
        super.onResume();
        String joke = sharedPreferences.getString(JOKE_KEY,null);
        if (joke!=null)
            textView.setText(joke);
        else
            textView.setText(R.string.wanna_hear_a_joke);
    }
    //this method sets up the shared preferences and makes them ready to use
    protected void setupSharedPreferences() {
        sharedPreferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        sharedPreferencesEditor = sharedPreferences.edit();
    }
    //this method uses volley api to retrieve a joke from icanhazdadjoke api
    // after the retrieval, the joke is then displayed to the user
    private void generateJoke() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, JOKE_API_URL, null,
                response -> {
                    try {
                        String joke = response.getString("joke");
                        textView.setText(joke);
                    } catch (JSONException e) {
                        Log.d("TAG45", "here1: ");
                    }
                },
                error -> {
                    Log.d("TAG45", "here2: ");
                }) {
            @Override
            public Map<String, String> getHeaders() {
                // the api requires me to send my application link via the header so i'm sending a dummy link
                Map<String, String> headers = new HashMap<>();
                headers.put("Accept", "application/json");
                headers.put("User-Agent", "My Library (https://github.com/username/repo)");
                return headers;
            }
        };
        requestQueue.add(jsonObjectRequest);
    }
}