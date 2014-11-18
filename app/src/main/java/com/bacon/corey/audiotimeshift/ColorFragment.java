package com.bacon.corey.audiotimeshift;

import android.os.Bundle;
import android.support.v4.app.Fragment;

public class ColorFragment extends Fragment {


    public ColorFragment() {
    }

    public static ColorFragment newInstance(int color) {
        final Bundle args = new Bundle();
        final ColorFragment fragment = new ColorFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public int getColor() {
        return -1;
    }

}

