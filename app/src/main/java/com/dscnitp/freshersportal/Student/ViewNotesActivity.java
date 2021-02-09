package com.dscnitp.freshersportal.Student;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dscnitp.freshersportal.Adapter.AdapterMaterials;
import com.dscnitp.freshersportal.Model.ModelQuestionPaper;
import com.dscnitp.freshersportal.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ViewNotesActivity extends AppCompatActivity {

    Button downloadNotes;
    AdapterMaterials adapter;
    SearchView searchView;
    List<ModelQuestionPaper> NotesList;
    DatabaseReference databaseReferenceNotes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_notes);

        final RecyclerView notesListrecyclerView = (RecyclerView) findViewById(R.id.pdf_list);
        searchView=findViewById(R.id.search_files);
        notesListrecyclerView.setLayoutManager(new LinearLayoutManager(this));
        NotesList=new ArrayList<>();

//        FirebaseRecyclerOptions<PdfFileInfo> options = new FirebaseRecyclerOptions.Builder<PdfFileInfo>()
//                .setQuery(FirebaseDatabase.getInstance().getReference("Notes"),PdfFileInfo.class).build();

//        adapter = new AdapterMaterials(options);

        databaseReferenceNotes=FirebaseDatabase.getInstance().getReference().child("Notes");
        databaseReferenceNotes.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds:snapshot.getChildren())
                {

                    String FileName=ds.child("filename").getValue().toString();
                    String FileUrl=ds.child("fileurl").getValue().toString();
                    ModelQuestionPaper modelQuestionPaper=new ModelQuestionPaper(FileName,FileUrl);
                    NotesList.add(modelQuestionPaper);
                }
                adapter = new AdapterMaterials(ViewNotesActivity.this,NotesList);
                notesListrecyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        /*downloadNotes = findViewById(R.id.download_file_btn);
        downloadNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                download();
            }
        });
*/
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                adapter.getFilter().filter(s);
                return false;
            }
        });
    }
/*
    private void download() {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        storageReference.child("");
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                downloadFile(ViewNotesActivity.this,"",".pdf",DIRECTORY_DOWNLOADS, );
            }
        }) .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
    }
    private void downloadFile(Context context,String fileName, String fileExtension,String destinationDirectory,String url) {
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(context,destinationDirectory,fileName + fileExtension);
        downloadManager.enqueue(request);
    }*/


//    @Override
//    protected void onStart() {
//        super.onStart();
//        adapter.startListening();
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        adapter.stopListening();
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_file_menu,menu);

        MenuItem item = menu.findItem(R.id.search_item);
        SearchView searchView = (SearchView)item.getActionView();


        return super.onCreateOptionsMenu(menu);
    }

    private void processSearch(String s) {
        // FirebaseRecyclerOptions<PdfFileInfo> options = new FirebaseRecyclerOptions.Builder<PdfFileInfo>()
        //       .setQuery(FirebaseDatabase.getInstance().getReference()
        //             .child("Materials").orderByChild("filename").startAt(s).endAt(s+"\uf8ff"),PdfFileInfo.class).build();

        //adapter = new AdapterMaterials(options);
        //adapter.startListening();
        //pdfListrecyclerView.setAdapter(adapter);

    }
}