package com.android.instaprofilegrabber.purchase;

import android.app.Activity;
import android.content.Intent;

import com.android.instaprofilegrabber.R;
import com.android.instaprofilegrabber.RUtil;

/**
 * Created by Ramin on 8/20/2017.
 */

public class PaymentHandler {
    private Activity context;
    private PaymentHandler paymentHandler;
    private IPaymentProvider paymentProvider;
    private String providerName;

    public PaymentHandler(Activity context){
        this.context = context;
        this.paymentHandler = this;
        this.providerName = context.getString(R.string.payment_provider);

        switch (this.providerName.toLowerCase()){
            case "bazaar":
                paymentProvider = new Bazaar(context){
                    @Override
                    public void onPurchaseSucceed(String purchaseId, int price){
                        paymentHandler.onPurchaseSucceed(purchaseId, price);
                    }

                    @Override
                    public void onPurchaseFailure(){
                        paymentHandler.onPurchaseFailure();
                    }
                };
                break;
            case "myket":
                paymentProvider = new Myket(context){
                    @Override
                    public void onPurchaseSucceed(String purchaseId, int price){
                        paymentHandler.onPurchaseSucceed(purchaseId, price);
                    }

                    @Override
                    public void onPurchaseFailure(){
                        paymentHandler.onPurchaseFailure();
                    }
                };
                break;
            case "iranapps":
                paymentProvider = new IranApps(context){
                    @Override
                    public void onPurchaseSucceed(String purchaseId, int price){
                        paymentHandler.onPurchaseSucceed(purchaseId, price);
                    }

                    @Override
                    public void onPurchaseFailure(){
                        paymentHandler.onPurchaseFailure();
                    }
                };
                break;
            case "zarinpal":
                paymentProvider = new ZarinPal(context){
                    @Override
                    public void onPurchaseSucceed(String purchaseId, int price){
                        paymentHandler.onPurchaseSucceed(purchaseId, price);
                    }

                    @Override
                    public void onPurchaseFailure(){
                        paymentHandler.onPurchaseFailure();
                    }
                };
                break;
        }
    }

    public void dispose(){
        if(paymentProvider != null) paymentProvider.dispose();
    }

    public void purchase(){
        if(paymentProvider != null) paymentProvider.purchase();
    }

    public void purchase_special(){
        if(paymentProvider != null) paymentProvider.purchase_special();
    }

    public void onPurchaseSucceed(String purchaseId, int price){
    }

    public void onPurchaseFailure(){
    }

    public int price_tomans(){
        return paymentProvider.price_tomans();
    }

    public int price_tomans_special(){
        return paymentProvider.price_tomans_special();
    }

    public boolean handleActivityResult(int requestCode, int resultCode, Intent data){
        return paymentProvider.handleActivityResult(requestCode, resultCode, data);
    }
}
