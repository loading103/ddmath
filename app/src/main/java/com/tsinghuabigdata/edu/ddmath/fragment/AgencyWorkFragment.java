package com.tsinghuabigdata.edu.ddmath.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.tsinghuabigdata.edu.ddmath.MVPModel.MyStudyModel;
import com.tsinghuabigdata.edu.ddmath.MVPModel.RequestListener;
import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.adapter.SubmitedWorkAdatper;
import com.tsinghuabigdata.edu.ddmath.adapter.ToSubmitAgencyBaseAdapter;
import com.tsinghuabigdata.edu.ddmath.bean.DoudouWork;
import com.tsinghuabigdata.edu.ddmath.bean.LoginInfo;
import com.tsinghuabigdata.edu.ddmath.bean.MonthSumbitedItem;
import com.tsinghuabigdata.edu.ddmath.bean.MyTutorClassInfo;
import com.tsinghuabigdata.edu.ddmath.bean.SubmitQuestion;
import com.tsinghuabigdata.edu.ddmath.bean.UserDetailinfo;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.constant.AppConst;
import com.tsinghuabigdata.edu.ddmath.event.AssignNewWorkEvent;
import com.tsinghuabigdata.edu.ddmath.event.ChangeClassEvent;
import com.tsinghuabigdata.edu.ddmath.event.CorrectWorkEvent;
import com.tsinghuabigdata.edu.ddmath.event.JoinClassEvent;
import com.tsinghuabigdata.edu.ddmath.event.RecorrectWorkEvent;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.DDUploadActivity;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.DDWorkManager;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.WorkCommitListener;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.LocalWorkInfo;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.WorkSubmitBean;
import com.tsinghuabigdata.edu.ddmath.module.errorbook.HelpUtil;
import com.tsinghuabigdata.edu.ddmath.module.studycheat.dailog.WorkAbilityDialog;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.AlertManager;
import com.tsinghuabigdata.edu.ddmath.util.AppLog;
import com.tsinghuabigdata.edu.ddmath.util.DataUtils;
import com.tsinghuabigdata.edu.ddmath.util.DensityUtils;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.LogUtils;
import com.tsinghuabigdata.edu.ddmath.util.MyViewUtils;
import com.tsinghuabigdata.edu.ddmath.util.ToastUtils;
import com.tsinghuabigdata.edu.ddmath.view.LoadingPager;
import com.tsinghuabigdata.edu.ddmath.view.MultiGridView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.LocalWorkInfo.WORK_COMMITED;


/**
 * 机构作业
 * Created by Administrator on 2017/3/14.
 */

public class AgencyWorkFragment extends MyBaseFragment implements View.OnClickListener, WorkCommitListener, PullToRefreshBase.OnRefreshListener2 {

    private static final int TOSUBMIT_WORK = 0;
    private static final int SUBMITED_WORK = 1;
    private static final int WORK_ROW = 2;

    private PullToRefreshListView mLvDoudouWork;
    private LoadingPager          mLoadingPager;
    private View                  mViewTosubmit;
//    private TextView              mTvCheckTips;
    private ImageView             mIvHowUse;
    private CheckBox              mCbMore;
    private CheckBox              mCbOverdue;
    private MultiGridView         mGvToSubmit;
    private RelativeLayout        mRlToSubmitTitle;

    private int pageNum = 1;
    private int pageSize = 10;
    private int toSumbitPageNum  = 1;
    private int toSumbitPageSize = 30;
    private boolean bActiving = false;
    private String studentId;
//    private String classId;
    private String mLoginName;

    private List<SubmitQuestion>      mListToSumbit = new ArrayList<SubmitQuestion>();
    private List<MonthSumbitedItem>   mListSumbited = new ArrayList<MonthSumbitedItem>();
    private HashMap<Integer, Integer> mHashMap      = new HashMap<Integer, Integer>();

    private Context mContext;
    private MyStudyModel mMyStudyModel = new MyStudyModel();
    private ToSubmitAgencyBaseAdapter mToSubmitAgencyWorkAdatper;
    private SubmitedWorkAdatper       mSubmitedAgencyWorkAdatper;
    private DDWorkManager             mDDWorkManager;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogUtils.i("AgencyWorkFragment onCreateView");
        View root;
        if (GlobalData.isPad()) {
            root = inflater.inflate(R.layout.fragment_agency_work, container, false);
        } else {
            root = inflater.inflate(R.layout.fragment_agency_work_phone, container, false);
        }
        initView(root);
        setPrepared();
        initData();
        EventBus.getDefault().register(this);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mDDWorkManager != null) {
            mDDWorkManager.removeCommitListener(this);
        }
        EventBus.getDefault().unregister(this);
    }


    @Override
    public void onPause() {
        super.onPause();
        bActiving = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        bActiving = true;
    }

    private void initView(View root) {
        mContext = getActivity();
        mLvDoudouWork = (PullToRefreshListView) root.findViewById(R.id.lv_doudou_work);
        mLoadingPager = (LoadingPager) root.findViewById(R.id.loadingPager_doudou_work);
        MyViewUtils.setPTRText(mContext, mLvDoudouWork);

        mLoadingPager.setEmptyText(R.string.no_find_work);
        mLoadingPager.setTargetView(mLvDoudouWork);
        initHeaderView();
        mLoadingPager.setListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLoadingPager.showLoading();
                queryToSubmitWork();
                querySubmitedWork();
            }
        });

        mSubmitedAgencyWorkAdatper = new SubmitedWorkAdatper(mContext, mListSumbited);
        mLvDoudouWork.setAdapter(mSubmitedAgencyWorkAdatper);
        mToSubmitAgencyWorkAdatper = new ToSubmitAgencyBaseAdapter(mContext, mListToSumbit);
        mToSubmitAgencyWorkAdatper.setFirst(mCbOverdue.isChecked());
        mGvToSubmit.setAdapter(mToSubmitAgencyWorkAdatper);
    }

    private void initHeaderView() {
        if (GlobalData.isPad()) {
            mViewTosubmit = getActivity().getLayoutInflater().inflate(R.layout.view_to_submit, null);
        } else {
            mViewTosubmit = getActivity().getLayoutInflater().inflate(R.layout.view_to_submit_phone, null);
        }
        mIvHowUse = (ImageView) mViewTosubmit.findViewById(R.id.iv_how_use);
        mCbMore = (CheckBox) mViewTosubmit.findViewById(R.id.cb_to_submit_more);
        mCbOverdue = (CheckBox) mViewTosubmit.findViewById(R.id.cb_to_submit_overdue);
        mGvToSubmit = (MultiGridView) mViewTosubmit.findViewById(R.id.gv_to_submit);
        mRlToSubmitTitle = (RelativeLayout) mViewTosubmit.findViewById(R.id.rl_to_submit_title);
        mLvDoudouWork.getRefreshableView().addHeaderView(mViewTosubmit);
        mLvDoudouWork.setOnRefreshListener(this);
        mIvHowUse.setOnClickListener(this);
        mCbMore.setOnClickListener(this);
        mCbOverdue.setOnClickListener(this);
    }

    private void initData() {
        mGvToSubmit.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SubmitQuestion submitQuestion = mListToSumbit.get(position);
                if (submitQuestion != null) {
                    Intent intent = new Intent(mContext, DDUploadActivity.class);
                    intent.putExtra(DDUploadActivity.PARAM_DDWORKID, submitQuestion.getExamId());
                    intent.putExtra(DDUploadActivity.PARAM_TITLE, submitQuestion.getExamName());
                    startActivityForResult(intent, 0);
                }
            }
        });
        createLoginInfo();
        queryData();
    }

    //查询登录信息
    private void createLoginInfo() {
        LoginInfo loginInfo = AccountUtils.getLoginUser();
        UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
        if (loginInfo != null) {
            mLoginName = loginInfo.getLoginName();
        }
        if (detailinfo != null) {
            studentId = detailinfo.getStudentId();
        }
    }


    //查询班级信息
//    private void queryClassInfo() {
//
//    }

    private void clearData() {
        mListToSumbit.clear();
        mListSumbited.clear();
        if (mToSubmitAgencyWorkAdatper != null) {
            mToSubmitAgencyWorkAdatper.clearShow();
            mToSubmitAgencyWorkAdatper.notifyDataSetChanged();
        }
        if (mSubmitedAgencyWorkAdatper != null) {
            mSubmitedAgencyWorkAdatper.notifyDataSetChanged();
        }
    }

    //查询机构作业列表
    public void queryData() {
        if (isPrepared) {
            if (!TextUtils.isEmpty(mLoginName)) {
                DDWorkManager.getDDWorkManager(mContext, mLoginName);
                mDDWorkManager = DDWorkManager.getDDWorkManager();
            }
            queryToSubmitWork();
            querySubmitedWork();
            setUploadListener();
        }
    }

    //上传作业监听
    private void setUploadListener() {
        if (mDDWorkManager != null) {
            mDDWorkManager.addCommitListener(this);
        }
    }

    //查询待提交机构作业列表
    private void queryToSubmitWork() {
        MyTutorClassInfo classInfo = AccountUtils.getCurrentClassInfo();
        if (classInfo== null || TextUtils.isEmpty(classInfo.getClassId())) {
            mLoadingPager.showEmpty( "你还没有加入班级" );
            return;
        }
        int status = 0;
        mMyStudyModel.queryDoudouWork(studentId, classInfo.getClassId(), toSumbitPageNum, toSumbitPageSize, status, new RequestListener<DoudouWork>() {

            @Override
            public void onSuccess(DoudouWork vo) {
                LogUtils.i("queryToSubmitWork onSuccess");
                if (vo == null) {
                    queryResult(TOSUBMIT_WORK, -1);
                    return;
                }
                List<SubmitQuestion> exerhomes = vo.getExerhomes();
                if (exerhomes == null || exerhomes.size() == 0) {
                    queryResult(TOSUBMIT_WORK, 0);
                    mRlToSubmitTitle.setVisibility(View.GONE);
                    return;
                }
                mRlToSubmitTitle.setVisibility(View.VISIBLE);
                //LogUtils.i("queryToSubmitWork exerhomes.size()=" + exerhomes.size());
                mListToSumbit.clear();
                mListToSumbit.addAll(updateLocalState(exerhomes));
                controlCbMore();
                mToSubmitAgencyWorkAdatper.setFirst(mCbOverdue.isChecked());
                mToSubmitAgencyWorkAdatper.notifyDataSetChanged();
                queryResult(TOSUBMIT_WORK, exerhomes.size());
            }

            @Override
            public void onFail(HttpResponse<DoudouWork> response, Exception ex) {
                LogUtils.i("queryToSubmitWork onFail");
                queryResult(TOSUBMIT_WORK, -1, ex);
            }
        });
    }

    private List<SubmitQuestion> updateLocalState(List<SubmitQuestion> exerhomes) {
        for (int i = 0; i < exerhomes.size(); i++) {
            SubmitQuestion vo = exerhomes.get(i);
            String examId = vo.getExamId();
            AppLog.d("sdfdsfdsf examId = " + examId);
            LocalWorkInfo workInfo = mDDWorkManager.getWorkInfo(examId);
            if (workInfo != null) {
                vo.setStatus(workInfo.getWorkStatus());
            }
            int havePageImage = mDDWorkManager.getHavePageImage(examId);
            vo.setTakedpageCount(havePageImage);
        }
        List<SubmitQuestion> list = new ArrayList<SubmitQuestion>();
        list.addAll(exerhomes);
        return list;
    }


    //查询已提交机构作业列表
    private void querySubmitedWork() {
        MyTutorClassInfo classInfo = AccountUtils.getCurrentClassInfo();
        if (classInfo== null || TextUtils.isEmpty(classInfo.getClassId())) {
            mLoadingPager.showEmpty( "你还没有加入班级" );
            return;
        }
        int status = 1;
        pageNum = 1;
        mMyStudyModel.queryDoudouWork(studentId, classInfo.getClassId(), 1, pageNum * pageSize, status, new RequestListener<DoudouWork>() {

            @Override
            public void onSuccess(DoudouWork vo) {
                LogUtils.i("querySubmitedWork onSuccess");
                if (vo == null) {
                    queryResult(SUBMITED_WORK, -1);
                    return;
                }
                List<SubmitQuestion> exerhomes = vo.getExerhomes();
                if (exerhomes == null || exerhomes.size() == 0) {
                    queryResult(SUBMITED_WORK, 0);
                    return;
                }
                mListSumbited.clear();
                mListSumbited.addAll(DataUtils.selectDoudouWork(exerhomes));
                mSubmitedAgencyWorkAdatper.notifyDataSetChanged();
                if (exerhomes.size() < pageSize) {
                    mLvDoudouWork.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                } else {
                    mLvDoudouWork.setMode(PullToRefreshBase.Mode.BOTH);
                    pageNum++;
                }
                queryResult(SUBMITED_WORK, exerhomes.size());
            }

            @Override
            public void onFail(HttpResponse<DoudouWork> response, Exception ex) {
                LogUtils.i("querySubmitedWork onFail");
                queryResult(SUBMITED_WORK, -1, ex);
            }
        });
    }

    private void queryResult(int index, int size) {
        queryResult(index, size, null);
    }

    //查询待提交/已提交豆豆作业列表的结果
    private synchronized void queryResult(int index, int size, Exception ex) {
        mHashMap.put(index, size);
        if (mHashMap.keySet().size() == 2) {
            if (mHashMap.containsValue(-1)) {
                //有一次请求失败 展示错误页面
                mLoadingPager.showFault(ex);
            } else if (mHashMap.get(TOSUBMIT_WORK) == 0 && mHashMap.get(SUBMITED_WORK) == 0) {
                //两次都请求列表为空 展示空页面
                setEmptyData();
                mLoadingPager.showEmpty();
            } else {
                //两次都请求成功才展示列表
                mLoadingPager.showTarget();
            }
            mHashMap.clear();
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_how_use:
                HelpUtil.showHelpActivity( getContext(), "拍照上传使用说明", "Q001");
                break;
            case R.id.cb_to_submit_more:
                if (mCbMore.isChecked()) {
                    mToSubmitAgencyWorkAdatper.setData(mCbOverdue.isChecked(), true);
                    mToSubmitAgencyWorkAdatper.notifyDataSetChanged();
                } else {
                    mToSubmitAgencyWorkAdatper.setData(mCbOverdue.isChecked(), false);
                    mToSubmitAgencyWorkAdatper.notifyDataSetChanged();
                }
                break;
            case R.id.cb_to_submit_overdue:
                controlCbMore();
                if (mCbOverdue.isChecked()) {
                    mToSubmitAgencyWorkAdatper.setData(true, mCbMore.isChecked());
                    mToSubmitAgencyWorkAdatper.notifyDataSetChanged();
                } else {
                    mToSubmitAgencyWorkAdatper.setData(false, mCbMore.isChecked());
                    mToSubmitAgencyWorkAdatper.notifyDataSetChanged();
                }
                break;
            default:
                break;
        }
    }

    private void controlCbMore() {
        if (mCbOverdue.isChecked()) {
            if (mListToSumbit.size() > WORK_ROW) {
                mCbMore.setVisibility(View.VISIBLE);
            } else {
                mCbMore.setVisibility(View.INVISIBLE);
            }
        } else {
            if (moreThanOneRow()) {
                mCbMore.setVisibility(View.VISIBLE);
            } else {
                mCbMore.setVisibility(View.INVISIBLE);
            }
        }
    }

    private boolean moreThanOneRow() {
        int num = 0;
        for (int i = 0; i < mListToSumbit.size(); i++) {
            SubmitQuestion vo = mListToSumbit.get(i);
            if (System.currentTimeMillis() - vo.getEndTime() < AppConst.RECENT_15_DAY) {
                num++;
                if (num > WORK_ROW) {
                    break;
                }
            }
        }
        LogUtils.i("moreThanOneRow num=" + num);
        return num > WORK_ROW;
    }

    //更新已提交作业列表
    private void updateSubmitedWork() {
        MyTutorClassInfo classInfo = AccountUtils.getCurrentClassInfo();
        if (classInfo== null || TextUtils.isEmpty(classInfo.getClassId())) {
            mLoadingPager.showEmpty( "你还没有加入班级" );
            return;
        }
        int status = 1;
        mMyStudyModel.queryDoudouWork(studentId, classInfo.getClassId(), 1, pageNum * pageSize, status, new RequestListener<DoudouWork>() {

            @Override
            public void onSuccess(DoudouWork vo) {
                LogUtils.i("updateSubmitedWork onSuccess");
                if (vo == null) {
                    return;
                }
                List<SubmitQuestion> exerhomes = vo.getExerhomes();
                if (exerhomes == null || exerhomes.size() == 0) {
                    mRlToSubmitTitle.setVisibility(View.GONE);
                    return;
                }
                mRlToSubmitTitle.setVisibility(View.VISIBLE);
                mListSumbited.clear();
                mListSumbited.addAll(DataUtils.selectDoudouWork(exerhomes));
                mSubmitedAgencyWorkAdatper.notifyDataSetChanged();
                if (exerhomes.size() < pageSize) {
                    mLvDoudouWork.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                } else {
                    mLvDoudouWork.setMode(PullToRefreshBase.Mode.BOTH);
                    pageNum++;
                }
            }

            @Override
            public void onFail(HttpResponse<DoudouWork> response, Exception ex) {
                AlertManager.showErrorInfo(getContext(), ex);
                LogUtils.i("updateSubmitedWork onFail");
            }
        });
    }


    @Override
    public void onPullDownToRefresh(PullToRefreshBase pullToRefreshBase) {
        refreshToSubmitWork();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase pullToRefreshBase) {
        getMoreData();
    }

    private void refreshToSubmitWork() {
        MyTutorClassInfo classInfo = AccountUtils.getCurrentClassInfo();
        if (classInfo== null || TextUtils.isEmpty(classInfo.getClassId())) {
            mLoadingPager.showEmpty( "你还没有加入班级" );
            return;
        }
        //先查询待提交
        mMyStudyModel.queryDoudouWork(studentId, classInfo.getClassId(), toSumbitPageNum, toSumbitPageSize, 0, new RequestListener<DoudouWork>() {

            @Override
            public void onSuccess(DoudouWork vo) {
                LogUtils.i("refreshToSubmitWork onSuccess");
                if (vo == null) {
                    mLvDoudouWork.onRefreshComplete();
                    ToastUtils.showShort(mContext, R.string.server_error);
                    return;
                }
                refreshSubmitedWork(vo.getExerhomes());
            }

            @Override
            public void onFail(HttpResponse<DoudouWork> response, Exception ex) {
                LogUtils.i("refreshToSubmitWork onFail");
                mLvDoudouWork.onRefreshComplete();
                AlertManager.showErrorInfo(getContext(), ex);
            }
        });
    }

    private void refreshSubmitedWork(final List<SubmitQuestion> toSubmitExerhomes) {
        MyTutorClassInfo classInfo = AccountUtils.getCurrentClassInfo();
        if (classInfo== null || TextUtils.isEmpty(classInfo.getClassId())) {
            mLoadingPager.showEmpty( "你还没有加入班级" );
            return;
        }
        int status = 1;
        pageNum = 1;
        mMyStudyModel.queryDoudouWork(studentId, classInfo.getClassId(), 1, pageNum * pageSize, status, new RequestListener<DoudouWork>() {

            @Override
            public void onSuccess(DoudouWork vo) {
                LogUtils.i("refreshSubmitedWork onSuccess");
                if (vo == null) {
                    mLvDoudouWork.onRefreshComplete();
                    ToastUtils.showShort(mContext, R.string.server_error);
                    return;
                }
                refreshToSubmiView(toSubmitExerhomes);
                List<SubmitQuestion> exerhomes = vo.getExerhomes();
                if (exerhomes == null || exerhomes.size() == 0) {
                    mLvDoudouWork.onRefreshComplete();
                } else {
                    mListSumbited.clear();
                    mListSumbited.addAll(DataUtils.selectDoudouWork(exerhomes));
                    mSubmitedAgencyWorkAdatper.notifyDataSetChanged();
                    if (exerhomes.size() < pageSize) {
                        mLvDoudouWork.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                    } else {
                        mLvDoudouWork.setMode(PullToRefreshBase.Mode.BOTH);
                        pageNum++;
                    }
                    mLvDoudouWork.onRefreshComplete();
                }
                showView(toSubmitExerhomes, exerhomes);
            }

            @Override
            public void onFail(HttpResponse<DoudouWork> response, Exception ex) {
                LogUtils.i("refreshSubmitedWork onFail");
                mLvDoudouWork.onRefreshComplete();
                AlertManager.showErrorInfo(getContext(), ex);
            }
        });
    }

    private void showView(List<SubmitQuestion> toSubmitExerhomes, List<SubmitQuestion> exerhomes) {
        boolean toSubmit = toSubmitExerhomes == null || toSubmitExerhomes.size() == 0;
        boolean submited = exerhomes == null || exerhomes.size() == 0;
        if (toSubmit && submited) {
            setEmptyData();
            mLoadingPager.showEmpty();
        } else {
            mLoadingPager.showTarget();
        }
    }

    private void refreshToSubmiView(List<SubmitQuestion> exerhomes) {
        if (exerhomes == null || exerhomes.size() == 0) {
            mListToSumbit.clear();
            mRlToSubmitTitle.setVisibility(View.GONE);
        } else {
            mRlToSubmitTitle.setVisibility(View.VISIBLE);
            mCbMore.setChecked(false);
            mListToSumbit.clear();
            mListToSumbit.addAll(updateLocalState(exerhomes));
            controlCbMore();
        }
        mToSubmitAgencyWorkAdatper.setFirst(mCbOverdue.isChecked());
        mToSubmitAgencyWorkAdatper.notifyDataSetChanged();
    }

    private void getMoreData() {
        MyTutorClassInfo classInfo = AccountUtils.getCurrentClassInfo();
        if (classInfo== null || TextUtils.isEmpty(classInfo.getClassId())) {
            mLoadingPager.showEmpty( "你还没有加入班级" );
            return;
        }
        int status = 1;
        LogUtils.i("getMoreData pageNum=" + pageNum);
        mMyStudyModel.queryDoudouWork(studentId, classInfo.getClassId(), 1, pageNum * pageSize, status, new RequestListener<DoudouWork>() {

            @Override
            public void onSuccess(DoudouWork vo) {
                LogUtils.i("getMoreData onSuccess");
                if (vo == null) {
                    mLvDoudouWork.onRefreshComplete();
                    ToastUtils.showShort(mContext, R.string.server_error);
                    return;
                }
                List<SubmitQuestion> exerhomes = vo.getExerhomes();
                if (exerhomes == null || exerhomes.size() == 0) {
                    mLvDoudouWork.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                    mLvDoudouWork.onRefreshComplete();
                } else {
                    mListSumbited.clear();
                    mListSumbited.addAll(DataUtils.selectDoudouWork(exerhomes));
                    mSubmitedAgencyWorkAdatper.notifyDataSetChanged();
                    mLvDoudouWork.onRefreshComplete();
                    if (exerhomes.size() < pageNum * pageSize) {
                        mLvDoudouWork.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                    } else {
                        mLvDoudouWork.setMode(PullToRefreshBase.Mode.BOTH);
                        pageNum++;
                    }
                }
            }

            @Override
            public void onFail(HttpResponse<DoudouWork> response, Exception ex) {
                LogUtils.i("getMoreData onFail");
                mLvDoudouWork.onRefreshComplete();
                AlertManager.showErrorInfo(getContext(), ex);
            }
        });
    }

    @Override
    public void workStatus(String examId, int status) {
        updateList(examId, status);
    }

    private synchronized void updateList(String examId, int status) {
        if (mToSubmitAgencyWorkAdatper == null || TextUtils.isEmpty(examId) )
            return;

        Iterator<SubmitQuestion> iterator = mListToSumbit.iterator();
        boolean remove = false;
        while (iterator.hasNext()) {
            SubmitQuestion submitQuestion = iterator.next();
            if (examId.equals(submitQuestion.getExamId())) {
                if (status == WORK_COMMITED) {
                    //如果提交完成就从待提交作业列表移除 同事更新已提交作业列表
                    iterator.remove();
                    remove = true;
                } else {
                    submitQuestion.setStatus(status);
                    int page = mDDWorkManager.getHavePageImage(examId);
                    submitQuestion.setTakedpageCount(page);
                }
            }
        }
        if (remove) {
            updateSubmitedWork();
            mToSubmitAgencyWorkAdatper.setFirst(mCbOverdue.isChecked());
            mCbMore.setChecked(false);
            controlCbMore();
        }
        mToSubmitAgencyWorkAdatper.notifyDataSetChanged();
    }

    @Override
    public void pageStatus(String examId, int status) {


    }

    @Override
    public void onSuccess(String examId, final WorkSubmitBean workSubmitBean) {

        if (!bActiving)
            return;

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                WorkAbilityDialog.showDialog(mContext, workSubmitBean, true );
            }
        });
    }

    @Override
    public void onFail(String examId, Exception ex) {
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //LogUtils.i("requestCode= " + requestCode + "resultCode= " + resultCode);
        if (requestCode == 0 && resultCode == RESULT_OK && mToSubmitAgencyWorkAdatper != null) {
            String id = data.getStringExtra(DDUploadActivity.PARAM_DDWORKID);
            //从机构作业详情返回 更新已拍页数
            for (int i = 0; i < mListToSumbit.size(); i++) {
                SubmitQuestion vo = mListToSumbit.get(i);
                if (id.equals(vo.getExamId())) {
                    int havePageImage = mDDWorkManager.getHavePageImage(id);
                    vo.setTakedpageCount(havePageImage);
                }
            }
            mToSubmitAgencyWorkAdatper.notifyDataSetChanged();
        }

    }

    private void autoToSubmitWork() {
        MyTutorClassInfo classInfo = AccountUtils.getCurrentClassInfo();
        if (classInfo== null || TextUtils.isEmpty(classInfo.getClassId())) {
            mLoadingPager.showEmpty( "你还没有加入班级" );
            return;
        }
        //先查询待提交
        mMyStudyModel.queryDoudouWork(studentId, classInfo.getClassId(), toSumbitPageNum, toSumbitPageSize, 0, new RequestListener<DoudouWork>() {

            @Override
            public void onSuccess(DoudouWork vo) {
                LogUtils.i("autoToSubmitWork onSuccess");
                if (vo == null) {
                    ToastUtils.showShort(mContext, R.string.server_error);
                    return;
                }
                autoSubmitedWork(vo.getExerhomes());
            }

            @Override
            public void onFail(HttpResponse<DoudouWork> response, Exception ex) {
                LogUtils.i("autoToSubmitWork onFail");
                AlertManager.showErrorInfo(getContext(), ex);
            }
        });
    }

    private void autoSubmitedWork(final List<SubmitQuestion> toSubmitExerhomes) {
        MyTutorClassInfo classInfo = AccountUtils.getCurrentClassInfo();
        if (classInfo== null || TextUtils.isEmpty(classInfo.getClassId())) {
            mLoadingPager.showEmpty( "你还没有加入班级" );
            return;
        }
        int status = 1;
        pageNum = 1;
        mMyStudyModel.queryDoudouWork(studentId, classInfo.getClassId(), 1, pageNum * pageSize, status, new RequestListener<DoudouWork>() {

            @Override
            public void onSuccess(DoudouWork vo) {
                LogUtils.i("autoSubmitedWork onSuccess");
                if (vo == null) {
                    ToastUtils.showShort(mContext, R.string.server_error);
                    return;
                }
                refreshToSubmiView(toSubmitExerhomes);
                List<SubmitQuestion> exerhomes = vo.getExerhomes();
                if (exerhomes == null || exerhomes.size() == 0) {
                } else {
                    mListSumbited.clear();
                    mListSumbited.addAll(DataUtils.selectDoudouWork(exerhomes));
                    mSubmitedAgencyWorkAdatper.notifyDataSetChanged();
                    if (exerhomes.size() < pageSize) {
                        mLvDoudouWork.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                    } else {
                        mLvDoudouWork.setMode(PullToRefreshBase.Mode.BOTH);
                        pageNum++;
                    }
                }
                showView(toSubmitExerhomes, exerhomes);
            }

            @Override
            public void onFail(HttpResponse<DoudouWork> response, Exception ex) {
                LogUtils.i("autoSubmitedWork onFail");
                AlertManager.showErrorInfo(getContext(), ex);
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receive(ChangeClassEvent event) {
        AppLog.d("receive event = "+ event );
        clearData();
        queryData();
    }


    @Subscribe
    public void receive(JoinClassEvent event) {
        AppLog.d("receive event = "+ event );
        clearData();
        queryData();
    }

    @Subscribe
    public void receive(AssignNewWorkEvent event) {
        AppLog.d("receive event = "+ event );
        autoToSubmitWork();
    }

    @Subscribe
    public void receive(CorrectWorkEvent event) {
        AppLog.d("receive event = "+ event );
        autoToSubmitWork();
    }

    @Subscribe
    public void receive(RecorrectWorkEvent event) {
        AppLog.d("receive event = "+ event );
        autoToSubmitWork();
    }

    public String getUmEventName() {
        return "learntask_school";
    }

    private void setEmptyData(){
        MyTutorClassInfo classInfo = AccountUtils.getCurrentClassInfo();
        if( classInfo != null ){

            String info   = classInfo.getSchoolName() + "-" + classInfo.getClassName()+"\n";
            String data =  "老师还没有布置作业！";
            if( classInfo.getTeachers()==null || classInfo.getTeachers().size()==0 ){
                data =  "此班级还没有老师加入哦！";
            }
            Spannable span = new SpannableString( info + data );
            int start = 0, end = info.length();
            span.setSpan( new AbsoluteSizeSpan(DensityUtils.sp2px( getContext(), GlobalData.isPad()?18:12 )), start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

            start   = end;
            end     = start + data.length();
            span.setSpan( new AbsoluteSizeSpan(DensityUtils.sp2px( getContext(), GlobalData.isPad()?22:16 )), start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            //整体粗体
            //span.setSpan( new StyleSpan(Typeface.BOLD), 0, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

            mLoadingPager.setEmptyText( span );
        }
    }
}
