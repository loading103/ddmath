package com.tsinghuabigdata.edu.ddmath.module.studyfeedback.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tsinghuabigdata.edu.ddmath.MVPModel.RequestListener;
import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.bean.UserDetailinfo;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.fragment.ReportBaseFragment;
import com.tsinghuabigdata.edu.ddmath.module.message.MessageUtils;
import com.tsinghuabigdata.edu.ddmath.module.mycenter.dialog.ShareLinkDialog;
import com.tsinghuabigdata.edu.ddmath.module.myscore.ScoreEventID;
import com.tsinghuabigdata.edu.ddmath.module.studyfeedback.StudyfeedbackModel;
import com.tsinghuabigdata.edu.ddmath.module.studyfeedback.bean.WeekAnalysisBean;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.DataUtils;
import com.tsinghuabigdata.edu.ddmath.util.DateUtils;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.LogUtils;
import com.tsinghuabigdata.edu.ddmath.util.ReportUtils;
import com.tsinghuabigdata.edu.ddmath.util.ToastUtils;
import com.tsinghuabigdata.edu.ddmath.view.LoadingPager;
import com.tsinghuabigdata.edu.ddmath.view.ProgressWebView;
import com.tsinghuabigdata.edu.ddmath.view.TimeSpinnerView;

import java.util.ArrayList;
import java.util.List;


/**
 * 周学习分享报告
 */
public class WeekAnalysisReportFragment extends ReportBaseFragment{

    private Context mContext;

    private LoadingPager        mLoadingPager;
    //private RelativeLayout      mainLayout;


    private ProgressWebView progressWebView;
    //private ImageView mIvShare;
    //private String    mUrl;
    private LinearLayout mEmptyLayout;

    private TimeSpinnerView mTimeSpinnerView;

    private List<WeekAnalysisBean> mDataList;
    private int timeIndex;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root;
        if (GlobalData.isPad()) {
            root = inflater.inflate(R.layout.fragment_weekanalysis_report, container, false);
        } else {
            root = inflater.inflate(R.layout.fragment_weekanalysis_report_mobile, container, false);
        }
        initView(root);
        setPrepared();
        loadData();
        return root;
    }

    private void initView(View root) {
        mContext = getActivity();
        RelativeLayout mainLayout = root.findViewById(R.id.main_layout);
        mLoadingPager = root.findViewById(R.id.loading_pager);

        progressWebView = root.findViewById(R.id.webView_report);
        ImageView mIvShare = root.findViewById(R.id.iv_share);
        mTimeSpinnerView = root.findViewById(R.id.timeSpinnerView);

        mEmptyLayout = root.findViewById(R.id.empty_layout);

        mLoadingPager.setTargetView(mainLayout);
        mLoadingPager.setListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLoadingPager.showLoading();
                loadData();
            }
        });

        mIvShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WeekAnalysisBean currBean = mDataList.get(timeIndex);

                ShareLinkDialog shareLinkDialog = new ShareLinkDialog(mContext);
                String shareName = ReportUtils.getShareName( currBean.getTitle());
                shareLinkDialog.setShareInfo( getReportUrl(), shareName, DateUtils.getShareText(currBean.getCreateTime()), true);
                shareLinkDialog.setScoreEventId(ScoreEventID.EVENT_WEEK_ANALYSIS);
                shareLinkDialog.setContentId( currBean.getReportId() );
                shareLinkDialog.show();
            }
        });
    }

    private void loadData() {
        UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
        if( detailinfo == null ){
            ToastUtils.show( mContext,"请重新登录");
            return;
        }
        new StudyfeedbackModel().queryWeekAnalysisReports( detailinfo.getStudentId(), new RequestListener<List<WeekAnalysisBean>>() {
            @Override
            public void onSuccess(List<WeekAnalysisBean> list) {
                if( isDetached() ) return;

                mLoadingPager.showTarget();
                //没有数据
                if( list== null || list.size() == 0 ){
                    mEmptyLayout.setVisibility( View.VISIBLE );
                    return;
                }
                mDataList = list;

                //构造数据
                final List<String> timelist = new ArrayList<>();
                for( WeekAnalysisBean bean : mDataList ){
                    //timelist.add()
                    String year_start = DateUtils.format( bean.getStartTime(), DateUtils.FORMAT_DATA);
                    String year_stop  = DateUtils.format( bean.getStopTime(), DateUtils.FORMAT_DATA);

                    StringBuilder sb = new StringBuilder();
                    if( !year_start.equals( year_stop) ){        //同一年
                        sb.append( DateUtils.format( bean.getStartTime(), DateUtils.FORMAT_DATA ));
                        sb.append( "至");
                        sb.append( DateUtils.format( bean.getStopTime(), DateUtils.FORMAT_DATA_MD ));
                    }else{
                        sb.append( DateUtils.format( bean.getStartTime(), DateUtils.FORMAT_DATA ));
                        sb.append( "至");
                        sb.append( DateUtils.format( bean.getStopTime(), DateUtils.FORMAT_DATA ));
                    }
                    timelist.add( sb.toString() );
                }

                mTimeSpinnerView.setText(timelist.get(timeIndex));
                mTimeSpinnerView.setData(timelist, timeIndex);
                mTimeSpinnerView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                       if (position != mTimeSpinnerView.getSelectedPosition()) {
                            mTimeSpinnerView.showCustom(false);
                            mTimeSpinnerView.setSelectedPosition(position);
                            timeIndex = position;
                            String time = timelist.get(position);
                            mTimeSpinnerView.setText(time);
                            mTimeSpinnerView.dismiss();
                            loadWebUrl();
                        } else {
                            mTimeSpinnerView.dismiss();
                        }
                    }
                });
                mTimeSpinnerView.setNumColumns( 2 );
                //加载默认报告
                loadWebUrl();
            }

            @Override
            public void onFail(HttpResponse response, Exception ex) {
                if( isDetached() ) return;
                mLoadingPager.showFault(ex);
            }
        });
    }

    private void loadWebUrl(){

        if( mDataList==null || timeIndex >= mDataList.size() ){
            return;
        }
        progressWebView.loadUrl( getReportUrl() );
    }
    private String getReportUrl(){
        UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
        if( detailinfo == null ){
            ToastUtils.show( mContext,"请重新登录");
            return "";
        }

        WeekAnalysisBean currBean = mDataList.get(timeIndex);
        String baseUrl = MessageUtils.getWeekReportUrl( detailinfo.getStudentId(), currBean.getReportId() );
        String mUrl = DataUtils.getUrl(mContext, baseUrl);
        //mUrl += "startTime=" + currBean.getStartTime() + "&endTime=" + currBean.getStopTime();
        LogUtils.i("errorCorrectReport url=" + mUrl);
        return mUrl;
    }

    @Override
    protected void refreshReport() {
        if (isPrepared) {
            mLoadingPager.showLoading();
            loadData();
        }
    }

    public String getUmEventName() {
        return "report_weekanslysis";
    }

}
