package com.dscnitp.freshersportal.Alumni;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.dscnitp.freshersportal.Model.Session;
import com.dscnitp.freshersportal.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class AlumniProfileActivity extends AppCompatActivity {

    EditText spinner1,spinner2;
    Button update;
    String selected,getSelected;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alumni_profile);
        spinner1 =  findViewById(R.id.spinner1);
        spinner2 =  findViewById(R.id.spinner2);
        update =  findViewById(R.id.update);
        spinner1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                AlertDialog.Builder builder=new AlertDialog.Builder(AlumniProfileActivity.this);
                builder.setTitle("Select Session")
                        .setItems(Session.options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                selected=Session.options[which];
                                spinner1.setText(selected);
                            }
                        }).show();
                return true;
            }
        });
        spinner2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                AlertDialog.Builder builder=new AlertDialog.Builder(AlumniProfileActivity.this);
                builder.setTitle("Select Session")
                        .setItems(Session.options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                getSelected=Session.options[which];
                                spinner2.setText(getSelected);
                            }
                        }).show();
                return true;
            }
        });
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("year", spinner1.getText().toString());
                DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("users");
                db.child(FirebaseAuth.getInstance().getUid()).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Intent i = new Intent(getApplicationContext(), AlumniMainActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                        finish();
                    }
                });

            }
        });
    }
    }
