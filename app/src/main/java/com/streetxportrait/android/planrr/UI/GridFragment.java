package com.streetxportrait.android.planrr.UI;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.streetxportrait.android.planrr.Model.PhotoList;
import com.streetxportrait.android.planrr.Model.Post;
import com.streetxportrait.android.planrr.R;
import com.streetxportrait.android.planrr.Util.PhotoListAdapter;

import java.io.IOException;
import java.lang.reflect.Type;


public class GridFragment extends Fragment implements PhotoListAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private PhotoListAdapter adapter;
    private static final String TAG = "Grid-fragments";
    private static final int PICK_IMAGE = 100;
    private FloatingActionButton fab;
    private PhotoList photoList;
    private SharedPreferences sharedPreferences;
    private MenuItem deleteItem;
    private MenuItem stopDelete;
    private ItemTouchHelper helper;
    private TextView addPhotoTV;

    public GridFragment() {
        // Required empty public constructor
    }


    public GridFragment(PhotoList photoList) {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_grid, container, false);

        getPhotos();
        fab = view.findViewById(R.id.fab);
        recyclerView = view.findViewById(R.id.recyclerView);
        addPhotoTV = view.findViewById(R.id.add_photo_tv);
        fab.setOnClickListener(v -> openGallery());

        adapter = new PhotoListAdapter(getContext(), photoList);
        recyclerView.setHasFixedSize(true);
        layoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        Log.d(TAG, "GridFragment: " + photoList);

        showAddTV();
        // allow swiping to rearrange pictures
        startItemTouchHelper();

        // set hiding and showing of fab
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    fab.hide();
                }
                else if (dy < 0) {
                    fab.show();
                }
            }
        });


        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.grid_menu, menu);
        deleteItem = menu.findItem(R.id.delete_items);
        stopDelete = menu.findItem(R.id.stop_selection);
        stopDelete.setVisible(false);
    }

    // stop deletion if user swipes away from fragment
    @Override
    public void onDestroyOptionsMenu() {
        super.onDestroyOptionsMenu();
        adapter.setOnItemClickListener(null);
    }

    /**
     * method to delete post in photo list
     * @param position where the post is in the photoList
     */
    private void delete(int position) {

        Log.d(TAG, "delete: " + position);
        photoList.removePhoto(position);
        adapter.notifyDataSetChanged();
        savePhotos();
    }

    /**
     * method for showing the text to prompting user to add a photo
     */
    private void showAddTV() {
        if (photoList.getSize() == 0) {
            addPhotoTV.setVisibility(View.VISIBLE);
        }
        else addPhotoTV.setVisibility((View.INVISIBLE));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch(item.getItemId()) {

            case R.id.delete_items:
                Toast.makeText(getContext(), "Tap an image to delete it and press stop to return to normal", Toast.LENGTH_SHORT).show();
                adapter.setOnItemClickListener(this);
                helper = null;
                Log.d(TAG, "start-" + adapter.getListener());
                deleteItem.setVisible(false);
                stopDelete.setVisible(true);
                return true;

            case R.id.stop_selection:

                adapter.setOnItemClickListener(null);
                startItemTouchHelper();
                Log.d(TAG, "stop-" + adapter.getListener());
                stopDelete.setVisible(false);
                deleteItem.setVisible(true);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

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


    /**
     * start item touch helper to switch items
     */
    private void startItemTouchHelper() {

         helper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT | ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder dragged, @NonNull RecyclerView.ViewHolder target) {
                int positionDragged = dragged.getAdapterPosition();
                int positionTarget = target.getAdapterPosition();

                photoList.swapPhotos(positionDragged, positionTarget);

                adapter.notifyItemMoved(positionDragged, positionTarget);

                savePhotos();

                return false;
            }


            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            }
        });

        helper.attachToRecyclerView(recyclerView);

    }

    /**
     * save photos from grid to shared preferences
     */
    private void savePhotos() {
        getActivity();
        showAddTV();
        sharedPreferences = getActivity().getSharedPreferences("key", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(photoList);
        editor.putString("Photos", json);
        Log.d(TAG, "savePhotos: saved");
        editor.apply();
    }


    private void getPhotos() {
        getActivity();
        sharedPreferences = this.getActivity().getSharedPreferences("key", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("Photos", null);
        Type type = new TypeToken<PhotoList>(){}.getType();

        photoList = gson.fromJson(json, type);

        if (photoList == null) {
            photoList = new PhotoList();
        }
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
    public void onItemClick(int position) {
        delete(position);
    }
}
