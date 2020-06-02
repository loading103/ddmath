package com.tsinghuabigdata.edu.ddmath.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/4/21.
 */
public class StdAnswerBean implements Serializable {

    /**
     * content : B
     * contentLatexGraph :
     * contentg : B
     * graph :
     * subStemNum : 0
     * wayOfNum : 0
     */


    /*{
        "content": "A<img>",
            "contentLatextGraph": null,
            "contentg": null,
            "graph": "group2/M00/00/42/rBAFA1iao9yIMD5kAAAMaWO5l38AAAAAwM8cOsAAAyB262.jpg",
            "subStemNum": 1,
            "wayOfNum": 0
    }*/

    private String content;
    private String contentLatexGraph;
    private String contentLatextGraph;
    private String contentg;
    private String graph;
    private int    subStemNum;
    private int    wayOfNum;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContentLatextGraph() {
        return contentLatexGraph!=null?contentLatexGraph:contentLatextGraph;
    }

    public void setContentLatextGraph(String contentLatextGraph) {
        this.contentLatexGraph = contentLatextGraph;
    }

    public String getContentg() {
        return contentg!=null?contentg:content;
    }

    public void setContentg(String contentg) {
        this.contentg = contentg;
    }

    public String getGraph() {
        return graph;
    }

    public void setGraph(String graph) {
        this.graph = graph;
    }

    public int getSubStemNum() {
        return subStemNum;
    }

    public void setSubStemNum(int subStemNum) {
        this.subStemNum = subStemNum;
    }

    public int getWayOfNum() {
        return wayOfNum;
    }

    public void setWayOfNum(int wayOfNum) {
        this.wayOfNum = wayOfNum;
    }


}
