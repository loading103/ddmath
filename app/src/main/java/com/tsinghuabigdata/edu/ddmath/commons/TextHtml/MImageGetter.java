package com.tsinghuabigdata.edu.ddmath.commons.TextHtml;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LevelListDrawable;
import android.text.Html.ImageGetter;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.tsinghuabigdata.edu.ddmath.util.AppLog;
import com.tsinghuabigdata.edu.ddmath.util.PicassoUtil;

/**
 * @author gaozhen
 * 重新的imagegetter类
 */
public class MImageGetter implements ImageGetter{

	private Context context;
	private TextView container;

	public MImageGetter(TextView text,Context c) {
		this.context = c;
		this.container = text;
	}
	public Drawable getDrawable(String source) {

		final LevelListDrawable drawable = new LevelListDrawable();

		PicassoUtil.getPicasso( context ).load( source).into(new Target() {
			@Override
			public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
				AppLog.i("down success bitmap " + bitmap );
				if(bitmap != null) {
					BitmapDrawable bitmapDrawable = new BitmapDrawable(bitmap);
					drawable.addLevel(1, 1, bitmapDrawable);
					drawable.setBounds(0, 0, bitmap.getWidth(),bitmap.getHeight());
					drawable.setLevel(1);
					container.invalidate();
					container.setText(container.getText()); // 解决图文重叠
				}
			}

			@Override
			public void onBitmapFailed(Drawable errorDrawable) {
				AppLog.i("down onBitmapFailed");
			}

			@Override
			public void onPrepareLoad(Drawable placeHolderDrawable) {
				AppLog.i("down onPrepareLoad");
			}
		});
		return drawable;
	 }

}
