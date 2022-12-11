package com.shodh.lms;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
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
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;
import com.shodh.lms.request.CacheRequest;
import com.shodh.lms.request.FileRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ViewBookActivity extends AppCompatActivity implements PaymentResultListener {

    private static final String TAG = "LMS_TEST";
    private RequestQueue requestQueue;
    private Intent intent;
    private SharedPreferences user;
    private TextView tvTitle,tvAuthor,tvPublisher,tvPrice,tvDate,tvCategory,tvRecommendedBy,tvBookAvailable,tvReturnLastDate,tvLostCharges,tvLateFee;
    private Button btnLostBook,btnPayFee,btnNotify;
    private ImageButton btnDownloadPdf,btnOpenBottomDialog;
    private ProgressDialog pd;
    private DialogLoading dialogLoading;
    private JSONObject book_detail;

    //payment action and amount
    private String PAYMENT_ACTION = null;
    private int LOST_BOOK_CHARGES = 0;
    private int LATE_FEES = 0;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_book_v2);
        Checkout.preload(getApplicationContext());

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
        dialogLoading = new DialogLoading(this);
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

        if(intent.getStringExtra("ACTION").equals("MY_BOOKS")){
            tvBookAvailable.setVisibility(View.GONE);
            btnDownloadPdf.setVisibility(View.GONE);
            setMyBookData(intent.getStringExtra("BOOK_ISSUE_ID"));
        }

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
                                        String[] ext = book_detail.getString("file").split("[.]",0);
                                        Log.i(TAG, "onResponse: "+ext[1]);
                                        String filename = book_detail.getString("title").toLowerCase().replace(" ","_")+"."+ext[1];
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
                                    } catch (JSONException e) {
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
        dialogLoading.show();
        CacheRequest cacheRequest = new CacheRequest(
                Request.Method.GET,
                Constants.GET_SINGLE_BOOK + book_id,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, "onResponse: "+response);
                        dialogLoading.dismiss();
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
        dialogLoading.show();
        CacheRequest cacheRequest = new CacheRequest(
                Request.Method.GET,
                Constants.GET_SINGLE_EBOOK + book_id,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, "onResponse: "+response);
                        dialogLoading.dismiss();
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

    private void setMyBookData(String book_id){
        dialogLoading.show();
        CacheRequest cacheRequest = new CacheRequest(
                Request.Method.GET,
                Constants.GET_SINGLE_ISSUE_BOOK + book_id,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, "onResponse: "+response);
                        dialogLoading.dismiss();
                        try {
                            book_detail = new JSONObject(response).getJSONArray("issued_record_detail").getJSONObject(0);
                            tvTitle.setText(book_detail.getString("title"));
                            tvAuthor.setText(book_detail.getString("author"));
                            tvPublisher.setText(book_detail.getString("publisher"));
                            tvPrice.setText(book_detail.getString("price"));
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

        if(intent.getStringExtra("ACTION").equals("VIEW_BOOK")){
            LocalDate date = LocalDate.now();
            btnLostBook.setVisibility(View.GONE);
            btnPayFee.setVisibility(View.GONE);
            tvReturnLastDate.setText(date.plusDays(15).toString());
            tvLateFee.setText("Rs. 10/Day");

        }else if(intent.getStringExtra("ACTION").equals("MY_BOOKS")){
            btnLostBook.setVisibility(View.VISIBLE);
            btnNotify.setVisibility(View.GONE);
        }

        if(book_detail != null){
            try {
                LOST_BOOK_CHARGES = book_detail.getInt("price");
                tvLostCharges.setText("Rs. "+book_detail.getString("price"));
                if(intent.getStringExtra("ACTION").equals("VIEW_BOOK")) {
                    if(book_detail.getInt("quantity")  <= 0){
                        btnNotify.setVisibility(View.VISIBLE);
                    }else{
                        btnNotify.setVisibility(View.GONE);
                    }
                }
                else if(intent.getStringExtra("ACTION").equals("MY_BOOKS")){
                    tvReturnLastDate.setText(book_detail.getString("expected_return_date"));

                    Date date = new Date();
                    Date expected_return_date = new SimpleDateFormat("yyyy-MM-dd").parse(book_detail.getString("expected_return_date"));

                    if (expected_return_date.after(date)){
                        btnPayFee.setVisibility(View.GONE);
                        tvLateFee.setText("Rs. 10/Day");
                        LATE_FEES = 0;
                    }else{
                        btnPayFee.setVisibility(View.VISIBLE);
                        int y =date.getYear() - expected_return_date.getYear();
                        int m = (y * 12) + (date.getMonth() - expected_return_date.getMonth());
                        int diff = (m * 30) + (date.getDate() - expected_return_date.getDate());
                        tvLateFee.setText("Rs. "+((10 * diff) - book_detail.getInt("fine")));
                        LATE_FEES = (10 * diff) - book_detail.getInt("fine");
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            catch (ParseException e) {
                e.printStackTrace();
            }
        }

        //on click send notification
        btnNotify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setNotification();
            }
        });

        //on click pay lost book payment and late fee if applicable
        btnLostBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PAYMENT_ACTION = "LOST_BOOK";
                payBookPayment(LOST_BOOK_CHARGES +LATE_FEES);
            }
        });

        //onclick pay late fee
        btnPayFee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PAYMENT_ACTION = "LATE_FEE";
                payBookPayment(LATE_FEES);
            }
        });


        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.BottomDialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    private void payBookPayment(int amount) {

        Checkout checkout = new Checkout();
        checkout.setKeyID("rzp_test_W9lNBqTkPjNmal");

        checkout.setImage(R.drawable.logo);
        final Activity activity = this;

        try {
            JSONObject options = new JSONObject();

            options.put("key", "rzp_test_W9lNBqTkPjNmal");
            options.put("name", "Library Management System");
//            options.put("description", "Reference No. #123456");
//            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.jpg");
//            options.put("order_id", "order_DBJOWzybf0sJbb");//from response of step 3.
            options.put("theme.color", "#FFA200");
            options.put("currency", "INR");
            options.put("amount", amount*100);//pass amount in currency subunits
            options.put("prefill.email", user.getString("email","email"));
            options.put("prefill.contact","9988776655");
//            JSONObject retryObj = new JSONObject();
//            retryObj.put("enabled", true);
//            retryObj.put("max_count", 4);
//            options.put("retry", retryObj);

            checkout.open(activity, options);

        } catch(Exception e) {
            Log.e(TAG, "Error in starting Razorpay Checkout", e);
        }
    }

    @Override
    public void onPaymentSuccess(String s) {
        Log.i(TAG, "onPaymentSuccess: "+s);
        if(PAYMENT_ACTION.equals("LATE_FEE")){

            payLateFee(s,LATE_FEES);

        }else if(PAYMENT_ACTION.equals("LOST_BOOK")){

            payLostBook(s, LOST_BOOK_CHARGES +LATE_FEES);

        }
    }

    @Override
    public void onPaymentError(int i, String s) {
        Log.i(TAG, "onPaymentError: "+s+" "+i);
    }

    private void payLostBook(String payment_id,int amount) {
        Log.i(TAG, "payLostBook: "+payment_id+" "+amount);
        pd.setMessage("Wait...");
        pd.show();

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                Constants.PAY_BOOK_LOST_FINE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, "onResponse: "+response);
                        pd.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if(jsonObject.has("payment")){
                                Toast.makeText(ViewBookActivity.this, "Payment Successfully !", Toast.LENGTH_SHORT).show();
                                MyBookActivity.myBookLiveViewModel.clearCache();
                                MyBookActivity.myBookLiveViewModel.makeApiCall(MyBookActivity.context,user);
                                finish();
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
                        pd.dismiss();
                    }
                }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<String,String>();
                param.put("razorpay_payment_id",payment_id);
                param.put("payable_fine",String.valueOf(amount));
                param.put("isbid",intent.getStringExtra("BOOK_ISSUE_ID"));

                return param;
            }

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

    private void payLateFee(String payment_id,int amount) {
        Log.i(TAG, "payLateFee: "+payment_id+" "+amount);
        pd.setMessage("Wait...");
        pd.show();
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                Constants.PAY_BOOK_LATE_FINE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, "onResponse: "+response);
                        pd.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if(jsonObject.has("payment")){
                                Toast.makeText(ViewBookActivity.this, "Payment Successfully !", Toast.LENGTH_SHORT).show();
                                MyBookActivity.myBookLiveViewModel.clearCache();
                                MyBookActivity.myBookLiveViewModel.makeApiCall(MyBookActivity.context,user);
                                finish();
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
                        pd.dismiss();
                    }
                }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<String,String>();
                param.put("razorpay_payment_id",payment_id);
                param.put("payable_fine",String.valueOf(amount));
                param.put("isbid", intent.getStringExtra("BOOK_ISSUE_ID"));

                return param;
            }
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

    public void goToBack(View view) {
        finish();
    }
}