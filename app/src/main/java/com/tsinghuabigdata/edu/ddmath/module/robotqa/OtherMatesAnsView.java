package com.tsinghuabigdata.edu.ddmath.module.robotqa;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.activity.SinglePicturePreviewActivity;
import com.tsinghuabigdata.edu.ddmath.bean.QuestionAns;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.PicassoUtil;

import java.util.List;


/**
 * Created by HP on 2016/10/21.
 */
public class OtherMatesAnsView extends LinearLayout implements View.OnClickListener {

    private TextView        tv_title;
    private TextView        tv_switch;
    private TextView        tvGivelike;
    private ImageView       ivOtherAns;
    private LinearLayout    llBottomTitleContainer;
    private OnClickListener listener;

    private OthermatesAnsData othermatesAnsData;

    public OtherMatesAnsView(Context context) {
        super(context);
        init();
    }

    public OtherMatesAnsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public OtherMatesAnsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public OtherMatesAnsView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        if (GlobalData.isPad()) {
            LayoutInflater.from(getContext()).inflate(R.layout.layout_othermates_ans_view, this);
        } else {
            LayoutInflater.from(getContext()).inflate(R.layout.layout_othermates_ans_view_phone, this);
        }
        tv_title = (TextView) findViewById(R.id.tv_title);
        tvGivelike = (TextView) findViewById(R.id.tv_givelike);
        tv_switch = (TextView) findViewById(R.id.tv_switch);
        ivOtherAns = (ImageView) findViewById(R.id.iv_otherans);
        llBottomTitleContainer = (LinearLayout) findViewById(R.id.ll_bottom_title);

        tv_switch.setOnClickListener(this);
        tvGivelike.setOnClickListener(this);
        ivOtherAns.setOnClickListener(this);
    }

    public void setOtherAnsData(List<QuestionAns> questionAns) {
        othermatesAnsData = new OthermatesAnsData(questionAns);
        refreshDisplay();
    }

    public void setOtherAnsData(OthermatesAnsData data) {
        othermatesAnsData = data;
        refreshDisplay();
    }

    private void refreshDisplay() {
        QuestionAns displayQuestionAns = othermatesAnsData.getDisplayAns();
        if (displayQuestionAns == null) {
            return;
        }
        if (TextUtils.isEmpty(displayQuestionAns.getSchoolName())) {
            tv_title.setText(displayQuestionAns.getStuName() + "的正确答案");
        } else {
            tv_title.setText(displayQuestionAns.getStuName() + "(" + displayQuestionAns.getSchoolName() + ")的正确答案");
        }

        //点赞状态显示
        showGivelikeView(displayQuestionAns);
        //同学答案图片显示
        loadImage(getContext(), ivOtherAns, displayQuestionAns.getAnswerUrl());
    }

    private void showGivelikeView(QuestionAns displayQuestionAns) {
        Drawable drawable;
        if (displayQuestionAns.isHadGivelike()) {
            tvGivelike.setText("已赞");
            tvGivelike.setEnabled(false);
            tvGivelike.setTextColor(getContext().getResources().getColor(R.color.give_five_text_color_disabled));
            drawable = getContext().getResources().getDrawable(R.drawable.ic_thumb_up_disable);
        } else {
            tvGivelike.setText("赞");
            tvGivelike.setEnabled(true);
            tvGivelike.setTextColor(getContext().getResources().getColor(R.color.msg_tag_recommand));
            drawable = getContext().getResources().getDrawable(R.drawable.ic_thumb_up);
        }
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        tvGivelike.setCompoundDrawables(drawable, null, null, null);
    }

    public void enableGivelike(boolean isenable) {
        tvGivelike.setEnabled(isenable);
    }

    private static void loadImage(Context context, ImageView imageView, String headImage) {
        if (TextUtils.isEmpty(headImage)) {
            return;
        }
        /*ImageLoader.ImageListener listener = ImageLoader.getImageListener(imageView, R.drawable.ic_temporary_image, R.drawable.ic_broken_image);
        ImageLoader imageLoader = new ImageLoader(Volley.newRequestQueue(context), CacheManager.getBitmapLruCache());
        imageLoader.get(headImage, listener);*/
        PicassoUtil.displayImageComUrl(headImage, imageView);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_switch:
                nextDisplayAnimation();
                break;
            case R.id.tv_givelike:
                if (listener != null) {
                    listener.onClick(v);
                }
                break;
            case R.id.iv_otherans:
                openBigimgActivity(othermatesAnsData.getDisplayAns().getAnswerUrl());
                break;
            default:
                break;
        }
    }

    private void openBigimgActivity(String url) {
        /*Intent intent = new Intent(getContext(), ClassmateAnsImageActivity.class);
        intent.putExtra("imgurl", url);
        getContext().startActivity(intent);*/
        SinglePicturePreviewActivity.startSinglePicturePreviewActivity(getContext(), url, "","");
    }

    public void showBottomView(boolean isshow) {
        if (isshow) {
            llBottomTitleContainer.setVisibility(VISIBLE);
        } else {
            llBottomTitleContainer.setVisibility(GONE);
        }
    }

    public void enableNextAnsView(boolean isshow) {
        tv_switch.setEnabled(isshow);
        if (isshow) {
            tv_switch.setTextColor(getResources().getColor(R.color.msg_tag_recommand));
        } else {
            tv_switch.setTextColor(getContext().getResources().getColor(R.color.give_five_text_color_disabled));
        }
    }

    public OnClickListener getListener() {
        return listener;
    }

    public void setListener(OnClickListener listener) {
        this.listener = listener;
    }

    private void nextDisplayAnimation() {

        AlphaAnimation alphaAnimation = new AlphaAnimation(1f, 0f);
        /** 设置缩放动画 */
        ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, .1f, 1f, .1f, Animation.RELATIVE_TO_SELF, 0.f, Animation.RELATIVE_TO_SELF, 0f);

        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(alphaAnimation);
        animationSet.addAnimation(scaleAnimation);
        animationSet.setDuration(200);//设置动画持续时间
        animationSet.setFillAfter(true);
        scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                nextDisplay();
                secondAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        ((View) getParent()).clearAnimation();
        ((View) getParent()).startAnimation(animationSet);
    }

    private void secondAnimation() {
        AlphaAnimation alphaAnimation = new AlphaAnimation(.0f, 1f);
        /** 设置缩放动画 */
        ScaleAnimation scaleAnimation = new ScaleAnimation(0.1f, 1f, 0.1f, 1f, Animation.RELATIVE_TO_SELF, 0.f, Animation.RELATIVE_TO_SELF, 0f);
        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(alphaAnimation);
        animationSet.addAnimation(scaleAnimation);
        animationSet.setDuration(300);//设置动画持续时间
        animationSet.setFillAfter(true);
        animationSet.setInterpolator(new DecelerateInterpolator());
        ((View) getParent()).clearAnimation();
        ((View) getParent()).startAnimation(animationSet);
    }

    private void nextDisplay() {
        othermatesAnsData.nextDisplay();
        refreshDisplay();
    }
}
