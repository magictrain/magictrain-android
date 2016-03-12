package ch.magictrain.magictrain.models;

import com.google.gson.Gson;

import java.util.ArrayList;

public class UpdateResponse {
    public final Train train;
    public final TrainLocation my_location;
    public final ArrayList<Friend> friends;

    public UpdateResponse(Train train, TrainLocation my_location, ArrayList<Friend> friends) {
        this.train = train;
        this.my_location = my_location;
        this.friends = friends;
    }

    public static UpdateResponse fromJson(String json) {
        final Gson gson = new Gson();
        return gson.fromJson(json, UpdateResponse.class);
    }

    public String toJson() {
        final Gson gson = new Gson();
        return gson.toJson(this);
    }
}
