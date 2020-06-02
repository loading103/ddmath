package com.tsinghuabigdata.edu.ddmath.module.errorbook;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.tsinghuabigdata.edu.ddmath.MVPModel.ErrorBookModel;
import com.tsinghuabigdata.edu.ddmath.MVPModel.RequestListener;
import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.activity.RoboActivity;
import com.tsinghuabigdata.edu.ddmath.bean.LoginInfo;
import com.tsinghuabigdata.edu.ddmath.bean.UserDetailinfo;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.constant.AppConst;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.view.WorkToolbar;
import com.tsinghuabigdata.edu.ddmath.module.errorbook.adapter.ErrorQuestionBrowerAdapter;
import com.tsinghuabigdata.edu.ddmath.module.errorbook.bean.DayCleanDetailBean;
import com.tsinghuabigdata.edu.ddmath.module.errorbook.bean.QuestionVo;
import com.tsinghuabigdata.edu.ddmath.module.errorbook.dialog.UploadRecordDialog;
import com.tsinghuabigdata.edu.ddmath.module.message.MessageUtils;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.AlertManager;
import com.tsinghuabigdata.edu.ddmath.util.DataUtils;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.MyViewUtils;
import com.tsinghuabigdata.edu.ddmath.util.ToastUtils;
import com.tsinghuabigdata.edu.ddmath.view.LoadingPager;
import com.umeng.analytics.MobclickAgent;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;

/**
 * 日日清详情界面（错题订正详情）
 */
public class EBookDayCleanActivity extends RoboActivity implements  /*AdapterView.OnItemClickListener, */PullToRefreshBase.OnRefreshListener<ListView>, CompoundButton.OnCheckedChangeListener {

    public static final String PARAM_DATE    = "date";
    public static final String PARAM_TITLE   = "title";
    public static final String PARAM_INDEX   = "index";
    public static final String PARAM_TOTAL   = "total";     //使用人次

    public static final String PARAM_SCORED   = "isscored";     //是不是定制学跳转过来的
    public static final String PARAM_OVERDO   = "isoverdo";     //有没有逾期
    public static final String PARAM_UNCORRNUMBER ="unvertnumber" ;  //还有多少题没有订正

    //直接打开当天的日日清界面
//    public static void openTodayDayCleanActivity(Context context){
//        Intent intent = new Intent( context, EBookDayCleanActivity.class );
//        Date date = new Date();
//        String today  = DateUtils.format( date );
//        String title  = DateUtils.format( date ) + DateUtils.getWeekOfDate(date)+"错题订正";
//        intent.putExtra( PARAM_DATE, today );
//        intent.putExtra( PARAM_TITLE, title );
//        context.startActivity( intent );
//    }
    @ViewInject(R.id.custom_toolbar)
    private WorkToolbar workToolbar;

    //@ViewInject(R.id.ebook_dayclean_mainlayout)
    //private LinearLayout mainLayout;

    @ViewInject(R.id.loadingPager)
    private LoadingPager mLoadingPager;

    @ViewInject(R.id.ebook_dayclean_allcount)
    private TextView allTextView;

    @ViewInject(R.id.ebook_dayclean_showcorrected)
    private CheckBox showCorrectCheckBox;

    @ViewInject( R.id.ebook_dayclean_questionlist )
    private PullToRefreshListView mListView;

    private Context mContext;

    //
    private ErrorQuestionBrowerAdapter mQuestionAdapter;

    private ErrorBookModel mEBookModel = new ErrorBookModel();

    //
    private String qdate;
    private String title;
    private int listNumber;
    private int correctTotal;

    private boolean bQuit = false;

    private int currPageNum = 1;
    private int totalPage   = 1;
    public  boolean isScored=false;
    public  int     isoverdo=1; //0未超期
    public int unRevertNumber;
    //    @Override
    protected int getContentViewId() {
        return GlobalData.isPad()?R.layout.activity_ebook_dayclean:R.layout.activity_ebook_dayclean_phone;
    }

    @Override
    protected void onCreate( Bundle savedInstanceState){
        transparent = true;
        super.onCreate(savedInstanceState);

        //设置全屏显示
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView( getContentViewId() );

        x.view().inject(this);

        mContext = this;

        if( !parseIntent() ){
            ToastUtils.show( mContext, "参数错误", Toast.LENGTH_SHORT );
            finish();
            return;
        }

        if( !checkUsrInfo() ){
            finish();
            return;
        }

        initView();
        loadData();
        MobclickAgent.onEvent( this, "revise_dayclean" );
    }

    public void onLeftClick(){
        finish();
    }

    public void onRightClick(){
        //跳转到日日清报告
        //EventBus.getDefault().post(new JumpEvent(MainActivity.FRAGMENT_STUDY_CONDITION, MyReportFragment.DAILY_CLEAR_I));
        //finish();

        UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
        if( detailinfo != null ){
            String baseUrl = MessageUtils.getCorrectReportUrl( detailinfo.getStudentId() );
            String mUrl = DataUtils.getUrl(mContext, baseUrl);

            MessageUtils.gotoNotitleWeburl( mContext, mUrl, "错题订正报告", "", false, "","");
        }
    }

//    @Override
//    public void onBackPressed(){
//        quit();
//    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        bQuit = true;
    }

    @Override
    public void onRefresh(PullToRefreshBase pullToRefreshBase) {
        if (pullToRefreshBase.isHeaderShown()) {
            refreshData();
        } else {
            getMoreData();
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        refreshData();
    }

    //----------------------------------------------------------------------
    private boolean checkUsrInfo(){
        LoginInfo loginInfo = AccountUtils.getLoginUser();
        UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
        if( loginInfo == null || detailinfo == null ){
            ToastUtils.showShort( mContext, "请登录" );
            return false;
        }
        return true;
    }
    private boolean parseIntent(){
        Intent intent = getIntent();
        qdate    = intent.getStringExtra( PARAM_DATE );
        title    = intent.getStringExtra( PARAM_TITLE );
        listNumber=intent.getIntExtra( PARAM_INDEX, 1);
        correctTotal=intent.getIntExtra( PARAM_TOTAL, 0);
        isoverdo=intent.getIntExtra( PARAM_OVERDO, 1);
        isScored=intent.getBooleanExtra(PARAM_SCORED,false);
        unRevertNumber=intent.getIntExtra( PARAM_UNCORRNUMBER, 0);
        return !( TextUtils.isEmpty( qdate ) || TextUtils.isEmpty(title) );
    }

    private void initView(){

        //标题
        workToolbar.setTitle( title );
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
        mLoadingPager.setTargetView( mListView );

        showCorrectCheckBox.setOnCheckedChangeListener( this );

        mQuestionAdapter = new ErrorQuestionBrowerAdapter( mContext,correctTotal ,title ,isScored,isoverdo,unRevertNumber);
        mQuestionAdapter.setShowCorrectBtn( true );
        //mQuestionAdapter.setShowMasterIcon( false );

        mLoadingPager.setListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadData();
            }
        });

        mListView.setAdapter( mQuestionAdapter );
        //mListView.setOnItemClickListener(this);
        mQuestionAdapter.setQuestionBookItem(new ErrorQuestionBrowerAdapter.QuestionBookItem() {
            @Override
            public void clickUpload(int position, int left, int top, int bottom) {
                QuestionVo questionVo = mQuestionAdapter.getItem(position);
                UploadRecordDialog uploadRecordDialog = new UploadRecordDialog(mContext, questionVo, left, top, bottom);
                uploadRecordDialog.show();
            }

            @Override
            public void revise(int position) {
                //
            }
        });

        //
        //View footerview = LayoutInflater.from(mContext).inflate(R.layout.view_footer_view, null);
        //mListView.getRefreshableView().addFooterView(footerview);

        MyViewUtils.setPTRText(mContext, mListView);
        mListView.setOnRefreshListener(this);
        //mListView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);        //仅仅上拉加载更多
    }

    //加载更多
    private void getMoreData() {
        currPageNum++;
        loadData();
    }

    //下拉刷新加载更新
    private void refreshData() {
        currPageNum = 1;
        loadData();
    }

    //加载的是本地保存的数据
    private void loadData(){

        //默认值
        allTextView.setText( "共--题" );

        final LoginInfo loginInfo = AccountUtils.getLoginUser();
        final UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
        if( loginInfo == null || detailinfo == null ){
            ToastUtils.show( mContext, "请登录", Toast.LENGTH_SHORT );
            return;
        }

        //String accessToken = loginInfo.getAccessToken();
        String studentId    = detailinfo.getStudentId();
        boolean master      = !showCorrectCheckBox.isChecked();

        if( currPageNum == 1 )
            mLoadingPager.showLoading();
        mEBookModel.queryDayCleanDetail( studentId, qdate, master, currPageNum, AppConst.MAX_PAGE_NUM, listNumber, new RequestListener() {
            @Override
            public void onSuccess(Object res) {
                if( bQuit ) return;
                mListView.onRefreshComplete();

                DayCleanDetailBean  detail = (DayCleanDetailBean)res;

                ArrayList<QuestionVo> list = (detail != null)?detail.getQuestions():null;

                if( detail == null || list==null || list.size() == 0 ){

                    //ToastUtils.show( mContext, "没有查找到作业数据!", Toast.LENGTH_SHORT );
                    //finish();
                    mLoadingPager.setEmptyText("没有查找到作业数据");
                    mLoadingPager.showEmpty();
                    return;
                }

                //workToolbar.setRightTitleAndLeftDrawable( "错题订正报告", R.drawable.ic_report2 );

                mLoadingPager.showTarget();
                //mainLayout.setVisibility( View.VISIBLE );

                //过滤已订正但是还没有批阅完的题目
//                if( master ){
//                    for( int i=list.size()-1; i>=0; i-- ){
//                        QuestionVo questionVo = list.get(i);
//                        ReviseResultInfo reviseResultInfo = questionVo.getReviseResultResponse();
//                        if( reviseResultInfo!=null && !TextUtils.isEmpty(reviseResultInfo.getAnswerUrl()) ){
//                            list.remove(i);
//                        }
//                    }
//                }

                //如果是下拉加载，清除原列表数据
                if( currPageNum == 1 ){
                    mQuestionAdapter.clear();
                }
                mQuestionAdapter.addAll( list );
                mQuestionAdapter.notifyDataSetChanged();

                String data = String.format( getResources().getString(R.string.ebook_allcount), detail.getTotalCount() );
                allTextView.setText( data );

                //currPageNum = detail.getPageNum();
                totalPage   = detail.getTotalPage();

                if( currPageNum == totalPage ){
                    //隐藏加载更多
                    mListView.setMode(PullToRefreshBase.Mode.DISABLED);
                }else if( currPageNum < totalPage ){
                    //显示加载更多
                    mListView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
                }
            }

            @Override
            public void onFail(HttpResponse response, Exception ex) {
                mListView.onRefreshComplete();
                if( bQuit ) return;
                if( currPageNum == 1 ){
                    mLoadingPager.showFault(ex);
                }else{
                    AlertManager.showErrorInfo( mContext, ex );
                }

            }
        });
    }

}

