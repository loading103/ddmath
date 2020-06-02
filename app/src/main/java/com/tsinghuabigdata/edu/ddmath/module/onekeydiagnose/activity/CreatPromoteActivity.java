package com.tsinghuabigdata.edu.ddmath.module.onekeydiagnose.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tsinghuabigdata.edu.ddmath.MVPModel.KnowledgeDiagnoseModel;
import com.tsinghuabigdata.edu.ddmath.MVPModel.RequestListener;
import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.activity.MainActivity;
import com.tsinghuabigdata.edu.ddmath.activity.RoboActivity;
import com.tsinghuabigdata.edu.ddmath.bean.UserDetailinfo;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.constant.AppConst;
import com.tsinghuabigdata.edu.ddmath.event.JumpEvent;
import com.tsinghuabigdata.edu.ddmath.fragment.ErrorBookFragment;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.view.WorkToolbar;
import com.tsinghuabigdata.edu.ddmath.module.onekeydiagnose.bean.QuestionCountBean;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.AlertManager;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.LogUtils;
import com.tsinghuabigdata.edu.ddmath.util.StringUtil;
import com.tsinghuabigdata.edu.ddmath.util.ToastUtils;
import com.tsinghuabigdata.edu.ddmath.view.LoadingPager;
import com.tsinghuabigdata.edu.ddmath.view.MyProgressDialog;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;


/**
 * 创建提升练习页面
 * Created by Administrator on 2018/8/6.
 */

public class CreatPromoteActivity extends RoboActivity implements View.OnClickListener {

    public static final String PARAM_KNOWLEDGES = "Knowledges";
    public static final String PARAM_STARTTIME  = "StartTime";
    public static final String PARAM_ENDTIME    = "EndTime";
    public static final String PARAM_FROM    = "from";

    public static final int MAX_COUNT = 20;     //变式训练本
    public static final int MAX_COUNT_BOOK = 50;//错题浏览本 错题再练本

    //private WorkToolbar    mWorktoolbar;
    //private RelativeLayout mRlContent;
    private TextView       mTvKnowledgeCount;
    //private LinearLayout   mLlErrorBook;
    private CheckBox       mCbErrorBook;
    private TextView       mTvErrorBook;
    //private LinearLayout   mLlErrorRefine;
    private CheckBox       mCbErrorRefine;
    private TextView       mTvErrorRefine;
    //private LinearLayout   mLlVariantTraining;
    private CheckBox       mCbVariantTraining;
    private TextView       mTvVariantTraining;
    private Button         mBtnSure;
    private LoadingPager   mLoadingPager;

    private MyProgressDialog progressDialog;
    private Context          mContext;

    private String mStudentId = "";
    private ArrayList<String> mList;
    private long              mCustomStartTime;
    private long              mCustomEndTime;
    private String            fromType;

    private int refineBookCount;        //变式训练本数量
    private int browerBookCount;        //错题浏览本数量

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        transparent = true;
        super.onCreate(savedInstanceState);

        //设置全屏显示
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (GlobalData.isPad()) {
            setContentView(R.layout.activity_create_promote);
        } else {
            setContentView(R.layout.activity_create_promote_phone);
        }
        initViews();
        initData();
    }

    private void initViews() {
        mContext = this;
        WorkToolbar mWorktoolbar =  findViewById(R.id.worktoolbar);
        RelativeLayout mRlContent =  findViewById(R.id.rl_content);
        mTvKnowledgeCount =  findViewById(R.id.tv_knowledge_count);
        LinearLayout mLlErrorBook =  findViewById(R.id.ll_error_book);
        mCbErrorBook =  findViewById(R.id.cb_error_book);
        mTvErrorBook =  findViewById(R.id.tv_error_book);
        LinearLayout mLlErrorRefine =  findViewById(R.id.ll_error_refine);
        mCbErrorRefine =  findViewById(R.id.cb_error_refine);
        mTvErrorRefine =  findViewById(R.id.tv_error_refine);
        LinearLayout mLlVariantTraining =  findViewById(R.id.ll_variant_training);
        mCbVariantTraining =  findViewById(R.id.cb_variant_training);
        mTvVariantTraining =  findViewById(R.id.tv_variant_training);
        mBtnSure =  findViewById(R.id.btn_sure);
        mLoadingPager =  findViewById(R.id.loadingPager);

        String title = "创建提升练习";
        mWorktoolbar.setTitle(title);
        mWorktoolbar.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        }, null);
        mLlErrorBook.setOnClickListener(this);
        mLlErrorRefine.setOnClickListener(this);
        mLlVariantTraining.setOnClickListener(this);
        mBtnSure.setOnClickListener(this);
        mLoadingPager.setTargetView(mRlContent);
        mLoadingPager.setListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getQuestionCount();
            }
        });
    }

    private void initData() {
        Intent intent = getIntent();
        mList = intent.getStringArrayListExtra(PARAM_KNOWLEDGES);
        mCustomStartTime = intent.getLongExtra(PARAM_STARTTIME, 0);
        mCustomEndTime = intent.getLongExtra(PARAM_ENDTIME, 0);
        fromType = intent.getStringExtra( PARAM_FROM );

        mTvKnowledgeCount.setText( String.valueOf(mList.size()));
        UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
        if (detailinfo!= null) {
            mStudentId = detailinfo.getStudentId();
        }
        getQuestionCount();
        setAllSelect();
        //analogData();
    }

    private void setAllSelect() {
        if(TextUtils.isEmpty(fromType)){
            mCbErrorBook.setChecked(true);
            mTvErrorBook.setActivated(true);
            mCbErrorRefine.setChecked(true);
            mTvErrorRefine.setActivated(true);
            mCbVariantTraining.setChecked(true);
            mTvVariantTraining.setActivated(true);
        }else if( AppConst.FROM_VARIANT.equals(fromType) ){
            mCbVariantTraining.setChecked(true);
            mTvVariantTraining.setActivated(true);
        }else if( AppConst.FROM_REFINE.equals(fromType) ){
            mCbErrorRefine.setChecked(true);
            mTvErrorRefine.setActivated(true);
        }else if( AppConst.FROM_BROWER.equals(fromType) ){
            mCbErrorBook.setChecked(true);
            mTvErrorBook.setActivated(true);
        }
    }

    private void getQuestionCount() {
        HashMap<String, String> params = new LinkedHashMap<>();
        params.put("studentId", mStudentId);
        params.put("startDate", mCustomStartTime + "");
        params.put("endDate", mCustomEndTime + "");
        //        String konwledgeIds = new Gson().toJson(mList);
        String konwledgeIds = StringUtil.transListToString(mList);
        params.put("knowledgeIds", konwledgeIds);

        mLoadingPager.showLoading();
        new KnowledgeDiagnoseModel().getQuestionCount(params, new RequestListener<QuestionCountBean>() {

            @Override
            public void onSuccess(QuestionCountBean res) {
                //LogUtils.i("getQuestionCount onSuccess");
                if (res == null) {
                    mLoadingPager.showServerFault();
                    return;
                }
                changeButtonStatus(res.getUseTimes());
                int count2;
                if (res.getQuestionCount() <= 3) {
                    count2 = res.getQuestionCount();
                } else {
                    int count = Math.max(Math.round(res.getQuestionCount() * 0.2f), 3);
                    count2 = Math.min(count, 20);
                }

                int questionCount = res.getQuestionCount();
                refineBookCount = questionCount%MAX_COUNT!=0?(questionCount/MAX_COUNT+1):(questionCount/MAX_COUNT);
                browerBookCount = questionCount%MAX_COUNT_BOOK!=0?(questionCount/MAX_COUNT_BOOK+1):(questionCount/MAX_COUNT_BOOK);
                String bookCount = String.format(Locale.getDefault(), "创建错题浏览本（全部错题约%d题，生成%d本）", questionCount, browerBookCount );
                String refineCount = String.format(Locale.getDefault(), "创建错题再练本（全部错题约%d题，生成%d本）", questionCount, refineBookCount );
                String trainingCount = String.format(Locale.getDefault(), "创建变式训练本（约%d题，生成%d本）", count2, count2%MAX_COUNT!=0?(count2/MAX_COUNT+1):(count2/MAX_COUNT) );

                mTvErrorBook.setText(bookCount);
                mTvErrorRefine.setText(refineCount);
                mTvVariantTraining.setText(trainingCount);
                mLoadingPager.showTarget();
            }

            @Override
            public void onFail(HttpResponse<QuestionCountBean> response, Exception ex) {
                LogUtils.i("getQuestionCount onFail");
                mLoadingPager.showFault(ex);
            }
        });
    }


    private void changeButtonStatus(int useTimes) {
        if (useTimes >= 5) {
            mBtnSure.setEnabled(false);
            mBtnSure.setText("今日5次机会已用完");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_error_book:
                boolean bookChecked = !mCbErrorBook.isChecked();
                mCbErrorBook.setChecked(bookChecked);
                mTvErrorBook.setActivated(bookChecked);
                break;
            case R.id.ll_error_refine:
                boolean refineChecked = !mCbErrorRefine.isChecked();
                mCbErrorRefine.setChecked(refineChecked);
                mTvErrorRefine.setActivated(refineChecked);
                break;
            case R.id.ll_variant_training:
                boolean trainingChecked = !mCbVariantTraining.isChecked();
                mCbVariantTraining.setChecked(trainingChecked);
                mTvVariantTraining.setActivated(trainingChecked);
                break;
            case R.id.btn_sure:
                creatPromote();
                break;
            default:
                break;
        }
    }

    private void creatPromote() {
        List<String> list = new ArrayList<>();
        if (mCbErrorBook.isChecked()) {
            list.add("1");

            if(browerBookCount>20){
                ToastUtils.showToastCenter( mContext, "抱歉，你选择的创建错题浏览本的数量超过20本哦，请重新选择时间；或者你也可去使用系统为你定期智能生成的错题浏览本，不需要自己再创建哦～");
                return;
            }
        }
        if (mCbErrorRefine.isChecked()) {
            list.add("2");
            if(refineBookCount>20){
                ToastUtils.showToastCenter( mContext, "抱歉，你选择的创建错题再练本的数量超过20本哦，请重新选择时间；或者你也可去使用系统为你定期智能生成的错题再练本，不需要自己再创建哦～");
                return;
            }
        }
        if (mCbVariantTraining.isChecked()) {
            list.add("3");
        }
        if (list.size() == 0) {
            ToastUtils.showLong(mContext, "你还没有选择提升练习的类型哦");
            return;
        }
        initProgressDialog();
        produceQuestionSet(list);
    }

    private void produceQuestionSet(List<String> typeList) {
        progressDialog.show();
        HashMap<String, String> params = new LinkedHashMap<>();
        params.put("studentId", mStudentId);
        params.put("startDate", mCustomStartTime + "");
        params.put("endDate", mCustomEndTime + "");
        //        String konwledgeIds = new Gson().toJson(mList);
        String types = StringUtil.transListToString(typeList);
        params.put("types", types);
        String konwledgeIds = StringUtil.transListToString(mList);
        params.put("knowledgeIds", konwledgeIds);
        new KnowledgeDiagnoseModel().produceQuestionSet(params, new RequestListener<String>() {

            @Override
            public void onSuccess(String res) {
                LogUtils.i("getQuestionCount onSuccess");
                progressDialog.dismiss();
                ToastUtils.showLong(mContext, "练习正在生成中，请耐心等待；生成结果会消息通知到你！");

                if(TextUtils.isEmpty(fromType)){
                    finish();
                }else if( AppConst.FROM_VARIANT.equals(fromType) ){
                    finishAll();
                    EventBus.getDefault().post(new JumpEvent(MainActivity.FRAGMENT_QUESTION_BOOK, ErrorBookFragment.MODEL_VARTRAIN));
                }else if( AppConst.FROM_REFINE.equals(fromType) ){
                    finishAll();
                    EventBus.getDefault().post(new JumpEvent(MainActivity.FRAGMENT_QUESTION_BOOK, ErrorBookFragment.MODEL_WEEKTRAIN));
                }else if( AppConst.FROM_BROWER.equals(fromType) ){
                    finishAll();
                    EventBus.getDefault().post(new JumpEvent(MainActivity.FRAGMENT_QUESTION_BOOK, ErrorBookFragment.MODEL_ERRBOOK));
                }
            }

            @Override
            public void onFail(HttpResponse<String> response, Exception ex) {
                LogUtils.i("getQuestionCount onFail");
                progressDialog.dismiss();
                AlertManager.showErrorInfo(mContext, ex);
            }
        });
    }

    private void initProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new MyProgressDialog(mContext);
            progressDialog.setMessage("请求服务器中...");
        }
    }


}
