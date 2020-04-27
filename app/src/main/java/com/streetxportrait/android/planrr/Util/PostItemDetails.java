package com.streetxportrait.android.planrr.Util;

import androidx.annotation.Nullable;
import androidx.recyclerview.selection.ItemDetailsLookup;

import com.streetxportrait.android.planrr.Model.Post;

public class PostItemDetails extends ItemDetailsLookup.ItemDetails {

    private final int adapterPosition;
    private final Post selectionKey;

    public PostItemDetails(int adapterPosition, Post selectionKey) {
        this.adapterPosition = adapterPosition;
        this.selectionKey = selectionKey;
    }

    @Override
    public int getPosition() {
        return adapterPosition;
    }

    @Nullable
    @Override
    public Object getSelectionKey() {
        return selectionKey;
    }
}
