package org.openmrs.mobile.activities.patientdashboard;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import org.openmrs.mobile.R;
import org.openmrs.mobile.sampledata.DiagnosesData;

import java.util.List;


public class ListDiagnoses extends RecyclerView.Adapter<ListDiagnoses.DiagnosesViewHolder> {

    public static class DiagnosesViewHolder extends RecyclerView.ViewHolder {


        public TextView title;

        public DiagnosesViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);

        }
    }

    private List<DiagnosesData> DiagnosesList;

    public ListDiagnoses(List<DiagnosesData> DiagnosesList) {
        this.DiagnosesList = DiagnosesList;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public DiagnosesViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_diagnoses, viewGroup, false);
        return new DiagnosesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final DiagnosesViewHolder holder, final int position) {
        holder.title.setText(DiagnosesList.get(position).title);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Load data for this Diagnose
            }
        });
    }

    @Override
    public int getItemCount() {
        return DiagnosesList.size();
    }
}
