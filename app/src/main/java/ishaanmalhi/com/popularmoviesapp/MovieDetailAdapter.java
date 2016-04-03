package ishaanmalhi.com.popularmoviesapp;

import android.content.Context;
import android.media.Image;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import java.util.ArrayList;

import com.squareup.picasso.Picasso;

public class MovieDetailAdapter extends ArrayAdapter<MovieDetail> {
    private static final String LOG_TAG = MovieDetailAdapter.class.getSimpleName();

    /* Custom Array Adapter for MovieDetail class*/

    public MovieDetailAdapter(Context context, ArrayList<MovieDetail> MovieDetails) {
        super(context, 0, MovieDetails);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MovieDetail MovieDetail = getItem(position);

        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.movie_item, parent, false);
        }
        ImageView poster = (ImageView) convertView.findViewById(R.id.movie_image);
        Picasso.with(getContext()).load(MovieDetail.image_url).into(poster);

        return convertView;
    }
}
