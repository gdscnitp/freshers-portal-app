package com.dscnitp.freshersportal.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dscnitp.freshersportal.Model.ModelQuestionPaper;
import com.dscnitp.freshersportal.Model.PdfFileInfo;
import com.dscnitp.freshersportal.R;
import com.dscnitp.freshersportal.ViewPdfActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class AdapterMaterials extends RecyclerView.Adapter<AdapterMaterials.MaterialsViewHolder> implements Filterable {

    private Context context;
    public List<ModelQuestionPaper> questionPaperList;
    public List<ModelQuestionPaper> QuestionPaperFull;

    public AdapterMaterials(Context context, List<ModelQuestionPaper> questionPaperList) {
        this.context = context;
        this.questionPaperList = questionPaperList;
        QuestionPaperFull=new ArrayList<>(questionPaperList);
    }

    @NonNull
    @Override
    public AdapterMaterials.MaterialsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_notes,parent,false);
        return new MaterialsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final AdapterMaterials.MaterialsViewHolder holder, int position) {
        final ModelQuestionPaper modelQuestionPaper=questionPaperList.get(position);
        holder.title.setText(modelQuestionPaper.getFileName());

        holder.viewPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(holder.viewPdf.getContext(), ViewPdfActivity.class);
                intent.putExtra("filename", modelQuestionPaper.getFileName());
                intent.putExtra("fileurl", modelQuestionPaper.getFileUrl());

                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                holder.viewPdf.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return questionPaperList.size();
    }

    @Override
    public Filter getFilter() {
        return QuestionFilter;
    }

    private Filter QuestionFilter=new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<ModelQuestionPaper> FilterQuestions=new ArrayList<>();
            if(charSequence==null  ||  charSequence.length()==0)
            {
                FilterQuestions.addAll(QuestionPaperFull);
            }
            else
            {
                String FilterPattern=charSequence.toString().toLowerCase().trim();
                for(ModelQuestionPaper Item : QuestionPaperFull)
                {
                    if(Item.getFileName().toLowerCase().contains(FilterPattern))
                    {
                        Log.i("FileName",Item.getFileName());
                        FilterQuestions.add(Item);
                    }
                }
            }

            FilterResults results=new FilterResults();
            results.values=FilterQuestions;
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            questionPaperList.clear();
            questionPaperList.addAll((List) filterResults.values);
            notifyDataSetChanged();
        }
    };

    public class MaterialsViewHolder extends RecyclerView.ViewHolder{

        TextView title;
        Button viewPdf;

        public MaterialsViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.pdftitle);
            viewPdf = itemView.findViewById(R.id.view_pdf_file_btn);
        }
    }
}