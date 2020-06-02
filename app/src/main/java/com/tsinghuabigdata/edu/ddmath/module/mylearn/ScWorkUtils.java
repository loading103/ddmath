package com.tsinghuabigdata.edu.ddmath.module.mylearn;

import android.graphics.Bitmap;

import com.tsinghuabigdata.edu.ddmath.commons.AppSessionCache;
import com.tsinghuabigdata.edu.ddmath.constant.AppConst;

/**
 * 超级错题本
 */
public class ScWorkUtils {

    //继续拍照
    public static final String IS_NEXT_IMAGE = "next";
    public final static String TASK_ID = "taskid";

    public final static String IMAGE_TYPE = "image_type";
    public final static String IMAGE_HANDLE = "image_handle";

    public final static String PARAM_BROADCAST = "broadcast";       //拍照处理后，用广播通知
    public final static String PARAM_FROM = "from";

    public final static String TYPE_LOCAL = "local_Path";
    public final static String TYPE_BITMAP = "bitmap";


    public static int setBitmap(Bitmap bitmap){
        AppSessionCache.getInstance().put(AppConst.SESSION_SCWORK_BITMAP, bitmap );
        return AppConst.SESSION_SCWORK_BITMAP;
    }
    public static Bitmap getBitmap(int session){
        AppSessionCache cache = AppSessionCache.getInstance();
        if( cache == null ){
            return null;
        }
        return cache.get( session );
    }
    public static void removeBitmap(int session ){
        AppSessionCache.getInstance().remove( session );
    }

}
