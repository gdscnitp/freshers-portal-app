package com.dscnitp.freshersportal.Student;

import androidx.annotation.Nullable;
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

import com.dscnitp.freshersportal.Alumni.AlumniMainActivity;
import com.dscnitp.freshersportal.R;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
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

public class LoginActivity extends AppCompatActivity {
    TextInputLayout t1, t2;
    TextView createaccount;
    ProgressBar bar;
    EditText emails, password;
    private static final String EMAIL = "email";
    public static final String Id = "id";
    public static final String imgurl = "imgUrl";
    public static final String dbname = "name";
    private static final int RC_SIGN_IN = 101;
    FloatingActionButton googleSignUp;
    Button login;
    String name2;
    FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    GoogleApiClient mGoogleApiClient;
    //    GoogleSignInClient mGoogleSignInClient;
    ProgressDialog progressDialog;
    Animation bounce;
    String mailid = "";
    String name = "";
    String photo = "";
   NoInternetDialog noInternetDialog;

    FirebaseUser currentUser;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        noInternetDialog = new NoInternetDialog.Builder(this).setBgGradientStart(Color.parseColor("#4488A7"))
                .setBgGradientCenter(Color.parseColor("#4488A7")).setButtonColor(Color.parseColor("#2196F3"))
                .setBgGradientEnd(Color.parseColor("#4488A7")).build();
        t1 = (TextInputLayout) findViewById(R.id.email_login);
        t2 = (TextInputLayout) findViewById(R.id.pwd_login);
        bar = (ProgressBar) findViewById(R.id.progressBar_login);
        createaccount = findViewById(R.id.createaccount);
        createaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
                finish();
            }
        });
        mAuth = FirebaseAuth.getInstance();
        FirebaseApp.initializeApp(this);
        emails = findViewById(R.id.emails);
        password = findViewById(R.id.password);
        googleSignUp = (FloatingActionButton) findViewById(R.id.btn_glogin);
        googleSignUp.setVisibility(View.INVISIBLE);
        Animation fadeOut = new AlphaAnimation(0, 1);
        fadeOut.setInterpolator(new AccelerateInterpolator()); //and this
        fadeOut.setStartOffset(1000);
        fadeOut.setDuration(1000);
        AnimationSet animation1 = new AnimationSet(false); //change to false
        animation1.addAnimation(fadeOut);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);


        googleSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setMessage("Getting accounts...");
                progressDialog.show();
                signIn();
            }
        });

        DatabaseReference daa = FirebaseDatabase.getInstance().getReference();

//        mGoogleSignInClient= GoogleSignIn.getClient(this,gso);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                    secondActivity();
                } else {
                    bounce = AnimationUtils.loadAnimation(getApplicationContext(),
                            R.anim.bounce);
                    googleSignUp.startAnimation(bounce);
                    googleSignUp.setVisibility(View.VISIBLE);
                }
            }
        };

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


        login = findViewById(R.id.login);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String validemail = "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +

                        "\\@" +

                        "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +

                        "(" +

                        "\\." +

                        "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +

                        ")+";


                String email = emails.getText().toString();

                Matcher matcher= Pattern.compile(validemail).matcher(email);

                if (matcher.matches()){
                    Toast.makeText(getApplicationContext(),"Logging you in!",Toast.LENGTH_LONG).show();
                    signinhere();
                }
                else {
                    Toast.makeText(getApplicationContext(),"Enter Valid Email-Id",Toast.LENGTH_LONG).show();
                }

//                if (emails.getText().equals("")) {
//                    emails.setError("Required");
//                    return;
//                }
//                if (password.getText().equals("")) {
//                    password.setError("Required");
//                    return;
//                }
                signinhere();
            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        noInternetDialog.onDestroy();
    }

    @Override
    public void onStart() {

        super.onStart();

        mAuth.addAuthStateListener(mAuthListener);

//        mAuth=FirebaseAuth.getInstance();

//        if (mAuth != null)
//        {
//            currentUser = mAuth.getCurrentUser();
//        }
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                FirebaseUser user=mAuth.getCurrentUser();
//                if(user==null){
//                    bounce = AnimationUtils.loadAnimation(getApplicationContext(),
//                            R.anim.bounce);
//                    googleSignUp.startAnimation(bounce);
//                    googleSignUp.setVisibility(View.VISIBLE);
//                }
//                else {
//                    uid=user.getUid();
//                    secondActivity();
//                }
//            }
//        },1000);
    }
    private void signIn() {
        progressDialog.dismiss();
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    String id;

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
//        if (requestCode == RC_SIGN_IN) {
//            // The Task returned from this call is always completed, no need to attach
//            // a listener.
//            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
//            handleSignInResult(task);
//        }
//    }
//
//    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
//        try {
//            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
//            // Signed in successfully, show authenticated UI.
////            updateUI(account);
//            Toast.makeText(getApplicationContext(), "Authentication Successful", Toast.LENGTH_SHORT).show();
//        } catch (ApiException e) {
//            // The ApiException status code indicates the detailed failure reason.
//            // Please refer to the GoogleSignInStatusCodes class reference for more information.
//           Toast.makeText(LoginActivity.this, "Auth Went Wrong", Toast.LENGTH_SHORT).show();
//        }
//    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            progressDialog.setMessage("Signing you in...");
            //progressDialog.show(EntryPage1.this, "", "Signing you in...");
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                id = account.getId();


//                SharedPreferences sharedPreferences = PreferenceManager
//                        .getDefaultSharedPreferences(this);
//                SharedPreferences.Editor editor = sharedPreferences.edit();
//                editor.putString("id", account.getId());
//                editor.apply();


                firebaseAuthWithGoogle(account);
//                mGoogleApiClient.clearDefaultAccountAndReconnect();
            } else {
                // Google Sign In failed, update UI appropriately
                // ...
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), " Connection failed", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("Login Page", "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
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
                            Toast.makeText(getApplicationContext(), "Successful signin", Toast.LENGTH_SHORT).show();
                            progressDialog.setMessage("Checking Email...");
                            checkingUserExist(id);
                            progressDialog.dismiss();
                        }
                    }
                });
    }

//    private void firebaseAuthWithGoogle(String IdToken)
//    {
//        AuthCredential credential=GoogleAuthProvider.getCredential(IdToken,null);
//        mAuth.signInWithCredential(credential)
//        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//            @Override
//            public void onComplete(@NonNull Task<AuthResult> task) {
//                if(task.isSuccessful())
//                {
//                    FirebaseUser user=mAuth.getCurrentUser();
//                    secondActivity();
//                }
//                else
//                {
//                    Toast.makeText(LoginActivity.this,"Authentication Failed",Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//    }


    private void checkingUserExist(String UID) {
        uid = FirebaseAuth.getInstance().getUid();
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("users");
        db.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    progressDialog.dismiss();
                    secondActivity();
                } else {
                    try {
                        mailid = mAuth.getCurrentUser().getEmail();
                        name = mAuth.getCurrentUser().getDisplayName();
                        photo = mAuth.getCurrentUser().getPhotoUrl().toString();
                    } catch (Exception e) {
                        Log.e("Getting Started", e.getMessage());
                    }
                    progressDialog.setMessage("Creating New User...");
                    createNewUser(mailid, name, photo);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    String uid;

    private void secondActivity() {
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("users");
        database.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final String accountType = "" + dataSnapshot.child("USER_TYPE").getValue();
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("uid", uid);
                database.child(uid).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        if (accountType.equals("student")) {
                            Intent mainIntent = new Intent(LoginActivity.this, DashboardActivity.class);
                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(mainIntent);
                            finish();
                        } else {
                            Intent mainIntent = new Intent(LoginActivity.this, AlumniMainActivity.class);
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

    public void createNewUser(String mailid, String name, String photo) {
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("email", mailid);
        hashMap.put("name", name);
        hashMap.put("roll", "");
        hashMap.put("branch", "");
        hashMap.put("year", "");
        hashMap.put("USER_TYPE", "student");
        hashMap.put("id", "");
        hashMap.put("device_token", "");
        hashMap.put("uid", uid);
        hashMap.put("imgUrl", photo);

        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("users");
        db.child(uid).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                progressDialog.dismiss();
                Intent i = new Intent(getApplicationContext(), DashboardActivity.class);
                i.putExtra("Name2", name2);
                startActivity(i);
                finish();
            }
        });
    }


    @Override
    public void onResume() {
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.e("USERUID----", mAuth.getCurrentUser().getUid());
                    if (user.isEmailVerified()) {
                        Log.e("Verify dialog true", user.isEmailVerified() + "");
                    } else {
                        Log.e("Verify Image dialog", user.isEmailVerified() + "");
                        Dialog mdialog = new Dialog(getApplicationContext());
                        mdialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    }
                }
            }
        };
        mAuth.addAuthStateListener(mAuthListener);
        super.onResume();
    }

    @Override
    public void onStop() {
        if (mAuthListener != null) {
//            auth.removeAuthStateListener(authStateListener);
        }
        super.onStop();
    }

    public void signinhere() {
        String email = t1.getEditText().getText().toString();
        String password = t2.getEditText().getText().toString();

        if (!email.equals("") && !password.equals("")) {

            if (!email.equals("") && !password.equals("")) {

                bar.setVisibility(View.VISIBLE);
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    bar.setVisibility(View.INVISIBLE);
                                    Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                                    intent.putExtra("email", mAuth.getCurrentUser().getEmail());
                                    intent.putExtra("uid", mAuth.getCurrentUser().getUid());
                                    startActivity(intent);
                                    finish();
                                } else {
                                    bar.setVisibility(View.INVISIBLE);
                                    t1.getEditText().setText("");
                                    t2.getEditText().setText("");
                                    Toast.makeText(getApplicationContext(), "Invalid Email/Password", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        }

    }
}