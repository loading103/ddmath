package com.tsinghuabigdata.edu.ddmath.MVPModel;

import com.tsinghuabigdata.edu.ddmath.commons.http.AppAsyncTask;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.requestHandler.VideoService;
import com.tsinghuabigdata.edu.ddmath.requestHandler.requestImpl.VideoServiceImpl;

/**
 * Created by Administrator on 2018/1/25.
 */

public class VideoModel {

    private VideoService mService = new VideoServiceImpl();

    private class RecordVideoCountTask extends AppAsyncTask<String, Void, String> {

        private RequestListener reqListener;

        RecordVideoCountTask(RequestListener requestListener) {
            reqListener = requestListener;
        }

        @Override
        protected String doExecute(String... params) throws Exception {
            String studentId = params[0];
            return mService.recordVideoCount(studentId);
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
     * 记录学生视频模块浏览记录
     */
    public void recordVideoCount(String studentId, RequestListener requestListener) {
        RecordVideoCountTask task = new RecordVideoCountTask(requestListener);
        task.executeMulti(studentId);
    }

}
