package com.tsinghuabigdata.edu.ddmath.event;

/**
 * Created by Administrator on 2018/4/26.
 */

public class MyWorkEvent {

    private boolean schoolWork;
    private boolean selfWork;

    public MyWorkEvent(boolean hasWork) {
        this.schoolWork = hasWork;
    }
    public MyWorkEvent(boolean hasWork, boolean work) {
        this.schoolWork = hasWork;
        this.selfWork = work;
    }

    public void combine( MyWorkEvent event ){
        schoolWork = schoolWork || event.schoolWork;
        selfWork   = selfWork || event.selfWork;
    }

    public boolean hasSchoolWork() {
        return schoolWork;
    }
    public boolean hasSelfWork(){ return selfWork; }

//    public void setHasWork(boolean hasWork) {
//        this.hasWork = hasWork;
//    }

}
