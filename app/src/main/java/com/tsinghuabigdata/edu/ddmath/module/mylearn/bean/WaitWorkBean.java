package com.tsinghuabigdata.edu.ddmath.module.mylearn.bean;

import android.text.TextUtils;

import com.tsinghuabigdata.edu.ddmath.constant.AppConst;
import com.tsinghuabigdata.edu.ddmath.module.mylearn.UploadImageListener;
import com.tsinghuabigdata.edu.ddmath.util.AppLog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 我的作业 一份作业的数据消息
 */
public class WaitWorkBean implements Serializable {

    private static final long serialVersionUID = -5953682520073361506L;

    //此份作业
    public static final int ST_UNUPLOAD = 0;   //:未上传
    public static final int ST_UPLOADING = 1;   //:上传中
    public static final int ST_UPLOADED = 2;   //:上传成功
    public static final int ST_UPLOADFAIL = 3;   //:上传失败
    public static final int ST_UPLOADEDIT = 4;   //:编辑状态

    private int uploadStatus;   //上传状态
    private long uploadTime;    //上传时间

    private int estimateCount;    //预估题数

    private ArrayList<UploadImage> imageList = new ArrayList<>();

    private String taskId;      //任务ID
    private String classId;

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public WaitWorkBean(){
        taskId = "task"+System.currentTimeMillis();
    }

    public JSONObject getJsonObject(){

        JSONObject json = new JSONObject();
        try {
            if( uploadStatus == WaitWorkBean.ST_UPLOADEDIT || uploadStatus == WaitWorkBean.ST_UNUPLOAD ){
                json.put( "uploadStatus", WaitWorkBean.ST_UNUPLOAD );
            }else if( uploadStatus == WaitWorkBean.ST_UPLOADING || uploadStatus == WaitWorkBean.ST_UPLOADFAIL ){
                json.put( "uploadStatus", WaitWorkBean.ST_UPLOADFAIL );
            }else {
                json.put( "uploadStatus", uploadStatus );
            }
            json.put( "uploadTime", uploadTime );       //
            json.put( "estimateCount", estimateCount );       //
            json.put( "taskId", taskId );

            JSONArray jsonArray = new JSONArray();
            AppLog.d("dfddfd save size="+imageList.size());
            for( UploadImage image : imageList ){
                if( image.getImagetype() == UploadImage.TYPE_IMAGE )
                    jsonArray.put( image.getJsonObject() );
                else
                    AppLog.d("dfddfd save add image");
            }
            json.put( "imageList", jsonArray );       //
            json.put( "classId", classId );
        }catch (Exception e){
            AppLog.i( "", e );
        }

        return json;
    }

    public int getUploadStatus() {
        return uploadStatus;
    }

    public void setUploadStatus(int uploadStatus) {
        this.uploadStatus = uploadStatus;
        //状态恢复
        if( uploadStatus == ST_UPLOADING ){
            for( UploadImage image : imageList ){
                if( image.getImagetype() == UploadImage.TYPE_IMAGE && image.getUploadStatus() != UploadImage.ST_SUCC ){
                    image.setUploadStatus( UploadImage.ST_NONE );
                }
            }
        }
    }

    public long getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(long uploadTime) {
        this.uploadTime = uploadTime;
    }

    public int getEstimateCount() {
        return estimateCount;
    }

    public void setEstimateCount(int estimateCount) {
        this.estimateCount = estimateCount;
    }

    public ArrayList<UploadImage> getImageList() {
        return imageList;
    }

    public void setImageList(ArrayList<UploadImage> imageList) {
        this.imageList = imageList;
    }

    public boolean addUploadImage( String imagePath ) {

        //
        if( TextUtils.isEmpty(imagePath) ){     //添加图片 ICON
            return false;
        }

        UploadImage uploadImage = new UploadImage( null, imagePath );

        //先判断最后一个是不是 add ICON
        if( imageList.size() > 0 ){
            int index = imageList.size()-1;
            UploadImage last = imageList.get( index );
            if( last.getImagetype() == UploadImage.TYPE_ADDIMAGE ){
                imageList.add( index, uploadImage );
            }else{
                imageList.add( uploadImage );
            }
        }else{
            imageList.add( uploadImage );
        }
        return true;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public int getImageCount(){
        int count = 0;
        for( UploadImage image : imageList ){
            if( !image.isDelfalg() && image.getImagetype() == UploadImage.TYPE_IMAGE ) count++;
        }
        return count;
    }

    public boolean hasImagePath(String path ){
        for( UploadImage image : imageList ){
            if( image.getImagetype() == UploadImage.TYPE_IMAGE && image.getLocalpath().equals(path) ) return true;
        }
        return false;
    }

    public boolean canUpload(){
        for( UploadImage image : imageList ){
            if( image.getImagetype() == UploadImage.TYPE_IMAGE ){
                if( image.getUploadStatus() != UploadImage.ST_SUCC || TextUtils.isEmpty(image.getUrl()) ){
                    //image.startUpload();
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isFinishUpload(){
        for( UploadImage image : imageList ){
            if( image.getImagetype() == UploadImage.TYPE_IMAGE ){
                if( image.getUploadStatus() == UploadImage.ST_UING || image.getUploadStatus() == UploadImage.ST_NONE ){
                    return false;
                }
            }
        }
        return true;
    }

    public JSONArray getUploadImageJsonArray(){
        JSONArray jsonArray = new JSONArray();

        for( UploadImage image : imageList ){
            if( image.getImagetype() == UploadImage.TYPE_IMAGE ){
                jsonArray.put(image.getUploadJsonObject() );
            }
        }
        return jsonArray;
    }

    //上传图片
    public void startUpload(){
        for( UploadImage image : imageList ){
            if( image.getImagetype() == UploadImage.TYPE_IMAGE ){
                image.startUpload();
            }
        }
    }

    public void removeAddTypeImage(){
        for( int i=imageList.size()-1; i>=0; i-- ){
            UploadImage image = imageList.get(i);
            if( image.getImagetype() == UploadImage.TYPE_ADDIMAGE ){
                imageList.remove( image );
            }
        }
    }

    public boolean addAddTypeImage(){

        if( uploadStatus != ST_UNUPLOAD )
            return false;

        if( imageList.size() >= AppConst.MAX_WORK_IMAGE ){
            return false;
        }

        //删除已存在的 add ICON
        for( int i=imageList.size()-1; i>=0; i-- ){
            UploadImage image = imageList.get(i);
            if( image.getImagetype() == UploadImage.TYPE_ADDIMAGE ){
                imageList.remove( image );
                break;
            }
        }
        //新添加到最后
        imageList.add( new UploadImage(UploadImage.TYPE_ADDIMAGE) );
        return true;
    }

    //
    public void setEditMode( boolean editMode ){
        for( UploadImage image : imageList ){
            if( image.getImagetype() == UploadImage.TYPE_IMAGE ){
                image.setEditMode( editMode );
            }
        }
    }

    public ArrayList<String> getImagesPath(){
        ArrayList<String> arrayList = new ArrayList<String>();
        for( UploadImage image : imageList ){
            if( image.getImagetype() == UploadImage.TYPE_IMAGE ){
                arrayList.add(image.getLocalpath());
            }
        }
        return arrayList;
    }

    public void addUploadListener(UploadImageListener listener){
        for( UploadImage image : imageList ){
            if( image.getImagetype() == UploadImage.TYPE_IMAGE ){
                image.setUploadImageListener( listener );
            }
        }
    }
    public void removeUploadListener(){
        for( UploadImage image : imageList ){
            if( image.getImagetype() == UploadImage.TYPE_IMAGE ){
                image.setUploadImageListener( null );
            }
        }
    }

    public boolean isEmtry(){
        for( UploadImage image : imageList ){
            if( image.getImagetype() == UploadImage.TYPE_IMAGE ){
                return false;
            }
        }
        return true;
    }

    //上传中调用，标记那个是最后一页上传的图片
    public void markupLastUploadPage(){

        AppLog.d("markupLastPage size = " + imageList.size() );
        //首先判断是否已经确定
        for( UploadImage image : imageList ){
            if( image.isLastUploadPage() ) return;
        }
        AppLog.d("markupLastPage 00000 " );

        //多页，判断是否仅剩下最后一页，还未上传   这里并不是每一页都需要上传的
        UploadImage tImage = null;
        int count = 0;      //记录未上传的个数
        for( UploadImage image : imageList ){
            if( !TextUtils.isEmpty(image.getLocalpath()) && image.getUploadStatus() != UploadImage.ST_SUCC ){       //没有图片，直接处理
                count++;
                tImage = image;
            }
        }

        AppLog.d("markupLastPage count = " + count );

        if( count == 0 ){
            count = imageList.size();
            if( count>0 ) imageList.get(count-1).setLastUploadPage( true );
        } else if(count == 1 ) tImage.setLastUploadPage( true );

        //多个，不标记
    }
    public void clearMarkupLastUploadPage(){
        //首先判断是否已经确定
        for( UploadImage image : imageList  ){
            image.setLastUploadPage( false );
        }
    }
}
