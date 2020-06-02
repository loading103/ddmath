package com.tsinghuabigdata.edu.ddmath.module.learnmaterial.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.adapter.CommonAdapter;
import com.tsinghuabigdata.edu.ddmath.adapter.ViewHolder;
import com.tsinghuabigdata.edu.ddmath.module.learnmaterial.bean.OriginalBookBean;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.PicassoUtil;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * 原版教辅列表适配器
 * Created by Administrator on 2018/1/16.
 */

public class SelectBookAdapter extends CommonAdapter<OriginalBookBean> {


    public SelectBookAdapter(Context context, List<OriginalBookBean> mDatas) {
        super(context, mDatas);
    }

    @Override
    protected int getLayoutId() {
        if (GlobalData.isPad()) {
            return R.layout.item_select_book;
        } else {
            return R.layout.item_select_book_phone;
        }
    }

    @Override
    protected void convert(ViewHolder helper, final int position, OriginalBookBean item) {
        helper.setText(R.id.tv_book_name, item.getBookName());
        helper.setText(R.id.tv_book_version, item.getPublishers());
        helper.setText(R.id.tv_print_times, item.getVersion());
        if (item.getUseTimes() > 0) {
            SimpleDateFormat sdf = new SimpleDateFormat("上次使用时间：yyyy.MM.dd  HH:mm:ss");
            String time = sdf.format(item.getLastModifyTime());
            helper.setText(R.id.tv_modify_time, time);
        } else {
            helper.setText(R.id.tv_modify_time, null);
        }

        // 控制是否显示 hot  及信息
        helper.setText(R.id.tv_hot_usetimes, String.format(Locale.getDefault(),"%d人次在使用该教辅", item.getUseCount()) );
        helper.getView( R.id.tv_hot_usetimes ).setVisibility( item.getUseCount()>0?View.VISIBLE:View.INVISIBLE );

        RelativeLayout rlMark = helper.getView(R.id.rl_selected_mark);
        rlMark.setVisibility( item.isSelect()?View.VISIBLE:View.INVISIBLE);

        ImageView ivBookCover = helper.getView(R.id.iv_book_cover);
        PicassoUtil.displaySpecificImage(item.getCoverPicture(), ivBookCover, R.drawable.myjiaofu);

    }


}
