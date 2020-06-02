package com.tsinghuabigdata.edu.ddmath.module.mycenter;

import android.os.AsyncTask;

import com.tsinghuabigdata.edu.ddmath.MVPModel.RequestListener;
import com.tsinghuabigdata.edu.ddmath.commons.http.AppAsyncTask;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.requestHandler.MycenterReqService;
import com.tsinghuabigdata.edu.ddmath.requestHandler.requestImpl.MycenterReqImpl;
import com.tsinghuabigdata.edu.ddmath.util.AsyncTaskCancel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by 28205 on 2016/12/14.
 */
public class MycenterModel {
    private MycenterReqService mycenterReqService = new MycenterReqImpl();
    private List<AsyncTask> runningTasks = new ArrayList<AsyncTask>();

    /**
     * 个人基本信息编辑
     */
    class EditPersonalinfoTask extends AppAsyncTask<String, Void, Void> {
        private RequestListener reqListener;

        public EditPersonalinfoTask(RequestListener listener) {
            this.reqListener = listener;
        }

        @Override
        protected Void doExecute(String... params) throws Exception {
            String accessToken = params[0];
            String accountId = params[1];
            String reallyName = params[2];
//            String schoolId = params[3];
            String serial = params[3];
            String sex = params[4];
            String nickName = params[5];
            String phone = params[6];
            mycenterReqService.editPersoninfo(accessToken, accountId, reallyName, serial, sex, nickName,phone);
            return null;
        }

        @Override
        protected void onResult(Void resultInfo) {
            reqListener.onSuccess(resultInfo);
        }

        @Override
        protected void onFailure(HttpResponse<Void> response, Exception ex) {
            reqListener.onFail(response, ex);
        }
    }

     class UpdatePersoninfoTask extends AppAsyncTask<HashMap<String, String>, Void, String> {

        private RequestListener reqListener;

         UpdatePersoninfoTask(RequestListener requestListener) {
            reqListener = requestListener;
        }

        @Override
        protected String doExecute(HashMap<String, String>... maps) throws Exception {
            HashMap<String, String> map = maps[0];
            return mycenterReqService.updatePersoninfo(map);
        }

        @Override
        protected void onResult(String bean) {
            reqListener.onSuccess(bean);
        }

        @Override
        protected void onFailure(HttpResponse<String> response, Exception ex) {
            reqListener.onFail(response, ex);
        }
    }


    /**
     * 个人信息编辑调用方法
     */
    public void editPersonalinfo(String accessToken, String accountId, String reallyName,
                                 String serial, String sex, String nickName, String phone, RequestListener requestListener) {
        EditPersonalinfoTask editPersonalinfoTask = new EditPersonalinfoTask(requestListener);
        editPersonalinfoTask.executeMulti(accessToken, accountId, reallyName,
                serial, sex, nickName, phone);
        runningTasks.add(editPersonalinfoTask);
    }

    /**
     * 个人信息更新(v12.0修改)
     */
    public void updatePersoninfo(HashMap<String, String> params, RequestListener requestListener) {
        UpdatePersoninfoTask task = new UpdatePersoninfoTask(requestListener);
        task.executeMulti(params);
        runningTasks.add(task);
    }

    public void stopRunningTasks() {
        if (runningTasks.size() > 0) {
            AsyncTaskCancel.cancel(runningTasks);
        }
    }
}
