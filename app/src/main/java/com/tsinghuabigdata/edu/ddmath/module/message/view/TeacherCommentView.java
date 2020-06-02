package com.tsinghuabigdata.edu.ddmath.module.message.view;


import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.bean.UserDetailinfo;
import com.tsinghuabigdata.edu.ddmath.module.message.bean.MessageInfo;
import com.tsinghuabigdata.edu.ddmath.parent.util.ReportDetailUtils;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.DateUtils;
import com.tsinghuabigdata.edu.ddmath.view.ProgressWebView;

import java.util.Locale;


public class TeacherCommentView extends LinearLayout {


    //老师评语
    //private TextView userNameView;
    private TextView teacherCommentView;
    private TextView timeView;

    private ProgressWebView progressWebView;

    private MessageInfo messageInfo;

    public TeacherCommentView(Context context) {
        super(context);
        initData(context);
    }

    public TeacherCommentView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initData(context);
    }

    public TeacherCommentView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initData( context );
    }

    public void setData(MessageInfo messageInfo){
        this.messageInfo = messageInfo;
        loadTeacherData();
    }

    private void loadTeacherData(){

        //先加载显示周报告
        com.alibaba.fastjson.JSONObject extentObj = JSON.parseObject(messageInfo.getRemark());

        if( !extentObj.containsKey("extent") ) return;
        extentObj = extentObj.getJSONObject("extent");

        final long createTime = extentObj.getLong("createTime");
        final boolean allRight = extentObj.getBoolean("allRight");
        final boolean allUnSubmit = extentObj.getBoolean("allUnSubmit");
        final String studentId = extentObj.getString("studentId");
        final String reportId = extentObj.getString("reportId");
        String url = ReportDetailUtils.getWeekReportUrl( studentId, reportId, false);
        url += "&status=share";
        progressWebView.loadUrl(url);

        timeView.setText(DateUtils.format( createTime, "yyyy年MM月dd日"));
        if( allRight ){
            String text = "      已为你定制了一份孩子本周的学习报告，请你和孩子认真阅读。孩子本周的知识点全部掌握了，棒棒哒，请多表扬孩子哦～";
            teacherCommentView.setText( text );
        }else if(allUnSubmit){
            String text = "      你的孩子本周没有提交作业，请督促孩子及时完成并提交。这样便于我们为你整理一份完整的学习报告，帮助孩子提升学习效果。";
            teacherCommentView.setText( text );
        }else{
            String text = "      已为你定制了一份孩子本周的学习报告，请你和孩子认真阅读。建议你督促孩子完成自己的错题再练或错题订正，进行查漏补缺，攻克薄弱知识点。如还有时间，可去练习源自中考常考题的变式训练，考试更加自信哦。";
            teacherCommentView.setText( text );

//            //查询信息
//            new MyWorldModel().queryWeekErrorStatus( studentId, createTime, new RequestListener<WeekErrorStatus>() {
//
//                @Override
//                public void onSuccess(WeekErrorStatus res) {
//
//                    int weekRefineCount = 0;
//                    int weekVariantCount = 0;
//                    if( res.getWeekExercises()!=null && res.getWeekExercises().size()>0 )
//                        weekRefineCount =  res.getWeekExercises().size();
//                    if( res.getExclusivePapers()!=null && res.getExclusivePapers().size()>0 )
//                        weekVariantCount = res.getExclusivePapers().size();
//
//                    teacherCommentView.setVisibility(VISIBLE);
//
//                    //默认两者都没有
//                    String text = "      ";
//
//                    //两者都有
//                    if( weekRefineCount > 0 && weekVariantCount > 0 ){
//                        text += "已为你定制了一份孩子本周的学习报告，请你和孩子认真阅读。建议你督促孩子完成自己的错题再练或错题订正，进行查漏补缺，攻克薄弱知识点。如还有时间，可去练习源自中考常考题的变式训练，考试更加自信哦。";
//                    }
//                    //有错题再练本，没有变式训练本
//                    else if( weekVariantCount > 0 ){
//                        text += "已为你定制了一份孩子本周的学习报告，请你和孩子认真阅读。建议你督促孩子完成自己的错题再练或错题订正，进行查漏补缺，攻克薄弱知识点。如还有时间，可去练习源自中考常考题的变式训练，考试更加自信哦。";
//                    }else{
//                        text += "你的孩子本周没有提交作业，请督促孩子及时完成并提交。这样便于我们为你整理一份完整的学习报告，帮助孩子提升学习效果。";
//                    }
//                    teacherCommentView.setText( text );
//                }
//
//                @Override
//                public void onFail(HttpResponse<WeekErrorStatus> response, Exception ex) {
//                    //teacherCommentView.setVisibility(GONE);
//                }
//            });
        }
    }

    //-------------------------------------------------------------------------
    private void initData( Context context ){
        //
        inflate( context, R.layout.view_teacher_comment, this );

        TextView userNameView =  findViewById( R.id.tv_username );
        teacherCommentView = findViewById( R.id.tv_teacher_comment );
        timeView = findViewById(R.id.tv_time);
        progressWebView = findViewById(R.id.progress_webview);
        progressWebView.setWebViewExpand(true);
        progressWebView.setListener(new ProgressWebView.FaultListener() {
            @Override
            public void retry() {
                loadTeacherData();
            }
        });

        UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
        if( detailinfo != null ){
            userNameView.setText( String.format(Locale.getDefault(),"%s同学家长：",detailinfo.getReallyName()) );
        }
    }

}

