package com.android.instaprofilegrabber;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONObject;

import java.net.URL;
import java.util.List;

import ir.adad.client.AdListener;
import ir.adad.client.AdView;

/**
 * Created by Ramin on 8/10/2017.
 */

public class UserView {
    private FragmentActivity context;
    private View view;
    private JSONObject user;
    private boolean addWatermark;

    public UserView(final FragmentActivity mContext,
                    LinearLayout root, JSONObject user, boolean add2Top, boolean mAddWatermark, boolean update) {
        this.context = mContext;
        this.user = user;
        this.addWatermark = mAddWatermark;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        try {
            view = inflater.inflate(R.layout.user, null);

            final String userId = user.getString(UsersContract.UsersEntry.COLUMN_NAME_UserID);
            final String username = user.getString(UsersContract.UsersEntry.COLUMN_NAME_Username);

            final TextView usernameView = (TextView) view.findViewById(R.id.username);
            usernameView.setText(username);

            TextView user_json = (TextView) view.findViewById(R.id.user_json);
            user_json.setText(user.toString());

            final TextView fullnameView = (TextView) view.findViewById(R.id.fullname);
            if (user.has(UsersContract.UsersEntry.COLUMN_NAME_FullName) &&
                    !user.getString(UsersContract.UsersEntry.COLUMN_NAME_FullName).equals("")) {
                fullnameView.setText(user.getString(UsersContract.UsersEntry.COLUMN_NAME_FullName));
                fullnameView.setVisibility(View.VISIBLE);
            }

            final ImageView loadingImage = (ImageView) view.findViewById(R.id.userLoading);
            Glide.with(context).load(R.mipmap.loading).into(loadingImage);

            final ShowImage showImage = new ShowImage() {
                @Override
                protected void onPostExecute(Bitmap image) {
                    try {
                        loadingImage.setVisibility(View.GONE);

                        if(RUtil.profile_downloadable()) view.findViewById(R.id.download_button).setVisibility(View.VISIBLE);
                        view.findViewById(R.id.bookmark_button).setVisibility(View.VISIBLE);

                        /*
                        if (addWatermark && !RUtil.is_pro_version(context))
                            image = RUtil.add_watermark(context, image);
                        */

                        ((ImageView) view.findViewById(R.id.userImage)).setImageBitmap(image);
                    } catch (Exception e) {
                        //Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                    }
                }
            };

            if (!update){
                String imageUrl = user.has(UserImagesContract.UserImagesEntry.COLUMN_NAME_PicURLHD) ?
                        user.getString(UserImagesContract.UserImagesEntry.COLUMN_NAME_PicURLHD) :
                        user.getString(UsersContract.UsersEntry.COLUMN_NAME_PicURL);
                showImage.execute(RUtil.get_full_size_url(imageUrl));
                open_insta(username);
            }
            else {
                new UserGet(context, username, userId) {
                    @Override
                    public void onUserGet(JSONObject newUsr) {
                        try {
                            if (newUsr == null) {
                                loadingImage.setVisibility(View.GONE);
                                view.findViewById(R.id.download_button).setVisibility(View.VISIBLE);
                                view.findViewById(R.id.bookmark_button).setVisibility(View.VISIBLE);
                                ((ImageView) view.findViewById(R.id.userImage)).setImageResource(R.mipmap.user_doesnt_exist);
                                return;
                            }

                            open_insta(username);

                            usernameView.setText(newUsr.getString(UsersContract.UsersEntry.COLUMN_NAME_Username));

                            if (newUsr.has(UsersContract.UsersEntry.COLUMN_NAME_FullName)) {
                                fullnameView.setText(newUsr.getString(UsersContract.UsersEntry.COLUMN_NAME_FullName));
                                fullnameView.setVisibility(View.VISIBLE);
                            } else fullnameView.setVisibility(View.GONE);

                            String imageUrl = newUsr.getString(UserImagesContract.UserImagesEntry.COLUMN_NAME_PicURLHD);
                            showImage.execute(RUtil.get_full_size_url(imageUrl));
                        } catch (Exception ex) {
                        }
                    }
                };
            }

            set_ondownload();
            set_onbookmark();

            if (add2Top) root.addView(view, 0);
            else root.addView(view);

            adad(root, add2Top);
        } catch (Exception ex) {
            //Toast.makeText(context, ex.toString(), Toast.LENGTH_LONG).show();
        }
    }

    private void open_insta(final String username){
        ImageView instaIcon = (ImageView) view.findViewById(R.id.insta_icon);
        instaIcon.setVisibility(View.VISIBLE);
        instaIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RUtil.open_instagram(context, username);
            }
        });
    }

    private void set_ondownload() {
        LinearLayout downloadButton = (LinearLayout) view.findViewById(R.id.download_button);

        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (RUtil.has_memory_write_permission(context)) save();
                else
                    ActivityCompat.requestPermissions(context, new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE
                    }, MainActivity.Activity.PERMISSION_REQUEST_SAVE_TO_GALLERY);
            }
        });
    }

    private boolean isBookmarked() {
        try {
            String userId = user.getString(UsersContract.UsersEntry.COLUMN_NAME_UserID);

            JSONObject searchParams = new JSONObject();
            searchParams.put(UsersContract.UsersEntry.COLUMN_NAME_UserID, userId);
            List<JSONObject> users = DBUtil.getUsers(context, searchParams);

            JSONObject dbUser = null;

            if (users.size() == 0) return false;

            dbUser = users.get(0);

            return dbUser.has(UsersContract.UsersEntry.COLUMN_NAME_Following) &&
                    dbUser.getString(UsersContract.UsersEntry.COLUMN_NAME_Following).equals("true");
        } catch (Exception ex) {
            return false;
        }
    }

    private void set_bookmark_values() {
        TextView bookmarkText = (TextView) view.findViewById(R.id.bookmark_text);
        ImageView bookmarkIcon = (ImageView) view.findViewById(R.id.bookmark_icon);

        boolean bookmarked = isBookmarked();

        bookmarkText.setText(bookmarked ? "پیگیرش هستم" : "پیگیرش می شم");
        bookmarkIcon.setImageResource(bookmarked ? R.mipmap.bookmark_green : R.mipmap.bookmark);
    }

    private void set_onbookmark() {
        LinearLayout bookmarkButton = (LinearLayout) view.findViewById(R.id.bookmark_button);

        set_bookmark_values();

        bookmarkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isBookmarked()) {
                    DBUtil.unfollow(context, user);
                    RUtil.log(context, context.getString(R.string.mysociome_log_remove_bookmark), user);
                } else {
                    DBUtil.follow(context, user);
                    RUtil.log(context, context.getString(R.string.mysociome_log_bookmark), user);
                }

                set_bookmark_values();
            }
        });
    }

    private void save() {
        ImageView imageView = (ImageView) view.findViewById(R.id.userImage);
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();

        try {
            RUtil.save(context, bitmap, user.getString(UsersContract.UsersEntry.COLUMN_NAME_Username));
        } catch (Exception e) {
        }
    }

    private void adad(LinearLayout root, boolean add2Top) {
        if (RUtil.is_pro_version(context)) return;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        try {
            View view = inflater.inflate(R.layout.adad, null);

            final AdView objBanner = (AdView) view.findViewById(R.id.banner_ad_view);

            objBanner.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {

                }

                @Override
                public void onAdFailedToLoad() {
                    objBanner.setVisibility(View.GONE);
                }

                @Override
                public void onMessageReceive(JSONObject jsonObject) {
                }

                @Override
                public void onRemoveAdsRequested() {
                }
            });

            if (add2Top) root.addView(view, 1);
            else root.addView(view);
        } catch (Exception ex) {
            //Toast.makeText(context, ex.toString(), Toast.LENGTH_LONG).show();
        }
    }

    class ShowImage extends AsyncTask<Object, Void, Bitmap> {
        private Exception exception = null;
        private String FullSizeURL;

        protected Bitmap doInBackground(Object... params) {
            try {
                if (params.length > 0) FullSizeURL = (String) params[0];

                return BitmapFactory.decodeStream(new URL(FullSizeURL).openConnection().getInputStream());
            } catch (Exception e) {
                this.exception = e;
                return null;
            }
        }

        protected void onPostExecute(Bitmap image) {
        }
    }
}
