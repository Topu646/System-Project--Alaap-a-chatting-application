package com.example.alaap;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.alaap.databinding.ItemContainerRecentConversationBinding;

import java.util.List;

public class recentConversationAdapter extends RecyclerView.Adapter<recentConversationAdapter.ConversationViewHolder> {

    private final List<ChatMessage> chatMessages;
    private final ConversationListener conversationListener;
    public recentConversationAdapter(List<ChatMessage> chatMessages, ConversationListener conversationListener) {
        this.chatMessages = chatMessages;
        this.conversationListener = conversationListener;
    }

    @NonNull
    @Override
    public ConversationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ConversationViewHolder(
                ItemContainerRecentConversationBinding.inflate(
                        LayoutInflater.from(parent.getContext()),
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ConversationViewHolder holder, int position) {
        holder.setData(chatMessages.get(position));
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    class ConversationViewHolder extends RecyclerView.ViewHolder{
         ItemContainerRecentConversationBinding binding;

         ConversationViewHolder(ItemContainerRecentConversationBinding itemContainerRecentConversationBinding)
         {
             super(itemContainerRecentConversationBinding.getRoot());
             binding = itemContainerRecentConversationBinding;
         }

         void setData(ChatMessage chatMessage)
         {
             binding.nametextview.setText(chatMessage.conversationNmae);
             binding.recentmsgtextview.setText(chatMessage.message);
             binding.getRoot().setOnClickListener(view -> {
                 Users user = new Users();
                 user.userid = chatMessage.conversationId;
                 user.name = chatMessage.conversationNmae;
                 conversationListener.onConversationClicked(user);
             });
         }
     }
}