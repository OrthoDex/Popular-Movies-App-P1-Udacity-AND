package ishaanmalhi.com.popularmoviesapp;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MovieFragment extends Fragment {

    //Private Variables
    private ArrayAdapter<String> adapter;

    public MovieFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.moviefragment, menu);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_movie, container, false);
    }

    class FetchMovieTask extends AsyncTask<String, Void, String[]>{

        private final String LOG_TAG = FetchMovieTask.class.getSimpleName();

        @Override
        protected void onPostExecute(String[] result) {
            if (result != null){
                adapter.clear();
                for (String movieInfostr : result){
                    adapter.add(movieInfostr);
                }
            }
        }

        private String[] getMovieDatafromJson(String movieJsonstr) throws JSONException
        {
            JSONObject movieInfo = new JSONObject(movieJsonstr);
            //Names of objects that need to be extracted
            final String 

            return result;
        }

        @Override
        protected String[] doInBackground(String... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String movieJsonstr = null;

            try {
                final String TMDB_BASE_URL = "http://api.themoviedb.org/3/movie/"+params[0];
                final String API_KEY = "api_key";
                Uri builtUri = Uri.parse(TMDB_BASE_URL)
                                    .buildUpon()
                                    .appendQueryParameter(API_KEY,BuildConfig.TMDB_API_KEY)
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

            String[] result = new String[0];
            try{
                result = getMovieDatafromJson(movieJsonstr);
            } catch (JSONException e){
                Log.e(LOG_TAG,"Error:",e);
            }
            return  result;
        }

    }

}
