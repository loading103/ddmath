package com.tsinghuabigdata.edu.ddmath.requestHandler;

import com.tsinghuabigdata.edu.commons.http.HttpRequestException;
import com.tsinghuabigdata.edu.ddmath.module.errorbook.bean.DayCleanDetailBean;
import com.tsinghuabigdata.edu.ddmath.module.errorbook.bean.DayCleanResult;
import com.tsinghuabigdata.edu.ddmath.module.errorbook.bean.StageReviewResult;
import com.tsinghuabigdata.edu.ddmath.module.errorbook.bean.WeekTrainResult;
import com.tsinghuabigdata.edu.ddmath.module.exclusivepractice.bean.ShareBean;

import org.json.JSONException;

public interface ErrorBookService {

    /**
     * 日日清任务列表
     * @param studentId  ID
     */
    DayCleanResult queryDayCleanTaskList(String studentId/*, long startDate, long endDate*/) throws HttpRequestException, JSONException;

    /**
     * 日日清任务详情
     * @param studentId  ID
     */
    DayCleanDetailBean queryDayCleanDetail(String studentId, String currentDate, int listNumber, boolean master, int pageNum, int pageSize ) throws HttpRequestException, JSONException;

    /**
     * 错题周题练列表
     * @param studentId  ID
     */
    WeekTrainResult queryWeekTrainList( String studentId,int pageNum, int pageSize, long startTime, long endTime, int paperType)  throws HttpRequestException, JSONException;

    /**
     * 错题周题练 分享下载
     * @param studentId  ID
     */
    ShareBean queryWeekTrainShare(String studentId, String recordId)  throws HttpRequestException, JSONException;


    /**
     * 阶段学习宝列表
     * @param studentId  ID
     *
     */
    StageReviewResult queryStageReviewList(String studentId,int pageNum, int pageSize, long startTime, long endTime, int paperType)  throws HttpRequestException, JSONException;

    /**
     * 阶段学习宝列表 详情
     * @param studentId  ID
     *
     */
    String queryStageReviewDetail(String studentId,String recordId)  throws HttpRequestException, JSONException;

    /**
     * 阶段学习宝列表 分享下载
     * @param studentId  ID
     *
     */
    String queryStageReviewShare(String studentId,String recordId)  throws HttpRequestException, JSONException;

}
