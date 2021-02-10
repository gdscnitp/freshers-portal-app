package com.dscnitp.freshersportal.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dscnitp.freshersportal.Common.Node;
import com.dscnitp.freshersportal.Model.ModelBlogs;
import com.dscnitp.freshersportal.PostDetailsActivity;
import com.dscnitp.freshersportal.R;
import com.dscnitp.freshersportal.Student.ProfileActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AdapterBlogs extends RecyclerView.Adapter<AdapterBlogs.MyHolder>{


    Context context;
    String myuid;
    private DatabaseReference liekeref,postref;
    boolean mprocesslike=false;


    public AdapterBlogs(Context context, List<ModelBlogs> modelPosts) {
        this.context = context;
        this.modelPosts = modelPosts;
        myuid= FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    List<ModelBlogs> modelPosts;

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.row_posts,parent,false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyHolder holder, final int position) {
        final String uid=modelPosts.get(position).getWrittenby();
        final String titlee=modelPosts.get(position).getTitle();
        final String descri=modelPosts.get(position).getDescription();
        final String ptime=modelPosts.get(position).getTime();
        final String type=modelPosts.get(position).getType();
        final String department=modelPosts.get(position).getDepartment();
        Calendar calendar=Calendar.getInstance(Locale.ENGLISH);
        if(ptime!=null)
        calendar.setTimeInMillis(Long.parseLong(ptime));
        String timedate= DateFormat.format("dd/MM/yyyy hh:mm aa",calendar).toString();
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child(Node.Users);
        reference.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name=dataSnapshot.child("name").getValue().toString();
                String imgUrl=dataSnapshot.child("imgUrl").getValue().toString();
                holder.name.setText(name);
                Glide.with(context).load(imgUrl).into(holder.picture);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        holder.title.setText(titlee);
        if(type.equals("Inspirational")){
            holder.title.setTextColor(Color.GREEN);
        } if(type.equals("Coding")){
            holder.title.setTextColor(Color.BLUE);
        } if(type.equals("Gate")){
            holder.title.setTextColor(Color.YELLOW);
        }
        holder.description.setText(descri);
        holder.time.setText(timedate);
        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, PostDetailsActivity.class);
                intent.putExtra("pid",ptime);
                context.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return modelPosts.size();
    }

    class MyHolder extends RecyclerView.ViewHolder{
        ImageView picture;
        TextView name,time,title,description,like,comments;
        Button likebtn,comment;
        LinearLayout profile;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            picture=itemView.findViewById(R.id.picturetv);
            name=itemView.findViewById(R.id.unametv);
            time=itemView.findViewById(R.id.utimetv);
            title=itemView.findViewById(R.id.ptitletv);
            description=itemView.findViewById(R.id.descript);
            like=itemView.findViewById(R.id.plikeb);
            comments=itemView.findViewById(R.id.pcommentco);
            likebtn=itemView.findViewById(R.id.like);
            comment=itemView.findViewById(R.id.comment);
            profile=itemView.findViewById(R.id.profilelayout);
        }
    }
}