package com.bacon.corey.audiotimeshift;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public  class ViewPagerAdapter extends FragmentPagerAdapter {
    static int ITEMS;
    public ViewPagerAdapter(FragmentManager fm, int ITEMS) {
        super(fm);
        this.ITEMS = ITEMS;
    }

    @Override
    public int getCount() {
        return ITEMS;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return TestFragment.newInstance(position);
            case 1:
                return FilesListFragment.newInstance(position);
            case 2:
                return RecordOptionsFragment.newInstance(position);

            default:
                return TestFragment.newInstance(position);
        }

    }
}
