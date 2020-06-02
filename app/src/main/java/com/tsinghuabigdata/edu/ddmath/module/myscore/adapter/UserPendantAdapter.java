package com.tsinghuabigdata.edu.ddmath.module.myscore.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.tsinghuabigdata.edu.ddmath.MVPModel.RequestListener;
import com.tsinghuabigdata.edu.ddmath.MVPModel.UserCenterModel;
import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.bean.UserDetailinfo;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.event.UpdateUserPendantEvent;
import com.tsinghuabigdata.edu.ddmath.module.myscore.bean.ScoreProductBean;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.AlertManager;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.PicassoUtil;
import com.tsinghuabigdata.edu.ddmath.util.ToastUtils;

import org.greenrobot.eventbus.EventBus;

import javax.annotation.Nonnull;

/**
 * 头像挂件
 */

public class UserPendantAdapter extends ArrayAdapter<ScoreProductBean> {

    private Context mContext;
    private UserDetailinfo detailinfo;
    public UserPendantAdapter(Context context, UserDetailinfo detailinfo) {
        super(context, 0);
        this.mContext = context;
        this.detailinfo = detailinfo;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @Nonnull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate( GlobalData.isPad()?R.layout.item_header_pendant:R.layout.item_header_pendant_mobile, parent, false);
            convertView.setTag(new ViewHolder(convertView));
        }
        ViewHolder viewHolder = ((ViewHolder) convertView.getTag());
        viewHolder.bindView( getItem(position), position);
        return convertView;
    }

    class ViewHolder implements View.OnClickListener{

        private int position;

        private ImageView imageView;
        private TextView nameTextView;
        private Button useButton;

        private ViewHolder(View convertView) {
            imageView  =  convertView.findViewById( R.id.iv_product_image );
            nameTextView=convertView.findViewById( R.id.tv_product_name );
            useButton=convertView.findViewById( R.id.btn_enter_use );
            useButton.setOnClickListener(this);
        }

        public void bindView(ScoreProductBean bean, int position ){
            this.position = position;

            nameTextView.setText( bean.getProductName() );

            if( !TextUtils.isEmpty(bean.getImagePath()) ){
                imageView.setVisibility(View.VISIBLE);
                String url = AccountUtils.getFileServer() + bean.getImagePath();
                PicassoUtil.getPicasso(mContext).load(url).error(R.drawable.ic_broken_image).placeholder(R.drawable.ic_temporary_image).into(imageView);
            }else{
                imageView.setVisibility(View.GONE);
            }

            if( bean.getUseStatus() == 1 ){
                useButton.setText("正在使用");
                useButton.setEnabled( false );
            }else{
                useButton.setText("立即使用");
                useButton.setEnabled( true );
            }
        }

        @Override
        public void onClick(View v) {
            final ScoreProductBean bean = getItem(position);
            if( bean == null || detailinfo == null ) return;

            final String recordId = bean.getRecordId();
//            if( TextUtils.isEmpty(recordId) ){      //使用默认挂件
//                enterUse( bean );
//            }
            //使用指定挂件
            new UserCenterModel().useHeaderPendant(detailinfo.getStudentId(), recordId, new RequestListener<Integer>() {
                @Override
                public void onSuccess(Integer status) {
                    if( status > 0 ){       //成功
                        //取消之前的
                        cancelPendantUse();
                        //设置当前使用的对象
                        bean.setUseStatus( 1 );
                        //刷新本地列表
                        notifyDataSetChanged();

                        //保存新用户信息
                        detailinfo.useHeadPendent( bean.getProductId() );
                        //通知更新用户挂件
                        EventBus.getDefault().post( new UpdateUserPendantEvent());

                        ToastUtils.show( mContext, "保存成功");
                    }else{
                        ToastUtils.show( mContext, "保存失败");
                    }
                }

                @Override
                public void onFail(HttpResponse<Integer> response, Exception ex) {
                    AlertManager.showErrorInfo( mContext, ex );
                }
            });

        }

        //取消当前使用的挂件
        private void cancelPendantUse(){
            int count = getCount();
            for( int i=0; i<count; i++ ){
                ScoreProductBean bean = getItem(i);
                if( bean!=null && bean.getUseStatus()>0 ){
                    bean.setUseStatus( 0 );
                }
            }
        }
    }

}
