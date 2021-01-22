package com.dscnitp.freshersportal.Student;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dscnitp.freshersportal.Common.Node;
import com.dscnitp.freshersportal.R;
import com.dscnitp.freshersportal.SplashScreen;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.HashMap;

public class EditProfileActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    private TextInputEditText Name, Branch, RollNo, Year;
    Button logout;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseStorage mStorage;
    private DatabaseReference databaseReferenceUsers;
    private String PhotoUrl;
    private Uri ServerFileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("users");


        Name = (TextInputEditText) findViewById(R.id.name);
        RollNo = (TextInputEditText) findViewById(R.id.roll);
        Branch = (TextInputEditText) findViewById(R.id.branch);
        Year = (TextInputEditText) findViewById(R.id.year);
        ImageView ivProfile = findViewById(R.id.profileimage);
        logout=findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                finish();
                startActivity(new Intent(EditProfileActivity.this, SplashScreen.class));
            }
        });

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {

            databaseReferenceUsers = FirebaseDatabase.getInstance().getReference().child(Node.Users);
            databaseReferenceUsers.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(Node.Name).getValue() != null)
                        Name.setText(dataSnapshot.child(Node.Name).getValue().toString());

                    if (dataSnapshot.child(Node.ROLL_NO).getValue() != null)
                        RollNo.setText(dataSnapshot.child(Node.ROLL_NO).getValue().toString());

                    if (dataSnapshot.child(Node.Branch).getValue() != null)
                        Branch.setText(dataSnapshot.child(Node.Branch).getValue().toString());

                    if (dataSnapshot.child(Node.Year).getValue() != null)
                        Year.setText(dataSnapshot.child(Node.Year).getValue().toString());

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            ServerFileUri = currentUser.getPhotoUrl();

            Glide.with(this)
                    .load(ServerFileUri)
                    .error(R.mipmap.ic_launcher_foreground)
                    .placeholder(R.mipmap.ic_launcher_foreground)
                    .load(ivProfile);
        }
    }

    public void btnSave(View V) {
        if (Name.getText().toString().trim().equals("")) {
            Name.setError(getString(R.string.etName));
        }
        if (RollNo.getText().toString().trim().equals("")) {
            RollNo.setError(getString(R.string.etRoll));
        }
        if (Branch.getText().toString().trim().equals("")) {
            Branch.setError(getString(R.string.etBranch));
        }
        if (Year.getText().toString().trim().equals("")) {
            Year.setError(getString(R.string.etYear));
        } else {
            //            if(localFileUri!=null)
            //                updateNameAndPhoto();
            //            else
            updateNameOnly();
        }


    }
    public void updateNameOnly()
    {
        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder().
                setDisplayName(Name.getText().toString().trim()).build();

        currentUser.updateProfile(request).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    String UserID=currentUser.getUid();
                    HashMap hashMap=new HashMap();
                    hashMap.put(Node.Name,Name.getText().toString().trim());
                    hashMap.put(Node.ROLL_NO,RollNo.getText().toString().trim());
                    hashMap.put(Node.Branch,Branch.getText().toString().trim());
                    hashMap.put(Node.Year,Year.getText().toString().trim());

                    databaseReferenceUsers.child(UserID).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                finish();
                            }
                            else
                            {
                                Toast.makeText(EditProfileActivity.this,"User Not Created",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else
                {
                    Toast.makeText(EditProfileActivity.this,"Failed to Update : %1$s"+task.getException(),Toast.LENGTH_SHORT).show();

                }
            }
        });
    }
}
