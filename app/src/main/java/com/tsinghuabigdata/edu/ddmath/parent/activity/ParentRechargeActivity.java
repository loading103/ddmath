package com.tsinghuabigdata.edu.ddmath.parent.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tsinghuabigdata.edu.ddmath.MVPModel.RequestListener;
import com.tsinghuabigdata.edu.ddmath.MVPModel.UserCenterModel;
import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.activity.BasePayActivity;
import com.tsinghuabigdata.edu.ddmath.bean.RewardBean;
import com.tsinghuabigdata.edu.ddmath.bean.StudyBean;
import com.tsinghuabigdata.edu.ddmath.bean.UserDetailinfo;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.event.BuySuiteEvent;
import com.tsinghuabigdata.edu.ddmath.event.RechargeBeanEvent;
import com.tsinghuabigdata.edu.ddmath.event.UpdateStudybeanEvent;
import com.tsinghuabigdata.edu.ddmath.event.WxPayResultEvent;
import com.tsinghuabigdata.edu.ddmath.module.message.MessageUtils;
import com.tsinghuabigdata.edu.ddmath.module.mystudybean.PayBeanAdapter;
import com.tsinghuabigdata.edu.ddmath.module.product.PayUtil;
import com.tsinghuabigdata.edu.ddmath.parent.view.ParentToolbar;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.AppLog;
import com.tsinghuabigdata.edu.ddmath.util.DataUtils;
import com.tsinghuabigdata.edu.ddmath.util.LogUtils;
import com.tsinghuabigdata.edu.ddmath.util.ToastUtils;
import com.tsinghuabigdata.edu.ddmath.view.LoadingPager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 家长端--家长充值界面
 */

public class ParentRechargeActivity extends BasePayActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    @ViewInject(R.id.worktoolbar)
    private ParentToolbar workToolbar;
    @ViewInject(R.id.loadingPager)
    private LoadingPager mLoadingPager;

    @ViewInject(R.id.tv_usable_xuedou)
    private TextView usabeXueDouView;

    @ViewInject(R.id.layout_main_content)
    private LinearLayout mainLayout;
    @ViewInject(R.id.gv_bean)
    private GridView gridView;
    @ViewInject(R.id.btn_to_pay)
    private Button goPayBtn;

    private List<RewardBean> mList = new ArrayList<>();
    private PayBeanAdapter mAdapter;

    private Context mContext;
    private int mCurPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        transparent = true;
        isParent = true;
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_parent_recharge);
        x.view().inject( this );
        mContext = this;
        initView();
        loadData();
        updateLearnDouCount();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public String getUmEventName() {
        return "parent_mycenter_recharge";
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position == mCurPosition) {
            return;
        }
        mCurPosition = position;
        mAdapter.setCurPosition(position);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        if( R.id.btn_to_pay == v.getId() ){
            RewardBean rewardBean = mList.get(mCurPosition);
            toPay(rewardBean);
        }
    }

    //触发学豆更新
    @Override
    protected void updateLearnDouCount(){
        UserDetailinfo detailinfo = AccountUtils.getParentUserDetailinfo();
        if( detailinfo == null ) return;

        new UserCenterModel().queryMyStudyBean( detailinfo.getStudentId(), new RequestListener<StudyBean>() {
            @Override
            public void onSuccess(StudyBean res) {
                LogUtils.i("updateLearnDouCount success");
                if (res == null) {
                    return;
                }
                usabeXueDouView.setText( String.format(Locale.getDefault(),"%d个", res.getTotalDdAmt() ));
            }

            @Override
            public void onFail(HttpResponse<StudyBean> response, Exception ex) {
                LogUtils.i("updateLearnDouCount failed");
            }
        });
    }

    //----------------------------------------------------------------------------------------
    private void initView(){

        workToolbar.setTitle( "学豆充值" );
        workToolbar.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToMyStudyBean();
            }
        },null,new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserDetailinfo detailinfo = AccountUtils.getParentUserDetailinfo();
                if( detailinfo == null ){
                    ToastUtils.show( mContext,"没有学生信息");
                    return;
                }
                String url = MessageUtils.getRechargeRecordUrl( detailinfo.getStudentId() );
                ReportDetailActivity.openReportDetailActivity( mContext, "充值记录", DataUtils.getUrl(mContext,url) );
            }
        } );
        workToolbar.setRightText( "充值记录" );

        mLoadingPager.setTargetView(mainLayout);
        mLoadingPager.setListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLoadingPager.showLoading();
                loadData();
            }
        });

        gridView.setOnItemClickListener( this );

        goPayBtn.setOnClickListener( this );

        //
        mAdapter = new PayBeanAdapter(mContext, mList);
        mAdapter.setFromParent();
        gridView.setAdapter(mAdapter);
        EventBus.getDefault().register(this);
    }

    private void loadData(){

        new UserCenterModel().queryRewardBean(new RequestListener<List<RewardBean>>() {
            @Override
            public void onSuccess(List<RewardBean> res) {
                LogUtils.i("queryRewardBean success");
                if (res == null || res.size() == 0) {
                    mLoadingPager.showServerFault();
                    return;
                }
                mList.clear();
                mList.addAll(res);
                mCurPosition = mList.size() - 1;
                mAdapter.setCurPosition(mCurPosition);
                mAdapter.notifyDataSetChanged();
                mLoadingPager.showTarget();
            }

            @Override
            public void onFail(HttpResponse<List<RewardBean>> response, Exception ex) {
                mLoadingPager.showFault(ex);
            }
        });
    }

    //----------------------------------------------------------------------------------------------
    @Subscribe
    public void receive(BuySuiteEvent event) {
        AppLog.d("receive BuySuiteEvent " + event );
        updateLearnDouCount();
    }

    @Subscribe
    public void receive(UpdateStudybeanEvent event) {
        AppLog.d("receive UpdateStudybeanEvent " + event );
        updateLearnDouCount();
    }

    @Subscribe
    public void receive(RechargeBeanEvent event) {
        AppLog.d("receive RechargeBeanEvent " + event );
        updateLearnDouCount();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receive(WxPayResultEvent event) {
        if (event.errCode == 0) {
            ToastUtils.showShort(mContext, "支付成功");
        } else {
            if (event.errCode == -2) {
                ToastUtils.showShort(mContext, "已取消支付");
            } else {
                ToastUtils.showShort(mContext, "支付出现错误");
            }
            String remark = "errCode:" + event.errCode + " errStr:" + event.errStr;
            PayUtil.updateStatusAfter(mRechargeNum, remark);
        }

    }

}
