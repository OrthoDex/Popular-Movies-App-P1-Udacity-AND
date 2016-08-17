package ishaanmalhi.com.popularmoviesapp.data;

import net.simonvt.schematic.annotation.*;

/**
 * Created by ishaan on 13/8/16.
 */
public interface MovieColumns {
    @DataType(DataType.Type.INTEGER) @PrimaryKey @AutoIncrement String _ID = "_id";
    @DataType(DataType.Type.TEXT) @NotNull String MOVIE_ID = "movie_id";
    @DataType(DataType.Type.TEXT) @NotNull String MOVIE_POSTER_URL = "image_url";
    @DataType(DataType.Type.TEXT) @NotNull String TITLE = "title";
    @DataType(DataType.Type.TEXT) @NotNull String SYNOPSIS =  "synopsis";
    @DataType(DataType.Type.TEXT) @NotNull String USER_RATING = "user_rating";
    @DataType(DataType.Type.TEXT) @NotNull String RELEASE_DATE = "release_date";
    @DataType(DataType.Type.TEXT) @NotNull String BACKDROP_PATH = "backdrop_path";
}


