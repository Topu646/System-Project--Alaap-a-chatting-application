package com.example.alaap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.example.alaap.databinding.ActivityUsersBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class UsersActivity extends AppCompatActivity {

    ActivityUsersBinding binding;
    private PreferenceManager preferenceManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUsersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferenceManager = new PreferenceManager(getApplicationContext());
        getUsers();
    }


    private void getUsers(){
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("users")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        String currentUserId = preferenceManager.getString("userId");
                        if (task.isSuccessful() && task.getResult() != null)
                        {
                            List<Users>users = new ArrayList<>();
                            for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult())
                            {
                                if (currentUserId.equals(queryDocumentSnapshot.getId()))
                                {
                                    continue;
                                }
                                Users user = new Users();
                                user.name = queryDocumentSnapshot.getString("name");
                                user.email = queryDocumentSnapshot.getString("email");
                                users.add(user);
                                if (users.size()> 0)
                                {
                                    UsersAdapter usersAdapter = new UsersAdapter(users);
                                    binding.usersrecyclerview.setAdapter(usersAdapter);
                                }else {
                                    Errormessage();
                                }
                            }
                        }else {
                            Errormessage();
                        }
                    }
                });
    }
    private void Errormessage()
    {
        binding.errortextview.setText("No users Available");
        binding.errortextview.setVisibility(View.VISIBLE);
    }
}