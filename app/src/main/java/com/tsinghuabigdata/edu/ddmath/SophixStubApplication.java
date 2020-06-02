package com.tsinghuabigdata.edu.ddmath;

import android.content.Context;
import android.util.Log;

import androidx.annotation.Keep;
import androidx.multidex.MultiDex;

import com.taobao.sophix.PatchStatus;
import com.taobao.sophix.SophixApplication;
import com.taobao.sophix.SophixEntry;
import com.taobao.sophix.SophixManager;
import com.taobao.sophix.listener.PatchLoadStatusListener;

/**
 * Sophix入口类，专门用于初始化Sophix，不应包含任何业务逻辑。
 * 此类必须继承自SophixApplication，onCreate方法不需要实现。
 * 此类不应与项目中的其他类有任何互相调用的逻辑，必须完全做到隔离。
 * AndroidManifest中设置application为此类，而SophixEntry中设为原先Application类。
 * 注意原先Application里不需要再重复初始化Sophix，并且需要避免混淆原先Application类。
 * 如有其它自定义改造，请咨询官方后妥善处理。
 */

public class SophixStubApplication  extends SophixApplication {
    private final String TAG = "SophixStubApplication";

    // 此处SophixEntry应指定真正的Application，并且保证RealApplicationStub类名不被混淆。
    @Keep
    @SophixEntry(ZxApplication.class)
    static class RealApplicationStub {}

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        //如果需要使用MultiDex，需要在此处调用。
        MultiDex.install(this);
        initSophix();
    }
    private void initSophix() {

        String IDSECRET = "24471221-1";
        String APPSECRET = "ab96820c492445595565d9c2a3e994dd";
        String RSASECRET = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDRtpkIe5MKW++62OkP4EfSI9GlHqINb3hb7LFvktlMzG469UMGOKI9o3ZO4ELjf1oQz4j2c7u/Dh3XELRa7uNc879worwQIeeRyBK2dX7qXYx/4Fur9uaH9lJPRTuQPTlvkG4X1Duy/XHcnLZGzAZg5AxZmlf2roSgO+X87boC57WYg2SXQqveSi3ukxVys943R86F3stQvn3hGBPZekaRqqBS03JEM/T4P52gsxWoPrmaUGB+LTTcWTx5n6Jk5/wnd9a94a+XvEwi/0gO555+kRkjNRQyfuskB9plKy61eV8e+Pmtn/I3DMfc2781TFK8QI0p4ZgYfGUvkiwBry07AgMBAAECggEAKl0fpvYHqyUNVYpx8lIVUB8KP7fjdqFZCADVizt4CCqX7fMTuhMAu0DlKmPlPz2/vPufqGGSwLKiVoq6VFBDv8hBmQhWw0+GDw4Csbckj0Wcl0yEWEXFJhwdXoQW1zl2+9GKW+f0s2C9eppxu/WFKFeJIySptlxk0FWpTSHc3K8CgpQO+NP3jePmNzpHO5+mmcbLkf/J9Zy/X7hrc2VoUhj5G+nX3/T4iFCE8Su0htyesUt9dfBP5KBuMn04+Tnq6liOD+ODIMbVXacufwZ/kBhD3SvN7eUgxvFWi2+X3HKXOCwH2EoGpUTPNpmogOqNDfP9ivuKEOibMxR2sUcRyQKBgQDqXptI+8ccaBDbgjbjZ8QAgSWD2DY2K+b9KLydRm21IOipXuE1AbYAL6jo4jBsrayHUk/5wieG7P8mZwBaGRGBBeG97spEO2CNLEoPUTETzJtKwV8W66uPzYX6bMpCxBsdwVllOM7EzT2N4fJY6hSyBIhh46gH3EORt8bl9r/M/wKBgQDlEXFtXZYy+gPLrpRDaLkFxOFaHPRJeKJE0zNk3Y56cHFOjQRwd6vVPVzYaEF2UyF9o3EZ19BGQI9rqLTVgRfu7yqJpmVOxn+1rDFNlQdM3Sl0obFETntcfyufkE4jzlwdigkA/NWvI2U2eOS8xrhuS/5BYHab2Ran44uVIqCTxQKBgDcuCxWF4f04Ky/jWpTz61vLDFAcabcbYwkbvDyzRkUs+FpYMmHOg1FL51LdXlVYFpTNtnKxWakUx4X9HkIYQNzFy75yKEEkUbkrQkyU6x8kPzO8j2zQE9aJpw+s6XoKve+lhCym0VfYkrODDRIOGs1G7+1MMlWXcHkwca3XCJV1AoGBAKCVDNYlcGEGIdkM5sUmyeZAcdZRS/OhjOgAwxunsfUllFkDxKPvUNepL8hJtfJ0Lps8E9KRF2HYanKHj1XJLPUEVuauboC96NN8BggkPDznIeOwYmvHNPXP9M3kRNMXHD5dARjfJhGkF5ULRRcNnEc+INzF+gAhPJ+vha+j2bwdAoGBALUAB8BGGYwcftfMOIQItx4Sl/9Cj5DaYGCdgnu+VMlm6jW79NrGxSW31mJUe6B6TPfJuMeOFJ5JQo4fdq81EPP5NRVwW1sSvWPJBmoA1uuIQKkTqiYwdZpIrrx08gLIuze1WW3I2KlvuOIAaqk6NshK5dHlqqiz75jhcodebXSp";

        String appVersion = "0.0.0";
        try {
            appVersion = this.getPackageManager()
                    .getPackageInfo(this.getPackageName(), 0)
                    .versionName;
        } catch (Exception e) {
            Log.i(TAG, "sophix get app version fail!");
        }
        final SophixManager instance = SophixManager.getInstance();
        instance.setContext(this)
                .setAppVersion(appVersion)
                .setSecretMetaData(IDSECRET, APPSECRET, RSASECRET)
                .setEnableDebug(true)
                .setEnableFullLog()
                .setPatchLoadStatusStub(new PatchLoadStatusListener() {
                    @Override
                    public void onLoad(final int mode, final int code, final String info, final int handlePatchVersion) {
                        Log.i(TAG, "sophix onload mode = "+mode + " code = " +code + "  info = " + info + "  handlePatchVersion = " + handlePatchVersion);
                        if (code == PatchStatus.CODE_LOAD_SUCCESS) {
                            Log.i(TAG, "sophix load patch success!");
                        } else if (code == PatchStatus.CODE_LOAD_RELAUNCH) {
                            // 如果需要在后台重启，建议此处用SharePreference保存状态。
                            Log.i(TAG, "sophix preload patch success. restart app to make effect.");
                        }
                    }
                }).initialize();
    }
}

