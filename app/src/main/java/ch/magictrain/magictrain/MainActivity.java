package ch.magictrain.magictrain;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.estimote.sdk.SystemRequirementsChecker;

import ch.magictrain.magictrain.models.UpdateResponse;
import ch.magictrain.magictrain.views.TrainListView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {

    CallbackManager callbackManager;

    //shared
    public static final String PREFS_NAME = "FbPref";

    private TrainListView list;
    private ProgressBar progress;
    private LoginButton loginButton;

    private String fb_id = "";
    private String fb_fname = "";
    private String fb_sname = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);

        list = (TrainListView) findViewById(R.id.trainListView);
        progress = (ProgressBar) findViewById(R.id.loading);


        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("email");
        loginButton.registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        AccessToken accessToken = loginResult.getAccessToken();
                        GraphRequest graphRequest = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback(){
                            public void onCompleted(JSONObject object, GraphResponse response) {

                                if (response.getError()!=null)
                                {
                                    Log.e("andreas","Error in Response "+ response);
                                }
                                else
                                {
                                    String email=object.optString("email");
                                    Log.e("andreas" ,"Json Object Data "+object+" Email id "+ email);
                                }
                            }});
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
                        editor.commit();

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



        SystemRequirementsChecker.checkWithDefaultDialogs(this);

        startBackgroundService();

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

        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }
    private void registerIntent() {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(Settings.LOGTAG, "RECEIVED BROADCAST");
                if(intent.getAction().equals(RECEIVE_UPDATE_FOR_VIEW)) {
                    final String json = intent.getStringExtra(EXTRA_JSON_DATA);
                    final UpdateResponse response = UpdateResponse.fromJson(json);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progress.setVisibility(View.INVISIBLE);
                            list.setData(response);
                        }
                    });
                }
            }
        };
        registerReceiver(receiver, new IntentFilter(RECEIVE_UPDATE_FOR_VIEW));
        Log.d(Settings.LOGTAG, "registered receiver");
    }

    private void unRegisterIntent() {
        Log.d(Settings.LOGTAG, "try to unregister receiver");
        try{
            unregisterReceiver(receiver);
        } catch (IllegalArgumentException ignored){
        }
    }

    public void startBackgroundService() {
        startService(new Intent(MainActivity.this,
                BackgroundService.class));
    }

    public static final String RECEIVE_UPDATE_FOR_VIEW = "ch.magictrain.magictrain.RECEIVE_UPDATE_FOR_VIEW";
    public static final String EXTRA_JSON_DATA = "EXTRA_JSON_DATA";

    private BroadcastReceiver receiver;
}
