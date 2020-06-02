package com.tsinghuabigdata.edu.ddmath.module.myscore;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.tsinghuabigdata.edu.ddmath.MVPModel.RequestListener;
import com.tsinghuabigdata.edu.ddmath.MVPModel.UserCenterModel;
import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.activity.RoboActivity;
import com.tsinghuabigdata.edu.ddmath.bean.UserDetailinfo;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.constant.AppConst;
import com.tsinghuabigdata.edu.ddmath.event.UpdateScoreEvent;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.view.WorkToolbar;
import com.tsinghuabigdata.edu.ddmath.module.myscore.adapter.ScoreProductAdapter;
import com.tsinghuabigdata.edu.ddmath.module.myscore.bean.ScoreProductList;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.AppLog;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.MyViewUtils;
import com.tsinghuabigdata.edu.ddmath.view.LoadingPager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;


/**
 * 积分兑换
 */

public class ScoreExchangeActivity extends RoboActivity implements PullToRefreshBase.OnRefreshListener<GridView> {

    //private            mWorktoolbar;

    private LoadingPager loadingPager;
    private TextView scoreView;
    private PullToRefreshGridView gridView;

    private int currPage = 1;

    private ScoreProductAdapter scoreProductAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        transparent = true;
        super.onCreate(savedInstanceState);

        //设置全屏显示
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (GlobalData.isPad()) {
            setContentView(R.layout.activity_score_exchange);
        } else {
            setContentView(R.layout.activity_score_exchange_phone);
        }
        initViews();
        loadData( false );
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onRefresh(PullToRefreshBase<GridView> pullToRefreshBase) {
        if (pullToRefreshBase.isHeaderShown()) {        //下拉重新加载
            currPage = 1;
            loadData( false );
        }else if( pullToRefreshBase.isFooterShown()){  //上拉加载更多
            loadData( true );
        }
    }
    @Subscribe
    public void receive(UpdateScoreEvent event){
        AppLog.d(" receive event = " + event );
        if(AccountUtils.userScoreBean!=null){
            scoreView.setText( String.valueOf(AccountUtils.userScoreBean.getTotalCredit()) );
        }
    }
    //------------------------------------------------------------------------------------------------------
    private void initViews() {
        WorkToolbar mWorktoolbar = findViewById(R.id.worktoolbar);
        mWorktoolbar.setTitle("积分兑换");
        mWorktoolbar.setRightTitle("兑换记录");
        mWorktoolbar.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity( new Intent(getBaseContext(),ExchangeRecordActivity.class));
            }
        });

        loadingPager = findViewById( R.id.loadingPager );
        LinearLayout linearLayout = findViewById( R.id.main_layout );
        loadingPager.setTargetView( linearLayout );
        loadingPager.setListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadData( false );
            }
        });

        scoreView = findViewById( R.id.tv_myscore );
        if(AccountUtils.userScoreBean!=null){
            scoreView.setText( String.valueOf(AccountUtils.userScoreBean.getTotalCredit()) );
        }

        scoreProductAdapter = new ScoreProductAdapter(this);
        gridView = findViewById( R.id.girdview_product );
        gridView.setAdapter( scoreProductAdapter );

        MyViewUtils.setPTRText( this, gridView);
        gridView.setOnRefreshListener(this);
        gridView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
    }

    private void loadData(final boolean nextPage) {

        UserDetailinfo userDetailinfo = AccountUtils.getUserdetailInfo();
        if( userDetailinfo==null ) return;

        loadingPager.showLoading();

        new UserCenterModel().queryProductList(userDetailinfo.getStudentId(), nextPage?currPage+1:currPage, AppConst.MAX_PAGE_NUM, new RequestListener<ScoreProductList>() {
            @Override
            public void onSuccess(ScoreProductList res) {
                loadingPager.showTarget();
                if( res == null || res.getProductList()==null || res.getProductList().size() == 0 ){
                    if(nextPage){
                        gridView.setMode(PullToRefreshBase.Mode.DISABLED);      //不能
                    }else{
                        loadingPager.showEmpty();
                    }
                    return;
                }
                if( !nextPage && currPage==1 ){
                    scoreProductAdapter.clear();
                }
                scoreProductAdapter.addAll( res.getProductList() );
                scoreProductAdapter.notifyDataSetChanged();

                if( nextPage ) currPage += 1;
                if( currPage >= res.getTotalPage() ){
                    gridView.setMode(PullToRefreshBase.Mode.DISABLED);
                }
            }

            @Override
            public void onFail(HttpResponse<ScoreProductList> response, Exception ex) {
                loadingPager.showFault( ex );
            }
        });
    }

}
