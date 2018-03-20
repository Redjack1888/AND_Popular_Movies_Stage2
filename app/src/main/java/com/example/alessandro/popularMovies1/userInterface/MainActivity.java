package com.example.alessandro.popularMovies1.userInterface;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.example.alessandro.popularMovies1.R;
import com.example.alessandro.popularMovies1.userInterface.fragments.FragmentDetails;
import com.example.alessandro.popularMovies1.userInterface.fragments.FragmentLoading;
import com.example.alessandro.popularMovies1.userInterface.fragments.FragmentMoviesList;
import com.example.alessandro.popularMovies1.userInterface.fragments.OfflineFragment;
import com.example.alessandro.popularMovies1.utilities.Utils;
import com.example.alessandro.popularMovies1.data.FavMoviesContract;
import com.example.alessandro.popularMovies1.listener.OnMoviesListFragmentListener;
import com.example.alessandro.popularMovies1.listener.OnOfflineFragmentListener;
import com.example.alessandro.popularMovies1.listener.OnLoadingFragmentListener;
import com.example.alessandro.popularMovies1.model.Movie;

// Class that can host MoviesListFragment, NoInternetFragment and LoadingFragment
public class MainActivity extends AppCompatActivity implements
        OnMoviesListFragmentListener,
        OnLoadingFragmentListener,
        OnOfflineFragmentListener {

    private boolean mIsTwoPane;
    private View mMoviesFragmentContainer;
    private View mDetailsFragmentContainer;
    private View mOfflineFragmentContainer;
    private static final String EXTRA_MOVIE = "intent_extra_movie";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupActionBar();

        mMoviesFragmentContainer = findViewById(R.id.movies_fragment_container);
        mDetailsFragmentContainer = findViewById(R.id.details_fragment_container);
        mOfflineFragmentContainer = findViewById(R.id.no_internet_container);

        mIsTwoPane = mDetailsFragmentContainer != null;

        if (savedInstanceState == null) {

            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            FragmentMoviesList fragmentMoviesList = FragmentMoviesList.newInstance();
            OfflineFragment offlineFragment = OfflineFragment.newInstance();
            fragmentTransaction.add(R.id.movies_fragment_container, fragmentMoviesList)
                    .add(R.id.no_internet_container, offlineFragment);

            if (mIsTwoPane) {
                FragmentDetails detailFragment = FragmentDetails.newInstance();
                fragmentTransaction.add(R.id.details_fragment_container, detailFragment);
            }

            fragmentTransaction.commit();
        }
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the App icon in the action bar.
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setIcon(R.mipmap.ic_launcher);
        }
    }

    // Change visibility of fragment according to current internet connection state
    private void changeNoInternetVisibility(boolean isInternetConnected) {

        if (isInternetConnected || Utils.isFavoriteSort(this)) {
            mOfflineFragmentContainer.setVisibility(View.GONE);
            mMoviesFragmentContainer.setVisibility(View.VISIBLE);

            if (mIsTwoPane) {
                mDetailsFragmentContainer.setVisibility(View.VISIBLE);
            }
        } else {
            mOfflineFragmentContainer.setVisibility(View.VISIBLE);
            mMoviesFragmentContainer.setVisibility(View.GONE);

            if (mIsTwoPane) {
                mDetailsFragmentContainer.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onRetry() {
        boolean isInternetConnected = Utils.isInternetConnected(this);

        changeNoInternetVisibility(Utils.isInternetConnected(this));

        if (!isInternetConnected) {
            Toast.makeText(this, R.string.toast_no_internet_connection, Toast.LENGTH_SHORT).show();

        } else {
            FragmentMoviesList fragmentMoviesList = (FragmentMoviesList)
                    getSupportFragmentManager().findFragmentById(R.id.movies_fragment_container);
            fragmentMoviesList.updateMoviesList();
        }
    }

    @Override
    public void onMoviesSelected(Movie movieItem) {
        if (movieItem != null) {
            if (!Utils.isInternetConnected(this)) {
                Toast.makeText(this, R.string.toast_check_your_internet_connection,
                        Toast.LENGTH_SHORT).show();
                return;
            }

            if (mIsTwoPane) {
                // In two-pane mode, show the detail view in this activity by
                // adding or replacing the detail fragment using a fragment transaction.

                FragmentDetails detailsFragment = FragmentDetails.newInstance(movieItem);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.details_fragment_container, detailsFragment).commit();
            } else {
                Intent intent = new Intent(this, DetailsActivity.class).putExtra(EXTRA_MOVIE,
                        movieItem);
                startActivity(intent);
            }
        }
    }

    @Override
    public void onFavoriteMovieSelected(Movie movieItem) {
        if (movieItem != null) {
            int movieID = Integer.parseInt(movieItem.getId());

            // Videos query
            Cursor videosCursor = getContentResolver().query(FavMoviesContract.VideosEntry
                            .CONTENT_URI, null,
                    FavMoviesContract.VideosEntry.COLUMN_MOVIE_ID + " = " + movieID, null,
                    null);
            if (videosCursor != null) {
                try {
                    movieItem.setVideos(Utils.createVideosFromCursor(videosCursor));
                } finally {
                    videosCursor.close();
                }
            }

            // Reviews query
            Cursor reviewsCursor = getContentResolver().query(FavMoviesContract.ReviewsEntry
                            .CONTENT_URI, null,
                    FavMoviesContract.ReviewsEntry.COLUMN_MOVIE_ID + " = " + movieID, null,
                    null);
            if (reviewsCursor != null) {
                try {
                    movieItem.setReviews(Utils.createReviewsFromCursor(reviewsCursor));
                } finally {
                    reviewsCursor.close();
                }
            }

            if (mIsTwoPane) {
                // In two-pane mode, show the detail view in this activity by
                // adding or replacing the detail fragment using an fragment transaction.

                FragmentDetails detailsFragment = FragmentDetails.newInstance(movieItem);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.details_fragment_container, detailsFragment).commit();
            } else {
                Intent intent = new Intent(this, DetailsActivity.class).putExtra(EXTRA_MOVIE, movieItem);
                startActivity(intent);
            }
        }
    }

    @Override
    public void onUpdateMoviesListVisibility() {
        changeNoInternetVisibility(Utils.isInternetConnected(this));
    }

    @Override
    public void onUpdateMovieDetails() {
        if (mIsTwoPane) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            FragmentDetails detailFragment = FragmentDetails.newInstance();
            fragmentTransaction.replace(R.id.details_fragment_container, detailFragment);
            fragmentTransaction.commit();
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
