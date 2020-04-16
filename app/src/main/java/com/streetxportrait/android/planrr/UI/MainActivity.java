package com.streetxportrait.android.planrr.UI;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.streetxportrait.android.planrr.Model.PhotoList;
import com.streetxportrait.android.planrr.Model.Post;
import com.streetxportrait.android.planrr.R;
import com.streetxportrait.android.planrr.Util.PhotoListAdapter;

import java.lang.reflect.Type;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private PhotoListAdapter adapter;
    private int editItemPosition;
    private static final String TAG = "Main-Activity";
    private static final int PICK_IMAGE = 100;
    private FloatingActionButton fab;
    private PhotoList photoList;
    private SharedPreferences sharedPreferences;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //photoList = new PhotoList();
        getPhotos();
        fab = findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(view -> openGallery());

        adapter = new PhotoListAdapter(this, photoList);
        recyclerView = findViewById(R.id.recyclerView);
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


    }

    private void getPhotos() {
        sharedPreferences = getSharedPreferences("key", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("Photos", null);
        Type type = new TypeToken<PhotoList>(){}.getType();

        photoList = gson.fromJson(json, type);

        if (photoList == null) {
            photoList = new PhotoList();
        }
    }

    private void savePhotos() {
        sharedPreferences = getSharedPreferences("key", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(photoList);
        editor.putString("Photos", json);
        Log.d(TAG, "savePhotos: saved");
        editor.apply();
    }

    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE){
            Post post = new Post(data.getData());
            photoList.addPhoto(post);

            adapter.notifyDataSetChanged();
            savePhotos();
        }
    }


}
