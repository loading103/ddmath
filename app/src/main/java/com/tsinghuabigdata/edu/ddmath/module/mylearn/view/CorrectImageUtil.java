package com.tsinghuabigdata.edu.ddmath.module.mylearn.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tsinghuabigdata.edu.commons.cache.CacheManager;
import com.tsinghuabigdata.edu.ddmath.constant.AppConst;
import com.tsinghuabigdata.edu.ddmath.module.learnmaterial.CreateWorkActivity;
import com.tsinghuabigdata.edu.ddmath.module.mylearn.bean.ImageCorrectBean;
import com.tsinghuabigdata.edu.ddmath.util.AssetsUtils;
import com.tsinghuabigdata.edu.ddmath.util.DensityUtils;
import com.tsinghuabigdata.edu.utils.MD5Util;

import java.util.ArrayList;

/**
 * 把学生图片和批改结果合成一张图片上面
 */
public class CorrectImageUtil {

    private static Bitmap rightBitmap;
    private static Bitmap wrongBitmap;
    private static Bitmap halfBitmap;
    private static Bitmap doubtBitmap;

    private Context mContext;
    public CorrectImageUtil(Context context){
        mContext = context;
        if( rightBitmap == null ){
            rightBitmap = AssetsUtils.getBitmapFromAssetsFile( context, "ico_right_big.png" ); //BitmapFactory.decodeResource(res, R.drawable.ic_right_big1);
            wrongBitmap = AssetsUtils.getBitmapFromAssetsFile( context, "ico_wrong_big.png" ); //BitmapFactory.decodeResource(res, R.drawable.ic_wrong_big1);
            halfBitmap  = AssetsUtils.getBitmapFromAssetsFile( context, "ico_half_right.png" ); //BitmapFactory.decodeResource(res, R.drawable.ic_half_right1);
            doubtBitmap = AssetsUtils.getBitmapFromAssetsFile( context, "ico_mark_help.png" ); //BitmapFactory.decodeResource(res, R.drawable.ic_mark_help1);
        }
    }

    /**
     *
     * @param url           原始url
     * @param srcBitmap     原始图片
     * @param correctResult 批改结果
     * @return 合成的图片
     */
    public Bitmap getCorrectBitmap( String url, Bitmap srcBitmap, String correctResult, String ekpoint ){
        String md5 = MD5Util.getMD5String( url+correctResult );
        Bitmap bitmap = (Bitmap) CacheManager.getAppCache().getCache( md5 );
        if( bitmap != null ) return bitmap;

        bitmap = createCorrectBitmap( srcBitmap, correctResult, ekpoint );
        if(bitmap!=null) CacheManager.getAppCache().putCache( md5, bitmap, AppConst.MAX_EXPIRE_TIME );
        return bitmap;
    }

    private Bitmap createCorrectBitmap( Bitmap srcBitmap, String correctResult, String ekpoints ){

        Bitmap bitmap = srcBitmap.copy( Bitmap.Config.ARGB_8888, true );
        ArrayList<ImageCorrectBean> list = new Gson().fromJson( correctResult, new TypeToken<ArrayList<ImageCorrectBean>>() {}.getType());
        if( list==null || list.size()==0 ) return srcBitmap;

        Canvas canvas = new Canvas( bitmap );
        for( ImageCorrectBean bean : list ){
            drawCorrectMark( canvas, bean );
        }

        //验证辅助图的绘图框信息
//        if( !TextUtils.isEmpty(questionArea) && questionArea.startsWith("[{") && questionArea.endsWith("}]")){
//            ArrayList<QuestionRect> rects = new Gson().fromJson( questionArea, new TypeToken<ArrayList<QuestionRect>>() {}.getType());
//            if( rects!=null && rects.size()>0 && rects.get(0).getFigure()!=null ){
//                drawFigureRect(canvas,rects.get(0).getFigure() );
//            }
//        }

        //绘制错误知识点
        if( !TextUtils.isEmpty(ekpoints) ){
            drawErrKPoint( canvas, ekpoints );
        }

        return bitmap;
    }

//    private void drawFigureRect( Canvas canvas, QuestionRect figureRect ){
//        if( figureRect==null )return;
//
//        int width = canvas.getWidth();
//        int height= canvas.getHeight();
//
//        Paint paint = new Paint();
//        paint.setColor(Color.BLUE);
//        paint.setStyle(Paint.Style.STROKE);//空心矩形框
//
//        int w = (int)(figureRect.getWidth()*width);
//        int x = (int)(figureRect.getX()*width);
//        int y = (int)(figureRect.getY()*height);
//        int h = (int)(figureRect.getHeight()*height);
//        canvas.drawRect( x, y, x+w, y+h, paint );
//    }
    private void drawErrKPoint( Canvas canvas, String ekpoints ){

        if(TextUtils.isEmpty(ekpoints))
            return;

        //ekpoints = "错误知识点:错误知识点:错误知识点:错误知识点:错误知识点:"+ekpoints;

        //先确认行高
        Paint paint = new Paint();
        paint.setTextSize(DensityUtils.sp2px(mContext,7));
        //paint.setTypeface(AssetsUtils.getMyTypeface(ZxApplication.getApplication()));
        Rect rect = new Rect();
        paint.getTextBounds("豆", 0, 1, rect);

        //绘制标注信息
        //先计算标注的空间
        paint.getTextBounds( ekpoints, 0, ekpoints.length(), rect);
        paint.setColor(Color.rgb(246,137,119));

        int startx = canvas.getWidth()*2/3;
        int starty = canvas.getHeight()/2+rect.height()/2;

        if( rect.width() > canvas.getWidth() ){
            //最多支持2行
            int pos = ekpoints.length() / 2;
            String ekpoints1 = ekpoints.substring(0,pos);
            String ekpoints2 = ekpoints.substring(pos);
            paint.getTextBounds( ekpoints1, 0, ekpoints1.length(), rect);

            //第一行
            if( startx+rect.width()>canvas.getWidth() ){
                startx = canvas.getWidth() - 10 - rect.width();
                starty = starty - rect.height()/2-4;
            }
            if(starty-rect.height()<0 ){
                starty = rect.height();
            }
            canvas.drawText( ekpoints1, startx, starty, paint );

            starty = starty + rect.height()+8;
            canvas.drawText( ekpoints2, startx, starty, paint );

        }else{
            //单行
            if( startx+rect.width()>canvas.getWidth() ){
                startx = canvas.getWidth() - 10 - rect.width();
            }
            if(starty-rect.height()<0 ){
                starty = rect.height();
            }
            canvas.drawText( ekpoints, startx, starty, paint );
        }

    }

    private void drawCorrectMark( Canvas canvas, ImageCorrectBean correctBean ){

        int width = canvas.getWidth();
        int height= canvas.getHeight();

        boolean bRight = false;
        Bitmap markBitmap;
        if( correctBean.getStatus() == ImageCorrectBean.CORRECT_RIGHT || correctBean.getStatus() == ImageCorrectBean.CORRECT_SMALL_RIGHT ){
            markBitmap = rightBitmap;
            bRight = true;
        }else if( correctBean.getStatus() == ImageCorrectBean.CORRECT_HALF || correctBean.getStatus() == ImageCorrectBean.CORRECT_SMALL_HALF ){
            markBitmap = halfBitmap;
        }else if( correctBean.getStatus() == ImageCorrectBean.CORRECT_WRONG   || correctBean.getStatus() == ImageCorrectBean.CORRECT_SMALL_WRONG ){
            markBitmap = wrongBitmap;
        }else /*if( correctBean.getStatus() == ImageCorrectBean.CORRECT_DOUBT_WRONG )*/{    //其他   疑问处理
            markBitmap = doubtBitmap;
        }

        Rect srcRect = new Rect();
        srcRect.top = 0;
        srcRect.left = 0;
        srcRect.right = markBitmap.getWidth();
        srcRect.bottom= markBitmap.getHeight();

        Rect dstRect = new Rect();
        dstRect.left = (int)( width*correctBean.getPos().getX() );
        dstRect.top  = (int)(height*correctBean.getPos().getY() );
        dstRect.right = (int)(width*correctBean.getSize().getWidth() ) + dstRect.left;
        dstRect.bottom= (int)(height*correctBean.getSize().getHeight())+ dstRect.top;

        //先确认行高
        Paint paint = new Paint();
        paint.setTextSize(DensityUtils.sp2px(mContext,8));
        Rect rect = new Rect();
        paint.getTextBounds("豆", 0, 1, rect);

        int lh = rect.height();

        int h = dstRect.height(), w = dstRect.width();
        int left = dstRect.left;
        int top  = dstRect.top;

        if( bRight || lh+10 >= canvas.getHeight() ){       //对 或者 图片太小  不处理
            canvas.drawBitmap( markBitmap, srcRect, dstRect, new Paint());
        }else{
            //需要处理的
            //目标图缩小50% 高度比较
            h = dstRect.height()*4/5;
            w = dstRect.width()*4/5;
            left = dstRect.left+w/5;
            top  = dstRect.top+h/5;
            dstRect.set( left, top, left+w, top+h );

            canvas.drawBitmap( markBitmap, srcRect, dstRect, new Paint());
        }

        String text = correctBean.getCommentContent();
        if(TextUtils.isEmpty(text))
            return;

        //绘制标注信息
        //先计算标注的空间
        paint.getTextBounds( text, 0, text.length(), rect);

        int startx = left+w;
        int starty = top+h;//rect.height();

        if( startx+rect.width()>canvas.getWidth() ){
            startx = canvas.getWidth() - rect.width();
            starty = starty + rect.height();
        }
        if(starty+rect.height()>canvas.getHeight() ){
            starty = canvas.getHeight() - rect.height();
        }
        paint.setColor(Color.RED);
        canvas.drawText( text, startx, starty, paint );
    }
}
