package com.svs.svs;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.IntDef;
import android.telephony.SmsManager;
import android.util.Base64;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Muhammad on 18/12/2017.
 */

public class SMSHandler {
    public static final int SMS_TYPE_STRING = 1;
    public static final int SMS_TYPE_IMAGE = 2;
    private String textMessage=null;
    private String contactNumber=null;
    private Bitmap imageMessage=null;
    public static SMSHandler INSTANCE = new SMSHandler();
    private int SMS_TYPE;
    private Activity activity;
    private SmsManager sms;
    private ArrayList<String> messageParts;
    private String SMS_SENT = "SMS_SENT";
    private String SMS_DELIVERED = "SMS_DELIVERED";

//    @Retention(RetentionPolicy.SOURCE)
//    @IntDef({SMS_TYPE_STRING, SMS_TYPE_IMAGE})
//    public @interface Type{}

    public void newMessage(Activity activity, int smsType, String textMessage, String contactNumber){
        this.activity = activity;
        this.SMS_TYPE = smsType;
        this.textMessage = textMessage;
        this.contactNumber = contactNumber;
    }

    public void newMessage(Activity activity, int smsType, Bitmap imageMessage, String contactNumber){
        this.activity = activity;
        this.SMS_TYPE = smsType;
        this.imageMessage = imageMessage;
        this.contactNumber = contactNumber;
    }

    public String getMessage(){
        return "Type: "+SMS_TYPE+", Message: "+textMessage;
    }

    public void send(){
        switch (SMS_TYPE){
            case SMS_TYPE_STRING:
                if(textMessage.isEmpty() || contactNumber.isEmpty()){
                    return;
                }
                else{
                    sms = SmsManager.getDefault();
                    messageParts = sms.divideMessage(textMessage);
                    int msgCount = messageParts.size();

                    ArrayList<PendingIntent> sentPendings = new ArrayList<PendingIntent>(msgCount);
                    ArrayList<PendingIntent> deliveredPendings = new ArrayList<PendingIntent>(msgCount);

                    for(int i = 0 ; i <msgCount ; i++){
                        Intent smsSent = new Intent(SMS_SENT);
                        smsSent.putExtra("total", msgCount);
                        smsSent.putExtra("current", i+1);
                        PendingIntent sentPendingIntent = PendingIntent.getBroadcast(activity, 0, smsSent, PendingIntent.FLAG_UPDATE_CURRENT);
                        activity.registerReceiver(new BroadcastReceiver() {
                            @Override
                            public void onReceive(Context context, Intent intent) {
                                switch (getResultCode()) {
                                    case Activity.RESULT_OK:
                                        Toast.makeText(context, "Message sent successfully", Toast.LENGTH_SHORT).show();
                                        break;
                                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                                        Toast.makeText(context, "Generic failure cause", Toast.LENGTH_SHORT).show();
                                        break;
                                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                                        Toast.makeText(context, "Service is currently unavailable", Toast.LENGTH_SHORT).show();
                                        break;
                                    case SmsManager.RESULT_ERROR_NULL_PDU:
                                        Toast.makeText(context, "No pdu provided", Toast.LENGTH_SHORT).show();
                                        break;
                                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                                        Toast.makeText(context, "Radio was explicitly turned off", Toast.LENGTH_SHORT).show();
                                        break;
                                }
                            }
                        }, new IntentFilter(SMS_SENT));

                        Intent smsDeliveredIntent = new Intent(SMS_DELIVERED);
                        smsSent.putExtra("total", msgCount);
                        smsSent.putExtra("current", i+1);
                        PendingIntent deliveredPendingIntent = PendingIntent.getBroadcast(activity, 0, smsDeliveredIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                        activity.registerReceiver(new BroadcastReceiver() {
                            @Override
                            public void onReceive(Context context, Intent intent) {
                                int total = intent.getExtras().getInt("total");
                                int current = intent.getExtras().getInt("current");
                                switch (getResultCode()) {
                                    case Activity.RESULT_OK:
                                        Toast.makeText(context, "SMS delivered", Toast.LENGTH_SHORT).show();
                                        break;
                                    case Activity.RESULT_CANCELED:
                                        Toast.makeText(context, "SMS not delivered", Toast.LENGTH_SHORT).show();
                                        break;
                                }
                            }
                        }, new IntentFilter(SMS_DELIVERED));

                        sentPendings.add(sentPendingIntent);
                        deliveredPendings.add(deliveredPendingIntent);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // Do something after 5s = 5000ms
                            }
                        }, 5000);
                    }
                    Toast.makeText(activity, messageParts.size()+"", Toast.LENGTH_SHORT).show();
                    //sms.sendTextMessage(contactNumber, null, messageParts.size()+"", null, null);
                    sms.sendMultipartTextMessage(contactNumber, null, messageParts, sentPendings, deliveredPendings);
                    //sms.sendTextMessage(contactNumber, null, textMessage, sentPendingIntent, deliveredPendingIntent);
                }
                break;

            case SMS_TYPE_IMAGE:

                String encStr = "SPS,"+new Base64Converter().encode(imageMessage);
                sms = SmsManager.getDefault();
                messageParts = sms.divideMessage(encStr);
                int msgCount = messageParts.size();

                ArrayList<PendingIntent> sentPendings = new ArrayList<PendingIntent>(msgCount);
                ArrayList<PendingIntent> deliveredPendings = new ArrayList<PendingIntent>(msgCount);

                for(int i = 0 ; i <msgCount ; i++){
                    Intent smsSent = new Intent(SMS_SENT);
                    smsSent.putExtra("total", msgCount);
                    smsSent.putExtra("current", i+1);
                    PendingIntent sentPendingIntent = PendingIntent.getBroadcast(activity, 0, smsSent, PendingIntent.FLAG_UPDATE_CURRENT);
                    activity.registerReceiver(new BroadcastReceiver() {
                        @Override
                        public void onReceive(Context context, Intent intent) {
                            switch (getResultCode()) {
                                case Activity.RESULT_OK:
                                    Toast.makeText(context, "Message sent successfully", Toast.LENGTH_SHORT).show();
                                    break;
                                case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                                    Toast.makeText(context, "Generic failure cause", Toast.LENGTH_SHORT).show();
                                    break;
                                case SmsManager.RESULT_ERROR_NO_SERVICE:
                                    Toast.makeText(context, "Service is currently unavailable", Toast.LENGTH_SHORT).show();
                                    break;
                                case SmsManager.RESULT_ERROR_NULL_PDU:
                                    Toast.makeText(context, "No pdu provided", Toast.LENGTH_SHORT).show();
                                    break;
                                case SmsManager.RESULT_ERROR_RADIO_OFF:
                                    Toast.makeText(context, "Radio was explicitly turned off", Toast.LENGTH_SHORT).show();
                                    break;
                            }
                        }
                    }, new IntentFilter(SMS_SENT));

                    Intent smsDeliveredIntent = new Intent(SMS_DELIVERED);
                    smsSent.putExtra("total", msgCount);
                    smsSent.putExtra("current", i+1);
                    PendingIntent deliveredPendingIntent = PendingIntent.getBroadcast(activity, 0, smsDeliveredIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    activity.registerReceiver(new BroadcastReceiver() {
                        @Override
                        public void onReceive(Context context, Intent intent) {
                            int total = intent.getExtras().getInt("total");
                            int current = intent.getExtras().getInt("current");
                            switch (getResultCode()) {
                                case Activity.RESULT_OK:
                                    Toast.makeText(context, "SMS delivered", Toast.LENGTH_SHORT).show();
                                    break;
                                case Activity.RESULT_CANCELED:
                                    Toast.makeText(context, "SMS not delivered", Toast.LENGTH_SHORT).show();
                                    break;
                            }
                        }
                    }, new IntentFilter(SMS_DELIVERED));

                    sentPendings.add(sentPendingIntent);
                    deliveredPendings.add(deliveredPendingIntent);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Do something after 5s = 5000ms
                        }
                    }, 5000);
                }

//                Toast.makeText(activity, messageParts.size()+"", Toast.LENGTH_SHORT).show();
                sms.sendMultipartTextMessage(contactNumber, null, messageParts, sentPendings, deliveredPendings);
                break;
        }
    }

    public String manipulateImage(Bitmap b){
//        File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), "SPS/Sent");
//        if (!mediaStorageDir.exists()) {
//            mediaStorageDir.mkdirs();
//        }
//
//        String path = mediaStorageDir.getAbsolutePath(); //access specific directory, path to our directory
//        String date = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
//        String file_name = path +"IMG_"+ date + ".jpg";
//        File picpath = new File(file_name);
//        FileOutputStream fos = null;
//        try {
//            fos = new FileOutputStream(picpath);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//        b.compress(Bitmap.CompressFormat.WEBP, 100, fos);
//        try {
//            fos.flush();
//            fos.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        FileInputStream fis = null;
//        String encStr="";
//        try {
//            fis = new FileInputStream(picpath);
//            byte[] arrayOfByte = new byte[(int)picpath.length()];
//            fis.read(arrayOfByte);
//            encStr = Base64.encodeToString(arrayOfByte, Base64.DEFAULT);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        Base64Converter base = new Base64Converter();
        String encStr = base.encode(b);
        return encStr;
    }
}
