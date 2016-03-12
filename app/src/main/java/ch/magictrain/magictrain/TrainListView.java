package ch.magictrain.magictrain;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

import java.util.ArrayList;

import ch.magictrain.magictrain.models.Friend;
import ch.magictrain.magictrain.models.Train;
import ch.magictrain.magictrain.models.TrainLocation;
import ch.magictrain.magictrain.models.UpdateResponse;

public class TrainListView extends ListView {
    private Train train;
    private ArrayList<Friend> friends;
    private TrainLocation myLocation;

    public TrainListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setData(UpdateResponse response) {
        train = response.train;
        friends = response.friends;
        myLocation = response.my_location;
    }
}
