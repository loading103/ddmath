
package com.tsinghuabigdata.edu.ddmath.module.studyfeedback.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.module.neteaseim.base.util.ScreenUtil;
import com.tsinghuabigdata.edu.ddmath.util.DensityUtils;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;

/**
 * 曲线图类型
 */
public class ChartBtnView extends LinearLayout{

    private TextView   typeView;            //类型名称
    private View        selectView;         //选中效果

    public ChartBtnView(Context context) {
        super(context);
        init( context );
    }

    public ChartBtnView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init( context );
    }

    public ChartBtnView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init( context );
    }

    public void selected( boolean selected ){
        //typeView.setTextColor( selected? Color.rgb(0x48,0xB8,0xFF) : Color.rgb(0xAB,0xDC,0xFF) );
        selectView.setVisibility( selected? VISIBLE : GONE );
    }

    public void setTypeName( String type ){
        typeView.setText( type );

        int width = DensityUtils.sp2px( getContext(), GlobalData.isPad()?24:16);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)selectView.getLayoutParams();
        layoutParams.width = 2*width*2;
        selectView.setLayoutParams( layoutParams );
    }

    //-----------------------------------------------------------------------------------
    private void init(Context context){

        inflate( context, GlobalData.isPad()?R.layout.view_chart_btn:R.layout.view_chart_btn_phone, this );

        typeView = (TextView)findViewById( R.id.chart_btn_type );
        selectView  = findViewById( R.id.chart_btn_select );
    }
}
