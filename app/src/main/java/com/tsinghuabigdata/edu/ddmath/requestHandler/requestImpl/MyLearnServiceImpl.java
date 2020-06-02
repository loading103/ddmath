package com.tsinghuabigdata.edu.ddmath.requestHandler.requestImpl;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tsinghuabigdata.edu.commons.http.HttpRequest;
import com.tsinghuabigdata.edu.commons.http.HttpRequestException;
import com.tsinghuabigdata.edu.ddmath.bean.DoudouWork;
import com.tsinghuabigdata.edu.ddmath.bean.MyCourse;
import com.tsinghuabigdata.edu.ddmath.commons.http.AppRequest;
import com.tsinghuabigdata.edu.ddmath.commons.http.AppRequestUtils;
import com.tsinghuabigdata.edu.ddmath.constant.AppConst;
import com.tsinghuabigdata.edu.ddmath.constant.AppRequestConst;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.DDWorkDetail;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.SubmitReviseBean;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.WorkSubmitBean;
import com.tsinghuabigdata.edu.ddmath.module.mylearn.bean.WorkBean;
import com.tsinghuabigdata.edu.ddmath.module.neteaseim.base.util.string.MD5;
import com.tsinghuabigdata.edu.ddmath.requestHandler.MyLearnService;
import com.tsinghuabigdata.edu.ddmath.util.AppLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class MyLearnServiceImpl extends BaseService implements MyLearnService {

    @Override
    public String uploadImage(String fileId, String accessToken, InputStream stream, String filepath) throws HttpRequestException, UnsupportedEncodingException, JSONException {

        String url = getUrl(AppRequestConst.POST_MYLEARN_UPLOADIMAGE);
        String md5 = MD5.getFileMD5(filepath);

        HttpRequest request = AppRequestUtils.post(url).setTimeout(100);
        request.putHeader("access_token", accessToken)
                .putFrom("fileId", fileId)
                .putFrom("md5", md5)
                .putFrom("file", stream, "file" + AppConst.IMAGE_SUFFIX_NAME)
                .upload();
        String res = request.getBody();
        AppLog.d("MyLearnServiceImpl upload image = " + res);
        AppLog.d("MyLearnServiceImpl upload filepath = " + filepath);

        JSONObject json = new JSONObject(res);
        return json.getString("path");
    }

    @Override
    public JSONArray removeImages(String accessToken, JSONArray files) throws HttpRequestException, UnsupportedEncodingException, JSONException {
        String url = getUrl(AppRequestConst.POST_MYLEARN_REMOVEIMAGE);
        AppLog.d("MyLearnServiceImpl remove images url = " + url);

        HttpRequest request = AppRequestUtils.post(url).setTimeout(200);
        request.putHeader("access_token", accessToken);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("files", files);
        } catch (Exception e) {
            AppLog.i("", e);
        }
        request.setJsonStringParams(jsonObject.toString());
        String res = request.upload().getBody();
        AppLog.d("MyLearnServiceImpl upload image = " + res);

        return new JSONArray(res);
    }

    @Override
    public String submitWorkInfo(String access_token, String studentId, String classId, int questionCount, JSONArray jsonArray) throws HttpRequestException, JSONException {
        String url = getUrl(AppRequestConst.POST_MYLEARN_WORKINFO);
        AppLog.d("xxxbook upload question url = " + url);

        HttpRequest request = AppRequestUtils.post(url);
        request.putHeader("access_token", access_token).setTimeout(5 * 60);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("studentId", studentId);
            jsonObject.put("classId", classId);
            jsonObject.put("questionCount", questionCount);
            jsonObject.put("files", jsonArray);
        } catch (Exception e) {
            AppLog.i("", e);
        }
        request.setJsonStringParams(jsonObject.toString());
        return request.requestJson().getBody();
    }

    @Override
    public WorkSubmitBean submitDDWorkInfo(String access_token, String studentId, String classId, String examId, int pageCount, JSONArray pages, int customType, int uploadType) throws HttpRequestException, JSONException {
        String url = getUrl(AppRequestConst.POST_MYLEARN_DDWORKINFO);
        AppLog.d("xxxbook upload question url = " + url);

        HttpRequest request = AppRequestUtils.post(url);
        request.putHeader("access_token", access_token).setTimeout(100);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("studentId", studentId);
            jsonObject.put("classId", classId);
            jsonObject.put("examId", examId);
            jsonObject.put("pageCount", pageCount);
            jsonObject.put("pages", pages);
            jsonObject.put("uploadType", uploadType);
            if( customType > 0 ) jsonObject.put( "customType", customType );
            jsonObject.put("md5", MD5.getStringMD5(pages.toString()));
        } catch (Exception e) {
            AppLog.i("", e);
        }
        request.setJsonStringParams(jsonObject.toString());
        String res = ((AppRequest) request.requestJson()).getDataBody();
        if (TextUtils.isEmpty(res))
            return null;

        return new Gson().fromJson(res, new TypeToken<WorkSubmitBean>() {
        }.getType());
    }

    @Override
    public ArrayList<WorkBean> getWorkList(String access_token, String studentId, String classId, String date) throws HttpRequestException, JSONException {
        String url = getUrl(AppRequestConst.GET_MYLEARN_WORKLIST) + "&studentId=" + studentId + "&classId=" + classId + "&date=" + date;
        AppLog.d("xxxbook upload getWorkList url = " + url);
        HttpRequest request = AppRequestUtils.get(url);
        request.putHeader("access_token", access_token);
        String res = request.requestJson().getBody();
        AppLog.d("xxxbook  getWorkList res = " + res);
        return new Gson().fromJson(res, new TypeToken<ArrayList<WorkBean>>() {
        }.getType());
    }

    @Override
    public DDWorkDetail getDDWorkDetail(String access_token, String studentId, String workId, String recordId) throws HttpRequestException, JSONException {

        String url = getUrl(AppRequestConst.GET_MYLEARN_DDWORK_DETAIL);
        HttpRequest request = AppRequestUtils.get(url);
        request.putRestfulParam("studentId", studentId);
        if (!TextUtils.isEmpty(workId)) {
            request.putRequestParam("examId", workId);
        }
        if (!TextUtils.isEmpty(recordId)) {
            request.putRequestParam("recordId", recordId);
        }
        String res = request.request().getBody();
        AppLog.d("xxxbook  getWorkList res = " + res);
        return new Gson().fromJson(res, new TypeToken<DDWorkDetail>() {
        }.getType());
    }

    @Override
    public List<MyCourse> myCourse(String access_token, String studentId, String classId, int pageNum, int pageSize) throws HttpRequestException, JSONException {
        String url = getUrl(AppRequestConst.GET_MY_COURSE);
        HttpRequest request = AppRequestUtils.get(url);
        request.putHeader("access_token", access_token)
                .putRequestParam("studentId", studentId)
                .putRequestParam("classId", classId)
                .putRequestParam("pageNum", pageNum + "")
                .putRequestParam("pageSize", pageSize + "");
        String res = request.request().getBody();
        return new Gson().fromJson(res, new TypeToken<List<MyCourse>>() {
        }.getType());
    }

    @Override
    public DoudouWork queryDoudouWork(String studentId, String classId, int pageNum, int pageSize, int status, int type) throws HttpRequestException, JSONException {
        String url = getUrl(AppRequestConst.QUERY_DOUDOU_WORK);
        HttpRequest request = AppRequestUtils.get(url);
        request.putRestfulParam("studentId", studentId)
                .putRequestParam("classId", classId)
                .putRequestParam("pageNum", pageNum + "")
                .putRequestParam("pageSize", pageSize + "")
                .putRequestParam("status", status + "")
                .putRequestParam("type", type + "");
        String res = request.request().getBody();
        return new Gson().fromJson(res, DoudouWork.class);
    }

    @Override
    public int getDDWorkRevokeStatus(String studentId, String examId) throws HttpRequestException, JSONException {
        String url = getUrl(AppRequestConst.GET_DDWORK_STATUS);
        HttpRequest request = AppRequestUtils.get(url);
        request.putRequestParam("studentId", studentId)
                .putRequestParam("examId", examId);
        String res = request.request().getDataBody();
        return Integer.valueOf(res);
    }

    @Override
    public boolean shareDDWorkRecord(String examId) throws HttpRequestException, JSONException {
        String url = getUrl(AppRequestConst.GET_DDWORK_SHARE);
        HttpRequest request = AppRequestUtils.post(url);
        request.putRestfulParam("examId", examId);
        String res = request.requestJson().getDataBody();

        com.alibaba.fastjson.JSONObject jsonObject = JSON.parseObject(res);
        Boolean success = jsonObject.getBoolean("success");
        if (success != null && success) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean applyCorrectError(String studentId, boolean isRevise, com.alibaba.fastjson.JSONObject json) throws HttpRequestException, JSONException {
        String url = getUrl(AppRequestConst.POST_USER_CORRECTERROR);
        if (isRevise) {
            url = getUrl(AppRequestConst.POST_REVISE_CORRECTERROR);
        }

        HttpRequest request = AppRequestUtils.post(url);
        request.putRestfulParam("studentId", studentId);

        request.setJsonStringParams(json.toString());

        String res = request.requestJson().getDataBody();

        return "success".equals(res);

    }

    @Override
    public SubmitReviseBean applyReviseError(String studentId, String examId, String questionId, com.alibaba.fastjson.JSONObject json) throws HttpRequestException, JSONException {

        String url = getUrl(AppRequestConst.POST_USER_REVISEERROR);

        HttpRequest request = AppRequestUtils.post(url);
        request.putRestfulParam("studentId", studentId);
        com.alibaba.fastjson.JSONObject jsonParam = new com.alibaba.fastjson.JSONObject();
        com.alibaba.fastjson.JSONArray array = new com.alibaba.fastjson.JSONArray();
        array.add(json);
        json.put("examId", examId);
        json.put("questionId", questionId);

        jsonParam.put("studentId", studentId);
        jsonParam.put("questions", array);

        request.setJsonStringParams(jsonParam.toString());
        String res = request.requestJson().getDataBody();
        return new Gson().fromJson(res, SubmitReviseBean.class);
    }

    @Override
    public int getTodayCheckWorkCount() throws HttpRequestException, JSONException {
        String url = getUrl(AppRequestConst.GET_TODAY_CHECKWORK_COUNT);
        HttpRequest request = AppRequestUtils.get(url);
        String res = request.request().getDataBody();

        JSONObject jsonObject = new JSONObject( res );
        return jsonObject.getInt("homeworkCount");
    }

    @Override
    public int getBookTeacherSubmitStatus(String bookId) throws HttpRequestException, JSONException {
        String url = getUrl(AppRequestConst.GET_CHECKWORK_TEACHER);
        HttpRequest request = AppRequestUtils.get(url);
        request.putRequestParam("bookId",bookId);
        String res = request.request().getDataBody();

        JSONObject jsonObject = new JSONObject( res );
        return jsonObject.getInt("status");
    }

}
