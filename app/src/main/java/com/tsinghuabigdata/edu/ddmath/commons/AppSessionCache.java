package com.tsinghuabigdata.edu.ddmath.commons;

import com.jakewharton.disklrucache.DiskLruCache;
import com.tsinghuabigdata.edu.commons.codec.MD5Utils;
import com.tsinghuabigdata.edu.commons.io.SerializableUtils;
import com.tsinghuabigdata.edu.session.AppSession;
import com.tsinghuabigdata.edu.session.Session;
import com.tsinghuabigdata.edu.ddmath.util.AppLog;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;

/**
 * <p>
 * Created by yanshen@tsinghuabigdata.com on 2015/11/24.
 * </p>
 *
 * @author yanshen@tsinghuabigdata.com
 * @version V1.0
 * @packageName: com.tsinghuabigdata.edu.ddmath.commons
 * @createTime: 2015/11/24 16:13
 */
public class AppSessionCache implements Session{

    private DiskLruCache cache;
    private Session session = AppSession.getInstance();

    private volatile static AppSessionCache instance;

    /**
     * 得到实例对象
     * @return
     */
    public static AppSessionCache getInstance(){
        return instance;
    }

    /**
     * 防止重复初始化
     */
    private AppSessionCache() {
        if (instance != null) {
            throw new RuntimeException("不能重复初始化");
        }
        instance = this;
    }

    /**
     * 初始化
     * @param cacheDir
     * @param appVersion
     * @throws IOException
     */
    public static void init(File cacheDir, int appVersion) throws IOException {
        instance = new AppSessionCache(cacheDir, appVersion);
    }

    public AppSessionCache(File cacheDir, int appVersion) throws IOException {
        cache = DiskLruCache.open(cacheDir, appVersion, 1, 1024 * 1024 * 128);
    }

    @Override
    public void put(int key, Object value) {
        if(value == null) return;

        // save to memory
        session.put(key, value);
        // save to local dir
        putLocalCache(key, value);
    }

    /**
     * 保存到本地
     * @param key
     * @param value
     */
    private void putLocalCache(int key, Object value) {
        if(value instanceof Serializable){
            String md5Key = MD5Utils.stringToMD5("" + key);
            try {
                if(cache.get(md5Key) != null){
                    cache.remove(md5Key);
                }
                DiskLruCache.Editor editor = cache.edit(md5Key);
                if (putStream((Serializable) value, editor)){
                    editor.commit();
                } else {
                    editor.abort();
                }
            } catch (Exception e) {
                AppLog.i("save to local dir", e);
            }
        }
    }

    private boolean putStream(Serializable value, DiskLruCache.Editor editor) {
        OutputStream outputStream = null;
        try {
            outputStream = editor.newOutputStream(0);
            outputStream.write(SerializableUtils.serializer(value));
            return true;
        } catch (IOException e) {
            AppLog.i("save to local dir", e);
            return false;
        } catch (Exception e) {
            AppLog.i("save to local dir", e);
            return false;
        } finally {
            if(outputStream != null) {
                try {
                    outputStream.flush();
                    outputStream.close();
                } catch (IOException e) {
                    AppLog.i("save to local dir", e);
                }
            }
        }
    }

    @Override
    public <T> T get(int key) {
        T value = session.get(key);
        if(value == null){
            value = (T) getLocalCache(key);
            if(value != null){
                session.put(key, value);
            }
        }
        return value;
    }

    /**
     * 从本地获取
     * @param key
     * @return
     */
    private Serializable getLocalCache(int key) {
        String md5Key = MD5Utils.stringToMD5(key + "");
        try {
            if (cache.get(md5Key) != null) {
                // 从DiskLruCahce取
                DiskLruCache.Snapshot snapshot = cache.get(md5Key);
                Serializable serializable = null;
                if (snapshot != null) {
                    serializable = SerializableUtils.unSerializer(IOUtils.toByteArray(snapshot.getInputStream(0)));
                }
                return serializable;
            }
        } catch (IOException e) {
            AppLog.i("get to local dir", e);
        } catch (Exception e) {
            AppLog.i("get to local dir", e);
        }
        return null;
    }

    @Override
    public void remove(int key) {
        session.remove(key);
        try {
            cache.remove(MD5Utils.stringToMD5(key + ""));
        } catch (IOException e) {
            AppLog.i("remove to local dir", e);
        }
    }

    @Override
    public boolean has(int key) {
        return get(key) != null;
    }
}
