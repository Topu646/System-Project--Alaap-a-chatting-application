package com.example.alaap;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.login.Login;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.auth.User;

public class LoginActivity extends AppCompatActivity {

    Button loginButton, signupButton;
    ImageView googleButton,facebookbutton;

    EditText email,password;

    GoogleSignInOptions gso;

    GoogleSignInClient gsc;

    FirebaseAuth mAuth;

    FirebaseDatabase database;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);

        loginButton = findViewById(R.id.loginbutton);
        signupButton = findViewById(R.id.signupbutton);

        googleButton = findViewById(R.id.googleButton);
        facebookbutton = findViewById(R.id.fbbutton);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();

        gsc = GoogleSignIn.getClient(this,gso);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if(account != null)
        {
            navigateToSecondActivity();
        }


        password.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2; // Index of drawableEnd

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (password.getRight() - password.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        // Handle the click on the drawableEnd here
                        // For example, toggle the password visibility
                        togglePasswordVisibility(password);
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


        googleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInwithGoogle();
            }
        });

//        facebookbutton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(email.getText().toString().isEmpty()==true){
                    email.setError("required");
                }
                if(password.getText().toString().isEmpty()==true){
                    password.setError("password empty");
                }
                if(!email.getText().toString().isEmpty() &&  !password.getText().toString().isEmpty()){
                    signInwithEmailPassword(email.getText().toString(),password.getText().toString());
                }
            }
        });

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent = new Intent(LoginActivity.this,HomeScreen.class);
            startActivity(intent);
        }
    }

    private void togglePasswordVisibility(EditText editText) {
        if (editText.getInputType() == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        } else {
            editText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        }
    }

    private void signOut() {
        // Implement sign out logic here
    }

    private void signInwithGoogle() {
       // Intent signInIntent = gsc.getSignInIntent();
        Intent signInintent = gsc.getSignInIntent();
        startActivityForResult(signInintent,1000);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1000) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
              //  task.getResult(ApiException.class);
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuth(account.getIdToken());
              //  navigateToSecondActivity();
            }
            catch (ApiException e) {
                Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuth(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken,null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
//                    FirebaseUser user = mAuth.getCurrentUser();
//
//                    Users users = new Users();
//                    users.setUserid(user.getUid());
//                    users.setName(user.getDisplayName());
//                    users.setEmail(user.getEmail());
//
//                    database.getReference().child("Users").child(user.getUid()).setValue(users);

                    navigateToSecondActivity();
                }
                else{
                    Toast.makeText(LoginActivity.this,"Error while log in with google",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void navigateToSecondActivity() {
        finish();
        Intent intent = new Intent(LoginActivity.this,HomeScreen.class);
        startActivity(intent);
    }

    void signInwithEmailPassword(String mail,String pass){
        mAuth.signInWithEmailAndPassword(mail,pass).addOnCompleteListener(this,new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                   // Log.d(TAG,"signInWithEmail:success");
                    Toast.makeText(LoginActivity.this,"Login successful",Toast.LENGTH_LONG).show();
                    FirebaseUser user = mAuth.getCurrentUser();

                    Intent intent = new Intent(LoginActivity.this,HomeScreen.class);
                    startActivity(intent);
                }
                else{
                    //Log.w(TAG,"signInWithEmail:Failed",task.getException());
                    Toast.makeText(LoginActivity.this,"Incorrect Email Or Password",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}