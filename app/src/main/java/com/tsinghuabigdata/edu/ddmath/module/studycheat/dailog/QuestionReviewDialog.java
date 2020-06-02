package com.tsinghuabigdata.edu.ddmath.module.studycheat.dailog;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.event.ChangeAbilityEvent;
import com.tsinghuabigdata.edu.ddmath.module.studycheat.bean.AbilityQueryBean;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;

import org.greenrobot.eventbus.EventBus;


/**
 * 错题回顾 Dialog
 */
public class QuestionReviewDialog extends Dialog implements View.OnClickListener{

    //
    private LinearLayout mainLayout;
    private ImageView ddImageView;

    //提示模式
    private RelativeLayout tipsLayout;
    private TextView       tipsTextView;

    //答错模式
    private LinearLayout errorLayout;
    private TextView     errorTextView;
    private LinearLayout progressLayout;

    //能力模式
    private LinearLayout abilityLayout;

    private TextView    learnTextView;
    private TextView    powerTextView;
    private TextView    speedTextView;
    private TextView    attackTextView;
    private TextView    forceTextView;

    private TextView    leaveTextView;

    //按钮
    private Button      leftBtn;
    private Button      rightBtn;
    private View        spaceView;

    private OnClickListener leftBtnListener;
    private OnClickListener rightBtnListener;

    public QuestionReviewDialog(Context context){
        super(context);
        initData();
    }
    public QuestionReviewDialog(Context context, int theme  ){
        super(context,theme);
        initData();
    }

    @Override
    public void onClick(View v) {
        switch ( v.getId() ){
            case R.id.dialog_qreview_leftbtn:{
                if( leftBtnListener!=null ) leftBtnListener.onClick( this, 0 );
                dismiss();
                break;
            }
            case R.id.dialog_qreview_rightbtn:{
                if( rightBtnListener!=null ) rightBtnListener.onClick( this, 0 );
                dismiss();
                break;
            }
            default:
                break;
        }
    }

    /**
     * 普通信息
     * @param message
     */
    public void setData( String message, String poBtnName, String neBtnName, OnClickListener positiveListener, OnClickListener negativeListener ){

        tipsLayout.setVisibility(View.VISIBLE);

        tipsTextView.setText( message );

        leftBtnListener = negativeListener;
        rightBtnListener= positiveListener;

        leftBtn.setText( neBtnName );
        rightBtn.setText( poBtnName );
    }

    /**
     * 错题回顾 做错
     * @param message               显示信息
     * @param positiveListener      点击回调
     * @param leaveCount            剩余个数
     * @param totalCount            总量
     */
    public void setErrorData( String message, int leaveCount, int totalCount, String btnName, OnClickListener positiveListener ){

        if( leaveCount < 0 ) leaveCount = 0;

        errorLayout.setVisibility(View.VISIBLE);

        errorTextView.setText( message );

        leftBtnListener = null;
        rightBtnListener= positiveListener;
        spaceView.setVisibility(View.GONE);

        //rightBtn.setBackground( getContext().getResources().getDrawable(R.drawable.btn_image_lightblue) );
        rightBtn.setText( btnName );
        leftBtn.setVisibility(View.GONE);

        //错误机会次数，先显示可用
        for( int i=0; i<leaveCount; i++ ){
            ImageView imageView = new ImageView( getContext() );
            imageView.setImageDrawable( getContext().getResources().getDrawable( R.drawable.ic_chicken_red) );
            progressLayout.addView( imageView );
        }

        //已用机会
        for( int i=0; i<totalCount-leaveCount; i++ ){
            ImageView imageView = new ImageView( getContext() );
            imageView.setImageDrawable( getContext().getResources().getDrawable( R.drawable.ic_chicken_grey) );
            progressLayout.addView( imageView );
        }
    }

    /**
     * 错题回顾进度
     * @param item 能力值
     * @param positiveListener 点击回调
     * @param negativeListener 点击回调
     */
    public void setFinishReviewData(AbilityQueryBean item, String poBtnName, String neBtnName, OnClickListener positiveListener, OnClickListener negativeListener ){

        leftBtnListener = negativeListener;
        rightBtnListener= positiveListener;

        abilityLayout.setVisibility( View.VISIBLE );

        learnTextView.setText( "学力 +" );learnTextView.append( String.valueOf((int)item.getIncrease()) );
//        powerTextView.setText( "+" + String.valueOf((int)item.getStr().getIncrease()) );
//        speedTextView.setText( "+" + String.valueOf((int)item.getAgi().getIncrease()) );
//        attackTextView.setText("+" + String.valueOf((int)item.getCon().getIncrease()) );
//        forceTextView.setText( "+" + String.valueOf((int)item.getCrt().getIncrease()) );

        boolean hasQuestion = item.getSurplus()!=0;
        if( hasQuestion ){
            leaveTextView.setVisibility( View.VISIBLE );
            leaveTextView.setText( "今日还剩" + item.getSurplus() + "题" );

            leftBtn.setText( neBtnName );
            rightBtn.setText( poBtnName );

        }else{
            leaveTextView.setVisibility( View.INVISIBLE );

            leftBtn.setVisibility( View.GONE );
            rightBtn.setText( poBtnName );
        }

        EventBus.getDefault().post(new ChangeAbilityEvent());
    }

    //--------------------------------------------------------------------------
    private void initData(){
        setContentView(GlobalData.isPad()?R.layout.dialog_questionreview:R.layout.dialog_questionreview_phone );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            if( window!=null ){
                window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            }
        }
        Window window = getWindow();
        if( window!=null ){
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        mainLayout = (LinearLayout)findViewById( R.id.dialog_qreview_mainlayout );

        ddImageView= (ImageView)findViewById( R.id.dialog_qreview_tipimage );
        tipsLayout = (RelativeLayout)findViewById( R.id.dialog_qreview_tiplayout );
        tipsTextView = (TextView)findViewById( R.id.dialog_qreview_tiptext );

        errorLayout = (LinearLayout)findViewById( R.id.dialog_qreview_errlayout );
        errorTextView = (TextView) findViewById( R.id.dialog_qreview_errtext );
        progressLayout = (LinearLayout)findViewById( R.id.dialog_qreview_errprogresslayout );

        abilityLayout = (LinearLayout)findViewById( R.id.dialog_qreview_abilitylayout );

        learnTextView = (TextView)findViewById( R.id.dialog_qreview_leranability );
        powerTextView = (TextView)findViewById( R.id.dialog_qreview_powertext );
        speedTextView = (TextView)findViewById( R.id.dialog_qreview_speedtext );
        attackTextView = (TextView)findViewById( R.id.dialog_qreview_attacktext );
        forceTextView = (TextView)findViewById( R.id.dialog_qreview_forcetext );

        leaveTextView = (TextView)findViewById( R.id.dialog_qreview_leavetext );

        //按钮
        leftBtn = (Button) findViewById( R.id.dialog_qreview_leftbtn );
        rightBtn = (Button)findViewById( R.id.dialog_qreview_rightbtn );
        spaceView= findViewById( R.id.dialog_qreview_spaceview );

        leftBtn.setOnClickListener( this );
        rightBtn.setOnClickListener( this );
    }


}
