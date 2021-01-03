package com.dscnitp.freshersportal.Adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dscnitp.freshersportal.Model.PdfFileInfo;
import com.dscnitp.freshersportal.R;
import com.dscnitp.freshersportal.ViewPdfActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class AdapterMaterials extends FirebaseRecyclerAdapter<PdfFileInfo, AdapterMaterials.notesviewholder> {

    public AdapterMaterials(@NonNull FirebaseRecyclerOptions<PdfFileInfo> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull final notesviewholder holder, int position, @NonNull final PdfFileInfo model) {

        holder.title.setText(model.getFilename());

            holder.viewPdf.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(holder.viewPdf.getContext(), ViewPdfActivity.class);
                    intent.putExtra("filename", model.getFilename());
                    intent.putExtra("fileurl", model.getFileurl());

                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    holder.viewPdf.getContext().startActivity(intent);
                }
            });

    }


    @NonNull
    @Override
    public notesviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_notes,parent,false);
        return new notesviewholder(view);

    }


    public class notesviewholder extends RecyclerView.ViewHolder{

        TextView title;
        Button viewPdf;

        public notesviewholder(@NonNull View itemView) {
             super(itemView);

             title = itemView.findViewById(R.id.pdftitle);
             viewPdf = itemView.findViewById(R.id.view_pdf_file_btn);

         }
     }
}
