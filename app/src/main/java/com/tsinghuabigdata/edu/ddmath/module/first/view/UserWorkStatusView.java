package com.tsinghuabigdata.edu.ddmath.module.first.view;

import android.content.Context;
import android.graphics.Paint;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tsinghuabigdata.edu.ddmath.MVPModel.MyWorldModel;
import com.tsinghuabigdata.edu.ddmath.MVPModel.RequestListener;
import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.activity.MainActivity;
import com.tsinghuabigdata.edu.ddmath.bean.MyTutorClassInfo;
import com.tsinghuabigdata.edu.ddmath.bean.RecentWorkStatus;
import com.tsinghuabigdata.edu.ddmath.bean.UserDetailinfo;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.constant.AppConst;
import com.tsinghuabigdata.edu.ddmath.event.JumpEvent;
import com.tsinghuabigdata.edu.ddmath.fragment.MyStudyFragment;
import com.tsinghuabigdata.edu.ddmath.module.learnmaterial.CreateWorkActivity;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.WindowUtils;
import com.tsinghuabigdata.edu.ddmath.view.DayTaskView;

import org.greenrobot.eventbus.EventBus;

import java.util.Locale;

/**
 * 用户作业提示管理View
 */
@Deprecated
public class UserWorkStatusView extends LinearLayout implements View.OnClickListener{

    //作业诊断
    public RelativeLayout diagnoseLayout;
    private TextView mTvDiagnose;
    private ImageView mIvDiagnose;

    //错题收集
    public RelativeLayout collegeLayout;
    private TextView mTvCollegeError;
    private ImageView mIvCollegeError;

//    private MyWorkEvent myWorkEvent;
    private UserWorkStatusListener userWorkStatusListener;
    //记录当前那个按钮是放大的
    private ImageView largeImageView;
    private TextView showTextView;

    //按钮大小
    private int smallImageWidth;
    private int smallImageHeight;
    private int largeImageWidth;
    private int largeImageHeight;

    private RecentWorkStatus mRecentWorkStatus;

    public UserWorkStatusView(Context context) {
        super(context);
        init();
    }

    public UserWorkStatusView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public UserWorkStatusView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    @Override
    public void onClick(View v) {
        if( v.getId() == R.id.layout_zdzy_status ){
            //当前是显示大图，则直接处理点击事件
            if( largeImageView.getId() == mIvDiagnose.getId() ){
                jumpWorkList( true );
            }else{  //先变大，在处理点击事件
                largeImageView = mIvDiagnose;
                switchButtonSize( mIvCollegeError,largeImageView, mTvCollegeError, mTvDiagnose );
                //要延迟处理
                mIvDiagnose.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        jumpWorkList( true );
                    }
                },500);
            }
        }else if( v.getId() == R.id.layout_college_err ){
            //当前是显示大图，则直接处理点击事件
            if( largeImageView.getId() == mIvCollegeError.getId() ){
                jumpWorkList( false );
            }else{  //先变大，在处理点击事件
                largeImageView = mIvCollegeError;
                switchButtonSize( mIvDiagnose,largeImageView,mTvDiagnose, mTvCollegeError );

                //要延迟处理
                mIvDiagnose.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        jumpWorkList(false);
                    }
                },500);
            }
        }
    }

    public void setUserWorkStatusListener( UserWorkStatusListener listener ){
        userWorkStatusListener = listener;
    }

//    public void setHasMyWork(MyWorkEvent hasMyWork) {
//        this.myWorkEvent = hasMyWork;
//    }

    private boolean initLayout = true;
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        //int width = getWidth();
        int height= getHeight();
        //重新计算大图，小图

        //以高度为准，5:3划分
        largeImageHeight = height * 5 / 9;
        largeImageWidth = largeImageHeight * 160 / 110;

        smallImageHeight = height * 3 / 9;
        smallImageWidth = smallImageHeight * 160 / 110;

        if( initLayout ){
            initLayout = false;
            if( largeImageView == mIvDiagnose ){
                switchButtonSize( mIvCollegeError,largeImageView, mTvCollegeError, mTvDiagnose );
            }else{
                switchButtonSize( mIvDiagnose,largeImageView,mTvDiagnose, mTvCollegeError );
            }
        }
    }

    //----------------------------------------------------------------------------------
    private void init() {

        if (!GlobalData.isPad()) {
            inflate(getContext(), R.layout.view_user_workstatus_phone, this);
        } else if (small()) {
            inflate(getContext(), R.layout.view_user_workstatus_small, this);
        } else {
            inflate(getContext(), R.layout.view_user_workstatus, this);
        }

        mTvDiagnose = findViewById(R.id.tv_diagnose);
        mIvDiagnose = findViewById(R.id.iv_diagnose_workstatus);

        mTvCollegeError = findViewById(R.id.tv_college_errtip);
        mIvCollegeError = findViewById(R.id.iv_college_err);

        diagnoseLayout = findViewById( R.id.layout_zdzy_status );
        diagnoseLayout.setOnClickListener( this );
        collegeLayout = findViewById( R.id.layout_college_err );
        collegeLayout.setOnClickListener( this );

        mTvDiagnose.getViewTreeObserver().addOnGlobalLayoutListener(new OnTvGlobalLayoutListener(mTvDiagnose));
        mTvCollegeError.getViewTreeObserver().addOnGlobalLayoutListener(new OnTvGlobalLayoutListener(mTvCollegeError));

        //默认是作业诊断
        largeImageView = mIvDiagnose;

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

    private boolean small() {
        int screenWidthDp = WindowUtils.getScreenWidthDp(getContext());
        return screenWidthDp < AppConst.NAVI_WIDTH_PAD + 1100;
    }

    //跳转到作业列表界面
    public void jumpWorkList( boolean diagnose ) {
        //没有历史老师布置的作业
        if( diagnose ){
            if( mRecentWorkStatus != null && mRecentWorkStatus.isHasNormalHomework() ){
                EventBus.getDefault().post(new JumpEvent(MainActivity.FRAGMENT_STUDY_TASK, MyStudyFragment.MODEL_SCHOOLWORK));
            }else{
                CreateWorkActivity.openCreateWorkActivity( getContext(), AppConst.UPLOAD_TYPE_CAMERA );
            }
        }else{
            if( mRecentWorkStatus != null && mRecentWorkStatus.isHasCollectionHomework() ){
                EventBus.getDefault().post(new JumpEvent(MainActivity.FRAGMENT_STUDY_TASK, MyStudyFragment.MODEL_SCHOOLWORK));
            }else{
                CreateWorkActivity.openCreateWorkActivity( getContext(), AppConst.UPLOAD_TYPE_MARKED );
            }
        }
    }

    private void switchButtonSize( ImageView smallView, ImageView largeView, TextView smallTextView, TextView largeTextView){
        ViewGroup.LayoutParams layoutParams = smallView.getLayoutParams();
        layoutParams.width = smallImageWidth;
        layoutParams.height = smallImageHeight;
        smallView.setLayoutParams( layoutParams );

        layoutParams = largeView.getLayoutParams();
        layoutParams.width = largeImageWidth;
        layoutParams.height = largeImageHeight;
        largeView.setLayoutParams( layoutParams );

        //文本处理
        if( smallTextView == showTextView ) smallTextView.setVisibility( View.GONE );
        if( largeTextView == showTextView ) largeTextView.setVisibility( View.VISIBLE );
    }

    //更换图片
    private void showWork(boolean show) {
        mIvDiagnose.setImageResource( show? R.drawable.zhenduanzuoye_yibuzhi: R.drawable.zhenduanzuoye_weibuzhi );
    }
    private void showWorkCollege(boolean show) {
        mIvCollegeError.setImageResource( show? R.drawable.shoujicuoti_yibuzhi: R.drawable.shoujicuoti_weibuzhi );
    }
    public void queryRecentWorkStatus(final boolean init, final DayTaskView mViewLookWork ) {

        MyTutorClassInfo classInfo = AccountUtils.getCurrentClassInfo();
        UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
        if ( classInfo == null || detailinfo == null ) {
            return;
        }

        //查询作业/考试
        new MyWorldModel().queryRecentWorkStatus(classInfo.getClassId(), detailinfo.getStudentId(), new RequestListener<RecentWorkStatus>() {

            @Override
            public void onSuccess(RecentWorkStatus res) {
                if( userWorkStatusListener!=null ) userWorkStatusListener.dealWorkStatusCallback( true, init, res, null );

                mRecentWorkStatus = res;
                if (res == null || TextUtils.isEmpty(res.getExamId())) {
                    initShowStatus();
                    //先按默认的显示
                    mViewLookWork.setDefault();
                    return;
                }
                //
                initShowStatus();
                if( RecentWorkStatus.TYPE_WORK == res.getUploadType() ){
                    if (res.getExerStatus() <= RecentWorkStatus.NOT_SUBMIT) {
                        setTextViewTip( mTvDiagnose, View.VISIBLE, String.format(Locale.getDefault(),"老师已布置了<%s>，快去拍照上传吧！",res.getExamName()) );
                        showWork(true);
                        mViewLookWork.setDefault();
                    } else if (res.getExerStatus() < RecentWorkStatus.CORRECTED) {
                        setTextViewTip( mTvDiagnose, View.VISIBLE, "作业已经提交，正在为你诊断，请耐心等待！" );
                        showWork(true);
                        mViewLookWork.setDefault();
                    } else if (res.getReadStatus() == RecentWorkStatus.NOT_READ) {
                        setTextViewTip( mTvDiagnose, View.GONE, null );
                        showWork(false);
                        mViewLookWork.setTaskNew("你有1份新的作业待查看，快去看看吧");
                    } else {
                        setTextViewTip( mTvDiagnose, View.GONE, null );
                        showWork(false);
                        mViewLookWork.setTaskFinish("查看作业详情的任务已完成，有空去看看其他作业吧");
                    }
                }else{
                    if (res.getExerStatus() <= RecentWorkStatus.NOT_SUBMIT) {
                        setTextViewTip( mTvCollegeError, View.VISIBLE,  String.format(Locale.getDefault(),"老师布置了已批阅的<%s>，快去拍照上传吧",res.getExamName())  );
                        showWorkCollege(true);
                        mViewLookWork.setDefault();
                    } else if (res.getExerStatus() < RecentWorkStatus.CORRECTED) {
                        setTextViewTip( mTvCollegeError, View.VISIBLE, "作业已经提交，正在为你生成报告，请耐心等待！" );
                        showWorkCollege(true);
                        mViewLookWork.setDefault();
                    } else if (res.getReadStatus() == RecentWorkStatus.NOT_READ) {
                        setTextViewTip( mTvCollegeError, View.GONE, null );
                        showWorkCollege(false);
                        mViewLookWork.setTaskNew("你有1份新的作业待查看，快去看看吧");
                    } else {
                        setTextViewTip( mTvCollegeError, View.GONE, null );
                        showWorkCollege(false);
                        mViewLookWork.setTaskFinish("查看作业详情的任务已完成，有空去看看其他作业吧");
                    }
                }
            }

            @Override
            public void onFail(HttpResponse<RecentWorkStatus> response, Exception ex) {
                if( userWorkStatusListener!=null ) userWorkStatusListener.dealWorkStatusCallback( false, init, null, ex );
            }
        });
    }

    private void initShowStatus(){
        setTextViewTip( mTvDiagnose, View.GONE, null );
        showWork(false);
        setTextViewTip( mTvCollegeError, View.GONE, null );
        showWorkCollege(false);
    }

    private void setTextViewTip( TextView textView, int show, String text ){
        if( View.VISIBLE == show ){
            showTextView = textView;
            textView.setVisibility( show );
            textView.setText( text );
            if( showTextView == mTvDiagnose ){
                switchButtonSize( mIvCollegeError,mIvDiagnose, mTvCollegeError, mTvDiagnose );
                largeImageView = mIvDiagnose;
            }else{
                switchButtonSize( mIvDiagnose, mIvCollegeError, mTvDiagnose, mTvCollegeError );
                largeImageView = mIvCollegeError;
            }
        }else{
            textView.setVisibility( View.GONE );
            if( showTextView!=null && showTextView == textView ){
                showTextView = null;
            }
        }
    }
    //-----------------------------------------------------------------
    public interface UserWorkStatusListener{
        void dealWorkStatusCallback( boolean succ, boolean init, RecentWorkStatus res, Exception ex );
    }

}

