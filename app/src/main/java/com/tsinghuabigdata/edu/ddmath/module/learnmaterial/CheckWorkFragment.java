package com.tsinghuabigdata.edu.ddmath.module.learnmaterial;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.annotation.Nullable;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.tsinghuabigdata.edu.ddmath.MVPModel.MyStudyModel;
import com.tsinghuabigdata.edu.ddmath.MVPModel.RequestListener;
import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.bean.DoudouWork;
import com.tsinghuabigdata.edu.ddmath.bean.MonthSumbitedItem;
import com.tsinghuabigdata.edu.ddmath.bean.MyTutorClassInfo;
import com.tsinghuabigdata.edu.ddmath.bean.SubmitQuestion;
import com.tsinghuabigdata.edu.ddmath.bean.UserDetailinfo;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.commons.newbieguide.HoleBean;
import com.tsinghuabigdata.edu.ddmath.commons.newbieguide.NewbieGuide;
import com.tsinghuabigdata.edu.ddmath.commons.newbieguide.NewbieGuideManager;
import com.tsinghuabigdata.edu.ddmath.constant.AppConst;
import com.tsinghuabigdata.edu.ddmath.event.ChangeClassEvent;
import com.tsinghuabigdata.edu.ddmath.event.RefreshCheckworkEvent;
import com.tsinghuabigdata.edu.ddmath.fragment.MyBaseFragment;
import com.tsinghuabigdata.edu.ddmath.module.errorbook.HelpUtil;
import com.tsinghuabigdata.edu.ddmath.module.learnmaterial.adapter.SubmitedCheckWorkAdatper;
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

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

/**
 * 豆豆检查作业
 * Created by Administrator on 2018/1/11.
 */

public class CheckWorkFragment extends MyBaseFragment implements PullToRefreshBase.OnRefreshListener2<ListView>, View.OnClickListener, NewbieGuide.OnGuideChangedListener {

    private Context mContext;

    private int type    = 1;
    private int pageNum = 1;
    private int pageSize = 20;
    private String mStudentId = "";

    private MyStudyModel myStudyModel = new MyStudyModel();

    //private List<SubmitQuestion>    mListDiagnosing = new ArrayList<>();
    private List<MonthSumbitedItem> mList           = new ArrayList<>();
    private SubmitedCheckWorkAdatper   mAdatper;
    //private DiagnosingCheckWorkAdatper mDiagnosingAdatper;

    private PullToRefreshListView mLvCheckWork;
    private LoadingPager          mLoadingPager;
    //private View                  mViewSubmitted;
    //private ImageView             mIvHowUse;
    private LinearLayout uploadWork;
    //private LinearLayout          mLlDiagnosing;
    //private MultiGridView         mGvDiagnosing;

    @Nullable
    @Override
    public View onCreateView(@Nonnull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root;
        if (GlobalData.isPad()) {
            root = inflater.inflate(R.layout.fragment_check_work, container, false);
        } else {
            root = inflater.inflate(R.layout.fragment_check_work_phone, container, false);
        }
        initView(root);
        setPrepared();
        //initData();
        return root;
    }

    @Override
    public void lazyLoad() {
        initCommonGudie();
        initData();
    }

    public String getUmEventName() {
        return "learntask_selfcheck";
    }

    @Override
    public void onClick(View v) {
        if( R.id.layout_create_correctwork == v.getId() ){

            myStudyModel.getTodayCheckWorkCount(new RequestListener<Integer>() {
                @Override
                public void onSuccess(Integer res) {
                    int count = res;
                    if( count >= 2 )
                        ToastUtils.showToastCenter( mContext, "你今天的次数已用完，请明天再来吧～" );
                    else
                        startActivity(new Intent(mContext, CreateWorkActivity.class));
                }

                @Override
                public void onFail(HttpResponse<Integer> response, Exception ex) {
                    AlertManager.showErrorInfo( mContext,ex );
                }
            });
        }else if(R.id.layout_upload_correctedwork == v.getId() ){
            myStudyModel.getTodayCheckWorkCount(new RequestListener<Integer>() {
                @Override
                public void onSuccess(Integer res) {
                    int count = res;
                    if( count >= 2 )
                        ToastUtils.showToastCenter( mContext, "你今天的次数已用完，请明天再来吧～" );
                    else
                        CreateWorkActivity.openCreateWorkActivity( mContext, AppConst.UPLOAD_TYPE_MARKED );
                }

                @Override
                public void onFail(HttpResponse<Integer> response, Exception ex) {
                    AlertManager.showErrorInfo( mContext,ex );
                }
            });
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

    //-----------------------------------------------------------------------------------------------

    private void initView(View root) {
        mContext = getActivity();

        mLvCheckWork =  root.findViewById(R.id.lv_check_work);
        mLoadingPager =  root.findViewById(R.id.loadingPager);
        LinearLayout createWork = root.findViewById(R.id.layout_create_correctwork);
        createWork.setOnClickListener( this );
        uploadWork = root.findViewById(R.id.layout_upload_correctedwork);
        uploadWork.setOnClickListener( this );

        mLoadingPager.stopAnim();
        MyViewUtils.setPTRText(mContext, mLvCheckWork);
        mLoadingPager.setTargetView(mLvCheckWork);
        initHeaderView();
        initSize();
        mLvCheckWork.setOnRefreshListener(this);
        mLoadingPager.setListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLoadingPager.showLoading();
                queryData();
            }
        });
    }

    private void initHeaderView() {
        View mViewSubmitted = LayoutInflater.from(mContext).inflate( GlobalData.isPad()?R.layout.view_check_work_submitted:R.layout.view_check_work_submitted_phone, null);

        ImageView mIvHowUse =  mViewSubmitted.findViewById(R.id.iv_how_use);
//        mLlDiagnosing =  mViewSubmitted.findViewById(R.id.ll_diagnosing);
//        mGvDiagnosing =  mViewSubmitted.findViewById(R.id.gv_diagnosing);
        mLvCheckWork.getRefreshableView().addHeaderView(mViewSubmitted);
        mIvHowUse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ProductHelpActivity.gotoProductHelpActivity(mContext, "自我诊断", AppConst.PRIVILEGE_ORIGINALMATERIAL);
                HelpUtil.showHelpActivity( mContext, "自由练使用说明", "Q010");
            }
        });
    }


    private void initSize() {
        //动态计算作业列表条目数量
        /*int contentWidth;
        int itemW;
        int itemWidth;
        int screenWidth = WindowUtils.getScreenWidth(getActivity());
        if (GlobalData.isPad()) {
            //校内作业左边距15 右边距20  列表左边距20 右边距 创建作业按钮长度122 两边间距40
            contentWidth = screenWidth - DensityUtils.dp2px(getActivity(), AppConst.NAVI_WIDTH_PAD + 15 + 20 + 20 + 122 + 40 + 10);
            itemW = 180;
            itemWidth = DensityUtils.dp2px(getActivity(), itemW + 15);
        } else {
            contentWidth = screenWidth - DensityUtils.dp2px(getActivity(), AppConst.NAVI_WIDTH_PHONE + 10 + 10 + 81 + 30 + 4);
            itemW = 134;
            itemWidth = DensityUtils.dp2px(getActivity(), itemW + 4);
        }
        LogUtils.i("screenWidth=" + screenWidth + "contentWidth=" + contentWidth + " itemWidth=" + itemWidth);
        int mNum = contentWidth / itemWidth;
        if (mNum < 2) {
            mNum = 2;
        }
        int space = contentWidth - DensityUtils.dp2px(getActivity(), mNum * itemW);
        int singleSpace = space / (mNum - 1);
        if (singleSpace < 0) {
            singleSpace = 0;
        }
        LogUtils.i("mNum=" + mNum + "space=" + space + "singleSpace=" + singleSpace);
*/
        mAdatper = new SubmitedCheckWorkAdatper(mContext, mList);
        mLvCheckWork.setAdapter(mAdatper);

//        mDiagnosingAdatper = new DiagnosingCheckWorkAdatper(mContext, mListDiagnosing);
//        mGvDiagnosing.setAdapter(mDiagnosingAdatper);
//        mGvDiagnosing.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                SubmitQuestion submitQuestion = (SubmitQuestion)mDiagnosingAdatper.getItem(position);
//                if (submitQuestion != null) {
//                    Intent intent = new Intent(mContext, DDUploadActivity.class);
//                    intent.putExtra(DDUploadActivity.PARAM_DDWORKID, submitQuestion.getExamId());
//                    intent.putExtra(DDUploadActivity.PARAM_TITLE, submitQuestion.getExamName());
//                    mContext.startActivity(intent);
//                }
//            }
//        });
    }

    private void initData() {
        createLoginInfo();
        mLoadingPager.startAnim();
        queryWorkList();
        EventBus.getDefault().register(this);
    }

    //查询登录信息
    private void createLoginInfo() {
        UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
        if (detailinfo != null) {
            mStudentId = detailinfo.getStudentId();
        }
    }

//    //查询班级信息
//    private void queryClassInfo() {
//
//    }

    public void clearData() {
        mList.clear();
        if (mAdatper != null) {
            mAdatper.notifyDataSetChanged();
        }
    }

    //查询机构作业列表
    public void queryData() {
        if (isPrepared) {
            queryWorkList();
        }
    }


    //查询作业列表
    private void queryWorkList() {

        MyTutorClassInfo classInfo = AccountUtils.getCurrentClassInfo();
        if (classInfo == null || TextUtils.isEmpty(classInfo.getClassId())) {
            mLoadingPager.showEmpty( "你还没有加入班级" );
            return;
        }

        int status = 1;
        pageNum = 1;
        myStudyModel.queryDoudouCheckWork(mStudentId, classInfo.getClassId(), 1, pageNum * pageSize, status, type, new RequestListener<DoudouWork>() {

            @Override
            public void onSuccess(DoudouWork vo) {
                LogUtils.i("queryDoudouWork onSuccess");
                if (vo == null || vo.getExerhomes() == null || vo.getExerhomes().size() == 0) {
                    mLoadingPager.showEmpty("还没有学习内容哦，可以点击右侧按钮，创建自己的作业～");
                    return;
                }
                List<SubmitQuestion> exerhomes = vo.getExerhomes();
                /*for (int i = 0; i < exerhomes.size(); i++) {
                    if (i % 3 == 0) {
                        exerhomes.get(i).setExerStatus(2);
                    }
                }*/
                //模拟诊断中
                changeAdapter(exerhomes);
                if (exerhomes.size() < pageSize) {
                    mLvCheckWork.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                } else {
                    mLvCheckWork.setMode(PullToRefreshBase.Mode.BOTH);
                    pageNum++;
                }
                mLoadingPager.showTarget();
            }

            @Override
            public void onFail(HttpResponse<DoudouWork> response, Exception ex) {
                LogUtils.i("queryDoudouWork onFail");
                mLoadingPager.showFault(ex);
            }
        });
    }

    private void changeAdapter(List<SubmitQuestion> exerhomes) {
        mList.clear();
        mList.addAll(DataUtils.selectDiagnosedWork(exerhomes));
        mAdatper.notifyDataSetChanged();
//        mListDiagnosing.clear();
//        mListDiagnosing.addAll(DataUtils.selectDiagnosingdWork(exerhomes));
//        mDiagnosingAdatper.notifyDataSetChanged();
//        if (mDiagnosingAdatper.getCount() > 0) {
//            mLlDiagnosing.setVisibility(View.VISIBLE);
//        } else {
//            mLlDiagnosing.setVisibility(View.GONE);
//        }
    }

    //查询作业列表(刷新或加载更多)
    private void getData(final boolean refresh) {
        MyTutorClassInfo classInfo = AccountUtils.getCurrentClassInfo();
        if (classInfo == null || TextUtils.isEmpty(classInfo.getClassId())) {
            mLoadingPager.showEmpty( "你还没有加入班级" );
            return;
        }

        int status = 1;
        if (refresh) {
            pageNum = 1;
        }
        /*TODO 需要优化，数据多了，性能问题 ，每次都重头加载到当前页的数量 */
        myStudyModel.queryDoudouCheckWork(mStudentId, classInfo.getClassId(), 1, pageNum * pageSize, status, type, new RequestListener<DoudouWork>() {

            @Override
            public void onSuccess(DoudouWork vo) {
                LogUtils.i("getData onSuccess");
                if (vo == null || vo.getExerhomes() == null || vo.getExerhomes().size() == 0) {
                    if (refresh) {
                        ToastUtils.showShort(mContext, R.string.server_error);
                    } else {
                        mLvCheckWork.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                    }
                    mLvCheckWork.onRefreshComplete();
                    return;
                }
                List<SubmitQuestion> exerhomes = vo.getExerhomes();
                changeAdapter(exerhomes);
                mLvCheckWork.onRefreshComplete();
                if (exerhomes.size() < pageNum * pageSize) {
                    mLvCheckWork.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                } else {
                    mLvCheckWork.setMode(PullToRefreshBase.Mode.BOTH);
                    pageNum++;
                }
            }

            @Override
            public void onFail(HttpResponse<DoudouWork> response, Exception ex) {
                LogUtils.i("getData onFail");
                mLvCheckWork.onRefreshComplete();
                AlertManager.showErrorInfo(getContext(), ex);
            }
        });
    }

    private void refreshData() {
        getData(true);
    }


    private void getMoreData() {
        getData(false);
    }

    @Subscribe
    public void receive(RefreshCheckworkEvent event) {
        AppLog.d("event = " + event );
        queryData();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receive(ChangeClassEvent event) {
        AppLog.d("event = " + event );
        clearData();
        queryData();
    }

    //--------------------------------------------------------------------------------------
    //增加浮层提示
    private NewbieGuideManager mGuideManager;
    private int guideViewIndex = 0;

    private void initCommonGudie() {
        String cname = CheckWorkFragment.class.getName();
        UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
        if (detailinfo == null)
            return;

        //没有显示过 且 手机号是空的 提示
        if ( NewbieGuideManager.isNeverShowed(getActivity(), cname, "_v1")) {
            mGuideManager = new NewbieGuideManager(getActivity(), cname, "_v1");
            mGuideManager.showWithListener(this);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    addGuideView();
                }
            }, 300);
        }
    }

    private void addGuideView() {

        if(0!=guideViewIndex) return;

        Rect rect = new Rect();
        uploadWork.getGlobalVisibleRect(rect);

        final NewbieGuide mNewbieGuide = mGuideManager.getNewbieGuide();
        //高亮区域
        mNewbieGuide.addHighLightView(uploadWork, HoleBean.TYPE_ROUNDRECT);

        if (GlobalData.isPad()) {
            int offx = rect.left - get(940);
            int offy = rect.top - get(190);
            if( offy <= 0 ) offy = 1;
            mNewbieGuide.addIndicateImg(R.drawable.img_ziwozhenduan, offx, offy, get(940), get(190));

            //下一步
            offx = offx + get(940) / 2 + get(30);
            offy = offy + get(190) + get(50);
            mNewbieGuide.addBtnImage(R.drawable.ic_i_see, offx, offy, get(180), get(90));
        } else {
            //指示图
            int offx = rect.left - get(472);
            int offy = rect.top - get(97);
            if( offy <= 0 ) offy = 1;
            mNewbieGuide.addIndicateImg(R.drawable.img_ziwozhenduan, offx, offy, get(472), get(97));

            //下一步
            offx = offx + get(472) / 2 + get(30);
            offy = offy + get(97) + get(30);
            mNewbieGuide.addBtnImage(R.drawable.ic_i_see, offx, offy, get(93), get(46));
        }

        mGuideManager.show();
    }

    private int get(int dis) {
        return DensityUtils.dp2px(mContext, dis);
    }

    @Override
    public void onShowed() {
        guideViewIndex++;
    }

    @Override
    public void onRemoved() {
        addGuideView();
    }

}
