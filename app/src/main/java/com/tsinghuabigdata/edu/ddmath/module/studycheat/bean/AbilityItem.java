package com.tsinghuabigdata.edu.ddmath.module.studycheat.bean;

import java.io.Serializable;

/**
 * 能力值变化
 */
public class AbilityItem implements Serializable {
    private static final long serialVersionUID = 1432214634218012560L;

//    increase	float	提升
//    value	float	值

    float increase;
    float value;

    public float getIncrease() {
        return increase;
    }

    public void setIncrease(float increase) {
        this.increase = increase;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }
}
