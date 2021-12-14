package com.android.instaprofilegrabber;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Ramin on 8/11/2017.
 */

public class Fragment_Downloads_Image extends Fragment {
    private static final String ARG_SECTION_NUMBER = "section_number";
    private View RootView = null;

    public Fragment_Downloads_Image() {
    }

    public static Fragment_Downloads_Image newInstance(int sectionNumber) {
        Fragment_Downloads_Image fragment = new Fragment_Downloads_Image();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (RootView != null) return RootView;

        RootView = inflater.inflate(R.layout.downloads_grid, container, false);

        new GridView_Downloads(getActivity(), RootView, App.getContext().getText(R.string.image_folder).toString(), false).begin();

        return RootView;
    }
}




    /*
    public class ImageAdapter extends BaseAdapter {
        private Context mContext;
        private boolean isVideo;
        private View[] TheView;

        public ImageAdapter(Context c, boolean isVideo) {
            this.mContext = c;
            this.isVideo = isVideo;
            this.TheView = new View[TheFiles.length];
        }

        public int getCount() {
            return TheFiles.length;
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            if(TheView[position] != null) return  TheView[position];
            else return (TheView[position] = isVideo ? getVideo(position, convertView, parent) : getImage(position, convertView, parent));
        }

        public View getVideo(int position, View convertView, ViewGroup parent) {
            try {
                View newView = null;

                if (convertView == null) {
                    ImageView imageView = new ImageView(mContext);
                    imageView.setLayoutParams(new GridView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    imageView.setAdjustViewBounds(true);

                    Bitmap bmp = ThumbnailUtils.createVideoThumbnail(TheFiles[position], 0);

                    imageView.setImageBitmap(RUtil.get_square_bitmap(bmp));

                    FrameLayout layout = new FrameLayout(mContext);
                    layout.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    layout.setForegroundGravity(Gravity.CENTER);

                    ImageView playIcon = new ImageView(mContext);
                    FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    lp.gravity = Gravity.CENTER;
                    playIcon.setLayoutParams(lp);
                    playIcon.setImageResource(R.mipmap.play);

                    layout.addView(imageView);
                    layout.addView(playIcon);

                    newView = layout;
                } else newView = convertView;

                return newView;
            }catch (Exception ex){
                return null;
            }
        }

        public View getImage(int position, View convertView, ViewGroup parent) {
            try {
                ImageView imageView = null;

                if (convertView == null) {
                    imageView = new ImageView(mContext);
                    imageView.setLayoutParams(new GridView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    imageView.setAdjustViewBounds(true);

                    Bitmap bmp = BitmapFactory.decodeFile(TheFiles[position]);

                    //imageView.setImageBitmap(RUtil.get_square_bitmap(bmp));
                    imageView.setImageBitmap(ThumbnailUtils.extractThumbnail(bmp, 100, 100));
                } else imageView = (ImageView) convertView;

                return imageView;
            }catch (Exception ex){
                return null;
            }
        }
    }
    */