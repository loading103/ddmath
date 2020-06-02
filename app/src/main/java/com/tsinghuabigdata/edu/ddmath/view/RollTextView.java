package com.tsinghuabigdata.edu.ddmath.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;

import com.tsinghuabigdata.edu.ddmath.MVPModel.MessageModel;
import com.tsinghuabigdata.edu.ddmath.MVPModel.RequestListener;
import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.activity.MainActivity;
import com.tsinghuabigdata.edu.ddmath.adapter.MessagePageAdapter;
import com.tsinghuabigdata.edu.ddmath.bean.UserDetailinfo;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.constant.AppConst;
import com.tsinghuabigdata.edu.ddmath.event.JumpEvent;
import com.tsinghuabigdata.edu.ddmath.fragment.MyStudyFragment;
import com.tsinghuabigdata.edu.ddmath.module.floatwindow.FloatActionController;
import com.tsinghuabigdata.edu.ddmath.module.floatwindow.bean.BubbleBean;
import com.tsinghuabigdata.edu.ddmath.module.floatwindow.bean.WorkInfoBean;
import com.tsinghuabigdata.edu.ddmath.module.message.MessageDetailActivity;
import com.tsinghuabigdata.edu.ddmath.module.message.bean.MessageInfo;
import com.tsinghuabigdata.edu.ddmath.parent.activity.ParentMsgDetailActivity;
import com.tsinghuabigdata.edu.ddmath.parent.bean.ParentInfo;
import com.tsinghuabigdata.edu.ddmath.parent.event.ParentCenterEvent;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.AppLog;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.LogUtils;
import com.tsinghuabigdata.edu.ddmath.util.ToastUtils;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

//轮播滚动 TextVuew
//仅支持文本
public class RollTextView extends LinearLayout {

    //
    private TextView mTvMessage;
    private ViewPager mViewPagerMessage;

    private int textColor = Color.WHITE;

    private Context mContext;
    //private MessagePageAdapter mMessagePageAdapter;
    private MessageScrollTask  mMessageScrollTask;
    private List<MessageInfo>  mMessageList = new ArrayList<>();

    private Handler mHandler = new Handler();

    public RollTextView(Context context) {
        super(context);
        init(context);
    }

    public RollTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public RollTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        loadData();
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        //AppLog.d("jsdakljfdksjfsd changedView = " + changedView + "   visibility = "+visibility);
        if( visibility == VISIBLE ){
            if (mMessageScrollTask != null) {
                mMessageScrollTask.start();
            }
        }else{
            if (mMessageScrollTask != null) {
                mMessageScrollTask.stop();
            }
        }
    }

    //作业布置，作业批阅完成的消息
    public void addWorkMessageList(List<MessageInfo> workList){

        if (workList != null && workList.size() > 0) {
            mMessageList.addAll(workList);
            //AppLog.d("dsfdsfds workList =" + workList.size());
            for (MessageInfo info : workList) {
                //AppLog.d("dsfdsfds " + info.getRemark());
                if (!TextUtils.isEmpty(info.getRemark())) {
                    try {
                        JSONObject jsonObject1 = new JSONObject(info.getRemark());
                        String workId = jsonObject1.getString("workId");
                        String workName = jsonObject1.getString("workName");
                        WorkInfoBean workBean = new WorkInfoBean();
                        workBean.setWorkId(workId);
                        workBean.setWorkName(workName);
                        BubbleBean bubbleBean = new BubbleBean(BubbleBean.TYPE_DYNAMIC, "all", info.getDescription());
                        bubbleBean.setExtend(workBean);
                        FloatActionController.getInstance().addBubble(bubbleBean);
                    } catch (Exception e) {
                        AppLog.i("", e);
                    }
                }
            }
        }
        updateMessage();
    }

    public void queryMessageBannerByKey(String rowKey) {
        for (int i = 0; i < mMessageList.size(); i++) {
            MessageInfo msg = mMessageList.get(i);
            if (rowKey.equals(msg.getRowKey())) {
                removeMessageBannerByKey(rowKey);
                break;
            }
        }
    }
    public void updateMessage(MessageInfo messageInfo, int type) {
        //LogUtils.i("updateAssginessage workId=");
        List<MessageInfo> list = new ArrayList<>();
        list.add(messageInfo);
        for (int i = 0; i < mMessageList.size(); i++) {
            MessageInfo msg = mMessageList.get(i);
            if (msg.getWorkType() != type) {
                list.add(msg);
            }
        }
        mMessageList.clear();
        mMessageList.addAll(list);
        updateMessage();
    }


    public void queryMessageBannerByWorkId(String workId) {
        //LogUtils.i("queryMessageBannerByWorkId workId=" + workId);
        for (int i = 0; i < mMessageList.size(); i++) {
            MessageInfo msg = mMessageList.get(i);
            if (workId.equals(msg.getWorkId())) {
                removeMessageBannerByWorkId(workId);
                break;
            }
        }
    }
    public void setTextColor(int color){
        textColor = color;
        mTvMessage.setTextColor( textColor );
    }
    //----------------------------------------------------------------------------------------------
    /**
     * 初始化界面元素
     */
    private void init(Context context) {
        mContext = context;
        inflate(getContext(), R.layout.view_roll_textview_phone, this);

        mViewPagerMessage = findViewById(R.id.viewPager_message);
        mTvMessage =  findViewById(R.id.tv_single_message);
        mTvMessage.setSelected(true);
        mTvMessage.setTextSize(TypedValue.COMPLEX_UNIT_SP, GlobalData.isPad()?18:12);
        mViewPagerMessage.setOffscreenPageLimit(5);
    }

    /**
     * 获取首页重要轮播消息（布置作业和批阅完成除外，最多3条）
     */
    private void loadData() {
        String userId;
        UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
        ParentInfo parentInfo = AccountUtils.getParentInfo();
        if ( detailinfo != null) {
            userId = detailinfo.getStudentId();
        }else if( parentInfo!=null ){
            userId = parentInfo.getParentId();
        }else{
            return;
        }
        //AppLog.d("dsfdsfds userId =" + userId);
        new MessageModel().queryImportantMsg(userId, new RequestListener<List<MessageInfo>>() {
            @Override
            public void onSuccess(List<MessageInfo> res) {
                //AppLog.d("dsfdsfds isShown = "+isShown());
                //if( !isShown() ) return;
                if (res == null || res.size() == 0) {
                    //initMessageList(null);
                    //AppLog.d("dsfdsfds res = null or size = 0");
                    return;
                }
                initMessageList(res);
            }

            @Override
            public void onFail(HttpResponse response, Exception ex) {
                //LogUtils.i("queryOtherMessageList onFail ex" + ex.getMessage());
                //initMessageList(workList, null);
            }
        });
    }

    private void initMessageList(List<MessageInfo> otherList) {

        if (otherList != null && otherList.size() > 0) {
            mMessageList.addAll(otherList);
            //mMessageList.add( otherList.get(0) );
            //AppLog.d("dsfdsfds otherList =" + otherList.size());
            for (MessageInfo info : otherList) {
                //AppLog.d("dsfdsfds " + info.getDescription());
                BubbleBean bubbleBean = new BubbleBean(BubbleBean.TYPE_DYNAMIC, "all", info.getDescription());
                bubbleBean.setExtend(info);
                FloatActionController.getInstance().addBubble(bubbleBean);
            }
        }
        updateMessage();
    }

    private void updateMessage() {
        LogUtils.i("updateMessage mMessageList.size() = " + mMessageList.size());
        if (mMessageScrollTask != null) {
            mMessageScrollTask.stop();
        }
        if (mMessageList.size() == 0) {
            setVisibility(View.GONE);
            return;
        }
        setVisibility(View.VISIBLE);
        if (mMessageList.size() == 1) {
            mViewPagerMessage.setVisibility(View.INVISIBLE);
            mTvMessage.setVisibility(View.VISIBLE);
            initTextView();
        } else {
            mViewPagerMessage.setVisibility(View.VISIBLE);
            mTvMessage.setVisibility(View.INVISIBLE);
            initMessageViewPager();
        }
    }

    private void initTextView() {
        final MessageInfo messageInfo = mMessageList.get(0);
        if (messageInfo != null) {
            //AppLog.d("dsfdsfds messageInfo = " + messageInfo.getDescription() );
            mTvMessage.setTextColor( textColor );
            mTvMessage.setText(messageInfo.getDescription());
            if( isEnabled() ){
                mTvMessage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        clickImportantMsg(messageInfo);
                    }
                });
            }
        }
    }

    private void initMessageViewPager() {
        MessagePageAdapter mMessagePageAdapter = new MessagePageAdapter(mContext, mMessageList);
        mMessagePageAdapter.setTextColor( textColor );
        mViewPagerMessage.setAdapter(mMessagePageAdapter);
        if( isEnabled() ){
            mMessagePageAdapter.setClickMessageListener(new MessagePageAdapter.ClickMessageListener() {
                @Override
                public void clickMessagePage(MessageInfo messageInfo) {
                    clickImportantMsg(messageInfo);
                }
            });
        }
        mViewPagerMessage.setCurrentItem(0);
        // 自动轮播
        mMessageScrollTask = new MessageScrollTask();
        mMessageScrollTask.start();
    }

    /**
     * 点击首页重要消息
     */
    private void clickImportantMsg(MessageInfo messageInfo) {
        if (messageInfo == null || TextUtils.isEmpty(messageInfo.getRowKey())) {
            ToastUtils.showShort(mContext, R.string.param_error);
            return;
        }
        if (TextUtils.isEmpty(messageInfo.getWorkId())) {
            goToMessageDetailActivity(messageInfo);
            removeMessageBannerByKey(messageInfo.getRowKey());
        } else {
            EventBus.getDefault().post(new JumpEvent(MainActivity.FRAGMENT_STUDY_TASK, MyStudyFragment.MODEL_SCHOOLWORK));
            removeMessageBannerByWorkId(messageInfo.getWorkId());
        }
        if(AccountUtils.getParentInfo()!=null){
            EventBus.getDefault().post(new ParentCenterEvent(ParentCenterEvent.TYPE_MSG_DEC));
        }
    }

    private void removeMessageBannerByWorkId(String workId) {
        List<MessageInfo> list = new ArrayList<>();
        list.addAll(mMessageList);
        mMessageList.clear();
        for (int i = 0; i < list.size(); i++) {
            MessageInfo msg = list.get(i);
            if (!workId.equals(msg.getWorkId())) {
                mMessageList.add(msg);
            }
        }
        updateMessage();
    }

    private void removeMessageBannerByKey(String rowKey) {
        List<MessageInfo> list = new ArrayList<>();
        list.addAll(mMessageList);
        mMessageList.clear();
        for (int i = 0; i < list.size(); i++) {
            MessageInfo msg = list.get(i);
            if (!rowKey.equals(msg.getRowKey())) {
                mMessageList.add(msg);
            }
        }
        updateMessage();
    }

    private void goToMessageDetailActivity(MessageInfo messageInfo) {
        ParentInfo parentInfo = AccountUtils.getParentInfo();
        UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
        if (parentInfo != null) {
            Intent intent = new Intent(mContext, ParentMsgDetailActivity.class);
            intent.putExtra(AppConst.MSG_ROWKEY, messageInfo.getRowKey());
            getContext().startActivity(intent);
        }else if( detailinfo!=null ){
            Intent intent = new Intent(mContext, MessageDetailActivity.class);
            intent.putExtra(AppConst.MSG_ROWKEY, messageInfo.getRowKey());
            getContext().startActivity(intent);
        }
        MobclickAgent.onEvent(getContext(), "myworld_msg");
    }
    //----------------------------------------------------------------------------------------------
    public class MessageScrollTask implements Runnable {
        /**
         * 开始滚动
         */
        public void start() {
            // 得到一个主线程的handler
            mHandler.removeCallbacks(this);
            mHandler.postDelayed(this, 5000);
        }

        /**
         * 停止滚动
         */
        public void stop() {
            // 移除任务
            mHandler.removeCallbacks(this);
        }

        @Override
        public void run() {
            int currentItem = mViewPagerMessage.getCurrentItem();
            currentItem++;
            mViewPagerMessage.setCurrentItem(currentItem);
            // 递归
            start();
        }
    }

}
