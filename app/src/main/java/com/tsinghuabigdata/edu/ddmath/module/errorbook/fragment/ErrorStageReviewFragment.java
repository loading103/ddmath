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
import com.tsinghuabigdata.edu.ddmath.MVPModel.ErrorBookModel;
import com.tsinghuabigdata.edu.ddmath.MVPModel.RequestListener;
import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.bean.LoginInfo;
import com.tsinghuabigdata.edu.ddmath.bean.UserDetailinfo;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.constant.AppConst;
import com.tsinghuabigdata.edu.ddmath.event.RefreshStageviewEvent;
import com.tsinghuabigdata.edu.ddmath.fragment.MyBaseFragment;
import com.tsinghuabigdata.edu.ddmath.module.errorbook.HelpUtil;
import com.tsinghuabigdata.edu.ddmath.module.errorbook.adapter.StageReviewAdapter;
import com.tsinghuabigdata.edu.ddmath.module.errorbook.bean.StageReviewBean;
import com.tsinghuabigdata.edu.ddmath.module.errorbook.bean.StageReviewResult;
import com.tsinghuabigdata.edu.ddmath.module.errorbook.view.NoDataTipsView;
import com.tsinghuabigdata.edu.ddmath.module.neteaseim.base.util.TimeUtil;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.AppLog;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.MyViewUtils;
import com.tsinghuabigdata.edu.ddmath.view.QuestionPager;
import com.tsinghuabigdata.edu.ddmath.view.TimeSpinnerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Nonnull;


/**
 * 错题本下载
 * Created by cuibo 2017.11.15
 */

public class ErrorStageReviewFragment extends MyBaseFragment implements View.OnClickListener,PullToRefreshBase.OnRefreshListener<ListView> {

    //private TextView useCountView;
    private PullToRefreshListView mListView;
    //private FrameLayout mainLayout;
    private NoDataTipsView noDataTipsView;

    private TimeSpinnerView mTimeSpinnerView;
    private TimeSpinnerView mKindSpinnerView;
    //private ImageView mEbookHelp;
    //加载
    private QuestionPager         mLoadingPager;

    private ErrorBookModel errorBookModel = new ErrorBookModel();

    private StageReviewAdapter mAdapter;
//    private StageReviewResult stageReviewResult;

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
            root = inflater.inflate(R.layout.fragment_ebook_stagereview, container, false);
        } else {
            root = inflater.inflate(R.layout.fragment_ebook_stagereview_phone, container, false);
        }

        //x.view().inject( this, inflater, container );
        initView( root );
        setPrepared();

        EventBus.getDefault().register(this);
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
        AppLog.d("fsdfsdfds  onDestroyView");
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
        if(v.getId() == R.id.ebook_help){
            HelpUtil.showHelpActivity( getContext(), "错题浏览本使用说明", "Q008");
        }
//        if(v.getId() == R.id.ebook_help){
//            ProductHelpActivity.gotoProductHelpActivity( getContext(), "错题本下载 ", AppConst.PRIVILEGE_PERIODREVIEW);
//        } else if( v.getId() == R.id.ebook_productintro ){
//            ProductDetailActivity.gotoProductDetailActivity( getContext(), "错题本下载 ", AppConst.PRIVILEGE_PERIODREVIEW );
//        } else if( v.getId() == R.id.ebook_freedroit ){
//
//            if( stageReviewResult == null || stageReviewResult.getDriotlist()==null || stageReviewResult.getDriotlist().size()==0 ) return;
//
//            //弹出提示
//            Rect rect = new Rect();
//            freeDroitView.getGlobalVisibleRect( rect );
//
//            FreeDroitDialog dialog = new FreeDroitDialog( getContext(), R.style.FullTransparentDialog );
//            dialog.setData( stageReviewResult.getDriotlist(), rect.left, rect.bottom, 0  );
//            dialog.show();
//        }
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
        return "errbook_download";
    }
    //---------------------------------------------------------------------------------------------
    private void initView(View root) {
        //mContext = getActivity();
        FrameLayout mainLayout = root.findViewById(R.id.main_layout);
        noDataTipsView = root.findViewById(R.id.ndtv_nodata_view);
        noDataTipsView.setData("平台每周会把本周的全部错题，智能生成错题浏览本；如需让平台生成自定近期时间段的全部错题，请去", "创建自定义错题浏览本", AppConst.FROM_VARIANT);

        //useCountView = root.findViewById( R.id.ebook_stagereview_useconut ); //
        mListView = root.findViewById( R.id.ebook_stagereview_list );
        MyViewUtils.setPTRText( getContext(), mListView);
        mListView.setOnRefreshListener(this);
        ImageView mEbookHelp=root.findViewById(R.id.ebook_help);
        mEbookHelp.setOnClickListener(this);
        mListView.setMode(PullToRefreshBase.Mode.BOTH);

        //
//        TextView textView = new TextView(getContext());
//        textView.setTextSize( GlobalData.isPad()?18:12 );
//        textView.setText("仅显示最近100个错题浏览本哦，请记得及时下载上传作业\n");
//        textView.setTextColor(getResources().getColor(R.color.color_999999));
//        textView.setGravity(Gravity.CENTER);
//        mListView.getRefreshableView().addFooterView( textView );

        mLoadingPager = root.findViewById( R.id.loading_pager );

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
        final List<String> kindlist = Arrays.asList("全部","周","自定义");
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

        mAdapter = new StageReviewAdapter( getActivity() );
        mListView.setAdapter(mAdapter);

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
        }

        long startTime = TimeUtil.getStartTime( timeIndex, mCustomStartTime );
        long endTime    = TimeUtil.getEndTime( timeIndex, mCustomEndTime );
        errorBookModel.queryStageReviewList( studentId, currPage, AppConst.MAX_PAGE_NUM, startTime, endTime, kindIndex, new RequestListener<StageReviewResult>() {

            @Override
            public void onSuccess(StageReviewResult result) {
                if( !isAdded() || isDetached() ) return;
                mListView.onRefreshComplete();

                if( result == null || result.getPeriodReviewInfoList()==null || result.getPeriodReviewInfoList().size() == 0 ){
                    mLoadingPager.showTarget();
                    if( currPage == 1 ){
                        noDataTipsView.setVisibility( View.VISIBLE );
                        noDataTipsView.setTvEmty("暂时还没有错题浏览本～");
                        mListView.setVisibility( View.GONE );
                    }
                    return;
                }

                mLoadingPager.showTarget();
                noDataTipsView.setVisibility( View.GONE );
                mListView.setVisibility(View.VISIBLE);

//                stageReviewResult = result;

                if( currPage == 1 ) {
                    mAdapter.clear();
                    mAdapter.add( new StageReviewBean(1) );
                }
                mAdapter.addAll( result.getPeriodReviewInfoList() );
                mAdapter.notifyDataSetChanged();

                mAdapter.setStageReviewResult( result );
                //处理加载多页的问题
                int count = result.getTotalCount();
                if( count > 100 ) count = 100;
                if( mAdapter.getCount() >= count )
                    mListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
            }

            @Override
            public void onFail(HttpResponse<StageReviewResult> response, Exception ex) {
                if( !isAdded() || isDetached() ) return;
                mListView.onRefreshComplete();
                mLoadingPager.showFault(ex);
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receive(RefreshStageviewEvent event) {
        AppLog.d("event = " + event );
        // 收到本地消息后 请求网络刷新错题本
        currPage = 1;
        loadData();
    }
}
