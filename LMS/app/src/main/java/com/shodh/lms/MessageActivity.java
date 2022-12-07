package com.shodh.lms;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.shodh.lms.adapter.MessageAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MessageActivity extends AppCompatActivity {

    private static final String TAG = "LMS_TEST";
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private MessageAdapter messageAdapter;
    private SharedPreferences user;
    private RequestQueue requestQueue;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        //----------------------Hooks-----------------
        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewMessages);
        user = getSharedPreferences("USER",MODE_PRIVATE);
        requestQueue = Volley.newRequestQueue(this);
        pd = new ProgressDialog(this);
        //----------------------Listener------------------

        setMessageRecycleView();
    }

    private void setMessageRecycleView() {
        linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);
        messageAdapter = new MessageAdapter(this);
        recyclerView.setAdapter(messageAdapter);
    }

    public void goToBack(View view) {
        finish();
    }

    public void openMessageDialog(View view) {
        Dialog dialog = new Dialog(this,R.style.DialogStyle);
        dialog.setContentView(R.layout.dialog_new_message);
//        dialog.getWindow().setBackgroundDrawableResource(R.drawable.white_all_round);
//
        ImageButton btnClose;
        Button btnSend;
        EditText etSubject,etMessage;

        btnClose = dialog.findViewById(R.id.btnNewMsgClose);
        btnSend = dialog.findViewById(R.id.btnNewMsgSend);
        etSubject = dialog.findViewById(R.id.etNewMsgSubject);
        etMessage = dialog.findViewById(R.id.etNewMsgMessage);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendNewMessage(etSubject.getText().toString(),etMessage.getText().toString());
                etSubject.setText("");
                etMessage.setText("");
            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.BottomDialogAnimation;
        dialog.getWindow().setGravity(Gravity.CENTER);
    }

    private void sendNewMessage(String subject, String message) {
        pd.setMessage("Sending...");
        pd.show();
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                Constants.SEND_NEW_MESSAGE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, "onResponse: "+response);
                        pd.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if(jsonObject.has("message")){
                                Toast.makeText(MessageActivity.this, "Message Send Successfully!", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(MessageActivity.this, "Failed to send message!", Toast.LENGTH_SHORT).show();
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
                param.put("subject",subject);
                param.put("message",message);
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