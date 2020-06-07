package com.streetxportrait.android.planrr.UI;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.Transition;

import com.bumptech.glide.Glide;
import com.streetxportrait.android.planrr.Model.PhotoList;
import com.streetxportrait.android.planrr.R;
import com.streetxportrait.android.planrr.Util.ImageProcessor;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

public class BatchProcessDialog extends DialogFragment {

    private static final String TAG = "BatchExportDialog";
    private ListView listView;
    private TextView textView;
    private ArrayList<ImageProcessor> processorList;
    private ArrayList<Bitmap> bitmaps;
    private BitmapAdapter bitmapAdapter;


    public static BatchProcessDialog newInstance(ArrayList<ImageProcessor> processors) {
        Bundle args = new Bundle();
        args.putSerializable("list", (Serializable) processors);
        BatchProcessDialog fragment = new BatchProcessDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme_FullScreenDialog);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.batch_process_dialog, container, false);


        Toolbar toolbar = rootView.findViewById(R.id.batch_process_toolbar);
        toolbar.setTitle("Batch Process");

        listView = rootView.findViewById(R.id.batch_list_view);
//        textView = rootView.findViewById(R.id.batch_export_prompt);

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        // Setting it to be fullscreen dialog
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_close_24);
        }
        setHasOptionsMenu(true);

        // getting selected images from activity
        processorList = (ArrayList<ImageProcessor>) getArguments().getSerializable("list");
        generatePreviews();


        bitmapAdapter = new BitmapAdapter(getContext(), bitmaps);
        listView.setAdapter(bitmapAdapter);

        return rootView;

    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void generatePreviews() {
        bitmaps = new ArrayList<>();

        for (ImageProcessor imageProcessor: processorList) {

            try {

                Bitmap ogBitmap = imageProcessor.createBitmap(getContext());
                bitmaps.add(imageProcessor.getScaledBitmap(imageProcessor.getBitmapWithBorder(Color.WHITE)));

            } catch (IOException e) {
                Log.d(TAG, "onCreateView: " + e.toString());
            }
        }
    }


    private void saveImages() {

        int i = 0;
        for (Bitmap image: bitmaps) {

            try {
                ImageProcessor.exportBitmap(image, getContext());

            } catch (Exception e) {
                Log.d(TAG, "saveImages: " + e);
                i++;
            }
        }

        if (i > 0) {
            Toast.makeText(getContext(), "There was a problem exporting some images", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(getContext(), bitmaps.size() + " images saved!", Toast.LENGTH_SHORT).show();

        }
        dismiss();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;

            dialog.getWindow().setLayout(width, height);
            dialog.getWindow().setWindowAnimations(R.style.DialogAnimation);
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        getActivity().getMenuInflater().inflate(R.menu.batch_process_menu,menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.batch_process_export) {
            saveImages();
            return true;
        }
        else if (id == android.R.id.home) {
            dismiss();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class BitmapAdapter extends ArrayAdapter<Bitmap> {

        public BitmapAdapter(Context context, ArrayList<Bitmap> bitmaps) {
            super(context, 0, bitmaps);
        }


        @RequiresApi(api = Build.VERSION_CODES.O)
        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            Bitmap bitmap = getItem(position);
            Log.d(TAG, "getView: called");
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.plan_view_content, parent, false);
            }
            ImageView imageView = convertView.findViewById(R.id.image);


            Glide.with(this.getContext())
                    .load(bitmap)
                    .centerCrop()
                    .into(imageView);


            return convertView;
        }
    }

}
