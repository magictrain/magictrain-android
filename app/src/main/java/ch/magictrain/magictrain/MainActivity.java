package ch.magictrain.magictrain;

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

        new UpdateAsyncTask().execute();
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
