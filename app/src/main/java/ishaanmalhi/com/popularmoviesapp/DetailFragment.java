package ishaanmalhi.com.popularmoviesapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

public class DetailFragment extends Fragment {
    private final String LOG_TAG = DetailFragment.class.getSimpleName();
    private MovieDetail movie_id;

    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        Intent intent = getActivity().getIntent();
        if(intent != null){
            movie_id = (MovieDetail) intent.getParcelableExtra("MovieDetails");
            ImageView poster = (ImageView) rootView.findViewById(R.id.poster_image);
            Picasso.with(getContext()).load(movie_id.image_url).into(poster);
            TextView title = (TextView) rootView.findViewById(R.id.original_title);
            title.setText(movie_id.title);
            TextView user_rating = (TextView) rootView.findViewById(R.id.user_rating);
            user_rating.setText(movie_id.user_rating);
            TextView synopsis = (TextView) rootView.findViewById(R.id.synopsis);
            synopsis.setText(movie_id.synopsis);
            TextView release_date = (TextView) rootView.findViewById(R.id.release_date);
            release_date.setText(movie_id.release_date);
        }
        return rootView;
    }

}
