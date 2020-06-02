package com.tsinghuabigdata.edu.ddmath.bean;

import java.io.Serializable;

public class School implements Serializable {
    private static final long serialVersionUID = 1668865344932190531L;
    /**
     * "activeCount":0,"aliases":"代理商演示学校01",
     * "areaCode":"510109",
     * "areaId":"510109",
     * "city":"成都市",
     * "county":"高新区",
     * "createTime":1456910608000,
     * "feedbackTime":48,"feedbackTimeUnit":"hour","lastModifyTime":1472093025000,
     * "name":"代理商演示学校01",
     * "parentId":null,
     * "province":"四川省","schoolDate":null,"schoolId":"FE66147EFD2941008AF0A28F892BE0C4","schoolMasterId":null,"schoolNum":"510109017
     */
    private String schoolId;
    private String name;
    private String city;
    private String county;
    private String province;

    public String getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(String schoolId) {
        this.schoolId = schoolId;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }
}
