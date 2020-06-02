package com.tsinghuabigdata.edu.ddmath.opencv;

import android.content.Context;
import android.graphics.Point;
import android.graphics.RectF;
import android.text.TextUtils;

import com.tsinghuabigdata.edu.ddmath.module.learnmaterial.bean.AnswerAreaBean;
import com.tsinghuabigdata.edu.ddmath.module.learnmaterial.bean.AreaBean;
import com.tsinghuabigdata.edu.ddmath.module.learnmaterial.bean.QuestionNumAreaBean;
import com.tsinghuabigdata.edu.ddmath.module.learnmaterial.bean.RelativeAreas;
import com.tsinghuabigdata.edu.ddmath.module.learnmaterial.bean.SplitLineBean;
import com.tsinghuabigdata.edu.ddmath.util.AppLog;
import com.tsinghuabigdata.edu.ddmath.util.AssetsUtils;

import java.util.ArrayList;


/**
 *
 */
public class OpenCVHelper {
    static {
        try{
            System.loadLibrary("OpenCV");
        }catch (Exception e){
            AppLog.i("",e);
        }
    }

    public static boolean init(Context mContext){
        String dstDir = mContext.getFilesDir().getAbsolutePath();

        boolean succ = AssetsUtils.copyAssetsToDst( mContext, "QuestionID_Printer.dat", dstDir);
        AppLog.d("dfgdfgdf copy dat succ = " + succ );
        if( !succ ) return false;
        succ = AssetsUtils.copyAssetsToDst( mContext, "QuestionID_Printer.mod", dstDir);
        AppLog.d("dfgdfgdf mod succ = " + succ );
        if( !succ ) return false;

        String dp = dstDir + "/QuestionID_Printer.dat";
        String mp = dstDir + "/QuestionID_Printer.mod";
        succ = OpenCVHelper.globalInitQuestionDigit( dp, mp);
        AppLog.d("dfgdfgdf init succ = " + succ );
        return succ;
    }

    //public static native int[] gray(int[] buf, int w, int h);
    //public static native int testdata();
    //public static native int detect(int[] buf, int w, int h);
    //public static native int detectFile(String buf);

    //public static native int detectBuffer(long srcaddr, long dstaddr);
    //参数都是相对长宽的比率，放大1000倍
    //public static native int detectBitmap( Object object, int x, int y, int width, int height );
    public static native int detectBitmapColour( Object object, int width, int height );

    public static native boolean getDetectImage( String filename );

    //增强模式只能选一个
    static final int MODE_ENHANCE_LOG  =  0x1;   //对数增强
    static final int MODE_ENHANCE_EXP  =  0x2;   //指数增强          推荐1
    static final int MODE_ENHANCE_GRAY =  0x4;   //灰度增强
    static final int MODE_ENHANCE_HIS  =  0x8;   //直方图增强
    static final int MODE_ENHANCE_GAMA =  0x10;  //gama增强         推荐2

    //
    public static final int MODE_DILATE_ERODE = 0x100;   //膨胀/腐蚀
    public static final int MODE_REMOVE_NOISE = 0x200;   //去噪
    public static final int MODE_GUASS_BLUR = 0x400;   //高斯光滑处理

    //public static final int DEF_BLOCKSIZE = 21;

    //public static final int DEF_ADJUST_VALUE = 10;

    //基于本地图片进行处理
    public static native boolean enhanceImage( String srcname, String dstname, int mode, int blocksize, int adjustvaule );
    //基于已有的Mat进行处理
    public static native boolean enhanceImageMat( int mode, int blocksize, int adjustvaule );
    //对图片进行局部增强处理
    public static native boolean regionEnhanceImage( String srcname, int rectarray[], int count, int mode, int blocksize, int adjustvaule );

    public static final int MODE_CLARITY_1 = 0;
    //public static final int MODE_CLARITY_2 = 1;

    //图像清晰度判断
    public static native double clarityImage( String srcname, int mode );

    public static native String toSingleChannelGray( String srcpath, String dstpath );

    public static native boolean globalInitQuestionDigit( String datapath, String modepath );

    //------------------------------------------------------------------------------
    //原版教辅

    //边缘检测
    public static native int[] detectFourEdge( Object object, boolean force );

    //保存为图片
    public static native boolean saveDetecEdgeImage( String path, boolean force );

    public static native boolean saveImage( String path );
    public static native boolean saveImageEdge( String path, int points[] );

    //释放缓存的图片
    public static native void releaseBitmap();

    //
    public static native int clarityMat(/* int mode */);
    public static native int fuzzyDetection();

    /**
     * 对 native saveImageEdge 的封装处理
     */
    public static boolean saveImageEdge( String path, ArrayList<Point> list ){
        int data[] = new int[list.size()*2];
        int index = 0;
        for( Point point : list ){
            data[index++] = point.x;
            data[index++] = point.y;
        }
        return saveImageEdge( path, data );
    }

    /**
     * 对 native getAnswerAreas 的封装处理
     * @param answerAreas 答案区域
     * @param figureAreas 辅助图区域
     * @return 区域
     */
    public static ArrayList<RectF> getAnswerAreas(ArrayList<AnswerAreaBean> answerAreas,ArrayList<AnswerAreaBean> figureAreas){
        //return test2AnswerArea( list );
        if( answerAreas == null /*|| answerAreas.size()==0*/ ) return null;
        if( figureAreas == null ) figureAreas = new ArrayList<>();
        float reta[] = getAnswerAreas( answerArea2Array( answerAreas ), answerArea2Array( figureAreas )  );
        return array2AnswerArea( reta );
    }

//    /**
//     * 对 native getAnswerAreas 的封装处理
//     * @param list 答案区域
//     * @return 区域
//     */
//    public static ArrayList<RectF> getFigureAreas(ArrayList<AnswerAreaBean> list){
////        return test2AnswerArea( list );
//        if( list == null || list.size()==0 ) return null;
//        float reta[] = getFigureAreas( answerArea2Array( list ) );
//        return array2AnswerArea( reta );
//    }

    //获得答题区域
    private static native float[] getAnswerAreas( float[] aswers, float figures[] );

    //获得答题区域
//    private static native float[] getFigureAreas( float[] array );

    //public static native void setPaperDefaultWidthHeight( float rate);
    //--------------------------------------------------------------------------------------
    //
    private static void addAreaBean( ArrayList<Float> alist, AreaBean bean ){
        alist.add( bean.getX() );
        alist.add( bean.getY() );
        alist.add( bean.getWidth() );
        alist.add( bean.getHeight() );

        AppLog.d("dfdsafdfgfdg  x = " + bean.getX() + ",, y="+bean.getY() + ",,w = "+bean.getWidth() + ",,h="+bean.getHeight() );
    }
//    private static AreaBean getAreaBean( float data[], int start ){
//        AreaBean areaBean = new AreaBean();
//        areaBean.setX( data[start] );
//        areaBean.setY( data[start+1] );
//        areaBean.setWidth( data[start+1] );
//        areaBean.setHeight( data[start+1] );
//        return areaBean;
//    }

    private static float[] answerArea2Array( ArrayList<AnswerAreaBean> list ){

        int count = list.size();

        //
        ArrayList<Float> alist = new ArrayList<>();

        //对象数量
        alist.add( (float)count );
        AppLog.d("dfdsafdfgfdg  count = " + count );
        for( int i=0; i<count; i++ ){
            AnswerAreaBean answer = list.get(i);

            AppLog.d("dfdsafdfgfdg  i = " + i );
            //保存SerialNum
            alist.add( (float)answer.getSerialNum() );

            AppLog.d("dfdsafdfgfdg  serialNum = " + answer.getSerialNum() );

            //保存 manualArea
            AreaBean manualArea = answer.getManualArea();
            alist.add( manualArea!=null?1f:0 );

            AppLog.d("dfdsafdfgfdg  manualArea flag = " + (manualArea!=null?1:0) );
            if( manualArea != null ){
                addAreaBean( alist, manualArea );
            }

            //保存SpliteLine
            SplitLineBean lineBean = answer.getSplitLine();
            alist.add( lineBean!=null?1f:0 );

            AppLog.d("dfdsafdfgfdg  lineBean flag = " + (lineBean!=null?1:0) );
            if( lineBean != null ){
                alist.add( lineBean.getX1() );
                alist.add( lineBean.getX2() );
                AppLog.d("dfdsafdfgfdg  lineBean x1 = " + lineBean.getX1() + ",,x1 = "+lineBean.getX2() );
            }

            //保存 textAreas
            ArrayList<AreaBean> textAreas = answer.getTextAreas();
            if( textAreas == null || textAreas.size() == 0 ){
                alist.add( 0f );

                AppLog.d("dfdsafdfgfdg  textAreas count = " + 0 );
            }else{
                //数量
                alist.add( (float)textAreas.size() );

                AppLog.d("dfdsafdfgfdg  textAreas count = " + textAreas.size() );
                //列表项
                for( AreaBean areaBean : textAreas ){
                    addAreaBean( alist, areaBean );
                }
            }

            //save relativeAreas
            RelativeAreas relativeAreas = answer.getRelativeAreas();

            //left
            if( relativeAreas != null && relativeAreas.getLeftArea() != null ){
                alist.add( 1f );  //1 标志有数据

                AppLog.d("dfdsafdfgfdg  relativeAreas left flag = " + 1 );
                addAreaBean( alist, relativeAreas.getLeftArea() );
            }
            else {
                AppLog.d("dfdsafdfgfdg  relativeAreas left flag = " + 0 );
                alist.add( 0f );  //0 标志没有数据
            }

            //right
            if( relativeAreas != null && relativeAreas.getRightArea() != null ){
                alist.add( 1f );  //1 标志有数据
                AppLog.d("dfdsafdfgfdg  relativeAreas right flag = " + 1 );
                addAreaBean( alist, relativeAreas.getRightArea() );
            }
            else {
                alist.add( 0f );  //0 标志没有数据
                AppLog.d("dfdsafdfgfdg  relativeAreas right flag = " + 0 );
            }

            //top
            if( relativeAreas != null && relativeAreas.getTopArea() != null ){
                alist.add( 1f );  //1 标志有数据
                AppLog.d("dfdsafdfgfdg  relativeAreas top flag = " + 1 );
                addAreaBean( alist, relativeAreas.getTopArea() );
            }
            else {
                alist.add( 0f );  //0 标志没有数据
                AppLog.d("dfdsafdfgfdg  relativeAreas top flag = " + 0 );
            }

            //bottom
            if( relativeAreas != null && relativeAreas.getBottomArea() != null ){
                alist.add( 1f );  //1 标志有数据
                AppLog.d("dfdsafdfgfdg  relativeAreas bottom flag = " + 1 );
                addAreaBean( alist, relativeAreas.getBottomArea() );
            }
            else {
                alist.add( 0f );  //0 标志没有数据
                AppLog.d("dfdsafdfgfdg  relativeAreas bottom flag = " + 0 );
            }

            //realAnswerArea
            ArrayList<AreaBean> realAnswerAreas = answer.getRealAnswerAreas();
            if( realAnswerAreas == null || realAnswerAreas.size() == 0 ){
                alist.add( 0f );
                AppLog.d("realAnswerAreas  count = " + 0 );
            }else{
                //数量
                alist.add( (float)realAnswerAreas.size() );

                AppLog.d("dfdsafdfgfdg  realAnswerAreas count = " + realAnswerAreas.size() );
                //列表项
                for( AreaBean areaBean : realAnswerAreas ){
                    addAreaBean( alist, areaBean );
                }
            }

            //
            QuestionNumAreaBean questionNumArea = answer.getQuestionNumArea();
            if( questionNumArea==null ){
                alist.add( 0f );
                AppLog.d("questionNumArea  = null" );
            }else {
                alist.add( 1f );
                AppLog.d("questionNumArea  != null" );
                alist.add( questionNumArea.isHasBracket()?1f:0 );
                addAreaBean( alist, questionNumArea.getLocation() );

                //
                String digitStr = questionNumArea.getQuestionDigit();
                if(TextUtils.isEmpty(digitStr)){
                    alist.add( 0f );
                    AppLog.d("QuestionDigit  = null" );
                }else{
                    String digits[] = digitStr.split(",");
                    alist.add( (float)digits.length );
                    for( String item : digits ){
                        alist.add( Float.valueOf(item) );
                    }
                }
            }
        }

        float array[] = new float[alist.size()];
        for( int i=0;i<array.length;i++ ){
            array[i] = alist.get(i);
        }
        return array;
    }

    //
    private static  ArrayList<RectF> array2AnswerArea( float []data ){

        ArrayList<RectF> list = new ArrayList<>();
        if( data==null ) return list;

        int index = 0;
        int count = (int)data[index++];

        AppLog.d("dssafsadfgrgrg  count = " + count );
        AppLog.d("dssafsadfgrgrg  length = " + data.length );
        //for( int i=0; i<data.length;i++ )
        //    AppLog.d("dssafsadfgrgrg  i = " + i + ",,,, " + data[i] );
        //多个 AnswerAreaBean 对象
        for( int i=0; i<count; i++ ){
            //
            RectF rectF = new RectF();
            list.add( rectF );

            rectF.left  = data[index++];
            rectF.top   = data[index++];
            rectF.right = data[index++];
            rectF.bottom= data[index++];

            float MIN = 0.015f;
            float HALF= MIN / 2;
            if( rectF.bottom - rectF.top < MIN ){
                float mid = (rectF.bottom + rectF.top) / 2;
                if( mid < HALF ){
                    rectF.top   = 0f;
                    rectF.bottom= mid + MIN;
                }else{
                    rectF.top   = mid - HALF;
                    rectF.bottom= mid + HALF;
                }
            }
            AppLog.d("dssafsadfgrgrg  left = " + rectF.left + ",,,top =" + rectF.top + "  right=" +  rectF.right + "  bottom=" +  rectF.bottom  );
        }
        return list;
    }

    private static  ArrayList<RectF> test2AnswerArea( ArrayList<AnswerAreaBean> dlist ){

        ArrayList<RectF> list = new ArrayList<>();
        if( dlist==null ) return list;

        //多个 AnswerAreaBean 对象
        for( AnswerAreaBean bean : dlist ){
            AreaBean areabean = bean.getManualArea();
            if( areabean == null )
                continue;

            RectF rectF = new RectF();
            list.add( rectF );

            rectF.left  = areabean.getX();
            rectF.top   = areabean.getY();
            rectF.right = areabean.getWidth()+rectF.left;
            rectF.bottom= areabean.getHeight()+rectF.top;
        }
        return list;
    }

//    public static void testDetect(Context context){
//                        //
//                String path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/testt.jpg";
//                Bitmap bitmap = BitmapFactory.decodeFile( path );
//                long time = System.currentTimeMillis();
//                int[] ret = OpenCVHelper.detectFourEdge( bitmap, false );
//                AppLog.d(" sdjflkdsajflks detect time = " + (System.currentTimeMillis() - time) );
//                int k = 0;
//                for( Integer i : ret ){
//                    AppLog.d(" sdjflkdsajflks ret["+ k+"] = " + i );
//                    k++;
//                }
//
//                if( ret[0] == 0 ){
//                    time = System.currentTimeMillis();
//
//                    boolean b = OpenCVHelper.saveDetecEdgeImage(  Environment.getExternalStorageDirectory().getAbsolutePath()+"/testt-1.jpg", false );
//                    AppLog.d(" sdjflkdsajflks detect time = " + (System.currentTimeMillis() - time) );
//
//                    AppLog.d(" sdjflkdsajflks b = " + b );
//                    ToastUtils.show(context, "成功");
//                }else{
//                    ToastUtils.show(context, "失败");
//                }
//
//                AppLog.d(" sdjflkdsajflks finish" );
//    }

}
