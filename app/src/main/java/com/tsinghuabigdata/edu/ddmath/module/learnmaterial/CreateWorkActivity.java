package com.tsinghuabigdata.edu.ddmath.module.learnmaterial;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tsinghuabigdata.edu.ddmath.MVPModel.LearnMaterialModel;
import com.tsinghuabigdata.edu.ddmath.MVPModel.MyStudyModel;
import com.tsinghuabigdata.edu.ddmath.MVPModel.RequestListener;
import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.activity.RoboActivity;
import com.tsinghuabigdata.edu.ddmath.bean.UserDetailinfo;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.constant.AppConst;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.DDUploadActivity;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.view.WorkToolbar;
import com.tsinghuabigdata.edu.ddmath.module.errorbook.HelpUtil;
import com.tsinghuabigdata.edu.ddmath.module.learnmaterial.adapter.CatalogAdapter;
import com.tsinghuabigdata.edu.ddmath.module.learnmaterial.bean.CatalogBean;
import com.tsinghuabigdata.edu.ddmath.module.learnmaterial.bean.OriginalBookBean;
import com.tsinghuabigdata.edu.ddmath.module.learnmaterial.bean.SectionListBean;
import com.tsinghuabigdata.edu.ddmath.module.learnmaterial.dialog.SelectBookDialog;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.AlertManager;
import com.tsinghuabigdata.edu.ddmath.util.DensityUtils;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.LogUtils;
import com.tsinghuabigdata.edu.ddmath.util.PicassoUtil;
import com.tsinghuabigdata.edu.ddmath.util.ToastUtils;
import com.tsinghuabigdata.edu.ddmath.util.WindowUtils;
import com.tsinghuabigdata.edu.ddmath.view.LoadingPager;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


/**
 * 创建一次作业
 * Created by Administrator on 2018/1/11.
 */

public class CreateWorkActivity extends RoboActivity implements View.OnClickListener {

    private final static String PARAM_UPLOAD_TYPE = "uploadtype";

    public static void openCreateWorkActivity( Context context, int uploadType){
        if( context == null ) return;
        Intent intent = new Intent(context, CreateWorkActivity.class);
        intent.putExtra( CreateWorkActivity.PARAM_UPLOAD_TYPE, uploadType);
        context.startActivity(intent);
    }

    private String mStudentId = "";
    private String mBookId    = "";
    private float mbookRate;

    private Context mContext;

    @ViewInject(R.id.worktoolbar)
    private WorkToolbar    mWorktoolbar;
    @ViewInject(R.id.loadingPager)
    private LoadingPager   mLoadingPager;
    @ViewInject(R.id.ll_content)
    private LinearLayout   mLlContent;
    @ViewInject(R.id.ll_book_name)
    private LinearLayout   mLlBookName;
    @ViewInject(R.id.tv_book_name)
    private TextView       mTvBookName;
    @ViewInject(R.id.bt_switch_book)
    private Button      mIvSwitchBook;

    @ViewInject(R.id.bt_switch_books)
    private Button      mIvSwitchBooks;

    @ViewInject(R.id.rl_guide)
    private RelativeLayout mRlGuide;

//    private LinearLayout   mLlGudie;
//    private ImageView      mIvGudie;
    @ViewInject(R.id.tv_gudie)
    private TextView       mTvGudie;
    @ViewInject(R.id.fl_selected)
    private FrameLayout    mFlSelected;
//    @ViewInject(R.id.ll_book_content)
//    private LinearLayout   mLlBookContent;
    @ViewInject(R.id.lv_book_chapter)
    private ListView       mLvBookChapter;
    @ViewInject(R.id.iv_confirm_select)
    private ImageView      mIvConfirmSelect;
    @ViewInject(R.id.iv_book_cover)
    private ImageView      mIvBookCover;

    //@ViewInject(R.id.loadingPager_catalog)
    //private LoadingPager   mLoadingPagerCatalog;
    @ViewInject(R.id.bt_switch_book)
    private Button mBtSwitchBook;
    private int     mNum;
    private int     mSingleSpace;
    private boolean switchtBook;

    private int uploadType;

//    private CreateWorkIntroDialog mIntroDialog;
    private SelectBookDialog      mSelectBookDialog;

    private List<SectionListBean> mList = new ArrayList<>();
    private CatalogAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        transparent = true;
        super.onCreate(savedInstanceState);

        //设置全屏显示
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        uploadType = getIntent().getIntExtra(PARAM_UPLOAD_TYPE, AppConst.UPLOAD_TYPE_CAMERA);
        setContentView( GlobalData.isPad()?R.layout.activity_create_work:R.layout.activity_create_work_phone);
        x.view().inject(this);

        initViews();
        initData();
    }

    private void initViews() {
        mContext = this;

        mWorktoolbar.setTitle("选择需诊断的页码");
        if( uploadType == AppConst.UPLOAD_TYPE_MARKED ){
            mTvGudie.setText("请选择老师已批阅的作业来源（教辅）");
            mWorktoolbar.setTitle("创建已批阅的作业");
        }

        mWorktoolbar.setRightTitle("使用说明");

        mWorktoolbar.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HelpUtil.showHelpActivity( mContext, "自我诊断使用说明", "Q004");
            }
        });

        int maxpixels;
        if (GlobalData.isPad()) {
            maxpixels = WindowUtils.getScreenWidth(mContext) - DensityUtils.dp2px(mContext, 36 + 250 + 136 + 36 + 40);
        } else {
            maxpixels = WindowUtils.getScreenWidth(mContext) - DensityUtils.dp2px(mContext, 56 + 16 + 132 + 94 + 28 + 40);
        }
        mTvBookName.setMaxWidth(maxpixels);
        mLoadingPager.setTargetView(mLlContent);
        mLoadingPager.setListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLoadingPager.showLoading();
                loadData();
            }
        });
        ///mLoadingPagerCatalog.setTargetView(mLlBookContent);
//        mLoadingPagerCatalog.setListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mLoadingPagerCatalog.showLoading();
//                loadData();
//            }
//        });
        mIvSwitchBook.setOnClickListener(this);
        mIvSwitchBooks.setOnClickListener(this);
        mIvConfirmSelect.setOnClickListener(this);
        mBtSwitchBook.setOnClickListener(this);
        initGridView();
    }

    private void initGridView() {
        int contentWidth;
        int itemW;
        int itemWidth;
        int screenWidth = WindowUtils.getScreenWidth(mContext);
        if (GlobalData.isPad()) {
            contentWidth = screenWidth - DensityUtils.dp2px(mContext, (36 + 26) * 2)-screenWidth/5;
            itemW = 70;
            itemWidth = DensityUtils.dp2px(mContext, itemW + 40);
        } else {
            contentWidth = screenWidth - DensityUtils.dp2px(mContext, (26 + 19) * 2)-screenWidth/5;
            itemW = 38;
            itemWidth = DensityUtils.dp2px(mContext, itemW + 20);
        }
        LogUtils.i("screenWidth=" + screenWidth + "contentWidth=" + contentWidth + " itemWidth=" + itemWidth);
        mNum = contentWidth / itemWidth;
        if (mNum < 2) {
            mNum = 2;
        }
        int space = contentWidth - DensityUtils.dp2px(mContext, mNum * itemW);
        mSingleSpace = space / (mNum - 1);
        if (mSingleSpace < 0) {
            mSingleSpace = 0;
        }
        LogUtils.i("mNum=" + mNum + "space=" + space + "singleSpace=" + mSingleSpace);
    }

    private void initData() {
        mLlBookName.setVisibility(View.INVISIBLE);
        mFlSelected.setVisibility(View.INVISIBLE);
        UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
        if (detailinfo != null) {
            mStudentId = detailinfo.getStudentId();
        }
        mAdapter = new CatalogAdapter(mContext, mList);
        mAdapter.setActvity(CreateWorkActivity.this);
        mAdapter.setParam(mNum, mSingleSpace,uploadType);
        mLvBookChapter.setAdapter(mAdapter);
        loadData();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_switch_book:
                showSelectBookDialog();
                break;
            case R.id.bt_switch_books:
                showSelectBookDialog();
                break;
            case R.id.iv_confirm_select:
                checkBookIsSubmit();
                break;
            default:
                break;
        }
    }

//    private void showIntroDialog() {
//        if (mIntroDialog == null) {
//            mIntroDialog = new CreateWorkIntroDialog(mContext);
//        }
//        mIntroDialog.show();
//    }

    private void showSelectBookDialog() {
        if (mSelectBookDialog == null) {
            mSelectBookDialog = new SelectBookDialog(mContext);
            mSelectBookDialog.setActivity(this);
            mSelectBookDialog.setSelectBookListener(new SelectBookDialog.SelectBookListener() {
                @Override
                public void select(OriginalBookBean bookBean) {
                    selectOriginalBook(bookBean);
                }
            });
        }
        mSelectBookDialog.setSelectBook(switchtBook);
        mSelectBookDialog.show();
    }

    private void selectOriginalBook(OriginalBookBean bookBean) {
        if (bookBean != null) {
            mBookId = bookBean.getBookId();
            mbookRate = bookBean.getWidthHeightRate();
            mTvBookName.setText(bookBean.getBookName());
            if(!TextUtils.isEmpty(bookBean.getCoverPicture())){
                PicassoUtil.displayImageIndetUrl(bookBean.getCoverPicture(), mIvBookCover);
            }
            showSwicthBook();
            //mLoadingPagerCatalog.showLoading();
            loadData();
        }
    }

    private void showSwicthBook() {
        switchtBook = true;
        mIvSwitchBook.setActivated(true);
        mLlBookName.setVisibility(View.VISIBLE);
        mRlGuide.setVisibility(View.INVISIBLE);
        mFlSelected.setVisibility(View.VISIBLE);
        if (mAdapter != null) {
            mAdapter.clearMap();
        }
        mIvConfirmSelect.setEnabled(false);
    }

    private void loadData() {
        HashMap<String, String> params = new LinkedHashMap<>();
        params.put("studentId", mStudentId);
        if (!TextUtils.isEmpty(mBookId)) {
            params.put("bookId", mBookId);
        }
        new LearnMaterialModel().queryBookCatalog(params, new RequestListener<CatalogBean>() {
            @Override
            public void onSuccess(CatalogBean catalogBean) {
                LogUtils.i("queryLatestcatalog onSuccess");
                //没有教辅书
                if ( catalogBean == null || catalogBean.getSectionList() == null || catalogBean.getSectionList().size() == 0) {
                    switchtBook = false;
                    mIvSwitchBook.setActivated(false);
                    mLoadingPager.showTarget();
                    mRlGuide.setVisibility(View.VISIBLE);
                    mLlContent.setVisibility(View.GONE);
                    return;
                }

                //没有作业要提交，显示内容
                mRlGuide.setVisibility(View.GONE);
                mLlContent.setVisibility(View.VISIBLE);

                switchtBook = true;
                mIvSwitchBook.setActivated(true);
                mBookId = catalogBean.getBookId();
                mTvBookName.setText(catalogBean.getBookName());
                PicassoUtil.displaySpecificImage(catalogBean.getCoverPicture(), mIvBookCover, R.drawable.myjiaofu);

                mbookRate = catalogBean.getWidthHeightRate();

                mList.clear();
                mList.addAll(catalogBean.getSectionList());
                mAdapter.notifyDataSetChanged();
                mLoadingPager.showTarget();
                showSwicthBook();
            }

            @Override
            public void onFail(HttpResponse<CatalogBean> response, Exception ex) {
                LogUtils.i("queryLatestcatalog onFail");
                mLoadingPager.showFault(ex);
            }
        });
    }

    private void checkBookIsSubmit(){

        new MyStudyModel().checkBookWorkSubmitStatus( mBookId, new RequestListener<Integer>() {

            @Override
            public void onSuccess(Integer status) {
                if( status != 0 ){  //有作业未提交
                    ToastUtils.show( mContext, "老师使用该教辅书布置了作业，请你提交作业后再选择此教辅书进行自我诊断。");
                }else{
                    if (mAdapter != null && mAdapter.mHashMap.size() > 0) {
                        ArrayList<Integer> arrayList = new ArrayList<>();
                        Iterator iter = mAdapter.mHashMap.entrySet().iterator();
                        while (iter.hasNext()) {
                            Map.Entry entry = (Map.Entry) iter.next();
                            //String key = (String) entry.getKey();
                            Integer value = (Integer) entry.getValue();
                            arrayList.add(value);
                        }
                        DDUploadActivity.openActivity(mContext, mBookId, arrayList, mbookRate, uploadType );
                    }
                }
            }

            @Override
            public void onFail(HttpResponse<Integer> response, Exception ex) {
                AlertManager.showErrorInfo( mContext, ex);
            }
        });
    }

    public void checkPageSize() {
        if (mAdapter != null) {
            mIvConfirmSelect.setEnabled(mAdapter.mHashMap.size() > 0);
        }
    }
}
