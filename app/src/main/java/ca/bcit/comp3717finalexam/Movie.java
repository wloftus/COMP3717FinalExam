package ca.bcit.comp3717finalexam;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * A movie object. Contains the movie's title, the movie's description, and a link to the movie.
 */
@IgnoreExtraProperties
public class Movie {

    public String title;
    public String description;
    public String link;

    public Movie() {
        // Default constructor required for calls to snapshot.getValue(Movie.class)
    }

    public Movie(String title, String description, String link) {
        this.title = title;
        this.description = description;
        this.link = link;
    }
}
