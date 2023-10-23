package com.example.alaap;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {

    Button signupbutton,loginButton;
    EditText emailtext,nametext,passwordtext;

    private PreferenceManager preferenceManager;
    FirebaseAuth mAuth;

    ProgressDialog progressDialog;
    String email,name,password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth=FirebaseAuth.getInstance();
        preferenceManager = new PreferenceManager(getApplicationContext());
        signupbutton = findViewById(R.id.signupbtn);
        loginButton = findViewById(R.id.loginbutton);

        emailtext = findViewById(R.id.email);
        nametext = findViewById(R.id.name);
        passwordtext = findViewById(R.id.password);

        mAuth = FirebaseAuth.getInstance();

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

                if(email.isEmpty()){
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
                    createNewUser(email,password);
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


    void createNewUser(String mail,String pass){
        progressDialog = new ProgressDialog(SignUpActivity.this);
        progressDialog.setMessage("Creating account....");
        progressDialog.show();

        mAuth.createUserWithEmailAndPassword(mail,pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
//                            Log.d(TAG, "New User Created Successfully!");
                            progressDialog.cancel();

                            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                            HashMap<String,Object> User = new HashMap<>();
                            User.put("name",name);
                            User.put("email",email);
                            User.put("password",password);

                            firestore.collection("users").add(User)
                                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                @Override
                                                public void onSuccess(DocumentReference documentReference) {
                                                    preferenceManager.putBoolean("isSignedIn",true);
                                                    preferenceManager.putString("userId",documentReference.getId());
                                                    preferenceManager.putString("name",name);
                                                    Toast.makeText(getApplicationContext(),"Registration successful",Toast.LENGTH_SHORT).show();

                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                            Toast.makeText(SignUpActivity.this, task.getException().getMessage().toString(), Toast.LENGTH_SHORT).show();
                                        }
                                    });

                            FirebaseUser user = mAuth.getCurrentUser();

//                            user.delete();
//
                            Intent intent = new Intent(SignUpActivity.this, HomeScreen.class);
                            intent.putExtra("email",mail);
                            intent.putExtra("name",name);
                            intent.putExtra("password",pass);
                            startActivity(intent);
                        }
                        else{
                            progressDialog.cancel();

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