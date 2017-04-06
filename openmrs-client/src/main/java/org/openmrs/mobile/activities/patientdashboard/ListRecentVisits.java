package org.openmrs.mobile.activities.patientdashboard;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.openmrs.mobile.R;
import org.openmrs.mobile.fragments.PatientDetails;
import org.openmrs.mobile.sampledata.Visit;

import java.util.List;


public class ListRecentVisits extends RecyclerView.Adapter<ListRecentVisits.RecentVisitViewHolder> {

    public static class RecentVisitViewHolder extends RecyclerView.ViewHolder {

        public TextView visit_date;
        public TextView visit_tag;

        public RecentVisitViewHolder(View itemView) {
            super(itemView);
            visit_date = (TextView) itemView.findViewById(R.id.visit_date);
            visit_tag = (TextView) itemView.findViewById(R.id.visit_tag);

        }
    }

    private List<Visit> visitList;
    private Activity activity;

    public ListRecentVisits(Activity activity, List<Visit> visitList) {
        this.activity = activity;
        this.visitList = visitList;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public RecentVisitViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_recent_visits, viewGroup, false);
        return new RecentVisitViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecentVisitViewHolder holder, final int position) {
        String str = visitList.get(position).start_date;
        if (!visitList.get(position).end_date.equals("")) {
            str += " - " + visitList.get(position).end_date;
        }
        holder.visit_date.setText(str);
        holder.visit_tag.setText((visitList.get(position).is_active == 1 ? activity.getResources().getString(R.string.label_active) + " - " : "") + "" + visitList.get(position).visit_type);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Fragment fragment = PatientDetails.class.newInstance();
                    ///dataPasser.onPatientDataPass(patients.get(position));
                    Bundle bundle = new Bundle();
                    //bundle.putSerializable(ApplicationConstants.Tags.PATIENT_ID, patients.get(position));
                    fragment.setArguments(bundle);
                    ((FragmentActivity) activity).getSupportFragmentManager().beginTransaction().setCustomAnimations(
                            R.anim.slide_in_left, R.anim.slide_out_right,
                            R.anim.pop_enter, R.anim.pop_exit).replace(R.id.fragment_container, fragment).addToBackStack(null).commit();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return visitList.size();
    }
}
