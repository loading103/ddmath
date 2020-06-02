package com.tsinghuabigdata.edu.ddmath.module.errorbook.adapter;

import android.content.Context;
import android.graphics.Rect;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.adapter.ViewHolder;
import com.tsinghuabigdata.edu.ddmath.bean.ExamNameBean;
import com.tsinghuabigdata.edu.ddmath.bean.MyTutorClassInfo;
import com.tsinghuabigdata.edu.ddmath.bean.StdAnswerBean;
import com.tsinghuabigdata.edu.ddmath.constant.AppConst;
import com.tsinghuabigdata.edu.ddmath.event.UploadScorePlanEvent;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.DDWorkUtil;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.DDWorkDetail;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.QuestionInfo;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.ReviseResultInfo;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.SolveThinkBean;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.dialog.ReviseErrorDialog;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.view.CorrectImage;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.view.QuestionAnswerImageView;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.view.QuestionItemView;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.view.QuestionTextView;
import com.tsinghuabigdata.edu.ddmath.module.errorbook.bean.QuestionVo;
import com.tsinghuabigdata.edu.ddmath.module.robotqa.RobotQaActivity;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.EventBusUtils;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.view.AnswerTextView;
import com.tsinghuabigdata.edu.ddmath.view.AskXiaoDouView;
import com.tsinghuabigdata.edu.ddmath.view.PentacleView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


/**
 * 错题本适配器
 * Created by Administrator on 2016/12/9.
 */

public class ErrorQuestionBrowerAdapter extends ArrayAdapter<QuestionVo> {

    private QuestionBookItem mQuestionBookItem;
    private String           keyWord;
    private int              correctTotal;
    private boolean          showCorrectBtn=false;     //是否显示订正按钮    错题浏览不显示，日日清显示
    private boolean          showMasterIcon=true;      //是否显示已掌握的标记,默认显示

    private String        showtitle;
    private boolean       isScored=false; //是不是定制学
    private int        overdue=1; //是不是逾期
    public int unDoNum; ////是1的时候代表最后一题
    public ErrorQuestionBrowerAdapter(Context context, int total) {
        super(context,0);
        correctTotal = total;
    }
    public ErrorQuestionBrowerAdapter(Context context, int total,String showtitle,boolean isScored,int overdue,int undonum) {
        super(context,0);
        correctTotal = total;
        this.showtitle=showtitle;
        this.overdue=overdue;
        this.isScored=isScored;
        this.unDoNum=undonum;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            ViewHolder viewHolder = new ViewHolder( getContext(), parent, GlobalData.isPad()?R.layout.item_question_book : R.layout.item_question_book_phone, position);
            convertView = viewHolder.getConvertView();
        }
        ViewHolder viewHolder = ((ViewHolder) convertView.getTag());
        convert( viewHolder, position, getItem(position) );
        return convertView;
    }

    public void setShowCorrectBtn(boolean showCorrectBtn) {
        this.showCorrectBtn = showCorrectBtn;
    }
    public void setShowMasterIcon( boolean showMasterIcon ){
        this.showMasterIcon = showMasterIcon;
    }

    //前提条件 已批阅
    private boolean judgeShowAnswer(List<StdAnswerBean> stdAnswers, int status, String reviseAnswerUrl ){
        if( stdAnswers == null || stdAnswers.size() == 0 )
            return false;       //不显示

        //不允许, 答对显示
        if(status>0)
            return true;

        //答错，已订正，显示
        if( !TextUtils.isEmpty(reviseAnswerUrl) )
            return true;    //已订正

        //老师是否允许
        boolean allowAnswer = true;
        MyTutorClassInfo currentClassInfo = AccountUtils.getCurrentClassInfo();
        if (currentClassInfo != null) {
            allowAnswer = currentClassInfo.isAllowAnswer();
        }
        if( allowAnswer )
            return true;        //显示

        return false;
    }

    private void convert(ViewHolder helper, final int position, final QuestionVo item) {
        String questionTypeName = DDWorkUtil.getQuestionTypeName(item.getQuestionType());
        helper.setText(R.id.tv_question_type, questionTypeName);
        PentacleView pentacleView = helper.getView(R.id.pentacleView);
        pentacleView.setLevel(item.getDifficultLevel());
        helper.setText(R.id.tv_total_count, "做过" + item.getTotalCount() + "次");
        helper.setText(R.id.tv_wrong_count, "错误" + item.getWrongCount() + "次");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm上传");
        List<ExamNameBean> examNames = item.getExamNames();
        if (examNames != null && examNames.size() > 0) {
            ExamNameBean examNameBean = examNames.get(0);
            String time = sdf.format(examNameBean.getUploadTime());
            helper.setText(R.id.tv_question_title, examNameBean.getExamName());
            helper.setText(R.id.tv_upload_time, time);
        } else {
            helper.setText(R.id.tv_question_title, null);
            helper.setText(R.id.tv_upload_time, null);
        }
        QuestionTextView questionTextView = helper.getView(R.id.tv_stem);
        questionTextView.setKeyWords(keyWord);
        questionTextView.setQuestionBook(item, position + 1, true);

        final LinearLayout llInfo = helper.getView(R.id.ll_upload_info);
        final RelativeLayout root = helper.getView(R.id.rl_item_question_book);
        llInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mQuestionBookItem != null) {
                    Rect rect = new Rect();
                    llInfo.getGlobalVisibleRect( rect );
                    Rect prect = new Rect();
                    root.getGlobalVisibleRect( prect );

                    mQuestionBookItem.clickUpload(position, rect.left, rect.bottom, prect.bottom);
                }
            }
        });

        final LinearLayout llExtend = helper.getView(R.id.ll_extend);
        final CheckBox checkBox = helper.getView(R.id.cb_unfold);
        checkBox.setChecked(item.isShow());
        if (item.isShow()) {
            llExtend.setVisibility(View.VISIBLE);
            checkBox.setText("收起");
        } else {
            llExtend.setVisibility(View.GONE);
            checkBox.setText("展开");
        }
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                item.setShow(checkBox.isChecked());
                root.setActivated(checkBox.isChecked());
                if (checkBox.isChecked()) {
                    llExtend.setVisibility(View.VISIBLE);
                    checkBox.setText("收起");
                } else {
                    llExtend.setVisibility(View.GONE);
                    checkBox.setText("展开");
                }
            }
        });

        CorrectImage answerImage = helper.getView(R.id.correctImage);
        //answerImage.setActivity((Activity) mContext);
        if (!TextUtils.isEmpty(item.getAnswerUrl())) {    //网络图片
            answerImage.setVisibility(View.VISIBLE);
            answerImage.loadImage(item.getAnswerUrl(), item.getAnswerArea(), "");
        } else {
            answerImage.setVisibility(View.GONE);
        }

        ReviseResultInfo reviseResultInfo = item.getReviseResultResponse();
        String reviseAnswerUrl = "";
        String reviseReasons = "";
        if (reviseResultInfo != null) {
            reviseAnswerUrl = reviseResultInfo.getAnswerUrl();
            reviseReasons = reviseResultInfo.getErrorAnalysis();
        }

        //参考答案
        LinearLayout llModelAnswer = helper.getView(R.id.ll_model_answer);
        AnswerTextView answerTextView = helper.getView(R.id.tv_model_answer);
        QuestionItemView thinkView = helper.getView(R.id.think_view);

        //没有班级意味着，所在的班已经完成上课了，所以可以看
        llModelAnswer.setVisibility(View.GONE);
        thinkView.setVisibility(View.GONE);
        if( judgeShowAnswer( item.getStdAnswers(), 0, reviseAnswerUrl ) ){
            llModelAnswer.setVisibility(View.VISIBLE);
            setStdAnswer(item.getStdAnswers(), answerTextView);

            //解题思路
            ArrayList<SolveThinkBean> solutions = item.getSolutions();
            if (solutions != null && solutions.size() > 0) {
                thinkView.setVisibility(View.VISIBLE);
                DDWorkUtil.setSolveThink(solutions, thinkView);
            } else {
                thinkView.setVisibility(View.GONE);
            }
        }
//
//        //在7天范围之内并且没有订正过则显示参考答案且 未掌握
//        if(inIime(examNames) && item.getIsNowCorrect() != 1 && ( TextUtils.isEmpty(reviseAnswerUrl) /*|| !allowAnswer*/ ) ) {
//
//        } else {
//
//        }

        //错误知识点
        QuestionItemView knowledgeView = helper.getView(R.id.knowledge_view);
        String data = DDWorkUtil.getErrorKnowledgeStr(item);
        if (item.getExamNames() != null && item.getExamNames().size() > 0 && item.getExamNames().get(0).isCorrect() || TextUtils.isEmpty(data)) {
            knowledgeView.setVisibility(View.GONE);
        } else {
            knowledgeView.setVisibility(View.VISIBLE);
            knowledgeView.setData("错误知识点", data, null, null);
        }

        //错题原因
        QuestionItemView reasonsView = helper.getView(R.id.errReasons_view);
        if (!TextUtils.isEmpty(reviseReasons)) {
            reasonsView.setVisibility(View.VISIBLE);
            reasonsView.setReasonData("错题原因", reviseReasons);
        } else {
            reasonsView.setVisibility(View.GONE);
        }

        ImageView imageView = helper.getView(R.id.iv_corrected);
        imageView.setVisibility(View.GONE);
        //错题订正 先显示已订正
        ReviseResultInfo resultInfo = item.getReviseResultResponse();
        if ( showCorrectBtn && resultInfo!=null && !TextUtils.isEmpty( resultInfo.getAnswerUrl() ) ) {
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageResource(R.drawable.ic_wrongbook_dayclean_tag2);
        }

        //已掌握
        if (item.getIsNowCorrect() == 1 && showMasterIcon ) {
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageResource(R.drawable.img_correct);
        }

        //问小豆
        AskXiaoDouView askXiaoDouView = helper.getView(R.id.askXiaoDouView);
        askXiaoDouView.setCount(item.getAsked());
        askXiaoDouView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (item.getExamNames() != null && item.getExamNames().size() > 0) {
                    ExamNameBean examNameBean = item.getExamNames().get(0);
                    RobotQaActivity.startRobotQaActivity( getContext(), 1, examNameBean.getExamId(), item.getQuestionId(), examNameBean.isCorrect(), "{}", false);
                }
            }
        });
        //问小豆设置   主观题才显示
        String qtpye = item.getQuestionType();
        if (!TextUtils.isEmpty(qtpye) && (qtpye.contains(QuestionInfo.TYPE_SOLUTION) || qtpye.contains(QuestionInfo.TYPE_CALCULATION))) {
            askXiaoDouView.setVisibility(View.VISIBLE);
        } else {
            askXiaoDouView.setVisibility(View.GONE);
        }

        //订正入口,只有已批阅错题错题 && 未超期时显示(7天内)
        Button reviseAnswerTv = helper.getView(R.id.btn_reviseErr);

        //没有订正答案图片
        int correctStatus = item.getCorrectErrorStatus();       //申诉纠错状态
        if (!TextUtils.isEmpty(reviseAnswerUrl)) {          //已订正
            reviseAnswerTv.setVisibility(View.INVISIBLE);
        } else if ( showCorrectBtn && inIime(examNames) /*&& item.getIsNowCorrect() != 1*/ && ( correctStatus == QuestionInfo.CORRECTSTATUS_UNAPPLY || correctStatus == QuestionInfo.CORRECTSTATUS_FINISHED ) ) {
            //在7天范围之内并且不是已掌握
            reviseAnswerTv.setVisibility(View.VISIBLE);
        } else {
            reviseAnswerTv.setVisibility(View.INVISIBLE);
        }
        reviseAnswerTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<ExamNameBean> examNames = item.getExamNames();
                if (examNames != null && examNames.size() > 0 && examNames.get(0) != null) {
                    ExamNameBean examNameBean = examNames.get(0);
                    ReviseErrorDialog.showReviseErrorDialog(getContext(), item, examNameBean.getExamId(), showtitle,isScored,overdue,unDoNum,true, correctTotal, "revise_fromdayclean,", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            item.setShow(true);
                            if(isScored){
                                EventBusUtils.postDelay(new UploadScorePlanEvent( "" ), new Handler());//定制学订正成功  就通知刷新
                                unDoNum--;
                            }
                            notifyDataSetChanged();
                            //MessageController.getInstance().sendMessage(new LocalMessage(AppConst.MESSAGE_REFRESH_ERRBOOK));
                        }
                    }, null);
                }
            }
        });

        QuestionAnswerImageView reviseAnswerImageView = helper.getView(R.id.reviseanswerimage);
        if (TextUtils.isEmpty(reviseAnswerUrl)){
            reviseAnswerImageView.setVisibility(View.GONE);
        }else {
            //订正答案显示
            reviseAnswerImageView.setVisibility(View.VISIBLE);
            int revise_status = DDWorkUtil.getQuestionCorrectStatus(item, true, DDWorkDetail.WORK_CORRECTED);
            reviseAnswerImageView.setReviseData(item, revise_status, "");
        }


    }


    private boolean inIime(List<ExamNameBean> examNames) {
        if (examNames != null && examNames.size() > 0) {
            ExamNameBean examNameBean = examNames.get(0);
            if (examNameBean != null) {
                if (System.currentTimeMillis() - examNameBean.getUploadTime() < AppConst.RECENT_7_DAY) {
                    return true;
                }
            }
        }
        return false;
    }


    private void setStdAnswer(List<StdAnswerBean> stdAnswers, AnswerTextView answerTextView) {
        if (/*allowAnswer &&*/ stdAnswers == null || stdAnswers.size() == 0) {
            answerTextView.setText(null);
            return;
        }
        StdAnswerBean fBean = stdAnswers.get(0);
        String contentLatextGraph = fBean.getContentLatextGraph();
        String contentg = fBean.getContentg();
        String graph = fBean.getGraph();
        if (stdAnswers.size() > 1) {
            for (int i = 1; i < stdAnswers.size(); i++) {
                StdAnswerBean bean = stdAnswers.get(i);
                if (TextUtils.isEmpty(bean.getContentg())) {
                    contentg += "\n";
                } else {
                    contentg += "\n" + bean.getContentg();
                }
                if (!TextUtils.isEmpty(bean.getContentLatextGraph())) {
                    if (TextUtils.isEmpty(contentLatextGraph)) {
                        contentLatextGraph += bean.getContentLatextGraph();
                    } else {
                        contentLatextGraph += "#%#" + bean.getContentLatextGraph();
                    }
                }
                if (!TextUtils.isEmpty(bean.getGraph())) {
                    if (TextUtils.isEmpty(graph)) {
                        graph += bean.getGraph();
                    } else {
                        graph += "#%#" + bean.getGraph();
                    }
                }
            }
        }
        StdAnswerBean stdAnswerBean = new StdAnswerBean();
        stdAnswerBean.setContentg(contentg);
        stdAnswerBean.setContentLatextGraph(contentLatextGraph);
        stdAnswerBean.setGraph(graph);
        answerTextView.setQuestionBook(stdAnswerBean, false);
    }

    public void setKeyWord(String keyWord) {
        this.keyWord = keyWord;
    }

    public void setQuestionBookItem(QuestionBookItem questionBookItem) {
        mQuestionBookItem = questionBookItem;
    }


    public interface QuestionBookItem {
        void clickUpload(int position, int left, int top, int bottom);

        void revise(int position);
    }


}
