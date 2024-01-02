package bzu.android.mohamad.jaradat1193265.entertainmentapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.RequestQueue;
import com.google.gson.Gson;


public abstract class AbstractAppManager extends AppCompatActivity {
    protected final static String MAIN_ACCOUNT_KEY = "MAIN ACCOUNT";
    protected Account mainAccount;

    protected final static String REMEMBER_ACCOUNT = "REMEMBER";
    protected final static String FEEDBACK_ERR_4 = "Empty name";
    protected final static String FEEDBACK_ERR_5 = "Empty email";
    protected final static String FEEDBACK_ERR_6 = "Password less than 8 characters";
    protected final static String FEEDBACK_OK_1 = "Account created successfully";
    protected final static String FEEDBACK_OK_2 = "Login successful";
    protected final static String FEEDBACK_ERR_0 = "Email doesn't seem to exist in the API";
    protected final static String FEEDBACK_ERR_1 = "Account doesn't exist";
    protected final static String FEEDBACK_ERR_2 = "Account already exist";
    protected final static String FEEDBACK_ERR_3 = "Incorrect password";
    protected RequestQueue queue;
    protected SharedPreferences sharedPreferences;
    protected SharedPreferences.Editor sharedPreferencesEditor;
    protected EditText nameEditText;
    protected EditText emailEditText;
    protected EditText passwordEditText;
    protected Button loginButton;
    protected Button singButton;


    //this method sets up the shared preferences and makes them ready to use
    protected void setupSharedPreferences() {
        sharedPreferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        sharedPreferencesEditor = sharedPreferences.edit();
    }
    //this method converts the given object to JSON format and saves it in the shared preferences based on a given key
    protected void putInSharedReferences(String KEY, Object object){
        Gson GSON = new Gson();
        String JSON_STRING = GSON.toJson(object);
        sharedPreferencesEditor.putString(KEY, JSON_STRING);
        sharedPreferencesEditor.apply();
    }
    //this method retrieves the main account from the preferences
    protected void loadAccountFromPreferences() {
        Gson GSON = new Gson();
        String JSON_STRING = sharedPreferences.getString(MAIN_ACCOUNT_KEY,null);
        mainAccount = GSON.fromJson(JSON_STRING, Account.class);
    }
    //this method links the views in the xml file and the backend variables
    protected void hookLayouts() {
        nameEditText = findViewById(R.id.userName);
        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginButton);
        singButton = findViewById(R.id.signButton);
    }
    // this method clears the entries in the views of the activity
    protected void clearViews() {
        emailEditText.setText("");
        passwordEditText.setText("");
    }
    // this method allows creating a toast in a fast way and more clean code
        protected void makeToast(Context context, String message){
        Toast.makeText(context, message , Toast.LENGTH_LONG).show();
    }
}