package com.example.chadi.orderfood;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.chadi.orderfood.Common.Common;
import com.example.chadi.orderfood.Databases.Database;
import com.example.chadi.orderfood.Model.Food;
import com.example.chadi.orderfood.Model.Order;

import com.example.chadi.orderfood.Model.Rating;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;

import java.util.ArrayList;
import java.util.Arrays;

public class FoodDetail extends AppCompatActivity implements RatingDialogListener {
    TextView food_name,food_description,food_price;
    ImageView food_image;
    CollapsingToolbarLayout collapsingToolbarLayout;
    FloatingActionButton btnCart,btnRating;

    ElegantNumberButton numberButton;
    String foodId="";

    FirebaseDatabase database;
    DatabaseReference food;
    DatabaseReference ratingTbl;

    Food currentFood;

    RatingBar ratingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);

        //init Fire
        database=FirebaseDatabase.getInstance();
        food=database.getReference().child("Foods");
        ratingTbl = database.getReference().child("Rating");

        //init view
        init();

        btnRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRatingDialog();
            }
        });

        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Database(getBaseContext()).addToCart(new Order(
                        foodId,
                        currentFood.getName(),
                        numberButton.getNumber(),
                        currentFood.getPrice(),
                        currentFood.getDiscount()
                ));

                Toast.makeText(FoodDetail.this, "Add to chart", Toast.LENGTH_SHORT).show();
            }

        });
    }

    private void showRatingDialog() {
        new AppRatingDialog.Builder()
                .setPositiveButtonText("Submit")
                .setNegativeButtonText("Cancal")
                .setNoteDescriptions(Arrays.asList("VeryBad","Not Good","Quite Ok","Very Good","Excellent"))
                .setDefaultRating(1)
                .setTitle("Rate this food")
                .setDescription("Please select some stars and give your feedback")
                .setTitleTextColor(R.color.colorPrimary)
                .setDescriptionTextColor(R.color.colorPrimary)
                .setHint("Please write your comment here...")
                .setHintTextColor(R.color.colorAccent)
                .setCommentTextColor(R.color.colorWhite)
                .setCommentBackgroundColor(R.color.colorPrimaryDark)
                .setWindowAnimation(R.style.RatingDialogFadeAnim)
                .create(FoodDetail.this)
                .show();
    }

    private void init() {
        food_name=(TextView)findViewById(R.id.food_name);
        food_price=(TextView)findViewById(R.id.food_price);
        food_description=(TextView)findViewById(R.id.food_description);
        food_image=(ImageView) findViewById(R.id.img_food);
        numberButton=(ElegantNumberButton)findViewById(R.id.number_button) ;
        btnCart=(FloatingActionButton)findViewById(R.id.btnCart);
        btnRating=(FloatingActionButton)findViewById(R.id.btnrating);


        collapsingToolbarLayout=(CollapsingToolbarLayout)findViewById(R.id.collapsing);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppbar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapseAppbar);

        //Get foodId from intent
        if(getIntent()!=null){
            foodId=getIntent().getStringExtra("FoodId");
            if(!foodId.isEmpty()){
                if(Common.isConnectedToInternet(getBaseContext())){
                    getDetailFood(foodId);
                    getRatingFood(foodId);
                }
                else {
                    Toast.makeText(FoodDetail.this, "Please check your connection !!", Toast.LENGTH_SHORT).show();
                    return;
                }

            }
        }

    }

    private void getRatingFood(String foodId) {
        com.google.firebase.database.Query foodRating = ratingTbl.orderByChild("foodId").equalTo(foodId);

        foodRating.addValueEventListener(new ValueEventListener() {
            int count =0,sum=0;
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    Rating item = postSnapshot.getValue(Rating.class);
                    sum+=Integer.parseInt(item.getRateValue());
                    count++;
                }
                if (count != 0){
                    float average =sum/count;
                    ratingBar.setRating(average);

                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void getDetailFood(String foodId) {
        food.child(foodId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                currentFood=dataSnapshot.getValue(Food.class);

                //Set Image
                Picasso.with(getBaseContext()).load(currentFood.getImage())
                        .into(food_image);

                collapsingToolbarLayout.setTitle(currentFood.getName());
                food_price.setText(currentFood.getPrice());
                food_description.setText(currentFood.getDescription());
                food_name.setText(currentFood.getName());


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onNegativeButtonClicked() {

    }

    @Override
    public void onPositiveButtonClicked(int value, String comments) {
        //Get rating and upload to firebase
        final Rating rating = new Rating(Common.CurrentUser.getPhone(),
                foodId,
                String.valueOf(value),
                comments);
        ratingTbl.child(Common.CurrentUser.getPhone()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(Common.CurrentUser.getPhone()).exists()){
                    //Remove old value
                    ratingTbl.child(Common.CurrentUser.getPhone()).removeValue();
                    //Update new value
                    ratingTbl.child(Common.CurrentUser.getPhone()).setValue(rating);
                }
                else {
                    //Update new value
                    ratingTbl.child(Common.CurrentUser.getPhone()).setValue(rating);
                }
                Toast.makeText(FoodDetail.this, "Thank your for submit", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
