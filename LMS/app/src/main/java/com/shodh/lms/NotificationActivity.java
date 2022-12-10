package com.shodh.lms;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.shodh.lms.adapter.NotificationAdapter;
import com.shodh.lms.viewmodel.NotificationLiveViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class NotificationActivity extends AppCompatActivity {

    private static final String TAG = "LMS_TEST";
    private Button btnClearAll;
    private ProgressDialog pd;
    private SwipeRefreshLayout swipeRefreshLayout;

    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private NotificationAdapter notificationAdapter;
    private JSONArray notification;
    private NotificationLiveViewModel notificationLiveViewModel;

    private SharedPreferences user;
    private RequestQueue requestQueue;

    private DialogLoading dialogLoading;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        //-------------------Hooks--------------------------
        btnClearAll = (Button) findViewById(R.id.btnClearAll);
        recyclerView = (RecyclerView) findViewById(R.id.recycleViewNotification);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        pd = new ProgressDialog(this);
        user = getSharedPreferences("USER",MODE_PRIVATE);
        requestQueue = Volley.newRequestQueue(this);
        notificationLiveViewModel = new NotificationLiveViewModel();


        //---------------------Listener---------------------

        btnClearAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearAllNotification();
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                notificationLiveViewModel.makeApiCall(NotificationActivity.this,user);
            }
        });
        setNotificationRecycleView();

    }

    private void clearAllNotification() {
        pd.setMessage("Clearing...");
        pd.show();
        StringRequest stringRequest = new StringRequest(
            Request.Method.DELETE,
            Constants.CLEAR_ALL_NOTIFICATION,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i(TAG, "onResponse: "+response);
                    pd.dismiss();
                    notificationLiveViewModel.makeApiCall(NotificationActivity.this,user);
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
        requestQueue.add(stringRequest);
    }

    private void setNotificationRecycleView() {

        //set loading dialog
        dialogLoading = new DialogLoading(this);
        dialogLoading.show();

        linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);

        notificationAdapter = new NotificationAdapter(NotificationActivity.this,notification,user);
        recyclerView.setAdapter(notificationAdapter);

        notificationLiveViewModel.getNotification().observe(this, new Observer<JSONArray>() {
            @Override
            public void onChanged(JSONArray jsonArray) {
                if(jsonArray != null){
                    notification = jsonArray;
                    notificationAdapter.updateNotification(jsonArray);
                    swipeRefreshLayout.setRefreshing(false);
                    dialogLoading.dismiss();
                }
            }
        });

        notificationLiveViewModel.makeApiCall(this,user);

    }

    public void goToBack(View view) {
        finish();
    }
}