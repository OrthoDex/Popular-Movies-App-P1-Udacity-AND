package ishaanmalhi.com.popularmoviesapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.facebook.stetho.Stetho;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity implements FavoriteMovieFragment.Callback {

    private final String MOVIE_FRAGMENT_TAG = "moviefragment";
    private final String FAVORITE_FRAGMENT_TAG = "favoritemovies";
    private String sort;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Debugging library
        Stetho.initializeWithDefaults(this);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sort = sharedPreferences.getString(getString(R.string.pref_sort_order_key),getString(R.string.pref_sort_order_default));

        Timber.plant(new Timber.DebugTree());
        if (savedInstanceState == null) {
            if (sort.equals("favorite")) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.container, new FavoriteMovieFragment(), FAVORITE_FRAGMENT_TAG)
                        .commit();
            } else {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.container, new MovieFragment(), MOVIE_FRAGMENT_TAG)
                        .commit();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (Utility.getSortOrder(this).equals("favorite")) {
            FavoriteMovieFragment fragment = (FavoriteMovieFragment) getSupportFragmentManager().findFragmentByTag(FAVORITE_FRAGMENT_TAG);
            if (fragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, fragment, FAVORITE_FRAGMENT_TAG)
                        .commit();
            } else {
                Timber.e(FAVORITE_FRAGMENT_TAG + " Not Found");
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, new FavoriteMovieFragment(), FAVORITE_FRAGMENT_TAG)
                        .commit();
            }
        } else {
            MovieFragment fragment = (MovieFragment) getSupportFragmentManager().findFragmentByTag(MOVIE_FRAGMENT_TAG);
            if (fragment != null && Utility.getSortOrder(this).equals(sort)) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, fragment, MOVIE_FRAGMENT_TAG)
                        .commit();
            } else {
                Timber.e(MOVIE_FRAGMENT_TAG + " Not Found");
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, new MovieFragment(), MOVIE_FRAGMENT_TAG)
                        .commit();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(Uri iduri) {
        Intent intent = new Intent(this, DetailActivity.class)
                .setData(iduri);
        startActivity(intent);
    }
}
