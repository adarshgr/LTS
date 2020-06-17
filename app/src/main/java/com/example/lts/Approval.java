package com.example.lts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lts.BL.DAL;
import com.example.lts.ui.leave.LeaveFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;

public class Approval extends AppCompatActivity {
    TextView t5,t3,t2,t1,t4,t6,t7,t8;
    DatabaseReference dbRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approval);
        Intent intent = getIntent();
        CustomList po=(CustomList)intent.getExtras().getSerializable("CustomList");
         t1=(TextView)findViewById(R.id.txt_username);
         t2=(TextView)findViewById(R.id.txt_fromdate);
         t3=(TextView)findViewById(R.id.txt_toDate);
         t4=(TextView)findViewById(R.id.txt_appliedon);
        t5=(TextView)findViewById(R.id.txt_id);
        t6=(TextView)findViewById(R.id.txt_leaveType);
        t7=(TextView)findViewById(R.id.txt_days);
        t8=(TextView)findViewById(R.id.txt_description);
        t1.setText(po.username.toString());
        t2.setText("From Date : "+po.fromDate.toString() );
        t3.setText("To Date : "+ po.toDate.toString());
       t4.setText( "Applied on : "+ po.entryDate);
        t5.setText( po.id);
        t6.setText("Leave Type : "+po.leaveType );
       t7.setText("Days : "+String.valueOf( po.daysCount));
        t8.setText( "Deatils : "+ po.description);

        Button approve=(Button) findViewById(R.id.btn_approve);
        Button reject=(Button) findViewById(R.id.btn_reject);
        getSupportActionBar().hide();
        approve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //approval here....
               final String Id= t5.getText().toString();
               dbRef= DAL.databaseReference("leave_request");
                dbRef.child(Id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists())
                        {

                            dbRef.child(Id).child("status").setValue("APPROVED");

                            onBackPressed();

                        }
                        else{
                            Toast.makeText(getApplicationContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                                                                                                                      }



        });
        reject.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //REJECTION here....
                final String Id= t5.getText().toString();
                dbRef= DAL.databaseReference("leave_request");
                dbRef.child(Id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists())
                        {

                            dbRef.child(Id).child("status").setValue("REJECTED");
                            updateLeaveCount(dataSnapshot.child("user_id").getValue(String.class),dataSnapshot.child("from_date").getValue(String.class),dataSnapshot.child("leave_type").getValue(String.class),dataSnapshot.child("days").getValue(Double.class));
                            onBackPressed();
                        }
                        else{
                            Toast.makeText(getApplicationContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


        }
        });
    }
    public  void updateLeaveCount(String userId,String fromDate, String leaveType, double cnt)
    {
        try {
            final String user_id=userId;
            final String leave_type = leaveType;
            final double count = cnt;
            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");

            final String year =fromDate.split("/")[2];
            dbRef = DAL.databaseReference("leave_count");
            // Query query = dbRef.orderByChild("user_id").equalTo(Login.userId);
            dbRef.orderByChild("user_id").equalTo(user_id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists())
                    {

                        for (DataSnapshot ds : dataSnapshot.getChildren())
                        {
                            String ds_year=ds.child("year").getValue().toString();
                            String key=ds.getKey();
                            if(ds_year.equals(year.toString())) {
                                String alreadyTaken =ds.child("count").getValue(String.class);
                                Toast.makeText(getApplicationContext(),alreadyTaken,Toast.LENGTH_LONG).show();
                                dbRef.child(key).child("count").setValue(String.valueOf(Double.valueOf(alreadyTaken )- count));
                            }

                        }


                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        catch (Exception e)
        {

        }
    }
}
