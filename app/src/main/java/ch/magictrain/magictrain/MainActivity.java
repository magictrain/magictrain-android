package ch.magictrain.magictrain;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;


public class MainActivity extends AppCompatActivity {

    CallbackManager callbackManager;

    private TrainListView list;
    private ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());

        callbackManager = CallbackManager.Factory.create();
        //LoginButton loginButton = (LoginButton) R.id.login_button;
        //loginButton.registerCallback(callbackManager,
        //        new FacebookCallback<LoginResult>() { /*toto*/
        //        });

        setContentView(R.layout.activity_main);
        list = (TrainListView) findViewById(R.id.trainListView);
        progress = (ProgressBar) findViewById(R.id.loading);

        SystemRequirementsChecker.checkWithDefaultDialogs(this);

        startBackgroundService();
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
