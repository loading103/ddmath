package com.tsinghuabigdata.edu.ddmath.module.myscore.dialog;

import android.app.Dialog;
import android.content.Context;
import androidx.annotation.NonNull;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tsinghuabigdata.edu.ddmath.MVPModel.RequestListener;
import com.tsinghuabigdata.edu.ddmath.MVPModel.UserCenterModel;
import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.bean.UserDetailinfo;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.event.UpdateScoreEvent;
import com.tsinghuabigdata.edu.ddmath.event.UpdateStudybeanEvent;
import com.tsinghuabigdata.edu.ddmath.event.UpdateUserPendantEvent;
import com.tsinghuabigdata.edu.ddmath.module.myscore.bean.ExchangeProductBean;
import com.tsinghuabigdata.edu.ddmath.module.myscore.bean.ScoreProductBean;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.AlertManager;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.PicassoUtil;
import com.tsinghuabigdata.edu.ddmath.util.ToastUtils;
import com.tsinghuabigdata.edu.ddmath.view.LoadingPager;

import org.greenrobot.eventbus.EventBus;

import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 商品详情
 */

public class ProductDetailDialog extends Dialog implements View.OnClickListener {

    private Context    context;
    private String productId;
    private ScoreProductBean productBean;

    private LoadingPager loadingPager;

    //private ImageView ivClose;
    private TextView tvExchangeType;
    private ImageView ivDefaultView;
    private ImageView ivProduct;
    private TextView nameView;
    private TextView typeView;
    private TextView leaveTimeView;
    private TextView priceView;
    private TextView introView;
    private Button exchangeBtn;

    private ExchangeSuccessListener mListener;

    private AtomicBoolean atomicBoolean = new AtomicBoolean( false );

    public ProductDetailDialog(@NonNull Context context, String id) {
        super(context, R.style.dialog);
        this.context = context;
        this.productId = id;
        initView();
        loadData();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_close_btn:
                dismiss();
                break;
            case R.id.btn_atonce_exchange:
                if( atomicBoolean.get() )
                    return;
                atomicBoolean.set( true );
                if( "立即使用".equals( exchangeBtn.getText() ) ){
                    usePendent();
                }else{
                    execExchange();
                }
                break;
            default:
                break;
        }
    }

    public void setListener(ExchangeSuccessListener mListener) {
        this.mListener = mListener;
    }
    //--------------------------------------------------------------------------------------------
    private void initView() {
        if (GlobalData.isPad()) {
            setContentView(R.layout.dialog_product_detail );
        } else {
            setContentView(R.layout.dialog_product_detail_phone);
        }

        loadingPager = findViewById(R.id.loadingPager );
        LinearLayout mainLayout = findViewById( R.id.main_layout );
        loadingPager.setTargetView( mainLayout );
        loadingPager.setListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadData();
            }
        });

        ImageView ivClose = findViewById(R.id.iv_close_btn);

        tvExchangeType = findViewById( R.id.tv_exchange_type );
        ivDefaultView = findViewById(R.id.iv_default_headimg);
        ivProduct = findViewById(R.id.iv_product_image);
        nameView =  findViewById(R.id.tv_product_name);
        typeView =  findViewById(R.id.tv_product_extype);
        leaveTimeView =  findViewById(R.id.tv_product_leavetime);
        priceView =  findViewById(R.id.tv_product_price);
        introView = findViewById(R.id.tv_product_intro);
        exchangeBtn = findViewById(R.id.btn_atonce_exchange);

        ivClose.setOnClickListener(this);
        exchangeBtn.setOnClickListener(this);

        //
        setCanceledOnTouchOutside(false);
        Window dialogWindow = getWindow();
        if( dialogWindow == null ) return;
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = context.getResources().getDisplayMetrics().widthPixels; // 宽度
        lp.height= context.getResources().getDisplayMetrics().heightPixels * 9 / 10; // 宽度
        lp.dimAmount = 0.5f;
        dialogWindow.setAttributes(lp);
        dialogWindow.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialogWindow.setGravity(Gravity.BOTTOM);//设置弹框在屏幕的下方
        dialogWindow.setWindowAnimations(R.style.recharge_dialog_anim);

    }

    private void loadData() {

        UserDetailinfo userDetailinfo = AccountUtils.getUserdetailInfo();
        if( userDetailinfo==null || TextUtils.isEmpty(productId)) return;

        loadingPager.showLoading();

        new UserCenterModel().queryProductDetail( userDetailinfo.getStudentId(), productId, new RequestListener<ScoreProductBean>() {
            @Override
            public void onSuccess(ScoreProductBean res) {

                if( res == null  ){
                    loadingPager.showEmpty();
                    ToastUtils.show( context, "加载商品信息出错");
                    dismiss();
                    return;
                }
                loadingPager.showTarget();
                productBean = res;
                showProductInfo();
            }

            @Override
            public void onFail(HttpResponse<ScoreProductBean> response, Exception ex) {
                loadingPager.showFault( ex );
            }
        });
    }

    private void showProductInfo(){
        if( productBean == null ) return;

        tvExchangeType.setText("兑换礼品");

        nameView.setText( productBean.getName() );
        String linitType = "日";
        if( productBean.getLimitUnit() == ScoreProductBean.TYPE_LIMIT_WEEK ){
            linitType = "周";
        }else if( productBean.getLimitUnit() == ScoreProductBean.TYPE_LIMIT_MONTH ){
            linitType = "月";
        }
        if( productBean.getLimitCount() > 0 ){
            typeView.setText( String.format(Locale.getDefault(),"（每%s限兑%d次）", linitType, productBean.getLimitCount()) );
        }else{
            typeView.setVisibility( View.GONE );
        }
        leaveTimeView.setText( String.format( Locale.getDefault(), "%d次", productBean.getRemainCount()));
        priceView.setText( String.format(Locale.getDefault(),"%d积分", productBean.getPointAmt()) );
        introView.setText( productBean.getRemark() );

        ivDefaultView.setVisibility( productBean.getCatalogId()==ScoreProductBean.PRODUCT_TYPE_PENDANT? View.VISIBLE: View.GONE );

        String url = AccountUtils.getFileServer() + productBean.getImagePath();
        PicassoUtil.getPicasso(context).load(url).error(R.drawable.ic_broken_image).placeholder(R.drawable.ic_temporary_image).into(ivProduct);

        int userScore = 0;
        if(AccountUtils.userScoreBean!=null){
            userScore = AccountUtils.userScoreBean.getTotalCredit();
        }
        //挂件，已有
        if( productBean.isUserExchange() && productBean.getCatalogId() == ScoreProductBean.PRODUCT_TYPE_PENDANT ){
            exchangeBtn.setEnabled( false );
            exchangeBtn.setText( "已拥有，可去个人资料页面查看" );
        }
        //礼品总数已兑换完
        else if( productBean.getRemainCount() <= 0 ){
            exchangeBtn.setEnabled( false );
            exchangeBtn.setText( "已兑换完" );
        }
        //个人限制
        else if( !productBean.isHaveExchangeChance() ){
            exchangeBtn.setEnabled( false );
            exchangeBtn.setText( String.format(Locale.getDefault(),"今日%d次机会已用完，明天再来吧～", productBean.getLimitCount() ) );
        }
        //积分不足
        else if( productBean.getPointAmt() > userScore ){
            exchangeBtn.setEnabled( false );
            exchangeBtn.setText( String.format(Locale.getDefault(),"积分不足，还差%d积分", (productBean.getPointAmt()-userScore)) );
        }

        //默认 马上兑换
    }

    private void usePendent(){
        final UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
        if( detailinfo==null || TextUtils.isEmpty(productId)){
            atomicBoolean.set( false );
            return;
        }
        //使用指定挂件
        new UserCenterModel().useHeaderPendant( detailinfo.getStudentId(), recordId, new RequestListener<Integer>() {
            @Override
            public void onSuccess(Integer status) {
                atomicBoolean.set( false );
                if( status > 0 ){       //成功

                    //保存新用户信息
                    detailinfo.useHeadPendent( productId );

                    //通知更新用户挂件
                    EventBus.getDefault().post( new UpdateUserPendantEvent() );

                    ToastUtils.show( context, "使用成功");
                    dismiss();
                }else{
                    ToastUtils.show( context, "使用失败，请重试!");
                }
            }

            @Override
            public void onFail(HttpResponse<Integer> response, Exception ex) {
                atomicBoolean.set( false );
                AlertManager.showErrorInfo( context, ex );
            }
        });
    }

    private String recordId;
    private void execExchange(){

        final UserDetailinfo userDetailinfo = AccountUtils.getUserdetailInfo();
        if( userDetailinfo==null || TextUtils.isEmpty(productId)){
            atomicBoolean.set( false );
            return;
        }

        new UserCenterModel().execExchangeProductAction( userDetailinfo.getStudentId(), productId, new RequestListener<ExchangeProductBean>() {
            @Override
            public void onSuccess(ExchangeProductBean bean) {

                atomicBoolean.set( false );
                if( bean == null || bean.getSuccess() != 0 ){
                    String data = "抱歉，兑换失败，请重试";
                    if( bean != null ){
                        if( 2 == bean.getSuccess() ){
                            data = "积分不足，兑换失败！";

                            //刷新积分
                            EventBus.getDefault().post( new UpdateScoreEvent(false) );

                            ToastUtils.show( context, data );
                            dismiss();
                            return;
                        }
                    }
                    ToastUtils.show( context, data );
                }else {
                    recordId = bean.getRecordId();
                    if(mListener!=null) mListener.onSuccess( productId );
                    //本地积分变化
                    if(AccountUtils.userScoreBean!=null){
                        AccountUtils.userScoreBean.useScore( productBean.getPointAmt() );
                    }
                    EventBus.getDefault().post( new UpdateScoreEvent(false) );                //触发积分刷新

                    //学豆
                    if( bean.getCatelogId() == ScoreProductBean.PRODUCT_TYPE_XUEDOU ){
                        //ToastUtils.showToastCenter( context, "恭喜你，兑换成功！，可在“我要购买”中查看可用学豆");
                        ToastUtils.showScoreExchangeXueDD(context);
                        EventBus.getDefault().post( new UpdateStudybeanEvent() );       //触发学豆刷新

                        dismiss();
                    }else if(bean.getCatelogId() == ScoreProductBean.PRODUCT_TYPE_PENDANT ){

                        //把挂件添加到用户信息里面
                        productBean.setRecordId( recordId );
                        userDetailinfo.addUserPendent( productBean );

                        //挂件
                        exchangeBtn.setText( "立即使用" );
                        ToastUtils.show( context, "恭喜你，兑换成功！" );
                    }else{
                        ToastUtils.show( context, "恭喜你，兑换成功！" );
                    }
                }
            }

            @Override
            public void onFail(HttpResponse<ExchangeProductBean> response, Exception ex) {
                //loadingPager.showFault( ex );
                AlertManager.showErrorInfo( context, ex );
                atomicBoolean.set( false );
            }
        });

    }

    public interface ExchangeSuccessListener{
        void onSuccess(String pruductId);
    }

}
