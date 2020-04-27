package com.streetxportrait.android.planrr.Util;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.selection.ItemKeyProvider;

import com.streetxportrait.android.planrr.Model.PhotoList;

public class PostKeyProvider extends ItemKeyProvider {

    private final PhotoList photoList;
    /**
     * Creates a new provider with the given scope.
     *
     * @param scope Scope can't be changed at runtime.
     */
    public PostKeyProvider(int scope, PhotoList photoList) {
        super(scope);

        this.photoList = photoList;
    }

    @Nullable
    @Override
    public Object getKey(int position) {
        return photoList.getPhoto(position);
    }

    @Override
    public int getPosition(@NonNull Object key) {
        return photoList.getIndexOfPhoto(key);
    }
}
