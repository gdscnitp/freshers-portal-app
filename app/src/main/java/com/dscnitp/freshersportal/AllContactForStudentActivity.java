package com.dscnitp.freshersportal;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AllContactForStudentActivity extends AppCompatActivity {

    ArrayList<ModelUser> modelUsers;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_contact_for_student);
        recyclerView=findViewById(R.id.allUsers);
        Toolbar toolbar=findViewById(R.id.allcontact);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("All Users");
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        modelUsers=new ArrayList<>();
        getAllUsers();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        if(item.getItemId()==R.id.action_search){
//
//        }
        return super.onOptionsItemSelected(item);
    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.search,menu);
//        MenuItem item=menu.findItem(R.id.action_search);
//        SearchView searchView=(SearchView)menu.findItem(R.id.action_search).getActionView();
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                if(!TextUtils.isEmpty(query.trim())){
//                    searchusers(query);
//                }
//                else {
//                    getAllUsers();
//                }
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                if(!TextUtils.isEmpty(newText.trim())){
//                    searchusers(newText);
//                }
//                else {
//                    getAllUsers();
//                }
//                return false;
//            }
//        });
//        return super.onCreateOptionsMenu(menu);
//    }

    private void getAllUsers() {
        final FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                modelUsers.clear();
                for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren()) {
                        ModelUser modelUser = dataSnapshot1.getValue(ModelUser.class);
                        if (!modelUser.getUid().equals(firebaseUser.getUid())) {
                            modelUsers.add(modelUser);
                        }
                        AdapterUser user = new AdapterUser(AllContactForStudentActivity.this, modelUsers);
                        recyclerView.setAdapter(user);
                        user.notifyDataSetChanged();
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
                modelUsers.clear();
                for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    String type = dataSnapshot1.child("type").getValue().toString();
                        ModelUser modelUser = dataSnapshot1.getValue(ModelUser.class);
                        if (!modelUser.getUid().equals(firebaseUser.getUid())) {
                            if (modelUser.getName().toLowerCase().contains(s.toLowerCase())) {
                                modelUsers.add(modelUser);
                            }
                            AdapterUser user = new AdapterUser(AllContactForStudentActivity.this, modelUsers);
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


}

