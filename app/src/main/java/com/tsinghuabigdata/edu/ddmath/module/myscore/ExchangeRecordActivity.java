package com.tsinghuabigdata.edu.ddmath.module.myscore;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.tsinghuabigdata.edu.ddmath.MVPModel.RequestListener;
import com.tsinghuabigdata.edu.ddmath.MVPModel.UserCenterModel;
import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.activity.RoboActivity;
import com.tsinghuabigdata.edu.ddmath.bean.UserDetailinfo;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.constant.AppConst;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.view.WorkToolbar;
import com.tsinghuabigdata.edu.ddmath.module.myscore.adapter.ExchangeRecordAdapter;
import com.tsinghuabigdata.edu.ddmath.module.myscore.bean.ExchangeRecordList;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.MyViewUtils;
import com.tsinghuabigdata.edu.ddmath.view.LoadingPager;


/**
 * 积分记录
 */

public class ExchangeRecordActivity extends RoboActivity implements PullToRefreshBase.OnRefreshListener<ListView> {

    //private            mWorktoolbar;

    private LoadingPager loadingPager;
    //private TextView scoreView;
    private PullToRefreshListView listView;

    private int currPage = 1;

    private ExchangeRecordAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        transparent = true;
        super.onCreate(savedInstanceState);

        //设置全屏显示
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (GlobalData.isPad()) {
            setContentView(R.layout.activity_exchange_record);
        } else {
            setContentView(R.layout.activity_exchange_record_phone);
        }
        initViews();
        loadData( false );
    }

    @Override
    public void onRefresh(PullToRefreshBase<ListView> pullToRefreshBase) {
        if (pullToRefreshBase.isHeaderShown()) {        //下拉重新加载
            currPage = 1;
            loadData( false );
        }else if( pullToRefreshBase.isFooterShown()){  //上拉加载更多
            loadData( true );
        }
    }

    //------------------------------------------------------------------------------------------------------
    private void initViews() {
        WorkToolbar mWorktoolbar = findViewById(R.id.worktoolbar);
        mWorktoolbar.setTitle("兑换记录");
        mWorktoolbar.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        }, null);

        loadingPager = findViewById( R.id.loadingPager );
        listView = findViewById( R.id.listview );

        loadingPager.setTargetView( listView );
        loadingPager.setListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadData( false );
            }
        });

        mAdapter = new ExchangeRecordAdapter(this);
        listView.setAdapter( mAdapter );

        MyViewUtils.setPTRText( this, listView);
        listView.setOnRefreshListener(this);
        listView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
    }

    private void loadData(final boolean nextPage) {

        UserDetailinfo userDetailinfo = AccountUtils.getUserdetailInfo();
        if( userDetailinfo==null ) return;

        loadingPager.showLoading();

        new UserCenterModel().queryExchangeRecordList(userDetailinfo.getStudentId(), nextPage?currPage+1:currPage, AppConst.MAX_PAGE_NUM, new RequestListener<ExchangeRecordList>() {
            @Override
            public void onSuccess(ExchangeRecordList res) {
                listView.onRefreshComplete();
                loadingPager.showTarget();
                if( res == null || res.getItems()==null || res.getItems().size() == 0 ){
                    if(nextPage){
                        listView.setMode(PullToRefreshBase.Mode.DISABLED);      //不能
                    }else{
                        loadingPager.showEmpty();
                    }
                    return;
                }
                if( !nextPage && currPage==1 ){
                    mAdapter.clear();
                }
                mAdapter.addAll( res.getItems() );
                mAdapter.notifyDataSetChanged();

                if( nextPage ) currPage += 1;
                if( currPage >= res.getTotalPage() ){
                    listView.setMode(PullToRefreshBase.Mode.DISABLED);
                }
            }

            @Override
            public void onFail(HttpResponse<ExchangeRecordList> response, Exception ex) {
                listView.onRefreshComplete();
                loadingPager.showFault( ex );
            }
        });
    }

}
