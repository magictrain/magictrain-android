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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.skyfishjy.library.RippleBackground;

import ch.magictrain.magictrain.R;
import ch.magictrain.magictrain.Settings;
import ch.magictrain.magictrain.models.Friend;

public class ListElementView extends RelativeLayout {
    private TrainListView.ListElement data;
    private TextView nameTextView;
    private TextView carriageNrTextView;
    private TextView decksTextView;
    private ImageView klassImageView;
    private ImageView background;
    private ImageView featureImageView;
    private LinearLayout bounceContainer;

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
        // create names string
        StringBuilder displayString = new StringBuilder();
        int numberOfPeople = 0;
        int imHere = 0;
        if(data.myLocation.isPresent()) {
            numberOfPeople++;
            imHere++;
            displayString.append("Du");
            if(data.carriage.decks > 1) {
                displayString.append(data.myLocation.get().deck == 1? " (lower floor)":" (upper floor)");
            }
        }
        for(Friend friend: data.friends) {
            numberOfPeople ++;
            if(displayString.length() != 0) {
                displayString.append(", ");
            }
            displayString.append(friend.fb_name);
            if(data.carriage.decks > 1) {
                displayString.append(friend.location.deck == 1? " (lower floor)":" (upper floor)");
            }
        }
        nameTextView.setText(displayString);
        background.setImageDrawable(getDrawable());


        bounceContainer.removeAllViews();
        // insert the bounce things
        for(int i=0; i<numberOfPeople; i++) {
            RippleBackground ripple;
            if(imHere > 0) {
                ripple = (RippleBackground)LayoutInflater.from(getContext()).inflate(R.layout.bounce_blue, null);
                imHere--;
            } else {
                ripple = (RippleBackground)LayoutInflater.from(getContext()).inflate(R.layout.bounce, null);
            }
            ripple.startRippleAnimation();
            bounceContainer.addView(ripple);
            bounceContainer.requestLayout();
            ripple.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
            ripple.getLayoutParams().width = 180;
        }

        // update carriage number
        if(data.isCarriageBegin) {
            carriageNrTextView.setText("Wagen "+ (data.id));
            decksTextView.setText(data.carriage.decks >1?"Double Deck":"Normal");
            if(!data.carriage.type.equals("Re 460")) {
                klassImageView.setImageResource(data.carriage.klass==1?R.drawable.sbb_sa_1:R.drawable.sbb_sa_2);
            }
            else {
                klassImageView.setImageResource(0);
            }

            if(data.carriage.type.contains("WR")) {
                featureImageView.setImageResource(R.drawable.sbb_sa_wr);
            } else if(data.carriage.type.contains("BT")) {
                featureImageView.setImageResource(R.drawable.sbb_sa_fa);
            } else if(data.carriage.type.contains("AD")) {
                featureImageView.setImageResource(R.drawable.sbb_sa_d);
            } else if (data.carriage.type.contains("Re 460")) {
                featureImageView.setImageResource(R.drawable.sbb_sa_dz);
            } else {
                featureImageView.setImageResource(0);
            }
        }
        else {
            carriageNrTextView.setText("");
            decksTextView.setText("");
            klassImageView.setImageResource(0);
            featureImageView.setImageResource(0);
        }


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
        bounceContainer = (LinearLayout) getRootView().findViewById(R.id.bounceContainer);
        decksTextView = (TextView) getRootView().findViewById(R.id.decksTextView);
        klassImageView = (ImageView) getRootView().findViewById(R.id.klassImageView);
        featureImageView = (ImageView) getRootView().findViewById(R.id.featureImageView);
    }
}
