package com.shodh.lms;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.shodh.lms.adapter.HistoryAdapter;
import com.shodh.lms.viewmodel.HistoryLiveViewModel;

import org.json.JSONArray;

public class HistoryActivity extends AppCompatActivity {

    private static final String TAG = "LMS_TEST";
    private SharedPreferences user;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerViewHistory;
    private LinearLayoutManager linearLayoutManager;
    private HistoryAdapter historyAdapter;
    private HistoryLiveViewModel historyLiveViewModel;
    private JSONArray books;

    private DialogLoading dialogLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        //-------------------Hooks----------------------------
        user = getSharedPreferences("USER",MODE_PRIVATE);
        recyclerViewHistory = (RecyclerView) findViewById(R.id.recycleViewHistory);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);

        //set loading dialog
        dialogLoading = new DialogLoading(this);
        dialogLoading.show();

        //------------------Listener--------------------------
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                historyLiveViewModel.makeApiCall(HistoryActivity.this,user);
            }
        });
        setRecycleViewHistory();
    }

    private void setRecycleViewHistory() {
        linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerViewHistory.setLayoutManager(linearLayoutManager);

        historyAdapter = new HistoryAdapter(this,books);
        recyclerViewHistory.setAdapter(historyAdapter);

        historyLiveViewModel = new HistoryLiveViewModel();

        historyLiveViewModel.getBooks().observe(this, new Observer<JSONArray>() {
            @Override
            public void onChanged(JSONArray jsonArray) {
                if(jsonArray != null){
                    books = jsonArray;
                    historyAdapter.updateBooks(jsonArray);
                    swipeRefreshLayout.setRefreshing(false);
                    dialogLoading.dismiss();
                }
            }
        });

        historyLiveViewModel.makeApiCall(this,user);

    }

    public void goToBack(View view) {
        finish();
    }
}