package ch.magictrain.magictrain.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import ch.magictrain.magictrain.R;
import ch.magictrain.magictrain.Settings;

public class ListElementView extends RelativeLayout {
    private TrainListView.ListElement data;
    private TextView nameTextView;
    private TextView carriageNrTextView;
    private ImageView background;

    public void setData(TrainListView.ListElement data) {
        this.data = data;
        updateViews();
    }

    private Drawable getDrawable() {
        StringBuilder fname = new StringBuilder();
        fname.append(data.carriage.type.replace(' ', '_').replace('-', '_').toLowerCase());
        fname.append('_');
        if(data.isCarriageBegin) {
            fname.append("01");
        } else if (data.isCarriageEnd) {
            fname.append("03");
        } else {
            fname.append("02");
        }

        Log.d(Settings.LOGTAG, "get resource " + fname.toString());

        int imageResource = getResources().getIdentifier(fname.toString(), "drawable", getContext().getPackageName());
        return getResources().getDrawable(imageResource, null);
    }

    private void updateViews() {
        nameTextView.setText(data.toString());
        background.setImageDrawable(getDrawable());
    }

    public ListElementView(Context context) {
        super(context);
        init(null, 0);
    }

    public ListElementView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public ListElementView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        inflate(getContext(), R.layout.list_element_view, this);
        nameTextView = (TextView)getRootView().findViewById(R.id.nameTextView);
        carriageNrTextView = (TextView)getRootView().findViewById(R.id.carriageNrTextView);
        background = (ImageView) getRootView().findViewById(R.id.background);
    }
}
