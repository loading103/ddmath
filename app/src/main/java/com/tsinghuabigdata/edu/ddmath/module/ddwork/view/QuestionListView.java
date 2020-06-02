package com.tsinghuabigdata.edu.ddmath.module.ddwork.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AbsListView;
import android.widget.ListView;

import com.tsinghuabigdata.edu.ddmath.module.ddwork.PageChangeListener;

/**
 *
 */

public class QuestionListView extends /*PullToRefresh*/ListView {

    private PageChangeListener pageChangeListener;

    private boolean pageSelectEnable;

    private int oldFirstVisibleItem = -1;

    public QuestionListView(Context context) {
        super(context);
        init();
    }

    public QuestionListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public QuestionListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public void reset(){
        oldFirstVisibleItem = -1;
    }
//    public QuestionListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes){
//        super( context,attrs,defStyleAttr,defStyleRes );
//        init();
//    }

//    public QuestionListView(Context context, Mode mode) {
//        super(context, mode);
//        init();
//    }
//
//    public QuestionListView(Context context, Mode mode, AnimationStyle style) {
//        super(context, mode, style);
//        init();
//    }

    public void setPageChangeListener( PageChangeListener listener ){
        pageChangeListener = listener;
    }

    public void setPageSelectEnable(boolean pageSelectEnable) {
        this.pageSelectEnable = pageSelectEnable;
    }

    //    public void setRefleshMode( int position, int count ){
//        if( position == 0 && count == 1 )
//            setMode( Mode.DISABLED );
//        else if( position == 0 && position < count ){
//            setMode( Mode.PULL_FROM_END );
//        }else if( position+1 == count ){
//            setMode( Mode.PULL_FROM_START );
//        }else {
//            setMode( Mode.BOTH );
//        }
//    }
    //
    private void init(){
//        ILoadingLayout loadingLayout = getLoadingLayoutProxy(true, true);
//        loadingLayout.setPullLabel("ddd");
//        loadingLayout.setRefreshingLabel("hhhhhh");
//        loadingLayout.setLastUpdatedLabel("释放换页");
//        loadingLayout.setReleaseLabel("rrrrrr");
//        loadingLayout.setLoadingDrawable( null );
//
//        setMode(PullToRefreshBase.Mode.BOTH);
//        //监听
//        setOnRefreshListener( new PullToRefreshBase.OnRefreshListener2<ListView>(){
//
//            @Override
//            public void onPullDownToRefresh(PullToRefreshBase<ListView> pullToRefreshBase) {
//                if( pageChangeListener!=null )
//                    pageChangeListener.pageUp();
//                listViewRefreshComplete();
//            }
//
//            @Override
//            public void onPullUpToRefresh(PullToRefreshBase<ListView> pullToRefreshBase) {
//                if( pageChangeListener!=null )
//                    pageChangeListener.pageDown();
//                listViewRefreshComplete();
//            }
//        });

        //AppLog.d("fdfdsfdsfds init");
        setOnScrollListener(new OnScrollListener() {
            int listViewScrollState = OnScrollListener.SCROLL_STATE_IDLE;


            /**
             * ListView的状态改变时触发
             */
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                listViewScrollState = scrollState;
                switch(scrollState){
                    case OnScrollListener.SCROLL_STATE_IDLE://空闲状态
                        //AppLog.d("fdfdsfdsfds IDLE");
                        break;
                    case OnScrollListener.SCROLL_STATE_FLING://滚动状态
                        //AppLog.d("fdfdsfdsfds FLING");
                        break;
                    case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL://触摸后滚动
                        //AppLog.d("fdfdsfdsfds SCROLL");
                        break;
                    default:
                        break;
                }
            }

            /**
             * 正在滚动
             * firstVisibleItem第一个Item的位置
             * visibleItemCount 可见的Item的数量
             * totalItemCount item的总数
             */
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                //控制显示和隐藏的代码...

                if( !pageSelectEnable || oldFirstVisibleItem == firstVisibleItem || totalItemCount==0 || listViewScrollState == OnScrollListener.SCROLL_STATE_IDLE )
                    return;
                oldFirstVisibleItem = firstVisibleItem;
                if( pageChangeListener!=null )  pageChangeListener.pageChange( firstVisibleItem );
                //AppLog.d("fdfdsfdsfds firstVisibleItem = "+firstVisibleItem + ",,, visibleItemCount = "+ visibleItemCount + ",,,totalItemCount = "+totalItemCount );
            }
        });

    }

//    private void listViewRefreshComplete(){
//        post(new Runnable() {
//            @Override
//            public void run() {
//                onRefreshComplete();
//            }
//        });
//    }

}
