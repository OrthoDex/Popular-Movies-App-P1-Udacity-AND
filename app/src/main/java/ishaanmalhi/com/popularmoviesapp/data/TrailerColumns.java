package ishaanmalhi.com.popularmoviesapp.data;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;
import net.simonvt.schematic.annotation.References;

/**
 * Created by ishaan on 16/8/16.
 */
public interface TrailerColumns {

    @DataType(DataType.Type.INTEGER) @PrimaryKey @AutoIncrement String _ID = "_id";
    @DataType(DataType.Type.TEXT) @NotNull String KEYS = "keys";
    @DataType(DataType.Type.INTEGER) @NotNull @References(table = MoviesDatabase.FAVORITES, column = MovieColumns.MOVIE_ID) String MOVIE_ID = "movie_id";
}
