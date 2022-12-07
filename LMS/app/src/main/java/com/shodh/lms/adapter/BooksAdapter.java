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

public class BooksAdapter extends RecyclerView.Adapter<BooksAdapter.ViewHolder> {
    Context context;
    JSONArray books;
    String BOOK_TYPE;
    public BooksAdapter(Context context,JSONArray books,String BOOK_TYPE){
        this.context = context;
        this.books = books;
        this.BOOK_TYPE = BOOK_TYPE;
    }

    public void updateBooks(JSONArray books){
        this.books = books;
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_book_card_v2,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(), android.R.anim.slide_in_left);
        holder.itemView.startAnimation(animation);

        try {
            JSONObject data = books.getJSONObject(position);
            holder.tvBookTitle.setText(data.getString("title"));
            holder.tvBookCategory.setText("Category : "+data.getString("category"));
            holder.tvBookAuthor.setText("Author : "+data.getString("author"));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {;
                    Intent i = new Intent(context, ViewBookActivity.class);
                    i.putExtra("ACTION","VIEW_BOOK");
                    i.putExtra("BOOK_TYPE",BOOK_TYPE);
                    try {
                        i.putExtra("BOOK_ID",data.getString("id"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    context.startActivity(i);

                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    @Override
    public int getItemCount() {
        if(books != null){
            return books.length();
        }else{
            return 0;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvBookTitle,tvBookCategory,tvBookAuthor;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBookTitle = itemView.findViewById(R.id.tvBookTitle);
            tvBookCategory = itemView.findViewById(R.id.tvBookCategory);
            tvBookAuthor = itemView.findViewById(R.id.tvAuthorName);

        }
    }
}
