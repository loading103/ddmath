package com.tsinghuabigdata.edu.ddmath.util;

/*
 * 文件相关操作
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import androidx.core.content.FileProvider;

import com.tsinghuabigdata.edu.ddmath.constant.AppConst;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtil {
    private static final int FILE_SIZE = 4 * 1024;
    private static final String TAG = "FileUtil";

    public static boolean hasSdcard() {
        String status = Environment.getExternalStorageState();
        return status.equals(Environment.MEDIA_MOUNTED);
    }

    public static boolean createPath(String path) {
        File f = new File(path);
        if (!f.exists()) {
            Boolean o = f.mkdirs();
            Log.i(TAG, "create dir:" + path + ":" + o.toString());
            return o;
        }
        return true;
    }


    public static void createPath(Context context, String path){
        synchronized (FileUtil.class){
            File file = new File(path);
            if( !file.exists() && !file.mkdirs() ){     //创建目录失败
                AppLog.i(" createPath dir = " + path );

                File testdir = new File(Environment.getExternalStorageDirectory().toString());
                AppLog.i("sdcard dir, state = " + Environment.getExternalStorageState() );
                AppLog.i("sdcard dir, canread = " + testdir.canRead() );
                AppLog.i("sdcard dir, canwrite = " + testdir.canWrite() );
                AppLog.i("sdcard dir, mount = " + Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) );

                if( context!= null ){
                    Intent intent = new Intent();
                    intent.setAction(AppConst.ACTION_APPLICATION_EXIT);
                    intent.putExtra("msg","创建文件夹失败，请重启应用或者重启机器。");
                    context.sendBroadcast( intent );
                }
            }
        }
    }

    public static boolean exists(String file) {
        return new File(file).exists();
    }

    public static File saveFile(String file, InputStream inputStream) {
        File f = null;
        OutputStream outSm = null;

        try {
            f = new File(file);
            String path = f.getParent();
            if (!createPath(path)) {
                Log.e(TAG, "can't create dir:" + path);
                return null;
            }

            if (!f.exists()) {
                f.createNewFile();
            }

            outSm = new FileOutputStream(f);
            byte[] buffer = new byte[FILE_SIZE];
            while ((inputStream.read(buffer)) != -1) {
                outSm.write(buffer);
            }
            outSm.flush();
        } catch (IOException ex) {
            AppLog.i( "", ex );
            return null;

        } finally {
            try {
                if (outSm != null) outSm.close();
            } catch (IOException ex) {
                AppLog.i( "", ex );
            }
        }
        Log.v(TAG, "[FileUtil]save file:" + file + ":" + Boolean.toString(f.exists()));

        return f;
    }

//    /**
//     * 保存文件
//     *
//     * @param fileName 文件名
//     * @param content  文件内容
//     * @throws Exception
//     */
//    public static void saveHtml(String fileName, String content) throws Exception {
//
//        // 由于页面输入的都是文本信息，所以当文件名不是以.txt后缀名结尾时，自动加上.txt后缀
//        if (!fileName.endsWith(".html")) {
//            fileName = fileName + ".html";
//        }
//
//        // Context.MODE_PRIVATE：为默认操作模式，代表该文件是私有数据，只能被应用本身访问，在该模式下，写入的内容会覆盖原文件的内容，如果想把新写入的内容追加到原文件中。可以使用Context.MODE_APPEND
//        // Context.MODE_APPEND：模式会检查文件是否存在，存在就往文件追加内容，否则就创建新文件。
//        // Context.MODE_WORLD_READABLE和Context.MODE_WORLD_WRITEABLE用来控制其他应用是否有权限读写该文件。
//        // MODE_WORLD_READABLE：表示当前文件可以被其他应用读取；MODE_WORLD_WRITEABLE：表示当前文件可以被其他应用写入。
//        // 如果希望文件被其他应用读和写，可以传入：
//        // openFileOutput("output.txt", Context.MODE_WORLD_READABLE + Context.MODE_WORLD_WRITEABLE);
//        File f = new File(fileName);
//        if (!f.exists()) {
//            f.createNewFile();
//        }
//        FileOutputStream fos = new FileOutputStream(f);
//        fos.write(content.getBytes("utf-8"));
//        fos.close();
//    }

    public static boolean delFile( String filename ) {
        File f = new File(filename);

        if (!f.exists()) {
            return true;
        }
        if( f.isDirectory() ){
            boolean st = true;

            File[] files = f.listFiles();
            // 如果是空文件夹则删除
            if (files != null  ) {
                for( File file : files ){
                    if( file.isFile() ){
                        st = st && file.delete();
                    }else{
                        st = st && delFile( file.getAbsolutePath() );
                    }
                }
            }
            return st && f.delete();
        }else{
            return f.delete();
        }
    }

//    public static Drawable getImageDrawable(String file) {
//        if (!exists(file)) return null;
//        try {
//            InputStream inp = new FileInputStream(new File(file));
//            return BitmapDrawable.createFromStream(inp, "img");
//        } catch (Exception ex) {
//            AppLog.i( "", ex );
//        }
//        return null;
//    }

    /**
     *  从文件获得URI
     *  @param context 上下文
     *  @param file 文件
     *  @return 文件对应的URI
     **/
    public static Uri uriFromFile(Context context, File file) {
        Uri fileUri; //7.0以上进行适配
        if (Build.VERSION.SDK_INT >= 24 /*Build.VERSION_CODES.N*/ ) {
            String p = context.getPackageName() + ".fileprovider";
            fileUri = FileProvider.getUriForFile( context, p, file);
        } else {
            fileUri = Uri.fromFile(file);
        }
        return fileUri;
    }

    /**
     * 设置Intent的data和类型，并赋予目标程序临时的URI读写权限
     * @param context 上下文
     * @param intent 意图
     * @param type 类型
     * @param file 文件
     * @param writeAble 是否赋予可写URI的权限
     * */
    public static void setIntentDataAndType(Activity context, Intent intent, String type, File file, boolean writeAble) {
        //7.0以上进行适配
        if (Build.VERSION.SDK_INT >= 24/*Build.VERSION_CODES.N*/) {
            intent.setDataAndType(uriFromFile(context, file), type);
            //临时赋予读写Uri的权限
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            if (writeAble) {
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            }
        } else {
            intent.setDataAndType(Uri.fromFile(file), type);
        }
    }

    /**
     * 设置Intent的data和类型，并赋予目标程序临时的URI读写权限
     * @param context 上下文
     * @param intent 意图
     * @param type 类型
     * @param fileUri 文件uri
     * @param writeAble 是否赋予可写URI的权限
     * */
    public static void setIntentDataAndType(Context context, Intent intent, String type, Uri fileUri, boolean writeAble) {
        //7.0以上进行适配
        if (Build.VERSION.SDK_INT >= 24/*Build.VERSION_CODES.N*/) {
            intent.setDataAndType(fileUri, type);
            //临时赋予读写Uri的权限
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            if (writeAble) {
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            }
        } else {
            intent.setDataAndType(fileUri, type);
        }
    }


}