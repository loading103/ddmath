package com.tsinghuabigdata.edu.ddmath.module.studycheat.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.module.neteaseim.base.util.TimeUtil;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;


/**
 * 提分秘籍任务View
 */
public class TimeView extends LinearLayout{

    private TextView  minitusView;
    private TextView  minitusLabel;
    private TextView  secondView;
    private TextView  secondLabel;

    public TimeView(Context context) {
        super(context);
        init();
    }

    public TimeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TimeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP )
    public TimeView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public void setText( int time, int color1, int color2 ){
        //minitusView.setTextColor( color2 );
        minitusView.setText( String.valueOf(TimeUtil.getTimeValue(time,1)) );
        //minitusLabel.setTextColor( color1 );

        //secondView.setTextColor( color2 );
        secondView.setText( String.valueOf(TimeUtil.getTimeValue(time,0)) );
        //secondLabel.setTextColor( color1 );
    }

    //-----------------------------------------------------------------------------------
    private void init(){
        inflate( getContext(), GlobalData.isPad()?R.layout.view_cheat_time:R.layout.view_cheat_time_phone, this);

        minitusView  = (TextView)findViewById( R.id.cheattask_time_minuteview );
        minitusLabel = (TextView)findViewById( R.id.cheattask_time_minutelabel );
        secondView = (TextView)findViewById( R.id.cheattask_time_secondview );
        secondLabel  = (TextView)findViewById( R.id.cheattask_time_secondlabel );
    }
}
