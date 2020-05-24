package com.streetxportrait.android.planrr.Model;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

public class Post implements Serializable {

    private String uri;
    private final static String TAG = "Post";
    private boolean isSelected = false;
    private String postDate;
    private String postTime;
    private String postCaption;

    public String getPostDate() {
        return postDate;
    }

    public void setPostDate(String postDate) {
        this.postDate = postDate;
    }

    public String getPostTime() {
        return postTime;
    }

    public void setPostTime(String postTime) {
        this.postTime = postTime;
    }

    public String getPostCaption() {
        return postCaption;
    }

    public void setPostCaption(String postCaption) {
        this.postCaption = postCaption;
    }

    public Post(Uri uri) {
        this.uri = uri.toString();
    }

    public String getUri() {
        return uri;
    }


    public Uri getParsedUri() {
        return Uri.parse(uri);
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean getIsSelected() {
        return isSelected;
    }

    public boolean checkIsImage(Context context, Uri uri) throws IOException {

        ContentResolver contentResolver = context.getContentResolver();
        String type = contentResolver.getType(uri);
        if (type != null) {
            return  type.startsWith("image/");
        } else {
            // try to decode as image (bounds only)3

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

}
