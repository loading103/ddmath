package com.tsinghuabigdata.edu.ddmath.module.mylearn.bean;

import java.io.Serializable;

/**
 * 图像大小
 */
public class ImageSizeBean implements Serializable{
    private static final long serialVersionUID = 8118454349135778444L;

    private double width;
    private double height;

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }
//    @Override
//    public String toString() {
//        StringBuilder builder = new StringBuilder();
//        builder.append("ImageSizeBean [width=").append(width).append(", height=").append(height).append("]");
//        return builder.toString();
//    }
//    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
//        out.writeDouble(width);
//        out.writeDouble(height);
//    }
//    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException{
//        width = in.readDouble();
//        height = in.readDouble();
//    }
}
