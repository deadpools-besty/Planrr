package com.streetxportrait.android.planrr.UI;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
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
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.streetxportrait.android.planrr.Model.PhotoList;
import com.streetxportrait.android.planrr.Model.Post;
import com.streetxportrait.android.planrr.R;
import com.streetxportrait.android.planrr.Util.SharedPrefManager;

import java.io.IOException;

import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.ScaleInAnimator;
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;


public class GridFragment extends Fragment {

    private static final String TAG = "Grid-fragments";
    private static final int PICK_IMAGE = 100;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private PhotoListAdapter adapter;
    private FloatingActionButton fab;
    private PhotoList photoList;
    private MenuItem deleteItem;
    private MenuItem stopDelete;
    private CoordinatorLayout coordinatorLayout;
    private ItemTouchHelper helper;
    private TextView addPhotoTV;
    private SharedPrefManager sharedPrefManager;
    private String currentTheme;
    private Post lastDeleted;

    public GridFragment() {
        // Required empty public constructor
    }


    public GridFragment(PhotoList photoList) {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_grid, container, false);
        sharedPrefManager = new SharedPrefManager(getContext());

        photoList = sharedPrefManager.loadPhotos();
        fab = view.findViewById(R.id.fab);
        recyclerView = view.findViewById(R.id.recyclerView);
        addPhotoTV = view.findViewById(R.id.add_photo_tv);
        coordinatorLayout = view.findViewById(R.id.coordinator);
        fab.setOnClickListener(v -> openGallery());

        adapter = new PhotoListAdapter(getContext(), photoList);
        recyclerView.setHasFixedSize(true);
        layoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
//        recyclerView.setAdapter(adapter);
        recyclerView.setAdapter(new AlphaInAnimationAdapter(adapter));

        showAddTV();

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

        recyclerView.setItemAnimator(new ScaleInAnimator());

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

        lastDeleted = photoList.getPhoto(position);
        photoList.removePhoto(position);
        adapter.notifyItemRemoved(position);
        showAddTV();
        sharedPrefManager.savePhotos(photoList);
        showDeleteSnackbar(lastDeleted, position);

    }

    private void showDeleteSnackbar(Post lastDeleted, int position) {

        int finalPos = position + 1;
        Snackbar snackbar = Snackbar.make(coordinatorLayout,"Post " + finalPos + " was deleted from grid", Snackbar.LENGTH_SHORT)
                .setAction("UNDO", v -> {
                    // reinsert photo into photo list notify adapter that photo has been reinserted and save list again
                    photoList.insertPhoto(lastDeleted, position);
                    adapter.notifyItemInserted(position);
                    sharedPrefManager.savePhotos(photoList);
                    Snackbar snackbar1 = Snackbar.make(coordinatorLayout, "Post Restored!", Snackbar.LENGTH_SHORT);
                    snackbar1.show();
                });
        snackbar.show();

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
                Toast.makeText(getContext(), "Tap a post to delete it, swipe on screen to rearrange posts.", Toast.LENGTH_SHORT).show();
                adapter.setOnItemClickListener(this::delete);

                // allow swiping to rearrange pictures
                startItemTouchHelper();
                deleteItem.setVisible(false);
                stopDelete.setVisible(true);
                return true;

            case R.id.stop_selection:

                adapter.setOnItemClickListener(null);
                helper.attachToRecyclerView(null);
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

            if (data != null) {
                ClipData clipData = data.getClipData();

                if (clipData != null) {
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        ClipData.Item item = clipData.getItemAt(i);
                        handleImageImport(item.getUri());
                    }
                }

                else {
                    handleImageImport(data.getData());
                }
            }

        }
    }

    private void handleImageImport(Uri uri) {

        Post post = new Post(uri);

        boolean validImage = false;
        try {
            validImage = post.checkIsImage(this.getContext(), uri);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (validImage) {
            photoList.addPhoto(post);
            adapter.notifyItemInserted(0);
            sharedPrefManager.savePhotos(photoList);
            showAddTV();
//              savePhotos();
        }

        else {
            Toast.makeText(getContext(), "" + uri.getLastPathSegment() + "was not an image. Please select an image file", Toast.LENGTH_SHORT).show();
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

                sharedPrefManager.savePhotos(photoList);
                return false;
            }


            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            }
        });

        helper.attachToRecyclerView(recyclerView);

    }



    /**
     * open gallery through intent to pick an image
     */
    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK);
        gallery.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        gallery.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(gallery, PICK_IMAGE);
    }


}
