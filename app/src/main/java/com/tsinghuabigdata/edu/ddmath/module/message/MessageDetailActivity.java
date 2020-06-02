package com.tsinghuabigdata.edu.ddmath.module.message;

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
import com.tsinghuabigdata.edu.ddmath.bean.UserDetailinfo;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.constant.AppConst;
import com.tsinghuabigdata.edu.ddmath.event.LookMsgDetailEvent;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.view.WorkToolbar;
import com.tsinghuabigdata.edu.ddmath.module.message.bean.MessageInfo;
import com.tsinghuabigdata.edu.ddmath.module.message.view.MsgDetailView;
import com.tsinghuabigdata.edu.ddmath.parent.bean.ParentInfo;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.ToastUtils;
import com.tsinghuabigdata.edu.ddmath.view.LoadingPager;

import org.greenrobot.eventbus.EventBus;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * 豆豆消息详情界面---仅手机版使用
 */
public class MessageDetailActivity extends RoboActivity {

    @ViewInject(R.id.work_toolbar)
    private WorkToolbar workToolbar;
    //消息详情
    @ViewInject(R.id.message_detailview)
    private MsgDetailView msgDetailView;
    @ViewInject(R.id.loadingPager)
    private LoadingPager  mLoadingPager;

    private Context mContext;

    private boolean bQuit = false;
    private String rowKey;      //消息ID，

    private MessageModel messageModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView( R.layout.activity_ddwork_messagedetail_mobile );

        x.view().inject(this);
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
    //----------------------------------------------------------------------
    private boolean parseIntent() {
        Intent intent = getIntent();
        if (intent.hasExtra(AppConst.MSG_ROWKEY)) {
            rowKey = intent.getStringExtra(AppConst.MSG_ROWKEY);
        }
        return !TextUtils.isEmpty(rowKey);
    }

    private void initView(){
        workToolbar.setTitle("消息详情");
        workToolbar.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        },null);

        mLoadingPager.setListener(new View.OnClickListener() {           //失败点击重新加载
            @Override
            public void onClick(View v) {
                showMsgData();
            }
        });

        msgDetailView.setWorkToolbar(workToolbar);
        messageModel = new MessageModel();
    }

    private void showMsgData() {
        if ( TextUtils.isEmpty(rowKey)) return;

        //加载详情
        UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
        ParentInfo parentInfo = AccountUtils.getParentInfo();
        String userId;
        if( parentInfo != null ) userId = parentInfo.getParentId();
        else if (detailinfo != null) userId = detailinfo.getStudentId();
        else {
            ToastUtils.show(mContext, "请登录", Toast.LENGTH_SHORT);
            return;
        }

        mLoadingPager.showLoading();
        messageModel.queryMsgDetail(userId, rowKey, new RequestListener<MessageInfo>() {
            @Override
            public void onSuccess(MessageInfo res) {

                if (bQuit) return;

                //没有数据
                if (res == null) {
                    mLoadingPager.showEmpty();
                    return;
                }

                mLoadingPager.hideall();
                msgDetailView.setMessageInfo(res);
                //发出消息，通知查看消息了
                EventBus.getDefault().post(new LookMsgDetailEvent(res.getRowKey()));
            }

            @Override
            public void onFail(HttpResponse response, Exception ex) {
                if (bQuit) return;
                mLoadingPager.showFault(ex);
            }
        });
    }
}
