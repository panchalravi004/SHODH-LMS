package com.shodh.lms;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "LMS_TEST";
    private TableLayout sectionOne,sectionTwo;
    private CardView btnGoToLogin,btnGoToSectionOne;
    private Button btnCreateAccount;
    private EditText etEnrollment,etFName,etMName,etLName,etEmail,etMobile,etPassword,etAddress;
    private TextView tvDob;
    private Spinner spGender,spCollege,spCourse,spSemester;
    private ProgressDialog pd;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //---------------------Hooks--------------------
        sectionOne = (TableLayout) findViewById(R.id.tableLayoutRegisterSectionOne);
        sectionTwo = (TableLayout) findViewById(R.id.tableLayoutRegisterSectionTwo);
        btnGoToLogin = (CardView) findViewById(R.id.btnGoToBack);
        btnGoToSectionOne = (CardView) findViewById(R.id.btnBackToSectionOne);
        btnCreateAccount = (Button) findViewById(R.id.btnCreateAccount);
        etEnrollment = (EditText) findViewById(R.id.etEnrollmentNo);
        etFName = (EditText) findViewById(R.id.etFirstName);
        etMName = (EditText) findViewById(R.id.etMiddleName);
        etLName = (EditText) findViewById(R.id.etLastName);
        etEmail = (EditText) findViewById(R.id.etUserEmail);
        etMobile = (EditText) findViewById(R.id.etUserMobile);
        tvDob = (TextView) findViewById(R.id.tvDob);
        etPassword = (EditText) findViewById(R.id.etUserPassword);
        etAddress = (EditText) findViewById(R.id.etUserAddress);
        spGender = (Spinner) findViewById(R.id.spGender);
        spCollege = (Spinner) findViewById(R.id.spCollege);
        spCourse = (Spinner) findViewById(R.id.spCourse);
        spSemester = (Spinner) findViewById(R.id.spSemester);
        pd = new ProgressDialog(this);

        //--------------------listener------------------
        tvDob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dpd = new DatePickerDialog(RegisterActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int y, int m, int d) {
                        tvDob.setText(y+"-"+m+"-"+d);
                    }
                },2022,12,12);
                dpd.show();
            }
        });
        btnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validate()){
                    createAccount();
                }
            }
        });
    }

    private void createAccount() {
        //do create account code here


    }

    private boolean validate() {
        String enrollment = etEnrollment.getText().toString();
        String fName = etFName.getText().toString();
        String mName = etMName.getText().toString();
        String lName = etLName.getText().toString();
        String email = etEmail.getText().toString();
        String mobile = etMobile.getText().toString();
        String dob = tvDob.getText().toString();
        String password = etPassword.getText().toString();
        String address = etAddress.getText().toString();
        int Gender = spGender.getSelectedItemPosition();
        int College = spCollege.getSelectedItemPosition();
        int Course = spCourse.getSelectedItemPosition();
        int Semester = spSemester.getSelectedItemPosition();

        if(enrollment.equals("")){
            Toast.makeText(this, "Please enter enrollment", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(fName.equals("")){
            Toast.makeText(this, "Please enter First Name", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(mName.equals("")){
            Toast.makeText(this, "Please enter Middle Name", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(lName.equals("")){
            Toast.makeText(this, "Please enter Last Name", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(email.equals("")){
            Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(mobile.equals("")){
            Toast.makeText(this, "Please enter mobile", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(dob.equals("")){
            Toast.makeText(this, "Please enter dob", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(password.equals("")){
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(address.equals("")){
            Toast.makeText(this, "Please enter address", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(Gender == 0){
            Toast.makeText(this, "Please select Gender", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(College == 0){
            Toast.makeText(this, "Please select College", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(Course == 0){
            Toast.makeText(this, "Please select Course", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(Semester == 0){
            Toast.makeText(this, "Please select Semester", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    public void goToBack(View view) {
        finish();
    }

    public void goToSectionOne(View view) {
        sectionOne.setVisibility(View.VISIBLE);
        sectionTwo.setVisibility(View.GONE);
        btnGoToLogin.setVisibility(View.VISIBLE);
        btnGoToSectionOne.setVisibility(View.GONE);
    }

    public void goToSectionTwo(View view) {
        sectionOne.setVisibility(View.GONE);
        sectionTwo.setVisibility(View.VISIBLE);
        btnGoToLogin.setVisibility(View.GONE);
        btnGoToSectionOne.setVisibility(View.VISIBLE);
    }
}