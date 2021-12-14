package com.android.instaprofilegrabber;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

/**
 * Created by Ramin on 8/11/2017.
 */

public class Fragment_Downloads extends Fragment {
    private static final String ARG_SECTION_NUMBER = "section_number";
    private View RootView = null;

    public Fragment_Downloads(){
    }

    public static Fragment_Downloads newInstance(int sectionNumber) {
        Fragment_Downloads fragment = new Fragment_Downloads();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(RootView != null) return RootView;

        RootView = inflater.inflate(R.layout.downloads, container, false);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                FragmentActivity activity = getActivity();

                TabLayout tabs = (TabLayout) RootView.findViewById(R.id.download_tabs);
                tabs.addTab(tabs.newTab());
                tabs.addTab(tabs.newTab());
                tabs.addTab(tabs.newTab());
                tabs.setTabGravity(TabLayout.GRAVITY_FILL);
                tabs.setTabTextColors(Color.BLACK, Color.BLACK);

                RTextView first = new RTextView(activity);
                RTextView second = new RTextView(activity);
                RTextView third = new RTextView(activity);

                first.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                second.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                third.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                first.setText("پروفایل");
                second.setText("عکس");
                third.setText("فیلم");

                tabs.getTabAt(0).setCustomView(first);
                tabs.getTabAt(1).setCustomView(second);
                tabs.getTabAt(2).setCustomView(third);

                final ViewPager pager = (ViewPager) RootView.findViewById(R.id.downloads_pager);
                pager.setAdapter(new SectionsPagerAdapter_Downloads(activity.getSupportFragmentManager()));

                pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabs));

                tabs.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener(){
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        pager.setCurrentItem(tab.getPosition());
                    }

                    @Override
                    public void onTabUnselected(TabLayout.Tab tab) {

                    }

                    @Override
                    public void onTabReselected(TabLayout.Tab tab) {

                    }
                });
            }
        }, 200);

        return RootView;
    }
}
