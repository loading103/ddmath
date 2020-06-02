package com.tsinghuabigdata.edu.ddmath.module.mylearn;

import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;

//上传监听回调
public interface UploadListener<T> {
    void onSuccess(T res);
    void onFail(HttpResponse<T> response, Exception ex);
    void onStatusChange();
}
