package ishaanmalhi.com.popularmoviesapp;
import android.os.Parcel;
import android.os.Parcelable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import timber.log.Timber;

public class MovieDetail implements Parcelable {
    String id;
    String image_url;
    String title;
    String synopsis;
    String user_rating;
    String release_date;
    String backdrop_path;
    String[] reviews;
    String[] trailer_keys;
    /*
    Custom class to hold movie id and image-url so that
    a query for movie id can be sent when movie-image is clicked
     */

    public MovieDetail(String movie_id, String movie_image_uri, String title, String synopsis, String release_date, String user_rating, String backdrop) {
        this.id = movie_id;
        this.image_url = movie_image_uri;

        Date date = null;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd").parse(release_date);
        } catch (ParseException e) {
            Timber.e(e.toString());
        }
        this.release_date = new SimpleDateFormat("dd MMMM yy").format(date);
        this.user_rating = user_rating;
        this.title = title;
        this.synopsis = synopsis;
        this.backdrop_path = backdrop;
    }

    // Setters for trailer key and reviews
    public void setTrailer_keys(String[] trailer_keys) {
        if (trailer_keys.length > 0) {
            this.trailer_keys = new String[trailer_keys.length];
            System.arraycopy(trailer_keys, 0, this.trailer_keys, 0, trailer_keys.length);
        } else {
            this.trailer_keys = new String[1];
            this.trailer_keys[0] = "No Trailers Found";
        }
    }

    public void setReviews(String[] reviews) {
        if (reviews.length > 0) {
            this.reviews = new String[reviews.length];
            System.arraycopy(reviews, 0, this.reviews, 0, reviews.length);
            Timber.d("review 0 :" + this.reviews[0]);
        } else {
            this.reviews = new String[1];
            this.reviews[0] = "No Reviews Found";
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    private MovieDetail(Parcel in){
        id = in.readString();
        image_url = in.readString();
        release_date = in.readString();
        user_rating = in.readString();
        title= in.readString();
        synopsis = in.readString();
        backdrop_path = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(image_url);
        dest.writeString(release_date);
        dest.writeString(user_rating);
        dest.writeString(title);
        dest.writeString(synopsis);
        dest.writeString(backdrop_path);
    }

    public static final Parcelable.Creator<MovieDetail> CREATOR = new Parcelable.Creator<MovieDetail>(){
        @Override
        public MovieDetail createFromParcel(Parcel source) {
            return new MovieDetail(source);
        }

        @Override
        public MovieDetail[] newArray(int size) {
            return new MovieDetail[size];
        }
    };
}
