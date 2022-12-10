package com.shodh.lms;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.transition.TransitionManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private final static String TAG = "LMS_TEST";
    private ScrollView scrollView;
    private View profileHeaderView;

    private CardView cardViewUserProfile,btnGoToBack;
    private ImageView imgUserProfile;
    private TextView tvUserName,tvEnrollNo,tvCollege,tvCourse,tvSemester,tvDob,tvGender;
    private EditText etEmail,etMobile,etAddress,etPassword;
    private Button btnUpdate;

    private ProgressDialog pd;
    private RequestQueue requestQueue;
    private SharedPreferences user;
    private SharedPreferences.Editor userEditor;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //-------------------Hooks-----------------------
        scrollView = (ScrollView) findViewById(R.id.scrollview);
        profileHeaderView = (View) findViewById(R.id.profile_header_view);
        cardViewUserProfile = (CardView) findViewById(R.id.cardViewUserProfile);
        btnGoToBack = (CardView) findViewById(R.id.btnGoToBack);
        imgUserProfile = (ImageView) findViewById(R.id.imgUserProfile);
        tvUserName = (TextView) findViewById(R.id.tvUserName);
        tvEnrollNo= (TextView) findViewById(R.id.tvEnrollNo);
        tvCollege= (TextView) findViewById(R.id.tvCollegeName);
        tvCourse= (TextView) findViewById(R.id.tvCourse);
        tvSemester= (TextView) findViewById(R.id.tvSemester);
        tvDob= (TextView) findViewById(R.id.tvDob);
        tvGender= (TextView) findViewById(R.id.tvGender);
        etEmail = (EditText) findViewById(R.id.etUserEmail);
        etMobile = (EditText) findViewById(R.id.etUserMobile);
        etAddress = (EditText) findViewById(R.id.etUserAddress);
        etPassword = (EditText) findViewById(R.id.etUserPassword);
        btnUpdate = (Button) findViewById(R.id.btnUpdateProfile);
        pd = new ProgressDialog(this);
        requestQueue = Volley.newRequestQueue(this);
        user = getSharedPreferences("USER",MODE_PRIVATE);
        userEditor = user.edit();
        //-----------------Listener----------------------

        //set set Profile Animation
        setProfileAnimation();
        setProfileData();
    }

    private void setProfileData(){
        tvUserName.setText(user.getString("fname","fname").toUpperCase()+" "+user.getString("mname","mname").toUpperCase()+" "+user.getString("lname","lname").toUpperCase());
        tvEnrollNo.setText(user.getString("enroll","enroll"));
        tvCollege.setText(user.getString("college","college").toUpperCase());
        tvCourse.setText(user.getString("course","course").toUpperCase());
        tvSemester.setText(user.getString("sem","sem"));
        tvDob.setText(user.getString("DOB","DOB"));
        tvGender.setText(user.getString("gender","gender"));
        etEmail.setText(user.getString("email","email"));
        etMobile.setText(user.getString("mo","mo"));
        etAddress.setText(user.getString("address","address"));
        etPassword.setText(user.getString("password","password"));

        Glide.with(this)
                .load(Constants.IMAGE_BASE_PATH + user.getString("image_url","image_url"))
                .into(imgUserProfile);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setProfileAnimation() {
        int default_height = 245;
        int max_scroll = 70;

        scrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View view, int i, int i1, int i2, int i3) {

                ValueAnimator anim;
                if(i1 > 10){
                    tvUserName.animate().x(20).translationY(20f)
                            .setInterpolator(new AccelerateDecelerateInterpolator())
                            .setDuration(70);

                    cardViewUserProfile.animate().translationX(450f).translationY(-25f).scaleX(1.2f).scaleY(1.2f)
                            .setInterpolator(new AccelerateDecelerateInterpolator())
                            .setDuration(70);

                    btnGoToBack.animate().translationX(-40f)
                            .setInterpolator(new AccelerateDecelerateInterpolator())
                            .setDuration(70);

                    anim = ValueAnimator.ofInt(profileHeaderView.getMeasuredHeight(), 240 - 70);
                    anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator valueAnimator) {
                            int val = (Integer) valueAnimator.getAnimatedValue();
                            ViewGroup.LayoutParams layoutParams = profileHeaderView.getLayoutParams();
                            layoutParams.height = val;
                            profileHeaderView.setLayoutParams(layoutParams);
                        }
                    });

                }else{
                    tvUserName.animate().translationX(0).translationY(0)
                            .setInterpolator(new AccelerateDecelerateInterpolator())
                            .setDuration(150);

                    cardViewUserProfile.animate().translationX(0).translationY(0).scaleX(1f).scaleY(1f)
                            .setInterpolator(new AccelerateDecelerateInterpolator())
                            .setDuration(150);

                    btnGoToBack.animate().translationX(0)
                            .setInterpolator(new AccelerateDecelerateInterpolator())
                            .setDuration(150);

                    FrameLayout.LayoutParams llm = (FrameLayout.LayoutParams) cardViewUserProfile.getLayoutParams();
                    llm.gravity = Gravity.LEFT|Gravity.BOTTOM;
                    cardViewUserProfile.setLayoutParams(llm);

                    anim = ValueAnimator.ofInt(profileHeaderView.getMeasuredHeight(), 240);
                    anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator valueAnimator) {
                            int val = (Integer) valueAnimator.getAnimatedValue();
                            ViewGroup.LayoutParams layoutParams = profileHeaderView.getLayoutParams();
                            layoutParams.height = val;
                            profileHeaderView.setLayoutParams(layoutParams);
                        }
                    });
                }
                anim.setDuration(50);
                anim.start();

//                Log.i(TAG, "onScrollChange: "+String.valueOf(i)+" "+String.valueOf(i1)+" "+String.valueOf(i2)+" "+String.valueOf(i3));
            }
        });
    }

    public void goToBack(View view) {
        finish();
    }

    public void updateProfile(View view) {
        pd.setMessage("Updating...");
        pd.show();
        if(validate()){
            String email = etEmail.getText().toString();
            String mobile = etMobile.getText().toString();
            String pwd = etPassword.getText().toString();
            String address = etAddress.getText().toString();

            //do update activity here
            StringRequest stringRequest = new StringRequest(
                Request.Method.PUT,
                Constants.STUDENT_PROFILE_UPDATE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, "onResponse: "+response);
                        pd.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if(jsonObject.has("message")){
                                userEditor.putString("address",address);
                                userEditor.putString("mo",mobile);
                                userEditor.putString("email",email);
                                userEditor.putString("password",pwd);
                                userEditor.apply();
                                Toast.makeText(ProfileActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i(TAG, "onErrorResponse: "+error);
                        pd.dismiss();
                    }
                }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> param = new HashMap<String,String>();
                    param.put("address",address);
                    param.put("mo",mobile);
                    param.put("password",pwd);
                    param.put("email",email);
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
    }

    private boolean validate() {
        String email = etEmail.getText().toString();
        String mobile = etMobile.getText().toString();
        String pwd = etPassword.getText().toString();
        String address = etAddress.getText().toString();

        if(email.equals("")){
            Toast.makeText(this, "Enter Your Email", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(mobile.equals("")){
            Toast.makeText(this, "Enter Your Mobile", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(address.equals("")){
            Toast.makeText(this, "Enter Your Address", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(pwd.equals("")){
            Toast.makeText(this, "Enter Your Password", Toast.LENGTH_SHORT).show();
            return false;
        }else{
            if(pwd.length() < 5){
                Toast.makeText(this, "Password must be >= 5", Toast.LENGTH_SHORT).show();
                return false;
            }else if(pwd.length() > 12){
                Toast.makeText(this, "Password must be <= 12", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }


}