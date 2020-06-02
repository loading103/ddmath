package com.tsinghuabigdata.edu.ddmath.module.robotqa;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 28205 on 2016/10/21.
 */
public class RobotQaTaskManager {
    private static RobotQaTaskManager instance = new RobotQaTaskManager();

    private RobotQaTaskManager() {

    }

    public static RobotQaTaskManager getInstance() {
        return instance;
    }

    private List<Runnable> tasks = new ArrayList<>();

    public void add(Runnable thread) {
        tasks.add(thread);
    }

    public void pop(){
        if (tasks.size() > 0){
            tasks.remove(tasks.size()-1);
        }
    }

    public void doAgain(){
        if (tasks.size() > 0){
            new Thread(tasks.get(tasks.size()-1)).start();
        }
    }
}
