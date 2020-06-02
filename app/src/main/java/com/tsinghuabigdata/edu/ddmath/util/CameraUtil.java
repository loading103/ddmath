package com.tsinghuabigdata.edu.ddmath.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.util.Size;
import android.view.Surface;

import com.tsinghuabigdata.edu.ddmath.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 相机相关功能
 */
public class CameraUtil {

    //拍照变换后的目标大小
    public final static int DIST_PICTURE_WIDTH  = 1200+100;
    public final static int DIST_PICTURE_HEIGHT = 1840+100;
    //最小像素
    private final static int DIST_MIN_PIXEL = 3000000;
    /**
     * 获取屏幕长宽的比例，包含导航条部分
     * @param activity activity
     * @return 长宽比例(确保 小于 1 )
     * 注意: 仅竖屏使用
     */
    public static float getScreenRate(Activity activity){

        int wh[] = WindowUtils.getScreenWHForCamera( activity );
        int screen_width = wh[1];//WindowUtils.getScreenWidthForCamera( mContext );
        int screen_height= wh[0];//WindowUtils.getScreenHeightForCamera( mContext )  + (navbar?WindowUtils.getBottomBarHeight(mContext):0);       //竖屏使用
        if( screen_height < screen_width ){
            int tmp = screen_height;
            screen_height = screen_width;
            screen_width = tmp;
        }

        float rate;
        if( screen_width < screen_height ){
            rate = screen_width*1f/screen_height;
        }else{
            rate = screen_height*1f/screen_width;
        }
        if( rate <= 0.5625f+(0.75f-0.5625)/2 )
            rate = 0.5625f;     //16:9
        else
            rate = 0.75f;       //4:3

        return rate;
    }

    //横屏拍照使用
    public static float getScreenRateLand(Activity activity){

        int wh[] = WindowUtils.getScreenWHForCamera( activity );
        int screen_width = wh[1];//WindowUtils.getScreenWidthForCamera( mContext );
        int screen_height= wh[0];//WindowUtils.getScreenHeightForCamera( mContext )  + (navbar?WindowUtils.getBottomBarHeight(mContext):0);       //竖屏使用
        if( screen_height > screen_width ){
            int tmp = screen_height;
            screen_height = screen_width;
            screen_width = tmp;
        }

        float rate;
        if( screen_width < screen_height ){
            rate = screen_width*1f/screen_height;
        }else{
            rate = screen_height*1f/screen_width;
        }
        if( rate <= 1.3333f+(1.7778f-1.3333)/2 )
            rate = 1.3333f;     //4:3
        else
            rate = 1.7778f;       //16:9

        return rate;
    }

    static class TSize{
        private int width;
        private int height;
        /*public*/ TSize( int w, int h ){
            width = w;
            height = h;
        }
    }
    /**
     * 根据屏幕长宽的比例，得到最合适的预览尺寸
     * @param list   支持的列表
     * @param screen_rate 目标比例
     * @return 找到的尺寸
     */
    public  static Point getBestCameraSize(List<Camera.Size> list, float screen_rate){
        List<TSize> sizeList = new ArrayList<>();
        for( Camera.Size size : list ){
            sizeList.add( new TSize( size.width, size.height ) );
        }
        return getBestCameraSizeBase( sizeList, screen_rate );
    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public  static Point getBestCameraSize2(List<Size> list, float screen_rate){
        List<TSize> sizeList = new ArrayList<>();
        for( Size size : list ){
            sizeList.add( new TSize( size.getWidth(), size.getHeight() ) );
        }
        return getBestCameraSizeBase( sizeList, screen_rate );
    }
    private static Point getBestCameraSizeBase(List<TSize> list, float screen_rate){
        float tmp,mindiff = 100f;
        TSize best = null;
        for( TSize size : list ){
            AppLog.d("---dfsfsdfds Size width = "+size.width+",,height="+size.height + ",,, rate = " + ( size.height *1f/size.width ) );
            tmp = Math.abs(((float)size.height/(float)size.width) - screen_rate);
            if( tmp < mindiff ){
                mindiff = tmp;
                best = size;
            }else if( Math.abs( tmp-mindiff ) < 0.01 && best != null  ){        //默认相等
                if( size.height*size.width > best.width*best.height ){
                    mindiff = tmp;
                    best = size;
                }
            }
        }
        return best==null?null:new Point(best.width, best.height);
    }

    /**
     * 根据相机成像尺寸，确认合适的尺寸
     * @param list   支持的列表
     * @param screen_rate 目标比例
     * @return 找到的尺寸
     */

    public  static Point getBestCameraPictureSize(List<Camera.Size> list, float screen_rate){
        List<TSize> sizeList = new ArrayList<>();
        for( Camera.Size size : list ){
            sizeList.add( new TSize( size.width, size.height ) );
        }
        return getBestCameraPictureSizeBase( sizeList, screen_rate );
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public  static Point getBestCameraPictureSize2(List<Size> list, float screen_rate){
        List<TSize> sizeList = new ArrayList<>();
        for( Size size : list ){
            sizeList.add( new TSize( size.getWidth(), size.getHeight() ) );
        }
        return getBestCameraPictureSizeBase( sizeList, screen_rate );
    }

    private   static Point getBestCameraPictureSizeBase(List<TSize> list, float screen_rate){

        boolean first = true;
        float tmp,mindiff = 100f;
        long minPixelDiff=0;
        TSize best = null;
        for( TSize size : list ){

            //注意:Size 按横屏算的，所以 width height 要反着处理

            //不能满足尺寸的，过滤
            if( size.width < DIST_PICTURE_HEIGHT || size.height < DIST_PICTURE_WIDTH)
                continue;

            //满足尺寸，最好在 300万像素左右的
            long count = size.width*size.height;
            if( count < DIST_MIN_PIXEL ) continue;     //小于300万，过滤

            AppLog.d("---dfsfsdfds ddd Size width = "+size.width+",,height="+size.height + ",,, rate = " + ( size.height *1f/size.width ) );

            //1最近屏幕比例 和 300万像素的目标
            if( first ){             //第一个作为备用目标
                mindiff = Math.abs(((float)size.height/(float)size.width) - screen_rate);
                minPixelDiff = count - DIST_MIN_PIXEL;
                best = size;
                first = false;
            }else{
                //比例最近 优先
                tmp = Math.abs(((float)size.height/(float)size.width) - screen_rate);
                if( Math.abs( tmp-mindiff ) < 0.05 ){
                    if( count - DIST_MIN_PIXEL < minPixelDiff  ){      //相等时，像素接近优先
                        minPixelDiff = count - DIST_MIN_PIXEL;
                        mindiff = tmp;
                        best = size;
                    }
                }else if( tmp < mindiff  ){        //不相等， 比例优先
                    minPixelDiff = count - DIST_MIN_PIXEL;
                    mindiff = tmp;
                    best = size;
                }
            }
        }
        return  best==null?null:new Point(best.width, best.height);
    }

    public static int getCameraDegree( Activity activity) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(0, info);
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
            default:
                break;
        }

        int degree;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            degree = (info.orientation + degrees) % 360;
            degree = (360 - degree) % 360; // compensate the mirror
        } else { // back-facing
            degree = (info.orientation - degrees + 360) % 360;
        }

        return degree;
    }


    //用户控制闪光灯的记录
    private int getCameraFlashMode(Context context) {
        SharedPreferences sharep = context.getSharedPreferences("ask_camera", 0);
        return sharep.getInt("flashmode", 0);
    }

    private String getCameraFlashModeStr(Context context) {
        int mode = getCameraFlashMode(context);
        String c_mode;
        if (mode == 1) {
            c_mode = Camera.Parameters.FLASH_MODE_TORCH;
        } else {
            c_mode = Camera.Parameters.FLASH_MODE_OFF;
        }
        return c_mode;
    }

    public static void setFlashMode(Context context,int mode) {
        SharedPreferences sharep = context.getSharedPreferences("ask_camera", 0);
        SharedPreferences.Editor editor = sharep.edit();
        editor.putInt("flashmode", mode);
        editor.apply();
    }
    public static int getImageFlashMode(Context context) {
        SharedPreferences sharep =context.getSharedPreferences("ask_camera", 0);
        int mode = sharep.getInt("flashmode", 0);
        int img_id;
        if (mode == 1) {
            img_id = R.drawable.ic_flashlight_orange;
        } else {
            img_id = R.drawable.ic_flashlight_white;
        }
        return img_id;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void openLedFlash( Activity activity,boolean open){
        try {
            CameraManager cameraManager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
            if( cameraManager == null ) return;
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if( open ){
                    cameraManager.setTorchMode( "0", true );
                }else{
                    cameraManager.setTorchMode( "0", false );
                }
            }
        } catch (Exception e) {
            AppLog.i( "", e );
        }
    }

}
