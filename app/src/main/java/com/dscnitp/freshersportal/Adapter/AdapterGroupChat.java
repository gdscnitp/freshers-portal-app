package com.dscnitp.freshersportal.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dscnitp.freshersportal.ImageViewActivity;
import com.dscnitp.freshersportal.Model.ModelGroupChats;
import com.dscnitp.freshersportal.Model.ModelGroupChats;
import com.dscnitp.freshersportal.R;
import com.dscnitp.freshersportal.ViewPdfActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;

public class AdapterGroupChat extends RecyclerView.Adapter<AdapterGroupChat.Myholder> {
    private static final int MSG_TYPE_LEFT=0;
    private static final int MSG_TYPR_RIGHT=1;
    Context context;

    BitmapDrawable drawable;
    Bitmap bitmap;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    public AdapterGroupChat(Context context, ArrayList<ModelGroupChats> modelGroupChats) {
        this.context = context;
        this.modelGroupChats = modelGroupChats;
        firebaseAuth= FirebaseAuth.getInstance();
    }

    ArrayList<ModelGroupChats> modelGroupChats;

    @NonNull
    @Override
    public Myholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if(viewType==MSG_TYPE_LEFT){
            View view= LayoutInflater.from(context).inflate(R.layout.row_groupchat_left,parent,false);
            return new Myholder(view);
        }
        else{
            View view= LayoutInflater.from(context).inflate(R.layout.row_groupchat_right,parent,false);
            return new Myholder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final Myholder holder, final int position) {

        ModelGroupChats chats=modelGroupChats.get(position);
        final String message=chats.getMessage();
        String sender=chats.getSender();
        String timestamp=chats.getTimestamp();
        String type=chats.getType();
        Calendar calendar=Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(Long.parseLong(timestamp));
        String timedate= DateFormat.format("dd/MM/yyyy hh:mm aa",calendar).toString();

        setUserName(chats,holder);
        holder.time.setText(timedate);
        if(type.equals("text")){
            holder.message.setVisibility(View.VISIBLE);
            holder.image.setVisibility(View.GONE);
            holder.message.setText(message);
        }
        else if (type.equals("pdf")) {
            holder.message.setVisibility(View.GONE);
            holder.image.setVisibility(View.VISIBLE);
            holder.image.setImageResource(R.drawable.ic_file);
        }
        else {
            holder.message.setVisibility(View.GONE);
            holder.image.setVisibility(View.VISIBLE);
            Glide.with(context).load(message).placeholder(R.drawable.ic_group).into(holder.image);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (modelGroupChats.get(position).getType().equals("image")) {
                    CharSequence options[] = new CharSequence[]{
                            "View",
                            "Cancel",
                    };
                    AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                    builder.setTitle("");
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == 0) {
                                Intent image = new Intent(holder.itemView.getContext(), ImageViewActivity.class);
                                image.putExtra("url", modelGroupChats.get(position).getMessage());
                                holder.itemView.getContext().startActivity(image);

                            }

                        }
                    });
                    builder.show();
                }
                if (modelGroupChats.get(position).getType().equals("pdf")){
                    CharSequence options[] = new CharSequence[]{
                            "Download",
                            "View",
                            "Cancel"
                    };
                    AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                    builder.setTitle("Choose One");
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == 0) {
                                Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse(message));
                                holder.itemView.getContext().startActivity(intent);
                            }
                            if (which == 1){
                                Intent intent=new Intent(holder.itemView.getContext(), ViewPdfActivity.class);
                                intent.putExtra("url",message);
                                holder.itemView.getContext().startActivity(intent);
                            }
                        }
                    });
                    builder.show();

                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return modelGroupChats.size();
    }
    private void setUserName(ModelGroupChats groupChats, final Myholder holder){
        final String[] colors={"#e74c3c","#3499db","#1abc9c","#0A527E","#0A527E","#ffa600","#272727","#f7c59f","#f2f5f8"
        ,"#ffe400"};
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("users");
        reference.orderByChild("uid").equalTo(groupChats.getSender())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot ds:dataSnapshot.getChildren()){
                            String names=""+ds.child("name").getValue();
                            Random random=new Random();
                            int randomc=random.nextInt(colors.length);
                            int color= Color.parseColor(colors[randomc]);
                            holder.name.setText(names);
                            holder.name.setTextColor(color);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
    @Override
    public int getItemViewType(int position) {

        if(modelGroupChats.get(position).getSender().equals(firebaseAuth.getUid())){
            return MSG_TYPR_RIGHT;
        }
        else {
            return MSG_TYPE_LEFT;
        }
    }
    class Myholder extends RecyclerView.ViewHolder{

        TextView name,message,time;
        ImageView image;
        public Myholder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.sendername);
            image=itemView.findViewById(R.id.imagegrp);
            message=itemView.findViewById(R.id.sendermsg);
            time=itemView.findViewById(R.id.timegrp);
        }
    }
}
