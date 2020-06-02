package com.tsinghuabigdata.edu.ddmath.module.ddwork.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.module.product.bean.RechargeCashbackBean;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;

import java.util.Locale;

/**
 * 校内作业toolbar
 */
public class WorkToolbar extends LinearLayout implements View.OnClickListener{

    private LinearLayout leftView;

    private ImageView backImage;
    private TextView backtext;

    private TextView titleView;

    private LinearLayout rightLayout;
    private TextView rightTextView;
    private ImageView rightImageView;

    //去充值
    private LinearLayout rechargeLayout;
    private TextView rechargeText;

    //网速监测
    private NetworkMonitorView networkMonitorView;

    private View.OnClickListener leftListener;
    private View.OnClickListener rightListener;

    public WorkToolbar(Context context) {
        super(context);
        init();
    }

    public WorkToolbar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WorkToolbar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.toolbar_lefttitle:
                if( leftListener!=null ) leftListener.onClick( v );
                break;
            case R.id.toolbar_rightexcharge_layuout:
            case R.id.toolbar_righttitle_layuout:
                if( rightListener!=null ) rightListener.onClick( v );
                break;
            default:
                break;
        }
    }

    /**
     * 回调
     * @param llistener  左按钮回调
     * @param rListener  右按钮回调
     */
    public void setClickListener(View.OnClickListener llistener, View.OnClickListener rListener){
        leftListener = llistener;
        rightListener=rListener;
    }

    public void setTitle( String title ){
        titleView.setText( title );
    }
    public String getTitle(){
        return titleView.getText().toString();
    }

    public void setBackText( String text, boolean show ){
        leftView.setVisibility( View.VISIBLE );
        backtext.setText( text );
        backImage.setVisibility( show?VISIBLE:GONE );
    }

    //设置右边按钮 和 图标
    public void setRightTitleAndLeftDrawable( String title, int rid ){
        rightLayout.setVisibility( View.VISIBLE );
        rightTextView.setText( title );
        if (title != null) {
            rightImageView.setVisibility( View.VISIBLE );
            rightImageView.setImageResource( rid );
        } else {
            rightLayout.setVisibility(View.GONE);
        }
    }

    //设置右边标题 没有图标
    public void setRightTitle( String title ){
        rightLayout.setVisibility( TextUtils.isEmpty(title)? View.GONE : View.VISIBLE );
        rightTextView.setText( title );
        rightImageView.setVisibility( View.GONE );
    }

    public String getRightTitle(){
        return rightTextView.getText().toString();
    }

    public void setShowRecharge( View.OnClickListener rListener ){
        rechargeLayout.setVisibility( VISIBLE );
        RechargeCashbackBean bean = AccountUtils.getRechargeCashback();
        if( bean != null ){
            rechargeText.setText( String.format( Locale.getDefault(), "充%d元送%d学豆", (int)bean.getRechargeMoney(), bean.getReturnDdAmt() ));
            rightListener = rListener;
        }

    }

    public NetworkMonitorView getNetworkMonitorView(){
        return networkMonitorView;
    }

    //-----------------------------------------------------------------------------
    private void init() {

        inflate( getContext(), GlobalData.isPad()?R.layout.ddwork_toolbar_layout:R.layout.ddwork_toolbar_layout_phone, this );
        backImage = findViewById( R.id.backimg );
        backtext  = findViewById( R.id.backtext );
        leftView =  findViewById( R.id.toolbar_lefttitle );

        titleView = findViewById( R.id.toolbar_title );

        rightLayout = findViewById( R.id.toolbar_righttitle_layuout );
        rightTextView = findViewById( R.id.toolbar_righttitle );
        rightImageView =  findViewById( R.id.toolbar_right_image );

        rechargeLayout = findViewById( R.id.toolbar_rightexcharge_layuout );
        rechargeText   = findViewById( R.id.toolbar_rightexcharge_value );

        networkMonitorView = findViewById( R.id.toolbar_networkmonitor );

        leftView.setOnClickListener( this );
        rightLayout.setOnClickListener( this );
        rechargeLayout.setOnClickListener( this );
    }

}
