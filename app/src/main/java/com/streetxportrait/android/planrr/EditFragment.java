package com.streetxportrait.android.planrr;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;


/**
 * A simple {@link Fragment} subclass.
 */
public class EditFragment extends Fragment {

    private ImageView imageView;
    private static final int PICK_IMAGE = 100;
    private FloatingActionButton floatingActionButton;

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

        return view;
    }

    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK);
        gallery.setDataAndType(MediaStore.Images.Media.INTERNAL_CONTENT_URI, "image/");
        startActivityForResult(gallery, PICK_IMAGE);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && requestCode == PICK_IMAGE && data != null){
            Uri uri = data.getData();
            Post photo = new Post(uri);

            try {
                Bitmap originalBitmap = photo.createBitmap(getContext());
                Bitmap finalBitmap = photo.getBitmapWithBorder();


                Glide.with(this)
                        .load(originalBitmap)
                        .centerInside()
                        .into(imageView);

                imageView.setOnClickListener(v -> {
                    if (floatingActionButton.isOrWillBeShown()) {
                        floatingActionButton.hide();
                    }
                    else {
                        floatingActionButton.show();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
