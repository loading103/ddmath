package com.tsinghuabigdata.edu.ddmath.parent.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tsinghuabigdata.edu.ddmath.R;

/**
 * 家长端toolbar
 */
public class ParentToolbar extends LinearLayout implements View.OnClickListener{

    //返回按钮
    private LinearLayout leftView;

    //中间标题
    private TextView titleView;

    //右边按钮
    private LinearLayout rightLayout;
    private LinearLayout right1BtnLayout;
    private ImageView rightView1;

    private LinearLayout right2BtnLayout;
    private ImageView rightView2;
    private TextView rightTextView2;

    private OnClickListener leftListener;
    private OnClickListener right1Listener;
    private OnClickListener right2Listener;

    public ParentToolbar(Context context) {
        super(context);
        init();
    }

    public ParentToolbar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ParentToolbar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.toolbar_lefttitle:
                if( leftListener!=null ) leftListener.onClick( v );
                break;
            case R.id.toolbar_rightbtn1_layuout:
                if( right1Listener!=null ) right1Listener.onClick( v );
                break;
            case R.id.toolbar_rightbtn2_layuout:
                if( right2Listener!=null ) right2Listener.onClick( v );
                break;
            default:
                break;
        }
    }

    /**
     * 回调
     * @param llistener  左按钮回调
     */
    public void setClickListener(OnClickListener llistener){
        leftListener = llistener;
    }
    public void setClickListener(OnClickListener llistener, OnClickListener r1listener, OnClickListener r2listener){
        leftListener = llistener;
        right1Listener = r1listener;
        right2Listener = r2listener;

        rightView1.setVisibility( right1Listener!=null? VISIBLE : GONE );
        rightView2.setVisibility( right2Listener!=null? VISIBLE : GONE );
        rightLayout.setVisibility( VISIBLE );
    }

    public void setRightBtnImage( int rid1, int rid2 ){
        rightView1.setVisibility(GONE);
        if( rid1!=0 ){
            rightView1.setVisibility(VISIBLE);
            rightView1.setImageResource( rid1 );
        }
        rightView2.setVisibility(GONE);
        if( rid2!=0 ){
            rightView2.setVisibility(VISIBLE);
            rightView2.setImageResource( rid2 );
        }
    }
    public void setRightText(String text){
        rightTextView2.setText( text );
        rightView2.setVisibility(GONE);
    }

    public void setTitle( String title ){
        titleView.setText( title );
    }

    //-----------------------------------------------------------------------------
    private void init() {

        inflate( getContext(),R.layout.parent_toolbar_layout, this );

        //mainLayout = (RelativeLayout)findViewById( R.id.toolbar );
        leftView = (LinearLayout) findViewById( R.id.toolbar_lefttitle );

        titleView = (TextView)findViewById( R.id.toolbar_title );

        rightLayout = (LinearLayout)findViewById( R.id.toolbar_right_layuout );
        right1BtnLayout = (LinearLayout)findViewById( R.id.toolbar_rightbtn1_layuout );
        rightView1 = (ImageView) findViewById( R.id.toolbar_right1_image );
        right2BtnLayout = (LinearLayout)findViewById( R.id.toolbar_rightbtn2_layuout );
        rightView2 = (ImageView) findViewById( R.id.toolbar_right2_image );
        rightTextView2 = (TextView)findViewById( R.id.toolbar_right2_text );

        leftView.setOnClickListener( this );
        right1BtnLayout.setOnClickListener( this );
        right2BtnLayout.setOnClickListener( this );
    }

}
