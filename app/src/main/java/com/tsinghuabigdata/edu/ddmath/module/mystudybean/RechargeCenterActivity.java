package com.tsinghuabigdata.edu.ddmath.module.mystudybean;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
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
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.event.BuySuiteEvent;
import com.tsinghuabigdata.edu.ddmath.event.RechargeBeanEvent;
import com.tsinghuabigdata.edu.ddmath.event.UpdateStudybeanEvent;
import com.tsinghuabigdata.edu.ddmath.event.WxPayResultEvent;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.view.WorkToolbar;
import com.tsinghuabigdata.edu.ddmath.module.message.MessageUtils;
import com.tsinghuabigdata.edu.ddmath.module.product.PayUtil;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.LogUtils;
import com.tsinghuabigdata.edu.ddmath.util.ToastUtils;
import com.tsinghuabigdata.edu.ddmath.view.LoadingPager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class RechargeCenterActivity extends BasePayActivity {

    private WorkToolbar  mWorktoolbar;
    private LinearLayout mLlContent;
    private TextView     mTvBeanValue;
    private GridView     mGvBean;
    private Button       mBtnToPay;
    private LoadingPager mLoadingPager;

    private UserCenterModel mModel    = new UserCenterModel();
    private String          studentId = "";
    private Context mContext;
    private int     mCurPosition;
    private List<RewardBean> mList = new ArrayList<>();
    private PayBeanAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        transparent = true;
        super.onCreate(savedInstanceState);
        // 设置全屏显示
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(GlobalData.isPad() ? R.layout.activity_recharge_center : R.layout.activity_recharge_center_phone);

        initView();
        initData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void initView() {
        mContext = this;
        mWorktoolbar = (WorkToolbar) findViewById(R.id.worktoolbar);
        mLlContent = (LinearLayout) findViewById(R.id.ll_content);
        mTvBeanValue = (TextView) findViewById(R.id.tv_bean_value);
        mGvBean = (GridView) findViewById(R.id.gv_bean);
        mBtnToPay = (Button) findViewById(R.id.btn_to_pay);
        mLoadingPager = (LoadingPager) findViewById(R.id.loadingPager);
        mLoadingPager.setTargetView(mLlContent);
        mLoadingPager.setListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLoadingPager.showLoading();
                queryRewardBean();
            }
        });
        mWorktoolbar.setTitle("学豆充值");
        mWorktoolbar.setRightTitleAndLeftDrawable("充值记录", R.drawable.ic_record);
        mWorktoolbar.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToMyStudyBean();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goRecordWebView();
            }
        });

        mBtnToPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RewardBean rewardBean = mList.get(mCurPosition);
                toPay(rewardBean);
            }
        });
        mGvBean.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == mCurPosition) {
                    return;
                }
                mCurPosition = position;
                mAdapter.setCurPosition(position);
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    private void initData() {
        if (AccountUtils.getUserdetailInfo() != null) {
            studentId = AccountUtils.getUserdetailInfo().getStudentId();
        }
        mTvBeanValue.setText(AccountUtils.getTotalBean() + "个");
        mAdapter = new PayBeanAdapter(mContext, mList);
        mGvBean.setAdapter(mAdapter);
        queryRewardBean();
        EventBus.getDefault().register(this);
    }

    private void goRecordWebView() {
        Intent intent = new Intent(this, WebViewActivity.class);
        intent.putExtra(WebViewActivity.MSG_URL, MessageUtils.getRechargeRecordUrl(studentId));
        intent.putExtra(WebViewActivity.MSG_TITLE, "充值记录");
        startActivity(intent);
    }

    private void queryRewardBean() {
        mModel.queryRewardBean(new RequestListener<List<RewardBean>>() {
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
                LogUtils.i("queryRewardBean failed " + ex.getMessage());
                mLoadingPager.showFault(ex);
            }
        });
    }

    //触发学豆更新
    protected void updateLearnDouCount() {
        new UserCenterModel().queryMyStudyBean(studentId, new RequestListener<StudyBean>() {
            @Override
            public void onSuccess(StudyBean res) {
                LogUtils.i("updateLearnDouCount success");
                if (res == null) {
                    return;
                }
                mTvBeanValue.setText(res.getTotalDdAmt() + "个");
            }

            @Override
            public void onFail(HttpResponse<StudyBean> response, Exception ex) {
                LogUtils.i("updateLearnDouCount failed");
            }
        });
    }

    @Subscribe
    public void receive(BuySuiteEvent event) {
        updateLearnDouCount();
    }

    @Subscribe
    public void receive(UpdateStudybeanEvent event) {
        updateLearnDouCount();
    }

    @Subscribe
    public void receive(RechargeBeanEvent event) {
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
