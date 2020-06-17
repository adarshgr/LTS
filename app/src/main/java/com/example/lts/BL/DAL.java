package com.example.lts.BL;

import android.database.sqlite.SQLiteDatabase;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public  class DAL {
    public static   FirebaseDatabase database;

         public static FirebaseDatabase db()
         {
             FirebaseDatabase database = FirebaseDatabase.getInstance();
             return  database;
         }
        public static DatabaseReference databaseReference(String tableName)
        {
            DatabaseReference dbRef = db().getReference(tableName);
        return  dbRef;
        }


}
