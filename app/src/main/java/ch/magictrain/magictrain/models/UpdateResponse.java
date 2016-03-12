package ch.magictrain.magictrain.models;

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
}
