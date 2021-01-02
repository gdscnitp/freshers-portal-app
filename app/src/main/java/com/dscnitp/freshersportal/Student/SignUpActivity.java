package com.dscnitp.freshersportal.Student;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.dscnitp.freshersportal.Alumni.AlumniMainActivity;
import com.dscnitp.freshersportal.Common.Node;
import com.dscnitp.freshersportal.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {

    private TextInputEditText t1,t2,names,rollNo;
    private ProgressBar bar;
    private FirebaseAuth mAuth;
    private ImageView ivProfile;
    private Uri localFileUri,ServerFileUri;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    Spinner BranchList;

    private FirebaseStorage mStorage;

    String email,password,name,RollNo,Branch;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        t1 = findViewById(R.id.email);
        t2 = findViewById(R.id.pwd);
        names = findViewById(R.id.name);
        rollNo=findViewById(R.id.RollNo);
        bar = (ProgressBar) findViewById(R.id.progressBar);
        ivProfile = findViewById(R.id.ivProfile);
        BranchList=findViewById(R.id.branchList);

        ArrayAdapter<String> myBranchAdapter=new ArrayAdapter<String>(SignUpActivity.this,android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.Branch));
        myBranchAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        BranchList.setAdapter(myBranchAdapter);

    }

    String uid;

    public void signuphere(View view)
    {
        email = t1.getText().toString();
        password = t2.getText().toString();
        name = names.getText().toString();
        RollNo=rollNo.getText().toString();
        Branch=BranchList.getSelectedItem().toString();

        if(RollNo.equals(""))
        {
            rollNo.setError("Enter Roll No");
        }

        if (!email.equals("")  &&  !password.equals("")  &&  !name.equals("")  &&  !RollNo.equals(""))
        {
            mAuth = FirebaseAuth.getInstance();
            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        bar.setVisibility(View.INVISIBLE);
                        firebaseUser=mAuth.getCurrentUser();
                        if(localFileUri!=null)
                            updateNameAndPhoto();
                        else
                            updateNameOnly();
//                        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
//                        HashMap<String, Object> hashMap = new HashMap<>();
//                        hashMap.put("email", email);
//                        hashMap.put("name", name);
//                        hashMap.put("USER_TYPE", "student");
//                        hashMap.put("id", "");
//                        hashMap.put("device_token", "");
//                        hashMap.put("uid", uid);
//                        hashMap.put("imgUrl", "");
//                        hashMap.put("rollNo",RollNo);
//                        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("users");
//                        db.child(uid).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>()
//                        {
//                            @Override
//                            public void onSuccess(Void aVoid)
//                            {
//                                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
//                                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                startActivity(i);
//                                finish();
//                                t1.getEditText().setText("");
//                                t2.getEditText().setText("");
//                                Toast.makeText(SignUpActivity.this, "Registered Successfully", Toast.LENGTH_LONG).show();
//                            }
//                        });
                    }
                    else {
                        bar.setVisibility(View.INVISIBLE);
                        t1.setText("");
                        t2.setText("");
                        Toast.makeText(SignUpActivity.this, "Process Error", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    public void gotosignin(View view)
    {
     startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
     finish();
    }

    public void PickImage(View V)
    {
        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED)
        {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 101);
        }
        else
        {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},102);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==101)
        {
            if(resultCode==RESULT_OK)
            {
                localFileUri=data.getData();
                ivProfile.setImageURI(localFileUri);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==102) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 101);
            } else {
                Toast.makeText(this, "Access Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void updateNameOnly()
    {
        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                .setDisplayName(name.trim())
                .build();

        firebaseUser.updateProfile(request).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if(task.isSuccessful())
                {
                    String UserID=firebaseUser.getUid();
                    databaseReference= FirebaseDatabase.getInstance().getReference().child(Node.Users);
                    HashMap<String,String> hashMap=new HashMap<>();
                    hashMap.put(Node.Name,name);
                    hashMap.put(Node.Email,email);
                    hashMap.put(Node.Photo,"");
                    hashMap.put(Node.Branch,Branch);
                    hashMap.put(Node.ROLL_NO,RollNo);
                    hashMap.put("uid",UserID);
                    hashMap.put(Node.USER_TYPE,"student");

                    databaseReference.child(UserID).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                Toast.makeText(SignUpActivity.this, "User Created Successfully", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(SignUpActivity.this,LoginActivity.class));
                                finish();
                            }
                            else
                            {
                                Toast.makeText(SignUpActivity.this,"User Not Created",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else
                {
                    Toast.makeText(SignUpActivity.this,"Failed to Update : %1$s"+task.getException(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void updateNameAndPhoto()
    {
        String strFile = firebaseUser.getUid()+".jpg";
        final StorageReference FileRef = mStorage.getInstance().getReference().child("Images/"+strFile);

        FileRef.putFile(localFileUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful())
                {
                    FileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            ServerFileUri=uri;

                            UserProfileChangeRequest request = new UserProfileChangeRequest.Builder().
                                    setDisplayName(name.trim())
                                    .setPhotoUri(ServerFileUri)
                                    .build();

                            firebaseUser.updateProfile(request).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful())
                                    {
                                        String UserID=firebaseUser.getUid();
                                        databaseReference= FirebaseDatabase.getInstance().getReference().child(Node.Users);
                                        HashMap<String,String> hashMap=new HashMap<>();
                                        hashMap.put(Node.Name,name);
                                        hashMap.put(Node.Email,email);
                                        hashMap.put(Node.Branch,Branch);
                                        hashMap.put("uid",UserID);
                                        hashMap.put(Node.ROLL_NO,RollNo);
                                        hashMap.put(Node.Photo,ServerFileUri.getPath());
                                        hashMap.put(Node.USER_TYPE, "student");

                                        databaseReference.child(UserID).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()) {
                                                    Toast.makeText(SignUpActivity.this, "User Created Successfully", Toast.LENGTH_SHORT).show();
                                                    startActivity(new Intent(SignUpActivity.this,LoginActivity.class));
                                                    finish();
                                                }
                                                else
                                                {
                                                    Toast.makeText(SignUpActivity.this,"User Not Created",Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }
                                    else
                                    {
                                        Toast.makeText(SignUpActivity.this,"Failed to Update : %1$s"+task.getException(),Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });

                        }
                    });
                }
            }
        });
    }

}