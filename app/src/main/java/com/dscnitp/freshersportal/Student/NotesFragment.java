package com.dscnitp.freshersportal.Student;


import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.dscnitp.freshersportal.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class NotesFragment extends Fragment implements View.OnClickListener {
    Button uploadPdf;
    private CardView quesPaper,book_pdf,notes;

    public NotesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_materials, container, false);
        uploadPdf = (Button) view.findViewById(R.id.sendgrpmsg);
        quesPaper = (CardView) view.findViewById(R.id.ques_paper_card);
        notes = (CardView) view.findViewById(R.id.notes_card);
        book_pdf = (CardView) view.findViewById(R.id.books_pdf_card);

        uploadPdf.setOnClickListener(this);
        quesPaper.setOnClickListener(this);
        notes.setOnClickListener(this);
        book_pdf.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {

        Intent intent;

        switch (view.getId()) {
            case R.id.ques_paper_card :  intent = new Intent(getActivity(), ViewQuesPaperActivity.class);startActivity(intent);break;
            case R.id.notes_card :   intent = new Intent(getActivity(), ViewNotesActivity.class);startActivity(intent);break;
            case R.id.books_pdf_card : intent = new Intent(getActivity(), ViewBooksActivity.class);startActivity(intent);break;
            case R.id.sendgrpmsg: intent = new Intent(getActivity(), UploadPdfActivity.class);startActivity(intent);break;
            default:break;
        }
    }
}
