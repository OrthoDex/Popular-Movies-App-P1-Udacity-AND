package ishaanmalhi.com.popularmoviesapp;

import android.content.Context;
import android.media.Image;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import java.util.ArrayList;

import com.squareup.picasso.Picasso;

public class MovieDetailAdapter extends ArrayAdapter<MovieDetail> {
    private static final String LOG_TAG = MovieDetailAdapter.class.getSimpleName();

    //ViewHolder Pattern to reduce no of calls to findViewById()

    private static class ViewHolder {
        public final ImageView poster;

        private ViewHolder(ImageView poster) {
            this.poster = poster;
        }
    }

    /* Custom Array Adapter for MovieDetail class*/

    public MovieDetailAdapter(Context context, ArrayList<MovieDetail> MovieDetails) {
        super(context, 0, MovieDetails);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MovieDetail movieDetail = getItem(position);
        ImageView poster;

        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.movie_item, parent, false);
            poster = (ImageView) convertView.findViewById(R.id.movie_image);
            convertView.setTag(new ViewHolder(poster));
        }

        ViewHolder viewHolder = (ViewHolder) convertView.getTag();
        poster = viewHolder.poster;

        //Log.v(LOG_TAG,"Picasso being called"+poster.toString());
        Picasso.with(getContext())
                .load(movieDetail.image_url)
                .error(R.drawable.ic_info_black_24dp)
                .into(poster);

        return convertView;
    }
}
