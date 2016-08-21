package ishaanmalhi.com.popularmoviesapp.data;

import android.net.Uri;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.InexactContentUri;
import net.simonvt.schematic.annotation.MapColumns;
import net.simonvt.schematic.annotation.TableEndpoint;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ishaan on 13/8/16.
 */
@ContentProvider(authority = MoviesProvider.AUTHORITY, database = MoviesDatabase.class)
public final class MoviesProvider {

    public static final String AUTHORITY = "ishaanmalhi.com.popularmoviesapp.data.MoviesProvider";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final String FAVORITES = "favorites";
    public static final String REVIEWS = "reviews";
    public static final String TRAILERS = "trailers";
    public static final String FROM_REVIEWS = "fromReview";
    public static final String FROM_TRAILERS = "fromTrailer";

    @TableEndpoint(table = MoviesDatabase.FAVORITES)
    public static class Favorites {

        @ContentUri(
                path = FAVORITES,
                type = "vnd.android.cursor.dir/favorite",
                defaultSort = MovieColumns._ID + " ASC")
        public static final Uri FAVORITES_URI = buildUri(FAVORITES);

        @InexactContentUri(
                name = "MOVIE_ID",
                path = FAVORITES + "/#",
                type = "vnd.android.cursor.item/favorite",
                whereColumn = MovieColumns._ID,
                pathSegment = 1)
        public static Uri withId(long id) {
            return buildUri(FAVORITES, String.valueOf(id));
        }
    }

    @TableEndpoint(table = MoviesDatabase.REVIEWS)
    public static class Reviews {

        @ContentUri(
                path = REVIEWS,
                type = "vnd.android.cursor.dir/review",
                defaultSort = ReviewColumns._ID + " ASC")
        public static final Uri REVIEW_URI = buildUri(REVIEWS);


        @ContentUri(
                path = FROM_REVIEWS,
                type = "vnd.android.cursor.dir/review_join",
                join = "INNER JOIN "
                        + MoviesDatabase.FAVORITES
                        + " ON "
                        + MoviesDatabase.FAVORITES + "." + MovieColumns.MOVIE_ID
                        + "="
                        + MoviesDatabase.REVIEWS + "." + ReviewColumns.MOVIE_ID)
        public static final Uri fromReview = buildUri(FROM_REVIEWS);
    }

    @TableEndpoint(table = MoviesDatabase.TRAILERS)
    public static class Trailers {

        @ContentUri(
                path = TRAILERS,
                type = "vnd.android.cursor.dir/trailer",
                defaultSort = TrailerColumns._ID + " ASC")
        public static final Uri TRAILER_URI = buildUri(TRAILERS);

        @ContentUri(
                path = FROM_TRAILERS,
                type = "vnd.android.cursor.dir/trailer_join",
                join = "INNER JOIN "
                        + MoviesDatabase.FAVORITES
                        + " ON "
                        + MoviesDatabase.FAVORITES + "." + MovieColumns.MOVIE_ID
                        + "="
                        + MoviesDatabase.TRAILERS + "." + TrailerColumns.MOVIE_ID)
        public static final Uri fromTrailer = buildUri(FROM_TRAILERS);

    }

    private static Uri buildUri(String ... paths){
        Uri.Builder builder = BASE_CONTENT_URI.buildUpon();
        for (String path : paths){
            builder.appendPath(path);
        }
        return builder.build();
    }
}