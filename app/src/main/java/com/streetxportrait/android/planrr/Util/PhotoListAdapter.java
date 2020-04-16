package com.streetxportrait.android.planrr.Util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.streetxportrait.android.planrr.Model.PhotoList;
import com.streetxportrait.android.planrr.Model.Post;
import com.streetxportrait.android.planrr.R;
import com.theophrast.ui.widget.SquareImageView;

public class PhotoListAdapter extends RecyclerView.Adapter<PhotoListAdapter.PhotoListViewHolder> {

    private Context context;
    private PhotoList photoList;
    private OnItemClickListener listener;
    private static final String TAG = "Adapter";

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public PhotoListAdapter(Context context, PhotoList photoList) {
        this.context = context;
        this.photoList = photoList;

    }

    @NonNull
    @Override
    public PhotoListAdapter.PhotoListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.plan_view_content, null);
        return new PhotoListViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoListAdapter.PhotoListViewHolder holder, int position) {

        Post post = photoList.getPhoto(position);


        Glide.with(context)
                .load(post.getUri())
                .centerInside()
                .into(holder.imageView);

    }

    @Override
    public int getItemCount() {
        return photoList.getSize();
    }

    public class PhotoListViewHolder extends RecyclerView.ViewHolder {

        SquareImageView imageView;

        public PhotoListViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);

            imageView = itemView.findViewById(R.id.image);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(position);
                    }
                }
            });
        }
    }
}
