package com.example.alaap;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import androidx.annotation.NonNull;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {

    Context context;
    List<PostItem>list;


    public CustomAdapter(Context context, List<PostItem> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item,parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        PostItem postItem = list.get(position);
        holder.usernametextview.setText(postItem.getUsername());
        holder.userposttextview.setText(postItem.getUserpost());

        String imageUri=null;
        imageUri = postItem.getImgstring();
        if(imageUri == null){ holder.postimageview.setVisibility(View.GONE); }
        else {
            holder.postimageview.setVisibility(View.VISIBLE);
            Picasso.get().load(imageUri).into(holder.postimageview);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView userposttextview;
        TextView usernametextview;
        ImageView postimageview;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            usernametextview = itemView.findViewById(R.id.usernameid);
            userposttextview = itemView.findViewById(R.id.userpostid);
            postimageview = itemView.findViewById(R.id.posimgtid);
        }

//    private Activity context;
//    private List<PostItem> postitemList;
//
//    public CustomAdapter(Activity context,List<PostItem> postitemList) {
//        super(context, R.layout.sample_view, postitemList);
//        this.context = context;
//        this.postitemList = postitemList;
//    }
//
//    @NonNull
//    @Override
//    public View getView(int position, View convertView,ViewGroup parent) {
//        LayoutInflater layoutInflater = context.getLayoutInflater();
//        View view = layoutInflater.inflate(R.layout.sample_view,null,true);
//
//        PostItem postitem = postitemList.get(position);
//        TextView posttextview = view.findViewById(R.id.postid);
//
//        posttextview.setText(postitem.getUserpost());
//
//        return view;
//    }
}
}
