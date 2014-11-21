package com.bacon.corey.audiotimeshift;


import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class RecordOptionsFragment extends ColorFragment{
        int mNum;
        public static int FRAGMENT_COLOR;
        Context context;
    /**
         * Create a new instance of CountingFragment, providing "num"
         * as an argument.
         */

        public static RecordOptionsFragment newInstance(int num, Context context) {

            FRAGMENT_COLOR = context.getResources().getColor(R.color.c15);

            RecordOptionsFragment f = new RecordOptionsFragment();
            // Supply num input as an argument.
            Bundle args = new Bundle();
            args.putInt("num", num);
            f.setArguments(args);
            return f;
        }

        /**
         * When creating, retrieve this instance's number from its arguments.
         */
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            this.context = context;

            mNum = getArguments() != null ? getArguments().getInt("num") : 1;
        }

        /**
         * The Fragment's UI is just a simple text view showing its
         * instance number.
         */
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.fragment_record_options, container, false);
            Spinner sampleRateSpinner = (Spinner) v.findViewById(R.id.sampleRateSpinner);
            ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.sampleRates, android.R.layout.simple_spinner_item);
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sampleRateSpinner.setAdapter(spinnerAdapter);
            return v;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

        }

    @Override
    public int getColor() {
        return FRAGMENT_COLOR;
    }
    }

