package com.shodh.lms;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.util.Timer;
import java.util.TimerTask;

public class DashboardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ImageButton btnMenuBar,btnNotification,btnGetBookList;
    private TextView tvNotificationCount,tvMyBookCount;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private RecyclerView newArrivalSlider;
    private LinearLayoutManager linearLayoutManager;
    private NewArrivalAdapter newArrivalAdapter;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        //------------------Hooks-------------------------
        btnMenuBar = (ImageButton) findViewById(R.id.btnMenuBar);
        btnNotification = (ImageButton) findViewById(R.id.btnNotification);
        btnGetBookList = (ImageButton) findViewById(R.id.btnGetBookList);
        tvNotificationCount = (TextView) findViewById(R.id.tvNotificationCount);
        tvMyBookCount = (TextView) findViewById(R.id.tvMyBooksCount);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        navigationView = (NavigationView) findViewById(R.id.navigationView);
        newArrivalSlider = (RecyclerView) findViewById(R.id.newArrivalSlider);

        //-------------------Listener-------------------------
        btnMenuBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        //set navigation listener to click the item
        navigationView.setNavigationItemSelectedListener(this);

        //set recycle view for new arrival slider
        setSliderRecycleView();
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
            case R.id.notification:
                startActivity(new Intent(this,NotificationActivity.class));
                break;
        }

        Toast.makeText(this, item.getTitle(), Toast.LENGTH_SHORT).show();

        return true;
    }
}