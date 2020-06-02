/*
 * Copyright (c) 2016. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.tsinghuabigdata.edu.ddmath.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.tsinghuabigdata.edu.ddmath.bean.LoginInfo;
import com.tsinghuabigdata.edu.ddmath.constant.AppConst;
import com.tsinghuabigdata.edu.ddmath.module.mylearn.ScWorkCropImageActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;

/**
 * 图片操作
 * Created by yanshen on 2016/4/7.
 */
public class BitmapUtils {

    /**
     * bitmap转byte array
     *
     * @param bm 图片
     * @return byte[]
     */
    public static byte[] bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    /**
     * 显示Asset里面的图片
     *
     * @param context  cc
     * @param fileName 图片名称
     */
    public static void showAssetImage(Context context, String fileName, ImageView imageView) {

        Bitmap bitmap = AssetsUtils.getBitmapFromAssetsFile(context, fileName);
        if (bitmap == null)
            return;

        int picWidth = bitmap.getWidth();//   2547;
        int picHeight = bitmap.getHeight();//  8191;
        AppLog.d("fsdfsdfds picWidth = " + picWidth + ",,,picHeight = " + picHeight);

        bitmap.recycle();

        String assetpath = "file:///android_asset/" + fileName;
        int screenWidth = WindowUtils.getScreenWidth(context);
        ViewGroup.LayoutParams params = imageView.getLayoutParams();
        if (picWidth < screenWidth) {
            params.width = picWidth;
            params.height = picHeight;
            imageView.setLayoutParams(params);
            PicassoUtil.getPicasso(context).load(assetpath).into(imageView);
        } else {
            params.height = screenWidth * picHeight / picWidth;
            imageView.setLayoutParams(params);
            PicassoUtil.getPicasso(context).load(assetpath).resize(screenWidth, params.height).into(imageView);
        }
    }

    /**
     * 控件对象转Bitmap
     *
     * @param v 对象
     * @return Bitmap
     */
    public static Bitmap loadBitmapFromViewBySystem(View v) {
        if (v == null) {
            return null;
        }
        v.setDrawingCacheEnabled(true);
        v.buildDrawingCache();
        return v.getDrawingCache();
    }

    //    public static byte[] getFilData(String filepath) {
    //
    //        File file = new File(filepath);
    //        if (!file.exists() || file.length() == 0)
    //            return null;
    //
    //
    //        FileInputStream fis = null;
    //        try {
    //            fis = new FileInputStream(file);
    //            byte buffer[] = new byte[(int) file.length()];
    //            int len = fis.read(buffer);
    //            fis.close();
    //            if (len == file.length())
    //                return buffer;
    //        } catch (Exception e) {
    //            AppLog.i( "", e );
    //        } finally {
    //            if (fis != null) {
    //                try {
    //                    fis.close();
    //                } catch (Exception e) {
    //                    AppLog.i( "", e );
    //                }
    //            }
    //        }
    //        return null;
    //    }

    /**
     * 把Bitmap保存为文件
     *
     * @param filepath 指定文件名
     * @param bitmap   图片
     * @return 保存的路径
     */
    public static String saveImage(String filepath, Bitmap bitmap) {

        if (TextUtils.isEmpty(filepath))
            return null;
        String filename;
        if (filepath.endsWith(".jpg") || filepath.endsWith(".png")) {
            filename = filepath;

            //保证目录存在
            int index = filepath.lastIndexOf("/");
            String path = filepath.substring(0, index);
            File dir = new File(path);
            boolean b = dir.mkdirs();
            AppLog.d("mkdir = " + b);

        } else {
            if (!filepath.endsWith("/"))
                filepath += "/";

            File dir = new File(filepath);
            boolean b = dir.mkdirs();
            AppLog.d("mkdir = " + b);

            filename = filepath + System.currentTimeMillis() + ".png";
        }

        File file = new File(filename);
        if (file.exists()) {
            boolean b = file.delete();
            AppLog.d("delete file = " + b);
        }

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            bitmap.compress(filename.endsWith(".jpg") ? Bitmap.CompressFormat.JPEG : Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            return filename;
        } catch (Exception e) {
            AppLog.i("", e);
        } finally {
            try {
                if (fos != null)
                    fos.close();
            } catch (IOException e) {
                AppLog.i("", e);
            }
        }
        return null;
    }

    //    /**
    //     * 保存图片到指定目录，并更新到系统相册
    //     *
    //     * @param context
    //     * @param bmp
    //     */
    //    public static boolean saveImageToGallery(Context context, Bitmap bmp, String dir, String fname) {
    //        // 首先保存图片
    //        File appDir = new File(Environment.getExternalStorageDirectory(), dir);
    //        if (!appDir.exists()) {
    //            appDir.mkdir();
    //        }
    //        if (TextUtils.isEmpty(fname)) {
    //            fname = System.currentTimeMillis() + ".jpg";
    //        } else {
    //            fname += ".jpg";
    //        }
    //        File file = new File(appDir, fname);
    //        FileOutputStream fos = null;
    //        try {
    //            fos = new FileOutputStream(file);
    //            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
    //            fos.flush();
    //            fos.close();
    //            fos = null;
    //        } catch (FileNotFoundException e) {
    //            AppLog.i( "", e );
    //            return false;
    //        } catch (Exception e) {
    //            AppLog.i( "", e );
    //            return false;
    //        }finally {
    //            if( fos != null ){
    //                try {
    //                    fos.close();
    //                }catch (Exception e){
    //                    AppLog.i( "", e );
    //                }
    //            }
    //        }
    //
    //        // 其次把文件插入到系统图库
    //        try {
    //            MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), fname, null);
    //        } catch (FileNotFoundException e) {
    //            AppLog.i( "", e );
    //        }
    //        // 最后通知图库更新
    //        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + appDir.getAbsolutePath())));
    //
    //        return true;
    //    }

    /**
     * @param drawable drawable 转  Bitmap
     */
    public static Bitmap drawableToBitmap(Drawable drawable) {
        // 取 drawable 的长宽
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();

        // 取 drawable 的颜色格式
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                : Bitmap.Config.RGB_565;
        // 建立对应 bitmap
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
        // 建立对应 bitmap 的画布
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        // 把 drawable 内容画到画布中
        drawable.draw(canvas);
        return bitmap;
    }

    //    /**
    //     * @param bitmap
    //     * @param roundPx 获取圆角图片
    //     */
    //    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx) {
    //        int w = bitmap.getWidth();
    //        int h = bitmap.getHeight();
    //        Bitmap output = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
    //        Canvas canvas = new Canvas(output);
    //        final int color = 0xff424242;
    //        final Paint paint = new Paint();
    //        final Rect rect = new Rect(0, 0, w, h);
    //        final RectF rectF = new RectF(rect);
    //        paint.setAntiAlias(true);
    //        canvas.drawARGB(0, 0, 0, 0);
    //        paint.setColor(color);
    //        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
    //        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
    //        canvas.drawBitmap(bitmap, rect, rect, paint);
    //
    //        return output;
    //    }

    /**
     *
     */

    public static Bitmap decodeBitmap(String imageFile/*,Display currentDisplay*/) {

        //屏幕大小
        //Display currentDisplay = getWindowManager().getDefaultDisplay();
        //int dw = currentDisplay.getWidth();
        //int dh = currentDisplay.getHeight();


        // 得到图片大小但是不加载图片
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imageFile, opts);

        //int heightRatio = (int) Math.ceil(opts.outHeight / (float) dh);
        //int widthRatio = (int) Math.ceil(opts.outWidth / (float) dw);

        if (opts.outHeight > 6000 || opts.outWidth > 6000) {
            opts.inSampleSize = 4;
        } else if (opts.outHeight > 2000 || opts.outWidth > 2000) {
            opts.inSampleSize = 2;
        }

        // 解析图片
        opts.inJustDecodeBounds = false;
        //opts.inSampleSize = widthRatio;
        //opts.inSampleSize = 1;
        opts.inPreferredConfig = Bitmap.Config.RGB_565;

        Bitmap bmp = null;
        try {
            bmp = BitmapFactory.decodeFile(imageFile, opts);
        } catch (OutOfMemoryError err) {
            AppLog.e("", "", err);
        }
        return bmp;
    }

    /**
     * 根据目标区域，从图片上面剪切指定图片
     */
    public static boolean cropRectImage(Rect dstRect, Bitmap bitmap, String cropimagepath, double image_rate, Context context) {

        //映射到实际图片的位置
        int x1 = (int) (dstRect.left * image_rate);
        int y1 = (int) (dstRect.top * image_rate);
        int width = (int) (dstRect.width() * image_rate);
        int height = (int) (dstRect.height() * image_rate);

        if (x1 < 0)
            x1 = 0;
        else if (x1 > bitmap.getWidth()) {
            x1 = bitmap.getWidth();
        }

        if (y1 < 0)
            y1 = 0;
        else if (y1 > bitmap.getHeight()) {
            y1 = bitmap.getHeight();
        }

        if (x1 + width > bitmap.getWidth()) {
            width = bitmap.getWidth() - x1;
        }
        if (y1 + height > bitmap.getHeight()) {
            height = bitmap.getHeight() - y1;
        }

        if (width <= 0)
            width = 1;
        if (height <= 0)
            height = 1;

        //剪切图
        Bitmap tmp = Bitmap.createBitmap(bitmap, x1, y1, width, height);
        return compressImage(cropimagepath, tmp, context);
    }

    /*
     * 压缩图片  对图片尺寸和大小进行处理
     */
    private static boolean compressImage(String cropimagepath, Bitmap bitmap, Context mContext) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中

        int fsize = baos.toByteArray().length / 1024;
        AppLog.d("----compressImage fsize =" + fsize + ",,,max_size = " + ScWorkCropImageActivity.OLD_SIZE);

        int options;

        /*if( fsize > 3000){
            options = 20;
        }else if( fsize > 2048 ){
            options = 30;
        }else if( fsize > 1024) {        //>1M
            options = 40;
        }else if( fsize > 700) {        //>
            options = 50;
        }else
        if (fsize > 500) {        //>
            options = 80;
        } else */if (fsize > 300) {        //>
            options = 80;
        } else if (fsize > 200) {        //>
            options = 90;
        } else {
            options = 100;
        }

        //int tc = 0;
        //用于上传显示的图片
        if (fsize > ScWorkCropImageActivity.OLD_SIZE) {
            while (baos.toByteArray().length / 1024 > ScWorkCropImageActivity.OLD_SIZE) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩
                baos.reset();//重置baos即清空baos
                options -= 10;//每次都减少10
                if (options <= 80) {
                    options = 80;
                    break;
                }
                bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
                //tc++;
            }
        }
        FileOutputStream fout = null;
        try {
            fout = new FileOutputStream(cropimagepath);
            if (!bitmap.compress(Bitmap.CompressFormat.JPEG, options, fout)) {
                AppLog.d("----compressImage save fail ");
                return false;
            }
        } catch (Exception e) {
            if (cropimagepath != null && cropimagepath.contains("null")) {
                AlertManager.toast(mContext, "出现异常，请先退出APP，重新操作。");
            } else
                AlertManager.toast(mContext, "磁盘空间不足，无法保存图片。");
            AppLog.i("", e);
            return false;
        } finally {
            if (fout != null) {
                try {
                    fout.close();
                    fout.flush();
                } catch (IOException e) {
                    AppLog.i("", e);
                }
            }
        }
        return true;
    }


    /**
     *
     */
    public static String getUrlQuality(String url, int quality, String accessToken) {
        if( url.endsWith(".jpg") ){
            url = url.replace(".jpg",",q"+quality+".jpg");
        }else if( url.endsWith(".jpeg") ){
            url = url.replace(".jpeg",",q"+quality+".jpeg");
        }else if( url.endsWith(".png") ){
            url = url.replace(".png",",q"+quality+".png");
        }
        return getUrlQuality( url, accessToken);
    }

    public static String getUrlQuality(String url, String accessToken) {
        if( !TextUtils.isEmpty(url) && !url.contains("?access_token=") ){
            try {
                url += "?access_token=" + URLEncoder.encode( accessToken,"utf-8");
            } catch (Exception e) {
                AppLog.i("", e);
            }
        }
        return url;
    }

    public static String getUrlWithToken(String url) {
        if(TextUtils.isEmpty(url))
            return "http://www.iclassedu.com/";
        if( url.startsWith("file://") )
            return url;

        LoginInfo loginInfo = AccountUtils.getLoginUser();
        if( loginInfo == null ) loginInfo = AccountUtils.getLoginParent();
        if( !url.startsWith("http://") && !url.startsWith("https://") ){
            String domain = "http://www.iclassedu.com"+(url.startsWith("/")?"":"/");
            if( loginInfo !=null ) domain = loginInfo.getFileServer();
            url = domain + url;
        }
        if( !url.contains("?access_token=") && loginInfo!=null ){
            try {
                url += "?access_token=" + URLEncoder.encode( loginInfo.getAccessToken(),"utf-8");
            } catch (Exception e) {
                AppLog.i("", e);
            }
        }
        return url;
    }
    /**
     * 白色变透明
     */
    public static Bitmap transparentBitmap(Bitmap bitmap) {

        int[] argb = new int[bitmap.getWidth() * bitmap.getHeight()];

        bitmap.getPixels(argb, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());// 获得图片的ARGB值

        for (int i = 0; i < argb.length; i++) {
            if (argb[i] == -1)
                argb[i] = (argb[i] & 0x00FFFFFF);
        }
        //bitmap.recycle();

        return Bitmap.createBitmap(argb, bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
    }

    /**
     * 透明变白色
     */
    public static Bitmap transToWhiteBitmap(Bitmap bitmap) {

        int[] argb = new int[bitmap.getWidth() * bitmap.getHeight()];
        bitmap.getPixels(argb, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());// 获得图片的ARGB值

        for (int i = 0; i < argb.length; i++) {
            if (argb[i] == 0)
                argb[i] = -1;
        }
        return Bitmap.createBitmap(argb, bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        //bitmap.recycle();
        //return dstBitmap;
    }

    /**
     * 使图片等比居中显示最大的大小,
     * ImageView scaleType="fitXY"
     */
    public static int showBestMaxBitmap(Bitmap bitmap, int maxwidth, int maxheight, View photoView) {

        //先以高度满计算宽度
        int width = bitmap.getWidth() * maxheight / bitmap.getHeight();
        int height = maxheight;

        //计算出来的值不合适，再以宽度占满，计算高度
        if (width > maxwidth) {
            width = maxwidth;
            height = bitmap.getHeight() * maxwidth / bitmap.getWidth();
        }

        //最大放大2倍
        int rate = width / bitmap.getWidth();
        if (rate > 2) {
            width = bitmap.getWidth() * 2;
            height = bitmap.getHeight() * 2;
        }

        //根据计算的值调整 photo的大小
        int marginWidth = (maxwidth - width) / 2;
        int marginHeight = (maxheight - height) / 2;
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) photoView.getLayoutParams();
        layoutParams.setMargins(marginWidth, marginHeight, marginWidth, marginHeight);
        photoView.setLayoutParams(layoutParams);

        return height;
    }

    /**
     * 整合两个bitmap（邀请好友页面专用）
     */
    public static Bitmap toConformBitmap(Bitmap background, Bitmap foreground) {
        if (background == null) {
            return null;
        }
        int bgWidth = background.getWidth();
        int bgHeight = background.getHeight();
        //int fgWidth = foreground.getWidth();
        int fgHeight = foreground.getHeight();
        //create the new blank bitmap 创建一个新的和SRC长度宽度一样的位图
        Bitmap newbmp = Bitmap.createBitmap(bgWidth, bgHeight, Bitmap.Config.ARGB_8888);
        Canvas cv = new Canvas(newbmp);
        cv.save();//保存
        //draw bg into
        cv.drawBitmap(background, 0, 0, null);//在 0，0坐标开始画入bg
        //draw fg into
        float left = bgWidth * (1 - AppConst.WIDTH_RATIO) / 2;
        float top = bgHeight * (1 - AppConst.DIS_RATIO) - fgHeight / 2;
        cv.drawBitmap(foreground, left, top, null);//在 0，0坐标开始画入fg ，可以从任意位置画入
        //save all clip
        //store
        cv.restore();//存储
        return newbmp;
    }

    /**
     * Bitmap缩放到指定的大小
     */
    public static Bitmap zoomImage(Bitmap bgimage, float newWidth,
                             float newHeight) {
        // 获取这个图片的宽和高
        float width = bgimage.getWidth();
        float height = bgimage.getHeight();
        // 创建操作图片用的matrix对象
        Matrix matrix = new Matrix();
        // 计算宽高缩放率
        float scaleWidth = newWidth / width;
        float scaleHeight =  newHeight / height;
        // 缩放图片动作
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, (int) width,
                (int) height, matrix, true);
        return bitmap;
    }

    /**
    * 图片去色,返回灰度图片
    * @param bmpOriginal 传入的图片
    * @return 去色后的图片
    */
    public static Bitmap toGrayscale(Bitmap bmpOriginal) {

        long time = System.currentTimeMillis();

//        for(int i = 0;i<bmpOriginal.getWidth();i++)
//        {
//            for(int j =0;j<bmpOriginal.getHeight();j++)
//            {
//                int col = bmpOriginal.getPixel(i, j);
//                int alpha = col&0xFF000000;
//                int red = (col&0x00FF0000)>>16;
//                int green = (col&0x0000FF00)>>8;
//                int blue = (col&0x000000FF);
//                int gray = (int)((float)red*0.3+(float)green*0.59+(float)blue*0.11);
//                int newColor = alpha|(gray<<16)|(gray<<8)|gray;
//                bmpOriginal.setPixel(i, j, newColor);
//                //Log.v("tag",  Integer.toHexString(col));
//            }
//        }
//        AppLog.d("dsfdsfsdfds time = " + (System.currentTimeMillis()-time));
//
//        return bmpOriginal;


        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();

        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);

        AppLog.d("dsfdsfsdfds time = " + (System.currentTimeMillis()-time));
        return bmpGrayscale;
    }
}

