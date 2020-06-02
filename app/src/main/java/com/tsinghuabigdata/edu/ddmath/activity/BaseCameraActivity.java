package com.tsinghuabigdata.edu.ddmath.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.tsinghuabigdata.edu.ddmath.util.AlertManager;
import com.tsinghuabigdata.edu.ddmath.util.AppLog;

import java.util.ArrayList;
import java.util.Iterator;


public class BaseCameraActivity extends BaseActivity{

    //一个整形常量
    public static final int MY_PERMISSIONS_REQUEST = 3000;
    //定义一个list，用于存储需要申请的权限
    private ArrayList<String> permissionList;

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = this;

        //
        permissionList = new ArrayList<>();
        permissionList.add(Manifest.permission.CAMERA);
        permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        checkAndRequestPermissions( permissionList );
    }

    //不管权限申请成功与否，都会调用该方法
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST: {

                int length = grantResults.length;
                boolean re_request = false;//标记位：如果需要重新授予权限，true；反之，false。
                for (int i = 0; i < length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        AppLog.d( "权限授予成功:" + permissions[i]);
                    } else {
                        AppLog.d( "权限授予失败:" + permissions[i]);
                        re_request = true;
                    }
                }
                if (re_request) {
                    //弹出对话框，提示用户重新授予权限
                    //关于弹出自定义对话框，可以查看本博文开头的知识扩展

                    AlertManager.showCustomDialog( mContext, "请授予相关权限，否则程序无法运行。\n\n点击确定，重新授予权限。\n点击取消，立即终止程序。\n",
                            "确定", "取消",
                            new DialogInterface.OnClickListener(){
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //此处需要弹出手动修改权限的系统界面
                                    checkAndRequestPermissions(permissionList);
                                }
                            }, new DialogInterface.OnClickListener(){
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            });


//                    final YesOrNoDialog  permissionDialog = new YesOrNoDialog (mContext);
//                    permissionDialog.setCanceledOnTouchOutside(false);
//                    permissionDialog.set("请授予相关权限，否则程序无法运行。\n\n点击确定，重新授予权限。\n点击取消，立即终止程序。\n");
//                    permissionDialog.setCallback(new YesOrNoDialog.YesOrNoDialogCallback() {
//                        @Override
//                        public void onClickButton(YesOrNoDialog.ClickedButton button, String message) {
//                            if (button == YesOrNoDialog.ClickedButton.POSITIVE) {
//                                permissionDialog.dismiss();
//                                //此处需要弹出手动修改权限的系统界面
//                                checkAndRequestPermissions(permissionList);
//                            } else if (button == YesOrNoDialog.ClickedButton.NEGATIVE) {
//                                permissionDialog.dismiss();
//                                finish();
//                            }
//                        }
//                    });
//                    permissionDialog.show();




                }
                break;
            }
            default:
                break;
        }
    }

    //------------------------------------------------------------------------------------
    //调用封装好的申请权限的方法
    private void checkAndRequestPermissions(ArrayList<String> permissionList) {

        AppLog.d("fdsfdsf 0000");
        ArrayList<String> list = new ArrayList<>(permissionList);
        Iterator<String> it = list.iterator();
        while (it.hasNext()) {
            String permission = it.next();
            //检查权限是否已经申请
            int hasPermission = ContextCompat.checkSelfPermission(this, permission);
            if (hasPermission == PackageManager.PERMISSION_GRANTED) {
                it.remove();
            }
        }
        /**
         *补充说明：ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO);
         *对于原生Android，如果用户选择了“不再提示”，那么shouldShowRequestPermissionRationale就会为true。
         *此时，用户可以弹出一个对话框，向用户解释为什么需要这项权限。
         *对于一些深度定制的系统，如果用户选择了“不再提示”，那么shouldShowRequestPermissionRationale永远为false
         *
         */

        AppLog.d("fdsfdsf 1111");
        if (list.size() == 0) {
            return;
        }

        AppLog.d("fdsfdsf 2222");
        String[] permissions = list.toArray(new String[0]);
        //正式请求权限
        ActivityCompat.requestPermissions(this, permissions, MY_PERMISSIONS_REQUEST);

    }

}
