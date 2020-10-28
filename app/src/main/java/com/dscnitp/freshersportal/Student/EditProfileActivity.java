package com.dscnitp.freshersportal.Student;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.dscnitp.freshersportal.R;
import com.dscnitp.freshersportal.SplashScreen;
import com.google.firebase.auth.FirebaseAuth;

public class EditProfileActivity extends AppCompatActivity {

    Button logout;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        logout=findViewById(R.id.logout);
        mAuth=FirebaseAuth.getInstance();
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                finish();
                Intent mainIntent = new Intent(EditProfileActivity.this, SplashScreen.class);
                startActivity(mainIntent);
            }
        });

    }
}
