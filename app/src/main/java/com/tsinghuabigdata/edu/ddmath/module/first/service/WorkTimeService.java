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
import com.tsinghuabigdata.edu.ddmath.bean.SubmitQuestion;
import com.tsinghuabigdata.edu.ddmath.bean.UserDetailinfo;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.event.LastWorkTypeEvent;
import com.tsinghuabigdata.edu.ddmath.event.MyWorkEvent;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;

/**
 * 首页消息服务
 * Created by Administrator on 2017/10/30.
 */
@Deprecated
public class WorkTimeService extends Service {

    private static final int TOSUBMIT_WORK = 0;
    private static final int SUBMITED_WORK = 1;
    private static final int CHECK_WORK    = 2;

    private String studentId;
    private String classId;
    private HashMap<Integer, Long> mHashMap = new HashMap();

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
        queryCheckWork();
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

    private synchronized void queryResult(int type, long time) {
        //SimpleDateFormat sdf = new SimpleDateFormat(" 最近时间：yyyy.MM.dd  HH:mm:ss");
        //String timestr = sdf.format(time);
        //LogUtils.i("type:" + type + timestr);
        mHashMap.put(type, time);
        if (mHashMap.keySet().size() >= 3) {
            boolean isCheckWork = mHashMap.get(TOSUBMIT_WORK) < mHashMap.get(CHECK_WORK) && mHashMap.get(SUBMITED_WORK) < mHashMap.get(CHECK_WORK);
            EventBus.getDefault().post(new LastWorkTypeEvent(isCheckWork));
            boolean hasNoMyWork = mHashMap.get(TOSUBMIT_WORK) == 0 && mHashMap.get(TOSUBMIT_WORK) == 0;
            EventBus.getDefault().post(new MyWorkEvent(!hasNoMyWork));
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
                    queryResult(TOSUBMIT_WORK, 0);
                    return;
                }
                SubmitQuestion submitQuestion = vo.getExerhomes().get(0);
                queryResult(TOSUBMIT_WORK, submitQuestion.getStartTime());
            }

            @Override
            public void onFail(HttpResponse<DoudouWork> response, Exception ex) {
                //LogUtils.i("queryToSubmitWork onFail");
                queryResult(TOSUBMIT_WORK, -1);
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
                    queryResult(SUBMITED_WORK, 0);
                    return;
                }
                SubmitQuestion submitQuestion = vo.getExerhomes().get(0);
                queryResult(SUBMITED_WORK, submitQuestion.getSubmitTime());
            }

            @Override
            public void onFail(HttpResponse<DoudouWork> response, Exception ex) {
                //LogUtils.i("querySubmitedWork onFail");
                queryResult(SUBMITED_WORK, -1);
            }
        });
    }

    //查询作业列表
    private void queryCheckWork() {
        int status = 1;
        new MyStudyModel().queryDoudouCheckWork(studentId, classId, 1, 1, status, 1, new RequestListener<DoudouWork>() {

            @Override
            public void onSuccess(DoudouWork vo) {
                //LogUtils.i("queryDoudouWork onSuccess");
                if (vo == null || vo.getExerhomes() == null || vo.getExerhomes().size() == 0) {
                    queryResult(CHECK_WORK, 0);
                    return;
                }
                SubmitQuestion submitQuestion = vo.getExerhomes().get(0);
                queryResult(CHECK_WORK, submitQuestion.getSubmitTime());
            }

            @Override
            public void onFail(HttpResponse<DoudouWork> response, Exception ex) {
                //LogUtils.i("queryDoudouWork onFail");
                queryResult(CHECK_WORK, 0);
            }
        });
    }


}
