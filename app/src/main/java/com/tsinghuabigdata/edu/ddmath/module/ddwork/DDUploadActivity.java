package com.tsinghuabigdata.edu.ddmath.module.ddwork;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.drawerlayout.widget.DrawerLayout;

import com.tsinghuabigdata.edu.ddmath.MVPModel.ErrorBookModel;
import com.tsinghuabigdata.edu.ddmath.MVPModel.LearnMaterialModel;
import com.tsinghuabigdata.edu.ddmath.MVPModel.MyStudyModel;
import com.tsinghuabigdata.edu.ddmath.MVPModel.PracticeModel;
import com.tsinghuabigdata.edu.ddmath.MVPModel.RequestListener;
import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.ZxApplication;
import com.tsinghuabigdata.edu.ddmath.activity.MainActivity;
import com.tsinghuabigdata.edu.ddmath.activity.RoboActivity;
import com.tsinghuabigdata.edu.ddmath.bean.LoginInfo;
import com.tsinghuabigdata.edu.ddmath.bean.MyTutorClassInfo;
import com.tsinghuabigdata.edu.ddmath.bean.UserDetailinfo;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.constant.AppConst;
import com.tsinghuabigdata.edu.ddmath.constant.AppRequestConst;
import com.tsinghuabigdata.edu.ddmath.dialog.CustomDialogNew;
import com.tsinghuabigdata.edu.ddmath.event.JumpEvent;
import com.tsinghuabigdata.edu.ddmath.event.JumpStudyBeanEvent;
import com.tsinghuabigdata.edu.ddmath.event.LookWorkEvent;
import com.tsinghuabigdata.edu.ddmath.event.RecorrectWorkEvent;
import com.tsinghuabigdata.edu.ddmath.event.RefreshCheckworkEvent;
import com.tsinghuabigdata.edu.ddmath.event.RefreshPracticeEvent;
import com.tsinghuabigdata.edu.ddmath.event.RefreshVariantEvent;
import com.tsinghuabigdata.edu.ddmath.event.RefreshWeektrainEvent;
import com.tsinghuabigdata.edu.ddmath.event.UploadMyWorkEvent;
import com.tsinghuabigdata.edu.ddmath.event.UploadScorePlanEvent;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.adapter.DDWorkPageAdapter;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.adapter.DDWorkQuestionAdapter;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.DDWorkDetail;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.LocalPageInfo;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.LocalQuestionInfo;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.LocalWorkInfo;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.WorkSubmitBean;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.view.GuideVideoView;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.view.NetworkMonitorView;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.view.QuestionListView;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.view.SelectErrorQuestionView;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.view.SelectQuestionView;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.view.SeriesHitView;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.view.WorkToolbar;
import com.tsinghuabigdata.edu.ddmath.module.exclusivepractice.bean.ShareBean;
import com.tsinghuabigdata.edu.ddmath.module.learnmaterial.LMCameraActivity;
import com.tsinghuabigdata.edu.ddmath.module.learnmaterial.view.LmGuideView;
import com.tsinghuabigdata.edu.ddmath.module.message.MessageUtils;
import com.tsinghuabigdata.edu.ddmath.module.mycenter.dialog.ShareDialog;
import com.tsinghuabigdata.edu.ddmath.module.mycenter.fragment.UserCenterFragment;
import com.tsinghuabigdata.edu.ddmath.module.myscore.ScoreEventID;
import com.tsinghuabigdata.edu.ddmath.module.mystudybean.BuySuiteActivity;
import com.tsinghuabigdata.edu.ddmath.module.neteaseim.base.util.NetworkUtil;
import com.tsinghuabigdata.edu.ddmath.module.product.ProductUtil;
import com.tsinghuabigdata.edu.ddmath.module.product.bean.ProductUseTimesBean;
import com.tsinghuabigdata.edu.ddmath.module.studycheat.dailog.WorkAbilityDialog;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.AlertManager;
import com.tsinghuabigdata.edu.ddmath.util.AppLog;
import com.tsinghuabigdata.edu.ddmath.util.DrawerLayoutUtil;
import com.tsinghuabigdata.edu.ddmath.util.EventBusUtils;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.PreferencesUtils;
import com.tsinghuabigdata.edu.ddmath.util.ToastUtils;
import com.tsinghuabigdata.edu.ddmath.view.LoadingPager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.Nonnull;

import static com.tsinghuabigdata.edu.ddmath.constant.AppConst.WORK_TYPE_CLASSIC;
import static com.tsinghuabigdata.edu.ddmath.constant.AppConst.WORK_TYPE_ORIGINALMATERIAL;
import static com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.LocalWorkInfo.WORK_COMMITED;
import static com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.LocalWorkInfo.WORK_COMMITFAIL;
import static com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.LocalWorkInfo.WORK_COMMITING;
import static com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.LocalWorkInfo.WORK_NONE;

/**
 * 豆豆作业提交界面
 */
public class DDUploadActivity extends RoboActivity implements WorkCommitListener, View.OnClickListener, AdapterView.OnItemClickListener, PageChangeListener, SelectQuestionView.FilterListener, DrawerLayout.DrawerListener {

    public static final String PARAM_DDWORKID = "ddworkid";
    public static final String PARAM_RECORDID = "recordid";
    public static final String PARAM_TITLE    = "title";
    public static final String PARAM_SHAREURL = "shareurl";
    public static final String PARAM_HASBUY   = "hasbuy";
    public static final String PARAM_PRIVILEDGEID = "priviledgeid";
    public static final String PARAM_PRODUCTID = "productid";

    public static final String PARAM_LMATERIAL = "learnmaterial";       //原版教辅类型,仅仅用来显示布置作业到提交完成后的状态
    public static final String PARAM_BOOKID    = "bookid";
    public static final String PARAM_BOOKPAGES = "bookpages";
    public static final String PARAM_BOOKRATE  = "bookrate";
    public static final String PARAM_UPLOADTYPE  = "uploadtype";

    public static final String PARAM_SCORED   = "isscored";     //是不是定制学跳转过来的
    public static final String PARAM_OVERDO   = "isoverdo";     //有没有逾期
    public static final String PARAM_SHOWTITLE  = "showtitle";   //需要的标题
    public static final String PARAM_DEDINE ="isdefine" ;    //区分周练习还是自定义


    @ViewInject(R.id.ddwork_list_mainlayout)
    private LinearLayout mainLayout;
    @ViewInject(R.id.ddwork_list_drawerlayout)
    private DrawerLayout drawerLayout;

    @ViewInject(R.id.ddwork_list_toolbar)
    private WorkToolbar workToolbar;

    //@ViewInject(R.id.ddwork_list_networkmonitor)
    private NetworkMonitorView monitorView;

    @ViewInject(R.id.ddwork_list_lmguideview)
    private LmGuideView lmGuideView;

    @ViewInject(R.id.loadingPager)
    private LoadingPager mLoadingPager;

    //页List
    @ViewInject(R.id.ddwork_list_worklist)
    private ListView pageListView;

    @ViewInject(R.id.ddwork_list_cameralayout)
    private RelativeLayout mCameraLayout;
    @ViewInject(R.id.ddwork_list_camerabtn)
    private LinearLayout   mCameraBtn;

    //作业状态
    @ViewInject(R.id.ddwork_list_workstatuslayout)
    private RelativeLayout workStatusLayout;
    @ViewInject(R.id.ddwork_list_workstatus)
    private TextView       workStatusView;
    @ViewInject(R.id.ddwork_list_workimage)
    private ImageView      workImageView;

    @ViewInject(R.id.ddwork_list_qustionlist)
    private QuestionListView questionListView;

    //已批阅
    @ViewInject(R.id.ddwork_list_correctlayout)
    private RelativeLayout correctedLayout;
    @ViewInject(R.id.ddwork_list_correctedResult_right)
    private TextView       resultRightView;
    @ViewInject(R.id.ddwork_list_correctedResult_wrong)
    private TextView       resultWrongView;

    @ViewInject(R.id.ddwork_list_slidelayout)
    private LinearLayout  slideLayout;
    @ViewInject(R.id.ddwork_list_serieshitview)
    private SeriesHitView seriesHitView;

    //侧边栏
    @ViewInject(R.id.ddwork_list_selectquestionview)
    private SelectQuestionView mFilterQuestionView;

    //标记错题
    @ViewInject(R.id.selectErrorQuestionView)
    private SelectErrorQuestionView selectErrorQuestionView;
    @ViewInject(R.id.tv_mark_errorquestion)
    private TextView markErrQuestionView;

    private Context               mContext;
    //
    private DDWorkPageAdapter     mPageAdapter;
    private DDWorkQuestionAdapter mQuestionAdapter;

    private MyStudyModel   myStudyModel;
    private LocalWorkInfo  localWorkInfo;
    private DDWorkDetail   workDetail;
    private GuideVideoView mGuideVideoView;

    //
    private String ddworkId;
    private String recordId;
    private String showTitle;
    private String shareUrl;        //分享下载Url
    private boolean hasBuy = false;
    private String priviledgeId;    //传递过来的作业对应的值，仅错题周题练,变式训练
    private String productId;       //套题对应的商品ID,仅精品套题使用
    private int mLmUploadtype = AppConst.UPLOAD_TYPE_CAMERA;    //默认拍照提交

    private boolean isLearnMaterail = false;
    private boolean isDefine = false;
    private String bookId;          //教辅ID
    private String bookpages;       //需要查询的页码
    private float  bookRate;          //原版教辅的宽高比

    public boolean isScored=false;
    public int   isoverdue=1;   //是不是逾期
    public  String scoreTitle=null;   //自定义需要的标题


    public static void openActivity(Context context, String bookId, ArrayList<Integer> list, float bookRate, int uploadType) {

        String pages = "";
        HashMap<String, String> hashMap = new HashMap<>();
        for (Integer in : list) {

            //重复页去掉
            if (hashMap.containsKey(in.toString()))
                continue;
            hashMap.put(in.toString(), "");

            if (TextUtils.isEmpty(pages))
                pages = in.toString();
            else
                pages += "," + in;
        }

        Intent intent = new Intent(context, DDUploadActivity.class);
        intent.putExtra(DDUploadActivity.PARAM_LMATERIAL, true);
        intent.putExtra(DDUploadActivity.PARAM_BOOKID, bookId);
        intent.putExtra(DDUploadActivity.PARAM_BOOKPAGES, pages);
        intent.putExtra(DDUploadActivity.PARAM_BOOKRATE, bookRate);
        intent.putExtra(DDUploadActivity.PARAM_UPLOADTYPE, uploadType);
        context.startActivity(intent);
    }

    private Activity mActivity;

    //    @Override
    protected int getContentViewId() {
        return GlobalData.isPad() ? R.layout.activity_ddwork_list : R.layout.activity_ddwork_list_phone;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        transparent = true;
        super.onCreate(savedInstanceState);

        //设置全屏显示
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(getContentViewId());

        x.view().inject(this);

        mContext = this;
        mActivity = this;

        if (!parseIntent()) {
            ToastUtils.show(mContext, "参数错误", Toast.LENGTH_SHORT);
            finish();
            return;
        }

        if (!checkUsrInfo()) {
            finish();
            return;
        }

        initView();
        EventBus.getDefault().register(this);
        loadData();
    }

    @Override
    public void onResume() {
        super.onResume();

        //触发刷新
        mQuestionAdapter.notifyDataSetChanged();
        mPageAdapter.notifyDataSetChanged();

        //更新作业状态
        if (localWorkInfo == null)
            return;
        setWorkStatus(localWorkInfo.getWorkStatus());

        //更新拍照
        LocalPageInfo pageInfo = mPageAdapter.getItem(mPageAdapter.getSelectIndex());
        if( pageInfo!=null ){
            setCameraText(pageInfo);
            //是否显示标记错题
            boolean show = AppConst.UPLOAD_TYPE_MARKED == workDetail.getUploadType() && !TextUtils.isEmpty(pageInfo.getLocalpath());
            markErrQuestionView.setVisibility( show?View.VISIBLE:View.GONE);
        }

        //page list 从头开始，体验不好
        pageListView.postDelayed(new Runnable() {
            @Override
            public void run() {

                if (isDestroyed())
                    return;
                int index = mPageAdapter.getSelectIndex();
                if (index >= 0)
                    pageListView.setSelection(index);
            }
        }, 800);

    }

    //触发题目移动刷新
    public void refreshQuestionListView(final String qid) {
        questionListView.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isDestroyed())
                    return;

                ArrayList<LocalQuestionInfo> list = workDetail.getPageInfo().get(0).getQuestions();
                int index = 0;
                for (LocalQuestionInfo qinfo : list) {
                    if (qinfo.getQuestionId().equals(qid))
                        break;
                    index++;
                }
                questionListView.setSelection(index + questionListView.getHeaderViewsCount());

                //触发页码同步更新
                pageChange(index);

                drawerLayout.closeDrawers();
            }
        }, 100);
    }

    public void onLeftClick() {
        quit();
    }

    public void onRightClick() {

        MyTutorClassInfo classInfo = AccountUtils.getCurrentClassInfo();
        final UserDetailinfo userDetailinfo = AccountUtils.getUserdetailInfo();
        if (classInfo == null || userDetailinfo == null)
            return;
        final String title = workToolbar.getRightTitle();
        final String reportName = showTitle;

        //先处理报告
        if ("作业报告".equals(title)) {
            //专属习题
            if (!TextUtils.isEmpty(shareUrl)) {
                if (workDetail.getQuestionScore() > 0) {
                    MessageUtils.gotoWeekWorkReport(mContext, workDetail.getParentExamId(), userDetailinfo.getStudentId(), classInfo.getClassId(), reportName, null);
                } else {
                    MessageUtils.gotoWeekExtractReport(mContext, workDetail.getParentExamId(), userDetailinfo.getStudentId(), classInfo.getClassId(), reportName, null);
                }
            }
            //教辅
            else {
                MessageUtils.gotoHomeworkReport(mContext, ddworkId, userDetailinfo.getStudentId(), classInfo.getClassId(), reportName, null);
            }
            return;
        } else if ("周题练报告".equals(title)) {
            MessageUtils.gotoWeekExtractReport(mContext, workDetail.getParentExamId(), userDetailinfo.getStudentId(), classInfo.getClassId(), reportName, null);
            return;
        } else if ("考试报告".equals(title)) {
            MessageUtils.gotoWeekWorkReport(mContext, workDetail.getParentExamId(), userDetailinfo.getStudentId(), classInfo.getClassId(), reportName, null);
            return;
        } else if (isLearnMaterail || workDetail.isOrginLearnMaterail()) {
            showGuideView(isLearnMaterail, true);
            return;
        }

        //分享并下载
        if (localWorkInfo == null) {
            //ToastUtils.showShort( mContext, "信息错误，不能分享" );
            return;
        }

        //周题练 变式训练 精品套题 分享下载
        if (!TextUtils.isEmpty(shareUrl)) {
            //要支持变式训练和精品套题
            execShareDownload();
            return;
        }

        //普通套题 考试分享下载
        shareCommonWorkDownload( reportName, userDetailinfo);
    }

    @Override
    public void onBackPressed() {
        quit();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        AccountUtils.mDDWorkDetail = null;
                //停止提交监听
        DDWorkManager manager = DDWorkManager.getDDWorkManager();
        if (manager != null) {
            manager.removeCommitListener(this);
            manager.saveData();
        }
        if (monitorView != null)
            monitorView.stopMonitor();
        drawerLayout.removeDrawerListener(this);
    }

    @Override
    public void onClick(View v) {

        if (R.id.ddwork_list_camerabtn == v.getId()) {

            //如果没有布置，则先分享下载
            if( TextUtils.isEmpty(ddworkId) && !TextUtils.isEmpty(recordId) ){
                showShareDiaolog( false );
                return;
            }

            int index = mPageAdapter.getSelectIndex();
            if (index < 0) {
                ToastUtils.show(mContext, "请先选择作业页面", Toast.LENGTH_SHORT);
                return;
            }

            LocalPageInfo localPageInfo = mPageAdapter.getItem(index);
            //原版教辅拍照-学生自己
            if (isLearnMaterail) {
                //已经显示过Tips，则不再显示
                if (showGuideView(true, false)) {
                    openLmActivity(localPageInfo, bookRate, false);
                }
            }
            //老师布置的原版教辅
            else if (workDetail.isOrginLearnMaterail()) {
                //已经显示过Tips，则不再显示
                if (showGuideView(false, false)) {
                    openLmActivity(localPageInfo, workDetail.getWidthHeightRate(), true);
                }
            }
            //其他
            else {
                DDCameraActivity.openActivity( mContext, localPageInfo, workDetail.getQuestionScore()>0, workDetail.getWidthHeightRate() );
            }
        } else if (R.id.ddwork_list_workstatuslayout == v.getId()) {

            String text = workStatusView.getText().toString();
            if (!("重新提交".equals(text) || "提交作业".equals(text) || "完成创建".equals(text) || "提交批阅结果".equals(text)))
                return;

            //网络检测
            if (!NetworkUtil.isNetAvailable(ZxApplication.getApplication())) {
                ToastUtils.showToastCenter(mContext, getResources().getString(R.string.ddwork_commitfail_tips));
                return;
            }

            //提交网速过低，请切换网络
            if (monitorView.getSpeedPoor()) {
                ToastUtils.showToastCenter(mContext, getResources().getString(R.string.ddwork_commitslow_tips));
                return;
            }

            if (localWorkInfo == null) {
                ToastUtils.showShort(mContext, "作业信息错误!");
                finish();
                return;
            }

            //检查是否有题目
            if (!localWorkInfo.hasSelectQuestions()) {
                ToastUtils.showShort(mContext, "没有选择题目，不能提交，请重新拍照!");
                resetUploadStatus();
                return;
            }

            //检查要提交页必须有一页数据
            if (!localWorkInfo.hasUploadPage()) {
                ToastUtils.showShort(mContext, "请至少要拍照一页进行提交!");
                return;
            }

            //檢查是否有重複的題目
            if (localWorkInfo.ckeckRepeatQuestion()) {
                ToastUtils.show(mContext, "检查到作业里面有重复的题目，将只保留第一个的题目");
            }

            //先检查
            String msg = checkUnCameraPage(true);

            if (TextUtils.isEmpty(msg)) {   //全部已拍照
                checkUploadType();
            } else {      //有页面没有拍照
                AlertManager.showCustomImageBtnDialog(mContext, msg, "确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        checkUploadType();
                    }
                }, null);
            }
        }else if( v.getId() == R.id.tv_mark_errorquestion ){
            //
            int index = mPageAdapter.getSelectIndex();
            if (index < 0 ) return;

            LocalPageInfo localPageInfo = mPageAdapter.getItem(index);
            if( localPageInfo == null ) return;

//            ArrayList<LocalQuestionInfo> list = localPageInfo.getQuestions();
//            if( localWorkInfo!=null ){
//                list = localWorkInfo.getPageList().get(0).getQuestions();
//            }else{
//                list = workDetail.getPageInfo().get(0).getQuestions();
//            }
            selectErrorQuestionView.setData( localPageInfo.getQuestions(), null, workDetail.getQuestionScore()>0 );
            selectErrorQuestionView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void workStatus(final String examId, final int status) {
        if (isDestroyed())
            return;
        if (localWorkInfo == null || TextUtils.isEmpty(examId) )
            return;

        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (examId.equals(localWorkInfo.getWorkId())) {
                    setWorkStatus(status);
                }
            }
        });
    }

    //每一页的状态
    @Override
    public void pageStatus(final String examId, final int status) {
        if (isDestroyed())
            return;

        if (localWorkInfo == null)
            return;

        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (examId.equals(localWorkInfo.getWorkId())) {
                    //动态检查
                    localWorkInfo.markupLastUploadPage();
                    mPageAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    //整个作业的状态
    @Override
    public void onSuccess(final String examId, final WorkSubmitBean workSubmitBean) {
        if (isDestroyed())
            return;
        mAtomicBoolean.set(false);
        if (examId == null || localWorkInfo == null || !examId.equals(localWorkInfo.getWorkId()))
            return;

        setWorkStatus(workSubmitBean != null ? WORK_COMMITED : WORK_COMMITFAIL);

        //提交成功，停止网络监听
        monitorView.stopMonitor();

        if (workSubmitBean != null) {
            EventBus.getDefault().post(new UploadMyWorkEvent());
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //ToastUtils.showToastCenter( mContext, "提交成功!" );
                    //隐藏拍照层
                    mCameraLayout.setVisibility(View.GONE);
                    //隐藏拍照小提示
                    workToolbar.setRightTitle(null);

                    if(isScored){
                        EventBusUtils.postDelay(new UploadScorePlanEvent( "" ), new Handler());
                        if(isDefine){
                            scoreTitle="";
                        }
                        WorkAbilityDialog.showScoreDialog(mContext, isoverdue,scoreTitle,workSubmitBean, isClassWork());
                    }else {
                        WorkAbilityDialog.showDialog(mContext, workSubmitBean, isClassWork());
                    }
                }
            });
        } else {
            //ToastUtils.showToastCenter( mContext, getResources().getString( R.string.ddwork_commitfail_tips2) );
            showUploadFailDialog();
        }

        //进行刷新
        if (!TextUtils.isEmpty(shareUrl)){
            //错题精炼
            if( AppConst.WORK_TYPE_EBOOKWEEKTRAIN.equals(workDetail.getSourceType())
                    || AppConst.WORK_TYPE_MONTH_REFINE.equals(workDetail.getSourceType())
                    || AppConst.WORK_TYPE_CUSTOM_REFINE.equals(workDetail.getSourceType()) ){
                EventBusUtils.postDelay(new RefreshWeektrainEvent( recordId ), new Handler());
            }
            //变式训练
            else if( AppConst.WORK_TYPE_WEEK_VARIANT.equals(workDetail.getSourceType())
                    || AppConst.WORK_TYPE_MONTH_VARIANT.equals(workDetail.getSourceType())
                    || AppConst.WORK_TYPE_CUSTOM_VARIANT.equals(workDetail.getSourceType()) ){
                EventBusUtils.postDelay(new RefreshVariantEvent( recordId ), new Handler());
            }
            //精品套题
            else if( AppConst.WORK_TYPE_CLASSIC.equals(workDetail.getSourceType()) ){
                EventBusUtils.postDelay(new RefreshPracticeEvent(), new Handler());
            }
        }
        //通知豆豆检查作业 更新
        else if (isLearnMaterail)
            EventBusUtils.postDelay(new RefreshCheckworkEvent(), new Handler());

        //老师布置的作业 走其他刷新流程
    }

    @Override
    public void onFail(final String examId, Exception ex) {
        if (isDestroyed())
            return;
        mAtomicBoolean.set(false);
        if (examId == null || localWorkInfo == null || !examId.equals(localWorkInfo.getWorkId()))
            return;

        AppLog.i("dfdfdsfds onFail ", ex);
        setWorkStatus(WORK_COMMITFAIL);

        if( ex.getMessage() != null && ex.getMessage().contains("请上传老师批阅后的结果")  ){

            String message = "你的极速智能诊断特权已失效，点击“极速智能诊断”前去获得特权。";

            CustomDialogNew dialog = AlertManager.showCustomImageBtnDialog(mContext, message, "极速智能诊断", "上传老师的批阅结果", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    EventBus.getDefault().post(new JumpEvent(MainActivity.FRAGMENT_MY_CENTER, UserCenterFragment.MODEL_MYSTUDYBEAN));
                    finishAll();
                }
            }, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    resetUploadStatus();
                    //要刷新作业列表
                    EventBus.getDefault().post( new RecorrectWorkEvent());
                    loadData();
                }
            }, null);
            dialog.setCloseViewVisibility(View.GONE);
            dialog.getLeftBtn().setBackgroundResource( R.drawable.bg_dialog_enter_r18 );
            dialog.getRightBtn().setBackgroundResource( R.drawable.bg_rect_blue_r24white );
            dialog.getRightBtn().setTextColor( mContext.getResources().getColor(R.color.color_3BCCD9));


        }else if (ex.getMessage() != null && (ex.getMessage().contains("请登录") || ex.getMessage().contains("加入班级"))) {
            ToastUtils.showToastCenter(mContext, ex.getMessage());
        } else if (ex.getMessage() != null && (ex.getMessage().contains("网络") || ex.getMessage().contains("time out") || ex.getMessage().contains("线程被中断"))) {
            ToastUtils.showToastCenter(mContext, getResources().getString(R.string.ddwork_commitfail_tips));
        } else if (!TextUtils.isEmpty(ex.getMessage())) {
            //ToastUtils.showToastCenter( mContext, ex.getMessage() );
            //}else{
            //ToastUtils.showToastCenter( mContext, getResources().getString( R.string.ddwork_commitfail_tips2) );
            if (hasBuy && "您目前没有使用权，请先兑换后使用".equals(ex.getMessage())) {
                ToastUtils.show(mContext, "购买后没有在使用有效期内提交作业，不能提交了。");
            } else if (ex.getMessage().contains("不能重复提交")) {
                ToastUtils.show(mContext, ex.getMessage());
            } else {
                showUploadFailDialog();
            }
        } else {
            showUploadFailDialog();
        }

        localWorkInfo.markupLastUploadPage();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //选中状态改变
        LocalPageInfo pageInfo = mPageAdapter.getItem(position);
        if (pageInfo == null)
            return;
        if (workDetail.getExerStatus() <= DDWorkDetail.WORK_UNSUBMIT) { //待提交
            loadQuestions(position);
        } else {
            showPageFisrtQuestion(position);
        }

        //当前选中的移动到中间，主要是向上移动
        int first = pageListView.getFirstVisiblePosition();
        int last = pageListView.getLastVisiblePosition();
        int middle = (first + last) / 2;
        if (position <= middle || position > last || last == mPageAdapter.getCount())
            return;

        //向上滚动一个Item的高度
        pageListView.smoothScrollByOffset(2);
    }

    @Override
    public void pageUp() {
    }

    @Override
    public void pageDown() {
    }

    //question显示改变，判断是否更改页码选择
    @Override
    public void pageChange(int firstVisibleItem) {

        //显示的第一个对象
        LocalQuestionInfo questionInfo = (LocalQuestionInfo) questionListView.getAdapter().getItem(firstVisibleItem); // mQuestionAdapter.getItem( firstVisibleItem );
        if (questionInfo == null)
            return;
        mPageAdapter.selectItem(questionInfo.getPageNum());
        pageListView.setSelection(questionInfo.getPageNum());
    }

    private void showPageFisrtQuestion(int page) {
        mPageAdapter.selectItem(page);
        questionListView.reset();
        LocalPageInfo pageInfo = mPageAdapter.getItem(0);
        if (pageInfo == null)
            return;

        ArrayList<LocalQuestionInfo> list = pageInfo.getQuestions();
        for (int i = 0; i < list.size(); i++) {
            if (page == list.get(i).getPageNum()) {
                questionListView.setSelection(i);
                break;
            }
        }
    }

    //----------------------------------------------------------------------
    @SuppressLint("WrongConstant")
    private void quit() {
        if (drawerLayout.isDrawerOpen(Gravity.END)) {
            drawerLayout.closeDrawers();
            return;
        }

        if (lmGuideView.isShown()) {
            lmGuideView.setVisibility(View.GONE);
            workToolbar.setRightTitle(getString(R.string.camerawork_tips));
            workToolbar.setTitle(showTitle);
            return;
        }

        if (localWorkInfo == null || workDetail.getExerStatus() >= DDWorkDetail.WORK_WAITCORRECT) {     //待提交，已批阅 直接返回
            quitfinish();
            return;
        }

        int status = localWorkInfo.getWorkStatus();
        if (WORK_NONE == status) {       //未提交
            //未拍照  部分拍照   已全拍照
            //先检查
            String msg = checkUnCameraPage(false);
            if (TextUtils.isEmpty(msg)) {   //全部已拍照
                //
                String data = "你还没有提交，确定要退出吗？";
                if (isLearnMaterail)
                    data = "你还没有提交，退出后作业数据不会保留,确定要退出吗？";
                AlertManager.showCustomDialog1(mContext, data, "确定", "取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        quitfinish();
                    }
                }, null);

            } else if (!checkAllUnCameraPage()) {       //部分拍照
                AlertManager.showCustomDialog1(mContext, msg, "确定", "取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        quitfinish();
                    }
                }, null);
            } else {              //未拍照
                quitfinish();
            }
        } else /*if( LocalWorkInfo.WORK_COMMITING == status || LocalWorkInfo.WORK_COMMITED == status )*/ {        //提交失败，提交中 提交完成  直接返回，
            if (isLearnMaterail) {
                //提交中
                if (WORK_COMMITING == status) {
                    AlertManager.showCustomDialog1(mContext, "正在创建作业，退出后作业数据不会保留, 确定要退出吗？", "确定", "取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            quitfinish();
                        }
                    }, null);
                }
                //提交失败
                else if (WORK_COMMITFAIL == status) {
                    AlertManager.showCustomDialog1(mContext, "创建作业失败，退出后作业数据不会保留,确定要退出吗？", "确定", "取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            quitfinish();
                        }
                    }, null);
                }
                //提交完成
                else {
                    quitfinish();
                }
            } else {
                quitfinish();
            }
        }
    }

    public void quitfinish() {
        Intent intent = new Intent();
        intent.putExtra(PARAM_DDWORKID, ddworkId);
        // 设置结果，并进行传送
        setResult(RESULT_OK, intent);

        //原版教辅，直接回到首页
        if (isLearnMaterail && localWorkInfo != null && localWorkInfo.getWorkStatus() == WORK_COMMITED)
            finishAll();
        else
            finish();
    }

    private void loadQuestions(int position) {

        LocalPageInfo pageInfo = mPageAdapter.getItem(position);
        if (pageInfo == null)
            return;
        mPageAdapter.selectItem(position);
        setCameraText(pageInfo);

        //题目内容改变
        mQuestionAdapter.clear();
        mQuestionAdapter.addQuestionList(pageInfo);
        mQuestionAdapter.notifyDataSetChanged();

        //是否显示标记错题
        boolean show = AppConst.UPLOAD_TYPE_MARKED == workDetail.getUploadType() && !TextUtils.isEmpty(pageInfo.getLocalpath());
        markErrQuestionView.setVisibility( show?View.VISIBLE:View.GONE);

        //设置是否可以上下拉
        //questionListView.setRefleshMode( position, mPageAdapter.getCount() );
    }

    private boolean checkUsrInfo() {
        LoginInfo loginInfo = AccountUtils.getLoginUser();
        UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
        if (loginInfo == null || detailinfo == null) {
            ToastUtils.showShort(mContext, "请登录");
            return false;
        }

        if (!AccountUtils.hasClass()) {
            ToastUtils.showShort(mContext, "你还没有班级信息。请联系老师，加入班级。");
            return false;
        }
        //确保
        DDWorkManager.getDDWorkManager(mContext, loginInfo.getLoginName());
        return true;
    }

    private boolean parseIntent() {
        Intent intent = getIntent();
        ddworkId = intent.getStringExtra(PARAM_DDWORKID);
        recordId = intent.getStringExtra(PARAM_RECORDID);
        showTitle = intent.getStringExtra(PARAM_TITLE);
        shareUrl = intent.getStringExtra(PARAM_SHAREURL);
        hasBuy = intent.getBooleanExtra(PARAM_HASBUY, false);
        priviledgeId = intent.getStringExtra( PARAM_PRIVILEDGEID);
        productId = intent.getStringExtra(PARAM_PRODUCTID);
        isLearnMaterail = intent.getBooleanExtra(PARAM_LMATERIAL, false);
        isDefine = intent.getBooleanExtra(PARAM_DEDINE, false);
        bookId = intent.getStringExtra(PARAM_BOOKID);
        bookpages = intent.getStringExtra(PARAM_BOOKPAGES);
        bookRate = intent.getFloatExtra(PARAM_BOOKRATE, -1);
        mLmUploadtype = intent.getIntExtra( PARAM_UPLOADTYPE, AppConst.UPLOAD_TYPE_CAMERA);

        isScored = intent.getBooleanExtra( PARAM_SCORED, false);
        isoverdue = intent.getIntExtra( PARAM_OVERDO, AppConst.UPLOAD_TYPE_SCAN);
        scoreTitle = intent.getStringExtra( PARAM_SHOWTITLE);
        if(TextUtils.isEmpty(showTitle)){
            scoreTitle=showTitle;
        }

        //已布置的作业作业
        boolean st = false;
        if (!TextUtils.isEmpty(ddworkId) && !TextUtils.isEmpty(showTitle)) {
            st = true;
        }
        //错题周题练
        else if (!TextUtils.isEmpty(recordId) && !TextUtils.isEmpty(showTitle)) {
            st = true;
        }

        //原版教辅
        else if (isLearnMaterail && !TextUtils.isEmpty(bookId) && !TextUtils.isEmpty(bookpages) && bookRate > 0) {
            st = true;
        }
        return st;
    }

    //提交作业中
    private AtomicBoolean mAtomicBoolean = new AtomicBoolean(false);

    //检查是否有批阅提交类型，是，就直接提交
    private void checkUploadType(){
        if( AppConst.UPLOAD_TYPE_MARKED == workDetail.getUploadType() ){
            if (mAtomicBoolean.get()) {
                return;
            }
            mAtomicBoolean.set(true);
            AlertManager.showCustomImageBtnDialog(mContext, getResources().getString(R.string.ddwork_commit_tips), "确认提交", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startCommiteDataed();
                }
            }, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mAtomicBoolean.set(false);
                }
            });

        }else{
            //检查使用权
            checkUploadPermission();
        }
    }

    //检查是否有提交的权限
    private void checkUploadPermission() {

        if (mAtomicBoolean.get()) {
            //ToastUtils.show( mContext, "正在提交中...");
            return;
        }
        mAtomicBoolean.set(true);

        //先检查有没有购买
        if (hasBuy) {
            startCommitData(false);
            return;
        }
        final String privilegeId = getPrivilegeIdByWorkType();
        if (TextUtils.isEmpty(privilegeId)) {
            ToastUtils.show(mContext, "不支持此类作业提交，请联系客服!");
            mAtomicBoolean.set(false);
            return;
        }

        //再检查有没有使用次数
        boolean succ = ProductUtil.checkProductUseTimes(null, privilegeId, new RequestListener<List<ProductUseTimesBean>>() {
            @Override
            public void onSuccess(List<ProductUseTimesBean> list) {
                if (isDestroyed())
                    return;
                if (list == null || list.size() == 0 || list.get(0) == null) {
                    ToastUtils.show(mContext, R.string.relogin);
                    mAtomicBoolean.set(false);
                    return;
                }
                ProductUseTimesBean bean = list.get(0);

                //已有次数,允许提交
                if (bean.getUseTimes() > 0 || bean.getUseTimes() == -1) {
                    workDetail.setUsePrivilege( DDWorkDetail.ST_BUYED );
                    startCommitData(false);
                }
                //没有次数，引导用户去兑换,提示   套题作业
                else if (AppConst.PRIVILEGE_ASSISTANTWORK.equals(privilegeId)       //教辅作业
                        || AppConst.PRIVILEGE_SETWORK.equals(privilegeId)          //套题作业
                        || AppConst.PRIVILEGE_VACATION_WORK.equals(privilegeId)    //假期作业
                        ) {

                    //判断混合网阅还是混合纸阅
                    if( workDetail.getSubUploadType() == 0 ){   //老师网阅
                        priviledgeNouseNetCorrect();
                    }else{      //纸上批阅
                        priviledgeLosePaperCorrect( true, mAtomicBoolean );
                    }
                } else {

                    mAtomicBoolean.set(false);
                    String data = "请先购买套餐后，再完成创建操作。\n正在为您跳转到购买套餐界面...";
                    if (!isLearnMaterail) {
                        data = "请先购买套餐后，再提交作业。\n正在为您跳转到购买套餐界面...";
                    }
                    if( !TextUtils.isEmpty(privilegeId) && AppConst.PRIVILEGE_CLASSICPRATICE.equals(privilegeId) ){     //精品套题特殊处理
                        data = "请确认是否已经购买此套题!";
                    }else{
                        mainLayout.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
                                if( detailinfo!=null && !detailinfo.enableBuySuite() ){     //不允许买
                                    ToastUtils.show( mContext, mContext.getResources().getString(R.string.tj_nobuy_tips) );
                                }else{  //跳转到购买界面
                                    BuySuiteActivity.startBuySuiteActivity(mContext, BuySuiteActivity.WORK, privilegeId);
                                }
                            }
                        }, 2100);
                    }
                    ToastUtils.showToastCenter(mContext, data);
                }
            }

            @Override
            public void onFail(HttpResponse<List<ProductUseTimesBean>> response, Exception ex) {
                if (isDestroyed())
                    return;
                AlertManager.showUploadErrorInfo(mContext, ex);
                mAtomicBoolean.set(false);
            }
        });

        //启动检查失败
        if (!succ)
            ToastUtils.show(mContext, R.string.relogin);
    }

    //
    private void startCommitData(boolean showDialog) {

        if (!showDialog) {
            startCommiteDataed();
            return;
        }

        AlertManager.showCustomImageBtnDialog(mContext, "已选择极速智能诊断方式并确认提交"/*getResources().getString(R.string.ddwork_commit_tips)*/, "极速智能诊断", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startCommiteDataed();
            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mAtomicBoolean.set(false);
            }
        });
    }

    private void startCommiteDataed() {
        UserDetailinfo userDetailinfo = AccountUtils.getUserdetailInfo();
        if (localWorkInfo == null || userDetailinfo == null) {
            ToastUtils.showShort(mContext, "请登录");
            mAtomicBoolean.set(false);
            return;
        }
        localWorkInfo.setWorkStatus(WORK_COMMITING);
        setWorkStatus(WORK_COMMITING);

        String studentId = userDetailinfo.getStudentId();
        String examId = localWorkInfo.getWorkId();

        //原版教辅直接提交
        if (!TextUtils.isEmpty(PARAM_BOOKID)) {
            localWorkInfo.startUploadData();
        } else {
            //先检查作业是否撤回
            myStudyModel.queryDDWorkRevokeStatus(studentId, examId, new RequestListener() {
                @Override
                public void onSuccess(Object res) {
                    if (isDestroyed())
                        return;
                    if (res instanceof Integer && 1 == (Integer) res) {  //
                        ToastUtils.showShort(mContext, getResources().getString(R.string.ddwork_revokework));
                        finish();
                    } else {
                        localWorkInfo.startUploadData();
                    }
                    mAtomicBoolean.set(false);
                }

                @Override
                public void onFail(HttpResponse response, Exception ex) {
                    if (isDestroyed())
                        return;
                    AlertManager.showUploadErrorInfo(mContext, ex);
                    localWorkInfo.setWorkStatus(WORK_COMMITFAIL);
                    setWorkStatus(WORK_COMMITFAIL);
                    mAtomicBoolean.set(false);
                }
            });
        }
    }

    //检查是否有页面没有拍照，返回相关信息
    private String checkUnCameraPage(boolean upload) {
        String data = "";

        int count = mPageAdapter.getCount();
        for (int i = 0; i < count; i++) {
            LocalPageInfo pageInfo = mPageAdapter.getItem(i);
            if (pageInfo == null)
                continue;
            if (TextUtils.isEmpty(pageInfo.getLocalpath())) {
                if (TextUtils.isEmpty(data)) {
                    data = "第" + String.valueOf(pageInfo.getPageNum());
                } else {
                    data += "、";
                    data += String.valueOf(pageInfo.getPageNum());
                }
            }
        }
        if (!TextUtils.isEmpty(data)) {
            if( workDetail.getUploadType() == AppConst.UPLOAD_TYPE_MARKED ){
                data += "页作业的批阅结果未拍摄，";
                if( upload ){
                    data += "提交后未拍摄的题将被标记为错题";
                    if( workDetail.getQuestionScore()>0 ){
                        data += "（错题记为0分）。\n\n";
                        data += String.format(Locale.getDefault(),"本次考试得分：%d分（共%d分），你确定要提交吗？", (int)DDWorkUtil.statStudentScore( localWorkInfo ),(int)workDetail.getQuestionScore() );
                    }else {
                        data +="。\n你确定要提交吗?";
                    }
                }else{
                    data += "你确定要退出吗?";
                }
            }else{
                data += "页作业未拍照，";
                if (upload) {
                    data += "你确定要提交吗?";
                } else {
                    data += "你确定要退出吗?";
                }
            }
        }else if( workDetail.getQuestionScore()>0 && upload && workDetail.getUploadType() == AppConst.UPLOAD_TYPE_MARKED  ){
            data += String.format(Locale.getDefault(),"本次考试总得分：%d分（共%d分），确定提交批阅结果？", (int)DDWorkUtil.statStudentScore(localWorkInfo),(int)workDetail.getQuestionScore() );
        }

        return data;
    }

    //true：全部未拍照，false：部分未拍照
    private boolean checkAllUnCameraPage() {
        int uncount = 0;
        int count = mPageAdapter.getCount();
        for (int i = 0; i < count; i++) {
            LocalPageInfo pageInfo = mPageAdapter.getItem(i);
            if (pageInfo != null && TextUtils.isEmpty(pageInfo.getLocalpath())) {
                uncount++;
            }
        }
        return uncount == count;
    }

    //检查作业是否已拍照
    private boolean hasLocalImage() {
        ArrayList<LocalPageInfo> list = localWorkInfo.getPageList();
        for (LocalPageInfo pageInfo : list) {
            if (!TextUtils.isEmpty(pageInfo.getLocalpath())) {
                return true;
            }
        }
        return false;
    }

    //未提交的状态
    private void setWorkStatus(int status) {

        if (WORK_NONE == status) {
            workStatusView.setText("提交作业");
            if (isLearnMaterail) {
                workStatusView.setText("完成创建");
            }

            //扫描作业
            if (workDetail.getUploadType() == AppConst.UPLOAD_TYPE_SCAN) {
                workStatusView.setText("待扫描");
            }
            //批阅结果提交
            else if(workDetail.getUploadType() == AppConst.UPLOAD_TYPE_MARKED) {
                workStatusView.setText("提交批阅结果");
            }

            if (hasLocalImage()) {
                workStatusLayout.setEnabled(true);
                workStatusLayout.setBackgroundResource(R.drawable.ic_work_submit);
            } else {
                workStatusLayout.setEnabled(false);
                workStatusLayout.setBackgroundResource(R.drawable.ic_submission);
            }
            workImageView.setVisibility(View.GONE);
        } else if (WORK_COMMITING == status) {
            workStatusView.setText("正在提交");
            workStatusLayout.setBackgroundResource(R.drawable.ic_submiting);

            workImageView.setVisibility(View.VISIBLE);

            Animation operatingAnim = AnimationUtils.loadAnimation(this, R.anim.rotate_center);
            LinearInterpolator lin = new LinearInterpolator();
            operatingAnim.setInterpolator(lin);
            workImageView.startAnimation(operatingAnim);

            mCameraBtn.setVisibility(View.GONE);
        } else if (WORK_COMMITED == status) {
            if( AppConst.UPLOAD_TYPE_MARKED == workDetail.getUploadType() ){
                workStatusView.setText( "提交成功" );       // 不能超过四个字
                workStatusLayout.setBackgroundResource(R.drawable.ic_rightandwrong);
            }else{
                boolean buy = (workDetail!=null&&(workDetail.getUsePrivilege()==DDWorkDetail.ST_BUYED))||hasBuy;
                workStatusView.setText( buy?"批阅中":"等待批阅");
                workStatusLayout.setBackgroundResource(R.drawable.ic_rightandwrong);
            }
            mCameraBtn.setVisibility(View.GONE);
            workImageView.setVisibility(View.GONE);
        } else if (WORK_COMMITFAIL == status) {
            workStatusView.setText("重新提交");
            workStatusLayout.setEnabled(true);
            workStatusLayout.setBackgroundResource(R.drawable.ic_lfailure);
            mCameraBtn.setVisibility(View.GONE);
            workImageView.setVisibility(View.GONE);
        }
    }

    //已提交的作业状态
    private void setWorkStatusSubmit(DDWorkDetail workDetail) {
        //隐藏拍照层
        mCameraLayout.setVisibility(View.GONE);
        //不能点击
        workStatusLayout.setEnabled(false);

        if (workDetail.getExerStatus() == DDWorkDetail.WORK_CORRECTED) {
            workStatusView.setVisibility(View.GONE);
            correctedLayout.setVisibility(View.VISIBLE);
            workStatusLayout.setVisibility(View.GONE);

            String rightdata = "正确 " + DDWorkUtil.getAlginCount(workDetail.getRightQuestionCount(), workDetail.getWrongQuestionCount()) + " 题";
            String errordata = "错误 " + DDWorkUtil.getAlginCount(workDetail.getWrongQuestionCount(), workDetail.getRightQuestionCount()) + " 题";
            correctedLayout.setBackgroundResource(R.drawable.ic_rightandwrong);
            if (workDetail.getQuestionScore() > 0) {      //周练作业，显示分数
                rightdata = "得 " + DDWorkUtil.getAlginCount((int) workDetail.getStudentScore(), (int) workDetail.getQuestionScore()) + " 分";
                errordata = "共 " + DDWorkUtil.getAlginCount((int) workDetail.getQuestionScore(), (int) workDetail.getStudentScore()) + " 分";
                correctedLayout.setBackgroundResource(R.drawable.ic_corrected_weekbtn);
            }
            resultRightView.setText(rightdata);
            resultWrongView.setText(errordata);
        } else {      //批阅中
            workStatusView.setVisibility(View.VISIBLE);
            correctedLayout.setVisibility(View.GONE);

            boolean buy = workDetail.getUsePrivilege()==DDWorkDetail.ST_BUYED||hasBuy;
            String data = workDetail.getUploadType()==AppConst.UPLOAD_TYPE_MARKED?"报告生成中":(buy?"批阅中":"等待批阅");
            workStatusView.setText( data );
            workStatusLayout.setBackgroundResource(R.drawable.ic_rightandwrong);
        }
    }

    @SuppressLint("WrongConstant")
    private void initView() {

        //
        monitorView = workToolbar.getNetworkMonitorView();

        //标题
        workToolbar.setTitle(showTitle);
        workToolbar.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLeftClick();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRightClick();
            }
        });

        //mLoadingPager.setWhite();
        mLoadingPager.setTargetView(mCameraLayout);
        mCameraBtn.setOnClickListener(this);

        mPageAdapter = new DDWorkPageAdapter(mContext, DDWorkDetail.WORK_UNSUBMIT);
        pageListView.setAdapter(mPageAdapter);
        pageListView.setOnItemClickListener(this);

        mQuestionAdapter = new DDWorkQuestionAdapter(mContext, DDWorkDetail.WORK_UNSUBMIT);
        questionListView.setPageChangeListener(this);

        mGuideVideoView = new GuideVideoView(mContext);

        workStatusLayout.setEnabled(false);
        workStatusLayout.setBackgroundColor(getResources().getColor(R.color.color_D4EEFF));
        workStatusLayout.setOnClickListener(this);

        mLoadingPager.setListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadData();
            }
        });

        myStudyModel = new MyStudyModel();

        DDWorkManager manager = DDWorkManager.getDDWorkManager();
        if (manager != null) {
            manager.addCommitListener(this);
        }
        lmGuideView.setListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoLmActivity();
                setGuideView();
                workToolbar.setRightTitle(getString(R.string.camerawork_tips));
                workToolbar.setTitle(showTitle);
            }
        });

        //默认情况下禁止滑动打开
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, Gravity.END);

        markErrQuestionView.setOnClickListener( this );
        selectErrorQuestionView.setSingleUse();
    }

    //加载的是本地保存的数据
    private void loadData() {

        final LoginInfo loginInfo = AccountUtils.getLoginUser();
        final UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
        final MyTutorClassInfo classInfo = AccountUtils.getCurrentClassInfo();
        if (loginInfo == null || detailinfo == null || classInfo == null) {
            ToastUtils.show(mContext, R.string.relogin);
            finish();
            return;
        }

        String accessToken = loginInfo.getAccessToken();
        final String studentId = detailinfo.getStudentId();
        //String classId     = classInfo.getClassId();

        mLoadingPager.showLoading();
        //普通作业 和 原版教辅
        if (!isLearnMaterail) {
            myStudyModel.queryDDWorkDetail(accessToken, studentId, ddworkId, recordId, new RequestListener() {
                @Override
                public void onSuccess(Object res) {
                    if (isDestroyed())
                        return;
                    workDetail = (DDWorkDetail) res;
                    AccountUtils.mDDWorkDetail = workDetail;
                    bookRate = workDetail.getWidthHeightRate();

                    if (workDetail.getPageInfo() == null) {
                        ToastUtils.show(mContext, "没有查找到作业数据!", Toast.LENGTH_SHORT);
                        quit();
                        return;
                    }

                    //判断是否撤回
                    if (workDetail.getIsRevoked() != 0) {
                        mLoadingPager.showEmpty(getResources().getString(R.string.ddwork_revokework1));
                        return;
                    }
                    mLoadingPager.showTarget();
                    mainLayout.setVisibility(View.VISIBLE);

                    //作业信息与本地数据进行关联处理
                    DDWorkManager ddWorkManager = DDWorkManager.getDDWorkManager(mContext, loginInfo.getLoginName());
                    if (ddWorkManager == null) {
                        ToastUtils.show(mContext, "DDWorkManager 管理器没有初始化", Toast.LENGTH_SHORT);
                        return;
                    }

                    //发出消息，通知查看作业了
                    if (!TextUtils.isEmpty(ddworkId)) {
                        EventBus.getDefault().post(new LookWorkEvent(ddworkId));
                    }

                    if( TextUtils.isEmpty(ddworkId) ){
                        ddworkId = workDetail.getWorkId();
                    }else{
                        workDetail.setWorkId(ddworkId);
                    }

                    //校园扫描作业 不能拍照
                    mCameraLayout.setVisibility( (workDetail.getUploadType() == AppConst.UPLOAD_TYPE_SCAN ) ? View.GONE : View.VISIBLE);

                    workDetail.setWorkName(showTitle);
                    if (workDetail.getExerStatus() <= DDWorkDetail.WORK_UNSUBMIT) {

                        //先判断作业上传类型是否变化了，不影响原有流程
                        LocalWorkInfo tmpWorkInfo = ddWorkManager.getWorkInfo(ddworkId);
                        //boolean type1t0 = false, type0t1 = false;
                        if( tmpWorkInfo!=null       //本地有缓存信息
                                && tmpWorkInfo.getUploadType() != workDetail.getUploadType()        //上传类型发生变化
                                && tmpWorkInfo.hasUploadPage()                                          //本地有拍照数据
                                && ( (tmpWorkInfo.getUploadType()== AppConst.UPLOAD_TYPE_CAMERA && workDetail.getUploadType()==AppConst.UPLOAD_TYPE_MARKED)     //有权限到没有权限
                                ) ){
                            if( workDetail.getSubUploadType() == 0 ){   //网阅
                                priviledgeLoseNetCorrect();
                            }else{      //纸阅
                                priviledgeLosePaperCorrect( false, null);
                            }
                        }

                        localWorkInfo = ddWorkManager.relateLocalWorkInfo(workDetail);
                    }
                    ArrayList<LocalPageInfo> list;
                    if (localWorkInfo != null) {
                        list = localWorkInfo.getPageList();
                        localWorkInfo.setLearnMaterial(workDetail.getCustomType());
                    } else {
                        list = workDetail.getPageInfo();
                    }

                    //首页加载 错误 或者没有数据   显示无
                    if (list == null || list.size() == 0) {
                        mLoadingPager.showEmpty();
                        return;
                    }

                    //带分数的题目处理
                    int color1 = getResources().getColor(R.color.color_B3B3B3);
                    int color2 = getResources().getColor(R.color.color_91DD95);
                    boolean corrected = workDetail.getExerStatus() >= DDWorkDetail.WORK_CORRECTED;
                    DDWorkUtil.dealQuestionWeekTrain(list, workDetail.getQuestionScore() > 0, corrected, corrected ? color2 : color1);

                    DDWorkUtil.dealAllQuestionSelect(list);
                    DDWorkUtil.dealQuestionType(list);
                    DDWorkUtil.dealMaterailName(list);
                    DDWorkUtil.dealQuestionPageInfo(list, workDetail.getExerStatus() >= DDWorkDetail.WORK_WAITCORRECT);
                    DDWorkUtil.dealUploadType( list, workDetail.getUploadType() );

                    //如果是下拉加载，清除原列表数据
                    mPageAdapter.clear();
                    mPageAdapter.addAll(list);
                    mPageAdapter.setLocalWorkInfo(localWorkInfo);
                    mPageAdapter.setUploadType(workDetail.getUploadType());
                    mPageAdapter.notifyDataSetChanged();

                    //更新作业状态
                    if (workDetail.getExerStatus() >= DDWorkDetail.WORK_WAITCORRECT) {
                        questionListView.setPageSelectEnable(true);
                        //去除重复的题目
                        DDWorkUtil.dealDuplicateQuestion(list.get(0).getQuestions());
                        setWorkStatusSubmit(workDetail);
                        mPageAdapter.setSubmitStatus(DDWorkDetail.WORK_CORRECTED);
                        if (workDetail.getExerStatus() == DDWorkDetail.WORK_CORRECTED) {
                            String title = "作业报告";
                            if (workDetail.getQuestionScore() > 0) {
                                title = "考试报告";
                            } else if (AppConst.WORK_TYPE_EBOOKWEEKTRAIN.equals(workDetail.getSourceType())) {
                                title = "周题练报告";
                            }
                            workToolbar.setRightTitleAndLeftDrawable(title, R.drawable.ic_report2);

                            //仅批阅完成后再出现
                            if (list.get(0).getQuestions() != null && list.get(0).getQuestions().size() > 0) {
                                initSlidingMenu(list.get(0).getQuestions());
                            }

                            if (AccountUtils.getRegRewardBean() == null) {
                                ProductUtil.updateRegisterReward();
                            }
                            //显示分享连击
                            seriesHitView.setData(workDetail, drawerLayout);

                            //显示名师精讲视频引导
                            if (workDetail.getTopKnowledgeList() != null && workDetail.getTopKnowledgeList().size() > 0) {
                                mGuideVideoView.setData(workDetail);
                                questionListView.addHeaderView(mGuideVideoView);
                            }
                        }
                    } else {
                        setWorkStatus(localWorkInfo.getWorkStatus());

                        //原版教辅 布置成扫描,本地修改成订正类型，流程同定制教辅
                        if (workDetail.getUploadType() == AppConst.UPLOAD_TYPE_SCAN) {
                            workDetail.setCustomType(DDWorkDetail.LMTPYE_DINGZHI);
                        }
                        else if( workDetail.getUploadType() == AppConst.UPLOAD_TYPE_MARKED ){
                            //提交已批阅的作业，默认都是正确的
                            //if(localWorkInfo==null)DDWorkUtil.dealQuestionCorrectRight(list);
                            //提交已批阅的作业,要设置属性
                            DDWorkUtil.dealQuestionCorrectUploadStaus(list);
                            showTipsDialog( loginInfo.getAccountId() );
                        }

                        //原版教辅老师布置作业
                        if (workDetail.isOrginLearnMaterail()) {
                            workToolbar.setRightTitle(getString(R.string.camerawork_tips));
                        }
                        //仅周题练 套题模式   已购买 则显示， 未购买，不显示
                        else if (!TextUtils.isEmpty(recordId)) {
                            //if ( /*!TextUtils.isEmpty(ddworkId)*/ hasBuy && AppConst.PRIVILEGE_QUESTION_WEEKPRACTICE.equals(getPrivilegeIdByWorkType()))
                            workToolbar.setRightTitleAndLeftDrawable("分享并下载", R.drawable.ic_share2);

                            //如果没有布置，则先分享下载
                            if( TextUtils.isEmpty(ddworkId) /*&& !TextUtils.isEmpty(recordId)*/ ){
                                showShareDiaolog( false );
                            }
                            //非本人布置的作业，提示用户下载
                            else if( !TextUtils.isEmpty(workDetail.getCreatorId()) && !workDetail.getCreatorId().equals(studentId) && !PreferencesUtils.getBoolean(mContext,ddworkId,false)){
                                PreferencesUtils.putBoolean(mContext,ddworkId,true);
                                showShareDiaolog( true );
                            }
                        }
                        //非定制教辅作业
                        else if (TextUtils.isEmpty(list.get(0).getLearnMaterialName()) || workDetail.getUploadType() == AppConst.UPLOAD_TYPE_SCAN)
                            workToolbar.setRightTitleAndLeftDrawable("分享并下载", R.drawable.ic_share2);
                        //扫描模式，没有提交速度
                        if (workDetail.getUploadType() != AppConst.UPLOAD_TYPE_SCAN)
                            monitorView.startMonitor();
                    }
                    mQuestionAdapter.setData(workDetail, workDetail.getAllowLookAnswer() == 0);
                    questionListView.setAdapter(mQuestionAdapter);
                    loadQuestions(0);
                }

                @Override
                public void onFail(HttpResponse response, Exception ex) {
                    if (isDestroyed())
                        return;
                    mLoadingPager.showFault(ex);
                }
            });
        }
        //原版教辅 待提交作业
        else {
            if (TextUtils.isEmpty(bookId) || TextUtils.isEmpty(bookpages)) {
                ToastUtils.show(mContext, "参数错误");
                finish();
                return;
            }
            getSelfWorkDetail(loginInfo);
        }
    }

    private void setCameraText(LocalPageInfo localPageInfo) {
        localPageInfo.setSelected(true);
    }

    //周题练下载分享
    private void shareWeekTrainDownload() {
        final LoginInfo loginInfo = AccountUtils.getLoginUser();
        final UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
        if (detailinfo == null || loginInfo == null)
            return;
        ErrorBookModel bookModel = new ErrorBookModel();
        bookModel.queryWeekTrainShare(detailinfo.getStudentId(), recordId, new RequestListener<ShareBean>() {
            @Override
            public void onSuccess(ShareBean bean) {
                if (bean == null || TextUtils.isEmpty(bean.getPath())) {
                    ToastUtils.show(mContext, "未找到对应的资源", Toast.LENGTH_SHORT);
                } else {
                    //显示拍照按钮，重新加载
                    ddworkId = bean.getExamId();
                    localWorkInfo.setWorkId( ddworkId );
                    workDetail.setWorkId( ddworkId );
                    //mCameraLayout.setVisibility(TextUtils.isEmpty(ddworkId) ? View.GONE : View.VISIBLE);

                    //通知错题周题练，进行刷新
                    EventBusUtils.postDelay(new RefreshWeektrainEvent(), new Handler());
                    if(isScored){
                        EventBusUtils.postDelay(new UploadScorePlanEvent( "" ), new Handler());
                    }
                    try {
                        String turl = loginInfo.getFileServer() + bean.getPath() + "?filename=" + URLEncoder.encode(detailinfo.getReallyName() + "_" + shareUrl + ".pdf", "utf-8");
                        //ShareUtils.shareUrl(mContext, turl);
                        ShareDialog.showShaerDialog(mContext, showTitle, turl, workDetail.getCreateTime() )
                                .setScoreEventId(ScoreEventID.EVENT_DONWLOAD_REFINE)
                                .setContentId( recordId );
                    } catch (Exception e) {
                        AppLog.i(e.toString(), e);
                    }
                }
            }

            @Override
            public void onFail(HttpResponse response, Exception ex) {
                ToastUtils.show(mContext, "网络异常", Toast.LENGTH_SHORT);
            }
        });
    }
    //分享下载只能点击一次
    private AtomicBoolean btnShare = new AtomicBoolean( false );

    //变式训练下载分享
    private void shareVariantTrainDownload(){
    //private void shareUrl( final PracticeBean bean ){
        //
        final LoginInfo loginInfo = AccountUtils.getLoginUser();
        final UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
        final MyTutorClassInfo classInfo = AccountUtils.getCurrentClassInfo();
        if (detailinfo == null || loginInfo == null || classInfo == null)
            return;

        if( btnShare.get() ){
            ToastUtils.show( mContext,"请不要重复点击");
            return;
        }
        btnShare.set( true );

        PracticeModel mPracticeModel = new PracticeModel();

        RequestListener requestListener = new RequestListener<ShareBean>() {
            @Override
            public void onSuccess(ShareBean shareBean) {
                if(shareBean ==null || TextUtils.isEmpty(shareBean.getPath()) ){
                    ToastUtils.show( mContext, R.string.nofind_practice );
                }
                //没有购买，不能分享 抛出异常处理
//                    else if( shareBean.getBuy()==0 ) {
//                        ToastUtils.show( getContext(), R.string.share_after_buy );
//                    }
                //开始分享
                else{

                    //设置ID,详情界面使用
                    ddworkId = shareBean.getExamId();

                    localWorkInfo.setWorkId( ddworkId );
                    workDetail.setWorkId( ddworkId );

                    //通知变式训练本，进行刷新
                    EventBusUtils.postDelay(new RefreshVariantEvent(), new Handler());
                    if(isScored){
                        EventBusUtils.postDelay(new UploadScorePlanEvent( "" ), new Handler());
                    }
                    try {
                        //String fileName = detailinfo.getReallyName()+ "_" + nameView.getText()+".pdf";
                        String turl = loginInfo.getFileServer() + shareBean.getPath() + "?filename=" + URLEncoder.encode(detailinfo.getReallyName() + "_" + shareUrl + ".pdf", "utf-8");
                        ShareDialog.showShaerDialog(mContext, showTitle, turl, workDetail.getCreateTime() )
                                .setScoreEventId( ScoreEventID.EVENT_DOWNLOAD_VARIENT_TRAINING )
                                .setContentId( recordId );
                    }catch (Exception e){
                        AppLog.i( e.toString(), e);
                    }
                }
                btnShare.set( false );
            }

            @Override
            public void onFail(HttpResponse response, Exception ex) {
                btnShare.set( false );
                if( 20236 == response.getCode() ){
                    ToastUtils.show( mContext, R.string.share_after_buy );
                }else{
                    ToastUtils.show( mContext, R.string.server_error );
                }
            }
        };

        //PracticeProductBean mPracticeProductBean = bean.getProductBean();
        mPracticeModel.sharePractice( classInfo.getClassId(), detailinfo.getStudentId(), productId, recordId, requestListener );
    }

    //精品套题分享下载
    private void shareClassPracticeDownload(){

        final LoginInfo loginInfo = AccountUtils.getLoginUser();
        final UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
        final MyTutorClassInfo classInfo = AccountUtils.getCurrentClassInfo();
        if (detailinfo == null || loginInfo == null || classInfo == null)
            return;

        if( btnShare.get() ){
            ToastUtils.show( mContext,"请不要重复点击");
            return;
        }
        btnShare.set( true );

        //      private void shareUrl( final MyTutorClassInfo classInfo, final LoginInfo loginInfo, final UserDetailinfo detailinfo, final PracticeBean bean ){
        PracticeModel mPracticeModel = new PracticeModel();

        RequestListener requestListener = new RequestListener<ShareBean>() {
            @Override
            public void onSuccess(ShareBean shareBean) {
                if(shareBean ==null || TextUtils.isEmpty(shareBean.getPath()) ){
                    ToastUtils.show( mContext, R.string.nofind_practice );
                }
                //没有购买，不能分享 抛出异常处理
//                    else if( shareBean.getBuy()==0 ) {
//                        ToastUtils.show( getContext(), R.string.share_after_buy );
//                    }
                //开始分享
                else{
                    //设置ID,详情界面使用
                    ddworkId = shareBean.getExamId();
                    localWorkInfo.setWorkId( ddworkId );
                    workDetail.setWorkId( ddworkId );
                    //通知精品套题，进行刷新
                    EventBusUtils.postDelay(new RefreshPracticeEvent(), new Handler());
                    if(isScored){
                        EventBusUtils.postDelay(new UploadScorePlanEvent( "" ), new Handler());
                    }
                    try {
                        //String fileName = detailinfo.getReallyName()+ "_" + nameView.getText()+".pdf";
                        //fileName = detailinfo.getReallyName()+ "_" + nameView.getText()+ "_" + kwpointView.getText()+".pdf";
                        String turl = loginInfo.getFileServer() + shareBean.getPath() + "?filename=" + URLEncoder.encode(detailinfo.getReallyName() + "_" + shareUrl + ".pdf", "utf-8");
                        ShareDialog.showShaerDialog(mContext, showTitle, turl, workDetail.getCreateTime() )
                                .setScoreEventId( ScoreEventID.EVENT_DOWNLOAD_EXCLUSIVE_PAPER )
                                .setContentId( recordId );
                    }catch (Exception e){
                        AppLog.i( e.toString(), e);
                    }
                }
                btnShare.set( false );
            }

            @Override
            public void onFail(HttpResponse response, Exception ex) {
                btnShare.set( false );
                if( 20236 == response.getCode() ){
                    ToastUtils.show( mContext, R.string.share_after_buy );
                }else{
                    ToastUtils.show( mContext, R.string.server_error );
                }
            }
        };
        mPracticeModel.shareClassicPractice( classInfo.getClassId(), detailinfo.getStudentId(), productId, recordId, requestListener );
    }

    //普通作业考试 分享下载
    private void shareCommonWorkDownload(final String reportName, final UserDetailinfo userDetailinfo ){

        final String examId = localWorkInfo.getWorkId();

        //先提交分享记录
        myStudyModel.shareDDWorkRecord(examId, new RequestListener() {
            @Override
            public void onSuccess(Object res) {
                if (res instanceof Boolean && (Boolean) res) {  //
                    String url = AppRequestConst.RESTFUL_ADDRESS + AppRequestConst.GET_DDWORK_DOWNLOAD + localWorkInfo.getWorkId() + "?studentId=" + userDetailinfo.getStudentId();
                    //ShareUtils.shareUrl(mContext, url);
                    ShareDialog.showShaerDialog(mContext, reportName, url, workDetail.getCreateTime() );

                } else {
                    ToastUtils.showShort(mContext, "分享失败");
                }
            }

            @Override
            public void onFail(HttpResponse response, Exception ex) {
                AlertManager.showUploadErrorInfo(mContext, ex);
            }
        });
    }

    //根据作业类型获得作业的privilegeId
    private String getPrivilegeIdByWorkType() {
        String privilegeId = null;
        if (workDetail == null)
            return null;

        //校内教辅作业
        if (AppConst.WORK_TYPE_LEARNBOOK.equals(workDetail.getSourceType()))
            privilegeId = AppConst.PRIVILEGE_ASSISTANTWORK;
            //周练作业   套题类型
        else if (AppConst.WORK_TYPE_WEEKTRAIN.equals(workDetail.getSourceType()) || AppConst.WORK_TYPE_NORMAL.equals(workDetail.getSourceType()))
            privilegeId = AppConst.PRIVILEGE_SETWORK;
            //假期作业
        else if (AppConst.WORK_TYPE_VACATION_WORK.equals(workDetail.getSourceType()))
            privilegeId = AppConst.PRIVILEGE_VACATION_WORK;

        //错题本周题练作业  错题精炼
        else if (AppConst.WORK_TYPE_EBOOKWEEKTRAIN.equals(workDetail.getSourceType()) || AppConst.WORK_TYPE_MONTH_REFINE.equals(workDetail.getSourceType()) || AppConst.WORK_TYPE_CUSTOM_REFINE.equals(workDetail.getSourceType()) ){
            privilegeId = AppConst.PRIVILEGE_QUESTION_WEEKPRACTICE;
        }
            //原教辅作业
        else if (WORK_TYPE_ORIGINALMATERIAL.equals(workDetail.getSourceType()))
            privilegeId = AppConst.PRIVILEGE_ORIGINALMATERIAL;
            //精品套题
        else if (WORK_TYPE_CLASSIC.equals(workDetail.getSourceType()))
            privilegeId = AppConst.PRIVILEGE_CLASSICPRATICE;

        return privilegeId;
    }

    //是否班级布置作业,
    private boolean isClassWork() {

        boolean classWork = false;
        if (workDetail == null)
            return false;

        if (AppConst.WORK_TYPE_LEARNBOOK.equals(workDetail.getSourceType())           //校内教辅作业
                || AppConst.WORK_TYPE_WEEKTRAIN.equals(workDetail.getSourceType())        //周练作业
                || AppConst.WORK_TYPE_NORMAL.equals(workDetail.getSourceType())            //套题类型
                || AppConst.WORK_TYPE_VACATION_WORK.equals(workDetail.getSourceType())    //假期作业
                || AppConst.WORK_TYPE_CLASSIC.equals(workDetail.getSourceType()))  //精品套题
            classWork = true;

        return classWork;
    }
    //根据privilegeId
    //    private String getUploadDialogTips( String privilegeId ){
    //        if( TextUtils.isEmpty(privilegeId) ) return "";
    //
    //        return "此功能为个性化服务功能，需单独兑换，请先兑换后再使用。";
    //    }

    //    private void goActivityByPrivilege( String privilegeId ){
    //        if( TextUtils.isEmpty(privilegeId) ) return;
    //
    //        //校内教辅作业
    //        if( privilegeId.equals( AppConst.PRIVILEGE_ASSISTANTWORK ) ){
    //            ProductExchangeActivity.gotoProductExchangeActivity( mContext, "校内教辅作业", privilegeId );
    //        }
    //        //周练作业   套题类型
    //        else if( privilegeId.equals( AppConst.PRIVILEGE_SETWORK ) ){
    //            ProductExchangeActivity.gotoProductExchangeActivity( mContext, "校内套题作业", privilegeId );
    //        }
    //        //假期作业
    //        else if( privilegeId.equals( AppConst.PRIVILEGE_VACATION_WORK ) ){
    //            ProductExchangeActivity.gotoProductExchangeActivity( mContext, "假期作业", privilegeId );
    //        }
    //        //原版教辅作业
    //        else if( privilegeId.equals( AppConst.PRIVILEGE_ORIGINALMATERIAL) ){
    //            ProductExchangeActivity.gotoProductExchangeActivity( mContext, "豆豆诊断作业", privilegeId );
    //        }
    //        //错题周题练作业
    //        else if( privilegeId.equals( AppConst.PRIVILEGE_QUESTION_WEEKPRACTICE ) ){
    //            finishAll();
    //            EventBus.getDefault().post(new JumpEvent(MainActivity.FRAGMENT_QUESTION_BOOK, ErrorBookFragment.MODEL_WEEKTRAIN));
    //        }
    //    }

    //查询原版教辅的信息
    private void getSelfWorkDetail(final LoginInfo loginInfo) {

        new LearnMaterialModel().querySelfWorkDetail(bookId, bookpages, new RequestListener<DDWorkDetail>() {
            @Override
            public void onSuccess(DDWorkDetail res) {
                if (isDestroyed())
                    return;
                workDetail = res;
                if (workDetail == null) {
                    mLoadingPager.showEmpty();
                    return;
                }
                //先设置提交类型
                workDetail.setUploadType( mLmUploadtype );
                mLoadingPager.showTarget();
                mainLayout.setVisibility(View.VISIBLE);

                //作业信息与本地数据进行关联处理
                DDWorkManager ddWorkManager = DDWorkManager.getDDWorkManager(mContext, loginInfo.getLoginName());
                if (ddWorkManager == null) {
                    ToastUtils.show(mContext, "DDWorkManager 管理器没有初始化", Toast.LENGTH_SHORT);
                    return;
                }

                //直接显示
                mCameraLayout.setVisibility(View.VISIBLE);

                workDetail.setWorkId("DDLM" + String.valueOf(System.currentTimeMillis()));

                workDetail.setSourceType(AppConst.WORK_TYPE_ORIGINALMATERIAL);

                //作业名称
                //String date = DateUtils.format( System.currentTimeMillis(), DateUtils.FORMAT_DATA);
                String title = mLmUploadtype==AppConst.UPLOAD_TYPE_CAMERA?"创建一次作业诊断":"创建一次已批阅的作业";//date + "-我的作业"+getMyWorkIndex( date, loginInfo.getAccountId() );
                workDetail.setWorkName(title);
                localWorkInfo = ddWorkManager.relateLocalWorkInfo(workDetail);
                localWorkInfo.setLearnMaterial(LocalWorkInfo.LMTYPE_DDCHECK);

                workToolbar.setTitle(title);

                ArrayList<LocalPageInfo> list = localWorkInfo.getPageList();

                //首页加载 错误 或者没有数据   显示无
                if (list == null || list.size() == 0) {
                    mLoadingPager.showEmpty();
                    return;
                }

                //                //带分数的题目处理
                //                int color1 = getResources().getColor( R.color.color_B3B3B3 );
                //                int color2 = getResources().getColor( R.color.color_91DD95 );
                //                boolean corrected = workDetail.getExerStatus() >= DDWorkDetail.WORK_CORRECTED;
                //                DDWorkUtil.dealQuestionWeekTrain( list, AppConst.WORK_TYPE_WEEKWORK.equals(workDetail.getSourceType()),corrected, corrected?color2:color1 );

                DDWorkUtil.dealOriginMaterailName(list, workDetail.getBookName());
                DDWorkUtil.dealAllQuestionSelect(list);
                DDWorkUtil.dealQuestionType(list);
                DDWorkUtil.dealMaterailName(list);
                DDWorkUtil.dealQuestionPageInfo(list, false);
                DDWorkUtil.dealUploadType( list, mLmUploadtype/* 原版教辅 要自己设置提交类型 */);
                //处理提交类型到每页上面
                DDWorkUtil.dealUploadType( list, workDetail.getUploadType() );
                if( AppConst.UPLOAD_TYPE_MARKED == mLmUploadtype ){
                    //提交已批阅的作业，默认都是正确的
                    //DDWorkUtil.dealQuestionCorrectRight(list);
                    //提交已批阅的作业,要设置属性
                    DDWorkUtil.dealQuestionCorrectUploadStaus(list);

                    showTipsDialog( loginInfo.getAccountId() );
                }

                //如果是下拉加载，清除原列表数据
                mPageAdapter.clear();
                mPageAdapter.addAll(list);
                mPageAdapter.setLocalWorkInfo(localWorkInfo);
                mPageAdapter.notifyDataSetChanged();

                //
                questionListView.setAdapter(mQuestionAdapter);

                //更新作业状态
                setWorkStatus(WORK_NONE);

                //启动网络监测
                monitorView.startMonitor();

                //设置题目数据
                mQuestionAdapter.setData(workDetail, false);

                //加载题目
                loadQuestions(0);

                //
                workToolbar.setRightTitle("拍照小tips");  //一直显示

            }

            @Override
            public void onFail(HttpResponse response, Exception ex) {
                if (isDestroyed())
                    return;
                mLoadingPager.showFault(ex);
            }
        });
    }

    //提交失败的未知原因
    private void showUploadFailDialog() {

        AlertManager.showCustomImageBtnDialog(mContext, getResources().getString(R.string.uploadfail_tip), "返回重新提交", "重新拍照", null, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                resetUploadStatus();
                loadData();
            }
        }, null);
    }

    //显示提交批阅作业的提示信息
    private void showTipsDialog(String accountId) {
        final SharedPreferences sp = mContext.getSharedPreferences( accountId, Activity.MODE_PRIVATE);
        final String key = "show_correct_tips";
        boolean show = sp.getBoolean( key, false);
        if( show ) return;

        CustomDialogNew dialog = AlertManager.showCustomImageBtnDialog(mContext, "该作业，需要你把老师已经批阅的作业拍照，并进行错题标记后提交。", "知道了", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sp.edit().putBoolean(key,true).apply();
            }
        }, null);
        dialog.setCloseViewVisibility(View.GONE);
    }

    private void resetUploadStatus() {
        if (localWorkInfo == null)
            return;

        localWorkInfo.init();
        DDWorkManager manager = DDWorkManager.getDDWorkManager();
        if (manager != null) {
            manager.saveData();
        }

        //提交按钮
        workStatusLayout.setEnabled(false);
        workStatusLayout.setBackgroundResource(R.drawable.ic_submission);
        //setWorkStatus(  );
        workStatusView.setText(isLearnMaterail ? "完成创建" : ((workDetail.getUploadType() == AppConst.UPLOAD_TYPE_MARKED)?"提交批阅结果":"提交作业"));

        //选中状态
        DDWorkUtil.dealAllQuestionSelect(localWorkInfo.getPageList());

        //拍照按钮
        mCameraLayout.setVisibility(View.VISIBLE);
        mCameraBtn.setVisibility(View.VISIBLE);

        mPageAdapter.notifyDataSetChanged();
        mQuestionAdapter.notifyDataSetChanged();
    }

    //    private String getMyWorkIndex( String currdate, String accountId ){
    //        String workdate = "lmdate", workIndex = accountId+"lmindex";
    //        int index = -1;
    //        SharedPreferences sharedPreferences = mContext.getSharedPreferences("LmWorkInfo", Activity.MODE_PRIVATE);
    //        String date = sharedPreferences.getString( workdate, null );
    //        //不一样，从01开始
    //        if( date != null && currdate.equals(date) ){
    //            index = sharedPreferences.getInt( workIndex, -1 ) + 1;
    //        }
    //        //更新记录
    //        sharedPreferences.edit().putString( workdate, currdate ).putInt( workIndex, index ).apply();
    //        if( index <= 0 ) return "";
    //        return index<10?"0"+index:String.valueOf(index);
    //    }

    private boolean showGuideView(boolean student, boolean forceShow) {
        String name = "lmcamera_tips.png";
        if (forceShow) {
            lmGuideView.setVisibility(View.VISIBLE);
            lmGuideView.setData(name);
            workToolbar.setRightTitle(null);
            workToolbar.setTitle(getString(R.string.camerawork_tips));
            return true;
        }

        LoginInfo loginInfo = AccountUtils.getLoginUser();
        if (loginInfo == null)
            return true;

        SharedPreferences sharedPreferences = mContext.getSharedPreferences("LmGuideView" + loginInfo.getAccountId(), Activity.MODE_PRIVATE);
        boolean show = sharedPreferences.getBoolean(student ? "show" : "teachershow", false);
        //
        if (show) {
            return true;
        } else {
            lmGuideView.setVisibility(View.VISIBLE);
            workToolbar.setRightTitle(null);
            workToolbar.setTitle(getString(R.string.camerawork_tips));
            lmGuideView.setData(name);
            return false;
        }
    }

    private void setGuideView() {
        LoginInfo loginInfo = AccountUtils.getLoginUser();
        if (loginInfo == null)
            return;

        SharedPreferences sharedPreferences = mContext.getSharedPreferences("LmGuideView" + loginInfo.getAccountId(), Activity.MODE_PRIVATE);
        sharedPreferences.edit().putBoolean(!workDetail.isOrginLearnMaterail() ? "show" : "teachershow", true).apply();
    }

    private void gotoLmActivity() {
        int index = mPageAdapter.getSelectIndex();
        if (index < 0) {
            return;
        }
        openLmActivity(mPageAdapter.getItem(index), bookRate, workDetail.isOrginLearnMaterail());
    }

    private void openLmActivity(LocalPageInfo localPageInfo, float rate, boolean teacher) {
        //        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        //            LMCameraActivity2.openActivity( mContext, localPageInfo, rate, teacher );
        //        }else {
        LMCameraActivity.openActivity(mContext, localPageInfo, rate, teacher, workDetail.getQuestionScore()>0);
        //        }
    }

    //
    private void showShareDiaolog( boolean cameraBtn ){

        if( TextUtils.isEmpty(priviledgeId) ){
            ToastUtils.show( mContext, "缺少参数" );
            return;
        }

        if( cameraBtn ){
            CustomDialogNew dialog = AlertManager.showCustomImageBtnDialog(mContext, "请确定已有4个定位点的答题纸，并完成答题；若无答题纸请先分享并下载。", "分享并下载",  "确定",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    execShareDownload();
                }
            }, null, null);
            dialog.getRightBtn().setBackgroundResource( R.drawable.bg_dialog_enter_r18 );
            dialog.getLeftBtn().setBackgroundResource( R.drawable.bg_rect_blue_r24white );
            dialog.getLeftBtn().setTextColor( mContext.getResources().getColor(R.color.color_3BCCD9));
        }else{
            AlertManager.showCustomImageBtnDialog(mContext, "需先分享并下载有4个定位点的答题纸，答题后再拍照上传～", "分享并下载", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    execShareDownload();
                }
            }, null);
        }

    }

    //执行分享下载功能
    private void execShareDownload(){
        //错题再练
        if( AppConst.PRIVILEGE_QUESTION_WEEKPRACTICE.equals(priviledgeId) || AppConst.PRIVILEGE_EXAM_RETRAINING.equals(priviledgeId)
           || AppConst.PRIVILEDGE_ERRORREFINE_MONTH.equals(priviledgeId)
           || AppConst.PRIVILEDGE_ERRORREFINE_CUSTOM.equals(priviledgeId) )
            shareWeekTrainDownload();
            //变式训练
        else if( AppConst.PRIVILEGE_WEEKLEAKFILLING.equals(priviledgeId) || AppConst.PRIVILEGE_EXAM_LEAKFILLING.equals(priviledgeId)
                || AppConst.PRIVILEDGE_VARTRAIN_MONTH.equals( priviledgeId )
                || AppConst.PRIVILEDGE_VARTRAIN_CUSTOM.equals( priviledgeId ) )
            shareVariantTrainDownload();

            //精品套题
        else if( AppConst.PRIVILEGE_CLASSICPRATICE.equals(priviledgeId) )
            shareClassPracticeDownload();
    }

    //使用权失效 纸阅
    private void priviledgeLosePaperCorrect( boolean submitfial, final AtomicBoolean atomicBoolean ){

        String message = (submitfial?"提交失败！":"")+"你的极速智能诊断特权已失效，点击“极速智能诊断”前去获得特权。";
        CustomDialogNew dialog = AlertManager.showCustomImageBtnDialog(mContext, message, "极速智能诊断", "上传老师的批阅结果", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {        //不够买，直接提交
                UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
                if( detailinfo!=null && !detailinfo.enableBuySuite() ){     //不允许买
                    ToastUtils.show( mContext, mContext.getResources().getString(R.string.tj_nobuy_tips) );
                }else{  //跳转到购买界面
                    BuySuiteActivity.startBuySuiteActivity(mContext, BuySuiteActivity.WORK, getPrivilegeIdByWorkType());
                }
                if( atomicBoolean!=null )atomicBoolean.set(false);
            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                workDetail.setUploadType( AppConst.UPLOAD_TYPE_MARKED );
                resetUploadStatus();
                loadData();
                if( atomicBoolean!=null )atomicBoolean.set(false);
            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //直接点击关闭按钮
                if( atomicBoolean!=null )atomicBoolean.set(false);
            }
        });
        dialog.getLeftBtn().setBackgroundResource( R.drawable.bg_dialog_enter_r18 );
        dialog.getRightBtn().setBackgroundResource( R.drawable.bg_rect_blue_r24white );
        dialog.getRightBtn().setTextColor( mContext.getResources().getColor(R.color.color_3BCCD9));
    }

    //失去使用权 网阅
    private void priviledgeLoseNetCorrect( /*boolean submitfial,*/ ){
        String message = "你的极速智能诊断特权已失效，点击“极速智能诊断”前去获得特权。";
        AlertManager.showCustomImageBtnDialog(mContext, message, "极速智能诊断","普通诊断", "逐行诊断,当日订正,高效必备", "等待老师整题批阅", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {        //不够买，直接提交

                UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
                if( detailinfo!=null && !detailinfo.enableBuySuite() ){     //不允许买
                    ToastUtils.show( mContext, mContext.getResources().getString(R.string.tj_nobuy_tips) );
                }else{  //跳转到购买界面
                    BuySuiteActivity.startBuySuiteActivity(mContext, BuySuiteActivity.WORK, getPrivilegeIdByWorkType());
                }
                mAtomicBoolean.set(false);
            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startCommitData(false);
            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //直接点击关闭按钮
                mAtomicBoolean.set(false);
            }
        });
    }
    //没有使用权 网阅
    private void priviledgeNouseNetCorrect( /*boolean submitfial,*/ ){
        AlertManager.showCustomImageBtnDialog(mContext, "请选择诊断方式并确认提交", "极速智能诊断", "普通诊断", "逐行诊断,当日订正,高效必备", "等待老师整题批阅", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {        //不够买，直接提交

                UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
                if( detailinfo!=null && !detailinfo.enableBuySuite() ){     //不允许买
                    ToastUtils.show( mContext, mContext.getResources().getString(R.string.tj_nobuy_tips) );
                }else{  //跳转到购买界面
                    BuySuiteActivity.startBuySuiteActivity(mContext, BuySuiteActivity.WORK, getPrivilegeIdByWorkType());
                }
                mAtomicBoolean.set(false);
            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startCommitData(false);
            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //直接点击关闭按钮
                mAtomicBoolean.set(false);
            }
        });
    }

    //---------------------------------------------------------------------------------------
    //侧边栏
    private void initSlidingMenu(ArrayList<LocalQuestionInfo> list) {

        slideLayout.setVisibility(View.VISIBLE);
        mFilterQuestionView.setVisibility(View.VISIBLE);

        drawerLayout.addDrawerListener(this);
        //drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, Gravity.END);
        DrawerLayoutUtil.setDrawerRightEdgeSize(mActivity, drawerLayout, 0.3f);

        mFilterQuestionView.setFilterListener(this);
        mFilterQuestionView.setData(list);
    }

    @Override
    public void filterChange() {
        mQuestionAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDrawerSlide(@Nonnull View drawerView, float slideOffset) {
    }

    @Override
    public void onDrawerOpened(@Nonnull View drawerView) {
    }

    @Override
    public void onDrawerClosed(@Nonnull View drawerView) {
    }

    @Override
    public void onDrawerStateChanged(int newState) {

    }

    @Subscribe
    public void receive(JumpStudyBeanEvent event) {
        AppLog.d("event = " + event );
        finish();
    }

    @Override
    public String getViewName() {
        return "work_detail";       //作业详情
    }


}

