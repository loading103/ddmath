package com.tsinghuabigdata.edu.ddmath.module.ddwork;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tsinghuabigdata.edu.ddmath.constant.AppConst;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.DDWorkDetail;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.LocalPageInfo;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.LocalQuestionInfo;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.LocalWorkInfo;
import com.tsinghuabigdata.edu.ddmath.util.AppLog;
import com.tsinghuabigdata.edu.ddmath.util.ContextUtils;
import com.tsinghuabigdata.edu.ddmath.util.FileUtil;

import org.json.JSONArray;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;

/**
 * 豆豆作业本地数据管理器
 */
public class DDWorkManager {


    //文件保存机制: 本地磁盘/ddmath/用户名/waitwork.json下面
    //图片保存在：  本地磁盘/ddmath/用户名/image/
    private static final String DATA_PATH = "/username/ddwork/works.json";
    private static final String IMAGE_PATH = "/username/ddwork/image/";

    private static String mImagePath;
    private static String mDataPath;

    private static String mUserName;

    //单例模式
    private static DDWorkManager mLocalDataManager;

    private ArrayList<LocalWorkInfo> localWorkList = new ArrayList<>();
    private ArrayList<WorkCommitListener> mCommitListeners = new ArrayList<>();

    //private Context mContext;
    /**
     * 用户本地作业图片管理
     * @param context  上下文
     * @param usrname  用户登录名
     * @return dd
     */
    public static DDWorkManager getDDWorkManager(Context context, String usrname ){

        String path = ContextUtils.getExternalCacheDir( context, AppConst.APP_NAME);

//        AppLog.d("dsgdfsgdfgdsafdsds path = " + path );
        mDataPath = path + DATA_PATH.replace("username", usrname);
        mImagePath= path + IMAGE_PATH.replace("username", usrname);

        if( mLocalDataManager == null ){
            mLocalDataManager = new DDWorkManager( context );
        }else if( mUserName!=null && !mUserName.equals(usrname) ){
            mLocalDataManager = new DDWorkManager( context );     //更换用户
        }
        mUserName = usrname;
        return mLocalDataManager;
    }

    public static DDWorkManager getDDWorkManager(){ return mLocalDataManager; }


    //--------------------------------------------------------------------------------------
    public static String getImagePath(){
        return mImagePath;
    }

    LocalWorkInfo relateLocalWorkInfo( DDWorkDetail workDetail ){

        LocalWorkInfo localWorkInfo = getWorkInfo( workDetail.getWorkId() );
        if( localWorkInfo == null ){
            localWorkInfo = new LocalWorkInfo();
            localWorkList.add( localWorkInfo );
            localWorkInfo.setWorkId( workDetail.getWorkId() );
        }
        //
        for( LocalPageInfo pageInfo : workDetail.getPageInfo() ){
            LocalPageInfo localPageInfo = getPageInfo(localWorkInfo.getPageList(), pageInfo.getPageNum(), pageInfo.getChapterName());
            if( localPageInfo == null ){
                localPageInfo = new LocalPageInfo( workDetail.getWorkId() );
                localWorkInfo.getPageList().add( localPageInfo );
            }
            localPageInfo.setSelected( false );
            localPageInfo.copy( pageInfo );
        }
        //saveData();
        return localWorkInfo;
    }
    private LocalPageInfo getPageInfo( ArrayList<LocalPageInfo> list, int pageNume, String chapterName ){
        if( list==null || list.size()==0 ) return null;
        for( LocalPageInfo pageInfo : list ){
            if(!TextUtils.isEmpty(chapterName)){
                if( pageInfo.getPageNum() == pageNume && chapterName.equals(pageInfo.getChapterName() )){
                    return pageInfo;
                }
            }else{
                if(pageInfo.getPageNum() == pageNume) return pageInfo;
            }
        }
        return null;
    }

    /**
     * 根据作业ID 获取作业本地对象信息
     * @param workId ID
     * @return 作业对象 或者 null
     */
    public LocalWorkInfo getWorkInfo( String workId ){
        for( LocalWorkInfo workInfo : localWorkList ){
            if( !TextUtils.isEmpty(workInfo.getWorkId()) && workInfo.getWorkId().equals( workId ) )
                return workInfo;
        }
        return null;
    }

    //返回指定作业已经拍照的图片数量
    public int getHavePageImage( String workId ){
        int count = 0;
        LocalWorkInfo workInfo = getWorkInfo( workId );
        if( workInfo !=null && workInfo.getPageList() != null ){
            for( LocalPageInfo pageInfo : workInfo.getPageList() ){
                if( !TextUtils.isEmpty(pageInfo.getLocalpath()) ){
                    if( new File( pageInfo.getLocalpath()).exists() ){
                        count++;
                    }else{
                        pageInfo.init();
                        AppLog.i("dfdsfdsfsd page = "+pageInfo.getPageNum() +", workId = " + workInfo.getWorkId() );
                    }
                }
            }
        }
        return count;
    }

    public ArrayList<WorkCommitListener> getWorkCommitListeners(){
        return mCommitListeners;
    }

    public void addCommitListener( WorkCommitListener listener ){
        mCommitListeners.add(listener);
    }

    public void removeCommitListener( WorkCommitListener listener ){
        mCommitListeners.remove(listener);
    }

    //------------------------------------------------------------------------------
    //隐藏构造函数
    private DDWorkManager(Context context ){

        FileUtil.createPath( context, mImagePath );

        localWorkList.clear();
        mCommitListeners.clear();
        loadData();

        clearImageData();
    }

    //加载本地存储的数据信息
    private void loadData(){

        //先判断文件是否存在
        File file = new File( mDataPath );
        if( !file.exists() || file.length() == 0 )
            return;

        FileInputStream fis = null;
        try {
            fis =  new FileInputStream( file );

            byte buffer[] = new byte[(int)file.length()];
            int len = fis.read( buffer );
            if( len == file.length() ){
                //数据获取正确
                String data = new String( buffer );
                ArrayList<LocalWorkInfo> list = new Gson().fromJson( data, new TypeToken<ArrayList<LocalWorkInfo>>() {}.getType());
                localWorkList.addAll( list );

                fis.close();
                fis = null;
            }else{
                //读数据异常，放弃文件
                fis.close();
                fis = null;
                boolean b = file.delete();
                AppLog.d("delete file b = " + b );
            }
        }catch (Exception e){
            AppLog.i( "", e );
        }finally {
            if( fis!=null ){
                try {
                    fis.close();
                }catch (Exception ee ){
                    AppLog.i( "", ee );
                }
            }
        }
    }

    //加载本地存储的数据信息
    public boolean saveData(){

        //先判断文件是否存在，存在，删除
        File file = new File( mDataPath );
        if( file.exists() ){
            boolean b = file.delete();
            AppLog.d("delete file b = " + b );
        }

        //数组转为字符串
        String data = convertJsonStr();

        FileOutputStream fos = null;
        try {
            fos =  new FileOutputStream( file );

            fos.write( data.getBytes() );
            fos.flush();
            fos.close();

            return true;
        }catch (Exception e){
            AppLog.i( "", e );
        }finally {
            if( fos!=null ){
                try {
                    fos.close();
                }catch (Exception ee ){
                    AppLog.i( "", ee );
                }
            }
        }
        return false;
    }

    /**
     * list数据转为jsonarray str
     */
    private String convertJsonStr(){
        JSONArray jsonArray = new JSONArray();

        for( LocalWorkInfo workBean : localWorkList ){
            //已提交的作业   原版教辅作业 都不保存
            if( workBean.getWorkStatus() != LocalWorkInfo.WORK_COMMITED && !isLmWorkType(workBean.getWorkId()) ){
                jsonArray.put( workBean.getJsonObject() );
            }
        }
        return jsonArray.toString();
    }
    private boolean isLmWorkType( String workId ){
        return !TextUtils.isEmpty(workId) && workId.startsWith("DDLM");
    }

    /**
     * 清理本地图片
     */
    private void clearImageData(){

        //先获取目录下面的所有图片
        File uploadDir = new File( mImagePath );
        File[] files = uploadDir.listFiles();
        // 如果是空文件夹则删除
        if (files == null || files.length == 0) {
            return;
        }
        ArrayList<String> mList = new ArrayList<>();
        for( File file : files ){
            if( file.isFile() && file.getName().endsWith( AppConst.IMAGE_SUFFIX_NAME ) ){
                mList.add( file.getAbsolutePath() );
            }
        }

        //从列表中去掉还需要的图片
        for( LocalWorkInfo workBean : localWorkList ){
            ArrayList<LocalPageInfo> list = workBean.getPageList();
            if(list!=null)for( LocalPageInfo pageInfo : list ){
                mList.remove( pageInfo.getLocalpath() );    //作业大图
                if(pageInfo.getQuestions()!=null)for(LocalQuestionInfo questionInfo:pageInfo.getQuestions() ){
                    mList.remove( questionInfo.getLocalpath() );    //每道题目的答案图
                }
            }
        }

        //删除不需要的图片
        for( String fname : mList ){
            boolean b = (new File(fname)).delete();
            AppLog.d("delete file b = " + b );
        }

    }

}
