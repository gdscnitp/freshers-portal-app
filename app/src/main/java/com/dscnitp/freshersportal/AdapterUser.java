package com.dscnitp.freshersportal;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterUser extends RecyclerView.Adapter<AdapterUser.MyHolder> {

    Context context;
    FirebaseAuth firebaseAuth;
    String uid;

    public AdapterUser(Context context, List<ModelUser> list) {
        this.context = context;
        this.list = list;
        firebaseAuth= FirebaseAuth.getInstance();
        uid=firebaseAuth.getUid();
    }

    List<ModelUser> list;
    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.row_users,parent,false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, final int position) {
        final String hisuid = list.get(position).getUid();
        String userImage = list.get(position).getImgUrl();
        String username = list.get(position).getName();
        String claase = list.get(position).getAboutYou();
        holder.name.setText(username);
        setClass(hisuid,holder);
        try {
            Glide.with(context).load(userImage).placeholder(R.drawable.ic_face).into(holder.profiletv);
        } catch (Exception e) {
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                            Intent intent = new Intent(context, ChatActivity.class);
                            intent.putExtra("uid", hisuid);
                            context.startActivity(intent);
                        }
                    });
            }

    private void setClass(String hisuid, final MyHolder holder) {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users");
        reference.orderByChild("uid").equalTo(hisuid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    String type=""+dataSnapshot1.child("type").getValue();
                    if (type.equals("student")){
                        String clas=""+dataSnapshot1.child("classs").getValue();
                        String category=""+dataSnapshot1.child("category").getValue();
                        if (clas.equals(null)){
                            clas="";
                        }
                        if (category.equals(null)){
                            category="";
                        }
                        holder.classes.setText(clas +" " +category);
                    }
                   else {
                        String subject=""+dataSnapshot1.child("subject").getValue();
                        holder.classes.setText(subject);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyHolder extends RecyclerView.ViewHolder{

        CircleImageView profiletv;
        TextView name,classes;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            profiletv=itemView.findViewById(R.id.imagep);
            name=itemView.findViewById(R.id.namep);
            classes=itemView.findViewById(R.id.classp);
        }

    }
}
