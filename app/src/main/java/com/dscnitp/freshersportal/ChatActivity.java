package com.dscnitp.freshersportal;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.anstrontechnologies.corehelper.AnstronCoreHelper;
import com.bumptech.glide.Glide;

import com.dscnitp.freshersportal.notifications.Data;
import com.dscnitp.freshersportal.notifications.Sender;
import com.dscnitp.freshersportal.notifications.Token;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.iceteck.silicompressorr.FileUtils;
import com.iceteck.silicompressorr.SiliCompressor;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView recyclerView;
    ImageView profile,block;
    TextView name,userstatus;
    EditText msg;
    ProgressDialog dialog;
    String nameh;
    AnstronCoreHelper helper;
    ImageButton send,attach;
    FirebaseAuth firebaseAuth;
    String uid,myuid,image;
    ValueEventListener valueEventListener;
    DatabaseReference userforseen;
    List<ModelChat> chatList;
    AdapterChat adapterChat;

    private static final int IMAGEPICK_GALLERY_REQUEST = 300;
    private static final int IMAGE_PICKCAMERA_REQUEST = 400;
    private static final int CAMERA_REQUEST = 100;
    private static final int STORAGE_REQUEST = 200;
    String cameraPermission[];
    String storagePermission[];
    Uri imageuri = null;
    private String checker="";
    FirebaseDatabase firebaseDatabase;
    DatabaseReference users;
    private RequestQueue requestQueue;
    private boolean notify=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        firebaseAuth= FirebaseAuth.getInstance();
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ActionBar actionBar=getSupportActionBar();
        toolbar.setTitle("");
        profile=findViewById(R.id.profiletv);
        name=findViewById(R.id.nameptv);
        userstatus=findViewById(R.id.onlinetv);
        msg=findViewById(R.id.messaget);
        helper=new AnstronCoreHelper(this);
        send=findViewById(R.id.sendmsg);
        attach=findViewById(R.id.attachbtn);
        requestQueue= Volley.newRequestQueue(getApplicationContext());
        dialog=new ProgressDialog(this);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView=findViewById(R.id.chatrecycle);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        uid=getIntent().getStringExtra("uid");
        firebaseDatabase= FirebaseDatabase.getInstance();
        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        checkuserstatus();
        users=firebaseDatabase.getReference("users");

        Query userquery=users.orderByChild("uid").equalTo(uid);
        userquery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren()) {
                    nameh = "" + dataSnapshot1.child("name").getValue();
                    if (dataSnapshot1.child("imgUrl").exists()) {
                        image = dataSnapshot1.child("imgUrl").getValue().toString();
                    }
                    name.setText(nameh);
                    try {
                        Glide.with(ChatActivity.this).load(image).placeholder(R.drawable.ic_face).into(profile);
                    }
                    catch (Exception e){

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        attach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence options[]=new CharSequence[]{
                        "Images",
                        "Pdf Files",
                        "Cancel"
                };
                androidx.appcompat.app.AlertDialog.Builder builder=new androidx.appcompat.app.AlertDialog.Builder(ChatActivity.this);
                builder.setTitle("Select Files");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which==0){
                            checker="images";
                            if(!checkStoragePermission()){
                                requestStoragePermission();
                            }
                            else {
                                pickFromGallery();
                            }
                        }
                        if(which==1){
                            checker="pdf";
                            Intent galleryIntent = new Intent();
                            galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                            galleryIntent.setType("application/pdf");
                            startActivityForResult(galleryIntent, 1);
                        }
                    }
                });
                builder.show();
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notify=true;
                String message=msg.getText().toString().trim();
                if (TextUtils.isEmpty(message)){
                    Toast.makeText(ChatActivity.this,"Please Write Something Here",Toast.LENGTH_LONG).show();
                }
                else {

                    sendmessage(message);
                }
                msg.setText("");
            }

        });
        readMessages();
        seenMessgae();
    }
    private void seenMessgae() {
        userforseen= FirebaseDatabase.getInstance().getReference();
        valueEventListener=userforseen.child("users").child(myuid).child("Messages").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    ModelChat chat=dataSnapshot1.getValue(ModelChat.class);
                    if(chat.getReceiver().equals(myuid)&&chat.getSender().equals(uid)){
                        HashMap<String ,Object> hashMap=new HashMap<>();
                        hashMap.put("dilihat",true);
                        dataSnapshot1.getRef().updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        userforseen= FirebaseDatabase.getInstance().getReference();
        valueEventListener=userforseen.child("users").child(uid).child("Messages").child(myuid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    ModelChat chat=dataSnapshot1.getValue(ModelChat.class);
                    if(chat.getReceiver().equals(myuid)&&chat.getSender().equals(uid)){
                        HashMap<String ,Object> hashMap=new HashMap<>();
                        hashMap.put("dilihat",true);
                        dataSnapshot1.getRef().updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        msg.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if(s.toString().trim().length()==0){
                    checkTypingStatus("noOne");
                }
                else {
                    checkTypingStatus(uid);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().trim().length()==0){
                    checkTypingStatus("noOne");
                }
            }
        });
    }
    private void checkTypingStatus(String typing){

        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("users").child(myuid);
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("typingTo", typing);
        dbref.updateChildren(hashMap);
    }
    @Override
    protected void onPause() {
        super.onPause();
        String timestamp= String.valueOf(System.currentTimeMillis());
        userforseen.removeEventListener(valueEventListener);
    }
    @Override
    protected void onResume() {
        super.onResume();
    }
    @Override
    protected void onStart() {
        checkuserstatus();

        super.onStart();
    }
    private void readMessages() {
        dialog.setMessage("Showing Messages..");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        chatList=new ArrayList<>();
        DatabaseReference dbref= FirebaseDatabase.getInstance().getReference().child("users").child(myuid).child("Messages").child(uid);
        dbref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    chatList.clear();
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        ModelChat modelChat = dataSnapshot1.getValue(ModelChat.class);
                        if (modelChat.getSender().equals(myuid) &&
                                modelChat.getReceiver().equals(uid) ||
                                modelChat.getReceiver().equals(myuid)
                                        && modelChat.getSender().equals(uid)) {
                            chatList.add(modelChat);
                            dialog.dismiss();
                        }
                        adapterChat = new AdapterChat(ChatActivity.this, chatList, image);
                        Collections.sort(chatList, new Comparator<ModelChat>() {
                            @Override
                            public int compare(ModelChat o1, ModelChat o2) {
                                return o1.getTimestamp().compareTo(o2.getTimestamp());
                            }
                        });
                        adapterChat.notifyDataSetChanged();
                        recyclerView.setAdapter(adapterChat);
                        recyclerView.scrollToPosition(chatList.size()-1);

                    }
                }
                dialog.dismiss();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void showImagePicDialog() {
        String options[]={ "Camera","Gallery"};
        AlertDialog.Builder builder=new AlertDialog.Builder(ChatActivity.this);
        builder.setTitle("Pick Image From");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(which==0){
                    if(!checkCameraPermission()){
                        requestCameraPermission();
                    }
                    else {
                        pickFromCamera();
                    }
                }else if(which==1){
                    if(!checkStoragePermission()){
                        requestStoragePermission();
                    }
                    else {
                        pickFromGallery();
                    }

                }
            }
        });
        builder.create().show();
    }
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode){
            case CAMERA_REQUEST:{
                if(grantResults.length>0){
                    boolean camera_accepted=grantResults[0]== PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageaccepted=grantResults[1]==PackageManager.PERMISSION_GRANTED;
                    if(camera_accepted&&writeStorageaccepted){
                        pickFromCamera();
                    }
                    else {
                        Toast.makeText(this,"Please Enable Camera and Storage Permissions",Toast.LENGTH_LONG).show();
                    }
                }
            }
            break;
            case STORAGE_REQUEST:{
                if(grantResults.length>0){
                    boolean writeStorageaccepted=grantResults[0]==PackageManager.PERMISSION_GRANTED;
                    if(writeStorageaccepted){
                        pickFromGallery();
                    }
                    else {
                        Toast.makeText(this,"Please Enable Storage Permissions",Toast.LENGTH_LONG).show();
                    }
                }
            }
            break;
        }

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == RESULT_OK) {
            if (!checker.equals("images")) {
                imageuri=data.getData();
                dialog.setMessage("Sending pdf");
                dialog.show();
                final String timestamp=""+System.currentTimeMillis();
                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Document Files");


                final String messagePushID = timestamp;
                final StorageReference filepath = storageReference.child(messagePushID + "." + checker);
                filepath.putFile(imageuri)
                        .continueWithTask(new Continuation() {
                            @Override
                            public Object then(@NonNull Task task) throws Exception {
                                if (!task.isSuccessful()) {
                                    throw task.getException();
                                }
                                return filepath.getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri uri = task.getResult();
                            String myurl;
                            myurl = uri.toString();
                            HashMap<String,Object> messageTextBody = new HashMap();
                            messageTextBody.put("message", myurl);
                            messageTextBody.put("type", checker);
                            messageTextBody.put("sender", myuid);
                            messageTextBody.put("receiver", uid);
                            messageTextBody.put("timestamp", timestamp);
                            messageTextBody.put("dilihat",false);
                            FirebaseDatabase.getInstance().getReference().child("users").child(myuid).child("Messages").child(uid).child(timestamp).setValue(messageTextBody);
                            FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("Messages").child(myuid).child(timestamp).setValue(messageTextBody);
                            DatabaseReference reference= FirebaseDatabase.getInstance().getReference("users").child(myuid);
                            reference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    ModelUser users=dataSnapshot.getValue(ModelUser.class);
                                    if(notify){
                                        sendNotification(uid,users.getName(),"Sent You a pdf");
                                        dialog.dismiss();
                                    }
                                    notify=false;
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                            final DatabaseReference ref1= FirebaseDatabase.getInstance().getReference("ChatList").child(uid).child(myuid);
                            ref1.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                                    Date dNow = new Date();
                                    SimpleDateFormat ft = new SimpleDateFormat("yyMMddhhmmssMs");
                                    String datetime = ft.format(dNow);

                                    if(!dataSnapshot.exists()){
                                        HashMap<String ,String > hashMap1=new HashMap<>();
                                        hashMap1.put("id",myuid);
                                        hashMap1.put("order",datetime);
                                        ref1.setValue(hashMap1);
                                    }else {
                                        HashMap<String ,Object > hashMap1=new HashMap<>();
                                        hashMap1.put("id",myuid);
                                        hashMap1.put("order",datetime);
                                        ref1.updateChildren(hashMap1);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                            final DatabaseReference ref2= FirebaseDatabase.getInstance().getReference("ChatList").child(myuid).child(uid);
                            ref2.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    Date dNow = new Date();
                                    SimpleDateFormat ft = new SimpleDateFormat("yyMMddhhmmssMs");
                                    String datetime = ft.format(dNow);
                                    if(!dataSnapshot.exists()){
                                        HashMap<String ,String > hashMap1=new HashMap<>();
                                        hashMap1.put("id",uid);
                                        hashMap1.put("order",datetime);
                                        ref2.setValue(hashMap1);
                                    }
                                    else {
                                        HashMap<String ,Object > hashMap1=new HashMap<>();
                                        hashMap1.put("id",uid);
                                        hashMap1.put("order",datetime);
                                        ref2.updateChildren(hashMap1);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                            final DatabaseReference referencee= FirebaseDatabase.getInstance().getReference("Chat").child(myuid).child(uid);
                            referencee.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    Date dNow = new Date();
                                    SimpleDateFormat ft = new SimpleDateFormat("yyMMddhhmmssMs");
                                    String datetime = ft.format(dNow);
                                    if(!dataSnapshot.exists()){
                                        HashMap<String ,String > hashMap1=new HashMap<>();
                                        hashMap1.put("uid",uid);
                                        hashMap1.put("order",datetime);
                                        hashMap1.put("name",nameh);
                                        hashMap1.put("image",image);
                                        referencee.setValue(hashMap1);
                                    }
                                    else {
                                        HashMap<String ,Object > hashMap1=new HashMap<>();
                                        hashMap1.put("uid",uid);
                                        hashMap1.put("order",datetime);
                                        hashMap1.put("name",nameh);
                                        hashMap1.put("image",image);
                                        referencee.updateChildren(hashMap1);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                            final DatabaseReference referencees= FirebaseDatabase.getInstance().getReference("Chat").child(uid).child(myuid);
                            referencees.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    Date dNow = new Date();
                                    SimpleDateFormat ft = new SimpleDateFormat("yyMMddhhmmssMs");
                                    String datetime = ft.format(dNow);
                                    if(!dataSnapshot.exists()){
                                        HashMap<String ,String > hashMap1=new HashMap<>();
                                        hashMap1.put("uid",myuid);
                                        hashMap1.put("order",datetime);
                                        hashMap1.put("name",myname);
                                        hashMap1.put("image",myimage);
                                        referencees.setValue(hashMap1);
                                    }
                                    else {
                                        HashMap<String ,Object > hashMap1=new HashMap<>();
                                        hashMap1.put("uid",myuid);
                                        hashMap1.put("order",datetime);
                                        hashMap1.put("name",myname);
                                        hashMap1.put("image",myimage);
                                        referencees.updateChildren(hashMap1);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ChatActivity.this, "MessageError...", Toast.LENGTH_SHORT).show();

                    }
                });
            } else {
                if (requestCode == IMAGEPICK_GALLERY_REQUEST) {
                    imageuri = data.getData();
                    try {
                        sendImageMessage(imageuri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (requestCode == IMAGE_PICKCAMERA_REQUEST) {
                    imageuri = data.getData();
                    try {
                        sendImageMessage(imageuri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void sendImageMessage(Uri imageuri) throws IOException {
        notify=true;
        final ProgressDialog dialog=new ProgressDialog(this);
        dialog.setMessage("Sending Image");
        dialog.show();

        final String timestamp=""+System.currentTimeMillis();
        File file=new File(SiliCompressor.with(ChatActivity.this).compress(FileUtils.getPath(this,imageuri),new File(this.getCacheDir(),"temp")));
        Uri uri=Uri.fromFile(file);
        StorageReference ref= FirebaseStorage.getInstance().getReference().child("ChatImages/").child(helper.getFileNameFromUri(uri));
        ref.putFile(uri) .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                dialog.dismiss();
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isSuccessful()) ;
                String downloadUri = uriTask.getResult().toString();

                if(uriTask.isSuccessful()){
                    DatabaseReference re= FirebaseDatabase.getInstance().getReference();
                    final HashMap<String,Object> hashMap=new HashMap<>();
                    hashMap.put("sender",myuid);
                    hashMap.put("receiver",uid);
                    hashMap.put("message",downloadUri);
                    hashMap.put("timestamp",timestamp);
                    hashMap.put("dilihat",false);
                    hashMap.put("type","images");
                    re.child("users").child(myuid).child("Messages").child(uid).child(timestamp).setValue(hashMap);
                    re.child("users").child(uid).child("Messages").child(myuid).child(timestamp).setValue(hashMap);
                    DatabaseReference reference= FirebaseDatabase.getInstance().getReference("users").child(myuid);
                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            ModelUser users=dataSnapshot.getValue(ModelUser.class);
                            if(notify){
                                sendNotification(uid,users.getName(),"Sent You a Photo");
                            }
                            notify=false;
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    final DatabaseReference ref1= FirebaseDatabase.getInstance().getReference("ChatList").child(uid).child(myuid);
                    ref1.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                            Date dNow = new Date();
                            SimpleDateFormat ft = new SimpleDateFormat("yyMMddhhmmssMs");
                            String datetime = ft.format(dNow);

                            if(!dataSnapshot.exists()){
                                HashMap<String ,String > hashMap1=new HashMap<>();
                                hashMap1.put("id",myuid);
                                hashMap1.put("order",datetime);
                                ref1.setValue(hashMap1);
                            }else {
                                HashMap<String ,Object > hashMap1=new HashMap<>();
                                hashMap1.put("id",myuid);
                                hashMap1.put("order",datetime);
                                ref1.updateChildren(hashMap1);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    final DatabaseReference ref2= FirebaseDatabase.getInstance().getReference("ChatList").child(myuid).child(uid);
                    ref2.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            Date dNow = new Date();
                            SimpleDateFormat ft = new SimpleDateFormat("yyMMddhhmmssMs");
                            String datetime = ft.format(dNow);
                            if(!dataSnapshot.exists()){
                                HashMap<String ,String > hashMap1=new HashMap<>();
                                hashMap1.put("id",uid);
                                hashMap1.put("order",datetime);
                                ref2.setValue(hashMap1);
                            }
                            else {
                                HashMap<String ,Object > hashMap1=new HashMap<>();
                                hashMap1.put("id",uid);
                                hashMap1.put("order",datetime);
                                ref2.updateChildren(hashMap1);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    final DatabaseReference referencee= FirebaseDatabase.getInstance().getReference("Chat").child(myuid).child(uid);
                    referencee.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            Date dNow = new Date();
                            SimpleDateFormat ft = new SimpleDateFormat("yyMMddhhmmssMs");
                            String datetime = ft.format(dNow);
                            if(!dataSnapshot.exists()){
                                HashMap<String ,String > hashMap1=new HashMap<>();
                                hashMap1.put("uid",uid);
                                hashMap1.put("order",datetime);
                                hashMap1.put("name",nameh);
                                hashMap1.put("image",image);
                                referencee.setValue(hashMap1);
                            }
                            else {
                                HashMap<String ,Object > hashMap1=new HashMap<>();
                                hashMap1.put("uid",uid);
                                hashMap1.put("order",datetime);
                                hashMap1.put("name",nameh);
                                hashMap1.put("image",image);
                                referencee.updateChildren(hashMap1);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    final DatabaseReference referencees= FirebaseDatabase.getInstance().getReference("Chat").child(uid).child(myuid);
                    referencees.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            Date dNow = new Date();
                            SimpleDateFormat ft = new SimpleDateFormat("yyMMddhhmmssMs");
                            String datetime = ft.format(dNow);
                            if(!dataSnapshot.exists()){
                                HashMap<String ,String > hashMap1=new HashMap<>();
                                hashMap1.put("uid",myuid);
                                hashMap1.put("order",datetime);
                                hashMap1.put("name",myname);
                                hashMap1.put("image",myimage);
                                referencees.setValue(hashMap1);
                            }
                            else {
                                HashMap<String ,Object > hashMap1=new HashMap<>();
                                hashMap1.put("uid",myuid);
                                hashMap1.put("order",datetime);
                                hashMap1.put("name",myname);
                                hashMap1.put("image",myimage);
                                referencees.updateChildren(hashMap1);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }

    private Boolean checkCameraPermission(){
        boolean result= ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA)
                ==(PackageManager.PERMISSION_GRANTED);
        boolean result1= ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                ==(PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }
    private void requestCameraPermission(){
        requestPermissions(cameraPermission,CAMERA_REQUEST);
    }
    private void pickFromCamera(){
        ContentValues contentValues=new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE,"Temp_image");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION,"Temp Camera");
        imageuri=getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues);
        Intent camerIntent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        camerIntent.putExtra(MediaStore.EXTRA_OUTPUT,imageuri);
        startActivityForResult(camerIntent,IMAGE_PICKCAMERA_REQUEST);
    }
    private void pickFromGallery(){
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, IMAGEPICK_GALLERY_REQUEST);
    }
    private Boolean checkStoragePermission(){
        boolean result= ContextCompat.checkSelfPermission(ChatActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                ==(PackageManager.PERMISSION_GRANTED);
        return result;
    }
    private void requestStoragePermission(){
        requestPermissions(storagePermission,STORAGE_REQUEST);
    }

    private void sendmessage(final String message) {

        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference();
        final String timestamp=String.valueOf(System.currentTimeMillis());
        final Calendar c=Calendar.getInstance();
        final HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("sender",myuid);
        hashMap.put("receiver",uid);
        hashMap.put("message",message);
        hashMap.put("timestamp",timestamp);
        hashMap.put("dilihat",false);
        hashMap.put("type","text");
        databaseReference.child("users").child(myuid).child("Messages").child(uid).child(timestamp).setValue(hashMap);
        databaseReference.child("users").child(uid).child("Messages").child(myuid).child(timestamp).setValue(hashMap);

        DatabaseReference databaseReference1= FirebaseDatabase.getInstance().getReference("users").child(myuid);
        databaseReference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ModelUser modelusers=dataSnapshot.getValue(ModelUser.class);
                if(notify){
                    sendNotification(uid,modelusers.getName(),message);
                }
                notify=false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final DatabaseReference ref1= FirebaseDatabase.getInstance().getReference("ChatList").child(uid).child(myuid);
        ref1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                Date dNow = new Date();
                SimpleDateFormat ft = new SimpleDateFormat("yyMMddhhmmssMs");
                String datetime = ft.format(dNow);

                if(!dataSnapshot.exists()){
                    HashMap<String ,String > hashMap1=new HashMap<>();
                    hashMap1.put("id",myuid);
                    hashMap1.put("order",datetime);
                    ref1.setValue(hashMap1);
                }else {
                    HashMap<String ,Object > hashMap1=new HashMap<>();
                    hashMap1.put("id",myuid);
                    hashMap1.put("order",datetime);
                    ref1.updateChildren(hashMap1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        final DatabaseReference ref2= FirebaseDatabase.getInstance().getReference("ChatList").child(myuid).child(uid);
        ref2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Date dNow = new Date();
                SimpleDateFormat ft = new SimpleDateFormat("yyMMddhhmmssMs");
                String datetime = ft.format(dNow);
                if(!dataSnapshot.exists()){
                    HashMap<String ,String > hashMap1=new HashMap<>();
                    hashMap1.put("id",uid);
                    hashMap1.put("order",datetime);
                    ref2.setValue(hashMap1);
                }
                else {
                    HashMap<String ,Object > hashMap1=new HashMap<>();
                    hashMap1.put("id",uid);
                    hashMap1.put("order",datetime);
                    ref2.updateChildren(hashMap1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        final DatabaseReference referencee= FirebaseDatabase.getInstance().getReference("Chat").child(myuid).child(uid);
        referencee.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Date dNow = new Date();
                SimpleDateFormat ft = new SimpleDateFormat("yyMMddhhmmssMs");
                String datetime = ft.format(dNow);
                if(!dataSnapshot.exists()){
                    HashMap<String ,String > hashMap1=new HashMap<>();
                    hashMap1.put("uid",uid);
                    hashMap1.put("order",datetime);
                    hashMap1.put("name",nameh);
                    hashMap1.put("image",image);
                    referencee.setValue(hashMap1);
                }
                else {
                    HashMap<String ,Object > hashMap1=new HashMap<>();
                    hashMap1.put("uid",uid);
                    hashMap1.put("order",datetime);
                    hashMap1.put("name",nameh);
                    hashMap1.put("image",image);
                    referencee.updateChildren(hashMap1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        final DatabaseReference referencees= FirebaseDatabase.getInstance().getReference("Chat").child(uid).child(myuid);
        referencees.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Date dNow = new Date();
                SimpleDateFormat ft = new SimpleDateFormat("yyMMddhhmmssMs");
                String datetime = ft.format(dNow);
                if(!dataSnapshot.exists()){
                    HashMap<String ,String > hashMap1=new HashMap<>();
                    hashMap1.put("uid",myuid);
                    hashMap1.put("order",datetime);
                    hashMap1.put("name",myname);
                    hashMap1.put("image",myimage);
                    referencees.setValue(hashMap1);
                }
                else {
                    HashMap<String ,Object > hashMap1=new HashMap<>();
                    hashMap1.put("uid",myuid);
                    hashMap1.put("order",datetime);
                    hashMap1.put("name",myname);
                    hashMap1.put("image",myimage);
                    referencees.updateChildren(hashMap1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendNotification(final String uid, final String name, final String message) {
        DatabaseReference alltoken= FirebaseDatabase.getInstance().getReference("Tokens");
        Query query=alltoken.orderByKey().equalTo(uid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    Token token=dataSnapshot1.getValue(Token.class);
                    Data data=new Data(
                            ""+myuid,
                            "ChatNotification",
                            ""+name + ": " + message,
                            "New Message",
                            ""+uid
                            ,R.drawable.logo);
                    Sender sender=new Sender(data,token.getToken());
                    try {
                        JSONObject jsonObject = new JSONObject(new Gson().toJson(sender));
                        JsonObjectRequest objectRequest =new JsonObjectRequest("https://fcm.googleapis.com/fcm/send", jsonObject, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.d("JSON_RESPONSE","onResponse: " +response.toString());
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d("JSON_RESPONSE","onResponse: " +error.toString());
                            }
                        }) {
                            @Override
                            public Map<String, String> getHeaders() throws AuthFailureError {
                                Map<String ,String > map=new HashMap<>();
                                map.put("Content-Type","application/json");
                                map.put("Authorization","key=AAAAkImAmoA:APA91bHkOdbKVcvuxzPswBwG8CHcztgDls2JZB9hEafFoEb59ZfG-imQHTsen0vBq9fI0spWL5FmD54NQ5A_58mreoGXRofTL1SkBPHwCqCdq7KxGRalxTxLdchNO2yYb2IDeW6SJVhq");
                                return map;
                            }
                        };
                        requestQueue.add(objectRequest);
                    }catch (JSONException e){

                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.search,menu);
//        menu.findItem(R.id.action_search).setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        if(item.getItemId()==R.id.action_search){
//
//        }
        return super.onOptionsItemSelected(item);
    }

    String myname,myimage;
    private void checkuserstatus(){
        FirebaseUser user=firebaseAuth.getCurrentUser();
        if(user!=null){
            myuid=user.getUid();
            DatabaseReference reference= FirebaseDatabase.getInstance().getReference("users");
            reference.child(myuid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    myname = dataSnapshot.child("name").getValue().toString();
                    if (dataSnapshot.child("imgUrl").exists()){
                        myimage=dataSnapshot.child("imgUrl").getValue().toString();
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
        else {
            startActivity(new Intent(ChatActivity.this, MainActivity.class));
            finish();
        }
    }


}
