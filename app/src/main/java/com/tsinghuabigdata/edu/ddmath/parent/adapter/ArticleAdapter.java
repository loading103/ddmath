package com.tsinghuabigdata.edu.ddmath.parent.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ImageSpan;
import android.widget.TextView;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.adapter.CommonAdapter;
import com.tsinghuabigdata.edu.ddmath.adapter.ViewHolder;
import com.tsinghuabigdata.edu.ddmath.parent.bean.ArticleItemBean;
import com.tsinghuabigdata.edu.ddmath.util.DateUtils;
import com.tsinghuabigdata.edu.ddmath.util.DensityUtils;
import com.tsinghuabigdata.edu.ddmath.util.PicassoUtil;
import com.tsinghuabigdata.edu.ddmath.view.RoundImageView;

import java.util.List;


/**
 * Created by Administrator on 2018/7/4.
 */

public class ArticleAdapter extends CommonAdapter<ArticleItemBean> {

    public ArticleAdapter(Context context, List<ArticleItemBean> mDatas) {
        super(context, mDatas);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.item_article;
    }

    @Override
    protected void convert(ViewHolder helper, int position, ArticleItemBean item) {
        RoundImageView iv = helper.getView(R.id.iv_article);
        //PicassoUtil.displayImage(item.getImgUrl(), iv);
        PicassoUtil.displayWithSpecImage(item.getImgUrl(), iv, R.drawable.morentupian);
        TextView tvTitle = helper.getView(R.id.tv_title);
        if (item.getTop() == ArticleItemBean.TOP) {
           /* SpannableString spannableString = new SpannableString("置顶 " + item.getArticleName());
            //设置颜色
            spannableString.setSpan(new ForegroundColorSpan(Color.WHITE), 0, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new BackgroundColorSpan(Color.parseColor("#F8AD00")), 0, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            //设置字体大小，true表示前面的字体大小20单位为dip
            spannableString.setSpan(new AbsoluteSizeSpan(12, true), 0, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            //设置字体，BOLD为粗体
//            spannableString.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            tvTitle.setText(spannableString);*/
            String str = "   " + item.getArticleName();
            SpannableString spannableString = new SpannableString(str);//textView控件
            ImageSpan imageSpan = new ImageSpan(getDrawable(), DynamicDrawableSpan.ALIGN_BASELINE);
            spannableString.setSpan(imageSpan, 0, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);//注意这里的字符串长度，我项目需求是图片添加到最后，但是SpannableString没有放在最后的方法，所以我在字符串的长度后面加了2个空格  这样就能放最后了
            tvTitle.setText(spannableString);

        } else {
            tvTitle.setText(item.getArticleName());
        }
        String date = DateUtils.getCurrDateStr(item.getCreateTime());
        helper.setText(R.id.tv_time, date);
        helper.setText(R.id.tv_read_Count, item.getReadCount() + "浏览");
        helper.setText(R.id.tv_like_Count, item.getLikeCount() + "赞");
    }

    private Drawable getDrawable() {
        Drawable drawable = mContext.getResources().getDrawable(R.drawable.ic_top);
        drawable.setBounds(0, 0, getPx(27), getPx(17)); //设置边界
        return drawable;
    }

    private int getPx(int i) {
        return DensityUtils.dp2px(mContext, i);
    }
}
