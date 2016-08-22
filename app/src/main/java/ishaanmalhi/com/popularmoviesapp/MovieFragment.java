package ishaanmalhi.com.popularmoviesapp;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import timber.log.Timber;

public class MovieFragment extends Fragment {

    //Private Variables
    private MovieDetailAdapter adapter;
    private ArrayList<MovieDetail> movie_list;
    private String sort_order;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView rv;

    public MovieFragment() {
        // Required empty public constructor
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(Utility.isNetWorkAvailable(getActivity())) {
            sort_order = Utility.getSortOrder(getActivity());
            movie_list = new ArrayList<MovieDetail>();
            if (savedInstanceState != null && savedInstanceState.containsKey("movies")) {
                movie_list = savedInstanceState.getParcelableArrayList("movies");
            } else {
                updateMovieList();
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.moviefragment, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void updateMovieList()
    {
        FetchMovieTask fetchMovieTask = new FetchMovieTask();
        sort_order = Utility.getSortOrder(getActivity());
        fetchMovieTask.execute(sort_order);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            if(Utility.isNetWorkAvailable(getActivity()))
                updateMovieList();
            return true;
        }
        return super.onOptionsItemSelected(item);

    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("movies", movie_list);
        super.onSaveInstanceState(outState);
    }

    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void onItemSelected(MovieDetail movie);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (!Utility.isNetWorkAvailable(getActivity())) {
            Timber.v("No Network Detected");
            Snackbar.make(getView(), getString(R.string.net_error), Snackbar.LENGTH_INDEFINITE).show();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Timber.d("Creating view");
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_movies_list, container, false);
        rv = (RecyclerView) view.findViewById(R.id.recyclerview);

        rv.setLayoutManager(new GridLayoutManager(rv.getContext(), getResources().getInteger(R.integer.movie_columns)));
        adapter = new MovieDetailAdapter(getContext(), new ArrayList<MovieDetail>());
        rv.setAdapter(adapter);

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Timber.d("Refreshing..");
                getActivity().recreate();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        String sort = Utility.getSortOrder(getActivity());
        if (sort_order == null)
            return;
        if (!sort_order.equals(sort))
            updateMovieList();
    }

    class FetchMovieTask extends AsyncTask<String, Void, MovieDetail[]> {

        protected void onPostExecute(MovieDetail[] result) {
            if (result != null){
                adapter.clear();
                for (MovieDetail movieInfostr : result){
                    movie_list.add(movieInfostr);
                    adapter.add(movieInfostr);
                }
            }
            mSwipeRefreshLayout.setRefreshing(false);
        }

        private MovieDetail[] getMovieDatafromJson(String movieJsonstr) throws JSONException
        {
            //Names of objects that need to be extracted
            final String TMDB_RESULTS = "results";
            final String ID = "id";
            final String IMAGE = "poster_path";
            final String TITLE = "original_title";
            final String SYNOPSIS = "overview";
            final String USER_RATING = "vote_average";
            final String RELEASE_DATE = "release_date";
            final String BACKDROP_IMG = "backdrop_path";

            JSONObject moviesJson = new JSONObject(movieJsonstr);
            JSONArray movieArray = moviesJson.getJSONArray(TMDB_RESULTS);

            //Log.v(LOG_TAG,movieArray.toString());
            MovieDetail[] result = new MovieDetail[movieArray.length()];

            for (int i = 0; i < movieArray.length(); i++){

                JSONObject movie = movieArray.getJSONObject(i);
                String id = movie.getString(ID);
                String image_uri = "http://image.tmdb.org/t/p/w500/".concat(movie.getString(IMAGE));
                String title = movie.getString(TITLE);
                String synopsis = movie.getString(SYNOPSIS);
                String release_date = movie.getString(RELEASE_DATE);
                String user_rating = movie.getString(USER_RATING);
                String backdrop_path = "http://image.tmdb.org/t/p/w780/".concat(movie.getString(BACKDROP_IMG));

                result[i] = new MovieDetail(id,image_uri,title,synopsis,release_date,user_rating,backdrop_path);
            }

            return result;
        }

        @Override
        protected MovieDetail[] doInBackground(String... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String movieJsonstr = null;

            try {
                String TMDB_BASE_URL = "http://api.themoviedb.org/3/movie";
                final String API_KEY = "api_key";

                Timber.v("Sort Order:"+params[0]);

                if(params[0].equals("popular")){
                    TMDB_BASE_URL += "/popular?";
                }
                else {
                    TMDB_BASE_URL += "/top_rated?";
                }
                Uri builtUri = Uri.parse(TMDB_BASE_URL)
                                    .buildUpon()
                                    .appendQueryParameter(API_KEY, BuildConfig.TMDB_API_KEY)
                                    .build();
                Timber.v("Built URI:"+builtUri.toString());
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

            } catch (Exception e) {
                Timber.e("Error" + e);
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
                        Timber.e("Error Closing Stream" + e);
                    }
                }
            }

            MovieDetail[] result = new MovieDetail[0];
            try{
                result = getMovieDatafromJson(movieJsonstr);
            } catch (JSONException e){
                Timber.e("Error:" + e);
            }
            return result;
        }

    }

}
