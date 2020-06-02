package com.tsinghuabigdata.edu.ddmath.bean;

import android.text.TextUtils;

import com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.QuestionInfo;

import java.io.Serializable;
import java.util.List;

public class ChatSubject implements Serializable {

    private static final long serialVersionUID = -7735057762050430606L;
    private int displayIndex;

    private String[] messages;
    private List<Topic> mTopicList;

    private List<QuestionInfo> mQuestionList;

    public ChatSubject(String[] messages) {
        this.messages = messages;
    }

    public ChatSubject(List<Topic> topicList) {
        mTopicList = topicList;
        messages = new String[topicList.size()];
        for (int i = 0 ; i < topicList.size(); i++){
            this.messages[i] = topicList.get(i).getContent().replaceAll("#%#","\n").replaceAll("<img>","");
        }
    }

    public ChatSubject(List<QuestionInfo> questionList,boolean b) {
        mQuestionList = questionList;
        messages = new String[questionList.size()];
        for (int i = 0 ; i < questionList.size(); i++){
            String stem = questionList.get(i).getStem();
            if (!TextUtils.isEmpty(stem)){
                this.messages[i] = stem.replaceAll("#%#","\n").replaceAll("<img>","");
            }
        }
    }

    public int getDisplayIndex() {
        return displayIndex;
    }

    public void setDisplayIndex(int displayIndex) {
        this.displayIndex = displayIndex;
    }

    public String[] getMessages() {
        return messages;
    }

    public void setMessages(String[] messages) {
        this.messages = messages;
    }

    public String getDisplayMessage() {
        if (messages == null || messages.length == 0){
            return null;
        }
        return messages[displayIndex];
    }
    public String getDisplayQuestionId(){
        if (mQuestionList != null && mQuestionList.size() > 0){
            return mQuestionList.get(displayIndex).getQuestionId();
        }
        if (mTopicList == null || mTopicList.size() == 0){
            return null;
        }
        return mTopicList.get(displayIndex).getQuestionId();
    }

    public QuestionInfo getDisplayQuestionInfo() {
        if (mQuestionList == null || mQuestionList.size() == 0){
            return null;
        }
        return mQuestionList.get(displayIndex);
    }

    public Topic getDisplayTopic(){
        if (mTopicList == null || mTopicList.size() == 0){
            return null;
        }
        return mTopicList.get(displayIndex);
    }
    public void nextDisplay() {
        this.displayIndex ++;
        if (this.displayIndex == messages.length) {
            this.displayIndex = 0;
        }
    }

    @Override
    public ChatSubject clone() {
        ChatSubject chatSubject = new ChatSubject(messages);
        chatSubject.setDisplayIndex(displayIndex);
        return chatSubject;
    }
}
