package com.tsinghuabigdata.edu.ddmath.module.studycheat.dailog;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.event.ChangeAbilityEvent;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.DDUploadActivity;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.WorkSubmitBean;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;

import org.greenrobot.eventbus.EventBus;

import java.util.Locale;
import java.util.Random;


/**
 * 作业提交显示Dialog
 */
public class WorkAbilityDialog extends Dialog implements View.OnClickListener{

    //
    //private RelativeLayout mainLayout;
    //private ImageView closeImageView;
    //private ImageView ddImageView;

    private RelativeLayout alibityLayout;
    private TextView    learnTextView;      //学力值

    private ImageView imageView;

    //能力模式
//    private LinearLayout abilityLayout;
//
//    private TextView    powerTextView;
//    private TextView    speedTextView;
//    private TextView    attackTextView;
//    private TextView    forceTextView;

    //鼓励话语
    private TextView    heartenTextView;

    private TextView    titleTextView;

    private Context mContext;

    private boolean fromRevise = false;
    //定制学背景
    private RelativeLayout    mRebglayout;
    private ImageView    mIvbg;
    private ImageView mIvclose;
    private ImageView    mIvhead;
    private TextView mTvcontent;

    public static final int[] a={ R.drawable.pic01, R.drawable.pic02, R.drawable.pic03, R.drawable.pic04, R.drawable.pic05, R.drawable.pic06};

    //    public WorkAbilityDialog(Context context){
//        super(context);
//        initData();
//    }
    private WorkAbilityDialog(Context context, int theme  ){
        super(context,theme);
        mContext = context;
        initData();
    }

    @Override
    public void onClick(View v) {
        switch ( v.getId() ){
            case R.id.dialog_close:{
                dismiss();
                break;
            }
            case R.id.iv_score_bg:{
                dismiss();
                break;
            }
            case R.id.iv_close_bg:{
                dismiss();
                break;
            }
            case R.id.dialog_sumit_enterbtn:{
                if( mContext instanceof DDUploadActivity && !fromRevise){
                    DDUploadActivity activity = (DDUploadActivity)mContext;
                    activity.quitfinish();
                }
                dismiss();
                break;
            }
            default:
                break;
        }
    }

    public static void showDialog( Context context, WorkSubmitBean workBean, boolean classWork ){
        WorkAbilityDialog dialog = new WorkAbilityDialog(context, R.style.FullTransparentDialog );
        dialog.setData( workBean,classWork );
        dialog.show();
    }

    //
    public static void showDialog( Context context, int ability ){
        WorkAbilityDialog dialog = new WorkAbilityDialog(context, R.style.FullTransparentDialog );
        dialog.setReviseData( ability );
        dialog.show();
    }
    /**
     * 定制学跳转
     */
    public static void showScoreDialog( Context context,int overdo,String title,WorkSubmitBean workBean, boolean classWork){
        WorkAbilityDialog dialog = new WorkAbilityDialog(context, R.style.FullTransparentDialog );
        dialog.setScoreData(overdo,title, workBean,  classWork);
        dialog.show();
    }

    public static void showScoreDialog( Context context,int overdo,String title,int lastone, int ability ){
        WorkAbilityDialog dialog = new WorkAbilityDialog(context, R.style.FullTransparentDialog );
        dialog.setScoreResvertData(overdo,title,lastone,ability);
        dialog.show();
    }
    public void setData(WorkSubmitBean workSubmitBean, boolean classWork ){
        int power = workSubmitBean.getValue();
        if( power > 0 ) {
            alibityLayout.setVisibility( View.VISIBLE );
            learnTextView.setText( String.format(Locale.getDefault(),"学力 +%d", workSubmitBean.getValue()) );      //学力值
        }
        //超期
        if( workSubmitBean.isOverdue() ){
            // 如果超期这样消失
            heartenTextView.setText( getContext().getResources().getString( R.string.cheat_submit_tips1 ));
            imageView.setImageResource( R.drawable.upload_succes_normal );
            alibityLayout.setVisibility(View.GONE);
            mRebglayout.setVisibility(View.GONE);

        }else if( power > 0 ){  //增加了学力
            String data = String.format( getContext().getResources().getString( R.string.cheat_submit_tips2 ), workSubmitBean.getRank() );
            if( !classWork )
                data = getContext().getResources().getString( R.string.cheat_submit_tips1 );
            heartenTextView.setText( data );
            imageView.setImageResource( R.drawable.upload_succes_rank );
            alibityLayout.setVisibility(View.VISIBLE);
            mRebglayout.setVisibility(View.GONE);
        }else{  //只显示排名
            String data = String.format( getContext().getResources().getString( R.string.cheat_submit_tips2 ), workSubmitBean.getRank() );
            heartenTextView.setText( data );
            imageView.setImageResource( R.drawable.upload_succes_rank );
            alibityLayout.setVisibility(View.GONE);
            mRebglayout.setVisibility(View.GONE);
        }

        EventBus.getDefault().post(new ChangeAbilityEvent());
    }

    /**
     * 定制学进入提交界面
     */

    public void setScoreData(int overdue,String content,WorkSubmitBean workSubmitBean, boolean classWork){
        //超期
        if( overdue==1 ){
            // 如果超期这样显示
            alibityLayout.setVisibility(View.GONE);
            mRebglayout.setVisibility(View.GONE);
            int power = workSubmitBean.getValue();
            if( power > 0 ) {
                alibityLayout.setVisibility( View.VISIBLE );
                learnTextView.setText( String.format(Locale.getDefault(),"学力 +%d", workSubmitBean.getValue()) );      //学力值
            }
            //超期
            if( workSubmitBean.isOverdue() ){
                // 如果超期这样消失
                heartenTextView.setText( getContext().getResources().getString( R.string.cheat_submit_tips1 ));
                imageView.setImageResource( R.drawable.upload_succes_normal );
                alibityLayout.setVisibility(View.GONE);
                mRebglayout.setVisibility(View.GONE);

            }else if( power > 0 ){  //增加了学力
                String data = String.format( getContext().getResources().getString( R.string.cheat_submit_tips2 ), workSubmitBean.getRank() );
                if( !classWork )
                    data = getContext().getResources().getString( R.string.cheat_submit_tips1 );
                heartenTextView.setText( data );
                imageView.setImageResource( R.drawable.upload_succes_rank );
                alibityLayout.setVisibility(View.VISIBLE);
                mRebglayout.setVisibility(View.GONE);
            }else{  //只显示排名
                String data = String.format( getContext().getResources().getString( R.string.cheat_submit_tips2 ), workSubmitBean.getRank() );
                heartenTextView.setText( data );
                imageView.setImageResource( R.drawable.upload_succes_rank );
                alibityLayout.setVisibility(View.GONE);
                mRebglayout.setVisibility(View.GONE);
            }
            EventBus.getDefault().post(new ChangeAbilityEvent());
        }else {
            alibityLayout.setVisibility(View.GONE);
            mRebglayout.setVisibility(View.VISIBLE);
            if(!TextUtils.isEmpty(content)){
                mTvcontent.setText("已完成"+content+",真棒！");
            }else {
                mTvcontent.setText("作业成功提交，真棒！");
            }
            mIvbg.setImageResource(a[new Random().nextInt(6)]);
        }
    }

    public void setScoreResvertData(int overdue,String content,int lastone, int ability ){
        if( overdue==0  && lastone==1){
            fromRevise = true;
            alibityLayout.setVisibility(View.GONE);
            mRebglayout.setVisibility(View.VISIBLE);
            if(!TextUtils.isEmpty(content)){
                mTvcontent.setText("已完成"+content+",真棒！");
            }else {
                mTvcontent.setText("作业成功提交，真棒！");
            }
            mIvbg.setImageResource(a[new Random().nextInt(6)]);
        }else {  //超期
            fromRevise = true;
            if( ability > 0 ){
                alibityLayout.setVisibility( View.VISIBLE );
                learnTextView.setText( String.format(Locale.getDefault(),"学力 +%d", ability) );      //学力值
            }
            imageView.setImageResource( R.drawable.upload_succes_rank );
            //鼓励话语
            heartenTextView.setText( getContext().getResources().getString( R.string.cheat_submit_tips1 ));
            titleTextView.setText( "提交成功!" );
            EventBus.getDefault().post(new ChangeAbilityEvent());
        }
    }

    private void setReviseData( int ability ){
        fromRevise = true;
        if( ability > 0 ){
            alibityLayout.setVisibility( View.VISIBLE );
            learnTextView.setText( String.format(Locale.getDefault(),"学力 +%d", ability) );      //学力值
        }
        imageView.setImageResource( R.drawable.upload_succes_rank );
        //鼓励话语
        heartenTextView.setText( getContext().getResources().getString( R.string.cheat_submit_tips1 ));
        titleTextView.setText( "提交成功!" );
        EventBus.getDefault().post(new ChangeAbilityEvent());
    }




    //--------------------------------------------------------------------------
    private void initData(){
        setContentView(GlobalData.isPad()?R.layout.dialog_ddwork_submitability:R.layout.dialog_ddwork_submitability_phone );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            if( window!=null ){
                window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            }
        }
        //mainLayout = (RelativeLayout)findViewById( R.id.dialog_mainlayout );
        //ddImageView= (ImageView)findViewById( R.id.dialog_tipimage );

//        abilityLayout = (LinearLayout)findViewById( R.id.dialog_abilitylayout );
//
//        powerTextView = (TextView)findViewById( R.id.dialog_qreview_powertext );
//        speedTextView = (TextView)findViewById( R.id.dialog_qreview_speedtext );
//        attackTextView = (TextView)findViewById( R.id.dialog_qreview_attacktext );
//        forceTextView = (TextView)findViewById( R.id.dialog_qreview_forcetext );

        alibityLayout = findViewById( R.id.dialog_submit_abilitylayout );
        learnTextView = findViewById( R.id.dialog_leranability );
        imageView = findViewById( R.id.dialog_submit_imageview );
        heartenTextView=findViewById( R.id.dialog_hearten_text );
        titleTextView = findViewById( R.id.dialog_submit_text );

        mRebglayout = findViewById( R.id.ll_score_bg );
        mIvbg=findViewById( R.id.iv_score_bg );
        mIvclose = findViewById( R.id.iv_close_bg );
        mIvhead=findViewById( R.id.iv_score_head );
        mTvcontent = findViewById( R.id.tv_score_content );

        mIvclose.setOnClickListener( this );
        mIvbg.setOnClickListener( this );
        ImageView closeImageView = findViewById( R.id.dialog_close );
        closeImageView.setOnClickListener( this );
        View enterView = findViewById( R.id.dialog_sumit_enterbtn );
        enterView.setOnClickListener( this );
    }

}
