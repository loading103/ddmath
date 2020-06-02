package com.tsinghuabigdata.edu.ddmath.module.errorbook.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.tsinghuabigdata.edu.ddmath.MVPModel.PracticeModel;
import com.tsinghuabigdata.edu.ddmath.MVPModel.RequestListener;
import com.tsinghuabigdata.edu.ddmath.MVPModel.UserCenterModel;
import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.activity.MainActivity;
import com.tsinghuabigdata.edu.ddmath.bean.LoginInfo;
import com.tsinghuabigdata.edu.ddmath.bean.MyTutorClassInfo;
import com.tsinghuabigdata.edu.ddmath.bean.StudyBean;
import com.tsinghuabigdata.edu.ddmath.bean.UserDetailinfo;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.event.BuyPracticeEvent;
import com.tsinghuabigdata.edu.ddmath.event.ChangeClassEvent;
import com.tsinghuabigdata.edu.ddmath.event.JumpEvent;
import com.tsinghuabigdata.edu.ddmath.event.RefreshPracticeEvent;
import com.tsinghuabigdata.edu.ddmath.event.SyncShowStudybeanEvent;
import com.tsinghuabigdata.edu.ddmath.fragment.BasePayFragment;
import com.tsinghuabigdata.edu.ddmath.fragment.MyReportFragment;
import com.tsinghuabigdata.edu.ddmath.module.errorbook.HelpUtil;
import com.tsinghuabigdata.edu.ddmath.module.errorbook.adapter.ClassicPracticeAdapter;
import com.tsinghuabigdata.edu.ddmath.module.exclusivepractice.bean.PracticeBean;
import com.tsinghuabigdata.edu.ddmath.module.exclusivepractice.bean.PracticeProductBean;
import com.tsinghuabigdata.edu.ddmath.module.exclusivepractice.view.SearchEditView;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.AppLog;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.MyViewUtils;
import com.tsinghuabigdata.edu.ddmath.util.ToastUtils;
import com.tsinghuabigdata.edu.ddmath.view.LoadingPager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;


/**
 *  精品套题
 * Created by cuibo 2018.08.08
 */

public class ClassicPracticeFragment extends BasePayFragment implements PullToRefreshBase.OnRefreshListener<ListView>, View.OnClickListener {

    //加载中
    private LoadingPager mLoadingPager;

    private SearchEditView searchEditView;
    //private ImageView mEbookHelp;
    //列表
    private PullToRefreshListView mListView;

    private PracticeModel practiceModel = new PracticeModel();

    private ClassicPracticeAdapter mAdapter;

    private int currPage = 1;       //当前加载页码

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root;
        if (GlobalData.isPad()) {
            root = inflater.inflate(R.layout.fragment_classicpractice, container, false);
        } else {
            root = inflater.inflate(R.layout.fragment_classicpractice_phone, container, false);
        }

        initView( root );
        setPrepared();
        return root;
    }

    @Override
    protected void lazyLoad() {
        currPage = 1;
        loadData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    //清除数据
    public void clearData() {
        if (mAdapter != null ) {
            mAdapter.clear();
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onRefresh(PullToRefreshBase<ListView> pullToRefreshBase) {
        if (pullToRefreshBase.isHeaderShown()) {        //下拉刷新
            currPage = 1;
        } else if( pullToRefreshBase.isFooterShown() ){     //上拉加载更多
            currPage++;
        }
        loadData();
    }


    @Override
    public void onClick(View v) {
        if( v.getId() == R.id.ebook_report ){
            //跳转到培优报告
            EventBus.getDefault().post(new JumpEvent(MainActivity.FRAGMENT_STUDY_CONDITION, MyReportFragment.WEEK_EXTRACT_I));
        }else if(v.getId() == R.id.ebook_help){
            HelpUtil.showHelpActivity( getContext(), "精品套题使用说明", "Q007");
        }
    }

    //触发学豆更新
    protected void updateLearnDouCount() {
        UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
        if( detailinfo == null ) return;
        new UserCenterModel().queryMyStudyBean( detailinfo.getStudentId(), new RequestListener<StudyBean>() {
            @Override
            public void onSuccess(StudyBean res) {
                if (res == null) {
                    return;
                }
                EventBus.getDefault().post(new SyncShowStudybeanEvent(res.getTotalDdAmt()));

                //刷新套题列表
                currPage = 1;
                loadData( false );
            }

            @Override
            public void onFail(HttpResponse<StudyBean> response, Exception ex) {
            }
        });
    }
    //---------------------------------------------------------------------------------------------
    private void initView(View root) {

        mListView = root.findViewById( R.id.ebook_classic_practice_list);
        MyViewUtils.setPTRText( getContext(), mListView);
        mListView.setOnRefreshListener(this);
        mListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);        //以后还是要支持上下拉刷新 ,分页机制功能
        ImageView mEbookHelp=root.findViewById(R.id.ebook_help);
        mEbookHelp.setOnClickListener(this);
        mLoadingPager = root.findViewById( R.id.loading_pager );

        mLoadingPager.setTargetView(mListView);
        mLoadingPager.stopAnim();
        mLoadingPager.setListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLoadingPager.showLoading();
                loadData();
            }
        });

        mAdapter = new ClassicPracticeAdapter( getActivity(), this );
        mListView.setAdapter(mAdapter);

        //是否需要刷新
        EventBus.getDefault().register(this);

        searchEditView= root.findViewById( R.id.fragment_practice_searchView );
        searchEditView.setSearchListener(new SearchEditView.SearchListener() {
            @Override
            public void search() {
                currPage = 1;
                loadData();
            }
        });

        ImageView imageView = root.findViewById( R.id.ebook_report );
        imageView.setOnClickListener( this );

    }
    private void loadData(){
        mLoadingPager.startAnim();
        loadData( true );
    }
    private void loadData( boolean showLoading ) {
        LoginInfo loginInfo = AccountUtils.getLoginUser();
        UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
        MyTutorClassInfo classInfo = AccountUtils.getCurrentClassInfo();
        if (loginInfo == null || detailinfo == null ) {
            ToastUtils.show( getContext(), R.string.relogin );
            mLoadingPager.showEmpty();
            return;
        }
        if( classInfo == null ){
            ToastUtils.show( getContext(), "请添加班级，完善信息！" );
            mLoadingPager.showEmpty();
            return;
        }
        String studentId = detailinfo.getStudentId();

        if( currPage == 1 && showLoading ){
            mLoadingPager.showLoading();
            //mListView.setMode(PullToRefreshBase.Mode.BOTH);
        }

        practiceModel.getClassicPracticeList( studentId, searchEditView.getSearchKeyword(), classInfo.getSchoolId(), requestListener );

    }

    private RequestListener requestListener = new RequestListener<List<PracticeProductBean>>() {

        @Override
        public void onSuccess(List<PracticeProductBean> list) {
            if( !isAdded() || isDetached() ) return;
            mListView.onRefreshComplete();

            if( list == null || list.size()==0 ){
                mLoadingPager.showEmpty();
                mLoadingPager.setEmptyText("没有找到相关套题");
                return;
            }
            mLoadingPager.showTarget();

            //改变结构
            ArrayList<PracticeBean> practiceList = new ArrayList<>();
            for( PracticeProductBean bean : list ){
                for( PracticeBean practiceBean : bean.getProductList() ){
                    practiceBean.setPaperType( bean.getProductType() );
                    practiceBean.setProductBean( bean );
                    practiceList.add( practiceBean );
                }
            }

            mAdapter.clear();
            mAdapter.addAll( practiceList );
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onFail(HttpResponse<List<PracticeProductBean>> response, Exception ex) {
            if( isDetached() ) return;
            mListView.onRefreshComplete();
            mLoadingPager.showFault(ex);
        }
    };

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receive(RefreshPracticeEvent event) {
        AppLog.d(" refresh PracticeEvent " + event );
        currPage = 1;
        loadData( false );
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receive(ChangeClassEvent event) {
        AppLog.d(" refresh ChangeClassEvent " + event );
        currPage = 1;
        loadData( false );
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receive(BuyPracticeEvent event) {
        String practiceId = event.getPracticeId();
        AppLog.d(" refresh BuyPracticeEvent " + event + " ,,, practiceId = " + practiceId);
        if(!TextUtils.isEmpty(practiceId)){
            for( int i=0; i<mAdapter.getCount(); i++ ){

                PracticeBean practiceBean = mAdapter.getItem(i);
                if( practiceBean!=null && practiceId.equals( practiceBean.getExcluId() ) ){
                    mAdapter.notifyDataSetChanged();
                    mAdapter.gotoDetailActivity( practiceBean );
                }
            }
        }
    }



    public String getUmEventName() {
        return "learntask_fine";
    }

}
