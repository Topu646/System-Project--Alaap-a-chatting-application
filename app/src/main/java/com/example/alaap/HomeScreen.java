package com.example.alaap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.QuickContactBadge;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class HomeScreen extends AppCompatActivity {

    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    String email,password,name;

    Button signoutbtn;
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);


        signoutbtn = findViewById(R.id.signoutbutton);

       gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build();
       gsc = GoogleSignIn.getClient(this,gso);

//        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
//        if(account != null)
//        {
//            String name = account.getDisplayName();
//            String email = account.getEmail();
//            nametext.setText(name);
//            emailtext.setText(email);
//        }

//        else{
            Bundle bundle = getIntent().getExtras();
            if(bundle != null)
            {
                email = bundle.getString("email");
                name = bundle.getString("name");
                password = bundle.getString("password");
            }

        signoutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gsc.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        finish();
                        startActivity(new Intent(HomeScreen.this,LoginActivity.class));
                    }
                });
            }
        });
    }
}