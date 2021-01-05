package com.dscnitp.freshersportal.Alumni;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.dscnitp.freshersportal.Adapter.AdapterBlogs;
import com.dscnitp.freshersportal.Common.Node;
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


/**
 * A simple {@link Fragment} subclass.
 */
public class AlumniHomeFragment extends Fragment {

    FirebaseAuth firebaseAuth;
    String myuid;
    RecyclerView recyclerView;
    List<ModelBlogs> posts;
    AdapterBlogs adapterPosts;

    public AlumniHomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_alumni_home, container, false);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        firebaseAuth=FirebaseAuth.getInstance();
        recyclerView=view.findViewById(R.id.postrecyclerview);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager=new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        posts=new ArrayList<>();
        adapterPosts=new AdapterBlogs(getActivity(),posts);
        recyclerView.setAdapter(adapterPosts);
        loadPosts();
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
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference().child("Blogs");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                posts.clear();
                for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    if(dataSnapshot1.exists()) {
                        //ModelBlogs modelPost = dataSnapshot1.getValue(ModelBlogs.class);

                        String department=dataSnapshot1.child(Node.Department).getValue()!=null?
                                dataSnapshot1.child(Node.Department).getValue().toString():"";
                        String description=dataSnapshot1.child(Node.Description).getValue()!=null?
                                dataSnapshot1.child(Node.Description).getValue().toString():"";
                        String Time=dataSnapshot1.child(Node.Time).getValue()!=null?
                                dataSnapshot1.child(Node.Time).getValue().toString():"";
                        String Title=dataSnapshot1.child(Node.Title).getValue()!=null?
                                dataSnapshot1.child(Node.Title).getValue().toString():"";
                        String Type=dataSnapshot1.child(Node.Type).getValue()!=null?
                                dataSnapshot1.child(Node.Type).getValue().toString():"";
                        String WrittenBy=dataSnapshot1.child(Node.Writtenby).getValue()!=null?
                                dataSnapshot1.child(Node.Writtenby).getValue().toString():"";
                        ModelBlogs modelPost = new ModelBlogs(Type,WrittenBy,Title,description,department,Time);
                        posts.add(modelPost);
                        adapterPosts.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(getActivity(),databaseError.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }
}
