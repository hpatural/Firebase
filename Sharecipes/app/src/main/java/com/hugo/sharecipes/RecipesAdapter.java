package com.hugo.sharecipes;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by hpatural on 06/06/2016.
 */
public class RecipesAdapter extends RecyclerView.Adapter<RecipesAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<Recipe> mRecipes;


    public RecipesAdapter(ArrayList<Recipe> recipes,Context context) {
        mContext = context;
        mRecipes = recipes;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recipe, parent, false);
        ViewHolder vh = new ViewHolder(v);
        vh.recipePicture = (ImageView)v.findViewById(R.id.recipePicture);
        vh.recipeTitle = (TextView)v.findViewById(R.id.recipeTitle);
        vh.recipeDescription = (TextView)v.findViewById(R.id.recipeDescription);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        String description = mRecipes.get(position).getDescription();
        String title = mRecipes.get(position).getTitle();

        if(description.equals("")){
            holder.recipeTitle.setText("No Title");
        }else{
            holder.recipeTitle.setText(title);
        }

        if(description.equals("")){
            holder.recipeDescription.setText("No Description");
        }else{
            holder.recipeDescription.setText(description);
        }

        holder.recipePicture.setImageBitmap(mRecipes.get(position).getPicture());
    }

    @Override
    public int getItemCount() {
        return mRecipes.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        //protected TextView noFriends;
        protected TextView recipeTitle;
        protected TextView recipeDescription;
        protected ImageView recipePicture;

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }


    public void setList(ArrayList<Recipe> recipes){
        this.mRecipes=recipes;
        this.notifyDataSetChanged();
    }
}
