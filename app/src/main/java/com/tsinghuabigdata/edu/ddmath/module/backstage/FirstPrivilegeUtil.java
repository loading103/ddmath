package com.tsinghuabigdata.edu.ddmath.module.backstage;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;

import com.tsinghuabigdata.edu.ddmath.MVPModel.RequestListener;
import com.tsinghuabigdata.edu.ddmath.MVPModel.UserCenterModel;
import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.activity.MainActivity;
import com.tsinghuabigdata.edu.ddmath.bean.LoginInfo;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.dialog.CustomDialog;
import com.tsinghuabigdata.edu.ddmath.event.JumpEvent;
import com.tsinghuabigdata.edu.ddmath.module.backstage.bean.FirstPrivilegeBean;
import com.tsinghuabigdata.edu.ddmath.module.mycenter.fragment.UserCenterFragment;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.AlertManager;
import com.tsinghuabigdata.edu.ddmath.util.DensityUtils;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;

import org.greenrobot.eventbus.EventBus;

/**
 * 用户首次登陆免费获得的用户权益 工具类
 */

public class FirstPrivilegeUtil {

    public void startQuery( final Activity activity ){
        if( activity == null ) return;

        final LoginInfo loginInfo = AccountUtils.getLoginUser();
        if( loginInfo == null ) return;

        //判断是否查询过
        String tag = "user_first_privilege";
        final SharedPreferences sp = activity.getSharedPreferences( tag, Activity.MODE_PRIVATE);
        if( sp == null ) return;
        boolean queryStatus = sp.getBoolean( loginInfo.getAccountId(), false );
        //已经查询显示过
        if( queryStatus ) return;

        //没有，则进行查询显示
        new UserCenterModel().queryUserFirstPrivilege( loginInfo.getAccountId(), new RequestListener<FirstPrivilegeBean>() {
            @Override
            public void onSuccess(FirstPrivilegeBean bean) {
                //界面已销毁
                if( activity.isDestroyed() || activity.isFinishing() ) return;

                //先记录
                sp.edit().putBoolean( loginInfo.getAccountId(), true ).apply();

                //已显示 or 非新用户
                if( !bean.getNewUser() || bean.getAlreadyShowed() ) return;

                CustomDialog dialog = AlertManager.showCustomDialog( activity, "","好哒", "查看特权详情", null, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //跳转用户中心个人信息界面
                        EventBus.getDefault().post(new JumpEvent(MainActivity.FRAGMENT_MY_CENTER, UserCenterFragment.MODEL_MYINFO));
                    }
                });
                dialog.setRightBtnAttr(R.drawable.bg_rect_blue_r24, R.color.white);

                //新增 引导家长去微信公众号
                dialog.showUserPriviledgeView();

                String data = bean.getAlterTitle();
//                String data = "感谢您的注册，恭喜您获得@21天@价值@199元的新用户免费使用特权@，开启您的学霸之旅吧！";

                String dataArray[] = data.split("@");
                if( dataArray.length!=5 ) return;

                String keyStr1   = dataArray[1];
                String keyStr2   = dataArray[3];

                Spannable span = new SpannableString( data.replaceAll("@",""));
                int start = dataArray[0].length(), end = start+keyStr1.length();
                span.setSpan( new AbsoluteSizeSpan(DensityUtils.sp2px( activity, GlobalData.isPad()?30:20 )), start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                span.setSpan( new ForegroundColorSpan( activity.getResources().getColor(R.color.color_F97F3A)), start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                //整体粗体
                span.setSpan( new StyleSpan(Typeface.BOLD), start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

                start   = end + dataArray[2].length();
                end     = start + keyStr2.length();
                span.setSpan( new AbsoluteSizeSpan(DensityUtils.sp2px( activity, GlobalData.isPad()?30:20 )), start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                span.setSpan( new ForegroundColorSpan(activity.getResources().getColor(R.color.color_F97F3A)), start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                //整体粗体
                span.setSpan( new StyleSpan(Typeface.BOLD), start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

                dialog.setTextView( span );
                dialog.show();
            }

            @Override
            public void onFail(HttpResponse<FirstPrivilegeBean> response, Exception ex) {
            }
        });

    }

}
