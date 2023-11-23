package com.example.alaap;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.SyncStateContract;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedDispatcherKt;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Consumer;

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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;


import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HomeScreen extends BaseActivity implements ConversationListener{

    private ActivityHomeScreenBinding binding;
    private PreferenceManager preferenceManager;
    private SharedPrefManager sharedPrefManager;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    String email, password, name;
    ImageView profileimageview,signout;

    Button addpostbutton;
    String namefromgoogle, emailfromgoogle;
    FirebaseAuth mauth;
//    TextView demotext, demotext2;
    FloatingActionButton floatingActionButton;
    private List<ChatMessage>conversations;
    private recentConversationAdapter conversationAdapter;
    private FirebaseFirestore database;

    private long backpressedTime;
    private Toast backToast;

    @Override
    public void onBackPressed() {


        if (backpressedTime + 2000 > System.currentTimeMillis())
        {
            backToast.cancel();
            finishAffinity();
            super.onBackPressed();
            return;
        }else {
            backToast = Toast.makeText(getApplicationContext(),"Press back again to exit",Toast.LENGTH_SHORT);
            backToast.show();
        }
        backpressedTime = System.currentTimeMillis();

    }

    //private ActivityHomeScreenBinding binding;
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        sharedPrefManager = new SharedPrefManager(getApplicationContext());

        preferenceManager = new PreferenceManager(getApplicationContext());
        binding = ActivityHomeScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        conversations = new ArrayList<>();
        conversationAdapter = new recentConversationAdapter(conversations,this);
        binding.conversationsRecyclerview.setAdapter(conversationAdapter);
        database = FirebaseFirestore.getInstance();

        mauth = FirebaseAuth.getInstance();

        floatingActionButton = findViewById(R.id.plusbtn);
        profileimageview = findViewById(R.id.profileicon);

        addpostbutton = findViewById(R.id.addpostbuttonid);

        signout = findViewById(R.id.imageSignout);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), UsersActivity.class);
                startActivity(intent);

            }
        });



        addpostbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeScreen.this,AddPostActivity.class);
                startActivity(intent);
            }
        });



        if (binding != null) {
            loadUserDetails();
        }
//        getToken();
        setListeners();

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build();
        gsc = GoogleSignIn.getClient(this, gso);

        listenConversations();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            name = account.getDisplayName();
            email = account.getEmail();
        }
        else {
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                email = bundle.getString("email");
                name = bundle.getString("name");
                password = bundle.getString("password");
            }
        }

//        demotext.setText(name);
//        demotext2.setText(email);
        profileimageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("ClickTest", "Profile image clicked!");
                Intent intent = new Intent(HomeScreen.this, EditProfile.class);
                intent.putExtra("name", name);
                intent.putExtra("email", email);
                startActivity(intent);
            }
        });




            signout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    signout_fun();
                }

            });
        }

    private void signout_fun() {
        gsc.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                finish();
                startActivity(new Intent(HomeScreen.this, LoginActivity.class));
            }
        });
        logout_user();
    }

    //  }


//    private void loadUserDetails() {
//        binding.textname.setText(preferenceManager.getString(Constants.KEY_NAME));
//        byte[] bytes = Base64.decode(preferenceManager.getString(Constants.KEY_IMAGE),Base64.DEFAULT);
//        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0,bytes.length);
//        binding.imageSignout.setImageBitmap(bitmap);
//    }

    private void setListeners()
    {
        binding.imageSignout.setOnClickListener(v -> logout_user());
    }
    private void loadUserDetails() {
        String name = preferenceManager.getString("name");
        String email = preferenceManager.getString("email");
        String imageString = preferenceManager.getString("image");

        if (name != null) {
            binding.demotext.setText(name);
        }

        if (email != null) {
            binding.demotext2.setText(email);
        }

        if (imageString != null) {
            byte[] bytes = Base64.decode(imageString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            binding.profileicon.setImageBitmap(bitmap);
        }
    }


    private void showToast(String message)
    {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void getToken()
    {
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(this::updateToken);
    }
    private void updateToken(String token)
    {
        preferenceManager.putString(Constants.KEY_FCM_TOKEN,token);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference = database.collection(Constants.KEY_COLLECTION_USERS).document(
                preferenceManager.getString(Constants.KEY_USER_ID)
        );
        documentReference.update(Constants.KEY_FCM_TOKEN, token)
                .addOnFailureListener(e -> showToast("Unable to update token"));
    }


    private void signOut()
    {
        showToast("Signing out....");
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference = database.collection(Constants.KEY_COLLECTION_USERS).document(
                preferenceManager.getString(Constants.KEY_USER_ID)
        );

        HashMap<String, Object>updates = new HashMap<>();
        updates.put(Constants.KEY_FCM_TOKEN, FieldValue.delete());
        documentReference.update(updates)
                .addOnSuccessListener(unused -> {
                    preferenceManager.clear();
                    startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> showToast("Unable to sign out"));
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


                logout_user();

//            if(code.equals("two"))
////            {
//                gsc.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
////                        finish();
////                        startActivity(new Intent(HomeScreen.this, LoginActivity.class));
//
//                        if (task.isSuccessful()) {
//                            // Sign out was successful, navigate to the login screen
//                            navigateToLogin();
//                        } else {
//                            Log.d("error", task.getException().getMessage());
//                        }
//                    }
//                });
////            }
////
////            if(code.equals("one"))
////            {
////                mauth.signOut();
////                signOutUser();
////            }

        }

        return super.onOptionsItemSelected(item);
    }

    private void logout_user() {
        FirebaseAuth.getInstance().signOut();
        sharedPrefManager.logout();
        Intent intent = new Intent(HomeScreen.this,LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
        Toast.makeText(getApplicationContext(),"You have been logged out",Toast.LENGTH_SHORT).show();
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