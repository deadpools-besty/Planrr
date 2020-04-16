package com.streetxportrait.android.planrr.Model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.IOException;
import java.io.Serializable;

public class Post implements Serializable {

    private String uri;
    private Bitmap bitmap;
    private final static String TAG = "Post";


    public Post(Uri uri) {
        this.uri = uri.toString();
    }

    public void setUri(Uri uri) {
        this.uri = uri.toString();
    }

    public String getUri() {
        return uri;
    }

    public Bitmap createBitmap(Context context) throws IOException {

        bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), Uri.parse(uri));

        return bitmap;
    }

    public Bitmap getScaledBitmap() {
        Bitmap scaledBitmap;
        float aspectRatio = bitmap.getWidth() / (float) bitmap.getHeight();

        if (bitmap.getWidth() > 1080 || bitmap.getHeight() > 1080) {
            Log.d(TAG, "getScaledBitmap: " + aspectRatio);
            if (aspectRatio > 1) {
                int width = 1080;
                int height = Math.round(width / aspectRatio);
                scaledBitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
            } else {
                Log.d(TAG, "getScaledBitmap: hello");
                int height = 1080;
                int width = Math.round(height * aspectRatio);
                scaledBitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
            }
            return scaledBitmap;
        }

        else {
            return bitmap;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Bitmap getBitmapWithBorder() {

        Bitmap borderedBitmap = Bitmap.createBitmap(1080, 1080, Bitmap.Config.RGBA_F16);
        Bitmap src = getScaledBitmap();
        Canvas canvas = new Canvas(borderedBitmap);
        canvas.drawColor(Color.WHITE);

        // centering bitmap in canvas
        float left;
        float top;
        if (src.getWidth() > src.getHeight()) {
            left = 0;
            top = (canvas.getHeight() / (float) 2) - (src.getHeight() / (float) 2);
        }
        else {
            left = (canvas.getWidth() / (float) 2) - (src.getWidth() / (float) 2);
            top = 0;
        }
        Log.d(TAG, "getBitmapWithBorder: left: " + left);
        Log.d(TAG, "getBitmapWithBorder: top: " + top);

        Log.d(TAG, "getBitmapWithBorder: cW : " + canvas.getWidth());
        Log.d(TAG, "getBitmapWithBorder: sW : " + src.getWidth());

        Log.d(TAG, "getBitmapWithBorder: cH: " + canvas.getHeight());
        Log.d(TAG, "getBitmapWithBorder: sH:  " + src.getHeight());

        canvas.drawBitmap(src, left, top, null);


        return borderedBitmap;

    }

}
