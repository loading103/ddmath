package com.tsinghuabigdata.engine.config;

/**
 * constants for tf loader
 * Created by ZhangXiao on 2018/7/26 16:27.
 */
public class TFLoaderConstants {
    private static String MAINMODELPath = "./src/main/resources/TFmodel/";
    public static final String TAGS_SERVE = "serve";
    public static final String TAGS_TEST = "TEST";
    public static final String TAGS_TRAIN = "TRAIN";
    public static final String CNNCHOICENEGATIVE = "choice_negative";
    private static final String CNNCHARINDEX = "idx.index";
    private static final String OBJDETECTINDEX = "classes_name.index";
    public static final String OBJDETECT = "object_detect_restnet101";
    public static final String PRINTSEGMENT = "print_segment";
    public static final String CHOICEANSWER = "choice_segment";
    public static final String CHOICEANSWERSEGREC = "choice_segment_rec";
    public static final String CHOICEANSWERSEGRECMOBILENET = "choice_segment_rec_mobilenet";
    public static int MAXCNNOUTSIZE = 3;
    public static final float OBJDETECTMINSCORE = 0.50f;
    public static String[] initEngineList = {
            CNNCHOICENEGATIVE, OBJDETECT
    };

    public static String[] OBJDETECTKEYS = {
            "num_detections", "detection_boxes", "detection_scores",
            "detection_classes", "detection_masks"};

    public static String getTFModelDir(String whatModel) {
        return MAINMODELPath + "/" + whatModel;
    }

    public static String getTFCNNIndex(String whatModel) {
        return getTFModelDir(whatModel) + "/" + CNNCHARINDEX;
    }

    public static String getTFObjDetectIndex(String whatModel) {
        return getTFModelDir(whatModel) + "/" + OBJDETECTINDEX;
    }
}
