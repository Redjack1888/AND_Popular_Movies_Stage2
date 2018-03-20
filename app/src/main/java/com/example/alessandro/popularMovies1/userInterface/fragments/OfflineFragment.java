package com.example.alessandro.popularMovies1.userInterface.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.alessandro.popularMovies1.R;
import com.example.alessandro.popularMovies1.listener.OnOfflineFragmentListener;

// Fragment displayed when internet connection is not available. It contains a text and a retry
// button.
public class OfflineFragment extends Fragment {

    private OnOfflineFragmentListener mOnOfflineFragmentListener;

    public OfflineFragment() {
        // Required empty public constructor
    }

    public static OfflineFragment newInstance() {
        return new OfflineFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.offline_fragment, container, false);
        Button retryButton = (Button) view.findViewById(R.id.retry_button);
        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onRetryButtonPressed();
            }
        });
        return view;
    }

    // Calls listener implemented by host Activity
    private void onRetryButtonPressed() {
        if (mOnOfflineFragmentListener != null) {
            mOnOfflineFragmentListener.onRetry();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnOfflineFragmentListener) {
            mOnOfflineFragmentListener = (OnOfflineFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mOnOfflineFragmentListener = null;
    }

}
