package com.svs.svs;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

/**
 * Created by Muhammad on 18/12/2017.
 */

public class Base64Converter {

    public String encode(Bitmap bitmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.WEBP, 50, baos);
        byte[] imageBytes = baos.toByteArray();
        String imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return imageString;
    }

    public Bitmap decode(String imageString){
        byte[] imageBytes = Base64.decode(imageString, Base64.DEFAULT);
        Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        return decodedImage;
    }
}
