package com.tsinghuabigdata.edu.ddmath.module.ddwork.dialog;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.tsinghuabigdata.edu.ddmath.MVPModel.MyStudyModel;
import com.tsinghuabigdata.edu.ddmath.MVPModel.RequestListener;
import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.activity.SinglePicturePreviewActivity;
import com.tsinghuabigdata.edu.ddmath.bean.MyTutorClassInfo;
import com.tsinghuabigdata.edu.ddmath.bean.UserDetailinfo;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.constant.AppConst;
import com.tsinghuabigdata.edu.ddmath.event.ErrorReviseEvent;
import com.tsinghuabigdata.edu.ddmath.event.RefreshBrowerEvent;
import com.tsinghuabigdata.edu.ddmath.event.RefreshDaycleanEvent;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.QuestionInfo;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.ReviseResultInfo;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.SubmitReviseBean;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.view.ChooseAnswerView;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.view.ErrorReasonView;
import com.tsinghuabigdata.edu.ddmath.module.errorbook.NumberUtil;
import com.tsinghuabigdata.edu.ddmath.module.mylearn.bean.UploadImage;
import com.tsinghuabigdata.edu.ddmath.module.mystudybean.BuySuiteActivity;
import com.tsinghuabigdata.edu.ddmath.module.product.ProductUtil;
import com.tsinghuabigdata.edu.ddmath.module.product.bean.ProductUseTimesBean;
import com.tsinghuabigdata.edu.ddmath.module.studycheat.dailog.WorkAbilityDialog;
import com.tsinghuabigdata.edu.ddmath.module.xbook.XBookCameraActivity;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.AlertManager;
import com.tsinghuabigdata.edu.ddmath.util.AppLog;
import com.tsinghuabigdata.edu.ddmath.util.BitmapUtils;
import com.tsinghuabigdata.edu.ddmath.util.ContextUtils;
import com.tsinghuabigdata.edu.ddmath.util.EventBusUtils;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.PicassoUtil;
import com.tsinghuabigdata.edu.ddmath.util.ToastUtils;
import com.tsinghuabigdata.edu.ddmath.view.MyProgressDialog;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.util.List;


/**
 * 错题订正 Dialog
 */
public class ReviseErrorDialog extends Dialog implements View.OnClickListener {

//    private final static int MAX_COUNT = 100;
//    //关闭按钮
//    //private ImageView closeBtn;
//

    private MyProgressDialog progressDialog;

    //选择题
    private LinearLayout chooseLayout;
    private ChooseAnswerView chooseAnswerView;

    //其他题
    private LinearLayout otherLayout;
    private ImageView cameraImage;
    private Button    cameraReBtn;
    private LinearLayout  cameraBtn;

    //错误原因
    private ErrorReasonView reasonView;

    private TextView totalView;
    //
    //private Button cancelBtn;
    private Button enterBtn;

    //访问网络
    private MyStudyModel myStudyModel;


    //传递过来的信息
    private String mExamId;
    private QuestionInfo mQuestionInfo;

    private View.OnClickListener cancelBtnListener;
    private View.OnClickListener enterBtnListener;

    //private Dialog self;
    private boolean isChoiceQuestion;
    private UploadImage mUploadImage;
    private CameraImageReceiver cameraImageReceiver;
    private Context mContext;
    //private int correctTotal = 0;

    private boolean bQuit = false;
    public static   String questiontitle=null;
    public static  int  overdo=1;
    public static  int  lastone=1; //是1的时候代表最后一题
    public static boolean isScored=false;

//    public ReviseErrorDialog(Context context) {
//        super(context);
//        mContext = context;
//        initData();
//    }

    private ReviseErrorDialog(Context context, int theme) {
        super(context, theme);
        mContext = context;
        initData();
    }
    public static void showReviseErrorDialog(Context context, QuestionInfo questionInfo, String examId, String name,boolean isScore,int overdue,int islastone, boolean fromErrQuestion, int correctTotal, String from, View.OnClickListener el, View.OnClickListener cl){
        overdo=overdue;
        isScored=isScore;
        questiontitle=name;
        lastone=islastone;
        ReviseErrorDialog dialog = new ReviseErrorDialog( context, R.style.FullTransparentDialog );
        dialog.setData( questionInfo, examId, fromErrQuestion, correctTotal, el, cl );
        dialog.show();
        MobclickAgent.onEvent( context, from );
    }
//    public static void showReviseErrorDialog(Context context, QuestionInfo questionInfo, String examId, View.OnClickListener el, View.OnClickListener cl){
//        ReviseErrorDialog dialog = new ReviseErrorDialog( context, R.style.FullTransparentDialog );
//        dialog.setData( questionInfo, examId, el, cl );
//        dialog.show();
//    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_revise_closebtn: {
                if( cancelBtnListener!=null ) cancelBtnListener.onClick( v );
                quit();
                break;
            }
            case R.id.dialog_revise_cameraimage:{           //预览图片
                if( TextUtils.isEmpty( mUploadImage.getLocalpath() ) )
                    return;
                SinglePicturePreviewActivity.startSinglePicturePreviewActivity(getContext(), mUploadImage.getLocalpath(), null, null);
                break;
            }

            case R.id.dialog_revise_cameralayout:
            case R.id.dialog_revise_cameraRebtn:{             //拍照or重拍
                Intent intent = new Intent( getContext(), XBookCameraActivity.class );
                intent.putExtra("subject","数学");
                intent.putExtra("qtype", "订正错题" );
                intent.putExtra("ctype", AppConst.TYPE_EDIT );      //没有剪刀
                getContext().startActivity( intent );
                break;
            }
            case R.id.dialog_revise_cancelbtn: {            //取消

                if( cancelBtnListener!=null ) cancelBtnListener.onClick( v );
                quit();
                break;
            }
            case R.id.dialog_revise_enterbtn: {             //提交

                enterBtn.setEnabled( false );
                //先检查有没有次数
                boolean succ = ProductUtil.checkProductUseTimes( null, AppConst.PRIVILEGE_QUESTION_DAYCLEAR, new RequestListener<List<ProductUseTimesBean>>() {
                    @Override
                    public void onSuccess(List<ProductUseTimesBean> list) {
                        if( list==null || list.size() == 0 || list.get(0) == null ){
                            ToastUtils.show( mContext, R.string.relogin );
                            enterBtn.setEnabled( true );
                            return;
                        }
                        ProductUseTimesBean bean = list.get(0);
                        //已有订正次数， -1 是无限次
                        if( bean.getUseTimes() > 0 || bean.getUseTimes()==-1 ){
                            submitApply();
                        }
                        //没有订正次数，引导用户去兑换
                        else{
                            enterBtn.setEnabled( true );
                            UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
                            if( detailinfo!=null && !detailinfo.enableBuySuite() ){     //不允许买
                                ToastUtils.show( mContext, mContext.getResources().getString(R.string.tj_nobuy_tips) );
                            }else{  //跳转到购买界面
//                                ToastUtils.showToastCenter( getContext(), "请先购买套餐后，再使用您的专属错题订正。\n正在为您跳转到购买套餐界面..." );
//
//                                enterBtn.postDelayed(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        BuySuiteActivity.startBuySuiteActivity(mContext,BuySuiteActivity.DAY_CLEAR,AppConst.PRIVILEGE_QUESTION_DAYCLEAR );
//                                    }
//                                },2100);
                                AlertManager.showCustomImageBtnDialog(mContext, "你的错题订正特权已失效，购买套餐继续学习吧～", "购买套餐", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        BuySuiteActivity.startBuySuiteActivity(mContext, BuySuiteActivity.DAY_CLEAR, AppConst.PRIVILEGE_QUESTION_DAYCLEAR);
                                    }
                                }, null);

                            }
                        }
                    }

                    @Override
                    public void onFail(HttpResponse<List<ProductUseTimesBean>> response, Exception ex) {
                        AlertManager.showErrorInfo( getContext(), ex );
                        enterBtn.setEnabled( true );
                    }
                });

                //启动检查失败
                if( !succ ) ToastUtils.show( mContext, R.string.relogin );

                break;
            }
            default:
                break;
        }
    }

    public void onBackPressed(){
//        if( cancelBtnListener!=null ){
//            cancelBtnListener.onClick( this, 0 );
//        }
        quit();
    }

    /**
     * @param questionInfo          题目信息
     * @param examId                作业ID
     */
    public void setData(QuestionInfo questionInfo, String examId, boolean fromErrQuestion, int correctTotal, View.OnClickListener el, View.OnClickListener cl ) {

        //if( !fromErrQuestion && !GlobalData.isPad() ){
        Window window = getWindow();
        if( window!=null ){
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        //}

        String questionType = questionInfo.getQuestionType();
        if( "choice".equals( questionType ) || "mutichoice".equals( questionType ) ){       //选择题
            chooseLayout.setVisibility( View.VISIBLE );
            otherLayout.setVisibility( View.GONE );
            isChoiceQuestion = true;
        }else{
            chooseLayout.setVisibility( View.GONE );
            otherLayout.setVisibility( View.VISIBLE );
            isChoiceQuestion = false;
        }

        //this.correctTotal = correctTotal;
        if( correctTotal >= 0 )
            totalView.setText( String.format( getContext().getResources().getString( R.string.revise_total ), NumberUtil.approximateNumber(correctTotal)));
        else
            totalView.setText("");

        mQuestionInfo = questionInfo;
        mExamId = examId;
        enterBtnListener  = el;
        cancelBtnListener = cl;
    }

    //--------------------------------------------------------------------------
    private void initData() {
        //self = this;
        setContentView(GlobalData.isPad()?R.layout.dialog_reviseerror_layout:R.layout.dialog_reviseerror_layout_phone);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            if( window!=null ){
                window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            }
        }
        ImageView closeView = (ImageView) findViewById(R.id.dialog_revise_closebtn);
        closeView.setOnClickListener(this);

        chooseLayout= (LinearLayout)findViewById( R.id.dialog_revise_chooselayout );
        chooseAnswerView = (ChooseAnswerView) findViewById( R.id.dialog_revise_chooseanswerview );

        //
        otherLayout = (LinearLayout)findViewById( R.id.dialog_revise_otherlayout );
        cameraImage = (ImageView)findViewById( R.id.dialog_revise_cameraimage );
        cameraImage.setOnClickListener( this );
        cameraReBtn = (Button)findViewById( R.id.dialog_revise_cameraRebtn );
        cameraReBtn.setOnClickListener( this );
        cameraBtn   = (LinearLayout) findViewById( R.id.dialog_revise_cameralayout );
        cameraBtn.setOnClickListener( this );

        reasonView = (ErrorReasonView) findViewById( R.id.dialog_revise_reasonview );
        reasonView.setReasons( R.array.question_reason_ch );

        totalView = (TextView)findViewById( R.id.dialog_revise_total );

        Button cancelBtn = (Button) findViewById(R.id.dialog_revise_cancelbtn);
        cancelBtn.setOnClickListener( this );
        enterBtn = (Button) findViewById(R.id.dialog_revise_enterbtn);
        enterBtn.setOnClickListener( this );

        myStudyModel = new MyStudyModel();
        mUploadImage = new UploadImage( UploadImage.TYPE_IMAGE );
        startAddImageReceiver();

        progressDialog = new MyProgressDialog(mContext);
    }

    //
    private void quit(){
        bQuit = true;
        stopAddImageReceiver();
        dismiss();
    }

    private int tryCount = 0;
    private void submitApply(){

        final UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
        if( detailinfo == null ){
            ToastUtils.showShort( getContext(), "请先登录!");
            enterBtn.setEnabled( true );
            return;
        }

        if( isChoiceQuestion ){
            //判断选择题是否已答
            String answer = chooseAnswerView.getChooseAnswer();
            if( TextUtils.isEmpty(answer) ){
                ToastUtils.show( getContext(), "请选择答案", Toast.LENGTH_SHORT );
                enterBtn.setEnabled( true );
                return;
            }

            //生成图片，并开始上传图片
            Bitmap bitmap = BitmapUtils.loadBitmapFromViewBySystem( chooseAnswerView.getMainLayout() );
            if( bitmap == null ){
                ToastUtils.show( getContext(), "生成答案图片失败，请重试!", Toast.LENGTH_SHORT );
                enterBtn.setEnabled( true );
                return;
            }
            //如果是透明的背景，转出白色背景
            bitmap = BitmapUtils.transToWhiteBitmap( bitmap );

            String filename = ContextUtils.getCacheDir(AppConst.IMAGE_DIR) + "/" + System.currentTimeMillis()+AppConst.IMAGE_SUFFIX_NAME;
            String path = BitmapUtils.saveImage( filename, bitmap );
            if( path == null ){
                ToastUtils.show( getContext(), "生成答案图片失败，请重试!", Toast.LENGTH_SHORT );
                enterBtn.setEnabled( true );
                return;
            }

            //开始上传
            mUploadImage.previewUploadLocalpath( path );
        }else{
            //判断是否拍照了
            if(TextUtils.isEmpty(mUploadImage.getLocalpath()) ){
                ToastUtils.show( getContext(), "请拍照", Toast.LENGTH_SHORT );
                enterBtn.setEnabled( true );
                return;
            }
        }

        //判断错误原因是否选择
        if( TextUtils.isEmpty(reasonView.getSelectedData()) ){
            ToastUtils.show( getContext(), "请选择错误原因", Toast.LENGTH_SHORT );
            enterBtn.setEnabled( true );
            return;
        }

        progressDialog.setMessage("提交中...");
        progressDialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {

                tryCount = 0;
                while (true){

                    //判断图片是否提交成功
                    boolean success = UploadImage.ST_SUCC == mUploadImage.getUploadStatus() && !TextUtils.isEmpty(mUploadImage.getUrl());

                    if( !success ){     //不成功，

                        if( UploadImage.ST_ERROR == mUploadImage.getUploadStatus() ){       //图片上传失败
                            tryCount++;
                            if(  tryCount >= 3 ) {     //最多三次机会

                                if( bQuit )return;

                                reasonView.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        enterBtn.setEnabled( true );
                                        ToastUtils.show( getContext(), "网络异常，图片上传失败", Toast.LENGTH_SHORT );
                                        progressDialog.dismiss();
                                    }
                                });
                                return;
                            }else
                                mUploadImage.startUpload();     //再次上传
                        }else if( UploadImage.ST_NONE == mUploadImage.getUploadStatus() ){
                            mUploadImage.startUpload();     //开始上传
                        }else if( UploadImage.ST_SUCC == mUploadImage.getUploadStatus() ){
                            mUploadImage.previewUploadLocalpath( mUploadImage.getLocalpath() );     //重新上传
                        }       //正在上传中，不处理

                        SystemClock.sleep( 100 );
                        continue;
                    }

                    //-----------------------------------------------------------------------
                    //已提交成功
//
                    JSONObject questionJson = new JSONObject();
                    if( isChoiceQuestion ){
                        questionJson.put("rightAnswer", chooseAnswerView.getChooseAnswer() );           //选择题，对错
                    }

                    questionJson.put("path", mUploadImage.getUrl() );                 //图片在服务器的路径
                    questionJson.put("questionId", mQuestionInfo.getQuestionId() );
                    questionJson.put("questionType", mQuestionInfo.getQuestionType() );
                    questionJson.put("examId", mExamId );
                    questionJson.put("wrongReason", reasonView.getSelectedData() );
                    MyTutorClassInfo currentClassInfo = AccountUtils.getCurrentClassInfo();
                    if (currentClassInfo != null) {
                        questionJson.put("classId", currentClassInfo.getClassId() );
                    }

                    AppLog.d("dfsdfsadfsdf " + questionJson.toJSONString() );

                    //setSubmitStatus( 1 );
                    myStudyModel.applyReviseErrorQuestion( detailinfo.getStudentId(), mExamId, mQuestionInfo.getQuestionId(), questionJson.toJSONString(), new RequestListener() {
                        @Override
                        public void onSuccess(Object res) {

                            //更新首页订正状态
                            EventBusUtils.postDelay(new ErrorReviseEvent(),new Handler());

                            progressDialog.dismiss();
                            if( bQuit )return;

                            SubmitReviseBean submitReviseBean = null;
                            if( res instanceof SubmitReviseBean )
                                submitReviseBean = (SubmitReviseBean)res;

                            if( submitReviseBean!=null && !TextUtils.isEmpty(submitReviseBean.getReviseId()) ){

                                ReviseResultInfo reviseResultInfo = mQuestionInfo.getReviseResultResponse();
                                if( reviseResultInfo == null ){
                                    reviseResultInfo = new ReviseResultInfo();
                                    mQuestionInfo.setReviseResultResponse( reviseResultInfo );
                                }

                                reviseResultInfo.setAnswerUrl( mUploadImage.getUrl() );
                                reviseResultInfo.setCorrectionStatus( QuestionInfo.CORRECTSTATUS_UNAPPLY );
                                reviseResultInfo.setReviseLocalpath( mUploadImage.getLocalpath() );

                                //错题原因
                                reviseResultInfo.setErrorAnalysis( reasonView.getSelectedData() );

                                if (enterBtnListener != null) enterBtnListener.onClick( enterBtn );
                                //ToastUtils.showToastCenter( getContext(), "提交成功！");
                                //setSubmitStatus( 2 );

                                //通知错题本日日清，进行刷新
                                EventBusUtils.postDelay(new RefreshDaycleanEvent(),new Handler(),4000);
                                //更新错题浏览， 进行刷新
                                EventBusUtils.postDelay(new RefreshBrowerEvent(),new Handler(),4000);

                                quit();
                                if(isScored){
                                    WorkAbilityDialog.showScoreDialog( mContext, overdo ,questiontitle,lastone, submitReviseBean.getAbility());
                                }else {
                                    //显示获得的学力
                                    WorkAbilityDialog.showDialog( mContext, submitReviseBean.getAbility() );
                                }

//                                new Thread(new Runnable() {         //2s后自动关闭
//                                    public void run() {
//                                        SystemClock.sleep(2000);
//                                        if (self != null && self.isShowing()) {
//                                            quit();
//                                        }
//                                    }
//                                }).start();

                            }else{
                                ToastUtils.showToastCenter( getContext(), "提交失败，请重试！");
                                //setSubmitStatus( 3 );
                                enterBtn.setEnabled( true );
                            }
                        }

                        @Override
                        public void onFail(HttpResponse response, Exception ex) {
                            progressDialog.dismiss();
                            if( bQuit )return;
                            if( "Data is exists ".equals( response.getMessage())  ){
                                ReviseResultInfo reviseResultInfo = mQuestionInfo.getReviseResultResponse();
                                if( reviseResultInfo == null ){
                                    reviseResultInfo = new ReviseResultInfo();
                                    mQuestionInfo.setReviseResultResponse( reviseResultInfo );
                                }
                                reviseResultInfo.setCorrectionStatus( QuestionInfo.CORRECTSTATUS_UNAPPLY );
                                ToastUtils.showToastCenter( getContext(), "此题已被订正");
                                quit();
                            }else{
                                AlertManager.showErrorInfo( getContext(), ex );
                            }
                            enterBtn.setEnabled( true );
                        }
                    });
                    //上传完成，退出
                    return;
                }
            }
        }).start();
    }

    /*private boolean judgeChooseAnswer( String answer ){
        List<StdAnswerBean> list = mQuestionInfo.getStdAnswers();
        if( TextUtils.isEmpty(answer) || list == null || list.size() == 0 ) return false;
        for( StdAnswerBean bean : list ){
            if( answer.equals( bean.getContent() ) )
                return true;
        }
        return false;
    }*/
    //-------------------------------------------------------------------------------------
    /**
     * 监听拍照图片完成
     */
    class CameraImageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // 监听退出消息
            if ( AppConst.ACTION_XBOOK_ADD.equals(intent.getAction()) ) {

                String path = intent.getStringExtra( "imagepath" );
                String qtype= intent.getStringExtra( "qtype" );
                if( TextUtils.isEmpty(path) ){
                    AppLog.i(" xbook add image params error. path="+path+",,qtype=" + qtype );
                }else{
                    cameraBtn.setVisibility( View.GONE );
                    cameraReBtn.setVisibility( View.VISIBLE );
                    //开始预上传
                    mUploadImage.previewUploadLocalpath( path );
                    //显示图片
                    PicassoUtil.getPicasso( getContext()).load(new File(path)).error(R.drawable.ic_broken_image).placeholder(R.drawable.ic_temporary_image).into( cameraImage );
                }
            }
        }
    }

    private void startAddImageReceiver(){
        cameraImageReceiver = new CameraImageReceiver();
        IntentFilter intentFilter = new IntentFilter( AppConst.ACTION_XBOOK_ADD );

        AppLog.d("dfdfdgfhfjh   Context = " + mContext );
        mContext.registerReceiver( cameraImageReceiver, intentFilter);
    }
    private void stopAddImageReceiver(){
        mContext.unregisterReceiver( cameraImageReceiver );
    }

}
