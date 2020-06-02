package com.tsinghuabigdata.edu.ddmath.parent.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.tsinghuabigdata.edu.ddmath.MVPModel.MessageModel;
import com.tsinghuabigdata.edu.ddmath.MVPModel.RequestListener;
import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.activity.RoboActivity;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.constant.AppConst;
import com.tsinghuabigdata.edu.ddmath.event.LookMsgDetailEvent;
import com.tsinghuabigdata.edu.ddmath.module.message.bean.MessageInfo;
import com.tsinghuabigdata.edu.ddmath.module.message.view.MsgDetailView;
import com.tsinghuabigdata.edu.ddmath.parent.bean.ParentInfo;
import com.tsinghuabigdata.edu.ddmath.parent.event.ParentCenterEvent;
import com.tsinghuabigdata.edu.ddmath.parent.event.ReadMsgEvent;
import com.tsinghuabigdata.edu.ddmath.parent.view.ParentToolbar;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.ToastUtils;
import com.tsinghuabigdata.edu.ddmath.view.LoadingPager;

import org.greenrobot.eventbus.EventBus;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * 豆豆消息详情界面---仅家长端使用
 */
public class ParentMsgDetailActivity extends RoboActivity {

    @ViewInject(R.id.worktoolbar)
    private ParentToolbar workToolbar;

    //消息详情
    @ViewInject(R.id.message_detailview)
    private MsgDetailView msgDetailView;
    @ViewInject(R.id.loading_pager)
    private LoadingPager mLoadingPager;

    private Context mContext;

    private boolean bQuit = false;
    private String rowKey;      //消息ID，
    private boolean isRead;     //消息是否已读

    private MessageModel messageModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        transparent = true;
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_parent_detailmessage);
        x.view().inject( this );

        mContext = this;

        if( !parseIntent() ){
            ToastUtils.showShort( mContext, "参数错误");
            finish();
            return;
        }

        initView();
        showMsgData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bQuit = true;
    }

    @Override
    public String getUmEventName() {
        return "parent_mycenter_msgdetail";
    }

    //----------------------------------------------------------------------
    private boolean parseIntent() {
        Intent intent = getIntent();
        if (intent.hasExtra(AppConst.MSG_ROWKEY)) {
            rowKey = intent.getStringExtra(AppConst.MSG_ROWKEY);
        }
        isRead = intent.getBooleanExtra( "isread", true );
        return !TextUtils.isEmpty(rowKey);
    }

    private void initView(){

        workToolbar.setTitle("消息详情");
        workToolbar.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mLoadingPager.stopAnim();
        mLoadingPager.setTargetView(msgDetailView);
        mLoadingPager.setListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLoadingPager.showLoading();
                showMsgData();
            }
        });

        messageModel = new MessageModel();
    }

    private void showMsgData() {
        if ( TextUtils.isEmpty(rowKey)) return;

        //加载详情
        ParentInfo parentInfo = AccountUtils.getParentInfo();
        if (parentInfo == null) {
            ToastUtils.show(mContext, "请登录", Toast.LENGTH_SHORT);
            return;
        }

        mLoadingPager.showLoading();
        messageModel.queryMsgDetail(parentInfo.getParentId(), rowKey, new RequestListener<MessageInfo>() {
            @Override
            public void onSuccess(MessageInfo res) {

                if (bQuit) return;

                //没有数据
                if (res == null) {
                    mLoadingPager.showEmpty();
                    return;
                }

                mLoadingPager.showTarget();
                msgDetailView.setMessageInfo(res);

                //发出消息，通知查看消息了
                EventBus.getDefault().post(new LookMsgDetailEvent(res.getRowKey()));

                if( !isRead ){
                    EventBus.getDefault().post(new ParentCenterEvent(ParentCenterEvent.TYPE_MSG_DEC));
                    EventBus.getDefault().post( new ReadMsgEvent(rowKey) );
                }
            }

            @Override
            public void onFail(HttpResponse response, Exception ex) {
                if (bQuit) return;
                mLoadingPager.showFault(ex);
            }
        });
    }
}
