package com.dscnitp.freshersportal.Alumni;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.Nullable;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.ImageView;
import com.dscnitp.freshersportal.Common.Node;
import com.dscnitp.freshersportal.R;
import com.dscnitp.freshersportal.Model.Session;
import com.dscnitp.freshersportal.Student.LoginActivity;
import com.dscnitp.freshersportal.Student.SignUpActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

import am.appwise.components.ni.NoInternetDialog;

public class AlumniSignUpActivity extends AppCompatActivity {

    EditText spinner1,spinner2;
    TextInputLayout name,email,password,company;
    ProgressBar bar;
    ImageView ivProfile;
    private FirebaseAuth mAuth;
    private Uri localFileUri,ServerFileUri;
    private FirebaseUser firebaseUser;
    private DatabaseReference db;
    private FirebaseStorage mStorage;


    String emails,Name,From,Company,passw,uid;
    String selected,getSelected;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alumni_sign_up);

        spinner1 =  findViewById(R.id.spinner1);
        name = (TextInputLayout) findViewById(R.id.name);
        email = (TextInputLayout) findViewById(R.id.email);
        password = (TextInputLayout) findViewById(R.id.pwd);
        company=(TextInputLayout) findViewById(R.id.Company);
        bar = (ProgressBar) findViewById(R.id.progressBar);
        ivProfile=findViewById(R.id.ivProfile);
        spinner2 =  findViewById(R.id.spinner2);
       spinner1.setOnTouchListener(new View.OnTouchListener() {
           @Override
           public boolean onTouch(View v, MotionEvent event) {
               AlertDialog.Builder builder=new AlertDialog.Builder(AlumniSignUpActivity.this);
               builder.setTitle("Select Session")
                       .setItems(Session.options, new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialog, int which) {
                               selected=Session.options[which];
                               spinner1.setText(selected);
                           }
                       }).show();
               return true;
           }
       });
        spinner2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                AlertDialog.Builder builder=new AlertDialog.Builder(AlumniSignUpActivity.this);
                builder.setTitle("Select Session")
                        .setItems(Session.options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                getSelected=Session.options[which];
                                spinner2.setText(getSelected);
                            }
                        }).show();
                return true;
            }
        });

    }


    public void signuphere(View view) {
        String emails = email.getEditText().getText().toString();
        String passw = password.getEditText().getText().toString();
        emails = email.getEditText().getText().toString();
        Name=name.getEditText().getText().toString();
        From=spinner1.getText().toString();
        Company=company.getEditText().getText().toString();
        passw = password.getEditText().getText().toString();

        if(Name.equals("")){
            name.setError("Enter Name");
        }
//        else if(CheckAlphabet(Name)==0) {
//            name.setError("Enter Alphabet Only");
//        }
        else if(emails.equals("")){
            email.setError("Enter Email");
        }
//        else if(CheckNITPEmail(emails)==0){
//            email.setError("Enter NITP Email Only");
//        }
        else if(passw.equals("")){
            password.setError("Enter Password");
        }
        else{
            mAuth = FirebaseAuth.getInstance();
            mAuth.createUserWithEmailAndPassword(emails, passw).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        bar.setVisibility(View.INVISIBLE);
                        firebaseUser=mAuth.getCurrentUser();
                        email.getEditText().setText("");
                        password.getEditText().setText("");
                        uid = firebaseUser.getUid();
                        if(localFileUri!=null)
                            updateNameAndPhoto();
                        else
                            updateNameOnly();
                    }
                    else
                    {
                        bar.setVisibility(View.INVISIBLE);
                        Toast.makeText(getApplicationContext(), "Process Error", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }
    public void gotosignin(View view) {
        startActivity(new Intent(AlumniSignUpActivity.this, AlumniLoginActivity.class));
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
                .setDisplayName(Name.trim())
                .build();

        firebaseUser.updateProfile(request).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if(task.isSuccessful())
                {
                    db= FirebaseDatabase.getInstance().getReference().child(Node.Users);
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put(Node.Email, emails);
                    hashMap.put(Node.Year, From);
                    hashMap.put(Node.Name, Name);
                    hashMap.put(Node.Company, Company);
                    hashMap.put(Node.USER_TYPE, "Alumni");
                    hashMap.put("id", "");
                    hashMap.put("device_token", "");
                    hashMap.put("uid", uid);
                    hashMap.put("imgUrl", "");
                    db.child(uid).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                Toast.makeText(AlumniSignUpActivity.this, "User Created Successfully", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(AlumniSignUpActivity.this,AlumniMainActivity.class));
                                finish();
                            }
                            else
                            {
                                Toast.makeText(AlumniSignUpActivity.this,"User Not Created",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else
                {
                    Toast.makeText(AlumniSignUpActivity.this,"Failed to Update : %1$s"+task.getException(),Toast.LENGTH_SHORT).show();
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
                                    setDisplayName(Name.trim())
                                    .setPhotoUri(ServerFileUri)
                                    .build();

                            firebaseUser.updateProfile(request).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful())
                                    {
                                        db= FirebaseDatabase.getInstance().getReference().child(Node.Users);
                                        HashMap<String, Object> hashMap = new HashMap<>();
                                        hashMap.put(Node.Email, emails);
                                        hashMap.put(Node.Year, From);
                                        hashMap.put(Node.Name, Name);
                                        hashMap.put(Node.Company, Company);
                                        hashMap.put(Node.USER_TYPE, "Alumni");
                                        hashMap.put("id", "");
                                        hashMap.put("device_token", "");
                                        hashMap.put("uid", uid);
                                        hashMap.put("imgUrl", ServerFileUri.getPath());
                                        db.child(uid).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()) {
                                                    Toast.makeText(AlumniSignUpActivity.this, "User Created Successfully", Toast.LENGTH_SHORT).show();
                                                    startActivity(new Intent(AlumniSignUpActivity.this,AlumniMainActivity.class));
                                                    finish();
                                                }
                                                else
                                                {
                                                    Toast.makeText(AlumniSignUpActivity.this,"User Not Created",Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }
                                    else
                                    {
                                        Toast.makeText(AlumniSignUpActivity.this,"Failed to Update : %1$s"+task.getException(),Toast.LENGTH_SHORT).show();

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
}
