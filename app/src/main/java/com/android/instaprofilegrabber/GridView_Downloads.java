package com.android.instaprofilegrabber;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Created by Ramin on 8/16/2017.
 */

public class GridView_Downloads {
    private Context context;
    private View RootView;
    private String FolderName;
    private boolean isVideo;
    private ImageListAdapter adapter;

    public GridView_Downloads(Context context, View rootView, String folderName, boolean isVideo){
        this.context = context;
        this.RootView = rootView;
        this.FolderName = folderName;
        this.isVideo = isVideo;
    }

    public void begin() {
        final SwipeRefreshLayout gridRefresh = (SwipeRefreshLayout) RootView.findViewById(R.id.grid_refresh);

        gridRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                populate();
                gridRefresh.setRefreshing(false);
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                populate();
            }
        }, 200);
    }

    private void nothing_found(boolean permissionDenied) {
        View gridMessageContainer = RootView.findViewById(R.id.grid_message_container);
        gridMessageContainer.setVisibility(View.VISIBLE);

        TextView gridMessage = (TextView) gridMessageContainer.findViewById(R.id.grid_message);
        gridMessage.setText(permissionDenied ? "امکان خواندن از حافظه وجود ندارد" : "چیزی برای نمایش موجود نیست");
    }

    private void files_found() {
        RootView.findViewById(R.id.grid_message_container).setVisibility(View.GONE);
    }

    private void populate() {
        try {
            if (!RUtil.has_memory_read_permission(context)) {
                nothing_found(true);
                return;
            }

            final String theFiles[] = get_files();

            if (theFiles == null || theFiles.length == 0) {
                nothing_found(false);
                return;
            } else files_found();

            GridView gridView = (GridView) RootView.findViewById(R.id.gridview);
            if (adapter != null) adapter.notifyDataSetChanged();
            gridView.invalidateViews();
            gridView.setAdapter(adapter = new ImageListAdapter(context, theFiles, isVideo));

            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    try {
                        if (theFiles.length <= i) return;

                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.parse("file://" + theFiles[i]), isVideo ? "video/*" : "image/*");
                        context.startActivity(intent);
                    } catch (Exception ex) {
                    }
                }
            });
        } catch (Exception e) {
        }
    }

    private String[] get_files(){
        try {
            final String ext = isVideo ? ".mp4" : ".jpg";

            String dirName = Environment.getExternalStorageDirectory() + "/" + context.getString(R.string.app_folder);
            if (!FolderName.equals("")) dirName += "/" + FolderName;

            File files[] = new File(dirName).listFiles(new FileFilter() {
                @Override
                public boolean accept(File file) {
                    return file.getName().toLowerCase().endsWith(ext);
                }
            });

            if (files == null || files.length == 0) return new String[0];

            Arrays.sort(files, new Comparator<File>() {
                @Override
                public int compare(File file, File t1) {
                    return Long.valueOf(t1.lastModified()).compareTo(file.lastModified());
                }
            });

            String arr[] = new String[files.length];

            for (int i = 0, lnt = files.length; i < lnt; ++i)
                arr[i] = files[i].getPath();

            return arr;
        }catch (Exception ex){
            //Toast.makeText(context, ex.toString(), Toast.LENGTH_LONG).show();
            return new String[0];
        }
    }

    private class ImageListAdapter extends ArrayAdapter {
        private Context context;
        private LayoutInflater inflater;
        private boolean isVideo;

        private String[] imageUrls;

        public ImageListAdapter(Context context, String[] imageUrls, boolean isVideo) {
            super(context, R.layout.saved_image, imageUrls);

            this.context = context;
            this.imageUrls = imageUrls;
            this.isVideo = isVideo;

            inflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) convertView = inflater.inflate(R.layout.saved_image, parent, false);

            if(isVideo) {
                Glide
                        .with(context)
                        .load(Uri.fromFile(new File(imageUrls[position])))
                        .into((GridViewItem) convertView);
            }
            else {
                Picasso
                        .with(context)
                        .load(new File(imageUrls[position]))
                        .centerCrop()
                        .fit() // will explain later
                        .into((GridViewItem) convertView);
            }

            return convertView;
        }
    }
}
