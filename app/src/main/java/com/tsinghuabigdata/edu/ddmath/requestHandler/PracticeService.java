package com.tsinghuabigdata.edu.ddmath.requestHandler;

import com.tsinghuabigdata.edu.commons.http.HttpRequestException;
import com.tsinghuabigdata.edu.ddmath.module.errorbook.bean.VarTrainResult;
import com.tsinghuabigdata.edu.ddmath.module.exclusivepractice.bean.PracticeProductBean;
import com.tsinghuabigdata.edu.ddmath.module.exclusivepractice.bean.ShareBean;

import org.json.JSONException;

import java.util.List;

/**
 * 专属套题
 * Created by cuibo on 2017/11/16.
 */

public interface PracticeService {

    /**
     * 查询套题列表
     */
    VarTrainResult getVarTrainPracticeList(String studentId, int pageNum, int pageSize, long startTime, long endTime, int paperType) throws HttpRequestException, JSONException;

    /**
     * 分享下载
     */
    ShareBean getPracticeShare(String classId, String studentId, String productId, String recordId ) throws HttpRequestException, JSONException;

    /**
     * 查询精品套题列表
     */
    List<PracticeProductBean> getClassicPracticeList(String studentId, String searchdata, String schoolId) throws HttpRequestException, JSONException;

    /**
     * 分享下载
     */
    ShareBean getClassicPracticeShare(String classId, String studentId, String productId, String excluId ) throws HttpRequestException, JSONException;


}
