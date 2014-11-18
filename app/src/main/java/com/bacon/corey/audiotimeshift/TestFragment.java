package com.bacon.corey.audiotimeshift;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class TestFragment extends ColorFragment{
        int mNum;
    public static int FRAGMENT_COLOR;

        /**
         * Create a new instance of CountingFragment, providing "num"
         * as an argument.
         */

        public static TestFragment newInstance(int num, Context context) {
            TestFragment f = new TestFragment();
            FRAGMENT_COLOR = context.getResources().getColor(R.color.c10);
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
            mNum = getArguments() != null ? getArguments().getInt("num") : 1;
        }

        /**
         * The Fragment's UI is just a simple text view showing its
         * instance number.
         */
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.fragment_test, container, false);
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

