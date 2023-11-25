package com.example.alaap;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {

    Context context;
    ArrayList<PostItem>list;


    public CustomAdapter(Context context, ArrayList<PostItem> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item,parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        PostItem postItem = list.get(position);
        holder.usernametextview.setText(postItem.getUsername());
        holder.userposttextview.setText(postItem.getUserpost());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView userposttextview;
        TextView usernametextview;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            usernametextview = itemView.findViewById(R.id.usernameid);
            userposttextview = itemView.findViewById(R.id.userpostid);
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
