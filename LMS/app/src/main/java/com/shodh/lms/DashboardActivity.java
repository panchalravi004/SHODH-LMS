package com.shodh.lms;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class DashboardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private final static String TAG = "LMS_TEST";
    private ImageButton btnMenuBar,btnNotification;
    private TextView tvNotificationCount,tvUserName,tvUserEmail;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageView imgUserProfile;

    private RecyclerView newArrivalSlider;
    private LinearLayoutManager linearLayoutManager;
    private NewArrivalAdapter newArrivalAdapter;

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
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        navigationView = (NavigationView) findViewById(R.id.navigationView);
        newArrivalSlider = (RecyclerView) findViewById(R.id.newArrivalSlider);

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

    private void openNewNotificationDialog() {

        Dialog dialog = new Dialog(this,R.style.DialogStyle);
        dialog.setContentView(R.layout.dialog_new_notification);

        RecyclerView recyclerView = dialog.findViewById(R.id.recycleViewNewNotification);
        LinearLayoutManager llm = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(llm);
        NewNotificationAdapter newNotificationAdapter = new NewNotificationAdapter(this);
        recyclerView.setAdapter(newNotificationAdapter);

        dialog.show();
        dialog.getWindow().setDimAmount(0);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.TOP|Gravity.RIGHT);


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