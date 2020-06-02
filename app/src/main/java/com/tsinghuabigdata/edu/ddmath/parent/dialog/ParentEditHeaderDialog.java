package com.tsinghuabigdata.edu.ddmath.parent.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.tsinghuabigdata.edu.ddmath.R;

/**
 * 家长端--更换用户头像
 */

public class ParentEditHeaderDialog extends Dialog implements View.OnClickListener{

    private View.OnClickListener cameraListener;
    private View.OnClickListener picsListener;

    public ParentEditHeaderDialog(@NonNull Context context) {
        super(context,R.style.dialog);
        initView();
    }

//    public ParentEditHeaderDialog(@NonNull Context context, int themeResId) {
//        super(context, themeResId);
//        init();
//    }
//
//    protected ParentEditHeaderDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
//        super(context, cancelable, cancelListener);
//        init();
//    }

    public void setListener(View.OnClickListener cameraListener, View.OnClickListener picsListener ){
        this.cameraListener = cameraListener;
        this.picsListener   = picsListener;
    }

    @Override
    public void onClick(View v) {
        switch ( v.getId() ){
            case R.id.btn_camera:
                if( cameraListener!=null )cameraListener.onClick( v );
                dismiss();
                break;
            case R.id.btn_pics:{
                if( picsListener!=null )picsListener.onClick( v );
                dismiss();
                break;
            }
            case R.id.btn_cancel:
                dismiss();
                break;
            default:
                break;
        }
    }
    //----------------------------------------------------------------------------------------
    private void initView(){
        setContentView(R.layout.activity_parent_editheader);

        TextView cameraView = (TextView)findViewById( R.id.btn_camera );
        TextView picsView   = (TextView)findViewById( R.id.btn_pics );
        TextView cancelView = (TextView)findViewById( R.id.btn_cancel );

        cameraView.setOnClickListener( this );
        picsView.setOnClickListener( this );
        cancelView.setOnClickListener( this );

        setCanceledOnTouchOutside(false);
        Window dialogWindow = getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
        dialogWindow.setWindowAnimations(R.style.recharge_dialog_anim);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = getContext().getResources().getDisplayMetrics().widthPixels; // 宽度
        lp.dimAmount = 0.5f;
        dialogWindow.setAttributes(lp);
        dialogWindow.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }



}
