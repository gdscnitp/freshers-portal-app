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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        edit=findViewById(R.id.edit);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, EditProfileActivity.class));
            }
        });
        TextView t1=findViewById(R.id.aboutUs);
        TextView t2=findViewById(R.id.privacy);

        t1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, AboutUs.class));
            }
        });
        t2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://freshers-portal.flycricket.io/privacy.html";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

         t1=findViewById(R.id.name);
        t2=findViewById(R.id.roll);
        t3=findViewById(R.id.student_branch);
        t4=findViewById(R.id.year);
        
        Intent i= getIntent();

        String s1=i.getStringExtra("name");
        String s2=i.getStringExtra("roll");
        String s3=i.getStringExtra("branch");
        String s4=i.getStringExtra("year");

        t1.setText(s1);
        t2.setText(s2);
        t3.setText(s3);
        t4.setText(s4);
    }
}
