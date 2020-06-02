package com.tsinghuabigdata.edu.ddmath.MVPModel;

import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;

/**
 * Created by 28205 on 2016/9/12.
 */
public interface RequestListener<T> {
    public void onSuccess(T res);

    public void onFail(HttpResponse<T> response, Exception ex);
}
