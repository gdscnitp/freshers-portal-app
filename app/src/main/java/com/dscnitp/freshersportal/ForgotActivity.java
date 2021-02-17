package com.dscnitp.freshersportal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ForgotActivity extends AppCompatActivity {

    private Button SendEmail;
    private TextInputEditText Email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot);

        Email=findViewById(R.id.Email);
        SendEmail=findViewById(R.id.SendEmail);

    }
    public void ResetPassword(View V)
    {
        String email=Email.getText().toString().trim();
        if(email.equals(""))
        {
            Email.setError("Enter Email");
        }
        else
        {
            FirebaseAuth mAuth=FirebaseAuth.getInstance();
            FirebaseUser firebaseUser=mAuth.getCurrentUser();
            mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful())
                    {
                        Toast.makeText(ForgotActivity.this,"Forgot Password Link Sent to Email",Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(ForgotActivity.this,"Forgot Password Link Not Sent to Email",Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }

}