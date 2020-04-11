package com.streetxportrait.android.planrr;

import java.util.ArrayList;
import java.util.Collections;

public class PhotoList {

    private ArrayList<Post> mPosts;


    public PhotoList() {
        mPosts = new ArrayList<>();
    }

    public ArrayList<Post> getPhotos() {
        return mPosts;
    }

    public Post getPhoto(int i) {
        return mPosts.get(i);
    }

    public int getSize() {
        return mPosts.size();
    }

    public void addPhoto(Post post) {
        mPosts.add(0, post);
    }

    public void removePhoto(Post post) {
        mPosts.remove(post);
    }

    public void removePhoto(int index) {
        mPosts.remove(index);
    }

    public void swapPhotos(int firstPhoto, int secondPhoto) {

        Collections.swap(mPosts, firstPhoto, secondPhoto);

    }


}
