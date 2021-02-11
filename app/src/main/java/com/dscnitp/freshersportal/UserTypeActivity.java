package com.dscnitp.freshersportal;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.dscnitp.freshersportal.Alumni.AlumniLoginActivity;
import com.dscnitp.freshersportal.Student.LoginActivity;

import am.appwise.components.ni.NoInternetDialog;

public class UserTypeActivity extends AppCompatActivity {


    LinearLayout student,alumni;
    NoInternetDialog noInternetDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_type);
        student=findViewById(R.id.studentOption);
        alumni=findViewById(R.id.alumniOption);
        noInternetDialog = new NoInternetDialog.Builder(this).setBgGradientStart(Color.parseColor("#4488A7"))
                .setBgGradientCenter(Color.parseColor("#4488A7")).setButtonColor(Color.parseColor("#2196F3"))
                .setBgGradientEnd(Color.parseColor("#4488A7")).build();

        student.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(UserTypeActivity.this,LoginActivity.class));

            }
        });
        alumni.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserTypeActivity.this, AlumniLoginActivity.class);
                startActivity(intent);

            }
        });


    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        noInternetDialog.onDestroy();
    }
}
