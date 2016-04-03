package ishaanmalhi.com.popularmoviesapp;

public class MovieImage {
    String id;
    String image_url;

    /*
    Custom class to hold movie id and image-url so that
    a query for movie id can be sent when movie-image is clicked
     */

    public MovieImage(String id, String url) {
        this.id = id;
        this.image_url = url;
    }
}
