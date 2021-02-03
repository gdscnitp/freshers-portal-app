package com.dscnitp.freshersportal.Student;


import android.os.Bundle;
import com.dscnitp.freshersportal.Common.Node;
import android.widget.ArrayAdapter;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.dscnitp.freshersportal.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddPostFragment extends Fragment {

    Button Post;
    TextView BlogBranch,BlogDes,BlogTitle,BlogType;
    Spinner Title,Type;
    EditText Description;
    DatabaseReference databaseReferenceBlogs;

    public AddPostFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_post, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        Post=view.findViewById(R.id.btn_post);
        BlogBranch=view.findViewById(R.id.textView2);
        BlogType=view.findViewById(R.id.textView3);
        BlogTitle=view.findViewById(R.id.textView4);
        BlogDes=view.findViewById(R.id.textView5);
        Type=view.findViewById(R.id.spinner_type);
        Title=view.findViewById(R.id.spinner_title);
        Description=view.findViewById(R.id.edit_text_description);
        databaseReferenceBlogs=FirebaseDatabase.getInstance().getReference().child("Blogs");

        final Spinner Branch=view.findViewById(R.id.spinner_branch);
        ArrayAdapter<String> myBranchAdapter=new ArrayAdapter<String>(AddPostFragment.this.getActivity(),android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.Branch));
        myBranchAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Branch.setAdapter(myBranchAdapter);

        ArrayAdapter<String> myBlogTypeAdapter=new ArrayAdapter<String>(AddPostFragment.this.getActivity(),android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.BlogType));
        myBlogTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Type.setAdapter(myBlogTypeAdapter);

        ArrayAdapter<String> myBlogTitleAdapter=new ArrayAdapter<String>(AddPostFragment.this.getActivity(),android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.BlogTitle));
        myBlogTitleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Title.setAdapter(myBlogTitleAdapter);


        Post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Fourth=Description.getText().toString().trim();
                if(Fourth.equals(""))
                {
                    BlogDes.setError(getString(R.string.EmptyBlog));
                    return;
                }
                final String timestamp=String.valueOf(System.currentTimeMillis());
                String First=Branch.getSelectedItem().toString();
                String Second=Type.getSelectedItem().toString();
                String Third=Title.getSelectedItem().toString();
                HashMap<String,String> hashMap=new HashMap<>();
                hashMap.put("Writtenby", FirebaseAuth.getInstance().getUid());
                hashMap.put("Type",Second);
                hashMap.put("Title",Third);
                hashMap.put("Description",Fourth);
                hashMap.put("Department",First);
                hashMap.put("Time",timestamp);
//                hashMap.put("Branch",First);
//                hashMap.put("BlogType",Second);
//                hashMap.put("BlogTitle",Third);
//                hashMap.put("Description",Fourth);
                databaseReferenceBlogs.child(timestamp.substring(5)).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(AddPostFragment.this.getActivity(),"Your Blog Posted",Toast.LENGTH_SHORT).show();
                        Description.setText("");
                    }
                });

            }
        });
    }
}
