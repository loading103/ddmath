package com.tsinghuabigdata.edu.ddmath.module.badge;

import android.content.Context;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.BadgeUtil;

/**
 * 桌面ICON小红点管理器
 */
public class BadgeManager {

    private int count = 0;      //当前小红点的数量
    private Context context;

    public BadgeManager( Context context ){
        this.context = context;
        count = getCount();
    }

    public void addBadge( int c ){
        if(AccountUtils.getLoginParent()!=null) return;
        count += c;
        saveCount();
        new BadgeUtil().setBadgeCount( context, count, R.drawable.ic_launcher );
    }

    public void resetBadge(int c){
        if(AccountUtils.getLoginParent()!=null) return;
        count = c;
        saveCount();
        new BadgeUtil().setBadgeCount( context, count, R.drawable.ic_launcher );
    }

    public void removeBadge(){
        if(AccountUtils.getLoginParent()!=null) return;
        count--;
        if( count<=0 ){
            count = 0;
            new BadgeUtil().resetBadgeCount( context, R.drawable.ic_launcher );
        }else{
            new BadgeUtil().setBadgeCount( context, count, R.drawable.ic_launcher );
        }
        saveCount();
    }

    //------------------------------------------------------------------------------------------
    private int getCount(){
        return context.getSharedPreferences("ddmath_badge", Context.MODE_PRIVATE ).getInt( "count", 0 );
    }

    private void saveCount(){
        context.getSharedPreferences("ddmath_badge", Context.MODE_PRIVATE ).edit().putInt("count", count ).apply();
    }

}
