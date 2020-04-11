package com.streetxportrait.android.planrr;

import android.graphics.Bitmap;
import android.net.Uri;

import java.io.Serializable;

public class Photo implements Serializable {

    private String uri;
    private Bitmap bitmap;

    public Photo(Uri uri) {
        this.uri = uri.toString();
    }

    public void setUri(Uri uri) {
        this.uri = uri.toString();
    }

    public String getUri() {
        return uri;
    }
}
