package com.example.alaap;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.alaap.databinding.ActivityChatBinding;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ChatActivity extends AppCompatActivity {

    private ActivityChatBinding binding;
    private Users recieverUser;
    private List<ChatMessage>chatMessages;
    private ChatAdapter chatAdapter;
    private PreferenceManager preferenceManager;
    private FirebaseFirestore firestore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.backbutton.setOnClickListener(view -> getOnBackPressedDispatcher().onBackPressed());
        loadRecieverDetails();
        init();
        listenMessage();
        binding.sendbutton.setOnClickListener(view -> sendMessage());

    }

    private void init()
    {
        preferenceManager = new PreferenceManager(getApplicationContext());
        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(
                chatMessages,
                preferenceManager.getString("userId")
        );
        binding.chatrecyclerview.setAdapter(chatAdapter);
        firestore = FirebaseFirestore.getInstance();
    }
    private final EventListener<QuerySnapshot> eventListener = (value , error) ->
    {
        if(error!= null)
        {
            System.out.println("chat error "+error);
            return ;
        }
        if (value!=null)
        {
            int count = chatMessages.size();
            for (DocumentChange documentChange : value.getDocumentChanges())
            {
                if (documentChange.getType() == DocumentChange.Type.ADDED)
                {
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.senderId = documentChange.getDocument().getString("senderId");
                    chatMessage.recieverId = documentChange.getDocument().getString("receiverId");
                    chatMessage.message = documentChange.getDocument().getString("message");
                    chatMessage.dateTime = getReadableDateTime(documentChange.getDocument().getDate("timestamp"));
                    chatMessage.dateObject = documentChange.getDocument().getDate("timestamp");
                    chatMessages.add(chatMessage);
                }

            }
            Collections.sort(chatMessages, (obj1 , obj2) -> obj1.dateObject.compareTo(obj2.dateObject));
            if (count == 0)
            {
                chatAdapter.notifyDataSetChanged();
            }else{

                chatAdapter.notifyItemRangeInserted(chatMessages.size(),chatMessages.size());
                binding.chatrecyclerview.smoothScrollToPosition(chatMessages.size() -1);
            }

        }


    };

    private void listenMessage(){
        firestore.collection("chat")
                .whereEqualTo("senderId",preferenceManager.getString("userId"))
                .whereEqualTo("receiverId",recieverUser.userid)
                .addSnapshotListener(eventListener);
        firestore.collection("chat")
                .whereEqualTo("senderId",recieverUser.userid)
                .whereEqualTo("receiverId",preferenceManager.getString("userId"))
                .addSnapshotListener(eventListener);
    }

    private void sendMessage()
    {
        HashMap<String,Object> message = new HashMap<>();
        message.put("senderId",preferenceManager.getString("userId"));
        message.put("receiverId",recieverUser.userid);
        message.put("message",binding.messageEditText.getText().toString());
        message.put("timestamp",new Date());
        firestore.collection("chat").add(message);
        binding.messageEditText.setText(null);
    }
    private void loadRecieverDetails()
    {
        recieverUser  = (Users) getIntent().getSerializableExtra("user");
        binding.nametextview.setText(recieverUser.name);
    }

    private String getReadableDateTime(Date date)
    {
        return  new SimpleDateFormat("MMMM dd, yyyy - hh:mm: a", Locale.getDefault()).format(date);
    }
}