package com.example.alaap;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.alaap.databinding.ItemContainerRecievedMessageBinding;
import com.example.alaap.databinding.ItemContainerSentMessageBinding;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private final List<ChatMessage>chatMessages;
    private final String senderId;
    private  Bitmap receiverProfileImage;

    public static final int View_Type_Sent =1;
    public static final int View_Type_Recieved =2;

    public void setReceiverProfileImage(Bitmap bitmap)
    {
        receiverProfileImage = bitmap;
    }
    public ChatAdapter(List<ChatMessage> chatMessages,Bitmap receiverProfileImage, String senderId) {
        this.chatMessages = chatMessages;
        this.senderId = senderId;
        this.receiverProfileImage = receiverProfileImage;
    }

    @Override
    public int getItemViewType(int position) {
        if (chatMessages.get(position).senderId.equals(senderId))
        {
            return View_Type_Sent;
        }else
        {
            return View_Type_Recieved;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == View_Type_Sent)
        {
            return new sentMessageViewHolder(
                    ItemContainerSentMessageBinding.inflate(
                            LayoutInflater.from(parent.getContext()),
                            parent,
                            false
                    )
            );
        }else {
            return new recievedMessageViewHolder(
                    ItemContainerRecievedMessageBinding.inflate(
                            LayoutInflater.from(parent.getContext()),
                            parent,
                            false
                    )
            );
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (getItemViewType(position) == View_Type_Sent)
        {
            ((sentMessageViewHolder) holder).setData(chatMessages.get(position));
        }else
        {
            ((recievedMessageViewHolder) holder).setData(chatMessages.get(position),receiverProfileImage);
        }
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    static class sentMessageViewHolder extends RecyclerView.ViewHolder{
        private final ItemContainerSentMessageBinding binding;

        public sentMessageViewHolder(ItemContainerSentMessageBinding itemContainerSentMessageBinding) {
            super(itemContainerSentMessageBinding.getRoot());
            binding = itemContainerSentMessageBinding;
        }

        void setData(ChatMessage chatMessage)
        {
            binding.textviewMessage.setText(chatMessage.message);
            binding.datetimeTextView.setText(chatMessage.dateTime);
        }
    }

    static class recievedMessageViewHolder extends RecyclerView.ViewHolder{
        private final ItemContainerRecievedMessageBinding binding;

        public recievedMessageViewHolder(ItemContainerRecievedMessageBinding itemContainerRecievedMessageBinding) {
            super(itemContainerRecievedMessageBinding.getRoot());
            binding = itemContainerRecievedMessageBinding;
        }

        void setData(ChatMessage chatMessage, Bitmap receiverProfileImage)
        {
            binding.textviewRecievedMessage.setText(chatMessage.message);
            binding.datetimerecievedTextView.setText(chatMessage.dateTime);
            if (receiverProfileImage != null)
            {
                binding.imageview.setImageBitmap(receiverProfileImage);
            }
        }
    }
}
