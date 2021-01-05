package com.dscnitp.freshersportal.Alumni;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dscnitp.freshersportal.Common.Node;
import com.dscnitp.freshersportal.R;
import com.dscnitp.freshersportal.SplashScreen;
import com.dscnitp.freshersportal.Student.EditProfileActivity;
import com.dscnitp.freshersportal.notifications.Token;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

public class AlumniMainActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private Uri ServerFileUri;
    private DatabaseReference databaseReferenceUsers;

    String myuid;
    Toolbar actionBar;
    BottomNavigationView navigationView;
    FirebaseAuth mAuth;

    private Button Edit;
    private ImageView ivProfile;
    private TextView Name,company;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alumni_main);
        actionBar=findViewById(R.id.toolbar);
        setSupportActionBar(actionBar);
        actionBar.setTitle("");
        mAuth=FirebaseAuth.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();
        navigationView=findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(selectedListener);
        actionBar.setTitle("Home");

        Edit=findViewById(R.id.edit);
        ivProfile=findViewById(R.id.logo);
        Name=findViewById(R.id.name);
        company=findViewById(R.id.Com);

        if(firebaseUser!=null)
        {
            Name.setText(firebaseUser.getDisplayName());
            ServerFileUri=firebaseUser.getPhotoUrl();

            databaseReferenceUsers = FirebaseDatabase.getInstance().getReference().child(Node.Users);
            databaseReferenceUsers.child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()) {
                        if (dataSnapshot.child(Node.Company).getValue()!=null)
                            company.setText(dataSnapshot.child(Node.Company).getValue().toString());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            if(ServerFileUri!=null)
            {
                Glide.with(this)
                        .load(ServerFileUri)
                        .placeholder(R.mipmap.ic_launcher_foreground)
                        .error(R.mipmap.ic_launcher_foreground)
                        .into(ivProfile);
            }
        }

        Edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AlumniMainActivity.this, AlumniProfileActivity.class));
            }
        });

        AlumniHomeFragment fragment=new AlumniHomeFragment();
        FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content,fragment,"");
        fragmentTransaction.commit();
        checkUserStatus();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener selectedListener=new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            switch (menuItem.getItemId()){
                case R.id.nav_home:
                    actionBar.setTitle("Home");
                    AlumniHomeFragment fragment=new AlumniHomeFragment();
                    FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.content,fragment,"");
                    fragmentTransaction.commit();

                    return true;
                case R.id.nav_message:
                    startActivity(new Intent(AlumniMainActivity.this, AlumniChatActivity.class));
                    return true;
                case R.id.nav_profile:
                    actionBar.setTitle("Profile");
                    AlumniProfileFragment listFragment2=new AlumniProfileFragment();
                    FragmentTransaction fragmentTransaction2=getSupportFragmentManager().beginTransaction();
                    fragmentTransaction2.replace(R.id.content,listFragment2,"");
                    fragmentTransaction2.commit();
            }
            return false;
        }
    };

    @Override
    protected void onStart() {
        super.onStart();

        DatabaseReference references= FirebaseDatabase.getInstance().getReference("users").child(myuid);
        references.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("year").exists()) {
                    String year = dataSnapshot.child("year").getValue().toString();
                    creategrp(year);
                }
                else {
                    startActivity(new Intent(AlumniMainActivity.this,AlumniProfileActivity.class));
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void creategrp(final String year){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Groups");
        reference.child(year).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String time=""+System.currentTimeMillis();
                    HashMap<String ,Object > hashMap1=new HashMap<>();
                    hashMap1.put("uid",firebaseAuth.getUid());
                    hashMap1.put("role","creator");
                    DatabaseReference reference2=FirebaseDatabase.getInstance().getReference("Groups");
                    reference2.child(year).child("Participants").child(firebaseAuth.getUid())
                            .updateChildren(hashMap1)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    return;
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                        }
                    });
                }else{
                    final String time=""+System.currentTimeMillis();
                    final HashMap<String,String> hashMap=new HashMap<>();
                    hashMap.put("grpId",year);
                    hashMap.put("grptitle","session" + year);
                    hashMap.put("grpdesc","This Group is created for this session");
                    hashMap.put("grpicon","");
                    hashMap.put("timestamp",time);
                    hashMap.put("createBy",firebaseAuth.getUid());
                    DatabaseReference reference1= FirebaseDatabase.getInstance().getReference("Groups");
                    reference1.child(year).setValue(hashMap)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    HashMap<String ,String > hashMap1=new HashMap<>();
                                    hashMap1.put("uid",firebaseAuth.getUid());
                                    hashMap1.put("role","creator");
                                    DatabaseReference reference2=FirebaseDatabase.getInstance().getReference("Groups");
                                    reference2.child(year).child("Participants").child(firebaseAuth.getUid())
                                            .setValue(hashMap1)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    return;
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
            startActivity(new Intent(AlumniMainActivity.this, SplashScreen.class));
            finish();
        }
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
            Intent mainIntent = new Intent(AlumniMainActivity.this, SplashScreen.class);
            startActivity(mainIntent);
        }
        if(item.getItemId()==R.id.action_post){
            Intent mainIntent = new Intent(AlumniMainActivity.this, AddPostActivity.class);
            startActivity(mainIntent);
        }
        return super.onOptionsItemSelected(item);
    }

    public void updateToken(String token){
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1=new Token(token);
        ref.child(myuid).setValue(token1);
        DatabaseReference references= FirebaseDatabase.getInstance().getReference("users").child(myuid);
        references.child("device_token").setValue(token);
    }

}
