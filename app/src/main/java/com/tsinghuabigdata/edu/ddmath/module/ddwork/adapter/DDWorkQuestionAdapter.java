package com.tsinghuabigdata.edu.ddmath.module.ddwork.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.bean.StdAnswerBean;
import com.tsinghuabigdata.edu.ddmath.constant.AppConst;
import com.tsinghuabigdata.edu.ddmath.event.RefreshBrowerEvent;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.DDWorkUtil;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.DDWorkDetail;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.LocalPageInfo;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.LocalQuestionInfo;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.QuestionInfo;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.ReviseResultInfo;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.SolveThinkBean;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.dialog.ReviseErrorDialog;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.view.AppealStatusView;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.view.QuestionAnswerImageView;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.view.QuestionItemView;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.view.QuestionTextView;
import com.tsinghuabigdata.edu.ddmath.module.robotqa.RobotQaActivity;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.view.AskXiaoDouView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;


/**
 * 每页错题信息列表
 */
public class DDWorkQuestionAdapter extends ArrayAdapter<LocalQuestionInfo> {

    private Context mContext;
    private LayoutInflater mInflater;
    //private ArrayList<QuestionType> mQuestionTypeList = new ArrayList<>();

    //private ArrayList<QuestionType> questionTypeList = new ArrayList<>();

    //private String numberStr[] = {"一","二","三","四","五","六","七","八","九","十"};

    //private int currPageNum = 1;

    private int submitStatus = 0;       //作业提交状态
    private DDWorkDetail workDetail;
    //private String mWorkName;
    private boolean allowAnswer;

    public DDWorkQuestionAdapter(Context context, int status ) {
        super(context, 0);
        this.mContext = context;
        submitStatus = status;

        mInflater=(LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);//LayoutInflater.from(mContext);
    }

//    public void addQuestionList( ArrayList<LocalQuestionInfo> qlist/*, int pageNum*/ ){
////        for( LocalQuestionInfo question : qlist ){
////            addQuestion( question );
/////        }
//        addAll( qlist );
//        //currPageNum = pageNum;
//        //Collections.sort( mQuestionTypeList, new QuestionTypeComparator() );
//    }
    public void addQuestionList(LocalPageInfo pageInfo ){
        addAll( pageInfo.getQuestions() );
    }

    public void setData(DDWorkDetail detail, boolean allow ){
        workDetail = detail;
        allowAnswer = allow;
        submitStatus = workDetail.getExerStatus();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView( int position, View convertView, @NonNull ViewGroup parent) {

        ViewHolder holder;
        if(convertView==null){
            convertView = mInflater.inflate(GlobalData.isPad()?R.layout.item_ddwork_question:R.layout.item_ddwork_question_phone, parent, false );
            holder = new ViewHolder(convertView);
            convertView.setTag( holder );
        } else if (((ViewHolder)convertView.getTag()).needReInflate) {
            convertView = mInflater.inflate(GlobalData.isPad()?R.layout.item_ddwork_question:R.layout.item_ddwork_question_phone, parent, false );
            holder = new ViewHolder(convertView);
            convertView.setTag( holder );
        }else{
            holder=(ViewHolder)convertView.getTag();
        }

        holder.bindView( position );
        return convertView;
    }

    //-------------------------------------------------------------------------------
    class ViewHolder implements View.OnClickListener, AppealStatusView.AppealListener{

        boolean needReInflate;

        private int position;

        private View rootView;
        private TextView questionTypeView;

        //private LinearLayout questionDataLayout;

        private QuestionTextView questionTextView;         //
        //private CorrectImage answerImage;       //题干图
        //private RelativeLayout noAnswerImage;        //没有答案时的默认图
        //private ImageView cheerImage;        //鼓励图
        private QuestionAnswerImageView answerImageView;            //用户答案图
        private QuestionAnswerImageView answerImageViewSecond;      //用户答案图，第二张
        private RelativeLayout optToolLayout;    //纠错 问小豆 订正
        private AskXiaoDouView askXiaoDouView;
        //private Button correctErrorBtn;
        //private TextView correctErrorStatusView;
        private AppealStatusView appealStatusView;

        //错题订正
        private Button reviseAnswerBtn;
        private QuestionAnswerImageView reviseAnswerImageView;      //用户订正答案图
        private AppealStatusView reviseAppealStatusView;

        private QuestionItemView answerView;        //参考答案
        private QuestionItemView thinkView;         //解题思路
        private QuestionItemView knowledgeView;     //知识点
        private QuestionItemView reasonsView;       //错误原因，错题，订正时用户选择的原因

        //已提交 已批阅，支持 直接滑动，页码信息
        private LinearLayout pageinfoLayout;
        private TextView pageinfoTextView;

        private View splitLineView;

        public ViewHolder(View root ){
            rootView = root.findViewById( R.id.item_ddwork_mainlayout );
            questionTypeView    = root.findViewById( R.id.item_ddwork_questiontype );

            questionTextView     = root.findViewById( R.id.item_ddwork_questiondata );

            answerImageView = root.findViewById( R.id.item_ddwork_question_answerimage );
            answerImageViewSecond = root.findViewById( R.id.item_ddwork_question_answerimage_second );

            optToolLayout     = root.findViewById( R.id.item_ddwork_correctErrorLayout );
            askXiaoDouView    = root.findViewById( R.id.item_ddwork_askDouDouView );
            askXiaoDouView.setOnClickListener( this );
            appealStatusView    = root.findViewById( R.id.item_ddwork_appealanswer );
            appealStatusView.setApplealListener( this );

            reviseAnswerBtn     = root.findViewById( R.id.item_ddwork_reviseErrBtn );
            reviseAnswerBtn.setOnClickListener( this );
            reviseAnswerImageView=root.findViewById( R.id.item_ddwork_reviseanswerimage );
            reviseAppealStatusView=root.findViewById( R.id.item_ddwork_reviceappealanswer );
            reviseAppealStatusView.setApplealListener( this );

            answerView      = root.findViewById( R.id.item_ddwork_standardAnswer );
            thinkView       = root.findViewById( R.id.item_ddwork_solveThink );
            knowledgeView   = root.findViewById( R.id.item_ddwork_errKonwledge );
            reasonsView     = root.findViewById( R.id.item_ddwork_errReasons );

            pageinfoLayout  = root.findViewById( R.id.item_ddwork_pageinfo_layout );
            pageinfoTextView= root.findViewById( R.id.item_ddwork_pageinfo_textview );

            splitLineView   = root.findViewById( R.id.item_ddwork_questionsplitline );
        }

        //前提条件 已批阅
        private boolean judgeShowAnswer(List<StdAnswerBean> stdAnswers, int status, String reviseAnswerUrl ){
            if( stdAnswers == null || stdAnswers.size() == 0 )
                return false;       //不显示

            //老师是否允许
            if( allowAnswer )
                return true;        //显示

            //不允许, 答对显示
            if(status>0)
                return true;

            //答错，已订正，显示
            if( !TextUtils.isEmpty(reviseAnswerUrl) )
                return true;    //已订正

            return false;
        }

        void bindView( int position ){
            //------------------------------------------------------------------------
            //记录显示的序号
            this.position = position;

            LocalQuestionInfo questionInfo = getItem( position );
            if( questionInfo==null ) return;

            if( !questionInfo.isSelect() ){
                if( rootView ==null ) return;
                ViewGroup.LayoutParams params = rootView.getLayoutParams();
                params.height = 1;// 这里不能设置高度为0，然后会有一个空白
                rootView.setLayoutParams(params);
                rootView.setVisibility(View.GONE);
                ((ViewHolder) rootView.getTag()).needReInflate = true;
            }

            //先判断实现显示页码信息
            if( !TextUtils.isEmpty( questionInfo.getPageInfo()) ){
                pageinfoLayout.setVisibility( View.VISIBLE );
                pageinfoTextView.setText( questionInfo.getPageInfo() );
            }else{
                pageinfoLayout.setVisibility( View.GONE );
            }

            //判断是否显示题型信息
            if( !TextUtils.isEmpty( questionInfo.getShowQuestionType()) ){
                questionTypeView.setVisibility( View.VISIBLE );
                questionTypeView.setText( questionInfo.getShowQuestionType() );
            }else{
                questionTypeView.setVisibility( View.GONE );
            }

            //周练要显示分数
            if(AppConst.WORK_TYPE_WEEKTRAIN.equals( workDetail.getSourceType()) || questionInfo.getQuestionScore() > 0 )
                questionTextView.setKeyWords( questionInfo.getKeywordString(), questionInfo.getKeywordColor() );

            //题目信息
            questionTextView.setQuestion( questionInfo );

            int status = DDWorkUtil.getQuestionCorrectStatus( questionInfo, false,submitStatus );          //批改状态
            int revise_status = DDWorkUtil.getQuestionCorrectStatus( questionInfo, true, submitStatus );   //订正批改状态
            ReviseResultInfo reviseResultInfo = questionInfo.getReviseResultResponse();
            String reviseAnswerUrl = "";
            String reviseReasons = "";
            if( reviseResultInfo != null ){
                reviseAnswerUrl = reviseResultInfo.getAnswerUrl();
                reviseReasons   = reviseResultInfo.getErrorAnalysis();
            }

            String ekpoints = DDWorkUtil.getTop3EKPoint( questionInfo, workDetail.getTopKnowledgeList(), status );
            int cameraStatus = 0;
            if( questionInfo.getPageNum() < workDetail.getPageInfo().size()  ){
                LocalPageInfo localPageInfo = workDetail.getPageInfo().get( questionInfo.getPageNum() );
                cameraStatus = localPageInfo.getSubmitStatus();
            }
            //
            //用户答案显示
            answerImageView.setData( questionInfo, submitStatus, status, ekpoints, 1 == cameraStatus );
            //原版教辅的第二张图
            answerImageViewSecond.setData( questionInfo );

            optToolLayout.setVisibility(View.GONE);
            answerView.setVisibility( View.GONE );
            thinkView.setVisibility( View.GONE );
            knowledgeView.setVisibility( View.GONE );

            //判断是否批改
            if( status >= 0 || submitStatus == DDWorkDetail.WORK_CORRECTED ) {      //此题已批改 或者 作业批改完成

                //参考答案
                List<StdAnswerBean> stdAnswers = questionInfo.getStdAnswers();

                if( judgeShowAnswer( stdAnswers, status, reviseAnswerUrl ) ){

                    //显示答案
                    answerView.setVisibility( View.VISIBLE );
                    setStdAnswer( stdAnswers, answerView );

                    //解题思路
                    ArrayList<SolveThinkBean> solutions = questionInfo.getSolutions();
                    if( solutions!=null && solutions.size()>0 ){
                        thinkView.setVisibility( View.VISIBLE );
                        DDWorkUtil.setSolveThink( solutions, thinkView );
                        //thinkView.setData( "解题思路", solutions );
                    }
                }
                //前提条件: 题目已被批阅
                //条件  1：老师允许     2，已订正   3，已做对       满足任意都显示
//                if( (allowAnswer && stdAnswers!=null && stdAnswers.size()>0) || !TextUtils.isEmpty(reviseAnswerUrl) || status>0 ){
//
//                }

                //错误知识点
//                String data = DDWorkUtil.getErrorKnowledgeStr(questionInfo);
//                if( status == 0 && !TextUtils.isEmpty(data) ){
//                    knowledgeView.setVisibility( View.VISIBLE );
//                    knowledgeView.setData("错误知识点", data, null, null);
//                }
            }

            //我要申诉功能
            appealStatusView.setData( workDetail, questionInfo, status, position, false );
            if( appealStatusView.getVisibility() == View.VISIBLE )
                optToolLayout.setVisibility( View.VISIBLE );

            //题目之间的分割线是否显示判断
            splitLineView.setVisibility( View.VISIBLE );        //默认显示
            if( position+1 >= getCount() ){     //最后一题不显示  某页的最后一题
                splitLineView.setVisibility( View.GONE );
            }else {
                LocalQuestionInfo questionInfo1 = getItem( position+1 );
                if( questionInfo1!=null && !TextUtils.isEmpty(questionInfo1.getPageInfo()) ){
                    splitLineView.setVisibility( View.GONE );
                }
            }

            //问小豆设置   主观题才显示
            String qtpye = questionInfo.getQuestionType();
            if( !TextUtils.isEmpty(qtpye) && ( qtpye.contains(QuestionInfo.TYPE_SOLUTION) || qtpye.contains(QuestionInfo.TYPE_CALCULATION)) ){
                askXiaoDouView.setVisibility( View.VISIBLE );
                optToolLayout.setVisibility( View.VISIBLE );
                askXiaoDouView.setCount( questionInfo.getAsked() );
            }else{
                askXiaoDouView.setVisibility( View.GONE );
            }

            //---------------------------------------------------------
            //错题订正

            //订正入口,只有已批阅错题错题 && 未超期时显示(7天内) && 未申诉 或者申诉重新批阅完成
            reviseAnswerBtn.setVisibility( View.INVISIBLE );
            int correctStatus = questionInfo.getCorrectErrorStatus();       //申诉纠错状态
            if((status == 0 || ( TextUtils.isEmpty(questionInfo.getAnswerUrl())
                && submitStatus >= DDWorkDetail.WORK_CORRECTED ))                                 //作业状态
                && ( correctStatus != QuestionInfo.CORRECTSTATUS_APPLYED )      //未申诉 或者申诉重新批阅完成
                && !workDetail.isOverdue()                                                      //是否超期
                && TextUtils.isEmpty(reviseAnswerUrl) ){                                        //是否订正
                reviseAnswerBtn.setVisibility( View.VISIBLE );
                optToolLayout.setVisibility( View.VISIBLE );
            }

            //订正答案显示
            reviseAnswerImageView.setReviseData( questionInfo, revise_status, revise_status==0?ekpoints:"" );

            //我要申诉功能
            reviseAppealStatusView.setData( workDetail, questionInfo, revise_status, position, true );

            //错题原因
            reasonsView.setVisibility( View.GONE );
            if( !TextUtils.isEmpty( reviseReasons ) ){
                reasonsView.setVisibility( View.VISIBLE );
                reasonsView.setReasonData("错误原因", reviseReasons );
            }

        }

        @Override
        public void onClick(View view) {

            switch (view.getId()){
                case R.id.item_ddwork_askDouDouView:{
                    final LocalQuestionInfo questionInfo = getItem( position );
                    if( questionInfo==null )return;

                    int type = submitStatus >= DDWorkDetail.WORK_WAITCORRECT ? 1:0;
                    int status = DDWorkUtil.getQuestionCorrectStatus( questionInfo, false,submitStatus );
                    boolean right = status>0;
                    if( type == 1 && status<0 ){     //已提交，还没有修改的题目
                        type = 0;
                    }
                    String examId = workDetail.getWorkId();
                    String questionId = questionInfo.getQuestionId();

                    RobotQaActivity.startRobotQaActivity( mContext, type, examId, questionId, right, "{}",true );
                    break;
                }
                case R.id.item_ddwork_reviseErrBtn:{

                    final LocalQuestionInfo questionInfo = getItem( position );
                    if( questionInfo==null )return;
                    //if( !DDWorkUtil.showReviseTipsDailog(getContext()) ) return;

                    ReviseErrorDialog.showReviseErrorDialog( mContext, questionInfo, workDetail.getWorkId(),null,false,1, 1,false, workDetail.getUsedCount(), "revise_fromdetail,", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            notifyDataSetChanged();
                        }
                    },null);
                    break;
                }
                default:
                    break;
            }
        }

        @Override
        public void appealSuccessListener() {
            notifyDataSetChanged();
            EventBus.getDefault().post(new RefreshBrowerEvent());
        }
    }

    private void setStdAnswer(List<StdAnswerBean> list, QuestionItemView textView){
        if( list == null || list.size() == 0 ){
            textView.setVisibility( View.GONE );
            return;
        }

        String contentLatextGraph = null;
        String contentg = null;
        String graph = null;

        for (int i = 0; i < list.size(); i++) {
            StdAnswerBean bean = list.get(i);
            if(TextUtils.isEmpty(contentg)){
                contentg =  bean.getContentg();
            }else if (TextUtils.isEmpty(bean.getContentg())){
                contentg += "\n";
            }else {
                contentg += "\n" + bean.getContentg();
            }
            if (!TextUtils.isEmpty(bean.getContentLatextGraph()) && !"(null)".equals(bean.getContentLatextGraph()) ) {
                if (TextUtils.isEmpty(contentLatextGraph)) {
                    contentLatextGraph = bean.getContentLatextGraph();
                } else {
                    contentLatextGraph += "#%#" + bean.getContentLatextGraph();
                }
            }
            if (!TextUtils.isEmpty(bean.getGraph()) && !"(null)".equals( bean.getGraph()) ) {
                if (TextUtils.isEmpty(graph)) {
                    graph = bean.getGraph();
                } else {
                    graph += "#%#" + bean.getGraph();
                }
            }
        }
        textView.setData( "参考答案",contentg, graph, contentLatextGraph );
    }



}