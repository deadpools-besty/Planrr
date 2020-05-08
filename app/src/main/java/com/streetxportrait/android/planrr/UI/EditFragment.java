package com.streetxportrait.android.planrr.UI;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.radiobutton.MaterialRadioButton;
import com.streetxportrait.android.planrr.Model.ImageProcessor;
import com.streetxportrait.android.planrr.R;

import java.io.IOException;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class EditFragment extends Fragment {

    private ImageView imageView;
    private static final int PICK_IMAGE = 100;
    private static final String TAG = "Edit-Fragment";
    private FloatingActionButton floatingActionButton;
    private ImageProcessor imageProcessor;
    private Bitmap originalBitmap;
    private Bitmap whiteBorderBitmap;
    private Bitmap dominantBitmap;
    private Bitmap dominantScaledBitmap;
    private Bitmap exportBitmap;
    private Bitmap originalScaledBitmap;
    private Bitmap whiteBorderScaledBitmap;
    private MenuItem saveMenuItem;
    private TextView addPhotoTV;
    private boolean validImage = false;
    private RadioGroup radioGroup;
    private MaterialRadioButton noBorderRadioButton, whiteBorderRadioButton, dominantBorderRadioButton;



    public EditFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.edit_menu, menu);
        saveMenuItem = menu.findItem(R.id.save_image);
        if (!validImage) {
            saveMenuItem.setVisible(false);
        }
        else saveMenuItem.setVisible(true);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_image:
                imageProcessor.saveBitmap(exportBitmap, getContext());
                Toast.makeText(getContext(), "Photo saved!", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onActivityResult: saved");
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_edit, container, false);


        floatingActionButton = view.findViewById(R.id.edit_fab);
        imageView = view.findViewById(R.id.edit_bitmap);
        addPhotoTV = view.findViewById(R.id.edit_add_photo_tv);
        radioGroup = view.findViewById(R.id.radioGroup);
        radioGroup.setVisibility(View.INVISIBLE);
        dominantBorderRadioButton = view.findViewById(R.id.dominantRadioButton);
        whiteBorderRadioButton =  view.findViewById(R.id.whiteRadioButton);
        noBorderRadioButton = view.findViewById(R.id.noBorderRadioButton);


        radioGroup.setOnCheckedChangeListener((group, checkedId) -> switchImages(checkedId));

        floatingActionButton.setOnClickListener(v -> {
            openGallery();
        });

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
            handleImageImport(data.getData());

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void handleImageImport(Uri uri) {
        Log.d(TAG, "handle image: " + uri);
        imageProcessor = new ImageProcessor(uri);


        try {
            validImage = imageProcessor.checkIsImage(this.getContext(), uri);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (validImage) {
            saveMenuItem.setVisible(true);
            addPhotoTV.setVisibility((View.INVISIBLE));
            radioGroup.setVisibility(View.VISIBLE);
            try {
                createBitmaps();
            } catch (IOException e) {
                Log.d(TAG, "handleImageImport: " + e);
            }

            radioGroup.check(whiteBorderRadioButton.getId());

        }
        else {
            Toast.makeText(getContext(), "Selected file was not an image. Please select an image file", Toast.LENGTH_SHORT).show();
        }
    }

    private void switchImages(int checkedId) {

        switch (checkedId) {
            case R.id.noBorderRadioButton:
                imageView.setBackground(getResources().getDrawable(R.drawable.bg_rect));
                Glide.with(this)
                        .load(originalScaledBitmap)
                        .into(imageView);
                return;
            case R.id.whiteRadioButton:
                exportBitmap = whiteBorderBitmap;
                Glide.with(this)
                        .load(whiteBorderScaledBitmap)
                        .fitCenter()
                        .into(imageView);
                return;
            case R.id.dominantRadioButton:
                exportBitmap = dominantBitmap;
                imageView.setBackground(getResources().getDrawable(R.drawable.bg_rect));
                Glide.with(this)
                        .load(dominantScaledBitmap)
                        .fitCenter()
                        .into(imageView);

        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createBitmaps() throws IOException {

        originalBitmap = imageProcessor.createBitmap(getContext());
        whiteBorderBitmap = imageProcessor.getBitmapWithBorder(Color.WHITE);
        originalScaledBitmap = imageProcessor.getScaledBitmap(originalBitmap);
        whiteBorderScaledBitmap = imageProcessor.getScaledBitmap(whiteBorderBitmap);
        Map<String, Integer> swatches =  imageProcessor.getSwatches();

        Integer dominantColor = swatches.get("dominant");
        Integer mutedColor = swatches.get("muted");
        Integer vibrantColor = swatches.get("vibrant");


        if (dominantColor != null) {
            dominantBitmap = imageProcessor.getBitmapWithBorder(dominantColor);
            dominantScaledBitmap = imageProcessor.getScaledBitmap(dominantBitmap);
        }
        else {
            dominantBorderRadioButton.setVisibility(View.INVISIBLE);
        }
    }

}
