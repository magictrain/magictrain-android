package ch.magictrain.magictrain;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.HashMap;

import ch.magictrain.magictrain.models.UpdateResponse;
import ch.magictrain.magictrain.net.GsonRequest;
import ch.magictrain.magictrain.net.RequestQueueStore;

public class BackgroundService extends Service {
    private static boolean isRunning = false;

    @Override
    public void onCreate() {
        super.onCreate();
        isRunning = true;
        Toast.makeText(this, "background service started", Toast.LENGTH_SHORT).show();
        new UpdateAsyncTask().execute();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        isRunning = false;
        Toast.makeText(this, "background service stopped", Toast.LENGTH_SHORT).show();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void sendUpdateToActivity(UpdateResponse response) {
        Intent i = new Intent(MainActivity.RECEIVE_UPDATE_FOR_VIEW);
        i.putExtra(MainActivity.EXTRA_JSON_DATA, response.toJson());
        sendBroadcast(i);
    }

    private class UpdateAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            String url = "http://magictrain.mybluemix.net/dummy";
            while(isRunning) {
                GsonRequest<UpdateResponse> req = new GsonRequest<>(
                        url,
                        UpdateResponse.class,
                        new HashMap<String, String>(),
                        new Listener(),
                        new ErrorListener());
                RequestQueueStore.getInstance(getApplicationContext()).addToRequestQueue(req);
                try {
                    Thread.sleep(1000 * 10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            Toast.makeText(getApplicationContext(), "background service really stopped", Toast.LENGTH_SHORT).show();

            return null;
        }

        private class Listener implements Response.Listener<UpdateResponse> {
            @Override
            public void onResponse(final UpdateResponse response) {
                Log.d(Settings.LOGTAG, response.toString());
                sendUpdateToActivity(response);
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
