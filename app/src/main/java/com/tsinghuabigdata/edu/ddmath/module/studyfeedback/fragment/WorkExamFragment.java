package com.tsinghuabigdata.edu.ddmath.module.studyfeedback.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.tsinghuabigdata.edu.ddmath.MVPModel.RequestListener;
import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.bean.LoginInfo;
import com.tsinghuabigdata.edu.ddmath.bean.QueryReportsInfo;
import com.tsinghuabigdata.edu.ddmath.bean.ReportInfo;
import com.tsinghuabigdata.edu.ddmath.bean.UserDetailinfo;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.constant.AppConst;
import com.tsinghuabigdata.edu.ddmath.event.RefreshStageviewEvent;
import com.tsinghuabigdata.edu.ddmath.fragment.ReportBaseFragment;
import com.tsinghuabigdata.edu.ddmath.module.studyfeedback.StudyfeedbackModel;
import com.tsinghuabigdata.edu.ddmath.module.studyfeedback.adapter.WorkExamAdapter;
import com.tsinghuabigdata.edu.ddmath.module.studyfeedback.view.ChartBtnView;
import com.tsinghuabigdata.edu.ddmath.module.studyfeedback.view.ChartLineView;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.AlertManager;
import com.tsinghuabigdata.edu.ddmath.util.AppLog;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.MyViewUtils;
import com.tsinghuabigdata.edu.ddmath.view.QuestionPager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import static com.tsinghuabigdata.edu.ddmath.module.studyfeedback.view.ChartLineView.TYPE_TREND_ALL;
import static com.tsinghuabigdata.edu.ddmath.module.studyfeedback.view.ChartLineView.TYPE_TREND_EXAM;
import static com.tsinghuabigdata.edu.ddmath.module.studyfeedback.view.ChartLineView.TYPE_TREND_WORK;


/**
 * 作业考试
 */

public class WorkExamFragment extends ReportBaseFragment implements View.OnClickListener,PullToRefreshBase.OnRefreshListener<ListView> {

    //报告类型 （0：综合，1：知识分析，2：考试报告，3：作业报告, 4:周练, 5:周题练, 6:专属习题, 7:精品套题 ）
    private static final String REPORT_TYPE  = "2,3,4";

    //加载
    private QuestionPager         mLoadingPager;
    //private LinearLayout          mainLayout;


    private ChartBtnView allTextView;
    private ChartBtnView workTextView;
    private ChartBtnView examTextView;

    private ChartLineView allChartLineView;
    private ChartLineView workChartLineView;
    private ChartLineView examChartLineView;

    //    private ExamLineChart mLineChart;
//    private ChartSelectView chartSelectView;
//    private RelativeLayout nodataLayout;
    //列表
    private PullToRefreshListView mListView;

    private WorkExamAdapter mAdapter;

    private int currPage = 1;

    //趋势数据
    private int mAllTrendData[];    //全部趋势数据
    private int mWorkTrendData[];   //作业趋势数据
    private int mExamTrendData[];   //考试趋势数据

    //private String mStudentId = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root;
        if (GlobalData.isPad()) {
            root = inflater.inflate(R.layout.fragment_studyfb_workexam, container, false);
        } else {
            root = inflater.inflate(R.layout.fragment_studyfb_workexam_phone, container, false);
        }
        initView( root );

        setPrepared();
        loadData();
        return root;
    }

    @Override
    protected void refreshReport() {
        currPage = 1;
        loadData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.studyfb_workexam_allbtn){
            showTrendData( TYPE_TREND_ALL );
        } else if( v.getId() == R.id.studyfb_workexam_workbtn ){
            showTrendData( TYPE_TREND_WORK );
        } else if( v.getId() == R.id.studyfb_workexam_exambtn ){
            showTrendData( TYPE_TREND_EXAM );
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
            currPage++;
            loadData();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receive(RefreshStageviewEvent event) {
        AppLog.d("event = " + event);
        // 收到本地消息后 请求网络刷新错题本
        currPage = 1;
        loadData();
    }
    public String getUmEventName() {
        return "report_workexam";
    }
    //---------------------------------------------------------------------------------------------
    private void initView(View root) {
        //mContext = getActivity();

        LinearLayout mainLayout = root.findViewById( R.id.studyfb_workexam_mainLayout );

        //切换按钮
        allTextView = root.findViewById( R.id.studyfb_workexam_allbtn );
        workTextView = root.findViewById( R.id.studyfb_workexam_workbtn );
        examTextView = root.findViewById( R.id.studyfb_workexam_exambtn );
        allTextView.setTypeName( "全部" );
        workTextView.setTypeName( "作业" );
        examTextView.setTypeName( "考试" );

        allTextView.setOnClickListener( this );
        workTextView.setOnClickListener( this );
        examTextView.setOnClickListener( this );

        allChartLineView = root.findViewById( R.id.studyfb_workexam_chart_all);
        workChartLineView = root.findViewById( R.id.studyfb_workexam_chart_work);
        examChartLineView = root.findViewById( R.id.studyfb_workexam_chart_exam);

        mListView = root.findViewById( R.id.studyfb_workexam_listview );
        MyViewUtils.setPTRText( getContext(), mListView);
        mListView.setOnRefreshListener(this);
        mListView.setMode(PullToRefreshBase.Mode.BOTH);

        mLoadingPager = root.findViewById( R.id.loading_pager );

        mLoadingPager.stopAnim();
        mLoadingPager.setTargetView(mainLayout);
        mLoadingPager.setListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLoadingPager.showLoading();
                loadData();
            }
        });

        mAdapter = new WorkExamAdapter( getActivity() );
        mListView.setAdapter(mAdapter);

        EventBus.getDefault().register(this);
    }

    private void loadData() {
        LoginInfo loginInfo = AccountUtils.getLoginUser();
        UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
        if (loginInfo == null || detailinfo == null) {
            return;
        }
        mLoadingPager.startAnim();
        String studentId = detailinfo.getStudentId();
        if( currPage==1 ){
            mLoadingPager.showLoading();
            mListView.setMode(PullToRefreshBase.Mode.BOTH);

            //初始化数据
            mAllTrendData = null;
            mWorkTrendData= null;
            mExamTrendData= null;
        }

        new StudyfeedbackModel().queryReports( studentId, REPORT_TYPE, currPage + "", AppConst.MAX_PAGE_NUM + "", new RequestListener<QueryReportsInfo>() {
            @Override
            public void onSuccess(QueryReportsInfo vo) {
                if( isDetached() ) return;
                mListView.onRefreshComplete();
                mLoadingPager.showTarget();
                if (vo == null || vo.getItems() == null || vo.getItems().size() == 0) {
//                    if( currPage==1 ) {
//                        allChartLineView.setData( ChartLineView.TYPE_TREND_ALL, mAllTrendData, mListView, mAdapter );
//                        showTrendData(TYPE_TREND_ALL);
//                    }
                    mLoadingPager.showEmpty("暂时还没有新的作业/考试报告，快去学习吧～");
                    return;
                }

                List<ReportInfo> infoList = vo.getItems();

                //处理加载多页的问题 listview 加载方式
                if (vo.getPageNum() >= vo.getTotalPage()) {
                    mListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                } else {
                    mListView.setMode(PullToRefreshBase.Mode.BOTH);
                }

                if( currPage == 1 )
                    mAdapter.clear();
                mAdapter.addAll( infoList );
                mAdapter.notifyDataSetChanged();

                //先分析作业和考试的数据数量
                int allCount, workCount=0, examCount = 0;
                for( int i=0; i<mAdapter.getCount();i++ ){
                    ReportInfo item = mAdapter.getItem(i);
                    if( item==null ) continue;
                    if ("exerhReport".equals(item.getReportType())) {
                        workCount++;
                    } else {
                        examCount++;
                    }
                }
                allCount = workCount + examCount;

                //
                mAllTrendData = new int[allCount*2];
                int index = 0, workIndex = 0, examIndex = 0;
                mWorkTrendData = null;
                mExamTrendData = null;
                for( ; index<mAdapter.getCount();index++ ){
                    ReportInfo item = mAdapter.getItem(index);
                    if( item==null ) continue;
                    float value;
                    if ("exerhReport".equals(item.getReportType())) {
                        value = item.getRightQuestionCount() * 1f /( item.getRightQuestionCount()+item.getWrongQuestionCount() );
                        if(mWorkTrendData==null) mWorkTrendData = new int[workCount*2];
                        mWorkTrendData[2 * ( workCount-1 - workIndex)]   = 0                 ;        //平均
                        mWorkTrendData[2 * ( workCount-1 - workIndex)+1] = Math.round( value*100 );    //个人
                        workIndex++;
                    } else {
                        value = item.getStudentScore() /item.getTotalScore();
                        if(mExamTrendData==null) mExamTrendData = new int[examCount*2];
                        mExamTrendData[2 * ( examCount-1 - examIndex)]   = 0                 ;        //平均
                        mExamTrendData[2 * ( examCount-1 - examIndex)+1] = Math.round( value*100 );    //个人
                        examIndex++;
                    }
                    mAllTrendData[2 * ( allCount-1- index)]   = 0                 ;        //平均
                    mAllTrendData[2 * ( allCount-1- index)+1] = Math.round( value*100 );    //个人
                }

                allChartLineView.setData( ChartLineView.TYPE_TREND_ALL, mAllTrendData, mListView, mAdapter );
                workChartLineView.setData( ChartLineView.TYPE_TREND_WORK, mWorkTrendData, mListView, mAdapter );
                examChartLineView.setData( ChartLineView.TYPE_TREND_EXAM, mExamTrendData, mListView, mAdapter );
                showTrendData( TYPE_TREND_ALL );
            }

            @Override
            public void onFail(HttpResponse response, Exception ex) {
                if( isDetached() ) return;
                mListView.onRefreshComplete();
                if( currPage==1 )  mLoadingPager.showFault(ex);
                else{
                    currPage--;
                    AlertManager.showErrorInfo( getContext(), ex );
                }
            }
        });
    }

    private void showTrendData( int type ){

        allTextView.selected( type== TYPE_TREND_ALL );
        workTextView.selected( type== TYPE_TREND_WORK );
        examTextView.selected( type== TYPE_TREND_EXAM );

        allChartLineView.setVisibility( type== TYPE_TREND_ALL?View.VISIBLE:View.GONE );
        workChartLineView.setVisibility( type==TYPE_TREND_WORK?View.VISIBLE:View.GONE );
        examChartLineView.setVisibility( type==TYPE_TREND_EXAM?View.VISIBLE:View.GONE );

        allTextView.setSelected( type==TYPE_TREND_ALL );
        workTextView.setSelected( type==TYPE_TREND_WORK );
        examTextView.setSelected( type==TYPE_TREND_EXAM );
    }

}
