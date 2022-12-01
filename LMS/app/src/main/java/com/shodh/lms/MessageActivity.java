package com.shodh.lms;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

public class MessageActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private MessageAdapter messageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        //----------------------Hooks-----------------
        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewMessages);

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
}