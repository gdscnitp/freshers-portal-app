package com.example.firebaseuserauthentication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class Profile extends AppCompatActivity
{
    TextView email_home, uid_home;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        email_home=(TextView)findViewById(R.id.email_home);
        uid_home=(TextView)findViewById(R.id.uid_home);

        email_home.setText("Email : "+ getIntent().getStringExtra("email").toString());
        uid_home.setText("UID : "+getIntent().getStringExtra("uid").toString());


    }

    public void logoutprocess(View view)
    {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(Profile.this, MainActivity.class));
    }
}