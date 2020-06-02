package com.tsinghuabigdata.edu.ddmath.module.ddwork.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/8/8.
 *
 */

public class SolveThinkBean implements Serializable {
    private static final long serialVersionUID = 588522333770174128L;

    private String contentg;
    private String contentLatexGraph;       //latex图片
    private String graph;

    public String getContentg() {
        return contentg;
    }

    public void setContentg(String contentg) {
        this.contentg = contentg;
    }

    public String getContentLatexGraph() {
        return contentLatexGraph;
    }

    public void setContentLatexGraph(String contentLatexGraph) {
        this.contentLatexGraph = contentLatexGraph;
    }

    public String getGraph() {
        return graph;
    }

    public void setGraph(String graph) {
        this.graph = graph;
    }
}
