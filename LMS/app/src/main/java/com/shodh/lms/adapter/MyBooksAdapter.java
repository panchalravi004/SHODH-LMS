package com.shodh.lms.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.shodh.lms.R;
import com.shodh.lms.ViewBookActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MyBooksAdapter extends RecyclerView.Adapter<MyBooksAdapter.ViewHolder> {
    Context context;
//    JSONArray books;
    public MyBooksAdapter(Context context){
        this.context = context;
//        this.books = books;
    }

//    public void updateBooks(JSONArray books){
//        this.books = books;
//        this.notifyDataSetChanged();
//    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_my_book,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(), android.R.anim.slide_in_left);
        holder.itemView.startAnimation(animation);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {;
                Intent i = new Intent(context, ViewBookActivity.class);
                i.putExtra("ACTION","MY_BOOK");
                i.putExtra("BOOK_TYPE","BOOKS");
                i.putExtra("BOOK_ID","1");
                context.startActivity(i);

            }
        });
    }

    @Override
    public int getItemCount() {
        return 5;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
