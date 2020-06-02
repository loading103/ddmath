package com.tsinghuabigdata.edu.ddmath.util;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;

/**
 * Created by 28205 on 2016/6/3.
 */
public class DirUtils {
    /**
     * 几种文件存储方式
     * 1.内部存储
     *    在Android中，以这种方式保存的和数据是只能被当前程序访问，在其他程序中是无法访问到的，而当用户卸载该程序的时候，这些文件也会随之被删除。
     *    使用内部存储的方式进行数据持久化，文件的地址将保存在/data/data/<package_name>/files/路径下.
     * 2.缓存(cache)
     * 　缓存文件(cache files)的操作与操作内部存储中的文件方式基本一致，只是获取文件的路径有说不同。使用Context.getCacheDir()方法获取文件保存的路径。
     * 　对于缓存文件而言，当设备内部内存存储空间不足的时候，Android会有自动删除的机制删除这些缓存文件,建议不要放太大或重要的数据。
     * 3.External Storage
     *   外部存储一般就是指的是sdcard。使用sdcard存储的数据，不限制只有本应用访问，任何可以有访问Sdcard权限的应用均可以访问，
     *   而Sdcard相对于设备的内部存储空间而言，会大很多，所以一般比较大的数据，均会存放在外部存储中。
     *
     *    一些路径的标准写法
     *    Environment.getDataDirectory() = /data
     *    Environment.getDownloadCacheDirectory() = /cache
     *    Environment.getExternalStorageDirectory() = /mnt/sdcard or /storage/0/..
     *    Environment.getRootDirectory() = /system
     *    context.getCacheDir() = /data/data/com.mt.mtpp/cache
     *    context.getExternalCacheDir() = /mnt/sdcard/Android/data/com.mt.mtpp/cache
     *    context.getFilesDir() = /data/data/com.mt.mtpp/files
     *    Context.getExternalFilesDir（） = /mnt/sdcard/Android/data/com.mt.mtpp/files，在应用被卸载的时候，系统会清理的就是这个目录,外部存储但是包私有的目录。
     */
    /**
     * 获取缓存目录
     *
     * @param dir
     * @return
     */
    public static String getExternalDir(String dir) {
        if (TextUtils.isEmpty(dir)) {
            return dir;
        }
        if (!dir.startsWith("/")) {
            dir = "/" + dir;
        }

        String path = Environment.getExternalStorageDirectory() + dir;
        File f = new File(path);
        if (!f.exists()) {
            f.mkdirs();
        }
        return path;
    }

    /**
     * 相对sdcard/Android/data/{package}/files/obj/
     *
     * @param context
     * @param dir
     * @return
     */
    public static String getExternalPrivateFileDir(Context context, String dir) {
        if (!dir.startsWith("/")) {
            dir = "/" + dir;
        }
        File file = context.getExternalFilesDir(null);
        if( file == null ) return "";

        String path = file.getAbsolutePath() + dir;
        File f = new File(path);
        if (!f.exists()) {
            f.mkdirs();
        }
        return path;
    }

    /**
     * @param dir
     * @return
     */
    public static String getAbsFileDir(String path, String dir) {
        if (!dir.startsWith("/")) {
            dir = "/" + dir;
        }
        if (path.endsWith("/")){
            path += dir.substring(1, dir.length());
        }else{
            path += dir;
        }
        File f = new File(path);
        if (!f.exists()) {
            f.mkdirs();
        }
        return path;
    }
}