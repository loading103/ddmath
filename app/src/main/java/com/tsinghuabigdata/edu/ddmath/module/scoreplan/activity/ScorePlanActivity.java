package com.tsinghuabigdata.edu.ddmath.module.scoreplan.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tsinghuabigdata.edu.ddmath.MVPModel.ProductModel;
import com.tsinghuabigdata.edu.ddmath.MVPModel.RequestListener;
import com.tsinghuabigdata.edu.ddmath.MVPModel.ScorePlanModel;
import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.activity.RoboActivity;
import com.tsinghuabigdata.edu.ddmath.adapter.PersonDetailsAdapter;
import com.tsinghuabigdata.edu.ddmath.adapter.PersonWeekAdapter;
import com.tsinghuabigdata.edu.ddmath.bean.LoginInfo;
import com.tsinghuabigdata.edu.ddmath.bean.MyTutorClassInfo;
import com.tsinghuabigdata.edu.ddmath.bean.PdfBean;
import com.tsinghuabigdata.edu.ddmath.bean.TaskBean;
import com.tsinghuabigdata.edu.ddmath.bean.UserDetailinfo;
import com.tsinghuabigdata.edu.ddmath.bean.WeekBean;
import com.tsinghuabigdata.edu.ddmath.bean.WeekDatailBean;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.constant.AppConst;
import com.tsinghuabigdata.edu.ddmath.event.LookWorkEvent;
import com.tsinghuabigdata.edu.ddmath.event.UploadScorePlanEvent;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.DDUploadActivity;
import com.tsinghuabigdata.edu.ddmath.module.errorbook.EBookDayCleanActivity;
import com.tsinghuabigdata.edu.ddmath.module.errorbook.HelpUtil;
import com.tsinghuabigdata.edu.ddmath.module.errorbook.PdfViewActivity;
import com.tsinghuabigdata.edu.ddmath.module.errorbook.bean.StageReviewBean;
import com.tsinghuabigdata.edu.ddmath.module.mycenter.dialog.ShareDialog;
import com.tsinghuabigdata.edu.ddmath.module.mystudybean.BuySuiteActivity;
import com.tsinghuabigdata.edu.ddmath.module.product.ProductUtil;
import com.tsinghuabigdata.edu.ddmath.module.product.bean.ProductUseTimesBean;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.AlertManager;
import com.tsinghuabigdata.edu.ddmath.util.AppLog;
import com.tsinghuabigdata.edu.ddmath.util.DateUtils;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.ToastUtils;
import com.tsinghuabigdata.edu.ddmath.view.LoadingPager;
import com.tsinghuabigdata.edu.ddmath.view.MyProgressDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 提分方案主页
 */
public class ScorePlanActivity extends RoboActivity implements View.OnClickListener, AdapterView.OnItemClickListener, PersonDetailsAdapter.mBtnOnClickListener {

    public static void openActivity(Context context){
        if( context!=null ) context.startActivity( new Intent( context, ScorePlanActivity.class));
    }
    @ViewInject(R.id.toolbar_lefttitle)
    private LinearLayout toolbar_lefttitle;
    @ViewInject(R.id.toolbar_title)
    private TextView toolbar_title;
    @ViewInject(R.id.toolbar_right_image)
    private ImageView toolbar_right_image;
    @ViewInject(R.id.toolbar_right_image_02)
    private ImageView toolbar_right_image_02;

    @ViewInject(R.id.ddwork_list_worklist)
    private ListView pageListView;
    @ViewInject(R.id.ddwork_list_qustionlist)
    private ListView detailListview;
    @ViewInject(R.id.loadingPager)
    private LoadingPager mLoadingPager;
    @ViewInject(R.id.loadingPager_details)
    private LoadingPager loadingPager_details;
    @ViewInject(R.id.ll_guide_no)
    private LinearLayout ll_gudie;
    @ViewInject(R.id.iv_zhidao)
    private ImageView iv_zhidao;

    @ViewInject(R.id.ddwork_list_mainlayout)
    private LinearLayout ll_content;

    private Context mContext;
    private PersonWeekAdapter madapter;
    private PersonDetailsAdapter mDetailAdapter;
    public String studentId,mSchoolId;
    public String classid;
    public int lastposition=0; //记录点击的是周几（默认为第一个）
    private List<WeekBean> weekdata=new ArrayList<>();
    public static final int  BTN_TYPE_CORRETING=1;  //错题联系
    public static final int  BTN_TYPE_WEIKE=2;       //微课联系
    public static final int  BTN_TYPE_DEFINE=3;      //自定义
    public static final int  BTN_TYPE_WEEKPRACTICE=4;  //周练习
    public static final int  BTN_TYPE_ONEKEYDOWN=5;    //一键下载


    MyProgressDialog mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(GlobalData.isPad()?R.layout.activity_person_subpro:R.layout.activity_person_subpro_phone);
        EventBus.getDefault().register(this);
        x.view().inject(this);
        mContext = this;
        mProgressBar = new MyProgressDialog(this);
        initView();
        initData();
    }
    private void initView() {
        toolbar_lefttitle.setOnClickListener(this);
        toolbar_right_image.setOnClickListener(this);
        toolbar_right_image_02.setOnClickListener(this);
        pageListView.setOnItemClickListener(this);
        iv_zhidao.setOnClickListener(this);
    }
    private void initData() {
        getStudentInfo();
        loadWeekDayData(false);
    }
    /**
     * 无数据显示
     */
    public void showEmpetView(List<WeekBean> weekdata,int lastposition){
        if(weekdata==null || weekdata.size()==0) {
            return;
        }
        detailListview.setVisibility(View.INVISIBLE);
        loadingPager_details.showEmpty();
        loadingPager_details.setEmptyText( weekdata.get(lastposition).getDayString().equals("今日") ?"今日还没有新的学习任务～":String.format(Locale.getDefault(), "%s没有学习任务～",  weekdata.get(lastposition).getWeek()));
    }

    public void showFaultView( Exception ex){
        detailListview.setVisibility(View.INVISIBLE);
        loadingPager_details.showFault(ex);
        loadingPager_details.setListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingPager_details.showLoading();
                loadWeekDetailData(lastposition);
            }
        });
    }

    /**
     * 给数据源添加item左边图标消失标志
     */
    private List<WeekDatailBean>  dealDetailData(List<WeekDatailBean> weeks) {
        if(weeks==null || weeks.size()==0){
            weeks.clear();
            return weeks;
        }
        for (int i = 0; i < weeks.size(); i++) {
            if(i==0){
                weeks.get(i).setVisible(true);
            }else{
                if(weeks.get(i).getWeekType()==weeks.get(i-1).getWeekType()){
                    weeks.get(i).setVisible(false);
                }else {
                    weeks.get(i).setVisible(true);
                }
            }

        }
        return weeks;
    }
    /**
     * 给数据源添加item左边虚线消失标志
     */
    public List<WeekDatailBean> getLineDetailData(List<WeekDatailBean> weekData){
        if(weekData==null || weekData.size()==0){
            weekData.clear();
            return weekData;
        }
        if(weekData.size()==1){
            weekData.get(0).setDowmLineVisible(false);
            weekData.get(0).setUpLineVisible(false);
            return  weekData;
        }
        int pos=0;
        for (int i = weekData.size()-1; i >0 ; i--) {
            if(weekData.get(i).getWeekType()!=weekData.get(i - 1).getWeekType()){
                pos=i;
                break;
            }
        }
        if(pos==0){
            for (int i =0; i <weekData.size() ; i++) {
                weekData.get(i).setDowmLineVisible(false);
                weekData.get(i).setUpLineVisible(false);
            }
        }else {
            for (int i = 0; i <pos ; i++) {
                if(i==0){
                    weekData.get(i).setUpLineVisible(false);
                    weekData.get(i).setDowmLineVisible(true);
                }else {
                    weekData.get(i).setDowmLineVisible(true);
                    weekData.get(i).setUpLineVisible(true);
                }
            }
            for (int i = pos; i <weekData.size() ; i++) {
                if(i==pos){
                    weekData.get(i).setUpLineVisible(true);
                    weekData.get(i).setDowmLineVisible(false);
                }else {
                    weekData.get(i).setUpLineVisible(false);
                    weekData.get(i).setDowmLineVisible(false);
                }
            }
        }
        return  weekData;
    }
    /**
     * 获取班级和个人id
     */
    public void  getStudentInfo() {
        final MyTutorClassInfo classInfo = AccountUtils.getCurrentClassInfo();
        final LoginInfo loginInfo = AccountUtils.getLoginUser();
        final UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
        if (classInfo== null || TextUtils.isEmpty(classInfo.getClassId())) {
            mLoadingPager.showEmpty( "你还没有加入班级" );
            return;
        }
        if (detailinfo == null || loginInfo==null) {
            return;
        }
        studentId = detailinfo.getStudentId();
        mSchoolId = detailinfo.getSchoolId();
        classid=classInfo.getClassId();
    }
    /**
     * 获取网络数据(获取星期列表)
     */
    private void loadWeekDayData(final boolean isrefresh) {
        getStudentInfo();
        mLoadingPager.showLoading();
        ll_content.setVisibility(View.INVISIBLE);
        new ScorePlanModel().queryWeekCheckWork(studentId, classid, new RequestListener<List<WeekBean>>() {
            @Override
            public void onSuccess(List<WeekBean> res) {
                if(res==null || res.size()==0){
                    mLoadingPager.showEmpty();
                    return;
                }
                mLoadingPager.setVisibility(View.INVISIBLE);
                ll_content.setVisibility(View.VISIBLE);
                weekdata=res;

                madapter=new PersonWeekAdapter(ScorePlanActivity.this,weekdata);
                pageListView.setAdapter(madapter);

                if(weekdata.size()>=2 && weekdata.get(1).getWeek().equals("周五") && weekdata.get(1).getDayString().equals("今日") &&!isrefresh){
                    weekdata.get(1).setSelected(true);
                    weekdata.get(0).setSelected(false);
                    madapter.notifyDataSetChanged();
                    lastposition=1;
                }
                if(lastposition!=0 && isrefresh){ //返回刷新数据
                    weekdata.get(lastposition).setSelected(true);
                    weekdata.get(0).setSelected(false);
                    pageListView.setSelection(lastposition);
                    madapter.notifyDataSetChanged();
                }
                loadWeekDetailData(lastposition);   //默认加载第一个

            }
            @Override
            public void onFail(HttpResponse response, Exception ex) {
                mLoadingPager.showEmpty();
            }
        });
    }
    /**
     * 获取网络数据(获取错题订正和同步微课列表)
     */
    private void loadCorrectedData(long date , final int positions) {
        new ScorePlanModel().queryCorretDataWork(studentId, classid,date, new RequestListener<List<WeekDatailBean>>() {
            @Override
            public void onSuccess(List<WeekDatailBean> res) {
                data2.clear();
                getcorreted=true;
                if(res==null || res.size()==0){
                    handleDatas(positions);
                    return;
                }
                data2.addAll(res);
                for (int i = 0; i < data2.size(); i++) {
                    if(data2.get(i).getSourceType().equals("videocourse")){
                        data2.get(i).setWeekType(3);
                    }else {
                        data2.get(i).setWeekType(2);
                    }
                }
                handleDatas(positions);
            }

            @Override
            public void onFail(HttpResponse response, Exception ex) {
                data2.clear();
                getcorreted=false;
                showFaultView(ex);
            }
        });

    }
    /**
     * 获取周错题再练周变式训练
     */
    private void loadWeekdTrainData(final long date, final int positions) {
        new ScorePlanModel().queryWeekTrainWork(studentId, classid,date, new RequestListener<TaskBean>() {
            @Override
            public void onSuccess(TaskBean res) {
                data1.clear();
                getweeked=true;
                if(res==null){
                    handleDatas(positions);
                    return;
                }
                if((res.getTaskList()==null|| res.getTaskList().size()==0) && res.isHasDownload()){
                    data1.add(new WeekDatailBean("无数据"));
                    for (int i = 0; i < data1.size(); i++) {
                        data1.get(i).setWeekType(1);
                    }
                    taskBean=res;
                    handleDatas(positions);
                    return;
                }
                taskBean=res;
                data1.addAll(res.getTaskList());
                for (int i = 0; i < data1.size(); i++) {
                    data1.get(i).setWeekType(1);
                }
                handleDatas(positions);
            }
            @Override
            public void onFail(HttpResponse response, Exception ex) {
                data1.clear();
                getweeked=false;
                showFaultView(ex);
            }
        });
    }
    /**
     * 自定义错题本变式训练错题列表
     */
    private void loadDefinedData(long date, final int positions) {
        new ScorePlanModel().queryDefineDataWork(studentId, classid, date, new RequestListener<List<WeekDatailBean>>() {
            @Override
            public void onSuccess(List<WeekDatailBean> res) {
                data3.clear();
                getdefineed=true;
                if (res == null || res.size() == 0) {
                    handleDatas(positions);
                    return;
                }
                data3.addAll(res);
                for (int i = 0; i < data3.size(); i++) {
                    data3.get(i).setWeekType(4);
                }
                handleDatas(positions);
            }
            @Override
            public void onFail(HttpResponse response, Exception ex) {
                data3.clear();
                getdefineed=false;
                showFaultView(ex);
            }
        });
    }

    private boolean getcorreted=false;
    private boolean getweeked=false;
    private boolean getdefineed=false;
    private List<WeekDatailBean>data1=new ArrayList<>();
    private List<WeekDatailBean>data2=new ArrayList<>();
    private List<WeekDatailBean>data3=new ArrayList<>();
    private List<WeekDatailBean> detailsdataed=new ArrayList<>(); //获取到虚线标志和图标消失标志后
    private void loadWeekDetailData(final int position) {
        loadingPager_details.setVisibility(View.INVISIBLE);
        detailListview.setVisibility(View.INVISIBLE);
        final long dateTime = weekdata.get(position).getDateTime();
        getcorreted=false;
        getdefineed=false;
        getweeked=false;
        loadingPager_details.showLoading();
        loadCorrectedData(dateTime,position);
        loadDefinedData(dateTime,position);
        if(weekdata.get(position).getWeek().equals("周末")){
            loadWeekdTrainData(dateTime,position);
        }else {
            data1.clear();
            getweeked=true;//周一到周五不调用周练习的接口
        }
    }
    private  TaskBean taskBean;
    private void handleDatas(int pos){
        List<WeekDatailBean> detailsdata=new ArrayList<>();
        if(getcorreted && getdefineed &&getweeked){
            loadingPager_details.setVisibility(View.GONE);
            detailsdata.addAll(data1);
            detailsdata.addAll(data2);
            detailsdata.addAll(data3);

            boolean isfirst = getSharedPreferences("info", MODE_PRIVATE).getBoolean("isfirst", true);
            if(weekdata.get(lastposition).getWeek().equals("周末") && isfirst){
                ll_gudie.setVisibility(View.VISIBLE);
                getSharedPreferences("info", MODE_PRIVATE).edit().putBoolean("isfirst", false).commit();
            }
            if(detailsdata.size()==0){
                showEmpetView(weekdata,lastposition);
                return;
            }
            loadingPager_details.setVisibility(View.INVISIBLE);
            detailListview.setVisibility(View.VISIBLE);
            detailsdataed.clear();
            detailsdataed.addAll(getLineDetailData(dealDetailData(detailsdata)));
            weekdata.get(pos).setDatas(detailsdataed);
            mDetailAdapter=new PersonDetailsAdapter(ScorePlanActivity.this,detailsdataed);
            if(taskBean!=null){
                mDetailAdapter.setTaskBean(taskBean);
            }
            detailListview.setAdapter(mDetailAdapter);
            mDetailAdapter.setmBtnOnClickListener(ScorePlanActivity.this);

        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.toolbar_lefttitle:
                finishAll();
                break;
            case R.id.toolbar_right_image:
                HelpUtil.showHelpActivity( this, "定制学使用说明", "Q010");
                break;
            case R.id.toolbar_right_image_02:
                ScorePlanIntroActivity.openActivity( mContext, ScorePlanIntroActivity.FROM_TYPE_USING,true, null);
                break;
            case R.id.iv_zhidao:
                ll_gudie.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        finishAll();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(lastposition==position){
            return;
        }
        weekdata.get(position).setSelected(true);
        weekdata.get(lastposition).setSelected(false);
        madapter.notifyDataSetChanged();
        lastposition=position;
        if(weekdata.get(position).getDatas()!=null && weekdata.get(position).getDatas().size()>0){ //右边的数据将数据源保存在临时数据中有直接用  没有重新获取
            loadingPager_details.setVisibility(View.INVISIBLE);
            detailListview.setVisibility(View.VISIBLE);
            detailsdataed.clear();
            detailsdataed.addAll(weekdata.get(position).getDatas());
            mDetailAdapter.notifyDataSetChanged();
        }else {
            loadWeekDetailData(position);
        }
    }
    @Override
    public void mBtnOnclick(int position,int type) {
        WeekDatailBean weekDatailBean = detailsdataed.get(position);
        switch (type){
            case BTN_TYPE_CORRETING:
                clickJudgeCorretRevise(weekDatailBean);                            //错题订正
                break;
            case BTN_TYPE_WEIKE:
                clickJudgeWeike(weekDatailBean,position);                            //同步微课
                break;
            case BTN_TYPE_DEFINE:   //自定义
                if(weekDatailBean.getSourceType().equals("dd_period_review") ){  //错题浏览本
                    clickJudgeReviewResver(weekDatailBean,position);
                }else {                                                          //错题再练本,变式训练
                    clickJudgeUserPriviledge( weekDatailBean,true);
                }
                break;
            case BTN_TYPE_WEEKPRACTICE:
                if(weekDatailBean.getSourceType().equals("dd_period_review") ){  //周错题浏览本
                    checkPession(weekDatailBean.getProductId(),hasPrivilegeid,position,"dd_period_review",false);
                }else {//周错题再练本,变式训练
                    if(weekDatailBean.getSourceType().equals("dd_week_exercise")){
                        hasPrivilegeid=AppConst.PRIVILEGE_QUESTION_WEEKPRACTICE;
                        checkPession(weekDatailBean.getProductId(),hasPrivilegeid,position,"dd_week_exercise",false);
                    }else {
                        hasPrivilegeid=AppConst.PRIVILEGE_WEEKLEAKFILLING;
                        checkPession(weekDatailBean.getProductId(),hasPrivilegeid,position,"exclusive_paper",false);
                    }
                }
                break;
            case BTN_TYPE_ONEKEYDOWN:  //一键下载
                clickJudgeOneKeyDown(taskBean,lastposition);
                break;
        }
    }


    /**
     * 错题订正
     */
    private void clickJudgeCorretRevise(WeekDatailBean weekDatailBean) {
        if( weekDatailBean == null ) return;
        Intent intent = new Intent( ScorePlanActivity.this, EBookDayCleanActivity.class );
        intent.putExtra( EBookDayCleanActivity.PARAM_DATE, DateUtils.format(weekDatailBean.getCreateTime()));
        intent.putExtra( EBookDayCleanActivity.PARAM_TITLE, weekDatailBean.getName() );
        intent.putExtra( EBookDayCleanActivity.PARAM_INDEX,weekDatailBean.getListNum() );
        intent.putExtra( EBookDayCleanActivity.PARAM_TOTAL, weekDatailBean.getQuestionCount());
        intent.putExtra( EBookDayCleanActivity.PARAM_UNCORRNUMBER, weekDatailBean.getUnReviseCount());
        intent.putExtra( EBookDayCleanActivity.PARAM_SCORED, true);
        intent.putExtra( EBookDayCleanActivity.PARAM_OVERDO, weekDatailBean.getOverdue());
        startActivity( intent );
    }
    /**
     * 同步微课跳转
     */
    private void clickJudgeWeike(  WeekDatailBean weekDatailBean,int index) {
        //先判断是否有使用权
        if (weekDatailBean.getHasPrivilege()) {
            ScorePlanVideoPayActivity.openScorePlanVideoPayActivity( mContext, weekDatailBean );
        } else {
            getProductUseTimes(weekDatailBean,index);
        }
    }
    //获取名师精讲商品剩余免费兑换次数
    private void getProductUseTimes(final WeekDatailBean weekDatailBean, final int index) {
        if (btnShare.get()) {
            ToastUtils.show(mContext, "请稍候在点击...");
            return;
        }
        btnShare.set(true);

        ProductUtil.videoCheckPermissionAndExchange(mContext, weekDatailBean.getContentId(), btnShare,ProductUtil.FROM_CUSTOM, new ProductUtil.ProductCallBack() {
            @Override
            public void onSuccess() {
                btnShare.set(false);
                //成功 状态改变
                detailsdataed.get(index).setHasPrivilege(true);
                //提示用户
                ScorePlanVideoPayActivity.openScorePlanVideoPayActivity(mContext, weekDatailBean);
            }
        });
    }
    /**
     * 错题再练本跳转、变式训练本
     */
    //分享下载只能点击一次
    private AtomicBoolean btnShare = new AtomicBoolean( false );
    private String hasPrivilegeid=null;
    //点击按钮先判断用户是否有权限进入详情界面
    private void clickJudgeUserPriviledge(final WeekDatailBean bean, final boolean isfine){
        if( btnShare.get() ){
            ToastUtils.show( mContext,"请不要重复点击");
            return;
        }
        btnShare.set( true );
        String productId="";
        if(!TextUtils.isEmpty(bean.getProductId())){
            productId=bean.getProductId();
        }
        //先判断是否已购买
        if( !bean.getHasPrivilege() ){     //没有购买
            ProductUtil.productCheckPermissionAndExchange( mContext, productId,bean.getPriviledgeId(), bean.getContentId(), "错题再练本", btnShare,ProductUtil.FROM_CUSTOM, new ProductUtil.ProductCallBack(){
                @Override
                public void onSuccess() {
                    //兑换成功处理   状态改变
                    bean.setHasPrivilege( true );
                    //兑换成功提示
                    gotoDetailActivity(bean, bean.getPriviledgeId(),isfine);
                }
            } );
        }
        //已购买,直接分享下载
        else{
            gotoDetailActivity(bean,bean.getPriviledgeId(),isfine);
            btnShare.set( false );
        }
    }

    //打开题目详情界面
    private void gotoDetailActivity(final WeekDatailBean bean,final String hasPrivilegeid,boolean isfine){
        LoginInfo loginInfo = AccountUtils.getLoginUser();
        UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
        if( bean == null || detailinfo == null || loginInfo == null ) return;
        Intent intent = new Intent(mContext, DDUploadActivity.class);
        intent.putExtra(DDUploadActivity.PARAM_DDWORKID, bean.getExamId());
        intent.putExtra(DDUploadActivity.PARAM_RECORDID, bean.getContentId());
        intent.putExtra(DDUploadActivity.PARAM_TITLE, bean.getName()  );
        intent.putExtra(DDUploadActivity.PARAM_SHOWTITLE, bean.getName() );
        intent.putExtra(DDUploadActivity.PARAM_HASBUY, bean.getHasPrivilege() );
        intent.putExtra(DDUploadActivity.PARAM_PRIVILEDGEID, hasPrivilegeid );
        intent.putExtra(DDUploadActivity.PARAM_PRODUCTID, bean.getProductId() );
        intent.putExtra(DDUploadActivity.PARAM_OVERDO, bean.getOverdue() );
        intent.putExtra(DDUploadActivity.PARAM_SCORED, true );
        intent.putExtra(DDUploadActivity.PARAM_DEDINE, isfine );
        mContext.startActivity(intent);
    }
    /**
     * 错题浏览本
     */
    private void clickJudgeReviewResver(final WeekDatailBean bean, final int index){
        if( bean == null ) return;
        final UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
        final LoginInfo loginInfo = AccountUtils.getLoginUser();
        final MyTutorClassInfo classInfo = AccountUtils.getCurrentClassInfo();
        if( detailinfo==null || loginInfo == null || classInfo == null ){
            ToastUtils.show( mContext, "请登录", Toast.LENGTH_SHORT );
            return;
        }

        if( btnShare.get() ){
            ToastUtils.show( mContext,"正在进行分享并下载，请稍候...");
            return;
        }
        btnShare.set( true );
        String productId="";
        if(TextUtils.isEmpty(bean.getProductId())){
            productId=bean.getProductId();
        }
        //先判断是否已购买
        if( !bean.getHasPrivilege() && bean.getDownloadStatus()==0 ){
            ProductUtil.productCheckPermissionAndExchange( mContext, productId,bean.getPriviledgeId(), bean.getContentId(), "错题浏览本", btnShare,ProductUtil.FROM_CUSTOM, new ProductUtil.ProductCallBack(){
                @Override
                public void onSuccess() {
                    //兑换成功 处理   状态改变
                    detailsdataed.get(index).setHasPrivilege( true );
                    //兑换成功提示
                    gotoDetailActivity(bean);
                }
            } );
        }else{
            gotoDetailActivity(bean);
            btnShare.set( false );
        }
    }
    //打开题目详情界面
    private void gotoDetailActivity(WeekDatailBean bean){
        StageReviewBean stageReviewBean = new StageReviewBean();
        stageReviewBean.setDownloadStatus(bean.getDownloadStatus());
        stageReviewBean.setScoretitle(bean.getName());
        stageReviewBean.setQuestionsId(bean.getContentId());
        stageReviewBean.setHasPrivilege(bean.getHasPrivilege());
        stageReviewBean.setCreateTime(bean.getCreateTime());
        stageReviewBean.setFrom("score");
        if( bean == null ) return;
        PdfViewActivity.startPreviewPdfViewActivity( mContext, stageReviewBean );
    }
    /**
     * 一键下载(先检查错题浏览本的使用权限)
     */
    private void clickJudgeOneKeyDown(final TaskBean bean, final int index){

        if(bean.getProductId()==null){
            bean.setProductId("");
        }
        checkPession(bean.getProductId(),AppConst.PRIVILEGE_PERIODREVIEW,index,"dd_period_review",true);
    }
    //获取一键下载pdfUrl
    private void getPdfUrl(String studentId, final long dateTime) {
        new ScorePlanModel().oneKeyDownPdf(studentId, dateTime, new RequestListener<PdfBean>() {
            @Override
            public void onSuccess(PdfBean res) {
                mProgressBar.dismiss();
                if(res==null){
                    return;
                }
                if(!TextUtils.isEmpty(res.getPdfUrl())){
                    //下载成功后 需要跟新有新增的提示
                    if(taskBean!=null && taskBean.getHasNewReport()){
                        changeNewResported();
                    }

                    //更新列表//
                     new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                loadWeekDetailData(lastposition);
                            }
                     },1000);

                    LoginInfo loginInfo = AccountUtils.getLoginUser();
                    UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
                    if( loginInfo == null ){
                        ToastUtils.show( mContext, "请登录", Toast.LENGTH_SHORT );
                        finish();
                        return;
                    }
                    String pdfUrl = loginInfo.getFileServer()+res.getPdfUrl();
                    try{
                        if(TextUtils.isEmpty( res.getTitle())){
                            res.setTitle( "周个人专属提分方案");
                        }
                        String turl = pdfUrl + "?filename=" + URLEncoder.encode( detailinfo.getReallyName()+"_" + res.getTitle()+".pdf","utf-8");
                        ShareDialog.showShaerDialog(mContext, res.getTitle(), turl, res.getCreateTime() );

                    }catch (Exception e){
                        AppLog.i( e.toString(), e);
                    }
                }
            }
            @Override
            public void onFail(HttpResponse<PdfBean> response, Exception ex) {
                mProgressBar.dismiss();
                ToastUtils.show( mContext, "下载失败，请稍后重试", Toast.LENGTH_SHORT );
            }
        });
    }
    //下载成功后 需要跟新有新增的提示
    private void changeNewResported() {
        taskBean.setHasNewReport(false);
        StringBuffer sb=new StringBuffer();
        if(!TextUtils.isEmpty(taskBean.getSuiteNumber())){
            sb.append(taskBean.getSuiteNumber()+"、");
        }
        if(!TextUtils.isEmpty(taskBean.getUpdateSuiteNumber())){
            sb.append(taskBean.getUpdateSuiteNumber());
        }
        taskBean.setUpdateSuiteNumber("");
        taskBean.setSuiteNumber(sb+"");
        mDetailAdapter.setTaskBean(taskBean);
        mDetailAdapter.notifyDataSetChanged();

    }

    /**
     * 没有特权统一提示跳转
     */
    private void showNoDiglog( final String hasPrivilegeid) {
        String data ="你的定制学学习特权已失效，去购买套餐开始愉快地学习吧～";
        String btnNmae = "购买套餐";
        AlertManager.showCustomImageBtnDialog(ScorePlanActivity.this, data, btnNmae, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                BuySuiteActivity.openActivityForSuite( ScorePlanActivity.this, AccountUtils.mScorePlanBean!=null?AccountUtils.mScorePlanBean.getCustomizedPrivilegeIds():hasPrivilegeid);
            }
        }, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
    }
    /**
     * 一键下载提示框
     */
    private void showDownLoadDiglog() {
        String data ="需一键下载定制学-个人专属提分方案，在有4个定位点的答题纸上答题后再拍照上传。";
        String content ="个人专属提分方案包含周学习分析、周错题再练本、周变式训练本、错题浏览本。";
        String btnNmae = "一键下载";
        AlertManager.showCustomImageContentDialog(ScorePlanActivity.this, data,content, btnNmae, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                clickJudgeOneKeyDown(taskBean,lastposition);
            }
        }, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
    }


    /**
     * 统一检查是不是有商品使用权
     */
    private void checkPession(final String productId, final String hasPrivilegeid, final int index, final String type, final boolean isonkeydown){
        if( btnShare.get() ){
            ToastUtils.show( mContext,"正在处理中，请稍候...");
            return;
        }
        btnShare.set( true );
        //先判断有没有权限 //在判断有没有下载
        if(type.equals("dd_week_exercise") && taskBean.getTaskList().get(index).getHasPrivilege()){  //错题再练本有权限
            btnShare.set( false );
            if(TextUtils.isEmpty(taskBean.getTaskList().get(index).getExamId())){   // 如果没有下载过 弹出一键下载的窗口并下载
                showDownLoadDiglog();
            }else { // 如果下载过 走提升流程
                clickJudgeUserPriviledge( taskBean.getTaskList().get(index),false);
            }

            return;
        }else if(type.equals("exclusive_paper") && taskBean.getTaskList().get(index).getHasPrivilege()){ //变式训练本有权限
            btnShare.set( false );
            if(TextUtils.isEmpty(taskBean.getTaskList().get(index).getExamId())){   // 如果没有下载过 弹出一键下载的窗口并下载
                showDownLoadDiglog();
            }else { // 如果下载过 走提升流程
                clickJudgeUserPriviledge( taskBean.getTaskList().get(index),false);
            }
            return;

        } else if(type.equals("dd_period_review") && !isonkeydown && taskBean.getTaskList().get(index).getHasPrivilege()){     //周错题浏览本有权限
            btnShare.set( false );
            if(TextUtils.isEmpty(taskBean.getTaskList().get(index).getExamId())){   // 如果没有下载过 弹出一键下载的窗口并下载
                showDownLoadDiglog();
            } else { // 如果下载过 走提升流程
                clickJudgeUserPriviledge( taskBean.getTaskList().get(index),false);
            }
            return;
        }

        ProductModel productModel = new ProductModel();
        mProgressBar.setMessage("请稍等...");
        mProgressBar.show();
        productModel.getProductUseTimes(studentId, classid,hasPrivilegeid, productId, new RequestListener<List<ProductUseTimesBean>>() {
            @Override
            public void onSuccess(List<ProductUseTimesBean> list) {
                btnShare.set( false );
                if (list == null || list.size() == 0 || list.get(0) == null) {
                    mProgressBar.dismiss();
                    ToastUtils.show(ScorePlanActivity.this, R.string.relogin);
                    return;
                }
                int freeCount = list.get(0).getUseTimes();
                if(type.equals("dd_period_review") && isonkeydown ){   //一键下载
                    if(freeCount<0 || freeCount>=1){
                        getPdfUrl(studentId,weekdata.get(index).getDateTime());
                    }else {
                        mProgressBar.dismiss();
                        showNoDiglog(hasPrivilegeid);  //没有使用权统一提示
                    }
                }else if(type.equals("dd_week_exercise")){  //错题再练本的权限
                    mProgressBar.dismiss();
                    if(freeCount<0 || freeCount>=1){
                        if(TextUtils.isEmpty(taskBean.getTaskList().get(index).getExamId())){   // 如果没有下载过 弹出一键下载的窗口并下载
                            showDownLoadDiglog();
                        }else { // 如果下载过 走提升流程
                            clickJudgeUserPriviledge( taskBean.getTaskList().get(index),false);
                        }
                    }else {
                        showNoDiglog(hasPrivilegeid);
                    }
                }else if(type.equals("exclusive_paper")){ //变式训练本
                    mProgressBar.dismiss();
                    if(freeCount<0 || freeCount>=1){
                        if(TextUtils.isEmpty(taskBean.getTaskList().get(index).getExamId())){   // 如果没有下载过 弹出一键下载的窗口并下载
                            showDownLoadDiglog();
                        }else { // 如果下载过 走提升流程
                            clickJudgeUserPriviledge( taskBean.getTaskList().get(index),false);
                        }
                    }else {
                        showNoDiglog(hasPrivilegeid);
                    }
                } else if(type.equals("dd_period_review") && !isonkeydown ){     //周错题浏览本
                    mProgressBar.dismiss();
                    if(freeCount<0 || freeCount>=1){
                        if(TextUtils.isEmpty(taskBean.getTaskList().get(index).getExamId())){   // 如果没有下载过 弹出一键下载的窗口并下载
                            showDownLoadDiglog();
                        } else { // 如果下载过 走提升流程
                            clickJudgeUserPriviledge( taskBean.getTaskList().get(index),false);
                        }
                    }else {
                        showNoDiglog(hasPrivilegeid);
                    }
                }else{
                    mProgressBar.dismiss();
                }
            }
            @Override
            public void onFail(HttpResponse response, Exception ex) {
                btnShare.set( false );
                mProgressBar.dismiss();
                Toast.makeText(mContext, "检查使用权失败", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receive(UploadScorePlanEvent event) {
//        loadWeekDetailData(lastposition);
        loadWeekDayData(true);
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().post(new LookWorkEvent(""));
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
