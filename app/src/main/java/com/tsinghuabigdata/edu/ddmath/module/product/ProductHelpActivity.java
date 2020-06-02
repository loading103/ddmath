package com.tsinghuabigdata.edu.ddmath.module.product;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.tsinghuabigdata.edu.ddmath.MVPModel.ProductModel;
import com.tsinghuabigdata.edu.ddmath.MVPModel.RequestListener;
import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.activity.RoboActivity;
import com.tsinghuabigdata.edu.ddmath.bean.MyTutorClassInfo;
import com.tsinghuabigdata.edu.ddmath.bean.UserDetailinfo;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.view.WorkToolbar;
import com.tsinghuabigdata.edu.ddmath.module.product.bean.ProductBean;
import com.tsinghuabigdata.edu.ddmath.module.product.bean.ProductPrivilegeBean;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.AppLog;
import com.tsinghuabigdata.edu.ddmath.util.BitmapUtils;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.LogUtils;
import com.tsinghuabigdata.edu.ddmath.util.PicassoUtil;
import com.tsinghuabigdata.edu.ddmath.util.ToastUtils;
import com.tsinghuabigdata.edu.ddmath.util.WindowUtils;
import com.tsinghuabigdata.edu.ddmath.view.LoadingPager;

/**
 * 宝贝如何使用界面
 */

public class ProductHelpActivity extends RoboActivity {

    private static final String PARAM_SOURCE      = "source";
    private static final String PARAM_PRODUCTID   = "productId";
    private static final String PARAM_PRODUCTNAME = "productName";
    private static final String PARAM_PRIVILEGEID = "privilegeId";
    private static final String PARAM_ASSETNAME   = "assetname";
    private static final String PARAM_LANDSCREEN   = "landscreen";

    private static final int TYPE_PRODUCTBEAN = 0;      //商品详情直接传递过来，网络图片
    private static final int TYPE_PRIVILEGEID = 1;      //商品使用权ID,需要去服务器拉去相关信息
    private static final int TYPE_APPLOCAL    = 2;      //显示APP预置图片

    //对象
    private LoadingPager mLoadingPager;

    private ImageView mIvIntro;

    //传递进来的参数
    private int sourceType = TYPE_PRODUCTBEAN;    //来源类型
    //private ProductBean mProductBean;           //
    private String mProductId;                 //商品ID
    private String mPrivilegeId;             //商品使用权ID
    private String mProductName;             //商品名称
    private String mAssetName;               //预置图片资源ID

    private        Context     mContext;
    private static ProductBean mSuiteBean;

    public static void gotoProductHelpActivityBySuite(Context context, ProductBean suiteBean) {
        if (context == null || suiteBean == null) {
            AppLog.d(" gotoProductHelpActivityBySuite context=" + context + ",,suiteBean=" + suiteBean.getName());
            return;
        }
        mSuiteBean = suiteBean;
        Intent intent = new Intent(context, ProductHelpActivity.class);
        intent.putExtra(PARAM_SOURCE, TYPE_PRODUCTBEAN);
        intent.putExtra(PARAM_PRODUCTID, suiteBean.getProductSuiteId());
        intent.putExtra(PARAM_PRODUCTNAME, suiteBean.getName());
        context.startActivity(intent);
    }

    //商品实体 显示线上的使用说明
    public static void gotoProductHelpActivityByProductId(Context context, String productName, String productId) {
        if (context == null || TextUtils.isEmpty(productName) || TextUtils.isEmpty(productId)) {
            AppLog.d(" gotoProductHelpActivityByProductId context=" + context + ",,productName=" + productName + ",productId=" + productId);
            return;
        }
        Intent intent = new Intent(context, ProductHelpActivity.class);
        intent.putExtra(PARAM_SOURCE, TYPE_PRODUCTBEAN);
        intent.putExtra(PARAM_PRODUCTID, productId);
        intent.putExtra(PARAM_PRODUCTNAME, productName);
        context.startActivity(intent);
    }

    //商品名称和ID 显示线上的使用说明
    public static void gotoProductHelpActivity(Context context, String productName, String privilegeId) {
        if (context == null || TextUtils.isEmpty(productName) || TextUtils.isEmpty(privilegeId)) {
            AppLog.d(" gotoProductHelpActivity context=" + context + ",,productName=" + productName + ",privilegeId=" + privilegeId);
            return;
        }
        Intent intent = new Intent(context, ProductHelpActivity.class);
        intent.putExtra(PARAM_SOURCE, TYPE_PRIVILEGEID);
        intent.putExtra(PARAM_PRODUCTNAME, productName);
        intent.putExtra(PARAM_PRIVILEGEID, privilegeId);
        context.startActivity(intent);
    }

    //商品名称和resID 显示App预置的使用说明
    public static void startProductHelpActivityAsset(Context context, String name, String assetImageName, boolean landscreen ) {
        Intent intent = new Intent(context, ProductHelpActivity.class);
        intent.putExtra(PARAM_SOURCE, TYPE_APPLOCAL);
        intent.putExtra(PARAM_PRODUCTNAME, name);
        intent.putExtra(PARAM_ASSETNAME, assetImageName);
        intent.putExtra(PARAM_LANDSCREEN,landscreen);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        transparent = true;
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //设置全屏显示
        boolean land = getIntent().getBooleanExtra(PARAM_LANDSCREEN,true);
        //默认横版
        if( !land )
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(GlobalData.isPad() ? R.layout.activity_treasure_help : R.layout.activity_treasure_help_phone);

        if (!parseIntent()) {
            ToastUtils.show(this, "参数错误");
            finish();
            return;
        }
        initViews( land );
        showData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSuiteBean = null;
    }

    //--------------------------------------------------------------------------------------
    private boolean parseIntent() {

        boolean success = false;

        Intent intent = getIntent();
        sourceType = intent.getIntExtra(PARAM_SOURCE, -1);
        if (TYPE_PRODUCTBEAN == sourceType) {
            mProductId = intent.getStringExtra(PARAM_PRODUCTID);
            mProductName = intent.getStringExtra(PARAM_PRODUCTNAME);
            success = !TextUtils.isEmpty(mProductName) && !TextUtils.isEmpty(mProductId);
        } else if (TYPE_PRIVILEGEID == sourceType) {
            mProductName = intent.getStringExtra(PARAM_PRODUCTNAME);
            mPrivilegeId = intent.getStringExtra(PARAM_PRIVILEGEID);
            success = !TextUtils.isEmpty(mProductName) && !TextUtils.isEmpty(mPrivilegeId);
        } else if (TYPE_APPLOCAL == sourceType) {
            mProductName = intent.getStringExtra(PARAM_PRODUCTNAME);
            mAssetName = intent.getStringExtra(PARAM_ASSETNAME);
            success = !TextUtils.isEmpty(mProductName) && !TextUtils.isEmpty(mAssetName);
        }
        return success;
    }

    private void initViews(boolean landScreen ) {

        mContext = this;

        WorkToolbar mWorktoolbar = (WorkToolbar) findViewById(R.id.worktoolbar);
        mLoadingPager = (LoadingPager) findViewById(R.id.loadingPager);

        mIvIntro = (ImageView) findViewById(R.id.iv_intro);

        mLoadingPager.setTargetView(mIvIntro);
        mLoadingPager.setListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLoadingPager.showLoading();
                loadData();
            }
        });

        if (mWorktoolbar == null)
            return;
        if( landScreen )
            mWorktoolbar.setTitle(mProductName + "使用说明");
        else
            mWorktoolbar.setTitle(mProductName);
        mWorktoolbar.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        }, null);

    }

    private void showData() {

        //显示商品
        if (mSuiteBean != null) {
            mLoadingPager.showTarget();
            showGuidePic(mSuiteBean.getUserGuideImageWidth(), mSuiteBean.getUserGuideImageHeight(), mSuiteBean.getUserGuideImagePath());
        }

        //得到商品详情
        else if (TYPE_PRODUCTBEAN == sourceType || TYPE_PRIVILEGEID == sourceType) {
            loadData();
        }
        //显示本地图片
        else if (TYPE_APPLOCAL == sourceType) {
            BitmapUtils.showAssetImage(mContext, mAssetName, mIvIntro);
            mLoadingPager.showTarget();
        }
    }

    //获取商品信息
    private void loadData() {
        mLoadingPager.showLoading();
        UserDetailinfo detailInfo = AccountUtils.getUserdetailInfo();
        MyTutorClassInfo classInfo = AccountUtils.getCurrentClassInfo();
        if (detailInfo == null || classInfo == null) {
            ToastUtils.show(mContext, detailInfo == null ? R.string.relogin : R.string.jionclass);
            finish();
            return;
        }

        //通过商品ID
        if (!TextUtils.isEmpty(mProductId)) {
            new ProductModel().getProductDetailByProductId(classInfo.getSchoolId(), classInfo.getClassId(), detailInfo.getStudentId(), mProductId, requestListener);
        }
        //通过privilegeId查找商品
        else if (!TextUtils.isEmpty(mPrivilegeId)) {
            new ProductModel().getProductDetail(classInfo.getSchoolId(), classInfo.getClassId(), detailInfo.getStudentId(), mPrivilegeId, requestListener);
        }
    }

    //查询商品详情 结果响应
    private RequestListener<ProductPrivilegeBean> requestListener = new RequestListener<ProductPrivilegeBean>() {

        @Override
        public void onSuccess(ProductPrivilegeBean bean) {
            LogUtils.i("getProductGuide onSuccess");
            if (bean == null || bean.getProduct() == null) {
                mLoadingPager.showEmpty(R.string.nofind_product_usage);
                return;
            }

            ProductBean mProductBean = bean.getProduct();

            showGuidePic(mProductBean.getUserGuideImageWidth(), mProductBean.getUserGuideImageHeight(), mProductBean.getUserGuideImagePath());
        }

        @Override
        public void onFail(HttpResponse<ProductPrivilegeBean> response, Exception ex) {
            LogUtils.i("getProductGuide onFail " + ex.getMessage());
            mLoadingPager.showFault(ex);
        }
    };

    //显示线上图片
    private void showGuidePic(int picWidth, int picHeight, String path) {

        mLoadingPager.showTarget();

        if (TextUtils.isEmpty(path)) {
            PicassoUtil.displayEmptyImage(mIvIntro);
            return;
        }

        final String url = BitmapUtils.getUrlWithToken(path);
        int screenWidth = WindowUtils.getScreenWidth(mContext);
        LogUtils.i("screenWidth=" + screenWidth + " picWidth=" + picWidth + " picHeight=" + picHeight);
        if (picWidth <= 0 || picHeight <= 0) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        final Bitmap bitmap = PicassoUtil.getPicasso().load(url).get();
                        mLoadingPager.post(new Runnable() {
                            @Override
                            public void run() {
                                setBitmap(bitmap, url);
                            }
                        });
                    } catch (Exception e) {
                        AppLog.i("", e);
                    }
                }
            }).start();
            return;
        }
        ViewGroup.LayoutParams params = mIvIntro.getLayoutParams();
        if (picWidth < screenWidth) {
            params.width = picWidth;
            params.height = picHeight;
            mIvIntro.setLayoutParams(params);
            PicassoUtil.getPicasso(mContext).load(url)./*error(R.drawable.ic_broken_image).placeholder(R.drawable.ic_temporary_image).*/into(mIvIntro);
        } else {
            int height = screenWidth * picHeight / picWidth;
            params.height = height;
            mIvIntro.setLayoutParams(params);
            PicassoUtil.getPicasso(mContext).load(url)/*.error(R.drawable.ic_broken_image).placeholder(R.drawable.ic_temporary_image)*/.resize(screenWidth, height).into(mIvIntro);
        }
    }

    private void setBitmap(Bitmap bitmap, String url) {
        ViewGroup.LayoutParams params = mIvIntro.getLayoutParams();
        int screenWidth = WindowUtils.getScreenWidth(mContext);
        LogUtils.i("screenWidth=" + screenWidth + " getWidth=" + bitmap.getWidth() + " getHeight=" + bitmap.getHeight());
        if (bitmap.getWidth() < screenWidth) {
            params.width = bitmap.getWidth();
            params.height = bitmap.getHeight();
            mIvIntro.setLayoutParams(params);
        } else {
            params.height = screenWidth * bitmap.getHeight() / bitmap.getWidth();
            mIvIntro.setLayoutParams(params);
        }
        PicassoUtil.displayImageComUrl(url, mIvIntro);
    }
}
