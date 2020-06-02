package com.tsinghuabigdata.edu.ddmath.view;

/*
 * HorizontalListView.java v1.5
 *
 * 
 * The MIT License
 * Copyright (c) 2011 Paul Soucy (paul@dev-smart.com)
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 */


import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.Scroller;

import java.util.LinkedList;
import java.util.Queue;

public class HorizontalListView extends AdapterView<ListAdapter> {

	protected ListAdapter mAdapter;
	private int mLeftViewIndex = -1;
	private int mRightViewIndex = 0;
	protected int mCurrentX;
	protected int mNextX;
	private int mMaxX = Integer.MAX_VALUE;
	private int mDisplayOffset = 0;
	protected Scroller mScroller;
	private GestureDetector mGesture;
	private Queue<View> mRemovedViewQueue = new LinkedList<View>();
	private OnItemSelectedListener mOnItemSelected;
	private OnItemClickListener mOnItemClicked;
	private OnItemLongClickListener mOnItemLongClicked;
	private boolean mDataChanged = false;

	private OnGestureMoveLinster mOnGestureMoveLinster;
	private LongClickMonitor mLongClickMonitor;

	public HorizontalListView(Context context) {
		super(context);
		initView();
	}
	public HorizontalListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}
	public HorizontalListView(Context context, AttributeSet attrs, int styleid ) {
		super(context, attrs, styleid);
		initView();
	}


	private synchronized void initView() {
		mLeftViewIndex = -1;
		mRightViewIndex = 0;
		mDisplayOffset = 0;
		mCurrentX = 0;
		mNextX = 0;
		mMaxX = Integer.MAX_VALUE;
		mScroller = new Scroller(getContext());
		mGesture = new GestureDetector(getContext(), mOnGesture);
		mLongClickMonitor = new LongClickMonitor();
	}
	
	@Override
	public void setOnItemSelectedListener(OnItemSelectedListener listener) {
		mOnItemSelected = listener;
	}
	
	@Override
	public void setOnItemClickListener(OnItemClickListener listener){
		mOnItemClicked = listener;
	}
	
	@Override
	public void setOnItemLongClickListener(OnItemLongClickListener listener) {
		mOnItemLongClicked = listener;
	}

	private DataSetObserver mDataObserver = new DataSetObserver() {

		@Override
		public void onChanged() {
			synchronized(HorizontalListView.this){
				mDataChanged = true;
			}
			invalidate();
			requestLayout();
		}

		@Override
		public void onInvalidated() {
			reset();
			invalidate();
			requestLayout();
		}
	};

	@Override
	public ListAdapter getAdapter() {
		return mAdapter;
	}

	@Override
	public View getSelectedView() {
		return null;
	}
	public void setOnGestureMoveLinster( OnGestureMoveLinster linster ){
		this.mOnGestureMoveLinster = linster;
	}
	public void setMaxX( int max ){
		mMaxX = max;
	}
	//一次滑动一个对象的宽度
	private int bakWidth = 0;
	private int scrollWidth = 0;
	public void setScrollWidth( int width ){
		scrollWidth = width;
	}

	public void setNextX(int px){
		synchronized(HorizontalListView.this){
			mNextX = px;
		}
		requestLayout();
	}

	@Override
	public void setAdapter(ListAdapter adapter) {
		if(mAdapter != null) {
			mAdapter.unregisterDataSetObserver(mDataObserver);
		}
		mAdapter = adapter;
		mAdapter.registerDataSetObserver(mDataObserver);
		reset();
	}
	
	private synchronized void reset(){
		initView();
		removeAllViewsInLayout();
        requestLayout();
	}

	@Override
	public void setSelection(int position) {
	}
	
	private void addAndMeasureChild(final View child, int viewPos) {
		LayoutParams params = child.getLayoutParams();
		if(params == null) {
			params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		}

		addViewInLayout(child, viewPos, params, true);
		child.measure(MeasureSpec.makeMeasureSpec(getWidth(), MeasureSpec.AT_MOST),
				MeasureSpec.makeMeasureSpec(getHeight(), MeasureSpec.AT_MOST));
	}

	@Override
	protected synchronized void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);

		if(mAdapter == null){
			return;
		}

		if(mDataChanged){
			int oldCurrentX = mCurrentX;
			initView();
			removeAllViewsInLayout();
			mNextX = oldCurrentX;
			mDataChanged = false;
		}

		if(mScroller.computeScrollOffset()){
			int scrollx = mScroller.getCurrX();
			mNextX = scrollx;
		}

		if(mNextX <= 0){
			mNextX = 0;
			mScroller.forceFinished(true);

		}
		if(mNextX >= mMaxX) {
			mNextX = mMaxX;
			mScroller.forceFinished(true);
		}

		int dx = mCurrentX - mNextX;
		
		removeNonVisibleItems(dx);
		fillList(dx);
		positionItems(dx);
		
		mCurrentX = mNextX;
		
		if(!mScroller.isFinished()){
			post(new Runnable(){
				@Override
				public void run() {
					requestLayout();
				}
			});
		}
	}
	
	private void fillList(final int dx) {
		int edge = 0;
		View child = getChildAt(getChildCount()-1);
		if(child != null) {
			edge = child.getRight();
		}
		fillListRight(edge, dx);
		
		edge = 0;
		child = getChildAt(0);
		if(child != null) {
			edge = child.getLeft();
		}
		fillListLeft(edge, dx);
		
		
	}
	
	private void fillListRight(int rightEdge, final int dx) {
		while(rightEdge + dx < getWidth() && mRightViewIndex < mAdapter.getCount()) {
			
			View child = mAdapter.getView(mRightViewIndex, mRemovedViewQueue.poll(), this);
			addAndMeasureChild(child, -1);
			rightEdge += child.getMeasuredWidth();
			
			if(mRightViewIndex == mAdapter.getCount()-1) {
				int bakMaxX = mCurrentX + rightEdge - getWidth();
				if( mMaxX == Integer.MAX_VALUE || bakMaxX > mMaxX ){
					mMaxX = bakMaxX;
				}
			}
			
			if (mMaxX < 0) {
				mMaxX = 0;
			}
			mRightViewIndex++;
		}
		
	}
	
	private void fillListLeft(int leftEdge, final int dx) {
		while(leftEdge + dx > 0 && mLeftViewIndex >= 0) {
			View child = mAdapter.getView(mLeftViewIndex, mRemovedViewQueue.poll(), this);
			addAndMeasureChild(child, 0);
			leftEdge -= child.getMeasuredWidth();
			mLeftViewIndex--;
			mDisplayOffset -= child.getMeasuredWidth();
		}
	}
	
	private void removeNonVisibleItems(final int dx) {
		View child = getChildAt(0);
		while(child != null && child.getRight() + dx <= 0) {
			mDisplayOffset += child.getMeasuredWidth();
			mRemovedViewQueue.offer(child);
			removeViewInLayout(child);
			mLeftViewIndex++;
			child = getChildAt(0);
			
		}
		
		child = getChildAt(getChildCount()-1);
		while(child != null && child.getLeft() + dx >= getWidth()) {
			mRemovedViewQueue.offer(child);
			removeViewInLayout(child);
			mRightViewIndex--;
			child = getChildAt(getChildCount()-1);
		}
	}
	
	private void positionItems(final int dx) {
		if(getChildCount() > 0){
			mDisplayOffset += dx;
			int left = mDisplayOffset;
			for(int i=0;i<getChildCount();i++){
				View child = getChildAt(i);
				int childWidth = child.getMeasuredWidth();
				child.layout(left, 0, left + childWidth, child.getMeasuredHeight());
				left += childWidth + child.getPaddingRight();
			}
		}
	}

	private boolean bOnLongPress = false;
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if( !bOnLongPress && scrollWidth != 0 ){
			comsumeTouchEvent( ev );
			return true;
		}else{
			if( bOnLongPress && ev.getAction() == MotionEvent.ACTION_UP ){
				bOnLongPress = false;
				if(mOnGestureMoveLinster!=null && mOnGestureMoveLinster.moveend()){
					return true;
				}
			}
			if( bOnLongPress && !mOnGestureMoveLinster.canmove() )
				return true;
			boolean handled = super.dispatchTouchEvent(ev);
			handled |= mGesture.onTouchEvent(ev);
			return handled;
		}
	}

	/** 用于记录开始时候的坐标位置 */
	private PointF startPoint = new PointF();
	private long startPressTime = 0;
	private boolean move_status = false;
    public boolean comsumeTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {

            case MotionEvent.ACTION_DOWN:{
                //AppLog.d(" MoveImageView onTouchEvent ACTION_DOWN ");
                startPoint.set(event.getRawX(), event.getRawY());
				bakWidth = 0;
				move_status = false;
				mLongClickMonitor.start();
				startPressTime = System.currentTimeMillis();
                break;
            }
            case MotionEvent.ACTION_MOVE:{
				//AppLog.d(" MoveImageView onTouchEvent ACTION_MOVE ");

                float dx = event.getRawX() - startPoint.x; // 得到x轴的移动距离
                float dy = event.getRawY() - startPoint.y; // 得到x轴的移动距离
                //避免和双击冲突,大于10f才算是拖动
                if(Math.abs(dx) < 40){
                    break;
                }
				move_status = true;
				if( bakWidth == 0 || bakWidth + ((int)dx) > scrollWidth || bakWidth + ((int)dx) < -scrollWidth ){
					bakWidth = (int)dx;
					if( mNextX == 0 ){
						int distance = scrollWidth - scrollWidth/8;
						mNextX += dx>0?-distance:distance;
					}else if(mNextX >=mMaxX){
						int distance = scrollWidth - scrollWidth/8;
						mNextX += dx>0?-distance:distance;
					}else{
						int distance = scrollWidth;
						mNextX += dx>0?-distance:distance;
					}
					requestLayout();
				}else{
					bakWidth += (int)dx;
				}
				startPoint.set(event.getRawX(), event.getRawY());
            }
            break;
            case MotionEvent.ACTION_UP: {
				mLongClickMonitor.stop();
				if( !move_status ){
					if( System.currentTimeMillis() - startPressTime < 300 ){      //300ms
						onClick();
					}
				}
				move_status = false;
				//AppLog.d(" MoveImageView onTouchEvent ACTION_UP ");
                break;
            }
            default:
                break;
        }
        return true;
    }

	protected boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
		synchronized(HorizontalListView.this){
			mScroller.fling(mNextX, 0, (int)-velocityX, 0, 0, mMaxX, 0, 0);
		}
		requestLayout();
		
		return true;
	}
	
	protected boolean onDown(MotionEvent e) {
		mScroller.forceFinished(true);
		return true;
	}
	
	private OnGestureListener mOnGesture = new GestureDetector.SimpleOnGestureListener() {

		@Override
		public boolean onDown(MotionEvent e) {
			return HorizontalListView.this.onDown(e);
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			return HorizontalListView.this.onFling(e1, e2, velocityX, velocityY);
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			if( mOnGestureMoveLinster!=null && mOnGestureMoveLinster.moveby( -distanceX, -distanceY ) ){

			}
			mNextX += (int)distanceX;
			requestLayout();
			return true;
		}

		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {

			for(int i=0;i<getChildCount();i++){
				View child = getChildAt(i);
				if (isEventWithinView(e, child)) {
					if(mOnItemClicked != null){
						mOnItemClicked.onItemClick(HorizontalListView.this, child, mLeftViewIndex + 1 + i, mAdapter.getItemId( mLeftViewIndex + 1 + i ));
					}
					if(mOnItemSelected != null){
						mOnItemSelected.onItemSelected(HorizontalListView.this, child, mLeftViewIndex + 1 + i, mAdapter.getItemId( mLeftViewIndex + 1 + i ));
					}
					break;
				}
				
			}
			return true;
		}
		
		@Override
		public void onLongPress(MotionEvent e) {
			int childCount = getChildCount();
			for (int i = 0; i < childCount; i++) {
				View child = getChildAt(i);
				if (isEventWithinView(e, child)) {
					if (mOnItemLongClicked != null) {
						bOnLongPress = true;
						mOnItemLongClicked.onItemLongClick(HorizontalListView.this, child, mLeftViewIndex + 1 + i, mAdapter.getItemId(mLeftViewIndex + 1 + i));
					}
					break;
				}
			}
		}


	};

	private boolean isEventWithinView(MotionEvent e, View child) {
		return isEventWithinView(  e.getRawX(), e.getRawY(),child );
	}
	private boolean isEventWithinView(  float x, float y, View child) {

		if( scrollWidth!=0 ){
			if( x < scrollWidth/8 || x > scrollWidth*9/8 )
				return false;
		}

		Rect viewRect = new Rect();
		int[] childPosition = new int[2];
		child.getLocationOnScreen(childPosition);
		int left = childPosition[0];
		int right = left + child.getWidth();
		int top = childPosition[1];
		int bottom = top + child.getHeight();
		viewRect.set(left, top, right, bottom);
		return viewRect.contains( (int)x, (int)y );
	}

	public interface OnGestureMoveLinster{
		//返回true 消费事件了
		boolean moveby( float dx, float dy );
		boolean moveend();
		boolean canmove();
	}

	private static final int MSG_LONG = 1;
	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch ( msg.what ) {
				case MSG_LONG:
					onLongClick();
					break;
				default:
					break;
			}
		}
	};

	class LongClickMonitor {

		private final int ST_RUN    = 1;
		private final int ST_STOP   = 0;

		private int run_status = ST_STOP;

		public LongClickMonitor(){
		}

		public void start(){
			if( run_status==ST_RUN ) return;

			run_status = ST_RUN;
			startRun();
		}

		public void stop(){
			if( run_status == ST_RUN ){
				run_status = ST_STOP;
			}
		}

		public void startRun() {
			new Thread(new Runnable() {
				@Override
				public void run() {

					while( run_status == ST_RUN ){
						SystemClock.sleep(10);
						if( System.currentTimeMillis() - startPressTime >= 1000 ){      //3S
							handler.sendEmptyMessage( MSG_LONG );
							break;
						}
					}
					run_status = ST_STOP;
				}
			}).start();
		}
	}
	private void onLongClick(){
		int childCount = getChildCount();
		for (int i = 0; i < childCount; i++) {
			View child = getChildAt(i);
			if (isEventWithinView( startPoint.x, startPoint.y, child)) {
				if (mOnItemLongClicked != null) {
					bOnLongPress = true;
					mOnItemLongClicked.onItemLongClick(HorizontalListView.this, child, mLeftViewIndex + 1 + i, mAdapter.getItemId(mLeftViewIndex + 1 + i));
				}
				break;
			}
		}
	}

	private void onClick(){
		for(int i=0;i<getChildCount();i++){
			View child = getChildAt(i);
			if (isEventWithinView( startPoint.x, startPoint.y, child)) {
				if(mOnItemClicked != null){
					mOnItemClicked.onItemClick(HorizontalListView.this, child, mLeftViewIndex + 1 + i, mAdapter.getItemId( mLeftViewIndex + 1 + i ));
				}
				if(mOnItemSelected != null){
					mOnItemSelected.onItemSelected(HorizontalListView.this, child, mLeftViewIndex + 1 + i, mAdapter.getItemId( mLeftViewIndex + 1 + i ));
				}
				break;
			}
		}
	}

}
