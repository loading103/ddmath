package com.tsinghuabigdata.edu.ddmath.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.constant.AppConst;
import com.tsinghuabigdata.edu.ddmath.util.DensityUtils;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.WindowUtils;


/**
 * 我的世界——每日任务（作业/考试 错题订正 错题再练 培优新题）
 * Created by Administrator on 2018/6/15.
 */
@Deprecated
public class DayTaskView extends RelativeLayout {

    public static final int STATUS_DEFAULT = 0;
    public static final int STATUS_NEW     = 1;
    public static final int STATUS_FINISH  = 2;  //统计完成

    private int mType;
    private int mStatus;

    /*public enum Type {
        LOOK_WORK, ERROR_REVISE, ERROR_PRACTICE, TRAIN_EXCELLENT
    }*/

    private LinearLayout mLlMain;
    private ImageView    mIvMain;
    private TextView     mTvDef;
    private TextView     mTvTask;
    private ImageView    mIvNew;
    private ImageView    mIvFinish;


    public DayTaskView(Context context) {
        this(context, null);
    }

    public DayTaskView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DayTaskView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray mTypedArray = context.getTheme().obtainStyledAttributes(
                attrs, R.styleable.DayTaskView, defStyleAttr, 0);
        mType = mTypedArray.getInteger(R.styleable.DayTaskView_tasktype, 0);
        //View.inflate(context, GlobalData.isPad() ? R.layout.v_day_task : R.layout.v_day_task_phone, this);
        if (!GlobalData.isPad()) {
            View.inflate(context, R.layout.v_day_task_phone, this);
        } else if (small()) {
            View.inflate(context, R.layout.v_day_task_small, this);
        } else {
            View.inflate(context, R.layout.v_day_task, this);
        }
        initView();
    }

    public LinearLayout getMainLayout(){ return mLlMain;}

    private void initView() {
        mLlMain = (LinearLayout) findViewById(R.id.ll_main);
        mIvMain = (ImageView) findViewById(R.id.iv_main);
        mTvDef = (TextView) findViewById(R.id.tv_def);
        mTvTask = (TextView) findViewById(R.id.tv_task);
        mIvNew = (ImageView) findViewById(R.id.iv_new);
        mIvFinish = (ImageView) findViewById(R.id.iv_finish);
        if (mType == 0) {
            mLlMain.setBackgroundResource(R.drawable.bg_look_work);
            mIvMain.setImageResource(R.drawable.ic_chakan);
            mTvDef.setTextColor(getContext().getResources().getColor(R.color.color_D19465));
            mTvTask.setTextColor(getContext().getResources().getColor(R.color.color_D19465));
            mTvDef.setText(R.string.look_work_detail);
        } else if (mType == 1) {
            mLlMain.setBackgroundResource(R.drawable.bg_error_correct);
            mIvMain.setImageResource(R.drawable.ic_cuotidingzheng);
            mTvDef.setTextColor(getContext().getResources().getColor(R.color.color_CC9F58));
            mTvTask.setTextColor(getContext().getResources().getColor(R.color.color_CC9F58));
            mTvDef.setText(R.string.everyday_error_correct);
        } else if (mType == 2) {
            mLlMain.setBackgroundResource(R.drawable.bg_error_practice);
            mIvMain.setImageResource(R.drawable.ic_cuotizhoutilian);
            mTvDef.setTextColor(getContext().getResources().getColor(R.color.color_50B162));
            mTvTask.setTextColor(getContext().getResources().getColor(R.color.color_50B162));
            mTvDef.setText(R.string.everyweek_error_practice);
        } else {
            mLlMain.setBackgroundResource(R.drawable.bg_train_excellent);
            mIvMain.setImageResource(R.drawable.ic_meizhoupeiyou);
            mTvDef.setTextColor(getContext().getResources().getColor(R.color.color_6DBAC3));
            mTvTask.setTextColor(getContext().getResources().getColor(R.color.color_6DBAC3));
            mTvDef.setText(R.string.everyweek_train_excellent);
        }
    }

    public void setDefault() {
        mStatus = STATUS_DEFAULT;
        mTvDef.setVisibility(VISIBLE);
        mTvTask.setVisibility(GONE);
        mIvNew.setVisibility(INVISIBLE);
        mIvFinish.setVisibility(INVISIBLE);
    }

    public void setTaskNew(String data) {
        mStatus = STATUS_NEW;
        String str = data + "  ";
        SpannableString spannableString = new SpannableString(str);//textView控件
        ImageSpan imageSpan = new ImageSpan(getDrawable(), DynamicDrawableSpan.ALIGN_BASELINE);
        spannableString.setSpan(imageSpan, str.length() - 2, str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);//注意这里的字符串长度，我项目需求是图片添加到最后，但是SpannableString没有放在最后的方法，所以我在字符串的长度后面加了2个空格  这样就能放最后了
        mTvTask.setText(spannableString);

        mTvDef.setVisibility(GONE);
        mTvTask.setVisibility(VISIBLE);
        mIvNew.setVisibility(VISIBLE);
        mIvFinish.setVisibility(INVISIBLE);
    }

    public void setTaskFinish(String data) {
        mStatus = STATUS_FINISH;
        String str = data + "  ";
        SpannableString spannableString = new SpannableString(str);//textView控件
        ImageSpan imageSpan = new ImageSpan(getDrawable(), DynamicDrawableSpan.ALIGN_BASELINE);
        spannableString.setSpan(imageSpan, str.length() - 2, str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);//注意这里的字符串长度，我项目需求是图片添加到最后，但是SpannableString没有放在最后的方法，所以我在字符串的长度后面加了2个空格  这样就能放最后了
        mTvTask.setText(spannableString);
        mTvDef.setVisibility(GONE);
        mTvTask.setVisibility(VISIBLE);
        mIvNew.setVisibility(INVISIBLE);
        mIvFinish.setVisibility(VISIBLE);
    }

    public int getStatus() {
        return mStatus;
    }

    public void setStatus(int status) {
        mStatus = status;
    }

    private Drawable getDrawable() {
        Drawable drawable;
        if (mType == 0) {
            drawable = getContext().getResources().getDrawable(R.drawable.red_next);
        } else if (mType == 1) {
            drawable = getContext().getResources().getDrawable(R.drawable.yellow_next);
        } else if (mType == 2) {
            drawable = getContext().getResources().getDrawable(R.drawable.green_next);
        } else {
            drawable = getContext().getResources().getDrawable(R.drawable.blue_next);
        }
        if (GlobalData.isPad()) {
            drawable.setBounds(0, 0, getPx(36), getPx(24)); //设置边界
        } else {
            drawable.setBounds(0, 0, getPx(18), getPx(12)); //设置边界
        }
        return drawable;
    }

    private int getPx(int i) {
        return DensityUtils.dp2px(getContext(), i);
    }

    private boolean small() {
        int screenWidthDp = WindowUtils.getScreenWidthDp(getContext());
        return screenWidthDp < AppConst.NAVI_WIDTH_PAD + 1100;
    }
}
