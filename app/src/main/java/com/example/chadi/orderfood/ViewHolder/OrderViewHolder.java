package com.example.chadi.orderfood.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.chadi.orderfood.Interface.ItemClickListener;
import com.example.chadi.orderfood.R;

public class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

     public TextView txtOrderId,txtOrderStatus,txtOrderPhone,txtOrderAddress;
     public ItemClickListener itemClickListener;

    public OrderViewHolder(View itemView) {
        super(itemView);
        txtOrderId=(TextView)itemView.findViewById(R.id.order_id);
        txtOrderStatus=(TextView)itemView.findViewById(R.id.order_status);
        txtOrderPhone=(TextView)itemView.findViewById(R.id.order_phone);
        txtOrderAddress=(TextView)itemView.findViewById(R.id.order_address);
        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v,getAdapterPosition(),false);


    }
}
