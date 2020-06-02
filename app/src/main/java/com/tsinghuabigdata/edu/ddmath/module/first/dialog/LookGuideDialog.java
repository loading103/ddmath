package com.tsinghuabigdata.edu.ddmath.module.first.dialog;

import android.app.Dialog;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.activity.MainActivity;
import com.tsinghuabigdata.edu.ddmath.event.JumpEvent;
import com.tsinghuabigdata.edu.ddmath.fragment.MyReportFragment;
import com.tsinghuabigdata.edu.ddmath.fragment.MyStudyFragment;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;

import org.greenrobot.eventbus.EventBus;


/**
 * 首页查看知识谱图/作业 引导Dialog
 * Created by Administrator on 2018/3/10.
 */

public class LookGuideDialog extends Dialog implements View.OnClickListener {


    private ImageView mIvClose;
    private ImageView mIvKnowledge;
    private ImageView mIvWorkReport;
    private ImageView mIvWorkDetail;
    private boolean   lastCheckWork;


    public LookGuideDialog(@NonNull Context context) {
        super(context, R.style.dialog);
        initData();
    }


    private void initData() {
        if (GlobalData.isPad()) {
            setContentView(R.layout.dialog_look_guide);
        } else {
            setContentView(R.layout.dialog_look_guide_phone);
        }
        mIvClose = (ImageView) findViewById(R.id.iv_close);
        mIvKnowledge = (ImageView) findViewById(R.id.iv_knowledge);
        mIvWorkReport = (ImageView) findViewById(R.id.iv_work_report);
        mIvWorkDetail = (ImageView) findViewById(R.id.iv_work_detail);
        mIvClose.setOnClickListener(this);
        mIvKnowledge.setOnClickListener(this);
        mIvWorkReport.setOnClickListener(this);
        mIvWorkDetail.setOnClickListener(this);

        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.dimAmount = 0.5f;
        dialogWindow.setAttributes(lp);
        dialogWindow.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_close:
                dismiss();
                break;
            case R.id.iv_knowledge:
                //跳转到知识图谱
                EventBus.getDefault().post(new JumpEvent(MainActivity.FRAGMENT_STUDY_CONDITION, MyReportFragment.KNOWLEDGE_I));
                dismiss();
                break;
            case R.id.iv_work_report:
                //跳转到作业/考试报告
                EventBus.getDefault().post(new JumpEvent(MainActivity.FRAGMENT_STUDY_CONDITION, MyReportFragment.WORK_I));
                dismiss();
                break;
            case R.id.iv_work_detail:
                //跳转到作业/考试详情 或者 豆豆检查作业
                if (lastCheckWork) {
                    EventBus.getDefault().post(new JumpEvent(MainActivity.FRAGMENT_STUDY_TASK, MyStudyFragment.MODEL_CHECK_WORK));
                } else {
                    EventBus.getDefault().post(new JumpEvent(MainActivity.FRAGMENT_STUDY_TASK, MyStudyFragment.MODEL_SCHOOLWORK));
                }
                dismiss();
                break;
            default:
                dismiss();
                break;
        }
    }


    public void setLastCheckWork(boolean lastCheckWork) {
        this.lastCheckWork = lastCheckWork;
    }
}
