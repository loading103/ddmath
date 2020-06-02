package com.tsinghuabigdata.edu.ddmath.module.exclusivepractice.view;


import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;

/**
 * 免费使用权
 */
public class FreeDroitView extends LinearLayout{


    private TextView freeDroitView;

    public FreeDroitView(Context context) {
        super(context);
        init(context);
    }

    public FreeDroitView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public FreeDroitView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init( context);
    }

    /**
     * 设置数据
     */
    public void setData( int freecount ){
        freeDroitView.setText( String.format( getContext().getResources().getString( R.string.epractice_free_count ), freecount ) );
    }

    //-------------------------------------------------------------------------
    private void init(Context context){
        inflate( context, GlobalData.isPad()?R.layout.view_practice_freedroit :R.layout.view_practice_freedroit_phone, this );
        freeDroitView = (TextView)findViewById( R.id.item_practice_freecount );
    }

}
