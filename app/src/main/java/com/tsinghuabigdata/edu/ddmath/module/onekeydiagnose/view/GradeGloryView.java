package com.tsinghuabigdata.edu.ddmath.module.onekeydiagnose.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.StyleRes;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.tsinghuabigdata.edu.ddmath.MVPModel.MyWorldModel;
import com.tsinghuabigdata.edu.ddmath.MVPModel.RequestListener;
import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.bean.GradeGloryRank;
import com.tsinghuabigdata.edu.ddmath.bean.MyTutorClassInfo;
import com.tsinghuabigdata.edu.ddmath.bean.UserDetailinfo;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.constant.AppConst;
import com.tsinghuabigdata.edu.ddmath.module.onekeydiagnose.adapter.GradeRankAdapter;
import com.tsinghuabigdata.edu.ddmath.module.onekeydiagnose.bean.RankBean;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.AlertManager;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.MyViewUtils;
import com.tsinghuabigdata.edu.ddmath.util.ToastUtils;
import com.tsinghuabigdata.edu.ddmath.view.BaseHeadView;
import com.tsinghuabigdata.edu.ddmath.view.HonourDialogLoadingPager;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 年级荣耀值排行榜
 */

public class GradeGloryView extends LinearLayout{

    private LinearLayout             mLlClass;
    private BaseHeadView mIvMyHeadClass;
    private TextView                 mTvMyRank;
    private TextView                 mTvAverageRate;
    private PullToRefreshListView    mLvClass;
    private HonourDialogLoadingPager mLoadingPagerClass;

    private String mStudentId = "";
    private String mClassId   = "";
    private int    pageNum    = 1;

    private List<RankBean> mList = new ArrayList<>();
    private GradeRankAdapter mAdapter;

    public GradeGloryView(@NonNull Context context) {
        super(context);
        init();
    }
    public GradeGloryView(@NonNull Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    public GradeGloryView(@NonNull Context context, AttributeSet attrs, @StyleRes int themeResId) {
        super(context, attrs, themeResId);
        init();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        loadData();
    }

    //-----------------------------------------------------------------------------
    private void init() {
        inflate( getContext(), GlobalData.isPad()?R.layout.view_grade_ranklist:R.layout.view_grade_ranklist_phone, this );

        mLlClass =  findViewById(R.id.ll_class);
        mIvMyHeadClass =  findViewById(R.id.iv_my_head_class);
        mTvMyRank =  findViewById(R.id.tv_my_rank);
        mTvAverageRate =  findViewById(R.id.tv_average_rate);
        mLvClass =  findViewById(R.id.lv_class);
        mLoadingPagerClass =  findViewById(R.id.loadingPager_class);

        mLoadingPagerClass.setTargetView(mLlClass);
        mLoadingPagerClass.setListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLoadingPagerClass.showLoading();
                loadClassRank();
            }
        });

        MyViewUtils.setPTRText(getContext(), mLvClass);
        mLvClass.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> pullToRefreshBase) {
                getMoreClassRank();
            }
        });
    }

    private void loadData() {
        createLoginInfo();
        loadClassRank();
    }

    //查询登录信息
    private void createLoginInfo() {
        UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
        if ( detailinfo != null) {
            mStudentId = detailinfo.getStudentId();
        }
        MyTutorClassInfo classInfo = AccountUtils.getCurrentClassInfo();
        if ( classInfo != null) {
            mClassId = classInfo.getClassId();
        }
    }

    private void loadClassRank() {
        new MyWorldModel().queryGradeGloryRank(mClassId, mStudentId, pageNum, AppConst.MAX_PAGE_NUM, new RequestListener<GradeGloryRank>() {
            @Override
            public void onSuccess(GradeGloryRank gradeGloryRank) {
                if (gradeGloryRank == null || gradeGloryRank.getStudentRank() == null) {
                    mLoadingPagerClass.showServerFault();
                    return;
                }
                List<RankBean> list = gradeGloryRank.getClassRank();
                if (list == null || list.size() == 0) {
                    mLoadingPagerClass.showServerFault();
                    return;
                }
                mList.clear();
                mList.addAll(gradeGloryRank.getClassRank());
                mAdapter = new GradeRankAdapter(getContext(), mList);
                mLvClass.setAdapter(mAdapter);
                if (pageNum < gradeGloryRank.getTotalPage()) {
                    mLvClass.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
                } else {
                    mLvClass.setMode(PullToRefreshBase.Mode.DISABLED);
                }
                pageNum++;
                //显示个人信息
                showMyClassInfo(gradeGloryRank.getStudentRank());
                mLoadingPagerClass.showTarget();
            }

            @Override
            public void onFail(HttpResponse response, Exception ex) {
                mLoadingPagerClass.showFault(ex);

            }
        });
    }

    private void getMoreClassRank() {
        new MyWorldModel().queryGradeGloryRank(mClassId, mStudentId, pageNum, AppConst.MAX_PAGE_NUM, new RequestListener<GradeGloryRank>() {
            @Override
            public void onSuccess(GradeGloryRank gradeGloryRank) {
                if (gradeGloryRank == null || gradeGloryRank.getClassRank() == null || gradeGloryRank.getClassRank().size() == 0) {
                    ToastUtils.showShort(getContext(), R.string.server_error);
                    mLvClass.onRefreshComplete();
                    return;
                }
                mList.addAll(gradeGloryRank.getClassRank());
                mAdapter.notifyDataSetChanged();
                mLvClass.onRefreshComplete();
                if (pageNum < gradeGloryRank.getTotalPage()) {
                    mLvClass.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
                } else {
                    mLvClass.setMode(PullToRefreshBase.Mode.DISABLED);
                }
                pageNum++;
            }

            @Override
            public void onFail(HttpResponse response, Exception ex) {
                AlertManager.showErrorInfo(getContext(), ex);
                mLvClass.onRefreshComplete();
            }
        });
    }

    private void showMyClassInfo(RankBean rankBean) {
        //HeadImageUtils.setHeadImage(mIvMyHeadClass, rankBean.getHeadImage(), R.drawable.doudou_portrait_default);
        mIvMyHeadClass.showHeadImage();
        mTvMyRank.setText( String.valueOf(rankBean.getRank()) );
        mTvAverageRate.setText( String.format(Locale.getDefault(),"%d%%",Math.round(rankBean.getGlory() * 100)));
    }
}
