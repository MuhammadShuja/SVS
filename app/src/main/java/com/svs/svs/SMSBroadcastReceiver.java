package com.svs.svs;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.RequiresApi;
import android.telephony.SmsMessage;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Muhammad on 19/12/2017.
 */

public class SMSBroadcastReceiver extends BroadcastReceiver {

    public static final String SMS_BUNDLE = "pdus";

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onReceive(Context context, Intent intent) {
        Map<String, MessageModel> msgs = RetrieveMessages(context, intent);

        for(MessageModel obj : msgs.values()){
            MainActivity inst = MainActivity.instance();
            inst.updateInbox(obj);
        }

//        Bundle intentExtras = intent.getExtras();
//        if (intentExtras != null) {
//            Object[] sms = (Object[]) intentExtras.get(SMS_BUNDLE);
//
//            for (int i = 0; i < sms.length; ++i) {
//                String format = intentExtras.getString("format");
//                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) sms[i], format);
//
//                String message = smsMessage.getMessageBody().toString();
//                String sender = getContactName(context, smsMessage.getOriginatingAddress());
//                Long timestamp = smsMessage.getTimestampMillis();
//                Calendar calendar = Calendar.getInstance();
//                calendar.setTimeInMillis(timestamp);
//                Date finaldate = calendar.getTime();
//                String time = finaldate.toString();
//                MessageModel obj;
//                if(message.startsWith("SPS")){
//                    obj = new MessageModel(sender, message, time, true);
//                }else{
//                    obj = new MessageModel(sender, message, time, false);
//                }
//                MainActivity inst = MainActivity.instance();
//                inst.updateInbox(obj);
////                Notification notification = new Notification.Builder(context)
////                        .setContentTitle("Smart Photo Sharing")
////                        .setContentText(sender+": "+message)
////                        .setSmallIcon(R.drawable.notify)
////                        .build();
////                notification.flags |= Notification.FLAG_AUTO_CANCEL | Notification.FLAG_ONGOING_EVENT;
////                NotificationManager notifier = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
////                notifier.notify(81237, notification);
//            }
//        }
    }

    private static Map<String, MessageModel> RetrieveMessages(Context c, Intent intent) {
        Map<String, MessageModel> msg = null;
        SmsMessage[] msgs = null;
        Bundle bundle = intent.getExtras();
        MessageModel model;

        if (bundle != null && bundle.containsKey("pdus")) {
            Object[] pdus = (Object[]) bundle.get("pdus");

            if (pdus != null) {
                int nbrOfpdus = pdus.length;
                msg = new HashMap<String, MessageModel>(nbrOfpdus);
                msgs = new SmsMessage[nbrOfpdus];

                // There can be multiple SMS from multiple senders, there can be a maximum of nbrOfpdus different senders
                // However, send long SMS of same sender in one message
                for (int i = 0; i < nbrOfpdus; i++) {
                    msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);

                    String originatinAddress = msgs[i].getOriginatingAddress();

                    // Check if index with number exists
                    if (!msg.containsKey(originatinAddress)) {
                        String sender = getContactName(c, msgs[i].getOriginatingAddress());
                        String message = msgs[i].getMessageBody();

                        Long timestamp = msgs[i].getTimestampMillis();

                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(timestamp);
                        Date finaldate = calendar.getTime();
                        String time = finaldate.toString();

                        if(message.startsWith("SPS")){
                            model = new MessageModel(sender, message, time, true);
                        }else{
                            model = new MessageModel(sender, message, time, false);
                        }

                        msg.put(msgs[i].getOriginatingAddress(), model);

                    } else {
                        // Number has been there, add content but consider that
                        // msg.get(originatinAddress) already contains sms:sndrNbr:previousparts of SMS,
                        // so just add the part of the current PDU
                        model = msg.get(msgs[i].getOriginatingAddress());
                        String previousparts = model.getMessage();
                        String msgString = previousparts + msgs[i].getMessageBody();
                        model.setMessage(msgString);
                        msg.put(originatinAddress, model);
                    }
                }
            }
        }

        return msg;
    }

    public static String getContactName(Context context, String phoneNo) {
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNo));
        Cursor cursor = cr.query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
        if (cursor == null) {
            return phoneNo;
        }
        String Name = phoneNo;
        if (cursor.moveToFirst()) {
            Name = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
        }

        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        return Name;
    }
}