package com.tsinghuabigdata.engine.config;

/**
 * configure for rec engine
 * Created by ZhangXiao on 2018/8/30 16:23.
 */
public class TFAndroidConstants {
    /**
     * choice seg with rec
     */
    public static final String TF_OD_API_MODEL_FILE_CHOICESEGREC = "file:///android_asset/TFmodel/choice_segment_rec_mobile.pb";
    public static final String TF_OD_API_LABELS_FILE_CHOICESEGREC = "file:///android_asset/TFmodel/choice_segment_rec_mobile.index";
    public static final String TF_OD_API_MODEL_FILE_JUDGESYMBOLDETECT = "file:///android_asset/TFmodel/answer_judge_symbol.pb";
    public static final String TF_OD_API_LABELS_FILE_JUDGESYMBOLDETECT = "file:///android_asset/TFmodel/answer_judge_symbol.index";
    public static final int TF_OD_API_INPUT_SIZE_CHOICESEGREC = 300;
    public static final float MINIMUM_CONFIDENCE_TF_OD_API_UDGESYMBOLDETECT = 0.4f;

    public static final int MaxJudgeSymbolRecPicWidth = 800;

    public static enum DetectType {
        choice_detect_rec,//自动提取选择题区域并识别
        judge_symbol_detect_rec,//自动提取勾叉与识别
        filling_detect_rec,//填空题提取与识别
        layout_detect_rec;//版面识别
    }


}
