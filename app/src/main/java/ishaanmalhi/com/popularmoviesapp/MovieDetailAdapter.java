package ishaanmalhi.com.popularmoviesapp;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import com.squareup.picasso.Downloader;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class MovieDetailAdapter extends ArrayAdapter<MovieDetail> {

    //ViewHolder Pattern to reduce no of calls to findViewById()

    static class ViewHolder {
        @BindView(R.id.movie_image) ImageView poster;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
            // Log.v(LOG_TAG,"Viewholder initialized, poster = " + poster.toString());
        }
    }

    /* Custom Array Adapter for MovieDetail class*/

    public MovieDetailAdapter(Context context, ArrayList<MovieDetail> MovieDetails) {
        super(context, 0, MovieDetails);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MovieDetail movieDetail = getItem(position);
        ViewHolder holder;

        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.movie_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        // Log.v(LOG_TAG,"Picasso being called"+holder.poster.toString());
        Picasso.with(getContext())
                .load(movieDetail.image_url)
                .error(R.drawable.ic_info_black_24dp)
                .into(holder.poster);

        return convertView;
    }
}
