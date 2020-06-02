package com.tsinghuabigdata.edu.ddmath.fragment;

import android.os.Handler;
import android.text.TextUtils;

import androidx.fragment.app.Fragment;

import com.tsinghuabigdata.edu.ddmath.module.floatwindow.FloatActionController;
import com.umeng.analytics.MobclickAgent;

public abstract class MyBaseFragment extends Fragment {
    //private static final String TAG = "MyBaseFragment";

    protected boolean first;
    // 标志位，标志已经初始化完成。
    protected boolean isPrepared;

    /**
     * 在这里实现Fragment数据的缓加载.
     *
     * @param isVisibleToUser boolean
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
            onVisible();
        } else {
            onInvisible();
        }
    }

    protected void onVisible() {
        if (isPrepared) {
            if (!first) {
                first = true;
                lazyLoad();
            }
            //增加友盟事件调用
            if( !TextUtils.isEmpty(getUmEventName())){
                MobclickAgent.onEvent( getContext(),getUmEventName() );
            }
            FloatActionController.getInstance().setCurrUiName( getUmEventName() );
        } else {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    onVisible();
                }
            }, 300);
        }

    }

    protected void lazyLoad() {
    }

    protected void onInvisible() {
    }

    public void setPrepared() {
        isPrepared = true;
    }

    //友盟记录点击事件次数
    public String getUmEventName() {
        return null;
    }
}
