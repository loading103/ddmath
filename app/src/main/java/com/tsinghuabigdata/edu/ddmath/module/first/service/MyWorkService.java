package com.tsinghuabigdata.edu.ddmath.module.first.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;
import com.tsinghuabigdata.edu.ddmath.MVPModel.MyStudyModel;
import com.tsinghuabigdata.edu.ddmath.MVPModel.RequestListener;
import com.tsinghuabigdata.edu.ddmath.bean.DoudouWork;
import com.tsinghuabigdata.edu.ddmath.bean.UserDetailinfo;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.event.MyWorkEvent;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;

/**
 * 首页消息服务    不在调用
 * Created by Administrator on 2017/10/30.
 */
@Deprecated
public class MyWorkService extends Service {

    private static final int TOSUBMIT_WORK = 0;
    private static final int SUBMITED_WORK = 1;

    private String studentId;
    private String classId;
    private HashMap<Integer, Boolean> mHashMap = new HashMap();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        //LogUtils.i("WorkTimeService onCreate");
        super.onCreate();
        createLoginInfo();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        //LogUtils.i("WorkTimeService onStart");
        super.onStart(intent, startId);
        queryToSubmitWork();
        querySubmitedWork();
        querySelfWorkWork();
    }

    private void createLoginInfo() {
        UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
        if (detailinfo != null) {
            studentId = detailinfo.getStudentId();
        }
        if (AccountUtils.getCurrentClassInfo() != null) {
            classId = AccountUtils.getCurrentClassInfo().getClassId();
        }
    }

    private synchronized void queryResult(int type, boolean has) {
        //SimpleDateFormat sdf = new SimpleDateFormat(" 最近时间：yyyy.MM.dd  HH:mm:ss");
        //String timestr = sdf.format(time);
        //LogUtils.i("type:" + type + timestr);
        mHashMap.put(type, has);
        if (mHashMap.keySet().size() >= 2) {
            boolean hasMyWork = mHashMap.get(TOSUBMIT_WORK) || mHashMap.get(SUBMITED_WORK);
            EventBus.getDefault().post(new MyWorkEvent(hasMyWork,false));
            stopSelf();
        }
    }

    //查询待提交机构作业列表
    private void queryToSubmitWork() {
        int status = 0;
        new MyStudyModel().queryDoudouWork(studentId, classId, 1, 1, status, new RequestListener<DoudouWork>() {

            @Override
            public void onSuccess(DoudouWork vo) {
                //LogUtils.i("queryToSubmitWork onSuccess");
                if (vo == null || vo.getExerhomes() == null || vo.getExerhomes().size() == 0) {
                    queryResult(TOSUBMIT_WORK, false);
                    return;
                }
                queryResult(TOSUBMIT_WORK, true);
            }

            @Override
            public void onFail(HttpResponse<DoudouWork> response, Exception ex) {
                //LogUtils.i("queryToSubmitWork onFail");
                queryResult(TOSUBMIT_WORK, false);
            }
        });
    }

    //查询已提交机构作业列表
    private void querySubmitedWork() {
        int status = 1;
        new MyStudyModel().queryDoudouWork(studentId, classId, 1, 1, status, new RequestListener<DoudouWork>() {

            @Override
            public void onSuccess(DoudouWork vo) {
                //LogUtils.i("querySubmitedWork onSuccess");
                if (vo == null || vo.getExerhomes() == null || vo.getExerhomes().size() == 0) {
                    queryResult(SUBMITED_WORK, false);
                    return;
                }
                queryResult(SUBMITED_WORK, true);
            }

            @Override
            public void onFail(HttpResponse<DoudouWork> response, Exception ex) {
                //LogUtils.i("querySubmitedWork onFail");
                queryResult(SUBMITED_WORK, false);
            }
        });
    }

    //查询自我诊断作业列表
    private void querySelfWorkWork() {
        int status = 1;
        new MyStudyModel().queryDoudouCheckWork(studentId, classId, 1, 1, status, 1, new RequestListener<DoudouWork>() {

            @Override
            public void onSuccess(DoudouWork vo) {
                //LogUtils.i("querySubmitedWork onSuccess");
                if (vo == null || vo.getExerhomes() == null || vo.getExerhomes().size() == 0) {
                    return;
                }
                EventBus.getDefault().post(new MyWorkEvent(false,true));
            }

            @Override
            public void onFail(HttpResponse<DoudouWork> response, Exception ex) {
            }
        });
    }

}
