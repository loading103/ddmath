package com.tsinghuabigdata.engine.model;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.os.Trace;

import com.tsinghuabigdata.engine.util.TFProvider;
import com.tsinghuabigdata.engine.util.env.Logger;

import org.tensorflow.Graph;
import org.tensorflow.Operation;
import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Vector;

/**
 * Created by ZhangXiao on 2018/8/30
 */
public class ObjectDetectAndroidModel implements TFProvider {
    private static final Logger LOGGER = new Logger();
    // Only return this many results.
    private static final int MAX_RESULTS = 100;
    // Config values.
    private String inputName = "image_tensor";

    //root file path
    private static final String modelRootPath = "file:///android_asset/";

    // Pre-allocated buffers.
    private Vector<String> labels = new Vector<String>();

    private String[] outputNames = new String[] {"detection_boxes", "detection_scores",
            "detection_classes", "num_detections"};;

    private boolean logStats = false;

    private TensorFlowInferenceInterface inferenceInterface;

    /**
     * Initializes a native TensorFlow session for classifying images.
     *
     * @param assetManager The asset manager to be used to load assets.
     * @param modelFilename The filepath of the model GraphDef protocol buffer.
     * @param labelFilename The filepath of label file for classes.
     */
    public static TFProvider create(
            final AssetManager assetManager,
            final String modelFilename,
            final String labelFilename) throws IOException {
        final ObjectDetectAndroidModel d = new ObjectDetectAndroidModel();

        InputStream labelsInput = null;
        String actualFilename = labelFilename.split(modelRootPath)[1];
        labelsInput = assetManager.open(actualFilename);
        BufferedReader br = null;
        br = new BufferedReader(new InputStreamReader(labelsInput));
        String line;
        while ((line = br.readLine()) != null) {
            LOGGER.w(line);
            d.labels.add(line);
        }
        br.close();


        d.inferenceInterface = new TensorFlowInferenceInterface(assetManager, modelFilename);

        final Graph g = d.inferenceInterface.graph();

        // The inputName node has a shape of [N, H, W, C], where
        // N is the batch size
        // H = W are the height and width
        // C is the number of channels (3 for our purposes - RGB)
        final Operation inputOp = g.operation(d.inputName);
        if (inputOp == null) {
            throw new RuntimeException("Failed to find input Node '" + d.inputName + "'");
        }
        // The outputScoresName node has a shape of [N, NumLocations], where N
        // is the batch size.
        final Operation outputOp1 = g.operation("detection_scores");
        if (outputOp1 == null) {
            throw new RuntimeException("Failed to find output Node 'detection_scores'");
        }
        final Operation outputOp2 = g.operation("detection_boxes");
        if (outputOp2 == null) {
            throw new RuntimeException("Failed to find output Node 'detection_boxes'");
        }
        final Operation outputOp3 = g.operation("detection_classes");
        if (outputOp3 == null) {
            throw new RuntimeException("Failed to find output Node 'detection_classes'");
        }
        return d;
    }

    private ObjectDetectAndroidModel() {}

    @Override
    public List<TFProvider.Recognition> recognizeImage(final Bitmap bitmap) {
        if(bitmap == null || bitmap.getWidth() <= 0 || bitmap.getHeight() <= 0){
            return new ArrayList<>();
        }
        // Pre-allocate buffers.
        int[] intValues = new int[bitmap.getHeight() * bitmap.getWidth()];
        byte[] byteValues = new byte[bitmap.getHeight() * bitmap.getWidth() * 3];


        // Log this method so that it can be analyzed with systrace.
        Trace.beginSection("recognizeImage");

        Trace.beginSection("preprocessBitmap");
        // Preprocess the image data from 0-255 int to normalized float based
        // on the provided parameters.
        bitmap.getPixels(intValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

        for (int i = 0; i < intValues.length; ++i) {
            byteValues[i * 3 + 2] = (byte) (intValues[i] & 0xFF);
            byteValues[i * 3 + 1] = (byte) ((intValues[i] >> 8) & 0xFF);
            byteValues[i * 3 + 0] = (byte) ((intValues[i] >> 16) & 0xFF);
        }
        Trace.endSection(); // preprocessBitmap

        // Copy the input data into TensorFlow.
        Trace.beginSection("feed");
        inferenceInterface.feed(inputName, byteValues, 1, bitmap.getHeight(), bitmap.getWidth(), 3);
        Trace.endSection();

        // Run the inference call.
        Trace.beginSection("run");
        inferenceInterface.run(outputNames, logStats);
        Trace.endSection();

        // Copy the output Tensor back into the output array.
        Trace.beginSection("fetch");
        float[] outputLocations = new float[MAX_RESULTS * 4];
        float[] outputScores = new float[MAX_RESULTS];
        float[] outputClasses = new float[MAX_RESULTS];
        float[] outputNumDetections = new float[1];
        inferenceInterface.fetch(outputNames[0], outputLocations);
        inferenceInterface.fetch(outputNames[1], outputScores);
        inferenceInterface.fetch(outputNames[2], outputClasses);
        inferenceInterface.fetch(outputNames[3], outputNumDetections);
        Trace.endSection();

        // Find the best detections.
        final PriorityQueue<TFProvider.Recognition> pq =
                new PriorityQueue<TFProvider.Recognition>(
                        1,
                        new Comparator<TFProvider.Recognition>() {
                            @Override
                            public int compare(final TFProvider.Recognition lhs, final TFProvider.Recognition rhs) {
                                // Intentionally reversed to put high confidence at the head of the queue.
                                return Float.compare(rhs.getConfidence(), lhs.getConfidence());
                            }
                        });

        // Scale them back to the input size.
        for (int i = 0; i < outputScores.length; ++i) {
            final RectF detection =
                    new RectF(
                            outputLocations[4 * i + 1] * bitmap.getWidth(),
                            outputLocations[4 * i] * bitmap.getHeight(),
                            outputLocations[4 * i + 3] * bitmap.getWidth(),
                            outputLocations[4 * i + 2] * bitmap.getHeight());
            pq.add(
                    new TFProvider.Recognition("" + i, labels.get((int) outputClasses[i]), outputScores[i], detection));
        }

        final ArrayList<TFProvider.Recognition> recognitions = new ArrayList<TFProvider.Recognition>();
        for (int i = 0; i < Math.min(pq.size(), MAX_RESULTS); ++i) {
            recognitions.add(pq.poll());
        }
        Trace.endSection(); // "recognizeImage"
        return recognitions;
    }

    @Override
    public void enableStatLogging(final boolean logStats) {
        this.logStats = logStats;
    }

    @Override
    public String getStatString() {
        return inferenceInterface.getStatString();
    }

    @Override
    public void close() {
        inferenceInterface.close();
    }
}
