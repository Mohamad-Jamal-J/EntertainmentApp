package bzu.android.mohamad.jaradat1193265.entertainmentapp;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.json.JSONException;
import android.graphics.Color;
import android.widget.EditText;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class SignupActivity extends AbstractAppManager{
    protected final static String SIGN_SAVED_INFO = "SIGN_SAVED_INFO";
    AlertDialog alertDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        queue = Volley.newRequestQueue(this);


        setupSharedPreferences();
        hookLayouts();
        loadAccountFromPreferences();


        loginButton.setOnClickListener(action->{ //already have account
            showWarningDialog();

        });
        singButton.setOnClickListener(action->{//fire signup activity
            String name = nameEditText.getText().toString();
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            if (isValidEntries(name,email,password))
                validateEmailWithApi(name,email,password);
        });

    }


        @Override
    protected void onStop() {
        super.onStop();

        String name = nameEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if (!name.isEmpty() || !email.isEmpty() || !password.isEmpty()){
            List<String> signSavedInfo = new ArrayList<>();
            signSavedInfo.add(name);
            signSavedInfo.add(email);
            signSavedInfo.add(password);

            Gson GSON = new Gson();
            String JSON_STRING = GSON.toJson(signSavedInfo);
            sharedPreferencesEditor.putString(SIGN_SAVED_INFO,JSON_STRING);
            sharedPreferencesEditor.apply();
        }
    }
    //the cycle of retrieving what the user was writing is only done here in the sign in page
    // i didn't do it in the login page to not confuse it with the remember me functionality.
    @Override
    protected void onResume() {
        super.onResume();
        Gson GSON = new Gson();
        String JSON_STRING = sharedPreferences.getString(SIGN_SAVED_INFO, null);
        if (JSON_STRING != null) {
            Type objectType = new TypeToken<ArrayList<String>>() {}.getType();
            List<String> signSavedInfo = GSON.fromJson(JSON_STRING, objectType);
            if (signSavedInfo!=null && signSavedInfo.size()==3)
                initializeSignViews(signSavedInfo.get(0), signSavedInfo.get(1), signSavedInfo.get(2));
        }
    }


    @Override
    protected void hookLayouts() {
        super.hookLayouts();
        nameEditText = findViewById(R.id.userName);
    }
    protected void clearViews() {
        super.clearViews();
        nameEditText.setText("");
    }
    private boolean isValidEntries(String name, String email, String password){
        if (name == null || name.trim().isEmpty()){
            makeToast(this, FEEDBACK_ERR_4);
            hintColor(nameEditText, false);
            return false;
        }
        if (email == null || email.trim().isEmpty()){
            makeToast(this, FEEDBACK_ERR_5);
            hintColor(emailEditText, false);
            return false;
        }
        if (!isValidPasswordEntry(password)){
            makeToast(this, FEEDBACK_ERR_6);
            hintColor(passwordEditText, false);
            return false;
        }
        hintColor(nameEditText,true);
        hintColor(emailEditText,true);
        hintColor(passwordEditText,true);
        return true;
    }
    private boolean isValidPasswordEntry(String password){
        return password != null && password.trim().length() >= 8;
    }
    private void hintColor(EditText view, boolean isOk){
        if (isOk)
            view.setHintTextColor(Color.GRAY);
        else
            view.setHintTextColor(Color.parseColor("#FFAACC15"));
    }
    protected void validateEmailWithApi(String name, String email, String password){
        String API_KEY = "bcf4d62f20ea4ef6902c0203ae831022";
        String url = "https://emailvalidation.abstractapi.com/v1/?api_key="+API_KEY+"&email="+email;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,url, null,
                response -> {
                    try {
                        boolean isValidFormat =  response.getJSONObject("is_valid_format").getBoolean("value");
                        String deliverability =  response.getString("deliverability");
                        double quality = response.getDouble("quality_score");
                        boolean isValidEmail = deliverability.equalsIgnoreCase("DELIVERABLE") && quality>=0.5 && isValidFormat;
                        if (isValidEmail) {//if valid check if already exist in preferences
                            if (mainAccount != null && email.equalsIgnoreCase(mainAccount.email))
                                //email already has account
                                 makeToast(this, FEEDBACK_ERR_2);
                            else
                                //create/ overwrite main account
                                    signAndCallLogin(name, email, password);
                        }else
                            makeToast(this, FEEDBACK_ERR_0);
                    } catch (JSONException e) {
                        Log.d("TAG888", e.toString());
                    }
                }
                , error ->Log.d("TAG888", "Volley Error: " + error.toString()));
        queue.add(request);
    }

    protected void signAndCallLogin(String name, String email, String password) {
        //delete all previous data of the user
        sharedPreferencesEditor.clear();
        sharedPreferencesEditor.commit();

        mainAccount = new Account(name.trim(), email.trim(), password.trim());
        clearViews();
        makeToast(this, FEEDBACK_OK_1);
        sharedPreferencesEditor.putBoolean(REMEMBER_ACCOUNT,true); // set remember me to true
        sharedPreferencesEditor.apply();
        putInSharedReferences(MAIN_ACCOUNT_KEY, mainAccount); //create /overwrite the old account
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish(); // destroy and remove this activity from background once the process is successful
    }

    private void initializeSignViews(String name, String email, String password) {
        nameEditText.setText(name);
        emailEditText.setText(email);
        passwordEditText.setText(password);
    }
    private void showWarningDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Warning, you'll be redirected to login page")
                .setMessage("You Might Lose any Information You've Entered");

        builder.setPositiveButton("Yes Leave",

                (positiveDialog, id)->{
                    sharedPreferencesEditor.putString(SIGN_SAVED_INFO,null);
                    sharedPreferencesEditor.apply();
                    clearViews();
                    positiveDialog.dismiss();
                    finish();
                });

        builder.setNegativeButton("CANCEL",
                (negativeDialog,id)->negativeDialog.dismiss());

        alertDialog = builder.create();
        alertDialog.show();
    }
}