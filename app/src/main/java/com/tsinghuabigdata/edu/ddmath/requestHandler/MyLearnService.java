package com.tsinghuabigdata.edu.ddmath.requestHandler;

import com.tsinghuabigdata.edu.commons.http.HttpRequestException;
import com.tsinghuabigdata.edu.ddmath.bean.DoudouWork;
import com.tsinghuabigdata.edu.ddmath.bean.MyCourse;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.DDWorkDetail;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.SubmitReviseBean;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.WorkSubmitBean;
import com.tsinghuabigdata.edu.ddmath.module.mylearn.bean.WorkBean;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public interface MyLearnService {

    /**
     * 上传图片
     * @param fileId   本地
     * @param stream  图片数据
     * @param accessToken token
     * @return fileurl
     * @throws HttpRequestException
     * @throws JSONException
     */
    String uploadImage( String fileId, String accessToken, InputStream stream, String filepath ) throws HttpRequestException, UnsupportedEncodingException, JSONException;

    /**
     * 删除已上传图片
     * @param accessToken token
     * @param files   图片路径
     * @return 删除失败的文件
     * @throws HttpRequestException
     * @throws JSONException
     */
    JSONArray removeImages( String accessToken, JSONArray files ) throws HttpRequestException, UnsupportedEncodingException, JSONException;

    /**
     * 提交其他题目
     * @param access_token   token
     * @param studentId  ID
     * @param classId    ID
     * @param questionCount 预估题数
     * @param jsonArray  作业信息
     * @return id
     * @throws HttpRequestException
     * @throws JSONException
     */
    String submitWorkInfo(String access_token, String studentId, String classId, int questionCount, JSONArray jsonArray) throws HttpRequestException, JSONException;

    /**
     * 提交豆豆作业
     * @param access_token   token
     * @param studentId  ID
     * @param classId    ID
     * @param examId 作业ID
     * @param pages  作业信息
     * @return id
     * @throws HttpRequestException
     * @throws JSONException
     */
    WorkSubmitBean submitDDWorkInfo(String access_token, String studentId, String classId, String examId, int pageCount, JSONArray pages, int customType, int uploadType) throws HttpRequestException, JSONException;

    /**
     * 作业列表  查找某一天
     * @param access_token   token
     * @param studentId  ID
     * @param classId     学科
     * @param date
     * @return list
     */
    ArrayList<WorkBean> getWorkList(String access_token, String studentId, String classId, String date ) throws HttpRequestException, JSONException;

    /**
     * 豆豆作业详情
     * @param access_token   token
     * @param studentId  ID
     * @param workId     学科
     * @return list
     */
    DDWorkDetail getDDWorkDetail(String access_token, String studentId, String workId, String recordId ) throws HttpRequestException, JSONException;

    List<MyCourse> myCourse(String access_token, String studentId, String classId, int pageNum, int pageSize) throws HttpRequestException, JSONException;

    DoudouWork queryDoudouWork(String studentId, String classId, int pageNum, int pageSize, int status, int type) throws HttpRequestException, JSONException;

    /**
     * 豆豆作业状态，是否撤回
     *
     * @param studentId  学生ID
     * @param examId     作业ID
     * @return 0:未撤回 1:已撤回
     */
    int getDDWorkRevokeStatus( String studentId, String examId ) throws HttpRequestException, JSONException;

    /**
     * 豆豆作业分享记录
     * @param examId     作业ID
     * @return true：成功；false：失败
     */
    boolean shareDDWorkRecord( String examId ) throws HttpRequestException, JSONException;

    /**
     * 错题纠错申请
     * @param studentId  学生ID
     * @param isRevise    是否错题订正
     * @return true：成功；false：失败
     */
    boolean applyCorrectError( String studentId, boolean isRevise, com.alibaba.fastjson.JSONObject json ) throws HttpRequestException, JSONException;

    /**
     * 错题订正申请
     * @param studentId  学生ID
     * @param examId     作业ID
     * @param questionId  题目ID
     * @return true：成功；false：失败
     */
    SubmitReviseBean applyReviseError(String studentId, String examId, String questionId, com.alibaba.fastjson.JSONObject json ) throws HttpRequestException, JSONException;

    /**
     * 今天自我检查作业布置的次数
     */
    int getTodayCheckWorkCount() throws HttpRequestException, JSONException;

    /**
     * 检查教辅是否布置过作业还有未提交的作业
     */
    int getBookTeacherSubmitStatus(String bookId) throws HttpRequestException, JSONException;

}
