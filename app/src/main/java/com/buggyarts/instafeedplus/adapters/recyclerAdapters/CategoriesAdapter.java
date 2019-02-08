package com.buggyarts.instafeedplus.adapters.recyclerAdapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.buggyarts.instafeedplus.Models.Category;
import com.buggyarts.instafeedplus.R;

import java.util.ArrayList;

import static com.buggyarts.instafeedplus.utils.Constants.BUSINESS;
import static com.buggyarts.instafeedplus.utils.Constants.ENTERTAINMENT;
import static com.buggyarts.instafeedplus.utils.Constants.GENERAL;
import static com.buggyarts.instafeedplus.utils.Constants.HEALTH;
import static com.buggyarts.instafeedplus.utils.Constants.SCIENCE;
import static com.buggyarts.instafeedplus.utils.Constants.SPORTS;
import static com.buggyarts.instafeedplus.utils.Constants.TECHNOLOGY;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.CategoryViewHolder> {

    public ArrayList<Category> categoryArrayList;
    private Context mContext;
    private Callback callback;

    interface Callback{
        void onCategoryClick(Category category);
    }

    public CategoriesAdapter(Context context, ArrayList<Category> categories, Callback callback){
        this.categoryArrayList = categories;
        this.mContext = context;
        this.callback = callback;
    }

    @Override
    public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.cell_category_item,parent,false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CategoryViewHolder holder, final int position) {

        final Category category = categoryArrayList.get(position);

        switch (category.getCategory()) {
            case BUSINESS:
                holder.category_title.setText(BUSINESS);
                holder.category_icon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.cat_business));
                break;
            case ENTERTAINMENT:
                holder.category_title.setText(ENTERTAINMENT);
                holder.category_icon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.cat_entertainment));
                break;
            case GENERAL:
                holder.category_title.setText(GENERAL);
                holder.category_icon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.cat_general));
                break;
            case HEALTH:
                holder.category_title.setText(HEALTH);
                holder.category_icon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.cat_health));
                break;
            case SCIENCE:
                holder.category_title.setText(SCIENCE);
                holder.category_icon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.cat_science));
                break;
            case SPORTS:
                holder.category_title.setText(SPORTS);
                holder.category_icon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.cat_sports));
                break;
            case TECHNOLOGY:
                holder.category_title.setText(TECHNOLOGY);
                holder.category_icon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.cat_technology));
                break;
        }

        holder.category_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onCategoryClick(category);
            }
        });
    }

    @Override
    public int getItemCount() {
        if(categoryArrayList.size()>0){
            return categoryArrayList.size();
        }
        return 0;
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder {

        public RelativeLayout category_layout;
        public ImageView category_icon;
        public TextView category_title;

        public CategoryViewHolder(View itemView) {
            super(itemView);
            category_layout = itemView.findViewById(R.id.cat_cell_layout);
            category_icon = itemView.findViewById(R.id.circularImageView);
            category_title = itemView.findViewById(R.id.cat_label);
        }
    }

}
