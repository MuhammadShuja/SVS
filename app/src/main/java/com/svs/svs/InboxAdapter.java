package com.svs.svs;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Muhammad on 19/12/2017.
 */

public class InboxAdapter extends ArrayAdapter<MessageModel> implements View.OnClickListener{

    private ArrayList<MessageModel> msgList;
    Context mContext;
    private int lastPosition = -1;
    private int messageLayout;

    MessageModel item;

    public InboxAdapter(ArrayList<MessageModel> data, Context context) {
        super(context, R.layout.layout_inbox_item, data);
        this.msgList = data;
        this.mContext=context;
        this.messageLayout = R.layout.layout_inbox_item;
    }

    @Override
    public void onClick(View v) {
        int position=(Integer) v.getTag();
        Object object= getItem(position);
        MessageModel msgModel=(MessageModel)object;

        switch (v.getId())
        {
            case 1:
//                Snackbar.make(v, "Release date " +dataModel.getFeature(), Snackbar.LENGTH_LONG)
//                        .setAction("No action", null).show();
            break;
        }
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final MessageModel msgModel = getItem(position);
        ViewHolder viewHolder;

        final View resultView;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(messageLayout, parent, false);
            viewHolder.txtFrom = (TextView) convertView.findViewById(R.id.sender);
            viewHolder.txtTime = (TextView) convertView.findViewById(R.id.time);
            viewHolder.txtMessage = (TextView) convertView.findViewById(R.id.message);
            viewHolder.profileImage = (ImageView) convertView.findViewById(R.id.profileImage);
            resultView=convertView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            resultView=convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        resultView.startAnimation(animation);
        lastPosition = position;

        viewHolder.txtFrom.setText(msgModel.getSender());
        viewHolder.txtTime.setText(msgModel.getTime());
        if(msgModel.isImageAvailable()){
            viewHolder.txtMessage.setText("This message contains an image!");
        }else{
            viewHolder.txtMessage.setText(msgModel.getMessage());
        }
        viewHolder.txtMessage.setTag(position);

        ColorGenerator generator = ColorGenerator.MATERIAL;
        int color = generator.getRandomColor();

        TextDrawable drawable = TextDrawable.builder()
                .buildRound(msgModel.getSender().substring(0,1), color);

        viewHolder.profileImage.setImageDrawable(drawable);


        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(mContext,
//                        "Sender: "+msgModel.getSender()+", Message: "+msgModel.getMessage()+", Time: "+msgModel.getTime(),
//                        Toast.LENGTH_LONG).show();
                Intent it = new Intent(mContext, ActivityConversation.class);
                it.putExtra("from", msgModel.getSender());
                it.putExtra("message", msgModel.getMessage());
                it.putExtra("time", msgModel.getTime());
                it.putExtra("image", msgModel.isImageAvailable());
                mContext.startActivity(it);
            }
        });
        // Return the completed view to render on screen
        return convertView;
    }

    public static boolean checkForImage(String message){
        return true;
    }

    private static class ViewHolder {
        TextView txtFrom;
        TextView txtMessage;
        TextView txtTime;
        ImageView profileImage;
    }
}
