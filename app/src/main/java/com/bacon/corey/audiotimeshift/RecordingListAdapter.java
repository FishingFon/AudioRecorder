package com.bacon.corey.audiotimeshift;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RecordingListAdapter extends ArrayAdapter<Recording> {
    private final Context context;
    private final List<Recording> recordings;
    private final int layoutResourceId;
    TextView length;
    TextView date;
    TextView title;
    TextView icon;
    View row;
    int color;

    ArrayList <Integer> colorList = new ArrayList<Integer>();
    public RecordingListAdapter(Context context, int layoutResourceId, List<Recording> recordings) {
        super(context, R.layout.row_layout, recordings);
        this.context = context;
        this.recordings = recordings;
        this.layoutResourceId = layoutResourceId;

        colorList.add(context.getResources().getColor(R.color.c1));
        colorList.add(context.getResources().getColor(R.color.c2));
        colorList.add(context.getResources().getColor(R.color.c3));
        colorList.add(context.getResources().getColor(R.color.c4));
        colorList.add(context.getResources().getColor(R.color.c5));
        colorList.add(context.getResources().getColor(R.color.c6));
        colorList.add(context.getResources().getColor(R.color.c7));
        colorList.add(context.getResources().getColor(R.color.c8));
        colorList.add(context.getResources().getColor(R.color.c9));
        colorList.add(context.getResources().getColor(R.color.c10));
        colorList.add(context.getResources().getColor(R.color.c11));
        colorList.add(context.getResources().getColor(R.color.c12));
        colorList.add(context.getResources().getColor(R.color.c14));
        colorList.add(context.getResources().getColor(R.color.c16));
        colorList.add(context.getResources().getColor(R.color.c17));
        colorList.add(context.getResources().getColor(R.color.c18));




    }

    //public void addItems()
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
         row = convertView;

        if(row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
        }
            //View rowView = inflater.inflate(R.layout.row_layout, parent, false);

            //ImageView rowIcon = (ImageView) row.findViewById(R.id.row_icon);

            icon = (TextView) row.findViewById(R.id.row_icon);
            long recordingLen = recordings.get(position).getAudioLengthInSeconds();



        if (recordingLen < 60){
            icon.setText(Long.toString(Math.round((double) recordingLen)) + "s");
        }
        else if (recordingLen >= 60 && recordingLen < 3600){
            icon.setText(Long.toString(Math.round((double) recordingLen)/60) + "m");

        }
        else if (recordingLen >= 3600){
            icon.setText(Long.toString(Math.round((double) recordingLen)/3600) + "h");

        }
            GradientDrawable iconBackground = (GradientDrawable)icon.getBackground();
            iconBackground.setColor(colorList.get(recordings.get(position).getColor()));
            title = (TextView) row.findViewById(R.id.title);
            date = (TextView) row.findViewById(R.id.date);
            length = (TextView) row.findViewById(R.id.length);
            date.setText(recordings.get(position).getDateString());
            title.setText(recordings.get(position).getTitleString());
            length.setText(recordings.get(position).getFileSizeString());


        return row;
    }
    public int getRowColor(int position){

        return colorList.get(recordings.get(position).getColor());
    }

}

