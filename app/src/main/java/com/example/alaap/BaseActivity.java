package com.example.alaap;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class BaseActivity extends AppCompatActivity {

    private DocumentReference documentReference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PreferenceManager preferenceManager = new PreferenceManager(getApplicationContext());
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        documentReference = database.collection("users").document(preferenceManager.getString("userId"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        documentReference.update("availability",0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        documentReference.update("availability",1);
    }
}
