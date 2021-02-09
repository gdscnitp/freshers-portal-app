package com.dscnitp.freshersportal.Student;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
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

import am.appwise.components.ni.NoInternetDialog;

public class SignUpActivity extends AppCompatActivity {
    TextInputEditText t1,t2,names,rollNo;
    ProgressBar bar;
    private FirebaseAuth mAuth;
    private ImageView ivProfile;
    private Uri localFileUri,ServerFileUri;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    Spinner BranchList;
    NoInternetDialog noInternetDialog;

    private FirebaseStorage mStorage;

    String email,password,name,RollNo,Branch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        noInternetDialog = new NoInternetDialog.Builder(this).setBgGradientStart(Color.parseColor("#4488A7"))
                .setBgGradientCenter(Color.parseColor("#4488A7")).setButtonColor(Color.parseColor("#2196F3"))
                .setBgGradientEnd(Color.parseColor("#4488A7")).build();

        t1 = findViewById(R.id.email);
        t2 = findViewById(R.id.pwd);
        names =  findViewById(R.id.name);
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

    public void signuphere(View view) {

            email = t1.getText().toString();
            password = t2.getText().toString();
            name = names.getText().toString();
            RollNo = rollNo.getText().toString();
            Branch = BranchList.getSelectedItem().toString();

            if(name.equals("")){
                names.setError("Enter Name");
            }
            else if(CheckAlphabet(name)==0) {
                names.setError("Enter Alphabet Only");
            }
            else if(email.equals("")){
            t1.setError("Enter Email");
            }
            else if(CheckNITPEmail(email)==0){
                t1.setError("Enter NITP Email Only");
            }
            else if(RollNo.length()!=7){
                rollNo.setError("Enter valid RollNo");
            }
            else if(password.equals("")){
            t2.setError("Enter Password");
            }
            else
                {
                mAuth = FirebaseAuth.getInstance();
                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            bar.setVisibility(View.INVISIBLE);
                            firebaseUser = mAuth.getCurrentUser();
                            if (localFileUri != null)
                                updateNameAndPhoto();
                            else
                                updateNameOnly();
                        } else {
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
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED)
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

    public int CheckAlphabet(String S)
    {
        int i;
        for(i=0;i<S.length();i++)
        {
            char A=S.charAt(i);
            if((A>='A'  &&  A<='Z')  || (A>='a'  &&  A<='z'))
            {
                ;
            }
            else
            {
                return 0;
            }
        }
        return 1;
    }

    public int CheckNITPEmail(String S)
    {
        String M="@nitp.ac.in";
        int i;
        int X=S.length();
        for(i=0;i<M.length();i++)
        {
            char Y=S.charAt(X-11+i);
            char Z=M.charAt(i);
            if(Y!=Z)
                return 0;
        }
        return 1;
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        noInternetDialog.onDestroy();
    }

}