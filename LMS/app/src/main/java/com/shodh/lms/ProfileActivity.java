package com.shodh.lms;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

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

        //-----------------Listener----------------------

        //set set Profile Animation
        setProfileAnimation();
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

                    tvUserName.animate().translationX(-250f).translationY(60f)
                            .setInterpolator(new AccelerateDecelerateInterpolator())
                            .setDuration(70);

                    cardViewUserProfile.animate().translationX(450f).translationY(-35f).scaleX(1.2f).scaleY(1.2f)
                            .setInterpolator(new AccelerateDecelerateInterpolator())
                            .setDuration(70);

                    btnGoToBack.animate().translationX(-40f).translationY(20f)
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

                    btnGoToBack.animate().translationX(0).translationY(0)
                            .setInterpolator(new AccelerateDecelerateInterpolator())
                            .setDuration(150);

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
        if(validate()){
            //do update activity here
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
        }if(address.equals("")){
            Toast.makeText(this, "Enter Your Address", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


}