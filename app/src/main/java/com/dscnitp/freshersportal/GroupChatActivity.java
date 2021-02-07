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
import android.text.TextUtils;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.anstrontechnologies.corehelper.AnstronCoreHelper;
import com.bumptech.glide.Glide;
import com.dscnitp.freshersportal.Adapter.AdapterGroupChat;
import com.dscnitp.freshersportal.Model.ModelChat;
import com.dscnitp.freshersportal.Model.ModelGroupChats;
import com.dscnitp.freshersportal.Model.ModelUser;
import com.dscnitp.freshersportal.R;
import com.dscnitp.freshersportal.notifications.Data;
import com.dscnitp.freshersportal.notifications.Sender;
import com.dscnitp.freshersportal.notifications.Token;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import am.appwise.components.ni.NoInternetDialog;

public class GroupChatActivity extends AppCompatActivity {
    String grouid,mygrprole="";
    Toolbar toolbar;
    ImageView grpicon;
    ImageButton attachbtn,sendmsgbtn;
    TextView grpTitle;
    ProgressDialog dialog;
    AnstronCoreHelper helper;
    EditText message;
    ValueEventListener valueEventListener;
    DatabaseReference userforseen;
    FirebaseAuth firebaseAuth;
    RecyclerView recyclerView;
    String uid;
    ArrayList<ModelGroupChats> groupChatsArrayList;
    AdapterGroupChat adapterGroupChat;
    private static final int IMAGEPICK_GALLERY_REQUEST = 1;
    private static final int IMAGE_PICKCAMERA_REQUEST = 400;
    private static final int CAMERA_REQUEST = 100;
    private static final int STORAGE_REQUEST = 200;
    String cameraPermission[];
    String storagePermission[];
    Uri imageuri = null;
    private String checker="";
    private boolean notify=false;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);
        toolbar=findViewById(R.id.toolbargrp);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        grpicon=findViewById(R.id.groupicontv);
        attachbtn=findViewById(R.id.grpattach);
        sendmsgbtn=findViewById(R.id.sendgrpmsg);
        grpTitle=findViewById(R.id.grptitletv);
        message=findViewById(R.id.grpmsg);
        helper=new AnstronCoreHelper(this);
        requestQueue= Volley.newRequestQueue(getApplicationContext());
        recyclerView=findViewById(R.id.grpchatrecycle);
        dialog=new ProgressDialog(this);
        Intent intent=getIntent();
        grouid=intent.getStringExtra("groupid");
        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        uid=FirebaseAuth.getInstance().getUid();
        firebaseAuth=FirebaseAuth.getInstance();
        loadGroupInfo();
        setSupportActionBar(toolbar);
        loadMessage();
        seenMessgae();
        loadMygroupRole();
        sendmsgbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messag=message.getText().toString().trim();
                if(TextUtils.isEmpty(messag)){
                    Toast.makeText(GroupChatActivity.this,"Cant send Input Message",Toast.LENGTH_LONG).show();
                    return;
                }
                else {
                    sendMessage(messag);
                    message.setText("");
                }
            }
        });
        attachbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence options[]=new CharSequence[]{
                        "Images",
                        "Pdf Files",
                        "Cancel"
                };
                androidx.appcompat.app.AlertDialog.Builder builder=new androidx.appcompat.app.AlertDialog.Builder(GroupChatActivity.this);
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
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
    private void loadMygroupRole() {
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Groups");
        reference.child(grouid).child("Participants").orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds:dataSnapshot.getChildren()){
                            mygrprole=""+ds.child("role").getValue();
                            invalidateOptionsMenu();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
    private void showImagePicDialog() {
        String options[]={ "Gallery"};
        AlertDialog.Builder builder=new AlertDialog.Builder(GroupChatActivity.this);
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
        contentValues.put(MediaStore.Images.Media.TITLE,"Group_pic");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION,"Group_Description");
        imageuri=this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues);
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
        boolean result= ContextCompat.checkSelfPermission(GroupChatActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                ==(PackageManager.PERMISSION_GRANTED);
        return result;
    }
    private void requestStoragePermission(){
        requestPermissions(storagePermission,STORAGE_REQUEST);
    }
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode){
            case CAMERA_REQUEST:{
                if(grantResults.length>0){
                    boolean camera_accepted=grantResults[0]==PackageManager.PERMISSION_GRANTED;
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
    private void loadMessage(){
        dialog.setCanceledOnTouchOutside(false);
        groupChatsArrayList=new ArrayList<>();
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Groups");
        reference.child(grouid).child("Messages")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        groupChatsArrayList.clear();
                        for (DataSnapshot ds:dataSnapshot.getChildren()){
                            dialog.dismiss();
                            ModelGroupChats model=ds.getValue(ModelGroupChats.class);
                            groupChatsArrayList.add(model);
                        }
                        adapterGroupChat=new AdapterGroupChat(GroupChatActivity.this,groupChatsArrayList);
                        recyclerView.setAdapter(adapterGroupChat);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
    String grptitle;
    private void loadGroupInfo(){
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Groups");
        reference.orderByChild("grpId").equalTo(grouid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds:dataSnapshot.getChildren()){
                            grptitle=""+ds.child("grptitle").getValue();
                            String grpdesc=""+ds.child("grpdesc").getValue();
                            String groupicon=""+ds.child("grpicon").getValue();
                            String timstamp=""+ds.child("timestamp").getValue();
                            String craetedby=""+ds.child("createBy").getValue();
                            grpTitle.setText(grptitle);
                            try {
                                Glide.with(getApplicationContext()).load(groupicon).into(grpicon);
                            }
                            catch (Exception e){
                                Glide.with(getApplicationContext()).load(R.drawable.ic_group).into(grpicon);
                            }


                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
    private void sendNotification(final String uid, final String name, final String message) {
        DatabaseReference alltoken=FirebaseDatabase.getInstance().getReference("Tokens");
        Query query=alltoken.orderByKey().equalTo(uid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    Token token=dataSnapshot1.getValue(Token.class);
                    Data data=new Data(
                            ""+firebaseAuth.getUid(),
                            "Chaotification",
                            ""+name + ": " + message,
                            "New Message From " + grptitle,
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
    private void sendMessage(final String messsage){
        notify=true;
        String timestamp=String.valueOf(System.currentTimeMillis());
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("sender",firebaseAuth.getUid());
        hashMap.put("message",""+messsage);
        hashMap.put("timestamp",""+timestamp);
        hashMap.put("type","text");
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Groups");
        reference.child(grouid).child("Messages").child(timestamp).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        DatabaseReference databaseReference1=FirebaseDatabase.getInstance().getReference("Groups").child(grouid).child("Participants");
                        databaseReference1.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                    final String uids = dataSnapshot1.child("uid").getValue().toString();
                                    DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("users").child(uid);
                                    databaseReference1.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            ModelUser modelUsers = dataSnapshot.getValue(ModelUser.class);
                                            if (notify) {
                                                sendNotification(uids, modelUsers.getName(), messsage);
                                                return;
                                            }
                                            notify = false;
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        message.setText("");
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(GroupChatActivity.this,""+e.getMessage(),Toast.LENGTH_LONG).show();
            }
        });


        HashMap<String,Object> hashMap1=new HashMap<>();
        hashMap1.put("seeen",0);
        DatabaseReference reference1=FirebaseDatabase.getInstance().getReference("GroupsSeenMsg");
        reference1.child(grouid).child(timestamp).setValue(hashMap1)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(GroupChatActivity.this,""+e.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }
    private void seenMessgae() {
        userforseen=FirebaseDatabase.getInstance().getReference();
        valueEventListener=userforseen.child("GroupsSeenMsg").child(grouid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    ModelChat chat=dataSnapshot1.getValue(ModelChat.class);
                    HashMap<String ,Object> map=new HashMap();
                    map.put(uid,true);
                    dataSnapshot1.getRef().child("seeen").updateChildren(map);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == RESULT_OK){
            imageuri=data.getData();
            if (!checker.equals("images")) {
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
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("sender", firebaseAuth.getUid());
                            hashMap.put("message", "" + myurl);
                            hashMap.put("timestamp", "" + timestamp);
                            hashMap.put("type", "pdf");
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");
                            reference.child(grouid).child("Messages").child(timestamp).setValue(hashMap)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("Groups").child(grouid).child("Participants");
                                            databaseReference1.addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                                        final String uids = dataSnapshot1.child("uid").getValue().toString();

                                                        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("Users").child(uid);
                                                        databaseReference1.addValueEventListener(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                ModelUser modelUsers = dataSnapshot.getValue(ModelUser.class);
                                                                if (notify) {
                                                                    sendNotification(uids, modelUsers.getName(), " Sent A Photo");
                                                                    return;
                                                                }
                                                                notify = false;
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                                            }
                                                        });
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });
                                            message.setText("");
                                        }
                                    });


                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(GroupChatActivity.this, "MessageError...", Toast.LENGTH_SHORT).show();

                    }
                });
            }
            else {
                if(requestCode==IMAGEPICK_GALLERY_REQUEST){
                    imageuri=data.getData();
                    try {
                        sendImageMessage(imageuri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if(requestCode==IMAGE_PICKCAMERA_REQUEST) {
                    imageuri=data.getData();
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
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        final String timestamp=""+System.currentTimeMillis();
        File file=new File(SiliCompressor.with(GroupChatActivity.this).compress(FileUtils.getPath(this,imageuri),new File(this.getCacheDir(),"temp")));
        Uri uri=Uri.fromFile(file);
        StorageReference ref= FirebaseStorage.getInstance().getReference().child("GroupChatImages/").child(helper.getFileNameFromUri(uri));
        ref.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                dialog.dismiss();
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isSuccessful()) ;
                String downloadUri = uriTask.getResult().toString();

                if(uriTask.isSuccessful()){
                    String timestamp=String.valueOf(System.currentTimeMillis());
                    HashMap<String,Object> hashMap=new HashMap<>();
                    hashMap.put("sender",firebaseAuth.getUid());
                    hashMap.put("message",""+downloadUri);
                    hashMap.put("timestamp",""+timestamp);
                    hashMap.put("type","image");
                    DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Groups");
                    reference.child(grouid).child("Messages").child(timestamp).setValue(hashMap)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    DatabaseReference databaseReference1=FirebaseDatabase.getInstance().getReference("Groups").child(grouid).child("Participants");
                                    databaseReference1.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                                final String uids = dataSnapshot1.child("uid").getValue().toString();

                                                DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("users").child(uid);
                                                databaseReference1.addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        ModelUser modelUsers = dataSnapshot.getValue(ModelUser.class);
                                                        if (notify) {
                                                            sendNotification(uids, modelUsers.getName(), " Sent A Photo");
                                                            return;
                                                        }
                                                        notify = false;
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                    }
                                                });
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                    message.setText("");
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(GroupChatActivity.this,""+e.getMessage(),Toast.LENGTH_LONG).show();
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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.inside_group_item,menu);
        menu.findItem(R.id.grpinfo).setVisible(true);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId()==R.id.grpinfo){
            Intent intent=new Intent(this,GroupInfoActivity.class);
            intent.putExtra("groupId",grouid);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}


