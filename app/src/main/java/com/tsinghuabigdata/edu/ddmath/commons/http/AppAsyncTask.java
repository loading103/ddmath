package com.tsinghuabigdata.edu.ddmath.commons.http;

import android.os.AsyncTask;

import com.tsinghuabigdata.edu.commons.network.NetworkAsyncTask;
import com.tsinghuabigdata.edu.ddmath.ZxApplication;
import com.tsinghuabigdata.edu.ddmath.util.AppLog;
import com.tsinghuabigdata.edu.ddmath.util.AppUtils;
import com.tsinghuabigdata.edu.ddmath.constant.ErrTag;

/**
 * <p>
 * Created by yanshen@tsinghuabigdata.com on 2015/11/5.
 * </p>
 *
 * 用于网络请求，可以接收服务器状态
 *
 * @author yanshen@tsinghuabigdata.com
 * @version V1.0
 * @packageName: com.tsinghuabigdata.edu.commons.network
 * @createTime: 2015/11/5 15:28
 */
public abstract class AppAsyncTask<Params, Progress, Result> extends NetworkAsyncTask<Params, Progress, Result> {

    private Params[] params;

    public Params[] getParams() {
        return params;
    }

    private boolean complete;

    public boolean isComplete() {
        return complete;
    }

    @Override
    protected final Result doInBackground(Params... params) {
        try {
            this.params = params;
            return doExecute(params);
        } catch (AppRequestException ex) {
            AppLog.w(ErrTag.TAG_HTTP,"服务端异常, CODE=" + ex.getResponse().getCode(), ex);
            setNetworkErr(100000, ex);
            return null;
        } catch (Exception ex) {
            AppLog.w(ErrTag.TAG_HTTP, "服务端异常", ex);
            setNetworkErr(100000, ex);
            return null;
        }
    }

    @Override
    protected final void onPostExecute(Result result) {
        complete = true;
        if(isNetworkErr()){
            Exception exception = getException();
            if( exception != null && exception instanceof AppRequestException) {
                onFailure(((AppRequestException) getException()).getResponse(), getException());
            } else {
                onFailure(new HttpResponse<Result>(isNoConnection() ? ResponseCode.CODE_40103 :
                        ResponseCode.CODE_40101, null, null, null, result), getException());
            }
        }
        else {
            onResult(result);
        }
    }

    public final AsyncTask<Params, Progress, Result> executeMulti(Params... params) {
        return executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
    }

    @Override
    public boolean isNoConnection() {
        return !AppUtils.isNetworkConnected(ZxApplication.getApplication()) || super.isNoConnection();
    }

    protected abstract Result doExecute(Params... params) throws Exception;

    /**
     * 成功
     * @param result
     */
    protected abstract void onResult(Result result);

    /**
     * 失败
     * @param response
     */
    protected abstract void onFailure(HttpResponse<Result> response, Exception ex);
}
