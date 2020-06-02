package com.tsinghuabigdata.edu.ddmath.module.ddwork.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.bean.LoginInfo;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.LocalQuestionInfo;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.QuestionInfo;
import com.tsinghuabigdata.edu.ddmath.module.studycheat.bean.FocreTrainQuestionInfo;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.AppLog;
import com.tsinghuabigdata.edu.ddmath.util.BitmapUtils;
import com.tsinghuabigdata.edu.ddmath.util.PicassoUtil;
import com.tsinghuabigdata.edu.ddmath.util.WindowUtils;
import com.tsinghuabigdata.edu.ddmath.view.CenteredImageSpan;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 题目内容显示
 */
public class QuestionTextView extends TextView {


    private static final String TAG_SPLIT = "#%#";

    private String questionData;
    private int    maxWidth;
    private String mKeyWords;
    private int    keywordColor;

    // 已下载的图片
    private Map<String, Bitmap>     mBitmapMaps    = new HashMap<>();
    private Map<String, DownTarget> mDownTargetMap = new HashMap<>();
    private Set<String> imageTags;

    // 渲染监听器
    private RendererListener mRendererListener;
    private LoginInfo        loginInfo;
    //private StudentInfo studentInfo;

    public QuestionTextView(Context context) {
        super(context);
    }

    public QuestionTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public QuestionTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom + 60);
        maxWidth = right - left - getPaddingLeft() - getPaddingRight();
    }

    //    public void setMaxImageWidth(int max) {
    //        this.maxWidth = max;
    //    }
    //
    public void setRendererListener(RendererListener RendererListener) {
        this.mRendererListener = RendererListener;
    }

    /**
     * 显示题干，默认主题干 字题干都显示
     *
     * @param question 题目类
     */
    public void setQuestion( LocalQuestionInfo question) {
        setQuestion( question, true);
    }

    /**
     * @param question    题目
     * @param showSubStem 是否显示子题干
     */
    public void setQuestion( LocalQuestionInfo question, boolean showSubStem) {
        loginInfo = AccountUtils.getLoginUser();
        if (loginInfo == null)
            return;

        //先显示，图片加载完成后再刷新
        String content = "";
        if( !TextUtils.isEmpty(question.getNumInPaper()) )
            content = question.getNumInPaper() + ". ";

        content += question.getStem();

        questionData = getQuestionStem(content);
        if (showSubStem) {
            questionData += "\n" + getQuestionStem(question.getSubStem());
        }
        renderLatex(true);

        questionData = replaceImageLatexTags(content, dealStemGraph(question.getStemGraph()), question.getStemLatexGraph());

        if (showSubStem) {
            questionData += "\n";
            questionData += replaceImageLatexTags(question.getSubStem(), dealStemGraph(question.getSubStemGraph()), question.getSubLatexGraph());
        }
        matchImageTags();
    }

    /**
     * @param question    题目
     * @param showSubStem 是否显示子题干
     */
    public void setQuestionBook(QuestionInfo question, boolean showSubStem) {
        loginInfo = AccountUtils.getLoginUser();
        if (loginInfo == null)
            return;

        //先显示，图片加载完成后再刷新
        String content = question.getStem();

        questionData = getQuestionStem(content);
        if (showSubStem) {
            questionData += "\n" + getQuestionStem(question.getSubStem());
        }
        renderLatex(true);

        questionData = replaceImageLatexTags(content, dealStemGraph(question.getStemGraph()), question.getStemLatexGraph());

        if (showSubStem) {
            questionData += "\n";
            questionData += replaceImageLatexTags(question.getSubStem(), dealStemGraph(question.getSubStemGraph()), question.getSubStemLatexGraph());
        }
        matchImageTags();
    }

    /**
     * @param question    题目
     * @param showSubStem 是否显示子题干
     */
    public void setSingleQuestionBook(QuestionInfo question, boolean showSubStem) {
        loginInfo = AccountUtils.getLoginUser();
        if (loginInfo == null)
            return;

        //先显示，图片加载完成后再刷新
        String content = question.getStem();

        questionData = getQuestionStem(content);
        if (showSubStem) {
            questionData += "\n" + getQuestionStem(question.getSubStem());
        }
        renderLatex(true);

        questionData = replaceImageLatexTags(content, dealStemGraph(question.getStemGraph()), question.getStemLatexGraph());

        if (showSubStem) {
            questionData += "\n";
            questionData += replaceImageLatexTags(question.getSubStem(), dealStemGraph(question.getSubStemGraph()), question.getSubLatexGraph());
        }
        matchImageTags();
    }

    /**
     * @param question    题目
     * @param showSubStem 是否显示子题干
     */
    public void setQuestionBook(QuestionInfo question, int number, boolean showSubStem) {
        loginInfo = AccountUtils.getLoginUser();
        if (loginInfo == null)
            return;

        //先显示，图片加载完成后再刷新
        String content = number + ". " + question.getStem();

        questionData = getQuestionStem(content);
        if (showSubStem) {
            questionData += "\n" + getQuestionStem(question.getSubStem());
        }
        renderLatex(true);

        questionData = replaceImageLatexTags(content, dealStemGraph(question.getStemGraph()), question.getStemLatexGraph());

        if (showSubStem) {
            questionData += "\n";
            questionData += replaceImageLatexTags(question.getSubStem(), dealStemGraph(question.getSubStemGraph()), question.getSubStemLatexGraph());
        }
        matchImageTags();
    }

    public void setQuestionContent(String content) {
        loginInfo = AccountUtils.getLoginUser();
        if (loginInfo == null)
            return;

        questionData = content;
        renderLatex(true);    //先显示
        matchImageTags();
    }

    private boolean bQuit = false;

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        bQuit = false;
    }

    @Override
    public void onStartTemporaryDetach() {
        super.onDetachedFromWindow();
        bQuit = true;
    }
    //    public void setSubStem( String content, String subStemGraph, String subStemLatexGraph ){
    //        loginInfo = AccountUtils.getLoginUser();
    //        studentInfo = AccountUtils.getStudentInfo();
    //
    //        //先显示，图片加载完成后再刷新
    //        questionData = getQuestionStem( content );
    //        renderLatex( true );
    //
    //        questionData = replaceImageLatexTags( content, subStemGraph, subStemLatexGraph );
    //
    //        matchImageTags();
    //    }

    /**
     * 显示题干，默认主题干 字题干都显示
     *
     * @param question 题目类
     */
    public void setQuestion(FocreTrainQuestionInfo question) {
        setQuestion(question, true);
    }

    /**
     * @param question    题目
     * @param showSubStem 是否显示子题干
     */
    public void setQuestion(FocreTrainQuestionInfo question, boolean showSubStem) {
        loginInfo = AccountUtils.getLoginUser();

        //先显示，图片加载完成后再刷新
        questionData = getQuestionStem(question.getStem());
        if (showSubStem) {
            questionData += "\n" + getQuestionStem(question.getSubStem());
        }
        renderLatex(true);

        questionData = replaceImageLatexTags(question.getStem(), dealStemGraph(question.getStemGraph()), question.getStemLatexGraph());

        if (showSubStem) {
            questionData += "\n";
            questionData += replaceImageLatexTags(question.getSubStem(), dealStemGraph(question.getSubStemGraph()), question.getSubStemLatexGraph());
        }
        matchImageTags();
    }

    /**
     * 高亮显示关键字
     */
    public void setKeyWords(String key) {
        mKeyWords = key;
        keywordColor = Color.rgb(0xFF, 0xB5, 0x55);//getResources().getColor(R.color.color_FFB555);
    }

    public void setKeyWords(String key, int color) {
        mKeyWords = key;
        keywordColor = color;
    }

    /**
     * 图片数据格式不规范，需要预处理
     *
     * @param graphs 数据
     * @return zsf
     */
    private static String dealStemGraph(String graphs) {
        if (TextUtils.isEmpty(graphs))
            return "";
        String images[] = graphs.split(TAG_SPLIT);
        boolean header = true;
        String newImages = "";
        for (String path : images) {
            if (!TextUtils.isEmpty(path.trim())) {
                if (!header) {
                    newImages += TAG_SPLIT;
                } else {
                    header = false;
                }
                newImages += path;
            }
        }
        return newImages;
    }

    private String getQuestionStem(String stem) {
        if (TextUtils.isEmpty(stem))
            return "";
        return stem.replaceAll(TAG_SPLIT, "\n");
    }

    private int statFindCount(String data, String tag) {
        int count = 0;
        int index = data.indexOf(tag);
        while (index != -1) {
            count++;
            index = data.indexOf(tag, index + tag.length());
        }
        return count;
    }

    private String replaceImageLatexTags(String stem, String graph, String latexGraph) {
        if (TextUtils.isEmpty(stem))
            return "";

        stem = stem.replaceAll(TAG_SPLIT, "\n");
        String content = stem;
        if (!TextUtils.isEmpty(graph)) {
            String images[] = graph.split(TAG_SPLIT);
            int count = statFindCount(stem, "<img>");
            while (count < images.length) {
                stem += "\n<img>";
                count++;
            }
            for (String imgurl : images) {
                if( !imgurl.startsWith("file://") ){
                    imgurl = loginInfo.getFileServer() + imgurl;
                }
                stem = stem.replaceFirst("<img>", "<img::" + imgurl + ">");
            }
            content = stem;
        }

        if (!TextUtils.isEmpty(latexGraph)) {
            String images[] = latexGraph.split("#%#");
            for (String imgurl : images) {
                content = content.replaceFirst("<latex>", "<img::" + loginInfo.getFileServer() + imgurl + ">");
            }
        }
        return content;
    }

    /**
     * 对外处理 题目里面的图片和公式
     *
     * @param stem       题干
     * @param graph      图片
     * @param latexGraph 公式图片
     * @param loginInfo  学生信息
     * @return 替换后的文本
     */
    public static String replaceImageLatexTags(String stem, String graph, String latexGraph, LoginInfo loginInfo) {
        if (TextUtils.isEmpty(stem))
            return "";

        String domain = loginInfo.getFileServer();
        if (domain != null && !domain.startsWith("http://")) {
            loginInfo.setFileServer("http://" + domain);
        }

        //stem = stem.replaceAll("#%#","\n");
        String content = stem;
        if (!TextUtils.isEmpty(graph)) {
            graph = dealStemGraph(graph);
            String images[] = graph.split("#%#");
            for (String imgurl : images) {
                if( !imgurl.startsWith("file://") ){
                    imgurl = loginInfo.getFileServer() + imgurl;
                }
                stem = stem.replaceFirst("<img>", "<img::" + imgurl + ">");
            }
            content = stem;
        }

        if (!TextUtils.isEmpty(latexGraph)) {
            String images[] = latexGraph.split("#%#");
            for (String imgurl : images) {
                content = content.replaceFirst("<latex>", "<img::" + loginInfo.getFileServer() + imgurl + ">");
            }
        }
        return content;
    }

    //    public void retry() {
    //        matchImageTags();
    //    }

    private void renderLatex(boolean first) {
        SpannableStringBuilder builder = new SpannableStringBuilder(questionData);
        renderStemsKeywords(builder);
        if (!first) {
            renderStemsImage(builder);
        }
        super.setText(builder);
        if (mRendererListener != null && !first) {
            mRendererListener.renderSuccess(this);
        }
    }

    /**
     * 渲染题干里面的关键字
     */
    private void renderStemsKeywords(SpannableStringBuilder builder) {
        //渲染关键字
        if (TextUtils.isEmpty(mKeyWords)) {
            return;
        }

        boolean hasBracket = mKeyWords.startsWith("(") && mKeyWords.endsWith(")");
        /*if ("(".equals(mKeyWords)) {
            mKeyWords = "\\(";
            mKeyWords = "[(]";
        } else if (")".equals(mKeyWords)) {
            mKeyWords = "\\)";
        }*/
        if (mKeyWords.contains("(")) {
            mKeyWords = mKeyWords.replace("(", "[(]");
        }
        if (mKeyWords.contains(")")) {
            mKeyWords = mKeyWords.replace(")", "[)]");
        }
        Pattern pattern = Pattern.compile(mKeyWords);
        Matcher matcher = pattern.matcher(builder.toString());
        while (matcher.find()) {
            if (hasBracket) {
                builder.setSpan(new ForegroundColorSpan(keywordColor), matcher.start() - 1, matcher.end() + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else {
                builder.setSpan(new ForegroundColorSpan(keywordColor), matcher.start(), matcher.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
    }

    /**
     * 渲染题干图片
     */
    private void renderStemsImage(SpannableStringBuilder builder) {
        int size = WindowUtils.spToPixels(getContext(), 18);
        Drawable drawable = getResources().getDrawable(R.drawable.ic_broken_image);
        if (drawable == null)
            return;
        drawable.setBounds(0, 0, size, size);
        for (String key : imageTags) {
            Pattern pattern = Pattern.compile(key);
            Matcher matcher = pattern.matcher(builder.toString());
            while (matcher.find()) {
                Bitmap bitmap = mBitmapMaps.get(key);
                if (bitmap != null) {
                    drawable = new BitmapDrawable(bitmap);

                    int width = bitmap.getWidth();
                    int height = bitmap.getHeight();
                    if (key.contains("fsglatex")) {
                        width = bitmap.getWidth() * 4 / 5;
                        height = bitmap.getHeight() * 4 / 5;
                    }
                    if (maxWidth != 0 && width > maxWidth) {
                        width = maxWidth;
                        height = bitmap.getHeight() * width / bitmap.getWidth();
                    }
                    drawable.setBounds(0, 0, width, height);
                }
                builder.setSpan(new CenteredImageSpan(drawable), matcher.start(), matcher.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
    }

    /**
     * 匹配所有的图标标签
     */
    private void matchImageTags() {
        if (mRendererListener != null) {
            mRendererListener.perRender(this);
        }
        Pattern pattern = Pattern.compile("<img::.*?>");
        Matcher matcher = pattern.matcher(questionData);
        // 使用Set保证数据唯一性
        imageTags = new HashSet<>(matcher.groupCount());
        while (matcher.find()) {
            imageTags.add(matcher.group());
        }

        downloadImages(imageTags);
    }

    /**
     * download image run in thread
     */

    private boolean postExecStatus = false;

    private void downloadImages(final Set<String> imageTags) {
        //AppLog.i("下载图片中...");
        //AppLog.i("download csssss  amin  size = " + imageTags.size() + ",,,,mDownTargetMap.size() = " + mDownTargetMap.size() );

        if (mDownTargetMap.size() > 0)
            return;

        final CountDownLatch countDownLatch = new CountDownLatch(imageTags.size());
        // download image by url
        new Thread() {
            @Override
            public void run() {
                try {
                    countDownLatch.await();
                    mDownTargetMap.clear();
                    //AppLog.i("download mBitmapMaps = " + mBitmapMaps.size()  + "  amin  size = " + imageTags.size() );

                    if (mBitmapMaps.size() < imageTags.size()) {
                        // failed
                        post(new Runnable() {
                            @Override
                            public void run() {
                                if (mRendererListener != null) {
                                    mRendererListener.renderFailed(QuestionTextView.this);
                                }
                                renderLatex(false);
                            }
                        });
                    } else {
                        // success
                        //AppLog.i("download all finish success" );

                        postExecStatus = false;
                        execSuccessPost();

                        SystemClock.sleep(110);
                        while (!postExecStatus) {
                            execSuccessPost();
                            SystemClock.sleep(110);
                            if (bQuit)
                                break;
                        }
                    }
                } catch (InterruptedException e) {
                    AppLog.i("", e);
                    mDownTargetMap.clear();
                    post(new Runnable() {
                        @Override
                        public void run() {
                            if (mRendererListener != null) {
                                mRendererListener.renderFailed(QuestionTextView.this);
                            }
                            renderLatex(false);
                        }
                    });
                }
            }
        }.start();


        for (final String imageTag : imageTags) {
            //AppLog.d("--- imageTag = " + imageTag);
            String array[] = imageTag.replaceAll("<|>", "").split("::");
            String url;
            if (array.length < 2) {
                url = "test.png";
            } else {
                url = array[1];
            }
            //AppLog.d("--- fff url = " + url);
            url = BitmapUtils.getUrlWithToken(url);
            DownTarget target = new DownTarget(countDownLatch, imageTag);
            mDownTargetMap.put(imageTag, target);
            try {
                PicassoUtil.getPicasso(getContext()).load(url)/*.transform(transformation)*/.into(target);
            } catch (Exception e) {
                AppLog.i("", e);
            }
        }
    }

    private void execSuccessPost() {
        //AppLog.i("download all finish success execSuccessPost start " );
        postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mRendererListener != null) {
                    mRendererListener.renderSuccess(QuestionTextView.this);
                }
                postExecStatus = true;
                //AppLog.i("download all finish execSuccessPost end " );
                renderLatex(false);
            }
        }, 100);
    }

    //    Transformation transformation = new Transformation() {
    //
    //        @Override
    //        public Bitmap transform(Bitmap source) {
    //
    //            //int targetWidth = maxWidth;
    //
    //            if( maxWidth!=0 && source.getWidth() > maxWidth ){
    //                float rate = maxWidth*1.0f / source.getWidth();
    //                int targetHeight = (int) (source.getHeight() * rate);
    //                Bitmap bitmap = Bitmap.createScaledBitmap(source, maxWidth, targetHeight, false);
    //                source.recycle();
    //                return bitmap;
    //            }else{
    //                return source;
    //            }
    //        }
    //
    //        @Override
    //        public String key() {
    //            return "transformation" + " desiredWidth";
    //        }
    //    };

    interface RendererListener {
        void perRender(QuestionTextView view);

        void renderFailed(QuestionTextView view);

        void renderSuccess(QuestionTextView view);
    }

    private class DownTarget implements Target {

        private CountDownLatch countDownLatch;
        private String         imageTag;

        DownTarget(CountDownLatch countDownLatch, String imageTag) {
            this.countDownLatch = countDownLatch;
            this.imageTag = imageTag;
        }

        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom loadedFrom) {
            // save to bitmaps
            //AppLog.i("down success imageTag = " + imageTag );
            if (imageTag.endsWith(".jpg>") || imageTag.endsWith(".jpeg>")) {
                //long time = System.currentTimeMillis();
                bitmap = BitmapUtils.transparentBitmap(bitmap);
                //AppLog.i("down success time = "+(System.currentTimeMillis()-time) + "  mutable="+ bitmap.isMutable() +" imageTag = " + imageTag );
            }
            mBitmapMaps.put(imageTag, bitmap);
            countDownLatch.countDown();
        }

        @Override
        public void onBitmapFailed(Drawable drawable) {
            // 下载失败
            AppLog.i("down failed");
            if (drawable instanceof BitmapDrawable) {
                mBitmapMaps.put(imageTag, ((BitmapDrawable) drawable).getBitmap());
            }
            countDownLatch.countDown();
        }

        @Override
        public void onPrepareLoad(Drawable drawable) {
        }
    }
}
