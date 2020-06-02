package com.tsinghuabigdata.edu.ddmath.module.product;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.tsinghuabigdata.edu.ddmath.MVPModel.ProductModel;
import com.tsinghuabigdata.edu.ddmath.MVPModel.RequestListener;
import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.activity.RoboActivity;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.view.WorkToolbar;
import com.tsinghuabigdata.edu.ddmath.module.product.adapter.AllTreasureAdapter;
import com.tsinghuabigdata.edu.ddmath.module.product.adapter.ProductCataAdapter;
import com.tsinghuabigdata.edu.ddmath.module.product.bean.ProductBean;
import com.tsinghuabigdata.edu.ddmath.module.product.bean.ProductCataBean;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.DensityUtils;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.LogUtils;
import com.tsinghuabigdata.edu.ddmath.util.ToastUtils;
import com.tsinghuabigdata.edu.ddmath.util.WindowUtils;
import com.tsinghuabigdata.edu.ddmath.view.GridViewWithHeaderAndFooter;
import com.tsinghuabigdata.edu.ddmath.view.LoadingPager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * 全部宝贝界面
 * Created by Administrator on 2017/12/19.
 */

public class AllTreasureActivity extends RoboActivity {


    public static final int EXCLUSIVE_RECOMMEND = 0;
    public static final int SUITE               = 1;
    public static final int RAISE_RESOUCE       = 2;


    private static final String MODULE = "module";


    private List<ProductBean>                  mList     = new ArrayList();
    private List<ProductCataBean>              mListCata = new ArrayList();
    private HashMap<String, List<ProductBean>> mCataMap  = new HashMap();


    private WorkToolbar                 mWorktoolbar;
    private LinearLayout                mLlTreasureContent;
    private ListView                    mLvCatalog;
    private GridViewWithHeaderAndFooter mGvTreasure;
    private LoadingPager                mLoadingPagerItem;
    private LoadingPager                mLoadingPager;

    private String mStudentId   = "";
    private String mSchoolId    = "";
    private String mClassId     = "";
    private int    mCurPosition = -1;
    private int mSourcePosition;
    private int mNum;


    private AllTreasureAdapter mAdapter;
    private ProductCataAdapter mCataAdapter;
    private Context            mContext;

    public static void startAllTreasureActivity(Context context, int index) {
        Intent intent = new Intent(context, AllTreasureActivity.class);
        intent.putExtra(MODULE, index);
        context.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        transparent = true;
        super.onCreate(savedInstanceState);

        //设置全屏显示
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (GlobalData.isPad()) {
            setContentView(R.layout.activity_all_treasure);
        } else {
            setContentView(R.layout.activity_all_treasure_phone);
        }
        initViews();
        initGridView();
        initData();
    }

    private void initViews() {
        mContext = this;
        mWorktoolbar = (WorkToolbar) findViewById(R.id.worktoolbar);
        mLlTreasureContent = (LinearLayout) findViewById(R.id.ll_all_treasure_content);
        mLvCatalog = (ListView) findViewById(R.id.lv_catalog);
        mGvTreasure = (GridViewWithHeaderAndFooter) findViewById(R.id.gv_treasure);
        mLoadingPagerItem = (LoadingPager) findViewById(R.id.loadingPager_item);
        mLoadingPager = (LoadingPager) findViewById(R.id.loadingPager);

        mLoadingPager.setTargetView(mLlTreasureContent);
        mLoadingPager.setListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLoadingPager.showLoading();
                queryCataList();
            }
        });
        mLoadingPagerItem.showEmpty();
        mLoadingPagerItem.setVisibility(View.INVISIBLE);
        mLvCatalog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                select(position);
            }
        });

        String title = "全部宝贝";
        mWorktoolbar.setTitle(title);
        mWorktoolbar.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        }, null);
    }

    private void initGridView() {
        int contentWidth;
        int itemW;
        int itemWidth;
        int screenWidth = WindowUtils.getScreenWidth(mContext);
        if (GlobalData.isPad()) {
            //导航栏180 左边距20 右边距20
            contentWidth = screenWidth - DensityUtils.dp2px(mContext, 180 + 20 + 20);
            itemW = 200;
            itemWidth = DensityUtils.dp2px(mContext, itemW + 15);
        } else {
            contentWidth = screenWidth - DensityUtils.dp2px(mContext, 124 + 15 + 15);
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

        mGvTreasure.setNumColumns(mNum);
        mGvTreasure.setHorizontalSpacing(singleSpace);
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View headerView = layoutInflater.inflate(R.layout.header_view_my_treasure, null);
        View footerView = layoutInflater.inflate(R.layout.header_view_my_treasure, null);
        mGvTreasure.addHeaderView(headerView);
        mGvTreasure.addFooterView(footerView);
    }

    private void select(int index) {
        if (index == mCurPosition) {
            return;
        }
        mCurPosition = index;
        for (int i = 0; i < mListCata.size(); i++) {
            mListCata.get(i).setSelect(i == index);
        }
        mCataAdapter.notifyDataSetChanged();
        updateData(index);
    }

    private void updateData(int index) {
        mList.clear();
        String catalogId = mListCata.get(index).getCatalogId();
        List<ProductBean> list = mCataMap.get(catalogId);
        mList.addAll(list);
        mAdapter.notifyDataSetChanged();
        if (mList.size() > 0) {
            mLoadingPagerItem.setVisibility(View.INVISIBLE);
        } else {
            if (index == 1) {
                mLoadingPagerItem.setEmptyText(R.string.no_product_set);
            } else {
                mLoadingPagerItem.setEmptyText(R.string.no_product);
            }
            mLoadingPagerItem.setVisibility(View.VISIBLE);
        }
    }

    private void initData() {
        mSourcePosition = getIntent().getIntExtra(MODULE, EXCLUSIVE_RECOMMEND);
        if (AccountUtils.getUserdetailInfo() != null) {
            mStudentId = AccountUtils.getUserdetailInfo().getStudentId();
        }
        if (AccountUtils.getCurrentClassInfo() != null) {
            mSchoolId = AccountUtils.getCurrentClassInfo().getSchoolId();
            mClassId = AccountUtils.getCurrentClassInfo().getClassId();
        }
        mAdapter = new AllTreasureAdapter(mContext, mList);
        mGvTreasure.setAdapter(mAdapter);
        mGvTreasure.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ProductBean productBean = mList.get(position);
                if (productBean != null) {
                    if (ProductBean.TYPE_PRODUCT.equals(productBean.getProductOrSuite())) {
                        ProductDetailActivity.gotoProductDetailByProductId(mContext, productBean.getName(), productBean.getProductId());
                    } else if (ProductBean.TYPE_SUITE.equals(productBean.getProductOrSuite())) {
                        ProductDetailActivity.gotoSuitDetail(mContext, productBean);
                    } else {
                        ToastUtils.showShort(mContext, R.string.param_error);
                    }
                }
            }
        });
        queryCataList();
    }

    /**
     * 查询所有商品分类列表
     */
    private void queryCataList() {
        new ProductModel().getProductGroupList(mStudentId, mSchoolId, mClassId, new RequestListener<List<ProductCataBean>>() {

            @Override
            public void onSuccess(List<ProductCataBean> res) {
                LogUtils.i("queryCataList onSuccess");
                if (res == null || res.size() == 0) {
                    mLoadingPager.showServerFault();
                    return;
                }
                queryProductList(res);
            }

            @Override
            public void onFail(HttpResponse<List<ProductCataBean>> response, Exception ex) {
                LogUtils.i("queryCataList onFail " + ex.getMessage());
                mLoadingPager.showFault(ex);
            }
        });
    }


    /**
     * 查询学校商品列表
     */
    private void queryProductList(final List<ProductCataBean> cataList) {
        new ProductModel().getSchoolProductList(mStudentId, mSchoolId, mClassId, new RequestListener<List<ProductBean>>() {

            @Override
            public void onSuccess(List<ProductBean> res) {
                LogUtils.i("queryProductList onSuccess");
                if (res == null || res.size() == 0) {
                    mLoadingPager.showEmpty();
                    return;
                }
                querySuiteList(cataList, res);
            }

            @Override
            public void onFail(HttpResponse<List<ProductBean>> response, Exception ex) {
                LogUtils.i("queryProductList onFail " + ex.getMessage());
                mLoadingPager.showFault(ex);
            }
        });
    }

    /**
     * 查询精品套餐列表
     */
    private void querySuiteList(final List<ProductCataBean> cataList, final List<ProductBean> productList) {
        new ProductModel().getSuiteList(mStudentId, mSchoolId, mClassId, "", new RequestListener<List<ProductBean>>() {

            @Override
            public void onSuccess(List<ProductBean> res) {
                LogUtils.i("querySuiteList onSuccess");
                classifyList(cataList, productList, res);
                mLoadingPager.showTarget();
            }

            @Override
            public void onFail(HttpResponse<List<ProductBean>> response, Exception ex) {
                LogUtils.i("querySuiteList onFail " + ex.getMessage());
                mLoadingPager.showFault(ex);
                //classifyList(cataList, productList, new ArrayList<ProductBean>());
                //mLoadingPager.showTarget();
            }
        });
    }

    /**
     * 对商品和套餐列表进行筛选分组
     *
     * @param cataList
     * @param productList
     * @param suiteList
     */
    private void classifyList(List<ProductCataBean> cataList, List<ProductBean> productList, List<ProductBean> suiteList) {
        creatCataList(cataList);
        List<ProductBean> listRecommendALL = new ArrayList();
        if (suiteList != null && suiteList.size() > 0) {
            List<ProductBean> tempSuiteList = mCataMap.get(ProductBean.PRODUCT_SET_ID);
            //遍历精品套餐列表 未使用的添加到专属推荐
            //SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            for (int i = 0; i < suiteList.size(); i++) {
                ProductBean vo = suiteList.get(i);
                //LogUtils.i("套餐列表 start:" + vo.getName() + " start:" + sdf.format(vo.getDisplayStartTime()) + "end:" + sdf.format(vo.getDisplayEndTime()));
                if (!ProductUtil.inDisplayTime(vo)) {
                    continue;
                }
                //添加精品套餐列表
                tempSuiteList.add(vo);
                if (vo.getProductType() == ProductBean.NOT_USED) {
                    listRecommendALL.add(vo);
                }
            }
            ProductUtil.rankExclusiveRecommend(tempSuiteList);
        }
        //遍历商品列表 进行筛选分组
        for (int i = 0; i < productList.size(); i++) {
            ProductBean vo = productList.get(i);
            //LogUtils.i("商品列表 name:" + vo.getName() + "end:" + sdf.format(vo.getDisplayEndTime()));
            if (!ProductUtil.inDisplayTime(vo) || !ProductBean.TYPE_PRODUCT.equals(vo.getProductOrSuite())) {
                continue;
            }
            if (vo.getProductType() == ProductBean.NOT_USED) {
                listRecommendALL.add(vo);
            }
            for (int j = 2; j < mListCata.size(); j++) {
                ProductCataBean productCataBean = mListCata.get(j);
                if (productCataBean.getCatalogId().equals(vo.getCatalogId())) {
                    List<ProductBean> list = mCataMap.get(productCataBean.getCatalogId());
                    list.add(vo);
                    break;
                }
            }
        }
        ProductUtil.rankExclusiveRecommend(listRecommendALL);
        List<ProductBean> listRecommend = mCataMap.get(ProductBean.EXCLUSIVE_RECOMMEND_ID);
        if (listRecommendALL.size() > 10) {
            listRecommend.addAll(listRecommendALL.subList(0, 10));
        } else if (listRecommendALL.size() > 0) {
            listRecommend.addAll(listRecommendALL);
        }
        selectCata();
        select(mSourcePosition);
    }

    private void creatCataList(List<ProductCataBean> cataList) {
        mListCata.add(new ProductCataBean(ProductBean.EXCLUSIVE_RECOMMEND_ID, "专属推荐", true));
        mListCata.add(new ProductCataBean(ProductBean.PRODUCT_SET_ID, "精品套餐", false));
        mListCata.addAll(cataList);
        mCataAdapter = new ProductCataAdapter(mContext, mListCata);
        mLvCatalog.setAdapter(mCataAdapter);
        for (int j = 0; j < mListCata.size(); j++) {
            ProductCataBean productCataBean = mListCata.get(j);
            List<ProductBean> list = new ArrayList<>();
            mCataMap.put(productCataBean.getCatalogId(), list);
        }
    }

    private void selectCata() {
        if (mSourcePosition == RAISE_RESOUCE) {
            for (int j = 0; j < mListCata.size(); j++) {
                ProductCataBean productCataBean = mListCata.get(j);
                if (productCataBean.getCatalogId().equals(ProductBean.RAISE_RESOUCE)) {
                    mSourcePosition = j;
                    break;
                }
            }
        }
    }

}
