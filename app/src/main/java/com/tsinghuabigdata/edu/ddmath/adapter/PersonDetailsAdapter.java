package com.tsinghuabigdata.edu.ddmath.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.bean.TaskBean;
import com.tsinghuabigdata.edu.ddmath.bean.WeekDatailBean;
import com.tsinghuabigdata.edu.ddmath.module.scoreplan.activity.ScorePlanActivity;
import com.tsinghuabigdata.edu.ddmath.module.scoreplan.view.ImaginaryLineView;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;

import java.util.List;
import java.util.Locale;

public class PersonDetailsAdapter extends BaseAdapter{
    private List<WeekDatailBean> datas;
    private Context context;
    private LayoutInflater inflater = null;

    private static final int TYPE_ONE = 0; //普通类型
    private static final int TYPE_TWO = 1;//训练本类型
    private static final int TYPE_CONTENT= 2;//item类型个数
    private  Boolean isfirstVisible=true;//训练本标题
    private TaskBean taskBean;
    public PersonDetailsAdapter(Context context, List<WeekDatailBean> weeks) {
        this.datas=weeks;
        this.context=context;
        inflater = LayoutInflater.from(context);
    }


    @Override
    public int getCount() {
        return datas.size();
    }
    @Override
    public WeekDatailBean getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TypeOneViewHolder holder_01=null;
        TypeTwoViewHolder holder_02=null;
        switch (getItemViewType(position)) {
            case TYPE_ONE:
                if(convertView==null){
                    holder_01 = new TypeOneViewHolder();
                    convertView = inflater.inflate(GlobalData.isPad()?R.layout.item_person_details:R.layout.item_person_details_phone, parent, false);
                    typeOneFinById(holder_01,convertView);
                    convertView.setTag(holder_01);
                }else {
                    holder_01 = (TypeOneViewHolder) convertView.getTag();
                }
                WeekDatailBean week = getItem(position);
                changTypeOneView(holder_01,week,position);

                break;
            case TYPE_TWO:
                if(convertView==null){
                    holder_02 = new TypeTwoViewHolder();
                    convertView = inflater.inflate(GlobalData.isPad()?R.layout.item_practice_details:R.layout.item_practice_details_phone, parent, false);
                    typeTwoFinById(holder_02,convertView);
                    convertView.setTag(holder_02);
                }else {
                    holder_02 = (TypeTwoViewHolder) convertView.getTag();
                }
                WeekDatailBean weeks = getItem(position);
                changTypeTwoView(holder_02,weeks,position);

                break;
        }
        return convertView;
    }




    @Override
    public int getItemViewType(int position) {
        if(datas.get(position).getWeekType()==1){
            return TYPE_TWO;
        }else{
            return TYPE_ONE ;
        }
    }
    @Override
    public int getViewTypeCount() {
        return TYPE_CONTENT;
    }


    public static class TypeOneViewHolder {
        public LinearLayout mllCorrecting;
        public TextView mTvtitle;
        public TextView mTvCorreted;
        public TextView mTvCorretno;
        public TextView mTvCorrectionState;
        public ImageView mIvstate;
        public ImageView mheadview;
        public Button mBtn;
        public LinearLayout ll_correct_ed;
        public TextView tv_correct_right;
        public TextView tv_correct_wrong;

        public LinearLayout mllSyn;
        public ImaginaryLineView line_dote_up;
        public ImaginaryLineView line_dote_down;
        public TextView mTvtitle_syn;
        public TextView mTvChoose_syn;
        public TextView mTvCorretno_syn;
        public ImageView mIvstate_syn;
        public Button mBtn_syn;

        public LinearLayout mllDefine;
        public TextView mTvtitle_define;
        public TextView mTvCorretno_define;
        public TextView mTvChoose_define;
        public TextView mTvCorrectionState_define;
        public ImageView mIvstate_define;
        public Button mBtn_define;
        public TextView mTvdefine;
    }
    public static class TypeTwoViewHolder {
        public ImaginaryLineView line_dote_up;
        public ImaginaryLineView line_dote_down;
        public LinearLayout ll_container;
        public TextView tv_download;
        public TextView tv_title_score;
        public TextView mTvtitle_textbook;
        public TextView mTvCorretno_textbook;
        public TextView mTvChoose_textbook;
        public TextView mTvCorrectionState_textbook;
        public TextView mTvdefine_textbook;
        public ImageView mIvstate_textbook;
        public Button mBtn_textbook;
        public ImageView mIvNewMessage;
        public TextView mTvNewMessage;
        public TextView mTvNewMessagered;
        public LinearLayout ll_textbook;
        public LinearLayout lltextContent;
    }
    private void typeOneFinById(TypeOneViewHolder holder_01, View convertView) {
        holder_01.mTvtitle=convertView.findViewById(R.id.tv_title);
        holder_01.mTvCorreted=convertView.findViewById(R.id.tv_correct_ed);
        holder_01.mTvCorretno=convertView.findViewById(R.id.tv_correct_no);
        holder_01.mTvCorrectionState=convertView.findViewById(R.id.tv_correction_state);
        holder_01.mBtn=convertView.findViewById(R.id.bt_correction);
        holder_01.mIvstate=convertView.findViewById(R.id.iv_state);
        holder_01.mheadview=convertView.findViewById(R.id.iv_headview);
        holder_01.line_dote_up=convertView.findViewById(R.id.line_dote_up);
        holder_01.line_dote_down=convertView.findViewById(R.id.line_dote_down);
        holder_01.ll_correct_ed=convertView.findViewById(R.id.ll_correct_ed);
        holder_01.tv_correct_right=convertView.findViewById(R.id.tv_correct_right);
        holder_01.tv_correct_wrong=convertView.findViewById(R.id.tv_correct_wrong);

        holder_01.mllSyn=convertView.findViewById(R.id.ll_syn);
        holder_01.mllDefine=convertView.findViewById(R.id.ll_define);
        holder_01.mllCorrecting=convertView.findViewById(R.id.ll_correcting);

        holder_01.mTvtitle_syn=convertView.findViewById(R.id.tv_title_syn);
        holder_01.mTvCorretno_syn=convertView.findViewById(R.id.tv_correct_no_syn);
        holder_01.mBtn_syn=convertView.findViewById(R.id.bt_correction_syn);
        holder_01.mIvstate_syn=convertView.findViewById(R.id.iv_state_syn);
        holder_01.mTvChoose_syn=convertView.findViewById(R.id.tv_choose_syn);

        holder_01.mTvChoose_define=convertView.findViewById(R.id.tv_choose_define);
        holder_01.mTvtitle_define=convertView.findViewById(R.id.tv_title_define);
        holder_01.mTvCorretno_define=convertView.findViewById(R.id.tv_correct_no_define);
        holder_01.mTvCorrectionState_define=convertView.findViewById(R.id.tv_correction_state_define);
        holder_01.mBtn_define=convertView.findViewById(R.id.bt_correction_define);
        holder_01.mIvstate_define=convertView.findViewById(R.id.iv_state_define);
        holder_01.mTvdefine=convertView.findViewById(R.id.tv_define_define);
    }

    private void typeTwoFinById(TypeTwoViewHolder holder_02, View convertView) {
        holder_02.line_dote_up=convertView.findViewById(R.id.line_dote_up);
        holder_02.line_dote_down=convertView.findViewById(R.id.line_dote_down);
        holder_02.tv_download=convertView.findViewById(R.id.tv_download);
        holder_02.ll_container=convertView.findViewById(R.id.ll_container);
        holder_02.ll_textbook=convertView.findViewById(R.id.ll_textbook);
        holder_02.tv_title_score=convertView.findViewById(R.id.tv_title_score);
        holder_02.mTvChoose_textbook=convertView.findViewById(R.id.tv_choose_textbook);
        holder_02.mIvNewMessage=convertView.findViewById(R.id.iv_new);
        holder_02.mTvNewMessage=convertView.findViewById(R.id.tv_title_blew);
        holder_02.mTvNewMessagered=convertView.findViewById(R.id.tv_title_blew_red);
        holder_02.mTvdefine_textbook=convertView.findViewById(R.id.tv_define_textbook);
        holder_02.mTvtitle_textbook=convertView.findViewById(R.id.tv_title_textbook);
        holder_02.mTvCorretno_textbook=convertView.findViewById(R.id.tv_correct_no_textbook);
        holder_02.mTvCorrectionState_textbook=convertView.findViewById(R.id.tv_correction_state_textbook);
        holder_02.mBtn_textbook=convertView.findViewById(R.id.bt_correction_textbook);
        holder_02.mIvstate_textbook=convertView.findViewById(R.id.iv_state_textbook);
        holder_02.lltextContent=convertView.findViewById(R.id.ll_text_content);
    }

    /**
     * 处理type1哪些显示 哪些不显示
     */
    private void changTypeOneView(TypeOneViewHolder holder_01, WeekDatailBean data, final int position) {
        if(data.getUpLineVisible()){
            holder_01.line_dote_up.setVisibility(View.VISIBLE);
        }else {
            holder_01.line_dote_up.setVisibility(View.INVISIBLE);
        }
        if(data.getDowmLineVisible()){
            holder_01.line_dote_down.setVisibility(View.VISIBLE);
        }else {
            holder_01.line_dote_down.setVisibility(View.INVISIBLE);
        }
        if(data.getSourceType().equals("dd_revise")){
            holder_01.mheadview.setImageResource(R.drawable.icon_cuotidingzheng);
            if(data.getVisible()){
                holder_01.mheadview.setVisibility(View.VISIBLE);
            }else {
                holder_01.mheadview.setVisibility(View.GONE);
            }
            holder_01.mllCorrecting.setVisibility(View.VISIBLE);
            holder_01.mllDefine.setVisibility(View.INVISIBLE);
            holder_01.mllSyn.setVisibility(View.INVISIBLE);
            changCorrectingView(holder_01,data,position);
            holder_01.mBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mBtnOnClickListener!=null){
                        mBtnOnClickListener.mBtnOnclick(position,ScorePlanActivity.BTN_TYPE_CORRETING);
                    }
                }
            });
            holder_01.mllCorrecting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mBtnOnClickListener!=null){
                        mBtnOnClickListener.mBtnOnclick(position,ScorePlanActivity.BTN_TYPE_CORRETING);
                    }
                }
            });

        }else  if(data.getSourceType().equals("videocourse")){  //同步课程
            holder_01.mheadview.setImageResource(R.drawable.icon_tongbuweike);
            if(data.getVisible()){
                holder_01.mheadview.setVisibility(View.VISIBLE);
            }else {
                holder_01.mheadview.setVisibility(View.GONE);
            }
            holder_01.mllSyn.setVisibility(View.VISIBLE);
            holder_01.mllCorrecting.setVisibility(View.INVISIBLE);
            holder_01.mllDefine.setVisibility(View.INVISIBLE);
            changWeikeView(holder_01,data,position);

            holder_01.mBtn_syn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mBtnOnClickListener!=null){
                        mBtnOnClickListener.mBtnOnclick(position,ScorePlanActivity.BTN_TYPE_WEIKE);
                    }
                }
            });
            holder_01.mllSyn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mBtnOnClickListener!=null){
                        mBtnOnClickListener.mBtnOnclick(position,ScorePlanActivity.BTN_TYPE_WEIKE);
                    }
                }
            });
        } else {
            holder_01.mheadview.setImageResource(R.drawable.icon_zidyingyi);
            if(data.getVisible()){
                holder_01.mheadview.setVisibility(View.VISIBLE);
            }else {
                holder_01.mheadview.setVisibility(View.GONE);
            }
            holder_01.mllDefine.setVisibility(View.VISIBLE);
            holder_01.mllSyn.setVisibility(View.INVISIBLE);
            holder_01.mllCorrecting.setVisibility(View.INVISIBLE);
            changDefineView(holder_01,data,position);
            holder_01.mBtn_define.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mBtnOnClickListener!=null){
                        mBtnOnClickListener.mBtnOnclick(position,ScorePlanActivity.BTN_TYPE_DEFINE);
                    }
                }
            });
            holder_01.mllDefine.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mBtnOnClickListener!=null){
                        mBtnOnClickListener.mBtnOnclick(position,ScorePlanActivity.BTN_TYPE_DEFINE);
                    }
                }
            });
        }
    }



    /**
     * 处理type2哪些显示 哪些不显示
     */
    private void changTypeTwoView(TypeTwoViewHolder holder_02, WeekDatailBean data, final int position) {
        if(data.getVisible()){
            holder_02.ll_container.setVisibility(View.VISIBLE);
            isfirstVisible=false;
        }else {
            holder_02.ll_container.setVisibility(View.GONE);
        }
        if(data.getName().equals("无数据")){
            holder_02.ll_container.setVisibility(View.VISIBLE);
            holder_02.ll_textbook.setVisibility(View.GONE);
            holder_02.tv_title_score.setText(taskBean.getName());
            if(TextUtils.isEmpty(taskBean.getSuiteNumber()) && TextUtils.isEmpty(taskBean.getUpdateSuiteNumber())){
                holder_02.lltextContent.setVisibility(View.GONE);
            }else {
                String sb=null;
                String sb_red=null;
                if(!TextUtils.isEmpty(taskBean.getSuiteNumber())){
                    sb=getDataString(taskBean.getSuiteNumber());
                }
                if(!TextUtils.isEmpty(taskBean.getUpdateSuiteNumber())){
                    sb_red=getDataString(taskBean.getUpdateSuiteNumber());
                }
                if (!TextUtils.isEmpty(sb)) {
                    if (TextUtils.isEmpty(sb_red)) {
                        holder_02.mTvNewMessage.setText(sb );
                        holder_02.mTvNewMessagered.setText("");
                    } else {
                        holder_02.mTvNewMessage.setText( sb + "、");
                        holder_02.mTvNewMessagered.setText(sb_red );
                    }
                } else {
                    if (TextUtils.isEmpty(sb_red)) {
                        holder_02.mTvNewMessage.setVisibility(View.GONE);
                        holder_02.mTvNewMessagered.setVisibility(View.GONE);
                    } else {
                        holder_02.mTvNewMessage.setText("");
                        holder_02.mTvNewMessagered.setText(sb_red);
                    }
                }
            }
        }else {
            changWeekPracticeView(holder_02,data,position);
            holder_02.mBtn_textbook.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mBtnOnClickListener!=null){
                        mBtnOnClickListener.mBtnOnclick(position,ScorePlanActivity.BTN_TYPE_WEEKPRACTICE);
                    }
                }
            });
            holder_02.ll_textbook.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mBtnOnClickListener!=null){
                        mBtnOnClickListener.mBtnOnclick(position,ScorePlanActivity.BTN_TYPE_WEEKPRACTICE);
                    }
                }
            });
        }
        holder_02.tv_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mBtnOnClickListener!=null){
                    mBtnOnClickListener.mBtnOnclick(position,ScorePlanActivity.BTN_TYPE_ONEKEYDOWN);
                }
            }
        });


        //判断虚线显示状态
        if(data.getDowmLineVisible()){
            holder_02.line_dote_down.setVisibility(View.VISIBLE);
        }else {
            holder_02.line_dote_down.setVisibility(View.INVISIBLE);
        }

        if(data.getUpLineVisible()){
            holder_02.line_dote_up.setVisibility(View.VISIBLE);
        }else {
            holder_02.line_dote_up.setVisibility(View.INVISIBLE);
        }
    }



    /**
     * 错题订正数据初始化
     */
    private void changCorrectingView(TypeOneViewHolder holder_01, WeekDatailBean data, int position) {
        holder_01.mTvtitle.setText(data.getName());
        holder_01.mTvCorretno.setText( String.format( Locale.getDefault(), "待订正%d题",data.getUnReviseCount() ) );
        if(data.getRevisedCount()==0){   //订正数量
            holder_01.ll_correct_ed.setVisibility(View.INVISIBLE);
            holder_01.mBtn.setText("开始订正");
        }else {
            holder_01.ll_correct_ed.setVisibility(View.VISIBLE);
            holder_01.mBtn.setText("继续订正");
            holder_01.mTvCorreted.setText(String.format( Locale.getDefault(), "已订正%d题",data.getRevisedCount() ));
            holder_01.tv_correct_right.setText(data.getRevisedRightCount()+"题,");
            holder_01.tv_correct_wrong.setText(data.getRevisedWrongCount()+" 题)");
        }
        if(data.getOverdue()==1){  // //是否逾期0 未逾期 1逾期
            holder_01.mTvCorrectionState.setText("逾期");
            holder_01.mBtn.setVisibility(View.VISIBLE);
        }else {
            if(data.getDeadDays()>1)
                holder_01.mTvCorrectionState.setText("需"+data.getDeadDays()+"日内完成");
            if (data.getDeadDays()==1){
                holder_01.mTvCorrectionState.setText("需今日内完成");
            }
            holder_01.mBtn.setVisibility(View.VISIBLE);
        }
        if(data.getReviseStatus()==1){      //完成状态 1 已完成 2 已订正
            holder_01.mIvstate.setVisibility(View.VISIBLE);
            holder_01.mBtn.setVisibility(View.INVISIBLE);
            holder_01.mTvCorrectionState.setVisibility(View.INVISIBLE);

        }else {
            holder_01.mIvstate.setVisibility(View.INVISIBLE);
        }
    }
    /**
     * 微课数据初始化
     */
    private void changWeikeView(TypeOneViewHolder holder_01, WeekDatailBean data, int position) {
        holder_01.mTvtitle_syn.setText(data.getName());
        holder_01.mTvCorretno_syn.setText( "《"+data.getTitle()+"》" );
        if(data.isHasWatch()==1){
            holder_01.mBtn_syn.setVisibility(View.INVISIBLE);
            holder_01.mIvstate_syn.setVisibility(View.VISIBLE);
        }else {
            holder_01.mBtn_syn.setVisibility(View.VISIBLE);
            holder_01.mIvstate_syn.setVisibility(View.INVISIBLE);
        }
    }
    /**
     * 自定义数据初始化
     */
    private void changDefineView(TypeOneViewHolder holder_01, WeekDatailBean data, int position) {
        holder_01.mTvtitle_define.setText(data.getName());
        holder_01.mTvCorretno_define.setText( "共"+data.getQuestionCount()+"题");
        holder_01.mTvCorrectionState_define.setVisibility(View.INVISIBLE);
        if(data.getSourceType().equals("dd_period_review")){  //错题浏览本
            if(data.getDownloadStatus()==0){  //0是为下载
                holder_01.mBtn_define.setVisibility(View.VISIBLE);
                holder_01.mIvstate_define.setVisibility(View.INVISIBLE);
                holder_01.mBtn_define.setText("查看详情");
            }else {
                holder_01.mBtn_define.setText("查看详情");
                holder_01.mIvstate_define.setVisibility(View.VISIBLE);
                holder_01.mIvstate_define.setImageResource(R.drawable.icon_yixiazai);
            }
        }else  if(data.getSourceType().equals("dd_week_exercise") || data.getSourceType().equals("exclusive_paper")){  //错题再练本
            if(data.getExerStatus()<2){ //0,查看详情 1,待上传，2,待批阅，3,批阅中，4,已批阅，5，统计完成*/
                holder_01.mBtn_define.setVisibility(View.VISIBLE);
                holder_01.mIvstate_define.setVisibility(View.INVISIBLE);
                holder_01.mBtn_define.setText("拍照上传");
                holder_01.mBtn_define.setTextColor(context.getResources().getColor(R.color.color_0B9AA8));
            }else  if(data.getExerStatus()==2|| data.getExerStatus()==3){
                holder_01.mBtn_define.setVisibility(View.VISIBLE);
                holder_01.mBtn_define.setText("批阅中");
                holder_01.mBtn_define.setTextColor(context.getResources().getColor(R.color.color_0B9AA8));
                holder_01.mIvstate_define.setVisibility(View.VISIBLE);
                holder_01.mIvstate_define.setImageResource(R.drawable.icon_yishangchuan);
            }else  if(data.getExerStatus()>=4){
                holder_01.mBtn_define.setVisibility(View.VISIBLE);
                holder_01.mBtn_define.setText("查看批阅结果");
                holder_01.mBtn_define.setTextColor(context.getResources().getColor(R.color.color_F5A623));
                holder_01.mIvstate_define.setVisibility(View.VISIBLE);
                holder_01.mIvstate_define.setImageResource(R.drawable.icon_yishangchuan);
            }
        }
    }
    /**
     * 周练习数据初始化
     */
    private void changWeekPracticeView(TypeTwoViewHolder holder_02, WeekDatailBean data, int position) {
        holder_02.mTvtitle_textbook.setText(data.getName());
        holder_02.mTvCorretno_textbook.setText("共" + data.getQuestionCount() + "题");
        if (data.getSourceType().equals("dd_period_review")) {  //错题浏览本
            holder_02.mTvCorrectionState_textbook.setVisibility(View.GONE);
            if (data.getDownloadStatus()==0) {  //0是为下载
                holder_02.mTvChoose_textbook.setVisibility(View.VISIBLE);
                holder_02.mBtn_textbook.setVisibility(View.VISIBLE);
                holder_02.mIvstate_textbook.setVisibility(View.INVISIBLE);
                holder_02.mBtn_textbook.setText("查看详情");
            } else {
                holder_02.mBtn_textbook.setText("查看详情");
                holder_02.mIvstate_textbook.setVisibility(View.VISIBLE);
                holder_02.mIvstate_textbook.setImageResource(R.drawable.icon_yixiazai);
            }
        } else if (data.getSourceType().equals("dd_week_exercise") || data.getSourceType().equals("exclusive_paper")) {  //错题再练本
            if (data.getSourceType().equals("dd_week_exercise")) {  //错题再练本
                holder_02.mTvCorrectionState_textbook.setVisibility(View.VISIBLE);
                if(data.getDeadDays()>1){
                    holder_02.mTvCorrectionState_textbook.setText("需"+data.getDeadDays()+"日内完成");
                }
                if (data.getDeadDays()==1){
                    holder_02.mTvCorrectionState_textbook.setText("需今日内完成");
                }
                holder_02.mTvChoose_textbook.setVisibility(View.INVISIBLE);
                holder_02.mTvdefine_textbook.setText("第二部分");
            }else {
                holder_02.mTvCorrectionState_textbook.setVisibility(View.GONE);
                holder_02.mTvChoose_textbook.setVisibility(View.VISIBLE);
                holder_02.mTvdefine_textbook.setText("第三部分");
            }
            if(taskBean.getHasNewReport()){
                holder_02.mIvNewMessage.setVisibility(View.VISIBLE);
            }else {
                holder_02.mIvNewMessage.setVisibility(View.INVISIBLE);
            }
            if(taskBean!=null) {
                holder_02.tv_title_score.setText(taskBean.getName());
                String sb=null;
                String sb_red=null;
                if(!TextUtils.isEmpty(taskBean.getSuiteNumber())){
                    sb=getDataString(taskBean.getSuiteNumber());
                }
                if(!TextUtils.isEmpty(taskBean.getUpdateSuiteNumber())){
                    sb_red=getDataString(taskBean.getUpdateSuiteNumber());
                }
                if (!TextUtils.isEmpty(sb)) {
                    if (TextUtils.isEmpty(sb_red)) {
                        holder_02.mTvNewMessage.setText(sb );
                        holder_02.mTvNewMessagered.setText("");
                    } else {
                        holder_02.mTvNewMessage.setText( sb + "、");
                        holder_02.mTvNewMessagered.setText(sb_red );
                    }
                } else {
                    if (TextUtils.isEmpty(sb_red)) {
                        holder_02.mTvNewMessage.setVisibility(View.GONE);
                        holder_02.mTvNewMessagered.setVisibility(View.GONE);
                    } else {
                        holder_02.mTvNewMessage.setText("");
                        holder_02.mTvNewMessagered.setText(sb_red);
                    }
                }
            }
            if (data.getExerStatus() <2) {  // 0：未提交，1：已提交，4:已批阅，5:已统计可看报告
                holder_02.mBtn_textbook.setVisibility(View.VISIBLE);
                holder_02.mIvstate_textbook.setVisibility(View.INVISIBLE);
                holder_02.mBtn_textbook.setText("拍照上传");
                holder_02.mBtn_textbook.setTextColor(context.getResources().getColor(R.color.color_0B9AA8));
            } else if (data.getExerStatus() == 2|| data.getExerStatus() == 3) {
                holder_02.mBtn_textbook.setVisibility(View.VISIBLE);
                holder_02.mBtn_textbook.setText("批阅中");
                holder_02.mTvCorrectionState_textbook.setVisibility(View.INVISIBLE);
                holder_02.mBtn_textbook.setTextColor(context.getResources().getColor(R.color.color_0B9AA8));
                holder_02.mIvstate_textbook.setVisibility(View.VISIBLE);
                holder_02.mIvstate_textbook.setImageResource(R.drawable.icon_yishangchuan);
            } else if (data.getExerStatus() == 4 || data.getExerStatus() == 5) {
                holder_02.mBtn_textbook.setVisibility(View.VISIBLE);
                holder_02.mBtn_textbook.setText("查看批阅结果");
                holder_02.mTvCorrectionState_textbook.setVisibility(View.INVISIBLE);
                holder_02.mBtn_textbook.setTextColor(context.getResources().getColor(R.color.color_F5A623));
                holder_02.mIvstate_textbook.setVisibility(View.VISIBLE);
                holder_02.mIvstate_textbook.setImageResource(R.drawable.icon_yishangchuan);
            }
            if(data.getOverdue()==1){
                if(data.getSourceType().equals("dd_week_exercise")){
                    holder_02.mTvCorrectionState_textbook.setVisibility(View.VISIBLE);
                }
                holder_02.mTvCorrectionState_textbook.setText("逾期");
            }
        }
    }

    private String getDataString(String suiteNumber) {
        String data=suiteNumber.replace("1","周学习分析").replace("2","周错题再练本").replace("3","周变式训练本").replace("4","周错题浏览本");
        return data;
    }

    public void setTaskBean(TaskBean taskBean) {
        this.taskBean = taskBean;
    }

    public interface mBtnOnClickListener{
        void  mBtnOnclick(int position,int type);   //1错题  ，2 是微课 3 自定义 4是周训练
    }
    private  mBtnOnClickListener mBtnOnClickListener;

    public void setmBtnOnClickListener(PersonDetailsAdapter.mBtnOnClickListener mBtnOnClickListener) {
        this.mBtnOnClickListener = mBtnOnClickListener;
    }
}
