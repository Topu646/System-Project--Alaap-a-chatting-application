package com.example.alaap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class NewsFeedactivity extends AppCompatActivity {

    private RecyclerView recyclerView;

    private ArrayList<PostItem>list;
     private CustomAdapter customAdapter;
    DatabaseReference databaseReference;
    String username;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_feedactivity);

        recyclerView = findViewById(R.id.recyclerviewid);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null)
        {
            username = bundle.getString("name");
        }

        databaseReference = FirebaseDatabase.getInstance().getReference("Post");
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        list = new ArrayList<>();

        customAdapter = new CustomAdapter(NewsFeedactivity.this,list);
        recyclerView.setAdapter(customAdapter);
    }

    @Override
    protected void onStart() {

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();

                if (snapshot.getChildrenCount() == 0) {
                    Toast.makeText(getApplicationContext(),"No data availbale", Toast.LENGTH_SHORT).show();
                } else {
                    for (DataSnapshot datasnapshot1 : snapshot.getChildren()) {
                        PostItem item = datasnapshot1.getValue(PostItem.class);
                        list.add(item);
                    }
                    customAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        super.onStart();
    }
}