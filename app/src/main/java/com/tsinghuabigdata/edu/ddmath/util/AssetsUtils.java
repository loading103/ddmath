package com.tsinghuabigdata.edu.ddmath.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Administrator on 2017/10/19.
 */

public class AssetsUtils {

    /**
     * 在Asserts中获取图标
     * @param context 上下文
     * @param fileName 文件名称，带后缀名
     */
    public static Bitmap getBitmapFromAssetsFile( Context context, String fileName) {
        Bitmap bitmap = null;
        AssetManager am = context.getResources().getAssets();
        try {
            InputStream is = am.open(fileName);
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            AppLog.i("in asserts not found " + fileName, e);
        }
        return bitmap;
    }

    public static Drawable assets2Drawable(Context context, String fileName) {
        InputStream open = null;
        Drawable drawable = null;
        try {
            open = context.getAssets().open(fileName);
            drawable = Drawable.createFromStream(open, null);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (open != null) {
                    open.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return drawable;
    }

    public static Typeface getMyTypeface(Context context) {
        return Typeface.createFromAsset(context.getAssets(), "fonts/happy.ttf");
    }

    /**
     *
     * @param context :application context
     * @param fileName :原文件名，不包含路径
     * @param dstPath : 目标文件夹
     */
    public static boolean copyAssetsToDst(Context context,String fileName,String dstPath) {

        //先判断文件是否存在
        String filePath = dstPath+"/"+fileName;
        if( new File(filePath).exists() )
            return true;

        //判断文件夹是否存在
        File dir = new File( dstPath );
        if( !dir.exists() ){
            if( !dir.mkdirs() )
                return false;
        }

        InputStream is = null;
        FileOutputStream fos = null;
        try {
            is = context.getAssets().open(fileName);
            fos = new FileOutputStream(new File(filePath));
            byte[] buffer = new byte[1024];
            int byteCount=0;
            while((byteCount=is.read(buffer))!=-1) {
                fos.write(buffer, 0, byteCount);
            }
            fos.flush();//刷新缓冲区
            return true;
        } catch (IOException e) {
            AppLog.i("in asserts not found " + fileName, e);
        }finally {
            if( is!=null){
                try{ is.close(); }catch (Exception e){ AppLog.d("",e);}
            }
            if( fos!=null){
                try{ fos.close(); }catch (Exception e){ AppLog.d("",e);}
            }
        }
        return false;
    }
}
