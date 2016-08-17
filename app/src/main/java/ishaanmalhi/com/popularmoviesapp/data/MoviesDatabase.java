package ishaanmalhi.com.popularmoviesapp.data;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.Table;

/**
 * Created by ishaan on 13/8/16.
 */
@Database(version = MoviesDatabase.VERSION)
public final class MoviesDatabase {
    private MoviesDatabase(){}

    public static final int VERSION = 1;

    @Table(MovieColumns.class) public static final String FAVORITES = "favorites";
    @Table(ReviewColumns.class) public static final String REVIEWS = "reviews";
    @Table(TrailerColumns.class) public static final String TRAILERS = "trailers";
}
