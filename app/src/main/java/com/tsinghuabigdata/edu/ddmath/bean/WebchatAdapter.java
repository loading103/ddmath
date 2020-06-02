package com.tsinghuabigdata.edu.ddmath.bean;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.tsinghuabigdata.edu.ddmath.MVPModel.RequestListener;
import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.constant.RobotQaConst;
import com.tsinghuabigdata.edu.ddmath.module.mycenter.HeadImageUtils;
import com.tsinghuabigdata.edu.ddmath.module.robotqa.KnowledgeView;
import com.tsinghuabigdata.edu.ddmath.module.robotqa.OtherMatesAnsView;
import com.tsinghuabigdata.edu.ddmath.module.robotqa.RecycleViewItemListener;
import com.tsinghuabigdata.edu.ddmath.module.robotqa.RobotClickFlowManager;
import com.tsinghuabigdata.edu.ddmath.module.robotqa.RobotQaActivity;
import com.tsinghuabigdata.edu.ddmath.module.robotqa.RobotQaPresent;
import com.tsinghuabigdata.edu.ddmath.module.robotqa.SubjectView;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.AppLog;
import com.tsinghuabigdata.edu.ddmath.util.AppUtils;
import com.tsinghuabigdata.edu.ddmath.util.UnitUtil;
import com.tsinghuabigdata.edu.ddmath.view.MyRatingBar;

import java.util.HashMap;
import java.util.List;

import static com.tsinghuabigdata.edu.ddmath.bean.ChatMessage.RobotEmotionType.EVAL;
import static com.tsinghuabigdata.edu.ddmath.bean.ChatMessage.RobotEmotionType.WELCOME;


public class WebchatAdapter extends RecyclerView.Adapter<WebchatAdapter.ViewHolder> implements View.OnClickListener {

    private static HashMap<Integer, Bitmap> bitmaps = new HashMap<Integer, Bitmap>();

    private Drawable                userImage;
    private List<ChatMessage>       mMessages;
    private Context                 mContext;
    private RecycleViewItemListener itemListener;
    private RobotQaPresent          robotQaPresent;

    private int commentStarTimes;

    public WebchatAdapter(Context context, RobotQaPresent robotQaPresent, RecycleViewItemListener listener) {
        //清除认知误差图的缓存
        bitmaps.clear();

        mContext = context;
        this.robotQaPresent = robotQaPresent;
        mMessages = robotQaPresent.getAllChatMessages();
        itemListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layout = -1;
        switch (viewType) {
            case ChatMessage.TYPE_COMEIN:
                layout = R.layout.listitem_message_comein;
                break;
            case ChatMessage.TYPE_OUT:
                layout = R.layout.listitem_message_out;
                break;
            case ChatMessage.TYPE_HIDENODE:
                layout = R.layout.listitem_message_comein_hidenode;
                break;
            default:
                layout = R.layout.listitem_message_comein_hidenode;
                break;
        }
        View v = LayoutInflater
                .from(parent.getContext())
                .inflate(layout, parent, false);
        v.setOnClickListener(this);
        return new ViewHolder(v, viewType);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        ChatMessage message = mMessages.get(position);
        //对象绑定在对应的view上
        viewHolder.itemView.setTag(message);
        if (message.getType() == ChatMessage.TYPE_OUT) {
            bindOutView(viewHolder, message);
        } else if (message.getType() == ChatMessage.TYPE_COMEIN) {
            bindComeinView(viewHolder, message, position);
        } else {
            bindOtherNodeView(viewHolder, message);
        }
    }

    private void bindOtherNodeView(ViewHolder viewHolder, ChatMessage message) {
    }

    private void bindComeinView(final ViewHolder viewHolder, final ChatMessage message, final int position) {
        switch (message.getContentType()) {
            case SUBJECT:
                viewHolder.showContainer(ChatMessage.ContentType.SUBJECT);
                //带公式的文本，不需要限行数
                if (message.isMathText()) {
                    viewHolder.llTopicContainer.showBottomView(false);
                    viewHolder.llTopicContainer.setSubjectMaxLines(50);
                    viewHolder.llTopicContainer.setMathText(true);
                    viewHolder.llTopicContainer.showExtendView(false);
                } else {
                    viewHolder.llTopicContainer.showBottomView(true);
                    viewHolder.llTopicContainer.setSubjectMaxLines(5);
                    viewHolder.llTopicContainer.showExtendView(true);
                    //扩展块，与评论块冲突
                    if (message.isHaveComment()) {
                        viewHolder.llTopicContainer.showExtendView(false);
                    } else {
                        viewHolder.llTopicContainer.showExtendView(true);
                        //题干状态显示
                        viewHolder.llTopicContainer.enableNextTopicView(message.isHaveNextTopic());
                    }
                }
                viewHolder.llTopicContainer.setMessages(message.getChatSubject());

                //subjectview设置监听事件
                viewHolder.llTopicContainer.setListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (v.getId()) {
                            case R.id.tv_detail:
                                //相似题查看详情后，添加可评论块
                                viewHolder.llTopicContainer.showExtendView(false);
                                mMessages.get(position).setHaveComment(true);
                                mMessages.get(position).setThumbup(true);

                                mMessages.get(position).setHaveNextTopic(false);
                                notifyItemChanged(position);
                                break;
                            default:
                                break;
                        }
                    }
                });
                break;
            case TEXT:
                viewHolder.showContainer(ChatMessage.ContentType.TEXT);
                viewHolder.tvContent.setTextColor(mContext.getResources().getColor(R.color.robot_common_text_color));
                viewHolder.showImageInMsgText(message.isHaveImgInText());
                if (message.getImgResid() == 0) {
                    viewHolder.showImageInMsgText(false);
                } else {
                    viewHolder.ivInText.setImageResource(message.getImgResid());
                }
                viewHolder.tvContent.setText(message.getTextContent());
                break;
            case KNOWLEDGE_IMAGE:
                viewHolder.showContainer(ChatMessage.ContentType.KNOWLEDGE_IMAGE);
                boolean isKnowviewExpand = message.isKnowviewExpand(); // true : 展开，不需要出现展开按钮
                viewHolder.ll_knowledge.setDatas(message.getKnowCongnitionData(), isKnowviewExpand, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (v.getId()) {
                            case R.id.tv_more:
                                //展开后
                                mMessages.get(position).setKnowviewExpand(true);
                                break;
                            default:
                                break;
                        }
                    }
                });
                break;
            case CLASSMATE_ANS:
                //同学答案节点
                viewHolder.showContainer(ChatMessage.ContentType.CLASSMATE_ANS);
                viewHolder.llMateAnsContainer.setOtherAnsData(message.getOthermatesAnsData());
                viewHolder.llMateAnsContainer.enableNextAnsView(message.isHaveNextAns());
                viewHolder.llMateAnsContainer.setListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (v.getId()) {
                            case R.id.tv_givelike:
                                //同学答案点赞
                                robotQaPresent.toast("点赞");
                                //避免重复点赞
                                viewHolder.llMateAnsContainer.enableGivelike(false);
                                final String likeStuid = message.getOthermatesAnsData().getDisplayAns().getStudentId();
                                robotQaPresent.givelike2mateAns(likeStuid, new RequestListener() {
                                    @Override
                                    public void onSuccess(Object res) {
                                        viewHolder.llMateAnsContainer.enableGivelike(false);
                                        message.getOthermatesAnsData().getDisplayAns().setHadGivelike(true);
                                        RobotClickFlowManager.getInstance().robotLookOtherAnsLikeClick(likeStuid);
                                        notifyItemChanged(position);
                                    }

                                    @Override
                                    public void onFail(HttpResponse reponse, Exception e) {
                                        viewHolder.llMateAnsContainer.enableGivelike(true);
                                        robotQaPresent.toast("点赞失败，请重试");
                                    }
                                });
                                break;
                            default:
                                break;
                        }
                    }
                });
                break;
            default:
                break;
        }

        //机器人表情显示
        robotEmotionShow(viewHolder, message);
        //显示评论区
        viewHolder.showInMsgCommentContainer(message.isHaveComment());
        //星星状态
        if (message.isHadCommentStar()) {
            viewHolder.ratingBar.setCanEdit(false);
        } else {
            viewHolder.ratingBar.setCanEdit(true);
        }
        viewHolder.ratingBar.setCountSelected(message.getNumStars());

        //评论区中的星星和点赞只能取一
        if (message.isStarComment()) {
            viewHolder.showInMsgCommentStar(true);
        } else {
            viewHolder.showInMsgCommentStar(false);
        }

        //点赞状态显示
        showThumbupView(viewHolder, message);
        //点赞点击事件
        viewHolder.tvThumbsup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHolder.tvThumbsup.setEnabled(false);
                robotQaPresent.toast("点赞");
                if (message.getContentType() == ChatMessage.ContentType.SUBJECT) {
                    //相似题点赞
                    robotQaPresent.giveLike("s", message.getChatSubject().getDisplayQuestionId(), new RequestListener<Integer>() {
                        @Override
                        public void onSuccess(Integer res) {
                            //点赞成功，重刷整个
                            mMessages.get(position).setThumbupText(res + "个同学狠赞");
                            mMessages.get(position).setHadThumbup(true);
                            mMessages.get(position).setHaveNextTopic(false);
                            robotQaPresent.toast("点赞成功");
                            notifyItemChanged(position);

                            //相似题点赞事件
                            RobotClickFlowManager.getInstance().robotLookSimilarTopicLikeClick(message.getChatSubject().getDisplayQuestionId());
                        }

                        @Override
                        public void onFail(HttpResponse reponse, Exception e) {
                            viewHolder.tvThumbsup.setEnabled(true);
                            robotQaPresent.toast("点赞失败，请重试");
                        }
                    });
                } else {
                    //认知点赞
                    robotQaPresent.giveLike("r", null, new RequestListener<Integer>() {
                        @Override
                        public void onSuccess(Integer res) {
                            //点赞成功，重刷整个
                            mMessages.get(position).setThumbupText(res + "个同学狠赞");
                            mMessages.get(position).setHadThumbup(true);
                            mMessages.get(position).setHaveNextTopic(false);
                            viewHolder.tvThumbsup.setEnabled(false);
                            notifyItemChanged(position);
                            robotQaPresent.getRecycleView().smoothScrollToPosition(position + 1);

                            //认知误差点赞事件
                            RobotClickFlowManager.getInstance().robotFoundReasonLikeClick();
                        }

                        @Override
                        public void onFail(HttpResponse reponse, Exception e) {
                            robotQaPresent.toast("点赞失败，请重试");
                            viewHolder.tvThumbsup.setEnabled(true);
                        }
                    });
                }
            }
        });
        //星星评价分数事件
        viewHolder.ratingBar.setOnRatingChangeListener(new MyRatingBar.OnRatingChangeListener() {
            @Override
            public void onChange(final int countSelected) {
//                robotQaPresent.toast("star " + countSelected);
                viewHolder.ratingBar.setCanEdit(false);
                robotQaPresent.commentRobotService(countSelected + "", "", robotQaPresent.getQuestionId(), new RequestListener<ResultInfo>() {
                    @Override
                    public void onSuccess(ResultInfo res) {
                        //星星评价次数统计
                        commentStarTimes++;

                        viewHolder.ratingBar.setCanEdit(false);
                        mMessages.get(position).setHadCommentStar(true);
                        mMessages.get(position).setNumStars(viewHolder.ratingBar.getCountSelected());
                        notifyItemChanged(position);
                        ActivityManager.RunningTaskInfo topTask = AppUtils.getTopTask(robotQaPresent.getContext());
                        String packageName = AppUtils.getPackageInfo(robotQaPresent.getContext()).packageName;
                        if (AppUtils.isTopActivity(topTask, packageName, RobotQaActivity.class.getName())) {
                            AppLog.d("cls : " + RobotQaActivity.class.getName() + "  在最前端");
                            robotQaPresent.toast("评价成功");
                        }
                        //星星评分事件
                        RobotClickFlowManager.getInstance().robotStarAssess(countSelected + "");
                    }

                    @Override
                    public void onFail(HttpResponse reponse, Exception e) {
                        //重新激活按钮
                        viewHolder.ratingBar.setCanEdit(true);

                        ActivityManager.RunningTaskInfo topTask = AppUtils.getTopTask(robotQaPresent.getContext());
                        String packageName = AppUtils.getPackageInfo(robotQaPresent.getContext()).packageName;
                        if (AppUtils.isTopActivity(topTask, packageName, RobotQaActivity.class.getName())) {
                            AppLog.d("cls : " + RobotQaActivity.class.getName() + "  在最前端");
                            robotQaPresent.toast("评价失败，请重试");
                        }
                    }
                });
            }
        });
    }

    private void robotEmotionShow(ViewHolder viewHolder, ChatMessage message) {

        AppLog.d("---- message.mType() = " + message.getType() + ",,,, getContentType = " + message.getContentType() + ",,,, EmotionType = " + message.getRobotEmotionType() );

        int resid = R.drawable.ic_doudou_question;
        if( ChatMessage.ContentType.TEXT == message.getContentType() ){
            if( WELCOME == message.getRobotEmotionType() ){
                resid = R.drawable.ic_doudou_hi;
            }else if( EVAL == message.getRobotEmotionType() ){
                resid = R.drawable.ic_doudou_evalute;
            }
        }else if( ChatMessage.ContentType.SUBJECT == message.getContentType() ){
            resid = R.drawable.ic_doudou_question;
        }else if( ChatMessage.ContentType.CLASSMATE_ANS == message.getContentType() ){
            resid = R.drawable.ic_doudou_answer;
        }else{
            resid = R.drawable.ic_doudou_question;
        }

//        if (message.getRobotEmotionType() == null) {
//            resid = R.drawable.ic_doudou_smile;
//        } else {
//            switch (message.getRobotEmotionType()) {
//                case WELCOME:
//                    resid = R.drawable.ic_doudou_hi;
//                    break;
//                case DIZZY:
//                    resid = R.drawable.ic_doudou_dizziness;
//                    break;
//                case ADMIRE:
//                    resid = R.drawable.ic_doudou_adore;
//                    break;
//                case SMILE:
//                    resid = R.drawable.ic_doudou_smile;
//                    break;
//                default:
//                    resid = R.drawable.ic_doudou_smile;
//                    break;
//            }
//        }
        //默认显示笑脸表情
        viewHolder.ivUserImg.setImageResource(resid);
    }

    private void showThumbupView(ViewHolder viewHolder, ChatMessage message) {
        //点赞状态显示
        if (!TextUtils.isEmpty(message.getThumbupText())) {
            viewHolder.tvThumbsup.setText(message.getThumbupText());
        } else {
            viewHolder.tvThumbsup.setText("有用，赞一个");
        }
        Drawable drawable;
        if (message.isHadThumbup()) {
            viewHolder.tvThumbsup.setEnabled(false);
            drawable = mContext.getResources().getDrawable(R.drawable.ic_thumb_up_disable);
            viewHolder.tvThumbsup.setTextColor(mContext.getResources().getColor(R.color.give_five_text_color_disabled));
        } else {
            viewHolder.tvThumbsup.setEnabled(true);
            drawable = mContext.getResources().getDrawable(R.drawable.ic_thumb_up);
            viewHolder.tvThumbsup.setTextColor(mContext.getResources().getColor(R.color.msg_tag_recommand));
        }
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        viewHolder.tvThumbsup.setCompoundDrawables(drawable, null, null, null);
    }

    private void bindOutView(ViewHolder viewHolder, ChatMessage message) {
        LinearLayout.LayoutParams lp;
        switch (message.getContentType()) {
            case QUESTION:
                viewHolder.ivUserImg.setVisibility(View.GONE);
                viewHolder.llContentContainer.setBackgroundResource(R.drawable.bubble2_r);
                lp = (LinearLayout.LayoutParams) viewHolder.llContentContainer.getLayoutParams();
                lp.setMargins(0, (int) UnitUtil.dp2Px(mContext, 3), 0, (int) UnitUtil.dp2Px(mContext, 1));
                viewHolder.tvContent.setTextColor(mContext.getResources().getColor(R.color.msg_tag_recommand));
                viewHolder.tvContent.setPadding(0, 0, 0, 0);
                if (message.getTextContent().equals(RobotQaConst.GO_TO_LOOK)) {
                    viewHolder.ivTextImg.setVisibility(View.VISIBLE);
                    viewHolder.ivTextImg.setImageResource(R.drawable.ic_smile);
                } else if (message.getTextContent().equals(RobotQaConst.DONT_GO_TO_LOOK)) {
                    viewHolder.ivTextImg.setVisibility(View.VISIBLE);
                    viewHolder.ivTextImg.setImageResource(R.drawable.ic_sad);
                } else {
                    viewHolder.ivTextImg.setVisibility(View.GONE);
                }
                break;
            case TEXT:
                viewHolder.ivTextImg.setVisibility(View.GONE);
                lp = (LinearLayout.LayoutParams) viewHolder.llContentContainer.getLayoutParams();
                lp.setMargins(0, (int) UnitUtil.dp2Px(mContext, 7), 0, (int) UnitUtil.dp2Px(mContext, 7));
                UserDetailinfo studentInfo = AccountUtils.getUserdetailInfo();
                if (userImage != null) {
                    viewHolder.ivUserImg.setImageDrawable(userImage);
                } else {
                    if (studentInfo != null) {
                        // 设置头像
                        HeadImageUtils.setHeadImage(viewHolder.ivUserImg);
                        userImage = viewHolder.ivUserImg.getDrawable();
                    }
                }
                viewHolder.ivUserImg.setVisibility(View.VISIBLE);
                viewHolder.llContentContainer.setBackgroundResource(R.drawable.bubble_r);
                viewHolder.tvContent.setTextColor(mContext.getResources().getColor(R.color.robot_text_color_right));
                //viewHolder.llContentContainer.getBackground().setColorFilter(mContext.getResources().getColor(R.color.robot_bubble_right_bg),
//                        PorterDuff.Mode.SRC_IN);
                viewHolder.tvContent.setPadding(8, 8, 8, 8);
                break;
            default:
                break;
        }
        //发送的消息只有文本
        viewHolder.tvContent.setText(message.getTextContent());
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mMessages.get(position).getType();
    }


    @Override
    public void onClick(View v) {
        ChatMessage chatmsg = (ChatMessage) v.getTag();
        //隐藏的结束节点
        if (chatmsg.getType() == ChatMessage.TYPE_HIDENODE) {
            return;
        }
        if (itemListener != null) {
            itemListener.onRecycleViewItemClick(v, chatmsg);
            //双击,内容容器作为标签载体
            LinearLayout view = (LinearLayout) v.findViewById(R.id.ll_content_container);
            long clicktime;
            if (view.getTag() != null) {
                clicktime = (long) view.getTag();
            } else {
                view.setTag(System.currentTimeMillis());
                return;
            }
            Log.d("cl", "onClick: pre " + clicktime);
            Log.d("cl", "onClick:  " + System.currentTimeMillis());
            long diff = Math.abs(System.currentTimeMillis() - clicktime);
            Log.d("cl", "onClick: diff " + diff);
            if (diff <= 300) {
                Log.d("cl", "onClick: double click  ");
                itemListener.onRecycleViewItemDoubleClick(v, chatmsg);
                view.setTag(1000L);
            } else {
                Log.d("cl", "onClick: only click  ");
                view.setTag(System.currentTimeMillis());
            }
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivUserImg;
        private TextView tvContent;
        private LinearLayout llContentContainer;

        //out msg
        private ImageView ivTextImg;

        //comein msg
        //三大控件容器
        private LinearLayout      llTextContainer;
        private SubjectView       llTopicContainer;
        private LinearLayout      llCommentContainer;
        private OtherMatesAnsView llMateAnsContainer;

        private TextView      tvThumbsup;
        private MyRatingBar   ratingBar;
        private KnowledgeView ll_knowledge;
        private ImageView     ivInText;

        public ViewHolder(View itemView, int viewType) {
            super(itemView);

            ivUserImg = (ImageView) itemView.findViewById(R.id.iv_user_img);
            tvContent = (TextView) itemView.findViewById(R.id.tv_content);
            llContentContainer = (LinearLayout) itemView.findViewById(R.id.ll_content_container);
            ivTextImg = (ImageView) itemView.findViewById(R.id.iv_textimg);

            //comein msg
            //4大控件容器,以下4者同时只能显一
            llTextContainer = (LinearLayout) itemView.findViewById(R.id.ll_text_container);
            llTopicContainer = (SubjectView) itemView.findViewById(R.id.ll_topic_container);
            ll_knowledge = (KnowledgeView) itemView.findViewById(R.id.ll_knowledge);
            llMateAnsContainer = (OtherMatesAnsView) itemView.findViewById(R.id.ll_mateans_container);

            llCommentContainer = (LinearLayout) itemView.findViewById(R.id.ll_comment_container);

            tvThumbsup = (TextView) itemView.findViewById(R.id.tv_thumbsup);
            ratingBar = (MyRatingBar) itemView.findViewById(R.id.ratingBar);
            ivInText = (ImageView) itemView.findViewById(R.id.iv_text_module);
        }

        private void showImageInMsgText(boolean isShowImg) {
            if (isShowImg) {
                ivInText.setVisibility(View.VISIBLE);
                tvContent.setVisibility(View.GONE);
            } else {
                ivInText.setVisibility(View.GONE);
                tvContent.setVisibility(View.VISIBLE);
            }
        }

        private void showInMsgTextContainer(boolean ishow) {
            if (ishow) {
                llTextContainer.setVisibility(View.VISIBLE);
                llTopicContainer.setVisibility(View.GONE);
                ll_knowledge.setVisibility(View.GONE);
            } else {
                ll_knowledge.setVisibility(View.GONE);
                llTextContainer.setVisibility(View.GONE);
                llTopicContainer.setVisibility(View.VISIBLE);
            }
        }

        private void showContainer(ChatMessage.ContentType contentType) {
            View[] views = new View[]{llTextContainer, llTopicContainer, ll_knowledge, llMateAnsContainer};
            for (View v : views) {
                v.setVisibility(View.GONE);
            }
            switch (contentType) {
                case TEXT:
                    llTextContainer.setVisibility(View.VISIBLE);
                    break;
                case KNOWLEDGE_IMAGE:
                    ll_knowledge.setVisibility(View.VISIBLE);
                    break;
                case SUBJECT:
                    llTopicContainer.setVisibility(View.VISIBLE);
                    break;
                case CLASSMATE_ANS:
                    llMateAnsContainer.setVisibility(View.VISIBLE);
                    break;
                default:
                    break;
            }
        }

        private void showInMsgCommentStar(boolean ishow) {
            if (ishow) {
                ratingBar.setVisibility(View.VISIBLE);
                tvThumbsup.setVisibility(View.GONE);
            } else {
                ratingBar.setVisibility(View.GONE);
                tvThumbsup.setVisibility(View.VISIBLE);
            }
        }

        private void showInMsgCommentContainer(boolean ishow) {
            if (ishow) {
                llCommentContainer.setVisibility(View.VISIBLE);
            } else {
                llCommentContainer.setVisibility(View.GONE);
            }
        }

        private void showInMsgKnowledge(boolean ishow) {
            if (ishow) {
                llTextContainer.setVisibility(View.GONE);
                llTopicContainer.setVisibility(View.GONE);
                ll_knowledge.setVisibility(View.VISIBLE);
            } else {
                ll_knowledge.setVisibility(View.GONE);
            }
        }
    }

    public int getCommentStarTimes() {
        return commentStarTimes;
    }

    public static HashMap<Integer, Bitmap> getBitmaps() {
        return bitmaps;
    }

    public static void setBitmaps(HashMap<Integer, Bitmap> bitmaps) {
        WebchatAdapter.bitmaps = bitmaps;
    }
}
