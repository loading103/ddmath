package com.tsinghuabigdata.edu.ddmath.module.errorbook.view;


import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.HeadImageUtils;
import com.tsinghuabigdata.edu.ddmath.view.CircleImageView;

/**
 * 用户等级自己的头像
 */
public class UserHeaderView extends RelativeLayout{

    //
    private CircleImageView headerView;

    public UserHeaderView(Context context) {
        super(context);
        init(context);
    }

    public UserHeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public UserHeaderView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init( context);
    }

    public void setHeaderImage( String url ){
        HeadImageUtils.showHeadImage( getContext(), url, headerView );
    }

    //-------------------------------------------------------------------------
    private void init(Context context){
        inflate( context, GlobalData.isPad()?R.layout.view_errbook_userheader :R.layout.view_errbook_userheader_phone, this );

        headerView = (CircleImageView) findViewById( R.id.userbank_headerimg ) ;
    }

}
