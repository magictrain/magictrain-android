package ch.magictrain.magictrain;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import ch.magictrain.magictrain.models.UpdateResponse;
import ch.magictrain.magictrain.net.GsonRequest;
import ch.magictrain.magictrain.net.RequestQueueStore;
import ch.magictrain.magictrain.views.TrainListView;

import java.util.Calendar;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private TrainListView list;
    private ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        list = (TrainListView) findViewById(R.id.trainListView);
        progress = (ProgressBar) findViewById(R.id.loading);

        startBackgroundService();

        new UpdateAsyncTask().execute();
    }

    private void startBackgroundService() {
        Intent intent = new Intent(MainActivity.this.getApplicationContext(), BackgroundService.class);
        PendingIntent pintent = PendingIntent.getService(MainActivity.this.getApplicationContext(), 0, intent, 0);
        AlarmManager alarm = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        long interval = 1000 * 60;
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis(), interval, pintent);
    }

    private class UpdateAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            String url = "http://magictrain.mybluemix.net/dummy";
            GsonRequest<UpdateResponse> req = new GsonRequest<>(
                    url,
                    UpdateResponse.class,
                    new HashMap<String, String>(),
                    new Listener(),
                    new ErrorListener());
            RequestQueueStore.getInstance(MainActivity.this).addToRequestQueue(req);

            return null;
        }

        private class Listener implements Response.Listener<UpdateResponse> {
            @Override
            public void onResponse(final UpdateResponse response) {
                Log.d(Settings.LOGTAG, response.toString());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progress.setVisibility(View.INVISIBLE);
                        list.setData(response);
                    }
                });
            }
        }
        private class ErrorListener implements Response.ErrorListener {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(Settings.LOGTAG, error.toString());
            }
        }
    }
}
