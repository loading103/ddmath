package com.tsinghuabigdata.edu.ddmath.requestHandler.requestImpl;

import com.google.gson.Gson;
import com.tsinghuabigdata.edu.commons.http.HttpRequest;
import com.tsinghuabigdata.edu.commons.http.HttpRequestException;
import com.tsinghuabigdata.edu.ddmath.bean.BookBean;
import com.tsinghuabigdata.edu.ddmath.commons.http.AppRequestUtils;
import com.tsinghuabigdata.edu.ddmath.constant.AppRequestConst;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.DDWorkDetail;
import com.tsinghuabigdata.edu.ddmath.module.learnmaterial.bean.BookListBean;
import com.tsinghuabigdata.edu.ddmath.module.learnmaterial.bean.CatalogBean;
import com.tsinghuabigdata.edu.ddmath.module.learnmaterial.bean.SelfWorkSubmitBean;
import com.tsinghuabigdata.edu.ddmath.module.neteaseim.base.util.string.MD5;
import com.tsinghuabigdata.edu.ddmath.requestHandler.LearnMaterialService;
import com.tsinghuabigdata.edu.ddmath.util.AppLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;


/**
 *
 */

public class LearnMaterialServiceImpl extends BaseService implements LearnMaterialService {

    /**
     * 原版教辅查询业内题目信息
     */
    @Override
    public DDWorkDetail querySelfWorkDetail(String bookId, String pageNums ) throws HttpRequestException, JSONException{

        String url = getUrl(AppRequestConst.QUERY_LMQUETSIONS_ANSWERAREA);
        HttpRequest request = AppRequestUtils.get(url);
        request.putRequestParam("bookId", bookId)
                .putRequestParam("pageNums", pageNums )
                .request();
        String res = request.getDataBody();
        //String res = "{\"bookId\":\"bbbiddddd\",\"bookName\":\"测试教辅\",\"allowLookAnswer\":0,\"exerStatus\":0,\"lastReviewer\":null,\"overdue\":false,\"pageCount\":1,\"pageInfo\":[{\"bookId\":null,\"pageNum\":1,\"questions\":[{\"answerArea\":null,\"answerUrl\":null,\"asked\":0,\"correct\":false,\"correctionStatus\":null,\"indexInPaper\":1,\"knowledges\":[{\"createTime\":null,\"creatorId\":null,\"difficult\":1,\"disciplineId\":null,\"knowledgeLevel\":null,\"knowledgePointContent\":\"\",\"knowledgePointId\":\"610102\",\"knowledgePointLevel\":\"4\",\"knowledgePointName\":\"个体\",\"lastModifyPId\":null,\"lastModifyTime\":null,\"parentId\":\"610100\",\"questionId\":\"31B6605224C541AE92625A86B8426E0B\",\"status\":null,\"version\":null}],\"numInPaper\":\"1\",\"questionArea\":\"[{\\\"areas\\\":[{\\\"height\\\":0.4787,\\\"width\\\":0.29,\\\"x\\\":0.04,\\\"y\\\":0},{\\\"height\\\":0.4787,\\\"width\\\":0.29,\\\"x\\\":0.3699,\\\"y\\\":0},{\\\"height\\\":0.4787,\\\"width\\\":0.29,\\\"x\\\":0.6999,\\\"y\\\":0},{\\\"height\\\":0.4787,\\\"width\\\":0.29,\\\"x\\\":0.04,\\\"y\\\":0.5213}],\\\"x\\\":0.013,\\\"y\\\":0.3217,\\\"width\\\":0.974,\\\"height\\\":0.0887,\\\"pageNum\\\":1}]\",\"questionId\":\"31B6605224C541AE92625A86B8426E0B\",\"questionScore\":9,\"questionType\":\"fill_in\",\"reviseResultResponse\":{\"answerArea\":null,\"answerUrl\":null,\"correct\":false,\"correctionStatus\":1,\"errorAnalysis\":null,\"reviseId\":null},\"solutions\":[{\"content\":\"根据题意得出点的坐标第一项是线段长度,第二项是夹角度数进而得出答案.\",\"contentLatexGraph\":\"\",\"contentg\":\"根据题意得出点的坐标第一项是线段长度,第二项是夹角度数进而得出答案.\",\"graph\":\"\",\"questionId\":\"31B6605224C541AE92625A86B8426E0B\",\"tipId\":\"E251226075F14F659D984D4C6602E4EF\"}],\"stdAnswers\":[{\"content\":\"解:∵线段OB、OC、OA的长度分别是1、2、3,且OC平分∠AOB.若将A点表示为(3,30°),B点表示为(1,120°)\",\"contentLatexGraph\":\"\",\"contentg\":\"解:∵线段OB、OC、OA的长度分别是1、2、3,且OC平分∠AOB.若将A点表示为(3,30°),B点表示为(1,120°)\",\"graph\":\"\",\"keyStep\":1,\"knowledgeIds\":\"610102\",\"knowledgeVoList\":null,\"latexUrl\":null,\"questionId\":\"31B6605224C541AE92625A86B8426E0B\",\"score\":1,\"stdAnswerId\":\"AE7BA218D8D643329235B615B81A73C1\",\"stepNum\":1,\"subStemCon\":0,\"subStemNum\":0,\"wayOfNum\":0},{\"content\":\"∴∠AOB=90°,∠AOC=45°\",\"contentLatexGraph\":\"\",\"contentg\":\"∴∠AOB=90°,∠AOC=45°\",\"graph\":\"\",\"keyStep\":0,\"knowledgeIds\":\"\",\"knowledgeVoList\":null,\"latexUrl\":null,\"questionId\":\"31B6605224C541AE92625A86B8426E0B\",\"score\":0,\"stdAnswerId\":\"9DB53EDB14E6419B8D5D4D556F699130\",\"stepNum\":2,\"subStemCon\":0,\"subStemNum\":0,\"wayOfNum\":0},{\"content\":\"则C点可表示为(2,75°)\",\"contentLatexGraph\":\"\",\"contentg\":\"则C点可表示为(2,75°)\",\"graph\":\"\",\"keyStep\":0,\"knowledgeIds\":\"\",\"knowledgeVoList\":null,\"latexUrl\":null,\"questionId\":\"31B6605224C541AE92625A86B8426E0B\",\"score\":0,\"stdAnswerId\":\"FD1FF15E888B4ED1B5941596139BD142\",\"stepNum\":3,\"subStemCon\":0,\"subStemNum\":0,\"wayOfNum\":0},{\"content\":\"故答案为:(2,75°)\",\"contentLatexGraph\":\"\",\"contentg\":\"故答案为:(2,75°)\",\"graph\":\"\",\"keyStep\":0,\"knowledgeIds\":\"\",\"knowledgeVoList\":null,\"latexUrl\":null,\"questionId\":\"31B6605224C541AE92625A86B8426E0B\",\"score\":0,\"stdAnswerId\":\"19B883ACFA374F4A9DC9A539F8F0B2C5\",\"stepNum\":4,\"subStemCon\":0,\"subStemNum\":0,\"wayOfNum\":0}],\"stem\":null,\"stemGraph\":\"group2/M00/01/3B/rBAHGVoFFPKAcjh5AAAPPCfFtGM962.jpg\",\"stemLatexGraph\":\"\",\"stemg\":\"(2017春·长安区期中)如图,线段OB、OC、OA的长度分别是1、2、3,且OC平分∠AOB.若将A点表示为(3,30°),B点表示为(1,120°),则C点可表示为　(2,75°)　.#%#<img>\",\"studentScore\":0,\"subKnowledges\":null,\"subLatexGraph\":null,\"subStem\":null,\"subStemGraph\":\"\",\"subStemg\":null},{\"answerArea\":null,\"answerUrl\":null,\"asked\":2,\"correct\":false,\"correctionStatus\":null,\"indexInPaper\":2,\"knowledges\":[{\"createTime\":null,\"creatorId\":null,\"difficult\":4,\"disciplineId\":null,\"knowledgeLevel\":null,\"knowledgePointContent\":\"\",\"knowledgePointId\":\"310405\",\"knowledgePointLevel\":\"4\",\"knowledgePointName\":\"钟面角\",\"lastModifyPId\":null,\"lastModifyTime\":null,\"parentId\":\"310400\",\"questionId\":\"B3FF4B4974B842488D04A15F34281B47\",\"status\":null,\"version\":null}],\"numInPaper\":\"第二\",\"questionArea\":\"[{\\\"x\\\":0.013,\\\"y\\\":0.4774,\\\"width\\\":0.974,\\\"height\\\":0.0566,\\\"pageNum\\\":1}]\",\"questionId\":\"B3FF4B4974B842488D04A15F34281B47\",\"questionScore\":30,\"questionType\":\"solution\",\"reviseResultResponse\":{\"answerArea\":null,\"answerUrl\":null,\"correct\":false,\"correctionStatus\":1,\"errorAnalysis\":null,\"reviseId\":null},\"solutions\":[{\"content\":\"原式先计算乘方运算,再计算乘除运算,最后算加减运算即可得到结果.\",\"contentLatexGraph\":\"\",\"contentg\":\"原式先计算乘方运算,再计算乘除运算,最后算加减运算即可得到结果.\",\"graph\":\"\",\"questionId\":\"B3FF4B4974B842488D04A15F34281B47\",\"tipId\":\"DDCB460F64A5425A95B640CDE4C4A95F\"}],\"stdAnswers\":[{\"content\":\"解:原式=-9-4+1\",\"contentLatexGraph\":\"\",\"contentg\":\"解:原式=-9-4+1\",\"graph\":\"\",\"keyStep\":1,\"knowledgeIds\":\"310405\",\"knowledgeVoList\":null,\"latexUrl\":null,\"questionId\":\"B3FF4B4974B842488D04A15F34281B47\",\"score\":1,\"stdAnswerId\":\"4B7546D28F174AFD85AB73A999ECC3E5\",\"stepNum\":1,\"subStemCon\":0,\"subStemNum\":0,\"wayOfNum\":0},{\"content\":\"=-12\",\"contentLatexGraph\":\"\",\"contentg\":\"=-12\",\"graph\":\"\",\"keyStep\":0,\"knowledgeIds\":\"\",\"knowledgeVoList\":null,\"latexUrl\":null,\"questionId\":\"B3FF4B4974B842488D04A15F34281B47\",\"score\":0,\"stdAnswerId\":\"129272B47DFF4C89942F31F56993C08D\",\"stepNum\":2,\"subStemCon\":0,\"subStemNum\":0,\"wayOfNum\":0}],\"stem\":null,\"stemGraph\":\"\",\"stemLatexGraph\":\"fsglatex/M00/26/30/rBAHGFmSyZiARaMtAAAWBvitWJE139.png\",\"stemg\":\"计算:<latex>.\",\"studentScore\":0,\"subKnowledges\":null,\"subLatexGraph\":null,\"subStem\":null,\"subStemGraph\":\"\",\"subStemg\":null}],\"submitStatus\":0,\"title\":null,\"tmpQuestion\":{\"answerArea\":null,\"answerUrl\":null,\"asked\":0,\"correct\":false,\"correctionStatus\":null,\"indexInPaper\":1,\"knowledges\":[{\"createTime\":null,\"creatorId\":null,\"difficult\":1,\"disciplineId\":null,\"knowledgeLevel\":null,\"knowledgePointContent\":\"\",\"knowledgePointId\":\"610102\",\"knowledgePointLevel\":\"4\",\"knowledgePointName\":\"个体\",\"lastModifyPId\":null,\"lastModifyTime\":null,\"parentId\":\"610100\",\"questionId\":\"31B6605224C541AE92625A86B8426E0B\",\"status\":null,\"version\":null}],\"numInPaper\":\"1\",\"questionArea\":\"[{\\\"areas\\\":[{\\\"height\\\":0.4787,\\\"width\\\":0.29,\\\"x\\\":0.04,\\\"y\\\":0},{\\\"height\\\":0.4787,\\\"width\\\":0.29,\\\"x\\\":0.3699,\\\"y\\\":0},{\\\"height\\\":0.4787,\\\"width\\\":0.29,\\\"x\\\":0.6999,\\\"y\\\":0},{\\\"height\\\":0.4787,\\\"width\\\":0.29,\\\"x\\\":0.04,\\\"y\\\":0.5213}],\\\"x\\\":0.013,\\\"y\\\":0.3217,\\\"width\\\":0.974,\\\"height\\\":0.0887,\\\"pageNum\\\":1}]\",\"questionId\":\"31B6605224C541AE92625A86B8426E0B\",\"questionScore\":9,\"questionType\":\"fill_in\",\"reviseResultResponse\":{\"answerArea\":null,\"answerUrl\":null,\"correct\":false,\"correctionStatus\":1,\"errorAnalysis\":null,\"reviseId\":null},\"solutions\":[{\"content\":\"根据题意得出点的坐标第一项是线段长度,第二项是夹角度数进而得出答案.\",\"contentLatexGraph\":\"\",\"contentg\":\"根据题意得出点的坐标第一项是线段长度,第二项是夹角度数进而得出答案.\",\"graph\":\"\",\"questionId\":\"31B6605224C541AE92625A86B8426E0B\",\"tipId\":\"E251226075F14F659D984D4C6602E4EF\"}],\"stdAnswers\":[{\"content\":\"解:∵线段OB、OC、OA的长度分别是1、2、3,且OC平分∠AOB.若将A点表示为(3,30°),B点表示为(1,120°)\",\"contentLatexGraph\":\"\",\"contentg\":\"解:∵线段OB、OC、OA的长度分别是1、2、3,且OC平分∠AOB.若将A点表示为(3,30°),B点表示为(1,120°)\",\"graph\":\"\",\"keyStep\":1,\"knowledgeIds\":\"610102\",\"knowledgeVoList\":null,\"latexUrl\":null,\"questionId\":\"31B6605224C541AE92625A86B8426E0B\",\"score\":1,\"stdAnswerId\":\"AE7BA218D8D643329235B615B81A73C1\",\"stepNum\":1,\"subStemCon\":0,\"subStemNum\":0,\"wayOfNum\":0},{\"content\":\"∴∠AOB=90°,∠AOC=45°\",\"contentLatexGraph\":\"\",\"contentg\":\"∴∠AOB=90°,∠AOC=45°\",\"graph\":\"\",\"keyStep\":0,\"knowledgeIds\":\"\",\"knowledgeVoList\":null,\"latexUrl\":null,\"questionId\":\"31B6605224C541AE92625A86B8426E0B\",\"score\":0,\"stdAnswerId\":\"9DB53EDB14E6419B8D5D4D556F699130\",\"stepNum\":2,\"subStemCon\":0,\"subStemNum\":0,\"wayOfNum\":0},{\"content\":\"则C点可表示为(2,75°)\",\"contentLatexGraph\":\"\",\"contentg\":\"则C点可表示为(2,75°)\",\"graph\":\"\",\"keyStep\":0,\"knowledgeIds\":\"\",\"knowledgeVoList\":null,\"latexUrl\":null,\"questionId\":\"31B6605224C541AE92625A86B8426E0B\",\"score\":0,\"stdAnswerId\":\"FD1FF15E888B4ED1B5941596139BD142\",\"stepNum\":3,\"subStemCon\":0,\"subStemNum\":0,\"wayOfNum\":0},{\"content\":\"故答案为:(2,75°)\",\"contentLatexGraph\":\"\",\"contentg\":\"故答案为:(2,75°)\",\"graph\":\"\",\"keyStep\":0,\"knowledgeIds\":\"\",\"knowledgeVoList\":null,\"latexUrl\":null,\"questionId\":\"31B6605224C541AE92625A86B8426E0B\",\"score\":0,\"stdAnswerId\":\"19B883ACFA374F4A9DC9A539F8F0B2C5\",\"stepNum\":4,\"subStemCon\":0,\"subStemNum\":0,\"wayOfNum\":0}],\"stem\":null,\"stemGraph\":\"group2/M00/01/3B/rBAHGVoFFPKAcjh5AAAPPCfFtGM962.jpg\",\"stemLatexGraph\":\"\",\"stemg\":\"(2017春·长安区期中)如图,线段OB、OC、OA的长度分别是1、2、3,且OC平分∠AOB.若将A点表示为(3,30°),B点表示为(1,120°),则C点可表示为　(2,75°)　.#%#<img>\",\"studentScore\":0,\"subKnowledges\":null,\"subLatexGraph\":null,\"subStem\":null,\"subStemGraph\":\"\",\"subStemg\":null}}],\"parentExamId\":\"WEEK942DD8C61933402491937390B0AA5949\",\"questionScore\":39,\"revoked\":0,\"rightQuestionCount\":0,\"sourceType\":\"training\",\"studentScore\":0,\"topKnowledgeList\":[],\"usedCount\":250,\"wrongQuestionCount\":0}";
        return new Gson().fromJson(res, DDWorkDetail.class);
    }

    /**
     * 原版教辅 学生创建 && 上传作业接口
     */
    @Override
    public SelfWorkSubmitBean createAndUploadSelfWork(String studentId, String classId, int pageCount, int uploadType, JSONArray pages ) throws HttpRequestException, JSONException{

        String url = getUrl(AppRequestConst.POST_SUBMIT_LMSELFWORK);
        HttpRequest request = AppRequestUtils.post(url);
        request.setTimeout(5 * 60);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("studentId", studentId);
            jsonObject.put("classId", classId);
            jsonObject.put("pageCount", pageCount);
            jsonObject.put("pages", pages);
            jsonObject.put("uploadType",uploadType);
            jsonObject.put("md5", MD5.getStringMD5(pages.toString()));
        } catch (Exception e) {
            AppLog.i("", e);
        }
        request.setJsonStringParams(jsonObject.toString());
        String res = request.requestJson().getBody();

        return new Gson().fromJson(res, SelfWorkSubmitBean.class);
    }

    @Override
    public BookListBean queryBookList(HashMap<String, String> params) throws HttpRequestException, JSONException {
        String url = getUrl(AppRequestConst.QUERY_BOOK_LIST);
        HttpRequest request = AppRequestUtils.get(url);
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
        return new Gson().fromJson(res, BookListBean.class);
    }

    @Override
    public BookBean selectBook(String studentId, String bookId) throws HttpRequestException, JSONException {
        String url = getUrl(AppRequestConst.SELECT_BOOK);
        HttpRequest request = AppRequestUtils.post(url);
        request.putRequestParam("studentId", studentId)
                .putRequestParam("bookId", bookId)
                .request();
        String res = request.getDataBody();
        return new Gson().fromJson(res, BookBean.class);
    }

    @Override
    public CatalogBean queryBookCatalog(HashMap<String, String> params) throws HttpRequestException, JSONException {
        String url = getUrl(AppRequestConst.QUERY_BOOK_CATALOG);
        HttpRequest request = AppRequestUtils.get(url);
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
        return new Gson().fromJson(res, CatalogBean.class);
    }

}
