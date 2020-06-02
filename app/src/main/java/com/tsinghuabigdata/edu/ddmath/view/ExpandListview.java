package com.tsinghuabigdata.edu.ddmath.view;

import android.content.Context;
import android.widget.ListView;

/**
 * Created by lenovo on 2017/3/8.
 */
public class ExpandListview extends ListView {

	public ExpandListview(Context context) {
		super(context);
	}

	public ExpandListview(Context context, android.util.AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int mExpandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, mExpandSpec);
	}
}
