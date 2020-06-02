package com.tsinghuabigdata.edu.ddmath.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.tsinghuabigdata.edu.ddmath.util.KnowCognitionUtil;


/**
 * 认知误差链 控件
 */
@SuppressLint("AppCompatCustomView")
public class KnowCognitionView extends ImageView
{

    //
    private Bitmap mBitmap;
    private Bitmap mZoomBitmap;
    private boolean bZoom = false;

    public KnowCognitionView(Context context) {
        super(context);
    }
    public KnowCognitionView(Context context, AttributeSet attrs){
        super(context, attrs);
    }

    public KnowCognitionView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

    }

    /**
     * 获取认知误差链图
     */
    public Bitmap getBitmap(){
        return mBitmap;
    }

    /**
     * 设置知识点认知链数据
     * @param data  数据 json 结构
     */
    private String databak = "";
    public void setKnowData( String data ){

        if( databak.equals(data) && mBitmap!=null )
            return;

        //测试数据
//        data = "[{" +
//                "        \"averageRate\": 50," +
//                "        \"cognitionRate\": 60," +
//                "        \"gap\": 10," +
//                "        \"nodeId\": \"1145\"," +
//                "        \"nodeName\": \"一元一次方程的解法\"," +
//                "        \"pNodeIds\": [\"1141\", \"2632\"]" +
//                "    }, {" +
//                "        \"averageRate\": 40," +
//                "        \"cognitionRate\": 60," +
//                "        \"gap\": 20," +
//                "        \"nodeId\": \"1141\"," +
//                "        \"nodeName\": \"解方程\"," +
//                "        \"pNodeIds\": [\"1131\"]" +
//                "    }, {" +
//                "        \"averageRate\": 50," +
//                "        \"cognitionRate\": 30," +
//                "        \"gap\": -20," +
//                "        \"nodeId\": \"2632\"," +
//                "        \"nodeName\": \"方程的解法\"," +
//                "        \"pNodeIds\": [\"1130\"]" +
//                "    }, {" +
//                "        \"averageRate\": 40," +
//                "        \"cognitionRate\": 40," +
//                "        \"gap\": -40," +
//                "        \"nodeId\": \"1131\"," +
//                "        \"nodeName\": \"方程的基本概念\"," +
//                "         \"pNodeIds\": []" +
//                "    }, {" +
//                "        \"averageRate\": 60," +
//                "        \"cognitionRate\": -1," +
//                "        \"gap\": 20," +
//                "        \"nodeId\": \"1130\"," +
//                "        \"nodeName\": \"方程与方程组\"," +
//                "        \"pNodeIds\": []" +
//                "    }]";

        KnowCognitionUtil knowCognition = new KnowCognitionUtil( getContext() );
        knowCognition.setKnowData( data );
        mBitmap = knowCognition.getKnowBitmap();
    }

    public void setBitmap( Bitmap bitmap ){
        mBitmap = bitmap;
    }



    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        if( mBitmap == null ){
            setMeasuredDimension( widthMeasureSpec,heightMeasureSpec );
            return;
        }

        int width = 0;
        int height = 0;

        /**
         * 设置宽度
         */
        int specMode = MeasureSpec.getMode(widthMeasureSpec);
        int specWidth = MeasureSpec.getSize(widthMeasureSpec);
        switch (specMode)
        {
            case MeasureSpec.EXACTLY:// 明确指定了
                width = specWidth;
                break;
            case MeasureSpec.AT_MOST:// 一般为WARP_CONTENT
                width = mBitmap.getWidth();
                break;
            case MeasureSpec.UNSPECIFIED:
                width = mBitmap.getHeight();
                break;
            default: break;
        }

        /**
         * 设置高度
         */
        specMode = MeasureSpec.getMode(heightMeasureSpec);
        int specHeight = MeasureSpec.getSize(heightMeasureSpec);
        switch (specMode)
        {
            case MeasureSpec.EXACTLY:// 明确指定了
                height = specHeight;
                break;
            case MeasureSpec.AT_MOST:// 一般为WARP_CONTENT
                height = mBitmap.getHeight();
                break;
            case MeasureSpec.UNSPECIFIED:
                height = mBitmap.getHeight();
                break;
            default:
                break;
        }

        //调整大小
        if( width > specWidth && specWidth > 0 ){
            width = specWidth;
        }else if( specWidth <= 0 ){
            //width =
        }
        if( height > specHeight && specHeight > 0 ){
            height = specHeight;
        }else if( specHeight <= 0 ){
            //width =
        }

        if( width == mBitmap.getWidth() && height == mBitmap.getHeight() ){
            bZoom = false;
        }else{
            bZoom = true;

            float fw = width*1f/mBitmap.getWidth();
            float fh = height*1f/mBitmap.getHeight();
            float mRate = fw<fh?fw:fh;

            width = (int)(mBitmap.getWidth()*mRate);
            height= (int)(mBitmap.getHeight()*mRate );
            mZoomBitmap = Bitmap.createBitmap( width, height, Bitmap.Config.RGB_565 );
            Canvas canvas = new Canvas(mZoomBitmap);
            Matrix matrix = new Matrix();
            matrix.postScale( mRate, mRate );
            canvas.drawBitmap( mBitmap,matrix,mPaint );
        }

        setMeasuredDimension( width+getPaddingLeft()+getPaddingRight(), height+getPaddingTop()+getPaddingBottom() );
    }

    private Paint mPaint = new Paint();
    @Override
    protected void onDraw(Canvas canvas)
    {
        if( mBitmap == null ) return;

        canvas.drawColor( getDrawingCacheBackgroundColor() );

        if( !bZoom ){
            canvas.drawBitmap( mBitmap, getPaddingLeft(), getPaddingTop(), mPaint );
        }else{
            int px = (getWidth() - getPaddingLeft() - getPaddingRight() - mZoomBitmap.getWidth())/2;
            int py = (getHeight() - getPaddingTop() - getPaddingBottom() - mZoomBitmap.getHeight())/2;

            canvas.drawBitmap( mZoomBitmap, px+getPaddingLeft(), py+getPaddingTop(), mPaint );
        }
    }


}
