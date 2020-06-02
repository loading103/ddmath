package com.tsinghuabigdata.edu.ddmath.view;

import android.content.Context;
import android.graphics.Bitmap;
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
import com.tsinghuabigdata.edu.ddmath.bean.StdAnswerBean;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.AppLog;
import com.tsinghuabigdata.edu.ddmath.util.PicassoUtil;
import com.tsinghuabigdata.edu.ddmath.util.WindowUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 答案内容显示
 */
public class AnswerTextView extends TextView {


    private static final String TAG_SPLIT = "#%#";

    private String questionData;
    private int maxWidth;
    private String mKeyWords;

    // 已下载的图片
    private Map<String, Bitmap> mBitmapMaps = new HashMap<>();
    private Map<String, DownTarget> mDownTargetMap = new HashMap<>();
    private Set<String> imageTags;

    // 渲染监听器
    private RendererListener mRendererListener;
    private LoginInfo loginInfo;
    //private StudentInfo studentInfo;

    public AnswerTextView(Context context) {
        super(context);
    }

    public AnswerTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AnswerTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        maxWidth = right - left - getPaddingLeft() - getPaddingRight();
    }

    /**
     *
     * @param question     题目
     * @param showSubStem  是否显示子题干
     */
    public void setQuestionBook(StdAnswerBean question, boolean showSubStem ) {
        loginInfo = AccountUtils.getLoginUser();

        //先显示，图片加载完成后再刷新
        String content = question.getContentg();

        questionData = getQuestionStem( content );
        renderLatex( true );

        questionData = replaceImageLatexTags( content, dealStemGraph( question.getGraph() ), question.getContentLatextGraph() );

        matchImageTags();
    }
    public void setQuestionContent( String content ){
        loginInfo = AccountUtils.getLoginUser();

        questionData = content;
        renderLatex( true );    //先显示
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
     * 高亮显示关键字
     */
//    public void setKeyWords( String key ){
//        mKeyWords = key;
//    }

    /**
     * 图片数据格式不规范，需要预处理
     * @param graphs 数据
     * @return
     */
    private static String dealStemGraph( String graphs ){
        if( TextUtils.isEmpty(graphs) ) return "";
        String images[] = graphs.split(TAG_SPLIT);
        boolean header = true;
        String newImages = "";
        for( String path : images ){
            if( !TextUtils.isEmpty(path.trim()) ){
                if( !header ){ newImages += TAG_SPLIT; }else{ header = false; }
                newImages += path;
            }
        }
        return newImages;
    }

    private String getQuestionStem( String stem ){
        if( TextUtils.isEmpty(stem) )
            return "";
        return stem.replaceAll(TAG_SPLIT,"\n");
    }

    private int statFindCount( String data, String tag ){
        int count = 0;
        int index = data.indexOf( tag );
        while( index!=-1 ){
            count++;
            index = data.indexOf( tag, index+tag.length() );
        }
        return count;
    }

    private String replaceImageLatexTags( String stem, String graph, String latexGraph ){
        if( TextUtils.isEmpty(stem) || loginInfo==null )
            return "";

        stem = stem.replaceAll(TAG_SPLIT,"\n");
        String content = stem;
        if( !TextUtils.isEmpty(graph) ){
            String images[] = graph.split(TAG_SPLIT);
            int count = statFindCount( stem, "<img>" );
            while ( count < images.length ){
                stem += "<img>";
                count++;
            }
            for( String imgurl : images ){
                stem = stem.replaceFirst("<img>","<img::" + loginInfo.getFileServer()+imgurl+">" );
            }
            content = stem;
        }

        if( !TextUtils.isEmpty(latexGraph) ){
            String images[] = latexGraph.split("#%#");
            for( String imgurl : images ){
                content = content.replaceFirst("<latex>","<img::" + loginInfo.getFileServer()+imgurl+">" );
            }
        }
        return content;
    }

    /**
     * 对外处理 题目里面的图片和公式
     * @param stem     题干
     * @param graph    图片
     * @param latexGraph 公式图片
     * @param loginInfo 学生信息
     * @return 替换后的文本
     */
    public static String replaceImageLatexTags( String stem, String graph, String latexGraph, LoginInfo loginInfo ){
        if( TextUtils.isEmpty(stem) )
            return "";

        String domain = loginInfo.getFileServer();
        if( domain!=null&&!domain.startsWith("http://") ){
            loginInfo.setFileServer("http://"+domain);
        }

        //stem = stem.replaceAll("#%#","\n");
        String content = stem;
        if( !TextUtils.isEmpty(graph) ){
            graph = dealStemGraph( graph );
            String images[] = graph.split("#%#");
            for( String imgurl : images ){
                stem = stem.replaceFirst("<img>","<img::" + loginInfo.getFileServer()+imgurl+">" );
            }
            content = stem;
        }

        if( !TextUtils.isEmpty(latexGraph) ){
            String images[] = latexGraph.split("#%#");
            for( String imgurl : images ){
                content = content.replaceFirst("<latex>","<img::" + loginInfo.getFileServer()+imgurl+">" );
            }
        }
        return content;
    }

//    public void retry() {
//        matchImageTags();
//    }

    private void renderLatex( boolean first ) {
        SpannableStringBuilder builder = new SpannableStringBuilder( questionData );
        renderStemsKeywords(builder);
        if( !first ){
            renderStemsImage(builder);
        }
        super.setText(builder);
        if (mRendererListener != null && !first ) {
            mRendererListener.renderSuccess(this);
        }
    }

    /**
     * 渲染题干里面的关键字
     */
    private void renderStemsKeywords(SpannableStringBuilder builder) {
        //渲染关键字
        if( TextUtils.isEmpty( mKeyWords) ){
            return;
        }

        Pattern pattern = Pattern.compile( mKeyWords );
        Matcher matcher = pattern.matcher( builder.toString() );
        while (matcher.find()) {
            builder.setSpan( new ForegroundColorSpan( getResources().getColor(R.color.blue)), matcher.start(), matcher.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }


    /**
     * 渲染题干图片
     */
    private void renderStemsImage(SpannableStringBuilder builder) {
        int size = WindowUtils.spToPixels(getContext(), 18);
        Drawable drawable = getResources().getDrawable( R.drawable.ic_broken_image );
        if( drawable==null ) return;
        drawable.setBounds(0, 0, size, size);
        for (String key : imageTags) {
            Pattern pattern = Pattern.compile(key);
            Matcher matcher = pattern.matcher( builder.toString() );
            while (matcher.find()) {
                Bitmap bitmap = mBitmapMaps.get(key);
                if (bitmap != null) {
                    drawable =new BitmapDrawable(bitmap);
                    int width = bitmap.getWidth()/2;
                    int height= bitmap.getHeight()/2;
                    if( maxWidth!=0 && width > maxWidth ){
                        width = maxWidth;
                        height= bitmap.getHeight() * width / bitmap.getWidth();
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
        Matcher matcher = pattern.matcher( questionData );
        // 使用Set保证数据唯一性
        imageTags = new HashSet<>(matcher.groupCount());
        while (matcher.find()) {
            imageTags.add(matcher.group());
        }

        downloadImages(imageTags);
    }

    /**
     * download image run in thread
     *
     */

    private boolean postExecStatus = false;
    private void downloadImages(final Set<String> imageTags) {
        AppLog.i("下载图片中...");
        AppLog.i("download csssss  amin  size = " + imageTags.size() + ",,,,mDownTargetMap.size() = " + mDownTargetMap.size() );

        if( mDownTargetMap.size() > 0 ) return;

        final CountDownLatch countDownLatch = new CountDownLatch(imageTags.size());
        // download image by url
        new Thread() {
            @Override
            public void run() {
                try {
                    countDownLatch.await();
                    mDownTargetMap.clear();
                    AppLog.i("download mBitmapMaps = " + mBitmapMaps.size()  + "  amin  size = " + imageTags.size() );

                    if (mBitmapMaps.size() < imageTags.size()) {
                        // failed
                        post(new Runnable() {
                            @Override
                            public void run() {
                                if (mRendererListener != null) {
                                    mRendererListener.renderFailed(AnswerTextView.this);
                                }
                                renderLatex( false );
                            }
                        });
                    } else {
                        // success
                        AppLog.i("download all finish success" );

                        postExecStatus = false;
                        execSuccessPost();

                        SystemClock.sleep( 110 );
                        while( !postExecStatus ){
                            execSuccessPost();
                            SystemClock.sleep( 110 );
                            if(bQuit) break;
                        }
                    }
                } catch (InterruptedException e) {
                    AppLog.i("",e);
                    mDownTargetMap.clear();
                    post(new Runnable() {
                        @Override
                        public void run() {
                            if (mRendererListener != null) {
                                mRendererListener.renderFailed(AnswerTextView.this);
                            }
                            renderLatex( false );
                        }
                    });
                }
            }
        }.start();


        for (final String imageTag : imageTags) {
            AppLog.d("--- imageTag = " + imageTag);
            String array[] = imageTag.replaceAll("<|>", "").split("::");
            String url;
            if (array.length < 2) {
                url = "test.png";
            } else {
                url = array[1];
            }
            AppLog.d("--- url = " + url);
            DownTarget target = new DownTarget(countDownLatch, imageTag);
            mDownTargetMap.put(imageTag, target);
            try {
                PicassoUtil.getPicasso(getContext()).load(url).into(target);
            }catch (Exception e){AppLog.i("", e);}
        }
    }
    private void execSuccessPost(){
        AppLog.i("download all finish success execSuccessPost start " );
        postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mRendererListener != null) {
                    mRendererListener.renderSuccess(AnswerTextView.this);
                }
                postExecStatus = true;
                AppLog.i("download all finish execSuccessPost end " );
                renderLatex( false );
            }
        },100);
    }

//    Transformation transformation = new Transformation() {
//
//        @Override
//        public Bitmap transform(Bitmap source) {
//
//            //int targetWidth = imagewidth_max;
//
//            if( imagewidth_max!=0 && source.getWidth() > imagewidth_max ){
//                float rate = imagewidth_max*1.0f / source.getWidth();
//                int targetHeight = (int) (source.getHeight() * rate);
//                Bitmap bitmap = Bitmap.createScaledBitmap(source, imagewidth_max, targetHeight, false);
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

    public interface RendererListener {
        void perRender(AnswerTextView view);

        void renderFailed(AnswerTextView view);

        void renderSuccess(AnswerTextView view);
    }

    class DownTarget implements Target {

        private CountDownLatch countDownLatch;
        private String imageTag;

        public DownTarget(CountDownLatch countDownLatch, String imageTag) {
            this.countDownLatch = countDownLatch;
            this.imageTag = imageTag;
        }

        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom loadedFrom) {
            // save to bitmaps
            AppLog.i("down success");
            mBitmapMaps.put(imageTag, bitmap);
            countDownLatch.countDown();
        }

        @Override
        public void onBitmapFailed(Drawable drawable) {
            // 下载失败
            AppLog.i("down failed");
            if( drawable instanceof BitmapDrawable ){
                mBitmapMaps.put(imageTag, ((BitmapDrawable) drawable).getBitmap() );
            }
            countDownLatch.countDown();
        }

        @Override
        public void onPrepareLoad(Drawable drawable) {
        }
    }
}
