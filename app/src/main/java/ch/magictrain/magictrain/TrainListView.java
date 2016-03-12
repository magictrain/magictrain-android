package ch.magictrain.magictrain;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.common.base.Optional;

import java.util.ArrayList;
import java.util.Iterator;


import ch.magictrain.magictrain.models.Carriage;
import ch.magictrain.magictrain.models.Friend;
import ch.magictrain.magictrain.models.Train;
import ch.magictrain.magictrain.models.TrainLocation;
import ch.magictrain.magictrain.models.UpdateResponse;

public class TrainListView extends ListView {
    private Train train;
    private ArrayList<Friend> friends;
    private TrainLocation myLocation;
    private Activity mActivity;

    ListAdapter adapter;

    private class ListElement {
        public final int id;
        public final ArrayList<Friend> friends;
        public final boolean isWagonBegin;
        public final boolean isWagonEnd;
        // might be null
        public final Optional<TrainLocation> myLocation;

        public ListElement(int id, ArrayList<Friend> friends, boolean isWagonBegin, boolean isWagonEnd, Optional<TrainLocation> myLocation) {
            this.id = id;
            this.friends = friends;
            this.isWagonBegin = isWagonBegin;
            this.isWagonEnd = isWagonEnd;
            this.myLocation = myLocation;
        }
    }

    public TrainListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mActivity = (Activity)getContext();
        adapter = new ListAdapter();
    }

    public void setData(UpdateResponse response) {
        Log.d(Settings.LOGTAG, "got data in list view");

        train = response.train;
        friends = response.friends;
        myLocation = response.my_location;

        adapter.updateData(getListElements());
        this.setAdapter(adapter);
    }

    private class ListAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        ArrayList<ListElement> listElements = new ArrayList<>();

        public ListAdapter() {
            this.inflater = mActivity.getLayoutInflater();
        }

        public void updateData(ArrayList<ListElement> listElements) {
            this.listElements = listElements;
            this.notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return listElements.size();
        }

        @Override
        public Object getItem(int position) {
            return listElements.get(position);
        }

        @Override
        public long getItemId(int position) {
            return listElements.get(position).id;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                convertView = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
            }

            TextView txt = (TextView)convertView.findViewById(android.R.id.text1);
            txt.setText(getItem(position).toString());

            return convertView;
        }
    }

    private ArrayList<ListElement> getListElements() {
        ArrayList<ListElement> listElements = new ArrayList<>();

        int id = 1;
        for(Carriage carriage : train.carriages) {
            final int nSegments = (int) Math.ceil(carriage.length / Settings.CARRIAGE_SEGMENT_LENGTH);
            for(int i = 0; i < nSegments; i++, id++) {
                final double segmentBegin = i * Settings.CARRIAGE_SEGMENT_LENGTH;
                final double segmentEnd = (i + 1) * Settings.CARRIAGE_SEGMENT_LENGTH;
                final boolean carriageBegin = i == 0;
                final boolean carriageEnd = i == (nSegments - 1);

                final ArrayList<Friend> friendsInSegment = new ArrayList<>();
                for(Friend friend: friends) {
                    final double offset = friend.location.offset;
                    if(friend.location.carriage_id.equals(carriage.id) &&
                            offset >= segmentBegin &&
                            offset < segmentEnd) {
                        friendsInSegment.add(friend);
                    }
                }

                Optional<TrainLocation> myLoc = Optional.absent();
                if(myLocation.carriage_id.equals(carriage.id) &&
                        myLocation.offset >= segmentBegin &&
                        myLocation.offset < segmentEnd) {
                    myLoc = Optional.of(myLocation);
                }

                listElements.add(new ListElement(
                        id,
                        friends,
                        carriageBegin,
                        carriageEnd,
                        myLoc
                ));
            }
        }
        return listElements;
    }
}
