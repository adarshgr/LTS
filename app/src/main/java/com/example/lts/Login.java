package com.example.lts;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.lts.BL.DAL;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {
    EditText txt_user_name,txt_password;
    Button login;
     public static String userId;
    public static String role;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();
        checkLogin();

        txt_user_name=findViewById(R.id.txt_username);
        txt_password=findViewById(R.id.txt_password);
        login=findViewById(R.id.btn_login);

            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    DatabaseReference dbRef = DAL.db().getReference("users");

                    dbRef.orderByChild("user_name").equalTo(txt_user_name.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener(){
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(!dataSnapshot.exists())
                            {     Toast.makeText(getApplicationContext(), "Invalid username or password!", Toast.LENGTH_SHORT).show();


                            }
                            for(DataSnapshot datas: dataSnapshot.getChildren()){

                                String keys=datas.child("password").getValue().toString();
                                String role=datas.child("role").getValue().toString();
                                String userId=datas.getKey();
                                if(keys.equals(txt_password.getText().toString()))
                                {
                                    Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();

                                    SQLiteDatabase mydatabase = openOrCreateDatabase("driver_db",MODE_PRIVATE,null);
                                    mydatabase.execSQL("DROP TABLE IF EXISTS login");
                                    mydatabase.execSQL("CREATE TABLE IF NOT EXISTS login(user_id VARCHAR,status VARCHAR,role VARCHAR);");

                                    ContentValues values = new ContentValues();
                                    values.put("user_id", userId);
                                    values.put("status", "logged_in");
                                    values.put("role", role);
// Inserting Row
                                    mydatabase.insert("login", null, values);
                                   // mydatabase.execSQL("INSERT INTO login VALUES(userId,'logged_in',role)");
                                    checkLogin();
                                }

                                else{
                                    Toast.makeText(getApplicationContext(), "Invalid username or password!", Toast.LENGTH_SHORT).show();

                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(getApplicationContext(), "Invalid username or password!", Toast.LENGTH_SHORT).show();

                        }

                    });

                    // Intent i =new Intent(getApplicationContext(),MainActivity.class);
                 //   startActivity(i);
                }
            });

    }
    void checkLogin()
    {

        SQLiteDatabase mydatabase = openOrCreateDatabase("driver_db",MODE_PRIVATE,null);
        mydatabase.execSQL("CREATE TABLE IF NOT EXISTS login(user_id VARCHAR,status VARCHAR,role VARCHAR);");

        Cursor resultSet = mydatabase.rawQuery("Select * from login",null);
        if(resultSet.getCount()>0)
        {
            resultSet.moveToFirst();
            String login_status = resultSet.getString(1);

            if (login_status.equals("logged_in")) {
                userId=resultSet.getString(0);
                role=resultSet.getString(2);
             //   Toast.makeText(getApplicationContext(), role, Toast.LENGTH_SHORT).show();
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            }
        }
    }

}
