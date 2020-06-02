package com.tsinghuabigdata.edu.ddmath.module.ddwork.view;


import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.DDWorkDetail;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.LocalQuestionInfo;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.QuestionInfo;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.ReviseResultInfo;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;

/**
 * 用户答案图片及批改信息展示
 */

public class QuestionAnswerImageView extends LinearLayout {

    //对象
    private ItemTitleView titleView;                     //标题

    private LinearLayout   mainLayout;
    private CorrectImage   answerImage;               //用户答案
    private RelativeLayout noAnswerImage;             //没有答案时，显示未拍照 默认图
    private ImageView      cheerImage;                //加油

    private ImageView      noCameraImage;           //未拍照
    private ImageView      dealingImame;            //图片剪切中

    public QuestionAnswerImageView(Context context) {
        super(context);
        initData(context);
    }

    public QuestionAnswerImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initData(context);
    }

    public QuestionAnswerImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initData(context);
    }

    /**
     * 设置用户答案信息
     *
     * @param questionInfo 本地题目信息
     * @param submitStatus 作业提交状态
     * @param status       作业批改状态
     * @param bCameraed    当前页是否已拍照
     */
    public void setData(LocalQuestionInfo questionInfo, int submitStatus, int status, String ekpoint, boolean bCameraed ) {

        noAnswerImage.setVisibility(View.GONE);
        titleView.setVisibility(View.GONE);
        String path = questionInfo.getLocalpath();
        //原版教辅
        if( questionInfo.getLocalpathList()!=null && questionInfo.getLocalpathList().size()>0 ){
            path = questionInfo.getLocalpathList().get(0);
        }

        if (!TextUtils.isEmpty(path)) {      //加载本地图片
            answerImage.loadLocalIamge( path );
            answerImage.setVisibility(View.VISIBLE);
        } else if (!TextUtils.isEmpty(questionInfo.getAnswerUrl())) {    //网络图片
            answerImage.loadImage( questionInfo.getAnswerUrl(), questionInfo.getAnswerArea(), ekpoint );
            answerImage.setVisibility(View.VISIBLE);
        } else if( bCameraed ){     //已拍照，但是切图还没有完成
            answerImage.setVisibility(View.GONE);
            noAnswerImage.setVisibility(View.VISIBLE);
            noCameraImage.setVisibility( View.GONE);
            dealingImame.setVisibility( View.VISIBLE);
        } else {      //显示未拍照默认图
            answerImage.setVisibility(View.GONE);
            if (submitStatus >= DDWorkDetail.WORK_WAITCORRECT){
                noAnswerImage.setVisibility(View.VISIBLE);
                noCameraImage.setVisibility( View.VISIBLE);
                dealingImame.setVisibility( View.GONE);
            }
        }

        if (status < 0)
            cheerImage.setVisibility(View.INVISIBLE);
        else if (status == 0) {
            cheerImage.setVisibility(View.VISIBLE);
            cheerImage.setImageResource(R.drawable.img_cheer_up);
        } else {
            cheerImage.setVisibility(View.VISIBLE);
            cheerImage.setImageResource(R.drawable.img_great);
        }
    }

    /**
     * 原版教辅第二张图片显示
     * @param questionInfo 题目信息
     */
    public void setData( LocalQuestionInfo questionInfo ) {

        noAnswerImage.setVisibility(View.GONE);
        titleView.setVisibility(View.GONE);
        cheerImage.setVisibility(View.INVISIBLE);

        answerImage.setVisibility(View.GONE);

        String path = null;
        //原版教辅
        if( questionInfo.getLocalpathList()!=null && questionInfo.getLocalpathList().size()>1 ){
            path = questionInfo.getLocalpathList().get(1);
        }

        if (!TextUtils.isEmpty(path)) {      //加载本地图片
            answerImage.loadLocalIamge( path );
            answerImage.setVisibility(View.VISIBLE);
            setVisibility(View.VISIBLE);
        } else {
            setVisibility(View.GONE);
        }
    }
    /**
     * 设置用户订正答案信息
     *
     * @param questionInfo 本地题目信息
     * @param status       作业批改状态
     */
    public void setReviseData(QuestionInfo questionInfo, int status, String ekpoint) {

        noAnswerImage.setVisibility(View.GONE);
        titleView.setVisibility(View.VISIBLE);
        titleView.setTitle("订正图片");
        mainLayout.setVisibility(View.VISIBLE);

        String localpath = "";
        String url = "";
        String markarea = "";
        ReviseResultInfo reviseResultInfo = questionInfo.getReviseResultResponse();
        if( reviseResultInfo!=null ){
            url = reviseResultInfo.getAnswerUrl();
            markarea = reviseResultInfo.getAnswerArea();
            localpath=reviseResultInfo.getReviseLocalpath();
        }
        if( !TextUtils.isEmpty(localpath) ){      //加载本地图片
            answerImage.loadLocalIamge( localpath );
            answerImage.setVisibility( View.VISIBLE );
        }else if (!TextUtils.isEmpty(url)) {    //网络图片
            answerImage.loadImage(url, markarea, ekpoint );
            answerImage.setVisibility(View.VISIBLE);
        } else {      //不显示未拍照默认图
            answerImage.setVisibility(View.GONE);
            titleView.setVisibility(View.GONE);
            mainLayout.setVisibility(View.GONE);
        }

        if (status < 0)
            cheerImage.setVisibility(View.INVISIBLE);
        else if (status == 0) {
            cheerImage.setVisibility(View.VISIBLE);
            cheerImage.setImageResource(R.drawable.img_cheer_up);
        } else {
            cheerImage.setVisibility(View.VISIBLE);
            cheerImage.setImageResource(R.drawable.img_great);
        }
    }

    //-------------------------------------------------------------------------
    private void initData(Context context) {

        inflate(context, GlobalData.isPad() ? R.layout.view_ddwork_answeriamage : R.layout.view_ddwork_answeriamage_phone, this);

        titleView = (ItemTitleView) findViewById(R.id.item_ddwork_answertitle);
        mainLayout = (LinearLayout) findViewById(R.id.item_ddwork_answerlayout);
        answerImage = (CorrectImage) findViewById(R.id.item_ddwork_answerimage);
        noAnswerImage = (RelativeLayout) findViewById(R.id.item_ddwork_defaultimage);
        cheerImage = (ImageView) findViewById(R.id.item_ddwork_cheerimage);
        noCameraImage = (ImageView)findViewById( R.id.item_ddwork_nocamera_photo );
        dealingImame  = (ImageView)findViewById( R.id.item_ddwork_deling_photo );
    }
}
