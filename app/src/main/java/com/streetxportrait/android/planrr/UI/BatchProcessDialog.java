package com.streetxportrait.android.planrr.UI;

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
    private ArrayList<ImageProcessor> photoList;
    private ProcessorAdapter processorAdapter;


    public static BatchProcessDialog newInstance(ArrayList<ImageProcessor> processors) {
        Bundle args = new Bundle();
        args.putSerializable("list", (Serializable) processors);
        BatchProcessDialog fragment = new BatchProcessDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.batch_process_dialog, container, false);


        Toolbar toolbar = rootView.findViewById(R.id.batch_process_toolbar);
        toolbar.setTitle("Batch Process");

        listView = rootView.findViewById(R.id.batch_list_view);
        textView = rootView.findViewById(R.id.batch_export_prompt);

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
        photoList = (ArrayList<ImageProcessor>) getArguments().getSerializable("list");
        processorAdapter = new ProcessorAdapter(getContext(), photoList);
        listView.setAdapter(processorAdapter);

        return rootView;

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        return dialog;
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



            return true;
        }
        else if (id == android.R.id.home) {
            dismiss();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class ProcessorAdapter extends ArrayAdapter<ImageProcessor> {

        public ProcessorAdapter(Context context, ArrayList<ImageProcessor> processors) {
            super(context, 0, processors);
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ImageProcessor imageProcessor = getItem(position);
            Log.d(TAG, "getView: called");
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.plan_view_content, parent, false);
            }
            ImageView imageView = convertView.findViewById(R.id.image);

            try {
                Bitmap originalBitmap = imageProcessor.createBitmap(getContext());
                Bitmap borderedBitmap = imageProcessor.getBitmapWithBorder(Color.WHITE);

                Glide.with(this.getContext())
                        .load(imageProcessor.getScaledBitmap(borderedBitmap))
                        .centerCrop()
                        .into(imageView);

            } catch (IOException e) {
                Log.d(TAG, "getView: " + e.toString());
            }

            return convertView;
        }
    }

}
