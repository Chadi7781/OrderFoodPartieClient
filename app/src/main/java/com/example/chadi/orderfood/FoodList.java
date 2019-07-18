package com.example.chadi.orderfood;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chadi.orderfood.Common.Common;
import com.example.chadi.orderfood.Databases.Database;
import com.example.chadi.orderfood.Interface.ItemClickListener;
import com.example.chadi.orderfood.Model.Food;
import com.example.chadi.orderfood.ViewHolder.FoodViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class FoodList extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference foodList;
    TextView txtFullName;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    String categoryId="";
    FirebaseRecyclerAdapter<Food,FoodViewHolder>adapter;

    //Search Functionnality
    FirebaseRecyclerAdapter<Food,FoodViewHolder>searchAdapter;
    List<String> suggestList=new ArrayList<>();
    MaterialSearchBar materialSearchBar;

    //FAvorites
    Database localDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);

        //Firebase
        database=FirebaseDatabase.getInstance();
        foodList=database.getReference().child("Foods");

        //local DB
        localDB = new Database(this);

        recyclerView=(RecyclerView)findViewById(R.id.recycler_food);
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //Get Intent here
        if(getIntent()!=null){
            categoryId=getIntent().getStringExtra("CategoryId");

            if(!categoryId.isEmpty() && categoryId!=null){

                if(Common.isConnectedToInternet(getBaseContext()))
                    loadListFood(categoryId);
                else {
                    Toast.makeText(FoodList.this, "Please check your connection !!", Toast.LENGTH_SHORT).show();
                    return;
                }

            }

            //Search
            materialSearchBar=(MaterialSearchBar)findViewById(R.id.searchBar);
            materialSearchBar.setHint("Enter your food");
            //materialSearchBar.setSpeechMode(false); No need, because we already define it at XML
            loadSuggest(); // Write Function to load Suggest from Firebase
            materialSearchBar.setLastSuggestions(suggestList);
            materialSearchBar.setCardViewElevation(10);
            materialSearchBar.addTextChangeListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    //when user type their text, we will change suggest list

                    List<String> suggest = new ArrayList<String>();
                    for(String search:suggestList){
                        if(search.toLowerCase().contains(materialSearchBar.getText().toLowerCase()))
                            suggest.add(search);
                    }
                    materialSearchBar.setLastSuggestions(suggest);

                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
                @Override
                public void onSearchStateChanged(boolean enabled) {
                    //when search bar is close
                    //Restor original suggest adapter
                    if(!enabled){
                        recyclerView.setAdapter(adapter);
                    }

                }

                @Override
                public void onSearchConfirmed(CharSequence text) {
                    //when search finish
                    startSearch(text);

                }

                @Override
                public void onButtonClicked(int buttonCode) {

                }
            });

        }

    }

    private void startSearch(CharSequence text) {
        searchAdapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(
                Food.class,
                R.layout.food_item,
                FoodViewHolder.class,
                foodList.orderByChild("name").equalTo(text.toString())
        ) {
            @Override
            protected void populateViewHolder(final FoodViewHolder viewHolder, Food model, final int position) {
                viewHolder.txtFoodName.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage())
                        .into(viewHolder.imageView);

                //Add favorites
                if(localDB.isFavorite(adapter.getRef(position).getKey()))
                    viewHolder.fav_image.setImageResource(R.drawable.ic_favorite_black_24dp);

                //Click to change state of favorite
                viewHolder.fav_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!localDB.isFavorite(adapter.getRef(position).getKey())) {
                            localDB.addToFavorites(adapter.getRef(position).getKey());
                            viewHolder.fav_image.setImageResource(R.drawable.ic_favorite_black_24dp);
                            Toast.makeText(FoodList.this, "was added to favorites", Toast.LENGTH_SHORT).show();
                        }
                        else {

                            localDB.removeFromFavorites(adapter.getRef(position).getKey());
                            viewHolder.fav_image.setImageResource(R.drawable.ic_favorite_black_24dp);
                            Toast.makeText(FoodList.this, "was removed from favorites", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

                final Food local = model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent foodDetail = new Intent(FoodList.this, FoodDetail.class);
                        foodDetail.putExtra("FoodId", searchAdapter.getRef(position).getKey());//send foodId to new activity
                        startActivity(foodDetail);

                    }
                });
            }
        };
        recyclerView.setAdapter(searchAdapter); //set adapter for recycler view is search result
    }


    private void loadSuggest() {
        foodList.orderByChild("menuId").equalTo(categoryId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot postSnapshot:dataSnapshot.getChildren()){
                            Food item = postSnapshot.getValue(Food.class);
                            suggestList.add(item.getName()); //add name of food to suggest list

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void loadListFood(String categoryId) {
        adapter=new FirebaseRecyclerAdapter<Food, FoodViewHolder>(Food.class,
                R.layout.food_item,
                FoodViewHolder.class,
                foodList.orderByChild("menuId").equalTo(categoryId))
        { // like :Select * from Food where MenuId=) {
            @Override
            protected void populateViewHolder(FoodViewHolder viewHolder, Food model, int position) {
                viewHolder.txtFoodName.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage())
                        .into(viewHolder.imageView);
                final Food local=model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent foodDetail=new Intent(FoodList.this,FoodDetail.class);
                        foodDetail.putExtra("FoodId",adapter.getRef(position).getKey());//send foodId to new activity
                        startActivity(foodDetail);

                    }
                });

            }
        };
        //set adapter
        Log.d("TAG",""+adapter.getItemCount());
        recyclerView.setAdapter(adapter);
    }
}
