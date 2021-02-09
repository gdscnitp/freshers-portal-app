package com.dscnitp.freshersportal;


import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dscnitp.freshersportal.Adapter.AdapterParticipantsAd;
import com.dscnitp.freshersportal.Model.ModelUser;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import am.appwise.components.ni.NoInternetDialog;

public class GroupInfoActivity extends AppCompatActivity {

    private String myGrprole="";
    FirebaseAuth firebaseAuth;
    String groupid;
    ActionBar actionBar;
    ImageView grpicon;
    Toolbar toolbar;
    TextView descri,createdby,editgrp,totalp;
    ArrayList<ModelUser> users;
    RecyclerView recyclerView;
    AdapterParticipantsAd participantsAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_info);

        toolbar=findViewById(R.id.toolbargrp);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        groupid=getIntent().getStringExtra("groupId");
        grpicon=findViewById(R.id.grpicons);
        descri=findViewById(R.id.descri);
        createdby=findViewById(R.id.createdby);
        editgrp=findViewById(R.id.editgrp);
        getSupportActionBar().setTitle("");

        totalp=findViewById(R.id.totalparticipants);
        recyclerView=findViewById(R.id.particpantstv);
        firebaseAuth= FirebaseAuth.getInstance();
        loadGroupInfo();
        loadParticipants();
        editgrp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(GroupInfoActivity.this,GroupEditActivity.class);
                intent.putExtra("groupId",groupid);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadGroupInfo();
    }

    private void loadParticipants() {

        users=new ArrayList<>();
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Groups");
        reference.child(groupid).child("Participants").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    users.clear();
                    final String uid=""+dataSnapshot1.child("uid").getValue();
                    DatabaseReference reference1=FirebaseDatabase.getInstance().getReference("users");
                    reference1.orderByChild("uid").equalTo(uid).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot dataSnapshot11:dataSnapshot.getChildren()){
                                ModelUser userss=dataSnapshot11.getValue(ModelUser.class);
                                users.add(userss);
                            }
                            participantsAd=new AdapterParticipantsAd(GroupInfoActivity.this,users,groupid,myGrprole);
                            recyclerView.setAdapter(participantsAd);
                            totalp.setText("Participants (" +users.size() + ")");
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadGroupInfo() {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Groups");
        reference.orderByChild("grpId").equalTo(groupid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds:dataSnapshot.getChildren()){
                            String grptitle=""+ds.child("grptitle").getValue();
                            String grpdesc=""+ds.child("grpdesc").getValue();
                            String groupicon=""+ds.child("grpicon").getValue();
                            String timstamp=""+ds.child("timestamp").getValue();
                            Calendar calendar=Calendar.getInstance(Locale.ENGLISH);
                            calendar.setTimeInMillis(Long.parseLong(timstamp));
                            String timedate= DateFormat.format("dd/MM/yyyy hh:mm aa",calendar).toString();
                            getSupportActionBar().setTitle(grptitle);
                            descri.setText(grpdesc);
                            try {
                                Glide.with(getApplicationContext()).load(groupicon).into(grpicon);
                            }
                            catch (Exception e){

                            }


                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
