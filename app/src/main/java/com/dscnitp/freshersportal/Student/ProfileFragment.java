package com.dscnitp.freshersportal.Student;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.dscnitp.freshersportal.AboutUs;
import com.dscnitp.freshersportal.Common.Node;
import com.dscnitp.freshersportal.R;
import com.dscnitp.freshersportal.SplashScreen;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.time.Year;
import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    Button edit;
    Button logout;
    FirebaseAuth mAuth;
    TextView Name,Branch,RollNo, Year;
    TextView aboutUs, privacy;
    FirebaseStorage mStorage;
    FirebaseUser firebaseUser;
    Uri ServerFileUri;
    ImageView ivProfile;
    DatabaseReference databaseReferenceUsers;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;




    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view= inflater.inflate(R.layout.fragment_profile, container, false);

        Name=view.findViewById(R.id.name);
        RollNo=view.findViewById(R.id.roll);
        Branch=view.findViewById(R.id.branch);

        Year= view.findViewById(R.id.year);


        ivProfile = view.findViewById(R.id.profile_image);

        aboutUs= view.findViewById(R.id.aboutUs);

        mAuth=FirebaseAuth.getInstance();
        logout=view.findViewById(R.id.logout);
        privacy=view.findViewById(R.id.privacy);

        mAuth= FirebaseAuth.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        user= firebaseAuth.getCurrentUser();
        firebaseDatabase= FirebaseDatabase.getInstance();
        databaseReference= firebaseDatabase.getReference("users");

        privacy.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String url = "https://freshers-portal.flycricket.io/privacy.html";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });


        aboutUs.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent myintent = new Intent(getActivity(), AboutUs.class);
                startActivity(myintent);
            }
        });


        logout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent mainIntent = new Intent(getActivity(), SplashScreen.class);
                startActivity(mainIntent);
            }
        });

        edit=view.findViewById(R.id.edit);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), EditProfileActivity.class);
                startActivity(intent);
            }
        });

        mStorage= FirebaseStorage.getInstance();
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();


        if(firebaseUser!=null)
        {
            Name.setText(firebaseUser.getDisplayName());
            ServerFileUri=firebaseUser.getPhotoUrl();
            Query query= databaseReference.orderByChild("email").equalTo(user.getEmail());
            query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                String name = "" + ds.child("name").getValue();
                Name.setText(name);
                String roll = "" + ds.child("rollNo").getValue();
                RollNo.setText(roll);
                String branch = "" + ds.child("Branch").getValue();
                Branch.setText(branch);
                String year = "" + ds.child("year").getValue();
                Year.setText(year);

            }
        }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
          //


        }


        return view;
    }
}
