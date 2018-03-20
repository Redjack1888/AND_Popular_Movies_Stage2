package com.example.alessandro.popularMovies1.userInterface.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.alessandro.popularMovies1.R;

/**
 * Fragment that displays a custom loading spinner.
 * Background is transparent so it can be used over another Fragment.
 */
public class FragmentLoading extends Fragment {

    public static final String FRAGMENT_TAG = FragmentLoading.class.getSimpleName();

    public FragmentLoading() {
        // Required empty public constructor
    }

    public static FragmentLoading newInstance() {
        return new FragmentLoading();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.loading_fragment, container, false);
    }
}
