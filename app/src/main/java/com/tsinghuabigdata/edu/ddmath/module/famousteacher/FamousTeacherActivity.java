package com.tsinghuabigdata.edu.ddmath.module.famousteacher;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;

import com.tsinghuabigdata.edu.ddmath.MVPModel.FamousTeacherModel;
import com.tsinghuabigdata.edu.ddmath.MVPModel.RequestListener;
import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.activity.RoboActivity;
import com.tsinghuabigdata.edu.ddmath.bean.LoginInfo;
import com.tsinghuabigdata.edu.ddmath.bean.MyTutorClassInfo;
import com.tsinghuabigdata.edu.ddmath.bean.UserDetailinfo;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.event.JumpStudyBeanEvent;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.view.WorkToolbar;
import com.tsinghuabigdata.edu.ddmath.module.famousteacher.adapter.FamousVideoAdapter;
import com.tsinghuabigdata.edu.ddmath.module.famousteacher.bean.FamousProductBean;
import com.tsinghuabigdata.edu.ddmath.module.famousteacher.bean.SingleVideoBean;
import com.tsinghuabigdata.edu.ddmath.module.product.ProductUtil;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.AlertManager;
import com.tsinghuabigdata.edu.ddmath.util.AppLog;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.ToastUtils;
import com.tsinghuabigdata.edu.ddmath.view.GridViewWithHeaderAndFooter;
import com.tsinghuabigdata.edu.ddmath.view.LoadingPager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 名师精讲
 * Created by Administrator on 2018/2/8.
 */

public class FamousTeacherActivity extends RoboActivity {

    public static final String KNOWLEDGEID = "knowledgeId";

    //private WorkToolbar                 mWorktoolbar;
    private GridViewWithHeaderAndFooter mGvVideo;
    private LoadingPager                mLoadingPager;
    private Context                     mContext;

    private String mStudentId = "";
    private String mClassId   = "";
    private String mSchoolId  = "";
    private String mKnowledgeIds;

    private FamousProductBean mFamousProductBean;
    private List<SingleVideoBean> mList = new ArrayList<>();
    private FamousVideoAdapter mAdatper;

    private AtomicBoolean atomicBoolean = new AtomicBoolean();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        transparent = true;
        super.onCreate(savedInstanceState);

        //设置全屏显示
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (GlobalData.isPad()) {
            setContentView(R.layout.activity_famous_teacher);
        } else {
            setContentView(R.layout.activity_famous_teacher_phone);
        }
        initViews();
        initData();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void initViews() {
        mContext = this;
        WorkToolbar mWorktoolbar = findViewById(R.id.worktoolbar);
        mGvVideo = findViewById(R.id.gv_video);
        mLoadingPager = findViewById(R.id.loadingPager_video);

        String title = "同步微课";
        mWorktoolbar.setTitle(title);
        mWorktoolbar.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        }, null);

        mLoadingPager.setTargetView(mGvVideo);
        mLoadingPager.setListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLoadingPager.showLoading();
                if (mFamousProductBean == null) {
                    queryFamousProductList();
                } else {
                    queryVideoList();
                }
            }
        });

        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View headerView = layoutInflater.inflate(R.layout.header_view_famous_teacher, null);
        View footerView = layoutInflater.inflate(R.layout.header_view_famous_teacher, null);
        mGvVideo.addHeaderView(headerView);
        mGvVideo.addFooterView(footerView);
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

    private void initData() {
        mKnowledgeIds = getIntent().getStringExtra(KNOWLEDGEID);
        if (TextUtils.isEmpty(mKnowledgeIds)) {
            ToastUtils.showShort(mContext, "知识点参数错误");
            finish();
        }
        UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
        if ( detailinfo != null) {
            mStudentId = detailinfo.getStudentId();
        }
        MyTutorClassInfo classInfo = AccountUtils.getCurrentClassInfo();
        if ( classInfo != null) {
            mSchoolId = classInfo.getSchoolId();
            mClassId = classInfo.getClassId();
        }
        queryFamousProductList();
        EventBus.getDefault().register(this);
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

    /**
     * 查询视频列表
     */
    private void queryVideoList() {
        HashMap<String, String> params = new LinkedHashMap<>();
        params.put("knowledgeIds", mKnowledgeIds);
        params.put("studentId", mStudentId);
        params.put("classId", mClassId);
        params.put("schoolId", mSchoolId);
        params.put("productId", mFamousProductBean.getProductId());
        new FamousTeacherModel().getVideoListByKnowledge(params, new RequestListener<List<SingleVideoBean>>() {

            @Override
            public void onSuccess(List<SingleVideoBean> res) {
                if (res == null || res.size() == 0) {
                    mLoadingPager.showEmpty(R.string.no_knowledge_video);
                    return;
                }
                mList.clear();
                mList.addAll(res);
                mAdatper.notifyDataSetChanged();
                mLoadingPager.showTarget();
            }

            @Override
            public void onFail(HttpResponse<List<SingleVideoBean>> response, Exception ex) {
                mLoadingPager.showFault(ex);
            }
        });


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
            getProductUseTimes(item);
        }
    }

    /**
     * 获取名师精讲商品剩余免费兑换次数
     */
    private void getProductUseTimes(final SingleVideoBean item) {
        if (atomicBoolean.get()) {
            ToastUtils.show(mContext, "请稍等...");
            return;
        }
        atomicBoolean.set(true);

        ProductUtil.videoCheckPermissionAndExchange(mContext, item.getVideoId(), atomicBoolean, ProductUtil.FROM_VIDEO, new ProductUtil.ProductCallBack() {
            @Override
            public void onSuccess() {
                atomicBoolean.set(false);
                //成功 状态改变
                item.setStatus(SingleVideoBean.Exchanged);
                mAdatper.notifyDataSetChanged();

                //更新学豆

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
    }
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
//                showExchangeDialog(item,freeCount, atomicBoolean);
//            }
//
//            @Override
//            public void onFail(HttpResponse<List<FamousProductBean>> response, Exception ex) {
//                ToastUtils.showShort(mContext,R.string.query_times_failure);
//                atomicBoolean.set( false );
//            }
//        });
//    }

//    private void showExchangeDialog(final SingleVideoBean item, int freeCount, final AtomicBoolean atomicBoolean) {
//
//        ProductUtil.showExchangePracticeDialog(mContext, true, AppConst.PRIVILEGE_SYNC_CLASSROOM, freeCount, "同步微课", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                //点击了确认， 开始兑换
//                exchangeVideoProduct(mFamousProductBean.getProductId(), item, atomicBoolean);
//            }
//        }, null, atomicBoolean );    //取消处理， 和多次点击处理
//    }

    //兑换套题商品,不区分用免费使用权 还是 学豆，服务器后台处理
//    private void exchangeVideoProduct(final String productId, final SingleVideoBean singleVideoBean, final AtomicBoolean atomicBoolean) {
//
//        //开始兑换
//        new ProductModel().exchangePracticeProduct(mStudentId, mClassId, productId, "", singleVideoBean.getVideoId(), new RequestListener() {
//            @Override
//            public void onSuccess(Object res) {
//
//                atomicBoolean.set( false );
//                //成功 状态改变
//                singleVideoBean.setStatus(SingleVideoBean.Exchanged);
//                mAdatper.notifyDataSetChanged();
//
//                //更新学豆
//
//                //提示用户
//                String data = String.format(Locale.getDefault(), "可以去观看“%s”啦！", FamousVideoAdapter.getName(singleVideoBean));
//                AlertManager.showCustomImageBtnDialog(mContext, data, "开始观看", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        goToWatch(singleVideoBean);
//                    }
//                }, null);
//            }
//
//            @Override
//            public void onFail(HttpResponse response, Exception ex) {
//                if( isDestroyed() || isFinishing() ) return;
//                String data = "购买失败，再来一次吧！";
//                AlertManager.showCustomImageBtnDialog(mContext, data, "再次发起购买", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        //再次购买
//                        exchangeVideoProduct(productId, singleVideoBean, atomicBoolean);
//                    }
//                }, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        atomicBoolean.set( false );
//                    }
//                });
//            }
//        });
//    }

    private void goToWatch(SingleVideoBean item) {
        FamousVideoPayActivity.openFamousVideoPayActivity( mContext, item, mFamousProductBean.getProductId() );
    }

    @Subscribe
    public void receive(JumpStudyBeanEvent event) {
        AppLog.d("event = " + event );
        finish();
    }


}
