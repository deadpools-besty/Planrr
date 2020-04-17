package com.streetxportrait.android.planrr.UI;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.streetxportrait.android.planrr.Model.PhotoList;
import com.streetxportrait.android.planrr.Model.Post;
import com.streetxportrait.android.planrr.R;
import com.streetxportrait.android.planrr.Util.PhotoListAdapter;

import java.io.IOException;


public class GridFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private PhotoListAdapter adapter;
    private int editItemPosition;
    private static final String TAG = "Main-Activity";
    private static final int PICK_IMAGE = 100;
    private FloatingActionButton fab;
    private PhotoList photoList;
    private SharedPreferences sharedPreferences;

    public GridFragment() {
        // Required empty public constructor
    }


    public GridFragment(PhotoList photoList) {
        this.photoList = photoList;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_main, container, false);

        fab = view.findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(v -> openGallery());

        adapter = new PhotoListAdapter(getContext(), photoList);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        // set hiding and showing of fab
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0)
                    fab.hide();
                else if (dy < 0)
                    fab.show();
            }
        });

        adapter.setOnItemClickListener(position -> {
            photoList.removePhoto(position);
            adapter.notifyDataSetChanged();
            savePhotos();
        });



        return view;
    }

    /**
     * save photos from grid to shared preferences
     */
    private void savePhotos() {
        getActivity();
        sharedPreferences = getActivity().getSharedPreferences("key", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(photoList);
        editor.putString("Photos", json);
        Log.d(TAG, "savePhotos: saved");
        editor.apply();
    }

    /**
     * open gallery through intent to pick an image
     */
    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK);
        gallery.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(gallery, PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == PICK_IMAGE){
            Post post = new Post(data.getData());

            boolean validImage = false;
            try {
                validImage = post.checkIsImage(this.getContext(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (validImage) {
                photoList.addPhoto(post);
                adapter.notifyDataSetChanged();
                savePhotos();
            }

            else {
                Toast.makeText(getContext(), "Selected file was not an image. Please select an image file", Toast.LENGTH_SHORT).show();
            }

        }
    }

}