package com.example.alessandro.popularMovies1.userInterface.fragments;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.alessandro.popularMovies1.R;
import com.example.alessandro.popularMovies1.data.FavMoviesContract;
import com.example.alessandro.popularMovies1.listener.OnMoviesListFragmentListener;
import com.example.alessandro.popularMovies1.model.Movie;
import com.example.alessandro.popularMovies1.utilities.Utils;

/** Class that manages the list of favorite movies (RecyclerView).
* Implementation based on BLOGC.AT - https://goo.gl/aGQiFq
*/
public class FavMoviesRecyclerViewAdapter extends RecyclerView
        .Adapter<FavMoviesRecyclerViewAdapter.ViewHolder> {


    private final OnMoviesListFragmentListener mOnMoviesListFragmentListener;
    private Cursor mCursor;

    @Override
    public final void onBindViewHolder(final ViewHolder holder, final int position) {
        final Cursor cursor = getItem(position);
        onBindViewHolder(holder, cursor);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_item_fragment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return mCursor != null ? mCursor.getCount() : 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        final View mView;
        final ImageView mPosterView;
        final TextView mTitle;

        int mCursorPosition;

        ViewHolder(View view) {
            super(view);
            mView = view;
            mPosterView = (ImageView) view.findViewById(R.id.poster);
            mTitle = (TextView) view.findViewById(R.id.title);
            mCursorPosition = -1;
        }
    }

    private Cursor getItem(final int position) {
        if (mCursor != null && !mCursor.isClosed()) {
            mCursor.moveToPosition(position);
        }

        return mCursor;
    }

    FavMoviesRecyclerViewAdapter(
            OnMoviesListFragmentListener listener) {
        mOnMoviesListFragmentListener = listener;
    }

    void swapCursor(Cursor cursor) {
        if (mCursor != null) {
            mCursor.close();
        }
        mCursor = cursor;
        notifyDataSetChanged();
    }

    private void onBindViewHolder(final ViewHolder holder, final Cursor cursor) {
        String movieTitle = cursor.getString(cursor.getColumnIndex(FavMoviesContract
                .MoviesEntry.COLUMN_TITLE));
        String posterUri = cursor.getString(cursor.getColumnIndex(FavMoviesContract
                .MoviesEntry.COLUMN_PORTER_URI));

        holder.mCursorPosition = cursor.getPosition();
        holder.mTitle.setText(movieTitle);

        Glide.with(holder.mPosterView.getContext()).load(posterUri)
                .dontTransform().diskCacheStrategy(DiskCacheStrategy.ALL)
                .dontAnimate().into(holder.mPosterView);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnMoviesListFragmentListener != null) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    cursor.moveToPosition(holder.mCursorPosition);
                    Movie movie = Utils.createMovieFromCursor(cursor);
                    mOnMoviesListFragmentListener.onFavoriteMovieSelected(movie);
                }
            }
        });
    }
}
