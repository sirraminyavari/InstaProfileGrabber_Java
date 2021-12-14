package com.android.instaprofilegrabber;

import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.ImageView;

public class InstagramLogin extends AppCompatActivity {
    public static InstagramLogin Activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instagram_login);

        InstagramLogin.Activity = this;

        //set status bar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = InstagramLogin.this.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(InstagramLogin.this, R.color.thm_color_verywarm));
        }
        //end of set status bar color

        ImageView backButton = (ImageView) findViewById(R.id.back_button);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InstagramLogin.this.finish();
            }
        });

        new InstaLogin((WebView) findViewById(R.id.login_webview)){
            @Override
            public void onLogin(){
                try{
                    MainActivity.Activity.on_login();
                }
                catch (Exception ex){}
            }
        }.login();
    }
}
