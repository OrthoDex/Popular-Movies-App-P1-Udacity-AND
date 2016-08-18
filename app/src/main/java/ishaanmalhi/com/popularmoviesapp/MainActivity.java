package ishaanmalhi.com.popularmoviesapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import com.facebook.stetho.Stetho;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity implements FavoriteMovieFragment.Callback {

    private final String MOVIE_FRAGMENT_TAG = "moviefragment";
    private final String FAVORITE_FRAGMENT_TAG = "favoritemovies";
    private final String DETAILFRAGMENT_TAG = "moviedetail";
    private String sort;
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Debugging library
        Stetho.initializeWithDefaults(this);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sort = sharedPreferences.getString(getString(R.string.pref_sort_order_key),getString(R.string.pref_sort_order_default));

        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_main_toolbar);
        setSupportActionBar(toolbar);

        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimaryDark));

        Timber.plant(new Timber.DebugTree());
        if (findViewById(R.id.movie_detail_container) != null) {
            mTwoPane = true;
            Timber.d("Two pane mode.");
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container, new DetailFragment(), DETAILFRAGMENT_TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
            Timber.d("Single Pane Mode.");
        }

        if (sort.equals("favorite")) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_movie, new FavoriteMovieFragment(), FAVORITE_FRAGMENT_TAG)
                    .commit();
        } else {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_movie, new MovieFragment(), MOVIE_FRAGMENT_TAG)
                    .commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Timber.d("Inside resume");
        if (Utility.getSortOrder(this).equals("favorite")) {
            FavoriteMovieFragment fragment = (FavoriteMovieFragment) getSupportFragmentManager().findFragmentByTag(FAVORITE_FRAGMENT_TAG);
            if (fragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_movie, fragment, FAVORITE_FRAGMENT_TAG)
                        .commit();
            } else {
                Timber.e(FAVORITE_FRAGMENT_TAG + " Not Found");
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_movie, new FavoriteMovieFragment(), FAVORITE_FRAGMENT_TAG)
                        .commit();
            }
        } else {
            MovieFragment fragment = (MovieFragment) getSupportFragmentManager().findFragmentByTag(MOVIE_FRAGMENT_TAG);
            if (fragment != null && Utility.getSortOrder(this).equals(sort)) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_movie, fragment, MOVIE_FRAGMENT_TAG)
                        .commit();
            } else {
                Timber.e(MOVIE_FRAGMENT_TAG + " Not Found");
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_movie, new MovieFragment(), MOVIE_FRAGMENT_TAG)
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
        if (mTwoPane) {
            Bundle args = new Bundle();
            args.putParcelable(DetailFragment.DETAIL_URI, iduri);

            DetailFragment detailFragment = new DetailFragment();
            detailFragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, detailFragment, DETAILFRAGMENT_TAG)
                    .commit();
        } else {
            Intent intent = new Intent(this, DetailActivity.class)
                    .setData(iduri);
            startActivity(intent);
        }
    }

}
