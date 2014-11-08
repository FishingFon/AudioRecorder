package com.bacon.corey.audiotimeshift;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class RecordingListAdapter extends ArrayAdapter<Recording> {
    private final Context context;
    private final List<Recording> recordings;
    private final int layoutResourceId;
    TextView length;
    TextView date;
    TextView title;
    public RecordingListAdapter(Context context, int layoutResourceId, List<Recording> recordings) {
        super(context, R.layout.row_layout, recordings);
        this.context = context;
        this.recordings = recordings;
        this.layoutResourceId = layoutResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;

        if(row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            //View rowView = inflater.inflate(R.layout.row_layout, parent, false);

            //ImageView rowIcon = (ImageView) row.findViewById(R.id.row_icon);
            title = (TextView) row.findViewById(R.id.title);
            date = (TextView) row.findViewById(R.id.date);
            length = (TextView) row.findViewById(R.id.length);
            date.setText(recordings.get(position).getDateString(position));
            title.setText(recordings.get(position).getTitleString(position));
            length.setText(recordings.get(position).getLengthString(position));

        }


        return row;
    }
}
