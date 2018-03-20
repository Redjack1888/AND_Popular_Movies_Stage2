package com.example.alessandro.popularMovies1.listener;

import com.example.alessandro.popularMovies1.model.Movie;

public interface OnMoviesListFragmentListener {
    void onMoviesSelected(Movie movie);

    void onFavoriteMovieSelected(Movie movie);

    void onUpdateMoviesListVisibility();

    void onUpdateMovieDetails();
}
