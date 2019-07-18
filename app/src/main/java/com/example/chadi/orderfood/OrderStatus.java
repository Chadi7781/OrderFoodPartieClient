package com.example.chadi.orderfood;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.example.chadi.orderfood.Common.Common;
import com.example.chadi.orderfood.Databases.Database;
import com.example.chadi.orderfood.Interface.ItemClickListener;
import com.example.chadi.orderfood.Model.Order;
import com.example.chadi.orderfood.Model.Request;
import com.example.chadi.orderfood.R;
import com.example.chadi.orderfood.ViewHolder.OrderViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class OrderStatus extends AppCompatActivity {

    private DatabaseReference requests;
    FirebaseDatabase db;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    FirebaseRecyclerAdapter<Request,OrderViewHolder>adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status);
        db = FirebaseDatabase.getInstance();
        requests = db.getReference("Requests");
        //init
        recyclerView = (RecyclerView) findViewById(R.id.listOrders);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        if (Common.isConnectedToInternet(getBaseContext())) {
            if (getIntent() == null)
                loadOrders(Common.CurrentUser.getPhone());
            else
                loadOrders(getIntent().getStringExtra("userPhone"));
        }
        else {
            Toast.makeText(OrderStatus.this, "Please check your connection !!", Toast.LENGTH_SHORT).show();
            return;
        }
    }


    private void loadOrders(String phone) {
        adapter = new FirebaseRecyclerAdapter<Request, OrderViewHolder>(
                Request.class,
                R.layout.order_layout,
                OrderViewHolder.class,
                requests.orderByChild("phone")
                        .equalTo(phone)
        ) {
            @Override
            protected void populateViewHolder(OrderViewHolder viewHolder, Request model, int position) {
                viewHolder.txtOrderId.setText(adapter.getRef(position).getKey());
                viewHolder.txtOrderStatus.setText(Common.convertCodeToStatus(model.getStatus()));
                viewHolder.txtOrderAddress.setText(model.getAddress());
                viewHolder.txtOrderPhone.setText(model.getPhone());


                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                    }
                });
            }
        };
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
    }



}