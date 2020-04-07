package com.streetxportrait.android.planrr;

import android.graphics.Bitmap;
import android.net.Uri;

public class Photo {

    private Uri uri;
    private Bitmap bitmap;

    public Photo(Uri uri) {
        this.uri = uri;
    }

    public Uri getUri() {
        return uri;
    }
}
