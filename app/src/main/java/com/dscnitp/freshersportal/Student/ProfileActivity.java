package com.dscnitp.freshersportal.Student;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.dscnitp.freshersportal.AboutUs;
import com.dscnitp.freshersportal.R;

public class ProfileActivity extends AppCompatActivity {

    Button edit;
    TextView t1, t2, t3, t4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        edit = findViewById(R.id.edit);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, EditProfileActivity.class));
            }
        });
        TextView t5 = findViewById(R.id.aboutUs);
        TextView t6 = findViewById(R.id.privacy);

        t5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, AboutUs.class));
            }
        });
        t6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://freshers-portal.flycricket.io/privacy.html";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

//
        t1 = findViewById(R.id.name);
        t2 = findViewById(R.id.roll);
        t3 = findViewById(R.id.student_branch);
        t4 = findViewById(R.id.year);

    }
}