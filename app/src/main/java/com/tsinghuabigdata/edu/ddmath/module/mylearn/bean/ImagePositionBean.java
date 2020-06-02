package com.tsinghuabigdata.edu.ddmath.module.mylearn.bean;

import java.io.Serializable;

/**
 * 图像 坐标信息
 */
public class ImagePositionBean implements Serializable{

    private static final long serialVersionUID = -3078760333591932772L;

    private double x;
    private double y;

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

//    @Override
//    public String toString() {
//        StringBuilder builder = new StringBuilder();
//        builder.append("ImagePositionBean [x=").append(x).append(", y=").append(y).append("]");
//        return builder.toString();
//    }
//    private void writeObject(java.io.ObjectOutputStream out) throws IOException{
//        out.writeDouble(x);
//        out.writeDouble(y);
//    }
//    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException{
//        x = in.readDouble();
//        y = in.readDouble();
//    }
}
