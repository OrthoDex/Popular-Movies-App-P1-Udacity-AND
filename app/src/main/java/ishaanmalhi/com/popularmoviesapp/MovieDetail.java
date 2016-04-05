package ishaanmalhi.com.popularmoviesapp;
import android.os.Parcel;
import android.os.Parcelable;

public class MovieDetail implements Parcelable {
    String id;
    String image_url;
    String title;
    String synopsis;
    String user_rating;
    String release_date;
    String backdrop_path;//For Later implementation
    /*
    Custom class to hold movie id and image-url so that
    a query for movie id can be sent when movie-image is clicked
     */

    public MovieDetail(String movie_id, String movie_image_uri, String title, String synopsis, String release_date, String user_rating, String backdrop) {
        this.id = movie_id;
        this.image_url = movie_image_uri;
        this.release_date = release_date;
        this.user_rating = user_rating;
        this.title = title;
        this.synopsis = synopsis;
        this.backdrop_path = backdrop;
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
