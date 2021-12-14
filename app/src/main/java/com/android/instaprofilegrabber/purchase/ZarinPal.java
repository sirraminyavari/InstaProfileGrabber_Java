package com.android.instaprofilegrabber.purchase;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.widget.TextView;

import com.android.instaprofilegrabber.RUtil;
import com.zarinpal.ewallets.purchase.OnCallbackRequestPaymentListener;
import com.zarinpal.ewallets.purchase.OnCallbackVerificationPaymentListener;
import com.zarinpal.ewallets.purchase.PaymentRequest;

/**
 * Created by Ramin on 8/19/2017.
 */

public class ZarinPal extends IPaymentProvider {
    private int price;

    public ZarinPal(Activity context) {
        super(context);

        com.zarinpal.ewallets.purchase.ZarinPal.getPurchase(getContext())
                .verificationPayment(getContext().getIntent().getData(), new OnCallbackVerificationPaymentListener() {
                    @Override
                    public void onCallbackResultVerificationPayment(boolean isPaymentSuccess, String refID, PaymentRequest paymentRequest) {
                        if (isPaymentSuccess) onPurchaseSucceed(refID, price);
                        else onPurchaseFailure();
                    }
                });
    }

    @Override
    public void dispose(){
        super.dispose();
    }

    @Override
    public void purchase(){
        payment((price = price_tomans()), "ارتقا به نسخه حرفه ای");
    }

    @Override
    public void purchase_special(){
        payment((price = price_tomans_special()), "حمایت و ارتقا به نسخه حرفه ای");
    }

    public int price_tomans(){
        return 1900;
    }

    public int price_tomans_special(){
        return 3900;
    }

    private void payment(long amountTomans, String title) {
        com.zarinpal.ewallets.purchase.ZarinPal purchase = com.zarinpal.ewallets.purchase.ZarinPal.getPurchase(getContext());
        PaymentRequest payment = com.zarinpal.ewallets.purchase.ZarinPal.getPaymentRequest();

        payment.setMerchantID("fbd4267e-8260-11e7-bdf8-005056a205be");
        payment.setAmount(amountTomans);
        payment.setDescription(title);
        payment.setCallbackURL("ipg://zarinpalpayment");

        purchase.startPayment(payment, new OnCallbackRequestPaymentListener() {
            @Override
            public void onCallbackResultPaymentRequest(int status, String authority, Uri paymentGatewayUri, Intent intent) {
                if (status == 100) getContext().startActivity(intent);
                else onPurchaseFailure();
            }
        });
    }
}
