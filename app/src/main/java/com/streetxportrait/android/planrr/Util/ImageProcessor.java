package com.streetxportrait.android.planrr.Util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.palette.graphics.Palette;

import com.streetxportrait.android.planrr.Model.Post;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class ImageProcessor extends Post {

    private static final String TAG = "Photo";
    private static final int LONG_EDGE_SIZE = 1080;
    private Bitmap bitmap;
    private String uri;

    public ImageProcessor(Uri uri) {
        super(uri);
        this.uri = uri.toString();

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
//            Log.d(TAG, "getScaledBitmap: " + aspectRatio);
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
    public Bitmap getBitmapWithBorder(int borderColor) {

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
        canvas.drawColor(borderColor);

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

//        Log.d(TAG, "getBitmapWithBorder: left: " + left);
//        Log.d(TAG, "getBitmapWithBorder: top: " + top);
//
//        Log.d(TAG, "getBitmapWithBorder: cW : " + canvas.getWidth());
//        Log.d(TAG, "getBitmapWithBorder: sW : " + src.getWidth());
//
//        Log.d(TAG, "getBitmapWithBorder: cH: " + canvas.getHeight());
//        Log.d(TAG, "getBitmapWithBorder: sH: " + src.getHeight());

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawBitmap(src, left, top, paint);

        return borderedBitmap;

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void saveBitmap(Bitmap bitmap, Context context) {

        Uri original = getParsedUri();

        String outputFileName = FilenameUtils.getBaseName(original.getLastPathSegment()) + "-bordered.jpg";
//        Log.d(TAG, "saveBitmap: " + original.getLastPathSegment());

        String path = Environment.getExternalStorageDirectory().toString() + "/Pictures/Planrr/" + outputFileName;

//        Log.d(TAG, "saveBitmap: " + path);
        File imageFile = new File(path);
        File parentFile = imageFile.getParentFile();
        assert parentFile != null;
        if (!parentFile.exists()) {
            parentFile.mkdir();
        }

        OutputStream out;
        try {
            out = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            Toast.makeText(context, "Photo saved!", Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            Toast.makeText(context, "Save unsuccessful, check storage permission", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "saveBitmap: " + e);
            e.printStackTrace();
        }
    }


    public Map<String, Integer> getSwatches() {


        Palette p = Palette.from(bitmap).generate();

        Map<String, Integer> swatchMap = new HashMap<String, Integer>();


        Palette.Swatch dominantSwatch = p.getDominantSwatch();
        Palette.Swatch mutedSwatch = p.getMutedSwatch();
        Palette.Swatch vibrantSwatch = p.getVibrantSwatch();

        if (dominantSwatch != null) {
            swatchMap.put("dominant", dominantSwatch.getRgb());
        }
        else {
            swatchMap.put("dominant", null);

        }

        if (mutedSwatch != null) {
            swatchMap.put("muted", mutedSwatch.getRgb());
        }
        else {
            swatchMap.put("muted", null);
        }
        if (vibrantSwatch != null) {
            swatchMap.put("vibrant", vibrantSwatch.getRgb());
        }
        else {
            swatchMap.put("vibrant", null);
        }

        return swatchMap;
    }

}
