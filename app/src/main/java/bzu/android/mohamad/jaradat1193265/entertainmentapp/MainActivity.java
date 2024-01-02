package bzu.android.mohamad.jaradat1193265.entertainmentapp;


import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
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
    //retrieving the main account in order to validate the login entries offered by the user
    // if the remember me option was selected before launching the app it will fill the views with
    // main account's data so the user can login after it,
    // please note that i commented out the loginButton.callOnClick since as i understood from
    //the assignment description is that you want to see the credentials instead of immediately logging in
    @Override
    protected void onResume() {
        super.onResume();

        Gson GSON = new Gson();
        String JSON_STRING = sharedPreferences.getString(MAIN_ACCOUNT_KEY, null);
        mainAccount = GSON.fromJson(JSON_STRING , Account.class);

        doRemember = sharedPreferences.getBoolean(REMEMBER_ACCOUNT,false);
        if (doRemember) {
            initializeLoginViews();
//            loginButton.callOnClick();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        doRemember = rememberBox.isChecked();
        sharedPreferencesEditor.putBoolean(REMEMBER_ACCOUNT,doRemember);
        sharedPreferencesEditor.apply();
        clearViews();
    }

    // this method fills the views with the main account data.
    private void initializeLoginViews() {
        if (mainAccount!=null){
            emailEditText.setText(mainAccount.getEmail());
            passwordEditText.setText(mainAccount.getPassword());
            rememberBox.setChecked(true);
        }
    }
    // this method links the views in the xml file with the variables in the backend
    @Override
    protected void hookLayouts() {
        super.hookLayouts();
        rememberBox = findViewById(R.id.rememberCheckbox);
    }
   // this method checks if the views were empty before checking their validity
    // also if there was no account existing in the preferences (first time launching the account)
    // the user will be asked to create an account
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
    // this method checks the given entries from the user to see if they match the
    // data ceredentials for the main account.
    // if all entries are valid the user will be able to log to their account and use the apis
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