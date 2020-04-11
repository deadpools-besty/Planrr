package com.streetxportrait.android.planrr;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;


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
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && requestCode == PICK_IMAGE){
            Uri uri = data.getData();

            Picasso.get()
                    .load(uri)
                    .transform(new WhiteBorderTransformation())
                    .into(imageView);
        }
    }
}
