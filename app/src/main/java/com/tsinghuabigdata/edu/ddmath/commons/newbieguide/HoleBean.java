package com.tsinghuabigdata.edu.ddmath.commons.newbieguide;

import android.graphics.RectF;
import android.view.View;

/**
 *
 */
public class HoleBean {

	public static final int TYPE_CIRCLE = 0;
	public static final int TYPE_RECTANGLE = TYPE_CIRCLE + 1;
	public static final int TYPE_OVAL = TYPE_RECTANGLE + 1;
	public static final int TYPE_ROUNDRECT = TYPE_OVAL + 1;

	public static final int DEFAUT_ROUNDRADIUS = -1;		//高度/高度 最小值的一半

	private View mHole;
	private int mType;
	private int roundRadius;
	public HoleBean(View hole, int type) {
		this.mHole = hole;
		this.mType = type;
	}

	public int getRadius() {
		return mHole != null ? Math.min(mHole.getWidth(), mHole.getHeight()) / 2 : 0;
	}

	public int getRoundRadius() {
		if( roundRadius == HoleBean.DEFAUT_ROUNDRADIUS ){
			return getRadius()+20;
		}else{
			return roundRadius+20;
		}
	}

	public void setRoundRadius(int roundRadius) {
		this.roundRadius = roundRadius;
	}

	public RectF getRectF() {
		RectF rectF = new RectF();
		if (mHole != null) {
			int[] location = new int[2];
			mHole.getLocationOnScreen(location);
			rectF.left = location[0] - 10;
			rectF.top = location[1] - 10;
			rectF.right = location[0] + mHole.getWidth() + 10;
			rectF.bottom = location[1] + mHole.getHeight() + 10;
		}
		return rectF;
	}

	public int getType() {
		return mType;
	}

}