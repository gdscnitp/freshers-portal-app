package com.dscnitp.freshersportal.Alumni;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dscnitp.freshersportal.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class AlumniProfileFragment extends Fragment {


    public AlumniProfileFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_alumni_profile, container, false);

        return view;
    }

}
