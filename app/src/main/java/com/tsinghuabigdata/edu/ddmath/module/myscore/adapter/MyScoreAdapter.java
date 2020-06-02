package com.tsinghuabigdata.edu.ddmath.module.myscore.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.activity.MainActivity;
import com.tsinghuabigdata.edu.ddmath.bean.MyTutorClassInfo;
import com.tsinghuabigdata.edu.ddmath.event.JumpEvent;
import com.tsinghuabigdata.edu.ddmath.fragment.MyStudyFragment;
import com.tsinghuabigdata.edu.ddmath.module.myscore.bean.MyScoreBean;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.AlertManager;
import com.tsinghuabigdata.edu.ddmath.util.DateUtils;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.PicassoUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.Locale;

import javax.annotation.Nonnull;

/**
 * 我的积分项
 */

public class MyScoreAdapter extends ArrayAdapter<MyScoreBean> {

    public MyScoreAdapter(Context context) {
        super(context,0);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @Nonnull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate( GlobalData.isPad() ? R.layout.item_myscore : R.layout.item_myscore_phone, parent, false);
            convertView.setTag(new ViewHolder(convertView));
        }
        ViewHolder viewHolder = ((ViewHolder) convertView.getTag());
        viewHolder.bindView( getItem(position), position);
        return convertView;
    }

    class ViewHolder implements View.OnClickListener {

        private int position;

        //标题类
        private RelativeLayout titleLayout;
        private TextView titleView;
        private TextView tipsView;

        private LinearLayout contentLayout;
        private ImageView imageView;
        private TextView nameTextView;
        private TextView ruleView;

        private Button gotoFinishBtn;
        private TextView gainScoreView;

        private LinearLayout emptyLayout;

        private ViewHolder(View convertView) {

            titleLayout = convertView.findViewById( R.id.layout_type_title );
            titleView = convertView.findViewById( R.id.tv_type_title );
            tipsView = convertView.findViewById( R.id.tv_type_tips );

            contentLayout = convertView.findViewById( R.id.layout_score_content );
            imageView = convertView.findViewById(R.id.iv_event_image);
            nameTextView = convertView.findViewById(R.id.tv_event_name);
            ruleView = convertView.findViewById(R.id.tv_event_rule);
            gotoFinishBtn = convertView.findViewById(R.id.btn_go_getscore);
            gainScoreView = convertView.findViewById(R.id.tv_gain_score);

            emptyLayout = convertView.findViewById( R.id.layout_emptydata );

            gotoFinishBtn.setOnClickListener(this);
        }

        public void bindView(MyScoreBean item, int position) {
            this.position = position;

            //推荐积分标题
            if( MyScoreBean.TYPE_COMMAND_TITLE == item.getType() ){
                titleLayout.setVisibility( View.VISIBLE );
                contentLayout.setVisibility( View.GONE );
                emptyLayout.setVisibility(View.GONE);

                titleView.setText( getContext().getText(R.string.recommand_score));
                tipsView.setText( getContext().getText(R.string.more_score_tips));
            }
            //已得积分标题
            else if( MyScoreBean.TYPE_RECORD_TITLE == item.getType() ){
                titleLayout.setVisibility( View.VISIBLE );
                contentLayout.setVisibility( View.GONE );
                emptyLayout.setVisibility(View.GONE);

                titleView.setText( getContext().getText(R.string.gained_score));
                tipsView.setText( getContext().getText(R.string.gained_score_tips));
            }
            //空数据
            else if( MyScoreBean.TYPE_EMPTY_DATA == item.getType() ){
                titleLayout.setVisibility( View.GONE );
                contentLayout.setVisibility( View.GONE );
                emptyLayout.setVisibility(View.VISIBLE);
            }
            //显示积分项
            else{
                titleLayout.setVisibility( View.GONE );
                contentLayout.setVisibility( View.VISIBLE );
                emptyLayout.setVisibility(View.GONE);
                //加载图像
                PicassoUtil.displayImageIndetUrl(item.getImgPath(), imageView);

                //名称
                nameTextView.setText(item.getEventName());

                if (MyScoreBean.TYPE_COMMAND_ITEM == item.getType()) {
                    ruleView.setText(String.format(Locale.getDefault(), "单次积分+%d（每%s%d次）", item.getPointAmt(), item.getUseUnitStr(), item.getUseLimit()));

                    gotoFinishBtn.setVisibility(View.VISIBLE);
                    gainScoreView.setVisibility(View.GONE);

                    if( MyScoreBean.ST_TASK_NONE == item.getStatus() ){
                        gotoFinishBtn.setEnabled(false);
                        gotoFinishBtn.setText("去完成");
                    }else if (MyScoreBean.ST_TASK_FINISHED == item.getStatus()) {
                        gotoFinishBtn.setEnabled(false);
                        gotoFinishBtn.setText("已完成");
                    } else {
                        gotoFinishBtn.setEnabled(true);
                        gotoFinishBtn.setText("去完成");
                    }
                } else {
                    ruleView.setText(DateUtils.format(item.getCreateTime(), DateUtils.FORMAT_DATA_TIME));

                    gotoFinishBtn.setVisibility(View.GONE);
                    gainScoreView.setVisibility(View.VISIBLE);
                    gainScoreView.setText(String.format(Locale.getDefault(), "积分 +%d", item.getPointAmt()));
                }
            }
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.btn_go_getscore) {

                MyScoreBean scoreBean = getItem( position );
                MyTutorClassInfo classInfo = AccountUtils.getCurrentClassInfo();
                if( scoreBean == null || classInfo == null) return;

                if( scoreBean.getEventName().startsWith("按时提交作业") ){
                    if( !TextUtils.isEmpty(scoreBean.getClassId()) && !classInfo.getClassId().equals( scoreBean.getClassId()) ){
                        String msg = String.format( Locale.getDefault(),"新的作业／考试所在班级：%s%s；可去切换班级完成作业，获得积分哦～", scoreBean.getSchoolName(),scoreBean.getClassName() );
                        AlertManager.showCustomImageBtnDialog( getContext(), msg, "确定", null, null);
                    }else{
                        EventBus.getDefault().post(new JumpEvent(MainActivity.FRAGMENT_STUDY_TASK, MyStudyFragment.MODEL_SCHOOLWORK));
                    }
                }else if( scoreBean.getEventName().startsWith("上传自我诊断") ){
                    EventBus.getDefault().post(new JumpEvent(MainActivity.FRAGMENT_STUDY_TASK, MyStudyFragment.MODEL_CHECK_WORK));
                }else if( scoreBean.getEventName().startsWith("订正错题") ){
                    EventBus.getDefault().post(new JumpEvent(MainActivity.FRAGMENT_STUDY_TASK, MyStudyFragment.MODEL_ERRORREVISE));
                }else if( scoreBean.getEventName().startsWith("查看诊断结果") ){
                    if( !TextUtils.isEmpty(scoreBean.getClassId()) && !classInfo.getClassId().equals( scoreBean.getClassId()) ){
                        String msg = String.format( Locale.getDefault(),"新的作业／考试诊断结果所在班级：%s%s；可去切换班级完成作业，获得积分哦～", scoreBean.getSchoolName(),scoreBean.getClassName() );
                        AlertManager.showCustomImageBtnDialog( getContext(), msg, "确定", null, null);
                    }else{
                        //2、如果作业／考试界面的诊断结果已经看完，则跳转到自我诊断查看诊断结果
                        EventBus.getDefault().post(new JumpEvent(MainActivity.FRAGMENT_STUDY_TASK, scoreBean.getToUrl()==0?MyStudyFragment.MODEL_SCHOOLWORK:MyStudyFragment.MODEL_CHECK_WORK));
                    }
                }
            }
        }

    }
}
