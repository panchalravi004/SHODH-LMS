package com.shodh.lms.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.shodh.lms.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
    Context context;
    JSONArray books;

    public void updateBooks(JSONArray books){
        this.books = books;
        this.notifyDataSetChanged();
    }

    public HistoryAdapter(Context context,JSONArray books){
        this.context = context;
        this.books = books;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_book_history,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(), android.R.anim.slide_in_left);
        holder.itemView.setAnimation(animation);

        try {
            JSONObject data = books.getJSONObject(position);
            holder.tvTitle.setText(data.getString("title"));
            holder.tvIssueDate.setText("Issue Date "+data.getString("issue_date"));

            if(data.getString("status").equals("L")){
                holder.tvLostFee.setText("Rs. "+data.getInt("price"));
                holder.tvLateFee.setText("Rs. "+(data.getInt("fine")-data.getInt("price")));
                holder.tvReturnDate.setText("LOST");
                holder.tvReturnDate.setBackgroundResource(R.drawable.lms_orange_all_round_white_border);
                holder.viewCircle.setBackgroundResource(R.drawable.lms_orange_circle);
            }else{
                holder.tvLostFee.setText("Rs. 0");
                holder.tvLateFee.setText("Rs. "+data.getInt("fine"));
                holder.tvReturnDate.setText("Return on "+data.getString("return_date"));
                holder.tvReturnDate.setBackgroundResource(R.drawable.lms_green_all_round_white_border);
                holder.viewCircle.setBackgroundResource(R.drawable.lms_green_circle);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        holder.btnShowMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.tableLayoutMoreHistory.getVisibility() == View.GONE) {
                    holder.tableLayoutMoreHistory.setVisibility(View.VISIBLE);
                    holder.btnShowMore.animate().rotation(90).setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(400);
                    holder.viewCircle.animate().scaleX(5).scaleY(5).setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(400);
                }else{
                    holder.tableLayoutMoreHistory.setVisibility(View.GONE);
                    holder.btnShowMore.animate().rotation(0).setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(400);
                    holder.viewCircle.animate().scaleX(3).scaleY(3).setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(400);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        if(books != null){
            return books.length();
        }else{
            return 0;
        }
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

        TextView tvTitle,tvLateFee,tvLostFee,tvIssueDate,tvReturnDate;
        ImageButton btnShowMore;
        View viewCircle;
        TableLayout tableLayoutMoreHistory;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvBookHistoryTitle);
            tvLateFee = itemView.findViewById(R.id.tvBookHistoryLateFee);
            tvLostFee = itemView.findViewById(R.id.tvBookHistoryLostFee);
            tvIssueDate = itemView.findViewById(R.id.tvBookHistoryIssueDate);
            tvReturnDate = itemView.findViewById(R.id.tvBookHistoryReturnDate);
            btnShowMore = itemView.findViewById(R.id.btnHistoryShowMore);
            viewCircle = itemView.findViewById(R.id.viewGreenCircle);
            tableLayoutMoreHistory = itemView.findViewById(R.id.tableLayoutMoreHistory);
        }
    }
}
