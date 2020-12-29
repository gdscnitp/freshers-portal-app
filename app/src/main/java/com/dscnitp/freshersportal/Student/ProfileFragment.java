package com.dscnitp.freshersportal.Student;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.dscnitp.freshersportal.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;


    //views from xml
    TextView nameTv, rollTv, branchTv, yearTv ;
    ImageView logo;
    Button editBtn, submit;
    RatingBar ratingBar;


    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view= inflater.inflate(R.layout.fragment_profile, container, false);
        ratingBar = (RatingBar) view.findViewById(R.id.ratingBar);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                Toast.makeText(getActivity(), rating + "stars", Toast.LENGTH_SHORT).show();
            }
        });
        submit = (Button) view.findViewById(R.id.submitBtn);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String totalStars = "Total Stars: " + ratingBar.getNumStars();
                String rating = "Rating : " + ratingBar.getRating();
                Toast.makeText(getActivity(), totalStars + "\n" + rating, Toast.LENGTH_SHORT).show();
            }
        });



        //init firebase
        firebaseAuth = FirebaseAuth.getInstance();
        user= firebaseAuth.getCurrentUser();
        firebaseDatabase= FirebaseDatabase.getInstance();
        databaseReference= firebaseDatabase.getReference("users");

        nameTv = view.findViewById(R.id.nameTv);
        rollTv= view.findViewById(R.id.rollTv);
        branchTv= view.findViewById(R.id.branchTv);
        yearTv= view.findViewById(R.id.yearTv);

        editBtn = (Button) view.findViewById(R.id.editBtn);
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), EditProfileActivity.class);
                startActivity(intent);
            }
        });
        //get info using signed in email of user

        Query query= databaseReference.orderByChild("email").equalTo(user.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String name = "" + ds.child("name").getValue();
                    String roll = "" + ds.child("roll").getValue();
                    String branch = "" + ds.child("branch").getValue();
                    String year = "" + ds.child("year").getValue();
                    nameTv.setText(name);
                    rollTv.setText(roll);
                    branchTv.setText(branch);
                    yearTv.setText(year);

               }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });



        return view;
    }
}
