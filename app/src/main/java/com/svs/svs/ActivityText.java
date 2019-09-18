package com.svs.svs;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

public class ActivityText extends AppCompatActivity {


    private static final int CONTACT_SELECTION_PERMISSION = 1;
    private String contactNumber="", contactName="", textMessage=null;

    private Button btnBrowse, btnSend;
    private EditText inputContact, inputMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_text);
        inputContact = (EditText) findViewById(R.id.inputContact);
        inputMessage = (EditText) findViewById(R.id.inputMessage);
        btnBrowse = (Button) findViewById(R.id.btnBrowse);
        btnBrowse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(Intent.ACTION_PICK);
                i.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                startActivityForResult(i, CONTACT_SELECTION_PERMISSION);
            }
        });

        btnSend = (Button) findViewById(R.id.btnSend);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textMessage = inputMessage.getText().toString();
                if(contactName == "" || contactNumber == ""){
                    Toast.makeText(getBaseContext(), "Please select contact before sending a message.", Toast.LENGTH_LONG).show();
                    return;
                }
                if(textMessage.isEmpty()){
                    Toast.makeText(getBaseContext(), "There's nothing to send, please write something.", Toast.LENGTH_LONG).show();
                    return;
                }
                SMSHandler.INSTANCE.newMessage(ActivityText.this, SMSHandler.SMS_TYPE_STRING, textMessage, contactNumber);
                SMSHandler.INSTANCE.send();
            }
        });
    }

    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        if(reqCode == CONTACT_SELECTION_PERMISSION){
            if(resultCode == RESULT_OK){
                Cursor cursor = null;
                try {
                    Uri uri = data.getData();
                    cursor = getContentResolver().query(uri, null, null, null, null);
                    cursor.moveToFirst();
                    int  phoneIndex =cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                    int  nameIndex =cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                    contactNumber = cursor.getString(phoneIndex);
                    contactName = cursor.getString(nameIndex);
                    inputContact.setText(contactName);
                    Toast.makeText(this, "Name: "+contactName+"..Number: "+contactNumber, Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }finally{
                    cursor.close();
                }

            }
        }
    }
}
