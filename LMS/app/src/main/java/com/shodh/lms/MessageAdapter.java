package com.shodh.lms;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    Context context;
    public MessageAdapter(Context context){
        this.context = context;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_message,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String userMsg = "I face some technical issue while using your android application please help";
        Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(), android.R.anim.slide_in_left);
        holder.itemView.setAnimation(animation);

        holder.tvMsgUserMessage.setText(userMsg.substring(0,41)+"...");

        holder.btnOpenMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.tvMsgReplyTitle.getVisibility() == View.GONE){

                    holder.tvMsgReplyTitle.setVisibility(View.VISIBLE);
                    holder.tvMsgReplyMessage.setVisibility(View.VISIBLE);
                    holder.tvMsgUserMessage.setText(userMsg);

                    holder.btnOpenMore.animate().rotation(90).setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(50);

                }else{
                    holder.tvMsgReplyTitle.setVisibility(View.GONE);
                    holder.tvMsgReplyMessage.setVisibility(View.GONE);
                    holder.tvMsgUserMessage.setText(userMsg.substring(0,41)+"...");

                    holder.btnOpenMore.animate().rotation(0).setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(50);

                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return 15;
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
        TextView tvMsgTitle,tvMsgUserMessage,tvMsgReplyTitle,tvMsgReplyMessage;
        ImageButton btnOpenMore;
        ImageView imgCheck;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMsgTitle = itemView.findViewById(R.id.tvMsgTitle);
            tvMsgUserMessage = itemView.findViewById(R.id.tvMsgUserMessage);
            tvMsgReplyTitle = itemView.findViewById(R.id.tvMsgReplyTitle);
            tvMsgReplyMessage = itemView.findViewById(R.id.tvMsgReplyMessage);

            btnOpenMore = itemView.findViewById(R.id.btnMsgOpenMore);
            imgCheck = itemView.findViewById(R.id.imgMsgCheck);

        }
    }
}
