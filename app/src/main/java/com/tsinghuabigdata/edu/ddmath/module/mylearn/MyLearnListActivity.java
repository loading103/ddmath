package com.tsinghuabigdata.edu.ddmath.module.mylearn;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.activity.RoboActivity;
import com.tsinghuabigdata.edu.ddmath.bean.LoginInfo;
import com.tsinghuabigdata.edu.ddmath.bean.MyTutorClassInfo;
import com.tsinghuabigdata.edu.ddmath.bean.UserDetailinfo;
import com.tsinghuabigdata.edu.ddmath.commons.http.AppAsyncTask;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.view.WorkToolbar;
import com.tsinghuabigdata.edu.ddmath.module.mylearn.adapter.WorkAdapter;
import com.tsinghuabigdata.edu.ddmath.module.mylearn.bean.WorkBean;
import com.tsinghuabigdata.edu.ddmath.requestHandler.MyLearnService;
import com.tsinghuabigdata.edu.ddmath.requestHandler.requestImpl.MyLearnServiceImpl;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.AlertManager;
import com.tsinghuabigdata.edu.ddmath.util.DateUtils;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.view.LoadingPager;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * 作业列表
 */
public class MyLearnListActivity extends RoboActivity {

    //
    private ListView     mListView;
    private LoadingPager mLoadingPager;

    private MyLearnService myLearnService;

    private String mDate = "";

    private GetWorkListTask mGetWorkListTask;
    private WorkAdapter mWorkAdapter;

    private Context mContext;

    @Override
    protected void onCreate( Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(GlobalData.isPad()?R.layout.activity_scwork_list:R.layout.activity_scwork_list_phone);
        mContext = this;

        if( !parseIntent() ){
            AlertManager.toast( this, "参数错误" );
            finish();
            return;
        }
        initView();
        loadData();
    }

    //----------------------------------------------------------------------
    private boolean parseIntent(){
        myLearnService = new MyLearnServiceImpl();

        Intent intent = getIntent();
        if( intent.hasExtra("date") ){
            mDate = intent.getStringExtra("date");
        }
        return !TextUtils.isEmpty( mDate );
    }
    private void initView(){
        WorkToolbar workToolbar = findViewById( R.id.work_toolbar );
        workToolbar.setTitle( getTitleStr() );
        workToolbar.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        },null);

        mListView = findViewById(R.id.mylearn_work_list);
        mLoadingPager =  findViewById(R.id.loadingPager);

        mWorkAdapter = new WorkAdapter( mContext, 0 );
        mWorkAdapter.setParentListView( mListView );
        mListView.setAdapter( mWorkAdapter );

        View rootView = LayoutInflater.from(this).inflate( R.layout.view_list_footer, null, false );
        mListView.addFooterView( rootView );

        mLoadingPager.setListener(new View.OnClickListener() {           //失败点击重新加载
            @Override
            public void onClick(View v) {
                loadData();
            }
        });
    }

    private void loadData(){
        if( mGetWorkListTask==null || mGetWorkListTask.isComplete() || mGetWorkListTask.isCancelled() ){
            mGetWorkListTask = new GetWorkListTask();
            mGetWorkListTask.execute();
            mLoadingPager.showLoading();
        }
    }

    private String getTitleStr(){
        Date mToday = new Date();
        String today = DateUtils.format( mToday );
        if( today.contains( mDate ) ){
            return "今日 " + DateUtils.getWeekOfDate( mToday ) ;
        }

        Date ctoday = DateUtils.parse( mDate, "yyyy-MM-dd" );
        return mDate + " " + DateUtils.getWeekOfDate( ctoday );
    }
    //--------------------------------------------------------------------------------------------
    class GetWorkListTask extends AppAsyncTask<String, Void, List<WorkBean>> {

        @Override
        protected List<WorkBean> doExecute(String... params) throws Exception {

            LoginInfo loginInfo = AccountUtils.getLoginUser();
            UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
            if( loginInfo == null || !AccountUtils.hasClass()){
                throw new Exception("请登录");
            }

            MyTutorClassInfo currentClassInfo = AccountUtils.getCurrentClassInfo();
            String accessToken = loginInfo.getAccessToken();
            String studentId   = detailinfo.getStudentId();
            String classId     = currentClassInfo.getClassId();

            return myLearnService.getWorkList( accessToken, studentId, classId, mDate );
        }

        @Override
        protected void onResult(List<WorkBean> list) {

            //首页加载 错误 或者没有数据   显示无
            if( list == null || list.size() == 0 ){
                mLoadingPager.showEmpty();
                return;
            }
            mLoadingPager.hideall();

            Collections.sort( list, new WorkComparable() );

            //如果是下拉加载，清除原列表数据
            mWorkAdapter.clear();
            mWorkAdapter.addAll( list );
            mWorkAdapter.notifyDataSetChanged();
        }

        @Override
        protected void onFailure(HttpResponse<List<WorkBean>> response, Exception ex) {
            mLoadingPager.showFault(ex);
//            mListView.onRefreshComplete();
//
//            AlertManager.showErrorInfo( mContext, ex);
//            if (!isFirstComplete) {
//                mProgressView.setFaultView( ex );
//            }
        }
    }

    class WorkComparable implements Comparator<WorkBean>{

        @Override
        public int compare(WorkBean lhs, WorkBean rhs) {
            Date ldate = DateUtils.parse( lhs.getCreateTime(), DateUtils.FORMAT_DATA_TIME );
            Date rdate = DateUtils.parse( rhs.getCreateTime(), DateUtils.FORMAT_DATA_TIME );
            int ret = 0;
            if( ldate !=null && rdate != null ){
                if( ldate.getTime() > rdate.getTime() ) ret = -1;
                else if( ldate.getTime() < rdate.getTime() ) ret = 1;
            }
            return ret;
        }
    }

}
