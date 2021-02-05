package com.dscnitp.freshersportal.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dscnitp.freshersportal.Model.ModelUser;
import com.dscnitp.freshersportal.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class AdapterParticipantsAd extends RecyclerView.Adapter<AdapterParticipantsAd.MyHolder> {

    Context context;


    ArrayList<ModelUser> users;

    public AdapterParticipantsAd(Context context, ArrayList<ModelUser> users, String groupid, String mygrouprole) {
        this.context = context;
        this.users = users;
        this.groupid = groupid;
        this.mygrouprole = mygrouprole;
    }

    String groupid,mygrouprole;

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.row_add_participants,parent,false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        final ModelUser modelUsers=users.get(position);
        String name=modelUsers.getName();
        String email=modelUsers.getEmail();
        String hisuid=modelUsers.getUid();
        String image=modelUsers.getImgUrl();
        final String uid=modelUsers.getUid();
        holder.name.setText(name);
        holder.email.setText(email);
        try {
            Glide.with(context).load(image).placeholder(R.drawable.ic_face).into(holder.icon);
        }
        catch (Exception e){

        }
        setClass(hisuid,holder);
        checkIfUserAlreadyExist(modelUsers,holder);

    }

    private void setClass(String hisuid, final AdapterParticipantsAd.MyHolder holder) {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("users");
        reference.orderByChild("uid").equalTo(hisuid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                        String category=""+dataSnapshot1.child("email").getValue();
                        holder.email.setText(category);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void checkIfUserAlreadyExist(ModelUser modelUsers, final MyHolder holder){
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Groups");
        reference.child(groupid).child("Participants").child(modelUsers.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            String hisRole=""+dataSnapshot.child("role").getValue();
                            holder.status.setText(hisRole);
                        }
                        else {
                            holder.status.setText("");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class MyHolder extends RecyclerView.ViewHolder{
        ImageView icon;
        TextView name,email,status;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            icon=itemView.findViewById(R.id.imagepg);
            name=itemView.findViewById(R.id.namepg);
            email=itemView.findViewById(R.id.emailpg);
            status=itemView.findViewById(R.id.status);
        }
    }
}
