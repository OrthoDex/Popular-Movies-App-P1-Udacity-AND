package ishaanmalhi.com.popularmoviesapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

public class MovieFragment extends Fragment {

    //Private Variables
    private MovieDetailAdapter adapter;
    private MovieDetail movie;
    public MovieFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.moviefragment, menu);
    }

    private void updateMovieList()
    {
        FetchMovieTask fetchMovieTask = new FetchMovieTask();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sort_order = sharedPreferences.getString(getString(R.string.pref_sort_order_key),getString(R.string.pref_sort_order_default));
        fetchMovieTask.execute(sort_order);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovieList();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable("movies", movie);
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_movie, container, false);

        adapter = new MovieDetailAdapter(getActivity(), new ArrayList<MovieDetail>());
        GridView gridView = (GridView) rootView.findViewById(R.id.movies_grid);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MovieDetail movie = (MovieDetail) parent.getItemAtPosition(position);
                Intent movieInfoIntent = new Intent(getActivity(),DetailActivity.class)
                                            .putExtra("MovieDetails",(Parcelable) movie);
                startActivity(movieInfoIntent);
            }
        });
        return rootView;
    }

    class FetchMovieTask extends AsyncTask<String, Void, MovieDetail[]> {

        private final String LOG_TAG = FetchMovieTask.class.getSimpleName();

        protected void onPostExecute(MovieDetail[] result) {
            if (result != null){
                adapter.clear();
                for (MovieDetail movieInfostr : result){
                    adapter.add(movieInfostr);
                }
            }
        }

        private MovieDetail[] getMovieDatafromJson(String movieJsonstr) throws JSONException
        {
            JSONObject movieInfo = new JSONObject(movieJsonstr);

            //Names of objects that need to be extracted
            final String TMDB_RESULTS = "results";
            final String ID = "id";
            final String IMAGE = "poster_path";
            final String TITLE = "original_title";
            final String SYNOPSIS = "overview";
            final String USER_RATING = "vote_average";
            final String RELEASE_DATE = "release_date";

            JSONObject moviesJson = new JSONObject(movieJsonstr);
            JSONArray movieArray = moviesJson.getJSONArray(TMDB_RESULTS);

            //Log.v(LOG_TAG,movieArray.toString());
            MovieDetail[] result = new MovieDetail[movieArray.length()];

            for (int i = 0; i < movieArray.length(); i++){

                JSONObject movie = movieArray.getJSONObject(i);
                String id = movie.getString(ID);
                String image_uri = "http://image.tmdb.org/t/p/w185/".concat(movie.getString(IMAGE));
                String title = movie.getString(TITLE);
                String synopsis = movie.getString(SYNOPSIS);
                String release_date = movie.getString(RELEASE_DATE);
                String user_rating = movie.getString(USER_RATING);
                //Log.v(LOG_TAG,image_uri);
                result[i] = new MovieDetail(id,image_uri,title,synopsis,release_date,user_rating);
            }

            return result;
        }

        @Override
        protected MovieDetail[] doInBackground(String... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String movieJsonstr = null;

            try {
                final String TMDB_BASE_URL = "http://api.themoviedb.org/3/discover/movie?";
                final String API_KEY = "api_key";
                final String SORT_ORDER = "sort-by";

                String sort_order = "popular.desc";
                if(params[0] != sort_order){
                    sort_order = "vote_average.desc";
                }
                Uri builtUri = Uri.parse(TMDB_BASE_URL)
                                    .buildUpon()
                                    .appendQueryParameter(SORT_ORDER,sort_order)
                                    .appendQueryParameter(API_KEY, BuildConfig.TMDB_API_KEY)
                                    .build();
                URL url = new URL(builtUri.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                //Read the input stream onto a string
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if(inputStream == null){
                    movieJsonstr = null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null){
                    //Adding new line to make debugging easier
                    buffer.append(line + "\n");
                }
                if (buffer.length() == 0){
                    //Empty Stream, so dont parse
                    movieJsonstr = null;
                }

                movieJsonstr = buffer.toString();

            } catch (MalformedURLException e) {
                Log.e(LOG_TAG, "Error", e);
                movieJsonstr = null;
            } catch (IOException e){
                Log.e(LOG_TAG, "Error", e);
                movieJsonstr = null;
            } finally {
                if (urlConnection != null){
                    urlConnection.disconnect();
                }
                if (reader != null){
                    try
                    {
                        reader.close();
                    } catch (IOException e) {
                        Log.e(LOG_TAG,"Error Closing Stream", e);
                    }
                }
            }

            MovieDetail[] result = new MovieDetail[0];
            try{
                result = getMovieDatafromJson(movieJsonstr);
            } catch (JSONException e){
                Log.e(LOG_TAG,"Error:" + result, e);
            }
            return result;
        }

    }

}
