package com.tsinghuabigdata.edu.ddmath.module.onekeydiagnose;

import com.tsinghuabigdata.edu.ddmath.module.onekeydiagnose.bean.LevelKnowledgesBean;
import com.tsinghuabigdata.edu.ddmath.module.onekeydiagnose.bean.MainKnowledgesBean;
import com.tsinghuabigdata.edu.ddmath.module.onekeydiagnose.bean.SubKnowledgesBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/8/16.
 */

public class KnowledgeUtils {


    public static List<SubKnowledgesBean> getSelectedlist(List<SubKnowledgesBean> list) {
        List<SubKnowledgesBean> selectedlist = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            SubKnowledgesBean bean = list.get(i);
            if (bean.isSelect()) {
                selectedlist.add(bean);
            }
        }
        return selectedlist;
    }


    public static ArrayList<String> getKnowledges(List<SubKnowledgesBean> selectedlist) {
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < selectedlist.size(); i++) {
            SubKnowledgesBean bean = selectedlist.get(i);
            list.add(bean.getKnowledgePointId());
        }
        return list;
    }

    //是否包含错题
    public static boolean hasWrong(List<SubKnowledgesBean> selectedlist) {
        for (int i = 0; i < selectedlist.size(); i++) {
            SubKnowledgesBean bean = selectedlist.get(i);
            if (bean.getWrongCount() > 0) {
                return true;
            }
        }
        return false;
    }


    public static List<SubKnowledgesBean> getSubKnowledges(LevelKnowledgesBean levelKnowledgesBean) {
        List<SubKnowledgesBean> list = new ArrayList<>();
        if (levelKnowledgesBean == null) {
            return list;
        }
        List<MainKnowledgesBean> mainKnowledges = levelKnowledgesBean.getKnowledges();
        if (mainKnowledges == null || mainKnowledges.size() == 0 || mainKnowledges.get(0) == null) {
            return list;
        }
        List<SubKnowledgesBean> subKnowledges = mainKnowledges.get(0).getSubKnowledges();
        if (subKnowledges == null || subKnowledges.size() == 0) {
            return list;
        }
        return subKnowledges;
    }

}
