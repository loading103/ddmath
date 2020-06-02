package com.tsinghuabigdata.edu.ddmath.bean;


import com.tsinghuabigdata.edu.ddmath.module.robotqa.OthermatesAnsData;

import java.io.Serializable;
import java.util.List;

public class ChatMessage implements Serializable{

    //消息类型
    public static final int TYPE_MESSAGE = 0;
    public static final int TYPE_COMEIN = 1;
    public static final int TYPE_OUT = 2;
    public static final int TYPE_HIDENODE = 3;
    private static final long serialVersionUID = 1473260054806565031L;

    public static enum ContentType {
        TEXT, SUBJECT, KNOWLEDGE_IMAGE, QUESTION, CLASSMATE_ANS     //内容类型：文本，题干，认知误差节点, 问题， 同学答案节点
    }
    public static enum RobotEmotionType {
        WELCOME, DIZZY, ADMIRE, SMILE, EVAL            //打招呼，空数据/异常, 崇拜, 笑容,评价
    }

    private int mType;
    private ContentType contentType;
    private RobotEmotionType robotEmotionType;
    private String sender;
    private String textContent;
    private int imgResid;

    //是否有星星
    private boolean isStarComment;
    //是否有点赞
    private boolean isThumbup;
    private int thumbupNum;
    //是否已点赞
    private boolean isHadThumbup;
    //点赞文本
    private String thumbupText;
    //是否有下一题
    private boolean isHaveNextTopic;
    //是否有下一条同学答案
    private boolean isHaveNextAns;
    //是否有详情
    private boolean isHaveTopicDetail;
    //是否是带评论的对话消息
    private boolean isHaveComment;
    //是否星星评价成功
    private boolean isHadCommentStar;
    //星星数量
    private int numStars;
    //点击后是否需要调用后台接口
    private boolean isAccessData;
    //文本中的图片
    private boolean isHaveImgInText;

    //带公式的文本
    private boolean isMathText;
    //认知图是否已开, false : 没有展开
    private boolean isKnowviewExpand;

    private List<String> knowCongnitionData;

    private ChatSubject       chatSubject;
    private OthermatesAnsData othermatesAnsData;

    public ChatMessage() {
    }

    public int getType() {
        return mType;
    }

    public void setType(int mType) {
        this.mType = mType;
    }

    public ContentType getContentType() {
        return contentType;
    }

    public void setContentType(ContentType contentType) {
        this.contentType = contentType;
    }

    public RobotEmotionType getRobotEmotionType() {
        return robotEmotionType;
    }

    public void setRobotEmotionType(RobotEmotionType robotEmotionType) {
        this.robotEmotionType = robotEmotionType;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getTextContent() {
        return textContent;
    }

    public void setTextContent(String textContent) {
        this.textContent = textContent;
    }

    public int getImgResid() {
        return imgResid;
    }

    public void setImgResid(int imgResid) {
        this.imgResid = imgResid;
    }

    public boolean isStarComment() {
        return isStarComment;
    }

    public void setStarComment(boolean starComment) {
        isStarComment = starComment;
    }

    public boolean isThumbup() {
        return isThumbup;
    }

    public void setThumbup(boolean thumbup) {
        isThumbup = thumbup;
    }

    public boolean isHadThumbup() {
        return isHadThumbup;
    }

    public void setHadThumbup(boolean hadThumbup) {
        isHadThumbup = hadThumbup;
    }

    public int getThumbupNum() {
        return thumbupNum;
    }

    public void setThumbupNum(int thumbupNum) {
        this.thumbupNum = thumbupNum;
    }

    public boolean isHaveNextTopic() {
        return isHaveNextTopic;
    }

    public void setHaveNextTopic(boolean haveNextTopic) {
        isHaveNextTopic = haveNextTopic;
    }

    public boolean isHaveTopicDetail() {
        return isHaveTopicDetail;
    }

    public void setHaveTopicDetail(boolean haveTopicDetail) {
        isHaveTopicDetail = haveTopicDetail;
    }

    public int getNumStars() {
        return numStars;
    }

    public void setNumStars(int numStars) {
        this.numStars = numStars;
    }

    public boolean isHaveComment() {
        return isHaveComment;
    }

    public void setHaveComment(boolean haveComment) {
        isHaveComment = haveComment;
    }

    public boolean isHadCommentStar() {
        return isHadCommentStar;
    }

    public void setHadCommentStar(boolean hadCommentStar) {
        isHadCommentStar = hadCommentStar;
    }

    private ChatMessage nextChatMsg;

    public ChatMessage getNextChatMsg() {
        return nextChatMsg;
    }

    public boolean isAccessData() {
        return isAccessData;
    }

    public void setAccessData(boolean accessData) {
        isAccessData = accessData;
    }

    public void setNextChatMsg(ChatMessage nextChatMsg) {
        this.nextChatMsg = nextChatMsg;
    }

    public String getThumbupText() {
        return thumbupText;
    }

    public void setThumbupText(String thumbupText) {
        this.thumbupText = thumbupText;
    }

    public boolean isKnowviewExpand() {
        return isKnowviewExpand;
    }

    public void setKnowviewExpand(boolean knowviewExpand) {
        isKnowviewExpand = knowviewExpand;
    }

    public boolean isMathText() {
        return isMathText;
    }

    public void setMathText(boolean mathText) {
        isMathText = mathText;
    }

    public ChatSubject getChatSubject() {
        return chatSubject;
    }

    public void setChatSubject(ChatSubject chatSubject) {
        this.chatSubject = chatSubject;
    }

    public List<String> getKnowCongnitionData() {
        return knowCongnitionData;
    }

    public void setKnowCongnitionData(List<String> knowCongnitionData) {
        this.knowCongnitionData = knowCongnitionData;
    }

    public boolean isHaveImgInText() {
        return isHaveImgInText;
    }

    public void setHaveImgInText(boolean haveImgInText) {
        isHaveImgInText = haveImgInText;
    }

    public OthermatesAnsData getOthermatesAnsData() {
        return othermatesAnsData;
    }

    public void setOthermatesAnsData(OthermatesAnsData othermatesAnsData) {
        this.othermatesAnsData = othermatesAnsData;
    }

    public boolean isHaveNextAns() {
        return isHaveNextAns;
    }

    public void setHaveNextAns(boolean haveNextAns) {
        isHaveNextAns = haveNextAns;
    }

    public static ChatMessage buildComeinMsg(ContentType contentType) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setType(ChatMessage.TYPE_COMEIN);
        chatMessage.setContentType(contentType);
        return chatMessage;
    }

    public static ChatMessage buildComeinHideMsg() {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setType(ChatMessage.TYPE_HIDENODE);
        return chatMessage;
    }

    public static ChatMessage buildOutMsg(ContentType contentType) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setType(ChatMessage.TYPE_OUT);
        chatMessage.setContentType(contentType);
        return chatMessage;
    }

    @Override
    public ChatMessage clone() {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setType(mType);
        chatMessage.setContentType(contentType);
        chatMessage.setTextContent(textContent);
        chatMessage.setImgResid(imgResid);
        chatMessage.setSender(sender);
        chatMessage.setChatSubject(chatSubject != null ? chatSubject.clone() : null);
        return chatMessage;
    }

    public ChatMessage buildItselfTextMsg() {
        ChatMessage tempmsg = (ChatMessage) clone();
        tempmsg.setContentType(ChatMessage.ContentType.TEXT);
        return tempmsg;
    }

    public ChatMessage buildMsg() {
        ChatMessage tempmsg = (ChatMessage) clone();
        return tempmsg;
    }

    public ChatMessage buildMsg(ContentType contentType) {
        ChatMessage tempmsg = (ChatMessage) clone();
        tempmsg.setContentType(contentType);
        return tempmsg;
    }

    public ChatMessage buildMsg(int type) {
        ChatMessage tempmsg = (ChatMessage) clone();
        tempmsg.setType(type);
        return tempmsg;
    }

    public ChatMessage buildMsg(int type, ContentType contentType) {
        ChatMessage tempmsg = (ChatMessage) clone();
        tempmsg.setType(type);
        tempmsg.setContentType(contentType);
        return tempmsg;
    }
}
