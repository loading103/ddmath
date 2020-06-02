package com.tsinghuabigdata.edu.ddmath.module.product;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;

import com.tsinghuabigdata.edu.ddmath.MVPModel.ProductModel;
import com.tsinghuabigdata.edu.ddmath.MVPModel.RequestListener;
import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.activity.MainActivity;
import com.tsinghuabigdata.edu.ddmath.activity.RoboActivity;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.constant.AppConst;
import com.tsinghuabigdata.edu.ddmath.event.JumpEvent;
import com.tsinghuabigdata.edu.ddmath.fragment.MyReportFragment;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.view.WorkToolbar;
import com.tsinghuabigdata.edu.ddmath.module.product.adapter.MyTreasureAdapter;
import com.tsinghuabigdata.edu.ddmath.module.product.bean.ProductBean;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.DensityUtils;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.LogUtils;
import com.tsinghuabigdata.edu.ddmath.util.WindowUtils;
import com.tsinghuabigdata.edu.ddmath.view.GridViewWithHeaderAndFooter;
import com.tsinghuabigdata.edu.ddmath.view.LoadingPager;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * 我的宝贝界面
 * Created by Administrator on 2017/12/20.
 */

public class MyTreasureActivity extends RoboActivity {

    private String mStudentId = "";
    private String mSchoolId  = "";
    private String mClassId   = "";

    private List<ProductBean> mList = new ArrayList<>();
    private MyTreasureAdapter mAdapter;
    private Context           mContext;

    private WorkToolbar                 mWorktoolbar;
    private GridViewWithHeaderAndFooter mGvMyTreasure;
    private LoadingPager                mLoadingPager;

    private int mNum;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        transparent = true;
        super.onCreate(savedInstanceState);

        //设置全屏显示
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (GlobalData.isPad()) {
            setContentView(R.layout.activity_my_treasure);
        } else {
            setContentView(R.layout.activity_my_treasure_phone);
        }
        initViews();
        initData();
    }

    private void initViews() {
        mContext = this;
        mWorktoolbar = (WorkToolbar) findViewById(R.id.worktoolbar);
        mGvMyTreasure = (GridViewWithHeaderAndFooter) findViewById(R.id.gv_my_treasure);
        mLoadingPager = (LoadingPager) findViewById(R.id.loadingPager);

        mWorktoolbar.setRightTitleAndLeftDrawable("添加宝贝", R.drawable.ic_add);
        mLoadingPager.setTargetView(mGvMyTreasure);
        mLoadingPager.setListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLoadingPager.showLoading();
                queryMyTreasure();
            }
        });

        String title = "我的宝藏";
        mWorktoolbar.setTitle(title);
        mWorktoolbar.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        }, null);
        mWorktoolbar.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ToastUtils.showShort(mContext, "添加宝贝");
                Intent intent = new Intent(mContext, AddTreasureActivity.class);
                startActivity(intent);
            }
        });
        initGridView();
    }

    private void initGridView() {
        int contentWidth;
        int itemW;
        int itemWidth;
        int screenWidth = WindowUtils.getScreenWidth(mContext);
        if (GlobalData.isPad()) {
            //左边距20 右边距20
            contentWidth = screenWidth - DensityUtils.dp2px(mContext, 20 + 20);
            itemW = 200;
            itemWidth = DensityUtils.dp2px(mContext, itemW + 15);
        } else {
            contentWidth = screenWidth - DensityUtils.dp2px(mContext, 15 + 15);
            itemW = 125;
            itemWidth = DensityUtils.dp2px(mContext, itemW + 10);
        }
        LogUtils.i("screenWidth=" + screenWidth + "contentWidth=" + contentWidth + " itemWidth=" + itemWidth);
        mNum = contentWidth / itemWidth;
        if (mNum < 2) {
            mNum = 2;
        }
        int space = contentWidth - DensityUtils.dp2px(mContext, mNum * itemW);
        int singleSpace = space / (mNum - 1);
        if (singleSpace < 0) {
            singleSpace = 0;
        }
        LogUtils.i("mNum=" + mNum + "space=" + space + "singleSpace=" + singleSpace);

        mGvMyTreasure.setNumColumns(mNum);
        mGvMyTreasure.setHorizontalSpacing(singleSpace);
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View headerView = layoutInflater.inflate(R.layout.header_view_my_treasure, null);
        View footerView = layoutInflater.inflate(R.layout.header_view_my_treasure, null);
        mGvMyTreasure.addHeaderView(headerView);
        mGvMyTreasure.addFooterView(footerView);
        mGvMyTreasure.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ProductBean productBean = mList.get(position);
                if (productBean != null) {
                    if (ProductUtil.effective(productBean, mClassId)) {
                        ProductDetailActivity.gotoProductDetailByProductId(mContext, productBean.getName(), productBean.getProductId());
                    } else {
                        int reportIndex = getReportIndex(productBean);
                        if (reportIndex >= 0) {
                            EventBus.getDefault().post(new JumpEvent(MainActivity.FRAGMENT_QUESTION_BOOK, reportIndex));
                            finish();
                        } else {
                            productBean.setShowIneffectiveText(true);
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        });
    }

    private int getReportIndex(ProductBean vo) {
        if (vo == null || vo.getPrivilegeVos() == null || vo.getPrivilegeVos().size() == 0) {
            return -1;
        }

        String privileageId = vo.getPrivilegeVos().get(0).getId();
        int index = -1;
        //错题订正
        if (AppConst.PRIVILEGE_QUESTION_DAYCLEAR.equals(privileageId)) {
            index = MyReportFragment.DAILY_CLEAR_I;
        }
        //错题周题练
        else if (AppConst.PRIVILEGE_QUESTION_WEEKPRACTICE.equals(privileageId)) {
            index = MyReportFragment.WEEK_EXTRACT_I;
        }
        //假期作业 校内套题作业 校内教辅作业
        else if (AppConst.PRIVILEGE_VACATION_WORK.equals(privileageId) || AppConst.PRIVILEGE_SETWORK.equals(privileageId) || AppConst.PRIVILEGE_ASSISTANTWORK.equals(privileageId)) {
            index = MyReportFragment.WORK_I;
        }
        return index;
    }


    private void initData() {
        if (AccountUtils.getUserdetailInfo() != null) {
            mStudentId = AccountUtils.getUserdetailInfo().getStudentId();
        }
        if (AccountUtils.getCurrentClassInfo() != null) {
            mSchoolId = AccountUtils.getCurrentClassInfo().getSchoolId();
            mClassId = AccountUtils.getCurrentClassInfo().getClassId();
        }
        queryMyTreasure();
    }

    private void queryMyTreasure() {
        new ProductModel().getSchoolProductList(mStudentId, mSchoolId, mClassId, new RequestListener<List<ProductBean>>() {

            @Override
            public void onSuccess(List<ProductBean> res) {
                LogUtils.i("queryProductList onSuccess");
                if (res == null || res.size() == 0) {
                    mLoadingPager.showEmpty();
                    return;
                }
                //LogUtils.i("res.size()=" + res.size());
                for (int i = 0; i < res.size(); i++) {
                    ProductBean vo = res.get(i);
                    if (vo.getProductType() == ProductBean.USED && !ProductBean.RAISE_RESOUCE.equals(vo.getCatalogId())) {
                        mList.add(vo);
                    }
                }
                if (mList.size() > 0) {
                    mAdapter = new MyTreasureAdapter(mContext, mList);
                    mGvMyTreasure.setAdapter(mAdapter);
                    mLoadingPager.showTarget();
                } else {
                    mLoadingPager.showEmpty();
                }

            }

            @Override
            public void onFail(HttpResponse<List<ProductBean>> response, Exception ex) {
                LogUtils.i("queryProductList onFail " + ex.getMessage());
                mLoadingPager.showFault(ex);
            }
        });
    }

}
