# Popular Movies (Stage 2)

This is Popular Movies Stage 2 of Udacity's Android Developer Nanodegree.
The purpose of this project was to built an app that helps users discover popular and top rated movies on the web.
It fetches themoviedb.org API to display the movies data, that way the content provided is always up-to-date and relevant.

For Popular Movies Stage 1 project click [HERE](https://github.com/Redjack1888/AND_Popular_Movies_Stage1)

## Stage 2 Features

This is the last and final part of Popular Movies Project.
It contains the following features:

- Upon launch, it presents users with an grid arrangement of movie posters and titles;
- Selecting a movie from the list displays more information about it, such as: original title, plot synopsis, user rating, release date, trailers and user reviews;
- Users can mark movies as favorites. All favorite movies are stored in the application private database for offline view.
- Users can share movies trailers;
- Configurable movies sort order via settings (popular, top rated and favorites);
- Optimized for both phones and tablets.


## Instructions

You need to create a free account on themoviedb.org and generate your personal API key. More info [HERE](https://www.themoviedb.org/documentation/api).

I have used info by this article [Best Way to Store your Api Keys for your Android Studio Project](https://technobells.com/best-way-to-store-your-api-keys-for-your-android-studio-project-e4b5e8bb7d23) to store my Apikey, so in your gradle.properties file, put your generated API Key like this: `TheMovieDbApiKey="COPY YOUR API KEY HERE"`

## Libraries

This project uses the following libraries:

[Glide](https://github.com/bumptech/glide)
