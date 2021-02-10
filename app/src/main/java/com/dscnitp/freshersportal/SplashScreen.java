package com.dscnitp.freshersportal;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.dscnitp.freshersportal.Alumni.AlumniMainActivity;
import com.dscnitp.freshersportal.Student.DashboardActivity;
import com.dscnitp.freshersportal.Student.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import am.appwise.components.ni.NoInternetDialog;

public class SplashScreen extends AppCompatActivity {

    Animation top, bottom;
    ImageView image;
    TextView logo;
    FirebaseUser user;
    FirebaseAuth firebaseAuth;
    private static int splash = 2000;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);
        top = AnimationUtils.loadAnimation(this,R.anim.top);
        bottom = AnimationUtils.loadAnimation(this,R.anim.bottom);

        image = findViewById(R.id.logoSplash);
        logo = findViewById(R.id.text_fresher);
        image.setAnimation(top);
        logo.setAnimation(bottom);
        firebaseAuth=FirebaseAuth.getInstance();
        if (firebaseAuth !=null) {
            user = firebaseAuth.getCurrentUser();
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                FirebaseUser user=firebaseAuth.getCurrentUser();
                if(user==null){
                    Intent intent = new Intent(SplashScreen.this, UserTypeActivity.class);
                    startActivity(intent);
                    finish();
                }
                else {
                    checkUserType();
                }
            }
        },splash);

    }

    private void checkUserType() {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("users");
        reference.orderByChild("uid").equalTo(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    String accountType=""+dataSnapshot1.child("USER_TYPE").getValue();
                    if (accountType.equals("student")){
                        startActivity(new Intent(SplashScreen.this, DashboardActivity.class));
                        finish();
                    }
                    else {
                        startActivity(new Intent(SplashScreen.this, AlumniMainActivity.class));
                        finish();
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
