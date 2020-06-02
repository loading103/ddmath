package com.tsinghuabigdata.edu.ddmath.module.robotqa;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;

import com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.QuestionInfo;


/**
 * Created by 28205 on 2016/9/14.
 */
public interface IRobotQaActivityView {
    Context getContext();

    void toast(String s);
    public void refreshChatview();

    String getQuestionId();
    String getExamId();
    void setQuestionStem(QuestionInfo vo, boolean success);
    boolean isRobotSimilarTopicEnable();
    void  setIsRobotSimilarTopicEnable(boolean b);
    boolean isDoRight();
    boolean isDoSubjectBefore();

    RecyclerView getRecycleView();
}
