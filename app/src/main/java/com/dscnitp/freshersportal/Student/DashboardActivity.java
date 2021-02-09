package com.dscnitp.freshersportal.Student;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


import com.dscnitp.freshersportal.Alumni.AlumniHomeFragment;
import com.dscnitp.freshersportal.R;
import com.dscnitp.freshersportal.SplashScreen;
import com.dscnitp.freshersportal.notifications.Token;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import am.appwise.components.ni.NoInternetDialog;

public class DashboardActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener
{

    private FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    String myuid;
//    Toolbar actionBar;
    BottomNavigationView navigationView;
    FirebaseAuth mAuth;
  NoInternetDialog noInternetDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        noInternetDialog = new NoInternetDialog.Builder(this).setBgGradientStart(Color.parseColor("#4488A7"))
                .setBgGradientCenter(Color.parseColor("#4488A7")).setButtonColor(Color.parseColor("#2196F3"))
                .setBgGradientEnd(Color.parseColor("#4488A7")).build();
//        actionBar=findViewById(R.id.toolbar);
//        setSupportActionBar(actionBar);
//        actionBar.setTitle("Profile Activity");
        mAuth=FirebaseAuth.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();
        loadFragments(new HomeFagment());
        navigationView=findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(this);
//
        checkUserStatus();
    }


    public boolean loadFragments(Fragment fragment) {
       if(fragment!=null)
       {
           getSupportFragmentManager().beginTransaction().replace(R.id.content, fragment).commit();
       }
        return true;
    }


    @Override
    protected void onResume() {
        checkUserStatus();
        super.onResume();
    }

    //////

    @Override
    protected void onStart() {
        super.onStart();
        checkUserStatus();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.logout,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            mAuth.signOut();
            finish();
            Intent mainIntent = new Intent(DashboardActivity.this, SplashScreen.class);
            startActivity(mainIntent);
        }
        if(item.getItemId()==R.id.action_post){
            Intent mainIntent = new Intent(DashboardActivity.this, ProfileActivity.class);
            startActivity(mainIntent);
        }
        return super.onOptionsItemSelected(item);
    }

    private void checkUserStatus(){
        FirebaseUser user=firebaseAuth.getCurrentUser();
        if(user!=null){
            myuid=user.getUid();
            SharedPreferences sharedPreferences=getSharedPreferences("SP_USER",MODE_PRIVATE);
            SharedPreferences.Editor editor=sharedPreferences.edit();
            editor.putString("CURRENT_USERID",myuid);
            editor.apply();
            updateToken(FirebaseInstanceId.getInstance().getToken());
        }
        else {
            startActivity(new Intent(DashboardActivity.this,SplashScreen.class));
            finish();
        }
    }

    public void updateToken(String token){
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1=new Token(token);
        ref.child(myuid).setValue(token1);
        DatabaseReference references= FirebaseDatabase.getInstance().getReference("users").child(myuid);
        references.child("device_token").setValue(token);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem)
    {
     Fragment fragment= null;

     switch(menuItem.getItemId())
     {
         case R.id.nav_home:
             fragment= new HomeFagment();
             break;
         case R.id.nav_message:
             fragment= new ChatFragment();
             break;
         case R.id.nav_post:
             fragment= new AddPostFragment();
             break;
         case R.id.nav_cnotes:
             fragment= new NotesFragment();
             break;
         case R.id.nav_profile:
             fragment= new ProfileFragment();
             break;
     }
        return loadFragments(fragment);
    }

    @Override
    public void onBackPressed() {
        if(navigationView.getSelectedItemId()== R.id.nav_home)
        {
            super.onBackPressed();
            finish();
        }
       else
        {
            navigationView.setSelectedItemId(R.id.nav_home);
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        noInternetDialog.onDestroy();
    }
}