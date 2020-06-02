package com.tsinghuabigdata.edu.ddmath.parent.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.Nullable;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.tsinghuabigdata.edu.ddmath.MVPModel.RequestListener;
import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.fragment.MyBaseFragment;
import com.tsinghuabigdata.edu.ddmath.parent.MVPModel.CommunityModel;
import com.tsinghuabigdata.edu.ddmath.parent.activity.ArticleDetailActivity;
import com.tsinghuabigdata.edu.ddmath.parent.adapter.ArticleAdapter;
import com.tsinghuabigdata.edu.ddmath.parent.bean.ArticleItemBean;
import com.tsinghuabigdata.edu.ddmath.parent.bean.ArticleListBean;
import com.tsinghuabigdata.edu.ddmath.parent.bean.ParentInfo;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.AlertManager;
import com.tsinghuabigdata.edu.ddmath.util.LogUtils;
import com.tsinghuabigdata.edu.ddmath.util.MyViewUtils;
import com.tsinghuabigdata.edu.ddmath.util.ToastUtils;
import com.tsinghuabigdata.edu.ddmath.view.LoadingPager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * 社区模块
 * Created by Administrator on 2018/6/27.
 */

public class CommunityFragment extends MyBaseFragment implements PullToRefreshBase.OnRefreshListener2 {

    public static final String MSG_LIKE_COUNT = "msglikecount";
    public static final String MSG_READ_COUNT = "msgreadcount";

    private PullToRefreshListView mLvCommunity;
    private LoadingPager          mLoadingPager;

    private Context        mContext;
    private ArticleAdapter mAdapter;
    private List<ArticleItemBean> mList = new ArrayList<>();

    private int pageNum  = 1;
    private int pageSize = 15;
    private int mCurPosition;
    private String mParentId = "";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogUtils.i("ParentReportFragment onCreateView");
        View root = inflater.inflate(R.layout.fragment_community_phone, container, false);
        initView(root);
        setPrepared();
        initData();
        //EventBus.getDefault().register(this);
        return root;
    }


    @Override
    public String getUmEventName(){
        return "parent_main_community";
    }

    private void initView(View root) {
        mContext = getActivity();
        mLvCommunity = (PullToRefreshListView) root.findViewById(R.id.lv_community);
        mLoadingPager = (LoadingPager) root.findViewById(R.id.loadingPager);
        MyViewUtils.setPTRText(mContext, mLvCommunity);

        mLoadingPager.setTargetView(mLvCommunity);
        mLoadingPager.setListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLoadingPager.showLoading();
                queryArticle();
            }
        });
        mLvCommunity.setOnRefreshListener(this);
        mLvCommunity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mCurPosition = position - 1;
                ArticleItemBean bean = mList.get(mCurPosition);
                Intent intent = new Intent(mContext, ArticleDetailActivity.class);
                //intent.putExtra(ArticleDetailActivity.MSG_URL, AppRequestConst.WEB_ADDRESS + AppRequestConst.WEB_KNOWLEDGE_REPORT);
                intent.putExtra("articleName", bean.getArticleName());
                intent.putExtra("articleId", bean.getArticleId());
                intent.putExtra("createTime", bean.getCreateTime());

                startActivityForResult(intent, 0);
            }
        });
    }


    private void initData() {
        createLoginInfo();
        mAdapter = new ArticleAdapter(mContext, mList);
        mLvCommunity.setAdapter(mAdapter);
        queryArticle();
    }

    //查询登录信息
    private void createLoginInfo() {
        ParentInfo parentInfo = AccountUtils.getParentInfo();
        if (parentInfo != null) {
            mParentId = parentInfo.getParentId();
        }
    }

    private void queryArticle() {
        //analogList();
        HashMap<String, String> params = new LinkedHashMap<>();
        params.put("pageNum", pageNum + "");
        params.put("pageSize", pageSize + "");
        new CommunityModel().queryArticleList(params, new RequestListener<ArticleListBean>() {

            @Override
            public void onSuccess(ArticleListBean res) {
                LogUtils.i("queryArticle onSuccess");
                if (res == null || res.getItems() == null || res.getItems().size() == 0) {
                    mLoadingPager.showEmpty();
                    return;
                }
                mList.clear();
                mList.addAll(res.getItems());
                mAdapter.notifyDataSetChanged();
                if (res.getItems().size() < pageSize) {
                    mLvCommunity.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                } else {
                    mLvCommunity.setMode(PullToRefreshBase.Mode.BOTH);
                    pageNum++;
                }
                mLoadingPager.showTarget();
            }

            @Override
            public void onFail(HttpResponse<ArticleListBean> response, Exception ex) {
                LogUtils.i("queryArticle onFail");
                mLoadingPager.showFault(ex);
            }
        });
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase pullToRefreshBase) {
        getArticle(true);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase pullToRefreshBase) {
        getArticle(false);
    }

    private void getArticle(final boolean refresh) {
        if (refresh) {
            pageNum = 1;
        }
        HashMap<String, String> params = new LinkedHashMap<>();
        params.put("pageNum", pageNum + "");
        params.put("pageSize", pageSize + "");
        new CommunityModel().queryArticleList(params, new RequestListener<ArticleListBean>() {

            @Override
            public void onSuccess(ArticleListBean res) {
                LogUtils.i("getArticle onSuccess");
                if (res == null || res.getItems() == null || res.getItems().size() == 0) {
                    mLvCommunity.onRefreshComplete();
                    if (refresh) {
                        ToastUtils.showShort(mContext, R.string.server_error);
                    } else {
                        mLvCommunity.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                    }
                    return;
                }
                if (refresh) {
                    mList.clear();
                }
                mList.addAll(res.getItems());
                mAdapter.notifyDataSetChanged();
                mLvCommunity.onRefreshComplete();
                if (res.getItems().size() < pageSize) {
                    mLvCommunity.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                } else {
                    mLvCommunity.setMode(PullToRefreshBase.Mode.BOTH);
                    pageNum++;
                }
            }

            @Override
            public void onFail(HttpResponse<ArticleListBean> response, Exception ex) {
                LogUtils.i("getArticle onFail");
                mLvCommunity.onRefreshComplete();
                AlertManager.showErrorInfo(mContext, ex);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            ArticleItemBean bean = mList.get(mCurPosition);
            int like = data.getIntExtra(MSG_LIKE_COUNT, 0);
            int read = data.getIntExtra(MSG_READ_COUNT, 0);
            LogUtils.i("onActivityResult like=" + like + " read=" + read);
            bean.setLikeCount(like);
            bean.setReadCount(read);
            //            bean.setLikeCount(data.getIntExtra(MSG_LIKE_COUNT, 0));
            //            bean.setReadCount(data.getIntExtra(MSG_READ_COUNT, 0));
            mAdapter.notifyDataSetChanged();
        }
    }
}
