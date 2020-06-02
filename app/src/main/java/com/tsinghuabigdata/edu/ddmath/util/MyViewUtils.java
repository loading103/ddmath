package com.tsinghuabigdata.edu.ddmath.util;

import android.content.Context;

import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.tsinghuabigdata.edu.ddmath.R;

/**
 * Created by Administrator on 2017/9/27.
 */

public class MyViewUtils {

    public static void setPTRText(Context context, PullToRefreshBase ptrView) {
        ptrView.setMode(PullToRefreshBase.Mode.BOTH);
        ILoadingLayout startLabels = ptrView.getLoadingLayoutProxy(true, false);
        startLabels.setPullLabel("下拉刷新");
        startLabels.setRefreshingLabel("正在刷新...");
        startLabels.setReleaseLabel("松开刷新");
        startLabels.setLoadingDrawable(context.getResources().getDrawable(R.drawable.refresh_loading_anim));

        ILoadingLayout endLabels = ptrView.getLoadingLayoutProxy(false, true);
        endLabels.setPullLabel("加载更多");
        endLabels.setRefreshingLabel("正在加载...");
        endLabels.setReleaseLabel("加载更多");
        ptrView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
    }

}
