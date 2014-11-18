package com.bacon.corey.audiotimeshift;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public  class ViewPagerAdapter extends FragmentPagerAdapter {
    static int ITEMS;
    Context context;

    public ViewPagerAdapter(FragmentManager fm, int ITEMS, Context context) {
        super(fm);
        this.ITEMS = ITEMS;
        this.context = context;
    }

    @Override
    public int getCount() {
        return ITEMS;
    }

    @Override
    public ColorFragment getItem(int position) {

        switch (position) {
            case 0:
                return TestFragment.newInstance(position, context);
            case 1:
                return FilesListFragment.newInstance(position, context);
            case 2:
                return RecordOptionsFragment.newInstance(position, context);

            default:
                return TestFragment.newInstance(position, context);
        }

    }
}
