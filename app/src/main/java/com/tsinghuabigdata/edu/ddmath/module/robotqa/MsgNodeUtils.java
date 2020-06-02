package com.tsinghuabigdata.edu.ddmath.module.robotqa;

import android.text.TextUtils;


import com.tsinghuabigdata.edu.ddmath.bean.ChatMessage;
import com.tsinghuabigdata.edu.ddmath.bean.UserDetailinfo;
import com.tsinghuabigdata.edu.ddmath.constant.RobotQaConst;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by 28205 on 2016/11/18.
 */
public class MsgNodeUtils {
    private RobotQaPresent robotQaPresent;

    public MsgNodeUtils(RobotQaPresent present) {
        robotQaPresent = present;
    }

    public List<ChatMessage> getFirstQuestMsgs(boolean isDoRight) {
        ArrayList<ChatMessage> temp = new ArrayList<>();

        String[] rightArr = new String[]{RobotQaConst.LOOK_OTHER_CLASSMATE_ANSWER, RobotQaConst.LOOK_SIMILAR_TOPIC};
        String[] wrongArr = new String[]{RobotQaConst.CHECK_WRONG_REASON, RobotQaConst.LOOK_OTHER_CLASSMATE_ANSWER,
                RobotQaConst.LOOK_SIMILAR_TOPIC};
        String[] dstArr;

        if (isDoRight) {
            dstArr = rightArr.clone();
        } else {
            dstArr = wrongArr.clone();
        }
        for (int i = 0; i < dstArr.length; i++) {
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setType(ChatMessage.TYPE_OUT);
            chatMessage.setContentType(ChatMessage.ContentType.QUESTION);
            chatMessage.setTextContent(dstArr[i]);

            //如果点击后需要访问服务端的，需设置
            switch (chatMessage.getTextContent()) {
                case RobotQaConst.LOOK_OTHER_CLASSMATE_ANSWER:
                case RobotQaConst.CHECK_WRONG_REASON:
                case RobotQaConst.LOOK_SIMILAR_TOPIC:
                    //好的，去看看
                    chatMessage.setAccessData(true);
                    break;
                default:
                    break;
            }
            temp.add(chatMessage);
        }
        return temp;
    }

    public List<ChatMessage> getFirstChatMsgs(int dotimes, boolean isDoRight) {
        ArrayList<ChatMessage> temp = new ArrayList<>();
        String mstContent;
        String username = getUserName();
        if (isDoRight) {
            mstContent = username + RobotQaConst.DO_RIGHT_ROBOT_HELLO;
        } else {
            if (dotimes <= 1) {
                mstContent = username + RobotQaConst.DO_WRONG_ROBOT_HELLO;
            } else {
                mstContent = username + "同学，你好，我是小豆，很高兴见到你(￣▽￣)~*这道题你已经做过 " + dotimes +
                        " 次了，这次你没有做对哦T^T让我们一起来攻克它吧～";
            }
        }
        ChatMessage chatMessage = ChatMessage.buildComeinMsg(ChatMessage.ContentType.TEXT);
        chatMessage.setTextContent(mstContent);
        chatMessage.setRobotEmotionType(ChatMessage.RobotEmotionType.WELCOME);
        temp.add(chatMessage);
        return temp;
    }

    public String getUserName() {
        //优先取学生真实姓名，如果没有就取昵称，如果再没有就取账号名。
        UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
        if (detailinfo != null) {
            if (!TextUtils.isEmpty(detailinfo.getReallyName())){
                return detailinfo.getReallyName();
            }
            if (!TextUtils.isEmpty(detailinfo.getNickName())){
                return detailinfo.getNickName();
            }
        }
        return "";
        /*String name = AccountUtils.getLoginUser().getReallyName();
        if (TextUtils.isEmpty(name)) {
            name = AccountUtils.getLoginUser().getLoginName();
        }
        if (name == null) {
            name = "";
        }
        return name;*/
    }

    /**
     * 重新选择错误原因对话内容组
     *
     * @return
     */
    public List<ChatMessage> getReSelectErrReasonMsgs(boolean isSimilarTopicEnable, boolean isNoErrorReason, boolean isHadLookClassmateAns, boolean isCheckErrReasonHadClicked) {
        ArrayList<ChatMessage> temp = new ArrayList<>();

        //没有错误原因
        if (!isNoErrorReason) {
            ChatMessage chatMessage = ChatMessage.buildOutMsg(ChatMessage.ContentType.QUESTION);
            if (isCheckErrReasonHadClicked) {
                chatMessage.setTextContent(RobotQaConst.RESELECT_WRONG_REASON);
            } else {
                chatMessage.setTextContent(RobotQaConst.CHECK_WRONG_REASON);
            }
            chatMessage.setAccessData(true);
            temp.add(chatMessage);
        }
        //还没有瞄同学答案
        if (!isHadLookClassmateAns) {
            temp.add(getLookOtherAnsQestionNode());
        }
        if (isSimilarTopicEnable) {
            ChatMessage chatMessage = getSimilarTopicQestionNode();
            temp.add(chatMessage);
        }

        //做错后的结束对话节点
        temp.add(getEndChatMsg(false));
        return temp;
    }

    /**
     * 做题前：重新选择问题对话内容组
     *
     * @return
     */
    public List<ChatMessage> getReSelectQuestionBefore(boolean isSimilarTopicEnable, boolean isNoQuestion) {
        ArrayList<ChatMessage> temp = new ArrayList<>();

        if (isSimilarTopicEnable) {
            ChatMessage chatMessage = getSimilarTopicQestionNode();
            temp.add(chatMessage);
        }
        //没有问题
        if (!isNoQuestion) {
            ChatMessage chatMessage = ChatMessage.buildOutMsg(ChatMessage.ContentType.QUESTION);
            chatMessage.setTextContent(RobotQaConst.RESELECT_QUESTION);
            chatMessage.setAccessData(true);
            temp.add(chatMessage);
        }

        //与做对后一样
        temp.add(getEndChatMsg(true));
        return temp;
    }

    /**
     * 做对后，可以选的问题按钮
     *
     * @param isSimilarTopicEnable
     * @param isHadLookClassmateAns
     * @return
     */
    public List<ChatMessage> getDoRightQuestionMsgs(boolean isSimilarTopicEnable, boolean isHadLookClassmateAns) {
        ArrayList<ChatMessage> temp = new ArrayList<>();

        if (isSimilarTopicEnable) {
            ChatMessage chatMessage = getSimilarTopicQestionNode();
            temp.add(chatMessage);
        }
        //还没看过同学的答案
        if (!isHadLookClassmateAns) {
            ChatMessage chatMessage = getLookOtherAnsQestionNode();
            temp.add(chatMessage);
        }

        temp.add(getEndChatMsg(true));
        return temp;
    }

    //只有自己做对节点
    public ChatMessage getOnlyUserDorightNode() {
        ChatMessage onlyUserDorightNode = ChatMessage.buildComeinMsg(ChatMessage.ContentType.TEXT);
        onlyUserDorightNode.setTextContent(RobotQaConst.ONLY_USER_DORIGHT);
        onlyUserDorightNode.setRobotEmotionType(ChatMessage.RobotEmotionType.DIZZY);
        return onlyUserDorightNode;
    }

    //没有人做对节点
    public ChatMessage getNooneDorightNode() {
        ChatMessage nooneDoNode = ChatMessage.buildComeinMsg(ChatMessage.ContentType.TEXT);
        nooneDoNode.setTextContent(RobotQaConst.NO_ONE_DORIGHT);
        nooneDoNode.setRobotEmotionType(ChatMessage.RobotEmotionType.DIZZY);
        return nooneDoNode;
    }

    //小豆也不会节点
    public ChatMessage getRobotCannotDoNode() {
        ChatMessage robotCannotDoNode = ChatMessage.buildComeinMsg(ChatMessage.ContentType.TEXT);
        robotCannotDoNode.setTextContent(RobotQaConst.ROBOT_CANNOT_DO);
        robotCannotDoNode.setRobotEmotionType(ChatMessage.RobotEmotionType.DIZZY);
        return robotCannotDoNode;
    }

    //看看相似题问题节点
    public ChatMessage getSimilarTopicQestionNode() {
        ChatMessage chatMessage = ChatMessage.buildOutMsg(ChatMessage.ContentType.QUESTION);
        chatMessage.setTextContent(RobotQaConst.LOOK_SIMILAR_TOPIC);
        chatMessage.setAccessData(true);
        return chatMessage;
    }

    //看其他同学的问题节点
    public ChatMessage getLookOtherAnsQestionNode() {
        ChatMessage chatMessage = ChatMessage.buildOutMsg(ChatMessage.ContentType.QUESTION);
        chatMessage.setTextContent(RobotQaConst.LOOK_OTHER_CLASSMATE_ANSWER);
        chatMessage.setAccessData(true);
        return chatMessage;
    }

    //加载失败节点
    public ChatMessage getLoadFailMsg() {
        ChatMessage failmsg = ChatMessage.buildComeinMsg(ChatMessage.ContentType.TEXT);
        failmsg.setTextContent(RobotQaConst.ROBOT_LOAD_FAIL);
        failmsg.setRobotEmotionType(ChatMessage.RobotEmotionType.DIZZY);
        return failmsg;
    }

    //隐藏的结束节点，用于标志对话的末尾位置
    public ChatMessage getHideEndNode() {
        ChatMessage failmsg = ChatMessage.buildComeinHideMsg();
        return failmsg;
    }

    //再试一次问题节点
    public ChatMessage getTryAgainMsg() {
        ChatMessage tryagain = ChatMessage.buildOutMsg(ChatMessage.ContentType.QUESTION);
        tryagain.setAccessData(true);
        tryagain.setTextContent(RobotQaConst.ROBOT_TRY_AGAIN);
        return tryagain;
    }

    //等待提示节点
    public ChatMessage getHoldonMsg() {
        ChatMessage addNode = ChatMessage.buildComeinMsg(ChatMessage.ContentType.TEXT);
        addNode.setTextContent(RobotQaConst.HOLDON_HINTS);
        addNode.setRobotEmotionType(ChatMessage.RobotEmotionType.SMILE);
        return addNode;
    }

    //结束对话问题节点
    public ChatMessage getEndChatMsg(boolean isDoright) {
        ChatMessage endNode = ChatMessage.buildOutMsg(ChatMessage.ContentType.QUESTION);
        endNode.setTextContent(RobotQaConst.END_CURR_CHAT);

        //点击后直接显示下一条对话的，需指定下一条消息内容
        //结束本次对话的,下一条为评价, 下一个节点
        ChatMessage tempmsg = endNode.buildMsg(ChatMessage.TYPE_COMEIN, ChatMessage.ContentType.TEXT);
        if (isDoright) {
            tempmsg.setTextContent(getUserName() + RobotQaConst.ENCOURAGE_HINTS);
        } else {
            tempmsg.setTextContent(getUserName() + RobotQaConst.COMMENT_AFTER_WRONG_HINTS);
        }
        tempmsg.setRobotEmotionType(ChatMessage.RobotEmotionType.EVAL);
        tempmsg.setNumStars(5);
        tempmsg.setStarComment(true); //星星类型的评价
        tempmsg.setHaveComment(true); //带评价功能

        endNode.setNextChatMsg(tempmsg);

        return endNode;
    }
}
