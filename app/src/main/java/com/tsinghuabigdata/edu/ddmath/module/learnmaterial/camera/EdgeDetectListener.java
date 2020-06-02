package com.tsinghuabigdata.edu.ddmath.module.learnmaterial.camera;

/**
 * 边缘检测结果回调
 */
public interface EdgeDetectListener {

    //探测结果回调
    boolean detectResult(int result[], boolean preview);

    //返回处理后的图片
    void detectFile(String filepath);

    //返回的是原图和原图上面的四个点
    void detectFile(String filepath, int[] recpoints);       //识别出的四个点

    //得到相机拍照的旋转角度
    void cameraRorate(int degree[]);

    //再次对焦处理
    void nextAutoFocus();

    //开始拍照处理
    void startTakePicture();

    //复位拍照处理
    void resetTakePicture();

    //显示toast提示
    void showCenterToast(String msg);

    //强制拍照
    void forceCamera();
}
