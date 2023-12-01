package com.example.alaap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

public class NewsFeedactivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ImageButton backbutton;
    private List<PostItem>list;
     private CustomAdapter customAdapter;
    DatabaseReference databaseReference;
    FirebaseStorage mStorage;
    FirebaseDatabase mDatabase;
    String username;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_feedactivity);

        recyclerView = findViewById(R.id.recyclerviewid);
        backbutton = findViewById(R.id.back);
        backbutton.setOnClickListener(view -> {
            getOnBackPressedDispatcher().onBackPressed();
        });
        Bundle bundle = getIntent().getExtras();
        if(bundle != null)
        {
            username = bundle.getString("name");
        }

        mDatabase=FirebaseDatabase.getInstance();
        databaseReference = mDatabase.getReference().child("Post");
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        list = new ArrayList<PostItem>();

        customAdapter = new CustomAdapter(NewsFeedactivity.this,list);
        recyclerView.setAdapter(customAdapter);

        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {


                PostItem items = snapshot.getValue(PostItem.class);
                if(items == null) return;

                String image =  snapshot.child("imagestring").getValue(String.class);

                items.setImgstring(image);

                String post = snapshot.child("Post").getValue(String.class);
                items.setUserpost(post);

                list.add(items);
                customAdapter.notifyItemInserted(list.size()-1);
           //     customAdapter.notifyDataSetChanged();
            }


//                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//                    PostItem items = snapshot.getValue(PostItem.class);
//                    if (items == null) {
//                        Log.d("Debug", "PostItem is null");
//                        return;
//                    }
//
//                    String image = snapshot.child("image").getValue(String.class);
//                    if (image != null) {
//                        items.setImgstring(image);
//                    }
//
//                    String post = snapshot.child("Post").getValue(String.class);
//                    items.setUserpost(post);
//
//                    list.add(items);
//                    customAdapter.notifyItemInserted(list.size() - 1);
//
//                    // Logging to check the retrieved data
//                    Log.d("Debug", "Post: " + items.getUserpost() + ", Image: " + items.getImgstringt());
//                }

                // Other ChildEventListener methods;


            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

//    @Override
//    protected void onStart() {
//
//        databaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                list.clear();
//
//                if (snapshot.getChildrenCount() == 0) {
//                    Toast.makeText(getApplicationContext(),"No data available", Toast.LENGTH_SHORT).show();
//                } else {
//                    for (DataSnapshot datasnapshot1 : snapshot.getChildren()) {
//                        PostItem item = datasnapshot1.getValue(PostItem.class);
//                        list.add(item);
//                    }
//                    customAdapter.notifyDataSetChanged();
//                }
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//        super.onStart();
//    }
}