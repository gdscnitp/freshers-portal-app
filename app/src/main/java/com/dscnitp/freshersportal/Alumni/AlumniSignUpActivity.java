package com.dscnitp.freshersportal.Alumni;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.dscnitp.freshersportal.R;
import com.dscnitp.freshersportal.Model.Session;
import com.dscnitp.freshersportal.Student.LoginActivity;
import com.dscnitp.freshersportal.Student.SignUpActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class AlumniSignUpActivity extends AppCompatActivity {

    EditText spinner1,spinner2;
    TextInputLayout name, email,password;
    ProgressBar bar;
    private FirebaseAuth mAuth;
    String selected,getSelected;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alumni_sign_up);
        spinner1 =  findViewById(R.id.spinner1);
        name = (TextInputLayout) findViewById(R.id.name);
        email = (TextInputLayout) findViewById(R.id.email);
        password = (TextInputLayout) findViewById(R.id.pwd);
        bar = (ProgressBar) findViewById(R.id.progressBar);
        spinner2 =  findViewById(R.id.spinner2);
       spinner1.setOnTouchListener(new View.OnTouchListener() {
           @Override
           public boolean onTouch(View v, MotionEvent event) {
               AlertDialog.Builder builder=new AlertDialog.Builder(AlumniSignUpActivity.this);
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
                AlertDialog.Builder builder=new AlertDialog.Builder(AlumniSignUpActivity.this);
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

    }

    String uid;
    public void signuphere(View view) {
        final String emails = email.getEditText().getText().toString();
        String passw = password.getEditText().getText().toString();
        if (!emails.equals("") && !password.equals("")) {
            mAuth = FirebaseAuth.getInstance();
            mAuth.createUserWithEmailAndPassword(emails, passw).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        bar.setVisibility(View.INVISIBLE);
                        email.getEditText().setText("");
                        password.getEditText().setText("");
                        uid = mAuth.getCurrentUser().getUid();
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("email", emails);
                        hashMap.put("year", spinner1.getText().toString());
                        hashMap.put("name", name.getEditText().getText().toString());
                        hashMap.put("USER_TYPE", "Alumni");
                        hashMap.put("id", "");
                        hashMap.put("device_token", "");
                        hashMap.put("uid", uid);
                        hashMap.put("imgUrl", "");
                        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("users");
                        db.child(uid).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Intent i = new Intent(getApplicationContext(), AlumniMainActivity.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(i);
                                finish();
                            }
                        });
                        Toast.makeText(getApplicationContext(), "Registered Successfully", Toast.LENGTH_LONG).show();
                    } else {
                        bar.setVisibility(View.INVISIBLE);
                        Toast.makeText(getApplicationContext(), "Process Error", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }
    public void gotosignin(View view) {
        startActivity(new Intent(AlumniSignUpActivity.this, LoginActivity.class));
        finish();
    }
}
