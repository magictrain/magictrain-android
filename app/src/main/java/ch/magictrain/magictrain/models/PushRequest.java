package ch.magictrain.magictrain.models;

import com.google.gson.Gson;

import java.util.ArrayList;

public class PushRequest {
    public final String fb_id;
    public final String fb_name;
    public final ArrayList<Beacon> beacons;

    public PushRequest(String fb_id, String fb_name, ArrayList<Beacon> beacons) {
        this.fb_id = fb_id;
        this.fb_name = fb_name;
        this.beacons = beacons;
    }

    public static PushRequest fromJson(String json) {
        final Gson gson = new Gson();
        return gson.fromJson(json, PushRequest.class);
    }

    public String toJson() {
        final Gson gson = new Gson();
        return gson.toJson(this);
    }
}
