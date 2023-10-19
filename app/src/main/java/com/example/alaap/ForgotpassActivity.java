package com.example.alaap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotpassActivity extends AppCompatActivity {

    Button resetbutton,backbutton;

    EditText emailtext;
    String email;
    FirebaseAuth mauth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpass);

        resetbutton = findViewById(R.id.resetbtn);
        backbutton = findViewById(R.id.backbtn);
        emailtext = findViewById(R.id.emailid);

        mauth = FirebaseAuth.getInstance();

        resetbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 validateData();
            }
        });

        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ForgotpassActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void validateData() {
        email = emailtext.getText().toString();
        if(email.isEmpty()){
            emailtext.setError("Enter an email adress");
            emailtext.requestFocus();
        }
        else{
            forgetpassword();
        }
    }

    private void forgetpassword() {
        mauth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(ForgotpassActivity.this,"Check your Email",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ForgotpassActivity.this,LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
                else{
                    Toast.makeText(ForgotpassActivity.this,"Error : "+task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}