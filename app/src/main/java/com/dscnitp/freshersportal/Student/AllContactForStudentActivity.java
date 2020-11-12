package com.dscnitp.freshersportal.Student;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dscnitp.freshersportal.Adapter.AdapterUser;
import com.dscnitp.freshersportal.Model.ModelUser;
import com.dscnitp.freshersportal.R;
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

