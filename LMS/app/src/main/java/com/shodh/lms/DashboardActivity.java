package com.shodh.lms;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.RenderEffect;
import android.graphics.Shader;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanIntentResult;
import com.journeyapps.barcodescanner.ScanOptions;
import com.shodh.lms.adapter.NewArrivalAdapter;
import com.shodh.lms.adapter.NewNotificationAdapter;
import com.shodh.lms.request.CacheRequest;
import com.shodh.lms.viewmodel.NewNotificationLiveViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class DashboardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private final static String TAG = "LMS_TEST";
    private ImageButton btnMenuBar,btnNotification,btnGetBookList;
    private TextView tvNotificationCount,tvUserName,tvUserEmail,tvTotalBookCount,tvMyBookCountTitle,tvMyBookCount,tvTotalEBookCount;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageView imgUserProfile;
    private ProgressDialog pd;
    private SwipeRefreshLayout swipeRefreshLayout;

    private RecyclerView newArrivalSlider;
    private LinearLayoutManager linearLayoutManager;
    private NewArrivalAdapter newArrivalAdapter;

    private NewNotificationAdapter newNotificationAdapter;
    private NewNotificationLiveViewModel newNotificationLiveViewModel;
    private JSONArray notification;

    private RequestQueue requestQueue;
    private SharedPreferences user;
    private SharedPreferences.Editor userEditor;

    private DialogLoading dialogLoading;

    private float dX = 0;
    private float dXOld = 0;
    private boolean goToBookAction = true;
    @SuppressLint({"MissingInflatedId", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        //------------------Hooks-------------------------
        btnMenuBar = (ImageButton) findViewById(R.id.btnMenuBar);
        btnNotification = (ImageButton) findViewById(R.id.btnNotification);
        btnGetBookList = (ImageButton) findViewById(R.id.btnGetBookList);
        tvNotificationCount = (TextView) findViewById(R.id.tvNotificationCount);
        tvTotalBookCount = (TextView) findViewById(R.id.tvTotalBookCount);
        tvTotalEBookCount = (TextView) findViewById(R.id.tvTotalEBookCount);
        tvMyBookCountTitle = (TextView) findViewById(R.id.tvMyBookCountTitle);
        tvMyBookCount = (TextView) findViewById(R.id.tvMyBooksCount);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        navigationView = (NavigationView) findViewById(R.id.navigationView);
        newArrivalSlider = (RecyclerView) findViewById(R.id.newArrivalSlider);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        pd = new ProgressDialog(this);
        dialogLoading = new DialogLoading(this);

        requestQueue = Volley.newRequestQueue(this);
        user = getSharedPreferences("USER",MODE_PRIVATE);
        userEditor = user.edit();

        //-------------------Listener-------------------------
        btnMenuBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        btnNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openNewNotificationDialog();
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                setDashboardData();
                fetchNotification();
            }
        });

        tvMyBookCountTitle.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
//                Log.i(TAG, "onTouch: touch");
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        dX = view.getX() - event.getRawX();
                        dXOld = view.getX();
                        break;
                    case MotionEvent.ACTION_MOVE:
//                        Log.i(TAG, "onTouch: moved"+event.getRawX());
                        view.setX(event.getRawX() + dX);
                        if((view.getX()+view.getMeasuredWidth()) >= btnGetBookList.getX()){
//                            Log.i(TAG, "onTouch: match");
                            if(goToBookAction){
                                startActivity(new Intent(DashboardActivity.this,MyBookActivity.class));
                                goToBookAction = false;
                            }
                        }
                        break;
                    default:
//                        Log.i(TAG, "onTouch: default");
                        view.setX(dXOld);
                        break;
                }
                return true;
            }
        });

        //set navigation listener to click the item
        navigationView.setNavigationItemSelectedListener(this);

        //set recycle view for new arrival slider
        setSliderRecycleView();
        setNavHeader();
        setDashboardData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchNotification();
        goToBookAction = true;
    }

    private void fetchNotification(){

        newNotificationAdapter = new NewNotificationAdapter(DashboardActivity.this,notification);

        newNotificationLiveViewModel = new NewNotificationLiveViewModel();
        newNotificationLiveViewModel.getNotification().observe(this, new Observer<JSONArray>() {
            @Override
            public void onChanged(JSONArray jsonArray) {
                if(jsonArray != null){
                    notification = jsonArray;
                    tvNotificationCount.setText(String.valueOf(jsonArray.length()));
                    newNotificationAdapter.updateNotification(jsonArray);
                    swipeRefreshLayout.setRefreshing(false);
                }else{
                    tvNotificationCount.setText("0");
                }
            }
        });

        newNotificationLiveViewModel.makeApiCall(this,user);
    }

    private void setNavHeader(){
        tvUserName = navigationView.getHeaderView(0).findViewById(R.id.tvUserNameNavHeader);
        tvUserEmail = navigationView.getHeaderView(0).findViewById(R.id.tvUserEmailNavHeader);
        imgUserProfile = navigationView.getHeaderView(0).findViewById(R.id.imgUserProfileNavHeader);

        tvUserName.setText(user.getString("fname","fname").toUpperCase(Locale.ROOT)+" "+user.getString("lname","lname").toUpperCase(Locale.ROOT));
        tvUserEmail.setText(user.getString("email","email"));

        Glide.with(this)
                .load(Constants.IMAGE_BASE_PATH + user.getString("image_url","image_url"))
                .into(imgUserProfile);
    }

    private void setDashboardData(){
        //set loading dialog
        dialogLoading.show();
        CacheRequest cacheRequest = new CacheRequest(
                Request.Method.GET,
                Constants.GET_DASHBOARD_COUNT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, "onResponse: "+response);
                        swipeRefreshLayout.setRefreshing(false);
                        dialogLoading.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response).getJSONObject("dashboard_data");
                            tvTotalBookCount.setText(jsonObject.getString("total_books"));
                            tvTotalEBookCount.setText(jsonObject.getString("total_ebooks"));
                            tvMyBookCount.setText(jsonObject.getString("my_books"));
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

    private void openNewNotificationDialog() {

        Dialog dialog = new Dialog(this,R.style.DialogStyle);
        dialog.setContentView(R.layout.dialog_new_notification);

        TextView btnMarkAll = dialog.findViewById(R.id.btnMarkAll);

        dialog.show();
        dialog.getWindow().setDimAmount(0);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.TOP|Gravity.RIGHT);

        RecyclerView recyclerView = dialog.findViewById(R.id.recycleViewNewNotification);
        LinearLayoutManager llm = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(llm);

        recyclerView.setAdapter(newNotificationAdapter);

        btnMarkAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pd.setMessage("Mark All ...");
                pd.show();
                StringRequest markAllRequest = new StringRequest(
                        Request.Method.GET,
                        Constants.MARK_ALL_NOTIFICATION,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.i(TAG, "onResponse: "+response);
                                pd.dismiss();
                                newNotificationLiveViewModel.makeApiCall(DashboardActivity.this,user);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                pd.dismiss();
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
                requestQueue.add(markAllRequest);

            }
        });

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

    // Register the launcher and result handler
    // scan book qr code
    ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(new ScanContract(),
            (ScanIntentResult result) -> {
        if(result.getContents() == null) {
            Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
            Intent i = new Intent(this, ViewBookActivity.class);
            i.putExtra("ACTION","VIEW_BOOK");
            i.putExtra("BOOK_TYPE","EBOOKS");
            i.putExtra("BOOK_ID",result.getContents());
            startActivity(i);
        }
    });

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.profile:
                startActivity(new Intent(this,ProfileActivity.class));
                break;
            case R.id.ebooks:
                Intent i = new Intent(this,BooksActivity.class);
                i.putExtra("BOOK_TYPE","EBOOKS");
                startActivity(i);
                break;
            case R.id.books:
                Intent ibooks = new Intent(this,BooksActivity.class);
                ibooks.putExtra("BOOK_TYPE","BOOKS");
                startActivity(ibooks);
                break;
            case R.id.myBooks:
                startActivity(new Intent(this,MyBookActivity.class));
                break;
            case R.id.notification:
                startActivity(new Intent(this,NotificationActivity.class));
                break;
            case R.id.history:
                startActivity(new Intent(this,HistoryActivity.class));
                break;
            case R.id.scanQR:
                ScanOptions options = new ScanOptions();
                options.setOrientationLocked(true);
                options.setPrompt("Scan a barcode");
                options.setBeepEnabled(true);
                options.setCaptureActivity(Capture.class);
                options.setBarcodeImageEnabled(true);
                barcodeLauncher.launch(options);
                break;
            case R.id.faqs:
                startActivity(new Intent(this,MessageActivity.class));
                break;
            case R.id.logout:
                logout();
                break;
        }

        Toast.makeText(this, item.getTitle(), Toast.LENGTH_SHORT).show();

        return true;
    }

    private void logout() {
        pd.setMessage("Logout...");
        pd.show();
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                Constants.STUDENT_LOGOUT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, "onResponse: "+response);
                        pd.dismiss();
                        userEditor.clear();
                        userEditor.apply();
                        startActivity(new Intent(DashboardActivity.this,LoginActivity.class));
                        finish();
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

        requestQueue.add(stringRequest);
    }
}