package com.example.alaap;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

public class HomeScreen extends AppCompatActivity {

    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    String email, password, name;
    String namefromgoogle, emailfromgoogle;
    FirebaseAuth mauth;
    TextView demotext, demotext2;
    private PreferenceManager preferenceManager;
    FloatingActionButton floatingActionButton;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        mauth = FirebaseAuth.getInstance();

        floatingActionButton = findViewById(R.id.floatingbutton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), UsersActivity.class);
                startActivity(intent);

            }
        });
        preferenceManager = new PreferenceManager(getApplicationContext());
        demotext = findViewById(R.id.demotext);
        demotext2 = findViewById(R.id.demotext2);
        //demotext.setText(preferenceManager.getString("name"));
        demotext2.setText(preferenceManager.getString("userId"));

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build();
        gsc = GoogleSignIn.getClient(this, gso);

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            namefromgoogle = account.getDisplayName();
            emailfromgoogle = account.getEmail();
        } else {
           Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                email = bundle.getString("email");
                name = bundle.getString("name");
                password = bundle.getString("password");
            }


//            signoutbtn.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    gsc.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
//                        @Override
//                        public void onComplete(@NonNull Task<Void> task) {
//                            finish();
//                            startActivity(new Intent(HomeScreen.this, LoginActivity.class));
//                        }
//                    });
//                }
//            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_item, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.signoutid) {

//            if(code.equals("two"))
//            {
                gsc.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
//                        finish();
//                        startActivity(new Intent(HomeScreen.this, LoginActivity.class));

                        if (task.isSuccessful()) {
                            // Sign out was successful, navigate to the login screen
                            navigateToLogin();
                        } else {
                            Log.d("error", task.getException().getMessage());
                        }
                    }
                });
//            }
//
//            if(code.equals("one"))
//            {
//                mauth.signOut();
//                signOutUser();
//            }

        }

        return super.onOptionsItemSelected(item);
    }

    private void navigateToLogin() {
        Intent intent = new Intent(HomeScreen.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }


    private void signOutUser() {
        Intent intent = new Intent(HomeScreen.this,LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}