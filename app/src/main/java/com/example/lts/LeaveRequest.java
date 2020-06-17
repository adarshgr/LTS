package com.example.lts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.lts.BL.DAL;
import com.example.lts.ui.leave.LeaveModal;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LeaveRequest extends AppCompatActivity {
    Spinner spinner;
    EditText d1, d2, desc;
    String[] leaveTypes;
    Button save;
    DatabaseReference dbRef;
    Double alreadyTaken ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave_request);
        spinner = findViewById(R.id.spinner);
        d1 = findViewById(R.id.editText);
        setDate fromDate = new setDate(d1, getApplicationContext());
        d2 = findViewById(R.id.editText2);
        setDate toDate = new setDate(d2, getApplicationContext());
        desc = findViewById(R.id.txt_description);
        // Spinner Drop down elements
        leaveTypes = new String[]{"GENERAL LEAVE","EMERGENCY LEAVE" };

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, leaveTypes);

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
        save = findViewById(R.id.btn_save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fromDate = d1.getText().toString();
                String toDate = d2.getText().toString();
                String leaveType = spinner.getSelectedItem().toString();
                String description = desc.getText().toString();
                String status = "Pending";
                String currentDate=new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
                if(getDays(fromDate,currentDate)>60) {
                    if (validateDates(fromDate, toDate)) {
                        double days = getDays(fromDate, toDate);
                        if (days <= 30) {
                            if (validatePendingLeavesforYear(days, leaveType, fromDate))
                            {
                               dbRef = DAL.databaseReference("leave_request");
                                String req_id = dbRef.push().getKey();
                                dbRef.child(req_id).child("user_id").setValue(Login.userId);
                               dbRef.child(req_id).child("from_date").setValue(fromDate);
                               dbRef.child(req_id).child("to_date").setValue(toDate);
                               dbRef.child(req_id).child("leave_type").setValue(leaveType);
                               dbRef.child(req_id).child("description").setValue(description);
                             dbRef.child(req_id).child("status").setValue(status);
                               dbRef.child(req_id).child("days").setValue(days);
                               dbRef.child(req_id).child("entry_date").setValue(currentDate);

                             //   LeaveModal lm= new LeaveModal(Login.userId,fromDate,toDate,Integer.valueOf((int) days),currentDate,leaveType,status,description);
                                //----update leave counts
                               // Toast.makeText(getApplicationContext(), "Saved successfully!", Toast.LENGTH_SHORT).show();
                             updateLeaveCount(fromDate,leaveType,days);
                                onBackPressed();


                            }
                            else{
                                Toast.makeText(getApplicationContext(), "No pending leave for the year", Toast.LENGTH_SHORT).show();

                            }
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "Maximun 30 days of leave in single application", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Invalid dates!", Toast.LENGTH_LONG).show();
                    }
                }
                else{
                    Toast.makeText(getApplicationContext(), "You should apply before 60days!", Toast.LENGTH_LONG).show();
                }
            }
        });
        getSupportActionBar().hide();
    }

    public boolean validatePendingLeavesforYear(double cnt, String leaveType,String fromDate) {
        try {
            alreadyTaken=0.0;
            final String year =fromDate.split("/")[2];
        String userId=Login.userId;
        dbRef = DAL.databaseReference("leave_count");
        dbRef.orderByChild("user_id").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
              if(dataSnapshot.exists()) {
                  for (DataSnapshot ds : dataSnapshot.getChildren()) {
                      String ds_year = ds.child("year").getValue().toString();

                      if (ds_year.equals(year.toString())) {
                          alreadyTaken = ds.child("count").getValue(Double.class);

                      }

                  }
              }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        double leave_limit = 60.0;
        if (alreadyTaken + cnt > leave_limit)
        {
           // Toast.makeText(getApplicationContext(),"Total:"+String.valueOf(alreadyTaken + cnt ),Toast.LENGTH_LONG).show();
           return false;
        }
        else
            {
               // Toast.makeText(getApplicationContext(),"Total:"+String.valueOf(alreadyTaken + cnt ),Toast.LENGTH_LONG).show();
            return true;
        }
        } catch (Exception e1) {

            return false;
        }
    }
   public boolean validateDates(String fromDate, String toDate)
   {
       try {
       Date date1 = null;
       Date date2 = null;
           Date date3 = null;
       SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
       String currentDate=new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());

       date1 = df.parse(fromDate);
       date2 = df.parse(toDate);
       date3=df.parse(currentDate);
       if (date2.before(date1)) {
          return  false;

       }
       if(date1.getYear()!=date3.getYear())
       {
           return  false;
       }
           if(date1.getYear()!=date2.getYear())
           {
               return  false;
           }
       return  true;
   } catch (Exception e1) {

     return false;
   }
   }
    public double getDays(String fromDate, String toDate) {
        try {

            Date date1 = null;
            Date date2 = null;
            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
            date1 = df.parse(toDate);
            date2 = df.parse(fromDate);

            double diff = Math.abs(date1.getTime() - date2.getTime());
            double diffDays = diff / (24 * 60 * 60 * 1000);

            return  diffDays+1;

        } catch (Exception e1) {
            Toast.makeText(getApplicationContext(),e1.getMessage(),Toast.LENGTH_LONG).show();
            return 0;
        }

    }
    public  void updateLeaveCount(String fromDate, String leaveType, double cnt)
    {
        try {
            final String leave_type = leaveType;
            final double count = cnt;

            final String year =fromDate.split("/")[2];
            dbRef = DAL.databaseReference("leave_count");

            // Query query = dbRef.orderByChild("user_id").equalTo(Login.userId);
             dbRef.orderByChild("user_id").equalTo(Login.userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists())
                    {
                        for (DataSnapshot ds : dataSnapshot.getChildren())
                        {
                            String ds_year=ds.child("year").getValue(String.class);
                            String key=ds.getKey();
                            if(ds_year.equals(year)) {
                                Double alreadyTaken =ds.child("count").getValue(Double.class);
                                dbRef.child(key).child("count").setValue(String.valueOf(alreadyTaken + count));
                            }
                            else{
                                String id = dbRef.push().getKey();
                                dbRef.child(id).child("user_id").setValue(Login.userId);
                                dbRef.child(id).child("year").setValue(year);
                                dbRef.child(id).child("count").setValue(count);
                            }
                        }


                    }
                    else{
                         String id = dbRef.push().getKey();
                        dbRef.child(id).child("user_id").setValue(Login.userId);
                        dbRef.child(id).child("year").setValue(year);
                        dbRef.child(id).child("count").setValue(count);

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