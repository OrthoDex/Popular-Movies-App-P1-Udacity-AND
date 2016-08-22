package ishaanmalhi.com.popularmoviesapp;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * Created by ishaan on 14/8/16.
 */
public class MovieCursorAdapter extends CursorAdapter {

    public class ViewHolder {
        @BindView(R.id.movie_image)
        ImageView poster;
        @BindView(R.id.movie_title)
        TextView title;
        @BindView(R.id.rating)
        TextView rating;

        public ViewHolder(View view) {
            ButterKnife.bind(this,view);
        }
    }
    public MovieCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        View view = LayoutInflater.from(context).inflate(R.layout.movie_item, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        view.setTag(holder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder) view.getTag();

        holder.title.setText(cursor.getString(FavoriteMovieFragment.COL_TITLE));
        holder.title.setText(cursor.getString(FavoriteMovieFragment.COL_USER_RATING));

        String path = cursor.getString(FavoriteMovieFragment.COL_POSTER_URL);
        Picasso.with(context)
                .load(new File(path))
                .fit()
                .centerInside()
                .error(R.drawable.ic_info_black_24dp)
                .into(holder.poster);
    }
}
