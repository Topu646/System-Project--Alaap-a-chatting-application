package com.example.alaap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.alaap.databinding.ActivityChatBinding;
import com.facebook.internal.WebDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends BaseActivity {

    private ActivityChatBinding binding;
    private Users recieverUser;
    private List<ChatMessage>chatMessages;
    private ChatAdapter chatAdapter;
    private PreferenceManager preferenceManager;
    private FirebaseFirestore firestore;
    private String conversationId = null;
    private Boolean isReceiverAvailable = false;
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
        if (conversationId == null)
        {
            checkForConversation();
        }

    };

    private void showToast(String message)
    {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void sendNotification(String messagebody)
    {
        ApiClient.getClient().create(ApiService.class).sendMessage(
                Constants.getRemoteMsgHeaders(),
                messagebody
        ).enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {

                if (response.isSuccessful())
                {
                    try {
                        if (response.body()!= null)
                        {
                            JSONObject responseJson = new JSONObject(response.body());
                            JSONArray results = responseJson.getJSONArray("results");
                            if (responseJson.getInt("failure") == 1)
                            {
                                JSONObject error = (JSONObject) results.get(0);
                                showToast(error.getString("error"));
                                return;
                            }
                        }
                    }catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                    showToast("Notification sent successfully");
                }else {
                    showToast("Error: "+ response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                showToast(t.getMessage());

            }
        });
    }
    private void listenAvailabilityOfReceiver(){
        firestore.collection("users").document(
                recieverUser.userid
        ).addSnapshotListener(ChatActivity.this, (value, error) -> {
            if (error != null){
                return;
            }
            if (value != null)
            {
                if (value.getLong("availability") != null)
                {
                    int availability = Objects.requireNonNull(
                            value.getLong("availability"))
                                    .intValue();
                            isReceiverAvailable = availability == 1;

                }
                recieverUser.token = value.getString(Constants.KEY_FCM_TOKEN);
            }
            if (isReceiverAvailable)
            {
                binding.availabilitytextview.setVisibility(View.VISIBLE);
            }else {
                binding.availabilitytextview.setVisibility(View.GONE);
            }
        });
    }

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
        if (conversationId != null)
        {
            updateConversation(binding.messageEditText.getText().toString());
        }else {
            HashMap<String,Object> conversation = new HashMap<>();
            conversation.put("senderId",preferenceManager.getString("userId"));
            conversation.put("senderName",preferenceManager.getString("name"));
            conversation.put("receiverId",recieverUser.userid);
            conversation.put("receiverName",recieverUser.name);
            conversation.put("lastMessage",binding.messageEditText.getText().toString());
            conversation.put("timestamp",new Date());
            addConversation(conversation);
        }
        if (!isReceiverAvailable)
        {
            try {
                JSONArray tokens = new JSONArray();
                tokens.put(recieverUser.token);

                JSONObject data = new JSONObject();
                data.put(Constants.KEY_USER_ID,preferenceManager.getString(Constants.KEY_USER_ID));
                data.put(Constants.KEY_NAME,preferenceManager.getString(Constants.KEY_NAME));
                data.put(Constants.KEY_FCM_TOKEN,preferenceManager.getString(Constants.KEY_FCM_TOKEN));
                data.put("message",binding.messageEditText.getText().toString());

                JSONObject body = new JSONObject();
                body.put(Constants.REMOTE_MSG_DATA,data);
                body.put(Constants.REMOTE_MSG_REGISTRATION_IDS,tokens);
                sendNotification(body.toString());
            }catch (Exception exception)
            {
                showToast(exception.getMessage());
            }
        }
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

    private void addConversation(HashMap<String,Object> conversation)
    {
        firestore.collection("conversations").add(conversation)
                .addOnSuccessListener(documentReference -> conversationId = documentReference.getId());
    }
    private void updateConversation(String message)
    {
        DocumentReference documentReference = firestore.collection("conversations").document(conversationId);
        documentReference.update("lastMessage",message,"timestamp", new Date());
    }

    private void checkForConversation(){
        if (chatMessages.size() != 0)
        {
            checkForConversationRemotely(preferenceManager.getString("userId"),recieverUser.userid);
            checkForConversationRemotely(recieverUser.userid,preferenceManager.getString("userId"));
        }
    }
    private void checkForConversationRemotely(String senderId, String receiverId)
    {
        firestore.collection("conversations")
                .whereEqualTo("senderId",senderId)
                .whereEqualTo("receiverId",receiverId)
                .get().addOnCompleteListener(conversationOnCompleteListener);

    }
    private final OnCompleteListener<QuerySnapshot> conversationOnCompleteListener = task -> {
        if (task.isSuccessful() && task.getResult() != null && task.getResult().getDocuments().size() > 0  )
        {
            DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
            conversationId = documentSnapshot.getId();
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        listenAvailabilityOfReceiver();
    }
}