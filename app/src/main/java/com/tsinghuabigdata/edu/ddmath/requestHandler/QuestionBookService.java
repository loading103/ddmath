package com.tsinghuabigdata.edu.ddmath.requestHandler;

import com.tsinghuabigdata.edu.commons.http.HttpRequestException;
import com.tsinghuabigdata.edu.ddmath.bean.BookBean;
import com.tsinghuabigdata.edu.ddmath.bean.StudentQuestionVo;

import org.json.JSONException;

import java.util.HashMap;


public interface QuestionBookService {

    /**
     * 学生错题查询
     */
    StudentQuestionVo queryQuestions(String studentId, String key, String sort, long startDate, long endDate, boolean isShowCorrect,
                                     String wrongQuestionClasstype, int pageNum, int pageSize) throws HttpRequestException, JSONException;

    /**
     * pdf生成请求
     */
    BookBean productPdf(HashMap<String,String> params) throws HttpRequestException, JSONException;

    /**
     * pdf生成结果查询
     */
    BookBean serachPdf(String bookId) throws HttpRequestException, JSONException;

}
