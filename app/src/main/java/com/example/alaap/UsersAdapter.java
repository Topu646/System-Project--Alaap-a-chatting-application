package com.example.alaap;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.alaap.databinding.ItemContainerUserBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserViewHolder>{

    private final List<Users> usersList;
    private final UserListener userListener;

    public UsersAdapter(List<Users> usersList , UserListener userListener) {
        this.usersList = usersList;
        this.userListener = userListener;
    }


    class UserViewHolder extends RecyclerView.ViewHolder{
        ItemContainerUserBinding binding;

        public UserViewHolder(ItemContainerUserBinding itemContainerUserBinding) {
            super(itemContainerUserBinding.getRoot());
            binding = itemContainerUserBinding;
        }

        void setUserData(Users users)
        {
            binding.nametextview.setText(users.name);
            binding.emailtextview.setText(users.email);
            binding.imageview.setImageBitmap(getUserImage(users.image));
            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    userListener.onUserClicked(users);
                }
            });
        }
    }

    private Bitmap getUserImage(String encodedImage)
    {

        if (encodedImage != null) {
            byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        } else {
            // Handle the case where encodedImage is null
            return null;
        }

    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemContainerUserBinding itemContainerUserBinding = ItemContainerUserBinding.inflate(LayoutInflater.from(
                parent.getContext()),parent,false);
        return new UserViewHolder(itemContainerUserBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {

        holder.setUserData(usersList.get(position));
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }
}
