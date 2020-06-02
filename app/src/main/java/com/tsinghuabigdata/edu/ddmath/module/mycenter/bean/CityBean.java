package com.tsinghuabigdata.edu.ddmath.module.mycenter.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/1/17.
 */

public class CityBean implements Serializable{


    private static final long serialVersionUID = 4862505237227250616L;
    String cityCode;
    String cityName;

    public String getCityCode() {
        return cityCode;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

}
