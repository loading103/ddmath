package com.tsinghuabigdata.edu.ddmath.module.errorbook.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.Nullable;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.tsinghuabigdata.edu.ddmath.MVPModel.PracticeModel;
import com.tsinghuabigdata.edu.ddmath.MVPModel.RequestListener;
import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.activity.MainActivity;
import com.tsinghuabigdata.edu.ddmath.bean.LoginInfo;
import com.tsinghuabigdata.edu.ddmath.bean.UserDetailinfo;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.constant.AppConst;
import com.tsinghuabigdata.edu.ddmath.event.JumpEvent;
import com.tsinghuabigdata.edu.ddmath.event.RefreshVariantEvent;
import com.tsinghuabigdata.edu.ddmath.fragment.MyBaseFragment;
import com.tsinghuabigdata.edu.ddmath.fragment.MyReportFragment;
import com.tsinghuabigdata.edu.ddmath.module.errorbook.HelpUtil;
import com.tsinghuabigdata.edu.ddmath.module.errorbook.adapter.VariantTrainAdapter;
import com.tsinghuabigdata.edu.ddmath.module.errorbook.bean.VarTrainResult;
import com.tsinghuabigdata.edu.ddmath.module.errorbook.view.NoDataTipsView;
import com.tsinghuabigdata.edu.ddmath.module.exclusivepractice.bean.PracticeBean;
import com.tsinghuabigdata.edu.ddmath.module.exclusivepractice.bean.PracticeProductBean;
import com.tsinghuabigdata.edu.ddmath.module.neteaseim.base.util.TimeUtil;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.AppLog;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.MyViewUtils;
import com.tsinghuabigdata.edu.ddmath.util.ToastUtils;
import com.tsinghuabigdata.edu.ddmath.view.QuestionPager;
import com.tsinghuabigdata.edu.ddmath.view.TimeSpinnerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nonnull;


/**
 * 变式训练
 * Created by cuibo 2018.08.08
 */

public class VariantTrainFragment extends MyBaseFragment implements View.OnClickListener,PullToRefreshBase.OnRefreshListener<ListView> {

    //列表
    private PullToRefreshListView mListView;
    //private FrameLayout mainLayout;
    private NoDataTipsView noDataTipsView;

    //private TextView useCountView;
    private TimeSpinnerView mTimeSpinnerView;
    private TimeSpinnerView mKindSpinnerView;
//    private ImageView mEbookHelp;
    //加载
    private QuestionPager         mLoadingPager;

    private PracticeModel practiceModel = new PracticeModel();
    private VariantTrainAdapter mAdapter;

    private int currPage = 1;

    private int timeIndex= 0;
    private int kindIndex = 0;
    private long   mCustomStartTime;
    private long   mCustomEndTime;

    @Nullable
    @Override
    public View onCreateView(@Nonnull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root;
        if (GlobalData.isPad()) {
            root = inflater.inflate(R.layout.fragment_vartrain, container, false);
        } else {
            root = inflater.inflate(R.layout.fragment_vartrain_phone, container, false);
        }

        //x.view().inject( this, inflater, container );
        initView( root );
        setPrepared();
        return root;
    }

    @Override
    protected void lazyLoad() {
        currPage = 1;
        loadData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    public void clearData() {
        if (mAdapter != null ) {
            mAdapter.clear();
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View v) {
        if( v.getId() == R.id.ebook_report ){
            //跳转到培优报告
            EventBus.getDefault().post(new JumpEvent(MainActivity.FRAGMENT_STUDY_CONDITION, MyReportFragment.WEEK_EXTRACT_I));
        }else if(v.getId() == R.id.ebook_help){
            HelpUtil.showHelpActivity( getContext(), "变式训练本使用说明", "Q006");
        }


        /* else if(v.getId() == R.id.ebook_help){
            ProductHelpActivity.gotoProductHelpActivity( getContext(), "错题周题练", AppConst.PRIVILEGE_QUESTION_WEEKPRACTICE);
        } else if( v.getId() == R.id.ebook_productintro ){
            ProductDetailActivity.gotoProductDetailActivity( getContext(), "错题周题练", AppConst.PRIVILEGE_QUESTION_WEEKPRACTICE );
        }else if( v.getId() == R.id.ebook_freedroit ){

            if( weekTrainResult == null || weekTrainResult.getDriotlist()==null || weekTrainResult.getDriotlist().size()==0 ) return;

            //弹出提示
            Rect rect = new Rect();
            freeDroitView.getGlobalVisibleRect( rect );

            FreeDroitDialog dialog = new FreeDroitDialog( getContext(), R.style.FullTransparentDialog );
            dialog.setData( weekTrainResult.getDriotlist(), rect.left, rect.bottom, 0  );
            dialog.show();
        }*/
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

    public String getUmEventName() {
        return "errbook_vartrain";
    }
    //---------------------------------------------------------------------------------------------
    private void initView(View root) {
        FrameLayout mainLayout = root.findViewById(R.id.main_layout);
        noDataTipsView = root.findViewById(R.id.ndtv_nodata_view);
        noDataTipsView.setData("平台每周会自动根据你的最薄弱知识点，智能推荐出专属变式训练本；如需让平台根据自定近期时间段的最薄弱知识点推荐，请去    ", "创建自定义变式训练本", AppConst.FROM_VARIANT);
        ImageView mEbookHelp=root.findViewById(R.id.ebook_help);
        mEbookHelp.setOnClickListener(this);
        mListView = root.findViewById( R.id.ebook_vartrain_list );
        MyViewUtils.setPTRText( getContext(), mListView);
        mListView.setOnRefreshListener(this);
        mListView.setMode(PullToRefreshBase.Mode.BOTH);        //下拉刷新,上拉加载更多

        //
//        TextView textView = new TextView(getContext());
//        textView.setTextSize( GlobalData.isPad()?18:12 );
//        textView.setText("仅显示最近100个变式训练本哦，请记得及时下载上传作业\n");
//        textView.setTextColor(getResources().getColor(R.color.color_999999));
//        textView.setGravity(Gravity.CENTER);
//        mListView.getRefreshableView().addFooterView( textView );

        mLoadingPager = root.findViewById( R.id.loading_pager );

        ImageView imageView = root.findViewById( R.id.ebook_report );
        imageView.setOnClickListener( this );

        mTimeSpinnerView = root.findViewById(R.id.timeSpinnerView);
        final List<String> timelist = Arrays.asList(getResources().getStringArray(R.array.time_filter_array));
        mTimeSpinnerView.setText(timelist.get(timeIndex));
        mTimeSpinnerView.setData(timelist, timeIndex);
        mTimeSpinnerView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == timelist.size() - 1) {
                    //显示自定义
                    mTimeSpinnerView.showCustom(true);
                    mTimeSpinnerView.autoShowCustom();
                    mTimeSpinnerView.setTempPosition(position);
                } else if (position != mTimeSpinnerView.getSelectedPosition()) {
                    mTimeSpinnerView.showCustom(false);
                    mTimeSpinnerView.setSelectedPosition(position);
                    timeIndex = position;
                    String time = timelist.get(position);
                    mTimeSpinnerView.setText(time);
                    mTimeSpinnerView.dismiss();

                    currPage = 1;
                    loadData();
                } else {
                    mTimeSpinnerView.dismiss();
                }

            }
        });
        mTimeSpinnerView.setCustomSelect(new TimeSpinnerView.CustomSelect() {
            @Override
            public void selectTime(long startTime, long endTime) {
                mCustomStartTime = startTime;
                mCustomEndTime = endTime;
                int lastPosition = timelist.size() - 1;
                if (timeIndex != lastPosition) {
                    timeIndex = lastPosition;
                    mTimeSpinnerView.setSelectedPosition(lastPosition);
                    String time = timelist.get(lastPosition);
                    mTimeSpinnerView.setText(time);
                }

                currPage = 1;
                loadData();
            }
        });

        mKindSpinnerView = root.findViewById(R.id.kindSpinnerView);
        final List<String> kindlist = Arrays.asList(getResources().getStringArray(R.array.datetype_filter_array));
        mKindSpinnerView.setShowDate( false );
        mKindSpinnerView.setText(kindlist.get(kindIndex));
        mKindSpinnerView.setData(kindlist, kindIndex);
        mKindSpinnerView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position != mKindSpinnerView.getSelectedPosition()) {
                    mKindSpinnerView.setSelectedPosition(position);
                    kindIndex = position;
                    String time = kindlist.get(position);
                    mKindSpinnerView.setText(time);

                    currPage = 1;
                    loadData();
                }
                mKindSpinnerView.dismiss();
            }
        });

        mLoadingPager.stopAnim();
        mLoadingPager.setTargetView(mainLayout);
        mLoadingPager.setListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLoadingPager.showLoading();
                loadData();
            }
        });

        mAdapter = new VariantTrainAdapter( getActivity() );
        mListView.setAdapter(mAdapter);

        EventBus.getDefault().register(this);
    }

    private void loadData() {
        LoginInfo loginInfo = AccountUtils.getLoginUser();
        UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
        if (loginInfo == null || detailinfo == null ) {
            ToastUtils.show( getContext(), R.string.relogin );
            mLoadingPager.showEmpty();
            return;
        }
        String studentId = detailinfo.getStudentId();

        mLoadingPager.startAnim();
        if( currPage == 1 ){
            mLoadingPager.showLoading();
            mListView.setMode(PullToRefreshBase.Mode.BOTH);
        }
        //
        long startTime = TimeUtil.getStartTime( timeIndex, mCustomStartTime );
        long endTime    = TimeUtil.getEndTime( timeIndex, mCustomEndTime );
        practiceModel.getVarTrainPracticeList( studentId, currPage, AppConst.MAX_PAGE_NUM, startTime, endTime, TimeUtil.getKindType(kindIndex), requestListener );
    }

    private RequestListener requestListener = new RequestListener<VarTrainResult>() {

        @Override
        public void onSuccess(VarTrainResult result) {
            mListView.onRefreshComplete();
            if( !isAdded() || isDetached() ) return;

            if( result == null || result.getProducts()==null || result.getProducts().size() == 0 ){
                mLoadingPager.showTarget();
                if( currPage == 1 ){
                    noDataTipsView.setVisibility( View.VISIBLE );
                    noDataTipsView.setTvEmty("暂时还没有变式训练本～");
                    mListView.setVisibility( View.GONE );
                }
                return;
            }

            mLoadingPager.showTarget();
            noDataTipsView.setVisibility( View.GONE );
            mListView.setVisibility(View.VISIBLE);

            ArrayList<PracticeProductBean> list = result.getProducts();
            ArrayList<PracticeBean> practiceList = new ArrayList<>();

            //转换结构
            for( PracticeProductBean bean : list ){

               for( PracticeBean practiceBean : bean.getProductList() ){
                    practiceBean.setPaperType( bean.getProductType() );
                    practiceBean.setProductBean( bean );
                    practiceList.add( practiceBean );
               }
            }

            //第一页
            if( currPage == 1 ) {
                mAdapter.clear();
                mAdapter.add( new PracticeBean(1) );
            }

            mAdapter.addAll( practiceList );
            mAdapter.notifyDataSetChanged();

            //处理加载多页的问题
            int count = result.getTotalPage();
            if( count > 10 ) count = 10;
            if( result.getPageNum() >= count )
                mListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);

        }

        @Override
        public void onFail(HttpResponse<VarTrainResult> response, Exception ex) {
            if( isDetached() ) return;
            mListView.onRefreshComplete();
            mLoadingPager.showFault(ex);
        }
    };

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receive(RefreshVariantEvent event) {
        AppLog.d("receive event = " + event );
        // 收到本地消息后 请求网络刷新错题本
        currPage = 1;
        loadData();
    }

}

