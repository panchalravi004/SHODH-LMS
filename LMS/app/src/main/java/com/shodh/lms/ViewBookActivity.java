package com.shodh.lms;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.shodh.lms.request.CacheRequest;
import com.shodh.lms.request.FileRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ViewBookActivity extends AppCompatActivity {

    private static final String TAG = "LMS_TEST";
    private RequestQueue requestQueue;
    private Intent intent;
    private SharedPreferences user;
    private TextView tvTitle,tvAuthor,tvPublisher,tvPrice,tvDate,tvCategory,tvRecommendedBy,tvBookAvailable,tvReturnLastDate,tvLostCharges,tvLateFee;
    private Button btnLostBook,btnPayFee,btnNotify;
    private ImageButton btnDownloadPdf,btnOpenBottomDialog;
    private ProgressDialog pd;
    private JSONObject book_detail;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_book_v2);
        //----------------hooks------------------
        intent = getIntent();
        requestQueue = Volley.newRequestQueue(this);
        user = getSharedPreferences("USER",MODE_PRIVATE);
        btnDownloadPdf = (ImageButton) findViewById(R.id.btnDownloadBookPdf);
        btnOpenBottomDialog = (ImageButton) findViewById(R.id.btnOpenBottomDialog);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvAuthor = (TextView) findViewById(R.id.tvAuthor);
        tvPublisher = (TextView) findViewById(R.id.tvPublisher);
        tvPrice = (TextView) findViewById(R.id.tvPrice);
        tvDate = (TextView) findViewById(R.id.tvDate);
        tvCategory = (TextView) findViewById(R.id.tvCategory);
        tvRecommendedBy = (TextView) findViewById(R.id.tvRecommendedBy);
        tvBookAvailable = (TextView) findViewById(R.id.tvBookAvailable);
        pd = new ProgressDialog(this);
        //-----------------Listener--------------------
        btnDownloadPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    downloadBook();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        if(intent.getStringExtra("BOOK_TYPE").equals("BOOKS")){
            btnDownloadPdf.setVisibility(View.GONE);
            setBookData(intent.getStringExtra("BOOK_ID"));
        }
        else if(intent.getStringExtra("BOOK_TYPE").equals("EBOOKS")){
            btnOpenBottomDialog.setVisibility(View.GONE);
            tvBookAvailable.setVisibility(View.GONE);
            setEBookData(intent.getStringExtra("BOOK_ID"));
        }
    }

    private void downloadBook() throws JSONException {
        pd.setMessage("Downloading...");
        pd.show();
        FileRequest fileRequest = new FileRequest(
                Request.Method.GET,
                Constants.FILE_BASE_PATH + book_detail.getString("file"),
                new Response.Listener<byte[]>() {
                    @Override
                    public void onResponse(byte[] response) {
                        pd.dismiss();
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
                            ActivityCompat.requestPermissions((Activity) ViewBookActivity.this,new String[]{
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);
                                if (response!=null) {

                                    try {
                                        String filename = "demo.pdf";
                                        File dir = new File("/sdcard/LMS/");
                                        dir.mkdirs();

                                        File myFile = new File(dir,filename);
                                        myFile.createNewFile();

                                        FileOutputStream outputStream = new FileOutputStream(myFile);
                                        //FileOutputStream outputStream = openFileOutput(name, MODE_PRIVATE);
                                        outputStream.write(response);
                                        outputStream.close();

                                        Toast.makeText(ViewBookActivity.this, "Download complete.", Toast.LENGTH_SHORT).show();
                                    } catch (FileNotFoundException e) {
                                        e.printStackTrace();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i(TAG, "onErrorResponse: "+error.getMessage());
                        pd.dismiss();
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
        requestQueue.add(fileRequest);
    }

    private void setBookData(String book_id){
        CacheRequest cacheRequest = new CacheRequest(
                Request.Method.GET,
                Constants.GET_SINGLE_BOOK + book_id,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, "onResponse: "+response);
                        try {
                            book_detail = new JSONObject(response).getJSONObject("book_detail").getJSONObject("book");
                            tvTitle.setText(book_detail.getString("title"));
                            tvAuthor.setText(book_detail.getString("author"));
                            tvPublisher.setText(book_detail.getString("publisher"));
                            tvPrice.setText(book_detail.getString("price"));
                            tvDate.setText(book_detail.getString("date"));
                            tvCategory.setText(book_detail.getString("category"));
                            tvRecommendedBy.setText("");

                            if(book_detail.getInt("quantity") > 0){
                                tvBookAvailable.setText("Available");
                                tvBookAvailable.setBackgroundDrawable(getDrawable(R.drawable.lms_green_all_round_white_border));
                            }else{
                                tvBookAvailable.setText("Not Available");
                                tvBookAvailable.setBackgroundDrawable(getDrawable(R.drawable.lms_orange_all_round_white_border));
                            }
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

    private void setEBookData(String book_id){
        CacheRequest cacheRequest = new CacheRequest(
                Request.Method.GET,
                Constants.GET_SINGLE_EBOOK + book_id,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, "onResponse: "+response);
                        try {
                            book_detail = new JSONObject(response).getJSONObject("book_detail").getJSONObject("ebook");
                            tvTitle.setText(book_detail.getString("title"));
                            tvAuthor.setText(book_detail.getString("author"));
                            tvPublisher.setText(book_detail.getString("publisher"));
                            tvPrice.setText("-");
                            tvDate.setText(book_detail.getString("date"));
                            tvCategory.setText(book_detail.getString("category"));
                            tvRecommendedBy.setText("");

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

    private void setNotification() {
        StringRequest cacheRequest = new StringRequest(
                Request.Method.GET,
                Constants.NOTIFY_ME+intent.getStringExtra("BOOK_ID"),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, "onResponse: "+response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if(jsonObject.has("message")){
                                Toast.makeText(ViewBookActivity.this, "Notification set", Toast.LENGTH_SHORT).show();
                            }
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void openBottomDialog(View view) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_view_book_actions);
//        dialog.getWindow().setBackgroundDrawableResource(R.drawable.white_all_round);

        btnLostBook = dialog.findViewById(R.id.btnLostBook);
        btnPayFee = dialog.findViewById(R.id.btnPayFee);
        btnNotify = dialog.findViewById(R.id.btnNotify);

        tvReturnLastDate = dialog.findViewById(R.id.tvReturnLastDate);
        tvLostCharges = dialog.findViewById(R.id.tvLostCharges);
        tvLateFee = dialog.findViewById(R.id.tvLateFee);

        LocalDate date = LocalDate.now();

        if(intent.getStringExtra("ACTION").equals("VIEW_BOOK")){
            btnLostBook.setVisibility(View.GONE);
            btnPayFee.setVisibility(View.GONE);
        }
        if(book_detail != null){
            try {
                tvLostCharges.setText("Rs. "+book_detail.getString("price"));
                tvReturnLastDate.setText(date.plusDays(15).toString());
                if(book_detail.getInt("quantity")  <= 0){
                    btnNotify.setVisibility(View.VISIBLE);
                }else{
                    btnNotify.setVisibility(View.GONE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        btnNotify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setNotification();
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