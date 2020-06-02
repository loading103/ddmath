package com.tsinghuabigdata.edu.ddmath.module.ddwork;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.ZxApplication;
import com.tsinghuabigdata.edu.ddmath.bean.SubmitQuestion;
import com.tsinghuabigdata.edu.ddmath.bean.UserDetailinfo;
import com.tsinghuabigdata.edu.ddmath.constant.AppConst;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.DDWorkDetail;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.EKPiontBean;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.KnowledgePiontBean;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.LocalPageInfo;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.LocalQuestionInfo;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.LocalWorkInfo;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.QuestionInfo;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.QuestionType;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.ReviseResultInfo;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.SolveThinkBean;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.view.QuestionItemView;
import com.tsinghuabigdata.edu.ddmath.module.mylearn.bean.ImageCorrectBean;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.AppLog;
import com.tsinghuabigdata.edu.ddmath.util.DateUtils;
import com.tsinghuabigdata.edu.ddmath.util.DensityUtils;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.PreferencesUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


/**
 * 题目相关工具类
 */
public class DDWorkUtil {

    /*public*/ final static String WORK_ID   = "workid";
    /*public*/ static final String PAGEINDEX = "index";

    //题目类型显示处理
    private static HashMap<String, String> questionHashMap = new HashMap<>();

    public static void init(Context mContext) {

        //题目类型
        String qtype_ch[] = mContext.getResources().getStringArray(R.array.question_type_ch);
        String qtype_en[] = mContext.getResources().getStringArray(R.array.question_type_en);

        int minlen = qtype_ch.length < qtype_en.length ? qtype_ch.length : qtype_en.length;
        for (int i = 0; i < minlen; i++) {
            questionHashMap.put(qtype_ch[i], qtype_en[i]);
            questionHashMap.put(qtype_en[i], qtype_ch[i]);
        }
    }

    //订正按钮点击前先检查是否有错题订正使用权
    //    public static boolean showReviseTipsDailog(final Context context ){
    //        ProductUseTimesDetail detail = AccountUtils.getProductUseTimesDetail();
    //        if( context == null || detail== null )
    //            return false;
    //
    //        if( detail.getErrReviseCount() > 0 )  return true;
    //
    //        AlertManager.showCustomImageBtnDialog( context, "您目前没有可使用的错题订正次数，请先兑换后再订正。", R.drawable.selector_goexchange_btn, new DialogInterface.OnClickListener() {
    //            @Override
    //            public void onClick(DialogInterface dialog, int which) {
    //                ProductExchangeActivity.gotoProductExchangeActivity( context, "错题订正", AppConst.PRIVILEGE_QUESTION_DAYCLEAR );
    //            }
    //        });
    //        return false;
    //    }

    public static String getQuestionTypeName(String type) {
        return questionHashMap.get(type);
    }

    //显示序号
    //    public static int getQuestionTypeIndex(Context mContext, String type) {
    //
    //        //题目类型
    //        String qtype_ch[] = mContext.getResources().getStringArray(R.array.question_type_ch);
    //        String qtype_en[] = mContext.getResources().getStringArray(R.array.question_type_en);
    //
    //        for (int i = 0; i < qtype_en.length; i++) {
    //            if (qtype_en[i].equals(type))
    //                return i;
    //        }
    //        for (int i = 0; i < qtype_ch.length; i++) {
    //            if (qtype_ch[i].equals(type))
    //                return i;
    //        }
    //        return -1;
    //    }

    //教辅名称处理，仅仅第一次出现时显示
    /*public*/
    static void dealMaterailName(ArrayList<LocalPageInfo> list) {
        if (list == null)
            return;

        String bookName = "";
        String chapterName = "";
        for (LocalPageInfo pageInfo : list) {
            String name = pageInfo.getLearnMaterialName();
            if (TextUtils.isEmpty(name))
                name = "";
            String chapter = pageInfo.getChapterName();
            if (TextUtils.isEmpty(chapter))
                chapter = "";
            //            if( !bookName.equals(name) || !chapterName.equals(chapter) ) {
            //                pageInfo.setShowTitle(true);
            //                bookName = name;
            //                chapterName = chapter;
            //            }
            if (!bookName.equals(name)) {
                pageInfo.setShowBookName(true);
                bookName = name;
            }
            if (!chapterName.equals(chapter)) {
                pageInfo.setShowChapterName(true);
                chapterName = chapter;
            }
        }
    }

    //作业上传类型，处理到每一页，拍照使用
    static void dealUploadType(ArrayList<LocalPageInfo> list, int uploadType) {
        if (list == null)
            return;
        for (LocalPageInfo pageInfo : list) {
            pageInfo.setUploadType( uploadType );
            for( LocalQuestionInfo questionInfo:pageInfo.getQuestions() ){
                questionInfo.setUploadType( uploadType );
            }
        }
    }
    //拍照上传作业，作业题目都默认正确
//    /*public*/ static void dealQuestionCorrectRight(ArrayList<LocalPageInfo> list) {
//        if (list == null)
//            return;
//        for (LocalPageInfo pageInfo : list) {
//            for (LocalQuestionInfo questionInfo:pageInfo.getQuestions()){
//                questionInfo.setCorrect(true);
//                if( questionInfo.getSubQuestions()!=null ){
//                    for(SubQuestionInfo subQuestionInfo: questionInfo.getSubQuestions() ){
//                        subQuestionInfo.setSubCorrect(true);
//                    }
//                }
//            }
//        }
//    }
    //拍照上传作业，作业题目都默认正确
    /*public*/ static void dealQuestionCorrectUploadStaus(ArrayList<LocalPageInfo> list) {
        if (list == null)
            return;
        for (LocalPageInfo pageInfo : list) {
            for (LocalQuestionInfo questionInfo:pageInfo.getQuestions()){
                questionInfo.setOnlyErrQuestion(true);
            }
        }
    }

    //原版教辅名称处理
    /*public*/
    static void dealOriginMaterailName(ArrayList<LocalPageInfo> list, String bookName) {
        if (list == null)
            return;

        for (LocalPageInfo pageInfo : list) {
            pageInfo.setLearnMaterialName(bookName);
        }
    }

    private static String numberStr[] = {"一", "二", "三", "四", "五", "六", "七", "八", "九", "十"};

    private static String getNumberStr(int index) {
        if (index < 0 || index >= 10)
            return "";
        return numberStr[index];
    }

    //处理题目的题型显示，非教辅作业
    static void dealQuestionType(ArrayList<LocalPageInfo> list) {
        if (list == null || list.size() == 0)
            return;

        //教辅类，不显示
        LocalPageInfo localPageInfo = list.get(0);
        if (!TextUtils.isEmpty(localPageInfo.getLearnMaterialName()))
            return;

        QuestionType questionType = null;
        for (LocalPageInfo pageInfo : list) {
            for (LocalQuestionInfo questionInfo : pageInfo.getQuestions()) {
                if (questionType == null) {
                    questionType = new QuestionType();
                    //questionType.setPageNum( pageNum );
                    questionType.setQustionType(questionInfo.getQuestionType());
                    questionType.setShowIndex(0);
                    //questionType.setSortIndex( DDWorkUtil.getQuestionTypeIndex(mContext,type) );
                    String data = getNumberStr(questionType.getShowIndex()) + "、" + DDWorkUtil.getQuestionTypeName(questionInfo.getQuestionType());
                    questionInfo.setShowQuestionType(data);
                } else if (questionType.getQustionType() == null) {
                    continue;
                } else if (!questionType.getQustionType().equals(questionInfo.getQuestionType())) {

                    QuestionType tmpQuestionType = new QuestionType();
                    //tmpQuestionType.setPageNum( pageNum );
                    tmpQuestionType.setQustionType(questionInfo.getQuestionType());
                    tmpQuestionType.setShowIndex(questionType.getShowIndex() + 1);
                    //questionType.setSortIndex( DDWorkUtil.getQuestionTypeIndex(mContext,type) );
                    String data = getNumberStr(tmpQuestionType.getShowIndex()) + "、" + DDWorkUtil.getQuestionTypeName(questionInfo.getQuestionType());
                    questionInfo.setShowQuestionType(data);

                    questionType = tmpQuestionType;
                }
                questionInfo.setIndexQuestionType(getNumberStr(questionType.getShowIndex()));
            }
        }

    }

    //处理题目,默认选中
    static void dealAllQuestionSelect(ArrayList<LocalPageInfo> list) {
        if (list == null || list.size() == 0)
            return;

        for (LocalPageInfo pageInfo : list) {
            for (LocalQuestionInfo questionInfo : pageInfo.getQuestions()) {
                questionInfo.setSelect(true);
            }
        }
    }

    //题目里面显示页信息，第一页不显示， 同时把题目都转移到第一页上面
    static void dealQuestionPageInfo(ArrayList<LocalPageInfo> list, boolean inlinepage) {
        if (list == null || list.size() == 0)
            return;

        ArrayList<LocalQuestionInfo> firstQuestionInfos = null;

        for (int i = 0; i < list.size(); i++) {
            LocalPageInfo pageInfo = list.get(i);
            if (i == 0) {
                firstQuestionInfos = pageInfo.getQuestions();
            }

            ArrayList<LocalQuestionInfo> questionInfos = pageInfo.getQuestions();
            if (questionInfos != null && questionInfos.size() > 0) {
                if (inlinepage) {
                    questionInfos.get(0).setPageInfo("第 " +/*(i+1)*/ pageInfo.getPageNum() + " 页");
                    if (i > 0)
                        firstQuestionInfos.addAll(questionInfos);
                }
                for (LocalQuestionInfo questionInfo : questionInfos) {
                    questionInfo.setPageNum(i);
                    questionInfo.setShowChapterName(pageInfo.getChapterName());
                }
            }
        }
    }

    //周练作业每个题目里面显示分数信息
    /*public*/
    static void dealQuestionWeekTrain(ArrayList<LocalPageInfo> list, boolean showScore, boolean isCorrected, int color) {
        if (list == null || list.size() == 0 || !showScore)
            return;

        for (LocalPageInfo pageInfo : list) {
            for (LocalQuestionInfo questionInfo : pageInfo.getQuestions()) {
                String score;
                boolean questionCorrected = false;
                if (!TextUtils.isEmpty(questionInfo.getAnswerArea()))
                    questionCorrected = true;

                if (isCorrected || questionCorrected) {      //此题已批阅
                    score = "（得" + (int) questionInfo.getStudentScore() + "分,共" + (int) questionInfo.getQuestionScore() + "分）";
                } else {
                    score = "（" + (int) questionInfo.getQuestionScore() + "分）";
                }
                questionInfo.setKeywordString(score);
                questionInfo.setKeywordColor(color);
                String stem = questionInfo.getStem();
                if( !TextUtils.isEmpty(stem) ){
                    if( stem.startsWith( score) ) score = "";
                }
                questionInfo.setStem(score + questionInfo.getStem());
            }
        }
    }

    //获得题目的错误知识点
    public static String getErrorKnowledgeStr(QuestionInfo questionInfo) {

        //先获取子问的错误知识点
        ArrayList<KnowledgePiontBean> list = questionInfo.getSubKnowledges();
        if (list == null || list.size() == 0) { //没有子问，或者没有错误知识点
            list = questionInfo.getKnowledges();        //题目知识点
        }
        if (list == null)
            return "";

        HashMap<String, String> knowMap = new HashMap<>();
        StringBuilder sb = new StringBuilder();
        for (KnowledgePiontBean bean : list) {
            if (!knowMap.containsKey(bean.getKnowledgeId())) {
                knowMap.put(bean.getKnowledgeId(), bean.getKnowledgeName());
                sb.append(bean.getKnowledgeName()).append("   ");
            }
        }
        return sb.toString();
    }

    //获得题目的错误知识点Top3
    public static String getTop3EKPoint(QuestionInfo questionInfo, ArrayList<EKPiontBean> ekpiontList, int status) {
        if (status != 0)      //未提交 或者 答对 不显示
            return "";

        //先获取子问的错误知识点
        ArrayList<KnowledgePiontBean> list = questionInfo.getSubKnowledges();
        if (list == null || list.size() == 0) { //没有子问，或者没有错误知识点
            list = questionInfo.getKnowledges();        //题目知识点
        }
        if (list == null || ekpiontList == null)
            return "";
        //top3  从里面查找知识点
        HashMap<String, String> topKnowMap = new HashMap<>();
        for (EKPiontBean ekPiontBean : ekpiontList) {
            topKnowMap.put(ekPiontBean.getKnowledgeId(), ekPiontBean.getKnowledgeName());
        }

        //缓存去重
        HashMap<String, String> knowMap = new HashMap<>();
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (KnowledgePiontBean bean : list) {
            if (topKnowMap.containsKey(bean.getKnowledgeId()) && !knowMap.containsKey(bean.getKnowledgeId())) {
                knowMap.put(bean.getKnowledgeId(), bean.getKnowledgeName());
                if (first) {
                    first = false;
                } else {
                    sb.append(",");
                }
                sb.append(bean.getKnowledgeName());
            }
        }
        return sb.toString();
    }

    //输出的字符串对齐显示
    /*public*/
    static String getAlginCount(int val1, int val2) {

        int max = val1 > val2 ? val1 : val2;
        if (max < 10)
            return String.valueOf(val1);
        else if (max < 100) {
            if (val1 < 10) {
                return " " + val1;
            } else
                return String.valueOf(val1);
        } else if (max < 1000) {
            if (val1 < 10) {
                return " " + val1 + " ";
            } else if (val1 < 100) {
                return " " + val1;
            } else
                return String.valueOf(val1);
        }
        //大于1000不处理
        return String.valueOf(val1);
    }

    //设置解题思路
    public static void setSolveThink(List<SolveThinkBean> list, QuestionItemView textView) {
        if (list == null || list.size() == 0)
            return;

        String contentLatextGraph = null;
        String contentg = null;
        String graph = null;

        for (int i = 0; i < list.size(); i++) {
            SolveThinkBean bean = list.get(i);
            if (TextUtils.isEmpty(contentg)) {
                contentg = bean.getContentg();
            } else if (TextUtils.isEmpty(bean.getContentg())) {
                contentg += "\n";
            } else {
                contentg += "\n" + bean.getContentg();
            }
            if (!TextUtils.isEmpty(bean.getContentLatexGraph()) && !"(null)".equals(bean.getContentLatexGraph())) {
                if (TextUtils.isEmpty(contentLatextGraph)) {
                    contentLatextGraph = bean.getContentLatexGraph();
                } else {
                    contentLatextGraph += "#%#" + bean.getContentLatexGraph();
                }
            }
            if (!TextUtils.isEmpty(bean.getGraph()) && !"(null)".equals(bean.getGraph())) {
                if (TextUtils.isEmpty(graph)) {
                    graph = bean.getGraph();
                } else {
                    graph += "#%#" + bean.getGraph();
                }
            }
        }
        if (!TextUtils.isEmpty(contentg))
            contentg += "#%# ";
        textView.setData("解题思路", contentg, graph, contentLatextGraph);
    }

    //reviseAnswer 是否是订正结果
    public static int getQuestionCorrectStatus(QuestionInfo questionInfo, boolean reviseAnswer, int submitStatus) {

        String answerUrl = questionInfo.getAnswerUrl();
        String correctResult = questionInfo.getAnswerArea();
        boolean isRight = questionInfo.isCorrect();
        if (reviseAnswer) {
            ReviseResultInfo reviseResultInfo = questionInfo.getReviseResultResponse();
            if (reviseResultInfo != null) {
                answerUrl = reviseResultInfo.getAnswerUrl();
                correctResult = reviseResultInfo.getAnswerArea();
                isRight = reviseResultInfo.isCorrect();
            } else {
                answerUrl = "";
                correctResult = "";
                isRight = false;
            }
        }else{
            //对于批阅结果上传的类型，只要提交了答案图片就已批阅结果为准
            if( AppConst.UPLOAD_TYPE_MARKED == questionInfo.getUploadType() && !TextUtils.isEmpty(questionInfo.getAnswerUrl()))
                return questionInfo.isCorrect() ? 1 : 0;
        }

        //没有提交
        if (TextUtils.isEmpty(correctResult) || (TextUtils.isEmpty(answerUrl) && submitStatus <= DDWorkDetail.WORK_UNSUBMIT))
            return -1;

        ArrayList<ImageCorrectBean> list = new Gson().fromJson(correctResult, new TypeToken<ArrayList<ImageCorrectBean>>() {
        }.getType());
        if (list == null || list.size() == 0)      //没有
            return -1;

        //        int result = 1;
        //        if( questionInfo.getQuestionScore() > 0 ){      //有分数
        //            if( questionInfo.getStudentScore() < questionInfo.getQuestionScore() )
        //                result = 0;
        //        }else{
        //            for( ImageCorrectBean bean : list ){
        //                if( bean.getStatus() == ImageCorrectBean.CORRECT_WRONG  || bean.getStatus() == ImageCorrectBean.CORRECT_SMALL_WRONG
        //                        ||bean.getStatus() == ImageCorrectBean.CORRECT_HALF || bean.getStatus() == ImageCorrectBean.CORRECT_SMALL_HALF
        //                        || bean.getStatus() == ImageCorrectBean.CORRECT_DOUBT_WRONG ){
        //                    return 0;
        //                }
        //            }
        //        }
        //        return result;
        return isRight ? 1 : 0;
    }

    /*public static void putWorkId(String workId) {
        UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
        if (detailinfo != null) {
            PreferencesUtils.putString(ZxApplication.getApplication(), detailinfo.getStudentId() + "_workId", workId);
        }
    }*/

    public static String getWorkId() {
        UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
        if (detailinfo != null) {
            PreferencesUtils.getString(ZxApplication.getApplication(), detailinfo.getStudentId() + "_workId", "");
        }
        return null;
    }

    //去除重复的题目
    /*public*/
    static void dealDuplicateQuestion(ArrayList<LocalQuestionInfo> list) {
        int index = 0;
        for (int i = index; i < list.size() - 1; i++) {
            LocalQuestionInfo questionInfo = list.get(i);
            for (int j = i + 1; j < list.size(); j++) {
                if (questionInfo.getQuestionId().equals(list.get(j).getQuestionId())) {
                    if (j + 1 < list.size()) {
                        LocalQuestionInfo q_dup = list.get(j);
                        LocalQuestionInfo q_nex = list.get(j + 1);
                        if (q_nex.getPageNum() == q_dup.getPageNum())
                            q_nex.setPageInfo(q_dup.getPageInfo());
                    }
                    list.remove(j);

                    dealDuplicateQuestion(list);
                    return;
                }
            }
        }
    }

    public static String getEndMinute(long endTime) {
        SimpleDateFormat format;
        if (DateUtils.IsToday(endTime)) {
            format = new SimpleDateFormat("今日HH:mm前提交", Locale.getDefault());
        } else {
            format = new SimpleDateFormat("M月d日HH:mm前提交", Locale.getDefault());
        }
        return format.format(endTime);
    }

    public static String getDayReportEndMinute(long endTime) {
        SimpleDateFormat format;
        if (DateUtils.IsToday(endTime)) {
            format = new SimpleDateFormat("请提醒孩子在 今日 HH:mm前提交", Locale.getDefault());
        } else {
            format = new SimpleDateFormat("请提醒孩子在 yyyy-MM-dd HH:mm前提交", Locale.getDefault());
        }
        return format.format(endTime);
    }

    public static String getDayReportSubmiteMinute(long endTime) {
        SimpleDateFormat format;
        if (DateUtils.IsToday(endTime)) {
            format = new SimpleDateFormat("提交时间  今日 HH:mm", Locale.getDefault());
        } else {
            format = new SimpleDateFormat("提交时间  yyyy-MM-dd HH:mm", Locale.getDefault());
        }
        return format.format(endTime);
    }

    public static String getStartDay(SubmitQuestion item) {
        String[] weeks = {"", "周一", "周二", "周三", "周四", "周五", "周六", "周日"};
        String date;
        if (DateUtils.IsToday(item.getStartTime())) {
            date = "今日";
        } else {
            date = DateUtils.getDay(item.getStartTime());
        }
        try {
            date = date + " " + weeks[item.getWeekday()];
        } catch (Exception e) {
            AppLog.i("", e);
        }
        return date;
    }

    public static Drawable getWorkTypeDrawable(Context context, String resourceType) {
        Drawable drawable;

        if (AppConst.WORK_TYPE_LEARNMATER.equals(resourceType) || AppConst.WORK_TYPE_LEARNBOOK.equals(resourceType)) {
            drawable = context.getResources().getDrawable(R.drawable.iconfudao);
        } else if (AppConst.WORK_TYPE_WEEKTRAIN.equals(resourceType)) {
            drawable = context.getResources().getDrawable(R.drawable.artboard);
        } else if (AppConst.WORK_TYPE_VACATION_WORK.equals(resourceType)) {
            drawable = context.getResources().getDrawable(R.drawable.jiaqi);
        } else if(AppConst.WORK_TYPE_NORMAL.equals(resourceType) ){
            drawable = context.getResources().getDrawable(R.drawable.ic_tao);
        } else {
            return null;
        }
        int w = DensityUtils.dp2px(context, GlobalData.isPad() ? 26 : 17);
        drawable.setBounds(0, 0, w, w); //设置边界
        return drawable;

    }

    //统计试卷学生得分
    /*public*/ static float statStudentScore( LocalWorkInfo localWorkInfo ){
        if( localWorkInfo==null || localWorkInfo.getPageList() == null ) return 0;

        float studentScore = 0;
        for( LocalPageInfo pageInfo : localWorkInfo.getPageList() ){
            for( LocalQuestionInfo questionInfo: pageInfo.getQuestions() ){
                studentScore += questionInfo.calStudentScore();
            }
        }
        return studentScore;
    }

}
