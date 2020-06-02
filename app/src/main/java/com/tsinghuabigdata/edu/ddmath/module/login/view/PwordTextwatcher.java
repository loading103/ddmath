package com.tsinghuabigdata.edu.ddmath.module.login.view;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.PatternSyntaxException;

/**
 * 密码输入监听
 */
public class PwordTextwatcher implements TextWatcher {

    private EditText etPasswd;
    private Button btRegister;

    public PwordTextwatcher( EditText editText, Button btn ){
        etPasswd = editText;
        btRegister = btn;
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        //cou = before + count;
        String editable = etPasswd.getText().toString();
        String str = stringFilter(editable); //过滤特殊字符
        if (!editable.equals(str)) {
            etPasswd.setText(str);
        }
        etPasswd.setSelection(etPasswd.length());
        //cou = etPasswd.length();
    }
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
                                  int after) {
    }
    @Override
    public void afterTextChanged(Editable s) {
        if( TextUtils.isEmpty(etPasswd.getText().toString().trim() ) ) {
            if(btRegister!=null)btRegister.setEnabled(false);
        } else {
            if(btRegister!=null)btRegister.setEnabled(true);
        }
    }

    //int mMaxLenth = 200;//设置允许输入的字符长度
    public static String stringFilter(String str) throws PatternSyntaxException {

        String regEx = "[/\\:*?<>|\" \n\t]";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("");
    }


}