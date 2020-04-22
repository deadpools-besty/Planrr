package com.streetxportrait.android.planrr.Model;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;

public class Post implements Serializable {

    private String uri;
    private Bitmap bitmap;
    private final static String TAG = "Post";
    private String uriFilename;
    private BitmapFactory.Options options;
    private static final int LONG_EDGE_SIZE = 1080;



    public Post(Uri uri) {
        this.uri = uri.toString();
    }

    public String getUri() {
        return uri;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String getFilename(Context context) {

        String result = null;

        getParsedUri().getPath();
        Cursor returnCursor = context.getContentResolver().query(getParsedUri(), null, null, null);

        try {
            if (returnCursor != null && returnCursor.moveToFirst()) {
                result = returnCursor.getString(returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
            }
        } finally {
            if (returnCursor != null)
            returnCursor.close();
        }

        if (result == null) {
            result = getParsedUri().getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut+1);
            }
        }
        uriFilename = result;
        return result;
    }

    public Uri getParsedUri() {
        return Uri.parse(uri);
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

        /*options = new BitmapFactory.Options();

        options.inJustDecodeBounds = false;
        options.inScaled = false;
        options.inDither = false;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        bitmap = BitmapFactory.decodeFile(getParsedUri().getPath(), options);
        Log.d(TAG, "createBitmap: " + getParsedUri().getPath());*/
        bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), Uri.parse(uri));

        return bitmap;
    }

    /**
     * scale bitmap down to 1080 pixels on its longest edge
     * @return bitmap that has been scaled down
     */
    public Bitmap getScaledBitmap(Bitmap bitmapToScale) {


        Bitmap scaledBitmap;

        int srcWidth = bitmapToScale.getWidth();
        int srcHeight = bitmapToScale.getHeight();
        float aspectRatio = srcWidth/ (float) srcHeight;

        if (srcWidth > LONG_EDGE_SIZE || srcHeight > LONG_EDGE_SIZE) {
            Log.d(TAG, "getScaledBitmap: " + aspectRatio);
            int fHeight;
            int fWidth;

            if (aspectRatio > 1) {
                fWidth = LONG_EDGE_SIZE;
                fHeight = Math.round(fWidth / aspectRatio);

            } else {
                fHeight = LONG_EDGE_SIZE;
                fWidth = Math.round(fHeight * aspectRatio);
            }


            scaledBitmap = Bitmap.createScaledBitmap(bitmapToScale, fWidth, fHeight, true);
            return scaledBitmap;
        }

        else {
            return bitmapToScale;
        }
    }

    /**
     * get bitmap with border around it
     * @return return bitmap with border around it
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public Bitmap getBitmapWithBorder() {

        Bitmap src = getScaledBitmap(bitmap);
        int srcLongEdge;
        int srcWidth = src.getWidth();
        int srcHeight = src.getHeight();

        if (srcWidth > srcHeight) {
            srcLongEdge = srcWidth;
        }
        else {
            srcLongEdge = srcHeight;
        }

        Bitmap borderedBitmap = Bitmap.createBitmap(srcLongEdge, srcLongEdge, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(borderedBitmap);
        canvas.drawColor(Color.WHITE);

        // centering bitmap in canvas
        float left;
        float top;
        if (srcWidth > srcHeight) {
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
        Log.d(TAG, "getBitmapWithBorder: sH: " + src.getHeight());

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawBitmap(src, left, top, paint);

        return borderedBitmap;

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void saveBitmap(Bitmap bitmap, Context context) {

        getFilename(context);
        String outputFileName = uriFilename;

        String path = Environment.getExternalStorageDirectory().toString() + "/Pictures/Planrr/" + outputFileName;

        Log.d(TAG, "saveBitmap: " + path);
        File imageFile = new File(path);
        if (!imageFile.getParentFile().exists()) {
            imageFile.getParentFile().mkdir();
        }

        OutputStream out;
        try {
            out = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (IOException e) {
            Log.d(TAG, "saveBitmap: " + e);
            e.printStackTrace();
        }
    }

}
