package ishaanmalhi.com.popularmoviesapp;

import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import ishaanmalhi.com.popularmoviesapp.data.MoviesProvider;
import timber.log.Timber;

/**
 * Created by ishaan on 15/8/16.
 */
public class FavoriteMovieFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private MovieCursorAdapter mCursorAdapter;
    private String sort_order;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        sort_order = Utility.getSortOrder(getActivity());
        super.onCreate(savedInstanceState);
    }

    private final String FAVORITE = "favorite";

    private static final int CURSOR_LOADER_ID = 0;

    static final int COL_MOVIE_ID = 0;
    static final int COL_POSTER_URL = 2;

    public interface Callback {
        // DetailFragment Callback when item is selected
        void onItemSelected(Uri iduri);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_movie, container, false);
        GridView gridView = (GridView) rootView.findViewById(R.id.movies_grid);

        mCursorAdapter = new MovieCursorAdapter(getActivity(), null, 0);

        if (sort_order.equals(FAVORITE)) {
            Timber.d("Adding CursorAdapter");
            gridView.setAdapter(mCursorAdapter);
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                    if (cursor != null) {
                        ((Callback) getActivity())
                                .onItemSelected(MoviesProvider.Favorites.withId(cursor.getLong(COL_MOVIE_ID)));
                    }
                }
            });
        }
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(CURSOR_LOADER_ID, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(),
                MoviesProvider.Favorites.FAVORITES_URI,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }
}
