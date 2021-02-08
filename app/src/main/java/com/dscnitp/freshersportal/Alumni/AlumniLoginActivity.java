package com.dscnitp.freshersportal.Alumni;

import androidx.appcompat.app.AppCompatActivity;

import androidx.annotation.NonNull;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dscnitp.freshersportal.R;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import am.appwise.components.ni.NoInternetDialog;

public class AlumniLoginActivity extends AppCompatActivity {
    TextInputLayout t1, t2;
    TextView createaccount;
    ProgressBar bar;
    FirebaseAuth mAuth;


    private static final String EMAIL = "email";
    public static final String Id="id";
    public static final String imgurl="imgUrl";
    public static final String dbname="name";
    private static final int RC_SIGN_IN = 101;
    FloatingActionButton googleSignUp;
    String name2;
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private GoogleApiClient mGoogleApiClient;
    ProgressDialog progressDialog;
    Animation bounce;
    String mailid = "";
    String name = "";
    String photo = "";
    NoInternetDialog noInternetDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alumni_login);
        t1 = (TextInputLayout) findViewById(R.id.email_login);
        t2 = (TextInputLayout) findViewById(R.id.pwd_login);
        bar = (ProgressBar) findViewById(R.id.progressBar_login);
        createaccount=findViewById(R.id.createaccount);
        noInternetDialog = new NoInternetDialog.Builder(this).setBgGradientStart(Color.parseColor("#4488A7"))
                .setBgGradientCenter(Color.parseColor("#4488A7")).setButtonColor(Color.parseColor("#2196F3"))
                .setBgGradientEnd(Color.parseColor("#4488A7")).build();

        createaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AlumniLoginActivity.this, AlumniSignUpActivity.class));
                finish();
            }
        });
        mAuth= FirebaseAuth.getInstance();
        FirebaseApp.initializeApp(this);
        googleSignUp=(FloatingActionButton) findViewById(R.id.btn_glogin);
        googleSignUp.setVisibility(View.INVISIBLE);
        Animation fadeOut = new AlphaAnimation(0, 1);
        fadeOut.setInterpolator(new AccelerateInterpolator()); //and this
        fadeOut.setStartOffset(1000);
        fadeOut.setDuration(1000);
        AnimationSet animation1 = new AnimationSet(false); //change to false
        animation1.addAnimation(fadeOut);
        auth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);

        DatabaseReference daa = FirebaseDatabase.getInstance().getReference();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                        progressDialog.dismiss();
                        Log.e("connection failed", connectionResult.getErrorMessage());
                        Toast.makeText(getApplicationContext(), "Connection failed", Toast.LENGTH_SHORT).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        googleSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setMessage("Getting accounts...");
                progressDialog.show();
                signIn();

            }
        });
    }
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    String id;
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == RC_SIGN_IN) {
            progressDialog.setMessage("Signing you in...");
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess())
            {
                GoogleSignInAccount account = result.getSignInAccount();
                id=account.getId();
                firebaseAuthWithGoogle(account);
                mGoogleApiClient.clearDefaultAccountAndReconnect();
            }
            else
                {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), " Connection failed", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("Login Page", "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("Login Page", "signInWithCredential:onComplete:" + task.isSuccessful());
                        if (!task.isSuccessful()) {
                            Log.w("Login Page", "signInWithCredential", task.getException());
                            Toast.makeText(getApplicationContext(), "Something went wrong\n" + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        } else {
                            Toast.makeText(getApplicationContext(), "Successful signin",
                                    Toast.LENGTH_SHORT).show();
                            progressDialog.setMessage("Checking Email...");
                            checkingUserExist(id);
                            progressDialog.dismiss();
                        }
                    }
                });
    }
    private void checkingUserExist(String UID) {
        uid=FirebaseAuth.getInstance().getUid();
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("users");
        db.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    progressDialog.dismiss();
                    secondActivity();
                }
                else{
                    try{
                        mailid = auth.getCurrentUser().getEmail();
                        name = auth.getCurrentUser().getDisplayName();
                        photo = auth.getCurrentUser().getPhotoUrl().toString();
                    }
                    catch (Exception e){
                        Log.e("Getting Started",e.getMessage());
                    }
                    progressDialog.setMessage("Creating New User...");
                    createNewUser(mailid,name,photo);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    String uid;
    private void secondActivity() {
        uid=FirebaseAuth.getInstance().getUid();
        final DatabaseReference database=FirebaseDatabase.getInstance().getReference().child("users");
        database.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final String accountType = "" + dataSnapshot.child("USER_TYPE").getValue();
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("uid", uid);
                database.child(uid).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        if (accountType.equals("Alumni")) {
                            Intent mainIntent = new Intent(AlumniLoginActivity.this, AlumniMainActivity.class);
                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(mainIntent);
                            finish();
                        }
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void createNewUser( String mailid, String name, String photo) {

        uid=FirebaseAuth.getInstance().getUid();
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("email",mailid);
        hashMap.put("name",name);
        hashMap.put("USER_TYPE", "Alumni");
        hashMap.put("id", "");
        hashMap.put("device_token", "");
        hashMap.put("uid", uid);
        hashMap.put("imgUrl",photo);
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("users");
        db.child(uid).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                progressDialog.dismiss();
                Intent i=new Intent(getApplicationContext(), AlumniMainActivity.class);
                i.putExtra("Name2",name2);
                startActivity(i);
                finish();
            }
        });
    }
    FirebaseUser currentUser;
    @Override
    public void onStart() {
        super.onStart();
        auth=FirebaseAuth.getInstance();
        if (auth != null) {
            currentUser = auth.getCurrentUser();
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                FirebaseUser user=auth.getCurrentUser();
                if(user==null){
                    bounce = AnimationUtils.loadAnimation(getApplicationContext(),
                            R.anim.bounce);
                    googleSignUp.startAnimation(bounce);
                    googleSignUp.setVisibility(View.VISIBLE);
                }
                else {
                    uid=user.getUid();
                    secondActivity();
                }
            }
        },1000);


    }


    @Override
    public void onResume() {


        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.e("USERUID----",auth.getCurrentUser().getUid());
                    if(user.isEmailVerified()) {
                        Log.e("Verify dialog true",user.isEmailVerified()+"" );
                    }
                    else {
                        Log.e("Verify Image dialog",user.isEmailVerified()+"" );
                        Dialog mdialog = new Dialog(getApplicationContext());
                        mdialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    }
                }
            }
        };
        auth.addAuthStateListener(authStateListener);
        super.onResume();
    }
    @Override
    public void onStop() {
        if (authStateListener != null) {
//            auth.removeAuthStateListener(authStateListener);
        }
        super.onStop();
    }

    public void signinhere(View view)
    {

        String email=t1.getEditText().getText().toString();
        String password=t2.getEditText().getText().toString();
        if(!email.equals("") && !password.equals("")){
            bar.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            bar.setVisibility(View.INVISIBLE);
                            Intent intent =new Intent(AlumniLoginActivity.this, AlumniMainActivity.class);
                            intent.putExtra("email",mAuth.getCurrentUser().getEmail());
                            intent.putExtra("uid",mAuth.getCurrentUser().getUid());
                            startActivity(intent);
                            finish();
                        }
                        else {
                            bar.setVisibility(View.INVISIBLE);
                            t1.getEditText().setText("");
                            t2.getEditText().setText("");
                            Toast.makeText(getApplicationContext(),"Invalid Email/Password", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

}
    @Override
    protected void onDestroy() {
        super.onDestroy();
        noInternetDialog.onDestroy();
    }
}