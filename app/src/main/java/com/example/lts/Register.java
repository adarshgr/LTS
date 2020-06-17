package com.example.lts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.lts.BL.DAL;
import com.example.lts.BL.UserProfile;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class Register extends AppCompatActivity  implements View.OnClickListener{
    Button reg;
    Spinner spinner;
    EditText username,password,confirm_pass,name,mobile,address;
    DatabaseReference dbRef,profileRef;
    String role;
    static String user_id;   String[] roles;
    private static final int PICK_IMAGE_REQUEST = 234;
    StorageReference mStorageRef;
    private ImageView imageView;

    //a Uri object to store file path
    private Uri filePath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        reg=findViewById(R.id.btn_register);
        username=findViewById(R.id.txt_username);
        password=findViewById(R.id.txt_password);
        confirm_pass=findViewById(R.id.txt_password_confirm);
        name=findViewById(R.id.txt_name);
        mobile=findViewById(R.id.txt_mobile_no);
        address=findViewById(R.id.txt_address);
        spinner=findViewById(R.id.spn_roles);
        roles = new String[]{"DRIVER", "MANAGER"};


          ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, roles);

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
        user_id="";
        role=spinner.getSelectedItem().toString();
        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               if (!username.getText().toString().isEmpty() && !password.getText().toString().isEmpty() && !confirm_pass.getText().toString().isEmpty()) {
                    if (!password.getText().toString().equals(confirm_pass.getText().toString())) {
                        Toast.makeText(getApplicationContext(), "passwords doesnot match!", Toast.LENGTH_LONG).show();
                    } else {

                         dbRef = DAL.databaseReference("users");

                        dbRef.orderByChild("user_name").equalTo(username.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener(){
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                if(!dataSnapshot.exists())
                                {
                                    user_id= dbRef.push().getKey();

                                    dbRef.child(user_id).child("user_name").setValue(username.getText().toString());
                                    dbRef.child(user_id).child("password").setValue(password.getText().toString());
                                    dbRef.child(user_id).child("role").setValue(role);
                                    Toast.makeText(getApplicationContext(),"User created successfully!",Toast.LENGTH_LONG).show();
                                    username.setText("");
                                    password.setText("");
                                    confirm_pass.setText("");
                                    uploadFile(user_id);
                                }
                                else{
                                        Toast.makeText(getApplicationContext(), "Username already exists!!", Toast.LENGTH_SHORT).show();

                                    }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Toast.makeText(getApplicationContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();

                            }

                        });

                        profileRef=DAL.databaseReference("user_profile");
                        profileRef.orderByChild("user_id").equalTo(user_id).addListenerForSingleValueEvent(new ValueEventListener(){
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                if(!dataSnapshot.exists())
                                {
                                    String id = profileRef.push().getKey();
                                    profileRef.child(id).child("user_id").setValue(user_id);
                                    profileRef.child(id).child("name").setValue(name.getText().toString());
                                    profileRef.child(id).child("mobile").setValue(mobile.getText().toString());
                                    profileRef.child(id).child("address").setValue(address.getText().toString());
                                    name.setText("");
                                    mobile.setText("");
                                    address.setText("");
                                }
                                else{
                                    Toast.makeText(getApplicationContext(), "Username already exists!!", Toast.LENGTH_SHORT).show();

                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Toast.makeText(getApplicationContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();

                            }

                        });

                    }

                }
            }
        });
          imageView = (ImageView) findViewById(R.id.imgView);

           imageView.setOnClickListener(this);
        getSupportActionBar().hide();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);

            } catch (IOException e) {
               Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
            }
        }
    }
    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onClick(View v) {
        if (v == imageView) {
            showFileChooser();
        }

    }
    private void uploadFile(String user_id) {
        //if there is a file to upload
        if (filePath != null) {
          String file_name=user_id+".jpg";
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading");
            progressDialog.show();
            mStorageRef = FirebaseStorage.getInstance().getReference();
            StorageReference imgRef = mStorageRef.child("Uploads").child(file_name);
            imgRef.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            progressDialog.dismiss();

                            Toast.makeText(getApplicationContext(), "File Uploaded ", Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            //if the upload is not successfull
                            //hiding the progress dialog
                            progressDialog.dismiss();

                            //and displaying error message
                            Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            //calculating progress percentage
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                            //displaying percentage in progress dialog
                            progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                        }
                    });
        }
        //if there is not any file
        else {
            //you can display an error toast
        }
    }
}
