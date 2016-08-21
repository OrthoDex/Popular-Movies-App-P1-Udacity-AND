package ishaanmalhi.com.popularmoviesapp.views;

import android.animation.ArgbEvaluator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.util.Property;
import android.view.View;

import ishaanmalhi.com.popularmoviesapp.Utility;

/**
 * Created by ishaan on 20/8/16.
 */
public class CircleView extends View {

    private static final int END_COLOR = 0x3F51B5;
    private static final int START_COLOR = 0x303F9F;

    private ArgbEvaluator argbEvaluator = new ArgbEvaluator();

    private Paint circlePaint = new Paint();
    private Paint maskPaint = new Paint();

    private Bitmap tempBitmap;
    private Canvas tempCanvas;

    private float outerRadiusCounter = 0f;
    private float innerRadiusCounter = 0f;

    private int maxRadius;
    
    public CircleView(Context context) {
        super(context);
        init();
    }

    public CircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        circlePaint.setStyle(Paint.Style.FILL);
        maskPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
    }

    public CircleView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        maxRadius = w / 2;
        tempBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        tempCanvas = new Canvas(tempBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        tempCanvas.drawColor(0xffffff, PorterDuff.Mode.CLEAR);
        tempCanvas.drawCircle(getWidth()/2, getHeight()/2, outerRadiusCounter * maxRadius, circlePaint);
        tempCanvas.drawCircle(getWidth()/2, getHeight()/2, innerRadiusCounter * maxRadius, maskPaint);
        canvas.drawBitmap(tempBitmap, 0, 0, null);
    }

    public void setInnerRadiusCounter(float innerRadiusCounter) {
        this.innerRadiusCounter = innerRadiusCounter;
        postInvalidate();
    }

    public float getInnerRadiusCounter() {
        return innerRadiusCounter;
    }

    public void setOuterradiusCounter(float outerRadiusCounter) {
        this.outerRadiusCounter = outerRadiusCounter;
        updateCircleColor();
        postInvalidate();;
    }

    private void updateCircleColor() {
        float colorCounter = (float) Utility.clamp(outerRadiusCounter, 0.5, 1);
        colorCounter = (float) Utility.mapValueFromRangeToRange(colorCounter, 0.5f, 1f, 0f, 1f);
        this.circlePaint.setColor((Integer) argbEvaluator.evaluate(colorCounter, START_COLOR, END_COLOR));
    }

    public float getOuterRadiusCounter() {
        return outerRadiusCounter;
    }

    public static final Property<CircleView, Float> INNER_CIRCLE_RADIUS_COUNTER =
            new Property<CircleView, Float>(Float.class, "innerRadiusCounter") {
                @Override
                public void set(CircleView circleView, Float value) {
                    circleView.setInnerRadiusCounter(value);
                }

                @Override
                public Float get(CircleView circleView) {
                    return circleView.getInnerRadiusCounter();
                }
            };

    public static final Property<CircleView, Float> OUTER_CIRCLE_RADIUS_COUNTER =
            new Property<CircleView, Float>(Float.class, "outerRadiusCounter") {
                @Override
                public void set(CircleView circleView, Float value) {
                    circleView.setOuterradiusCounter(value);
                }

                @Override
                public Float get(CircleView circleView) {
                    return circleView.getOuterRadiusCounter();
                }
            };

}
