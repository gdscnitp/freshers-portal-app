package com.dscnitp.freshersportal.Student;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.dscnitp.freshersportal.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class EditProfileActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    Button update;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("users");


        update = (Button) findViewById(R.id.update);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Query query = databaseReference.orderByChild("email").equalTo(user.getEmail());
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                        FirebaseDatabase database = FirebaseDatabase.getInstance();

                        DatabaseReference myRef = database.getReference("name");
                        TextInputEditText name = (TextInputEditText) findViewById(R.id.name);
                        String text = name.getText().toString();
                        myRef.setValue(text);

                        DatabaseReference myRef1 = database.getReference("roll");
                        TextInputEditText roll = (TextInputEditText) findViewById(R.id.roll);
                        String text1 = roll.getText().toString().trim();
                        myRef1.setValue(text1);

                        DatabaseReference myRef2 = database.getReference("branch");
                        TextInputEditText branch = (TextInputEditText) findViewById(R.id.branch);
                        String text2 = branch.getText().toString();
                        myRef2.setValue(text2);

                        DatabaseReference myRef3 = database.getReference("year");
                        TextInputEditText year = (TextInputEditText) findViewById(R.id.year);
                        String text3 = year.getText().toString();
                        myRef3.setValue(text3);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }
        });


    }
}