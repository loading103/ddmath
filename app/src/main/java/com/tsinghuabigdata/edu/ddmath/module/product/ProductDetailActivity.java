package com.tsinghuabigdata.edu.ddmath.module.product;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tsinghuabigdata.edu.ddmath.MVPModel.ProductModel;
import com.tsinghuabigdata.edu.ddmath.MVPModel.RequestListener;
import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.activity.MainActivity;
import com.tsinghuabigdata.edu.ddmath.activity.RoboActivity;
import com.tsinghuabigdata.edu.ddmath.bean.MyTutorClassInfo;
import com.tsinghuabigdata.edu.ddmath.bean.UserDetailinfo;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.constant.AppConst;
import com.tsinghuabigdata.edu.ddmath.event.JumpEvent;
import com.tsinghuabigdata.edu.ddmath.fragment.ErrorBookFragment;
import com.tsinghuabigdata.edu.ddmath.fragment.MyStudyFragment;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.view.WorkToolbar;
import com.tsinghuabigdata.edu.ddmath.module.errorbook.NumberUtil;
import com.tsinghuabigdata.edu.ddmath.module.product.bean.FreeUseTimesBean;
import com.tsinghuabigdata.edu.ddmath.module.product.bean.PrivilegeVo;
import com.tsinghuabigdata.edu.ddmath.module.product.bean.ProductBean;
import com.tsinghuabigdata.edu.ddmath.module.product.bean.ProductPrivilegeBean;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.AlertManager;
import com.tsinghuabigdata.edu.ddmath.util.AppLog;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.LogUtils;
import com.tsinghuabigdata.edu.ddmath.util.PicassoUtil;
import com.tsinghuabigdata.edu.ddmath.util.ToastUtils;
import com.tsinghuabigdata.edu.ddmath.util.WindowUtils;
import com.tsinghuabigdata.edu.ddmath.view.LoadingPager;

import org.greenrobot.eventbus.EventBus;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;
import java.util.Locale;

import static java.lang.String.format;


/**
 * 宝贝特权界面
 * Created by Administrator on 2017/11/20.
 */

public class ProductDetailActivity extends RoboActivity implements View.OnClickListener {

    private static final String PARAM_PROCTUCTNAME     = "productname";
    private static final String PARAM_PRIVILEGE        = "privilegeid";
    private static final String PARAM_PROCTUCTID       = "productid";
    //private static final String PARAM_PROCTUCTTYPE      = "producttype";

    @ViewInject( R.id.worktoolbar )
    private WorkToolbar    mWorktoolbar;
    @ViewInject( R.id.loadingPager )
    private LoadingPager   mLoadingPager;

    @ViewInject(R.id.activity_goodsdetail_maincontent)
    private LinearLayout mainLayout;

    //@ViewInject( R.id.activity_goodsdetail_scollview )
    //private ScrollView mScrollView;

    @ViewInject( R.id.activity_goodsdetail_intro )
    private ImageView      mIvIntro;

    @ViewInject( R.id.activity_goodsdetail_exchangetype )
    private ImageView      mIvExchangeProductType;

    @ViewInject( R.id.activity_goodsdetail_leavecount )
    private TextView      mLeaveCountView;

    @ViewInject( R.id.activity_goodsdetail_count )
    private TextView       mUserCountView;

    @ViewInject( R.id.activity_goodsdetail_usedate )
    private TextView useDateView;

    @ViewInject( R.id.activity_goodsdetail_gostudy )
    private Button         mBtnToStudy;
    @ViewInject( R.id.activity_goodsdetail_gobuy )
    private ImageView      mBtnToBuy;

    @ViewInject( R.id.activity_goodsdetail_priceofbuy )
    private TextView       mPriceView;

    //参数
    private ProductPrivilegeBean productPrivilegeBean;
    private String  mProductId;             //商品ID
    private String  mPrivilegeId;           //商品 使用权ID
    private String  mProductName;           //商品显示名称
    //private String  productType;            //区分商品和套餐
    private static ProductBean mSuiteBean;         //套餐实体类

    private Context mContext;

    private ProductBean mProductBean;       //商品信息

    public static void gotoProductDetailByProductId(Context context, String productName, String productId) {
        gotoProductDetailActivityById(context,productName,productId);
    }

    public static void gotoSuitDetail(Context context, ProductBean suiteBean) {
        if( context==null || suiteBean==null ){
            AppLog.d(" gotoSuitDetail context="+context+",,suiteBean="+ suiteBean );
            return;
        }
        mSuiteBean = suiteBean;
        gotoProductDetailActivityById(context,mSuiteBean.getName(), mSuiteBean.getProductSuiteId() );
    }

    //通过productId
    public static void gotoProductDetailActivityById(Context context, String productName, String productId) {
        if( context==null || TextUtils.isEmpty(productName) || TextUtils.isEmpty(productId) ){
            AppLog.d(" gotoProductDetailActivityByProductId context="+context+",,productName="+ productName + ",productId="+ productId);
            return;
        }
        Intent intent = new Intent(context, ProductDetailActivity.class );
        intent.putExtra( PARAM_PROCTUCTID, productId );
        intent.putExtra( PARAM_PROCTUCTNAME, productName );
        context.startActivity(intent);
    }

    //
    public static void gotoProductDetailActivity(Context context, String productName, String privilegeId ) {
        if( context==null || TextUtils.isEmpty(productName) || TextUtils.isEmpty(privilegeId) ){
            AppLog.d(" gotoProductDetailActivityByProductId context="+context+",,productName="+ productName + ",privilegeId="+ privilegeId);
            return;
        }
        Intent intent = new Intent(context, ProductDetailActivity.class);
        intent.putExtra(PARAM_PRIVILEGE, privilegeId);
        intent.putExtra( PARAM_PROCTUCTNAME, productName );
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        transparent = true;
        super.onCreate(savedInstanceState);

        //设置全屏显示
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView( GlobalData.isPad()?R.layout.activity_treasure:R.layout.activity_treasure_phone);
        x.view().inject(this);
        if( !parseIntent() ){
            ToastUtils.show( this, "参数错误" );
            finish();
            return;
        }
        initViews();
        loadData();
    }

    private int clickCount = 0;
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_goodsdetail_count:{
                clickCount++;
                if( clickCount >= 5 ){
                    clickCount = 0;
                    if( mProductBean != null )
                    ToastUtils.showShort( mContext, "Use: "+mProductBean.getPrivilegeVos().get(0).getUsePeople() );
                }
                break;
            }
            case R.id.activity_goodsdetail_gostudy:{
                goToStudy();
                break;
            }
            case R.id.activity_goodsdetail_gobuy:{

                //商品套餐
                if( mSuiteBean != null ){
                    //商品是否过期
                    if( ProductUtil.ckeckProductOverdue( mContext, mSuiteBean ) ){
                        break;
                    }
                    //跳转到
                    ProductExchangeActivity.gotoProductSuiteExchangeActivity( mContext, mSuiteBean );
                }else{
                    //商品是否过期
                    if( ProductUtil.ckeckProductOverdue( mContext, mProductBean ) ){
                        break;
                    }

                    //已发送使用权的，不能再次购买：单个商品类， 提分之源类
                    if( ProductBean.VACATION_WORK.equals( mProductBean.getCatalogId() ) || ProductBean.RAISE_RESOUCE.equals( mProductBean.getCatalogId() ) || (ProductBean.OTHER.equals( mProductBean.getCatalogId() ) && AppConst.PRIVILEGE_CAMPUS_SCANNING.equals(getPrivilegeId(mProductBean)) ) ){
                        //判断数据是否正常
                        if( mProductBean.getPrivilegeVos()!=null && mProductBean.getPrivilegeVos().size()>0 && mProductBean.getPrivilegeVos().get(0)!=null ){
                            queryProductIfBuyed( mProductBean.getProductId(), mProductBean.getCatalogId() );
                            break;
                        }
                    }
                    //其他默认处理
                    gotoExchange();
                }
                break;
            }
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if( resultCode == RESULT_OK && intent.hasExtra("bean") ){
            ProductPrivilegeBean privilegeBean = (ProductPrivilegeBean)intent.getSerializableExtra("bean");
            if( privilegeBean!=null && privilegeBean.getProduct()!=null ){
                ProductBean productBean = privilegeBean.getProduct();

                if( productPrivilegeBean!=null && productPrivilegeBean.getProduct()!=null ){
                    productPrivilegeBean.getProduct().setClassUseTimesList( productBean.getClassUseTimesList() );
                    showLeaveUseTimes( productPrivilegeBean.getProduct().getPrivilegeVos().get(0).getId() );
                }
            }
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        mSuiteBean = null;
    }
    //-----------------------------------------------------------------
    private void initViews() {
        mContext = this;

        mBtnToStudy.setOnClickListener(this);
        mBtnToBuy.setOnClickListener(this);
        mUserCountView.setOnClickListener( this );

        mLoadingPager.setTargetView(mainLayout);
        mLoadingPager.setListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLoadingPager.showLoading();
                loadData();
            }
        });

        mWorktoolbar.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if( mSuiteBean!=null ){
                    ProductHelpActivity.gotoProductHelpActivityBySuite( mContext, mSuiteBean );
                }else if( mProductBean!=null ){
                    ProductHelpActivity.gotoProductHelpActivityByProductId( mContext, mProductBean.getName(), mProductBean.getProductId() );
                }
            }
        });

        mWorktoolbar.setTitle( mProductName + "特权" );
    }

    private boolean parseIntent(){
        mProductId     = getIntent().getStringExtra( PARAM_PROCTUCTID );
        mPrivilegeId   = getIntent().getStringExtra( PARAM_PRIVILEGE );
        mProductName   = getIntent().getStringExtra( PARAM_PROCTUCTNAME );
        //productType    = getIntent().getStringExtra( PARAM_PROCTUCTTYPE );

        return !TextUtils.isEmpty(mProductName) && (!TextUtils.isEmpty(mProductId) || !TextUtils.isEmpty(mPrivilegeId));      //任意一个有值就可以
    }

    private void loadData() {
        UserDetailinfo detailInfo = AccountUtils.getUserdetailInfo();
        MyTutorClassInfo classInfo = AccountUtils.getCurrentClassInfo();
        if( detailInfo == null || classInfo == null ) {
            ToastUtils.show( mContext, detailInfo==null?R.string.relogin:R.string.jionclass );
            finish();
            return;
        }

        //显示商品套餐
        if( mSuiteBean!=null ){
            mLoadingPager.showTarget();
            //显示
            if( mSuiteBean.getProductVoList() == null || mSuiteBean.getProductVoList().size() == 0 ){
                new ProductModel().queryProductSuitedetail( mSuiteBean.getProductSuiteId(), requestSuiteListener );
            }else{
                //直接显示
                mWorktoolbar.setRightTitleAndLeftDrawable( "如何使用", R.drawable.ic_howtouse_white );
                showProductSuiteDetail();
            }
        }
        //通过productId查询商品
        else if( !TextUtils.isEmpty(mProductId) ){
            mLoadingPager.showLoading();
            new ProductModel().getProductDetailByProductId( classInfo.getSchoolId(), classInfo.getClassId(), detailInfo.getStudentId(), mProductId, requestListener );
        }
        //通过privilegeId查找商品
        else if( !TextUtils.isEmpty(mPrivilegeId) ){
            mLoadingPager.showLoading();
            new ProductModel().getProductDetail( classInfo.getSchoolId(), classInfo.getClassId(), detailInfo.getStudentId(), mPrivilegeId, requestListener );
        }
    }

    //商品查询结果的回调处理
    private RequestListener<ProductPrivilegeBean> requestListener = new RequestListener<ProductPrivilegeBean>() {

        @Override
        public void onSuccess(ProductPrivilegeBean bean) {
            LogUtils.i("getProductGuide onSuccess");
            if (bean == null || bean.getProduct() == null ) {
                mLoadingPager.showEmpty(  R.string.nofind_product );
                return;
            }
            mLoadingPager.showTarget();
            productPrivilegeBean = bean;
            mProductBean = bean.getProduct();

            //加载成功，再显示
            mWorktoolbar.setRightTitleAndLeftDrawable( "如何使用", R.drawable.ic_howtouse_white );

            showProductDetail();
        }

        @Override
        public void onFail(HttpResponse<ProductPrivilegeBean> response, Exception ex) {
            LogUtils.i("getProductGuide onFail " + ex.getMessage());
            mLoadingPager.showFault(ex);
        }
    };

    //显示商品信息
    private void showProductDetail(){
        if( mProductBean == null ) return;

        //标题
        mWorktoolbar.setTitle( mProductBean.getName()+"特权" );

        //介绍图片
        showGuidePic( mProductBean.getIntroImageWidth(), mProductBean.getIntroImageHeight(), mProductBean.getIntroImagePath() );

        String privilegeId = "";
        int useCount = 0;
        List<PrivilegeVo> list = mProductBean.getPrivilegeVos();
        if (list != null && list.size() > 0 && list.get(0) != null) {
            useCount = list.get(0).getUsePeople();
            privilegeId = list.get(0).getId();
        }
        //显示剩余使用次数
        showLeaveUseTimes( privilegeId );

        //使用人次
        mUserCountView.setText( format( Locale.getDefault(), "已有%s人次在使用", NumberUtil.approximateNumber(useCount) ) );

        //使用期限
        useDateView.setText( ProductUtil.getProductUseTime( mProductBean ) );

        //直接购买类商品，单次
        if( ProductBean.CHARGE_SINGLE_PRODUCT == mProductBean.getChargeType() ) {
            mBtnToBuy.setVisibility(View.INVISIBLE);

            mPriceView.setVisibility(View.GONE);
            mPriceView.setText(format(Locale.getDefault(), "需学豆：%d个", mProductBean.getChargeDdAmt()));
        }

        //按套题次  去兑换（校内教辅和校内套题）
        //按题次 里面的日日清
        else if( (ProductBean.CHARGE_QUESTIONSET_TIMES == mProductBean.getChargeType()&& !ProductBean.FAMOUS_TEACHER.equals(mProductBean.getCatalogId()) )
                || AppConst.PRIVILEGE_QUESTION_DAYCLEAR.equals( privilegeId )
                ){
            mBtnToBuy.setVisibility(View.INVISIBLE);
            mPriceView.setVisibility(View.GONE);
        }

        //在其他地方购买    套次类
        else{
            mBtnToBuy.setVisibility(View.GONE);
            mPriceView.setVisibility(View.GONE);
        }
    }

    //显示商品套餐信息
    private void showProductSuiteDetail(){

        if( mSuiteBean == null ) return;

        //标题
        mWorktoolbar.setTitle( mSuiteBean.getName()+"特权" );

        //介绍图片
        showGuidePic( mSuiteBean.getIntroImageWidth(), mSuiteBean.getIntroImageHeight(), mSuiteBean.getIntroImagePath() );

        //显示剩余使用次数
        mLeaveCountView.setVisibility( View.GONE );
        //showLeaveUseTimes( privilegeId );

        //使用人次
        mUserCountView.setText( format( Locale.getDefault(), "已有%s人次在使用", NumberUtil.approximateNumber( mSuiteBean.getUsePeopleCount() ) ) );

        //使用期限
        useDateView.setText( ProductUtil.getProductUseTime( mSuiteBean ) );

        //仅仅显示去兑换
        mBtnToBuy.setVisibility(View.INVISIBLE);
        mPriceView.setVisibility(View.GONE);

        mBtnToStudy.setVisibility( View.GONE );
    }

    //显示剩余次数设置
    private void showLeaveUseTimes( String privilegeId ){
        mIvExchangeProductType.setImageResource( R.drawable.ic_frequency );

        int count = ProductUtil.getLeaveUseTimes( mProductBean.getClassUseTimesList() );
        String leaveCount = count >=0 ? String.valueOf( count ) : "无限";

        //原版教辅 剩余次数
        if( AppConst.PRIVILEGE_ORIGINALMATERIAL.equals( privilegeId ) ){
            mLeaveCountView.setText( format( Locale.getDefault(), "剩余豆豆诊断作业上传次数：%s次", leaveCount  ) );

            //设置专门的图片按钮
            mBtnToBuy.setImageResource( R.drawable.btn_tochange_worktimes );
            ViewGroup.LayoutParams layoutParams = mBtnToBuy.getLayoutParams();
            layoutParams.width = WindowUtils.dpToPixels( mContext, GlobalData.isPad()? 240:160 );
            layoutParams.height = WindowUtils.dpToPixels( mContext, GlobalData.isPad()? 54:36 );
            mBtnToBuy.setLayoutParams( layoutParams );
        }
        //校内教辅 剩余次数
        else if( AppConst.PRIVILEGE_ASSISTANTWORK.equals( privilegeId ) ){
            mLeaveCountView.setText( format( Locale.getDefault(), "剩余教辅作业上传次数：%s次", leaveCount  ) );

            //设置专门的图片按钮
            mBtnToBuy.setImageResource( R.drawable.selector_exchangematerial_btn );
            ViewGroup.LayoutParams layoutParams = mBtnToBuy.getLayoutParams();
            layoutParams.width = WindowUtils.dpToPixels( mContext, GlobalData.isPad()? 240:160 );
            layoutParams.height = WindowUtils.dpToPixels( mContext, GlobalData.isPad()? 54:36 );
            mBtnToBuy.setLayoutParams( layoutParams );
        }
        //校内套题 剩余次数
        else if( AppConst.PRIVILEGE_SETWORK.equals( privilegeId ) ){

            mLeaveCountView.setText( format( Locale.getDefault(), "剩余套题作业上传次数：%s次", leaveCount  ) );

            //设置专门的图片按钮
            mBtnToBuy.setImageResource( R.drawable.selector_exchangepratice_btn );
            ViewGroup.LayoutParams layoutParams = mBtnToBuy.getLayoutParams();
            layoutParams.width = WindowUtils.dpToPixels( mContext, GlobalData.isPad()? 240:160 );
            layoutParams.height = WindowUtils.dpToPixels( mContext, GlobalData.isPad()? 54:36 );
            mBtnToBuy.setLayoutParams( layoutParams );

        }
        //错题订正次数 剩余次数
        else if( AppConst.PRIVILEGE_QUESTION_DAYCLEAR.equals( privilegeId ) ) {     //
            mLeaveCountView.setText( format( Locale.getDefault(), "剩余订正次数：%s次", leaveCount ) );

            //设置专门的图片按钮
            mBtnToBuy.setImageResource( R.drawable.selector_exchange_correcttimes_btn );
            ViewGroup.LayoutParams layoutParams = mBtnToBuy.getLayoutParams();
            layoutParams.width = WindowUtils.dpToPixels( mContext, GlobalData.isPad()? 192:128 );
            layoutParams.height = WindowUtils.dpToPixels( mContext, GlobalData.isPad()? 54:36 );
            mBtnToBuy.setLayoutParams( layoutParams );
        }
        //校园扫描作业
        else if( AppConst.PRIVILEGE_CAMPUS_SCANNING.equals( privilegeId ) ){
            
            mLeaveCountView.setText( format( Locale.getDefault(), "剩余校园扫描服务次数：%s次", leaveCount ) );

            //设置专门的图片按钮
            mBtnToBuy.setImageResource( R.drawable.btn_tochargescanserve );
            ViewGroup.LayoutParams layoutParams = mBtnToBuy.getLayoutParams();
            layoutParams.width = WindowUtils.dpToPixels( mContext, GlobalData.isPad()? 270:180 );
            layoutParams.height = WindowUtils.dpToPixels( mContext, GlobalData.isPad()? 60:40 );
            mBtnToBuy.setLayoutParams( layoutParams );

        } else{
            //mIvExchangeProductType.setVisibility( View.GONE );
            mLeaveCountView.setVisibility( View.GONE );
        }
    }

    //显示商品介绍图片
    private void showGuidePic(int picWidth, int picHeight, String path) {
        if (TextUtils.isEmpty(path)) {
            PicassoUtil.displayEmptyImage(mIvIntro);
            return;
        }
        final String url = AccountUtils.getFileServer() + path;
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
            PicassoUtil.getPicasso(mContext).load(url).error(R.drawable.ic_broken_image).placeholder(R.drawable.ic_temporary_image).into(mIvIntro);
        } else {
            int height = screenWidth * picHeight / picWidth;
            params.height = height;
            mIvIntro.setLayoutParams(params);
            PicassoUtil.getPicasso(mContext).load(url).error(R.drawable.ic_broken_image).placeholder(R.drawable.ic_temporary_image).resize(screenWidth, height).into(mIvIntro);
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

    //去学习
    private void goToStudy() {

        if( mProductBean==null || mProductBean.getPrivilegeVos()==null || mProductBean.getPrivilegeVos().size()==0 ) return;

        String privileageId = mProductBean.getPrivilegeVos().get(0).getId();
        String catalogId = mProductBean.getCatalogId();

        final int STUDY_WORK_BASE = 10;

        int index = -1;

        //错题订正
        if (AppConst.PRIVILEGE_QUESTION_DAYCLEAR.equals(privileageId)) {
            index = STUDY_WORK_BASE + MyStudyFragment.MODEL_ERRORREVISE;
        }
        //错题周题练
        else if (AppConst.PRIVILEGE_QUESTION_WEEKPRACTICE.equals(privileageId)) {
            index = ErrorBookFragment.MODEL_WEEKTRAIN;
        }
        //错题本下载
        else if (AppConst.PRIVILEGE_PERIODREVIEW.equals(privileageId)) {
            index = ErrorBookFragment.MODEL_ERRBOOK;
        }
        //提分之源类: 校内教辅作业  校内套题作业
        //豆豆检查作业
        else if ( AppConst.PRIVILEGE_ORIGINALMATERIAL.equals(privileageId) ) {
            index = STUDY_WORK_BASE + MyStudyFragment.MODEL_CHECK_WORK;
        }
        //假期作业类:
        else if ( ProductBean.RAISE_RESOUCE.equals(catalogId) /*AppConst.PRIVILEGE_SETWORK.equals(privileageId) || AppConst.PRIVILEGE_ASSISTANTWORK.equals(privileageId)*/ || ProductBean.VACATION_WORK.equals(catalogId) || AppConst.PRIVILEGE_CAMPUS_SCANNING.equals(privileageId) ) {
            index = STUDY_WORK_BASE + MyStudyFragment.MODEL_SCHOOLWORK;
        }
        //专属套题
        else if ( ProductBean.EXCLUSIVE_SET.equals(catalogId) ) {
            //index = STUDY_WORK_BASE + MyStudyFragment.MODEL_PRACTICE;
        }
        //名师精讲
        else if( ProductBean.FAMOUS_TEACHER.equals(catalogId ) ){
            index = STUDY_WORK_BASE + MyStudyFragment.MODEL_FAMOUS_TEACHER;
        }

        if (index >= 0 && index <= ErrorBookFragment.MODEL_BROWER) {
            finishAll();
            EventBus.getDefault().post(new JumpEvent(MainActivity.FRAGMENT_QUESTION_BOOK, index));
        } else if (index >= STUDY_WORK_BASE ) {
            finishAll();
            EventBus.getDefault().post(new JumpEvent(MainActivity.FRAGMENT_STUDY_TASK, index-STUDY_WORK_BASE));
        } else {
            ToastUtils.showShort(mContext, "正在建设中，敬请期待");
        }
    }

    //查看是否已经购买过
    private void queryProductIfBuyed( final String productId, final String catalogId ){

        UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
        MyTutorClassInfo classInfo = AccountUtils.getCurrentClassInfo();
        if( detailinfo == null || classInfo==null ) return;

        ProductModel productModel = new ProductModel();
        productModel.getProductUseTimesByProduct(detailinfo.getStudentId(), classInfo.getClassId(), productId, new RequestListener<FreeUseTimesBean>() {
            @Override
            public void onSuccess(FreeUseTimesBean bean) {

                if( bean != null ) {
                    //假期作业类
                    if(  ProductBean.VACATION_WORK.equals( catalogId ) ){
                        if( bean.getTotalUseTimes() > 0 ){
                            ToastUtils.show( mContext, "您目前已有该宝贝的使用权，请先去学习吧。");
                            return;
                        }
                    }
                    //校内教辅作业和校内套题作业，
                    else {
                        if( bean.getFreeUseTimes() > 0 ){
                            ToastUtils.show( mContext, "您目前已有该宝贝的使用权，请先去学习吧。");
                            return;
                        }
                    }
                }
                //mPriceView.setVisibility(View.VISIBLE);
                //开始兑换
                gotoExchange();
            }

            @Override
            public void onFail(HttpResponse response, Exception ex) {
                AlertManager.showErrorInfo( mContext, ex );
            }
        });
    }

    //点击去兑换的操作
    private void gotoExchange(){
        String privilegeId = "";
        List<PrivilegeVo> list = mProductBean.getPrivilegeVos();
        if (list != null && list.size() > 0 && list.get(0) != null) {
            privilegeId = list.get(0).getId();
        }

        //直接购买类商品，单次： 寒假作业 类型
        if( ProductBean.CHARGE_SINGLE_PRODUCT == mProductBean.getChargeType() ) {
            ProductExchangeActivity.gotoProductExchangeActivity( mContext, productPrivilegeBean );
        }

        //按套题次  去兑换：教辅作业上传  套题作业上传
        //按题次 里面的错题订正
        else if( ProductBean.CHARGE_QUESTIONSET_TIMES == mProductBean.getChargeType()
                || AppConst.PRIVILEGE_QUESTION_DAYCLEAR.equals( privilegeId )
                ){
            ProductExchangeActivity.gotoProductExchangeActivity( mContext, productPrivilegeBean );
        }

        //其他
        else{
            ToastUtils.show( mContext, R.string.product_no_relate);
        }
    }

    //
    private String getPrivilegeId( ProductBean bean ){
        String privilegeId = "";
        if( bean.getPrivilegeVos()!=null && bean.getPrivilegeVos().size()>0 && bean.getPrivilegeVos().get(0)!=null ){
            privilegeId = bean.getPrivilegeVos().get(0).getId();
        }
        return privilegeId;
    }

    //商品套餐
    private RequestListener<ProductBean> requestSuiteListener = new RequestListener<ProductBean>() {

        @Override
        public void onSuccess(ProductBean bean) {
            LogUtils.i("getProductGuide onSuccess");
            if (bean == null || bean.getProductVoList() == null ) {
                mLoadingPager.showEmpty(  R.string.nofind_product );
                return;
            }
            mLoadingPager.showTarget();

            mSuiteBean = bean;

            //加载成功，再显示
            mWorktoolbar.setRightTitleAndLeftDrawable( "如何使用", R.drawable.ic_howtouse_white );
            showProductSuiteDetail();
        }

        @Override
        public void onFail(HttpResponse<ProductBean> response, Exception ex) {
                mLoadingPager.showFault(ex);
        }
    };
}
