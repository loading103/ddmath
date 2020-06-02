package com.tsinghuabigdata.edu.ddmath.module.floatwindow;

import android.content.Context;
import android.text.TextUtils;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.module.floatwindow.bean.BubbleBean;
import com.tsinghuabigdata.edu.ddmath.util.AppLog;

import java.util.ArrayList;

/**
 * 气泡内容管理器
 */

public class BubbleManager {

    //存放动态显示的列表
    private  ArrayList<BubbleBean> dynamicList = new ArrayList<>();
    //存放界面专属的内容
    private ArrayList<BubbleBean> specialList = new ArrayList<>();
    //通用数据内容
    private ArrayList<BubbleBean> communalList = new ArrayList<>();

    public BubbleManager(Context context){

        //预置
        String[] array = context.getResources().getStringArray(R.array.cartoon_dd_bubble);
        for (String item : array ) {
            String[] data = item.split("#");
            BubbleBean bean = new BubbleBean( Integer.valueOf(data[0]), data[1], data[2] );
            if( BubbleBean.TYPE_COMMON == bean.getType() ){
                communalList.add( bean );
            }else{
                specialList.add( bean );
            }
        }

        //动态添加内容
        dynamicGenerateBubble();
    }

    //在指定界面下获取对话数据
    public BubbleBean getBubbleData( String uiname ){

        //先生成随机数0-1000
        int random = (int)(Math.random() * 1000);
        AppLog.d( "dsdfdsf random = " + random );
        //0-400,动态内容，400-800，显示专属， 800-1000显示通用， 如果对应的专属没有，则显示通用
        ArrayList<BubbleBean> list = null;
        if( random < 600 && dynamicList.size() > 0 ){
            list = dynamicList;
        }else if( random < 900 ){
            list = getSpecialListByUiName( uiname );
        }

        if( list==null || list.size() == 0 ){
            list = communalList;
        }

        int index = random % list.size();
        return list.get( index );
    }

    //添加指定内容
    public void addBubbleData( BubbleBean bean ){
        dynamicList.add( bean );
    }

    //添加指定内容
    public void removeBubble( BubbleBean bean ){
        dynamicList.remove( bean );
    }
    //-------------------------------------------------------------------
    //通过界面名称 获取对应界面的专属数据
    private ArrayList<BubbleBean> getSpecialListByUiName(String uiname) {

        //没有输入界面名称 或者 APP级别 返回全部专属数据
        if(TextUtils.isEmpty(uiname) || BubbleBean.UI_APP.equals(uiname) ){
            return specialList;
        }

        ArrayList<BubbleBean> list = new ArrayList<>();
        for( BubbleBean bean : specialList ){
            if( uiname.equals( bean.getUiname()) )
                list.add( bean );
        }
        return list;
    }

    //动态添加内容，直接添加到通用里面
    private void dynamicGenerateBubble(){
        BubbleBean bean = new BubbleBean( 0, "", "太晚了,小豆自己也困了!" );
        communalList.add( bean );

        communalList.add( new BubbleBean( 0, "", "一日之计在于晨!" ) );

        communalList.add( new BubbleBean( 0, "", "早起的鸟儿有虫吃!" ) );

        communalList.add( new BubbleBean( 0, "", "石油的生成要经历漫长的岁月!" ) );
    }


}
