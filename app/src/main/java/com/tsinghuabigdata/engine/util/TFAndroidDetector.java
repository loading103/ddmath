package com.tsinghuabigdata.engine.util;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.util.Log;

import com.tsinghuabigdata.engine.config.TFAndroidConstants;
import com.tsinghuabigdata.engine.model.ObjectDetectAndroidModel;
import com.tsinghuabigdata.engine.util.env.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * engine interface for 3rd user
 * Created by ZhangXiao on 2018/8/30 16:23.
 */
public class TFAndroidDetector {
    private TFProvider detector;
    private static final Logger LOGGER = new Logger();
    private boolean isInit = false;
    private int cropSize = TFAndroidConstants.TF_OD_API_INPUT_SIZE_CHOICESEGREC;
    private static TFAndroidDetector tfAndroidDetector = null;

    private TFAndroidDetector(AssetManager assetManager,TFAndroidConstants.DetectType type){
            this.isInit = initEngine(assetManager,type);
    }
    public boolean isInit(){
        return this.isInit;
    }

    private boolean initEngine(AssetManager assetManager,TFAndroidConstants.DetectType type){
        try {
            switch (type) {
                case judge_symbol_detect_rec:
                    detector = ObjectDetectAndroidModel.create(assetManager, TFAndroidConstants.TF_OD_API_MODEL_FILE_JUDGESYMBOLDETECT, TFAndroidConstants.TF_OD_API_LABELS_FILE_JUDGESYMBOLDETECT);
                    break;
                default:
                    detector = ObjectDetectAndroidModel.create(assetManager, TFAndroidConstants.TF_OD_API_MODEL_FILE_CHOICESEGREC, TFAndroidConstants.TF_OD_API_LABELS_FILE_CHOICESEGREC);
                break;
            }
            return true;
        } catch (final IOException e) {
            LOGGER.e("Exception initializing classifier!", e);
            return false;
        }
    }

    public static TFAndroidDetector getInstance(AssetManager assetManager, TFAndroidConstants.DetectType type){
        if(tfAndroidDetector == null)
            tfAndroidDetector = new TFAndroidDetector(assetManager,type);
        if(tfAndroidDetector.isInit)
            return tfAndroidDetector;
        else
            return null;
    }

    public Bitmap resizeBitmap(Bitmap bitmap) {
        if(bitmap.getWidth() > TFAndroidConstants.MaxJudgeSymbolRecPicWidth){
            Matrix matrix = new Matrix();
            float ratio = (float) TFAndroidConstants.MaxJudgeSymbolRecPicWidth / bitmap.getWidth();
            matrix.postScale(ratio,ratio);//等比缩放
            Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                    bitmap.getHeight(), matrix, true);
            return newBitmap;
        }
        else {
            return bitmap;
        }
    }

    public List<TFProvider.Recognition> recBitmap(Bitmap bitmap){
        if(bitmap == null){
            return new ArrayList<>();
        }
        Bitmap recImg = Bitmap.createBitmap(bitmap);
        if(bitmap.getWidth() > TFAndroidConstants.MaxJudgeSymbolRecPicWidth){
            recImg = resizeBitmap(bitmap);
        }
        final List<TFProvider.Recognition> mappedRecognitions =
                new LinkedList<TFProvider.Recognition>();
        List<TFProvider.Recognition> results = this.detector.recognizeImage(recImg);
        Log.d("dfddf","size = " + results.size() );
        for(TFProvider.Recognition result : results) {
            final RectF location = result.getLocation();
            Log.d("dfddf","location = " + location );
            Log.d("dfddf","score = " + result.getConfidence() );
            if (location != null && result.getConfidence() >= TFAndroidConstants.MINIMUM_CONFIDENCE_TF_OD_API_UDGESYMBOLDETECT) {
                //归一化到 0-1范围内
                location.left = location.left / recImg.getWidth();
                location.right= location.right / recImg.getWidth();
                location.top  = location.top / recImg.getHeight();
                location.bottom= location.bottom / recImg.getHeight();
                result.setLocation( location );
                mappedRecognitions.add(result);
            }
        }
        return mappedRecognitions;
    }
}
