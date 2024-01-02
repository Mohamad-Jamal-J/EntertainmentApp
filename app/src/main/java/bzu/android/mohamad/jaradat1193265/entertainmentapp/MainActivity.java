package bzu.android.mohamad.jaradat1193265.entertainmentapp;


import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.Toast;

import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

public class MainActivity extends AbstractAppManager {

    protected CheckBox rememberBox;
    protected final static String USER_NAME = "username";
    private  boolean doRemember=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        queue = Volley.newRequestQueue(this);
        setupSharedPreferences();

        loadAccountFromPreferences();
        hookLayouts();


        loginButton.setOnClickListener(action->{
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (checkInputs(email,password))
                checkCredentials(email,password);

        });
        singButton.setOnClickListener(action->{//fire signup activity
            Intent intent = new Intent(this, SignupActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        Gson GSON = new Gson();
        String JSON_STRING = sharedPreferences.getString(MAIN_ACCOUNT_KEY, null);
        mainAccount = GSON.fromJson(JSON_STRING , Account.class);

        doRemember = sharedPreferences.getBoolean(REMEMBER_ACCOUNT,false);
        if (doRemember)
            initializeLoginViews();
    }

    private void initializeLoginViews() {
        if (mainAccount!=null){
            emailEditText.setText(mainAccount.getEmail());
            passwordEditText.setText(mainAccount.getPassword());
            rememberBox.setChecked(true);
        }
    }
    @Override
    protected void hookLayouts() {
        super.hookLayouts();
        rememberBox = findViewById(R.id.rememberCheckbox);
    }
    private boolean checkInputs(String email, String password){
        if (email.isEmpty()) {
            makeToast(this, "Fill email");
            return false;
        }
        else if (password.isEmpty() || password.length()<8) {
            makeToast(this, FEEDBACK_ERR_6);
            return false;
        }else if (mainAccount==null){
            makeToast(this, "Please sign up first");
            return false;
        }
        return true;
    }
    private void checkCredentials(String email, String password){
        if (email.equalsIgnoreCase(mainAccount.email))
            if (password.equals(mainAccount.password))
            {
                makeToast(this, FEEDBACK_OK_2);
                doRemember = rememberBox.isChecked();
                sharedPreferencesEditor.putBoolean(REMEMBER_ACCOUNT, doRemember);
                sharedPreferencesEditor.apply();
                putInSharedReferences(MAIN_ACCOUNT_KEY,mainAccount);
                Intent intent = new Intent(this, APIChoiceActivity.class);
                intent.putExtra(USER_NAME, mainAccount.name);
                startActivity(intent);
            }
            else
                makeToast(this, FEEDBACK_ERR_3);
        else
            makeToast(this,FEEDBACK_ERR_1);
    }
}