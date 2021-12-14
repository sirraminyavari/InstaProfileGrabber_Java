package com.android.instaprofilegrabber;

import android.provider.BaseColumns;

/**
 * Created by Ramin on 1/27/2017.
 */

public final class UserImagesContract {
    private UserImagesContract(){}

    public static class UserImagesEntry implements BaseColumns {
        public static final String TABLE_NAME = "UserImages";
        public static final String COLUMN_NAME_UserID = "user_id";
        public static final String COLUMN_NAME_Username = "username";
        public static final String COLUMN_NAME_FullName = "fullname";
        public static final String COLUMN_NAME_FullSizePicURL = "full_size_pic_url";
        public static final String COLUMN_NAME_PicURL = "pic_url";
        public static final String COLUMN_NAME_PicURLHD = "pic_url_hd";
    }
}

