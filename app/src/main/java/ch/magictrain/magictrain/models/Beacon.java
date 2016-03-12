package ch.magictrain.magictrain.models;

public class Beacon {
    public final float proximity_m;
    public final String uuid; // MAC *lol*

    public Beacon(float proximity_m, String uuid) {
        this.proximity_m = proximity_m;
        this.uuid = uuid;
    }
}
