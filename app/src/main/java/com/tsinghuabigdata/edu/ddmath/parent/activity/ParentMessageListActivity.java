package com.tsinghuabigdata.edu.ddmath.parent.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.tsinghuabigdata.edu.ddmath.MVPModel.MessageModel;
import com.tsinghuabigdata.edu.ddmath.MVPModel.RequestListener;
import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.activity.RoboActivity;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.constant.AppConst;
import com.tsinghuabigdata.edu.ddmath.module.message.bean.MessageInfo;
import com.tsinghuabigdata.edu.ddmath.parent.adapter.ParentMessageAdapter;
import com.tsinghuabigdata.edu.ddmath.parent.bean.ParentInfo;
import com.tsinghuabigdata.edu.ddmath.parent.event.ReadMsgEvent;
import com.tsinghuabigdata.edu.ddmath.parent.view.ParentToolbar;
import com.tsinghuabigdata.edu.ddmath.receive.JPushClientReceiver;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.MyViewUtils;
import com.tsinghuabigdata.edu.ddmath.view.LoadingPager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;

/**
 * 家长端--消息列表
 */

public class ParentMessageListActivity extends RoboActivity implements PullToRefreshBase.OnRefreshListener<ListView>, AdapterView.OnItemClickListener {

    @ViewInject(R.id.worktoolbar)
    private ParentToolbar workToolbar;
    @ViewInject(R.id.message_list)
    private PullToRefreshListView mListView;
    @ViewInject(R.id.loading_pager)
    private LoadingPager mLoadingPager;

    private Context mContext;

    private ParentMessageAdapter messageAdapter;
    private MessageModel messageModel;
    //private MessageInfo currMessageInfo;
    private int currPage = 1;

    private boolean bQuit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        transparent = true;
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_parent_message);
        x.view().inject( this );
        EventBus.getDefault().register(this);
        mContext = this;

        initView();
        loadData();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        bQuit = true;
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receive(ReadMsgEvent event) {
        // 收到本地消息后，更新用户信息
        int count = messageAdapter.getCount();
        for( int i=0; i<count; i++ ){
            MessageInfo info = messageAdapter.getItem(i);
            if( info!=null && info.getRowKey().equals( event.getMsgId()) ){
                info.setStatus( MessageInfo.S_READ );
                messageAdapter.notifyDataSetChanged();
                break;
            }
        }
    }

    @Override
    public String getUmEventName() {
        return "parent_mycenter_messagelist";
    }


    @Override
    public void onRefresh(PullToRefreshBase<ListView> pullToRefreshBase) {
        //下拉重新加载
        if (pullToRefreshBase.isHeaderShown()) {
            currPage = 1;
            loadData();
        }
        //上拉加载更多
        else if( pullToRefreshBase.isFooterShown() ){
            currPage++;
            loadData();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        position--;
        if( position >= messageAdapter.getCount() )
            return;

        //选中状态改变
        MessageInfo item = messageAdapter.getItem(position);
        //boolean status = messageAdapter.selectItem(position);
        if( item==null ) return;
        //消息内容
        Intent intent = new Intent( mContext, ParentMsgDetailActivity.class);
        intent.putExtra( AppConst.MSG_ROWKEY, item.getRowKey() );
        intent.putExtra( "isread", MessageInfo.S_READ.equals(item.getStatus()) );
        startActivity( intent );

    }

    //----------------------------------------------------------------------------------------
    private void initView(){

        workToolbar.setTitle( "我的消息" );
        workToolbar.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        View footerview = LayoutInflater.from(mContext).inflate(R.layout.view_footer_view, null);
        mListView.getRefreshableView().addFooterView(footerview);

        MyViewUtils.setPTRText( mContext, mListView);
        mListView.setOnRefreshListener(this);
        mListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);

        messageAdapter = new ParentMessageAdapter(mContext);
        mListView.setAdapter(messageAdapter);
        mListView.setOnItemClickListener(this);

        mLoadingPager.stopAnim();
        mLoadingPager.setTargetView(mListView);
        mLoadingPager.setListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLoadingPager.showLoading();
                loadData();
            }
        });

        messageModel = new MessageModel();
    }

    //加载信息
    private void loadData() {

        ParentInfo parentInfo = AccountUtils.getParentInfo();
        if (parentInfo == null) {
            return;
        }
        mLoadingPager.startAnim();
        if( currPage==1 ){
            mLoadingPager.showLoading();
        }

        messageModel.queryUserMsg( parentInfo.getParentId(), new RequestListener<ArrayList<MessageInfo>>() {
            @Override
            public void onSuccess(ArrayList<MessageInfo> list) {
                if (bQuit) return;
                mListView.onRefreshComplete();
                //JPushClientReceiver.sendNewMsgBrd(mContext, false);
                JPushClientReceiver.clearLocalNotifications();

                //没有数据
                if (list == null || list.size() == 0) {
                    mLoadingPager.showEmpty();
                    return;
                }

                mLoadingPager.showTarget();

                //处理消息类型
                for (MessageInfo tinfo : list) {
                    tinfo.initData();
                }

                //默认加载第一个信息
                if( currPage == 1 ){
                    messageAdapter.clear();
                }
                messageAdapter.addAll(list);
                messageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFail(HttpResponse response, Exception ex) {
                if (bQuit) return;
                mListView.onRefreshComplete();
                mLoadingPager.showFault(ex);
            }
        });
    }

}
