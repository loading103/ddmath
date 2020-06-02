package com.tsinghuabigdata.edu.ddmath.module.ddwork.bean;

import android.os.SystemClock;
import android.text.TextUtils;

import com.tsinghuabigdata.edu.ddmath.ZxApplication;
import com.tsinghuabigdata.edu.ddmath.bean.LoginInfo;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.WorkCommitListener;
import com.tsinghuabigdata.edu.ddmath.opencv.OpenCVHelper;
import com.tsinghuabigdata.edu.ddmath.requestHandler.MyLearnService;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.AppLog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * 保存在本地的作业页信息
 */
public class LocalPageInfo extends WorkPageInfo implements Serializable {

    private static final long serialVersionUID = 4705837390463621118L;


    public static final int ST_NONE  = 0;
    /*public*/ static final int ST_UING  = 1;
    public static final int ST_ERROR = 2;
    public static final int ST_SUCC  = 3;

    //上传的状态
    private int uploadStatus = ST_NONE;       //， 0：没有上传， 1：上传中 2:上传错误 3：上传成功

    //作业图片
    private String localpath;       //图片保存在本地的路径 彩色图
    private String grayLocalPath;   //灰度图

    //上传后的url
    private String url="";

    private String fileId;

    //作业ID
    private String workId;

    private transient boolean selected;

    private transient String uploadFailReason;

    //关联的图片是否最后一个上传
    private transient boolean bLastUploadPage = false;

    //作业上传类型, 自己赋值
    private int uploadType;

    //拍照时四个点是否移动过
    private boolean moveFlag = false;

    private ArrayList<WorkCommitListener> mListeners;

    public LocalPageInfo( String workId ){
        this.workId = workId;
        fileId = System.currentTimeMillis()+"";
    }

    public void init(){
        super.init();

        //上传的状态
        uploadStatus = ST_NONE;

        //图片保存在本地的路径
        localpath = "";
        grayLocalPath = "";

        //上传后的url
        url = "";

        uploadFailReason = "";
        moveFlag = false;
    }

    //for Localsave use
    public JSONObject getJsonObject(){
        JSONObject json = new JSONObject();
        try {

            if( uploadStatus == ST_SUCC ){
                json.put( "uploadStatus", ST_SUCC );
            }else{
                json.put( "uploadStatus", ST_NONE );
            }

            json.put( "localpath", localpath );
            json.put( "grayLocalPath", grayLocalPath );
            json.put( "fileId", fileId );
            json.put( "workId", workId );
            json.put( "url", url );
            json.put( "pageNum", pageNum );
            json.put( "bLastUploadPage",bLastUploadPage);
            json.put( "title", title);
            json.put( "chapterName", chapterName);
            json.put( "uploadType", uploadType);
            json.put( "moveFlag", moveFlag );

            JSONArray jsonArray = new JSONArray();
            for( LocalQuestionInfo questionInfo : questions ){
                jsonArray.put( questionInfo.getJsonObject() );
            }
            json.put( "questions", jsonArray );

        }catch (Exception e){
            AppLog.i("",e);
        }
        return json;
    }

    //for 上传使用
    public JSONObject getJson(){

        if( TextUtils.isEmpty(url) )
            return null;

        JSONObject json = new JSONObject();
        try {
            json.put( "path", url );
            json.put( "page", pageNum );
            json.put( "moveFlag", moveFlag );

            JSONArray jsonArray = new JSONArray();
            for( LocalQuestionInfo questionInfo : questions ){
                jsonArray.put( questionInfo.getJson() );
            }
            json.put( "cutInfos", jsonArray );

        }catch (Exception e){
            AppLog.i("",e);
        }
        return json;
    }

    //原版教辅上传 使用
    JSONObject getLmJson(){

        JSONObject json = new JSONObject();
        try {
            json.put( "path", url );
            json.put( "pageNum", pageNum );
            json.put( "moveFlag", moveFlag );

            JSONArray jsonArray = new JSONArray();
            for( LocalQuestionInfo questionInfo : questions ){
                if( !questionInfo.isSelect() ) continue;
                jsonArray.put( questionInfo.getLmJson() );
            }

            if( jsonArray.length() == 0 ) return null;      //此页没有题目
            json.put( "newCutInfos", jsonArray );

            //该页题目列表
            jsonArray = new JSONArray();
            for( LocalQuestionInfo questionInfo : questions ){
                if( !questionInfo.isSelect() ) continue;
                jsonArray.put( questionInfo.getLmQuestionJson() );
            }
            json.put( "questions", jsonArray );

        }catch (Exception e){
            AppLog.i("",e);
        }
        return json;
    }

    void setPageListener( ArrayList<WorkCommitListener> listeners ){
        mListeners = listeners;
    }

    public void copy( WorkPageInfo pageInfo ){
        pageNum = pageInfo.getPageNum();
        submitStatus = pageInfo.getSubmitStatus();
        title = pageInfo.getLearnMaterialName();
        chapterName = pageInfo.getChapterName();

        if( questions == null ){
            questions = new ArrayList<>();

            if( pageInfo.questions!=null )
            for( QuestionInfo questionInfo : pageInfo.questions ){
                LocalQuestionInfo localQuestionInfo = new LocalQuestionInfo();
                localQuestionInfo.copy( questionInfo );
                questions.add( localQuestionInfo );
            }
        }else if( pageInfo.questions!=null ){
            int min_count = questions.size()<pageInfo.questions.size() ? questions.size() : pageInfo.questions.size();
            for( int i=0; i<min_count; i++ ){
                questions.get(i).copy( pageInfo.questions.get(i) );
            }
        }
    }

    //拍照过程中使用，保证拍照中产生的临时数据不影响基本数据
    private boolean moveFlagCache = false;
    public void setMoveFlagCache(){
        moveFlagCache = true;
    }
    public void initCacheData(){
        //moveFlagCache = moveFlag;     //使用默认值

        ArrayList<LocalQuestionInfo> list = getQuestions();
        for( LocalQuestionInfo questionInfo : list ){
            questionInfo.initCacheData();
        }
    }

    //批阅结果 拍照时 默认题目是对的
    public void setDeflautCorrectCache(){
        ArrayList<LocalQuestionInfo> list = getQuestions();
        for( LocalQuestionInfo questionInfo : list ){
            questionInfo.setDeflautCorrectCache();
        }
    }
    public void useCacheData(){
        moveFlag = moveFlagCache;
        ArrayList<LocalQuestionInfo> list = getQuestions();
        for( LocalQuestionInfo questionInfo : list ){
            questionInfo.useCacheData();
        }
    }

    String getUploadFailReason(){
        return uploadFailReason;
    }

    public void setWorkId(String workId) {
        this.workId = workId;
    }
//-----------------------------------------------------------------------------------------
//    public ArrayList<LocalQuestionInfo> getQuestionInfos() {
//        return questionInfos;
//    }
//
//    public void setQuestionInfos(ArrayList<LocalQuestionInfo> questionInfos) {
//        this.questionInfos = questionInfos;
//    }

//    public String getFileId() {
//        return fileId;
//    }
//
//    public void setFileId(String fileId) {
//        this.fileId = fileId;
//    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLocalpath() {
        return localpath;
    }

    public void setLocalpath(String localpath) {
        this.localpath = localpath;
        setGrayLocalPath();
    }
    private void setGrayLocalPath(){
        String dstpath = "";
        if( localpath.endsWith(".png") ) dstpath = localpath.replace(".png","-single.png");
        if( localpath.endsWith(".jpg") ) dstpath = localpath.replace(".jpg","-single.jpg");

        if( TextUtils.isEmpty(dstpath) ) return;

        grayLocalPath = OpenCVHelper.toSingleChannelGray( localpath, dstpath);

        AppLog.d("dfsdadsf localpath = " + localpath );
        AppLog.d("dfsdadsf dstpath = " + dstpath );
        AppLog.d("dfsdadsf grayLocalPath = " + grayLocalPath );

        //预上传
        if( !TextUtils.isEmpty(grayLocalPath) && new File(grayLocalPath).exists() ){
            if( ST_NONE == uploadStatus || ST_ERROR == uploadStatus ){      //未上传 或者上传错误
                startUpload();
                //AppLog.d("setLocalpath startUpload st="+(uploadStatus==ST_NONE?"none":"err"));
            }else if( ST_SUCC == uploadStatus ){    //已上传成功，初始化状态
                url = "";
                uploadStatus = ST_NONE;
                startUpload();
                //AppLog.d("setLocalpath startUpload st=succ");
            }else if( ST_UING == uploadStatus ){    //正在上传中，等待完成后重新
                //触发保存？？？
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //等待完成
                        while( ST_UING == uploadStatus ) SystemClock.sleep(100);

                        //初始化，在重新上传
                        url = "";
                        uploadStatus = ST_NONE;
                        startUpload();
                        //AppLog.d("setLocalpath startUpload st=ing");
                    }
                }).start();
            }
        }else if( TextUtils.isEmpty(grayLocalPath) ){
            AppLog.i(" LocalPageInfo setLocalpath grayLocalPath = " + grayLocalPath + ",,, localpath = " + localpath );
        }
    }

//    public int getIndex() {
//        return index;
//    }
//
//    public void setIndex(int index) {
//        this.index = index;
//    }

    public int getUploadStatus() {
        return uploadStatus;
    }

    public int getUploadStatusNew() {
        if( ST_SUCC == uploadStatus && bLastUploadPage ){
            return ST_UING;
        }else
            return uploadStatus;
    }
    public void setUploadStatus(int uploadStatus) {
        this.uploadStatus = uploadStatus;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    /*public*/ boolean isLastUploadPage() {
        return bLastUploadPage;
    }

    /*public*/ void setLastUploadPage(boolean bLastUploadPage) {
        this.bLastUploadPage = bLastUploadPage;
    }

//    public boolean isMoveFlag() {
//        return moveFlag;
//    }
//
//    public void setMoveFlag(boolean moveFlag) {
//        this.moveFlag = moveFlag;
//    }

    //--------------------------------------------------------------------------------------
    /*public*/ void startUpload(){
        for( LocalPageInfo uploadImage : uploadlist ){
            if( uploadImage.equals( this ) ){
                return;
            }
        }
        callbackListener( uploadStatus );
        uploadlist.add( this );
        startUploadThread();
    }

    private void callbackListener( int status ){
        if( mListeners!=null ){
            for( WorkCommitListener listener : mListeners ){
                listener.pageStatus( workId, status );
            }
        }
    }
    private void upload(){

        if( uploadStatus == ST_UING || uploadStatus == ST_SUCC )
            return;

        uploadFailReason = "";
        uploadStatus = ST_UING;
        callbackListener( uploadStatus );

        LoginInfo loginInfo = AccountUtils.getLoginUser();
        if( loginInfo == null ){
            uploadStatus = ST_ERROR;
            callbackListener( uploadStatus );
            return;
        }

        MyLearnService learnService = ZxApplication.getLearnService();
        if( learnService == null ){
            uploadStatus = ST_ERROR;
            callbackListener( uploadStatus );
            return;
        }

        if( TextUtils.isEmpty(grayLocalPath) ){
            uploadStatus = ST_ERROR;
            callbackListener( uploadStatus );
            return;
        }

        FileInputStream fis = null;
        try{
            File file = new File( grayLocalPath );
            fis = new FileInputStream( file );
        }catch (Exception e){
            AppLog.i("",e);
        }
        if( fis == null ){
            uploadStatus = ST_ERROR;
            callbackListener( uploadStatus );
            return;
        }

        try{
            String path = learnService.uploadImage( fileId, loginInfo.getAccessToken(), fis, grayLocalPath );
            if( !TextUtils.isEmpty(path) ){
                url = path;
                uploadStatus = ST_SUCC;
                callbackListener( uploadStatus );
                fis.close();
                return;
            }
        }catch (Exception e){
            AppLog.i("",e);
            uploadFailReason = e.getMessage();
        }
        //
        try {
            fis.close();
        }catch (Exception e){
            AppLog.i("",e);
        }
        uploadStatus = ST_ERROR;
        callbackListener( uploadStatus );
    }

    //------------------------------------------------------------------
    //由于不能同时上传，现在改为依次上传
    private static ArrayList<LocalPageInfo> uploadlist = new ArrayList<>();

    private static final int THREAD_IDLE = 0;
    private static final int THREAD_RUN = 1;

    private static int run_status = THREAD_IDLE;

//    public static void stopUploadThread(){
//        run_status = THREAD_IDLE;
//    }
    private static void startUploadThread(){
        if( run_status == THREAD_RUN )
            return;
        run_status = THREAD_RUN;

        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    if( uploadlist.size() == 0 || run_status == THREAD_IDLE)
                        break;

                    //AppLog.d("pppppp uploadlist size = " + uploadlist.size() );
//                    long time = System.currentTimeMillis();
                    LocalPageInfo pageInfo = uploadlist.get(0);
                    if( pageInfo != null )
                        pageInfo.upload();
                    uploadlist.remove( pageInfo );
                    //AppLog.d("pppppp upload time = " + ( System.currentTimeMillis() - time ) );
                }
                run_status = THREAD_IDLE;
            }
        }).start();
    }

    public int getUploadType() {
        return uploadType;
    }

    public void setUploadType(int uploadType) {
        this.uploadType = uploadType;
    }
}
