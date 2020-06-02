package com.tsinghuabigdata.edu.ddmath.module.myscore.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.tsinghuabigdata.edu.ddmath.MVPModel.RequestListener;
import com.tsinghuabigdata.edu.ddmath.MVPModel.UserCenterModel;
import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.bean.UserDetailinfo;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.constant.AppConst;
import com.tsinghuabigdata.edu.ddmath.event.AssignNewWorkEvent;
import com.tsinghuabigdata.edu.ddmath.event.SchoolWorkEvent;
import com.tsinghuabigdata.edu.ddmath.event.UpdateScoreEvent;
import com.tsinghuabigdata.edu.ddmath.fragment.MyBaseFragment;
import com.tsinghuabigdata.edu.ddmath.module.myscore.ScoreExchangeActivity;
import com.tsinghuabigdata.edu.ddmath.module.myscore.adapter.MyScoreAdapter;
import com.tsinghuabigdata.edu.ddmath.module.myscore.bean.MyScoreBean;
import com.tsinghuabigdata.edu.ddmath.module.myscore.bean.ScoreRecordResult;
import com.tsinghuabigdata.edu.ddmath.module.myscore.bean.UserScoreBean;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.AppLog;
import com.tsinghuabigdata.edu.ddmath.util.DateUtils;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.MyViewUtils;
import com.tsinghuabigdata.edu.ddmath.view.LoadingPager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Locale;

import javax.annotation.Nonnull;


/**
 * 我的积分
 */
public class MyScoreFragment extends MyBaseFragment implements View.OnClickListener, PullToRefreshBase.OnRefreshListener<ListView>  {


    private LoadingPager mLoadingPager;

    private PullToRefreshListView mListView;

    private TextView     mTotalScoreView;
    private TextView     mTodayScoreView;

    private int currPage = 1;
    private MyScoreAdapter myScoreAdapter;


    @Nullable
    @Override
    public View onCreateView(@Nonnull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root;
        if (GlobalData.isPad()) {
            root = inflater.inflate(R.layout.fragment_my_score, container, false);
        } else {
            root = inflater.inflate(R.layout.fragment_my_score_phone, container, false);
        }
        initView(root);
        setPrepared();
        loadData();
        EventBus.getDefault().register(this);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onClick(View v) {
        if( R.id.btn_score_exchange == v.getId() ){
            startActivity( new Intent( getContext(), ScoreExchangeActivity.class) );
        }
    }

    @Override
    public void onRefresh(PullToRefreshBase<ListView> pullToRefreshBase) {
        //下拉重新加载
        if (pullToRefreshBase.isHeaderShown()) {
            currPage = 1;
            loadData();
        }
        //上拉加载更多
        else if( pullToRefreshBase.isFooterShown() ){
            queryScoreRecordList( false, true);
        }
    }
    //----------------------------------------------------------------------------------------------
    private void initView(View root) {
        mLoadingPager = root.findViewById(R.id.loadingPager);
        LinearLayout mainLayout = root.findViewById( R.id.main_layout );
        mTotalScoreView = root.findViewById( R.id.tv_tatol_score );
        mTodayScoreView = root.findViewById( R.id.tv_today_score );

        mListView = root.findViewById( R.id.lv_score_listview );
        MyViewUtils.setPTRText( getContext(), mListView);
        mListView.setOnRefreshListener(this);
        mListView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);

        myScoreAdapter = new MyScoreAdapter( getContext() );
        mListView.setAdapter( myScoreAdapter );

        Button btn = root.findViewById( R.id.btn_score_exchange );
        btn.setOnClickListener( this );

        mLoadingPager.setTargetView(mainLayout);
        mLoadingPager.setListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLoadingPager.showLoading();
                loadData();
            }
        });
    }

    private void loadData(){
        mLoadingPager.showLoading();
        queryUserScore( true );
    }

    //查询积分
    private void queryUserScore( final boolean init ) {
        UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
        if (detailinfo == null) {
            if(init) mLoadingPager.showFault();
            return;
        }

        new UserCenterModel().queryUserScore(detailinfo.getStudentId(), new RequestListener<UserScoreBean>() {

            @Override
            public void onSuccess(UserScoreBean bean) {
                if (bean != null) {
                    AccountUtils.userScoreBean = bean;
                    showScoreData(bean);
                    if(init) queryScoreCommandList( true );
                }
            }

            @Override
            public void onFail(HttpResponse<UserScoreBean> response, Exception ex) {
                if(init)mLoadingPager.showFault( ex );
            }
        });
    }

    //查询推荐积分列表
    private void queryScoreCommandList( final boolean init ){
        UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
        if (detailinfo == null) {
            if(init) mLoadingPager.showFault();
            return;
        }

        new UserCenterModel().queryCommandScoreList(detailinfo.getStudentId(), new RequestListener<ArrayList<MyScoreBean>>() {

            @Override
            public void onSuccess(ArrayList<MyScoreBean> list) {
                if (list != null && list.size() != 0) {
                    queryScoreRecordList( init, false );

                    myScoreAdapter.clear();

                    //标题
                    myScoreAdapter.add( new MyScoreBean(MyScoreBean.TYPE_COMMAND_TITLE));
                    for( MyScoreBean scoreBean: list ){
                        scoreBean.setType( MyScoreBean.TYPE_COMMAND_ITEM);
                    }
                    // 排序依次是：去完成、已领取、去完成置灰
                    myScoreAdapter.addAll( list );
                    myScoreAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFail(HttpResponse<ArrayList<MyScoreBean>> response, Exception ex) {
                if(init)mLoadingPager.showFault( ex );
            }
        });
    }

    //查询积分记录列表
    private void queryScoreRecordList( final boolean init, final boolean nextpage ){
        UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
        if (detailinfo == null) {
            if(init) mLoadingPager.showFault();
            return;
        }

        int page = currPage + (nextpage?1:0);
        long startime = DateUtils.getRecentDay(2);      //三天前的00:00:00时间
        //AppLog.d("--hfjksdfhjdsk starttime = " + DateUtils.format(startime,DateUtils.FORMAT_DATA_TIME));
        new UserCenterModel().queryScoreRecordList( detailinfo.getStudentId(),  page, AppConst.MAX_PAGE_NUM, startime, System.currentTimeMillis(), new RequestListener<ScoreRecordResult>() {

            @Override
            public void onSuccess(ScoreRecordResult result) {
                mListView.onRefreshComplete();
                mLoadingPager.showTarget();
                //标题
                if(!nextpage) myScoreAdapter.add( new MyScoreBean(MyScoreBean.TYPE_RECORD_TITLE));
                if (result != null) {
                    if( result.getItems()!=null && result.getItems().size() > 0 ){

                        ArrayList<MyScoreBean> list = result.getItems();
                        for( MyScoreBean scoreBean: list ){
                            scoreBean.setType( MyScoreBean.TYPE_RECORD_ITEM);
                        }
                        myScoreAdapter.addAll( list );
                    }else if( !nextpage && currPage == 1 ){
                        myScoreAdapter.add( new MyScoreBean(MyScoreBean.TYPE_EMPTY_DATA));
                    }

                    if( nextpage ) currPage += 1;
                    if( currPage < result.getTotalPage() ){
                        mListView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
                    }else{
                        mListView.setMode(PullToRefreshBase.Mode.DISABLED);
                    }
                }else{
                    myScoreAdapter.add( new MyScoreBean(MyScoreBean.TYPE_EMPTY_DATA));

                    mListView.setMode(PullToRefreshBase.Mode.DISABLED);
                }
                myScoreAdapter.notifyDataSetChanged();

            }

            @Override
            public void onFail(HttpResponse<ScoreRecordResult> response, Exception ex) {
                mListView.onRefreshComplete();
                if(init)mLoadingPager.showFault( ex );
            }
        });
    }

    //显示积分
    private void showScoreData(UserScoreBean scoreBean) {
        mTotalScoreView.setText( String.valueOf( scoreBean.getTotalCredit() ) );
        mTodayScoreView.setText( String.format(Locale.getDefault(), "+%d", scoreBean.getTodayCredit()));
    }

    @Subscribe
    public void receive(UpdateScoreEvent event){
        AppLog.d(" receive event = " + event );
        queryUserScore( false );
        if( event.isAdd() ){
            queryScoreCommandList( false );
            //queryScoreRecordList( false, false );
        }
    }
    @Subscribe
    public void receive(AssignNewWorkEvent event){
        AppLog.d(" receive event = " + event );
        queryScoreCommandList( false );
    }
    @Subscribe
    public void receive(SchoolWorkEvent event){
        AppLog.d(" receive event = " + event );
        queryScoreCommandList( false );
    }

    public String getUmEventName() {
        return "mycenter_myscore";
    }

}
