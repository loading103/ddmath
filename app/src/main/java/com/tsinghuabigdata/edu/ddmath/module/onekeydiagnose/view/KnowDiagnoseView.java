package com.tsinghuabigdata.edu.ddmath.module.onekeydiagnose.view;

import android.app.Dialog;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.tsinghuabigdata.edu.ddmath.MVPModel.MyWorldModel;
import com.tsinghuabigdata.edu.ddmath.MVPModel.RequestListener;
import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.activity.MainActivity;
import com.tsinghuabigdata.edu.ddmath.bean.MyTutorClassInfo;
import com.tsinghuabigdata.edu.ddmath.bean.QueryReportsInfo;
import com.tsinghuabigdata.edu.ddmath.bean.ReportInfo;
import com.tsinghuabigdata.edu.ddmath.bean.UserDetailinfo;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.event.GotoCreateWorkEvent;
import com.tsinghuabigdata.edu.ddmath.event.JumpEvent;
import com.tsinghuabigdata.edu.ddmath.fragment.MyStudyFragment;
import com.tsinghuabigdata.edu.ddmath.module.mycenter.HeadImageUtils;
import com.tsinghuabigdata.edu.ddmath.module.onekeydiagnose.adapter.KnowDiagnoseAdapter;
import com.tsinghuabigdata.edu.ddmath.module.onekeydiagnose.bean.KnowledgeMasterBean;
import com.tsinghuabigdata.edu.ddmath.module.onekeydiagnose.bean.KnowledgeMasterDetail;
import com.tsinghuabigdata.edu.ddmath.module.studyfeedback.StudyfeedbackModel;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.MyViewUtils;
import com.tsinghuabigdata.edu.ddmath.view.CircleImageView;
import com.tsinghuabigdata.edu.ddmath.view.HonourDialogLoadingPager;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * 知识诊断
 */
@Deprecated
public class KnowDiagnoseView extends LinearLayout implements AdapterView.OnItemClickListener{

    //没有数据
    private RelativeLayout nodataLayout;
    private TextView nodataTextView;
    private ImageView btnImageView;

    //正式内容
    private LinearLayout mainLayout;
    private CircleImageView mIvMyHeadClass;
    private TextView mTvMyNameClass;
    private TextView mTvMyDiagnoseTime;
    private TextView mTvMyHonourValue;
    private PullToRefreshListView mLvClass;

    //加载对象
    private HonourDialogLoadingPager mLoadingPagerClass;

    private MyWorldModel myWorldModel = new MyWorldModel();

    private List<KnowledgeMasterBean> mListClass = new ArrayList<>();
    private KnowDiagnoseAdapter mAdapter;

    private boolean bAttachedToWindow = false;
    private Dialog mDialog;

    public KnowDiagnoseView(Context context) {
        super(context);
        init();
    }

    public KnowDiagnoseView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public KnowDiagnoseView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void onDetachedFromWindow() {
        bAttachedToWindow = false;
        super.onDetachedFromWindow();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        bAttachedToWindow = true;

        loadData();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //先取消其他
        for( int i=0; i<mListClass.size(); i++ ){
            KnowledgeMasterBean bean = mListClass.get(i);
            if( i+1 == position ){
                bean.setSelect( !bean.isSelect() );
            }else{
                bean.setSelect( false );
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    public void setDailog( Dialog dialog ){
        mDialog = dialog;
    }
    //-----------------------------------------------------------------------------
    private void init() {
        inflate( getContext(), GlobalData.isPad()?R.layout.view_diagnose_know:R.layout.view_diagnose_know_phone, this );

        //mFlClass = (FrameLayout) findViewById(R.id.fl_class);
        mainLayout = (LinearLayout) findViewById(R.id.ll_class);
        mIvMyHeadClass = (CircleImageView) findViewById(R.id.iv_my_head_class);
        mTvMyNameClass = (TextView) findViewById(R.id.tv_my_name_class);
        mTvMyHonourValue = (TextView) findViewById(R.id.tv_my_honorvalue);
        mTvMyDiagnoseTime = (TextView) findViewById(R.id.tv_my_diagnosetime);
        mLvClass = (PullToRefreshListView) findViewById(R.id.lv_class);
        mLvClass.setOnItemClickListener( this );
        mLoadingPagerClass = (HonourDialogLoadingPager) findViewById(R.id.loadingPager_class);

        mLoadingPagerClass.setTargetView( mainLayout );
        mLoadingPagerClass.setListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mLoadingPagerClass.showLoading();
                loadData();
            }
        });

        MyViewUtils.setPTRText(getContext(), mLvClass);
        mLvClass.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> pullToRefreshBase) {
                loadData();
            }
        });

        nodataLayout = (RelativeLayout)findViewById( R.id.giagnose_nodata_layout );
        nodataTextView= (TextView)findViewById( R.id.giagnose_nodata_tips );
        btnImageView = (ImageView)findViewById( R.id.giagnose_start_gbtn );
        btnImageView.setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //先要关闭dialog， 再进行跳转
                if( mDialog!=null ){
                    mDialog.dismiss();
                }
                EventBus.getDefault().post(new JumpEvent(MainActivity.FRAGMENT_STUDY_TASK, MyStudyFragment.MODEL_CHECK_WORK));
                EventBus.getDefault().post(new GotoCreateWorkEvent());
            }
        } );
    }

    private void showMyClassInfo(KnowledgeMasterDetail detail, UserDetailinfo detailinfo ) {
        HeadImageUtils.setHeadImage(mIvMyHeadClass, detailinfo.getHeadImage(), R.drawable.doudou_portrait_default);
        mTvMyHonourValue.setText( String.format( Locale.getDefault(), "我的荣耀值: %d", (int)(detail.getTotalScore() *10))  );
        mTvMyDiagnoseTime.setText( String.format( Locale.getDefault(), "诊断时间: %s", detail.getCreateTime())  );
        mTvMyNameClass.setText( detailinfo.getReallyName() );
    }

    private void loadData(){

        final UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
        MyTutorClassInfo currentClassInfo = AccountUtils.getCurrentClassInfo();
        if (detailinfo == null || currentClassInfo==null) {
            mLoadingPagerClass.showServerFault();
            return;
        }

        //先获得知识图谱报告的信息
        mLoadingPagerClass.showLoading();
        new StudyfeedbackModel().queryReports( detailinfo.getStudentId(), "1", "1", "1", new RequestListener<QueryReportsInfo>() {
            @Override
            public void onSuccess(QueryReportsInfo vo) {
                if ( !bAttachedToWindow )
                    return;
                if (vo == null || vo.getItems() == null || vo.getItems().size() == 0) {
                    showNoDataView();
                    return;
                }
                if ( vo.getItems() == null || vo.getItems().get(0) == null) {
                    showNoDataView();
                    return;
                }
                ReportInfo reportInfo = vo.getItems().get(0);
                String mExamId = reportInfo.getExamId();
                String mLastExamId = reportInfo.getLastExamId();
                if( mLastExamId == null ){
                    mLastExamId = "";
                }
                //
                loadClassRank( detailinfo, mExamId, mLastExamId );
            }

            @Override
            public void onFail(HttpResponse response, Exception ex) {
                if ( !bAttachedToWindow )
                    return;
                mLoadingPagerClass.showFault(ex);
            }
        });

    }

    //知识点变化详情
    private void loadClassRank( final UserDetailinfo detailinfo, String examId, String lastExamId ) {

        myWorldModel.queryKnowDiagnose( /*currentClassInfo.getClassId(), */detailinfo.getStudentId(),examId, lastExamId, new RequestListener<KnowledgeMasterDetail>() {
            @Override
            public void onSuccess(KnowledgeMasterDetail detail) {
                if ( !bAttachedToWindow )
                    return;

                if ( detail == null ) {
                    showNoDataView();
                    return;
                }
                List<KnowledgeMasterBean> list = detail.getThirdLevelKnMastery();
                if (list == null || list.size() == 0) {
                    showNoDataView();
                    return;
                }
                mListClass.clear();

                //过滤数据，只要有变化的部分数据
                for( KnowledgeMasterBean bean : list ){
                    if( bean.getAccuracy() != bean.getLastAccuracy() )
                        mListClass.add( bean );
                }

                //没有数据更新的提示
                if (mListClass.size() == 0) {
                    showNoDataView( "近期知识点掌握率没有变化哦!", false );
                    return;
                }

                Collections.sort(mListClass, new Comparator<KnowledgeMasterBean>() {
                    @Override
                    public int compare(KnowledgeMasterBean lhs, KnowledgeMasterBean rhs) {
                        float left = lhs.getAccuracy() - lhs.getLastAccuracy();
                        float right = rhs.getAccuracy() - rhs.getLastAccuracy();
                        int ret = 0;
                        if( left > right ) ret = -1;
                        else if( left < right ) ret = 1;
                        return ret;
                    }
                });
                //有数据
                nodataLayout.setVisibility( View.GONE );
                mLoadingPagerClass.showTarget();

                //显示个人信息
                showMyClassInfo( detail, detailinfo);

                mAdapter = new KnowDiagnoseAdapter(getContext(), mListClass);
                mLvClass.setAdapter(mAdapter);

                mLvClass.setMode(PullToRefreshBase.Mode.DISABLED);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFail(HttpResponse response, Exception ex) {
                if (!bAttachedToWindow)
                    return;
                mLoadingPagerClass.showFault(ex);
            }
        });
    }

    private void showNoDataView(){
        mLoadingPagerClass.hideall();
        mainLayout.setVisibility( View.GONE );
        nodataLayout.setVisibility( View.VISIBLE );
    }

    private void showNoDataView( String data, boolean show ){
        mLoadingPagerClass.hideall();
        mainLayout.setVisibility( View.GONE );
        nodataTextView.setText( data );
        btnImageView.setVisibility( show?VISIBLE:GONE );
        nodataLayout.setVisibility( View.VISIBLE );
    }
}
