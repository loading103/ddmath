package com.tsinghuabigdata.edu.ddmath.module.exclusivepractice.view;


import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.bean.UserDetailinfo;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;

/**
 * 专属标记View
 */
public class ExclusiveImageView extends LinearLayout{

    //private TextView userNameView;

    public ExclusiveImageView(Context context) {
        super(context);
        init(context);
    }

    public ExclusiveImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ExclusiveImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init( context);
    }

    //-------------------------------------------------------------------------
    private void init(Context context){
        inflate( context, GlobalData.isPad()?R.layout.view_practice_exclusive :R.layout.view_practice_exclusive_phone, this );
        TextView userNameView = (TextView)findViewById( R.id.item_practice_username );

        UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
        if( detailinfo == null ){
            userNameView.setVisibility( View.GONE );
            return;
        }

        userNameView.setVisibility( View.VISIBLE );
        String username = detailinfo.getReallyName();
        if( username == null || username.length() == 0 ){
            username = "专属";
        }else {
            username = getShortName( username );
        }
        userNameView.setText( username );
    }

    //保证汉字最多显示5个，英文10个字母
    private String getShortName( String name ){
        int max = 10;
        int count = 0;
        StringBuilder sb = new StringBuilder();

        for( int i=0; i<name.length(); i++ ){
            if( count >= max )
                break;
            int ch = name.charAt(i);
            count++;
            //汉字
            if( ch >= 0x80 ) count++;
            sb.append( name.charAt( i ) );
        }
        return sb.toString();
    }

}
