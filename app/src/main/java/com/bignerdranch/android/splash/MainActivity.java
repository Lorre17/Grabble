package com.bignerdranch.android.splash;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.bignerdranch.android.splash.Maps_Activity.alphabet;

/**
 * Created by Lorena on 19/12/2016.
 */

public class MainActivity extends AppCompatActivity {
    private EditText emailEditText;
    private EditText passEditText;
    private EditText emailVerificationEditText;
    private CallbackManager mCallbackManager;
    private Context mContext;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        setContentView(R.layout.activity_login);

        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        Maps_Activity.score = sharedPref.getInt(getString(R.string.shared_score), 0);
        Maps_Activity.totalScore = sharedPref.getInt(getString(R.string.shared_totalscore), 0);

        // Address the email and password field
        emailEditText = (EditText) findViewById(R.id.username);
        passEditText = (EditText) findViewById(R.id.password);

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        mContext = getApplicationContext();

        mCallbackManager = CallbackManager.Factory.create();

        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("Debugging..", "Inside onSuccess");Toast.makeText(getApplicationContext(), "Login successful", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, Maps_Activity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                MainActivity.this.startActivity(intent);
            }

            @Override
            public void onCancel() {
                Log.d("Debugging..", "Inside onCancel");
                Toast.makeText(getApplicationContext(), R.string.cancel_login, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Log.d("Debugging..", "Inside onError");
                Toast.makeText(getApplicationContext(), R.string.error_login, Toast.LENGTH_SHORT).show();
            }
        });

        if(AccessToken.getCurrentAccessToken() != null) {
            goMapsActivity();
        }
    }

    private void goMapsActivity() {
        Intent intent = new Intent(mContext, Maps_Activity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        Log.d("Debugging..", "Inside onActivityResult()");

    }

    public void checkLogin(View arg0) {

        final String email = emailEditText.getText().toString();
        if (!isValidEmail(email)) {
            //Set error message for email field
            emailEditText.setError("Invalid Email");
        }

        final String pass = passEditText.getText().toString();
        if (!isValidPassword(pass)) {
            //Set error message for password field
            passEditText.setError("Password cannot be empty");
        }

        if (isValidEmail(email) && isValidPassword(pass)) {
//            setContentView(R.layout.activity_maps);
            Intent intent = new Intent(mContext, Maps_Activity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
        }

    }

    public String fetchUrl() {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        String file = "";

        switch (day) {
            case Calendar.SUNDAY:
                file =  "http://www.inf.ed.ac.uk/teaching/courses/selp/coursework/sunday.kml";
            case Calendar.MONDAY:
                file = "http://www.inf.ed.ac.uk/teaching/courses/selp/coursework/monday.kml";

            case Calendar.TUESDAY:
                file = "http://www.inf.ed.ac.uk/teaching/courses/selp/coursework/tuesday.kml";
            case Calendar.WEDNESDAY:
                file = "http://www.inf.ed.ac.uk/teaching/courses/selp/coursework/wednesday.kml";
            case Calendar.THURSDAY:
                file = "http://www.inf.ed.ac.uk/teaching/courses/selp/coursework/thursday.kml";
            case Calendar.FRIDAY:
                file = "http://www.inf.ed.ac.uk/teaching/courses/selp/coursework/friday.kml";
            case Calendar.SATURDAY:
                file = "http://www.inf.ed.ac.uk/teaching/courses/selp/coursework/saturday.kml";
        }
        return file;
    }

    public void verificationLogin (View v) {
        setContentView(R.layout.activity_forgot_password);
        emailVerificationEditText = (EditText) findViewById(R.id.verif_username);
    }

    public void checkVerificationLogin(View arg0) {
        Context RequestSubmitted = getApplicationContext();
        CharSequence text = "Your request has been submitted!";
        int duration = Toast.LENGTH_SHORT;

        final String email = emailVerificationEditText.getText().toString();
        if (!isValidEmail(email)) {
            //Set error message for email field
            emailVerificationEditText.setError("Invalid Email");
        }

        else {
            Toast.makeText(RequestSubmitted, text, duration).show();
            setContentView(R.layout.activity_login);
        }
    }

    public void forgotPassword(View arg0) {

    }

    // validating email id
    private boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    // validating password
    private boolean isValidPassword(String pass) {
        return pass != null && pass.length() >= 4;
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();

        if (AccessToken.getCurrentAccessToken() != null) {
            LoginManager.getInstance().logOut();
        }

        SharedPreferences sharedPref = getSharedPreferences("Scores", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(getString(R.string.shared_score), Maps_Activity.score);
        editor.putInt(getString(R.string.shared_totalscore), Maps_Activity.totalScore);
        editor.commit();

//        SharedPreferences sharedPref2 = getSharedPreferences("Map", Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor2 = sharedPref2.edit();
//        Gson gson = new Gson();
//        String json = gson.toJson(alphabet);
//        editor.putString(getResources().getString(R.string.alphabet_shared_preference),json);
//        editor.apply();


        try
        {
            FileOutputStream fos = mContext.openFileOutput("hashmap.ser", Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(alphabet);
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
