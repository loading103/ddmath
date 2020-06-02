package com.tsinghuabigdata.edu.ddmath.module.mycenter.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2018/1/17.
 *
 */

public class AreaBean implements Serializable{


    private static final long serialVersionUID = 2824534215651828370L;
    String provinceCode;

    String provinceName;

    List<CityBean> cityVoList;

    public String getProvinceCode() {
        return provinceCode;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public List<CityBean> getCityVoList() {
        return cityVoList;
    }

    public void setProvinceCode(String provinceCode) {
        this.provinceCode = provinceCode;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public void setCityVoList(List<CityBean> cityVoList) {
        this.cityVoList = cityVoList;
    }
}
