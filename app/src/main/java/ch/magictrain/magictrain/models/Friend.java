package ch.magictrain.magictrain.models;

public class Friend {
    public final String fb_name;
    public final String fb_id;
    public final TrainLocation location;

    public Friend(String fb_name, String fb_id, TrainLocation location) {
        this.fb_name = fb_name;
        this.fb_id = fb_id;
        this.location = location;
    }
}
