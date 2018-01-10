package com.buggyarts.instafeedplus.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.buggyarts.instafeedplus.R;

import java.util.ArrayList;

/**
 * Created by mayank on 1/7/18
 */

public class OptionsRecyclerViewAdapter extends RecyclerView.Adapter<OptionsRecyclerViewAdapter.VH> {

    ArrayList<String> categoryList;
    OnCategoryClick onCategoryClick;

    public interface OnCategoryClick {
        void onCategoryClickListener(int index, String category);
    }

    public OptionsRecyclerViewAdapter(ArrayList<String> categoryList, OnCategoryClick onCategoryClick) {
        this.categoryList = categoryList;
        this.onCategoryClick = onCategoryClick;
    }

    @Override
    public OptionsRecyclerViewAdapter.VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.simple_grid_model, parent, false);
        return new OptionsRecyclerViewAdapter.VH(v);
    }

    @Override
    public void onBindViewHolder(OptionsRecyclerViewAdapter.VH holder, final int position) {
        final String category = categoryList.get(position);
        holder.category.setText(category.replace("-", " "));
        holder.category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCategoryClick.onCategoryClickListener(position, category);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    class VH extends RecyclerView.ViewHolder {
        TextView category;

        public VH(View itemView) {
            super(itemView);
            category = itemView.findViewById(R.id.title_text);
        }
    }
}
