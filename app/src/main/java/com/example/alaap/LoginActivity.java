package com.example.alaap;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
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
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.Login;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;

import io.grpc.ClientStreamTracer;

public class LoginActivity extends AppCompatActivity {

    Button loginButton, signupButton;
    ImageView googleButton,facebookbutton;
    TextView forgotpasstext;

    PreferenceManager preferenceManager;
    EditText email,password;

    GoogleSignInOptions gso;

    GoogleSignInClient gsc;

    FirebaseAuth mAuth;

    FirebaseDatabase database;
    FirebaseDatabase firebaseDatabase;

    ProgressDialog progressDialog;

    String mail,name,code;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        preferenceManager = new PreferenceManager(getApplicationContext());
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);

        forgotpasstext = findViewById(R.id.forgotpassid);

        loginButton = findViewById(R.id.loginbutton);
        signupButton = findViewById(R.id.signupbutton);

        googleButton = findViewById(R.id.googleButton);
        facebookbutton = findViewById(R.id.fbbutton);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();

        gsc = GoogleSignIn.getClient(this,gso);

        mAuth = FirebaseAuth.getInstance();
        String uid = mAuth.getUid();
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
                //code = "two";
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
                    email.setError("Enter email adress");
                    email.requestFocus();
                    return;
                }
                if(password.getText().toString().isEmpty()==true){
                    password.setError("Enter password");
                    password.requestFocus();
                    return;
                }
                if(!email.getText().toString().isEmpty() &&  !password.getText().toString().isEmpty()){
                  //  code = "one";
                    progressDialog = new ProgressDialog(LoginActivity.this);
                    progressDialog.setMessage("Logging in process...");
                    progressDialog.show();
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

        forgotpasstext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this,ForgotpassActivity.class);
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
            String email = currentUser.getEmail();
            String name = currentUser.getDisplayName();
            intent.putExtra("name",name);
            intent.putExtra("email",email);
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

//    private void signOut() {
//        // Implement sign out logic here
//    }

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


                    FirebaseUser firebaseUseruser = mAuth.getCurrentUser();
                    String email = firebaseUseruser.getEmail();
                    String name = firebaseUseruser.getDisplayName();


                    String user = mAuth.getCurrentUser().getUid();

                    firebaseDatabase.getInstance().getReference().child("user").child(user).setValue(new User(name,email));

                    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                    HashMap<String,Object> User = new HashMap<>();
                    User.put("name",name);
                    User.put("email",email);
                    String uid = mAuth.getUid();


                    firestore.collection("users").document(uid).set(User)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {

                                    preferenceManager.putBoolean("isSignedIn",true);
                                    preferenceManager.putString("userId",uid);
                                    preferenceManager.putString("name",name);
                                    preferenceManager.putString("email",email);
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
                                    Intent intent = new Intent(LoginActivity.this, HomeScreen.class);
                                    intent.putExtra("email",email);
                                    intent.putExtra("name",name);
                                    startActivity(intent);
                                    Toast.makeText(getApplicationContext(),"Registration successful",Toast.LENGTH_SHORT).show();

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    Toast.makeText(LoginActivity.this, task.getException().getMessage().toString(), Toast.LENGTH_SHORT).show();
                                }
                            });

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
      //  intent.putExtra("code",code);
        startActivity(intent);
    }

    void signInwithEmailPassword(String mail,String pass){
        mAuth.signInWithEmailAndPassword(mail,pass).addOnCompleteListener(this,new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                   // Log.d(TAG,"signInWithEmail:success");
                    progressDialog.cancel();

                    FirebaseFirestore firestore = FirebaseFirestore.getInstance();


                    FirebaseUser user = mAuth.getCurrentUser();
                    String email = user.getEmail();
                    String name = user.getDisplayName();
                    preferenceManager.putBoolean("isSignedIn",true);
                    preferenceManager.putString("email",email);
                    preferenceManager.putString("name",name);
                    preferenceManager.putString("userId",user.getUid());
                    Toast.makeText(LoginActivity.this,"Login successful",Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(LoginActivity.this,HomeScreen.class);
                    intent.putExtra("name",name);
                    intent.putExtra("email",email);
                    startActivity(intent);
                }
                else{
                    progressDialog.cancel();
                    //Log.w(TAG,"signInWithEmail:Failed",task.getException());
                    Toast.makeText(LoginActivity.this,"Incorrect Email Or Password",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}