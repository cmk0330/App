package com.cmk.app.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cmk.app.R;

public class NestAdapter extends RecyclerView.Adapter<NestAdapter.NestVH> {

    @NonNull
    @Override
    public NestVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_nest, parent, false);
        return new NestVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NestVH holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 10;
    }

    public static class NestVH extends RecyclerView.ViewHolder {
        public NestVH(@NonNull View itemView) {
            super(itemView);
        }
    }
}
