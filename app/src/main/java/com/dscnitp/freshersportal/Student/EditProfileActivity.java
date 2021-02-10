package com.dscnitp.freshersportal.Student;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.dscnitp.freshersportal.Common.Node;
import com.dscnitp.freshersportal.R;
import com.dscnitp.freshersportal.SplashScreen;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

import am.appwise.components.ni.NoInternetDialog;

public class EditProfileActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    StorageReference storageReference;
    FirebaseUser firebaseUser;
    Uri ServerFileUri;
    NoInternetDialog noInternetDialog;


    private TextInputEditText Name, Branch, RollNo, Year;
    Button logout, edit;
    ImageView profilePic;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseStorage mStorage;
    private DatabaseReference databaseReferenceUsers;
    private String PhotoUrl;
    private Uri imageUri;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);


        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("users");
        noInternetDialog = new NoInternetDialog.Builder(this).setBgGradientStart(Color.parseColor("#4488A7"))
                .setBgGradientCenter(Color.parseColor("#4488A7")).setButtonColor(Color.parseColor("#2196F3"))
                .setBgGradientEnd(Color.parseColor("#4488A7")).build();
        databaseReferenceUsers=FirebaseDatabase.getInstance().getReference("users");

        Name = (TextInputEditText) findViewById(R.id.name);
        RollNo = (TextInputEditText) findViewById(R.id.roll);
        Branch = (TextInputEditText) findViewById(R.id.branch);
        Year = (TextInputEditText) findViewById(R.id.enroll);
        profilePic = (ImageView) findViewById(R.id.student_profile_image);

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(EditProfileActivity.this,"Image clicked",Toast.LENGTH_SHORT).show();
                CropImage.activity().setAspectRatio(1, 1).start(EditProfileActivity.this);
            }
        });
        logout = findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                finish();
                startActivity(new Intent(EditProfileActivity.this, SplashScreen.class));
            }
        });

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {

            Query query= databaseReference.orderByChild("email").equalTo(user.getEmail());
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        String name = "" + ds.child("name").getValue();
                        Name.setText(name);
                        String roll = "" + ds.child("rollNo").getValue();
                        RollNo.setText(roll);
                        String branch = "" + ds.child("Branch").getValue();
                        Branch.setText(branch);
                        String year = "" + ds.child("enrollment").getValue();
                        Year.setText(year);
                       // String url = (String) ds.child("photo").getValue();
                        //Picasso.get().load(url).into(profilePic);

                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();

            uploadImage(imageUri);
        } else {
            Toast.makeText(this, "Error try again", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadImage(Uri imageUri) {

        if (imageUri != null) {
            String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
            storageReference = FirebaseStorage.getInstance().getReference();
            final StorageReference fileRef = storageReference.child("profile images").child(id).child("profile.jpg");
            fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            HashMap<String, Object> userMap = new HashMap<>();
                            userMap.put(Node.Photo, uri.toString());
                            databaseReference = FirebaseDatabase.getInstance().getReference().child(Node.Users);
                            databaseReferenceUsers.child(currentUser.getUid()).updateChildren(userMap);

                            Picasso.get().load(uri).into(profilePic);
                            Toast.makeText(EditProfileActivity.this, "Image uploaded", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(EditProfileActivity.this, "Image upload failed try again...", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "Image not selected", Toast.LENGTH_SHORT).show();
        }

    }

    public void btnSave(View V) {
        if (Name.getText().toString().trim().equals("")) {
            Name.setError(getString(R.string.etName));
        }
        if (RollNo.getText().toString().trim().equals("")) {
            RollNo.setError(getString(R.string.etRoll));
        }
        if (Branch.getText().toString().trim().equals("")) {
            Branch.setError(getString(R.string.etBranch));
        }
        if (Year.getText().toString().trim().equals("")) {
            Year.setError(getString(R.string.etYear));
        } else {

            updateNameOnly();
        }


    }
    public void updateNameOnly() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Updating your profile");
        progressDialog.setMessage("Please wait ....");
        progressDialog.show();

        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder().
                setDisplayName(Name.getText().toString().trim()).build();

        currentUser.updateProfile(request).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    String UserID = FirebaseAuth.getInstance().getUid();
                    HashMap<String, Object> hashMap=new HashMap<String, Object>();
                    hashMap.put(Node.Name,Name.getText().toString().trim());
                    hashMap.put(Node.ROLL_NO,RollNo.getText().toString().trim());
                    hashMap.put(Node.Branch,Branch.getText().toString().trim());
                    hashMap.put("enrollment",Year.getText().toString().trim());
                    databaseReferenceUsers.child(currentUser.getUid()).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                progressDialog.dismiss();
                                Toast.makeText(EditProfileActivity.this, "User updated", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(EditProfileActivity.this, DashboardActivity.class));
                                finish();
                            }
                            else
                            {
                                Toast.makeText(EditProfileActivity.this,"User Not Created",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else
                {
                    Toast.makeText(EditProfileActivity.this,"Failed to Update : %1$s"+task.getException(),Toast.LENGTH_SHORT).show();

                }
            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        noInternetDialog.onDestroy();
    }
}
