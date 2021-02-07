package com.dscnitp.freshersportal.Student;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.dscnitp.freshersportal.Adapter.AdapterBlogs;
import com.dscnitp.freshersportal.Model.ModelBlogs;
import com.dscnitp.freshersportal.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import am.appwise.components.ni.NoInternetDialog;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFagment extends Fragment {


    public HomeFagment() {
        // Required empty public constructor
    }

    FirebaseAuth firebaseAuth;
    String myuid;
    RecyclerView recyclerView;
    List<ModelBlogs> posts;
    AdapterBlogs adapterPosts;
    //NoInternetDialog noInternetDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_home_fagment, container, false);
        firebaseAuth= FirebaseAuth.getInstance();
        recyclerView=view.findViewById(R.id.postrecyclerview);

        //noInternetDialog = new NoInternetDialog.Builder(this).build();

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager=new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        posts=new ArrayList<>();
        loadPosts();
        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        loadPosts();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadPosts();
    }

    private void loadPosts() {

        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Blogs");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                posts.clear();
                for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    if(dataSnapshot1.exists()) {
                        ModelBlogs modelPost = dataSnapshot1.getValue(ModelBlogs.class);
                        posts.add(modelPost);
                        adapterPosts = new AdapterBlogs(getActivity(), posts);
                        recyclerView.setAdapter(adapterPosts);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(getActivity(),databaseError.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

}
