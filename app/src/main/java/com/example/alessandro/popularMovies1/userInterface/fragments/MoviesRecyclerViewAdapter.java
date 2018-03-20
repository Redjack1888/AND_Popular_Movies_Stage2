package com.example.alessandro.popularMovies1.userInterface.fragments;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.alessandro.popularMovies1.R;
import com.example.alessandro.popularMovies1.listener.OnMoviesListFragmentListener;
import com.example.alessandro.popularMovies1.model.Movie;

import java.util.List;

// Class that manages the list of movies (RecyclerView)
public class MoviesRecyclerViewAdapter extends RecyclerView.Adapter<MoviesRecyclerViewAdapter
        .ViewHolder> {

    private final List<Movie> mMoviesList;
    private final OnMoviesListFragmentListener mListener;

    MoviesRecyclerViewAdapter(List<Movie> moviesList, OnMoviesListFragmentListener
            listener) {
        mMoviesList = moviesList;
        mListener = listener;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_item_fragment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mMoviesList.get(position);

        Glide.with(holder.mPosterView.getContext()).load(holder.mItem.getPosterUri())
                .dontTransform().diskCacheStrategy(DiskCacheStrategy.ALL)
                .dontAnimate().into(holder.mPosterView);

        holder.mTitle.setText(holder.mItem.getTitle());
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onMoviesSelected(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mMoviesList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        Movie mItem;

        final View mView;
        final ImageView mPosterView;
        final TextView mTitle;

        ViewHolder(View view) {
            super(view);
            mView = view;
            mPosterView = (ImageView) view.findViewById(R.id.poster);
            mTitle = (TextView) view.findViewById(R.id.title);
        }

    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        Glide.clear(holder.mPosterView);
    }

    // Method implementation based on "Remove all items from RecyclerView" by
    // StackOverflow - short url: https://goo.gl/qyv3Xq
    // It resets the list and notifies the adapter
    void clearRecyclerViewData() {
        int size = mMoviesList.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                mMoviesList.remove(0);
            }
            notifyItemRangeRemoved(0, size);
        }
    }

}
