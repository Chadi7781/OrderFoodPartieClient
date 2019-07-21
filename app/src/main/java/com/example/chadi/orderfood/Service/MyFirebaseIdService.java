package com.example.chadi.orderfood.Service;

import com.example.chadi.orderfood.Common.Common;
import com.example.chadi.orderfood.Model.Token;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class MyFirebaseIdService  extends FirebaseInstanceIdService {


    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String tokenRefreshed =FirebaseInstanceId.getInstance().getToken();
        if(Common.CurrentUser != null)
        updateTokenToFirbase(tokenRefreshed);
    }

    private void updateTokenToFirbase(String tokenRefreshed) {
        FirebaseDatabase db =FirebaseDatabase.getInstance();
        DatabaseReference tokens = db.getReference("Tokens");

        Token token = new Token(tokenRefreshed ,false);//false because this token send from client app
        tokens.child(Common.CurrentUser.getPhone()).setValue(token);
    }
}
