package ch.magictrain.magictrain.views;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.google.common.base.Optional;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;


import ch.magictrain.magictrain.Settings;
import ch.magictrain.magictrain.models.Carriage;
import ch.magictrain.magictrain.models.Friend;
import ch.magictrain.magictrain.models.Train;
import ch.magictrain.magictrain.models.TrainLocation;
import ch.magictrain.magictrain.models.UpdateResponse;

public class TrainListView extends ListView {
    private Train train;
    private ArrayList<Friend> friends;
    private TrainLocation myLocation;

    ListHeaderAdapter adapter;

    public class ListElement {
        public final int id;
        public final int carriageNo;
        public final ArrayList<Friend> friends;
        public final boolean isCarriageBegin;
        public final boolean isCarriageEnd;
        public final Optional<TrainLocation> myLocation;
        public final Carriage carriage;

        public ListElement(int id, ArrayList<Friend> friends, boolean isWagonBegin, boolean isWagonEnd, Optional<TrainLocation> myLocation, Carriage carriage, int carriageNo) {
            this.id = id;
            this.friends = friends;
            this.isCarriageBegin = isWagonBegin;
            this.isCarriageEnd = isWagonEnd;
            this.myLocation = myLocation;
            this.carriage = carriage;
            this.carriageNo = carriageNo;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            Formatter formatter = new Formatter(sb, Locale.US);
            formatter.format(Locale.US, "ListElement<car=%s-%s id=%d nFriends=%d begin=%b end=%b myloc=%b>",
                    carriage.type, carriage.id, id, friends.size(), isCarriageBegin, isCarriageEnd, myLocation.isPresent());
            return sb.toString();
        }
    }

    public TrainListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        adapter = new ListHeaderAdapter();
    }

    public void setData(UpdateResponse response) {
        Log.d(Settings.LOGTAG, "got data in list view");

        train = response.train;
        friends = response.friends;
        myLocation = response.my_location;

        adapter.updateData(getListElements());
        this.setAdapter(adapter);
    }

    private class ListHeaderAdapter extends BaseAdapter {
        private ArrayList<ListElement> listElements = new ArrayList<>();
        private ListHeader head;

        public ListHeaderAdapter() {
        }

        public void updateData(ArrayList<ListElement> listElements) {
            this.listElements = listElements;
            this.notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            if(listElements.size() == 0)
                return 0;
            return listElements.size() + 1;
        }

        @Override
        public Object getItem(int position) {
            Log.d(Settings.LOGTAG, "get object with pos=" + position);
            if(position < 1)
                return null;
            return listElements.get(position - 1);
        }

        @Override
        public long getItemId(int position) {
            Log.d(Settings.LOGTAG, "getItemId pos="+position);
            if(position == 0)
                return -1;
            return ((ListElement)getItem(position)).id;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(position == 0) {
                if(head == null)
                    head = new ListHeader(getContext());
                head.setData(train);
                return head;
            }

            Log.d(Settings.LOGTAG, "getView convertView=" + convertView);

            if(convertView == null || convertView instanceof ListHeader) {
                convertView = new ListElementView(getContext());
            }

            ListElementView elementView = (ListElementView)convertView;

            elementView.setData((ListElement) getItem(position));

            return elementView;
        }
    }

    private ArrayList<ListElement> getListElements() {
        ArrayList<ListElement> listElements = new ArrayList<>();

        int id = 1;
        int carriageNo = 0;
        for(Carriage carriage : train.carriages) {
            final int nSegments = (int) Math.ceil(carriage.length / Settings.CARRIAGE_SEGMENT_LENGTH);
            final List<ListElement> carriageSegments = new ArrayList<>();
            int peopleInCarriage = 0;
            for(int i = 0; i < nSegments; i++, id++) {
                final double segmentBegin = i * Settings.CARRIAGE_SEGMENT_LENGTH;
                final double segmentEnd = (i + 1) * Settings.CARRIAGE_SEGMENT_LENGTH;
                final boolean carriageBegin = i == 0;
                final boolean carriageEnd = i == (nSegments - 1);

                final ArrayList<Friend> friendsInSegment = new ArrayList<>();
                for(Friend friend: friends) {
                    // empty friend object :(
                    if(friend.location == null) {
                        continue;
                    }
                    final double offset = friend.location.offset;
                    if(friend.location.carriage_id.equals(carriage.id) &&
                            offset >= segmentBegin &&
                            offset < segmentEnd) {
                        friendsInSegment.add(friend);
                        peopleInCarriage++;
                    }
                }

                Optional<TrainLocation> myLoc = Optional.absent();
                if(myLocation.carriage_id.equals(carriage.id) &&
                        myLocation.offset >= segmentBegin &&
                        myLocation.offset < segmentEnd) {
                    myLoc = Optional.of(myLocation);
                    peopleInCarriage++;
                }

                carriageSegments.add(new ListElement(
                        id,
                        friendsInSegment,
                        carriageBegin,
                        carriageEnd,
                        myLoc,
                        carriage,
                        carriageNo
                ));
            }

            if(peopleInCarriage == 0) {
                while(carriageSegments.size() > 2) {
                    carriageSegments.remove(1);
                }
            }
            listElements.addAll(carriageSegments);
            carriageNo++;
        }
        return listElements;
    }
}
