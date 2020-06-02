package com.tsinghuabigdata.edu.ddmath.module.learnmaterial.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.tsinghuabigdata.edu.ddmath.MVPModel.LearnMaterialModel;
import com.tsinghuabigdata.edu.ddmath.MVPModel.MyStudyModel;
import com.tsinghuabigdata.edu.ddmath.MVPModel.RequestListener;
import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.module.learnmaterial.adapter.SelectBookAdapter;
import com.tsinghuabigdata.edu.ddmath.module.learnmaterial.bean.BookListBean;
import com.tsinghuabigdata.edu.ddmath.module.learnmaterial.bean.OriginalBookBean;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.AlertManager;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.KeyboardUtil;
import com.tsinghuabigdata.edu.ddmath.util.LogUtils;
import com.tsinghuabigdata.edu.ddmath.util.MyViewUtils;
import com.tsinghuabigdata.edu.ddmath.util.ToastUtils;
import com.tsinghuabigdata.edu.ddmath.util.WindowUtils;
import com.tsinghuabigdata.edu.ddmath.view.LoadingPager;
import com.tsinghuabigdata.edu.ddmath.view.TimeSpinnerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * 创建一次作业--选择教辅Dialog
 * Created by Administrator on 2018/1/15.
 */

public class SelectBookDialog extends Dialog implements View.OnClickListener, PullToRefreshBase.OnRefreshListener2 {

    private TimeSpinnerView       mGradeSpinnerView;
    private TextView              mTvTitle;
    private EditText              mEdtSearchBook;
    private RelativeLayout        mRlSearchBook;
    private PullToRefreshGridView mGvBook;
    private LoadingPager          mLoadingPager;
    private ImageView             mIvCancel;
    private ImageView             mIvSure;

    private String mStudentId   = "";
    private String mSearchedkey = "";
    private int    pageNum      = 1;
    private int    pageSize     = 10;  //3行
    private int selectPosition = -1;
    private int gradeIndex = 6;         //默认全部

    private Context            mContext;
    private Activity           mActivity;
    private SelectBookListener mSelectBookListener;
    private SelectBookAdapter  mAdapter;
    private List<OriginalBookBean> mList = new ArrayList<>();

    public SelectBookDialog(Context context) {
        super(context, R.style.FullTransparentDialog);
        initView();
        initData();
    }


    private void initView() {
        mContext = getContext();
        if (GlobalData.isPad()) {
            setContentView(R.layout.dialog_select_book);
        } else {
            setContentView(R.layout.dialog_select_book_phone);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            if( window!=null ){
                window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            }
        }
        mTvTitle = (TextView) findViewById(R.id.tv_title);
        mEdtSearchBook = (EditText) findViewById(R.id.edt_search_book);
        mRlSearchBook = (RelativeLayout) findViewById(R.id.rl_search_book);
        mGvBook = (PullToRefreshGridView) findViewById(R.id.gv_book);
        mLoadingPager = (LoadingPager) findViewById(R.id.loadingPager);
        mIvCancel = (ImageView) findViewById(R.id.iv_cancel);
        mIvSure = (ImageView) findViewById(R.id.iv_sure);
        mIvSure.setEnabled(false);
        mEdtSearchBook.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_SEARCH) {
                    KeyboardUtil.hidInput(mActivity);
                    searchBook();
                }
                return false;
            }
        });
        mLoadingPager.setTargetView(mGvBook);
        mLoadingPager.setListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchBook();
            }
        });
        initGridView();


        mRlSearchBook.setOnClickListener(this);
        mIvCancel.setOnClickListener(this);
        mIvSure.setOnClickListener(this);

        setCancelable(true);
        setCanceledOnTouchOutside(false);

        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.dimAmount = 0.5f;
        lp.width = (int) (WindowUtils.getScreenWidth(getContext()) * 0.9);
        lp.height = (int) (WindowUtils.getScreenHeight(getContext()) * 0.9);
        dialogWindow.setAttributes(lp);
        dialogWindow.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        mGradeSpinnerView = (TimeSpinnerView)findViewById( R.id.gradeSpinnerView );
        final List<String> gradelist = Arrays.asList( getContext().getResources().getStringArray(R.array.grade_filter_array));
        mGradeSpinnerView.setShowDate( false );
        mGradeSpinnerView.setText( gradelist.get(gradeIndex) );
        mGradeSpinnerView.setData( gradelist, gradeIndex );
        mGradeSpinnerView.setOffset( WindowUtils.getScreenHeight(getContext()) /20,WindowUtils.getScreenWidth(getContext()) /20 );
        mGradeSpinnerView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position != mGradeSpinnerView.getSelectedPosition()) {
                    mGradeSpinnerView.setSelectedPosition(position);
                    gradeIndex = position;
                    mGradeSpinnerView.setText(gradelist.get(position));
                    searchBook();
                }
                mGradeSpinnerView.dismiss();
            }
        });
        //mGradeSpinnerView.setTextColor(Color.rgb( 0x0C, 0xC9, 0xDC ) );
    }


    private void initGridView() {
        pageSize = 2 * 5;
        MyViewUtils.setPTRText(mContext, mGvBook);
        mGvBook.setOnRefreshListener(this);
        mGvBook.setMode(PullToRefreshBase.Mode.BOTH);
    }

    private void initData() {
        if (AccountUtils.getUserdetailInfo() != null) {
            mStudentId = AccountUtils.getUserdetailInfo().getStudentId();
        }
        queryBookList();
        mAdapter = new SelectBookAdapter(getContext(), mList);
        mGvBook.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (selectPosition == position) {
                    showNoSelect();
                } else {
                    selectPosition = position;
                    mIvSure.setEnabled(true);
                }
                updateList();
            }
        });
        mGvBook.setAdapter(mAdapter);
    }

    private void showNoSelect() {
        selectPosition = -1;
        mIvSure.setEnabled(false);
    }

    private void queryBookList() {
        HashMap<String, String> params = new LinkedHashMap<>();
        params.put("studentId", mStudentId);
        params.put("bookName", mSearchedkey);
        params.put("pageNum", pageNum + "");
        params.put("pageSize", pageSize + "");
        if( gradeIndex < 6 ){      //全部不传
            params.put("gradeId", "000000000"+( gradeIndex+4 ) );
        }
        new LearnMaterialModel().queryBookList(params, new RequestListener<BookListBean>() {
            @Override
            public void onSuccess(BookListBean bean) {
                LogUtils.i("queryBookList onSuccess");
                showNoSelect();
                if (bean == null || bean.getItems() == null || bean.getItems().size() == 0) {
                    mLoadingPager.showEmpty();
                    return;
                }
                mList.clear();
                dealOriginalBookName( bean.getItems() );
                mList.addAll(bean.getItems());
                if (mAdapter == null) {
                    mAdapter = new SelectBookAdapter(mContext, mList);
                    mGvBook.setAdapter(mAdapter);
                } else {
                    mAdapter.notifyDataSetChanged();
                }
                if (bean.getItems().size() < pageSize) {
                    mGvBook.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                } else {
                    mGvBook.setMode(PullToRefreshBase.Mode.BOTH);
                    pageNum++;
                }
                mLoadingPager.showTarget();
            }

            @Override
            public void onFail(HttpResponse<BookListBean> response, Exception ex) {
                LogUtils.i("queryBookList onFail");
                showNoSelect();
                mLoadingPager.showFault(ex);
            }
        });
    }

    private void getData(final boolean refresh) {
        if (refresh) {
            pageNum = 1;
        }
        HashMap<String, String> params = new LinkedHashMap<>();
        params.put("studentId", mStudentId);
        params.put("bookName", mSearchedkey);
        params.put("pageNum", pageNum + "");
        params.put("pageSize", pageSize + "");
        if( gradeIndex < 6 ){      //全部不传
            params.put("gradeId", "000000000"+( gradeIndex+4 ) );
        }
        new LearnMaterialModel().queryBookList(params, new RequestListener<BookListBean>() {
            @Override
            public void onSuccess(BookListBean bean) {
                LogUtils.i("queryBookList onSuccess");
                if (bean == null || bean.getItems() == null || bean.getItems().size() == 0) {
                    if (refresh) {
                        ToastUtils.showShort(mContext, R.string.server_error);
                    } else {
                        mGvBook.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                    }
                    mGvBook.onRefreshComplete();
                    return;
                }
                if (refresh) {
                    mList.clear();
                }
                dealOriginalBookName( bean.getItems() );
                mList.addAll(bean.getItems());
                mAdapter.notifyDataSetChanged();
                mGvBook.onRefreshComplete();
                if (bean.getItems().size() < pageSize) {
                    mGvBook.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                } else {
                    mGvBook.setMode(PullToRefreshBase.Mode.BOTH);
                    pageNum++;
                }
                showNoSelect();
            }

            @Override
            public void onFail(HttpResponse<BookListBean> response, Exception ex) {
                LogUtils.i("queryBookList onFail");
                mGvBook.onRefreshComplete();
                AlertManager.showErrorInfo(getContext(), ex);
            }
        });
    }

    private void dealOriginalBookName( List<OriginalBookBean> list){
        if( list == null || list.size() == 0 ) return;
        for( OriginalBookBean book : list ){
            String bookName = book.getBookName();
            if( bookName != null ){
                book.setBookName( bookName.replace("-Y","") );
            }
        }
    }

    private void updateList() {
        for (int i = 0; i < mList.size(); i++) {
            OriginalBookBean bookBean = mList.get(i);
            if (i == selectPosition) {
                bookBean.setSelect(true);
            } else {
                bookBean.setSelect(false);
            }
        }
        mAdapter.notifyDataSetChanged();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_search_book:
                searchBook();
                break;
            case R.id.iv_cancel:
                dismiss();
                break;
            case R.id.iv_sure:
                if (mSelectBookListener != null) {
                    if (selectPosition >= 0) {
                        OriginalBookBean bookBean = mList.get(selectPosition);
                        checkBookIsSubmit( bookBean );
                        return;
                    }
                }
                dismiss();
                break;
            default:
                dismiss();
                break;
        }
    }

    private void checkBookIsSubmit(final OriginalBookBean bookBean){

        new MyStudyModel().checkBookWorkSubmitStatus( bookBean.getBookId(), new RequestListener<Integer>() {

            @Override
            public void onSuccess(Integer status) {
                if( status != 0 ){  //有作业未提交
                    ToastUtils.show( mContext, "老师使用该教辅书布置了作业，请你提交作业后再选择此教辅书进行自我诊断。");
                }else{
                    dismiss();
                    mSelectBookListener.select(bookBean);
                }
            }

            @Override
            public void onFail(HttpResponse<Integer> response, Exception ex) {
                AlertManager.showErrorInfo( mContext, ex);
            }
        });
    }

    private void searchBook() {
        pageNum = 1;
        mLoadingPager.showLoading();
        mSearchedkey = mEdtSearchBook.getText().toString().trim();
        queryBookList();
    }

    public void setSelectBook(boolean switchtBook) {
        if (switchtBook) {
            mTvTitle.setText("切换教辅");
        } else {
            mTvTitle.setText("选择教辅");
        }
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
        getData(true);
    }

    private void getMoreData() {
        getData(false);
    }

//    private void clearSelectPosition() {
//        selectPosition = -1;
//        mIvSure.setEnabled(false);
//    }

    public void setSelectBookListener(SelectBookListener selectBookListener) {
        mSelectBookListener = selectBookListener;
    }

    public interface SelectBookListener {
        void select(OriginalBookBean bookBean);
    }

    public void setActivity(Activity activity) {
        mActivity = activity;
    }

}

