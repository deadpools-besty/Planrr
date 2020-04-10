package com.streetxportrait.android.planrr;

import java.util.ArrayList;

public class PhotoList {

    private ArrayList<Photo> mPhotos;


    public PhotoList() {
        mPhotos = new ArrayList<>();
    }

    public ArrayList<Photo> getPhotos() {
        return mPhotos;
    }

    public Photo getPhoto(int i) {
        return mPhotos.get(i);
    }

    public int getSize() {
        return mPhotos.size();
    }

    public void addPhoto(Photo photo) {
        mPhotos.add(0, photo);
    }

    public void removePhoto(Photo photo) {
        mPhotos.remove(photo);
    }

    public void removePhoto(int index) {
        mPhotos.remove(index);
    }

    public void swapPhotos(Photo firstPhoto, Photo secondPhoto) {
        int tempIndex1 = mPhotos.indexOf(firstPhoto);
        int tempIndex2 = mPhotos.indexOf(secondPhoto);

        mPhotos.add(tempIndex1, secondPhoto);
        mPhotos.add(tempIndex2, firstPhoto);

    }


}
