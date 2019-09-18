package com.svs.svs;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ActivityImage extends AppCompatActivity {

    private static final int CONTACT_SELECTION_PERMISSION = 1;
    private static final int IMAGE_SELECTION_PERMISSION = 2;
    private String contactNumber="", contactName="";
    private Bitmap selectedImage = null ;

    private Button btnBrowse, btnImage, btnSend, btnCapture;
    private EditText inputContact;
    private ImageView img;
    private File picPath = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_image);
        img = (ImageView) findViewById(R.id.imageView);
        inputContact = (EditText) findViewById(R.id.inputContact);
        btnBrowse = (Button) findViewById(R.id.btnBrowse);
        btnBrowse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(Intent.ACTION_PICK);
                i.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                startActivityForResult(i, CONTACT_SELECTION_PERMISSION);
            }
        });

        btnImage = (Button) findViewById(R.id.btnImage);
        btnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.setType("image/*");
                i.addCategory(Intent.CATEGORY_OPENABLE);
                //  chooseFile.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                if (i.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(i, IMAGE_SELECTION_PERMISSION);
                }
            }
        });

        btnSend = (Button) findViewById(R.id.btnSend);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(contactName == "" || contactNumber == ""){
                    Toast.makeText(getBaseContext(), "Please select contact before sending a message.", Toast.LENGTH_LONG).show();
                    return;
                }
                if(selectedImage == null){
                    Toast.makeText(getBaseContext(), "There's nothing to send, please select an image.", Toast.LENGTH_LONG).show();
                    return;
                }

                SMSHandler.INSTANCE.newMessage(ActivityImage.this, SMSHandler.SMS_TYPE_IMAGE, selectedImage, contactNumber);
                SMSHandler.INSTANCE.send();
            }
        });

        btnCapture = (Button) findViewById(R.id.btnCapture);
        //handle button clicks
        btnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    //use standard intent to capture an image

                    Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");

                    File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), "SPS/Temp/");
                    if (!mediaStorageDir.exists()) {
                        mediaStorageDir.mkdirs();
                    }
                    String path = mediaStorageDir.getAbsolutePath(); //access specific directory, path to our directory
                    String date = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
                    String file_name = path +"IMG_"+ date + ".jpg";
                    picPath = new File(file_name);

                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(picPath));
                    startActivityForResult(intent, 111);

                    //Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    //we will handle the returned data in onActivityResult
                    //startActivityForResult(captureIntent, CAMERA_CAPTURE);
                } catch (ActivityNotFoundException anfe) {
                    //display an error message
                    String errorMessage = "Whoops - your device doesn't support capturing images!";
                    Toast toast = Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK){
            if (reqCode == 111) {
                try {
                    cropCapturedImage(Uri.fromFile(picPath));
                } catch (ActivityNotFoundException aNFE) {
                    //display an error message if user device doesn't support
                    String errorMessage = "Sorry - your device doesn't support the crop action!";
                    Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
                    toast.show();
                }
            }

            if (reqCode == 222) {
                //Create an instance of bundle and get the returned data
                Bundle extras = data.getExtras();
                //get the cropped bitmap from extras
                Bitmap thePic = extras.getParcelable("data");
                //set image bitmap to image view
                img.setImageBitmap(thePic);

                if (picPath.exists()) {
                    picPath.delete();
                }
                try {
                    FileOutputStream out = new FileOutputStream(picPath);
                    thePic.compress(Bitmap.CompressFormat.JPEG, 90, out);
                    out.flush();
                    out.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else if(reqCode == CONTACT_SELECTION_PERMISSION){
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
            else if(reqCode == IMAGE_SELECTION_PERMISSION){
                try {
                    final Uri imageUri = data.getData();
                    final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                    selectedImage = BitmapFactory.decodeStream(imageStream);
                    Base64Converter base = new Base64Converter();
                    GZIP gz = new GZIP();
                    String enc = base.encode(selectedImage);

                    byte[] bytes = new byte[0];
                    try {
                        bytes= gz.compress(enc);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    String dec="";
                    try {
                        dec = gz.decompress(bytes);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(this, "Decoding done", Toast.LENGTH_SHORT).show();
                    Bitmap bb = base.decode(dec);
                    img.setImageBitmap(bb);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    //   Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
    public void cropCapturedImage(Uri picUri){
        //call the standard crop action intent
        Intent cropIntent = new Intent("com.android.camera.action.CROP");
        //indicate image type and Uri of image
        cropIntent.setDataAndType(picUri, "image/*");
        //set crop properties
        cropIntent.putExtra("crop", "true");
        //indicate aspect of desired crop
        cropIntent.putExtra("aspectX", 1);
        cropIntent.putExtra("aspectY", 1);
        //indicate output X and Y
        cropIntent.putExtra("outputX", 256);
        cropIntent.putExtra("outputY", 256);
        //retrieve data on return
        cropIntent.putExtra("return-data", true);
        //start the activity - we handle returning in onActivityResult
        startActivityForResult(cropIntent, 222);
    }
}
