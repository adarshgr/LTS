package com.example.lts.ui.home;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.lts.BL.DAL;
import com.example.lts.LeaveRequest;
import com.example.lts.Login;
import com.example.lts.R;
import com.example.lts.Register;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import static android.content.Context.MODE_PRIVATE;

public class HomeFragment extends Fragment {
    ImageView iv;
    FloatingActionButton reg;
    TextView username,mobile,address,name,logout;
    DatabaseReference dbRef,profileRef;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        final View root = inflater.inflate(R.layout.fragment_home, container, false);
        name=root.findViewById(R.id.txt_name);
        username=root.findViewById(R.id.txt_username);
        mobile=root.findViewById(R.id.txt_mobile);
        address=root.findViewById(R.id.txt_address);
        iv=root.findViewById(R.id.img_profilepic);

       // else{
       //     reg.setVisibility(View.INVISIBLE);
       // }
       // StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl("src=\"https://firebasestorage.googleapis.com/v0/b/edriver-2df4d.appspot.com/o/Uploads%2Fpic.jpg?alt=media&token=927a7f37-4144-4948-a8c4-f0b7279d0e3a\"");
//gs://edriver-2df4d.appspot.com/Uploads/-M3Pj5drsMEFlmP4V3HC.jpg
       // String url="https://firebasestorage.googleapis.com/v0/b/edriver-2df4d.appspot.com/o/Uploads%2F-M2ptXcCTkCwuftcetVy.png?alt=media&token=6993e0e7-5372-4680-8d50-076e2ef90282";
    String profile_pic=Login.userId+".jpg";
      String url="https://firebasestorage.googleapis.com/v0/b/edriver-2df4d.appspot.com/o/Uploads%2F"+profile_pic+"?alt=media&token=6993e0e7-5372-4680-8d50-076e2ef90282";
        Toast.makeText(getContext(),Login.userId,Toast.LENGTH_LONG).show();
        Glide.with(getContext()).load(url).into(iv);
        profileRef = DAL.databaseReference("user_profile");
        profileRef.orderByChild("user_id").equalTo(Login.userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    for(DataSnapshot profile : dataSnapshot.getChildren())
                    {
                        name.setText(profile.child("name").getValue(String.class));
                        mobile.setText(profile.child("mobile").getValue(String.class));
                        address.setText(profile.child("address").getValue(String.class));

                        dbRef = DAL.databaseReference("users");
                        dbRef.orderByKey().equalTo(Login.userId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()) {
                                    for(DataSnapshot user : dataSnapshot.getChildren()) {
                                        username.setText(user.child("user_name").getValue(String.class));

                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return root;
    }

}