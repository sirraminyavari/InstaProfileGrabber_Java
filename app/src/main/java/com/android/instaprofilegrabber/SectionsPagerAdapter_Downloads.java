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
public class SectionsPagerAdapter_Downloads extends FragmentPagerAdapter {
    public SectionsPagerAdapter_Downloads(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        switch (position) {
            case 0:
                return Fragment_Downloads_Profile.newInstance(position + 1);
            case 1:
                return Fragment_Downloads_Image.newInstance(position + 1);
            case 2:
                return Fragment_Downloads_Video.newInstance(position + 1);
        }

        return null;
    }

    @Override
    public int getCount() {
        // Show 3 total pages.
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "پروفایل";
            case 1:
                return "عکس";
            case 2:
                return "فیلم";
        }
        return null;
    }
}