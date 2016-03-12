package ch.magictrain.magictrain;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;


import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ch.magictrain.magictrain.models.PushRequest;
import ch.magictrain.magictrain.models.UpdateResponse;
import ch.magictrain.magictrain.net.GsonRequest;
import ch.magictrain.magictrain.net.RequestQueueStore;

public class BackgroundService extends Service {
    private final static String REGION_ID_ = "BEACON_REGION_MAGICTRAIN";

    // for API rate limiting
    private long lastMessageSent = 0L;

    private BeaconManager beaconManager;
    private final Region region = new Region(REGION_ID_, Settings.BEACON_UUID, null, null);

    public void setupBeacons() {
        beaconManager = new BeaconManager(this);
        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, List<Beacon> beacons) {
                long now = System.currentTimeMillis();
                if(now - lastMessageSent > Settings.RATELIMIT_MS) {
                    Log.d(Settings.LOGTAG, "RATELIMIT sent new data");
                    new UpdateAsyncTask().execute(beacons);
                    lastMessageSent = now;
                } else {
                    Log.d(Settings.LOGTAG, "RATELIMIT got new data but rate limited");
                }

                if(beacons.size() > 0)
                    Log.d(Settings.LOGTAG, "nearest beacon mac=" + beacons.get(0).getMacAddress().toStandardString());
            }
        });

        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                beaconManager.startRanging(region);
            }
        });
    }

    public void teardownBeacons() {
        beaconManager.stopRanging(region);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setupBeacons();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        teardownBeacons();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void sendUpdateToActivity(UpdateResponse response) {
        Intent i = new Intent(TrainActivity.RECEIVE_UPDATE_FOR_VIEW);
        i.putExtra(TrainActivity.EXTRA_JSON_DATA, response.toJson());
        sendBroadcast(i);
    }

    private PushRequest preparePostData(List<Beacon> beacons) {
        PushRequest req = new PushRequest(
                // TODO dummy values
                "1337", "Test User", new ArrayList<ch.magictrain.magictrain.models.Beacon>()
        );
        int i = 0;
        for(Beacon b: beacons) {
            req.beacons.add(new ch.magictrain.magictrain.models.Beacon(
                    i++,
                    b.getMacAddress().toStandardString()
            ));
        }
        return req;
    }

    private class UpdateAsyncTask extends AsyncTask<List<Beacon>, Void, Void> {
        @SafeVarargs
        @Override
        protected final Void doInBackground(List<Beacon>... beacons) {
            PushRequest data = preparePostData(beacons[0]);
            JSONObject json = new JSONObject();
            try {
                json = new JSONObject(data.toJson());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.d(Settings.LOGTAG, "json send data = " + data.toJson());

            String url = "http://magictrain.mybluemix.net/push";
            GsonRequest<UpdateResponse> req = new GsonRequest<>(
                    Request.Method.POST,
                    url,
                    UpdateResponse.class,
                    json,
                    new Listener(),
                    new ErrorListener());
            RequestQueueStore.getInstance(getApplicationContext()).addToRequestQueue(req);

            return null;
        }

        private class Listener implements Response.Listener<UpdateResponse> {
            @Override
            public void onResponse(final UpdateResponse response) {
                Log.d(Settings.LOGTAG, "json return data = " + response.toJson());
                sendUpdateToActivity(response);
            }
        }
        private class ErrorListener implements Response.ErrorListener {
            @Override
            public void onErrorResponse(VolleyError error) {
                sendUpdateToActivity(UpdateResponse.fromJson("{}"));
                Log.d(Settings.LOGTAG, error.toString());
            }
        }
    }
}
