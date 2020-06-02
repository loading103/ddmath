package com.tsinghuabigdata.edu.ddmath.module.robotqa;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.activity.CommonWebviewActivity;
import com.tsinghuabigdata.edu.ddmath.bean.ChatSubject;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.QuestionInfo;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.view.QuestionTextView;


/**
 * Created by HP on 2016/10/21.
 */
public class SubjectView extends LinearLayout implements View.OnClickListener {

    private ChatSubject      chatSubject;
    private QuestionTextView tv_subject_content;
    private TextView         tv_detail;
    private TextView         tv_switch;
    private RatingBar        topicDiffRatinbBar;
    private LinearLayout     llExtentContainer;
    private LinearLayout     llBottomTitleContainer;
    private OnClickListener  listener;

    private boolean isMathText;

    public SubjectView(Context context) {
        super(context);
        init();
    }

    public SubjectView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SubjectView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SubjectView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.listitem_message_comein_subject, this);
        tv_subject_content = (QuestionTextView) findViewById(R.id.tv_subject_content);
        tv_detail = (TextView) findViewById(R.id.tv_detail);
        tv_switch = (TextView) findViewById(R.id.tv_switch);
        llExtentContainer = (LinearLayout) findViewById(R.id.ll_extendContainer);
        llBottomTitleContainer = (LinearLayout) findViewById(R.id.ll_bottom_title);
        topicDiffRatinbBar = (RatingBar) findViewById(R.id.ratingBar_diffv);

        tv_subject_content.setLineSpacing(0.5f, 1.5f);
        tv_switch.setOnClickListener(this);
        tv_detail.setOnClickListener(this);
    }

    public void setMessages(ChatSubject chatSubject) {
        this.chatSubject = chatSubject;
        refreshDisplay();
    }

    private void refreshDisplay() {
        QuestionInfo questionInfo = chatSubject.getDisplayQuestionInfo();
        //纯公式文本和题干两大类型
        if (isMathText) {

        } else {
            //            int diff = Integer.valueOf(chatSubject.getDisplayTopic().getCoefficient());
            //            topicDiffRatinbBar.setRating(diff);
            if (questionInfo != null) {
                float diff = (float) Math.ceil(questionInfo.getDifficult() * 5);
                topicDiffRatinbBar.setRating(diff);
            } else if (chatSubject.getDisplayTopic() != null) {
                int diff = Integer.valueOf(chatSubject.getDisplayTopic().getCoefficient());
                topicDiffRatinbBar.setRating(diff);
            }
        }
        if (questionInfo != null) {
            tv_subject_content.setQuestionBook(questionInfo, true);
        } else if (!TextUtils.isEmpty(chatSubject.getDisplayMessage())) {
            tv_subject_content.setQuestionContent(chatSubject.getDisplayMessage());
        }

    }

    @Override
    public void onClick(View v) {
        if (v == tv_switch) {
            nextDisplayAnimation();
        } else if (v == tv_detail) {
            /*Map<String, String> restfulParams = new HashMap<>();
            restfulParams.put("studentID", AccountUtils.getUserdetailInfo().getStudentId());
            restfulParams.put("questionId", chatSubject.getDisplayQuestionId());
            String url = RestfulParamsUtils.buildUrl(AppRequestConst.WEB_ADDRESS + AppRequestConst.ROBOT_ALIKE_DETAIL, restfulParams);
            openWebByUrl(url, "查看详情");*/ //内嵌网页改成原生页面
            boolean allowAnswer = true;
            if (getContext() instanceof Activity) {
                RobotQaActivity robotQaActivity = (RobotQaActivity) getContext();
                allowAnswer = robotQaActivity.isAllowAnswer();
                //Log.i("sky", "allowAnswer=" + allowAnswer);
            }
            QuestionDetailActivity.startQuestionDetailActivity(getContext(), chatSubject.getDisplayQuestionId(), allowAnswer);
            if (listener != null) {
                listener.onClick(v);
            }
        }
    }

    private void openWebByUrl(String url, String title) {
        Intent intent = new Intent(getContext(), CommonWebviewActivity.class);
        intent.putExtra(CommonWebviewActivity.MSG_URL, url);
        intent.putExtra(CommonWebviewActivity.MSG_TITLE, title);
        getContext().startActivity(intent);
    }

    public void enableNextTopicView(boolean isshow) {
        tv_switch.setEnabled(isshow);
        if (isshow) {
            tv_switch.setTextColor(getResources().getColor(R.color.msg_tag_recommand));
        } else {
            tv_switch.setTextColor(getContext().getResources().getColor(R.color.give_five_text_color_disabled));
        }
    }

    public void showDetailView(boolean isshow) {
        if (isshow) {
            tv_detail.setVisibility(VISIBLE);
        } else {
            tv_detail.setVisibility(GONE);
        }
    }

    public void showExtendView(boolean isshow) {
        if (isshow) {
            llExtentContainer.setVisibility(VISIBLE);
        } else {
            llExtentContainer.setVisibility(GONE);
        }
    }

    public void showBottomView(boolean isshow) {
        if (isshow) {
            llBottomTitleContainer.setVisibility(VISIBLE);
        } else {
            llBottomTitleContainer.setVisibility(GONE);
        }
    }

    public boolean isMathText() {
        return isMathText;
    }

    public void setMathText(boolean mathText) {
        isMathText = mathText;
    }

    public void setSubjectMaxLines(int num) {
        tv_subject_content.setMaxLines(num);
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
        chatSubject.nextDisplay();
        refreshDisplay();
    }
}
