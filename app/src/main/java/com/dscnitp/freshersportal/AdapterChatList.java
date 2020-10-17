package com.dscnitp.freshersportal;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class AdapterChatList extends RecyclerView.Adapter<AdapterChatList.Myholder> {

    Context context;
    FirebaseAuth firebaseAuth;
    String uid;
    public AdapterChatList(Context context, List<ModelUser> users) {
        this.context = context;
        this.usersList = users;
       lastMessageMap = new HashMap<>();
        firebaseAuth= FirebaseAuth.getInstance();
        uid=firebaseAuth.getUid();
    }

    List<ModelUser> usersList;
    private HashMap<String,String> lastMessageMap;
    @NonNull
    @Override
    public Myholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.row_chatlist,parent,false);
        return new Myholder(view);
    }
    String userimage;

    @Override
    public void onBindViewHolder(@NonNull final Myholder holder, final int position) {

        final String hisuid=usersList.get(position).getUid();
        String username=usersList.get(position).getName();
        String lastmess=lastMessageMap.get(hisuid);
        holder.name.setText(username);
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("users");
        reference.child(hisuid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("imgUrl").exists()){
                    userimage=dataSnapshot.child("imgUrl").getValue().toString();
                    try {
                        Glide.with(context).load(userimage).placeholder(R.drawable.ic_face).into(holder.profile);
                    }
                    catch (Exception e){
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, ChatActivity.class);
                intent.putExtra("uid",hisuid);
                context.startActivity(intent);
            }
        });
        setLastMessage(holder,position,hisuid);
    }

    private void setLastMessage(final Myholder holder, final int position, final String hisuid) {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("users");
        reference.child(uid).child("Messages").child(hisuid).limitToLast(1).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String message = "" + ds.child("message").getValue();
                    String sender = "" + ds.child("sender").getValue();
                    String receiver = "" + ds.child("receiver").getValue();
                    final String timestamp = "" + ds.child("timestamp").getValue();
                    Calendar calendar=Calendar.getInstance(Locale.ENGLISH);
                    calendar.setTimeInMillis(Long.parseLong(timestamp));
                    String timedate= DateFormat.format("dd/MM/yyyy hh:mm aa",calendar).toString();
                    boolean seen=(Boolean) ds.child("dilihat").getValue();
                    String type = "" + ds.child("type").getValue();
                    if (hisuid.equals(sender)) {
                        if (type.equals("text")) {
                            holder.lastmessage.setText(message);
                        } else if (type.equals("images")){
                            holder.lastmessage.setText("Received a Photo");
                        }else {
                            holder.lastmessage.setText("Received a Pdf");
                        }
                    }
                    else {
                        if (type.equals("text")) {
                            holder.lastmessage.setText(message);
                        } else if (type.equals("images")){
                            holder.lastmessage.setText("Sent a Photo");
                        }
                        else {
                            holder.lastmessage.setText("Sent a Pdf");
                        }
                    }
                    if (!sender.equals(uid)) {
                        if (seen) {
                            holder.seen.setVisibility(View.INVISIBLE);
                        } else {

                            holder.seen.setVisibility(View.VISIBLE);
                        }
                    }
                    holder.sendertime.setText(timedate);

                    setOrderByTime(holder,timestamp,position);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private void setOrderByTime(Myholder holder, String timestamp, int position) {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users");
        reference.child(uid).child("Messages").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int totalc=(int)dataSnapshot.getChildrenCount();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void setlastMessageMap(String userId,String lastmessage){
        lastMessageMap.put(userId,lastmessage);
    }
    @Override
    public int getItemCount() {
        return usersList.size();
    }


    class Myholder extends RecyclerView.ViewHolder{
        ImageView profile,status,block,seen;
        TextView name,lastmessage,sendertime;
        public Myholder(@NonNull View itemView) {
            super(itemView);
            profile=itemView.findViewById(R.id.profileimage);
            status=itemView.findViewById(R.id.onlinestatus);
            name=itemView.findViewById(R.id.nameonline);
            lastmessage=itemView.findViewById(R.id.lastmessge);
            sendertime=itemView.findViewById(R.id.sendingtime);
            block=itemView.findViewById(R.id.blocking);
            seen=itemView.findViewById(R.id.seen);
        }
    }
}
