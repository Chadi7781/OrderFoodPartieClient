package com.example.chadi.orderfood.Databases;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

import com.example.chadi.orderfood.Model.Order;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class Database extends SQLiteAssetHelper {
    private static final String DB_NAME="EatItDB.db";
    private static final int DB_VER=1;
    private Context ctx;
    private static final  String Order_Detail="OrderDetail";
   private String databasePath;

    SQLiteDatabase db;
    String dirPath="";

    public Database(Context ctx) {
        super(ctx, DB_NAME, null, DB_VER);
        this.ctx=ctx;
        //Config();
    }
//    public void Config( ){
//        dirPath="/data/data/"+Database.this.ctx.getPackageName()+"/databases/EatItDB.db";
//        db=SQLiteDatabase.openOrCreateDatabase(dirPath , null );
//        String dirPath=Environment.getExternalStorageDirectory().getAbsolutePath();
//        File dir=new File(dirPath);
//        if(!dir.exists())
//            dir.mkdirs ();
//        db=SQLiteDatabase.openOrCreateDatabase(dirPath,null);
//    }

    public List<Order>getCarts(){
        SQLiteDatabase db;

         db=this.getReadableDatabase();
        SQLiteQueryBuilder qb=new SQLiteQueryBuilder();
        String[] sqlSelect={"productId","productName","quantity","price","discount"};
        String sqlTable="OrderD";
        qb.setTables(sqlTable);
        Cursor c=qb.query(db,sqlSelect,null,null,null,null,null);

        final List<Order>result=new ArrayList<>();
        if(c.moveToFirst()){
            do{
                result.add(new Order(c.getString(c.getColumnIndex("productId")),
                                    c.getString(c.getColumnIndex("productName")),
                                    c.getString(c.getColumnIndex("quantity")),
                                    c.getString(c.getColumnIndex("price")),
                                    c.getString(c.getColumnIndex("discount"))
                                    ));
            }while (c.moveToNext());
        }
        return result;
    }

    public void addToCart(Order order){
       // Config();
     //dirPath="/data/data/"+this.ctx.getPackageName()+"/databases/EatItDB.db";
       // dirPath = "/assets/databases/EatItDB.db";
       // db=SQLiteDatabase.openOrCreateDatabase(dirPath , null );
       //String dirPath=Environment.getExternalStorageDirectory().getAbsolutePath();
       try {
           File dir = new File(dirPath);
           if (!dir.exists())
               dir.mkdirs();
          SQLiteDatabase db = getReadableDatabase();
//           String req = "INSERT INTO  OrderD (productId,productName,quantity,price,discount) VALUES ('%s','%s','%s','%s','%s');";
           String query = String.format("INSERT INTO OrderD(productId,productName,quantity,price,discount) VALUES('%s','%s','%s','%s','%s');"
                   ,order.getProductId(),
                   order.getProductName(),
                   order.getQuantity(),
                   order.getPrice(),
                   order.getDiscount());
           db.execSQL(query);
           db.close();
       }catch (Exception ex){
           Toast.makeText(ctx.getApplicationContext(), "Hello "+ex.getMessage(), Toast.LENGTH_LONG).show();
       }
    }




    public void cleanCart() {

        SQLiteDatabase db=getReadableDatabase();
        String query=String.format("DELETE FROM OrderD");
        db.execSQL(query);
        db.close();
    }



    //Favorites
    public void addToFavorites(String FoodId){
        File dir = new File(dirPath);
        if (!dir.exists())
            dir.mkdirs();
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("INSERT INTO Favorites(foodId) VALUES('%s');",FoodId);
        db.execSQL(query);
        db.close();
    }

    public void removeFromFavorites(String FoodId){
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("DELETE FROM Favorites WHERE foodId='%s';",FoodId);
        db.execSQL(query);
    }

    public boolean isFavorite(String FoodId){
        SQLiteDatabase db =getReadableDatabase();

        String query = String.format("SELECT * FROM Favorites WHERE foodId='%s';",FoodId);
        Cursor cursor =db.rawQuery(query,null);

        if(cursor.getCount() <= 0){
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }






    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onUpgrade(db, oldVersion, newVersion);
    }


}

