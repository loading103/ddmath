package com.tsinghuabigdata.edu.ddmath.requestHandler.requestImpl;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tsinghuabigdata.edu.commons.http.HttpRequest;
import com.tsinghuabigdata.edu.commons.http.HttpRequestException;
import com.tsinghuabigdata.edu.ddmath.commons.http.AppRequestUtils;
import com.tsinghuabigdata.edu.ddmath.constant.AppRequestConst;
import com.tsinghuabigdata.edu.ddmath.module.errorbook.bean.VarTrainResult;
import com.tsinghuabigdata.edu.ddmath.module.exclusivepractice.bean.PracticeProductBean;
import com.tsinghuabigdata.edu.ddmath.module.exclusivepractice.bean.ShareBean;
import com.tsinghuabigdata.edu.ddmath.requestHandler.PracticeService;

import org.json.JSONException;

import java.util.List;

/**
 * 专属习题
 */
public class PracticeServiceImpl extends BaseService implements PracticeService {

    @Override
    public VarTrainResult getVarTrainPracticeList(String studentId, int pageNum, int pageSize, long startTime, long endTime, int paperType) throws HttpRequestException, JSONException {
        String url = getUrl(AppRequestConst.GET_PRACTICE_LIST);
        HttpRequest request = AppRequestUtils.get(url);
        request.putRequestParam("studentId", studentId)
                .putRequestParam("pageNum", String.valueOf(pageNum))
                .putRequestParam("pageSize", String.valueOf(pageSize))
                .putRequestParam("startTime", String.valueOf(startTime))
                .putRequestParam("endTime", String.valueOf(endTime))
                .putRequestParam("paperType", String.valueOf(paperType));
        String res = request.request().getDataBody();

        return new Gson().fromJson(res, new TypeToken<VarTrainResult>() {
        }.getType());
    }

    @Override
    public ShareBean getPracticeShare(String classId, String studentId, String productId, String recordId ) throws HttpRequestException, JSONException {
        String url = getUrl(AppRequestConst.GET_PRACTICE_SHARE);
        HttpRequest request = AppRequestUtils.get(url);
        request.putRequestParam("studentId", studentId)
               .putRequestParam("classId", classId)
               .putRequestParam("excluId", recordId );
        if( !TextUtils.isEmpty(productId)  )
                request.putRequestParam("productId", productId);
        String res = request.request().getDataBody();

        return new Gson().fromJson(res, new TypeToken<ShareBean>() {
        }.getType());
    }

    @Override
    public  List<PracticeProductBean> getClassicPracticeList(String studentId, String searchdata, String schoolId) throws HttpRequestException, JSONException{
        String url = getUrl(AppRequestConst.GET_CLASSIC_PRACTICE_LIST);
        HttpRequest request = AppRequestUtils.get(url);
        request.putRequestParam("studentId", studentId);
        request.putRequestParam("schoolId", schoolId);
        if( !TextUtils.isEmpty(searchdata) ){
            request.putRequestParam( "searchContent",  searchdata );
        }
        String res = request.request().getDataBody();

        return new Gson().fromJson(res, new TypeToken<List<PracticeProductBean>>() {
        }.getType());
    }

    @Override
    public ShareBean getClassicPracticeShare(String classId, String studentId, String productId, String excluId ) throws HttpRequestException, JSONException {
        String url = getUrl(AppRequestConst.GET_CLASSIC_PRACTICE_SHARE);
        HttpRequest request = AppRequestUtils.get(url);
        request.putRequestParam("studentId", studentId)
                .putRequestParam("productId", productId)
                .putRequestParam("classId", classId)
                .putRequestParam("excluId", excluId );
        String res = request.request().getDataBody();

        return new Gson().fromJson(res, new TypeToken<ShareBean>() {
        }.getType());
    }

}
