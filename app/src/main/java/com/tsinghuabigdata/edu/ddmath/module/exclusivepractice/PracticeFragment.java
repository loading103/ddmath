package com.tsinghuabigdata.edu.ddmath.module.exclusivepractice;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.tsinghuabigdata.edu.ddmath.MVPModel.PracticeModel;
import com.tsinghuabigdata.edu.ddmath.MVPModel.RequestListener;
import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.bean.LoginInfo;
import com.tsinghuabigdata.edu.ddmath.bean.MyTutorClassInfo;
import com.tsinghuabigdata.edu.ddmath.bean.UserDetailinfo;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.event.ChangeClassEvent;
import com.tsinghuabigdata.edu.ddmath.event.RefreshPracticeEvent;
import com.tsinghuabigdata.edu.ddmath.fragment.MyBaseFragment;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.DDWorkManager;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.WorkCommitListener;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.WorkSubmitBean;
import com.tsinghuabigdata.edu.ddmath.module.exclusivepractice.adapter.ExclusivePracticeAdapter;
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

import java.util.List;


/**
 * 专属套题   精品套题
 * Created by cuibo 2017.12.19
 */
@Deprecated
public class PracticeFragment extends MyBaseFragment implements WorkCommitListener,PullToRefreshBase.OnRefreshListener<ListView> {

    public static final int TYPE_EXCLUSIVE      = 1;    //专属套题
    public static final int TYPE_CLASSIC    = 2;    //精品套题

    //加载中
    private LoadingPager mLoadingPager;

    //private TextView introView;
    //private LinearLayout searchLayout;
    private SearchEditView searchEditView;

    //列表
    private PullToRefreshListView mListView;

    //private String studentId;

    private PracticeModel practiceModel = new PracticeModel();

    private ExclusivePracticeAdapter mAdapter;

    private boolean bLoadData = false;

    private int currPage = 1;       //当前加载页码
    private int practiceType = TYPE_EXCLUSIVE;

    private DDWorkManager mDDWorkManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root;
        if (GlobalData.isPad()) {
            root = inflater.inflate(R.layout.fragment_practice_list, container, false);
        } else {
            root = inflater.inflate(R.layout.fragment_practice_list_phone, container, false);
        }

        initView( root );
        setPrepared();
        return root;
    }

    @Override
    protected void lazyLoad() {
        currPage = 1;
        bLoadData = true;
        mDDWorkManager = DDWorkManager.getDDWorkManager();
        if( mDDWorkManager!=null ){
            mDDWorkManager.addCommitListener(this);
        }
        loadData();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        AppLog.d("fsdfssdfdfdfdgass  onAttach");
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if( mDDWorkManager!=null ){
            mDDWorkManager.removeCommitListener(this);
        }
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
    public void workStatus(String examId, int status) {}

    @Override
    public void pageStatus(String examId, int status) {
    }

    @Override
    public void onSuccess(String examId, final WorkSubmitBean workSubmitBean) {

        //更新
        if( bLoadData ){
            currPage = 1;
            loadData( false );
        }
    }

    @Override
    public void onFail(String examId, Exception ex) {
    }

    public void setPracticeType( int type ){
        practiceType = type;
    }
    //---------------------------------------------------------------------------------------------
    private void initView(View root) {

        mListView = (PullToRefreshListView)root.findViewById( R.id.fragment_practice_list);
        MyViewUtils.setPTRText( getContext(), mListView);
        mListView.setOnRefreshListener(this);
        mListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);        //以后还是要支持上下拉刷新 ,分页机制功能

        mLoadingPager = (LoadingPager) root.findViewById( R.id.loading_pager );

        mLoadingPager.setTargetView(mListView);
        mLoadingPager.stopAnim();
        mLoadingPager.setListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLoadingPager.showLoading();
                loadData();
            }
        });

        mAdapter = new ExclusivePracticeAdapter( getActivity() );
        mAdapter.setPracticeType( practiceType );
        mListView.setAdapter(mAdapter);

        //是否需要刷新
        EventBus.getDefault().register(this);

        //头布局
        View headerView = getActivity().getLayoutInflater().inflate( GlobalData.isPad()?R.layout.view_practice_header:R.layout.view_practice_header_phone, null);
        mListView.getRefreshableView().addHeaderView( headerView );

        TextView introView = (TextView)headerView.findViewById( R.id.fragment_practice_introView );
        LinearLayout searchLayout = (LinearLayout)headerView.findViewById( R.id.fragment_practice_searchlayout );
        searchEditView= (SearchEditView)headerView.findViewById( R.id.fragment_practice_searchView );
        if( practiceType == TYPE_CLASSIC ){     //精品套题
            introView.setVisibility( View.GONE );
            searchLayout.setVisibility( View.VISIBLE );
            searchEditView.setSearchListener(new SearchEditView.SearchListener() {
                @Override
                public void search() {
                    currPage = 1;
                    loadData();
                }
            });
        }else {
            introView.setVisibility( View.VISIBLE );
            searchLayout.setVisibility( View.GONE );
        }

    }
    private void loadData(){
        mLoadingPager.startAnim();
        loadData( true );
    }
    private void loadData( boolean showLoading ) {
        LoginInfo loginInfo = AccountUtils.getLoginUser();
        UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
        MyTutorClassInfo classInfo = AccountUtils.getCurrentClassInfo();
        if (loginInfo == null || detailinfo == null || classInfo == null) {
            ToastUtils.show( getContext(), R.string.relogin );
            return;
        }
        String studentId = detailinfo.getStudentId();

        if( currPage == 1 && showLoading ){
            mLoadingPager.showLoading();
            //mListView.setMode(PullToRefreshBase.Mode.BOTH);
        }

        if( practiceType == TYPE_CLASSIC ){
            practiceModel.getClassicPracticeList( studentId, searchEditView.getSearchKeyword(), classInfo.getSchoolId(), requestListener );
        }else{
            //practiceModel.getPracticeList( studentId, requestListener );
        }
    }

    private RequestListener requestListener = new RequestListener<List<PracticeProductBean>>() {

        @Override
        public void onSuccess(List<PracticeProductBean> list) {
            mListView.onRefreshComplete();

            if( list == null || list.size()==0 ){
                mLoadingPager.showEmpty();
                return;
            }
            mLoadingPager.showTarget();

            mAdapter.clear();
            mAdapter.addAll( list );
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onFail(HttpResponse<List<PracticeProductBean>> response, Exception ex) {
            mListView.onRefreshComplete();
            mLoadingPager.showFault(ex);
        }
    };

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receive(RefreshPracticeEvent event) {
        AppLog.d(" refresh PracticeEvent " + event );
        loadData( false );
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receive(ChangeClassEvent event) {
        AppLog.d("receive event = "+ event );
        if( TYPE_CLASSIC == practiceType ){
            loadData( false );
        }
    }


    public String getUmEventName() {
        return practiceType==TYPE_EXCLUSIVE?"learntask_exclusive":"learntask_fine";
    }
}
