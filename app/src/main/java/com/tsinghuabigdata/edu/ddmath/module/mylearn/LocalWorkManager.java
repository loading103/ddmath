package com.tsinghuabigdata.edu.ddmath.module.mylearn;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tsinghuabigdata.edu.ddmath.bean.MyTutorClassInfo;
import com.tsinghuabigdata.edu.ddmath.constant.AppConst;
import com.tsinghuabigdata.edu.ddmath.module.mylearn.bean.UploadImage;
import com.tsinghuabigdata.edu.ddmath.module.mylearn.bean.WaitWorkBean;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.AppLog;
import com.tsinghuabigdata.edu.ddmath.util.ContextUtils;
import com.tsinghuabigdata.edu.ddmath.util.FileUtil;

import org.json.JSONArray;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;

/**
 * 本地作业图片管理器
 */
public class LocalWorkManager {

    private ArrayList<WaitWorkBean> waitWorkList = new ArrayList<>();

    //文件保存机制: 本地磁盘/ddmath/用户名/waitwork.json下面
    //图片保存在：  本地磁盘/ddmath/用户名/image/
    private static final String DATA_PATH = "/username/scwork/classId/waitwork.json";
    private static final String IMAGE_PATH = "/username/scwork/classId/image/";

    private static String mImagePath;
    private static String mDataPath;

    private static String mUserName;


    //单例模式
    private static LocalWorkManager localWorkManager;

    private Context mContext;

    private static String classId;     //保存信息与班级相关，支持多班级处理

    /**
     * 用户本地作业图片管理
     * @param context  上下文
     * @param usrname  用户登录名
     * @return dd
     */
    public static LocalWorkManager getLocalWorkManager( Context context, String usrname ){

        AppLog.i("kjkjkkkk  usrname = " + usrname + ",,,," );
        MyTutorClassInfo currentClassInfo = AccountUtils.getCurrentClassInfo();

        if( TextUtils.isEmpty(usrname) || currentClassInfo == null ) return null;

        if( classId == null ){
            classId = currentClassInfo.getClassId();
        }else if( !classId.equals(currentClassInfo.getClassId()) ){
            classId = currentClassInfo.getClassId();
            localWorkManager = null;
        }
        AppLog.i("kjkjkkkk  classId = " + classId + ",,,," );

        String path = ContextUtils.getExternalCacheDir( context, AppConst.APP_NAME);
        mDataPath = (path + DATA_PATH.replace("username", usrname)).replace("classId",classId);
        mImagePath= (path + IMAGE_PATH.replace("username", usrname)).replace("classId",classId);

        if( localWorkManager == null ){
            localWorkManager = new LocalWorkManager( context );
        }else if( mUserName!=null && !mUserName.equals(usrname) ){
            localWorkManager = new LocalWorkManager( context );     //更换用户
        }
        mUserName = usrname;
        return localWorkManager;
    }

    public static LocalWorkManager getLocalWorkManager(){ return localWorkManager; }

    /**
     * 获得有几组未上传的图片数据
     */
    public int getUnuploadCount(){
        int count = 0;
        for( WaitWorkBean workBean : waitWorkList ){
            if( workBean.getUploadStatus() != WaitWorkBean.ST_UPLOADED && workBean.getImageCount()>0 ){
                count++;
            }
        }
        return count;
    }

    /**
     *
     */
    public ArrayList<WaitWorkBean> getWaitWorkList(){
        return waitWorkList;
    }

    /**
     * 只有一份未上传的对象
     */
    public WaitWorkBean getNotUploadWaitWork(){
        for( WaitWorkBean work : waitWorkList ){
            if( work.getUploadStatus() == WaitWorkBean.ST_UNUPLOAD )
                return work;
        }
        return null;
    }

    /**
     * 添加待上传任务
     */
    public boolean addWaitWorkTask( WaitWorkBean task ){
        if( task == null ) return false;
        for( WaitWorkBean workBean : waitWorkList ){
            if( workBean.getTaskId().equals(task.getTaskId()) )
                return false;
        }
        waitWorkList.add(0, task );
        task.setClassId( classId );
        saveData();
        return true;
    }

    /**
     * 根据taskID,获取指定任务
     */
    public WaitWorkBean getWaitWorkTask( String taskId ){
        if(TextUtils.isEmpty(taskId)) return null;
        for( WaitWorkBean workBean : waitWorkList ){
            if( workBean.getTaskId().equals( taskId) )
                return workBean;
        }
        return null;
    }

    /**
     * 删除已经上传成功的任务对象
     */
    public void removeSuccessBean(){
        for( int index=waitWorkList.size()-1; index>=0; index-- ){
            WaitWorkBean workBean = waitWorkList.get( index );
            if( workBean.getUploadStatus() == WaitWorkBean.ST_UPLOADED ){
                waitWorkList.remove( workBean );
            }
        }
    }
    /**
     * 检查是否有正在编辑中的状态
     * @return true/false
     */
    public boolean checkInEditMode(){
        for( WaitWorkBean workBean : waitWorkList ){
            if( workBean.getUploadStatus() == WaitWorkBean.ST_UPLOADEDIT )
                return true;
        }
        return false;
    }

    /**
     * 检查是否有正在上传中的状态
     * @return true/false
     */
    public boolean checkInUploading(){
        for( WaitWorkBean workBean : waitWorkList ){
            if( workBean.getUploadStatus() == WaitWorkBean.ST_UPLOADING )
                return true;
        }
        return false;
    }

    public void updataUploadingWorkStatus(){
        for( WaitWorkBean workBean : waitWorkList ){
            if( workBean.getUploadStatus() == WaitWorkBean.ST_UPLOADING )
                workBean.markupLastUploadPage();
        }
    }


    public static String getImagePath(){
        if( mImagePath!=null ){
            File file = new File( mImagePath );
            if( !file.exists() ){
                file.mkdirs();
            }
        }
        return mImagePath;
    }

    //----------------------------------------------------------------------
    //隐藏构造函数
    private LocalWorkManager(Context context){
        mContext = context;

        //确保文件路径存在
        FileUtil.createPath( mContext, mImagePath );

        loadData();

        clearImageData();
    }

    //加载本地存储的数据信息
    private void loadData(){

        //先判断文件是否存在
        File file = new File( mDataPath );
        if( !file.exists() || file.length() == 0 )
            return;

        FileInputStream fis = null;
        try {
            fis =  new FileInputStream( file );

            byte buffer[] = new byte[(int)file.length()];
            int len = fis.read( buffer );
            if( len == file.length() ){
                //数据获取正确
                String data = new String( buffer );
                ArrayList<WaitWorkBean> list = new Gson().fromJson( data, new TypeToken<ArrayList<WaitWorkBean>>() {}.getType());
                waitWorkList.addAll( list );
                fis.close();
                fis = null;
            }else{
                //读数据异常，放弃文件
                fis.close();
                fis = null;
                file.delete();
            }
        }catch (Exception e){
            AppLog.i("", e );
        }finally {
            if( fis!=null ){
                try {
                    fis.close();
                }catch (Exception ee ){
                    AppLog.i("", ee );
                }
            }
        }
    }

    //加载本地存储的数据信息
    public boolean saveData(){

        //先判断文件是否存在，存在，删除
        File file = new File( mDataPath );
        if( file.exists() ) file.delete();

        //数组转为字符串
        String data = convertJsonStr();

        FileOutputStream fos = null;
        try {
            fos =  new FileOutputStream( file );

            fos.write( data.getBytes() );
            fos.flush();
            fos.close();

            return true;
        }catch (Exception e){
            AppLog.i("", e );
        }finally {
            if( fos!=null ){
                try {
                    fos.close();
                }catch (Exception ee ){
                    AppLog.i("", ee );
                }
            }
        }
        return false;
    }

    /**
     * list数据转为jsonarray str
     */
    private String convertJsonStr(){
        JSONArray jsonArray = new JSONArray();

        for( WaitWorkBean workBean : waitWorkList ){
            if( workBean.getUploadStatus() != WaitWorkBean.ST_UPLOADED ){
                if( workBean.getUploadStatus() == WaitWorkBean.ST_UNUPLOAD ){
                    if( !workBean.isEmtry() ){
                        jsonArray.put( workBean.getJsonObject() );
                    }
                }else{
                    jsonArray.put( workBean.getJsonObject() );
                }
            }
        }
        return jsonArray.toString();
    }

    /**
     * 清理本地图片
     */
    private void clearImageData(){

        //先获取目录下面的所有图片
        File uploadDir = new File( mImagePath );
        File[] files = uploadDir.listFiles();
        // 如果是空文件夹则删除
        if (files == null || files.length == 0) {
            return;
        }
        ArrayList<String> mList = new ArrayList<>();
        for( File file : files ){
            if( file.isFile() && file.getName().endsWith( AppConst.IMAGE_SUFFIX_NAME ) ){
                mList.add( file.getAbsolutePath() );
            }
        }

        //从列表中去掉还需要的图片
        for( WaitWorkBean workBean : waitWorkList ){
            ArrayList<UploadImage> list = workBean.getImageList();
            for( UploadImage image : list ){
                mList.remove( image.getLocalpath() );
            }
        }

        //删除不需要的图片
        for( String fname : mList ){
            (new File(fname)).delete();
        }

    }

}
