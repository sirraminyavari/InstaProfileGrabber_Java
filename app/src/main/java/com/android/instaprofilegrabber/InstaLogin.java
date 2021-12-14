package com.android.instaprofilegrabber;

import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpCookie;
import java.net.URI;

/**
 * Created by Ramin on 1/27/2017.
 */

public class InstaLogin {
    private InstagramLogin Context;
    private boolean LoggedOutInsta;
    private boolean LoggedOutMySocioMe;
    private WebView view;

    public InstaLogin(WebView view) {
        Context = InstagramLogin.Activity;
        LoggedOutInsta = LoggedOutMySocioMe = false;
        this.view = view;
    }

    public void onLogin(){}

    public void login() {
        try {
            CookieManager cookieManager = new CookieManager();
            cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
            CookieHandler.setDefault(cookieManager);

            HttpCookie cookie = new HttpCookie("instagram.com", "sessionid=''");
            cookie.setDomain("instagram.com");
            cookie.setPath("/");
            cookie.setVersion(0);
            cookie.setValue("sessionid=''");
            cookieManager.getCookieStore().add(new URI("https://instagram.com/"), cookie);

            cookieManager.getCookieStore().removeAll();
        } catch (Exception ex) {
        }

        final WebView webView = view;
        webView.setVerticalScrollBarEnabled(false);
        webView.setHorizontalScrollBarEnabled(false);

        final ProgressBar progressBar = (ProgressBar) Context.findViewById(R.id.progress_bar);

        webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                progressBar.setProgress(progress);
                if (progress == 100) progressBar.setVisibility(View.GONE);
                else progressBar.setVisibility(View.VISIBLE);
            }
        });

        final String redirectUri = "redirect_uri=http://gesichaghochi.com";

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith(Context.getString(R.string.insta_logout_url)) ||
                        url.startsWith(Context.getString(R.string.mysociome_logout_url))) {
                    return false;
                }
                else if (!LoggedOutInsta) {
                    LoggedOutInsta = true;
                    view.loadUrl(Context.getString(R.string.mysociome_logout_url));
                    return true;
                } else if (!LoggedOutMySocioMe) {
                    LoggedOutMySocioMe = true;
                    view.loadUrl(Context.getString(R.string.mysociome_login_url) + "&" + redirectUri);
                    return true;
                } else if (url.indexOf("token=") > 0) {
                    String token = url.split("=")[1];

                    RUtil.set_token(Context, token);

                    onLogin();

                    Context.finish();
                    return true;
                } else if (url.indexOf("code=") > 0) {
                    String agentId = Context.getString(R.string.agent_id);

                    view.loadUrl(url + "&" + redirectUri + (agentId.isEmpty() ? "" : "&invitedby=" + agentId));
                    return true;
                }

                return false;
            }
        });

        android.webkit.CookieManager.getInstance().removeAllCookie();
        android.webkit.CookieManager.getInstance().removeSessionCookie();

        webView.getSettings().setJavaScriptEnabled(true);
        //webView.loadUrl(Context.getString(R.string.insta_logout_url));
        webView.loadUrl(Context.getString(R.string.mysociome_logout_url));
    }
}