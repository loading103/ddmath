package com.tsinghuabigdata.edu.ddmath.bean;

import java.io.Serializable;

/**
 * Created by 28205 on 2016/10/20.
 */
public class QAQuestion implements Serializable {
    private static final long serialVersionUID = 5188426935714293963L;

    private String questionThink;

    private String stdAnswers;

    private String stem;

    private String stemGraph;

    public void setQuestionThink(String questionThink) {
        this.questionThink = questionThink;
    }

    public String getQuestionThink() {
        return this.questionThink;
    }

    public void setStdAnswers(String stdAnswers) {
        this.stdAnswers = stdAnswers;
    }

    public String getStdAnswers() {
        return this.stdAnswers;
    }

    public void setStem(String stem) {
        this.stem = stem;
    }

    public String getStem() {
        return this.stem;
    }

    public void setStemGraph(String stemGraph) {
        this.stemGraph = stemGraph;
    }

    public String getStemGraph() {
        return this.stemGraph;
    }
}
