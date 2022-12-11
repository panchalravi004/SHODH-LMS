package com.shodh.lms;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.shodh.lms.adapter.DownloadedEbookAdapter;
import com.shodh.lms.adapter.MyBooksAdapter;
import com.shodh.lms.viewmodel.MyBookLiveViewModel;

import org.json.JSONArray;

import java.io.File;

public class MyBookActivity extends AppCompatActivity {

    private static final String TAG = "LMS_TEST";
    private EditText etSearchBook;
    private ImageButton btnSearchBook,btnMyEbookDialog;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SharedPreferences user;

    private RecyclerView recyclerViewMyBookList;
    private LinearLayoutManager linearLayoutManager;
    private MyBooksAdapter myBooksAdapter;
    public static MyBookLiveViewModel myBookLiveViewModel;
    private JSONArray books;
    public static Context context;
    private DialogLoading dialogLoading;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_book);
        //--------------------Hooks----------------------------
        etSearchBook = (EditText) findViewById(R.id.etSearchBooks);
        btnSearchBook = (ImageButton) findViewById(R.id.btnSearch);
        recyclerViewMyBookList = (RecyclerView) findViewById(R.id.recycleViewMyBooksList);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        user = getSharedPreferences("USER",MODE_PRIVATE);
        btnMyEbookDialog = (ImageButton) findViewById(R.id.btnMyEbooks);
        context = MyBookActivity.this;
        dialogLoading = new DialogLoading(this);


        //--------------------Listener-------------------------
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                myBookLiveViewModel.makeApiCall(MyBookActivity.this,user);
            }
        });
        setRecycleViewMyBookList();
    }

    private void setRecycleViewMyBookList() {

        //set loading dialog
        dialogLoading.show();

        linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerViewMyBookList.setLayoutManager(linearLayoutManager);
        myBooksAdapter = new MyBooksAdapter(this,books);
        recyclerViewMyBookList.setAdapter(myBooksAdapter);
        myBookLiveViewModel = new MyBookLiveViewModel();

        myBookLiveViewModel.getBooks().observe(this, new Observer<JSONArray>() {
            @Override
            public void onChanged(JSONArray jsonArray) {
                if(jsonArray != null){
                    books = jsonArray;
                    myBooksAdapter.updateBooks(jsonArray);
                    swipeRefreshLayout.setRefreshing(false);
                    dialogLoading.dismiss();
                }
            }
        });
        myBookLiveViewModel.makeApiCall(this,user);
    }

    public void openDownloadedEbookDialog(View view) {
        Dialog dialog = new Dialog(this,R.style.DialogStyle);
        dialog.setContentView(R.layout.dialog_downloaded_ebooks);
        ImageButton btnClose = dialog.findViewById(R.id.btnClose);

        RecyclerView recyclerView = dialog.findViewById(R.id.recyclerViewDownloadedEBooks);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            ActivityCompat.requestPermissions((Activity) this,new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);

            File dir = new File("/sdcard/LMS/");
            dir.mkdirs();
            File[] filesList = dir.listFiles();

            DownloadedEbookAdapter downloadedEbookAdapter = new DownloadedEbookAdapter(this,filesList);
            recyclerView.setAdapter(downloadedEbookAdapter);
        }

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.BottomDialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    public void goToBack(View view) {
        finish();
    }
}