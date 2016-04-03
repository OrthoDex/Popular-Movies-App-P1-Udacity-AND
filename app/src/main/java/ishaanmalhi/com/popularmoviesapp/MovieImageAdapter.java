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

public class MovieImageAdapter extends ArrayAdapter<MovieImage> {
    private static final String LOG_TAG = MovieImageAdapter.class.getSimpleName();

    /* Custom Array Adapter for MovieImage class*/

    public MovieImageAdapter(Context context, ArrayList<MovieImage> movieImages) {
        super(context, 0, movieImages);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MovieImage movieImage = getItem(position);

        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.movie_item, parent, false);
        }
        ImageView poster = (ImageView) convertView.findViewById(R.id.movie_image);
        Picasso.with(getContext()).load(movieImage.image_url).into(poster);

        return convertView;
    }
}
