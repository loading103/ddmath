package com.tsinghuabigdata.edu.ddmath.module.xbook.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.module.xbook.QuestionFilterListener;
import com.tsinghuabigdata.edu.ddmath.util.AppLog;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.WindowUtils;
import com.tsinghuabigdata.edu.utils.ArrayUtil;

/**
 * 时间筛选控件
 */
public class TimeFilterView extends LinearLayout implements View.OnClickListener{

    private TimeArrowView arrowImage;     //指示箭头
    private LinearLayout layout;            //

    private QuestionFilterListener mFilterChangeListener;
    private String timeArray[];
    private int selectIndex = 0;

    private int itemWidth;

    public TimeFilterView(Context context) {
        super(context);
        init();
    }

    public TimeFilterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TimeFilterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TimeFilterView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public void setTimeData( String array[] ){

        if(ArrayUtil.isEmpty( array) ) return;

        timeArray = array;

        int size[] = WindowUtils.getWindowSize( getContext() );
        if(GlobalData.isPad()){
            itemWidth = (size[0] - 2*WindowUtils.dpToPixels( getContext(), 116 )) / array.length;       //
        }else{
            itemWidth = (size[0] - WindowUtils.dpToPixels( getContext(), 108 )) / array.length;
        }

        int itemheight= WindowUtils.dpToPixels( getContext(), 20 );

        for( String time : array ){

            RelativeLayout root = (RelativeLayout) inflate(getContext(), R.layout.view_xbook_timeview, null );
            TextView textView = (TextView) root.findViewById( R.id.xbook_list_timetext );
            textView.setText( time );

            ViewGroup.LayoutParams layoutParams = root.getLayoutParams();
            if( layoutParams == null ){
                layoutParams = new MarginLayoutParams(itemWidth,itemheight);
            }
            layoutParams.width = itemWidth;
            layoutParams.height= itemheight;
            root.setLayoutParams( layoutParams );
            root.setOnClickListener( this );

            layout.addView( root );
        }

        onClick( layout.getChildAt(0) );    //默认选中第一个
        selectIndex = 0;

        //这时控件大小没有分配,需要调整箭头位置
        arrowImage.setX( itemWidth/2 - arrowImage.getWidth()/2 );
    }

    @Override
    public void onClick(View v) {
        int count = layout.getChildCount();
        for( int i=0; i<count; i++ ){
            View child = layout.getChildAt(i);
            TextView textView = (TextView)child.findViewById( R.id.xbook_list_timetext );
            if( v.equals( child) ){
                textView.setTextColor( getResources().getColor( R.color.color_6472BE ) );

                //设置箭头位置
                setArrowPos( child );

                //textView.getText();
                selectIndex = i;

                //事件回调
                if( mFilterChangeListener!=null )
                    mFilterChangeListener.filterChange();

            }else{
                textView.setTextColor( getResources().getColor( R.color.color_ACA1CC) );
            }
        }
    }

    public void setFilterListener( QuestionFilterListener listener ){
        mFilterChangeListener = listener;
    }

    public String getTimeType(){

        if(timeArray==null ) return "";

        if( selectIndex < timeArray.length && selectIndex >= 0 )
            return timeArray[selectIndex];
        return "";

//        Rect rect = new Rect();
//        int index = 0;
//        if( arrowImage.getGlobalVisibleRect( rect ) ){
//            int size[] = WindowUtils.getWindowSize( getContext() );
//            int itemwidth = size[0] / timeArray.length;
//
//            index = rect.left / itemwidth;
//            if( index >= timeArray.length )
//                index = timeArray.length - 1;
//        }
//        return timeArray[index];
    }

    //---------------------------------------------------------------------------------------
    private void init(){
        inflate(getContext(), R.layout.view_xbook_timefilter, this);
        arrowImage = (TimeArrowView) findViewById(R.id.xbook_time_arrow);
        layout = (LinearLayout) findViewById(R.id.xbook_time_layout);

        arrowImage.setListener( arrowImageChangeListener );
    }

    private void setArrowPos( View view ){
        Rect rect = new Rect();
        if( view.getGlobalVisibleRect( rect ) ){
            int dx = (rect.left+rect.right)/2;
            this.getGlobalVisibleRect( rect );
            arrowImage.setX( dx - arrowImage.getWidth()/2 - rect.left );    //相对于父类
        }
    }

    private ArrowImageChangeListener arrowImageChangeListener = new ArrowImageChangeListener() {
        @Override
        public void positionChage() {

            Rect gRect = new Rect();
            arrowImage.getGlobalVisibleRect( gRect );

            int mx = (gRect.left+gRect.right) / 2;
            int index = mx / itemWidth;

            int count = layout.getChildCount();
            for( int i=0; i<count; i++ ){
                View child = layout.getChildAt(i);
                TextView textView = (TextView)child.findViewById( R.id.xbook_list_timetext );
                if( index == i ){
                    selectIndex = index;
                    textView.setTextColor( getResources().getColor( R.color.color_6472BE) );
                }else{
                    textView.setTextColor( getResources().getColor( R.color.color_999999) );
                }
            }
        }

        @Override
        public void onTouchUp( float startx, float endx ) {

            //动画 返回的中心
            if( Math.abs( startx-endx ) > 1 ){

                Rect gRect = new Rect();
                arrowImage.getGlobalVisibleRect( gRect );

                int mx = (gRect.left+gRect.right) / 2;
                int index = mx / itemWidth;
                int stopx = index*itemWidth + (itemWidth - arrowImage.getWidth())/2;

                arrowImage.moveAnimation( gRect.left, stopx );
                AppLog.d("sdsfdfdsf startx = " + gRect.left + ",,,stopx = " + stopx);
            }
            //是否刷新，重新加载
            if( Math.abs( startx-endx ) > itemWidth && mFilterChangeListener!=null ){
                mFilterChangeListener.filterChange();
            }
        }
    };

    //------------------------------------------------------------------------------
    public interface ArrowImageChangeListener{
        void positionChage();
        void onTouchUp(float startx, float endx);
    }
}
