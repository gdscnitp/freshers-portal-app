package com.dscnitp.freshersportal.Alumni;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;

import android.content.DialogInterface;
import android.content.Intent;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.dscnitp.freshersportal.Model.ModelUser;

import com.dscnitp.freshersportal.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

import am.appwise.components.ni.NoInternetDialog;

public class AddPostActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    Toolbar actionBar;
    EditText title, des,type;
    ProgressDialog pd;
    String edititle, editdes, editimage;
    String name, email, uid, dp;
    DatabaseReference databaseReference;
    Button upload;
    String[] titles={"Coding","UPSC","Gate"};
    String[] types={"Issue","Inspirational","Opportunity"};
    String selected,getSelected;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);
        actionBar=findViewById(R.id.toolbar);
        setSupportActionBar(actionBar);
        actionBar.setTitle("Add Blogs Here");
        firebaseAuth = FirebaseAuth.getInstance();
        title = findViewById(R.id.ptitle);
        type = findViewById(R.id.type);
        uid=FirebaseAuth.getInstance().getUid();



        title.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                AlertDialog.Builder builder=new AlertDialog.Builder(AddPostActivity.this);
                builder.setTitle("Select Title")
                        .setItems(titles, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                selected=titles[which];
                                title.setText(selected);
                            }
                        }).show();
                return true;
            }
        });

        type.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                AlertDialog.Builder builder=new AlertDialog.Builder(AddPostActivity.this);
                builder.setTitle("Select Type of Blog")
                        .setItems(types, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                selected=types[which];
                                type.setText(selected);
                            }
                        }).show();
                return true;
            }
        });

        des = findViewById(R.id.pdes);
        upload = findViewById(R.id.pupload);
        pd = new ProgressDialog(this);
        pd.setCanceledOnTouchOutside(false);
        Intent intent = getIntent();
        actionBar.setTitle("Add New Post");
        upload.setText("Upload");
        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        Query query = databaseReference.child(uid).orderByChild("email").equalTo(email);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    ModelUser modelUsers = dataSnapshot.getValue(ModelUser.class);
                    name = dataSnapshot.child("name").getValue().toString();
                    email = "" + dataSnapshot.child("email").getValue();
                    dp = "" + dataSnapshot.child("imgUrl").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String titl = ""+title.getText().toString().trim();
                String description = ""+des.getText().toString().trim();
                if (TextUtils.isEmpty(titl)) {
                    Toast.makeText(AddPostActivity.this, "Title can't be left empty", Toast.LENGTH_LONG).show();
                    return;
                }
                if (TextUtils.isEmpty(description)) {
                    Toast.makeText(AddPostActivity.this, "Description can't be left empty", Toast.LENGTH_LONG).show();
                    return;
                }
                    uploadData(titl, description);


            }
        });
        actionBar.setSubtitle(email);

    }


    private void prepareNotification(String pId,String title,String description,String notificationtype,String notificationtopic) throws JSONException {

        String NOTIFICATION_TOPIC="/topics/" + notificationtopic;
        String NOTIFICATION_TILE=title;
        String NOTIFICATION_ESSAGE=description;
        String NOTIFICATION_TYPE=notificationtype;

        JSONObject object=new JSONObject();
        JSONObject notificationObject=new JSONObject();
        notificationObject.put("notificationType",NOTIFICATION_TYPE);
        notificationObject.put("sender",uid);
        notificationObject.put("pId",pId);
        notificationObject.put("pTitle",NOTIFICATION_TILE);
        notificationObject.put("pDescription",NOTIFICATION_ESSAGE);
        object.put("to",NOTIFICATION_TOPIC);
        object.put("data",notificationObject);

        sendNotification(object);
    }

    private void sendNotification(JSONObject object) {
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest("https://fcm.googleapis.com/fcm/send", object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("FCM_RESPONSE","onResponse:"+response.toString());

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(AddPostActivity.this,""+error.getMessage(),Toast.LENGTH_LONG).show();
            }
        })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String ,String > map=new HashMap<>();
                map.put("Content-Type","application/json");
                map.put("Authorization","key=AAAAux-y-Cc:APA91bFXuXd6jvnnZ2ZC3kGL4tEfkug7ruuxI1HrDimSboYCAL0ZrdxZnCD0y949pW6Xf15n28iDe3H7GesRtmqvOlh60XNLGVkgaCYcYjYeC3Gmg2UXJtzo5GK3ws9FTRh6FQqVp5r5");

                return map;
            }
        };
        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }

    private void uploadData(final String titl, final String description) {

        pd.setMessage("Publishing Post");
        pd.show();
        final String timestamp=String.valueOf(System.currentTimeMillis());
            pd.show();
            HashMap<Object,String > hashMap=new HashMap<>();
            hashMap.put("Writtenby",uid);
            hashMap.put("Type",type.getText().toString());
            hashMap.put("Title",titl);
            hashMap.put("Description",description);
            hashMap.put("Department",description);
            hashMap.put("Time",timestamp);
            DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("Blogs");
            databaseReference.child(timestamp.substring(5)).setValue(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            pd.dismiss();
                            Toast.makeText(AddPostActivity.this,"Published",Toast.LENGTH_LONG).show();
                            title.setText("");
                            des.setText("");
                            try {
                                prepareNotification(
                                        ""+timestamp
                                        ,""+name+" added new post ",
                                        ""+titl+ " " ,
                                        "POST_NOTIFICATION","POST");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            startActivity(new Intent(AddPostActivity.this, AlumniMainActivity.class));
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    pd.dismiss();
                    Toast.makeText(AddPostActivity.this,"Failed",Toast.LENGTH_LONG).show();
                }
            });

        }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }


}
