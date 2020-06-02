package com.tsinghuabigdata.edu.ddmath.parent.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.tsinghuabigdata.edu.ddmath.MVPModel.ProductModel;
import com.tsinghuabigdata.edu.ddmath.MVPModel.RequestListener;
import com.tsinghuabigdata.edu.ddmath.MVPModel.UserCenterModel;
import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.activity.BasePayActivity;
import com.tsinghuabigdata.edu.ddmath.bean.MyTutorClassInfo;
import com.tsinghuabigdata.edu.ddmath.bean.StudyBean;
import com.tsinghuabigdata.edu.ddmath.bean.UserDetailinfo;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.module.message.MessageUtils;
import com.tsinghuabigdata.edu.ddmath.module.product.ProductUtil;
import com.tsinghuabigdata.edu.ddmath.module.product.bean.ProductBean;
import com.tsinghuabigdata.edu.ddmath.parent.adapter.ParentSuiteAdapter;
import com.tsinghuabigdata.edu.ddmath.parent.bean.ParentInfo;
import com.tsinghuabigdata.edu.ddmath.parent.view.ParentToolbar;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.DataUtils;
import com.tsinghuabigdata.edu.ddmath.util.LogUtils;
import com.tsinghuabigdata.edu.ddmath.util.MyViewUtils;
import com.tsinghuabigdata.edu.ddmath.util.ToastUtils;
import com.tsinghuabigdata.edu.ddmath.view.LoadingPager;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 家长端--商品套餐界面
 */

public class ParentProductActivity extends BasePayActivity implements PullToRefreshBase.OnRefreshListener<ListView> {

    @ViewInject(R.id.worktoolbar)
    private ParentToolbar workToolbar;
    @ViewInject(R.id.loadingPager)
    private LoadingPager mLoadingPager;

    @ViewInject(R.id.main_layout)
    private LinearLayout mainLayout;
    @ViewInject(R.id.tv_usable_xuedou)
    private TextView usabeXueDouView;
    @ViewInject(R.id.lv_product_list)
    private PullToRefreshListView mListView;
    @ViewInject(R.id.ll_no_class_tips)
    private LinearLayout noClassLayout;

    private Context mContext;
    private ParentSuiteAdapter mAdapter;
    private ArrayList<ProductBean> productList = new ArrayList<>();

    //参数
    private String privilegeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        transparent = true;
        isParent = true;
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_parent_product);
        privilegeId = getIntent().getStringExtra("privilegeId");
        x.view().inject( this );
        mContext = this;
        initView();
        loadData();
        updateLearnDouCount();
    }
    @Override
    public String getUmEventName() {
        return "parent_mycenter_product";
    }

    @Override
    public void onRefresh(PullToRefreshBase<ListView> pullToRefreshBase) {
        //下拉重新加载
        if (pullToRefreshBase.isHeaderShown()) {
            loadData();
        }
//        //上拉加载更多
//        else if( pullToRefreshBase.isFooterShown() ){
//            //currPage++;
//            loadData();
//        }
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

        workToolbar.setTitle( "我要购买" );
        workToolbar.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        },null,new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserDetailinfo detailinfo = AccountUtils.getParentUserDetailinfo();
                if( detailinfo == null ){
                    ToastUtils.show( mContext,"没有学生信息");
                    return;
                }
                String url =MessageUtils.getUseBeanDetailUrl( detailinfo.getStudentId() );
                ReportDetailActivity.openReportDetailActivity( mContext, "购买详情", DataUtils.getUrl(mContext,url) );
            }
        } );
        workToolbar.setRightText( "购买详情" );

        MyViewUtils.setPTRText( mContext, mListView);
        mListView.setOnRefreshListener(this);
        mListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);

        mAdapter = new ParentSuiteAdapter( mContext, productList);
        mListView.setAdapter(mAdapter);
        mAdapter.setSuiteSelectListener(new ParentSuiteAdapter.SuiteSelectListener() {
            @Override
            public void select(ProductBean item) {
                if (!ProductUtil.ckeckAllowRepeatBuy(mContext, item)) {
                    return;
                }
                if (ProductUtil.ckeckSuiteOverdue(mContext, item)) {
                    return;
                }
                toPay( item );
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
    }

    private void loadData(){

        ParentInfo parentInfo = AccountUtils.getParentInfo();
        UserDetailinfo userDetailinfo = AccountUtils.getParentUserDetailinfo();
        MyTutorClassInfo classInfo = AccountUtils.getCurrentClassInfoForParent();
        if (parentInfo == null || userDetailinfo == null ) {
            return;
        }
        if( classInfo==null ){
            //mLoadingPager.showEmpty("您的孩子还没有加入班级，去提醒他加入班级吧～");
            noClassLayout.setVisibility(View.VISIBLE);
            mListView.setVisibility( View.GONE );
            mLoadingPager.showTarget();
            return;
        }

        mLoadingPager.startAnim();
        mLoadingPager.showLoading();

        new ProductModel().getSuiteList( userDetailinfo.getStudentId(), classInfo.getSchoolId(), classInfo.getClassId(), AccountUtils.getSchoolIdsForParent(), new RequestListener<List<ProductBean>>() {

            @Override
            public void onSuccess(List<ProductBean> list) {
                mListView.onRefreshComplete();

                List<ProductBean> tlist;
                if (list == null || list.size() == 0 || (tlist = ProductUtil.rankSuite(list)).size() == 0) {
                    mLoadingPager.showEmpty(R.string.no_product_set);
                    return;
                }
                mLoadingPager.showTarget();
                productList.clear();
                productList.addAll(tlist);
                int curPosition = ProductUtil.getPrivilegePosition(tlist, privilegeId) + 1;
                mAdapter.setCurPosition(curPosition - 1);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFail(HttpResponse<List<ProductBean>> response, Exception ex) {
                mListView.onRefreshComplete();
                mLoadingPager.showFault(ex);
            }
        });
    }

}
