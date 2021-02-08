package com.dscnitp.freshersportal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.dscnitp.freshersportal.Alumni.AlumniLoginActivity;
import com.dscnitp.freshersportal.Student.LoginActivity;

public class UserTypeActivity extends AppCompatActivity {

    LinearLayout student,alumni;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_type);
        student=findViewById(R.id.student);
        alumni=findViewById(R.id.alumni);
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
}
