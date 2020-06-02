package com.tsinghuabigdata.edu.ddmath.module.entrance;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tsinghuabigdata.edu.ddmath.constant.AppConst;
import com.tsinghuabigdata.edu.ddmath.module.mylearn.UploadImageListener;
import com.tsinghuabigdata.edu.ddmath.module.mylearn.bean.UploadImage;
import com.tsinghuabigdata.edu.ddmath.util.AppLog;
import com.tsinghuabigdata.edu.ddmath.util.ContextUtils;
import com.tsinghuabigdata.edu.ddmath.util.FileUtil;
import com.tsinghuabigdata.edu.utils.MD5Util;

import org.json.JSONArray;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;

/**
 * 入学评测 本地作业图片管理器
 */
public class LocalImageManager {

    //图片信息列表
    private ArrayList<UploadImage> waitWorkList = new ArrayList<>();

    //文件保存机制: 本地磁盘/ddmath/用户名/entrance/waitwork.json下面
    //图片保存在：  本地磁盘/ddmath/用户名/entrance/image/
    private static final String DATA_PATH = "/username/entrance/waitwork.json";
    private static final String IMAGE_PATH = "/username/entrance/image/";

    private static String mImagePath;
    private static String mDataPath;

    private static String mUserName;
    private Context mContext;

    //单例模式
    private static LocalImageManager localImageManager;

    /**
     * 用户本地评测图片管理
     * @param usrname  用户登录名
     * @return dd
     */
    public static LocalImageManager getLocalImageManager(Context context, String usrname ){

        if( TextUtils.isEmpty(usrname) ) return null;

        String path = ContextUtils.getExternalCacheDir( context, AppConst.APP_NAME);
        mDataPath = path + DATA_PATH.replace("username", usrname);
        mImagePath= path + IMAGE_PATH.replace("username", usrname);

        if( localImageManager == null ){
            localImageManager = new LocalImageManager(context);
        }else if( mUserName!=null && !mUserName.equals(usrname) ){
            localImageManager = new LocalImageManager(context);     //更换用户
        }
        mUserName = usrname;

        //开始预上传
        localImageManager.startUpload();
        return localImageManager;
    }

    public static LocalImageManager getLocalImageManager(){ return localImageManager; }

    /**
     *
     */
    public static String getImagePath(){
        if( mImagePath!=null ){
            File file = new File( mImagePath );
            if( !file.exists() ){
                file.mkdirs();
            }
        }
        return mImagePath;
    }

    /**
     * 图片总量(待上传)
     */
    public int getImageCount(){
        int count = waitWorkList.size();
        for( UploadImage image : waitWorkList ){
            if( image.getUploadStatus() == UploadImage.ST_SUCC)
                count--;
        }
        return count;
    }

    public int getLocalImageCount(){
        int count = waitWorkList.size();
        for( UploadImage image : waitWorkList ){
            if( TextUtils.isEmpty(image.getLocalpath()) )
                count--;
        }
        return count;
    }

    public String getUrlsMd5(){
        StringBuilder sb = new StringBuilder();
        for( UploadImage image : waitWorkList ){
            if( !TextUtils.isEmpty(image.getUrl()) )
                sb.append(image.getUrl());
        }
        return sb.length()==0?"":MD5Util.getMD5String(sb.toString());
    }

    public ArrayList<UploadImage> getImageList(){
        return waitWorkList;
    }

    /**
     * 添加待上传图片
     */
    public boolean addUploadImage( UploadImage image ){
        if( image == null ) return false;
//        for( WaitWorkBean workBean : waitWorkList ){
//            if( workBean.getTaskId().equals(task.getTaskId()) )
//                return false;
//        }
        waitWorkList.add( image );
        image.startUpload();        //预上传

        //删除已存在的 add ICON
        boolean hasAddType = false;
        for( int i=waitWorkList.size()-1; i>=0; i-- ){
            image = waitWorkList.get(i);
            if( image.getImagetype() == UploadImage.TYPE_ADDIMAGE ){
                waitWorkList.remove( image );
                hasAddType = true;
                break;
            }
        }
        if( hasAddType && waitWorkList.size() < AppConst.MAX_ENTRANCE_IMAGE ){
            waitWorkList.add( new UploadImage(UploadImage.TYPE_ADDIMAGE) );
        }
        saveData();
        return true;
    }

    public void addAddTypeImage(){
        //删除已存在的 add ICON
        for( int i=waitWorkList.size()-1; i>=0; i-- ){
            UploadImage image = waitWorkList.get(i);
            if( image.getImagetype() == UploadImage.TYPE_ADDIMAGE ){
                waitWorkList.remove( image );
                break;
            }
        }
        //新添加到最后
        waitWorkList.add( new UploadImage(UploadImage.TYPE_ADDIMAGE) );
    }

    public void removeAddTypeImage(){
        for( int i=waitWorkList.size()-1; i>=0; i-- ){
            UploadImage image = waitWorkList.get(i);
            if( image.getImagetype() == UploadImage.TYPE_ADDIMAGE ){
                waitWorkList.remove( image );
            }
        }
    }

    //
    public void setEditMode( boolean editMode ){
        for( UploadImage image : waitWorkList ){
            if( image.getImagetype() == UploadImage.TYPE_IMAGE ){
                image.setEditMode( editMode );
            }
        }
    }

    /**
     * 获取上传数据
     */
    public ArrayList<String> getUploadData(){
        ArrayList<String> list = new ArrayList<>();
        for( UploadImage image : waitWorkList ){
            list.add( image.getUrl() );
        }
        return list;
    }

    /**
     *  是否都已经成功上传
     */
    public boolean uploadSuccess(){
        for( UploadImage image : waitWorkList ){
            if( image.getUploadStatus() != UploadImage.ST_SUCC)
                return false;
        }
        return true;
    }

    /**
     * 是否都已经上传完成(包含上传失败的情况)
     */
    public boolean isFinishUpload(){
        for( UploadImage image : waitWorkList ){
            if( image.getImagetype() == UploadImage.TYPE_IMAGE ){
                if( image.getUploadStatus() == UploadImage.ST_UING || image.getUploadStatus() == UploadImage.ST_NONE ){
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 添加监听接口
     * @param listener
     */
    public void addUploadListener( UploadImageListener listener ){
        for( UploadImage image : waitWorkList ){
            image.setUploadImageListener( listener );
        }
    }

    /**
     * 开始上传
     */
    public void startUpload(){
        for( UploadImage image : waitWorkList ){
            if( image.getUploadStatus() != UploadImage.ST_SUCC ){
                if( UploadImage.ST_ERROR == image.getUploadStatus() ){
                    image.setUploadStatus( UploadImage.ST_NONE );
                }
                image.startUpload();
            }
        }
    }

    /**
     * 触发状态变化
     */
    public void noticeStatusChange(){
        for( UploadImage image : waitWorkList ){
            image.callbackListener( image.getUploadStatus() );
        }
    }

    /**
     * 上传成功后 清理本地数据
     */
    public void clearData(){

        //先删除文件
        FileUtil.delFile( mImagePath.replace("image/","") );
        FileUtil.delFile( DATA_PATH );

        //确保文件路径存在
        File file = new File(mImagePath);
        if( !file.exists() ) file.mkdirs();

        waitWorkList.clear();
        //
        //localImageManager = null;
    }

    //上传中调用，标记那个是最后一页上传的图片
    public void markupLastUploadPage(){

        AppLog.d("markupLastPage size = " + waitWorkList.size() );
        //首先判断是否已经确定
        for( UploadImage image : waitWorkList ){
            if( image.isLastUploadPage() ) return;
        }
        AppLog.d("markupLastPage 00000 " );

        //多页，判断是否仅剩下最后一页，还未上传   这里并不是每一页都需要上传的
        UploadImage tImage = null;
        int count = 0;      //记录未上传的个数
        for( UploadImage image : waitWorkList ){
            if( !TextUtils.isEmpty(image.getLocalpath()) && image.getUploadStatus() != UploadImage.ST_SUCC ){       //没有图片，直接处理
                count++;
                tImage = image;
            }
        }

        AppLog.d("markupLastPage count = " + count );

        if( count == 0 ){
            count = waitWorkList.size();
            if( count>0 ) waitWorkList.get(count-1).setLastUploadPage( true );
        } else if(count == 1 ) tImage.setLastUploadPage( true );

        //多个，不标记
    }
    public void clearMarkupLastUploadPage(){
        //首先判断是否已经确定
        for( UploadImage image : waitWorkList  ){
            image.setLastUploadPage( false );
        }
    }
    //----------------------------------------------------------------------
    //隐藏构造函数
    private LocalImageManager(Context context){

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
                ArrayList<UploadImage> list = new Gson().fromJson( data, new TypeToken<ArrayList<UploadImage>>() {}.getType());
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
        if( TextUtils.isEmpty(data) )
            return true;

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

        for( UploadImage image : waitWorkList ){
            if( image.getImagetype() == UploadImage.TYPE_IMAGE )
                jsonArray.put( image.getJsonObject() );
        }
        return jsonArray.toString();
    }

    /**
     * 清理多余本地图片
     */
    private void clearImageData(){

        //先获取目录下面的所有图片
        File uploadDir = new File( mImagePath );
        File[] files = uploadDir.listFiles();
        // 如果是空文件夹则删除
        if (files == null || files.length == 0) {
            return;
        }

        //所有图片路径
        ArrayList<String> mList = new ArrayList<>();
        for( File file : files ){
            if( file.isFile() && file.getName().endsWith( AppConst.IMAGE_SUFFIX_NAME ) ){
                mList.add( file.getAbsolutePath() );
            }
        }

        //从列表中去掉还需要的图片
        for( UploadImage image : waitWorkList ){
            mList.remove( image.getLocalpath() );
        }

        //删除不需要的图片
        for( String fname : mList ){
            (new File(fname)).delete();
        }
    }


}
