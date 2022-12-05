package com.shodh.lms;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LMS_TEST";

    private Button btnLogin;
    private EditText etEnrollmentNo,etPassword;
    private ProgressDialog pd;

    private RequestQueue requestQueue;
    private SharedPreferences user;
    private SharedPreferences.Editor userEditor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //---------------Hooks----------------
        btnLogin = (Button) findViewById(R.id.btnLogin);
        etEnrollmentNo = (EditText) findViewById(R.id.etEnrollmentNo);
        etPassword = (EditText) findViewById(R.id.etPassword);

        pd = new ProgressDialog(this);
        requestQueue = Volley.newRequestQueue(this);
        user = getSharedPreferences("USER",MODE_PRIVATE);
        userEditor = user.edit();

        //---------------Listener--------------
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doLogin();
            }
        });
    }

    private void doLogin(){
        pd.setMessage("Authenticate...");
        pd.show();
        if(validate()){
            //Do Some Authentication Activity Here

            StringRequest stringRequest = new StringRequest(
                    Request.Method.POST,
                    Constants.STUDENT_LOGIN,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            pd.dismiss();
                            Log.i(TAG, "onResponse: "+response);
                            try {
                                JSONObject jsonObject = new JSONObject(response);

                                if(jsonObject.getBoolean("login")){

                                    JSONObject student_data = jsonObject.getJSONObject("student_data");

                                    userEditor.putString("access_token",jsonObject.getString("access_token"));
                                    userEditor.putString("token_type",jsonObject.getString("token_type"));
                                    userEditor.putString("id",student_data.getString("id"));
                                    userEditor.putString("enroll",student_data.getString("enroll"));
                                    userEditor.putString("fname",student_data.getString("fname"));
                                    userEditor.putString("mname",student_data.getString("mname"));
                                    userEditor.putString("lname",student_data.getString("lname"));
                                    userEditor.putString("college",student_data.getString("college"));
                                    userEditor.putString("course",student_data.getString("course"));
                                    userEditor.putString("sem",student_data.getString("sem"));
                                    userEditor.putString("DOB",student_data.getString("DOB"));
                                    userEditor.putString("address",student_data.getString("address"));
                                    userEditor.putString("mo",student_data.getString("mo"));
                                    userEditor.putString("email",student_data.getString("email"));
                                    userEditor.putString("gender",student_data.getString("gender"));
                                    userEditor.putString("image_url",student_data.getString("picture"));
                                    userEditor.putString("password",student_data.getString("password"));
                                    userEditor.apply();
//                                    getStudentProfileImage(student_data.getString("picture"));
                                    startActivity(new Intent(LoginActivity.this,DashboardActivity.class));
                                    finish();

                                }else{
                                    Toast.makeText(LoginActivity.this, "Please check your credential", Toast.LENGTH_SHORT).show();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            pd.dismiss();
                            Log.i(TAG, "onErrorResponse: "+error.getMessage());
                        }
                    }){
                @Nullable
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> param = new HashMap<String,String>();
                    param.put("enroll",etEnrollmentNo.getText().toString());
                    param.put("password",etPassword.getText().toString());
                    return param;
                }
            };

            requestQueue.add(stringRequest);
        }
    }

    private void getStudentProfileImage(String url) {

        if(url.equals("")){
            userEditor.putString("image","");
            userEditor.apply();
        }else{
            ImageRequest imageRequest = new ImageRequest(
                    Constants.IMAGE_BASE_PATH + url, new Response.Listener<Bitmap>() {
                @Override
                public void onResponse(Bitmap response) {
                    Log.i(TAG, "onResponse: "+ImageConvert.getBitmapString(response));
                    userEditor.putString("image",ImageConvert.getBitmapString(response));
                    userEditor.apply();
                }
            }, 100, 100, null, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.i(TAG, "onErrorResponse: "+error.getMessage());
                }
            });
            requestQueue.add(imageRequest);
        }


    }



    private boolean validate() {
        String no = etEnrollmentNo.getText().toString();
        String pwd = etPassword.getText().toString();

        if(no.equals("")){
            Toast.makeText(this, "Enter Enrollment No", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(pwd.equals("")){
            Toast.makeText(this, "Enter Password", Toast.LENGTH_SHORT).show();
            return false;
        }
        else{
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