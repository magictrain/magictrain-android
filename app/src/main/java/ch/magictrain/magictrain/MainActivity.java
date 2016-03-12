package ch.magictrain.magictrain;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import ch.magictrain.magictrain.models.UpdateResponse;

import com.google.gson.JsonSyntaxException;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

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

    public class GsonRequest<T> extends Request<T> {
        private final Gson gson = new Gson();
        private final Class<T> clazz;
        private final Map<String, String> headers;
        private final Response.Listener<T> listener;

        /**
         * Make a GET request and return a parsed object from JSON.
         *
         * @param url URL of the request to make
         * @param clazz Relevant class object, for Gson's reflection
         * @param headers Map of request headers
         */
        public GsonRequest(String url, Class<T> clazz, Map<String, String> headers,
                           Response.Listener<T> listener, Response.ErrorListener errorListener) {
            super(Method.POST, url, errorListener);
            this.clazz = clazz;
            this.headers = headers;
            this.listener = listener;
        }

        @Override
        public Map<String, String> getHeaders() throws AuthFailureError {
            return headers != null ? headers : super.getHeaders();
        }

        @Override
        protected void deliverResponse(T response) {
            listener.onResponse(response);
        }

        @Override
        protected Response<T> parseNetworkResponse(NetworkResponse response) {
            try {
                String json = new String(
                        response.data,
                        HttpHeaderParser.parseCharset(response.headers));
                return Response.success(
                        gson.fromJson(json, clazz),
                        HttpHeaderParser.parseCacheHeaders(response));
            } catch (UnsupportedEncodingException e) {
                return Response.error(new ParseError(e));
            } catch (JsonSyntaxException e) {
                return Response.error(new ParseError(e));
            }
        }
    }
}
