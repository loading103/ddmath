package com.tsinghuabigdata.edu.ddmath.util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

/**
 * 权限检查工具类
 */
public class PermissionUtil {

    // 音频获取源
    public static final int AUDIOSOURCE    = MediaRecorder.AudioSource.MIC;
    // 设置音频采样率，44100是目前的标准，但是某些设备仍然支持22050，16000，11025
    public static final int SAMPLERATEINHZ = 44100;
    // 设置音频的录制的声道CHANNEL_IN_STEREO为双声道，CHANNEL_CONFIGURATION_MONO为单声道
    public static final int CHANNELCONFIG  = AudioFormat.CHANNEL_IN_STEREO;
    // 音频数据格式:PCM 16位每个样本。保证设备支持。PCM 8位每个样本。不一定能得到设备支持。
    public static final int AUDIOFORMAT    = AudioFormat.ENCODING_PCM_16BIT;

    /**
     * 判断是是否有录音权限
     */
    public static boolean hasAudioPermission(Context context) {
        int bufferSizeInBytes = 0;
        bufferSizeInBytes = AudioRecord.getMinBufferSize(SAMPLERATEINHZ,
                CHANNELCONFIG, AUDIOFORMAT);
        AudioRecord audioRecord = new AudioRecord(AUDIOSOURCE, SAMPLERATEINHZ,
                CHANNELCONFIG, AUDIOFORMAT, bufferSizeInBytes);
        //开始录制音频
        try {
            // 防止某些手机崩溃，例如联想
            audioRecord.startRecording();
        } catch (IllegalStateException e) {
            AppLog.i("",e);
        }
        /**
         * 根据开始录音判断是否有录音权限
         */
        if (audioRecord.getRecordingState() != AudioRecord.RECORDSTATE_RECORDING) {
            return false;
        }
        audioRecord.stop();
        audioRecord.release();
        audioRecord = null;

        return true;
    }

    /**
     * 根据开始录音判断是否有开启摄像头权限
     */
    public static boolean hasCameraPermission() {

        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        int cameraCount = Camera.getNumberOfCameras();//得到摄像头的个数

        for (int i = 0; i < cameraCount; i++) {
            Camera.getCameraInfo(i, cameraInfo);//得到每一个摄像头的信息
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {//代表摄像头的方位，CAMERA_FACING_FRONT前置      CAMERA_FACING_BACK后置
                try {
                    Camera camera = Camera.open(i);//打开当前选中的摄像头
                    camera.release();
                    return true;
                } catch (Exception e) {
                    AppLog.i("",e);
                }
                break;
            }
        }
        return false;
    }

    public static boolean hasExternalStoragePermission(Context context){
        int perm = context.checkCallingOrSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE");
        return perm == PackageManager.PERMISSION_GRANTED;
    }
}
