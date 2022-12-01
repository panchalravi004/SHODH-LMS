package com.shodh.lms;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class NotificationActivity extends AppCompatActivity {

    private Button btnClearAll;

    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private NotificationAdapter notificationAdapter;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        //-------------------Hooks--------------------------
        btnClearAll = (Button) findViewById(R.id.btnClearAll);
        recyclerView = (RecyclerView) findViewById(R.id.recycleViewNotification);

        //---------------------Listener---------------------

        setNotificationRecycleView();

    }

    private void setNotificationRecycleView() {
        linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);
        notificationAdapter = new NotificationAdapter(this);
        recyclerView.setAdapter(notificationAdapter);
    }

    public void goToBack(View view) {
        finish();
    }
}