package com.streetxportrait.android.planrr;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import androidx.annotation.RequiresApi;

import java.io.IOException;
import java.io.Serializable;

public class Post implements Serializable {

    private String uri;
    private Bitmap bitmap;


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

        if (!(bitmap.getWidth() > 1080 || bitmap.getHeight() > 1080)) {
            if (bitmap.getWidth() >= bitmap.getHeight()) {
                int width = 1080;
                int height = Math.round(width / aspectRatio);
                scaledBitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
            } else {
                int height = 1080;
                int width = Math.round(height / aspectRatio);
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
        int canvasCentreV = canvas.getHeight() /2;
        int canvasCentreH = canvas.getWidth() / 2;
        int topLeftCornerX = canvasCentreV - (src.getHeight()/2);
        int topLeftCornerY = canvasCentreH - (src.getWidth()/2);
        int right = topLeftCornerX + src.getWidth();
        int bottom = topLeftCornerY + src.getHeight();

        Rect finalRect = new Rect(topLeftCornerX, topLeftCornerY, right, bottom);

        canvas.drawBitmap(src, null, finalRect, null);

        return borderedBitmap;

    }

}
