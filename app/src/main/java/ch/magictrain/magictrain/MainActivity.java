package ch.magictrain.magictrain;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import com.estimote.sdk.SystemRequirementsChecker;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    CallbackManager callbackManager;

    //shared
    public static final String PREFS_NAME = "FbPref";

    private String fb_id = "";
    private String fb_fname = "";
    private String fb_sname = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SystemRequirementsChecker.checkWithDefaultDialogs(this);

        callbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        assert loginButton != null;
        loginButton.setReadPermissions("email");
        loginButton.registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        AccessToken accessToken = loginResult.getAccessToken();
                        GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
                            public void onCompleted(JSONObject object, GraphResponse response) {

                                if (response.getError() != null) {
                                    Log.e("andreas", "Error in Response " + response);
                                } else {
                                    String email = object.optString("email");
                                    Log.e("andreas", "Json Object Data " + object + " Email id " + email);
                                }
                            }
                        });
                        // App code
                        Log.d("andreas", "Callback in onCreateonSuccess");
                        fb_id = accessToken.getUserId();
                        fb_fname = "testfname";
                        fb_sname = "testsname";
                        Log.d("andreas", fb_id);

                        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString("fb_id", fb_id);
                        editor.putString("fb_fname", fb_fname);
                        editor.putString("fb_sname", fb_sname);
                        editor.apply();

                        launchTrainActivity();
                    }

                    @Override
                    public void onCancel() {
                        // App code
                        Log.d("andreas", "Callback in onCreateonCancel");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                        Log.d("andreas", "Callback in onCreateonError");
                    }

                });

        // Restore preferences
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        fb_id = settings.getString("fb_id", "");
        Log.d("andreas", fb_id);
        fb_fname = settings.getString("fb_fname", "");
        Log.d("andreas", fb_fname);
        fb_sname = settings.getString("fb_sname", "");
        Log.d("andreas", fb_sname);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode,
                resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppEventsLogger.deactivateApp(this);
    }

    public void launchTrainActivity() {
        Intent intent = new Intent(this, TrainActivity.class);
        startActivity(intent);
    }
}
