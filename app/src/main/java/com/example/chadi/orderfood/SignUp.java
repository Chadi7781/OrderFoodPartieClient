package com.example.chadi.orderfood;

import android.app.AlertDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.chadi.orderfood.Common.Common;
import com.example.chadi.orderfood.Model.User;
import com.example.chadi.orderfood.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import dmax.dialog.SpotsDialog;

public class SignUp extends AppCompatActivity {
  MaterialEditText edtPhone,edtName,edtPassword,edtSecureCode;
  Button btnSignUp;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_sign_up);
    init();
    //init firebase;
    final FirebaseDatabase database=FirebaseDatabase.getInstance();
    final DatabaseReference table_user=database.getReference("User");

    btnSignUp.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if(Common.isConnectedToInternet(getBaseContext())) {
          final AlertDialog alertDialog;
          alertDialog = new SpotsDialog.Builder().setContext(SignUp.this).build();
          alertDialog.setMessage("Please waiting...");
          alertDialog.show();

          table_user.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
              //check if aleardy user phone
              if (dataSnapshot.child(edtPhone.getText().toString()).exists()) {
                alertDialog.dismiss();
                Toast.makeText(SignUp.this, "Phone number already register", Toast.LENGTH_SHORT).show();
              } else {
                alertDialog.dismiss();
                User user = new User(edtName.getText().toString(), edtPassword.getText().toString()
                                    ,edtSecureCode.getText().toString());
                table_user.child(edtPhone.getText().toString()).setValue(user);
                Toast.makeText(SignUp.this, "Sign Up successfuly", Toast.LENGTH_SHORT).show();
                finish();
              }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
          });
        }else {
          Toast.makeText(SignUp.this, "Please check your connection !!", Toast.LENGTH_SHORT).show();
          return;
        }

      }
    });
  }

















  private void init() {
    edtName=findViewById(R.id.editName);
    edtPhone=findViewById(R.id.editPhone);
    edtPassword=findViewById(R.id.editPassword);
    btnSignUp=findViewById(R.id.btnSignUp);
    edtSecureCode=findViewById(R.id.editSecureCode);

  }


}
