package com.shodh.lms;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    private Button btnLogin;
    private EditText etEnrollmentNo,etPassword;
    private ProgressDialog pd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //---------------Hooks----------------
        btnLogin = (Button) findViewById(R.id.btnLogin);
        etEnrollmentNo = (EditText) findViewById(R.id.etEnrollmentNo);
        etPassword = (EditText) findViewById(R.id.etPassword);

        pd = new ProgressDialog(this);

        //---------------Listener--------------
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    doLogin();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void doLogin() throws InterruptedException {
        if(validate()){
            //Do Some Authentication Activity Here
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
        return true;
    }
}