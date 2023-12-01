package com.example.alaap;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class AddPostActivity extends AppCompatActivity {

    Button createpostbutton,addimagebtn;
    ImageButton backbutton;
    ImageView addimage;
    EditText createpostedittext;
    DatabaseReference databaseReference;
    FirebaseStorage mStorage;
    FirebaseDatabase mDatabase;
    String post,username;
    private String encodedImage;
    private static final int Gallery_code=1;
    Uri imageurl;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        backbutton =(ImageButton) findViewById(R.id.back);
        createpostbutton = findViewById(R.id.addpostbuttonid);
        createpostedittext = findViewById(R.id.createposttextid);
        addimagebtn = findViewById(R.id.addimagebtnid);

        addimage = findViewById(R.id.addimagepostid);

        databaseReference = FirebaseDatabase.getInstance().getReference("Post");
        mDatabase = FirebaseDatabase.getInstance();
        mStorage = FirebaseStorage.getInstance();
        progressDialog = new ProgressDialog(this);


        backbutton.setOnClickListener(view -> {
            getOnBackPressedDispatcher().onBackPressed();
        });

        Bundle bundle = getIntent().getExtras();
        if(bundle != null)
        {
            username = bundle.getString("name");
        }


        addimagebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, Gallery_code);
            }
        });


        createpostbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                post = createpostedittext.getText().toString().trim();
                if((!post.isEmpty() && imageurl!=null) || (post.isEmpty() && imageurl!=null))
                {
                    progressDialog.setTitle("Uploading...");
                    progressDialog.show();
                    StorageReference filepath = mStorage.getReference().child("imagePost").child(imageurl.getLastPathSegment());
                    filepath.putFile(imageurl).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            Task<Uri> downloadUrl= taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    String t = task.getResult().toString();

                                    Map<String,Object> map = new HashMap<>();
                                    map.put("Post",post);
                                    map.put("imagestring",t);
                                    map.put("username",username);

                                    databaseReference.push().updateChildren(map)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        progressDialog.dismiss();

                                                        Intent intent = new Intent(AddPostActivity.this,NewsFeedactivity.class);
                                                        startActivity(intent);
                                                        createpostedittext.setText("");
                                                        addimage.setVisibility(View.GONE);
                                                    }
                                                    else{
                                                        Toast.makeText(AddPostActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });

                                }
                            });
                        }
                    });
                }

                if(imageurl == null && !post.isEmpty())
                {
                    String key = databaseReference.push().getKey();

                    Map<String,Object> map = new HashMap<>();
                    map.put("Post",post);
                    map.put("username",username);

                    databaseReference.child(key).updateChildren(map);
                    Toast.makeText(getApplicationContext(),"Posted successfully",Toast.LENGTH_SHORT).show();
                    createpostedittext.setText("");
                    addimage.setVisibility(View.GONE);
                    Intent intent = new Intent(AddPostActivity.this,NewsFeedactivity.class);
                    intent.putExtra("name",username);
                    startActivity(intent);
                    finish();
                }else {
                    Toast.makeText(AddPostActivity.this, "Enter text to post!", Toast.LENGTH_SHORT).show();
                }
            }
        });

//        createpostbutton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                post = createpostedittext.getText().toString().trim();
//                if (!post.isEmpty())
//                {
//                    String key = databaseReference.push().getKey();
//
//                    PostItem item = new PostItem(username,post,encodedImage);
//
//                    databaseReference.child(key).setValue(item);
//                    Toast.makeText(getApplicationContext(),"Posted successfully",Toast.LENGTH_SHORT).show();
//                    createpostedittext.setText("");
//                    Intent intent = new Intent(AddPostActivity.this,NewsFeedactivity.class);
//                    intent.putExtra("name",username);
//                    startActivity(intent);
//                    finish();
//                }else {
//                    Toast.makeText(AddPostActivity.this, "Enter text to post!", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Gallery_code && resultCode == RESULT_OK) {
            imageurl = data.getData();
            addimage.setImageURI(imageurl);
        }
    }


    }



    //    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
//            new ActivityResultContracts.StartActivityForResult(),
//            result -> {
//                if (result.getResultCode() == RESULT_OK)
//                {
//                    if (result.getData() != null)
//                    {
//                        Uri imageuri = result.getData().getData();
//                        try {
//                            InputStream inputStream = getContentResolver().openInputStream(imageuri);
//                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
//                            addimage.setImageBitmap(bitmap);
//                            encodedImage = encodeImage(bitmap);
////
////                            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
////                            firestore.collection("users").document(uid).update("image",encodedImage);
//
//                        }catch (FileNotFoundException e)
//                        {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//            }
//    );


//    private String  encodeImage(Bitmap bitmap)
//    {
//        int previewWidth = 150;
//        int previewHeight = bitmap.getHeight() + previewWidth/ bitmap.getWidth();
//        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap,previewWidth,previewHeight,false);
//        ByteArrayOutputStream byteArrayOutputStream= new ByteArrayOutputStream();
//        previewBitmap.compress(Bitmap.CompressFormat.JPEG,50,byteArrayOutputStream);
//        byte[] bytes = byteArrayOutputStream.toByteArray();
//        return Base64.encodeToString(bytes,Base64.DEFAULT);
//    }

