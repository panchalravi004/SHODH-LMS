package com.shodh.lms.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.shodh.lms.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class NewNotificationAdapter extends RecyclerView.Adapter<NewNotificationAdapter.ViewHolder> {
    Context context;
    JSONArray notification;
    public NewNotificationAdapter(Context context,JSONArray notification){
        this.context = context;
        this.notification = notification;
    }

    public void updateNotification(JSONArray notification){
        this.notification = notification;
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_new_notification_v2,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            JSONObject data = notification.getJSONObject(position);
            if(data.getString("title").length() > 25){
                holder.title.setText(data.getString("title").substring(0,25)+"...");
            }else{
                holder.title.setText(data.getString("title"));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return notification.length();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView title;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tvNewNotificationTitle);
        }
    }
}
