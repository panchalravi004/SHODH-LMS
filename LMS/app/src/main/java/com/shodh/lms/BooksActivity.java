package com.shodh.lms;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import com.shodh.lms.adapter.BooksAdapter;

public class BooksActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private BooksAdapter booksAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_books);
        //---------------------Hooks----------------
        recyclerView = (RecyclerView) findViewById(R.id.recycleViewBooksList);

        //----------------------Listener-----------------


        setBooksListRecycleView();
    }

    private void setBooksListRecycleView() {

        linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);
        booksAdapter = new BooksAdapter(this);
        recyclerView.setAdapter(booksAdapter);

    }

    public void goToBack(View view) {
        finish();
    }
}