package com.example.alessandro.popularMovies1.userInterface;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.alessandro.popularMovies1.R;
import com.example.alessandro.popularMovies1.userInterface.fragments.FragmentDetails;
import com.example.alessandro.popularMovies1.userInterface.fragments.FragmentLoading;
import com.example.alessandro.popularMovies1.listener.OnLoadingFragmentListener;
import com.example.alessandro.popularMovies1.model.Movie;

// Activity that hosts DetailsFragment and LoadingFragment
public class DetailsActivity extends AppCompatActivity implements OnLoadingFragmentListener {

    private static final String LOG_TAG = DetailsActivity.class.getSimpleName();
    private static final String EXTRA_MOVIE = "intent_extra_movie";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        if (savedInstanceState == null) {
            Intent intent = getIntent();
            if (intent != null && intent.hasExtra(EXTRA_MOVIE)) {
                Movie movie = intent
                        .getParcelableExtra(EXTRA_MOVIE);

                FragmentDetails fragmentDetails = FragmentDetails.newInstance(movie);
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.details_fragment_container, fragmentDetails).commit();
            } else {
                Log.d(LOG_TAG, "Something went wrong. Intent doesn't have Constants.EXTRA_MOVIE" +
                        " extra. Finishing DetailsActivity.");
                finish();
            }
        }
    }

    @Override
    public void onLoadingDisplay(boolean fromDetails, boolean display) {
        Fragment loadingFragment = getSupportFragmentManager()
                .findFragmentByTag(FragmentLoading.FRAGMENT_TAG);
        if (display && loadingFragment == null) {
            loadingFragment = FragmentLoading.newInstance();
            if (fromDetails) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.details_fragment_container,
                                loadingFragment, FragmentLoading.FRAGMENT_TAG).commit();
            } else {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.movies_fragment_container,
                                loadingFragment, FragmentLoading.FRAGMENT_TAG).commit();
            }
        } else if (!display && loadingFragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .remove(loadingFragment).commit();
        }
    }
}
