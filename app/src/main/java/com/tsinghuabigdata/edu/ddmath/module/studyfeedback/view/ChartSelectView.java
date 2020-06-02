
package com.tsinghuabigdata.edu.ddmath.module.studyfeedback.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;

/**
 * 曲线图选择中的显示
 */
public class ChartSelectView extends LinearLayout{

    private TextView  rateValueView;        //得分率
    private TextView  workNameView;         //作业名称
    private TextView  workScoreView;        //得分详情

    private Drawable bgDrawable;

    public ChartSelectView(Context context) {
        super(context);
        init( context );
    }

    public ChartSelectView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init( context );
    }

    public ChartSelectView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init( context );
    }

    public void setData( int rate, String name, String score ){
        rateValueView.setText( ""+rate+"%");
        workNameView.setText( name );
        workScoreView.setText( score );
    }

    //-----------------------------------------------------------------------------------
    private void init(Context context){

        inflate( context, GlobalData.isPad()?R.layout.view_chart_select:R.layout.view_chart_select_phone, this );

        rateValueView = (TextView)findViewById( R.id.studyfb_workexam_valuetxt );        //得分率
        workNameView  = (TextView)findViewById( R.id.studentexam_select_nametxt );       //作业名称
        workScoreView = (TextView)findViewById( R.id.studentexam_select_scoretxt );      //得分详情

    }
}
