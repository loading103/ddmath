package com.tsinghuabigdata.edu.ddmath.dialog;

import android.app.Dialog;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tsinghuabigdata.edu.ddmath.util.AppLog;

import java.util.ArrayList;


/**
 * 可控Dialog，优先级高的先显示
 */
public class BaseDialog extends Dialog{

    protected int level = 1;        //默认1,最低

    private static ArrayList<BaseDialog> list = new ArrayList<>();

    public BaseDialog(Context context) {
        super(context);
        list.add( this );
    }

    public BaseDialog(Context context, int theme) {
        super(context, theme);
        list.add( this );
    }

    protected BaseDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        list.add( this );
    }

    public void setLevel(int level){
        this.level = level;
    }

    /**
     * 如果显示层级底，则不显示，如果，层级高，则优先显示
     */
    @Override
    public void show(){
        AppLog.d("dsdsfdsfds show level = " + level + ",,, size = " + list.size());
        //仅仅自己一个
        if( list.size() == 1 ){
            super.show();
            return;
        }
        //找到当前正在显示的dialog
        BaseDialog showDiaolog = null;
        for(BaseDialog dialog : list){
            //不是自己,且正在显示
            if( dialog!=this && dialog.isShowing()){
                showDiaolog = dialog;
                break;
            }
        }

        //比较显示层级 层级低于要显示的，则暂不显示了,优先显示层级高的
        if( showDiaolog == null ){
            super.show();
        }else if( showDiaolog.level <= level ){
            showDiaolog.dismiss();
            super.show();       //显示
        }
    }

    /**
     * 删除缓存，否则会内存泄漏
     */
    public void finishDismiss(){
        list.remove(this);
        dismiss();

        //还有隐藏的dialog
        if( list.size() > 0 ){
            BaseDialog showDiaolog = list.get(0);
            for( BaseDialog dialog : list ){
                if( dialog!=showDiaolog && dialog.level > showDiaolog.level ){
                    showDiaolog = dialog;
                }
            }
            showDiaolog.show();
        }
    }

}
