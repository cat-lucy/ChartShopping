package com.chart_shopping.www.chartshopping.processing;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.chart_shopping.www.chartshopping.R;
import com.chart_shopping.www.chartshopping.database.Database;
import com.chart_shopping.www.chartshopping.database.DatabaseContract;
import com.chart_shopping.www.chartshopping.database.Purchase;
import com.chart_shopping.www.chartshopping.MainActivity;
import com.chart_shopping.www.chartshopping.statistics.Rounder;

public class SmsService extends Service {

    static final String TITLE = "Новое приобретение";
    private Database database;

    public SmsService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String sms_body = intent.getStringExtra("sms_body");
        SmsParser sms_parser = new SmsParser(sms_body);
        Purchase purchase = sms_parser.parse();
        if (purchase != null && purchase.value < 0) {
            database = new Database(this);
            database.openWrite();
            database.addLine(DatabaseContract.TABLE_NAME, purchase);
            showNotification(purchase.organization + " : " + Rounder.fullRound(purchase.value));
        }
        return START_STICKY;
    }

    @SuppressWarnings("deprecation")
    public void showNotification(String text) {
        Intent main_intent = new Intent(this, MainActivity.class);
        main_intent.putExtra("sms_body", text);
        PendingIntent content_intent = PendingIntent.getActivity(this, 0, main_intent, 0);
        Context context = getApplicationContext();
        Notification.Builder builder = new Notification.Builder(context);
        builder.setContentTitle(TITLE);
        builder.setContentText(text);
        builder.setContentIntent(content_intent);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setAutoCancel(true);

        NotificationManager notification_manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = builder.getNotification();
        notification_manager.notify(R.mipmap.ic_launcher, notification);
    }
}
