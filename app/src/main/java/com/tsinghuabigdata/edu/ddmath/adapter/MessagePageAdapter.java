package com.tsinghuabigdata.edu.ddmath.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.viewpager.widget.PagerAdapter;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.constant.AppConst;
import com.tsinghuabigdata.edu.ddmath.module.message.bean.MessageInfo;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.WindowUtils;

import java.util.List;


/**
 *
 */

public class MessagePageAdapter extends PagerAdapter {


    private Context              context;
    private List<MessageInfo>    mList;
    private ClickMessageListener mClickMessageListener;
    private int textColor = Color.WHITE;
    public MessagePageAdapter(Context context, List<MessageInfo> list) {
        this.context = context;
        mList = list;
    }

    public void setTextColor( int textColor ){
        this.textColor = textColor;
    }
    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(final ViewGroup container, int position) {

        View root = LayoutInflater.from(context).inflate(R.layout.item_roll_textview, null);
        TextView textView = (TextView)root.findViewById(R.id.tv_roll_message);
        textView.setSelected(true);
        textView.setTextColor( textColor );

        int size;
        if (!GlobalData.isPad()) {
            size = 12;
        } else if (small()) {
            size = 18;
        } else {
            size = 24;
        }
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
        //textView.setTextColor(context.getResources().getColor(R.color.white));
//        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//        textView.setLayoutParams(params);
        if (mList.size() > 0) {
            final MessageInfo messageInfo = mList.get(position % mList.size());
            if (messageInfo != null) {
                textView.setText(messageInfo.getDescription());
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mClickMessageListener != null) {
                            mClickMessageListener.clickMessagePage(messageInfo);
                        }
                    }
                });
            }
        }
        container.addView(root);
        return root;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    public void setClickMessageListener(ClickMessageListener clickMessageListener) {
        mClickMessageListener = clickMessageListener;
    }

    public interface ClickMessageListener {
        void clickMessagePage(MessageInfo messageInfo);
    }

    private boolean small() {
        int screenWidthDp = WindowUtils.getScreenWidthDp(context);
        return screenWidthDp < AppConst.NAVI_WIDTH_PAD + 1100;
    }
}
