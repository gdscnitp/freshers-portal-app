package com.dscnitp.freshersportal.Student;


import android.content.Intent;
import android.net.Uri;
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

import com.bumptech.glide.Glide;
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


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    Button edit;
    Button logout;
    FirebaseAuth mAuth;
    TextView Name,Branch,RollNo;
    FirebaseStorage mStorage;
    FirebaseUser firebaseUser;
    Uri ServerFileUri;
    ImageView ivProfile;
    DatabaseReference databaseReferenceUsers;


    //views from xml
    TextView yearTv ;

    Button submit;
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





        Name=view.findViewById(R.id.name);
        RollNo=view.findViewById(R.id.roll);
        Branch=view.findViewById(R.id.branch);
        yearTv= view.findViewById(R.id.yearTv);



        ivProfile=view.findViewById(R.id.ProfileImage);

        mAuth=FirebaseAuth.getInstance();
        logout=view.findViewById(R.id.logout);
        mAuth= FirebaseAuth.getInstance();

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

            databaseReferenceUsers = FirebaseDatabase.getInstance().getReference().child(Node.Users);
            databaseReferenceUsers.child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(Node.ROLL_NO).getValue() != null)
                        RollNo.setText(dataSnapshot.child(Node.ROLL_NO).getValue().toString());

                    if (dataSnapshot.child(Node.Branch).getValue() != null)
                        Branch.setText(dataSnapshot.child(Node.Branch).getValue().toString());
//                    yearTv.setText(yearTv);

               }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        }

        if(ServerFileUri!=null)
        {
            Glide.with(this)
                    .load(ServerFileUri)
                    .placeholder(R.mipmap.ic_launcher_foreground)
                    .error(R.mipmap.ic_launcher_foreground)
                    .into(ivProfile);

        }


        return view;
    }
}
