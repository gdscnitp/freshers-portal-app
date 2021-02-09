package com.dscnitp.freshersportal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;


import com.bumptech.glide.Glide;
import com.dscnitp.freshersportal.Adapter.AdapterComment;
import com.dscnitp.freshersportal.Model.ModelComment;
import com.dscnitp.freshersportal.Student.DashboardActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import am.appwise.components.ni.NoInternetDialog;

public class PostDetailsActivity extends AppCompatActivity {


    String hisuid,ptime,myuid,myname,myemail,mydp,uimage,
            postId,plike,hisdp,hisname;
    ImageView picture,image;
    TextView name,time,title,description;
    ImageButton more;
    Button likebtn,share;
    LinearLayout profile;
    EditText comment;
    ImageButton sendb;
    RecyclerView recyclerView;
    List<ModelComment> commentList;
    AdapterComment adapterComment;
    ImageView imagep;
    boolean mlike=false;
//    Toolbar actionBar;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);

//        actionBar = findViewById(R.id.toolbar);
//        actionBar.setTitle("Post Details");
//        actionBar= getActionBar().setDisplayHomeAsUpEnabled(0);
//        actionBar=getActionBar().setDisplayShowHomeEnabled();
        postId=getIntent().getStringExtra("pid");
        recyclerView=findViewById(R.id.recyclecomment);
        checkUserStatus();
        picture=findViewById(R.id.pictureco);
        name=findViewById(R.id.unameco);
        time=findViewById(R.id.utimeco);
        title=findViewById(R.id.ptitleco);
        description=findViewById(R.id.descriptco);

        likebtn=findViewById(R.id.like);
        comment=findViewById(R.id.typecommet);
        sendb=findViewById(R.id.sendcomment);
        imagep=findViewById(R.id.commentimge);
        profile=findViewById(R.id.profilelayout);
        progressDialog=new ProgressDialog(this);
        loadPostInfo();

        loadUserInfo();
//        actionBar.setSubtitle("SignedInAs:" +myemail);
        loadComments();
        sendb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postComment();
            }
        });


//        like.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent=new Intent(PostDetailsActivity.this, PostLikedByActivity.class);
//                intent.putExtra("pid",postId);
//                startActivity(intent);
//            }
//        });
    }
    private void checkUserStatus(){
        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null){

            myemail=user.getEmail();
            myuid=user.getUid();
        }
        else {
            startActivity(new Intent(PostDetailsActivity.this, DashboardActivity.class));
            finish();
        }
    }
    private void shareTextOnly(String titlee, String descri) {

        String sharebody= titlee + "\n" + descri;
        Intent intentt=new Intent(Intent.ACTION_SEND);
        intentt.setType("text/plain");
        intentt.putExtra(Intent.EXTRA_SUBJECT,"Subject Here");
        intentt.putExtra(Intent.EXTRA_TEXT,sharebody);
        startActivity(Intent.createChooser(intentt,"Share Via"));
    }

    private void shareImageandText(String titlee, String descri, Bitmap bitmap) {
        Uri uri=saveImageToShare(bitmap);
        String sharebody= titlee + "\n" + descri;
        Intent intent=new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM,uri);
        intent.putExtra(Intent.EXTRA_TEXT,sharebody);
        intent.putExtra(Intent.EXTRA_SUBJECT,"Subject Here");
        intent.setType("image/png");
        startActivity(Intent.createChooser(intent,"Share Via"));
    }
    private void addToHisNotification(String hisUid,String pid,String notification){
        String timestamp=""+System.currentTimeMillis();
        HashMap<Object,String> hashMap=new HashMap<>();
        hashMap.put("pid",pid);
        hashMap.put("timestamp",timestamp);
        hashMap.put("puid",hisUid);
        hashMap.put("notification",notification);
        hashMap.put("suid",myuid);
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users");
        reference.child(hisUid).child("Notifications").child(timestamp).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }
    private Uri saveImageToShare(Bitmap bitmap) {
        File imagefolder=new File(getCacheDir(),"images");
        Uri uri=null;
        try {
            imagefolder.mkdirs();
            File file=new File(imagefolder,"shared_image.png");
            FileOutputStream outputStream=new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG,90,outputStream);
            outputStream.flush();
            outputStream.close();
            uri= FileProvider.getUriForFile(this,"com.example.socialmediaapp.fileprovider",file);
        }
        catch (Exception e){

            Toast.makeText(PostDetailsActivity.this,""+e.getMessage(),Toast.LENGTH_LONG).show();
        }
        return uri;
    }

    private void loadComments() {

        LinearLayoutManager layoutManager=new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        commentList=new ArrayList<>();
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Posts").child(postId).child("Comments");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                commentList.clear();
                for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    ModelComment modelComment=dataSnapshot1.getValue(ModelComment.class);
                    commentList.add(modelComment);
                    adapterComment=new AdapterComment(getApplicationContext(),commentList,myuid,postId);
                    recyclerView.setAdapter(adapterComment);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }





    private void likepost() {

        mlike=true;
        final DatabaseReference liekeref=FirebaseDatabase.getInstance().getReference().child("Likes");
        final DatabaseReference postref=FirebaseDatabase.getInstance().getReference().child("Posts");
        liekeref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(mlike){
                    if(dataSnapshot.child(postId).hasChild(myuid)){
                        postref.child(postId).child("plike").setValue(""+(Integer.parseInt(plike)-1));
                        liekeref.child(postId).child(myuid).removeValue();
                        mlike=false;

                    }
                    else {
                        postref.child(postId).child("plike").setValue(""+(Integer.parseInt(plike)+1));
                        liekeref.child(postId).child(myuid).setValue("Liked");
                        mlike=false;
                        addToHisNotification(""+hisuid,""+myuid,"Liked Your Post");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void postComment() {
        progressDialog.setMessage("Adding Comment");

        final String commentss=comment.getText().toString().trim();
        if(TextUtils.isEmpty(commentss)){
            Toast.makeText(PostDetailsActivity.this,"Empty comment",Toast.LENGTH_LONG).show();
            return;
        }
        progressDialog.show();
        String timestamp=String.valueOf(System.currentTimeMillis());
        DatabaseReference datarf=FirebaseDatabase.getInstance().getReference("Posts").child(postId).child("Comments");
        HashMap<String ,Object> hashMap=new HashMap<>();
        hashMap.put("cId",timestamp);
        hashMap.put("comment",commentss);
        hashMap.put("ptime",timestamp);
        hashMap.put("uid",myuid);
        hashMap.put("uemail",myemail);
        hashMap.put("udp",mydp);
        hashMap.put("uname",myname);
        datarf.child(timestamp).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                progressDialog.dismiss();
                Toast.makeText(PostDetailsActivity.this,"Added",Toast.LENGTH_LONG).show();
                addToHisNotification(""+hisuid,""+myuid,"Commented On Your Post");
                comment.setText("");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(PostDetailsActivity.this,"Failed",Toast.LENGTH_LONG).show();
            }
        });
    }

    boolean count=false;
    private void loadUserInfo() {

        Query myref=FirebaseDatabase.getInstance().getReference("users");
        myref.orderByChild("uid").equalTo(myuid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    myname=dataSnapshot1.child("name").getValue().toString();
                    mydp=dataSnapshot1.child("imgUrl").getValue().toString();
                    try {
                        Glide.with(PostDetailsActivity.this).load(mydp).into(imagep);
                    }
                    catch (Exception e){

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
     String commentcount;
    private void loadPostInfo() {

        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Blogs");
        Query query=databaseReference.orderByChild("Time").equalTo(postId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {

                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    String ptitle=dataSnapshot1.child("Title").getValue().toString();
                    String descriptions=dataSnapshot1.child("Description").getValue().toString();
                    if(dataSnapshot1.child("udp").exists()) {
                        hisdp = dataSnapshot1.child("udp").getValue().toString();
                    }
                    hisuid = dataSnapshot1.child("Writtenby").getValue().toString();
                    DatabaseReference reference=FirebaseDatabase.getInstance().getReference("users");
                    reference.child(hisuid).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            hisname = snapshot.child("name").getValue().toString();
                            name.setText(hisname);
                            hisdp = snapshot.child("imgUrl").getValue().toString();
                            Glide.with(PostDetailsActivity.this).load(hisdp).placeholder(R.drawable.ic_face).into(picture);
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    if(dataSnapshot1.child("Time").exists()) {
                        ptime = dataSnapshot1.child("Time").getValue().toString();
                    }

                    if(dataSnapshot1.child("pcomments").exists()) {
                         commentcount = dataSnapshot1.child("pcomments").getValue().toString();
                    }

                    Calendar calendar=Calendar.getInstance(Locale.ENGLISH);
                    calendar.setTimeInMillis(Long.parseLong(ptime));
                    String timedate= DateFormat.format("dd/MM/yyyy hh:mm aa",calendar).toString();

                    title.setText(ptitle);
                    description.setText(descriptions);
                    time.setText(timedate);



                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

}