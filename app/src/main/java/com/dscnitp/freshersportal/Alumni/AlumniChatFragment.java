package com.dscnitp.freshersportal.Alumni;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dscnitp.freshersportal.Adapter.AdapterChatList;
import com.dscnitp.freshersportal.Adapter.AdapterUser;
import com.dscnitp.freshersportal.Model.ModelChat;
import com.dscnitp.freshersportal.Model.ModelChatList;
import com.dscnitp.freshersportal.Model.ModelUser;
import com.dscnitp.freshersportal.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import am.appwise.components.ni.NoInternetDialog;


/**
 * A simple {@link Fragment} subclass.
 */
public class AlumniChatFragment extends Fragment {



    public AlumniChatFragment() {
        // Required empty public constructor
    }

    FloatingActionButton actionBar;
    FirebaseAuth firebaseAuth;
    RecyclerView recyclerView;
    List<ModelChatList> chatListList;
    List<ModelUser> usersList;
    Query reference;
    AdapterChatList adapterChatList;
    List<ModelChat> chatList;
    FirebaseUser firebaseUser;
    NoInternetDialog noInternetDialog;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_alumni_chat, container, false);
        noInternetDialog = new NoInternetDialog.Builder(this).build();
        actionBar =view.findViewById(R.id.seeallcontacts);
        actionBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seeAllContact();
            }
        });
        firebaseAuth= FirebaseAuth.getInstance();
        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        recyclerView=view.findViewById(R.id.alumnichats);
        LinearLayoutManager layoutManager=new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        chatListList=new ArrayList<>();
        chatList=new ArrayList<>();
        load();
        return view;
    }
    private void seeAllContact() {
        startActivity(new Intent(getContext(), AlumniAllContactActivity.class));
    }

    @Override
    public void onStart() {
        super.onStart();
        load();
    }


    private void load() {
        reference= FirebaseDatabase.getInstance().getReference("ChatList").child(firebaseUser.getUid()).orderByChild("datetime");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatListList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    ModelChatList modelChatList = ds.getValue(ModelChatList.class);
                    if(!modelChatList.getId().equals(firebaseUser.getUid())) {
                        chatListList.add(modelChatList);
                    }

                }
                loadChats();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        load();
    }


    private void loadChats() {
        usersList=new ArrayList<>();
        reference=FirebaseDatabase.getInstance().getReference("Chat").child(firebaseUser.getUid()).orderByChild("order").startAt("2").endAt("2\uf8ff");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usersList.clear();
                for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    ModelUser user=dataSnapshot1.getValue(ModelUser.class);
                    for (ModelChatList chatList:chatListList){
                        if(user.getUid()!=null && user.getUid().equals(chatList.getId())){
                            usersList.add(user);
                            break;
                        }
                    }
                    adapterChatList=new AdapterChatList(getActivity(),usersList);
                    recyclerView.setAdapter(adapterChatList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void searchusers(final String s)
    {
        final FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usersList.clear();
                for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    ModelUser modelUser=dataSnapshot1.getValue(ModelUser.class);
                    if(!modelUser.getUid().equals(firebaseUser.getUid())){
                        if(modelUser.getName().toLowerCase().contains(s.toLowerCase())){
                            usersList.add(modelUser);
                        }
                        AdapterUser user=new AdapterUser(getActivity(),usersList);
                        recyclerView.setAdapter(user);
                        user.notifyDataSetChanged();
                    }

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
