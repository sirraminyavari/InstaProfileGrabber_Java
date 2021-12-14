package com.android.instaprofilegrabber;

/**
 * Created by Ramin on 8/6/2017.
 */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {
    private int PagesCount = 3;

    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public SectionsPagerAdapter(FragmentManager fm, int num) {
        super(fm);

        PagesCount = num;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        switch (position){
            case 0:
                return Fragment_HomePage.newInstance(position + 1);
            case 1:
                return Fragment_Recent.newInstance(position + 1);
            case 2:
                return Fragment_Downloads.newInstance(position + 1);
        }

        return null;
    }

    @Override
    public int getCount() {
        return PagesCount;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "SECTION 1";
            case 1:
                return "SECTION 2";
            case 2:
                return "SECTION 3";
        }
        return null;
    }
}
