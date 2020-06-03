package com.streetxportrait.android.planrr.Model;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PhotoList {

    private static final String TAG = "Photo-list";
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

    public void insertPhoto(Post post, int index) {
        mPosts.add(index, post);
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

    public int getIndexOfPhoto(Object post) {
        return mPosts.indexOf(post);
    }

    public List<Integer> getSelectedItems() {
        ArrayList<Integer> selectedList = new ArrayList<>();

        for (Post p : mPosts) {
            if (p.getIsSelected()) {
                selectedList.add(mPosts.indexOf(p));
            }
        }

        return selectedList;
    }


}
