package com.tsinghuabigdata.edu.ddmath.module.ddwork.camera;

/**
 * 探测结果回调
 */
public interface DetectListener {

    //探测结果回调
    void detectResult(int result);

    //返回处理后的图片
    void detectFile(String filepath);

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
}
