package com.tsinghuabigdata.edu.ddmath.module.product;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tsinghuabigdata.edu.ddmath.MVPModel.ProductModel;
import com.tsinghuabigdata.edu.ddmath.MVPModel.RequestListener;
import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.activity.MainActivity;
import com.tsinghuabigdata.edu.ddmath.activity.RoboActivity;
import com.tsinghuabigdata.edu.ddmath.bean.MyTutorClassInfo;
import com.tsinghuabigdata.edu.ddmath.bean.StudyBean;
import com.tsinghuabigdata.edu.ddmath.bean.UserDetailinfo;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.constant.AppConst;
import com.tsinghuabigdata.edu.ddmath.event.JumpEvent;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.view.WorkToolbar;
import com.tsinghuabigdata.edu.ddmath.module.mycenter.fragment.UserCenterFragment;
import com.tsinghuabigdata.edu.ddmath.module.product.adapter.ProductExchangeTimeAdapter;
import com.tsinghuabigdata.edu.ddmath.module.product.bean.ExchangeTimeBean;
import com.tsinghuabigdata.edu.ddmath.module.product.bean.PrivilegeVo;
import com.tsinghuabigdata.edu.ddmath.module.product.bean.ProductBean;
import com.tsinghuabigdata.edu.ddmath.module.product.bean.ProductPrivilegeBean;
import com.tsinghuabigdata.edu.ddmath.module.product.bean.UseTimesVo;
import com.tsinghuabigdata.edu.ddmath.module.product.dialog.NoEnoughLearnDouDialog;
import com.tsinghuabigdata.edu.ddmath.module.product.view.HolidayWorkView;
import com.tsinghuabigdata.edu.ddmath.module.product.view.ProductSetMealView;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.AlertManager;
import com.tsinghuabigdata.edu.ddmath.util.AppLog;
import com.tsinghuabigdata.edu.ddmath.util.DateUtils;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.LogUtils;
import com.tsinghuabigdata.edu.ddmath.util.ToastUtils;
import com.tsinghuabigdata.edu.ddmath.view.LoadingPager;

import org.greenrobot.eventbus.EventBus;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.tsinghuabigdata.edu.ddmath.util.AccountUtils.getStudyBean;

/**
 * 兑换次数界面
 */
public class ProductExchangeActivity extends RoboActivity implements View.OnClickListener{

    private static final String PARAM_PRODUCTBEAN   = "productbean";
    private static final String PARAM_PRIVILEGEID   = "privilegeId";
    private static final String PARAM_TITLE         = "title";

    //对象
    @ViewInject(R.id.worktoolbar)
    private WorkToolbar    mWorktoolbar;

    @ViewInject(R.id.loadingPager)
    private LoadingPager   mLoadingPager;

    @ViewInject(R.id.activity_productexchange_mainlayout)
    private LinearLayout exchangeMainLayout;

    @ViewInject( R.id.activity_productexchange_maincontent )
    private LinearLayout mainLayout;

    @ViewInject(R.id.activity_productexchange_grid)
    private GridView gridView;

    @ViewInject( R.id.activity_goodsdetail_holidayView )
    private HolidayWorkView holidayWorkView;

    @ViewInject( R.id.activity_goodsdetail_setmeal )
    private ProductSetMealView productSetMealView;

    //剩余学豆
    @ViewInject(R.id.activity_productexchange_leavelearndou)
    private TextView leaveLearnDouView;

    @ViewInject(R.id.activity_productexchange_suitetips)
    private TextView suiteTipsView;

    //使用期限
    @ViewInject(R.id.activity_productexchange_usedate)
    private TextView useDateView;

    //清零提示
    @ViewInject(R.id.activity_productexchange_cleantips)
    private TextView cleanTipsView;

    //确认兑换
    @ViewInject(R.id.activity_productexchange_gorecharge)
    private TextView enterRechargeBtn;

    //剩余次数
    @ViewInject(R.id.activity_productexchange_leavecount)
    private TextView leaveUseCountView;


    //传递进来的参数
    private String  mTitle;                   //商品名称
    private String  mPrivilegeId;             //商品 使用权ID
    private ProductPrivilegeBean mProductPrivilegeBean;
    private static ProductBean mSuiteBean;           //商品套餐

    private boolean closeAllActivity = true; //兑换成功后是否关闭其他Activity，然后跳转

    private ProductExchangeTimeAdapter mAdapter;
    

    private Context mContext;
    //private ExchangeDetail mExchangeDetail;

//    public static void gotoProductExchangeActivity(Activity activity, ProductPrivilegeBean bean ) {
//        Intent intent = new Intent(activity, ProductExchangeActivity.class);
//        intent.putExtra( PARAM_TITLE, bean.getProduct().getName() );
//        intent.putExtra( PARAM_PRODUCTBEAN, bean );
//        activity.startActivityForResult(intent,100);
//    }
    public static void gotoProductExchangeActivity(Context context, ProductPrivilegeBean bean ) {
        Intent intent = new Intent(context, ProductExchangeActivity.class);
        intent.putExtra( PARAM_TITLE, bean.getProduct().getName() );
        intent.putExtra( PARAM_PRODUCTBEAN, bean );
        context.startActivity(intent);
    }
    //商品套餐
    public static void gotoProductSuiteExchangeActivity(Context context, ProductBean bean ) {
        mSuiteBean = bean;

        Intent intent = new Intent(context, ProductExchangeActivity.class);
        intent.putExtra( PARAM_TITLE, mSuiteBean.getName() );
        intent.putExtra( PARAM_PRIVILEGEID, mSuiteBean.getProductSuiteId() );
        context.startActivity(intent);
    }
    public static void gotoProductExchangeActivity(Context context, String title, String privilegeId) {
        Intent intent = new Intent(context, ProductExchangeActivity.class);
        intent.putExtra( PARAM_TITLE, title );
        intent.putExtra( PARAM_PRIVILEGEID, privilegeId );
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        transparent = true;
        super.onCreate(savedInstanceState);

        //设置全屏显示
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView( GlobalData.isPad()?R.layout.activity_product_exchange:R.layout.activity_product_exchange_phone);

        x.view().inject( this );

        if( !parseIntent() ){
            ToastUtils.show( this, "参数错误" );
            finish();
            return;
        }
        initViews();

        loadData();
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        mSuiteBean = null;
    }

    @Override
    public void onClick(View v) {
        switch ( v.getId() ){
            case R.id.activity_productexchange_suitetips:{
                AllTreasureActivity.startAllTreasureActivity( mContext, AllTreasureActivity.SUITE );
                finishAll();
                break;
            }
            case R.id.activity_productexchange_gorecharge:{

                //套餐的兑换流程
                if( mSuiteBean != null ){
                    exchangeProductSuite();
                    break;
                }
                if( !TextUtils.isEmpty(mPrivilegeId) ){
                    //来自privilegeId 查询的商品 要进行过期期判断
                    //商品是否过期
                    if( ProductUtil.ckeckProductOverdue( mContext, mProductPrivilegeBean.getProduct() ) ){
                        break;
                    }
                }

                ProductBean productBean = mProductPrivilegeBean.getProduct();
                String privilegeId = "";
                if(  productBean == null ){
                   break;
                }
                List<PrivilegeVo> list = productBean.getPrivilegeVos();
                if (list != null && list.size() > 0 && list.get(0) != null) {
                    privilegeId = list.get(0).getId();
                }
                //直接购买类商品，单次： 寒假作业 类型
                if( ProductBean.CHARGE_SINGLE_PRODUCT == productBean.getChargeType() ) {
                    readySingleExchangeProduct();
                }

                //按套题次  去兑换：教辅作业上传  套题作业上传
                //按题次 里面的错题订正
                else if( ProductBean.CHARGE_QUESTIONSET_TIMES == productBean.getChargeType()
                        || AppConst.PRIVILEGE_QUESTION_DAYCLEAR.equals( privilegeId )
                        ){
                    enterExchange();
                }
                break;
            }
            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateLearnDouCount();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        quit();
    }

    //--------------------------------------------------------------------------------------
    private void quit(){

        //更新使用次数
        if( mProductPrivilegeBean!=null ){
            Intent intent = new Intent();
            intent.putExtra( "bean", mProductPrivilegeBean );
            setResult( RESULT_OK, intent );
        }

        finish();
    }

    private boolean parseIntent(){
        Intent intent = getIntent();
        if( intent.hasExtra( PARAM_PRODUCTBEAN ) ){
            mTitle = intent.getStringExtra( PARAM_TITLE );
            mProductPrivilegeBean = (ProductPrivilegeBean) intent.getSerializableExtra( PARAM_PRODUCTBEAN );
            closeAllActivity = true;
            return !TextUtils.isEmpty(mTitle) && mProductPrivilegeBean !=null;
        }else if( intent.hasExtra( PARAM_PRIVILEGEID ) ){
            mTitle = intent.getStringExtra( PARAM_TITLE );
            mPrivilegeId   = intent.getStringExtra( PARAM_PRIVILEGEID );
            closeAllActivity = false;
            return !TextUtils.isEmpty(mTitle) && !TextUtils.isEmpty(mPrivilegeId);
        }
        return false;
    }
    private void initViews() {

        mContext = this;

        mWorktoolbar.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quit();
            }
        }, null);
        mWorktoolbar.setShowRecharge( new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finishAll();

                //跳转用户中心学豆充值界面
                EventBus.getDefault().post(new JumpEvent(MainActivity.FRAGMENT_MY_CENTER, UserCenterFragment.MODEL_MYSTUDYBEAN));
            }
        } );
        mLoadingPager.setTargetView(mainLayout);
        mLoadingPager.setListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLoadingPager.showLoading();
                loadData();
            }
        });

        mAdapter = new ProductExchangeTimeAdapter( mContext );
        gridView.setAdapter( mAdapter );

        enterRechargeBtn.setOnClickListener( this );
        suiteTipsView.setOnClickListener( this );
    }
    
    //显示其他数据
    private void showData(){

        if( mProductPrivilegeBean == null || mProductPrivilegeBean.getProduct()==null ) return;

        ProductBean productBean = mProductPrivilegeBean.getProduct();
        if( productBean == null || productBean.getPrivilegeVos() == null || productBean.getPrivilegeVos().size() == 0 ) return;

        mLoadingPager.showTarget();

        //显示剩余次数
        showLeaveUseTimes( productBean );

        //使用期限
        String data;
        //1-按起始时间设置
        if( ProductBean.PRODUCT_USETYPE_ONLINE == productBean.getUseTimeType() ){
            data = String.format(Locale.getDefault(), "使用期限：%s 至 %s", DateUtils.format( productBean.getUseStartTime(), DateUtils.FORMAT_DATA_TIME), DateUtils.format( productBean.getUseEndTime(), DateUtils.FORMAT_DATA_TIME) );
        }
        //2-按兑换起时长设置
        else if( ProductBean.PRODUCT_USETYPE_EXCHANGE == productBean.getUseTimeType() ){
            String[] ta = {"日", "周", "月", "年"};
            int index = productBean.getUseTimeUnit();
            if( index <= 0 || index > ta.length )
                index = 1;
            data = String.format( Locale.getDefault(), "使用期限：从兑换时间起%d%s内有效", productBean.getUseTimeLimit(), ta[index-1] );
        }
        //
        else{
            data = "";
        }
        useDateView.setText( data );

        //剩余学豆
        showLeaveLearnDouView();

        //是假期作业,专门的购买UI
        if( ProductBean.VACATION_WORK.equals( productBean.getCatalogId() ) ){
            holidayWorkView.setVisibility( View.VISIBLE );
            holidayWorkView.setData( productBean.getName(), productBean.getChargeDdAmt(), mProductPrivilegeBean.getBookNameList(), mProductPrivilegeBean.getPaperSetNameList()  );

            cleanTipsView.setVisibility( View.GONE );

            mWorktoolbar.setTitle( "兑换<"+ mTitle+">" );
        }
        //次数
        else {

            mWorktoolbar.setTitle( "兑换<"+ mTitle+">使用次数" );

            exchangeMainLayout.setVisibility( View.VISIBLE );
            if( mProductPrivilegeBean.getPriseList() == null ){
                ToastUtils.show( mContext, "缺少商品兑换次数信息" );
                return;
            }
            //设置
            mAdapter.clear();
            mAdapter.addAll( mProductPrivilegeBean.getPriseList() );
            mAdapter.notifyDataSetChanged();

            cleanTipsView.setVisibility( View.VISIBLE );
        }

    }

    //显示剩下使用次数
    private void showLeaveUseTimes( ProductBean productBean ){
        String privilegeId = productBean.getPrivilegeVos().get(0).getId();
        String content;
        int count = ProductUtil.getLeaveUseTimes( productBean.getClassUseTimesList() );
        String leaveCount = count >=0 ? String.valueOf( count ) : "无限";
        //校内教辅
        if( AppConst.PRIVILEGE_ASSISTANTWORK.equals( privilegeId ) ){
            content = String.format( Locale.getDefault(), "剩余教辅作业上传次数：%s次", leaveCount );
        }
        //校内套题
        else if( AppConst.PRIVILEGE_SETWORK.equals( privilegeId ) ){
            content = String.format( Locale.getDefault(), "剩余套题作业上传次数：%s次", leaveCount );
        }
        //日日清 错题订正
        else if( AppConst.PRIVILEGE_QUESTION_DAYCLEAR.equals( privilegeId ) ){
            content = String.format( Locale.getDefault(), "剩余订正次数：%s次", leaveCount );
        }
        //豆豆检查作业
        else/* if( AppConst.PRIVILEGE_ORIGINALMATERIAL.equals( privilegeId ) )*/{
            content = String.format( Locale.getDefault(), "剩余使用次数：%s次", leaveCount );   //这里最好统一
        }
        leaveUseCountView.setText( content );
    }

    //设置剩余学豆次数显示
    private void showLeaveLearnDouView(){

        StudyBean studyBean = AccountUtils.getStudyBean();
        if( studyBean != null ){
            leaveLearnDouView.setText( String.format( Locale.getDefault(),"剩余学豆数：%d", studyBean.getTotalDdAmt() ) );
        }else{
            leaveLearnDouView.setText("");
        }
    }

    //加载兑换次数
    private void loadData() {
        //商品套餐显示
        if( mSuiteBean != null ){
            showSetMealData();
        }
        //已有商品信息，直接加载兑换次数信息
        else if( mProductPrivilegeBean != null ){         
            showData();
        }else if( !TextUtils.isEmpty(mPrivilegeId) ){     //只有商品ID，还要获取商品信息
            queryProductBean();
        }
    }

    //查询商品信息
    private void queryProductBean(){

        UserDetailinfo detailInfo = AccountUtils.getUserdetailInfo();
        MyTutorClassInfo classInfo = AccountUtils.getCurrentClassInfo();
        if( detailInfo == null || classInfo == null ) {
            ToastUtils.show( mContext, detailInfo==null?R.string.relogin:R.string.jionclass );
            finish();
            return;
        }

        mLoadingPager.showLoading();
        new ProductModel().getProductDetail(  classInfo.getSchoolId(), classInfo.getClassId(), detailInfo.getStudentId(), mPrivilegeId, new RequestListener< ProductPrivilegeBean >() {

            @Override
            public void onSuccess(ProductPrivilegeBean bean) {
                LogUtils.i("getProductGuide onSuccess");
                if (bean == null ) {
                    mLoadingPager.showEmpty( R.string.nofind_product );
                    return;
                }
                mProductPrivilegeBean = bean;
                showData();
            }

            @Override
            public void onFail(HttpResponse<ProductPrivilegeBean> response, Exception ex) {
                if( !TextUtils.isEmpty(response.getInform()) ){
                    mLoadingPager.showEmpty( response.getInform() );
                }else{
                    mLoadingPager.showFault(ex);
                }
            }
        });
    }

    //点击确认兑换  执行兑换前的检查工作
    private void enterExchange( ){

        final ExchangeTimeBean bean = mAdapter.getSelectProductBean();
        if( bean == null ){
            ToastUtils.show( mContext, "请选择要兑换的次数");
            return;
        }

        final ProductBean productBean = mProductPrivilegeBean.getProduct();
        final UserDetailinfo detailInfo = AccountUtils.getUserdetailInfo();
        final MyTutorClassInfo classInfo= AccountUtils.getCurrentClassInfo();
        final StudyBean studyBean = getStudyBean();
        if(  productBean == null || productBean.getPrivilegeVos() == null || productBean.getPrivilegeVos().size() == 0 || detailInfo == null || classInfo==null || studyBean ==null ){
            //
            return;
        }

        //先区分学豆是否足够
        int currLearnDou = studyBean.getTotalDdAmt();
        if( currLearnDou < bean.getDdAmt() ){        //提示去充值学豆

            String data = String.format( Locale.getDefault(), "现有%d学豆，仍需充值%d学豆。", currLearnDou,bean.getDdAmt()-currLearnDou );

            //充值优惠提示
            NoEnoughLearnDouDialog dialog = new NoEnoughLearnDouDialog( mContext, R.style.FullTransparentDialog);
            dialog.setData( data, R.drawable.selector_recharge_btn, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //去充值 还要返回，，刷新剩余学豆
                    //关闭其他界面
                    finishAll();

                    //跳转用户中心学豆充值界面
                    EventBus.getDefault().post(new JumpEvent(MainActivity.FRAGMENT_MY_CENTER, UserCenterFragment.MODEL_MYSTUDYBEAN));
                }
            } );
            dialog.show();

        }else{          //学豆足够，用户确认后开始兑换

            //确认是否兑换
            String data = String.format( Locale.getDefault(), "即将使用%d学豆兑换%d次%s上传次数。",bean.getDdAmt(),bean.getChangeNum(),productBean.getName() );
            if( productBean.getName().equals("错题订正") )
                data = String.format( Locale.getDefault(), "即将使用%d学豆兑换%d次错题订正服务。",bean.getDdAmt(),bean.getChangeNum() );

//            AlertManager.showCustomImageBtnDialog(mContext, data, R.drawable.selector_cancel_btn, R.drawable.selector_enter_btn,
//                    new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            //取消 无动作
//                        }
//                    }, new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            //
//                            startExchange( detailInfo, classInfo, bean );
//                        }
//                    });
        }
    }

    //执行兑换次数，调用接口
    private void startExchange(UserDetailinfo detailInfo, final MyTutorClassInfo classInfo, final ExchangeTimeBean bean ){

        final ProductBean productBean = mProductPrivilegeBean.getProduct();
        //开始兑换
        new ProductModel().exchangeProductUseTimes(detailInfo.getStudentId(), classInfo.getClassId(), productBean.getProductId(), ""/*productBean.getPrivilegeVos().get(0).getId()*/, bean.getChangeNum(), new RequestListener() {
            @Override
            public void onSuccess(Object res) {
                //更新使用次数
                ProductBean productBean = mProductPrivilegeBean.getProduct();

                List<UseTimesVo> list = productBean.getClassUseTimesList();
                List<UseTimesVo> tmplist = new ArrayList<>();
                for( UseTimesVo useTimesVo : list ){
                    if( useTimesVo.getClassId().equals( classInfo.getClassId() ) ){
                        UseTimesVo useTimesVo1 = new UseTimesVo();
                        useTimesVo1.setClassId( classInfo.getClassId() );
                        useTimesVo1.setUseTimes( bean.getChangeNum() );
                        tmplist.add( useTimesVo1 );
                    }
                }
                if( tmplist.size()>0 ) list.addAll( tmplist );
                showLeaveUseTimes( productBean );

                //更新学豆数量
                updateLearnDouCount();
                StudyBean studyBean = AccountUtils.getStudyBean();
                if( studyBean !=null ){
                    studyBean.setReturnDdAmt( 0-bean.getDdAmt() );
                }
                AccountUtils.setStudyBean( studyBean );

                showLeaveLearnDouView();

                final String privilegeId = productBean.getPrivilegeVos().get(0).getId();

                String data = "";
                //错题订正 日日清
                if(AppConst.PRIVILEGE_QUESTION_DAYCLEAR.equals( privilegeId ) ){
                    data = String.format( Locale.getDefault(), "恭喜您成功兑换%d次错题订正服务！", bean.getChangeNum() );
                }
                //校内套题
                else if(AppConst.PRIVILEGE_SETWORK.equals( privilegeId ) ){
                    data = "现在可以去拍照上传套题作业啦！";
                }
                //校内教辅
                else if(AppConst.PRIVILEGE_ASSISTANTWORK.equals( privilegeId ) ){
                    data = "现在可以去拍照上传教辅作业啦！";
                }
                //原版教辅
                else if(AppConst.PRIVILEGE_ORIGINALMATERIAL.equals( privilegeId ) ){
                    data = "现在可以去拍照上传豆豆诊断作业啦！";
                }
                //校园扫描
                else if(AppConst.PRIVILEGE_CAMPUS_SCANNING.equals( privilegeId ) ){
                    data = "现在可以进行老师PC端上传作业或考试的操作了！";
                }

                //成功，提示用户
//                AlertManager.showCustomImageBtnDialog( mContext, data, R.drawable.selector_back_btn, R.drawable.selector_gostudy,
//                        null, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//
//                                //直接返回即可
//                                if( !closeAllActivity ){
//                                    finish();
//                                    return;
//                                }
//
//                                //跳转 去错题订正
//                                if(AppConst.PRIVILEGE_QUESTION_DAYCLEAR.equals( privilegeId ) ){
//                                    EventBus.getDefault().post(new JumpEvent(MainActivity.FRAGMENT_QUESTION_BOOK, ErrorBookFragment.MODEL_DAYCLEAR));
//                                }
//                                //校内套题
//                                //校内教辅
//                                //校园扫描
//                                else if( AppConst.PRIVILEGE_SETWORK.equals( privilegeId ) || AppConst.PRIVILEGE_ASSISTANTWORK.equals( privilegeId ) || AppConst.PRIVILEGE_CAMPUS_SCANNING.equals( privilegeId ) ){
//                                    EventBus.getDefault().post(new JumpEvent(MainActivity.FRAGMENT_STUDY_TASK, MyStudyFragment.MODEL_SCHOOLWORK));
//                                }
//
//                                //豆豆检查作业
//                                else if( AppConst.PRIVILEGE_ORIGINALMATERIAL.equals( privilegeId ) ){
//                                    EventBus.getDefault().post(new JumpEvent(MainActivity.FRAGMENT_STUDY_TASK, MyStudyFragment.MODEL_CHECK_WORK));
//                                }
//
//                                //关闭界面 除了MainActivity
//                                finishAll();
//                            }
//                        });
            }

            @Override
            public void onFail(HttpResponse response, Exception ex) {
                AlertManager.showErrorInfo( mContext,ex );
            }
        });
    }

    //准备单次兑换商品
    private void readySingleExchangeProduct(){

        final ProductBean productBean = mProductPrivilegeBean.getProduct();
        final StudyBean studyBean = getStudyBean();
        if(  productBean == null || studyBean ==null ){
            //
            return;
        }

        //先区分学豆是否足够
        int currLearnDou = studyBean.getTotalDdAmt();
        if( currLearnDou < productBean.getChargeDdAmt() ){        //提示去充值学豆

            String data = String.format( Locale.getDefault(), "现有%d学豆，仍需充值%d学豆。", currLearnDou,productBean.getChargeDdAmt()-currLearnDou );

            //充值优惠提示
            NoEnoughLearnDouDialog dialog = new NoEnoughLearnDouDialog( mContext, R.style.FullTransparentDialog);
            dialog.setData( data, R.drawable.selector_recharge_btn, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //去充值 还要返回，，刷新剩余学豆
                    //关闭其他界面
                    finishAll();

                    //跳转用户中心学豆充值界面
                    EventBus.getDefault().post(new JumpEvent(MainActivity.FRAGMENT_MY_CENTER, UserCenterFragment.MODEL_MYSTUDYBEAN));
                }
            } );
            dialog.show();

        }else{          //学豆足够，用户确认后开始兑换

            //确定使用1500个学豆兑换2017-11-06至11-10的寒假作业吗？
            String data = String.format( Locale.getDefault(), "确定使用%d个学豆兑换%s吗？", productBean.getChargeDdAmt(), productBean.getName() );

//            AlertManager.showCustomImageBtnDialog( mContext, data, R.drawable.selector_cancel_btn, R.drawable.selector_enter_btn, null,new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    execExchangeSingleProduct();
//                }
//            });
        }
    }

    //开始兑换单独的商品
    private void execExchangeSingleProduct() {
        final ProductBean productBean = mProductPrivilegeBean.getProduct();
        UserDetailinfo detailInfo = AccountUtils.getUserdetailInfo();
        MyTutorClassInfo classInfo= AccountUtils.getCurrentClassInfo();
        if( detailInfo == null || classInfo == null || productBean==null ) {
            ToastUtils.show( mContext, detailInfo==null?R.string.relogin:R.string.jionclass );
            finish();
            return;
        }
        new ProductModel().exchangeSingleProduct( detailInfo.getStudentId(), classInfo.getClassId(), productBean.getProductId(), new RequestListener<Boolean>() {

            @Override
            public void onSuccess(Boolean success) {
                showExchangeResult( success, productBean );
            }

            @Override
            public void onFail(HttpResponse<Boolean> response, Exception ex) {
                String data = response.getInform();
                if( TextUtils.isEmpty(data) )
                    data = "抱歉，本次操作失败，需要重新兑换。";
//                AlertManager.showCustomImageBtnDialog( mContext, data, R.drawable.selector_back_btn, null );
            }
        });
    }

    //兑换结果显示
    private void showExchangeResult(final Boolean success, final ProductBean productBean ){

        String data;
        int btnResId;
        if( success ) {
            data = String.format(Locale.getDefault(), "现在可以去做%s啦！", productBean.getName() );
            btnResId = R.drawable.selector_gostudy;
        }else{
            data = "抱歉，本次操作失败，需要重新兑换。";
            btnResId = R.drawable.selector_back_btn;
        }
//        AlertManager.showCustomImageBtnDialog( mContext, data, btnResId, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//
//                //假期作业
//                if( success && ProductBean.VACATION_WORK.equals(productBean.getCatalogId()) ){
//                    //跳转到学习任务界面
//                    EventBus.getDefault().post(new JumpEvent(MainActivity.FRAGMENT_STUDY_TASK, MyStudyFragment.MODEL_SCHOOLWORK));
//                    //关闭界面
//                    finishAll();
//                }
//            }
//        });
    }

    //触发学豆更新
    private void updateLearnDouCount(){
        ProductUtil.updateLearnDou(new RequestListener<StudyBean>() {
            @Override
            public void onSuccess(StudyBean res) {
                showLeaveLearnDouView();
            }
            @Override
            public void onFail(HttpResponse<StudyBean> response, Exception ex) {
                AppLog.d("学豆更新更新失败");
            }
        });
    }

    //-------------------------------------------------------------------------------------
    //商品套餐

    //套餐兑换
    private void exchangeProductSuite(){

        //商品是否过期
        if( ProductUtil.ckeckProductOverdue( mContext, mSuiteBean ) ){
            return;
        }


        final StudyBean studyBean = getStudyBean();
        if( studyBean ==null ){
            return;
        }

        //先区分学豆是否足够
        int currLearnDou = studyBean.getTotalDdAmt();
        if( currLearnDou < mSuiteBean.getChargeDdAmt() ){        //提示去充值学豆

            String data = String.format( Locale.getDefault(), "现有%d学豆，仍需充值%d学豆。", currLearnDou, mSuiteBean.getChargeDdAmt()-currLearnDou );

            //充值优惠提示
            NoEnoughLearnDouDialog dialog = new NoEnoughLearnDouDialog( mContext, R.style.FullTransparentDialog);
            dialog.setData( data, R.drawable.selector_recharge_btn, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //去充值 还要返回，，刷新剩余学豆
                    //关闭其他界面
                    finishAll();

                    //跳转用户中心学豆充值界面
                    EventBus.getDefault().post(new JumpEvent(MainActivity.FRAGMENT_MY_CENTER, UserCenterFragment.MODEL_MYSTUDYBEAN));
                }
            } );
            dialog.show();

        }else{          //学豆足够，用户确认后开始兑换
            //确认是否兑换
            String data = String.format( Locale.getDefault(), "确认使用%d学豆兑换%s吗？", mSuiteBean.getChargeDdAmt(), mSuiteBean.getName() );

//            AlertManager.showCustomImageBtnDialog(mContext, data, R.drawable.selector_cancel_btn, R.drawable.selector_enter_btn,
//                    new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            //取消 无动作
//                        }
//                    }, new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            //
//                            startExchangeSuite();
//                        }
//                    });
        }
    }

    //执行兑换次数，调用接口
    private void startExchangeSuite(){

        final UserDetailinfo detailInfo = AccountUtils.getUserdetailInfo();
        final MyTutorClassInfo classInfo= AccountUtils.getCurrentClassInfo();

        if( detailInfo == null || classInfo == null ){
            ToastUtils.show( mContext, R.string.relogin );
            return;
        }

        //开始兑换
        new ProductModel().exchangeProductSuite( classInfo.getClassId(), detailInfo.getStudentId(), mSuiteBean.getProductSuiteId(), "", new RequestListener() {
            @Override
            public void onSuccess(Object res) {

                //兑换失败
                if( !(Boolean)res ){
//                    AlertManager.showCustomImageBtnDialog( mContext, "抱歉，本次操作失败，需要重新兑换。", R.drawable.selector_back_btn, null );
                    return;
                }

                //兑换成功

                //更新学豆数量
                updateLearnDouCount();
                StudyBean studyBean = AccountUtils.getStudyBean();
                if( studyBean !=null ){
                    studyBean.setReturnDdAmt( 0-mSuiteBean.getChargeDdAmt() );
                }
                AccountUtils.setStudyBean( studyBean );

                showLeaveLearnDouView();

                //成功，提示用户
//                AlertManager.showCustomImageBtnDialog( mContext, data, R.drawable.selector_enter_btn, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//
//                                //触发我的世界重新加载
//                                EventBus.getDefault().post(new JumpEvent(MainActivity.FRAGMENT_FIRST));
//                                //关闭界面 除了MainActivity
//                                finishAll();
//                            }
//                        });
            }

            @Override
            public void onFail(HttpResponse response, Exception ex) {
                String data = response.getInform();
                if( TextUtils.isEmpty(data) )
                    data = "抱歉，本次操作失败，需要重新兑换。";
//                AlertManager.showCustomImageBtnDialog( mContext, data, R.drawable.selector_back_btn, null );
            }
        });
    }

    //显示数据
    private void showSetMealData(){

        if( mSuiteBean == null || mSuiteBean.getProductVoList()==null ) return;

        //ProductBean productBean = mProductPrivilegeBean.getProduct();
        //if( productBean == null || productBean.getPrivilegeVos() == null || productBean.getPrivilegeVos().size() == 0 ) return;

        mLoadingPager.showTarget();

        mWorktoolbar.setTitle( "兑换<"+ mTitle+">" );

        //使用期限
        useDateView.setText( ProductUtil.getProductUseTime( mSuiteBean ) );

        //剩余学豆
        showLeaveLearnDouView();

        //专门的购买UI
        productSetMealView.setVisibility( View.VISIBLE );
        productSetMealView.setData( mSuiteBean.getName(), mSuiteBean.getChargeDdAmt(), mSuiteBean.getProductVoList() );

        cleanTipsView.setVisibility( View.GONE );
    }

}
