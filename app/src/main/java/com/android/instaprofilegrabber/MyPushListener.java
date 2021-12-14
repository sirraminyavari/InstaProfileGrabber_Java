package com.android.instaprofilegrabber;

import org.json.JSONObject;

import co.ronash.pushe.PusheListenerService;

public class MyPushListener extends PusheListenerService {
    @Override
    public void onMessageReceived(JSONObject message, JSONObject content){
        //android.util.Log.i("Pushe","Custom json Message: "+ message.toString());
        // Your Code
    }
}
