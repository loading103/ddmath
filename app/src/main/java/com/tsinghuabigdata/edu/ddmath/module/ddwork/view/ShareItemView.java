package com.tsinghuabigdata.edu.ddmath.module.ddwork.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.ShareBean;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;

/**
 * 分享项View
 */
public class ShareItemView extends LinearLayout{

    private TextView startView;
    private TextView middleView;
    private TextView endView;

    private TextView nameView;

    public ShareItemView(Context context) {
        super(context);
        init();
    }

    public ShareItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ShareItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setData(ShareBean shareBean){

        //设置连击数据
        if( ShareBean.TYPE_SERIESHIT == shareBean.getType() ){
            nameView.setText( "连击" );
            middleView.setText( String.valueOf( shareBean.getValue()) );
            //endView.setText( "X " + shareBean.getTimes() );
        }
        //设置正确率
        else if( ShareBean.TYPE_RIGHTRATE == shareBean.getType() ){
            nameView.setText(TextUtils.isEmpty(shareBean.getName())?"正确率":shareBean.getName());
            middleView.setText( String.valueOf(shareBean.getValue()) );
            endView.setText( "%" );
        }
        //提交名称
        else if( ShareBean.TYPE_SUBMITWORK == shareBean.getType() ){
            nameView.setText( "提交" );

            startView.setText("第");
            middleView.setText( String.valueOf(shareBean.getValue()) );
            endView.setText( "个" );
        }
        //班级排名
        else if( ShareBean.TYPE_CLASSRANK == shareBean.getType() ){
            nameView.setText( "班级" );

            startView.setText("第");
            middleView.setText( String.valueOf(shareBean.getValue()) );
            endView.setText( "名" );
        }
    }

    //-----------------------------------------------------------------------------
    private void init() {

        inflate( getContext(), GlobalData.isPad()?R.layout.view_ddwork_shareitem:R.layout.view_ddwork_shareitem_phone, this );

        startView  = (TextView) findViewById( R.id.view_startview );
        middleView = (TextView) findViewById( R.id.view_midview );
        endView    = (TextView) findViewById( R.id.view_endview );
        nameView   = (TextView) findViewById( R.id.view_nameview );
    }

}
