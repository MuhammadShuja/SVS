package com.svs.svs;

/**
 * Created by Muhammad on 19/12/2017.
 */

public class MessageModel {
    private String sender;
    private String message;
    private String time;
    private boolean isImageAvailable;

    public MessageModel(String sender, String message, String time, boolean imgAvail) {
        this.sender = sender;
        this.time = time;
        this.message = message;
        this.isImageAvailable = imgAvail;
    }

    public void setMessage(String s){this.message = s;}

    public String getSender(){ return this.sender; }
    public String getMessage(){ return this.message; }
    public String getTime(){ return this.time; }
    public boolean isImageAvailable(){return this.isImageAvailable;}
}
