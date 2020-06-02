package com.tsinghuabigdata.edu.ddmath.MVPModel;

import com.tsinghuabigdata.edu.ddmath.bean.BookBean;
import com.tsinghuabigdata.edu.ddmath.bean.StudentQuestionVo;
import com.tsinghuabigdata.edu.ddmath.commons.http.AppAsyncTask;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.requestHandler.QuestionBookService;
import com.tsinghuabigdata.edu.ddmath.requestHandler.requestImpl.QuestionBookServiceImpl;

import java.util.HashMap;


/**
 * 错题本
 */

public class QuestionBookModel {

    private QuestionBookService mQuestionBookService = new QuestionBookServiceImpl();



    /**
     * 学生错题查询
     */
    class QuestionsTask extends AppAsyncTask<String, Void, StudentQuestionVo> {

        private RequestListener reqListener;

        public QuestionsTask(RequestListener requestListener) {
            reqListener = requestListener;
        }


        @Override
        protected StudentQuestionVo doExecute(String... params) throws Exception {
            String studentId = params[0];
            String key = params[1];
            String sort = params[2];
            long startDate = Long.parseLong(params[3]);
            long endDate = Long.parseLong(params[4]);
            boolean isShowCorrect = Boolean.parseBoolean(params[5]);
            String wrongQuestionClasstype = params[6];
            int pageNum = Integer.parseInt(params[7]);
            int pageSize = Integer.parseInt(params[8]);
            return mQuestionBookService.queryQuestions(studentId, key, sort, startDate, endDate, isShowCorrect, wrongQuestionClasstype, pageNum, pageSize);
        }

        @Override
        protected void onResult(StudentQuestionVo studentQuestionVo) {
            reqListener.onSuccess(studentQuestionVo);
        }

        @Override
        protected void onFailure(HttpResponse<StudentQuestionVo> response, Exception ex) {
            reqListener.onFail(response, ex);
        }


    }

    /**
     * pdf生成请求
     */
    class ProductPdfTask extends AppAsyncTask<HashMap<String, String>, Void, BookBean> {

        private RequestListener reqListener;

        public ProductPdfTask(RequestListener requestListener) {
            reqListener = requestListener;
        }


        @Override
        protected BookBean doExecute(HashMap<String, String>... maps) throws Exception {
            HashMap<String, String> map = maps[0];
            return mQuestionBookService.productPdf(map);
        }

        @Override
        protected void onResult(BookBean bookBean) {
            reqListener.onSuccess(bookBean);
        }

        @Override
        protected void onFailure(HttpResponse<BookBean> response, Exception ex) {
            reqListener.onFail(response, ex);
        }

    }

    /**
     * pdf生成结果查询
     */
    class SearchPdfTask extends AppAsyncTask<String, Void, BookBean> {

        private RequestListener reqListener;

        public SearchPdfTask(RequestListener requestListener) {
            reqListener = requestListener;
        }


        @Override
        protected BookBean doExecute(String... params) throws Exception {
            String map = params[0];
            return mQuestionBookService.serachPdf(map);
        }

        @Override
        protected void onResult(BookBean bookBean) {
            reqListener.onSuccess(bookBean);
        }

        @Override
        protected void onFailure(HttpResponse<BookBean> response, Exception ex) {
            reqListener.onFail(response, ex);
        }


    }


    /**
     * 学生错题查询
     */
    public void queryQuestions(String studentId, String key, String sort, long startDate, long endDate, boolean isShowCorrect,
                               String wrongQuestionClasstype, int pageNum, int pageSize, RequestListener requestListener) {
        QuestionsTask task = new QuestionsTask(requestListener);
        task.executeMulti(studentId, key, sort, startDate + "", endDate + "", isShowCorrect + "", wrongQuestionClasstype, pageNum + "", pageSize + "");
    }


    /**
     * pdf生成请求
     */
    public void productPdf(HashMap<String, String> params, RequestListener requestListener) {
        ProductPdfTask task = new ProductPdfTask(requestListener);
        task.executeMulti(params);
    }


    /**
     * pdf生成结果查询
     */
    public void searchPdf(String bookId, RequestListener requestListener) {
        SearchPdfTask task = new SearchPdfTask(requestListener);
        task.executeMulti(bookId);
    }


}
