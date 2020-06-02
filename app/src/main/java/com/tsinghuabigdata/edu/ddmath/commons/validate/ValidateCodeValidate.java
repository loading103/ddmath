package com.tsinghuabigdata.edu.ddmath.commons.validate;

/*
 * 验证码
 */

import android.text.TextUtils;
import android.util.Pair;
import android.view.View;

import com.mobsandgeeks.saripaar.ContextualAnnotationRule;
import com.mobsandgeeks.saripaar.Rule;
import com.mobsandgeeks.saripaar.ValidationContext;
import com.mobsandgeeks.saripaar.adapter.ViewDataAdapter;
import com.mobsandgeeks.saripaar.exception.ConversionException;
import com.tsinghuabigdata.edu.ddmath.util.AppLog;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

/**
 * 验证码验证
 */
class ValidateCodeValidate extends ContextualAnnotationRule<MobileValidateCode, String> {

    protected ValidateCodeValidate(ValidationContext validationContext, MobileValidateCode mobileValidateCode) {
        super(validationContext, mobileValidateCode);
    }

    @Override
    public boolean isValid(String o) {
        try {
            if (TextUtils.isEmpty(o)) {
                return false;
            }
            Field field = mValidationContext.getClass().getDeclaredField("mViewRulesMap");
            field.setAccessible(true);
            Map<View, ArrayList<Pair<Rule, ViewDataAdapter>>> mViewRulesMap =
                    (Map<View, ArrayList<Pair<Rule, ViewDataAdapter>>>) field.get(mValidationContext);
            Set<View> views = mViewRulesMap.keySet();
            for (View view : views) {
                Object data = mViewRulesMap.get(view).get(0).second.getData(view);
                if (data==null) {
                    return true;
                }
            }
        } catch (NoSuchFieldException e) {
            AppLog.i("err", e);
        } catch (IllegalAccessException e) {
            AppLog.i("err", e);
        } catch (ConversionException e) {
            AppLog.i("err", e);
        }
        return o.equals("123456");
    }
}
