package com.tsinghuabigdata.edu.ddmath.module.first.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.tsinghuabigdata.edu.ddmath.MVPModel.MyWorldModel;
import com.tsinghuabigdata.edu.ddmath.MVPModel.RequestListener;
import com.tsinghuabigdata.edu.ddmath.MVPModel.ScorePlanModel;
import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.activity.MainActivity;
import com.tsinghuabigdata.edu.ddmath.bean.MyTutorClassInfo;
import com.tsinghuabigdata.edu.ddmath.bean.ScorePlanBean;
import com.tsinghuabigdata.edu.ddmath.bean.UserDetailinfo;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.constant.AppConst;
import com.tsinghuabigdata.edu.ddmath.event.JumpEvent;
import com.tsinghuabigdata.edu.ddmath.fragment.MyStudyFragment;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.DDUploadActivity;
import com.tsinghuabigdata.edu.ddmath.module.first.bean.FirstWorkStatusBean;
import com.tsinghuabigdata.edu.ddmath.module.scoreplan.activity.ScoreGuideActivity;
import com.tsinghuabigdata.edu.ddmath.module.scoreplan.activity.ScorePlanActivity;
import com.tsinghuabigdata.edu.ddmath.module.scoreplan.activity.ScorePlanIntroActivity;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.AppLog;
import com.tsinghuabigdata.edu.ddmath.util.DensityUtils;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.PreferencesUtils;
import com.tsinghuabigdata.edu.ddmath.util.WindowUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.Locale;

/**
 * 用户首页功能按钮
 */
public class UserModelNavView extends LinearLayout implements View.OnClickListener{

    private ImageView btnSubmitWorkView;
    private ImageView btnCustomPlanView;
    private ImageView btnFreeTrainView;

    private RelativeLayout tipsSubmitWorkLayout;
    private RelativeLayout tipsCustomPlanLayout;
    private ImageView smallTipsCustomPlanView;
    private RelativeLayout tipsFreeTrainLayout;


    private TextView msgSubmitWorkView;
    private TextView msgCustomPlanView;
    private TextView msgFreeTrainView;

    private ImageView arrawSubmitWork;
    private ImageView arrawFreeTrain;

//    //按钮大小
//    private int smallImageWidth;
//    private int smallImageHeight;
//    private int largeImageWidth;
//    private int largeImageHeight;

    private FirstWorkStatusBean mFirstWorkStatusBean;
    private ScorePlanBean mScorePlanBean;

    private UserWorkStatusListener userWorkStatusListener;

    public UserModelNavView(Context context) {
        super(context);
        init();
    }

    public UserModelNavView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public UserModelNavView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_function_submitwork:
                if( !TextUtils.isEmpty(mFirstWorkStatusBean.getExamId()) && FirstWorkStatusBean.WORK_STATED <= mFirstWorkStatusBean.getExerStatus() ){
                    //看作业详情
                    Intent intent = new Intent(getContext(), DDUploadActivity.class);
                    intent.putExtra(DDUploadActivity.PARAM_DDWORKID, mFirstWorkStatusBean.getExamId());
                    intent.putExtra(DDUploadActivity.PARAM_TITLE, mFirstWorkStatusBean.getExamName());
                    getContext().startActivity(intent);
                }else{
                    EventBus.getDefault().post(new JumpEvent(MainActivity.FRAGMENT_STUDY_TASK, MyStudyFragment.MODEL_SCHOOLWORK));
                }
                break;
            case R.id.iv_function_customplan:{
                if( mScorePlanBean != null ){
                    if( mScorePlanBean.isRight() ){
                        //是否加入定制学
                        if( mScorePlanBean.isConfirmJoin() ){
                            if(PreferencesUtils.getBoolean( getContext(), ScoreGuideActivity.PRE_KEY_FIRST, false ) ){
                                ScorePlanActivity.openActivity( getContext() );
                            }else{  //先去引导界面
                                getContext().startActivity(new Intent( getContext(), ScoreGuideActivity.class));
                            }
                        }else{
                            ScorePlanIntroActivity.openActivity( getContext(), ScorePlanIntroActivity.FROM_TYPE_NOUSE,false, mScorePlanBean);
                        }
                    }else{
                        //没有权限进入
                        ScorePlanIntroActivity.openActivity( getContext(), ScorePlanIntroActivity.FROM_TYPE_NOBUY,false, mScorePlanBean);
                    }
                }else{
                    // 怎么处理 ,再次触发查询
                    //ScorePlanIntroActivity.openActivity( getContext(), ScorePlanIntroActivity.FROM_TYPE_NOBUY, null);
                    queryCustomPlanStatus( false );
                }
                break;
            }
            case R.id.iv_function_freetrain:
                EventBus.getDefault().post(new JumpEvent(MainActivity.FRAGMENT_STUDY_TASK, MyStudyFragment.MODEL_CHECK_WORK));
                break;
            default:
                break;
        }
    }
//
    public void setUserWorkStatusListener( UserWorkStatusListener listener ){
        userWorkStatusListener = listener;
    }

//    public void setHasMyWork(MyWorkEvent hasMyWork) {
//        this.myWorkEvent = hasMyWork;
//    }

    private boolean initflag = false;
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        AppLog.d("fdsfsafs changed = " + changed );

        if( initflag || !changed ) return;
        initflag = true;

        int width = getWidth();
        int height= getHeight();
        int offsetX = 0, offsetY = 0;

        //保证定制学 图片和气泡可以同时显示 178*130  212*81 文本高度 10，总的w:212 h:220
        int tw = width / 3;
        if( tw * 220  > height * 212 ){     //高度不足,按高度重新计算
            width = height * 212  * 3/ 220;
            offsetX = (getWidth()-width) / 2;
        }else{      //宽度够，计算高度偏移
            int th = tw * 220 / 212;
            offsetY = ( height - th )/2;
            int defaultBottom = DensityUtils.dp2px(getContext(),10);
            if( offsetY < defaultBottom ) offsetY = defaultBottom;
        }

        //水平等分为三份
        int itemWidth = width / 3;
        int itemCustomWidth = (int)(itemWidth * 1.0);       //定制学图片 90%的宽度,气泡长度大一些
        int itemCustomHeight = itemCustomWidth * 130 / 178;
        int itemOtherWidth = itemCustomWidth * 110 / 178;         //交作业 自由练 宽度，相对定制学确定
        int itemOtherHeight = itemOtherWidth * 106 / 110;

        //.int bottomMargin = DensityUtils.dp2px( getContext(), 30);       /   确认显示底部
        //调整定制学  默认居中
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)btnCustomPlanView.getLayoutParams();
        layoutParams.width = itemCustomWidth;
        layoutParams.height= itemCustomHeight;
        layoutParams.bottomMargin = offsetY;
        btnCustomPlanView.setLayoutParams( layoutParams );

        //调整交作业
        layoutParams = (RelativeLayout.LayoutParams)btnSubmitWorkView.getLayoutParams();
        layoutParams.width = itemOtherWidth;
        layoutParams.height= itemOtherHeight;
        layoutParams.bottomMargin = offsetY;
        layoutParams.leftMargin = offsetX + (itemWidth - itemOtherWidth) / 2;
        //layoutParams.bottomMargin = bottomMargin;
        btnSubmitWorkView.setLayoutParams( layoutParams );

        //自由练的位置
        layoutParams = (RelativeLayout.LayoutParams)btnFreeTrainView.getLayoutParams();
        layoutParams.width = itemOtherWidth;
        layoutParams.height= itemOtherHeight;
        layoutParams.bottomMargin = offsetY;
        layoutParams.rightMargin = offsetX + (itemWidth - itemOtherWidth) / 2;
        //layoutParams.bottomMargin = bottomMargin;
        btnFreeTrainView.setLayoutParams( layoutParams );

        //调整箭头位置
        layoutParams = (RelativeLayout.LayoutParams)arrawSubmitWork.getLayoutParams();
        //宽度是 平均宽度的1/2 在加上 0.1,
        layoutParams.width = itemWidth * 3 / 5;
        layoutParams.height= layoutParams.width * 50 / 132;
        layoutParams.leftMargin = offsetX + itemWidth / 2;    //中间开始
        arrawSubmitWork.setLayoutParams( layoutParams );

        layoutParams = (RelativeLayout.LayoutParams)arrawFreeTrain.getLayoutParams();
        //宽度是 平均宽度的1/2 在加上 0.1,
        layoutParams.width = itemWidth * 3 / 5;
        layoutParams.height= layoutParams.width * 50 / 132;
        layoutParams.rightMargin = offsetX + itemWidth / 2;    //中间开始
        arrawFreeTrain.setLayoutParams( layoutParams );

        //调整气泡大小 及对应的文本大小
        float rateCustom = itemWidth * 1f / 212;
        float rateOther  = rateCustom * 0.9f;
        layoutParams = (RelativeLayout.LayoutParams)tipsSubmitWorkLayout.getLayoutParams();
        layoutParams.width = itemWidth * 9 / 10;            //90%
        layoutParams.height= layoutParams.width * 81 / 212;
        layoutParams.leftMargin = offsetX + itemWidth / 20;
        tipsSubmitWorkLayout.setLayoutParams( layoutParams );

        // w:170, h:40, top:6, left:33
        layoutParams = (RelativeLayout.LayoutParams)msgSubmitWorkView.getLayoutParams();
        layoutParams.width = (int)(rateOther * 170);
        layoutParams.height= (int)(rateOther * 40);
        layoutParams.topMargin = (int)(rateOther * 4);
        layoutParams.leftMargin = (int)(rateOther * 33);
        layoutParams.bottomMargin = -60;
        msgSubmitWorkView.setLayoutParams( layoutParams );

        layoutParams = (RelativeLayout.LayoutParams)tipsCustomPlanLayout.getLayoutParams();
        layoutParams.width = itemWidth;         //整个宽度
        layoutParams.height= itemWidth * 81 / 212;
        tipsCustomPlanLayout.setLayoutParams( layoutParams );

        // w:178, h:40, top:7, left:30
        layoutParams = (RelativeLayout.LayoutParams)msgCustomPlanView.getLayoutParams();
        layoutParams.width = (int)(rateCustom * 178);
        layoutParams.height= (int)(rateCustom * 40);
        layoutParams.topMargin = (int)(rateCustom * 5);
        layoutParams.leftMargin = (int)(rateCustom * 30);
        msgCustomPlanView.setLayoutParams( layoutParams );

        //小气泡提示
        layoutParams = (RelativeLayout.LayoutParams)smallTipsCustomPlanView.getLayoutParams();
        layoutParams.width = (int)(rateCustom * 30);
        layoutParams.height= (int)(rateCustom * 30);
        layoutParams.bottomMargin = (int)(rateCustom * -18);
        layoutParams.rightMargin = (int)(rateCustom * 22);
        smallTipsCustomPlanView.setLayoutParams( layoutParams );

        layoutParams = (RelativeLayout.LayoutParams)tipsFreeTrainLayout.getLayoutParams();
        layoutParams.width = itemWidth * 9 / 10;
        layoutParams.height= layoutParams.width * 81 / 203;
        layoutParams.rightMargin = offsetX + itemWidth / 20;
        layoutParams.bottomMargin = -60;
        tipsFreeTrainLayout.setLayoutParams( layoutParams );

        // w:150, h:40, top:6, left:21
        layoutParams = (RelativeLayout.LayoutParams)msgFreeTrainView.getLayoutParams();
        layoutParams.width = (int)(rateOther * 150);
        layoutParams.height= (int)(rateOther * 40);
        layoutParams.topMargin = (int)(rateOther * 4);
        layoutParams.leftMargin = (int)(rateOther * 21);
        msgFreeTrainView.setLayoutParams( layoutParams );
    }

    //----------------------------------------------------------------------------------
    private void init() {

        inflate(getContext(), R.layout.view_user_modelnav_phone, this);

        btnSubmitWorkView =  findViewById(R.id.iv_function_submitwork);
        btnCustomPlanView =  findViewById(R.id.iv_function_customplan);
        btnFreeTrainView =  findViewById(R.id.iv_function_freetrain);
        btnSubmitWorkView.setOnClickListener( this );
        btnCustomPlanView.setOnClickListener( this );
        btnFreeTrainView.setOnClickListener( this );

        tipsSubmitWorkLayout =  findViewById(R.id.layout_bubble_submitwork);
        tipsCustomPlanLayout =  findViewById(R.id.layout_bubble_customplan);
        smallTipsCustomPlanView = findViewById(R.id.iv_customplan_smalltips);
        tipsFreeTrainLayout =  findViewById(R.id.layout_bubble_freetrain);

        msgSubmitWorkView =  findViewById(R.id.tv_bubbletips_submitwork);
        msgCustomPlanView =  findViewById(R.id.tv_bubbletips_customplan);
        msgFreeTrainView =  findViewById(R.id.tv_bubbletips_freetrain);

        msgSubmitWorkView.getViewTreeObserver().addOnGlobalLayoutListener(new OnTvGlobalLayoutListener(msgSubmitWorkView));
        msgCustomPlanView.getViewTreeObserver().addOnGlobalLayoutListener(new OnTvGlobalLayoutListener(msgCustomPlanView));
        msgFreeTrainView.getViewTreeObserver().addOnGlobalLayoutListener(new OnTvGlobalLayoutListener(msgFreeTrainView));

        arrawSubmitWork =  findViewById(R.id.iv_arrow_submitwork);
        arrawFreeTrain =  findViewById(R.id.iv_arrow_freetrain);

        TextView submitModelName = findViewById(R.id.tv_modeame_submit);
        TextView customModelName = findViewById(R.id.tv_modeame_custom);
        TextView freetrainModelName = findViewById(R.id.tv_modeame_freetrain);

        if (!GlobalData.isPad()) {      //手机
            int fontSize = 12;
            submitModelName.setTextSize(fontSize);
            customModelName.setTextSize(fontSize);
            freetrainModelName.setTextSize(fontSize);

            msgSubmitWorkView.setTextSize(fontSize);
            msgCustomPlanView.setTextSize(fontSize);
            msgFreeTrainView.setTextSize(fontSize);

        } else if (small()) {       //
            int fontSize = 18;
            submitModelName.setTextSize(fontSize);
            customModelName.setTextSize(fontSize);
            freetrainModelName.setTextSize(fontSize);

            msgSubmitWorkView.setTextSize(fontSize);
            msgCustomPlanView.setTextSize(fontSize);
            msgFreeTrainView.setTextSize(fontSize);
        } else {        //pad
            int fontSize = 24;
            submitModelName.setTextSize(fontSize);
            customModelName.setTextSize(fontSize);
            freetrainModelName.setTextSize(fontSize);

            msgSubmitWorkView.setTextSize(fontSize);
            msgCustomPlanView.setTextSize(fontSize);
            msgFreeTrainView.setTextSize(fontSize);
        }

    }

    private boolean small() {
        int screenWidthDp = WindowUtils.getScreenWidthDp(getContext());
        return screenWidthDp < AppConst.NAVI_WIDTH_PAD + 1100;
    }

    //查询交作业和自由练气泡显示状态
    public void queryWorkStatus( final boolean init ) {

        MyTutorClassInfo classInfo = AccountUtils.getCurrentClassInfo();
        UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
        if ( classInfo == null || detailinfo == null ) {
            return;
        }

        //查询作业/考试
        new MyWorldModel().queryFirstWorkStatus(detailinfo.getStudentId(), classInfo.getClassId(), new RequestListener<FirstWorkStatusBean>() {

            @Override
            public void onSuccess(FirstWorkStatusBean res) {
                if( userWorkStatusListener!=null ) userWorkStatusListener.dealWorkStatusCallback( true, init, null );

                //初始化时依次调用
                if(init) queryCustomPlanStatus( true );

                mFirstWorkStatusBean = res;
                if (res == null /*|| TextUtils.isEmpty(res.getExamId())*/) {
                    //先按默认的显示
                    initShowStatus();
                    return;
                }
                //
                initShowStatus();

                //显示气泡 交作业
                //1，有新的“需要诊断的作业”“收集错题的作业”时，点击进入“交作业”板块，可提交作业；提示的消息内容跟之前的规则一致，需要带上作业名称；在完成该提醒对应的作业/考试后，气泡消失；
                //2, 有新的“报告”时，也在首页提示，点击进入“交作业”板块；提示的内通跟之前的规则一致，需要带上报告名称；在查看了该提醒对应的作业/考试的详情页后，气泡消失；
                //3, 如果同时有作业/报告的提示，只显示作业提示，不显示报告提示；有多份作业/考试需要学生提交，气泡提醒，仅显示最近一份作业/考试名称；
                //4, 没有需要学生提交的作业/考试，但有需要老师扫描上传的作业/考试，无气泡提醒哇；
                if( !TextUtils.isEmpty(res.getExamId()) ){
                    //有新作业 还未提交
                    if( FirstWorkStatusBean.WORK_UNSUBMIT >= res.getExerStatus()){
                        tipsSubmitWorkLayout.setVisibility(View.VISIBLE);
                        tipsSubmitWorkLayout.setBackgroundResource(R.drawable.bg_tips_homework);
                        msgSubmitWorkView.setText(String.format(Locale.getDefault(),"老师已布置了<%s>，快去拍照上传吧！",res.getExamName()));
                    }
                    //看报告
                    else if( FirstWorkStatusBean.WORK_STATED <= res.getExerStatus()){
                        tipsSubmitWorkLayout.setVisibility(View.VISIBLE);
                        tipsSubmitWorkLayout.setBackgroundResource(R.drawable.ic_tips_report);
                        msgSubmitWorkView.setText(String.format(Locale.getDefault(),"<%s>的报告已经生成，去看看吧！",res.getExamName()));
                    }
                }

                //自由练 ：超过3天（精确到时分秒，已作业的开始时间计算）老师没有布置作业/自己也没有自由练，在“自由练”提示
                if( !res.isHasHomeworkLast3Days() ){
                    tipsFreeTrainLayout.setVisibility(View.VISIBLE);
                    msgFreeTrainView.setText("坚持收集错题，才能显著提分哦：）");
                }
            }

            @Override
            public void onFail(HttpResponse<FirstWorkStatusBean> response, Exception ex) {
                if( userWorkStatusListener!=null ) userWorkStatusListener.dealWorkStatusCallback( false, init, ex );
            }
        });
    }

    //查询定制学气泡显示状态
    public void queryCustomPlanStatus(final boolean init) {

        MyTutorClassInfo classInfo = AccountUtils.getCurrentClassInfo();
        UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
        if ( classInfo == null || detailinfo == null ) {
            return;
        }

        //查询作业/考试
        new ScorePlanModel().queryScorePlan( detailinfo.getStudentId(), new RequestListener<ScorePlanBean>() {

            @Override
            public void onSuccess(ScorePlanBean bean) {
                //if( userWorkStatusListener!=null ) userWorkStatusListener.dealWorkStatusCallback( true, init, res, null );

                mScorePlanBean = bean;
                AccountUtils.mScorePlanBean = bean;

                tipsFreeTrainLayout.setVisibility( View.INVISIBLE );
                smallTipsCustomPlanView.setVisibility( View.INVISIBLE );

                //默认不显示
                tipsCustomPlanLayout.setVisibility(View.INVISIBLE);
                if (bean == null) {
                    return;
                }

                //显示规则
                //刷新的触发
                //1, 周一-周五平时有任务时的提醒，小提醒：只是提醒，不用显示内容；在当日完成了所有错题订正以后，小提醒消失；
                //2, 周末的提醒，大气泡提醒：生成了个人周专属提分方案；在下载了定制学个人专属提分方案后，气泡消失；
                //3, 周末没有生成个人专属提分方案 但是还有错题订正 这时候就还是给小提示 其他的时候 周末只要有个人专属提分方案 就不管别的 只提示个人专属提方案的气泡提醒；

                if( bean.getDayWeek() <= 5 ){   //平时, 显示小提醒, 仅仅指 错题订正 是否完成
                    if( !bean.isFinishAllTasks() ){
                        tipsCustomPlanLayout.setVisibility(View.INVISIBLE);
                        smallTipsCustomPlanView.setVisibility(View.VISIBLE);
                        smallTipsCustomPlanView.setImageResource(R.drawable.ic_tips_personal_little);
                    }
                }else {      //周末
                    if (!TextUtils.isEmpty(bean.getRemindContent())) {
                        tipsCustomPlanLayout.setVisibility(View.VISIBLE);
                        smallTipsCustomPlanView.setVisibility(View.INVISIBLE);
                        msgCustomPlanView.setText(bean.getRemindContent());
                    }else if( !bean.isFinishAllTasks() ){   //有错题订正
                        tipsCustomPlanLayout.setVisibility(View.INVISIBLE);
                        smallTipsCustomPlanView.setVisibility(View.VISIBLE);
                        smallTipsCustomPlanView.setImageResource(R.drawable.ic_tips_personal_little);
                    }
                }
            }

            @Override
            public void onFail(HttpResponse<ScorePlanBean> response, Exception ex) {
                if( userWorkStatusListener!=null ) userWorkStatusListener.dealWorkStatusCallback( false, init, ex );
            }
        });
    }

    private void initShowStatus(){

        //气泡都隐藏
        tipsSubmitWorkLayout.setVisibility( View.INVISIBLE );
        tipsCustomPlanLayout.setVisibility( View.INVISIBLE );
    }
    //-----------------------------------------------------------------
    public interface UserWorkStatusListener{
        void dealWorkStatusCallback(boolean succ, boolean init, Exception ex);
    }

    private class OnTvGlobalLayoutListener implements ViewTreeObserver.OnGlobalLayoutListener {

        private TextView mText;
        /*public*/ OnTvGlobalLayoutListener( TextView textView){
            mText = textView;
        }

        @Override
        public void onGlobalLayout() {
            //mText.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            final String newText = autoSplitText(mText);
            if (!TextUtils.isEmpty(newText)) {
                mText.setText(newText);
            }
        }
        private String autoSplitText(final TextView tv) {
            final String rawText = tv.getText().toString(); //原始文本
            final Paint tvPaint = tv.getPaint(); //paint，包含字体等信息
            final float tvWidth = tv.getWidth() - tv.getPaddingLeft() - tv.getPaddingRight(); //控件可用宽度
            if( tvWidth < 30 ) return rawText;

            //将原始文本按行拆分
            String [] rawTextLines = rawText.replaceAll("\r", "").split("\n");
            StringBuilder sbNewText = new StringBuilder();
            for (String rawTextLine : rawTextLines) {
                if (tvPaint.measureText(rawTextLine) <= tvWidth) {
                    //如果整行宽度在控件可用宽度之内，就不处理了
                    sbNewText.append(rawTextLine);
                } else {
                    //如果整行宽度超过控件可用宽度，则按字符测量，在超过可用宽度的前一个字符处手动换行
                    float lineWidth = 0;
                    for (int cnt = 0; cnt != rawTextLine.length(); ++cnt) {
                        char ch = rawTextLine.charAt(cnt);
                        lineWidth += tvPaint.measureText(String.valueOf(ch));
                        if (lineWidth <= tvWidth) {
                            sbNewText.append(ch);
                        } else {
                            sbNewText.append("\n");
                            lineWidth = 0;
                            --cnt;
                        }
                    }
                }
                sbNewText.append("\n");
            }

            //把结尾多余的\n去掉
            if (!rawText.endsWith("\n")) {
                sbNewText.deleteCharAt(sbNewText.length() - 1);
            }

            return sbNewText.toString();
        }
    }

}

