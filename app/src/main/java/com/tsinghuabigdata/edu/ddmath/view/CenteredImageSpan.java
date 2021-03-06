package com.tsinghuabigdata.edu.ddmath.view;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.style.ImageSpan;

/**
 * 居中显示span
 */

public class CenteredImageSpan extends ImageSpan
{
    public CenteredImageSpan(Context context, int resourceId) {
        super(context, resourceId);
    }
    public CenteredImageSpan(Context context, Bitmap bitmap) {
        super(context, bitmap);
    }

    public CenteredImageSpan(Drawable drawable) {
        super( drawable );
    }

    public int getSize(Paint paint, CharSequence text, int start, int end,
                       Paint.FontMetricsInt fm) {
        Drawable d = getDrawable();
        Rect rect = d.getBounds();
        if (fm != null) {
            Paint.FontMetricsInt fmPaint=paint.getFontMetricsInt();
            int fontHeight = fmPaint.bottom - fmPaint.top;
            int drHeight=rect.bottom-rect.top;

            int top= drHeight/2 - fontHeight/4;
            int bottom=drHeight/2 + fontHeight/4;

            fm.ascent=-bottom;
            fm.top=-bottom;
            fm.bottom=top;
            fm.descent=top;
        }
        return rect.right;
    }

    @Override
    public void draw(Canvas canvas, CharSequence text, int start, int end,
                     float x, int top, int y, int bottom, Paint paint) {
        Drawable b = getDrawable();
        canvas.save();
        int h = bottom-top;
        if( h > b.getBounds().bottom ){
            float rate = h * 1.0f/b.getBounds().bottom;
            canvas.translate(x, top);
            canvas.scale( 1, rate);
        }else{
            int transY = ((bottom-top) - b.getBounds().bottom)/2+top;
            canvas.translate(x, transY);
        }
        b.draw(canvas);
        canvas.restore();
    }
}

