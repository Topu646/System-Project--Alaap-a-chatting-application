package com.example.alaap;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.UUID;

public class EditProfile extends AppCompatActivity {

    String name,email,uid,bio;
    private String encodedImage;
    String editedname,edittedemail,editedbio;
    Button editstatusbutton,changeprofilebutton;
    TextView emailtextview,usernametextview,uppernametextview, upperemailtextview,biotextview;

    ImageView imgprofile,header_img;

    FirebaseFirestore firebaseFirestore;

    ImageView nameeditimageview,emaileditimageview,bioeditimageview;

    private Bitmap selectedimagebitmap;
    private String selectedimagebase64;

    private ImageButton backbutton;

    EditText nameedittext,emailedittext,bioedittext,socialedittext;
    private Uri imagePath;
    FirebaseAuth mauth;
    FirebaseFirestore db;
    DatabaseReference databaseReference;
   // StorageReference userRef;
   DocumentReference userRef;

    @SuppressLint("MissingInflatedId")
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

        editstatusbutton = findViewById(R.id.editstatusbtn);

        imgprofile = findViewById(R.id.profilePicture);
        header_img = findViewById(R.id.roundImageView);




        firebaseFirestore = FirebaseFirestore.getInstance();
        DocumentReference documentReference = firebaseFirestore.collection("users").document(uid);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                 usernametextview.setText(documentSnapshot.getString("name"));
                 biotextview.setText(documentSnapshot.getString("bio"));
                 emailtextview.setText(documentSnapshot.getString("email"));
                 upperemailtextview.setText(documentSnapshot.getString("email"));
                 uppernametextview.setText(documentSnapshot.getString("name"));

                String imagestring = documentSnapshot.getString("image");

                if (imagestring != null) {
                    byte[] bytes = Base64.decode(imagestring, Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    imgprofile.setImageBitmap(bitmap);
                    header_img.setImageBitmap(bitmap);
                }
            }
        });



        backbutton = findViewById(R.id.back);
        backbutton.setOnClickListener(view -> {
            getOnBackPressedDispatcher().onBackPressed();
        });


        editstatusbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name,bio;
                name = usernametextview.getText().toString().trim();
                bio = biotextview.getText().toString().trim();
                Intent intent = new Intent(EditProfile.this,EditStatus.class);
                intent.putExtra("name",name);
                intent.putExtra("bio",bio);
                startActivity(intent);
            }
        });



        changeprofilebutton = findViewById(R.id.changeProfilePictureButton);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            name = bundle.getString("name");
            email = bundle.getString("email");
            bio = bundle.getString("bio");
            biotextview.setText(bio);
        }


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
                       // Picasso.get().load(profilePictureUrl).into(imgprofile);
                       //
                        // Picasso.get().load(profilePictureUrl).into(header_img);
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
//                Intent photoIntent = new Intent(Intent.ACTION_PICK);
//                photoIntent.setType("image/*");
//                startActivityForResult(photoIntent, 1);


                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                pickImage.launch(intent);
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
                        Toast.makeText(EditProfile.this, Objects.requireNonNull(task.getException()).getLocalizedMessage(), Toast.LENGTH_SHORT).show();
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

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == 1 && resultCode == RESULT_OK && data!=null){
//            imagePath = data.getData();
//            getImageInImageView();
//            uploadImage();
//        }
//    }


    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK)
                {
                    if (result.getData() != null)
                    {
                        Uri imageuri = result.getData().getData();
                        try {
                            InputStream inputStream = getContentResolver().openInputStream(imageuri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            imgprofile.setImageBitmap(bitmap);
                            header_img.setImageBitmap(bitmap);
                            encodedImage = encodeImage(bitmap);

                            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                            firestore.collection("users").document(uid).update("image",encodedImage);

                        }catch (FileNotFoundException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
            }
    );


    private String  encodeImage(Bitmap bitmap)
    {
        int previewWidth = 150;
        int previewHeight = bitmap.getHeight() + previewWidth/ bitmap.getWidth();
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap,previewWidth,previewHeight,false);
        ByteArrayOutputStream byteArrayOutputStream= new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG,50,byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes,Base64.DEFAULT);
    }

//    private void getImageInImageView() {
//
//        Bitmap bitmap = null;
//        try {
//            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),imagePath);
//        }
//        catch (IOException e){
//            e.printStackTrace();
//        }
//
//        if(bitmap != null)
//        {
//            //imgprofile.setImageBitmap(bitmap);
//           // header_img.setImageBitmap(bitmap);
//
////            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
////            bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
////            byte[] bytearray = byteArrayOutputStream.toByteArray();
////            selectedimagebase64 = Base64.encodeToString(bytearray, Base64.DEFAULT);
//
//            Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap,previewWidth,previewHeight,false);
//            ByteArrayOutputStream byteArrayOutputStream= new ByteArrayOutputStream();
//            previewBitmap.compress(Bitmap.CompressFormat.JPEG,50,byteArrayOutputStream);
//            byte[] bytes = byteArrayOutputStream.toByteArray();
//            return Base64.encodeToString(bytes,Base64.DEFAULT);
//
//            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
//            firestore.collection("users").document(uid).update("image",selectedimagebase64);
//        }
//
//    }
}