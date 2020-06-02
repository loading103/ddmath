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
import com.tsinghuabigdata.edu.ddmath.event.GotoCreateWorkEvent;
import com.tsinghuabigdata.edu.ddmath.event.JumpEvent;
import com.tsinghuabigdata.edu.ddmath.fragment.MyStudyFragment;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;

import org.greenrobot.eventbus.EventBus;

/**
 * 上传作业引导Dialog
 * Created by Administrator on 2018/3/10.
 */

public class UploadGuideDialog extends Dialog implements View.OnClickListener {


    private ImageView mIvClose;
    private ImageView mIvDirectUpload;
    private ImageView mIvGiveAndUpload;

    private boolean hasMyWork = true;


    public UploadGuideDialog(@NonNull Context context) {
        super(context, R.style.dialog);
        initData();
    }


    private void initData() {
        if (GlobalData.isPad()) {
            setContentView(R.layout.dialog_upload_guide);
        } else {
            setContentView(R.layout.dialog_upload_guide_phone);
        }
        mIvClose = (ImageView) findViewById(R.id.iv_close);
        mIvDirectUpload = (ImageView) findViewById(R.id.iv_direct_upload);
        mIvGiveAndUpload = (ImageView) findViewById(R.id.iv_give_and_upload);
        mIvDirectUpload.setEnabled(hasMyWork);
        mIvClose.setOnClickListener(this);
        mIvDirectUpload.setOnClickListener(this);
        mIvGiveAndUpload.setOnClickListener(this);

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
            case R.id.iv_direct_upload:
                EventBus.getDefault().post(new JumpEvent(MainActivity.FRAGMENT_STUDY_TASK, MyStudyFragment.MODEL_SCHOOLWORK));
                dismiss();
                break;
            case R.id.iv_give_and_upload:
                EventBus.getDefault().post(new JumpEvent(MainActivity.FRAGMENT_STUDY_TASK, MyStudyFragment.MODEL_CHECK_WORK));
                EventBus.getDefault().post(new GotoCreateWorkEvent());
                dismiss();
                break;
            default:
                dismiss();
                break;
        }
    }

    public boolean isHasMyWork() {
        return hasMyWork;
    }

    public void setHasMyWork(boolean hasMyWork) {
        this.hasMyWork = hasMyWork;
        if (mIvDirectUpload != null) {
            mIvDirectUpload.setEnabled(hasMyWork);
        }
    }
}
