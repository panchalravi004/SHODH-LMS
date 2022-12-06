package com.shodh.lms.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.shodh.lms.R;

public class NewArrivalAdapter extends RecyclerView.Adapter<NewArrivalAdapter.ViewHolder> {

    Context context;

    public NewArrivalAdapter(Context context){
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_book_card_v2,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 5;
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgBook,imgTeacher,imgVerified;
        TextView tvTitle,tvDescription,tvAuthorName,tvTeacherName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgBook = itemView.findViewById(R.id.imgBook);
            imgTeacher = itemView.findViewById(R.id.imgTeacher);
            imgVerified = itemView.findViewById(R.id.imgVerified);
            tvTitle = itemView.findViewById(R.id.tvBookTitle);
            tvDescription = itemView.findViewById(R.id.tvBookDescription);
            tvAuthorName = itemView.findViewById(R.id.tvAuthorName);
            tvTeacherName = itemView.findViewById(R.id.tvTeacherName);

        }
    }
}
