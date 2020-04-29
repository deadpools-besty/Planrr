package com.streetxportrait.android.planrr.UI;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.selection.OnDragInitiatedListener;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.selection.StorageStrategy;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.play.core.assetpacks.as;
import com.google.gson.Gson;
import com.streetxportrait.android.planrr.Model.PhotoList;
import com.streetxportrait.android.planrr.Model.Post;
import com.streetxportrait.android.planrr.R;
import com.streetxportrait.android.planrr.Util.ActionModeController;
import com.streetxportrait.android.planrr.Util.PhotoListAdapter;
import com.streetxportrait.android.planrr.Util.PostDetailsLookup;
import com.streetxportrait.android.planrr.Util.PostKeyProvider;

import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;


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
    private SelectionTracker selectionTracker;
    private ActionMode actionMode;
    private BottomAppBar bottomAppBar;
    private MenuItem deleteItem;
    private ItemTouchHelper helper;
    private static final int START_DELETE_SELECTION = 15;
    private static final int END_DELETE_SELECTION = 17;
    private int currentDelete = START_DELETE_SELECTION;

    public GridFragment() {
        // Required empty public constructor
    }


    public GridFragment(PhotoList photoList) {
        this.photoList = photoList;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (savedInstanceState != null) {
            selectionTracker.onRestoreInstanceState(savedInstanceState);
        }

        View view = inflater.inflate(R.layout.activity_main, container, false);

        fab = view.findViewById(R.id.fab);
        recyclerView = view.findViewById(R.id.recyclerView);
        bottomAppBar = view.findViewById(R.id.bottom_app_bar);

        ((AppCompatActivity) getActivity()).setSupportActionBar(bottomAppBar);

        fab.setOnClickListener(v -> openGallery());

        adapter = new PhotoListAdapter(getContext(), photoList);
        recyclerView.setHasFixedSize(true);
        layoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        //startSelectionTracking();

        startItemTouchHelper();


        // set hiding and showing of fab
        Log.d(TAG, "onCreateView: " + bottomAppBar.getHideOnScroll());
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

/*
        adapter.setOnItemClickListener(position -> {
            photoList.removePhoto(position);
            adapter.notifyDataSetChanged();
            savePhotos();
        });
*/

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            selectionTracker.onRestoreInstanceState(savedInstanceState);
        }
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.grid_menu, menu);
        deleteItem = menu.findItem(R.id.delete_items);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.delete_items) {

            if (currentDelete == START_DELETE_SELECTION) {
                helper = null;
                currentDelete = END_DELETE_SELECTION;
                Toast.makeText(getContext(), "Select items then press delete again", Toast.LENGTH_SHORT ).show();
                startSelectionTracking();
            }

            else if (currentDelete == END_DELETE_SELECTION) {
                deletePosts();
                selectionTracker.clearSelection();
                selectionTracker = null;
                startItemTouchHelper();
                currentDelete = START_DELETE_SELECTION;
            }
            return true;
        }
        else {
            return false;
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

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (selectionTracker != null)
            selectionTracker.onSaveInstanceState(outState);
    }



    /**
     * start item touch helper to switch items
     */
    public void startItemTouchHelper() {

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
     * start selection to delete
     */
    public void startSelectionTracking() {

        selectionTracker = new SelectionTracker.Builder<>(
                "post-select",
                recyclerView,
                new PostKeyProvider(1, photoList),
                new PostDetailsLookup(recyclerView),
                StorageStrategy.createStringStorage()
        ).withOnDragInitiatedListener(e -> {
            Log.d(TAG, "onDragInitiated: ");
            return true;
        }).build();

        adapter.setSelectionTracker(selectionTracker);


    }

    public void deletePosts() {
        // delete items
        selectionTracker.addObserver(new SelectionTracker.SelectionObserver() {
            @Override
            public void onSelectionChanged() {
                super.onSelectionChanged();
                if (selectionTracker.hasSelection() && actionMode == null) {
                    actionMode = ((AppCompatActivity) getActivity()).startSupportActionMode(new ActionModeController(getContext(), selectionTracker));
                }
                else if (!selectionTracker.hasSelection() && actionMode != null) {
                    actionMode.finish();
                    actionMode = null;
                }
                for (Post post : (Iterable<Post>) selectionTracker.getSelection()) {
                    Log.d(TAG, "onSelectionChanged: " + post.getUri());
                    photoList.removePhoto(post);
                }
            }
        });

        savePhotos();

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


}
