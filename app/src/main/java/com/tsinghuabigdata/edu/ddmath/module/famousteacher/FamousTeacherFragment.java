package com.tsinghuabigdata.edu.ddmath.module.famousteacher;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.tsinghuabigdata.edu.ddmath.MVPModel.FamousTeacherModel;
import com.tsinghuabigdata.edu.ddmath.MVPModel.RequestListener;
import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.bean.LoginInfo;
import com.tsinghuabigdata.edu.ddmath.bean.MyTutorClassInfo;
import com.tsinghuabigdata.edu.ddmath.bean.UserDetailinfo;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.event.ChangeClassEvent;
import com.tsinghuabigdata.edu.ddmath.fragment.MyBaseFragment;
import com.tsinghuabigdata.edu.ddmath.module.errorbook.HelpUtil;
import com.tsinghuabigdata.edu.ddmath.module.famousteacher.adapter.FamousVideoAdapter;
import com.tsinghuabigdata.edu.ddmath.module.famousteacher.bean.FamousProductBean;
import com.tsinghuabigdata.edu.ddmath.module.famousteacher.bean.FamousVideoBean;
import com.tsinghuabigdata.edu.ddmath.module.famousteacher.bean.SingleVideoBean;
import com.tsinghuabigdata.edu.ddmath.module.product.ProductUtil;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.AlertManager;
import com.tsinghuabigdata.edu.ddmath.util.AppLog;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.KeyboardUtil;
import com.tsinghuabigdata.edu.ddmath.util.MyViewUtils;
import com.tsinghuabigdata.edu.ddmath.util.ToastUtils;
import com.tsinghuabigdata.edu.ddmath.view.LoadingPager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 豆豆检查作业
 * Created by Administrator on 2018/2/6.
 */

public class FamousTeacherFragment extends MyBaseFragment implements PullToRefreshBase.OnRefreshListener2, View.OnClickListener {

    private Context mContext;

    private int    pageNum      = 1;
    private int    pageSize     = 10;
    private String mStudentId   = "";
    private String mClassId     = "";
    private String mSchoolId    = "";
    private String mSearchedkey = "";
    private boolean clickExchanged;

    private FamousProductBean mFamousProductBean;

    private List<SingleVideoBean> mList = new ArrayList<>();
    private FamousVideoAdapter mAdatper;

    private LinearLayout          mLlSearch;
    private ImageView             mIvSearch;
    private EditText              mEdtSearch;
    private ImageView             mIvCourseHelp;
    private CheckBox              mCbShowExchanged;
    private PullToRefreshGridView mGvVideo;
    private LoadingPager          mLoadingPager;

    private AtomicBoolean atomicBoolean = new AtomicBoolean();
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root;
        if (GlobalData.isPad()) {
            root = inflater.inflate(R.layout.fragment_famous_teacher, container, false);
        } else {
            root = inflater.inflate(R.layout.fragment_famous_teacher_phone, container, false);
        }
        initView(root);
        setPrepared();
        //initData();
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void lazyLoad() {
        initData();
    }

    private void initView(View root) {
        mContext = getActivity();
        mLlSearch =  root.findViewById(R.id.ll_famous_search);
        mIvSearch =  root.findViewById(R.id.iv_search);
        mEdtSearch =  root.findViewById(R.id.edt_search);
        mIvCourseHelp =  root.findViewById(R.id.iv_course_help);
        mCbShowExchanged =  root.findViewById(R.id.cb_show_exchanged);
        mGvVideo =  root.findViewById(R.id.gv_video);
        mLoadingPager =  root.findViewById(R.id.loadingPager_video);

        mLoadingPager.stopAnim();
        MyViewUtils.setPTRText(mContext, mGvVideo);
        mLoadingPager.setTargetView(mGvVideo);
        mGvVideo.setOnRefreshListener(this);
        mIvSearch.setOnClickListener(this);
        mIvCourseHelp.setOnClickListener(this);
        mLoadingPager.setListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLoadingPager.showLoading();
                if (mFamousProductBean == null) {
                    queryFamousProductList();
                } else if (clickExchanged && mCbShowExchanged.isChecked()) {
                    queryExchangedVideoList();
                } else {
                    queryVideoList();
                }

            }
        });
        mCbShowExchanged.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                clickExchanged = true;
                mLoadingPager.showLoading();
                if (isChecked) {
                    queryExchangedVideoList();
                } else {
                    pageNum = 1;
                    queryVideoList();
                }

            }
        });
        mEdtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                //LogUtils.i("actionId=" + actionId);
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_SEARCH) {
                    KeyboardUtil.hidInput(getActivity());
                    searchVideo();
                }
                return false;
            }
        });
        mGvVideo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SingleVideoBean singleVideoBean = mList.get(position);
                if (singleVideoBean != null) {
                    clickWatch(singleVideoBean);
                }
            }
        });
    }


    private void searchVideo() {
        clickExchanged = false;
        mLoadingPager.showLoading();
        pageNum = 1;
        mSearchedkey = mEdtSearch.getText().toString().trim();
        queryVideoList();
    }


    private void initData() {
        //LogUtils.i("FamousTeacherFragment initData");
        createLoginInfo();
        mLoadingPager.startAnim();
        queryFamousProductList();
        EventBus.getDefault().register(this);
    }

    //查询登录信息
    private void createLoginInfo() {
        if (AccountUtils.getUserdetailInfo() != null) {
            mStudentId = AccountUtils.getUserdetailInfo().getStudentId();
        }
        queryClassInfo();
    }

    //查询班级信息
    private void queryClassInfo() {
        if (AccountUtils.getCurrentClassInfo() != null) {
            mClassId = AccountUtils.getCurrentClassInfo().getClassId();
            mSchoolId = AccountUtils.getCurrentClassInfo().getSchoolId();
        }
    }

    private void clickWatch(final SingleVideoBean item) {
        final LoginInfo loginInfo = AccountUtils.getLoginUser();
        final UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
        final MyTutorClassInfo classInfo = AccountUtils.getCurrentClassInfo();
        if (item == null || detailinfo == null || loginInfo == null || classInfo == null) {
            return;
        }
        //先判断是否已购买 已购买,直接观看/未购买，先查询剩余使用次数
        if (item.getStatus() == SingleVideoBean.Exchanged) {
            goToWatch(item);
        } else {
            //showExchangeDialog(item);
            getProductUseTimes(item);
        }
    }

    /**
     * 获取名师精讲商品剩余免费兑换次数
     */
    private void getProductUseTimes(final SingleVideoBean item) {
        if( atomicBoolean.get()){
            ToastUtils.show( mContext, "请稍候在点击...");
            return;
        }
        atomicBoolean.set( true );

        ProductUtil.videoCheckPermissionAndExchange(mContext, item.getVideoId(), atomicBoolean, ProductUtil.FROM_VIDEO, new ProductUtil.ProductCallBack() {
            @Override
            public void onSuccess() {
                atomicBoolean.set(false);
                //成功 状态改变
                item.setStatus(SingleVideoBean.Exchanged);
                mAdatper.notifyDataSetChanged();

                //提示用户
                String data = String.format(Locale.getDefault(), "可以去观看“%s”啦！", FamousVideoAdapter.getName(item));
                AlertManager.showCustomImageBtnDialog(mContext, data, "开始观看", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        goToWatch(item);
                    }
                }, null);
            }
        });

//        new FamousTeacherModel().getFamousProductList(mStudentId, mSchoolId, new RequestListener<List<FamousProductBean>>() {
//
//            @Override
//            public void onSuccess(List<FamousProductBean> res) {
//                if (res == null || res.size() == 0 || res.get(0) == null) {
//                    mLoadingPager.showEmpty(R.string.no_product);
//                    atomicBoolean.set( false );
//                    return;
//                }
//                FamousProductBean bean = res.get(0);
//                int freeCount = bean.getFreeUseTimes();
//                showExchangeDialog(item, freeCount);
//            }
//
//            @Override
//            public void onFail(HttpResponse<List<FamousProductBean>> response, Exception ex) {
//                ToastUtils.showShort(mContext, R.string.query_times_failure);
//                atomicBoolean.set( false );
//            }
//        });
    }

//    private void showExchangeDialog(final SingleVideoBean item, int freeCount) {
//
//        ProductUtil.showExchangePracticeDialog(mContext, true, AppConst.PRIVILEGE_SYNC_CLASSROOM, freeCount, "同步微课", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                //点击了确认， 开始兑换
//                exchangeVideoProduct(mFamousProductBean.getProductId(), item);
//            }
//        }, null, atomicBoolean);  // 点击取消处理   多次点击处理
//    }

//    //兑换套题商品,不区分用免费使用权 还是 学豆，服务器后台处理
//    private void exchangeVideoProduct(final String productId, final SingleVideoBean singleVideoBean) {
//
//        //开始兑换
//        new ProductModel().exchangePracticeProduct(mStudentId, mClassId, productId, "", singleVideoBean.getVideoId(), new RequestListener() {
//            @Override
//            public void onSuccess(Object res) {
//                //成功 状态改变
//                singleVideoBean.setStatus(SingleVideoBean.Exchanged);
//                mAdatper.notifyDataSetChanged();
//
//                atomicBoolean.set( false );
//                //刷新学豆
//                //EventBus.getDefault().post(new UpdateStudybeanEvent());
//
//                //提示用户
//                String data = String.format(Locale.getDefault(), "可以去观看“%s”啦！", FamousVideoAdapter.getName(singleVideoBean));
//
//                AlertManager.showCustomImageBtnDialog(getContext(), data, "开始观看", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        goToWatch(singleVideoBean);
//                    }
//                }, null);
//            }
//
//            @Override
//            public void onFail(HttpResponse response, Exception ex) {
//                String data = "购买失败，再来一次吧！";
//                atomicBoolean.set( false );
//                AlertManager.showCustomImageBtnDialog(getContext(), data, "再次发起购买", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        //再次购买
//                        exchangeVideoProduct(productId, singleVideoBean);
//                    }
//                }, null);
//            }
//        });
//    }

    private void goToWatch(SingleVideoBean item) {
        FamousVideoPayActivity.openFamousVideoPayActivity( mContext, item, mFamousProductBean.getProductId() );
    }

    /**
     * 获取名师精讲商品列表
     */
    private void queryFamousProductList() {
        new FamousTeacherModel().getFamousProductList(mStudentId, mSchoolId, new RequestListener<List<FamousProductBean>>() {

            @Override
            public void onSuccess(List<FamousProductBean> res) {
                if (res == null || res.size() == 0 || res.get(0) == null) {
                    mLoadingPager.showEmpty(R.string.no_product);
                    return;
                }
                mFamousProductBean = res.get(0);
                mAdatper = new FamousVideoAdapter(mContext, mList);
                mAdatper.setFreeUseTimes(mFamousProductBean.getFreeUseTimes());
                mAdatper.setPrice(mFamousProductBean.getPrice() + "学豆");
                mGvVideo.setAdapter(mAdatper);
                queryVideoList();
            }

            @Override
            public void onFail(HttpResponse<List<FamousProductBean>> response, Exception ex) {
                mLoadingPager.showFault(ex);
            }
        });
    }


    public void clearData() {
        mList.clear();
        if (mAdatper != null) {
            mAdatper.notifyDataSetChanged();
        }
    }

    //查询商品列表
    public void queryProductData() {
        if (isPrepared) {
            queryFamousProductList();
        }
    }

    //查询视频列表
    public void queryData() {
        if (isPrepared) {
            queryVideoList();
        }
    }


    /**
     * 查询视频列表
     */
    private void queryVideoList() {
        HashMap<String, String> params = new LinkedHashMap<>();
        params.put("productId", mFamousProductBean.getProductId());
        params.put("studentId", mStudentId);
        params.put("classId", mClassId);
        params.put("schoolId", mSchoolId);
        params.put("pageNum", pageNum + "");
        params.put("pageSize", pageSize + "");
        if (!clickExchanged && !TextUtils.isEmpty(mSearchedkey)) {
            params.put("content", mSearchedkey);
        }
        new FamousTeacherModel().getVideoList(params, new RequestListener<FamousVideoBean>() {

            @Override
            public void onSuccess(FamousVideoBean res) {
                //LogUtils.i("queryVideoList onSuccess");
                if (res == null || res.getItems() == null || res.getItems().size() == 0) {
                    mLoadingPager.showEmpty(R.string.no_video);
                    return;
                }
                mList.clear();
                mList.addAll(res.getItems());
                mAdatper.notifyDataSetChanged();
                if (res.getItems().size() < pageSize) {
                    mGvVideo.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                } else {
                    mGvVideo.setMode(PullToRefreshBase.Mode.BOTH);
                    pageNum++;
                }
                mLlSearch.setVisibility(View.VISIBLE);
                mLoadingPager.showTarget();
            }

            @Override
            public void onFail(HttpResponse<FamousVideoBean> response, Exception ex) {
                //LogUtils.i("queryVideoList onSuccess");
                mLoadingPager.showFault(ex);
            }
        });
    }

    /**
     * 查看已兑换视频列表
     */
    private void queryExchangedVideoList() {
        new FamousTeacherModel().getExchangedVideoList(mStudentId, mFamousProductBean.getProductId(), new RequestListener<List<SingleVideoBean>>() {

            @Override
            public void onSuccess(List<SingleVideoBean> res) {
                if (res == null || res.size() == 0) {
                    mLoadingPager.showEmpty(R.string.no_exchanged_video);
                    return;
                }
                mList.clear();
                mList.addAll(res);
                mAdatper.notifyDataSetChanged();
                mGvVideo.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                mLoadingPager.showTarget();
            }

            @Override
            public void onFail(HttpResponse<List<SingleVideoBean>> response, Exception ex) {
                mLoadingPager.showFault(ex);
            }
        });
    }


    //查询视频列表(刷新或加载更多)
    private void getVideos(final boolean refresh) {
        if (refresh) {
            pageNum = 1;
        }
        HashMap<String, String> params = new LinkedHashMap<>();
        params.put("productId", mFamousProductBean.getProductId());
        params.put("studentId", mStudentId);
        params.put("classId", mClassId);
        params.put("schoolId", mSchoolId);
        params.put("pageNum", pageNum + "");
        params.put("pageSize", pageSize + "");
        if (!clickExchanged && !TextUtils.isEmpty(mSearchedkey)) {
            params.put("content", mSearchedkey);
        }
        new FamousTeacherModel().getVideoList(params, new RequestListener<FamousVideoBean>() {

            @Override
            public void onSuccess(FamousVideoBean res) {
                if (res == null || res.getItems() == null || res.getItems().size() == 0) {
                    if (refresh) {
                        ToastUtils.showShort(mContext, R.string.server_error);
                    } else {
                        mGvVideo.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                    }
                    mGvVideo.onRefreshComplete();
                    return;
                }
                if (refresh) {
                    mList.clear();
                }
                mList.addAll(res.getItems());
                mAdatper.notifyDataSetChanged();
                mGvVideo.onRefreshComplete();
                if (res.getItems().size() < pageSize) {
                    mGvVideo.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                } else {
                    mGvVideo.setMode(PullToRefreshBase.Mode.BOTH);
                    pageNum++;
                }
            }

            @Override
            public void onFail(HttpResponse<FamousVideoBean> response, Exception ex) {
                mGvVideo.onRefreshComplete();
                AlertManager.showErrorInfo(getContext(), ex);
            }
        });
    }

    /**
     * 刷新已兑换视频列表
     */
    private void refreshExchangedVideoList() {
        new FamousTeacherModel().getExchangedVideoList(mStudentId, mFamousProductBean.getProductId(), new RequestListener<List<SingleVideoBean>>() {

            @Override
            public void onSuccess(List<SingleVideoBean> res) {
                if (res == null || res.size() == 0) {
                    ToastUtils.showShort(mContext, R.string.server_error);
                    return;
                }
                mList.clear();
                mList.addAll(res);
                mAdatper.notifyDataSetChanged();
                mGvVideo.onRefreshComplete();
                mGvVideo.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
            }

            @Override
            public void onFail(HttpResponse<List<SingleVideoBean>> response, Exception ex) {
                mGvVideo.onRefreshComplete();
                AlertManager.showErrorInfo(getContext(), ex);
            }
        });
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
        if (clickExchanged && mCbShowExchanged.isChecked()) {
            refreshExchangedVideoList();
        } else {
            getVideos(true);
        }
    }

    private void getMoreData() {
        getVideos(false);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_search:
                searchVideo();
                break;
            case R.id.iv_course_help:
                HelpUtil.showHelpActivity( getContext(), "同步微课使用说明", "Q003");
                break;
            default:
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receive(ChangeClassEvent event) {
        AppLog.d("receive event = "+ event );
        clearData();
        queryClassInfo();
        queryProductData();
    }

    public String getUmEventName() {
        return "learntask_vidoe";
    }
}
