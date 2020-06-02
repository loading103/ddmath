package com.tsinghuabigdata.edu.ddmath.requestHandler;

import com.tsinghuabigdata.edu.commons.http.HttpRequestException;
import com.tsinghuabigdata.edu.ddmath.bean.BookBean;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.DDWorkDetail;
import com.tsinghuabigdata.edu.ddmath.module.learnmaterial.bean.BookListBean;
import com.tsinghuabigdata.edu.ddmath.module.learnmaterial.bean.CatalogBean;
import com.tsinghuabigdata.edu.ddmath.module.learnmaterial.bean.SelfWorkSubmitBean;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;


public interface LearnMaterialService {

    /**
     * 原版教辅查询业内题目信息
     */
    DDWorkDetail querySelfWorkDetail(String bookId, String pageNums ) throws HttpRequestException, JSONException;

    /**
     * 原版教辅 学生创建 && 上传作业接口
     */
    SelfWorkSubmitBean createAndUploadSelfWork(String studentId, String classId, int pageCount, int uploadType, JSONArray pages ) throws HttpRequestException, JSONException;

    /**
     * 查询学生可用的原版教辅列表
     */
    BookListBean queryBookList(HashMap<String, String> params) throws HttpRequestException, JSONException;

    /**
     * 学生选择原版教辅
     */
    BookBean selectBook(String studentId, String bookId) throws HttpRequestException, JSONException;

    /**
     * 查询教辅章和页码信息
     */
    CatalogBean queryBookCatalog(HashMap<String, String> params) throws HttpRequestException, JSONException;

}
