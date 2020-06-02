package com.tsinghuabigdata.edu.ddmath.module.ddwork.bean;

import android.os.AsyncTask;
import android.os.SystemClock;
import android.text.TextUtils;

import com.tsinghuabigdata.edu.ddmath.ZxApplication;
import com.tsinghuabigdata.edu.ddmath.bean.LoginInfo;
import com.tsinghuabigdata.edu.ddmath.bean.MyTutorClassInfo;
import com.tsinghuabigdata.edu.ddmath.bean.UserDetailinfo;
import com.tsinghuabigdata.edu.ddmath.commons.http.AppAsyncTask;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.constant.AppConst;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.DDWorkManager;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.WorkCommitListener;
import com.tsinghuabigdata.edu.ddmath.module.learnmaterial.bean.SelfWorkSubmitBean;
import com.tsinghuabigdata.edu.ddmath.module.neteaseim.base.util.NetworkUtil;
import com.tsinghuabigdata.edu.ddmath.requestHandler.MyLearnService;
import com.tsinghuabigdata.edu.ddmath.requestHandler.requestImpl.LearnMaterialServiceImpl;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.AppLog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 保存在本地的作业信息
 */
public class LocalWorkInfo implements Serializable {

    private static final long serialVersionUID = 6877455798122949600L;

    public final static int WORK_NONE = 0;              //未处理状态，默认
    public final static int WORK_COMMITING = 1;         //提交中
    public final static int WORK_COMMITED = 3;          //提交完成
    public final static int WORK_COMMITFAIL = 2;        //提交失败

    //作业类型
    public final static int LMTYPE_DDCHECK = 3;             //豆豆检查作业
    private final static int LMTYPE_TEACHER_DZ = 1;          //老师布置订制教辅
    private final static int LMTYPE_TEACHER_ORG = 2;         //老师布置原版教辅

    //作业ID
    private String workId;

    //页信息
    private ArrayList<LocalPageInfo> pageList = new ArrayList<>();

    //状态
    private int workStatus = WORK_NONE;

    //是否原版教辅
    private transient int lmType = LMTYPE_TEACHER_DZ;

    public void init(){
        workStatus = WORK_NONE;
        if( pageList!=null ){
            for( LocalPageInfo pageInfo : pageList )
                pageInfo.init();
        }
    }

    //for save
    public JSONObject getJsonObject(){
        JSONObject json = new JSONObject();
        try {
            if( workStatus == WORK_COMMITING || workStatus == WORK_COMMITFAIL ){
                json.put( "workStatus", WORK_COMMITFAIL );
            }else {
                json.put( "workStatus", workStatus );
            }
            json.put( "workId", workId );       //

            JSONArray jsonArray = new JSONArray();
            for( LocalPageInfo pageInfo : pageList ){
               jsonArray.put( pageInfo.getJsonObject() );
            }
            json.put( "pageList", jsonArray );       //
        }catch (Exception e){
            AppLog.i("",e);
        }

        return json;
    }

    //检查是否有重复的题目，如果重复，则只保留第一次出现的
    public boolean ckeckRepeatQuestion(){
        boolean repeat = false;
        String data = "";
        for( LocalPageInfo pageInfo : pageList ){
            ArrayList<LocalQuestionInfo> qlist = pageInfo.getQuestions();
            boolean first = true;
            for( LocalQuestionInfo questionInfo:qlist ){
                if( questionInfo.isSelect() ){
                    if( data.contains( questionInfo.getQuestionId() ) ){
                        if( !first || !data.endsWith(questionInfo.getQuestionId()) ){
                            repeat = true;
                            questionInfo.setSelect( false );
                        }
                    }
                    data += questionInfo.getQuestionId();
                }
                first = false;
            }
        }
        return repeat;
    }

    //检查是否有要上传的题目
    public boolean hasSelectQuestions(){
        for( LocalPageInfo pageInfo : pageList ){
            ArrayList<LocalQuestionInfo> qlist = pageInfo.getQuestions();
            for( LocalQuestionInfo questionInfo:qlist ){
                if( questionInfo.isSelect() ){
                    return true;
                }
            }
        }
        return false;
    }

    //检查要上传页必须有一页数据
    public boolean hasUploadPage(){
        for( LocalPageInfo pageInfo : pageList ){
            if( !TextUtils.isEmpty(pageInfo.getLocalpath()) ){
                ArrayList<LocalQuestionInfo> qlist = pageInfo.getQuestions();
                for( LocalQuestionInfo questionInfo:qlist ){
                    if( questionInfo.isSelect() ){
                        return true;
                    }
                }
            }
        }
        return false;
    }
    //------------------------------------------------------------------------------------
    private JSONArray getUploadData(){
        JSONArray jsonArray = new JSONArray();
        for( LocalPageInfo pageInfo : pageList ){
            JSONObject json = pageInfo.getJson();
            if( json!=null )
                jsonArray.put( json );
        }
        return jsonArray;
    }

    //原版教辅上传
    private JSONArray getLMUploadData(){
        JSONArray jsonArray = new JSONArray();
        for( LocalPageInfo pageInfo : pageList ){
            JSONObject json = pageInfo.getLmJson();
            if( json!=null )
                jsonArray.put( json );
        }
        return jsonArray;
    }

    //----------------------------------------------------------------------------

//    public boolean isLearnMaterial() {
//        return isLearnMaterial;
//    }

    public void setLearnMaterial(int learnMaterial) {
        lmType = learnMaterial;
    }

    public int getWorkStatus() {
        return workStatus;
    }

    public void setWorkStatus(int workStatus) {
        this.workStatus = workStatus;
    }

    public String getWorkId() {
        return workId;
    }

    public void setWorkId(String workId) {
        this.workId = workId;

        for( LocalPageInfo pageInfo : pageList ){
            pageInfo.setWorkId( workId );
        }
    }

    public ArrayList<LocalPageInfo> getPageList() {
        return pageList;
    }

//    public void setPageList(ArrayList<LocalPageInfo> pageList) {
//        this.pageList = pageList;
//    }

    //----------------------------------------------------------------------------
    private transient ArrayList<WorkCommitListener> mCommitListeners;
    private transient SubmitQuestionTask mSubmitQuestionTask;

//    public void setWorkCommitListener(ArrayList<WorkCommitListener> listeners){
//        mCommitListeners = listeners;
//    }

    /**
     * 上传先检查图片是否上传完成，再上传信息
     */
    public void startUploadData(){

        //
        DDWorkManager manager = DDWorkManager.getDDWorkManager();
        if( manager != null ){
            mCommitListeners = manager.getWorkCommitListeners();
        }

        if( mSubmitQuestionTask==null || mSubmitQuestionTask.isCancelled() || mSubmitQuestionTask.isComplete() ){
            mSubmitQuestionTask = new SubmitQuestionTask();
            mSubmitQuestionTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    //
//    public void stopUploadData(){
//        if( mSubmitQuestionTask!=null  ){
//            mSubmitQuestionTask.cancel( true );
//        }
//    }

    private void commitStatusChange( Object obj ){
        if( mCommitListeners!=null ){
            for( WorkCommitListener listener : mCommitListeners ){
                if( obj == null ){
                    listener.workStatus( workId, workStatus );
//                }else if( obj instanceof Boolean ){
//                    listener.workStatus( workId, workStatus );
//                    listener.onSuccess( workId, (Boolean)obj );
                }else if( obj instanceof Exception ){
                    listener.workStatus( workId, workStatus );
                    listener.onFail( workId, (Exception)obj );
                }else if( obj instanceof String && obj.equals("page") ){
                    listener.pageStatus( workId, 0 );
                }else if( obj instanceof WorkSubmitBean ){
                    listener.workStatus( workId, workStatus );
                    listener.onSuccess( workId, (WorkSubmitBean)obj );
                }
            }
        }
    }

    //上传中调用，标记那个是最后一页上传的图片
    public void markupLastUploadPage(){

        AppLog.d("markupLastPage size = " + pageList.size() );
        //首先判断是否已经确定
        for( LocalPageInfo pageInfo : pageList ){
            if( pageInfo.isLastUploadPage() ) return;
        }
        AppLog.d("markupLastPage 00000 " );

        //多页，判断是否仅剩下最后一页，还未上传   这里并不是每一页都需要上传的
        LocalPageInfo tPageInfo=null;
        int count = 0;      //记录未上传的个数
        for( LocalPageInfo pageInfo : pageList ){
            if( !TextUtils.isEmpty(pageInfo.getLocalpath()) && pageInfo.getUploadStatus() != LocalPageInfo.ST_SUCC ){       //没有图片，直接处理
                count++;
                tPageInfo = pageInfo;
            }
        }

        AppLog.d("markupLastPage count = " + count );

        if( count == 0 ) AppLog.d("markupLastPage error.");
        else if(count == 1 ) tPageInfo.setLastUploadPage( true );

        //多个，不标记
    }
    private void clearMarkupLastUploadPage(){
        //首先判断是否已经确定
        for( LocalPageInfo pageInfo : pageList ){
           pageInfo.setLastUploadPage( false );
        }
    }

    //向每一页注册状态变化信息
    private void addWorkCommitListener(){
        for( LocalPageInfo pageInfo : pageList ){
            if( !TextUtils.isEmpty( pageInfo.getLocalpath()) )
                pageInfo.setPageListener( mCommitListeners );
        }
    }

    //全部启动上传
    private void startUpload(){
        for( LocalPageInfo pageInfo : pageList ){
            if( !TextUtils.isEmpty( pageInfo.getLocalpath()) )
                pageInfo.startUpload();
        }
    }

    //是否全部上传成功
    private boolean isUploadSuccess(){
        for( LocalPageInfo pageInfo : pageList ){
            if( !TextUtils.isEmpty( pageInfo.getLocalpath()) )
                if( pageInfo.getUploadStatus() != LocalPageInfo.ST_SUCC ){
                    return false;
                }
        }
        return true;
    }

    //是否上传全部动作完成
    private boolean isFinishUpload(){
        for( LocalPageInfo pageInfo : pageList ){
            if( !TextUtils.isEmpty( pageInfo.getLocalpath()) )
                if( pageInfo.getUploadStatus() == LocalPageInfo.ST_UING || pageInfo.getUploadStatus() == LocalPageInfo.ST_NONE ){
                    return false;
                }
        }
        return true;
    }
    private String getUploadErrorReasons(){
        String reasons = "";
        for( LocalPageInfo pageInfo : pageList ){
            if( !TextUtils.isEmpty( pageInfo.getLocalpath()) ) {
                String reason = pageInfo.getUploadFailReason();
                if (!TextUtils.isEmpty(reason))
                    reasons += reason;
            }
        }
        return reasons;
    }

    //已经拍照的数量
    private int getUploadPageCount(){
        int count = 0;
        for( LocalPageInfo pageInfo : pageList ){
            if( !TextUtils.isEmpty( pageInfo.getLocalpath()) ) {
                count++;
            }
        }
        return count;
    }

    public int getUploadType(){
        int type = AppConst.UPLOAD_TYPE_CAMERA;
        for( LocalPageInfo pageInfo:pageList ){
            type = pageInfo.getUploadType();
            break;
        }
        return type;
    }

    private class SubmitQuestionTask extends AppAsyncTask<String, Void, WorkSubmitBean> {

        SubmitQuestionTask(){
            workStatus = WORK_COMMITING;
            commitStatusChange( null );
        }
        @Override
        protected WorkSubmitBean doExecute(String... params) throws Exception {

            //网络检测
            if( !NetworkUtil.isNetAvailable( ZxApplication.getApplication() ) ){
                throw new Exception("没有网络");
            }

            LoginInfo loginInfo = AccountUtils.getLoginUser();
            UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
            if( loginInfo == null || detailinfo == null ){
                throw new Exception("请登录");
            }

            if( !AccountUtils.hasClass() ){
                throw new Exception("你还没有班级信息。请联系老师，加入班级。" );
            }

            MyLearnService service = ZxApplication.getLearnService();
            if( service==null ){
                throw new Exception("没有启动 LearnService");
            }

            String accessToken = loginInfo.getAccessToken();
            String studentId   = detailinfo.getStudentId();
            MyTutorClassInfo currentClassInfo = AccountUtils.getCurrentClassInfo();
            String classId = "";
            if( currentClassInfo!=null )
                classId = currentClassInfo.getClassId();
            int uploadType = getUploadType();

            //0, 先注册监听事件
            addWorkCommitListener();
            markupLastUploadPage();

            //1，启动图片上传
            startUpload();

            //2，检测图片是否上传完成
            int index = 0;
            int count = pageList.size() * 1000+100;       //超时最大100s，一个最多需要检测1000次
            while( true ){
                SystemClock.sleep(100);
                index++;

                //先判断是否都上传成功
                if( isUploadSuccess() )
                    break;
                //失败或者成功，上传动作完成
                if( isFinishUpload() ){
                    throw new Exception( getUploadErrorReasons() );
                }

                //超过时间，失败提示 300s
                if( index > count ){
                    throw new Exception("网络异常，超时");
                }
            }

            //3，上传作业信息

            //原版教辅上传
            if( LMTYPE_DDCHECK == lmType ){
                JSONArray jsonArray = getLMUploadData();
                int pageCount = getPageList().size();
                SelfWorkSubmitBean bean = new LearnMaterialServiceImpl().createAndUploadSelfWork(studentId,classId, pageCount, uploadType, jsonArray);
                if( bean!=null ){
                    WorkSubmitBean workBean = new WorkSubmitBean();
                    workBean.setRecordId( bean.getExamId() );
                    workBean.setRank( bean.getRank() );
                    workBean.setValue( bean.getValue() );
                    return workBean;
                }
            }

            //老师布置的原版教辅作业
            else if( LMTYPE_TEACHER_ORG == lmType ){
                JSONArray jsonArray = getLMUploadData();
                WorkSubmitBean workBean = service.submitDDWorkInfo( accessToken, studentId, classId, getWorkId(), getUploadPageCount(), jsonArray, 2, uploadType );
                if( workBean!=null && !TextUtils.isEmpty( workBean.getRecordId() ) ) {
                    return workBean;
                }
            }
            //老师布置的定制教辅  or 套题作业
            else{
                JSONArray jsonArray = getUploadData();
                WorkSubmitBean workBean = service.submitDDWorkInfo( accessToken, studentId, classId, getWorkId(), getUploadPageCount(), jsonArray, -1, uploadType );
                if( workBean!=null && !TextUtils.isEmpty( workBean.getRecordId() ) ) {
                    return workBean;
                }
            }
            return null;
        }

        @Override
        protected void onResult(WorkSubmitBean workSubmitBean) {

            workStatus = workSubmitBean!=null?WORK_COMMITED:WORK_COMMITFAIL;
            if( workSubmitBean!=null ){
                clearMarkupLastUploadPage();
                commitStatusChange("page");
            }
            commitStatusChange( workSubmitBean );

            DDWorkManager workManager = DDWorkManager.getDDWorkManager();
            if( workManager == null ) return;
            workManager.saveData();

        }

        @Override
        protected void onFailure(HttpResponse<WorkSubmitBean> response, Exception ex) {

            workStatus = WORK_COMMITFAIL;
            if( !NetworkUtil.isNetAvailable( ZxApplication.getApplication() ) ){
                ex = new Exception("没有网络");
            }else if( !TextUtils.isEmpty(response.getInform()) ){
                ex = new Exception(response.getInform());
            }
            commitStatusChange("page");
            commitStatusChange( ex );

            DDWorkManager workManager = DDWorkManager.getDDWorkManager();
            if( workManager == null ) return;
            workManager.saveData();
        }
    }
}
