package com.example.lts.ui.leave;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.lts.Approval;
import com.example.lts.BL.DAL;
import com.example.lts.CustomList;
import com.example.lts.CustomListAdapter;
import com.example.lts.LeaveRequest;
import com.example.lts.Login;
import com.example.lts.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LeaveFragment extends Fragment {
    private CustomListAdapter listAdapter;
    DatabaseReference dbRef;
    List<CustomList> list ;
    CustomList[] items =new CustomList[]{} ;
    ListView listView;
     static String username;
    Query query;

    public LeaveFragment() {
    }
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
         View root = inflater.inflate(R.layout.fragment_leave, container, false);
        listView = (ListView) root.findViewById(R.id.lv_custom_list);
        Toast.makeText(getContext(),Login.userId,Toast.LENGTH_LONG).show();
        final DatabaseReference dbRef = DAL.db().getReference("leave_request");

        if(Login.role.equals("driver")) {

            dbRef.orderByChild("user_id").equalTo(Login.userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        if (dataSnapshot.getChildrenCount() > 0) {

                         final   ArrayList<CustomList> newObj = new ArrayList<CustomList>(Arrays.asList(items));
                            for (final DataSnapshot ds : dataSnapshot.getChildren()) {
                                String userId = ds.child("user_id").getValue(String.class);

                                DatabaseReference newRef = DAL.db().getReference("user_profile");
                                Query q = newRef.orderByChild("user_id").equalTo(userId);
                                q.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for (DataSnapshot profile : dataSnapshot.getChildren()) {
                                            String uri = "@drawable/"+ds.child("status").getValue(String.class).toLowerCase();
                                            Toast.makeText(getContext(),uri,Toast.LENGTH_LONG).show();
                                            int imageResource = getResources().getIdentifier(uri, null,getContext().getPackageName());

                                            username = profile.child("name").getValue(String.class);
                                            CustomList listitem = new CustomList(ds.getKey(), username, ds.child("from_date").getValue(String.class), ds.child("to_date").getValue(String.class), ds.child("days").getValue(double.class),
                                                    ds.child("entry_date").getValue(String.class), ds.child("status").getValue(String.class), ds.child("leave_type").getValue(String.class), ds.child("description").getValue(String.class), imageResource);
                                            newObj.add(listitem);
                                            listAdapter = new CustomListAdapter(getActivity(), newObj);
                                            listView.setAdapter(listAdapter);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });



                            }
                            listAdapter = new CustomListAdapter(getActivity(), newObj);
                            listView.setAdapter(listAdapter);
                        }

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        else
            {
                dbRef.orderByChild("status").equalTo("Pending").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            if (dataSnapshot.getChildrenCount() > 0) {

                                final ArrayList<CustomList> newObj = new ArrayList<CustomList>(Arrays.asList(items));
                                for (final DataSnapshot ds : dataSnapshot.getChildren()) {
                                    String userId = ds.child("user_id").getValue(String.class);

                                    DatabaseReference newRef = DAL.db().getReference("user_profile");
                                    Query q = newRef.orderByChild("user_id").equalTo(userId);
                                    q.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            for (DataSnapshot profile : dataSnapshot.getChildren()) {
                                                String uri = "@drawable/"+ds.child("status").getValue(String.class).toLowerCase();
                                              Toast.makeText(getContext(),uri,Toast.LENGTH_LONG).show();
                                                int imageResource = getResources().getIdentifier(uri, null,getContext().getPackageName());

                                                username = profile.child("name").getValue(String.class);
                                                CustomList listitem = new CustomList(ds.getKey(), username, ds.child("from_date").getValue(String.class), ds.child("to_date").getValue(String.class), ds.child("days").getValue(double.class),
                                                        ds.child("entry_date").getValue(String.class), ds.child("status").getValue(String.class), ds.child("leave_type").getValue(String.class), ds.child("description").getValue(String.class), imageResource);
                                                newObj.add(listitem);
                                                listAdapter = new CustomListAdapter(getActivity(), newObj);
                                                listView.setAdapter(listAdapter);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });



                                }
                                listAdapter = new CustomListAdapter(getActivity(), newObj);
                                listView.setAdapter(listAdapter);
                            }

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
        }

       // listAdapter = new CustomListAdapter(getActivity(), Arrays.asList(items));
       // listView.setAdapter(listAdapter);
      //  if(!Login.role.equals("driver")) {

            Toast.makeText(getContext(),"Not driver",Toast.LENGTH_LONG).show();
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent i = new Intent(getContext(), Approval.class);
                    CustomList c = listAdapter.getItem(position);
                    i.putExtra("CustomList", (Serializable) c);
                    startActivity(i);
                }

            });
      //  }
        // Get a reference to the ListView, and attach this adapter to it.

        FloatingActionButton fab = (FloatingActionButton) root.findViewById(R.id.fab);
       if(Login.role.equals("driver")) {
           fab.setVisibility(View.VISIBLE);
           fab.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   Intent i = new Intent(view.getContext(), LeaveRequest.class);
                   startActivity(i);
               }
           });
       }
       else{
           fab.setVisibility(View.INVISIBLE);
       }
        return root;
    }
}