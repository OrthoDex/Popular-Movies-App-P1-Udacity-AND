package ishaanmalhi.com.popularmoviesapp;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
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

public class MovieDetailAdapter extends RecyclerView.Adapter<MovieDetailAdapter.ViewHolder> {
    private ArrayList<MovieDetail> mValues;

    public void clear() {
        mValues.clear();
        notifyDataSetChanged();
    }

    public void add(MovieDetail movie) {
        mValues.add(movie);
        notifyItemInserted(mValues.size() - 1);
    }

    //ViewHolder Pattern to reduce no of calls to findViewById()

    public class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.movie_image) ImageView poster;
        View mView;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            ButterKnife.bind(this, view);
            // Log.v(LOG_TAG,"Viewholder initialized, poster = " + poster.toString());
        }
    }

    /* Custom RecyclerViewAdapter for MovieDetail class*/

    public MovieDetailAdapter(Context context, ArrayList<MovieDetail> movieDetails) {
        mValues = movieDetails;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_item, parent, false);
        return new ViewHolder(convertView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final MovieDetail movieDetail = mValues.get(position);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = view.getContext();
                Intent movieInfoIntent = new Intent(context,DetailActivity.class)
                        .putExtra("MovieDetails", movieDetail);
                context.startActivity(movieInfoIntent);
            }
        });
        Picasso.with(holder.poster.getContext())
                .load(movieDetail.image_url)
                .fit()
                .centerCrop()
                .error(R.drawable.ic_info_black_24dp)
                .into(holder.poster);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }
}
