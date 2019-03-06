package com.example.ramonsl.thetopmovies.Data;


import android.graphics.Bitmap;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Movie {

    public String id;
    public String vote_average;
    public String title;
    public String popularity;
    public String poster_path;
    public String overview;

    public Bitmap poster;
}

