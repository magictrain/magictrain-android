package ch.magictrain.magictrain.models;

public class TrainLocation {
    public final String carriage_id;
    public final float offset;
    public final int deck;

    public TrainLocation(String carriage_id, float offset, int deck) {
        this.carriage_id = carriage_id;
        this.offset = offset;
        this.deck = deck;
    }
}
