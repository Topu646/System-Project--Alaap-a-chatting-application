package com.example.alaap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.UUID;

public class EditProfile extends AppCompatActivity {

    String name,email,uid;
    Button editstatusbutton,changeprofilebutton;
    TextView nametextview,emailtextview,usernametextview,statustextview;

    ImageView imgprofile,header_img;
    EditText nameedittext,emailedittext,bioedittext,socialedittext;
    private Uri imagePath;
    FirebaseAuth mauth;
    DatabaseReference databaseReference,userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        mauth = FirebaseAuth.getInstance();
        uid = mauth.getCurrentUser().getUid();

//        nameeditbutton = findViewById(R.id.nameeditbtn);
//        emaileditbutton = findViewById(R.id.emaileditbtn);
//        bioeditbutton = findViewById(R.id.bioeditbtn);
//        socialimeditbutton = findViewById(R.id.socialeditbtn);


        imgprofile = findViewById(R.id.profilePicture);
        header_img = findViewById(R.id.roundImageView);

//        nameedittext = findViewById(R.id.nameidedit);
//        emailedittext = findViewById(R.id.emailidedit);
//        bioedittext = findViewById(R.id.bioidedit);
//        socialedittext = findViewById(R.id.socialidedit);

        changeprofilebutton = findViewById(R.id.changeProfilePictureButton);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            name = bundle.getString("name");
            email = bundle.getString("email");
        }
        nametextview.setText(name);
        emailtextview.setText(email);

        usernametextview.setText(name);
        statustextview.setText(email);

   userRef = FirebaseDatabase.getInstance().getReference().child("user").child(uid);;


        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String name = dataSnapshot.child("username").getValue(String.class);
                    String profilePictureUrl = dataSnapshot.child("profilePicture").getValue(String.class);

                    usernametextview.setText(name);
                    nametextview.setText(name);
                    if (profilePictureUrl != null && !profilePictureUrl.isEmpty()) {
                        Picasso.get().load(profilePictureUrl).into(imgprofile);
                        Picasso.get().load(profilePictureUrl).into(header_img);
                    }
                    else {

                    }

                } else {
                    // Handle the case where the user data doesn't exist
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors
            }
        });



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
                                    updateProfilePicture(task.getResult().toString());
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

        private void updateProfilePicture(String url) {
            FirebaseDatabase.getInstance().getReference("user/"+uid+"/profilePicture").setValue(url);

            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String name = dataSnapshot.child("username").getValue(String.class);
                        String profilePictureUrl = dataSnapshot.child("profilePicture").getValue(String.class);

                        usernametextview.setText(name);
                        nametextview.setText(name);
                        if (profilePictureUrl != null && !profilePictureUrl.isEmpty()) {
                            Picasso.get().load(profilePictureUrl).into(imgprofile);
                            Picasso.get().load(profilePictureUrl).into(header_img);
                        }
                        else {

                        }

                    } else {
                        // Handle the case where the user data doesn't exist
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle any errors
                }
            });
        }

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