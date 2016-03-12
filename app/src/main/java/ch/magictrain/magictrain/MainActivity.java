package ch.magictrain.magictrain;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.estimote.sdk.SystemRequirementsChecker;

import ch.magictrain.magictrain.models.UpdateResponse;
import ch.magictrain.magictrain.views.TrainListView;


public class MainActivity extends AppCompatActivity {
    private TrainListView list;
    private ProgressBar progress;
    private TextView noData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        list = (TrainListView) findViewById(R.id.trainListView);
        progress = (ProgressBar) findViewById(R.id.loading);
        noData = (TextView) findViewById(R.id.noData);

        SystemRequirementsChecker.checkWithDefaultDialogs(this);

        startBackgroundService();
    }

    @Override
    protected void onPause() {
        unRegisterIntent();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerIntent();
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
                            if(response.train == null) {
                                list.setVisibility(View.INVISIBLE);
                                noData.setVisibility(View.VISIBLE);
                            } else {
                                list.setData(response);
                                noData.setVisibility(View.INVISIBLE);
                                list.setVisibility(View.VISIBLE);
                            }
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
