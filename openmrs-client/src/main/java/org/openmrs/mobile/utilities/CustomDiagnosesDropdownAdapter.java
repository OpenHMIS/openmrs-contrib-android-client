package org.openmrs.mobile.utilities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.openmrs.mobile.R;
import org.openmrs.mobile.models.Concept;

import java.util.List;

public class CustomDiagnosesDropdownAdapter extends ArrayAdapter<Concept> {
	public CustomDiagnosesDropdownAdapter(Context context, int resource, List<Concept> conceptSearchResult) {
		super(context, resource, conceptSearchResult);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Concept conceptSearchResult = getItem(position);
		LayoutInflater inflater = LayoutInflater.from(getContext());
		View item = inflater.inflate(R.layout.custom_dropdown_item, parent, false);
		TextView label = (TextView)item.findViewById(R.id.textView);
		label.setText(conceptSearchResult.toString());
		return item;
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		return getView(position, convertView, parent);
	}
}