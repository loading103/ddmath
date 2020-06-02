package com.tsinghuabigdata.edu.ddmath.module.mycenter.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.tsinghuabigdata.edu.ddmath.MVPModel.ProductModel;
import com.tsinghuabigdata.edu.ddmath.MVPModel.RequestListener;
import com.tsinghuabigdata.edu.ddmath.MVPModel.UserCenterModel;
import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.bean.MyTutorClassInfo;
import com.tsinghuabigdata.edu.ddmath.bean.StudyBean;
import com.tsinghuabigdata.edu.ddmath.bean.UserDetailinfo;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.event.BuySuiteEvent;
import com.tsinghuabigdata.edu.ddmath.event.ChangeClassEvent;
import com.tsinghuabigdata.edu.ddmath.event.JoinClassEvent;
import com.tsinghuabigdata.edu.ddmath.event.JumpSuiteEvent;
import com.tsinghuabigdata.edu.ddmath.event.RechargeBeanEvent;
import com.tsinghuabigdata.edu.ddmath.event.SyncShowStudybeanEvent;
import com.tsinghuabigdata.edu.ddmath.event.SyncShowStudybeanFromDialogEvent;
import com.tsinghuabigdata.edu.ddmath.event.UpdateClassEvent;
import com.tsinghuabigdata.edu.ddmath.event.UpdateStudybeanEvent;
import com.tsinghuabigdata.edu.ddmath.fragment.BasePayFragment;
import com.tsinghuabigdata.edu.ddmath.module.message.MessageUtils;
import com.tsinghuabigdata.edu.ddmath.module.mycenter.adapter.SuiteAdapter;
import com.tsinghuabigdata.edu.ddmath.module.mystudybean.RechargeCenterActivity;
import com.tsinghuabigdata.edu.ddmath.module.mystudybean.WebViewActivity;
import com.tsinghuabigdata.edu.ddmath.module.product.ProductUtil;
import com.tsinghuabigdata.edu.ddmath.module.product.bean.ProductBean;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.AlertManager;
import com.tsinghuabigdata.edu.ddmath.util.AppLog;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.LogUtils;
import com.tsinghuabigdata.edu.ddmath.util.ToastUtils;
import com.tsinghuabigdata.edu.ddmath.view.LoadingPager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Administrator on 2017/12/5.
 * 我的学豆模块
 */

public class MyStudyBeanFragment extends BasePayFragment implements View.OnClickListener, PullToRefreshBase.OnRefreshListener2<ListView> {

    public static final  String PARAM_SHOW_BEAN = "showbean";
    public static final  String PARAM_SHOW_PRIVILEDGEIDS = "more_priviledgeids";
    public static final  String PARAM_SHOW_SINGLE_PRIVILEDGEID = "single_priviledgeid";
//    private MyProgressDialog mProgressDialog;

    private FrameLayout           mFlContent;
    private LoadingPager          mLoadingPager;
    //private LinearLayout          mLlStudybeanContent;
    private RelativeLayout        mRlStudybeanTitle;
    //private Button                mBtnBuyDetail;
    private TextView              mTvTotalBean;
    //private Button                mBtnRechargeBean;
    private PullToRefreshListView mLvSuite;
    //    private TextView              mTvSuitePrice;
    //    private TextView              mTvBuyNow;
    private LinearLayout          mLlNoClassTips;
    private LinearLayout          mLlNoFormalClassTips;

    private Context      mContext;
    private SuiteAdapter mAdapter;
    private ArrayList<ProductBean> mList      = new ArrayList<>();
    private String            mStudentId = "";
    private String            mSchoolId  = "";
    private String            mClassId   = "";
    private boolean showBean = true;
    private String targetPriviledgeIds;         //仅仅显示包含有这个ID的套餐
    private String singlePriviledgeId;         //包含单个功能的套餐

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root;
        if (GlobalData.isPad()) {
            root = inflater.inflate(R.layout.fragment_my_studybean, container, false);
        } else {
            root = inflater.inflate(R.layout.fragment_my_studybean_phone, container, false);
        }
        initView(root);
        setPrepared();
        initData();
        return root;
    }

    private void initView(View root) {
        mContext = getActivity();
//        mProgressDialog = new MyProgressDialog(mContext);
//        mProgressDialog.setMessage("购买中");

        mFlContent =  root.findViewById(R.id.fl_content);
        mLoadingPager =  root.findViewById(R.id.loadingPager);
        LinearLayout mLlStudybeanContent =  root.findViewById(R.id.ll_studybean_content);
        mRlStudybeanTitle =  root.findViewById(R.id.rl_studybean_title);
        Button mBtnBuyDetail =  root.findViewById(R.id.btn_buy_detail);
        mTvTotalBean =  root.findViewById(R.id.tv_total_bean);
        Button mBtnRechargeBean =  root.findViewById(R.id.btn_recharge_bean);
        mLvSuite =  root.findViewById(R.id.lv_suite);
        mLlNoClassTips =  root.findViewById(R.id.ll_no_class_tips);
        mLlNoFormalClassTips =  root.findViewById(R.id.ll_no_formal_class_tips);

        mBtnBuyDetail.setOnClickListener(this);
        mBtnRechargeBean.setOnClickListener(this);
        mLvSuite.setOnRefreshListener(this);
        mLvSuite.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        mLoadingPager.setTargetView(mLlStudybeanContent);
        mLoadingPager.setListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLoadingPager.showLoading();
                queryStudyBean( true );
            }
        });
    }

    private void initData() {
        Bundle arguments = getArguments();
        if (arguments != null) {
            showBean = arguments.getBoolean(PARAM_SHOW_BEAN);
            targetPriviledgeIds = arguments.getString(PARAM_SHOW_PRIVILEDGEIDS);
            singlePriviledgeId  = arguments.getString(PARAM_SHOW_SINGLE_PRIVILEDGEID);
            //LogUtils.i("showBean = " + showBean);
            if (!showBean) {
                mRlStudybeanTitle.setVisibility(View.GONE);
            }
        }
        createLoginInfo();
        initAdapter();
        EventBus.getDefault().register(this);
    }

    private void createLoginInfo() {
        UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
        if ( detailinfo!= null) {
            mStudentId = detailinfo.getStudentId();
        }
        if (!AccountUtils.hasClass()) {
            showNoClassView();
            queryStudyBean(false);
        } else if (!AccountUtils.hasFormalClass()) {
            showNoFormalClassView();
            queryStudyBean(false);
        } else {
            showCourseContentView();
            queryClassInfo();
            queryStudyBean(true);
        }
    }

    //展示 2、未加入班级页面
    private void showNoClassView() {
        mLlNoClassTips.setVisibility(View.VISIBLE);
        mLlNoFormalClassTips.setVisibility(View.INVISIBLE);
        mLoadingPager.setVisibility(View.INVISIBLE);
    }

    //展示 3、未加入正式班级页面
    private void showNoFormalClassView() {
        mLlNoClassTips.setVisibility(View.INVISIBLE);
        mLlNoFormalClassTips.setVisibility(View.VISIBLE);
        mLoadingPager.setVisibility(View.INVISIBLE);
    }

    //展示 4、已加入班级页面
    private void showCourseContentView() {
        mLlNoClassTips.setVisibility(View.INVISIBLE);
        mLlNoFormalClassTips.setVisibility(View.INVISIBLE);
        mFlContent.setVisibility(View.VISIBLE);
    }

    //查询班级信息
    private void queryClassInfo() {
        MyTutorClassInfo classInfo = AccountUtils.getCurrentClassInfo();
        if (classInfo != null) {
            mClassId = classInfo.getClassId();
            mSchoolId = classInfo.getSchoolId();
        }
    }

    private void initAdapter() {
        mAdapter = new SuiteAdapter(getActivity(), mList);
        mLvSuite.setAdapter(mAdapter);
        mAdapter.setSuiteSelectListener(new SuiteAdapter.SuiteSelectListener() {
            @Override
            public void select(ProductBean item) {
                if (!ProductUtil.ckeckAllowRepeatBuy(mContext, item)) {
                    return;
                }
                if (ProductUtil.ckeckSuiteOverdue(mContext, item)) {
                    return;
                }
                toPay(item);
            }
        });
    }

    private void queryStudyBean(final boolean querySuite) {
        new UserCenterModel().queryMyStudyBean(mStudentId, new RequestListener<StudyBean>() {
            @Override
            public void onSuccess(StudyBean res) {
                if (res == null) {
                    mLoadingPager.showServerFault();
                    return;
                }
                EventBus.getDefault().post(new SyncShowStudybeanEvent(res.getTotalDdAmt()));
                mTvTotalBean.setText( String.valueOf(res.getTotalDdAmt() ));
                if(querySuite)querySuiteList();
            }

            @Override
            public void onFail(HttpResponse<StudyBean> response, Exception ex) {
                mLoadingPager.showFault(ex);
            }
        });
    }

    /**
     * 查询精品套餐列表
     */
    private void querySuiteList() {
        queryClassInfo();
        new ProductModel().getSuiteList(mStudentId, mSchoolId, mClassId, "", new RequestListener<List<ProductBean>>() {

            @Override
            public void onSuccess(List<ProductBean> res) {
                LogUtils.i("querySuiteList onSuccess");

                List<ProductBean> tlist;
                if (res == null || res.size() == 0 || (tlist = ProductUtil.rankSuite(res)).size() == 0) {
                    mLoadingPager.showEmpty(R.string.no_product_set);
                    return;
                }
                mList.clear();
                List<ProductBean> list = filterSuite(tlist);
                if( list == null || list.size() == 0 ){
                    mLoadingPager.showEmpty(R.string.no_product_set);
                    return;
                }
                mLoadingPager.showTarget();
                mList.addAll( list );
                change();
            }

            @Override
            public void onFail(HttpResponse<List<ProductBean>> response, Exception ex) {
                LogUtils.i("querySuiteList onFail " + ex.getMessage());
                mLoadingPager.showFault(ex);
            }
        });
    }

    //每一个商品都有包含，指定的功能
    private List<ProductBean> filterSuite( List<ProductBean> list) {
        if (!TextUtils.isEmpty(singlePriviledgeId)) {
            return filterSinglePrivilegeId( list );
        } else if (!TextUtils.isEmpty(targetPriviledgeIds)) {
            return filterMorePrivilegeIds(list);
        } else
            return list;
    }
    private List<ProductBean> filterSinglePrivilegeId( List<ProductBean> list){
        List<ProductBean> tmplist = new ArrayList<>();
        for( ProductBean suiteBean : list ){
            if( containPriviledgeId( suiteBean, singlePriviledgeId) )
                tmplist.add( suiteBean );
        }
        return tmplist;
    }
    private List<ProductBean> filterMorePrivilegeIds( List<ProductBean> list){
        String priviledgeIds[] = targetPriviledgeIds.split(",");
        List<ProductBean> tmplist = new ArrayList<>();
        for( ProductBean suiteBean : list ){
            if( containAllPriviledgeId( suiteBean, priviledgeIds) )
                tmplist.add( suiteBean );
        }
        return tmplist;
    }
    private boolean containAllPriviledgeId( ProductBean suiteBean, String priviledgeIds[] ){
        for( String priviledgeId : priviledgeIds ){
            if( !containPriviledgeId(suiteBean,priviledgeId) ) return false;
        }
        //全部包含
        return true;
    }
    private boolean containPriviledgeId( ProductBean suiteBean, String priviledgeId ){
        ArrayList<ProductBean> list = suiteBean.getProductVoList();
        for( ProductBean productBean : list ){
            if( priviledgeId.equals( productBean.getPrivilegeId()) )
                return true;
        }
        return false;
    }

    private void change() {
        String expandPrivilege = "";
        if( !TextUtils.isEmpty(targetPriviledgeIds) ){
            expandPrivilege = targetPriviledgeIds.split(",")[0];
        }else if( !TextUtils.isEmpty(singlePriviledgeId) ){
            expandPrivilege = singlePriviledgeId;
        }
        int curPosition = ProductUtil.getPrivilegePosition(mList, expandPrivilege) + 1;
        mAdapter.setCurPosition(curPosition - 1);
        mAdapter.notifyDataSetChanged();
        //LogUtils.i("curPosition = " + curPosition);
        if (curPosition >= 1 && curPosition <= mAdapter.getCount() + 1) {
            //            mLvSuite.getRefreshableView().smoothScrollToPosition(curPosition);
            //            mLvSuite.getRefreshableView().smoothScrollToPositionFromTop(curPosition,0);
            mLvSuite.getRefreshableView().setSelection(curPosition);
        }
    }

    //触发学豆更新
    protected void updateLearnDouCount() {
        new UserCenterModel().queryMyStudyBean(mStudentId, new RequestListener<StudyBean>() {
            @Override
            public void onSuccess(StudyBean res) {
                LogUtils.i("updateLearnDouCount success");
                if (res == null) {
                    return;
                }
                EventBus.getDefault().post(new SyncShowStudybeanEvent(res.getTotalDdAmt()));
                mTvTotalBean.setText( String.valueOf(res.getTotalDdAmt()) );
            }

            @Override
            public void onFail(HttpResponse<StudyBean> response, Exception ex) {
                LogUtils.i("updateLearnDouCount failed");
            }
        });
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase pullToRefreshBase) {
        refreshSuiteList();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase pullToRefreshBase) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_recharge_bean:
                Intent intent = new Intent(mContext, RechargeCenterActivity.class);
                startActivityForResult(intent,0);
                break;
            case R.id.btn_buy_detail:
                Intent in2 = new Intent(mContext, WebViewActivity.class);
                in2.putExtra(WebViewActivity.MSG_URL, MessageUtils.getUseBeanDetailUrl(mStudentId));
                in2.putExtra(WebViewActivity.MSG_TITLE, "购买详情");
                mContext.startActivity(in2);
                break;
            default:
                break;
        }
    }

    /**
     * 刷新精品套餐列表
     */
    private void refreshSuiteList() {
        //GlobalData.setExpandPrivilege(null);
        queryClassInfo();
        new ProductModel().getSuiteList(mStudentId, mSchoolId, mClassId, "", new RequestListener<List<ProductBean>>() {

            @Override
            public void onSuccess(List<ProductBean> res) {
                LogUtils.i("refreshSuiteList onSuccess");
                List<ProductBean> tlist;
                if (res == null || res.size() == 0 || (tlist = ProductUtil.rankSuite(res)).size() == 0) {
                    ToastUtils.showShort(mContext, R.string.server_error);
                    return;
                }
                mList.clear();
                mList.addAll(filterSuite(tlist));
                change();
                mLvSuite.onRefreshComplete();
            }

            @Override
            public void onFail(HttpResponse<List<ProductBean>> response, Exception ex) {
                LogUtils.i("refreshSuiteList onFail " + ex.getMessage());
                mLvSuite.onRefreshComplete();
                AlertManager.showErrorInfo(getContext(), ex);
            }
        });
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receive(JoinClassEvent event) {
        AppLog.d("event="+event);
        createLoginInfo();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receive(JumpSuiteEvent event) {
        AppLog.d("event="+event);
        singlePriviledgeId = event.getPrivilegeId();
        if (mAdapter != null) {
            change();
        }
    }

    @Subscribe
    public void receive(BuySuiteEvent event) {
        AppLog.d("event="+event);
        updateLearnDouCount();
        refreshSuiteList();
    }

    @Subscribe
    public void receive(UpdateStudybeanEvent event) {
        AppLog.d("event="+event);
        updateLearnDouCount();
    }

    @Subscribe
    public void receive(RechargeBeanEvent event) {
        AppLog.d("event="+event);
        updateLearnDouCount();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receive(ChangeClassEvent event) {
        AppLog.d("receive event = "+ event );
        mLoadingPager.showLoading();
        queryClassInfo();
        queryStudyBean(true);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receive(UpdateClassEvent event) {
        AppLog.d("event="+event);
        createLoginInfo();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receive(SyncShowStudybeanFromDialogEvent event) {
        mTvTotalBean.setText( String.valueOf(event.getTotalBean()) );
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receive(SyncShowStudybeanEvent event) {
        if (showBean) {
            mTvTotalBean.setText( String.valueOf(event.getTotalBean()) );
        }
    }

    public String getUmEventName() {
        return "mycenter_buy";
    }
}
