package com.shodh.lms;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.shodh.lms.adapter.MyBooksAdapter;

public class MyBookActivity extends AppCompatActivity {

    private static final String TAG = "LMS_TEST";
    private EditText etSearchBook;
    private ImageButton btnSearchBook;
    private SharedPreferences user;

    private RecyclerView recyclerViewMyBookList;
    private LinearLayoutManager linearLayoutManager;
    private MyBooksAdapter myBooksAdapter;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_book);
        //--------------------Hooks----------------------------
        etSearchBook = (EditText) findViewById(R.id.etSearchBooks);
        btnSearchBook = (ImageButton) findViewById(R.id.btnSearch);
        recyclerViewMyBookList = (RecyclerView) findViewById(R.id.recycleViewMyBooksList);
        user = getSharedPreferences("USER",MODE_PRIVATE);


        //--------------------Listener-------------------------

        setRecycleViewMyBookList();
    }

    private void setRecycleViewMyBookList() {

        linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerViewMyBookList.setLayoutManager(linearLayoutManager);
        myBooksAdapter = new MyBooksAdapter(this);
        recyclerViewMyBookList.setAdapter(myBooksAdapter);
    }

    public void goToBack(View view) {
        finish();
    }
}