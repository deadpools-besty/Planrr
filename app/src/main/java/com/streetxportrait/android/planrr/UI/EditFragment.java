package com.streetxportrait.android.planrr.UI;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.streetxportrait.android.planrr.Model.Post;
import com.streetxportrait.android.planrr.R;

import java.io.IOException;


/**
 * A simple {@link Fragment} subclass.
 */
public class EditFragment extends Fragment {

    private PhotoView imageView;
    private static final int PICK_IMAGE = 100;
    private static final String TAG = "Edit-Fragment";
    private FloatingActionButton floatingActionButton;
    private Post photo;
    private Bitmap originalBitmap;
    private Bitmap finalBitmap;
    private Button saveButton;
    private Bitmap originalScaledBitmap;
    private static final int ORGINAL_SHOWING = 1;
    private static final int BORDERED_SHOWING = 0;
    private int IMAGE_SHOWING = BORDERED_SHOWING;
    private Bitmap finalScaledBitmap;


    public EditFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_edit, container, false);


        floatingActionButton = view.findViewById(R.id.edit_fab);
        imageView = view.findViewById(R.id.edit_bitmap);

        floatingActionButton.setOnClickListener(v -> {
            openGallery();
        });
        saveButton = view.findViewById(R.id.save_button);


        return view;
    }


    /**
     * open gallery through intent to pick an image
     */
    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK);
        gallery.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(gallery, PICK_IMAGE);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && requestCode == PICK_IMAGE && data != null) {
            handleImage(data.getData());

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void handleImage(Uri uri) {
        Log.d(TAG, "handle image: " + uri);
        photo = new Post(uri);

        boolean validImage = false;
        try {
            validImage = photo.checkIsImage(this.getContext(), uri);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (validImage) {
            saveButton.setVisibility(View.VISIBLE);
            saveButton.setOnClickListener(v -> {
                photo.saveBitmap(finalBitmap, getContext());
                Log.d(TAG, "onActivityResult: saved");

            });

            try {
                originalBitmap = photo.createBitmap(getContext());
                finalBitmap = photo.getBitmapWithBorder();
                originalScaledBitmap = photo.getScaledBitmap(originalBitmap);
                finalScaledBitmap = photo.getScaledBitmap(finalBitmap);
                Glide.with(this)
                        .load(finalScaledBitmap)
                        .fitCenter()
                        .into(imageView);

                imageView.setOnClickListener(v -> switchImages());
            } catch (IOException e) {
                Log.d(TAG, "handle image: " + e.toString());
                e.printStackTrace();
            }

        }
        else {
            Toast.makeText(getContext(), "Selected file was not an image. Please select an image file", Toast.LENGTH_SHORT).show();
        }
    }

    private void switchImages() {
        if (IMAGE_SHOWING == BORDERED_SHOWING) {
            IMAGE_SHOWING = ORGINAL_SHOWING;
            Glide.with(this)
                    .load(originalScaledBitmap)
                    .fitCenter()
                    .into(imageView);
        }
        else {
            IMAGE_SHOWING = BORDERED_SHOWING;
            Glide.with(this)
                    .load(finalScaledBitmap)
                    .fitCenter()
                    .into(imageView);
        }
    }

}
