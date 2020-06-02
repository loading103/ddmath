package com.tsinghuabigdata.edu.ddmath.module.message;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.tsinghuabigdata.edu.ddmath.MVPModel.MessageModel;
import com.tsinghuabigdata.edu.ddmath.MVPModel.RequestListener;
import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.activity.RoboActivity;
import com.tsinghuabigdata.edu.ddmath.bean.LoginInfo;
import com.tsinghuabigdata.edu.ddmath.bean.UserDetailinfo;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.constant.AppConst;
import com.tsinghuabigdata.edu.ddmath.event.LookMsgDetailEvent;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.view.WorkToolbar;
import com.tsinghuabigdata.edu.ddmath.module.message.adapter.MessageAdapter;
import com.tsinghuabigdata.edu.ddmath.module.message.bean.MessageInfo;
import com.tsinghuabigdata.edu.ddmath.module.message.view.MsgDetailView;
import com.tsinghuabigdata.edu.ddmath.receive.JPushClientReceiver;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.MyViewUtils;
import com.tsinghuabigdata.edu.ddmath.util.ToastUtils;
import com.tsinghuabigdata.edu.ddmath.view.LoadingPager;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;

/**
 * 豆豆消息界面
 */
public class MessageActivity extends RoboActivity implements AdapterView.OnItemClickListener, PullToRefreshBase.OnRefreshListener {

    @ViewInject(R.id.work_toolbar)
    private WorkToolbar workToolbar;

    @ViewInject(R.id.loadingPager)
    private LoadingPager mLoadingPager;

    //页List
    @ViewInject(R.id.message_msglist)
    private PullToRefreshListView msgListView;

    //消息详情
    @ViewInject(R.id.message_detailview)
    private MsgDetailView msgDetailView;
    @ViewInject(R.id.detail_LoadingPager)
    private LoadingPager mLoadingPagerdetail;

    private Context mContext;
    //
    private MessageAdapter messageAdapter;

    private MessageModel messageModel;
    private MessageInfo currMessageInfo;

    private boolean bQuit = false;

    //private int pageNum = 1;

    private String rowKey;      //消息ID，

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        transparent = true;
        super.onCreate(savedInstanceState);

        setContentView( GlobalData.isPad() ? R.layout.activity_ddwork_message : R.layout.activity_ddwork_message_mobile );

        x.view().inject(this);
        mContext = this;

        parseIntent();

        initView();
        loadData();

        MobclickAgent.onEvent( this, "message_center" );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bQuit = true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        position--;

        if( position >= messageAdapter.getCount() )
            return;

        //选中状态改变
        MessageInfo item = messageAdapter.getItem(position);
        boolean status = messageAdapter.selectItem(position);

        if( item == null ) return;

        //消息内容
        if( GlobalData.isPad() ){
            if (status) showMsgData( item );
        }else{
            Intent intent = new Intent( mContext, MessageDetailActivity.class);
            intent.putExtra( AppConst.MSG_ROWKEY, item.getRowKey() );
            startActivity( intent );
        }
    }

    @Override
    public void onRefresh(PullToRefreshBase pullToRefreshBase) {
        if (pullToRefreshBase.isHeaderShown()) {
            refreshData();
        } else {
            getMoreData();
        }
    }

    //----------------------------------------------------------------------
    private void parseIntent() {
        Intent intent = new Intent();
        if (intent.hasExtra(AppConst.MSG_ROWKEY)) {
            rowKey = intent.getStringExtra(AppConst.MSG_ROWKEY);
        }
    }

    private void initView() {

        workToolbar.setTitle("消息中心");
        workToolbar.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        },null);

        msgDetailView.setWorkToolbar(workToolbar);

        messageAdapter = new MessageAdapter(mContext);
        msgListView.setAdapter(messageAdapter);
        msgListView.setOnItemClickListener(this);

        //
        View footerview = LayoutInflater.from(mContext).inflate(R.layout.view_footer_view, null);
        msgListView.getRefreshableView().addFooterView(footerview);

        MyViewUtils.setPTRText(mContext, msgListView);
        msgListView.setOnRefreshListener(this);
        msgListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);        //仅仅下拉刷新

        mLoadingPager.setWhite();
        mLoadingPager.setListener(new View.OnClickListener() {           //失败点击重新加载
            @Override
            public void onClick(View v) {
                loadData();
            }
        });
        mLoadingPagerdetail.setListener(new View.OnClickListener() {           //失败点击重新加载
            @Override
            public void onClick(View v) {
                showMsgData(currMessageInfo);
            }
        });
        mLoadingPagerdetail.hideall();
        mLoadingPagerdetail.setWhite();
        messageModel = new MessageModel();
    }

    private void showMsgData(MessageInfo msgInfo) {
        if (msgInfo == null) return;
        currMessageInfo = msgInfo;
        //加载详情
        UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
        if (detailinfo == null) {
            ToastUtils.show(mContext, "请登录", Toast.LENGTH_SHORT);
            return;
        }

        mLoadingPagerdetail.showLoading();
        messageModel.queryMsgDetail(detailinfo.getStudentId(), msgInfo.getRowKey(), new RequestListener<MessageInfo>() {
            @Override
            public void onSuccess(MessageInfo res) {

                if (bQuit) return;
                //没有数据
                if (res == null) {
                    mLoadingPagerdetail.showEmpty();
                    return;
                }
                //发出消息，通知查看消息了
                EventBus.getDefault().post(new LookMsgDetailEvent(res.getRowKey()));
                mLoadingPagerdetail.hideall();
                msgDetailView.setMessageInfo(res);
            }

            @Override
            public void onFail(HttpResponse response, Exception ex) {
                if (bQuit) return;
                mLoadingPagerdetail.showFault(ex);
            }
        });
    }

    //加载信息
    private void loadData(){
        loadData( true );
    }
    private void loadData( boolean showLoading ) {

        LoginInfo loginInfo = AccountUtils.getLoginUser();
        UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
        if (loginInfo == null || detailinfo == null) {
            ToastUtils.show(mContext, "请登录", Toast.LENGTH_SHORT);
            mLoadingPager.showEmpty();
            return;
        }
        String studentId = detailinfo.getStudentId();

        if( showLoading )
            mLoadingPager.showLoading();
        messageModel.queryUserMsg(studentId, new RequestListener() {
            @Override
            public void onSuccess(Object res) {
                if (bQuit) return;
                msgListView.onRefreshComplete();
                JPushClientReceiver.sendNewMsgBrd(mContext, false);
                JPushClientReceiver.clearLocalNotifications();

                ArrayList<MessageInfo> list = (ArrayList<MessageInfo>) res;

                //没有数据
                if (list == null || list.size() == 0) {
                    mLoadingPager.showEmpty();
                    return;
                }

                mLoadingPager.hideall();

                MessageInfo info = list.get(0);         //默认第一个
                if (!TextUtils.isEmpty(rowKey)) {       //消息跳转到指定的
                    for (MessageInfo tinfo : list) {
                        if (rowKey.equals(tinfo.getRowKey())) {
                            info = tinfo;
                        }
                    }
                }
                if( GlobalData.isPad() ){
                    info.setSelect(true);
                    if (MessageInfo.S_UNREAD.equals(info.getStatus())) {
                        info.setStatus(MessageInfo.S_READ);
                    }
                }

                //处理消息类型
                for (MessageInfo tinfo : list) {
                    tinfo.initData();
                }

                //默认加载第一个信息
                //if( pageNum == 1 ){
                if( GlobalData.isPad() )
                    showMsgData(info);
                messageAdapter.clear();
                //}
                messageAdapter.addAll(list);
                messageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFail(HttpResponse response, Exception ex) {
                if (bQuit) return;
                msgListView.onRefreshComplete();
                mLoadingPager.showFault(ex);
            }
        });
    }

    //加载更多
    private void getMoreData() {
        //pageNum++;
        loadData();
    }

    //下拉刷新加载更新
    private void refreshData() {
        //pageNum = 1;
        loadData( false );
    }

}
