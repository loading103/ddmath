package com.tsinghuabigdata.edu.ddmath.requestHandler.requestImpl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tsinghuabigdata.edu.commons.http.HttpRequest;
import com.tsinghuabigdata.edu.commons.http.HttpRequestException;
import com.tsinghuabigdata.edu.ddmath.commons.http.AppRequestUtils;
import com.tsinghuabigdata.edu.ddmath.constant.AppRequestConst;
import com.tsinghuabigdata.edu.ddmath.module.errorbook.bean.DayCleanDetailBean;
import com.tsinghuabigdata.edu.ddmath.module.errorbook.bean.DayCleanResult;
import com.tsinghuabigdata.edu.ddmath.module.errorbook.bean.StageReviewResult;
import com.tsinghuabigdata.edu.ddmath.module.errorbook.bean.WeekTrainResult;
import com.tsinghuabigdata.edu.ddmath.module.exclusivepractice.bean.ShareBean;
import com.tsinghuabigdata.edu.ddmath.requestHandler.ErrorBookService;
import com.tsinghuabigdata.edu.ddmath.util.AppLog;

import org.json.JSONException;

public class ErrorBookServiceImpl extends BaseService implements ErrorBookService {


    @Override
    public DayCleanResult queryDayCleanTaskList(String studentId/*, long startDate, long endDate*/) throws HttpRequestException, JSONException{

        String url = getUrl(AppRequestConst.EBOOK_DAYCLEAN_LIST);
        HttpRequest request = AppRequestUtils.get(url);
        request.putRestfulParam( "studentId", studentId );

        String res = request.request().getBody();

        return new Gson().fromJson( res, new TypeToken<DayCleanResult>() {}.getType());
    }

    @Override
    public DayCleanDetailBean queryDayCleanDetail(String studentId, String currentDate, int listNumber, boolean master, int pageNum, int pageSize ) throws HttpRequestException, JSONException{

        String url = getUrl(AppRequestConst.EBOOK_DAYCLEAN_DETAIL);
        HttpRequest request = AppRequestUtils.get(url);
        request.putRestfulParam( "studentId", studentId )
               .putRequestParam( "currentDate", currentDate )
               .putRequestParam( "listNumber", String.valueOf(listNumber) )
               .putRequestParam( "isDisplayHasRevised", Boolean.toString(master) )
               .putRequestParam( "pageNumber", Integer.toString(pageNum) )
               .putRequestParam( "pageCapacity", Integer.toString(pageSize) );

        String res = request.request().getBody();
        return new Gson().fromJson( res, new TypeToken<DayCleanDetailBean>() {}.getType());
    }

    @Override
    public WeekTrainResult queryWeekTrainList(String studentId, int pageNum, int pageSize, long startTime, long endTime, int paperType) throws HttpRequestException, JSONException{

        //String url = "http://172.16.40.218:92"+AppRequestConst.XBOOK_QUESTION_LIST+"?v=" + System.currentTimeMillis();
        String url = getUrl(AppRequestConst.EBOOK_WEEKTRAIN_LIST);
        HttpRequest request = AppRequestUtils.get(url);
        request.putRestfulParam( "studentId", studentId )
               .putRequestParam( "pageNum", String.valueOf(pageNum) )
               .putRequestParam( "pageSize", String.valueOf(pageSize) )
                .putRequestParam( "startTime", String.valueOf(startTime) )
                .putRequestParam( "stopTime", String.valueOf(endTime) )
                .putRequestParam( "paperType", String.valueOf(paperType) );

        String res = request.request().getBody();
        AppLog.d("xxxbook upload question = " + res );

        return new Gson().fromJson( res, new TypeToken<WeekTrainResult>() {}.getType());
    }

    @Override
    public ShareBean queryWeekTrainShare(String studentId, String recordId)  throws HttpRequestException, JSONException{

        String url = getUrl(AppRequestConst.EBOOK_WEEKTRAIN_SHARE);
        HttpRequest request = AppRequestUtils.get(url);
        request.putRequestParam( "studentId", studentId );
        request.putRequestParam( "recordId", recordId );

        String res = request.request().getBody();
        AppLog.d("xxxbook queryWeekTrainShare  " + res );

        return new Gson().fromJson( res, new TypeToken<ShareBean>() {}.getType());
    }

    @Override
    public StageReviewResult queryStageReviewList(String studentId,int pageNum, int pageSize, long startTime, long endTime, int paperType) throws HttpRequestException, JSONException{

        String url = getUrl(AppRequestConst.EBOOK_STAGEREVIEW_LIST);
        HttpRequest request = AppRequestUtils.get(url);
        request.putRestfulParam( "studentId", studentId )
                .putRequestParam( "pageNum", String.valueOf(pageNum) )
                .putRequestParam( "pageSize", String.valueOf(pageSize) )
                .putRequestParam( "startTime", String.valueOf(startTime) )
                .putRequestParam( "stopTime", String.valueOf(endTime) )
                .putRequestParam( "paperType", String.valueOf(paperType) );

        String res = request.request().getBody();
        AppLog.d("xxxbook upload question = " + res );

        return new Gson().fromJson( res, new TypeToken<StageReviewResult>() {}.getType());
    }

    @Override
    public String queryStageReviewDetail(String studentId, String recordId)  throws HttpRequestException, JSONException{

        //String url = "http://172.16.40.218:92"+AppRequestConst.XBOOK_QUESTION_LIST+"?v=" + System.currentTimeMillis();
        String url = getUrl(AppRequestConst.EBOOK_STAGEREVIEW_DETAIL);
        HttpRequest request = AppRequestUtils.get(url);
        request.putRestfulParam( "studentId", studentId )
                 .putRequestParam( "questionsId", recordId );

        String res = request.request().getBody();
        AppLog.d("xxxbook upload question = " + res );

//        String res = "{\"questionsUrl\": \"http://www.iclassedu.com/group1/M00/00/01/ChoZqVoOf1uIdMDGAEEmMSrpTsgAAACxADnz9MAQSZJ361.pdf\"}";

       //JSONObject json = JSONObject.parseObject( res );
        return res;//json.getString("questionsUrl");
    }

    @Override
    public String queryStageReviewShare(String studentId,String recordId)  throws HttpRequestException, JSONException{

        String url = getUrl(AppRequestConst.EBOOK_STAGEREVIEW_SHARE);
        HttpRequest request = AppRequestUtils.get(url);
        request.putRestfulParam( "studentId", studentId )
                .putRequestParam( "questionsId", recordId );
        String data = request.request().getBody();
        JSONObject json = JSON.parseObject( data );
        return json.getString("path");
    }

}