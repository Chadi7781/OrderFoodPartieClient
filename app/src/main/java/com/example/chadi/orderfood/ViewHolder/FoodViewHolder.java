package com.example.chadi.orderfood.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chadi.orderfood.Interface.ItemClickListener;
import com.example.chadi.orderfood.R;

public class FoodViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

  public TextView txtFoodName;
  public ImageView imageView, fav_image;

  private ItemClickListener itemClickListener;

  public void setItemClickListener(ItemClickListener itemClickListener) {
    this.itemClickListener = itemClickListener;
  }

  public FoodViewHolder(View itemView) {
    super(itemView);
    txtFoodName=(TextView)itemView.findViewById(R.id.food_name);
    imageView=(ImageView) itemView.findViewById(R.id.food_image);
    fav_image=(ImageView) itemView.findViewById(R.id.fav);

    itemView.setOnClickListener(this);
  }

  @Override
  public void onClick(View v) {
    itemClickListener.onClick(v,getAdapterPosition(),false);


  }
}
