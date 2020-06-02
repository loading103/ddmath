package com.tsinghuabigdata.edu.ddmath.module.ddwork;

import com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.WorkSubmitBean;

/**
 * 豆豆作业提交状态回调
 */
public interface WorkCommitListener {
    //作业上传状态改变
    void workStatus(String examId, int status);
    //作业页上传状态改变
    void pageStatus(String examId, int status);

    //上传结果 成功
    void onSuccess(String examId, WorkSubmitBean res);
    //上传结果 失败
    void onFail( String examId, Exception ex);

}
