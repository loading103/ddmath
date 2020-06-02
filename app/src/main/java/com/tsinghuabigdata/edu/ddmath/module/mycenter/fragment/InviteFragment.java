package com.tsinghuabigdata.edu.ddmath.module.mycenter.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.tsinghuabigdata.edu.ddmath.MVPModel.InviteModel;
import com.tsinghuabigdata.edu.ddmath.MVPModel.RequestListener;
import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.constant.AppConst;
import com.tsinghuabigdata.edu.ddmath.fragment.MyBaseFragment;
import com.tsinghuabigdata.edu.ddmath.module.mycenter.adapter.InviteBaseAdapter;
import com.tsinghuabigdata.edu.ddmath.module.mycenter.bean.InviteCountBean;
import com.tsinghuabigdata.edu.ddmath.module.mycenter.bean.RegRewardBean;
import com.tsinghuabigdata.edu.ddmath.module.mycenter.dialog.ShareDialog;
import com.tsinghuabigdata.edu.ddmath.module.mycenter.dialog.ShareLinkDialog;
import com.tsinghuabigdata.edu.ddmath.module.myscore.ScoreEventID;
import com.tsinghuabigdata.edu.ddmath.module.xbook.view.CoverFlow;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.AppUtils;
import com.tsinghuabigdata.edu.ddmath.util.DensityUtils;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.LogUtils;
import com.tsinghuabigdata.edu.ddmath.util.WindowUtils;
import com.tsinghuabigdata.edu.ddmath.view.LoadingPager;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


/**
 * 邀请好友
 * Created by Administrator on 2018/4/18.
 */

public class InviteFragment extends MyBaseFragment implements View.OnClickListener {

    private List<String> mList = new ArrayList<>();
    private Context mContext;

    private LoadingPager   mLoadingPager;
    //private RelativeLayout mRlInviteContent;
    //private LinearLayout   mLlInviteInfo;
    private TextView       mTvInviteInfo;
    //private ImageView      mIvShare;
    private LinearLayout   mLlShareRuleUnexpand;
    //private TextView       mIvLookMore;
    //private TextView       mTvSharePicTips;
    //private ViewPager      mViewPager;
    private CoverFlow      mCoverFlow;
    //private View           mViewShadow;
    private LinearLayout   mLlShareRuleExpand;
    //private TextView       mIvLookLess;
    //private TextView       mTvShareRule;

    private int itemHeight;
    //private int middleWidth;
    //private int allWidth;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root;
        if (GlobalData.isPad()) {
            root = inflater.inflate(R.layout.fragment_invite, container, false);
        } else {
            root = inflater.inflate(R.layout.fragment_invite_phone, container, false);
        }
        initView(root);
        initSize();
        setPrepared();
        initData();
        return root;
    }

    public String getUmEventName() {
        return "mycenter_invitefriend";
    }


    private void initView(View root) {
        mContext = getActivity();
        mLoadingPager =  root.findViewById(R.id.loadingPager);
        RelativeLayout mRlInviteContent =  root.findViewById(R.id.rl_invite_content);
        //mLlInviteInfo = (LinearLayout) root.findViewById(R.id.ll_invite_info);
        mTvInviteInfo =  root.findViewById(R.id.tv_invite_info);
        Button mIvShare =  root.findViewById(R.id.iv_share);
        mLlShareRuleUnexpand =  root.findViewById(R.id.ll_share_rule_unexpand);
        TextView mIvLookMore =  root.findViewById(R.id.iv_look_more);
        //mTvSharePicTips = (TextView) root.findViewById(R.id.tv_share_pic_tips);
        mCoverFlow =  root.findViewById(R.id.coverflow);
        View mViewShadow = root.findViewById(R.id.view_shadow);
        mLlShareRuleExpand =  root.findViewById(R.id.ll_share_rule_expand);
        TextView mIvLookLess =  root.findViewById(R.id.iv_look_less);
        TextView mTvShareRule =  root.findViewById(R.id.tv_share_rule);

        String content = "1.长按二维码的图片，再发送给你的小伙伴，识别二维码便可注册"+ AppUtils.getAppName() +"；\n" +
                "2.直接点击右上角的“去分享”按钮，一键分享给小伙伴吧。\n" +
                "分享快乐，赶快行动吧！";
        RegRewardBean bean = AccountUtils.getRegRewardBean();
        String text;
        if (bean == null) {
            text = String.format(Locale.getDefault(), content, 100, 300);
        } else {
            text = String.format(Locale.getDefault(), content, bean.getRegWithOutRecAward(), bean.getRegWithRecWard());
        }
        mTvShareRule.setText(text);
        mIvShare.setOnClickListener(this);
        mIvLookMore.setOnClickListener(this);
        mIvLookLess.setOnClickListener(this);
        mViewShadow.setOnClickListener(this);
        mLoadingPager.setTargetView(mRlInviteContent);
        mLoadingPager.setListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLoadingPager.showLoading();
                queryInviteCount();
            }
        });

        mLlShareRuleUnexpand.setVisibility(View.VISIBLE);
        mLlShareRuleExpand.setVisibility(View.INVISIBLE);
    }

    private void initSize() {
        //动态计算作业列表条目数量
        int itemWidth;
        int screenWidth = WindowUtils.getScreenWidth(mContext);
        int screenHeight = WindowUtils.getScreenHeight(mContext);
        if (GlobalData.isPad()) {
            itemWidth = screenWidth - DensityUtils.dp2px(mContext, AppConst.NAVI_WIDTH_PAD + 300);
            itemHeight = screenHeight - DensityUtils.dp2px(mContext, 10 + 6 + 42 + 56 + 24 + 10 * 2);
        } else {
            //顶部10 底部6 标题栏35 规则以及分享提示栏34+17 滑动控件上下间距6
            itemWidth = screenWidth - DensityUtils.dp2px(mContext, AppConst.NAVI_WIDTH_PHONE + 170);
            itemHeight = screenHeight - DensityUtils.dp2px(mContext, 10 + 6 + +35 + 34 + 17 + 6 * 2);
        }

        //int middleWidth = itemHeight * 2 / 3;
        int allWidth = itemHeight * 4 / 3;
        LogUtils.i("screenWidth=" + screenWidth + "screenHeight=" + screenHeight);
        LogUtils.i("itemWidth=" + itemWidth + "itemHeight=" + itemHeight + "allWidth=" + allWidth);
        if (allWidth < itemWidth) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mCoverFlow.getLayoutParams();
            layoutParams.width = allWidth;
            mCoverFlow.setLayoutParams(layoutParams);
        }
    }

    private void initData() {
        /*if (AppUtils.isDebug()) {
            analogData();
        } else {
            queryInviteCount();
        }*/
        queryInviteCount();
    }

    private void queryInviteCount() {
        new InviteModel().getInviteCount(new RequestListener<InviteCountBean>() {

            @Override
            public void onSuccess(InviteCountBean res) {
                LogUtils.i("queryStudyBean success");
                if (res == null) {
                    mLoadingPager.showServerFault();
                    return;
                }
                queryShareImages(res);
            }

            @Override
            public void onFail(HttpResponse<InviteCountBean> response, Exception ex) {
                LogUtils.i("queryStudyBean failed" + ex.getMessage());
                mLoadingPager.showFault(ex);
            }
        });
    }

    private void queryShareImages(final InviteCountBean inviteCountBean) {
        new InviteModel().getShareImages(new RequestListener<List<String>>() {

            @Override
            public void onSuccess(List<String> res) {
                if (res == null || res.size() == 0) {
                    mLoadingPager.showEmpty("图片更新中");
                    return;
                }
                showData(inviteCountBean, res);
                mLoadingPager.showTarget();
            }

            @Override
            public void onFail(HttpResponse<List<String>> response, Exception ex) {
                LogUtils.i("queryShareImages failed" + ex.getMessage());
                mLoadingPager.showFault(ex);
            }
        });
    }

    private void showData(InviteCountBean inviteCountBean, List<String> res) {
        if (inviteCountBean.getRecPersonNum() <= 0) {
            mTvInviteInfo.setText("你还没有邀请过好友哦，现在去邀请好友得学豆吧！");
        } else {
            String content = "已成功邀请%d人，获得%d学豆！";
            String text = String.format(Locale.getDefault(), content, inviteCountBean.getRecPersonNum(), inviteCountBean.getDdAwardNum());
            mTvInviteInfo.setText(text);
        }
        mList.addAll(res);
        InviteBaseAdapter adapter = new InviteBaseAdapter(mContext, mList);
        adapter.setItemHeight(itemHeight);
        mCoverFlow.setAdapter(adapter);
        mCoverFlow.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String url = mList.get(position);
                ShareDialog sharePicDialog = new ShareDialog(mContext);
                sharePicDialog.setSharPicture( url );
                sharePicDialog.show();
                sharePicDialog.setScoreEventId(ScoreEventID.EVENT_INVITE_FRIEND);
                return false;
            }
        });
    }

//    private void analogData() {
//        createList();
//        InviteBaseAdapter adapter = new InviteBaseAdapter(mContext, mList);
//        adapter.setItemHeight(itemHeight);
//        mCoverFlow.setAdapter(adapter);
//        mCoverFlow.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//                String url = mList.get(position);
//                ShareDialog sharePicDialog = new ShareDialog(mContext);
//                sharePicDialog.setSharPicture( url );
//                sharePicDialog.show();
//                return false;
//            }
//        });
//        mLoadingPager.showTarget();
//    }


//    private void createList() {
//        String[] pics = {"http://n.sinaimg.cn/sports/2_img/upload/4f160954/525/w1360h765/20180424/rZ2j-fzqvvsa3943931.jpg",
//                "http://n.sinaimg.cn/sports/2_img/upload/85dcfa84/780/w632h948/20180418/ffyD-fzihnep1861632.jpg",
//                "http://n.sinaimg.cn/sports/2_img/upload/cf0d0fdd/227/w440h587/20180418/LgOj-fzihnep4564312.jpg"};
//        for (int i = 0; i < 3; i++) {
//            mList.add(pics[i]);
//        }
//    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_share:
                //ToastUtils.showShort(mContext, "去分享");
                ShareLinkDialog shareLinkDialog = new ShareLinkDialog(mContext);
                shareLinkDialog.setScoreEventId(ScoreEventID.EVENT_INVITE_FRIEND);
                shareLinkDialog.show();
                break;
            case R.id.iv_look_more:
                mLlShareRuleUnexpand.setVisibility(View.INVISIBLE);
                mLlShareRuleExpand.setVisibility(View.VISIBLE);
                break;
            case R.id.iv_look_less:
                mLlShareRuleUnexpand.setVisibility(View.VISIBLE);
                mLlShareRuleExpand.setVisibility(View.INVISIBLE);
                break;
            case R.id.view_shadow:
                mLlShareRuleUnexpand.setVisibility(View.VISIBLE);
                mLlShareRuleExpand.setVisibility(View.INVISIBLE);
                break;
            default:
                break;
        }
    }
}
