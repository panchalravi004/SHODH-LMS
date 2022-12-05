package com.shodh.lms;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Base64;

public class ImageConvert {

    public static String getBitmapString(Bitmap bitmap) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        byte[] imageStyle = bos.toByteArray();
        String encode = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            encode = Base64.getEncoder().encodeToString(imageStyle);
        }
        return encode;
    }
    public static Bitmap getStringBitmap(String image) {

//        Log.i("LMS_TEST", "getStringBitmap: "+image);

        byte [] encodeByte= new byte[0];
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            encodeByte = Base64.getDecoder().decode(image.trim());
        }
        InputStream inputStream  = new ByteArrayInputStream(encodeByte);
        Bitmap bitmap  = BitmapFactory.decodeStream(inputStream);
        return bitmap;
    }
}
