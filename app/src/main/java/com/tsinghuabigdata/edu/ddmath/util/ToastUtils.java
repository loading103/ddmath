package com.tsinghuabigdata.edu.ddmath.util;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.Interpolator;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tsinghuabigdata.edu.ddmath.R;


/**
 * Toast统一管理类
 * Created by Administrator on 2016/12/13.
 */

public class ToastUtils {

    /**
     * 短时间显示Toast
     */
    public static void showShort(Context context, CharSequence message) {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    /**
     * 短时间显示Toast
     */
    public static void showShort(Context context, int message) {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    /**
     * 长时间显示Toast
     */
    public static void showLong(Context context, CharSequence message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

//    /**
//     * 长时间显示Toast
//     */
//    public static void showLong(Context context, int message) {
//        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
//    }

    /**
     * 自定义显示Toast时间
     */
    public static void show(Context context, CharSequence message, int duration) {
        Toast.makeText(context, message, duration).show();
    }
    public static void show(Context context, CharSequence message ) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
    public static void show(Context context, int resid ) {
        Toast.makeText(context, context.getText(resid), Toast.LENGTH_SHORT).show();
    }
    /**
     * 自定义显示Toast时间
     */
    public static void show(Context context, int message, int duration) {
        Toast.makeText(context, message, duration).show();
    }

    /**
     * 积分兑换学豆成功提示
     */
    public static void showScoreExchangeXueDD(Context context) {
        final Toast toast = new Toast(context);
        View root = View.inflate(context, R.layout.toast_score_exchange_xuedou, null);
        toast.setView(root);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
    }

    /**
     * 显示积分增加动画效果
     *
     */
    public static void showPoint(Context context, CharSequence message) {
        final Toast toast = new Toast(context);
        View root = View.inflate(context, R.layout.toast_point, null);
        RelativeLayout mainLayout = root.findViewById(R.id.main_layout);
        TextView tvToast = root.findViewById(R.id.tv_content);
        tvToast.setText(message);
        toast.setView(root);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.show();
        startAnimation(mainLayout,context);
        tvToast.postDelayed(new Runnable() {
            @Override
            public void run() {
                toast.cancel();
            }
        },2000);
    }

    private static void startAnimation(View tvToast, Context context) {
        int dis = DensityUtils.dp2px(context, 120);
        TranslateAnimation ta = new TranslateAnimation(0, 0, 0, 0,
                Animation.ABSOLUTE/*RELATIVE_TO_SELF*/, dis,
                Animation.ABSOLUTE, -dis);
        AlphaAnimation aa = new AlphaAnimation(0, 1);
        AnimationSet set = new AnimationSet(true);
        set.addAnimation(ta);
        set.addAnimation(aa);
        set.setInterpolator(new Interpolator(){
            public float getInterpolation(float input) {
                if (input < 0.4f) {
                    return input * 10 / 4;
                } else {
                    return 1;
                }
            }
        });
        set.setDuration(2000);
        set.setFillAfter(true);
        tvToast.startAnimation(set);
    }


    /**
     * 显示在屏幕中间
     * @param context 上下文
     * @param msg     消息内容
     */
    public static void showToastCenter(Context context, String msg ) {
        Toast toast = new Toast(context);
        View root = View.inflate(context, GlobalData.isPad()?R.layout.toast_text:R.layout.toast_text_phone, null);
        TextView textView = root.findViewById( R.id.tv_point );
        textView.setText( msg );
        toast.setView(root);
        toast.setGravity(Gravity.CENTER, 0, 0 );
        toast.setDuration(Toast.LENGTH_LONG);
        toast.show();
    }

    /**
     * 显示上传结果，显示在右下角
     * @param context 上下文
     * @param success     true：上传成功 false:失败
     */
    public static void showToastUploadResult(final Context context, boolean success ) {

        View root = View.inflate(context, R.layout.toast_upload, null);
        TextView uploadView = root.findViewById( R.id.toast_upload_text );
        TextView detailView = root.findViewById( R.id.toast_upload_detail );
        //ImageView imageView = root.findViewById( R.id.toast_upload_image );

        if( success ){
            uploadView.setText( "上传成功!" );
            detailView.setVisibility(View.GONE);
            //imageView.setImageResource( R.drawable.bg_doudou_success );
        }else{
            uploadView.setText( "上传失败!" );
            detailView.setVisibility(View.VISIBLE);
            detailView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppLog.d("dhjfdhjfhdjfh ff");
//                    Intent intent = new Intent(context, WaitUploadActivity.class);
//                    intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
//                    context.startActivity( intent );
                }
            });

            //imageView.setImageResource( R.drawable.bg_doudou_tips_fail );
        }

//        Snackbar.make( root, "这是massage", Snackbar.LENGTH_LONG ).setAction("这是action", new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText( context,"你点击了action",Toast.LENGTH_SHORT).show();
//            }
//        }).show();

//        SuperToast superToast = new SuperToast(context);
//        superToast.set  root);
//        superToast.setGravity( Gravity.BOTTOM|Gravity.RIGHT, 0, 50 );
//        superToast.setDuration(Toast.LENGTH_LONG);
//        superToast.show();

        Toast toast = new Toast(context);
        toast.setView(root);
        toast.setGravity( Gravity.BOTTOM|Gravity.END, 0, 50 );
        toast.setDuration(Toast.LENGTH_LONG);
        toast.show();
    }

}
