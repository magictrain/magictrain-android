package ch.magictrain.magictrain.models;

import java.util.ArrayList;

public class Train {
    public final String id;
    public final String name;
    public final String destination;
    public final Location location;
    public final ArrayList<Carriage> carriages;

    public Train(String id, String name, String destination, Location location, ArrayList<Carriage> carriages) {
        this.id = id;
        this.name = name;
        this.destination = destination;
        this.location = location;
        this.carriages = carriages;
    }
}
