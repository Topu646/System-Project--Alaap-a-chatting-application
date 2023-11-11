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

import com.example.alaap.databinding.ActivityHomeScreenBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HomeScreen extends BaseActivity implements ConversationListener{

    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    String email, password, name;
    String namefromgoogle, emailfromgoogle;
    FirebaseAuth mauth;
    TextView demotext, demotext2;
    private PreferenceManager preferenceManager;
    FloatingActionButton floatingActionButton;
    private List<ChatMessage>conversations;
    private recentConversationAdapter conversationAdapter;
    private FirebaseFirestore database;

    private ActivityHomeScreenBinding binding;
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        conversations = new ArrayList<>();
        conversationAdapter = new recentConversationAdapter(conversations,this);
        binding.conversationsRecyclerview.setAdapter(conversationAdapter);
        database = FirebaseFirestore.getInstance();
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
        demotext.setText(preferenceManager.getString("name"));
        demotext2.setText(preferenceManager.getString("email"));

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build();
        gsc = GoogleSignIn.getClient(this, gso);

        listenConversations();
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

    private final EventListener<QuerySnapshot> eventListener = (value , error) ->
    {
        if(error!= null)
        {

            return ;
        }
        if (value!=null)
        {


            for (DocumentChange documentChange : value.getDocumentChanges())
            {
                if (documentChange.getType() == DocumentChange.Type.ADDED)
                {
                    String senderId = documentChange.getDocument().getString("senderId");
                    String receiverId = documentChange.getDocument().getString("receiverId");
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.senderId = senderId;
                    chatMessage.recieverId = receiverId;
                    if (preferenceManager.getString("userId").equals(senderId))
                    {
                        chatMessage.conversationNmae = documentChange.getDocument().getString("receiverName");
                        chatMessage.conversationId = documentChange.getDocument().getString("receiverId");
                    }else {
                        chatMessage.conversationNmae = documentChange.getDocument().getString("senderName");
                        chatMessage.conversationId = documentChange.getDocument().getString("senderId");
                    }
                    chatMessage.message = documentChange.getDocument().getString("lastMessage");
                    chatMessage.dateObject = documentChange.getDocument().getDate("timestamp");
                    conversations.add(chatMessage);
                }else if (documentChange.getType() == DocumentChange.Type.MODIFIED)
                {
                    for (int i =0; i < conversations.size();i++)
                    {
                        String senderId = documentChange.getDocument().getString("senderId");
                        String receiverId = documentChange.getDocument().getString("receiverId");
                        if (conversations.get(i).senderId.equals(senderId) && conversations.get(i).recieverId.equals(receiverId))
                        {
                            conversations.get(i).message = documentChange.getDocument().getString("lastMessage");
                            conversations.get(i).dateObject = documentChange.getDocument().getDate("timestamp");
                            break;
                        }
                    }
                }

            }
            Collections.sort(conversations, (obj1 , obj2) -> obj2.dateObject.compareTo(obj1.dateObject));
            conversationAdapter.notifyDataSetChanged();
            binding.conversationsRecyclerview.smoothScrollToPosition(0);
            binding.conversationsRecyclerview.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.GONE);


        }


    };

    private void listenConversations()
    {
        database.collection("conversations").whereEqualTo("senderId",preferenceManager.getString("userId"))
                .addSnapshotListener(eventListener);
        database.collection("conversations").whereEqualTo("receiverId",preferenceManager.getString("userId"))
                .addSnapshotListener(eventListener);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mauth.getCurrentUser();

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

    @Override
    public void onConversationClicked(Users user) {
        Intent intent = new Intent(getApplicationContext(),ChatActivity.class);
        intent.putExtra("user",user);
        startActivity(intent);
    }
}