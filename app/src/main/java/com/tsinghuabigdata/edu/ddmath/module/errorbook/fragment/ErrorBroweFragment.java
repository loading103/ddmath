package com.tsinghuabigdata.edu.ddmath.module.errorbook.fragment;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.tsinghuabigdata.edu.ddmath.MVPModel.QuestionBookModel;
import com.tsinghuabigdata.edu.ddmath.MVPModel.RequestListener;
import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.bean.BookBean;
import com.tsinghuabigdata.edu.ddmath.bean.LoginInfo;
import com.tsinghuabigdata.edu.ddmath.bean.StudentQuestionVo;
import com.tsinghuabigdata.edu.ddmath.bean.UserDetailinfo;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.dialog.CancelBookDialog;
import com.tsinghuabigdata.edu.ddmath.module.errorbook.dialog.ProduceBookDialog;
import com.tsinghuabigdata.edu.ddmath.module.errorbook.dialog.UploadRecordDialog;
import com.tsinghuabigdata.edu.ddmath.event.RefreshBrowerEvent;
import com.tsinghuabigdata.edu.ddmath.fragment.MyBaseFragment;
import com.tsinghuabigdata.edu.ddmath.module.errorbook.adapter.ErrorQuestionBrowerAdapter;
import com.tsinghuabigdata.edu.ddmath.module.errorbook.bean.QuestionVo;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.AlertManager;
import com.tsinghuabigdata.edu.ddmath.util.DateUtils;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.KeyboardUtil;
import com.tsinghuabigdata.edu.ddmath.util.LogUtils;
import com.tsinghuabigdata.edu.ddmath.util.MyViewUtils;
import com.tsinghuabigdata.edu.ddmath.util.ToastUtils;
import com.tsinghuabigdata.edu.ddmath.view.MyProgessLine;
import com.tsinghuabigdata.edu.ddmath.view.QuestionPager;
import com.tsinghuabigdata.edu.ddmath.view.SortView;
import com.tsinghuabigdata.edu.ddmath.view.TimeSpinnerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


/**
 * 错题本
 * Created by Administrator on 2016/12/14.
 */
@Deprecated
public class ErrorBroweFragment extends MyBaseFragment implements View.OnClickListener, PullToRefreshBase.OnRefreshListener2 {

    private static final int PRODUCE_ING     = 1;
    private static final int PRODUCE_SUCCESS = 2;
    private static final int PRODUCE_FAIL    = 3;

    private LinearLayout          mLlContent;
    private ImageView             mIvSearch;
    private EditText              mEdtSearch;
    private ImageView             mIvDelete;
    private CheckBox              mIvShowCorrect;
    private LinearLayout          mLlPhotographCollect;
    private TimeSpinnerView       mTimeSpinnerView;
    private TimeSpinnerView       mKindSpinnerView;
    private LinearLayout          mLlQuestionCount;
    private TextView              mTvQuestionCount;
    private TextView              mTvShare;
    private SortView              mSortView;
    private PullToRefreshListView mLvQuestionBook;
    private QuestionPager         mLoadingPager;

    private String studentId;
    private String mSearchedkey = "";
    private String mBookId;
    private long   mCustomStartTime;
    private long   mCustomEndTime;
    private int    timeIndex;
    private int    mTotalCount;
    private int      kindIndex = 2;
    private int      sortIndex = 1;
    private int      pageNum   = 1;
    private int      pageSize  = 10;
    private String[] sorts     = {"uploadTime,asc;wrongCount,desc", "uploadTime,desc;wrongCount,desc", "wrongCount,asc;uploadTime,desc", "wrongCount,desc;uploadTime,desc"};
    private String[] types     = {"ddExerhome", "ddWeekExam",/* "ddExam",*/ ""};

    private Context           mContext;
    private ProduceBookDialog mProduceBookDialog;
    private CancelBookDialog  mCancelBookDialog;
    private QuestionBookModel mModel = new QuestionBookModel();
    //private List<QuestionVo>  mList  = new ArrayList<QuestionVo>();
    private ErrorQuestionBrowerAdapter mAdapter;

    //private MyProgressDialog mMyProgressDialog;
    private AutoQueryTask mAutoQueryTask;


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };
    private MyProgessLine mMyProgessLine;
    private boolean       hasCancel;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root;
        if (GlobalData.isPad()) {
            root = inflater.inflate(R.layout.fragment_question_book, container, false);
        } else {
            root = inflater.inflate(R.layout.fragment_question_book_phone, container, false);
        }
        initView(root);
        setPrepared();
        return root;
    }

    public String getUmEventName() {
        return "errbook_brower";
    }

    @Override
    protected void lazyLoad() {
        initData();
    }

    private void initView(View root) {
        mContext = getActivity();
        mLlContent = (LinearLayout) root.findViewById(R.id.ll_content);
        mIvSearch = (ImageView) root.findViewById(R.id.iv_search);
        mEdtSearch = (EditText) root.findViewById(R.id.edt_search);
        mIvDelete = (ImageView) root.findViewById(R.id.iv_delete);
        mIvShowCorrect = (CheckBox) root.findViewById(R.id.iv_show_correct);
        mIvShowCorrect.setChecked( true );
        mLlPhotographCollect = (LinearLayout) root.findViewById(R.id.ll_photograph_collect);
        mTimeSpinnerView = (TimeSpinnerView) root.findViewById(R.id.timeSpinnerView);
        mKindSpinnerView = (TimeSpinnerView) root.findViewById(R.id.kindSpinnerView);
        mLlQuestionCount = (LinearLayout) root.findViewById(R.id.ll_question_count);
        mTvQuestionCount = (TextView) root.findViewById(R.id.tv_question_count);
        mTvShare = (TextView) root.findViewById(R.id.tv_share);
        mSortView = (SortView) root.findViewById(R.id.sortView);
        mLvQuestionBook = (PullToRefreshListView) root.findViewById(R.id.lv_question_book);
        mLoadingPager = (QuestionPager) root.findViewById(R.id.ebook_errbrower_loading_pager);

        mLoadingPager.stopAnim();
        mLoadingPager.setTargetView(mLvQuestionBook);
        mLoadingPager.setListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLoadingPager.showLoading();
                queryQuestions();
            }
        });
        MyViewUtils.setPTRText(mContext, mLvQuestionBook);
        mLvQuestionBook.setOnRefreshListener(this);

        mIvShowCorrect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //mAdapter.showAll(isChecked);
                mLoadingPager.showLoading();
                mSearchedkey = mEdtSearch.getText().toString().trim();
                queryQuestions();
            }
        });
        mIvSearch.setOnClickListener(this);
        mIvDelete.setOnClickListener(this);
        mLlPhotographCollect.setOnClickListener(this);
        mTvShare.setEnabled(false);
        mTvShare.setOnClickListener(this);
        mEdtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                //LogUtils.i("actionId=" + actionId);
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_SEARCH) {
                    KeyboardUtil.hidInput((Activity) mContext);
                    mSearchedkey = mEdtSearch.getText().toString().trim();
                    queryQuestions();
                }
                return false;
            }
        });
        mEdtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String str = mEdtSearch.getText().toString();
                if (TextUtils.isEmpty(str)) {
                    mIvDelete.setVisibility(View.INVISIBLE);
                } else {
                    mIvDelete.setVisibility(View.VISIBLE);
                }
            }
        });
        final List<String> timelist = Arrays.asList(getResources().getStringArray(R.array.time_filter_array));
        mTimeSpinnerView.setText(timelist.get(timeIndex));
        mTimeSpinnerView.setData(timelist, timeIndex);
        mTimeSpinnerView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LogUtils.i("position=" + position + "timeIndex=" + timeIndex);
                if (position == timelist.size() - 1) {
                    mTimeSpinnerView.showCustom(true);
                    mTimeSpinnerView.autoShowCustom();
                    mTimeSpinnerView.setTempPosition(position);
                } else if (position != mTimeSpinnerView.getSelectedPosition()) {
                    mTimeSpinnerView.showCustom(false);
                    mTimeSpinnerView.setSelectedPosition(position);
                    timeIndex = position;
                    String time = timelist.get(position);
                    mTimeSpinnerView.setText(time);
                    mLoadingPager.showLoading();
                    queryQuestions();
                    mTimeSpinnerView.dismiss();
                } else {
                    mTimeSpinnerView.dismiss();
                }

            }
        });
        mTimeSpinnerView.setCustomSelect(new TimeSpinnerView.CustomSelect() {
            @Override
            public void selectTime(long startTime, long endTime) {
                mCustomStartTime = startTime;
                mCustomEndTime = endTime;
                int lastPosition = timelist.size() - 1;
                if (timeIndex != lastPosition) {
                    timeIndex = lastPosition;
                    mTimeSpinnerView.setSelectedPosition(lastPosition);
                    String time = timelist.get(lastPosition);
                    mTimeSpinnerView.setText(time);
                }
                LogUtils.i("timeIndex=" + timeIndex);
                mLoadingPager.showLoading();
                queryQuestions();
            }
        });
        final List<String> kindlist = Arrays.asList(getResources().getStringArray(R.array.kind_filter_array));
        mKindSpinnerView.setShowDate( false );
        mKindSpinnerView.setText(kindlist.get(kindIndex));
        mKindSpinnerView.setData(kindlist, kindIndex);
        mKindSpinnerView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LogUtils.i("position=" + position + "kindIndex=" + kindIndex);
                if (position != mKindSpinnerView.getSelectedPosition()) {
                    mKindSpinnerView.setSelectedPosition(position);
                    kindIndex = position;
                    String time = kindlist.get(position);
                    mKindSpinnerView.setText(time);
                    mLoadingPager.showLoading();
                    queryQuestions();
                }
                mKindSpinnerView.dismiss();
            }
        });
        if (mSortView != null) {
            mSortView.setIndex(sortIndex);
            mSortView.setSortSelect(new SortView.SortSelect() {
                @Override
                public void select(int postion) {
                    sortIndex = postion;
                    mLoadingPager.showLoading();
                    queryQuestions();
                }
            });
        }
    }


    private void initData() {
        mAdapter = new ErrorQuestionBrowerAdapter(mContext,-1);
        mAdapter.setQuestionBookItem(new ErrorQuestionBrowerAdapter.QuestionBookItem() {
            @Override
            public void clickUpload(int position, int left, int top, int bottom) {
                showUploadDialog(position, left, top, bottom);
            }

            @Override
            public void revise(int position) {
                mLvQuestionBook.getRefreshableView().setSelection(position);
            }
        });
        mLvQuestionBook.setAdapter(mAdapter);
        mLoadingPager.startAnim();
        createLoginInfo();
        EventBus.getDefault().register(this);
    }


    private void createLoginInfo() {
        LoginInfo loginInfo = AccountUtils.getLoginUser();
        UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
        if (loginInfo != null && detailinfo != null) {
            studentId = detailinfo.getStudentId();
            queryQuestions();
        }
    }

    public void queryQuestions() {
        String sort = sorts[sortIndex];
        long startTime = getStartTime();
        long endTime = getEndTime();
        boolean showCorrect = mIvShowCorrect.isChecked();
        String questionType = types[kindIndex];
        pageNum = 1;
        mModel.queryQuestions(studentId, mSearchedkey, sort, startTime, endTime, showCorrect, questionType, pageNum, pageSize, new RequestListener<StudentQuestionVo>() {

            @Override
            public void onSuccess(StudentQuestionVo vo) {
                if( !isAdded() || isDetached() ) return;
                if (vo == null || vo.getItems() == null || vo.getItems().size() == 0) {
                    mLoadingPager.showEmpty();
                    if (mSortView != null && GlobalData.isPad() ) {
                        mSortView.setVisibility(View.INVISIBLE);
                    }
                    mTvShare.setEnabled(false);
                    setTotalCount(0);
                    return;
                }
                List<QuestionVo> questions = vo.getItems();
                mTvShare.setEnabled(true);
                setTotalCount(vo.getTotalCount());
                mAdapter.clear();
                mAdapter.addAll(questions);
                mAdapter.setKeyWord(mSearchedkey);
                //mAdapter.setAllowAnswer(vo.isAllowAnswer());
                mAdapter.notifyDataSetChanged();
                if (pageNum >= vo.getTotalPage()) {
                    mLvQuestionBook.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                } else {
                    mLvQuestionBook.setMode(PullToRefreshBase.Mode.BOTH);
                    pageNum++;
                }
                mLoadingPager.showTarget();
                if (mSortView != null && GlobalData.isPad()) {
                    mSortView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFail(HttpResponse<StudentQuestionVo> response, Exception ex) {
                LogUtils.i("queryQuestionBook onFail");
                if( !isAdded() || isDetached() ) return;
                mLoadingPager.showFault(ex);
                if (mSortView != null && GlobalData.isPad() ) {
                    mSortView.setVisibility(View.INVISIBLE);
                }
                mTvShare.setEnabled(false);
                mTvQuestionCount.setText("共--题");
            }
        });
    }

    private long getStartTime() {
        long time = 0;
        if (timeIndex == 0) {
            time = DateUtils.getTimesRecentWeek();
        } else if (timeIndex == 1) {
            time = DateUtils.getTimesTwoWeek();
        } else if (timeIndex == 2) {
            time = DateUtils.getTimesRecentMonth();
        } else if (timeIndex == 3) {
            time = DateUtils.getTimesTwoMonth();
        } else if (timeIndex == 5) {
            time = mCustomStartTime;
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String str = format.format(time);
        LogUtils.i("getStartTime time=" + str);
        return time;
    }

    private long getEndTime() {
        long time;
        if (timeIndex == 5) {
            time = mCustomEndTime;
        } else {
            time = System.currentTimeMillis();
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String str = format.format(time);
        LogUtils.i("getEndTime time=" + str);
        return time;
    }

    private void setTotalCount(int count) {
        mTotalCount = count;
        mTvQuestionCount.setText("共" + count + "题");
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_search:
                KeyboardUtil.hidInput((Activity) mContext);
                queryQuestions();
                break;
            case R.id.iv_delete:
                mEdtSearch.setText(null);
                break;
            case R.id.ll_photograph_collect:
                //goToActivity(XBookListActivity.class);
                break;
            case R.id.tv_share:
                sharePrint();
                break;
            default:
                break;
        }
    }

    private void sharePrint() {
        hasCancel = false;
        if (mTotalCount > 50) {
            ToastUtils.showShort(mContext, "最多打印50题，请缩小时间段重新选择错题");
            return;
        }
        if (mProduceBookDialog == null) {
            mProduceBookDialog = new ProduceBookDialog(mContext);
            mMyProgessLine = mProduceBookDialog.getMyProgessLine();
            mProduceBookDialog.setProduceBook(new ProduceBookDialog.produceBook() {
                @Override
                public void cancel() {
                    showCancelBookDialog();
                }
            });
        }
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("studentId", studentId);
        map.put("key", mSearchedkey);
        map.put("sort", sorts[sortIndex]);
        map.put("startDate", getStartTime() + "");
        map.put("endDate", getEndTime() + "");
        map.put("isShowCorrect", mIvShowCorrect.isChecked() + "");
        map.put("wrongQuestionClasstype", types[kindIndex]);
        mModel.productPdf(map, new RequestListener<BookBean>() {

            @Override
            public void onSuccess(BookBean vo) {
                if (vo == null || TextUtils.isEmpty(vo.getBookId())) {
                    mProduceBookDialog.dismiss();
                    ToastUtils.showShort(mContext, "生成Pdf失败，请重试");
                    return;
                }
                mBookId = vo.getBookId();
                startPolling();
            }

            @Override
            public void onFail(HttpResponse<BookBean> response, Exception ex) {
                mProduceBookDialog.dismiss();
                AlertManager.showErrorInfo(mContext, ex);
            }
        });
        mProduceBookDialog.setProduceTitle("正在准备生成错题本…");
        mMyProgessLine.setProgress(0);
        mProduceBookDialog.show();
    }

    private void showCancelBookDialog() {
        if (mCancelBookDialog == null) {
            mCancelBookDialog = new CancelBookDialog(mContext);
            mCancelBookDialog.setCancelBook(new CancelBookDialog.cancelBook() {
                @Override
                public void waitProduce() {
                    //ToastUtils.showShort(mContext,"waitProduce");
                }

                @Override
                public void sure() {
                    mProduceBookDialog.dismiss();
                    hasCancel = true;
                    //ToastUtils.showShort(mContext,"sure");
                }
            });
        }
        mCancelBookDialog.show();
    }

    //开始轮询
    private void startPolling() {
        LogUtils.i("startPolling");
        if (mAutoQueryTask == null) {
            mAutoQueryTask = new AutoQueryTask();
        }
        queryPdfProgress();
        int waitTime = (int) (mTotalCount * 1000 * 0.9);
        LogUtils.i("waitTime=" + waitTime);
        mProduceBookDialog.setProduceTitle("正在生成错题本，请稍候…");
        mMyProgessLine.dodo(0.03f, 0.9f, waitTime);
    }

    public class AutoQueryTask implements Runnable {
        /**
         * 开始查询
         */
        public void start() {
            // 得到一个主线程的handler
            mHandler.removeCallbacks(this);
            mHandler.postDelayed(this, 5000);
        }


        @Override
        public void run() {
            queryPdfProgress();
        }
    }

    private void queryPdfProgress() {
        mModel.searchPdf(mBookId, new RequestListener<BookBean>() {

            @Override
            public void onSuccess(BookBean vo) {
                LogUtils.i("queryPdfProgress onSuccess");
                if (hasCancel) {
                    return;
                }
                if (vo == null) {
                    continuePoll();
                    return;
                }
                if (vo.getStatus() == PRODUCE_SUCCESS) {
                    if (mCancelBookDialog != null) {
                        mCancelBookDialog.dismiss();
                    }
                    if (TextUtils.isEmpty(vo.getPath())) {
                        mProduceBookDialog.dismiss();
                        ToastUtils.showShort(mContext, "无法获取Pdf路径，请重试");
                    } else {
                        produceSuccess(vo.getPath());
                    }
                } else if (vo.getStatus() == PRODUCE_FAIL) {
                    mProduceBookDialog.dismiss();
                    ToastUtils.showShort(mContext, "生成Pdf失败，请重试");
                } else {
                    continuePoll();
                }

            }

            @Override
            public void onFail(HttpResponse<BookBean> response, Exception ex) {
                LogUtils.i("queryPdfProgress onFail");
                if (hasCancel) {
                    return;
                }
                continuePoll();
            }
        });
    }

    private void produceSuccess(final String path) {
        mMyProgessLine.cancel();
        float progress = mMyProgessLine.getProgress();
        long remainMillisecond = (long) ((1 - progress) * 1000);
        LogUtils.i("cancel progress=" + progress + " remainMillisecond=" + remainMillisecond);
        mMyProgessLine.dodo(progress, 1.0f, remainMillisecond, new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                LogUtils.i("onAnimationEnd");
                mProduceBookDialog.dismiss();
                LoginInfo loginInfo = AccountUtils.getLoginUser();
                String mSeverAddr = "http://www.iclassedu.com";
                if (loginInfo != null) {
                    mSeverAddr = loginInfo.getFileServer();
                }
                shareUrl(mSeverAddr + path);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    private void continuePoll() {
        mAutoQueryTask.start();
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase pullToRefreshBase) {
        refreshData();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase pullToRefreshBase) {
        getMoreData();
    }

    private void refreshData() {
        String sort = sorts[sortIndex];
        long startTime = getStartTime();
        long endTime = getEndTime();
        boolean showCorrect = mIvShowCorrect.isChecked();
        String questionType = types[kindIndex];
        pageNum = 1;
        mModel.queryQuestions(studentId, mSearchedkey, sort, startTime, endTime, showCorrect, questionType, pageNum, pageSize, new RequestListener<StudentQuestionVo>() {

            @Override
            public void onSuccess(StudentQuestionVo vo) {
                if( isDetached() ) return;
                LogUtils.i("refreshData queryQuestionBook onSuccess");
                if (vo == null || vo.getItems() == null || vo.getItems().size() == 0) {
                    mLvQuestionBook.onRefreshComplete();
                    ToastUtils.showShort(mContext, R.string.server_error);
                    return;
                }
                List<QuestionVo> questions = vo.getItems();
                setTotalCount(vo.getTotalCount());
                mAdapter.clear();
                mAdapter.addAll(questions);
                mAdapter.setKeyWord(mSearchedkey);
                mAdapter.notifyDataSetChanged();
                mLvQuestionBook.onRefreshComplete();
                if (pageNum >= vo.getTotalPage()) {
                    mLvQuestionBook.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                } else {
                    mLvQuestionBook.setMode(PullToRefreshBase.Mode.BOTH);
                    pageNum++;
                }
            }

            @Override
            public void onFail(HttpResponse<StudentQuestionVo> response, Exception ex) {
                LogUtils.i("refreshData queryQuestionBook onFail");
                if( isDetached() ) return;
                mLvQuestionBook.onRefreshComplete();
                AlertManager.showErrorInfo(mContext, ex);
            }
        });
    }


    private void getMoreData() {
        String sort = sorts[sortIndex];
        long startTime = getStartTime();
        long endTime = getEndTime();
        boolean showCorrect = mIvShowCorrect.isChecked();
        String questionType = types[kindIndex];
        mModel.queryQuestions(studentId, mSearchedkey, sort, startTime, endTime, showCorrect, questionType, pageNum, pageSize, new RequestListener<StudentQuestionVo>() {

            @Override
            public void onSuccess(StudentQuestionVo vo) {
                if( isDetached() ) return;
                LogUtils.i("getMoreData queryQuestionBook onSuccess");
                if (vo == null || vo.getItems() == null || vo.getItems().size() == 0) {
                    mLvQuestionBook.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                    mLvQuestionBook.onRefreshComplete();
                    return;
                }
                List<QuestionVo> questions = vo.getItems();
                setTotalCount(vo.getTotalCount());
                mAdapter.addAll(questions);
                mAdapter.setKeyWord(mSearchedkey);
                mAdapter.notifyDataSetChanged();
                mLvQuestionBook.onRefreshComplete();
                if (pageNum >= vo.getTotalPage()) {
                    mLvQuestionBook.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                } else {
                    mLvQuestionBook.setMode(PullToRefreshBase.Mode.BOTH);
                    pageNum++;
                }
            }

            @Override
            public void onFail(HttpResponse<StudentQuestionVo> response, Exception ex) {
                if( isDetached() ) return;
                LogUtils.i("getMoreData queryQuestionBook onFail");
                mLvQuestionBook.onRefreshComplete();
                AlertManager.showErrorInfo(mContext, ex);
            }
        });
    }

    private void showUploadDialog(int position, int left, int top, int bottom) {
        QuestionVo questionVo = mAdapter.getItem(position);
        UploadRecordDialog uploadRecordDialog = new UploadRecordDialog(mContext, questionVo, left, top, bottom);
        uploadRecordDialog.show();
        //ReportQuestionDetailActivity.startReportQuestionDetailActivity(mContext,questionVo.getExamNames().get(0).getExamId(),questionVo.getQuestionId(),true);
    }

    private void goToActivity(Class clazz) {
        Intent intent = new Intent(mContext, clazz);
        mContext.startActivity(intent);
    }

    public void clearData() {
        if (mAdapter != null) {
            mAdapter.clear();
            mAdapter.notifyDataSetChanged();
        }
    }

    //分享
    public void shareUrl(String url) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
        String reallyName = null;
        if (detailinfo != null) {
            reallyName = detailinfo.getReallyName();
        }
        SimpleDateFormat format = new SimpleDateFormat("_yyyy-MM-dd");
        String date = format.format(getEndTime());
        String fileName = reallyName + date + "_豆豆数学错题本.pdf";
        String address = url;
        try {
            address += "?filename=" + URLEncoder.encode(fileName, "utf-8");
        } catch (UnsupportedEncodingException e) {
            LogUtils.e(e.getMessage());
        }
        LogUtils.i("shareUrl address=" + address);
        shareIntent.putExtra(Intent.EXTRA_TEXT, address);
        shareIntent.setType("text/plain");
        //设置分享列表的标题，并且每次都显示分享列表
        startActivity(Intent.createChooser(shareIntent, "分享到"));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void receive(RefreshBrowerEvent event) {
        // 收到本地消息后 请求网络刷新错题本
        if (!TextUtils.isEmpty(studentId)) {
            queryQuestions();
        }
    }
}
