package com.example.alaap;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.UUID;

public class EditProfile extends AppCompatActivity {

    String name,email,uid;
    String editedname,edittedemail,editedbio;
    Button editstatusbutton,changeprofilebutton;
    TextView emailtextview,usernametextview,uppernametextview, upperemailtextview,biotextview;

    ImageView imgprofile,header_img;

    ImageView nameeditimageview,emaileditimageview,bioeditimageview;
    EditText nameedittext,emailedittext,bioedittext,socialedittext;
    private Uri imagePath;
    FirebaseAuth mauth;
    FirebaseFirestore db;
    DatabaseReference databaseReference;
   // StorageReference userRef;
   DocumentReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        mauth = FirebaseAuth.getInstance();
        uid = mauth.getCurrentUser().getUid();

        usernametextview = findViewById(R.id.usernameid);
        emailtextview = findViewById(R.id.statusid);
        uppernametextview = findViewById(R.id.nametextid);
        upperemailtextview =findViewById(R.id.emailtextid);
        biotextview = findViewById(R.id.bioid);

       nameeditimageview = findViewById(R.id.nameeditbtn);
       emaileditimageview = findViewById(R.id.emaileditbtn);
       bioeditimageview = findViewById(R.id.bioeditbtn);

       nameedittext = findViewById(R.id.nameedittext);
       emailedittext = findViewById(R.id.emailedittext);
       bioedittext = findViewById(R.id.bioedittext);


        imgprofile = findViewById(R.id.profilePicture);
        header_img = findViewById(R.id.roundImageView);


        nameeditimageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nameedittext.setVisibility(View.VISIBLE);
                usernametextview.setText("");
                editedname = nameedittext.getText().toString().trim();
            }
        });

        emaileditimageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emailedittext.setVisibility(View.VISIBLE);
                edittedemail = emailedittext.getText().toString().trim();
                emailtextview.setText("");
            }
        });

        bioeditimageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bioedittext.setVisibility(View.VISIBLE);
                biotextview.setText("");
                editedbio = bioedittext.getText().toString().trim();
            }
        });



        changeprofilebutton = findViewById(R.id.changeProfilePictureButton);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            name = bundle.getString("name");
            email = bundle.getString("email");
        }
        //nametextview.setText(name);
        //emailtextview.setText(email);

        usernametextview.setText(name);
        emailtextview.setText(email);
        upperemailtextview.setText(email);
        uppernametextview.setText(name);
    //    userRef = FirebaseDatabase.getInstance().getReference().child("user").child(uid);

        db = FirebaseFirestore.getInstance();
        userRef = db.collection("users").document(uid);


        userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
//                    String name = documentSnapshot.getString("name");
//                    String profilePictureUrl = documentSnapshot.getString("image");

                    String name = documentSnapshot.getString("name");
                    String profilePictureUrl = documentSnapshot.getString("image");

                    // Check if the profilePictureUrl is not null or empty
                    if (profilePictureUrl != null && !profilePictureUrl.isEmpty()) {
                        // Use Picasso to load and display the profile picture
                        Picasso.get().load(profilePictureUrl).into(imgprofile);
                        Picasso.get().load(profilePictureUrl).into(header_img);
                    } else {
                        // Handle the case where the profile picture URL is missing
                    }
                } else {
                    // Handle the case where the user document doesn't exist
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Handle any errors that may occur during the Firestore query
                Log.e(TAG, "Error retrieving user document: " + e.getMessage());

            }
        });

//        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if (dataSnapshot.exists()) {
//                    String name = dataSnapshot.child("username").getValue(String.class);
//                    String profilePictureUrl = dataSnapshot.child("profilePicture").getValue(String.class);
//
//                    //usernametextview.setText(name);
//
//                    if (profilePictureUrl != null && !profilePictureUrl.isEmpty()) {
//                        Picasso.get().load(profilePictureUrl).into(imgprofile);
//                        Picasso.get().load(profilePictureUrl).into(header_img);
//                    }
//                    else {
//
//                    }
//
//                } else {
//                    // Handle the case where the user data doesn't exist
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                // Handle any errors
//            }
//        });



        changeprofilebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoIntent = new Intent(Intent.ACTION_PICK);
                photoIntent.setType("image/*");
                startActivityForResult(photoIntent, 1);
            }
        });



    }


        private void uploadImage() {
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading....");
            progressDialog.show();

            FirebaseStorage.getInstance().getReference("images/"+ UUID.randomUUID().toString()).putFile(imagePath).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()){
                        task.getResult().getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()){
                                    //updateProfilePicture(task.getResult().toString());
                                }

                            }

                        });
                        Toast.makeText(EditProfile.this, "Image Uploaded", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(EditProfile.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                    progressDialog.dismiss();
                }

            });
        }

//        private void updateProfilePicture(String url) {
//            FirebaseDatabase.getInstance().getReference("user/"+uid+"/profilePicture").setValue(url);
//
//            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    if (dataSnapshot.exists()) {
//                        String name = dataSnapshot.child("username").getValue(String.class);
//                        String profilePictureUrl = dataSnapshot.child("profilePicture").getValue(String.class);
//
//                        usernametextview.setText(name);
//                        usernametextview.setText(name);
//                        if (profilePictureUrl != null && !profilePictureUrl.isEmpty()) {
//                            Picasso.get().load(profilePictureUrl).into(imgprofile);
//                            Picasso.get().load(profilePictureUrl).into(header_img);
//                        }
//                        else {
//
//                        }
//
//                    } else {
//                        // Handle the case where the user data doesn't exist
//                    }
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//                    // Handle any errors
//                }
//            });
//        }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data!=null){
            imagePath = data.getData();
            getImageInImageView();

            uploadImage();
        }
    }

    private void getImageInImageView() {

        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),imagePath);
        }
        catch (IOException e){
            e.printStackTrace();
        }

        imgprofile.setImageBitmap(bitmap);
    }
}