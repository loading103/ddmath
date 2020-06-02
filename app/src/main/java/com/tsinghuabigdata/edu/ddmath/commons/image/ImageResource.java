/*
 * Copyright (c) 2016. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.tsinghuabigdata.edu.ddmath.commons.image;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import com.tsinghuabigdata.edu.ddmath.util.AlertManager;
import com.tsinghuabigdata.edu.ddmath.util.FileUtil;

import java.io.File;


/**
 * 图片资源
 * Created by yanshen on 2016/4/7.
 */
public class ImageResource {

    private Activity mContext;

    public ImageResource(Activity context) {
        this.mContext = context;
    }

//    /**
//     * 调用相册
//     *
//     * @param requestCode
//     */
//    public void openPhotoSel(int requestCode) {
//        Intent intent = new Intent();
//        /* 开启Pictures画面Type设定为image */
//        intent.setType("image/*");
//        /* 使用Intent.ACTION_GET_CONTENT这个Action */
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        mContext.startActivityForResult(Intent.createChooser(intent, "请选择上传头像"), requestCode);
//    }

//    /**
//     * 调用相册
//     *
//     * @param requestCode
//     */
//    public void openPhotoSel2(int requestCode) {
//        Intent intent = new Intent();
//
//        /* 使用Intent.ACTION_GET_CONTENT这个Action */
//        intent.setAction(Intent.ACTION_PICK);
//        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
//        mContext.startActivityForResult(Intent.createChooser(intent, "请选择上传头像"), requestCode);
//    }
    /** * 打开相册 */
    public static void openPhotoSel2(Activity activity, int requestCode){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction("android.intent.action.PICK");
        intent.addCategory("android.intent.category.DEFAULT");
        activity.startActivityForResult(intent, requestCode);
    }


//    /**
//     * 调用图片剪辑程序
//     *
//     * @param uri
//     * @param requestCode
//     */
//    public void openPhotoClip(Uri uri, int requestCode) {
//        Intent intent = new Intent("com.android.camera.action.CROP");
//        intent.setDataAndType(uri, "image/*");
//        intent.putExtra("crop", "true");
//        intent.putExtra("aspectX", 1);
//        intent.putExtra("aspectY", 1);
//        intent.putExtra("outputX", 200);
//        intent.putExtra("outputY", 200);
//        intent.putExtra("return-data", true);
//        mContext.startActivityForResult(intent, requestCode);
//    }

    /** * 打开图片裁切 */
    public static void openPhotoClip(Activity activity, Uri uri, File outputFile,int requestCode ) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        FileUtil.setIntentDataAndType(activity, intent, "image/*", uri, true);
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        //return-data为true时，直接返回bitmap，可能会很占内存，不建议，小米等个别机型会出异常！！！
        // 所以适配小米等个别机型，裁切后的图片，不能直接使用data返回，应使用uri指向
        // 裁切后保存的URI，不属于我们向外共享的，所以可以使用fill:// 类型的URI
        Uri outputUri = Uri.fromFile(outputFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
        intent.putExtra("return-data", false);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);
        activity.startActivityForResult(intent, requestCode);
    }



//    /**
//     * 调用相机
//     *
//     * @param requestCode
//     */
//    public void openCamera(Uri uri, int requestCode) {
//
//        // 调用照相机
//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE, null);
//        // 设置保存文件路径
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
//        intent.putExtra("return-data", false);
//        mContext.startActivityForResult(intent, requestCode);
//    }
    /** * 打开相机拍照 */
    public static void openCamera(Activity activity, File outputFile,int requestCode){
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))// 判断是否有SD卡
        {
            AlertManager.toast(activity, "没有SD卡");
            return;
        }
        Intent intent = new Intent();
        intent.setAction("android.media.action.IMAGE_CAPTURE");
        intent.addCategory("android.intent.category.DEFAULT");
        Uri uri = FileUtil.uriFromFile(activity, outputFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        activity.startActivityForResult(intent, requestCode);
    }

}
