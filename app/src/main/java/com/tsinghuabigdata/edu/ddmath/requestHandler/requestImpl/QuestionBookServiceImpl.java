package com.tsinghuabigdata.edu.ddmath.requestHandler.requestImpl;

import com.google.gson.Gson;
import com.tsinghuabigdata.edu.commons.http.HttpRequest;
import com.tsinghuabigdata.edu.commons.http.HttpRequestException;
import com.tsinghuabigdata.edu.ddmath.bean.BookBean;
import com.tsinghuabigdata.edu.ddmath.bean.StudentQuestionVo;
import com.tsinghuabigdata.edu.ddmath.commons.http.AppRequestUtils;
import com.tsinghuabigdata.edu.ddmath.constant.AppRequestConst;
import com.tsinghuabigdata.edu.ddmath.requestHandler.QuestionBookService;

import org.json.JSONException;
import java.util.HashMap;
import java.util.Iterator;


/**
 * Created by Administrator on 2017/3/11.
 */

public class QuestionBookServiceImpl extends BaseService implements QuestionBookService {


    @Override
    public StudentQuestionVo queryQuestions(String studentId, String key, String sort, long startDate, long endDate, boolean isShowCorrect,
                                            String wrongQuestionClasstype, int pageNum, int pageSize) throws HttpRequestException, JSONException {
        String url = getUrl(AppRequestConst.QUESTION_BOOK);
        HttpRequest request = AppRequestUtils.get(url);
        request.putRestfulParam("studentId", studentId)
                .putRequestParam("key", key)
                .putRequestParam("sort", sort)
                .putRequestParam("startDate", String.valueOf(startDate))
                .putRequestParam("endDate", String.valueOf(endDate))
                .putRequestParam("isShowCorrect", String.valueOf(isShowCorrect))
                .putRequestParam("wrongQuestionClasstype", wrongQuestionClasstype)
                .putRequestParam("pageNum", String.valueOf(pageNum))
                .putRequestParam("pageSize", String.valueOf(pageSize))
                .request();
        String res = request.getDataBody();
        return new Gson().fromJson(res, StudentQuestionVo.class);
    }

    @Override
    public BookBean productPdf(HashMap<String, String> params) throws HttpRequestException, JSONException {
        String url = getUrl(AppRequestConst.PRODUCT_PDF);
        //url = "http://192.168.30.22:8080"+AppRequestConst.PRODUCT_PDF;
        HttpRequest request = AppRequestUtils.post(url);
        if (params != null) {
            Iterator<String> it = params.keySet().iterator();
            while (it.hasNext()) {
                String key = it.next();
                String value = params.get(key);
                request.putRequestParam(key, value);
            }
        }
        request.request();
        String res = request.getDataBody();
        return new Gson().fromJson(res, BookBean.class);

        /*if (params != null) {
            Iterator<String> it = params.keySet().iterator();
            JSONObject jsonObject = new JSONObject();
            while (it.hasNext()) {
                String key = it.next();
                String value = params.get(key);
                jsonObject.put(key, value);
            }
            request.setJsonStringParams(jsonObject.toString());
        }
        String body = request.requestJson().getBody();
        return new Gson().fromJson(body, BookBean.class);*/
    }

    @Override
    public BookBean serachPdf(String bookId) throws HttpRequestException, JSONException {
        String url = getUrl(AppRequestConst.SERACH_PDF);
        HttpRequest request = AppRequestUtils.get(url);
        request.putRequestParam("bookId", bookId)
                .request();
        String res = request.getDataBody();
        return new Gson().fromJson(res, BookBean.class);
    }
}
