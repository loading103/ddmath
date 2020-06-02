package com.tsinghuabigdata.edu.ddmath.module.message.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.tsinghuabigdata.edu.ddmath.MVPModel.MessageModel;
import com.tsinghuabigdata.edu.ddmath.MVPModel.RequestListener;
import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.activity.MainActivity;
import com.tsinghuabigdata.edu.ddmath.activity.RoboActivity;
import com.tsinghuabigdata.edu.ddmath.bean.LoginInfo;
import com.tsinghuabigdata.edu.ddmath.bean.UserDetailinfo;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.dialog.NewTopMessageDialog;
import com.tsinghuabigdata.edu.ddmath.event.TopMessageListEvent;
import com.tsinghuabigdata.edu.ddmath.module.message.TopMessageUtils;
import com.tsinghuabigdata.edu.ddmath.module.message.bean.MessageInfo;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * 首页消息服务
 * Created by Administrator on 2017/10/30.
 */

public class TopMessagService extends Service {


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        //LogUtils.i("TopMessagService onCreate");
        super.onCreate();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        //LogUtils.i("TopMessagService onStart");
        super.onStart(intent, startId);
        queryHomeMsg();
    }

    private void queryHomeMsg() {
        UserDetailinfo userdetailInfo = AccountUtils.getUserdetailInfo();
        LoginInfo loginUser = AccountUtils.getLoginUser();
        if (userdetailInfo == null || loginUser == null) {
            return;
        }
        new MessageModel().queryHomeMsg(loginUser.getAccessToken(), userdetailInfo.getStudentId(), "student", new RequestListener<List<MessageInfo>>() {
            @Override
            public void onSuccess(List<MessageInfo> res) {
                //LogUtils.i("queryHomeMsg onSuccess");
                List<MessageInfo> homeList = TopMessageUtils.isHomeMsgAvailable(res);
                if (homeList != null && homeList.size() > 0) {
                    //LogUtils.i("queryHomeMsg homeList.size()="+homeList.size());
                    goToNewTopMessageActivity(homeList);
                }
                List<MessageInfo> bannerList = TopMessageUtils.isBannerMsgAvailable(res);
                if (bannerList != null && bannerList.size() > 0) {
                    //LogUtils.i("queryHomeMsg bannerList.size()="+bannerList.size());
                    EventBus.getDefault().post(new TopMessageListEvent(bannerList));
                }
                stopSelf();
            }

            @Override
            public void onFail(HttpResponse response, Exception ex) {
                //LogUtils.i("queryHomeMsg onFail ex" + ex.getMessage());
                stopSelf();
            }

        });
    }

    private void goToNewTopMessageActivity(List<MessageInfo> list) {
//        Intent intent = new Intent(this, NewTopMessageActivity.class);
//        intent.putExtra("list", (Serializable) list);
//        intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
//        startActivity(intent);

        RoboActivity activity = MainActivity.getInstance();
        if( activity!=null ){
            NewTopMessageDialog dialog = new NewTopMessageDialog(activity, R.style.FullTransparentDialog);
            dialog.setData(list);
            dialog.show();
        }
    }


}
