package com.tsinghuabigdata.edu.ddmath.module.robotqa;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.tsinghuabigdata.edu.ddmath.MVPModel.RequestListener;
import com.tsinghuabigdata.edu.ddmath.MVPModel.RobotQaModel;
import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.activity.RoboActivity;
import com.tsinghuabigdata.edu.ddmath.bean.ChatMessage;
import com.tsinghuabigdata.edu.ddmath.bean.MyTutorClassInfo;
import com.tsinghuabigdata.edu.ddmath.bean.ResultInfo;
import com.tsinghuabigdata.edu.ddmath.bean.UserDetailinfo;
import com.tsinghuabigdata.edu.ddmath.bean.WebchatAdapter;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.constant.RobotQaConst;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.AnswerPermission;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.QuestionInfo;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.view.QuestionTextView;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.view.WorkToolbar;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.AlertManager;
import com.tsinghuabigdata.edu.ddmath.util.AppLog;
import com.tsinghuabigdata.edu.ddmath.util.AppUtils;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.umeng.analytics.MobclickAgent;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

public class RobotQaActivity extends RoboActivity implements View.OnClickListener, RecycleViewItemListener, IRobotQaActivityView {

    @ViewInject(R.id.work_toolbar)
    private WorkToolbar workToolbar;
    @ViewInject(R.id.rv_qa)
    private RecyclerView     mQaRecycleview;
    @ViewInject(R.id.fl_stem)
    private FrameLayout      fl_stem;
    @ViewInject(R.id.sv_stem)
    private ScrollView       sv_stem;
    @ViewInject(R.id.tv_stem)
    private QuestionTextView mQuestionTextView;
    @ViewInject(R.id.tv_loadfail)
    private TextView         tvloadfail;

    private RecyclerView.Adapter mQaRvAdapter;


    private Context context;

    RobotQaPresent mRobotQaPresent = new RobotQaPresent(this);
    private RobotQaModel mRobotQaModel = new RobotQaModel();

    //相似题查看功能是否开启
    private boolean isSimilarTopicEnable = true;
    //intent传入参数
    private boolean isBeforeDoSubject;
    private boolean hasClass;
    private boolean isDoRight;
    private boolean allowAnswer = true;
    private String questionId;
    private String examId;
    private String extend;
    private String studentId;
    private String curClassId;

    /**
     * 进入微问界面
     *
     * @param type       0:做题之前  1：做题后
     * @param right      true:答对   false：答错
     * @param questionId 问题ID
     * @param extend     扩展数据你，JSON结构字符串
     */
    public static void startRobotQaActivity(Context context, int type, String examId, String questionId, boolean right, String extend, boolean hasClass) {
        Intent intent = new Intent(context, RobotQaActivity.class);
        intent.putExtra("type", type);
        intent.putExtra("examId", examId);
        intent.putExtra("questionId", questionId);
        intent.putExtra("right", right);
        intent.putExtra("extend", extend);
        intent.putExtra("hasClass", hasClass);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView( GlobalData.isPad() ? R.layout.activity_robot_qa_new : R.layout.activity_robot_qa_new_phone );

        x.view().inject(this);
        context = this;

        getIntentValue();
        initView();
        //学生信息

        mRobotQaPresent.askRobot(questionId);
        //获取原题干数据
        getSrcTopic();
        getAllowAnswer();

        MobclickAgent.onEvent( this, "ask_doudou" );
    }


    private void getSrcTopic() {
        sv_stem.setVisibility(View.GONE);
        tvloadfail.setVisibility(View.VISIBLE);
        tvloadfail.setOnClickListener(this);
        tvloadfail.setText("加载中。。。");
        mRobotQaPresent.getQuestionInfo(questionId);
    }


    private void getAllowAnswer() {
        if (hasClass && !TextUtils.isEmpty(studentId) && !TextUtils.isEmpty(curClassId)) {
            mRobotQaModel.getAnswerPermission(studentId, curClassId, new RequestListener<AnswerPermission>() {

                @Override
                public void onSuccess(AnswerPermission vo) {
                    //Log.i("sky", "getAllowAnswer onSuccess ");
                    if (vo != null) {
                        //Log.i("sky", "vo.getAllowLookAnswer()= " + vo.getShowAnswer());
                        allowAnswer = vo.getAllowLookAnswer() == 0;
                    }
                }

                @Override
                public void onFail(HttpResponse<AnswerPermission> response, Exception ex) {
                    //Log.i("sky", "getAllowAnswer onFail ");
                }
            });
        }
    }

    private void getIntentValue() {
        /* 进入微问界面
         * @param type              0:做题之前  1：做题后
         * @param right             true:答对   false：答错
         * @param questionId        问题ID
         * @param extend            扩展数据你，JSON结构字符串
         */
        Intent intent = getIntent();
        isBeforeDoSubject = intent.getIntExtra("type", 1) == 0;
        isDoRight = intent.getBooleanExtra("right", false);
        questionId = intent.getStringExtra("questionId");
        examId = intent.getStringExtra("examId");
        extend = intent.getStringExtra("extend");
        hasClass = intent.getBooleanExtra("hasClass", false);

        //初始化点击流事件参数
        String hasDone = isBeforeDoSubject ? "0" : "1";
        UserDetailinfo studentInfo = AccountUtils.getUserdetailInfo();
        if (studentInfo != null) {
            studentId = studentInfo.getStudentId();
        }
        MyTutorClassInfo myTutorClassInfo = AccountUtils.getCurrentClassInfo();
        if (myTutorClassInfo != null) {
            curClassId = myTutorClassInfo.getClassId();
        }
        Log.d("studentid", "id : " + studentId);
        RobotClickFlowManager.getInstance().getCommonJson(context, studentId, examId, questionId, hasDone, isDoRight);
        RobotClickFlowManager.getInstance().robotClick();
    }

    private void initView() {

        workToolbar.setTitle("问小豆");
        workToolbar.setRightTitle("显示原题干");
        workToolbar.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRightClick();
            }
        });

        mQaRecycleview.setLayoutManager(new QALayoutManager(context));
        mQaRvAdapter = new WebchatAdapter(context, mRobotQaPresent, this);
        mQaRecycleview.setAdapter(mQaRvAdapter);

        //获取初始对话内容
        if (!isBeforeDoSubject) {
            //做题后
            if (isDoRight) {
                //做对了，对次数没有要求，随便取值
                mRobotQaPresent.queryChatMsgs(1, isDoRight, true);
            } else {
                //先取得做题次数
                mRobotQaPresent.getQuestionDotimes(questionId);
            }
        } else {
            //做题前
            mRobotQaPresent.initChatMsgDosubjectBefore(questionId);
        }

        fl_stem.setOnClickListener(this);

    }

    public void onRightClick() {
        //切换右标题
        String src = "显示原题干";
        String dst = "隐藏原题干";
        if (workToolbar.getRightTitle().equals(src)) {
            workToolbar.setRightTitle(dst);
        } else {
            workToolbar.setRightTitle(src);
        }

        sv_stem.clearAnimation();
        int height = sv_stem.getMeasuredHeight();
        if (fl_stem.getVisibility() == View.GONE) {
            fl_stem.setVisibility(View.VISIBLE);
            if (height == 0) {
                height = sv_stem.getLayoutParams().height;
            }
            TranslateAnimation animation = new TranslateAnimation(0, 0, -height, 0);
            animation.setFillEnabled(true);
            animation.setDuration(200);
            sv_stem.startAnimation(animation);
        } else {
            TranslateAnimation animation = new TranslateAnimation(0, 0, 0, -height);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    sv_stem.clearAnimation();
                    fl_stem.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            animation.setFillEnabled(true);
            animation.setDuration(200);
            sv_stem.startAnimation(animation);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_loadfail:
                getSrcTopic();
                break;
            default:
                break;
        }
    }

    @Override
    public void onRecycleViewItemClick(View v, ChatMessage chatmsg) {
        final ChatMessage addNode;

        if (chatmsg.getContentType() != ChatMessage.ContentType.QUESTION) {
            //普通文本放大
            return;
        }
        //点击事件统计
        clickEventHandle(chatmsg);

        //点击后，消息类型改变
        if (chatmsg.getTextContent().equals(RobotQaConst.ROBOT_TRY_AGAIN)) {

        } else {
            ChatMessage tempmsg = chatmsg.buildItselfTextMsg();
            mRobotQaPresent.clearQuestionMsgs();
            mRobotQaPresent.addChatMsgs(tempmsg, true);
        }

        //点击后构建下一个节点
        ChatMessage nextNode = chatmsg.getNextChatMsg();
        if (nextNode != null) {
            if (nextNode.getContentType() == ChatMessage.ContentType.QUESTION) {
                mRobotQaPresent.addQuestionChatMsgs(nextNode, true);
            } else {
                mRobotQaPresent.addChatMsgs(nextNode, true);
            }
        }

        //做错后，都会出现重新选择错误原因的对话选择
        switch (chatmsg.getTextContent()) {
            case RobotQaConst.DONT_UNDSTAND_TOPIC:
            case RobotQaConst.DONT_GOT_KNOWLEDGE:
            case RobotQaConst.UNABLE_ANALYSIS:
            case RobotQaConst.LOOK_ROBOT_FOUND_REASON:
                if (isBeforeDoSubject) {
                    mRobotQaPresent.addReselecQuestionsDoBefore();
                } else {
                    mRobotQaPresent.addReselectErrorReasonQuestions(isSimilarTopicEnable,
                            mRobotQaPresent.isNoErrorReason(), mRobotQaPresent.isHadLookOtherAns());
                }
                break;
            default:
                break;

        }
        //如果点击后需要从后台取数据
        if (chatmsg.isAccessData()) {
            //数据请求
            if (chatmsg.getTextContent().equals(RobotQaConst.LOOK_SIMILAR_TOPIC)) {
                //做对，好的，去看看，开始请求相似题,1难度系数为高. 2为难度低
                //做题前与做对相似题难度为1，做错后为2
                mRobotQaPresent.getNewAlikeQuestion(questionId, "1", true);
                /*if (isBeforeDoSubject) {
                    mRobotQaPresent.getAlikeQuestion(questionId, "1", true);
                } else {
                    if (isDoRight) {
                        mRobotQaPresent.getAlikeQuestion(questionId, "1", true);
                    } else {
                        mRobotQaPresent.getAlikeQuestion(questionId, "2", true);
                    }
                }*/
            }

            if (chatmsg.getTextContent().equals(RobotQaConst.LOOK_OTHER_CLASSMATE_ANSWER)) {
                //瞄一眼同学的答案
                mRobotQaPresent.getOtherClassmateAns(examId, questionId);
                //                if (false){
                //                    //有同学答案
                //                    List<QuestionAns> ss = new ArrayList<>();
                //                    QuestionAns a = new QuestionAns();
                //                    a.setAnswerUrl("https://www.baidu.com/img/baidu_jgylogo3.gif");
                //                    a.setSchoolName("天中学");
                //                    a.setStuName("xiaojjj");
                //                    QuestionAns b = new QuestionAns();
                //                    b.setAnswerUrl("https://gss0.baidu.com/-vo3dSag_xI4khGko9WTAnF6hhy/zhidao/wh%3D800%2C450/sign=1ac96c1ac03d70cf4cafa205c8ecfd34/adaf2edda3cc7cd9e8e70afa3001213fb90e91ce.jpg");
                //                    b.setSchoolName("天s中学");
                //                    b.setStuName("xiaojssjj");
                //                    ss.add(a);
                //                    ss.add(b);
                //                    ChatMessage classmateAnsNode = ChatMessage.buildComeinMsg(ChatMessage.ContentType.CLASSMATE_ANS);
                //                    classmateAnsNode.setOthermatesAnsData(new OthermatesAnsData(ss));
                //                    mRobotQaPresent.addChatMsgs(classmateAnsNode, true);
                //                }
            }

            if (chatmsg.getTextContent().equals(RobotQaConst.CHECK_WRONG_REASON) ||
                    chatmsg.getTextContent().equals(RobotQaConst.RESELECT_WRONG_REASON)) {
                //做错后，查找错误原因或重新选择错误原因，开始请求
                mRobotQaPresent.getErrorReason(examId, questionId, RobotQaConst.CHECK_WRONG_REASON);
            }
            if (chatmsg.getTextContent().equals(RobotQaConst.RESELECT_QUESTION)) {
                //做题前，查找答疑结果
                mRobotQaPresent.getQAAnsDosubjectBefore(questionId);
            }

            if (chatmsg.getTextContent().equals(RobotQaConst.ROBOT_TRY_AGAIN)) {
                //点击再试一次，重新请求
                mRobotQaPresent.clearQuestionMsgs();
                mRobotQaPresent.tryAgain();
                //修改前一节点：即加载失败的节点内容改为加载中
                mRobotQaPresent.switchLastNodeToOngoing(true);
            }

            if (!chatmsg.getTextContent().equals(RobotQaConst.ROBOT_TRY_AGAIN)) {
                //等待对话节点
                mRobotQaPresent.addChatMsgs(mRobotQaPresent.getMsgNodeUtils().getHoldonMsg(), true);
            }
        }
    }

    private void clickEventHandle(ChatMessage chatmsg) {
        switch (chatmsg.getTextContent()) {
            case RobotQaConst.DONT_UNDSTAND_TOPIC:
                RobotClickFlowManager.getInstance().robotDontUQClick();
                break;
            case RobotQaConst.DONT_GOT_KNOWLEDGE:
                RobotClickFlowManager.getInstance().robotKnowledgeUnknowClick();
                break;
            case RobotQaConst.UNABLE_ANALYSIS:
                RobotClickFlowManager.getInstance().robotUnableAnalysisClick();
                break;
            case RobotQaConst.LOOK_ROBOT_FOUND_REASON:
                RobotClickFlowManager.getInstance().robotFoundReasonClick();
                break;
            case RobotQaConst.LOOK_OTHER_CLASSMATE_ANSWER:
                RobotClickFlowManager.getInstance().robotLookOtherAnsClick();
                break;
            case RobotQaConst.LOOK_SIMILAR_TOPIC:
                RobotClickFlowManager.getInstance().robotLookSimilarTopicClick();
                break;
            default:
                break;
        }
    }

    @Override
    public void onRecycleViewItemDoubleClick(View v, ChatMessage chatmsg) {
        /*if (chatmsg.getContentType() == ChatMessage.ContentType.TEXT ||
                chatmsg.getContentType() == ChatMessage.ContentType.SUBJECT) {

            String content;
            if (chatmsg.getContentType() == ChatMessage.ContentType.SUBJECT) {
                content = chatmsg.getChatSubject().getDisplayMessage();
            } else {
                //普通文本放大
                content = chatmsg.getTextContent();
            }
            Bundle bundle = new Bundle();
            bundle.putString(TextZoomActivity.MSG_CONTENT, content);
            goActivity(TextZoomActivity.class, bundle);
        }*/
    }

    @Override
    public Context getContext() {
        return context;
    }

    @Override
    public void toast(String s) {
        AlertManager.toast(context, s);
    }

    @Override
    public void refreshChatview() {
        mQaRvAdapter.notifyDataSetChanged();//必须用此方法才能进行recycleview的刷新。（末尾刷新）
        List<ChatMessage> allChatMessages = mRobotQaPresent.getAllChatMessages();
        int lastComein = allChatMessages.size() - 1;
        for (int i = allChatMessages.size() - 1; i >= 0; i--) {
            int type = allChatMessages.get(i).getType();
            if (type == ChatMessage.TYPE_COMEIN) {
                lastComein = i;
                break;
            }
        }
        mQaRecycleview.smoothScrollToPosition(lastComein); //滚动最后一个小豆回答的条目——适配手机端
        //        mQaRecycleview.smoothScrollToPosition(mQaRvAdapter.getItemCount());//滚动到底部
    }

    @Override
    public String getQuestionId() {
        return questionId;
    }

    @Override
    public String getExamId() {
        return examId;
    }

    @Override
    public void setQuestionStem(final QuestionInfo questionInfo, boolean success) {
        if (success) {
            tvloadfail.setVisibility(View.GONE);
            sv_stem.setVisibility(View.VISIBLE);
            mQuestionTextView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mQuestionTextView.setQuestionBook(questionInfo, true);
                }
            }, 500);
        } else {
            tvloadfail.setVisibility(View.VISIBLE);
            tvloadfail.setText("加载失败，点击重试。");
            sv_stem.setVisibility(View.GONE);
        }

    }

    @Override
    public boolean isRobotSimilarTopicEnable() {
        return isSimilarTopicEnable;
    }

    @Override
    public void setIsRobotSimilarTopicEnable(boolean b) {
        isSimilarTopicEnable = b;
    }

    @Override
    public boolean isDoRight() {
        return isDoRight;
    }

    @Override
    public boolean isDoSubjectBefore() {
        return isBeforeDoSubject;
    }

    @Override
    public RecyclerView getRecycleView() {
        return mQaRecycleview;
    }

    @Override
    protected void onResume() {
        super.onResume();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        WebchatAdapter adapter = (WebchatAdapter) mQaRvAdapter;
        if (adapter.getCommentStarTimes() == 0) {
            if (!mRobotQaPresent.isHaveCommentStarNode()) {
                return;
            }
            //如果没有评价，退出时自动评价一次
            mRobotQaPresent.commentRobotService(5 + "", "", getQuestionId(), new RequestListener<ResultInfo>() {
                @Override
                public void onSuccess(ResultInfo res) {
                    ActivityManager.RunningTaskInfo topTask = AppUtils.getTopTask(getContext());
                    String packageName = AppUtils.getPackageInfo(getContext()).packageName;
                    if (AppUtils.isTopActivity(topTask, packageName, RobotQaActivity.class.getName())) {
                        AppLog.d("cls : " + RobotQaActivity.class.getName() + "  在最前端");
                        toast("评价成功");
                    }
                }

                @Override
                public void onFail(HttpResponse reponse, Exception e) {
                    ActivityManager.RunningTaskInfo topTask = AppUtils.getTopTask(getContext());
                    String packageName = AppUtils.getPackageInfo(getContext()).packageName;
                    if (AppUtils.isTopActivity(topTask, packageName, RobotQaActivity.class.getName())) {
                        AppLog.d("cls : " + RobotQaActivity.class.getName() + "  在最前端");
                        toast("评价失败，请重试");
                    }
                }
            });
        }
        //清除认知误差图的缓存
        WebchatAdapter.getBitmaps().clear();
    }

    public boolean isAllowAnswer() {
        return allowAnswer;
    }
}
