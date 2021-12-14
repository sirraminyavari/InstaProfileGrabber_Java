package com.android.instaprofilegrabber;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Ramin on 8/11/2017.
 */

public class Fragment_Downloads_Profile extends Fragment {
    private static final String ARG_SECTION_NUMBER = "section_number";
    private View RootView = null;

    public Fragment_Downloads_Profile() {
    }

    public static Fragment_Downloads_Profile newInstance(int sectionNumber) {
        Fragment_Downloads_Profile fragment = new Fragment_Downloads_Profile();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (RootView != null) return RootView;

        RootView = inflater.inflate(R.layout.downloads_grid, container, false);

        new GridView_Downloads(getActivity(), RootView, "", false).begin();

        return RootView;
    }
}