package com.lunchdash.lunchdash.fragments;

import android.app.DialogFragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.lunchdash.lunchdash.R;

public class FilterDialog extends DialogFragment {
    private Spinner spinSortBy;
    private Spinner spinMaxDistance;

    public FilterDialog() {
    }

    public static FilterDialog newInstance() {
        return new FilterDialog();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE); //Get rid of extra white space at the top of the dialog
        View v = inflater.inflate(R.layout.fragment_filter_options, container);
        spinSortBy = (Spinner) v.findViewById(R.id.spinSortBy);
        spinMaxDistance = (Spinner) v.findViewById(R.id.spinMaxDistance);
        Button btnOk = (Button) v.findViewById(R.id.btnOk);
        Button btnCancel = (Button) v.findViewById(R.id.btnCancel);

        ArrayAdapter<CharSequence> spinSortByAdapter = ArrayAdapter.createFromResource(this.getActivity(), R.array.spinnerSortBy, android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> spinMaxDistanceAdapter = ArrayAdapter.createFromResource(this.getActivity(), R.array.spinnerMaxDistance, android.R.layout.simple_spinner_item);

        spinSortBy.setAdapter(spinSortByAdapter);
        spinMaxDistance.setAdapter(spinMaxDistanceAdapter);

        //Restore values if any.
        SharedPreferences filters = getActivity().getSharedPreferences("settings", 0);
        String sortBy = filters.getString("sortBy", "Best Match");
        String maxDistance = filters.getString("maxDistance", "Best Match");

        setSpinnerValue(spinSortBy, sortBy);
        setSpinnerValue(spinMaxDistance, maxDistance);

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences settings = getActivity().getSharedPreferences("settings", 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("sortBy", spinSortBy.getSelectedItem().toString());
                editor.putString("maxDistance", spinMaxDistance.getSelectedItem().toString());
                editor.commit();
                dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return v;
    }

    //Sets the spinner value by iterating through the spinner items until one matches the string.
    public void setSpinnerValue(Spinner spinner, String value) {
        int index = 0;
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).equals(value)) {
                index = i;
                break;
            }
        }
        spinner.setSelection(index);
    }
}
