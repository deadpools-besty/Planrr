package com.streetxportrait.android.planrr.Model;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.IOException;
import java.io.InputStream;
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

    public boolean checkIsImage(Context context, Uri uri) throws IOException {
        ContentResolver contentResolver = context.getContentResolver();
        String type = contentResolver.getType(uri);
        if (type != null) {
            return  type.startsWith("image/");
        } else {
            // try to decode as image (bounds only)
            InputStream inputStream = null;
            try {
                inputStream = contentResolver.openInputStream(uri);
                if (inputStream != null) {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeStream(inputStream, null, options);
                    return options.outWidth > 0 && options.outHeight > 0;
                }
            } catch (IOException e) {
                // ignore
            } finally {
                inputStream.close();
            }
        }
        // default outcome if image not confirmed
        return false;
    }

    public Bitmap createBitmap(Context context) throws IOException {

        bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), Uri.parse(uri));

        return bitmap;
    }

    /**
     * scale bitmap down to 1080 pixels on its longest edge
     * @return bitmap that has been scaled down
     */
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

    /**
     * get bitmap with border around it
     * @return return bitmap with border around it
     */
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
