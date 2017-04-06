package org.openmrs.mobile.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.patientdashboard.PatientDashboardActivity;
import org.openmrs.mobile.sampledata.Patient;
import org.openmrs.mobile.utilities.ApplicationConstants;

import java.util.List;


public class ListPatients extends RecyclerView.Adapter<ListPatients.PatientViewHolder> {

    public static class PatientViewHolder extends RecyclerView.ViewHolder {


        private TextView given_name;
        private TextView middle_name;
        private TextView family_name;
        private TextView age;
        private TextView id;
        private ImageView gender;
        private ImageView active_visit;

        public PatientViewHolder(View itemView) {
            super(itemView);
            given_name = (TextView) itemView.findViewById(R.id.given_name);
            middle_name = (TextView) itemView.findViewById(R.id.middle_name);
            family_name = (TextView) itemView.findViewById(R.id.family_name);
            age = (TextView) itemView.findViewById(R.id.age);
            id = (TextView) itemView.findViewById(R.id.patient_id);
            gender = (ImageView) itemView.findViewById(R.id.genderIcon);
            active_visit = (ImageView) itemView.findViewById(R.id.activeVisitIcon);
        }
    }

    private List<Patient> patients;
    private Context context;

    public ListPatients(Context context, List<Patient> patients) {
        this.context = context;
        this.patients = patients;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public PatientViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_patients, viewGroup, false);
        return new PatientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final PatientViewHolder holder, int position) {
        holder.given_name.setText(patients.get(holder.getAdapterPosition()).given_name);
        holder.middle_name.setText(patients.get(holder.getAdapterPosition()).middle_name);
        holder.family_name.setText(patients.get(holder.getAdapterPosition()).family_name);
        holder.age.setText(String.valueOf(patients.get(holder.getAdapterPosition()).age));
        holder.id.setText(patients.get(holder.getAdapterPosition()).id);
        holder.gender.setImageResource(String.valueOf(patients.get(holder.getAdapterPosition()).gender).toLowerCase().equals("m") ? R.drawable.ic_male : R.drawable.ic_female);
        if (patients.get(holder.getAdapterPosition()).active_visit == 1) {
            holder.active_visit.setVisibility(View.VISIBLE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PatientDashboardActivity.class);
                //intent.putExtra(ApplicationConstants.Tags.PATIENT_ID, patients.get(position));
                intent.putExtra(ApplicationConstants.Tags.PATIENT_ID, patients.get(holder.getAdapterPosition()).id);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return patients.size();
    }
}
