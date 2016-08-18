package ishaanmalhi.com.popularmoviesapp.views;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by ishaan on 17/8/16.
 */
public class PopularTextView extends TextView {
    public PopularTextView(Context context) {

        super(context);

        if (!isInEditMode())
            init(context);
    }

    public PopularTextView(Context context, AttributeSet attrs) {

        super(context, attrs);

        if (!isInEditMode())
            init(context);
    }

    public PopularTextView(Context context, AttributeSet attrs, int defStyleAttr) {

        super(context, attrs, defStyleAttr);

        if (!isInEditMode())
            init(context);

    }

    private void init(Context context) {

        Typeface t = Typeface.createFromAsset(context.getAssets(), "fonts/GrandHotel-Regular.ttf");
        this.setTypeface(t);
    }
}
