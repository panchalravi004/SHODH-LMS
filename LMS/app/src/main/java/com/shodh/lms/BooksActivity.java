package com.shodh.lms;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.shodh.lms.adapter.BooksAdapter;
import com.shodh.lms.viewmodel.BookLiveViewModel;

import org.json.JSONArray;

public class BooksActivity extends AppCompatActivity {

    private TextView tvBookActivityTitle;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private BooksAdapter booksAdapter;
    private BookLiveViewModel bookLiveViewModel;
    private JSONArray books;
    private SharedPreferences user;
    private Intent intent;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_books);
        //---------------------Hooks----------------
        recyclerView = (RecyclerView) findViewById(R.id.recycleViewBooksList);
        tvBookActivityTitle = (TextView) findViewById(R.id.tvBookActivityTitle);
        user = getSharedPreferences("USER",MODE_PRIVATE);
        intent = getIntent();
        bookLiveViewModel = new BookLiveViewModel(intent.getStringExtra("BOOK_TYPE"));

        //----------------------Listener-----------------

        setBooksListRecycleView();
        setActivityTitle(intent.getStringExtra("BOOK_TYPE"));
    }

    private void setActivityTitle(String type){
        if(type.equals("BOOKS")){
            tvBookActivityTitle.setText("Books List");
        }else if(type.equals("EBOOKS")){
            tvBookActivityTitle.setText("EBooks List");
        }
    }

    private void setBooksListRecycleView() {

        linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);

        booksAdapter = new BooksAdapter(this,books,intent.getStringExtra("BOOK_TYPE"));
        recyclerView.setAdapter(booksAdapter);

        bookLiveViewModel.getBooks().observe(this, new Observer<JSONArray>() {
            @Override
            public void onChanged(JSONArray jsonArray) {
                if(jsonArray != null){
                    books = jsonArray;
                    booksAdapter.updateBooks(jsonArray);
                }
            }
        });
        bookLiveViewModel.makeApiCall(this,user);

    }

    public void goToBack(View view) {
        finish();
    }
}