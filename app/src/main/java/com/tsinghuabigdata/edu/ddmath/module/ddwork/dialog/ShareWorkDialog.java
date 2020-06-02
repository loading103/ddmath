package com.tsinghuabigdata.edu.ddmath.module.ddwork.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.os.Environment;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.bean.LoginInfo;
import com.tsinghuabigdata.edu.ddmath.bean.UserDetailinfo;
import com.tsinghuabigdata.edu.ddmath.constant.AppConst;
import com.tsinghuabigdata.edu.ddmath.constant.AppRequestConst;
import com.tsinghuabigdata.edu.ddmath.constant.BaseConfig;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.ShareBean;
import com.tsinghuabigdata.edu.ddmath.module.mycenter.HeadImageUtils;
import com.tsinghuabigdata.edu.ddmath.module.mycenter.bean.RegRewardBean;
import com.tsinghuabigdata.edu.ddmath.module.myscore.ScoreEventID;
import com.tsinghuabigdata.edu.ddmath.module.myscore.UserScoreUtil;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.AppUtils;
import com.tsinghuabigdata.edu.ddmath.util.BitmapUtils;
import com.tsinghuabigdata.edu.ddmath.util.DensityUtils;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.PicassoUtil;
import com.tsinghuabigdata.edu.ddmath.util.QRCodeUtil;
import com.tsinghuabigdata.edu.ddmath.util.ShareUtils;
import com.tsinghuabigdata.edu.ddmath.util.ToastUtils;
import com.tsinghuabigdata.edu.ddmath.view.CircleImageView;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;


/**
 * 分享 Dialog
 */
public class ShareWorkDialog extends Dialog  implements View.OnClickListener, PlatformActionListener {


    private LinearLayout shareLayout;
    private ImageView shareImage;

    private CircleImageView headImageView;
//    private ShareItemView firstView;
//    private ShareItemView secondView;
//    private ShareItemView thirdView;
    private TextView usernameView;
    private TextView shareTextView;

    private ShareListener shareListener;

    private Context mContext;
    private String shareUrl = "";
    private String mConetntId;

    public ShareWorkDialog(Context context, int theme) {
        super(context, theme);
        mContext = context;
        initData();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.view_closehitlayout:
                if (shareListener != null) shareListener.dealShare();
                dismiss();
                break;
            case R.id.view_share_weixin:
                share(ShareUtils.PLAT_WEIXIN);
                MobclickAgent.onEvent( getContext(), "share_weixin" );
                break;
            case R.id.view_share_QQ:
                share(ShareUtils.PLAT_QQ);
                MobclickAgent.onEvent( getContext(), "share_qq" );
                break;
            case R.id.view_share_weibo:
                share(ShareUtils.PLAT_WEIBO);
                break;
            case R.id.view_share_wxcircle:
                share(ShareUtils.PLAT_WXCIRCLE);
                MobclickAgent.onEvent( getContext(), "share_wxcircle" );
                break;
            case R.id.view_share_qzone:
                share(ShareUtils.PLAT_QZONE);
                MobclickAgent.onEvent( getContext(), "share_qzone" );
                break;
            default:
                break;
        }
    }

    public void setConetntId(String id) {
        this.mConetntId = id;
    }

    public void setData(String url, ArrayList<ShareBean> list) {
        if (list == null || list.size() == 0) {
            //外部处理
            return;
        }

        //加载分享图片
        LoginInfo logininfo = AccountUtils.getLoginUser();
        if (logininfo != null) {
            url = logininfo.getFileServer() + url;
        }
        PicassoUtil.getPicasso(getContext()).load(url).into(shareImage);

        //用户昵称
        UserDetailinfo detailInfo = AccountUtils.getUserdetailInfo();
        if( detailInfo!=null ){
            usernameView.setText( detailInfo.getNickName() );
        }

        //分享内容
        shareTextView.setText( getShareData( list ) );

//        if (list.size() == 1) {
//            firstView.setData(list.get(0));
//        } else if (list.size() == 2) {
//            firstView.setData(list.get(0));
//            secondView.setData(list.get(1));
//        } else {
//            firstView.setData(list.get(0));
//            secondView.setData(list.get(1));
//            thirdView.setData(list.get(2));
//        }

        //用户头像
        HeadImageUtils.setHeadImage( headImageView, R.drawable.doudou_portrait_default );
    }

    public void setShareListener(ShareListener listener) {
        shareListener = listener;
    }

    @Override
    public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
        if (shareListener != null) shareListener.dealShare();
        ToastUtils.show(getContext(), "分享成功");
        if( !TextUtils.isEmpty(mConetntId) ) UserScoreUtil.addUserScore(ScoreEventID.EVENT_SHARE_EXERHOME,mConetntId);
        dismiss();
    }

    @Override
    public void onError(Platform platform, int i, Throwable throwable) {
        if (platform.getName().contains("Wechat")) {
            ToastUtils.show(getContext(), "分享失败，请检查是否安装了微信。");
        } else if (platform.getName().contains("QZone") || platform.getName().contains("QQ")) {
            ToastUtils.show(getContext(), "分享失败，请检查是否安装了QQ。");
        }
    }

    @Override
    public void onCancel(Platform platform, int i) {
    }

    public void onBackPressed() {
        if (shareListener != null) {
            shareListener.dealShare();
        }
        super.onBackPressed();
    }

    public interface ShareListener {
        void dealShare();
    }

    //--------------------------------------------------------------------------
    private SpannableStringBuilder getShareData(ArrayList<ShareBean> list){

        StringBuilder sb = new StringBuilder();
        sb.append("本次作业");
        boolean first = true;
        for( ShareBean shareBean : list ){

            if( first ){
                first = false;
            }else{
                sb.append("，");
            }

            //全对数量
            if( ShareBean.TYPE_RIGHTALL == shareBean.getType() ){
                String data = String.valueOf( shareBean.getValue() );
                shareBean.setStartpos( sb.length() );
                shareBean.setLen( data.length() );
                sb.append( data ).append("题正确");
            }
            //设置连击数据
            else if( ShareBean.TYPE_SERIESHIT == shareBean.getType() ){
                String data = String.valueOf( shareBean.getValue() );
                shareBean.setStartpos( sb.length() );
                shareBean.setLen( data.length() );
                sb.append( data ).append("连击");
            }
            //设置正确率
            else if( ShareBean.TYPE_RIGHTRATE == shareBean.getType() ){
                String data = String.valueOf(shareBean.getValue());
                sb.append( TextUtils.isEmpty(shareBean.getName())?"正确率":shareBean.getName() );
                shareBean.setStartpos( sb.length() );
                shareBean.setLen( data.length()+1 );
                sb.append( data ).append("%");
            }
            //提交名称
            else if( ShareBean.TYPE_SUBMITWORK == shareBean.getType() ){
                String data = String.valueOf(shareBean.getValue());
                sb.append( "第" );
                shareBean.setStartpos( sb.length() );
                shareBean.setLen( data.length() );
                sb.append( data ).append("个提交");
            }
            //班级排名
            else if( ShareBean.TYPE_CLASSRANK == shareBean.getType() ){
                String data = String.valueOf(shareBean.getValue());
                sb.append( "班级第" );
                shareBean.setStartpos( sb.length() );
                shareBean.setLen( data.length() );
                sb.append( data ).append("名");
            }
        }
        sb.append("。");

        SpannableStringBuilder ssb = new SpannableStringBuilder( sb.toString() );
        for( ShareBean shareBean : list ){
            ssb.setSpan( new ForegroundColorSpan( getContext().getResources().getColor( R.color.color_F97F3A) ), shareBean.getStartpos(), shareBean.getStartpos()+shareBean.getLen(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE );
        }
        return ssb;
    }
    private void initData() {

        setContentView(GlobalData.isPad() ? R.layout.view_ddwork_share : R.layout.view_ddwork_share_phone);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            if (window != null) {
                window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            }
        }
        //关闭按钮
        RelativeLayout closeLayout = (RelativeLayout) findViewById(R.id.view_closehitlayout);
        closeLayout.setOnClickListener(this);

        //要展示的内容
        shareLayout= (LinearLayout)findViewById( R.id.view_sharelayout );
        shareImage = (ImageView) findViewById(R.id.view_shareimage);

        headImageView=(CircleImageView)findViewById( R.id.iv_head );
//        firstView = (ShareItemView) findViewById(R.id.view_share_firstItem);
//        secondView = (ShareItemView) findViewById(R.id.view_share_secondItem);
//        thirdView = (ShareItemView) findViewById(R.id.view_share_thirdItem);
        usernameView = (TextView)findViewById( R.id.view_share_username );
        shareTextView= (TextView)findViewById( R.id.view_share_sharetext );

        //分享到
        ImageView shareView;
        shareView = (ImageView) findViewById(R.id.view_share_weixin);
        shareView.setOnClickListener(this);
        shareView = (ImageView) findViewById(R.id.view_share_QQ);
        shareView.setOnClickListener(this);
        shareView = (ImageView) findViewById(R.id.view_share_weibo);
        shareView.setOnClickListener(this);
        shareView = (ImageView) findViewById(R.id.view_share_wxcircle);
        shareView.setOnClickListener(this);
        shareView = (ImageView) findViewById(R.id.view_share_qzone);
        shareView.setOnClickListener(this);
    }

    private void share(String plat) {
        //
        //ToastUtils.show(mContext, "开始分享。");

        String path = getShareBitmap();
        if(TextUtils.isEmpty(path)){
            ToastUtils.show(mContext, "分享失败，请重试.");
            return;
        }

        new ShareUtils().share(getContext(), shareUrl, plat, path, this);
    }

    //生成要分享的图片
    private String getShareBitmap() {

        String filePath = Environment.getExternalStorageDirectory().toString() + AppConst.IMAGE_DIR;
        Bitmap bitmap = BitmapUtils.loadBitmapFromViewBySystem( shareLayout );

        //原图宽高
        int width = bitmap.getWidth();
        int height= bitmap.getHeight();

        //计算出新的图片高度
        //上下各留20px高度，计算高度，文字 行距:10 * 2, 段间距：20
        int newheight = height + 20*2 + 10*2 + 20;

        Paint paint = new Paint();
        paint.setFakeBoldText( true );
        paint.setTextSize(DensityUtils.sp2px( getContext(), 14) );

        Rect rect = new Rect();
        String appName = AppUtils.getAppName();
        String text = appName;
        paint.getTextBounds( text, 0, text.length(), rect);
        newheight += rect.height()*2;      //文字高
        int ddh = rect.height();

        //
        paint.setFakeBoldText( false );
        paint.setTextSize(DensityUtils.sp2px( getContext(), 10) );

        text = "长按二维码，加入" + appName + "，开始诊断吧。";
        paint.getTextBounds( text, 0, text.length(), rect);
        newheight += rect.height()*2;//文字高
        int tiph = rect.height();

        //绘制到一张图上面
        Bitmap dstBitmap = Bitmap.createBitmap( width, newheight, Bitmap.Config.ARGB_4444 );
        Canvas canvas = new Canvas( dstBitmap );
        canvas.drawColor( Color.rgb(0xff,0xfe, 0xf7) );
        //绘制原图
        canvas.drawBitmap( bitmap, 0, 0, paint );

        int startx = 20;

        //绘制文本
        paint.setFakeBoldText( true );
        paint.setTextSize(DensityUtils.sp2px( getContext(), 14) );
        paint.setColor( Color.rgb(0x13,0xc1,0xd2) );
        int offh = 20 + ddh;            //高度上面的偏移
        canvas.drawText( appName, startx, height+offh, paint );
        offh = offh + 10 + ddh;
        canvas.drawText( "作业诊断+提分专家", startx, height+offh, paint );

        paint.setFakeBoldText( false );
        paint.setTextSize(DensityUtils.sp2px( getContext(), 10) );
        paint.setColor( Color.rgb(0x99,0x99,0x99) );
        offh = offh + 20 + tiph;
        canvas.drawText( "长按二维码，加入"+appName+"，开始诊断吧。", startx, height+offh, paint );
        offh = offh + 10 + tiph;
        RegRewardBean bean = AccountUtils.getRegRewardBean();
        if( bean == null ){
            text = String.format( Locale.getDefault(), "注册即可获得价值%d元的%d学豆哦！", 30, 300);
        }else {
            text = String.format( Locale.getDefault(), "注册即可获得价值%d元的%d学豆哦！", bean.getRegWithRecWard()/10, bean.getRegWithRecWard()  );
        }
        canvas.drawText( text, startx, height+offh, paint );

        LoginInfo loginInfo = AccountUtils.getLoginUser();
        if( loginInfo == null ) return null;
        //绘制二维码
        String channelId = getContext().getString(R.string.channelId);
        if( appName.contains("豆豆数学") ){     //豆豆数学的推广算个人的
            channelId = AppConst.WORK_SHARE_CHANNELID;
        }
        String url = BaseConfig.WEB_ADDRESS + String.format(Locale.getDefault(), AppRequestConst.URL_SHARE_RIGISTER, channelId, loginInfo.getAccountId() );
        shareUrl = url;
        Bitmap logoBitmap = BitmapFactory.decodeResource( getContext().getResources(), R.drawable.ic_launcher );
        Bitmap qrcode = QRCodeUtil.createQRCodeBitmap( url, 400, logoBitmap, 0.3f );
        if( qrcode == null )
            return null;

        //可用高度
        int uh = 20+offh;

        Matrix matrix = new Matrix();
        float rate = uh*1f/400;
        matrix.postScale( rate, rate );
        matrix.postTranslate( width-20-rate*400, newheight-10-uh );
        canvas.drawBitmap( qrcode, matrix, paint);

        String fpath = BitmapUtils.saveImage( filePath, dstBitmap );

        dstBitmap.recycle();
        return fpath;
    }
}
