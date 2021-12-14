package com.android.instaprofilegrabber;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by Ramin on 8/10/2017.
 */

public class CustomAdapter_Recent extends ArrayAdapter<SuggestedUser> {
    Context mContext;
    int layoutResourceId;
    SuggestedUser data[] = null;

    public CustomAdapter_Recent(Context mContext, int layoutResourceId, SuggestedUser[] data) {

        super(mContext, layoutResourceId, data);

        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        try {
            if (convertView == null) {
                LayoutInflater inflater = ((MainActivity) mContext).getLayoutInflater();
                convertView = inflater.inflate(layoutResourceId, parent, false);
            }

            SuggestedUser objectItem = data[position];

            ImageView icon = (ImageView) convertView.findViewById(R.id.suggestedIcon);
            icon.setImageResource(objectItem.Searched ? R.mipmap.search_bright : R.mipmap.recent_bright);

            TextView textViewItem = (TextView) convertView.findViewById(R.id.suggestedText);
            textViewItem.setText(objectItem.Username + (objectItem.FullName.equals("") ? "" : " (" + objectItem.FullName + ")"));

            if(objectItem.ProfilePicURL != null && !objectItem.ProfilePicURL.equals("")){
                Picasso
                        .with(mContext)
                        .load(objectItem.ProfilePicURL)
                        .centerCrop()
                        .fit() // will explain later
                        .into((ImageView) convertView.findViewById(R.id.suggestedPic));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return convertView;
    }
}
