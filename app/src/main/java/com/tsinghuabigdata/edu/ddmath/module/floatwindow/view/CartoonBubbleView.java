package com.tsinghuabigdata.edu.ddmath.module.floatwindow.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.constant.AppConst;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.DDUploadActivity;
import com.tsinghuabigdata.edu.ddmath.module.floatwindow.BubbleManager;
import com.tsinghuabigdata.edu.ddmath.module.floatwindow.FloatWindowManager;
import com.tsinghuabigdata.edu.ddmath.module.floatwindow.bean.BubbleBean;
import com.tsinghuabigdata.edu.ddmath.module.floatwindow.bean.WorkInfoBean;
import com.tsinghuabigdata.edu.ddmath.module.message.MessageDetailActivity;
import com.tsinghuabigdata.edu.ddmath.module.message.bean.MessageInfo;
import com.tsinghuabigdata.edu.ddmath.util.DensityUtils;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;


/**
 * 卡通豆豆气泡界面
 */
public class CartoonBubbleView extends FrameLayout implements View.OnClickListener {

    //private final int MAX_LEN = 16;
    private TextView bubbleView;

    private final WindowManager mWindowManager;
    private WindowManager.LayoutParams mWmParams;
    private WindowManager.LayoutParams relateWmParams;
    private Drawable mDrawable;

    private BubbleManager bubbleManager;
    private ShowManager showManager;

    //当前UI名称
    private String currUiName;
    //当前气泡结构
    private BubbleBean currBubbleBean;
    private boolean isPad = false;

    public CartoonBubbleView(Context context) {
        this(context, null);
    }

    public CartoonBubbleView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        mDrawable = getResources().getDrawable( R.drawable.cartoon_dd_bubble );
        isPad = GlobalData.isPad();
        LayoutInflater.from(context).inflate( GlobalData.isPad()?R.layout.float_bubble_layout :R.layout.float_bubble_layout_phone, this);

        bubbleView = (TextView)findViewById( R.id.bubble_textview );
        bubbleView.setOnClickListener( this );
        showManager = new ShowManager();
        //
        bubbleManager = new BubbleManager( context );

    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        showManager.start();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        showManager.stop();
    }

    public void setCurrUiName( String uiName ){
        currUiName = uiName;
    }


    /**
     * 将小悬浮窗的参数传入，用于更新小悬浮窗的位置。
     *
     * @param params 小悬浮窗的参数
     */
    public void setParams(WindowManager.LayoutParams params, WindowManager.LayoutParams relparams) {
        mWmParams = params;
        relateWmParams = relparams;
    }

    public void addBubbleData(  BubbleBean bean  ){
        bubbleManager.addBubbleData( bean );
    }

    public void onClick(View v) {
        //点击气泡 进行跳转
        if( v.getId() == R.id.bubble_textview ){
            if( currBubbleBean!=null && currBubbleBean.getExtend()!=null ){

                Object extend = currBubbleBean.getExtend();
                if( extend instanceof WorkInfoBean ){
                    WorkInfoBean infoBean = (WorkInfoBean) extend;
                    Intent intent = new Intent(getContext(), DDUploadActivity.class);
                    intent.setFlags( FLAG_ACTIVITY_NEW_TASK );
                    intent.putExtra(DDUploadActivity.PARAM_DDWORKID, infoBean.getWorkId() );
                    intent.putExtra(DDUploadActivity.PARAM_TITLE, infoBean.getWorkName() );
                    getContext().startActivity(intent);
                }else if( extend instanceof MessageInfo){
                    MessageInfo messageInfo = (MessageInfo)extend;
                    Intent intent = new Intent( getContext(), MessageDetailActivity.class);
                    intent.setFlags( FLAG_ACTIVITY_NEW_TASK );
                    intent.putExtra(AppConst.MSG_ROWKEY, messageInfo.getRowKey());
                    getContext().startActivity(intent);
                }

                bubbleManager.removeBubble( currBubbleBean );
            }
        }
    }

    public void updateView(){
        mWmParams.x = relateWmParams.x - (isPad?0:30);
        int height = mDrawable.getIntrinsicHeight();
        //要动态计算高度
        String text = bubbleView.getText().toString();
        int count = 0;
        int index = text.indexOf("\n");
        while ( index >= 0 ){
            count++;
            index = text.indexOf("\n", index+1);
        }
        height += ( count*DensityUtils.sp2px( getContext(), isPad?14:12 ));

        mWmParams.y = relateWmParams.y - height;

        if(FloatWindowManager.hasShow())
            mWindowManager.updateViewLayout( this, mWmParams );
    }
    public void showBubble(){
        showBubble( bubbleManager.getBubbleData( currUiName ) );
    }
    public void showBubble( BubbleBean bean ){
        setVisibility( View.VISIBLE );
        String data = bean.getData();
        int MAX_LEN = 20;
        if( data.length() > MAX_LEN ){
            int count = data.length() / MAX_LEN;
            int remainder = data.length() % MAX_LEN;

            StringBuilder sb = new StringBuilder();
            for( int i=0; i<count; i++ ){
                if( sb.length() > 0 ) sb.append("\n");
                sb.append( data.substring( i*MAX_LEN, (i+1)*MAX_LEN ) );
            }
            if( remainder > 0 ){
                sb.append("\n");
                sb.append( data.substring( count*MAX_LEN ) );
            }
            data = sb.toString();
        }

        bubbleView.setText( data );
        bubbleView.setTextColor( bean.getExtend()==null? Color.rgb(0xEA,0x62,0x09): Color.BLUE );
        showManager.start();

        bubbleView.startAnimation( childBigAnimation( 1000, 1.0f) );
        updateView();
    }
    //------------------------------------------------------------------------------------------

    /**菜单项点击时，缩小隐藏，透明度变换动画*/
    private AnimationSet childSmallAnimation(int duration, float scale) {
        AnimationSet animationSet=new AnimationSet(true);
        ScaleAnimation scaleAnimation=new ScaleAnimation(scale,0.0f,scale,0.0f, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        AlphaAnimation alphaAnimation=new AlphaAnimation(1.0f,0.0f);
        //TranslateAnimation translateAnimation = new TranslateAnimation( 0, 0, 20, 80 );

        animationSet.setFillAfter(true);
        animationSet.setDuration(duration);
        animationSet.addAnimation(scaleAnimation);
        animationSet.addAnimation(alphaAnimation);
        //animationSet.addAnimation( translateAnimation );
        return  animationSet;
    }
    /**菜单项点击时，放大显示，透明度变换动画*/
    private AnimationSet childBigAnimation(int duration,float scale) {
        AnimationSet animationSet=new AnimationSet(true);
        //缩放动画
        ScaleAnimation scaleAnimation=new ScaleAnimation( 0f,scale,0f,scale,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        AlphaAnimation alphaAnimation=new AlphaAnimation(0f,1.0f);
        //TranslateAnimation translateAnimation = new TranslateAnimation( -20, -80, 0, 0);
        animationSet.setFillAfter(false);
        animationSet.setDuration(duration);
        animationSet.addAnimation(scaleAnimation);
        animationSet.addAnimation(alphaAnimation);
        //animationSet.addAnimation( translateAnimation );
        return  animationSet;
    }

    //气泡显示时间控制,自动控制
    private class ShowManager{

        final int MAX_TIME = 20;    //
        final int SHOW_TIME = 10;
        final int ST_NONE = 0;
        final int ST_RUN  = 1;
        //final int ST_NONE = 0;


        int time = MAX_TIME;
        int status = ST_NONE;

        public void stop(){
            status = ST_NONE;
        }

        public void start(){

            time = MAX_TIME;
            if( status == ST_RUN )
                return;
            status = ST_RUN;
            new Thread(new Runnable() {
                @Override
                public void run() {

                    while (status == ST_RUN){
                        //一秒钟查询一次
                        SystemClock.sleep( 1000 );
                        time--;

                        if( time <= 0 ){        //显示气泡
                            time = MAX_TIME;

                            //出现气泡
                            bubbleView.post(new Runnable() {
                                @Override
                                public void run() {
                                    currBubbleBean = bubbleManager.getBubbleData( currUiName );
                                    showBubble( currBubbleBean );
                                }
                            });
                        }else if( time == (MAX_TIME-SHOW_TIME) ){     //隐藏气泡
                            bubbleView.post(new Runnable() {
                                @Override
                                public void run() {
                                    currBubbleBean = null;
                                    bubbleView.setText( "" );
                                    bubbleView.startAnimation( childSmallAnimation(1000,0));
                                    setVisibility( View.GONE );
                                }
                            });
                        }
                    }
                }
            }).start();
        }
    }
}
