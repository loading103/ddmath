package com.tsinghuabigdata.edu.ddmath.module.robotqa;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import androidx.recyclerview.widget.RecyclerView;

import com.tsinghuabigdata.edu.ddmath.MVPModel.RequestListener;
import com.tsinghuabigdata.edu.ddmath.MVPModel.RobotQaModel;
import com.tsinghuabigdata.edu.ddmath.bean.ChatMessage;
import com.tsinghuabigdata.edu.ddmath.bean.ChatSubject;
import com.tsinghuabigdata.edu.ddmath.bean.MyTutorClassInfo;
import com.tsinghuabigdata.edu.ddmath.bean.OtherStuAns;
import com.tsinghuabigdata.edu.ddmath.bean.QAAnsInfo;
import com.tsinghuabigdata.edu.ddmath.bean.Topic;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.constant.ErrTag;
import com.tsinghuabigdata.edu.ddmath.constant.RobotQaConst;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.QuestionInfo;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.AppLog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 28205 on 2016/9/2.
 */
public class RobotQaPresent {
    private static final String TAG               = "RobotQaPresent";
    public static final  int    REFRESH_CHAT_VIEW = 1200;

    private IRobotQaActivityView robotQaActivityView;
    private RobotQaModel         robotQaModel;
    private MsgNodeUtils         msgNodeUtils;

    private List<Runnable> errorTasks = new ArrayList<>();
    List<ChatMessage> chatMessages        = new ArrayList<ChatMessage>();
    List<ChatMessage> chatQustionMessages = new ArrayList<ChatMessage>();
    private List<ChatMessage> allChatMessages = new ArrayList<ChatMessage>();

    private int     commentNodeAddTimes;
    private boolean isNoErrorReason;
    private boolean isHadLookOtherAns;
    //做题前的答疑请求次数
    private int     doSubjectBeforeTaskTimes;
    //第一次查找错误原因
    private boolean isCheckErrReasonHadClicked;

    String studentId;
    String classId;
    String accessToken;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case REFRESH_CHAT_VIEW:
                    robotQaActivityView.refreshChatview();
                    break;
                default:
                    break;
            }

        }
    };

    public RobotQaPresent(IRobotQaActivityView robotQaView) {
        this.robotQaActivityView = robotQaView;
        robotQaModel = new RobotQaModel();
        msgNodeUtils = new MsgNodeUtils(this);

        studentId = AccountUtils.getUserdetailInfo().getStudentId();
        accessToken = AccountUtils.getLoginUser().getAccessToken();
        MyTutorClassInfo classInfo = AccountUtils.getCurrentClassInfo();
        if (classInfo != null)
            classId = classInfo.getClassId();
        else
            classId = "unknowclass_errorquestionbook";
    }

    public synchronized void addChatMsgs(ChatMessage chatMessage, boolean isRefresh) {
        chatMessages.add(chatMessage);
        if (isRefresh) {
            refreshChatView();
        }
    }

    public synchronized void addQuestionChatMsgs(ChatMessage chatMessage, boolean isRefresh) {
        chatQustionMessages.add(chatMessage);
        if (isRefresh) {
            refreshChatView();
        }
    }

    /**
     * 取得做题次数
     */
    public void getQuestionDotimes(final String questionId) {
        Runnable task = new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run: getQuestDotimes + questionId " + questionId);
                robotQaModel.getQuestDotimes(accessToken, studentId, questionId, new RequestListener<Integer>() {
                    @Override
                    public void onSuccess(Integer res) {
                        //刷新做错时的初始对话
                        queryChatMsgs(res, false, true);
                        RobotQaTaskManager.getInstance().pop();
                    }

                    @Override
                    public void onFail(HttpResponse reponse, Exception e) {
                        queryChatMsgs(1, false, true);
                        RobotQaTaskManager.getInstance().pop();
                    }
                });
            }
        };
        RobotQaTaskManager.getInstance().add(task);
        RobotQaTaskManager.getInstance().doAgain();
    }

    /**
     * 取得相似题(13.7开始弃用)
     */
    public void getOldAlikeQuestion(final String questionId, final String diff, final boolean isAddResel) {
        Runnable task = new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run: getAlikeQuestion + questionId " + questionId + "diff " + diff);
                robotQaModel.getAlikeQuestion(accessToken, questionId, diff, new RequestListener<List<Topic>>() {
                    @Override
                    public void onSuccess(List<Topic> res) {
                        //取得相似题后，首先去除提示用户等待的消息
                        removeHoldonMsg();
                        //加入题干消息
                        constructSubjectNode(res, isAddResel);
                        //执行成功后移除任务
                        RobotQaTaskManager.getInstance().pop();
                    }

                    @Override
                    public void onFail(HttpResponse reponse, Exception e) {
                        removeHoldonMsg();
                        chatMessages.add(msgNodeUtils.getLoadFailMsg());
                        chatQustionMessages.add(msgNodeUtils.getTryAgainMsg());
                        refreshChatView();
                    }
                });
            }
        };
        RobotQaTaskManager.getInstance().add(task);
        RobotQaTaskManager.getInstance().doAgain();
    }

    /**
     * 取得相似题(13.7开始使用)
     */
    public void getNewAlikeQuestion(final String questionId, final String diff, final boolean isAddResel) {
        Runnable task = new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run: getAlikeQuestion + questionId " + questionId + "diff " + diff);
                robotQaModel.getNewAlikeQuestion(accessToken, questionId, studentId, new RequestListener<List<QuestionInfo>>() {
                    @Override
                    public void onSuccess(List<QuestionInfo> res) {
                        //取得相似题后，首先去除提示用户等待的消息
                        removeHoldonMsg();
                        //加入题干消息
                        constructNewSubjectNode(res, isAddResel);
                        //执行成功后移除任务
                        RobotQaTaskManager.getInstance().pop();
                    }

                    @Override
                    public void onFail(HttpResponse reponse, Exception e) {
                        removeHoldonMsg();
                        chatMessages.add(msgNodeUtils.getLoadFailMsg());
                        chatQustionMessages.add(msgNodeUtils.getTryAgainMsg());
                        refreshChatView();
                    }
                });
            }
        };
        RobotQaTaskManager.getInstance().add(task);
        RobotQaTaskManager.getInstance().doAgain();
    }

    private void constructSubjectNode(List<Topic> res, boolean isAddReselect) {
        robotQaActivityView.setIsRobotSimilarTopicEnable(false);
        ChatMessage addNode;
        if (res == null || res.size() == 0) {
            robotQaActivityView.setIsRobotSimilarTopicEnable(false);
            addNode = ChatMessage.buildComeinMsg(ChatMessage.ContentType.TEXT);
            addNode.setTextContent(RobotQaConst.NO_ALIKE_TOPIC_HINTS);
            addNode.setRobotEmotionType(ChatMessage.RobotEmotionType.DIZZY);
        } else {
            addNode = ChatMessage.buildComeinMsg(ChatMessage.ContentType.SUBJECT);
            //大于2题，出现下一题
            if (res.size() >= 2) {
                addNode.setHaveNextTopic(true);
            }
            addNode.setHaveTopicDetail(true);
            addNode.setChatSubject(new ChatSubject(res));
        }
        addChatMsgs(addNode, true);

        //没有相似题，包含做对，做错,做题前的情况
        //判断是否添加 做对时的可见问题块，重新选择错误原因块，重新选择问题
        //做对后，相似题是否为空，都要添加可选问题块（瞄一眼同学的答案，看看相似题，结束本次对话）
        //做错后，相似题是否为空，都要添加重新选择错误原因块（瞄一眼同学的答案，看看相似题，结束本次对话等）
        //做题前，相似题是否为空，都要添加重新选择问题（重新选择问题，结束本次对话）
        if (robotQaActivityView.isDoRight()) {
            //做对
            addDoRightQuestions(robotQaActivityView.isRobotSimilarTopicEnable(), isHadLookOtherAns);
        } else {
            //做错
            addReselectErrorReasonQuestions(robotQaActivityView.isRobotSimilarTopicEnable(), isNoErrorReason(), isHadLookOtherAns);
        }
        //做题前
        if (robotQaActivityView.isDoSubjectBefore()) {
            addReselecQuestionsDoBefore();
        }
    }

    private void constructNewSubjectNode(List<QuestionInfo> res, boolean isAddReselect) {
        robotQaActivityView.setIsRobotSimilarTopicEnable(false);
        ChatMessage addNode;
        if (res == null || res.size() == 0) {
            robotQaActivityView.setIsRobotSimilarTopicEnable(false);
            addNode = ChatMessage.buildComeinMsg(ChatMessage.ContentType.TEXT);
            addNode.setTextContent(RobotQaConst.NO_ALIKE_TOPIC_HINTS);
            addNode.setRobotEmotionType(ChatMessage.RobotEmotionType.DIZZY);
        } else {
            addNode = ChatMessage.buildComeinMsg(ChatMessage.ContentType.SUBJECT);
            //大于2题，出现下一题
            if (res.size() >= 2) {
                addNode.setHaveNextTopic(true);
            }
            addNode.setHaveTopicDetail(true);
            addNode.setChatSubject(new ChatSubject(res, false));
        }
        addChatMsgs(addNode, true);

        //没有相似题，包含做对，做错,做题前的情况
        //判断是否添加 做对时的可见问题块，重新选择错误原因块，重新选择问题
        //做对后，相似题是否为空，都要添加可选问题块（瞄一眼同学的答案，看看相似题，结束本次对话）
        //做错后，相似题是否为空，都要添加重新选择错误原因块（瞄一眼同学的答案，看看相似题，结束本次对话等）
        //做题前，相似题是否为空，都要添加重新选择问题（重新选择问题，结束本次对话）
        if (robotQaActivityView.isDoRight()) {
            //做对
            addDoRightQuestions(robotQaActivityView.isRobotSimilarTopicEnable(), isHadLookOtherAns);
        } else {
            //做错
            addReselectErrorReasonQuestions(robotQaActivityView.isRobotSimilarTopicEnable(), isNoErrorReason(), isHadLookOtherAns);
        }
        //做题前
        if (robotQaActivityView.isDoSubjectBefore()) {
            addReselecQuestionsDoBefore();
        }
    }

    /**
     * 查找错误原因
     */
    public void getErrorReason(final String examId, final String questionId, final String checkWrongReason) {
        Runnable task = new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run: getErrorReason + questionId " + questionId + "examid " + examId);
                robotQaModel.getErrorReason(accessToken, studentId, examId, questionId, new RequestListener<List<QAAnsInfo>>() {
                    @Override
                    public void onSuccess(List<QAAnsInfo> res) {
                        //取得错误原因后，首先去除提示用户等待的消息
                        if (checkWrongReason.equals(RobotQaConst.CHECK_WRONG_REASON)) {
                            isCheckErrReasonHadClicked = true;
                        }
                        removeHoldonMsg();
                        //查看错误原因的下一条消息
                        ChatMessage tempmsg = ChatMessage.buildComeinMsg(ChatMessage.ContentType.TEXT);
                        tempmsg.setTextContent(RobotQaConst.DONT_WORRY_SELECT_WRONG_REASON);
                        //完成后再加
                        addChatMsgs(tempmsg, true);
                        addQuestionNodes(res, false);
                        refreshChatView();
                        //执行成功后移除任务
                        RobotQaTaskManager.getInstance().pop();
                    }

                    @Override
                    public void onFail(HttpResponse response, Exception e) {
                        removeHoldonMsg();
                        chatMessages.add(msgNodeUtils.getLoadFailMsg());
                        chatQustionMessages.add(msgNodeUtils.getTryAgainMsg());
                        refreshChatView();
                    }
                });
            }
        };
        RobotQaTaskManager.getInstance().add(task);
        RobotQaTaskManager.getInstance().doAgain();
    }

    /**
     * 瞄一眼同学的答案
     */
    public void getOtherClassmateAns(final String examId, final String questionId) {
/*        studentId = "STU5E4A01E6598C4496B9FDADD19C1E398A";
        examId = "EXERTB9DAD00C67914810B0A44A9C072C78D3";
        questionId = "01FE3DD9CE5D4A95B5F80BCA3C67FA5D";*/

        Runnable task = new Runnable() {
            @Override
            public void run() {
                robotQaModel.getOtherStuAns(accessToken, studentId, examId, questionId, new RequestListener<OtherStuAns>() {
                    @Override
                    public void onSuccess(OtherStuAns res) {
                        RobotQaTaskManager.getInstance().pop();
                        isHadLookOtherAns = true;
                        removeHoldonMsg();
                        handleOtherAns(res);
                        if (robotQaActivityView.isDoRight()) {
                            addDoRightQuestions(robotQaActivityView.isRobotSimilarTopicEnable(), isHadLookOtherAns);
                        } else {
                            addReselectErrorReasonQuestions(robotQaActivityView.isRobotSimilarTopicEnable(), isNoErrorReason(), isHadLookOtherAns);
                        }
                    }

                    @Override
                    public void onFail(HttpResponse reponse, Exception e) {
                        removeHoldonMsg();
                        chatMessages.add(msgNodeUtils.getLoadFailMsg());
                        chatQustionMessages.add(msgNodeUtils.getTryAgainMsg());
                        refreshChatView();
                    }
                });
            }
        };
        RobotQaTaskManager.getInstance().add(task);
        RobotQaTaskManager.getInstance().doAgain();
    }

    /**
     * 做题前的答疑
     */
    public void getQAAnsDosubjectBefore(final String questionId) {
        Runnable task = new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run: queryQAAnsDoBefore + questionId " + questionId);
                robotQaModel.queryQAAnsDoBefore(accessToken, studentId, questionId, new RequestListener<List<QAAnsInfo>>() {
                    @Override
                    public void onSuccess(List<QAAnsInfo> res) {
                        doSubjectBeforeTaskTimes++;
                        //取得做题前答疑结果，首先去除提示用户等待的消息
                        removeHoldonMsg();
                        if (doSubjectBeforeTaskTimes > 1) {
                            //查看错误原因的下一条消息
                            ChatMessage tempmsg = ChatMessage.buildComeinMsg(ChatMessage.ContentType.TEXT);
                            tempmsg.setTextContent(RobotQaConst.DO_YOU_HAVE_ANY_QUESTION);
                            addChatMsgs(tempmsg, true);
                        }
                        addQuestionNodes(res, true);
                        refreshChatView();
                        //执行成功后移除任务
                        RobotQaTaskManager.getInstance().pop();
                    }

                    @Override
                    public void onFail(HttpResponse response, Exception e) {
                        removeHoldonMsg();
                        //第一次请求
                        if (doSubjectBeforeTaskTimes == 0) {
                            doSubjectBeforeTaskTimes++;
                            //失败后，加相似题
                            ChatMessage similarSubject = ChatMessage.buildOutMsg(ChatMessage.ContentType.QUESTION);
                            similarSubject.setTextContent(RobotQaConst.GO_LOOK_SIMILAR_TOPIC);
                            similarSubject.setAccessData(true);
                            addQuestionChatMsgs(similarSubject, true);
                        } else {
                            chatMessages.add(msgNodeUtils.getLoadFailMsg());
                            chatQustionMessages.add(msgNodeUtils.getTryAgainMsg());
                            refreshChatView();
                        }
                    }
                });
            }
        };
        RobotQaTaskManager.getInstance().add(task);
        RobotQaTaskManager.getInstance().doAgain();
    }

    public void getQuestionInfo(final String questionId) {
        Runnable task = new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run: getQuestionInfo + questionId " + questionId);
                robotQaModel.getQuestionInfo(questionId, new RequestListener<QuestionInfo>() {
                    @Override
                    public void onSuccess(QuestionInfo res) {
                        robotQaActivityView.setQuestionStem(res, true);
                    }

                    @Override
                    public void onFail(HttpResponse response, Exception e) {
                        robotQaActivityView.setQuestionStem(null, false);
                    }
                });
            }
        };
        new Thread(task).start();
    }

    public void askRobot(final String questionId) {
        Runnable task = new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run: getQuestionInfo + questionId " + questionId);
                robotQaModel.askRobot(accessToken, questionId, studentId, classId, new RequestListener<Void>() {
                    @Override
                    public void onSuccess(Void res) {
                        AppLog.i("ask robot success");
                        RobotQaTaskManager.getInstance().pop();
                    }

                    @Override
                    public void onFail(HttpResponse response, Exception e) {
                        AppLog.w(ErrTag.TAG_HTTP, response.getErrorDescribe() + "|" + response.getInform() + "|" + response.getMessage());
                        RobotQaTaskManager.getInstance().pop();
                    }
                });
            }
        };
        RobotQaTaskManager.getInstance().add(task);
        RobotQaTaskManager.getInstance().doAgain();
    }

    //瞄一眼同学答案的处理过程
    private void handleOtherAns(OtherStuAns res) {
        //过滤掉空数据
        //        if (res != null && res.getOtherAnswers().size() > 0) {
        //            for (QuestionAns questionAns : res.getOtherAnswers()){
        //
        //            }
        //        }

        if (res == null || res.getOtherAnswers().size() == 0) {
            //为空，只有自己做对
            if (robotQaActivityView.isDoRight()) {
                addChatMsgs(msgNodeUtils.getOnlyUserDorightNode(), true);
            } else {
                addChatMsgs(msgNodeUtils.getNooneDorightNode(), true);
            }
        } else {
            //有同学答案
            ChatMessage classmateAnsNode = ChatMessage.buildComeinMsg(ChatMessage.ContentType.CLASSMATE_ANS);
            if (res.getOtherAnswers().size() == 1) {
                classmateAnsNode.setHaveNextAns(false);
            } else {
                classmateAnsNode.setHaveNextAns(true);
            }
            classmateAnsNode.setOthermatesAnsData(new OthermatesAnsData(res.getOtherAnswers()));
            addChatMsgs(classmateAnsNode, true);
        }
    }

    /**
     * @param likeType   s  ,  r  认知
     * @param questionId
     */
    public void giveLike(String likeType, String questionId, RequestListener listener) {
        if (likeType.equals("r") && questionId == null) {
            questionId = robotQaActivityView.getQuestionId();
        }
        robotQaModel.giveLike(accessToken, likeType, questionId, studentId, listener);
    }

    /**
     * 给同学的答案点赞
     */
    public void givelike2mateAns(String likeStudentId, RequestListener listener) {
        robotQaModel.givelikeToOtherStuAns(accessToken, studentId, robotQaActivityView.getQuestionId(),
                likeStudentId, robotQaActivityView.getExamId(), listener);
    }

    public void commentRobotService(String score, String scoreType, String questionId, RequestListener listener) {
        robotQaModel.commentRobot(accessToken, score, scoreType, questionId, studentId, listener);
    }

    public String getQuestionId() {
        return robotQaActivityView.getQuestionId();
    }

    public void addReselectErrorReasonQuestions(boolean isSimilarTopicEnable, boolean isNoReason, boolean isHadLookClassmateAns) {
        clearQuestionMsgs();

        chatQustionMessages.addAll(msgNodeUtils.getReSelectErrReasonMsgs(isSimilarTopicEnable, isNoReason,
                isHadLookClassmateAns, isCheckErrReasonHadClicked));
        refreshChatView();
    }

    /**
     * 做对后，可以重新出现的问题按钮
     *
     * @param isSimilarTopicEnable
     */
    public void addDoRightQuestions(boolean isSimilarTopicEnable, boolean isHadLookClassmateAns) {
        clearQuestionMsgs();
        chatQustionMessages.addAll(msgNodeUtils.getDoRightQuestionMsgs(isSimilarTopicEnable, isHadLookClassmateAns));
        refreshChatView();
    }

    public void addReselecQuestionsDoBefore() {
        clearQuestionMsgs();
        //看看相似题，不在做题前的重新选择项里出现
        chatQustionMessages.addAll(msgNodeUtils.getReSelectQuestionBefore(false, isNoErrorReason));
        refreshChatView();
    }

    /**
     * 做题前的答疑查询，做错后的错误原因，都在该函数里处理
     *
     * @param res
     * @param isDoSubjectBefore
     */
    private void addQuestionNodes(List<QAAnsInfo> res, boolean isDoSubjectBefore) {
        clearQuestionMsgs();
        //过滤空内容
        List<QAAnsInfo> newQAAnsinsfos = new ArrayList<>();
        if (res != null) {
            for (QAAnsInfo obj : res) {
                if (TextUtils.isEmpty(obj.getContent())) {
                    continue;
                }
                newQAAnsinsfos.add(obj);
            }
        }

        if (res == null || newQAAnsinsfos.size() == 0) {
            isNoErrorReason = true;
            if (isDoSubjectBefore) {

            } else {
                removeDontSelectReasonMsg();
                //为空时，增加小豆不会的信息
                ChatMessage robotCannotDoNode = msgNodeUtils.getRobotCannotDoNode();
                addChatMsgs(robotCannotDoNode, true);
                addReselectErrorReasonQuestions(robotQaActivityView.isRobotSimilarTopicEnable(), true, isHadLookOtherAns);
                return;
            }
        }
        for (QAAnsInfo qaAnsInfo : newQAAnsinsfos) {
            ChatMessage newNode = ChatMessage.buildOutMsg(ChatMessage.ContentType.QUESTION);
            ChatMessage nextNode = ChatMessage.buildComeinMsg(ChatMessage.ContentType.TEXT);
            newNode.setTextContent(qaAnsInfo.getTitle());

            if (qaAnsInfo.getTitle().equals(RobotQaConst.LOOK_ROBOT_FOUND_REASON)) {
                //看看小豆找到的原因
                nextNode.setContentType(ChatMessage.ContentType.KNOWLEDGE_IMAGE);
                nextNode.setHaveComment(true);
                newNode.setKnowviewExpand(false);
                String datas = qaAnsInfo.getContent();
                String[] dataArry = datas.split("####%%####");
                List<String> ress = new ArrayList<>();
                for (int i = 0; i < dataArry.length; i++) {
                    if (!TextUtils.isEmpty(dataArry[i])) {
                        ress.add(new String(dataArry[i]));
                    }
                }
                nextNode.setKnowCongnitionData(ress);
                newNode.setNextChatMsg(nextNode);
            } else if (qaAnsInfo.getTitle().equals(RobotQaConst.DONT_UNDSTAND_TOPIC) ||
                    qaAnsInfo.getTitle().equals(RobotQaConst.UNABLE_ANALYSIS)) {
                //不理解题意中的结果包含有题干分析
                nextNode.setContentType(ChatMessage.ContentType.SUBJECT);
                nextNode.setHaveNextTopic(false);
                nextNode.setMathText(true);
                ChatSubject topics = new ChatSubject(new String[]{qaAnsInfo.getContent().replaceAll("#%#", "\n")});
                //                ChatSubject topics = new ChatSubject(new String[]{"audjjiij  jufiej 88 + $(2*x-1)^{2}$ + $3^{x}$=23444"});
                nextNode.setChatSubject(topics);
                nextNode.setHaveComment(false);
                nextNode.setHaveTopicDetail(false);
                newNode.setNextChatMsg(nextNode);
            } else {
                nextNode.setTextContent(qaAnsInfo.getContent().replaceAll("#%#", "\n"));
                newNode.setNextChatMsg(nextNode);
            }
            chatQustionMessages.add(newNode);
        }

        //做题前，原因是否为空，相似题要加进去
        if (isDoSubjectBefore && robotQaActivityView.isRobotSimilarTopicEnable()) {
            ChatMessage similarSubject = ChatMessage.buildOutMsg(ChatMessage.ContentType.QUESTION);
            similarSubject.setTextContent(RobotQaConst.LOOK_SIMILAR_TOPIC);
            similarSubject.setAccessData(true);
            addQuestionChatMsgs(similarSubject, true);
        }
    }

    public Context getContext() {
        return robotQaActivityView.getContext();
    }

    public RecyclerView getRecycleView() {
        return robotQaActivityView.getRecycleView();
    }

    public boolean isHaveCommentStarNode() {
        for (ChatMessage msg : allChatMessages) {
            if (msg.isStarComment()) {
                return true;
            }
        }
        return false;
    }

    public void initChatMsgDosubjectBefore(String questionId) {
        ChatMessage newNode = ChatMessage.buildComeinMsg(ChatMessage.ContentType.TEXT);
        newNode.setTextContent(msgNodeUtils.getUserName() + RobotQaConst.BEFORE_DO_ROBOT_HELLO);
        addChatMsgs(newNode, true);
        //        addChatMsgs(getHoldonMsg(), true);
        //请求做题前答疑
        getQAAnsDosubjectBefore(questionId);
    }

    private void removeHoldonMsg() {
        if (chatMessages.size() > 0) {
            ChatMessage holdMsg = chatMessages.get(chatMessages.size() - 1);
            if (holdMsg == null || holdMsg.getTextContent() == null) {
                return;
            }
            if (holdMsg.getTextContent().equals(RobotQaConst.HOLDON_HINTS)) {
                chatMessages.remove(chatMessages.size() - 1);
            }
        }
    }

    private void removeDontSelectReasonMsg() {
        if (chatMessages.size() > 0) {
            ChatMessage holdMsg = chatMessages.get(chatMessages.size() - 1);
            if (holdMsg == null) {
                return;
            }
            if (holdMsg.getTextContent().equals(RobotQaConst.DONT_WORRY_SELECT_WRONG_REASON)) {
                chatMessages.remove(chatMessages.size() - 1);
            }
        }
    }

    private void removeLoadFailMsg() {
        if (chatMessages.size() > 0) {
            ChatMessage holdMsg = chatMessages.get(chatMessages.size() - 1);
            if (holdMsg == null) {
                return;
            }
            if (holdMsg.getTextContent().equals(RobotQaConst.ROBOT_LOAD_FAIL)) {
                chatMessages.remove(chatMessages.size() - 1);
            }
        }
    }

    //refresh recycleview
    public void refreshChatView() {
        allChatMessages.clear();
        allChatMessages.addAll(chatMessages);
        allChatMessages.addAll(chatQustionMessages);
        //添加隐藏的结束节点
        allChatMessages.add(msgNodeUtils.getHideEndNode());
        mHandler.sendEmptyMessage(REFRESH_CHAT_VIEW);
    }

    public boolean isNoErrorReason() {
        return isNoErrorReason;
    }

    public boolean isHadLookOtherAns() {
        return isHadLookOtherAns;
    }

    public void toast(String s) {
        robotQaActivityView.toast(s);
    }

    public void clearQuestionMsgs() {
        chatQustionMessages.clear();
    }

    public void queryChatMsgs(int dotimes, boolean isdoRight, boolean isFirst) {
        if (isFirst) {
            chatMessages.addAll(msgNodeUtils.getFirstChatMsgs(dotimes, isdoRight));
            chatQustionMessages.addAll(msgNodeUtils.getFirstQuestMsgs(isdoRight));
        } else {

        }
        refreshChatView();
    }

    public void tryAgain() {
        RobotQaTaskManager.getInstance().doAgain();
    }

    public void switchLastNodeToOngoing(boolean isgoing) {
        if (!isgoing) {
            removeHoldonMsg();
            addChatMsgs(msgNodeUtils.getLoadFailMsg(), true);
        } else {
            removeLoadFailMsg();
            addChatMsgs(msgNodeUtils.getHoldonMsg(), true);
        }
    }

    public String getUsername() {
        return msgNodeUtils.getUserName();
    }

    public MsgNodeUtils getMsgNodeUtils() {
        return msgNodeUtils;
    }

    public List<ChatMessage> getAllChatMessages() {
        return allChatMessages;
    }

    public void setAllChatMessages(List<ChatMessage> allChatMessages) {
        this.allChatMessages = allChatMessages;
    }
}
