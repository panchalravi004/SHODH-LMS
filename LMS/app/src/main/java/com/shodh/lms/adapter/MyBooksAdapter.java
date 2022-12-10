package com.shodh.lms.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.shodh.lms.R;
import com.shodh.lms.ViewBookActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

public class MyBooksAdapter extends RecyclerView.Adapter<MyBooksAdapter.ViewHolder> {
    Context context;
    JSONArray books;
    public MyBooksAdapter(Context context,JSONArray books){
        this.context = context;
        this.books = books;
    }

    public void updateBooks(JSONArray books){
        this.books = books;
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_my_book,parent,false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(), android.R.anim.slide_in_left);
        holder.itemView.startAnimation(animation);
        try {
            JSONObject data = books.getJSONObject(position);
            holder.tvBookTitle.setText(data.getString("title"));
            holder.tvIssueDate.setText("Issue Date : "+data.getString("issue_date"));

            Date date = new Date();
            Date expected_return_date = new SimpleDateFormat("yyyy-MM-dd").parse(data.getString("expected_return_date"));

            if (expected_return_date.after(date)){
                int y = expected_return_date.getYear() - date.getYear();
                int m = (y * 12) + (expected_return_date.getMonth() - date.getMonth());
                int diff = (m * 30) + (expected_return_date.getDate() - date.getDate());
                holder.tvDaysLeft.setText(String.valueOf(diff)+" Days Left");
                holder.tvDaysLeft.setBackgroundResource(R.drawable.lms_orange_all_round_white_border);
            }else{
                int y =date.getYear() - expected_return_date.getYear();
                int m = (y * 12) + (date.getMonth() - expected_return_date.getMonth());
                int diff = (m * 30) + (date.getDate() - expected_return_date.getDate());
                holder.tvDaysLeft.setText(String.valueOf(diff)+" Days Over");
                holder.tvDaysLeft.setBackgroundResource(R.drawable.lms_red_all_round_white_border);
            }


            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(context, ViewBookActivity.class);
                    i.putExtra("ACTION","MY_BOOKS");
                    i.putExtra("BOOK_TYPE","MY_BOOKS");
                    try {
                        i.putExtra("BOOK_ISSUE_ID", data.getString("id"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    context.startActivity(i);

                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
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
        TextView tvBookTitle,tvIssueDate,tvDaysLeft;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBookTitle = itemView.findViewById(R.id.tvMyBookTitle);
            tvIssueDate = itemView.findViewById(R.id.tvMyBookIssueDate);
            tvDaysLeft = itemView.findViewById(R.id.tvMyBookReturnDayLeft);
        }
    }
}
