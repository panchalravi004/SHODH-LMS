package com.shodh.lms;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.navigation.NavigationView;
import com.shodh.lms.adapter.NewArrivalAdapter;
import com.shodh.lms.adapter.NewNotificationAdapter;
import com.shodh.lms.adapter.NotificationAdapter;
import com.shodh.lms.viewmodel.NewNotificationLiveViewModel;
import com.shodh.lms.viewmodel.NotificationLiveViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class DashboardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private final static String TAG = "LMS_TEST";
    private ImageButton btnMenuBar,btnNotification;
    private TextView tvNotificationCount,tvUserName,tvUserEmail,tvTotalBookCount,tvTotalEBookCount;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageView imgUserProfile;
    private ProgressDialog pd;

    private RecyclerView newArrivalSlider;
    private LinearLayoutManager linearLayoutManager;
    private NewArrivalAdapter newArrivalAdapter;

    private NewNotificationAdapter newNotificationAdapter;
    private NewNotificationLiveViewModel newNotificationLiveViewModel;
    private JSONArray notification;

    private RequestQueue requestQueue;
    private SharedPreferences user;
    private SharedPreferences.Editor userEditor;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        //------------------Hooks-------------------------
        btnMenuBar = (ImageButton) findViewById(R.id.btnMenuBar);
        btnNotification = (ImageButton) findViewById(R.id.btnNotification);
        tvNotificationCount = (TextView) findViewById(R.id.tvNotificationCount);
        tvTotalBookCount = (TextView) findViewById(R.id.tvTotalBookCount);
        tvTotalEBookCount = (TextView) findViewById(R.id.tvTotalEBookCount);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        navigationView = (NavigationView) findViewById(R.id.navigationView);
        newArrivalSlider = (RecyclerView) findViewById(R.id.newArrivalSlider);
        pd = new ProgressDialog(this);

        requestQueue = Volley.newRequestQueue(this);
        user = getSharedPreferences("USER",MODE_PRIVATE);
        userEditor = user.edit();

        //-------------------Listener-------------------------
        btnMenuBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        btnNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openNewNotificationDialog();
            }
        });

        //set navigation listener to click the item
        navigationView.setNavigationItemSelectedListener(this);

        //set recycle view for new arrival slider
        setSliderRecycleView();
        setNavHeader();
        setDashboardData();
        fetchNotification();
    }

    private void setNavHeader(){
        tvUserName = navigationView.getHeaderView(0).findViewById(R.id.tvUserNameNavHeader);
        tvUserEmail = navigationView.getHeaderView(0).findViewById(R.id.tvUserEmailNavHeader);
        imgUserProfile = navigationView.getHeaderView(0).findViewById(R.id.imgUserProfileNavHeader);

        tvUserName.setText(user.getString("fname","fname").toUpperCase(Locale.ROOT)+" "+user.getString("lname","lname").toUpperCase(Locale.ROOT));
        tvUserEmail.setText(user.getString("email","email"));

//        if(!user.getString("image","image").equals("")){
//            imgUserProfile.setImageBitmap(ImageConvert.getStringBitmap(user.getString("image","image")));
//        }

        ImageRequest imageRequest = new ImageRequest(
                Constants.IMAGE_BASE_PATH + user.getString("image_url","image_url"), new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                imgUserProfile.setImageBitmap(response);
            }
        }, 100, 100, null, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, "onErrorResponse: "+error.getMessage());
            }
        });

        requestQueue.add(imageRequest);
    }

    private void setDashboardData(){
        CacheRequest cacheRequest = new CacheRequest(
            Request.Method.GET,
            Constants.GET_DASHBOARD_COUNT,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i(TAG, "onResponse: "+response);
                    try {
                        JSONObject jsonObject = new JSONObject(response).getJSONObject("dashboard_data");
                        tvTotalBookCount.setText(jsonObject.getString("total_books"));
                        tvTotalEBookCount.setText(jsonObject.getString("total_ebooks"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.i(TAG, "onErrorResponse: "+error.getMessage());
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
        requestQueue.add(cacheRequest);
    }

    private void fetchNotification(){

        newNotificationAdapter = new NewNotificationAdapter(DashboardActivity.this,notification);

        newNotificationLiveViewModel = new NewNotificationLiveViewModel();
        newNotificationLiveViewModel.getNotification().observe(this, new Observer<JSONArray>() {
            @Override
            public void onChanged(JSONArray jsonArray) {
                if(jsonArray != null){
                    notification = jsonArray;
                    tvNotificationCount.setText(String.valueOf(jsonArray.length()));
                    newNotificationAdapter.updateNotification(jsonArray);
                }
            }
        });

        newNotificationLiveViewModel.makeApiCall(this,user);
    }

    private void openNewNotificationDialog() {

        Dialog dialog = new Dialog(this,R.style.DialogStyle);
        dialog.setContentView(R.layout.dialog_new_notification);

        TextView btnMarkAll = dialog.findViewById(R.id.btnMarkAll);

        dialog.show();
        dialog.getWindow().setDimAmount(0);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.TOP|Gravity.RIGHT);

        RecyclerView recyclerView = dialog.findViewById(R.id.recycleViewNewNotification);
        LinearLayoutManager llm = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(llm);

        recyclerView.setAdapter(newNotificationAdapter);

        btnMarkAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pd.setMessage("Mark All ...");
                pd.show();
                StringRequest markAllRequest = new StringRequest(
                        Request.Method.GET,
                        Constants.MARK_ALL_NOTIFICATION,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.i(TAG, "onResponse: "+response);
                                pd.dismiss();
                                newNotificationLiveViewModel.makeApiCall(DashboardActivity.this,user);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                pd.dismiss();
                                Log.i(TAG, "onErrorResponse: "+error.getMessage());
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
                requestQueue.add(markAllRequest);

            }
        });

    }

    private void setSliderRecycleView() {

        linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        newArrivalSlider.setLayoutManager(linearLayoutManager);
        newArrivalAdapter = new NewArrivalAdapter(this);
        newArrivalSlider.setAdapter(newArrivalAdapter);

        LinearSnapHelper linearSnapHelper = new LinearSnapHelper();
        linearSnapHelper.attachToRecyclerView(newArrivalSlider);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {

                if(linearLayoutManager.findLastCompletelyVisibleItemPosition() < (newArrivalAdapter.getItemCount()-1)){
                    linearLayoutManager.smoothScrollToPosition(newArrivalSlider,new RecyclerView.State(),linearLayoutManager.findLastCompletelyVisibleItemPosition()+1);
                }else{
                    linearLayoutManager.smoothScrollToPosition(newArrivalSlider,new RecyclerView.State(),0);
                }

            }
        },0,3000);

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.profile:
                startActivity(new Intent(this,ProfileActivity.class));
                break;
            case R.id.ebooks:
                Intent i = new Intent(this,BooksActivity.class);
                i.putExtra("DATA","EBOOKS");
                startActivity(i);
                break;
            case R.id.books:
                Intent ibooks = new Intent(this,BooksActivity.class);
                ibooks.putExtra("DATA","BOOKS");
                startActivity(ibooks);
                break;
            case R.id.notification:
                startActivity(new Intent(this,NotificationActivity.class));
                break;
            case R.id.faqs:
                startActivity(new Intent(this,MessageActivity.class));
                break;
            case R.id.logout:
                logout();
                break;
        }

        Toast.makeText(this, item.getTitle(), Toast.LENGTH_SHORT).show();

        return true;
    }

    private void logout() {

        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                Constants.STUDENT_LOGOUT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, "onResponse: "+response);
                        userEditor.clear();
                        userEditor.apply();
                        startActivity(new Intent(DashboardActivity.this,LoginActivity.class));
                        finish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i(TAG, "onErrorResponse: "+error.getMessage());
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
}