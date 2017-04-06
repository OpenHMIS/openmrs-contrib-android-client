package org.openmrs.mobile.fragments;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.patientdashboard.TabsPageView;


public class VisitTasks extends Fragment {

    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_visit_tasks, container, false);
        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();

        return view;
    }

    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_tab_diagnoses);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_tab_vitals);
    }

    private void setupViewPager(ViewPager viewPager) {
        TabsPageView adapter = new TabsPageView(getActivity().getSupportFragmentManager());

        Bundle bundle = new Bundle();
        //bundle.putSerializable(ApplicationConstants.Tags.PATIENT_DATA, patient);

        Fragment tasksToDo = new TasksTodo();
        tasksToDo.setArguments(bundle);
        adapter.addFragment( tasksToDo, getResources().getString(R.string.label_tasks));

        Fragment tasksComplete = new TasksComplete();
        tasksComplete.setArguments(bundle);
        adapter.addFragment(tasksComplete, getResources().getString(R.string.label_complete));

        viewPager.setAdapter(adapter);
    }
}