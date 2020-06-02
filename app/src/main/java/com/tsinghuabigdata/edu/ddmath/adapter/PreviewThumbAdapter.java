package com.tsinghuabigdata.edu.ddmath.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.inter.MyItemClickListener;
import com.tsinghuabigdata.edu.ddmath.util.AppLog;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.PicassoUtil;

import java.io.File;
import java.util.List;


/**
 * Created by fish on 2016/10/20.
 */

public class PreviewThumbAdapter extends RecyclerView.Adapter {

    private Context      mContext;
    private List<String> mList;
    private int          curPosition;
    private MyItemClickListener mItemClickListener;

    public PreviewThumbAdapter(Context context, List<String> data, int index) {
        mContext = context;
        mList = data;
        curPosition = index;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View root = LayoutInflater.from(mContext).inflate(GlobalData.isPad()?R.layout.item_picture_preview:R.layout.item_picture_preview_phone, parent, false);
        return new PreviewHolder(root,mItemClickListener);
    }

    /**
     * 设置Item点击监听
     * @param listener
     */
    public void setOnItemClickListener(MyItemClickListener listener){
        this.mItemClickListener = listener;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        PreviewHolder holder1 = (PreviewHolder) holder;
        holder1.tv.setText(String.valueOf(position + 1));
        String url = mList.get(position);
        if (url.startsWith("http")) {
            String comUrl = url;
            try{
                if( comUrl.endsWith(".jpg") ){
                    comUrl = comUrl.replace(".jpg","") + ",124x124.jpg";
                }else if( comUrl.endsWith(".jpeg") ){
                    comUrl = comUrl.replace(".jpeg","") + ",124x124.jpeg";
                }
            }catch (Exception e){
                AppLog.i("",e );
            }
            PicassoUtil.getPicasso(mContext).load(comUrl).error(R.drawable.ic_broken_image).placeholder(R.drawable.ic_temporary_image).into(holder1.iv);
        } else {
            PicassoUtil.getPicasso(mContext).load(new File(url)).error(R.drawable.ic_broken_image).placeholder(R.drawable.ic_temporary_image).resize(124,124).into(holder1.iv);
        }
        if (position == curPosition) {
            holder1.bg.setBackgroundResource(R.drawable.bg_preview_select);
        } else {
            holder1.bg.setBackgroundResource(R.drawable.bg_preview_unselect);
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class PreviewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView iv;
        TextView       tv;
        View bg;
//        LinearLayout   ll;
        MyItemClickListener mListener;

        public PreviewHolder(View itemView,MyItemClickListener listener) {
            super(itemView);
            iv = (ImageView) itemView.findViewById(R.id.iv_preview);
            tv = (TextView) itemView.findViewById(R.id.tv_preview_position);
            bg = itemView.findViewById(R.id.view_preview);
            this.mListener = listener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(mListener != null){
                mListener.onItemClick(v,getPosition());
            }
        }
    }

    public void select(int position) {
        if (position != curPosition) {
            notifyItemChanged(position);
            notifyItemChanged(curPosition);
            curPosition = position;
        }
    }

}
