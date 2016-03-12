package ch.magictrain.magictrain.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import ch.magictrain.magictrain.R;
import ch.magictrain.magictrain.models.Train;

public class ListHeader extends RelativeLayout {
    private Train train;
    private TextView trainName;

    public void setData(Train train) {
        this.train = train;
        updateViews();
    }

    private void updateViews() {
        trainName.setText(train.name + " to " + train.destination);
    }

    public ListHeader(Context context) {
        super(context);
        init(null, 0);
    }

    public ListHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public ListHeader(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        inflate(getContext(), R.layout.list_header, this);
        trainName = (TextView)getRootView().findViewById(R.id.trainName);
    }
}
