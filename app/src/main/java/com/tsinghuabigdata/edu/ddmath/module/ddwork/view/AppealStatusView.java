package com.tsinghuabigdata.edu.ddmath.module.ddwork.view;


import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.constant.AppConst;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.DDWorkUtil;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.DDWorkDetail;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.LocalPageInfo;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.LocalQuestionInfo;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.QuestionInfo;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.ReviseResultInfo;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.dialog.CorrectErrorDialog;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;

import java.util.ArrayList;
import java.util.List;

/**
 * 我要申诉状态功能
 */
public class AppealStatusView extends LinearLayout implements View.OnClickListener {

    private LocalQuestionInfo questionInfo;
    private DDWorkDetail workDetail;
    private boolean isLearnMaterial;      //是否是教辅
    private int position;

    private AppealListener mAppealListener;

    //对象
    private RelativeLayout appealLayout;              //申诉按钮
    private RelativeLayout appealStatusLayout;       //
    private TextView appealStatusView;                //申诉处理状态

    private boolean isReviseError = false;

    public AppealStatusView(Context context) {
        super(context);
        initData(context);
    }

    public AppealStatusView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initData(context);
    }

    public AppealStatusView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initData( context);
    }

    public void setApplealListener(AppealListener listener){
        mAppealListener = listener;
    }
    /**
     * 设置数据
     * @param questionInfo      题目信息
     * @param status            作业批改状态
     */
    public void setData( DDWorkDetail detail, LocalQuestionInfo questionInfo, int status, int position, boolean reviseError ){
        this.questionInfo = questionInfo;
        this.workDetail = detail;
        this.position   = position;

        isReviseError = reviseError;

        if( workDetail!=null ){     //判断第一个对象是不是教辅名称
            isLearnMaterial = !TextUtils.isEmpty( workDetail.getPageInfo().get(0).getLearnMaterialName() );
        }

        //Resources resources = getContext().getResources();

        String answerUrl = questionInfo.getAnswerUrl();
        int qstatus = questionInfo.getCorrectErrorStatus();
        boolean noRevise;      //是否已订正，显示原答案时使用
        if( isReviseError ){
            noRevise = false;
            ReviseResultInfo reviseResultInfo = questionInfo.getReviseResultResponse();
            if( reviseResultInfo!=null ){
                answerUrl = reviseResultInfo.getAnswerUrl();
                qstatus = reviseResultInfo.getCorrectionStatus();
            }else{
                answerUrl = "";
                qstatus = QuestionInfo.CORRECTSTATUS_UNAPPLY;
            }
        }else{
            ReviseResultInfo reviseResultInfo = questionInfo.getReviseResultResponse();
            noRevise = reviseResultInfo!=null && !TextUtils.isEmpty( reviseResultInfo.getAnswerUrl() );
        }

        //纠错，七天内显示, 批阅结果不能申诉
        if( !noRevise && status>=0 && !workDetail.isOverdue() && !TextUtils.isEmpty( answerUrl ) && ( isReviseError || AppConst.UPLOAD_TYPE_MARKED!=questionInfo.getUploadType() )){
            setVisibility( View.VISIBLE );
            if( qstatus == QuestionInfo.CORRECTSTATUS_UNAPPLY || qstatus == QuestionInfo.CORRECTSTATUS_NONE ){
                appealStatusLayout.setVisibility(View.GONE);
                appealLayout.setVisibility(View.VISIBLE);
            }else{
                appealStatusLayout.setVisibility(View.VISIBLE);
                appealLayout.setVisibility(View.GONE);
                if( qstatus == QuestionInfo.CORRECTSTATUS_APPLYED ){        //已申请
                    appealStatusView.setText( getContext().getText( R.string.ddwork_correctapply ) );
                    //appealStatusView.setTextColor( resources.getColor(R.color.color_999999));
                    //appealStatusView.setBackground( resources.getDrawable(R.drawable.bg_crect_correcterred));
                }else{
                    appealStatusView.setText( getContext().getText( R.string.ddwork_corrected ) );
                    //appealStatusView.setTextColor( resources.getColor(R.color.color_9AC878));
                    //appealStatusView.setBackgroundColor( resources.getColor(R.color.transparent)  );
                }
            }
        }else{      //都不显示
            //appealBtn.setVisibility( View.GONE );
            //appealStatusView.setVisibility( View.GONE );
            setVisibility( View.GONE );
        }
    }

    @Override
    public void onClick(View view) {
        if( view.getId() == R.id.layout_ddwork_correctErrorBtn ){
            if( questionInfo==null )return;

            String[] params = new String[2];
            String data = getQuestionIndex(position, params);

            //需要包含 , , ， ，，， ，
            JSONObject json = new JSONObject();
            json.put("examName", workDetail.getWorkName() );
            json.put("questionType", params[0] );
            json.put("indexInPaper", params[1] );
            json.put("questionId", questionInfo.getQuestionId() );
            json.put("teacherId",  "teacherId" );

            final ReviseResultInfo reviseResultInfo = questionInfo.getReviseResultResponse();
            if( isReviseError && reviseResultInfo!=null ){
                json.put("correctTime", reviseResultInfo.getCorrectTime() );
                //json.put("reviseId",reviseResultInfo.getReviseId() );
                json.put("examId", reviseResultInfo.getReviseId() );
            }else{
                json.put("correctTime", questionInfo.getCorrectTime() );
                json.put("examId", workDetail.getWorkId() );
            }

            CorrectErrorDialog dialog = new CorrectErrorDialog( getContext(), R.style.FullTransparentDialog );
            dialog.setData( data, isReviseError, json, false, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if( isReviseError && reviseResultInfo!=null ){
                        reviseResultInfo.setCorrectionStatus( 2 );
                    }else{
                        questionInfo.setCorrectErrorStatus( 2 );
                    }
                    if( mAppealListener!=null )mAppealListener.appealSuccessListener();
                }
            }, null);
            dialog.show();
        }
    }
    //-------------------------------------------------------------------------
    private void initData(Context context){

        inflate( context, GlobalData.isPad()?R.layout.view_ddwork_appealview:R.layout.view_ddwork_appealview_phone, this );


        appealLayout = (RelativeLayout) findViewById( R.id.layout_ddwork_correctErrorBtn ) ;
        appealLayout.setOnClickListener( this );
        appealStatusLayout = (RelativeLayout)findViewById( R.id.layout_ddwork_correctErrorStatus  );

        appealStatusView    = (TextView) findViewById( R.id.item_ddwork_correctErrorStatus );
    }

    private String getQuestionIndex( int position, String params[] ){
        StringBuilder sb = new StringBuilder();
        if( questionInfo == null ) {
            params[0] = "";
            params[1] = "";
            return "";
        }
        if( isLearnMaterial ){      //教辅

            int pageNum = questionInfo.getPageNum()+1; //getPageNum( currPageNum );
            String materialName = getLearnMaterialName(pageNum);
            String pageInfo = getQuestionPageName( position );

            sb.append("<").append( materialName ).append(">");
            sb.append( pageInfo ).append(" ").append(" 第 ").append( questionInfo.getNumInPaper() ).append(" 题");
            params[0] = "<"+materialName+">"+pageInfo;
            params[1] = questionInfo.getNumInPaper();
            return sb.toString();
        }

        //普通作业

        //
        //题型中文名称
        String typename_ch = DDWorkUtil.getQuestionTypeName(questionInfo.getQuestionType());
        String questionIndex = questionInfo.getNumInPaper();

        sb.append( questionInfo.getIndexQuestionType() ).append("、").append( typename_ch ).append(" 第 ").append( questionIndex ).append(" 题");

        params[0] = typename_ch;
        params[1] = String.valueOf(questionIndex);
        return sb.toString();
    }

    //查找指定项的教辅名称
    private String getLearnMaterialName( int position ){
        List<LocalPageInfo> pages = workDetail.getPageInfo();
        if( position > pages.size() || position <= 0 ) return "";

        for( int i=position-1; i>=0; i-- ){
            LocalPageInfo pageInfo = pages.get(i);
            String name = pageInfo.getLearnMaterialName();
            if( !TextUtils.isEmpty(pageInfo.getChapterName()) ) name += pageInfo.getChapterName();
            if( !TextUtils.isEmpty( name ) )
                return name;
        }
        return "";
    }
    //查找指定项的页码名称
    private String getQuestionPageName( int position ){
        List<LocalPageInfo> pages = workDetail.getPageInfo();
        if( position < 0 || pages==null ) return "";

        ArrayList<LocalQuestionInfo> qlist = pages.get(0).getQuestions();
        if( position+1 > qlist.size() ) return "";

        for( int i=position; i>=0; i-- ){
            LocalQuestionInfo questionInfo = qlist.get(i);
            if( !TextUtils.isEmpty(questionInfo.getPageInfo()) )
                return questionInfo.getPageInfo();
        }
        return "";
    }

    //申诉成功回调接口
    public interface AppealListener{
        void appealSuccessListener();
    }

}
