package com.shodh.lms;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.shodh.lms.adapter.BooksAdapter;
import com.shodh.lms.viewmodel.BookLiveViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

public class BooksActivity extends AppCompatActivity {

    private TextView tvBookActivityTitle;
    private EditText etSearchBook;
    private ImageButton btnSearch;
    private SwipeRefreshLayout swipeRefreshLayout;
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
        etSearchBook = (EditText) findViewById(R.id.etSearchBooks);
        btnSearch = (ImageButton) findViewById(R.id.btnSearch);
        tvBookActivityTitle = (TextView) findViewById(R.id.tvBookActivityTitle);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        user = getSharedPreferences("USER",MODE_PRIVATE);
        intent = getIntent();
        bookLiveViewModel = new BookLiveViewModel(intent.getStringExtra("BOOK_TYPE"));

        //----------------------Listener-----------------
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchBook();
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                bookLiveViewModel.makeApiCall(BooksActivity.this,user);
            }
        });

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
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
        bookLiveViewModel.makeApiCall(this,user);

    }

    private void searchBook(){
        String searchText = etSearchBook.getText().toString();
        JSONArray searchedBook = new JSONArray();
        if(!searchText.equals("")){
            if(books != null){
                for (int i = 0; i < books.length(); i++) {
                    try {
                        JSONObject jo = books.getJSONObject(i);
                        if(jo.getString("title").toLowerCase().contains(searchText.toLowerCase())){
                            searchedBook.put(jo);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
            booksAdapter.updateBooks(searchedBook);
        }else{
            booksAdapter.updateBooks(books);
        }
    }
    public void goToBack(View view) {
        finish();
    }
}