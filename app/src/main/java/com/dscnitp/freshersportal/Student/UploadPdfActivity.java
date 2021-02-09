package com.dscnitp.freshersportal.Student;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.dscnitp.freshersportal.Model.PdfFileInfo;
import com.dscnitp.freshersportal.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.type.TimeOfDayOrBuilder;

import am.appwise.components.ni.NoInternetDialog;


public class UploadPdfActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Button uploadPDF,selectFile;
    TextView fileStatus;
    FirebaseStorage storage;
    FirebaseDatabase database;
    Uri pdfUri;
    Spinner materialsType;
    EditText materialsTitle;
    String text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_pdf);

        storage = FirebaseStorage.getInstance();
        database = FirebaseDatabase.getInstance();
        selectFile = (Button) findViewById(R.id.selectFile_button);
        uploadPDF = (Button) findViewById(R.id.uploadPDF);
        fileStatus = (TextView) findViewById(R.id.fileName);
        materialsType = (Spinner) findViewById(R.id.material_type_spinner);
        materialsTitle = (EditText) findViewById(R.id.material_title);


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.MaterialsType, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        materialsType.setAdapter(adapter);
        materialsType.setOnItemSelectedListener(this);

        selectFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ContextCompat.checkSelfPermission(UploadPdfActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
                    selectPdf();
                }
                else{
                    ActivityCompat.requestPermissions(UploadPdfActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},9);
                }
            }
        });

        uploadPDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(pdfUri != null) {
                    uploadFile(pdfUri);
                }else{
                    Toast.makeText(UploadPdfActivity.this, "Select a file", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void uploadFile(Uri pdfUri) {

        final ProgressDialog progressDialog = new ProgressDialog(UploadPdfActivity.this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setTitle("Uploading File...");
        progressDialog.setProgress(0);
        progressDialog.show();

        final String  title = materialsTitle.getText().toString();
        final String typeTitle = materialsType.getSelectedItem().toString();

        final StorageReference storageReference = storage.getReference();
        storageReference.child(typeTitle).child(title).putFile(pdfUri)

                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                        task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String url = uri.toString();

                                PdfFileInfo obj = new PdfFileInfo(title,url);
                                DatabaseReference databaseReference = database.getReference(typeTitle);

                                databaseReference.child(databaseReference.push().getKey()).setValue(obj)

                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    Toast.makeText(UploadPdfActivity.this, "File uploaded", Toast.LENGTH_SHORT).show();
                                                    progressDialog.dismiss();
                                                    materialsTitle.setText("");
                                                    fileStatus.setText("");
                                                }else{
                                                    Toast.makeText(UploadPdfActivity.this, "File upload failed", Toast.LENGTH_SHORT).show();
                                                    progressDialog.dismiss();
                                                }
                                            }
                                        });
                            }
                        });

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UploadPdfActivity.this, "Error!! something went wrong try again.", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();

            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {

                int currentProgress = (int) (100*snapshot.getBytesTransferred()/snapshot.getTotalByteCount());
                progressDialog.setProgress(currentProgress);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 9 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            selectPdf();
        }
        else{
            Toast.makeText(UploadPdfActivity.this, "Please provide permission..", Toast.LENGTH_SHORT).show();
        }
    }

    private void selectPdf() {
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT); //to fetch files
        startActivityForResult(intent,86);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 86 && resultCode == RESULT_OK && data != null) {
            pdfUri = data.getData();
            fileStatus.setText("Selected file: " + data.getData().getLastPathSegment());

        } else {
            Toast.makeText(UploadPdfActivity.this, "Please select the file", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
         text = adapterView.getItemAtPosition(i).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
