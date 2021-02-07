package com.dscnitp.freshersportal.Student;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.Toast;

import com.dscnitp.freshersportal.Adapter.AdapterMaterials;
import com.dscnitp.freshersportal.Model.ModelQuestionPaper;
import com.dscnitp.freshersportal.Model.PdfFileInfo;
import com.dscnitp.freshersportal.R;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ViewBooksActivity extends AppCompatActivity {

    AdapterMaterials adapter;
    SearchView searchView;
    List<ModelQuestionPaper> BooksList;
    DatabaseReference databaseReferenceBooks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_books);

        final RecyclerView booksListrecyclerView = (RecyclerView) findViewById(R.id.books_list);
        searchView=findViewById(R.id.search_files);
        booksListrecyclerView.setLayoutManager(new LinearLayoutManager(this));
        BooksList=new ArrayList<>();

//        FirebaseRecyclerOptions<PdfFileInfo> options = new FirebaseRecyclerOptions.Builder<PdfFileInfo>()
//                .setQuery(FirebaseDatabase.getInstance().getReference().child("Books-PDFs"),PdfFileInfo.class).build();
//
//        adapter = new AdapterMaterials(options);

        databaseReferenceBooks=FirebaseDatabase.getInstance().getReference().child("Books-PDFs");
        databaseReferenceBooks.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds:snapshot.getChildren())
                {
                    String FileName=ds.child("filename").getValue().toString();
                    String FileUrl=ds.child("fileurl").getValue().toString();
                    ModelQuestionPaper modelQuestionPaper=new ModelQuestionPaper(FileName,FileUrl);
                    BooksList.add(modelQuestionPaper);
                }
                adapter = new AdapterMaterials(ViewBooksActivity.this,BooksList);
                booksListrecyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

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
        //FirebaseRecyclerOptions<PdfFileInfo> options = new FirebaseRecyclerOptions.Builder<PdfFileInfo>()
        //     .setQuery(FirebaseDatabase.getInstance().getReference()
        //         .child("Books-PDFs").orderByChild("filename").startAt(s).endAt(s+"\uf8ff"),PdfFileInfo.class).build();

        //adapter = new AdapterMaterials(options);
        //adapter.startListening();
        //booksListrecyclerView.setAdapter(adapter);

    }
}