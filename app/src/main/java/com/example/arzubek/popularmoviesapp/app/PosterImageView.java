package com.example.arzubek.popularmoviesapp.app;

/**
 * Created by arzubek on 8/13/15.
 */
import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;


public class PosterImageView extends ImageView {
    private static final float HEIGHT_TO_WIDTH_RATIO = 1.5f;


    public PosterImageView(Context context) {
        super(context);
    }

    public PosterImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PosterImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth(), (int)(getMeasuredWidth() * HEIGHT_TO_WIDTH_RATIO));
    }
}
