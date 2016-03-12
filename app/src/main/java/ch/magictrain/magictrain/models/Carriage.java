package ch.magictrain.magictrain.models;

public class Carriage {
    public final String id;
    public final String type;
    public final int klass;
    public final int decks;
    public final boolean is_locomotive;
    public final float length;

    public Carriage(String id, String type, int klass, int decks, boolean is_locomotive, float length) {
        this.id = id;
        this.type = type;
        this.klass = klass;
        this.decks = decks;
        this.is_locomotive = is_locomotive;
        this.length = length;
    }
}
