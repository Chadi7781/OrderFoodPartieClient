package com.example.chadi.orderfood;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chadi.orderfood.Common.Common;
import com.example.chadi.orderfood.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;

public class SignIn extends AppCompatActivity {
  EditText edtPhone,edtPassword;
  Button btnSignIn;
  com.rey.material.widget.CheckBox ckbRemember;
  TextView txtForgotPwd;

  FirebaseDatabase database;
  DatabaseReference table_user;

  User user;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_sign_in);
    init();

    //init Paper
    Paper.init(this);

    //init firebase;
     database=FirebaseDatabase.getInstance();
     table_user=database.getReference("User");


    txtForgotPwd.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        showForgotDialog();
      }
    });
    btnSignIn.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View v) {
        if(Common.isConnectedToInternet(getBaseContext())) {

          //Save user && password
          if(ckbRemember.isChecked()){
            Paper.book().write(Common.USER_KEY,edtPhone.getText().toString());
            Paper.book().write(Common.PWD_KEY,edtPassword.getText().toString());
          }


          final AlertDialog alertDialog;
          alertDialog = new SpotsDialog.Builder().setContext(SignIn.this).build();
          alertDialog.setMessage("Please waiting...");
          alertDialog.show();
          table_user.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

              //CHECK IF USER NOT EXIST IN DATABASE
              if (dataSnapshot.child(edtPhone.getText().toString()).exists()) {

                //GET USER INFORMATION
                alertDialog.dismiss();
                User user = dataSnapshot.child(edtPhone.getText().toString()).getValue(User.class);
                user.setPhone(edtPhone.getText().toString()); //Set phone
                if (user.getPassword().equals(edtPassword.getText().toString())) {
                  Intent homeIntent = new Intent(SignIn.this, Home.class);
                  Common.CurrentUser = user;
                  startActivity(homeIntent);
                  finish();
                } else {
                  Toast.makeText(SignIn.this, "Wrong Password", Toast.LENGTH_SHORT).show();
                }
              } else {
                alertDialog.dismiss();
                Toast.makeText(SignIn.this, "User not exist in database", Toast.LENGTH_SHORT).show();

              }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
          });
        } else {
          Toast.makeText(SignIn.this, "Please check your connection !!", Toast.LENGTH_SHORT).show();
          return;
        }
        
      }
    });
  }

  private void showForgotDialog() {
    final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
    alertDialog.setTitle("Forgot Password");
    alertDialog.setMessage("Enter your secure code");

    LayoutInflater inflater = this.getLayoutInflater();
    View forgot_view = inflater.inflate(R.layout.forgot_password_layout,null);

    alertDialog.setView(forgot_view);
    alertDialog.setIcon(R.drawable.ic_security_black_24dp);

    final MaterialEditText edtPhone = (MaterialEditText)forgot_view.findViewById(R.id.edtPhone);
    final MaterialEditText edtSecureCode = (MaterialEditText)forgot_view.findViewById(R.id.editSecureCode);

    alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        //Check if user available
        table_user.addValueEventListener(new ValueEventListener() {
          @Override
          public void onDataChange(DataSnapshot dataSnapshot) {
             user = dataSnapshot.child(edtPhone.getText().toString())
                    .getValue(User.class);



            if(user.getSecureCode().equals(edtSecureCode.getText().toString()))
              Toast.makeText(SignIn.this ,"Your password :"+user.getPassword(),Toast.LENGTH_LONG).show();
            else
              Toast.makeText(SignIn.this, "Wrong secure code !", Toast.LENGTH_SHORT).show();
          }

          @Override
          public void onCancelled(DatabaseError databaseError) {

          }
        });

      }
    });

    alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {

      }
    });

    alertDialog.show();


  }


  private void init(){
    edtPassword=(MaterialEditText)findViewById(R.id.edtPassword);
    edtPhone=(MaterialEditText)findViewById(R.id.edtPhone);
    btnSignIn=(Button)findViewById(R.id.btnSignIn);
    ckbRemember=(com.rey.material.widget.CheckBox) findViewById(R.id.ckbRemeber);
    txtForgotPwd=(TextView) findViewById(R.id.txtForgotPwd);
  }
}