package com.tsinghuabigdata.edu.ddmath.util;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;


/**
 * <p>
 * Created by yanshen@tsinghuabigdata.com on 2015/11/10.
 * </p>
 *
 * @author yanshen@tsinghuabigdata.com
 * @version V1.0
 * @packageName:
 * @createTime: 2015/11/10 16:44
 */
public class ContextUtils {
    /**
     * 获取缓存目录
     * @param dir
     * @return
     */
    public static String getCacheDir(String dir){
        if(TextUtils.isEmpty(dir)){
            return dir;
        }
        if(!dir.startsWith("/")){
            dir = "/" + dir;
        }

        String path = Environment.getExternalStorageDirectory() + dir;
        File file = new File(path);
        file.mkdirs();
        return path;
    }

    /**
     * 相对sdcard/Android/data/{package}/files/obj/
     * @param context
     * @param dir
     * @return
     */
    public static String getExternalCacheDir(Context context, String dir){
        if(TextUtils.isEmpty(dir)){
            return dir;
        }
        if(!dir.startsWith("/")){
            dir = "/" + dir;
        }
//        return context.getExternalCacheDir() + dir;
        return context.getExternalFilesDir(null) + dir;
    }

}
