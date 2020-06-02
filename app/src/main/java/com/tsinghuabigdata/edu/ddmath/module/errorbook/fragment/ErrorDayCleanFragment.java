package com.tsinghuabigdata.edu.ddmath.module.errorbook.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.tsinghuabigdata.edu.ddmath.MVPModel.ErrorBookModel;
import com.tsinghuabigdata.edu.ddmath.MVPModel.RequestListener;
import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.activity.MainActivity;
import com.tsinghuabigdata.edu.ddmath.bean.LoginInfo;
import com.tsinghuabigdata.edu.ddmath.bean.UserDetailinfo;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.constant.AppConst;
import com.tsinghuabigdata.edu.ddmath.event.JumpEvent;
import com.tsinghuabigdata.edu.ddmath.event.RefreshDaycleanEvent;
import com.tsinghuabigdata.edu.ddmath.fragment.MyBaseFragment;
import com.tsinghuabigdata.edu.ddmath.fragment.MyReportFragment;
import com.tsinghuabigdata.edu.ddmath.module.errorbook.HelpUtil;
import com.tsinghuabigdata.edu.ddmath.module.errorbook.NumberUtil;
import com.tsinghuabigdata.edu.ddmath.module.errorbook.adapter.DayCleanAdapter;
import com.tsinghuabigdata.edu.ddmath.module.errorbook.bean.DayCleanResult;
import com.tsinghuabigdata.edu.ddmath.module.product.ProductDetailActivity;
import com.tsinghuabigdata.edu.ddmath.module.product.ProductExchangeActivity;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.AppLog;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.MyViewUtils;
import com.tsinghuabigdata.edu.ddmath.view.QuestionPager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;



/**
 * 错题订正
 * Created by cuibo 2017.11.15
 */

public class ErrorDayCleanFragment extends MyBaseFragment implements View.OnClickListener,PullToRefreshBase.OnRefreshListener<GridView> {

    //使用人次
    private TextView useCountView;
    //列表
    private PullToRefreshGridView mGridView;

    //使用次数
    //private FreeDroitView freeDroitView;

    //加载
    private QuestionPager         mLoadingPager;

    private String studentId;

    //private Context           mContext;

    private ErrorBookModel errorBookModel = new ErrorBookModel();

    private DayCleanAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root;
        if (GlobalData.isPad()) {
            root = inflater.inflate(R.layout.fragment_ebook_dayclean, container, false);
        } else {
            root = inflater.inflate(R.layout.fragment_ebook_dayclean_phone, container, false);
        }

        //x.view().inject( this, inflater, container );
        initView( root );
        setPrepared();
        initData();
        return root;
    }

    private void initData() {
        loadData();
        EventBus.getDefault().register(this);
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
            //切换到日日清报告
            EventBus.getDefault().post(new JumpEvent(MainActivity.FRAGMENT_STUDY_CONDITION, MyReportFragment.DAILY_CLEAR_I));
        } else if(v.getId() == R.id.ebook_help){
            HelpUtil.showHelpActivity( getContext(), "错题订正使用说明", "Q002");
        } else if( v.getId() == R.id.ebook_productintro ){
            ProductDetailActivity.gotoProductDetailActivity( getContext(), "错题订正", AppConst.PRIVILEGE_QUESTION_DAYCLEAR );
        } else if( v.getId() == R.id.ebook_dayclean_exchangebtn ){
            ProductExchangeActivity.gotoProductExchangeActivity( getContext(), "错题订正", AppConst.PRIVILEGE_QUESTION_DAYCLEAR );
        }
    }

    @Override
    public void onRefresh(PullToRefreshBase<GridView> pullToRefreshBase) {
        if (pullToRefreshBase.isHeaderShown()) {
            loadData();
        }
    }

    public String getUmEventName() {
        return "errbook_dayclean";
    }
    //---------------------------------------------------------------------------------------------
    private void initView(View root) {
        //mContext = getActivity();
        useCountView = root.findViewById( R.id.ebook_dayclean_useconut );

        mGridView = root.findViewById( R.id.ebook_dayclean_list);
        MyViewUtils.setPTRText( getContext(), mGridView);
        mGridView.setOnRefreshListener(this);
        mGridView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);        //仅仅下来刷新

        mLoadingPager = root.findViewById( R.id.loading_pager );

        TextView textView = root.findViewById( R.id.ebook_productintro );
        textView.setOnClickListener( this );
        textView.setVisibility( View.GONE );
        textView = root.findViewById( R.id.ebook_dayclean_exchangebtn );
        textView.setOnClickListener( this );

        ImageView imageView =  root.findViewById( R.id.ebook_report );
        imageView.setOnClickListener( this );
        imageView.setVisibility(View.GONE);
        imageView =  root.findViewById( R.id.ebook_help );
        imageView.setOnClickListener( this );

        //freeDroitView = (FreeDroitView)root.findViewById( R.id.ebook_freedroit );

        mLoadingPager.setTargetView(mGridView);
        mLoadingPager.setListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLoadingPager.showLoading();
                loadData();
            }
        });

        mAdapter = new DayCleanAdapter( getActivity() );
        mGridView.setAdapter(mAdapter);
    }

    private void loadData() {
        LoginInfo loginInfo = AccountUtils.getLoginUser();
        UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
        if (loginInfo == null || detailinfo == null) {
            return;
        }
        studentId = detailinfo.getStudentId();

        mLoadingPager.showLoading();
        errorBookModel.queryDayCleanList( studentId, /*startDate, endDate,*/ new RequestListener<DayCleanResult>() {

            @Override
            public void onSuccess(DayCleanResult result) {
                if( !isAdded() || isDetached() ) return;
                mGridView.onRefreshComplete();
                if( result!=null ){
                    useCountView.setText( String.format( getResources().getString(R.string.ebook_dayclean_tips), NumberUtil.approximateNumber(result.getTotalCount())) );
                }
                //2018/4/9
                //freeDroitView.setData();

                if( result == null || result.getWrongQuestionDCInfoList()==null || result.getWrongQuestionDCInfoList().size() == 0 ){
                    mLoadingPager.showEmpty();
                    return;
                }

                mLoadingPager.showTarget();

                mAdapter.clear();
                mAdapter.addAll( result.getWrongQuestionDCInfoList() );
                mAdapter.notifyDataSetChanged();

                mAdapter.setCorrectTotal( result.getTotalCount() );
            }

            @Override
            public void onFail(HttpResponse<DayCleanResult> response, Exception ex) {
                if( isDetached() ) return;
                mGridView.onRefreshComplete();
                mLoadingPager.showFault(ex);
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receive(RefreshDaycleanEvent event) {
        AppLog.d("event = " + event);
        // 收到本地消息后 请求网络刷新错题本
        if (!TextUtils.isEmpty(studentId)) {
            loadData();
        }
    }

}
