package com.example.alaap;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class EditStatus extends AppCompatActivity {

    private EditText nameedittext,bioedittext;
    private Button updatebutton,cancelbutton;
    String name,bio,editedname,editedbio;
    FirebaseAuth mauth;
    ImageButton backbutton;
    PreferenceManager preferenceManager;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_status);

        nameedittext = findViewById(R.id.nameedittextid);
        bioedittext = findViewById(R.id.bioedittextid);

        updatebutton = findViewById(R.id.updatebtn);
        cancelbutton = findViewById(R.id.cancelbtn);

        cancelbutton.setOnClickListener(view -> {
            getOnBackPressedDispatcher().onBackPressed();
        });
        backbutton = findViewById(R.id.back);
        backbutton.setOnClickListener(view -> {
            getOnBackPressedDispatcher().onBackPressed();
        });

        mauth = FirebaseAuth.getInstance();
        preferenceManager = new PreferenceManager(getApplicationContext());

        Bundle bundle = getIntent().getExtras();
        if(bundle != null)
        {
            name = bundle.getString("name");
            bio = bundle.getString("bio");
            nameedittext.setText(name);
            bioedittext.setText(bio);
        }

        updatebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editedname = nameedittext.getText().toString().trim();
                editedbio = bioedittext.getText().toString().trim();

                FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                HashMap<String,Object> User = new HashMap<>();
                User.put("name",editedname);
                User.put("bio",editedbio);
                String uid = mauth.getUid();



                firestore.collection("users").document(uid).update(User)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                                preferenceManager.putBoolean("isSignedIn",true);
                                preferenceManager.putString("userId",uid);
                                preferenceManager.putString("name",editedname);
                                preferenceManager.putString("bio",editedbio);
                                FirebaseUser firebaseuser = mauth.getCurrentUser();

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


                                Intent intent = new Intent(EditStatus.this, EditProfile.class);
                                intent.putExtra("name",editedname);
                                intent.putExtra("bio",editedbio);
                                startActivity(intent);

                                Toast.makeText(getApplicationContext(),"Successfully updated info",Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });
            }
        });
    }
}