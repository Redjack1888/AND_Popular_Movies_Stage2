package com.example.alessandro.popularMovies1.userInterface.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.alessandro.popularMovies1.R;
import com.example.alessandro.popularMovies1.userInterface.MainActivity;
import com.example.alessandro.popularMovies1.userInterface.SettingsActivity;
import com.example.alessandro.popularMovies1.data.FavMoviesContract;
import com.example.alessandro.popularMovies1.listener.OnLoadingFragmentListener;
import com.example.alessandro.popularMovies1.listener.OnMoviesListFragmentListener;
import com.example.alessandro.popularMovies1.model.Movie;
import com.example.alessandro.popularMovies1.service.MoviesIntentService;
import com.example.alessandro.popularMovies1.utilities.DynamicSpanCountRecyclerView;
import com.example.alessandro.popularMovies1.utilities.Utils;

import java.util.ArrayList;
import java.util.Collections;

// Class containing a list of movies
public class FragmentMoviesList extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String STATE_LAYOUT_MANAGER = "state_recycler_view";
    private static final String STATE_MOVIES_LIST = "state_movies_list";
    private static final String SAVE_LAST_UPDATE_ORDER = "save_last_update_order";
    private static final String SAVE_LAST_SORT_ORDER = "save_last_sort_order";
    private static final int LOADER_FAVORITE_MOVIES = 1;

    private final ResponseReceiver mReceiver = new ResponseReceiver();
    private Context mContext;
    private OnMoviesListFragmentListener mOnMoviesListFragmentListener;
    private OnLoadingFragmentListener mOnLoadingFragmentListener;
    private FavMoviesRecyclerViewAdapter mFavMoviesRecyclerViewAdapter;
    private MoviesRecyclerViewAdapter mMoviesRecyclerViewAdapter;
    private ArrayList<Movie> mMoviesList;
    private String mLastUpdateOrder;
    private String mLastSortOrder;
    private DynamicSpanCountRecyclerView mRecyclerView;

    private static final String ACTION_MOVIES_REQUEST =
            "com.example.alessandro.popularMovies1.ACTION_MOVIES_REQUEST";
    private static final String ACTION_MOVIES_RESULT =
            "com.example.alessandro.popularMovies1.ACTION_MOVIES_RESULT";

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public FragmentMoviesList() {
    }

    // Create new Fragment instance
    public static FragmentMoviesList newInstance() {
        return new FragmentMoviesList();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!Utils.isFavoriteSort(mContext)) {
            if (savedInstanceState == null) {
                updateMoviesList();
            }
        }
        setHasOptionsMenu(true);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_movies_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_settings:
                mContext.startActivity(new Intent(mContext, SettingsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(SAVE_LAST_UPDATE_ORDER, mLastUpdateOrder);
        outState.putString(SAVE_LAST_SORT_ORDER, mLastSortOrder);

        if (mRecyclerView != null) {
            outState.putParcelable(STATE_LAYOUT_MANAGER, mRecyclerView.getLayoutManager()
                    .onSaveInstanceState());
        }
        if (mMoviesList != null) {
            outState.putParcelableArrayList(STATE_MOVIES_LIST, mMoviesList);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        String currentSortOrder = Utils.getSortPref(mContext);
        chooseAdapter(currentSortOrder);
        updateMoviesListVisibility(currentSortOrder);
        mLastSortOrder = currentSortOrder;

        if (!TextUtils.equals(mLastUpdateOrder, Utils.getSortPref(mContext))) {
            updateMoviesList();
        }

        if (Utils.isFavoriteSort(mContext)) {
            if (mOnLoadingFragmentListener != null) {
                mOnLoadingFragmentListener.onLoadingDisplay(false, false);
            }
        }
    }

    private void updateMoviesListVisibility(String currentSortOrder) {
        if (!TextUtils.equals(mLastSortOrder, currentSortOrder)
                || !TextUtils.equals(mLastUpdateOrder, currentSortOrder)) {
            if (mOnMoviesListFragmentListener != null) {
                mOnMoviesListFragmentListener.onUpdateMoviesListVisibility();
            }
        }
    }

    // Starts AsyncTask to fetch The Movie DB API
    public void updateMoviesList() {
        if (Utils.isInternetConnected(getActivity()) && !Utils.isFavoriteSort(mContext)) {
            String currentSortOrder = Utils.getSortPref(mContext);
            mLastUpdateOrder = currentSortOrder;
            Intent intent = new Intent(mContext, MoviesIntentService.class);
            intent.setAction(ACTION_MOVIES_REQUEST);
            intent.putExtra(MoviesIntentService.EXTRA_MOVIES_SORT, currentSortOrder);
            mContext.startService(intent);

            if (mOnLoadingFragmentListener != null) {
                mOnLoadingFragmentListener.onLoadingDisplay(false, true);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.movie_list_fragment, container, false);

        if (view instanceof DynamicSpanCountRecyclerView) {
            mRecyclerView = (DynamicSpanCountRecyclerView) view;
            mMoviesList = new ArrayList<>();

            if (savedInstanceState != null) {
                mLastUpdateOrder = savedInstanceState.getString(SAVE_LAST_UPDATE_ORDER);
                mLastSortOrder = savedInstanceState.getString(SAVE_LAST_SORT_ORDER);
                Parcelable mLayoutManager = savedInstanceState.getParcelable(STATE_LAYOUT_MANAGER);
                mMoviesList = savedInstanceState.getParcelableArrayList(STATE_MOVIES_LIST);
                mRecyclerView.getLayoutManager().onRestoreInstanceState(mLayoutManager);
            }

            chooseAdapter(Utils.getSortPref(mContext));
        }
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnMoviesListFragmentListener) {
            mOnMoviesListFragmentListener = (OnMoviesListFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnMoviesListFragmentListener");
        }

        if (context instanceof OnLoadingFragmentListener) {
            mOnLoadingFragmentListener = (OnLoadingFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnLoadingFragmentListener");
        }

        mContext = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mOnMoviesListFragmentListener = null;
        mOnLoadingFragmentListener = null;
    }


    public class ResponseReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null || intent.getAction() == null) {
                return;
            }

            if (intent.getAction().equals(ACTION_MOVIES_RESULT) && intent.hasExtra
                    (MoviesIntentService.EXTRA_MOVIES_RESULT)) {
                Movie[] movies = (Movie[]) intent.getParcelableArrayExtra(MoviesIntentService
                        .EXTRA_MOVIES_RESULT);

                if (mMoviesRecyclerViewAdapter != null && mMoviesList != null && movies != null) {
                    mMoviesRecyclerViewAdapter.clearRecyclerViewData();
                    Collections.addAll(mMoviesList, movies);
                    mMoviesRecyclerViewAdapter.notifyItemRangeInserted(0, movies.length);
                }
            } else {
                Toast.makeText(mContext, R.string.toast_failed_to_retrieve_data, Toast.LENGTH_SHORT)
                        .show();
            }

            if (mOnLoadingFragmentListener != null) {
                mOnLoadingFragmentListener.onLoadingDisplay(false, false);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(mContext)
                .registerReceiver(mReceiver, new IntentFilter(ACTION_MOVIES_RESULT));
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(mReceiver);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case LOADER_FAVORITE_MOVIES:
                return new CursorLoader(mContext, FavMoviesContract.MoviesEntry
                        .CONTENT_URI,
                        null, null, null, null);
            default:
                Log.d(LOG_TAG, "Couldn't find loader");
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (mFavMoviesRecyclerViewAdapter != null) {
            mFavMoviesRecyclerViewAdapter.swapCursor(data);
            if (data != null && data.getCount() <= 0) {
                Toast.makeText(mContext, R.string.favorites_empty, Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (mFavMoviesRecyclerViewAdapter != null) {
            mFavMoviesRecyclerViewAdapter.swapCursor(null);
        }
    }

    private void chooseAdapter(String currentSortOrder) {

        if (!TextUtils.equals(mLastUpdateOrder, currentSortOrder) && mMoviesRecyclerViewAdapter
                != null) {
            mMoviesRecyclerViewAdapter.clearRecyclerViewData();
        }

        RecyclerView.Adapter currentAdapter = mRecyclerView.getAdapter();

        if (Utils.isFavoriteSort(mContext, currentSortOrder)
                && !(currentAdapter instanceof FavMoviesRecyclerViewAdapter)) {
            mLastUpdateOrder = currentSortOrder;
            mFavMoviesRecyclerViewAdapter = new FavMoviesRecyclerViewAdapter
                    (mOnMoviesListFragmentListener);
            mRecyclerView.setAdapter(mFavMoviesRecyclerViewAdapter);

            getLoaderManager().initLoader(LOADER_FAVORITE_MOVIES, null, this);


        } else if (!Utils.isFavoriteSort(mContext, currentSortOrder)
                && !(currentAdapter instanceof MoviesRecyclerViewAdapter)) {

            mMoviesRecyclerViewAdapter = new MoviesRecyclerViewAdapter(mMoviesList,
                    mOnMoviesListFragmentListener);
            mRecyclerView.setAdapter(mMoviesRecyclerViewAdapter);

        }

        // Instantiate an empty Details Fragment if sort order has changed
        if (!TextUtils.equals(mLastSortOrder, currentSortOrder)
                && mOnMoviesListFragmentListener != null) {
            mOnMoviesListFragmentListener.onUpdateMovieDetails();
        }
    }
}