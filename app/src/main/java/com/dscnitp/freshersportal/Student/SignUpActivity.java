package com.dscnitp.freshersportal.Student;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.dscnitp.freshersportal.Alumni.AlumniMainActivity;
import com.dscnitp.freshersportal.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {
    TextInputLayout t1,t2,names;
    ProgressBar bar;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        t1 = (TextInputLayout) findViewById(R.id.email);
        t2 = (TextInputLayout) findViewById(R.id.pwd);
        names = (TextInputLayout) findViewById(R.id.name);
        bar = (ProgressBar) findViewById(R.id.progressBar);
    }

    String uid;

    public void signuphere(View view)
    {
        final String email = t1.getEditText().getText().toString();
        String password = t2.getEditText().getText().toString();
        final String name = names.getEditText().getText().toString();

        if (!email.equals("")  &&  !password.equals("")  &&  !name.equals(""))
        {
            mAuth = FirebaseAuth.getInstance();
            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        bar.setVisibility(View.INVISIBLE);
//                      bar.setVisibility(View.INVISIBLE);
                        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("email", email);
                        hashMap.put("name", name);
                        hashMap.put("USER_TYPE", "student");
                        hashMap.put("id", "");
                        hashMap.put("device_token", "");
                        hashMap.put("uid", uid);
                        hashMap.put("imgUrl", "");
                        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("users");
                        db.child(uid).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>()
                        {
                            @Override
                            public void onSuccess(Void aVoid)
                            {
                                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(i);
                                finish();
                                t1.getEditText().setText("");
                                t2.getEditText().setText("");
                                Toast.makeText(SignUpActivity.this, "Registered Successfully", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                    else {
                        bar.setVisibility(View.INVISIBLE);
                        t1.getEditText().setText("");
                        t2.getEditText().setText("");
                        Toast.makeText(SignUpActivity.this, "Process Error", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    public void gotosignin(View view)
    {
     startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
     finish();
    }
}