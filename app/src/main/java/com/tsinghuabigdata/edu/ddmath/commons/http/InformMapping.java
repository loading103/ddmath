package com.tsinghuabigdata.edu.ddmath.commons.http;

import android.content.Context;

import com.tsinghuabigdata.edu.ddmath.util.AppLog;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

/**
 * <p>
 * Created by yanshen@tsinghuabigdata.com on 2015/11/21.
 * </p>
 *
 * @author yanshen@tsinghuabigdata.com
 * @version V1.0
 * @packageName: com.tsinghuabigdata.edu.ddmath
 * @createTime: 2015/11/21 12:08
 */
public class InformMapping {

    private Properties properties;

    private static InformMapping instance;

    public static void initialization(Context context) {
        InputStream inputStream = null;
        try {
            inputStream = context.getAssets().open("config/inform_mapping.properties");
            setInstance(new InformMapping(inputStream));
        } catch (IOException e) {
            AppLog.i("err", e);
        } finally {
            try {
                if (inputStream != null)
                    inputStream.close();
            } catch (IOException e) {
                AppLog.i("err", e);
            }
        }
    }

    public static InformMapping getInstance() {
        return instance;
    }

    private static void setInstance(InformMapping instance) {
        InformMapping.instance = instance;
    }

    private InformMapping(InputStream inputStream) throws IOException {
        properties = new Properties();
        properties.load(inputStream);
    }

    public String get(String key) {
        if (key == null) {
            return "未知的映射";
        }
        key = key.toLowerCase();
        String value = key;
        if (properties.containsKey(key)) {
            value = getUtf8String((String) properties.get(key));
        }
        return value;
    }

    public boolean containsKey(String key) {
        if (key == null) return false;
        key = key.toLowerCase();
        return properties.containsKey(key);
    }

    private String getUtf8String(String value) {
        try {
            value = new String(value.getBytes("ISO-8859-1"), "UTF-8");//这一句是重点
        } catch (UnsupportedEncodingException e) {
            AppLog.i("err", e);
            value = null;
        }
        return value;
    }
}
