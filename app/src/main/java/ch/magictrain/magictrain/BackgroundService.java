package ch.magictrain.magictrain;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;

import java.util.HashMap;
import java.util.List;

import ch.magictrain.magictrain.models.UpdateResponse;
import ch.magictrain.magictrain.net.GsonRequest;
import ch.magictrain.magictrain.net.RequestQueueStore;

public class BackgroundService extends Service {
    private final static String REGION_ID_ = "BEACON_REGION_MAGICTRAIN";

    private BeaconManager beaconManager;
    private final Region region = new Region(REGION_ID_, Settings.BEACON_UUID, null, null);

    public void setupBeacons() {
        beaconManager = new BeaconManager(this);
        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, List<Beacon> list) {
            new UpdateAsyncTask().execute(list);
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
        Intent i = new Intent(MainActivity.RECEIVE_UPDATE_FOR_VIEW);
        i.putExtra(MainActivity.EXTRA_JSON_DATA, response.toJson());
        sendBroadcast(i);
    }

    private class UpdateAsyncTask extends AsyncTask<List<Beacon>, Void, Void> {
        @SafeVarargs
        @Override
        protected final Void doInBackground(List<Beacon>... beacons) {
            for (Beacon b : beacons[0]){
                Log.d(Settings.LOGTAG, "received beacons b=" + b);
            }

            String url = "http://magictrain.mybluemix.net/dummy";
            GsonRequest<UpdateResponse> req = new GsonRequest<>(
                    url,
                    UpdateResponse.class,
                    new HashMap<String, String>(),
                    new Listener(),
                    new ErrorListener());
            RequestQueueStore.getInstance(getApplicationContext()).addToRequestQueue(req);

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
