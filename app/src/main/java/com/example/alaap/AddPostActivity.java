package com.example.alaap;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddPostActivity extends AppCompatActivity {

    Button createpostbutton;
    EditText createpostedittext;
    DatabaseReference databaseReference;
    String post,username;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        createpostbutton = findViewById(R.id.addpostbuttonid);
        createpostedittext = findViewById(R.id.createposttextid);

        databaseReference = FirebaseDatabase.getInstance().getReference("Post");


        Bundle bundle = getIntent().getExtras();
        if(bundle != null)
        {
            username = bundle.getString("name");
        }

        createpostbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                post = createpostedittext.getText().toString().trim();
                String key = databaseReference.push().getKey();

                PostItem item = new PostItem(username,post);

                databaseReference.child(key).setValue(item);
                Toast.makeText(getApplicationContext(),"Posted successfully",Toast.LENGTH_SHORT).show();
                createpostedittext.setText("");
                Intent intent = new Intent(AddPostActivity.this,NewsFeedactivity.class);
                intent.putExtra("name",username);
                startActivity(intent);
                finish();
            }
        });
    }
}