package com.example.alaap;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.alaap.databinding.ActivitySignUpBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.auth.UserProfileChangeRequest;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {

    Button signupbutton,loginButton;
    EditText emailtext,nametext,passwordtext;
    private ActivitySignUpBinding binding;
    private PreferenceManager preferenceManager;
    FirebaseAuth mAuth;
    private String encodedImage;
    FirebaseDatabase firebaseDatabase;
    ProgressDialog progressDialog;
    String email,name,password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth=FirebaseAuth.getInstance();
        preferenceManager = new PreferenceManager(getApplicationContext());
        signupbutton = findViewById(R.id.signupbtn);
        loginButton = findViewById(R.id.loginbutton);

        emailtext = findViewById(R.id.email);
        nametext = findViewById(R.id.name);
        passwordtext = findViewById(R.id.password);

      //  mAuth = FirebaseAuth.getInstance();

        binding.profileimage.setOnClickListener(view ->{
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickImage.launch(intent);
        });
        passwordtext.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2; // Index of drawableEnd

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (passwordtext.getRight() - passwordtext.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        // Handle the click on the drawableEnd here
                        // For example, toggle the password visibility
                        togglePasswordVisibility(passwordtext);
                        return true; // Consume the touch event
                    }
                }
                return false; // Let the EditText handle the touch event
            }

            private void togglePasswordVisibility(EditText password) {
                if (password.getInputType() == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                    password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                } else {
                    password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                }
            }
        });


        signupbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if the edittexts are empty then set error , else change the activity.


                email=emailtext.getText().toString().trim();
                name=nametext.getText().toString().trim();
                password=passwordtext.getText().toString().trim();

                if (encodedImage == null)
                {
                    Toast.makeText(SignUpActivity.this, "Select profile picture.", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(email.isEmpty()){
                    emailtext.setError("Enter an email adress");
                    emailtext.requestFocus();
                    return;
                }
                else if(name.isEmpty()){
                    nametext.setError("not filled");
                    nametext.requestFocus();
                    return;
                }
                else if(password.isEmpty()){
                    passwordtext.setError("not filled");
                    passwordtext.requestFocus();
                    return;
                }
//                else if(!email.getText().toString().isEmpty() && !name.getText().toString().isEmpty() && !password.getText().toString().isEmpty()){
//                    createNewUser(email.getText().toString(),password.getText().toString());
//                }

                else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
                {
                    emailtext.setError("Enter a valid email adress");
                    emailtext.requestFocus();
                    return;
                }

                else if(password.length() < 6)
                {
                    passwordtext.setError("password must be greater than 6 characters");
                    passwordtext.requestFocus();
                    return;
                }
                else{
                    createNewUser(email,password,name);
                }
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

    }
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
                            binding.profileimage.setImageBitmap(bitmap);
                            encodedImage = encodeImage(bitmap);
                        }catch (FileNotFoundException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
            }
    );


    void createNewUser(String mail,String pass,String name){
        progressDialog = new ProgressDialog(SignUpActivity.this);
        progressDialog.setMessage("Creating account....");
        progressDialog.show();

        mAuth.createUserWithEmailAndPassword(mail,pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @SuppressLint("RestrictedApi")
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.cancel();
                        if(task.isSuccessful()){
//                            Log.d(TAG, "New User Created Successfully!");

                            String user = mAuth.getCurrentUser().getUid();

                            firebaseDatabase.getInstance().getReference().child("user").child(user).setValue(new User(nametext.getText().toString(),emailtext.getText().toString(),""));
                            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                            HashMap<String,Object> User = new HashMap<>();
                            User.put("name",name);
                            User.put("email",email);
                            User.put("password",password);
                            User.put("image",encodedImage);
                            String uid = mAuth.getUid();

                            firestore.collection("users").document(uid).set(User)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {

                                            preferenceManager.putBoolean("isSignedIn",true);
                                            preferenceManager.putString("userId",uid);
                                            preferenceManager.putString("name",name);
                                            preferenceManager.putString("image",encodedImage);
                                            preferenceManager.putString("email",email);
                                            Toast.makeText(getApplicationContext(),"Registration successful",Toast.LENGTH_SHORT).show();

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                            Toast.makeText(SignUpActivity.this, task.getException().getMessage().toString(), Toast.LENGTH_SHORT).show();
                                        }
                                    });

                            FirebaseUser firebaseuser = mAuth.getCurrentUser();

                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(name)
                                    .build();
                            firebaseuser.updateProfile(profileUpdates)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                                 if (task.isSuccessful())
                                                 {
                                                     Log.d(TAG, "User profile updated.");
                                                 }
                                        }
                                    });
//                            user.delete();
//
                            Intent intent = new Intent(SignUpActivity.this, HomeScreen.class);
                            intent.putExtra("email",mail);
                            intent.putExtra("name",nametext.getText().toString());
                            intent.putExtra("password",pass);
                            startActivity(intent);
                        }
                        else{

                            if (task.getException() instanceof FirebaseAuthWeakPasswordException){
                                passwordtext.setError("password must be greater than 6 characters");
                                Toast.makeText(SignUpActivity.this,"Invalid Password",Toast.LENGTH_LONG).show();
                            }
                            if(task.getException() instanceof FirebaseAuthUserCollisionException){
                                emailtext.setError("This Email is Already Registered");
                                Toast.makeText(SignUpActivity.this,"Email Already Registered",Toast.LENGTH_LONG).show();
                            }
                            else {
                                Toast.makeText(SignUpActivity.this,task.getException().getMessage(),Toast.LENGTH_LONG).show();
                            }

//                            Log.w(TAG,"user creation failed",task.getException());
                            Toast.makeText(SignUpActivity.this,"Registration failed",Toast.LENGTH_LONG).show();
//                            Toast.makeText(SignUpActivity.this,task.getException().getMessage(),Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
