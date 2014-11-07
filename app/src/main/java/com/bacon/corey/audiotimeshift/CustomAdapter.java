package com.bacon.corey.audiotimeshift;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class CustomAdapter extends ArrayAdapter {
    private final Context context;
    private final List recordings;

    public CustomAdapter(Context context, List<Recording> recordings) {
        super(context, R.layout.row_layout, recordings);
        this.context = context;
        this.recordings = recordings;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.row_layout, parent, false);

        ImageView rowIcon = (ImageView) rowView.findViewById(R.id.row_icon);
        TextView title = (TextView) rowView.findViewById(R.id.title);
        TextView date = (TextView) rowView.findViewById(R.id.date);
        TextView length = (TextView) rowView.findViewById(R.id.length);
        

        return super.getView(position, convertView, parent);
    }
}
