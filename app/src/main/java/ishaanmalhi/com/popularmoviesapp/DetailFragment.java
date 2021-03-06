package ishaanmalhi.com.popularmoviesapp;

import android.content.ContentProviderOperation;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gc.materialdesign.views.ButtonRectangle;
import com.github.aakira.expandablelayout.ExpandableLinearLayout;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import ishaanmalhi.com.popularmoviesapp.data.MovieColumns;
import ishaanmalhi.com.popularmoviesapp.data.MoviesDatabase;
import ishaanmalhi.com.popularmoviesapp.data.MoviesProvider;
import ishaanmalhi.com.popularmoviesapp.data.ReviewColumns;
import ishaanmalhi.com.popularmoviesapp.data.TrailerColumns;
import ishaanmalhi.com.popularmoviesapp.views.LikeButtonView;
import timber.log.Timber;

public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String MOVIE_SHARE_STRING = "Hey! I just found an amazing movie, check it out!: ";
    private static final String FAVORITE = "favorite";
    private static final String[] DETAIL_COLUMNS = {
            MovieColumns._ID,
            MovieColumns.MOVIE_ID,
            MovieColumns.MOVIE_POSTER_URL,
            MovieColumns.TITLE,
            MovieColumns.SYNOPSIS,
            MovieColumns.USER_RATING,
            MovieColumns.RELEASE_DATE,
            MovieColumns.BACKDROP_PATH,
    };

    private static final int COL_MOVIE_ID = 0;
    private static final int COL_TMDB_ID = 1;
    private static final int COL_POSTER_URL = 2;
    private static final int COL_TITLE = 3;
    private static final int COL_SYNOPSIS = 4;
    private static final int COL_USER_RATING = 5;
    private static final int COL_RELEASE_DATE = 6;
    private static final int COL_BACKDROP_PATH = 7;

    private static final int COL_CONTENT = 0;
    private static final int COL_TRAILER_KEY = 0;

    private MovieDetail movie;
    private Uri mUri;
    static final String DETAIL_URI = "URI";
    private static final int DETAIL_LOADER = 0;
    private ShareActionProvider mShareActionProvider;


    public class MovieDetailsView {
        @BindView(R.id.poster_image)
        ImageView poster;
        @BindView(R.id.backdrop_path)
        ImageView backdrop;
        @BindView(R.id.original_title)
        TextView title;
        @BindView(R.id.user_rating)
        TextView user_rating;
        @BindView(R.id.synopsis)
        TextView synopsis;
        @BindView(R.id.release_date)
        TextView release_date;
        @BindView(R.id.trailer_list)
        LinearLayout trailer_list;
        @BindView(R.id.review_list)
        LinearLayout review_list;
        @BindView(R.id.trailer_header)
        LinearLayout trailer_header;
        @BindView(R.id.trailer_expandable_list)
        ExpandableLinearLayout TexpandableLinearLayout;
        @BindView(R.id.review_header)
        LinearLayout review_header;
        @BindView(R.id.review_expandable_list)
        ExpandableLinearLayout RexpandableLinearLayout;
        @BindView(R.id.like)
        FloatingActionButton favorite;
        @BindView(R.id.like_button)
        LikeButtonView likeButton;

        public MovieDetailsView(View view) {
            ButterKnife.bind(this, view);
        }
    }
    @BindView(R.id.stub_import) ViewStub viewStub;

    private Unbinder unbinder;
    private MovieDetailsView movieDetailsView;
    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            movie = savedInstanceState.getParcelable("movie");
        } else {
            Bundle arguments = getArguments();
            if (arguments != null) {
                if (Utility.getSortOrder(getActivity()).equals(FAVORITE))
                    mUri = arguments.getParcelable(DetailFragment.DETAIL_URI);
                else {
                    Intent intent = getActivity().getIntent();
                    movie = intent.getParcelableExtra("MovieDetails");
                    if (movie == null) {
                        movie = arguments.getParcelable("MovieDetails");
                    }

                    new FetchMovieDetailsTask().execute(movie.id);
                }
            }
            Timber.d("movie:" + movie + "URI:" + mUri);
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        unbinder = ButterKnife.bind(this, rootView);


        if (Utility.getSortOrder(getActivity()).equals(FAVORITE)) {
            View view = viewStub.inflate();
            movieDetailsView = new MovieDetailsView(view);
        }
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_detail,menu);
        MenuItem menuItem = menu.findItem(R.id.action_share);
        Timber.d("Menu inflated");
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        if (movie != null) {
            mShareActionProvider.setShareIntent(createShareMovieTrailerIntent());
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    private Intent createShareMovieTrailerIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        shareIntent.setType("text/plain");
        String shareText;
        if (movie.trailer_keys != null) {
            shareText = MOVIE_SHARE_STRING + movie.title + " Trailer : " + "https://www.youtube.com/watch?v=" + movie.trailer_keys[0];
        } else {
            shareText = MOVIE_SHARE_STRING + movie.title;
        }
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        return shareIntent;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        if (Utility.getSortOrder(getActivity()).equals(FAVORITE))
            getLoaderManager().initLoader(DETAIL_LOADER, null, this);

        super.onActivityCreated(savedInstanceState);
    }

    public void markFavorite() {
        // Code for putting movie into favorite
        Timber.i("Favorite button tapped");
        if (Utility.getSortOrder(getActivity()).equals("favorite")) {
            Toast.makeText(getActivity(), "Movie is already added to favorites!", Toast.LENGTH_SHORT).show();
        } else {
            Cursor favoriteCursor = getActivity()
                    .getContentResolver()
                    .query(
                            MoviesProvider.Favorites.FAVORITES_URI,
                            new String[]{MovieColumns.TITLE},
                            MovieColumns.TITLE + "=?",
                            new String[]{movie.title},
                            null);

            if (favoriteCursor.getCount() != 0)
                Toast.makeText(getActivity(), "Movie is already added to favorites!", Toast.LENGTH_SHORT).show();
            else {
                Toast.makeText(getActivity(), "Movie added to favorites!", Toast.LENGTH_SHORT).show();
                insertData();
            }
            favoriteCursor.close();
        }
    }

    private void insertData() {
        Timber.d("In insertData()");
        ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>();

        ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(MoviesProvider.Favorites.FAVORITES_URI);

         /*
         Store Movie poster images, i.e @image_url and @backdrop_path
         as Bitmap Images in Internal Storage
          */
        String poster_path = Utility.saveToInternalStorage(movieDetailsView.poster,movie.title,getActivity());
        String backdrop_path = Utility.saveToInternalStorage(movieDetailsView.backdrop,movie.title + "_backdrop",getActivity());

        builder.withValue(MovieColumns.MOVIE_ID, movie.id);
        builder.withValue(MovieColumns.MOVIE_POSTER_URL, poster_path);
        builder.withValue(MovieColumns.TITLE, movie.title);
        builder.withValue(MovieColumns.SYNOPSIS, movie.synopsis);
        builder.withValue(MovieColumns.USER_RATING, movie.user_rating);
        builder.withValue(MovieColumns.RELEASE_DATE, movie.release_date);
        builder.withValue(MovieColumns.BACKDROP_PATH, backdrop_path);

        batchOperations.add(builder.build());

        try {
            getActivity().getContentResolver().applyBatch(MoviesProvider.AUTHORITY, batchOperations);
        } catch (Exception e) {
            Timber.e("Error applying movie detail insert" + e);
        }

        ArrayList<ContentProviderOperation> reviewBatchOperations = new ArrayList<>(movie.reviews.length);
        // Store reviews for offline access as well.
        for (String review : movie.reviews) {
            ContentProviderOperation.Builder rBuilder = ContentProviderOperation.newInsert(MoviesProvider.Reviews.REVIEW_URI);

            rBuilder.withValue(ReviewColumns.CONTENT,review);
            rBuilder.withValue(ReviewColumns.MOVIE_ID, movie.id);
            reviewBatchOperations.add(rBuilder.build());
        }
        try {
            getActivity().getContentResolver().applyBatch(MoviesProvider.AUTHORITY, reviewBatchOperations);
        } catch (Exception e) {
            Timber.e("Error applying review insert" + e);
        }

        ArrayList<ContentProviderOperation> trailerBatchOperations = new ArrayList<>(movie.trailer_keys.length);
        // Store reviews for offline access as well.
        for (String trailer : movie.trailer_keys) {
            ContentProviderOperation.Builder tBuilder = ContentProviderOperation.newInsert(MoviesProvider.Trailers.TRAILER_URI);

            tBuilder.withValue(TrailerColumns.KEYS,trailer);
            tBuilder.withValue(TrailerColumns.MOVIE_ID, movie.id);
            trailerBatchOperations.add(tBuilder.build());
        }
        try {
            getActivity().getContentResolver().applyBatch(MoviesProvider.AUTHORITY, trailerBatchOperations);
        } catch (Exception e) {
            Timber.e("Error applying trailer key insert" + e);
        }

    }

    // Custom OnClickListener for handling trailer buttons
    class ButtonClickListener implements View.OnClickListener {
        String trailer;

        public ButtonClickListener(String trailer) {
            this.trailer = trailer;
        }
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri videoId = Uri.parse("https://www.youtube.com/watch?")
                    .buildUpon()
                    .appendQueryParameter("v",this.trailer)
                    .build();
            intent.setData(videoId);
            if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                startActivity(intent);
            } else {
                Timber.d("Couldn't call " + videoId.toString() + ", no receiving apps installed!");
            }
        }
    }

    class FetchMovieDetailsTask extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPostExecute(Void aVoid) {
            View view = viewStub.inflate();
            movieDetailsView = new MovieDetailsView(view);
            Picasso.with(getContext()).load(movie.backdrop_path).fit().centerCrop().into(movieDetailsView.backdrop);
            Picasso.with(getContext()).load(movie.image_url).into(movieDetailsView.poster);
            movieDetailsView.title.setText(movie.title);
            String user_rating_text = movie.user_rating + getResources().getString(R.string.user_rating_base_value);
            movieDetailsView.user_rating.setText(user_rating_text);
            movieDetailsView.synopsis.setText(movie.synopsis);
            movieDetailsView.release_date.setText(movie.release_date);

            // Define Layout Params
            LinearLayout.LayoutParams buttonlp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            // Add every trailer key as a button
            for (int i = 0; i < movie.trailer_keys.length; i++) {
                if (movie.trailer_keys[i].equals("No Trailers Found")) {
                    TextView textView = new TextView(getActivity());
                    textView.setText(movie.trailer_keys[i]);
                    movieDetailsView.trailer_list.addView(textView, buttonlp);
                } else {
                    Button trailerButton = new Button(getActivity());
                    int j = i + 1;
                    trailerButton.setText(getResources().getString(R.string.watch_trailer) + j);
                    movieDetailsView.trailer_list.addView(trailerButton, buttonlp);
                    String trailer = movie.trailer_keys[i];
                    trailerButton.setOnClickListener(new ButtonClickListener(trailer));
                }
            }

            movieDetailsView.trailer_header.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    movieDetailsView.TexpandableLinearLayout.toggle();
                }
            });

            LinearLayout.LayoutParams reviewlp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            for (int i = 0; i < movie.reviews.length; i++) {
                TextView review = new TextView(getActivity());
                review.setText(movie.reviews[i]);
                movieDetailsView.review_list.addView(review, reviewlp);
            }
            try {
                getView().findViewById(R.id.progressBarCircularIndeterminate).setVisibility(View.INVISIBLE);
            } catch (NullPointerException e) {
                Timber.e("Error : " + e);
            }

            movieDetailsView.review_header.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    movieDetailsView.RexpandableLinearLayout.toggle();
                }
            });

            movieDetailsView.favorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    markFavorite();
                    movieDetailsView.likeButton.onClick(view);
                }
            });
            getActivity().invalidateOptionsMenu();

            super.onPostExecute(aVoid);
        }

        @Override
        protected Void doInBackground(String... strings) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String movie_id = strings[0];
            String[] type = {"reviews","videos"};
            String movieJsonstr = null;
            for (String aType : type) {
                try {
                    String TMDB_TRAILERS_URL = "http://api.themoviedb.org/3/movie/" + movie_id + "/" + aType;
                    final String API_KEY = "api_key";

                    Uri builtUri = Uri.parse(TMDB_TRAILERS_URL)
                            .buildUpon()
                            .appendQueryParameter(API_KEY, BuildConfig.TMDB_API_KEY)
                            .build();
                    Timber.v("Built URI:" + builtUri.toString());
                    URL url = new URL(builtUri.toString());
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();

                    //Read the input stream onto a string
                    InputStream inputStream = urlConnection.getInputStream();
                    StringBuffer buffer = new StringBuffer();
                    if (inputStream == null) {
                        movieJsonstr = null;
                    }
                    reader = new BufferedReader(new InputStreamReader(inputStream));

                    String line;
                    while ((line = reader.readLine()) != null) {
                        //Adding new line to make debugging easier
                        buffer.append(line + "\n");
                    }
                    if (buffer.length() == 0) {
                        //Empty Stream, so dont parse
                        movieJsonstr = null;
                    }

                    movieJsonstr = buffer.toString();

                } catch (Exception e) {
                    Timber.e("Error" + e);
                    movieJsonstr = null;
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (IOException e) {
                            Timber.e("Error Closing Stream" + e);
                        }
                    }
                }
                try {
                    if (movieJsonstr == null) {
                        Snackbar.make(getView(), getString(R.string.net_error), Snackbar.LENGTH_INDEFINITE).show();
                    } else {
                        getMovieDetailsfromJson(movieJsonstr, aType);
                    }
                } catch (JSONException e) {
                    Timber.e("Error:" + e);
                }
            }
            return null;
        }

        private void getMovieDetailsfromJson(String movieJsonstr, String type) throws JSONException {

            final String RESULTS = "results";
            final String KEY = "key";
            final String AUTHOR = "author";
            final String CONTENT = "content";

            JSONObject movieDetailsJson = new JSONObject(movieJsonstr);
            switch (type) {
                case "videos":
                    JSONArray videosArray = movieDetailsJson.getJSONArray(RESULTS);
                    if (videosArray == null) {
                        Snackbar.make(getView(), getString(R.string.net_error), Snackbar.LENGTH_INDEFINITE).show();
                    }

                    String[] keys = new String[videosArray.length()];
                    for (int i = 0; i < videosArray.length(); i++) {
                        JSONObject video = videosArray.getJSONObject(i);
                        keys[i] = video.getString(KEY);
                    }
                    Timber.d("Trailer keys count:" + keys.length);
                    movie.setTrailer_keys(keys);
                    break;
                case "reviews":
                    JSONArray reviewsArray = movieDetailsJson.getJSONArray(RESULTS);
                    if (reviewsArray == null) {
                        Snackbar.make(getView(), getString(R.string.net_error), Snackbar.LENGTH_INDEFINITE).show();
                    }

                    String[] reviews = new String[reviewsArray.length()];
                    for (int i = 0; i < reviewsArray.length(); i++) {
                        JSONObject video = reviewsArray.getJSONObject(i);
                        reviews[i] = video.getString(AUTHOR) + ":" + video.getString(CONTENT);
                    }
                    Timber.d("Trailer reviews count:" + reviews.length);
                    movie.setReviews(reviews);
                    break;
            }

        }

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Timber.d("Movie Uri:" + mUri);
        if (mUri != null && Utility.getSortOrder(getActivity()).equals(FAVORITE)) {
            return new CursorLoader(
                    getActivity(),
                    mUri,
                    DETAIL_COLUMNS,
                    null,
                    null,
                    null
            );
        }
        return null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable("movie",movie);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (!Utility.getSortOrder(getActivity()).equals(FAVORITE)) {
            return;
        }
        Timber.d("Count : " + data.getPosition());
        if (data != null && data.moveToFirst()) {
            Timber.d("In Onload finished");
            String backdrop_path = data.getString(COL_BACKDROP_PATH);
            Picasso.with(getContext()).load(new File(backdrop_path)).fit().centerCrop().into(movieDetailsView.backdrop);

            String image_url = data.getString(COL_POSTER_URL);
            Picasso.with(getContext()).load(new File(image_url)).into(movieDetailsView.poster);

            String title = data.getString(COL_TITLE);
            movieDetailsView.title.setText(title);

            String user_rating = data.getString(COL_USER_RATING);
            movieDetailsView.user_rating.setText(user_rating + getResources().getString(R.string.user_rating_base_value));

            String synopsis = data.getString(COL_SYNOPSIS);
            movieDetailsView.synopsis.setText(synopsis);

            String release_date = data.getString(COL_RELEASE_DATE);
            movieDetailsView.release_date.setText(release_date);

            String tmdb_id = data.getString(COL_TMDB_ID);
            Timber.d("Movie ID: " + tmdb_id);

            Cursor trailerCursor = getActivity()
                    .getContentResolver()
                    .query(
                            MoviesProvider.Trailers.fromTrailer,
                            new String[]{TrailerColumns.KEYS},
                            MoviesDatabase.TRAILERS + "." + TrailerColumns.MOVIE_ID + "=?",
                            new String[]{tmdb_id},
                            null);
            String[] trailer_keys = new String[trailerCursor.getCount()];
            trailerCursor.moveToFirst();

            int ctr = 0;

            Timber.d("Trailer Count: " + trailerCursor.getCount());
            while (!trailerCursor.isAfterLast()) {
                trailer_keys[ctr] = trailerCursor.getString(COL_TRAILER_KEY);
                ctr++;
                trailerCursor.moveToNext();
            }

            trailerCursor.close();
            // Define Layout Params
            LinearLayout.LayoutParams buttonlp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            // Add every trailer key as a button, if network is present, otherwise give simple error
            for (String key : trailer_keys) {
                if (!Utility.isNetWorkAvailable(getActivity())) {
                    Timber.d("Offline Trailer Message");
                    TextView textView = new TextView(getActivity());
                    textView.setText(R.string.offline_error);
                    movieDetailsView.trailer_list.addView(textView, buttonlp);
                    break;
                } else if (key.equals("No Trailers Found")) {
                    Timber.d("No Trailer message");
                    TextView textView = new TextView(getActivity());
                    textView.setText(key);
                    movieDetailsView.trailer_list.addView(textView, buttonlp);
                } else {
                    Timber.d("Online, Found Trailers in Database");
                    Button trailerButton = new Button(getActivity());
                    trailerButton.setText(getResources().getString(R.string.watch_trailer));
                    movieDetailsView.trailer_list.addView(trailerButton, buttonlp);
                    String trailer = key;
                    trailerButton.setOnClickListener(new ButtonClickListener(trailer));
                }
            }

            movieDetailsView.trailer_header.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    movieDetailsView.TexpandableLinearLayout.toggle();
                }
            });

            Cursor reviewCursor = getActivity()
                    .getContentResolver()
                    .query(
                            MoviesProvider.Reviews.fromReview,
                            new String[]{ReviewColumns.CONTENT},
                            MoviesDatabase.REVIEWS + "." + ReviewColumns.MOVIE_ID + "=?",
                            new String[]{tmdb_id},
                            null);
            String[] reviews = new String[reviewCursor.getCount()];
            reviewCursor.moveToFirst();

            ctr = 0;

            while (!reviewCursor.isAfterLast()) {
                reviews[ctr] = reviewCursor.getString(COL_CONTENT);
                ctr++;
                reviewCursor.moveToNext();
            }

            reviewCursor.close();

            LinearLayout.LayoutParams reviewlp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            for (String singleReview : reviews) {
                TextView review = new TextView(getActivity());
                review.setText(singleReview);
                movieDetailsView.review_list.addView(review, reviewlp);
            }

            movieDetailsView.review_header.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    movieDetailsView.RexpandableLinearLayout.toggle();
                }
            });

            getView().findViewById(R.id.progressBarCircularIndeterminate).setVisibility(View.INVISIBLE);
            movieDetailsView.favorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    markFavorite();
                }
            });
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) { }

}
