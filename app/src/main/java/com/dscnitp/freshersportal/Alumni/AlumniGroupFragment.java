package com.dscnitp.freshersportal.Alumni;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.dscnitp.freshersportal.Adapter.AdapterGroupChatList;
import com.dscnitp.freshersportal.Model.ModelGroupChatList;
import com.dscnitp.freshersportal.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import am.appwise.components.ni.NoInternetDialog;


/**
 * A simple {@link Fragment} subclass.
 */
public class AlumniGroupFragment extends Fragment {

    RecyclerView recyclerView;
    FirebaseAuth firebaseAuth;
    ArrayList<ModelGroupChatList> groupChats;
    AdapterGroupChatList chatList;
    NoInternetDialog noInternetDialog;


    public AlumniGroupFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_alumni_group, container, false);
        recyclerView=view.findViewById(R.id.grptv);
        firebaseAuth=FirebaseAuth.getInstance();
        loadGroupChat();
        noInternetDialog = new NoInternetDialog.Builder(this).build();
        return view;
    }
    private void loadGroupChat(){
        groupChats=new ArrayList<>();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Groups");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                groupChats.clear();
                for (DataSnapshot ds:dataSnapshot.getChildren()){
                    if(ds.child("Participants").child(firebaseAuth.getUid()).exists()){
                        ModelGroupChatList chat=ds.getValue(ModelGroupChatList.class);
                        groupChats.add(chat);
                    }
                    chatList=new AdapterGroupChatList(getActivity(),groupChats);
                    recyclerView.setAdapter(chatList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void searchGroupChat(final String query){
        groupChats=new ArrayList<>();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Groups");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                groupChats.clear();
                for (DataSnapshot ds:dataSnapshot.getChildren()){
                    if(ds.child("Participants").child(firebaseAuth.getUid()).exists()){
                        if(ds.child("grptitle").toString().toLowerCase().contains(query.toLowerCase())) {
                            ModelGroupChatList chat = ds.getValue(ModelGroupChatList.class);
                            groupChats.add(chat);
                        }
                    }
                    chatList=new AdapterGroupChatList(getActivity(),groupChats);
                    recyclerView.setAdapter(chatList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        noInternetDialog.onDestroy();
    }

}
