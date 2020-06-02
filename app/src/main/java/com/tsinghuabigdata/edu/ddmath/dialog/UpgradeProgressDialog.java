package com.tsinghuabigdata.edu.ddmath.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.util.DensityUtils;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.LogUtils;


/**
 * 发现新版本Dialog
 */
public class UpgradeProgressDialog extends Dialog {


    private TextView                mTvProgressTitle;
    private TextView                mTvProgressPercent;
    private ProgressBar             mProgress;
    private Button                  mBtnInstall;
    private UpgradeProgressListener mUpgradeProgressListener;

    private boolean mForce;

    public UpgradeProgressDialog(Context context) {
        super(context);
        initData();
    }

    public UpgradeProgressDialog(Context context, int theme) {
        super(context, theme);
        initData();
    }


    private void initData() {
        if (GlobalData.isPad()) {
            setContentView(R.layout.dialog_upgrade_progress);
        } else {
            setContentView(R.layout.dialog_upgrade_progress_phone);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            if( window!=null ){
                window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            }
        }
        mTvProgressTitle = (TextView) findViewById(R.id.tv_progress_title);
        mTvProgressPercent = (TextView) findViewById(R.id.tv_progress_percent);
        mProgress = (ProgressBar) findViewById(R.id.progress);
        mBtnInstall = (Button) findViewById(R.id.btn_install);
        mProgress.setMax(100);
        setCancelable(false);
        setCanceledOnTouchOutside(false);

        mBtnInstall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUpgradeProgressListener != null) {
                    mUpgradeProgressListener.install();
                }
            }
        });

        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        int w = GlobalData.isPad() ? 591 : 329;
        int h = GlobalData.isPad() ? 133 : 74;
        lp.width = DensityUtils.dp2px(getContext(), w);
        lp.height = DensityUtils.dp2px(getContext(), h);
        lp.dimAmount = 0.5f;
        dialogWindow.setAttributes(lp);
        //        dialogWindow.addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
        dialogWindow.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }


    /**
     * 普通提示框
     */
    public void setInfo(String message) {
        mTvProgressTitle.setText(message);
    }


    public void setProgress(int progress) {
        mProgress.setProgress(progress);
    }

    public void setMessage(final String s) {
        //Log.i("sky", "s=" + s);
//        mTvProgressPercent.setText(s);
        mTvProgressTitle.post(new Runnable() {
            @Override
            public void run() {
                mTvProgressPercent.setText(s);
            }
        });

    }

    public void setAfterDownload() {
        LogUtils.i("sky", "setAfterDownload start");
        if (mForce) {
            setForceInstall();
        } else {
            dismiss();
        }
        LogUtils.i("sky", "setAfterDownload finish");
    }

    private void setForceInstall() {
        mTvProgressTitle.post(new Runnable() {
            @Override
            public void run() {
                mTvProgressTitle.setText("已下载完成，请安装");
                mTvProgressPercent.setText("100%");
                mProgress.setProgress(100);
                mBtnInstall.setVisibility(View.VISIBLE);
            }
        });
    }

    public interface UpgradeProgressListener {
        void install();
    }

    public void setUpgradeProgressListener(UpgradeProgressListener upgradeProgressListener) {
        mUpgradeProgressListener = upgradeProgressListener;
    }

    public void setForce(boolean force) {
        mForce = force;
    }
}
