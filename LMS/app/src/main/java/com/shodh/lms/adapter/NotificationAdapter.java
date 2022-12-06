package com.shodh.lms.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.shodh.lms.Constants;
import com.shodh.lms.R;
import com.shodh.lms.viewmodel.NotificationLiveViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private static final String TAG = "LMS_TEST";
    private Context context;
    private ProgressDialog pd;
    private JSONArray notification;
    private NotificationLiveViewModel notificationLiveViewModel;
    private SharedPreferences user;


    public NotificationAdapter(Context context,JSONArray notification,SharedPreferences user){
        this.context = context;
        this.notification = notification;
        this.user = user;
        pd = new ProgressDialog(context);
    }

    public void updateNotification(JSONArray notification) {
        this.notification = notification;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_notification_v2,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(), android.R.anim.slide_in_left);
        holder.itemView.startAnimation(animation);

        try {
            JSONObject data = notification.getJSONObject(position);
            if(data.getString("title").length() > 25){
                holder.tvBookTitle.setText(data.getString("title").substring(0,25)+"...");
            }else{
                holder.tvBookTitle.setText(data.getString("title"));
            }

            if(data.getString("status").equals("D")){
                holder.btnMarkRead.setVisibility(View.GONE);
                holder.imgCheck.setVisibility(View.VISIBLE);
            }
            if(data.getString("status").equals("A")){
                holder.btnMarkRead.setVisibility(View.VISIBLE);
                holder.imgCheck.setVisibility(View.GONE);

                String id = data.getString("id");

                holder.btnMarkRead.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        pd.setMessage("Mark...");
                        pd.show();
                        StringRequest stringRequest = new StringRequest(
                            Request.Method.GET,
                            Constants.MARK_SINGLE_NOTIFICATION+id,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    Log.i(TAG, "onResponse: "+response);
                                    pd.dismiss();
                                    notificationLiveViewModel = new NotificationLiveViewModel();
                                    notificationLiveViewModel.getNotification().observe((LifecycleOwner) context, new Observer<JSONArray>() {
                                        @Override
                                        public void onChanged(JSONArray jsonArray) {
                                            if(jsonArray != null){
                                                updateNotification(jsonArray);
                                            }
                                        }
                                    });

                                    notificationLiveViewModel.makeApiCall(context,user);
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.i(TAG, "onErrorResponse: "+error.getMessage());
                                    pd.dismiss();
                                }
                            }){
                            @Override
                            public Map<String, String> getHeaders() throws AuthFailureError {
                                String bearer = user.getString("token_type","token_type")+" "+user.getString("access_token","access_token");
                                Map<String, String> headersSys = super.getHeaders();
                                Map<String, String> headers = new HashMap<String, String>();
                                headersSys.remove("Authorization");
                                headers.put("Authorization", bearer);
                                headers.putAll(headersSys);
                                return headers;
                            }
                        };
                        RequestQueue requestQueue = Volley.newRequestQueue(context);
                        requestQueue.add(stringRequest);
                    }
                });
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        if(notification != null){
            return notification.length();

        }else{
            return 0;
        }
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
        TextView tvBookTitle;
        Button btnMarkRead;
        ImageView imgCheck;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBookTitle = itemView.findViewById(R.id.tvBookTitle);
            btnMarkRead = itemView.findViewById(R.id.btnMarkAsRead);
            imgCheck = itemView.findViewById(R.id.btnCheck);
        }
    }
}
