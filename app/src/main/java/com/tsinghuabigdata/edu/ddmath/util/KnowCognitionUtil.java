package com.tsinghuabigdata.edu.ddmath.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.NinePatch;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.constant.ErrTag;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 认知误差链图 产生工具
 */

public class KnowCognitionUtil {

    //默认值
    private static final int default_itemwidth = 420;       //280;
    private static final int default_itemHeight = 315;      //210;
    private static final int default_bracketwidth = 62;     //48;
    private static final int default_itemvspace = 30;       //20;

    private static final int default_paddingLeft = 30;      //20;
    private static final int default_paddingTop = 30;      //20;

    //Item内部
    private static final int default_splitline_distance = 24;//16;
    private static final int default_rectconner = 39;       //26;

    private static final int default_ErrorTipsHeight = 60;       //26;

    //显示知识点字体大小
    private float itemnamefontsize = 0;
    private float itemvaluefontsize = 0;
    private float itemErrorfontsize = 0;

    private String errknow = "你的错误知识点";

    //大括号的宽度
    private int bracketwidth;

    private NinePatch mBbracketUpper;
    private NinePatch mBbracketLower;

    private Bitmap mGhostBitmap;

    private CognitionItem mCognitionItem;           //根对象
    private CognitionItem mCognitionArray[][];      //对象绘制位置分布

    private static final int linewidth = 4;

    private static final int MaxFontCount = 7;
    private static final int MaxFontSize = 48;     //40;

    private boolean bRootRate = false;

    /**
     * 绘制时控制文本绘制的范围
     */
    private Paint mPaint;

    private Context mContext;

    public KnowCognitionUtil(Context context) {
        mContext = context;
        mPaint = new Paint();
        init();
    }

    /**
     * @return false ：低于平均水平，true: 高于平均
     */
    public boolean getRootItemRate() {
        return bRootRate;
    }

    /**
     * 设置知识点认知链数据
     *
     * @param data 数据 json 结构
     */
    public void setKnowData(String data) {

        List<CognitionItem> list = null;
        //解析json
        try {
            //解析数据
//            list = new Gson().fromJson( data, new TypeToken<List<CognitionItem>>() {}.getType());
            list = new ArrayList<>();
            JSONArray jsonArr = new JSONArray(data);
            for (int i = 0; i < jsonArr.length(); i++) {
                CognitionItem citem = new Gson().fromJson(jsonArr.get(i).toString(), new TypeToken<CognitionItem>() {
                }.getType());
                JSONObject citemjson = new JSONObject(jsonArr.get(i).toString());
                //"averageRate":63,"cognitionRate":66,"gap":3,"knowledgeIds":null,"nodeId":"1933","nodeName":"检验并作答","pNodeIds":["1927"],"predecessor":null
                citem.setAverageRate(citemjson.getInt("averageRate"));
                citem.setCognitionRate(citemjson.getInt("cognitionRate"));
                citem.setNodeId(citemjson.getString("nodeId"));
                citem.setGap(citemjson.getInt("gap"));
                citem.setKnowledgeIds(citemjson.getString("knowledgeIds"));
                citem.setNodeName(citemjson.getString("nodeName"));
                citem.setPredecessor(citemjson.getString("predecessor"));

                List<String> pnodeids;
                JSONArray pnodeJarr = citemjson.getJSONArray("pNodeIds");
                pnodeids = new Gson().fromJson(pnodeJarr.toString(), new TypeToken<List<String>>() {
                }.getType());
                citem.setPNodeIds(pnodeids);
                Log.d("dataknow", "list: jobj " + jsonArr.get(i).toString());
                Log.d("dataknow", "list: citem " + citem.toString());
                list.add(citem);
            }

            if (list != null)
                for (CognitionItem item : list) {
                    Log.d("dataknow", "list: item " + item.toString());
                    item.init();            //必要
                }
        } catch (Exception je) {
            AppLog.w(ErrTag.TAG_JSON, "认知误差链数据错误", je);
        }

        if (list == null || list.size() == 0) {
            return;
        }

        Log.d("dataknow", "setKnowData: data " + data);
        Log.d("dataknow", "list: " + list.toString());
        for (CognitionItem c : list) {
            Log.d("dataknow", "list: " + c.toString());
        }
        //恢复对象的 父子关系
        mCognitionItem = list.get(0);         //第一个是根节点
        dealParentChild(mCognitionItem, list);

        //数据解析，排列显示时的位置
        parseData();

        bRootRate = false;
        if (mCognitionItem.getCognitionRate() >= mCognitionItem.getAverageRate()) {
            bRootRate = true;
        }
    }

    /**
     * 判断个人水平是不是小于平均水平
     *
     * @param data 认知误差链数据
     * @return true: 个人 小于平均水平  false: 大于等于
     */
    public static boolean isLowerAverage(String data) {

        List<CognitionItem> cognitionItems = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(data);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                CognitionItem item = new Gson().fromJson(jsonObject.toString(), new TypeToken<CognitionItem>() {
                }.getType());
                cognitionItems.add(item);
                if (i == 0) break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (cognitionItems.size() == 0) {
            return false;
        }
        CognitionItem item = cognitionItems.get(0);         //第一个是根节点
        return item.getCognitionRate() < item.getAverageRate();
    }

    /**
     * 完成绘制任务  返回Bitmap图片
     */
    public Bitmap getKnowBitmap() {
        //计算图片宽高
        int width = getCalWidth() + 2 * default_paddingLeft;
        int height = getCalHeight() + 2 * default_paddingTop;

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

        Canvas canvas = new Canvas(bitmap);
        onDraw(canvas);
        return bitmap;
    }

    protected void onDraw(Canvas canvas) {

        //清屏
        canvas.drawColor(Color.WHITE);

        if (mCognitionArray == null)
            return;

//        for( int i=0; i<mCognitionArray.length; i++ ){
//            AppLog.d("kkkkkk ");
//            for( int j=0; j<mCognitionArray[i].length; j++ ){
//                AppLog.d("         ( i="+i+",j="+j+")"+ (mCognitionArray[i][j]==null?"":mCognitionArray[i][j].getNodeName()) );
//            }
//            AppLog.d("");
//        }

        int countlevel = mCognitionArray[0].length;
        int countRow = mCognitionArray.length;

        //计算字体
        itemnamefontsize = calKnowNameFont(mPaint, default_itemwidth, default_itemHeight);
        itemvaluefontsize = calKnowValeFont(mPaint, default_itemwidth, default_itemHeight, itemnamefontsize);
        itemErrorfontsize = calErrorFont(mPaint, default_itemwidth, default_itemHeight, itemnamefontsize);

        //先绘制左边的对象
        int itemwidth = getItemWidth();
        int itemheight = getItemHeight();
        int itemdis = getItemVDistance();

        //AppLog.d(" measure getWidth = "+ getWidth() + ",,,,getHeight = " + getHeight() );
        //AppLog.d(" measure getWidth = "+ (countlevel * itemwidth + (countlevel-1) * bracketwidth) + ",,,,getHeight = " + (countRow*itemheight + (countRow-1)*default_itemvspace) );

        //计算上下左右 偏移
        int dy = 0;
        int dx = 0;

        for (int row = 0; row < mCognitionArray.length; row++) {
            CognitionItem item = mCognitionArray[row][0];
            if (item != null) {
                Log.d("data", "Item : nodeid " + item.getNodeId() + " nodename" + item.getNodeName());
                drawKnowItem(canvas, mPaint, item, default_paddingLeft, row * (itemheight + itemdis) + default_paddingTop, itemwidth, itemheight);
            }
        }

        //绘制右边子类
        for (int level = 1; level < countlevel; level++) {

            dx = (level) * (itemwidth + getBracketWidth()) + default_paddingLeft;

            int pretop = 0;         //上一个对象绘制的 top位置
            for (int row = 0; row < mCognitionArray.length; row++) {
                CognitionItem item = mCognitionArray[row][level];
                if (item != null) {

                    if (item.getParents().size() == 0) {        //没有父类

                        //绘制框
                        dy = (row) * (itemheight + itemdis) + default_paddingTop;
                        drawKnowItem(canvas, mPaint, item, dx, dy, itemwidth, itemheight);

                    } else if (item.getParents().size() == 1) {  //一个父类   先绘制从父类到子类的箭头

                        //先绘制箭头
                        mPaint.setStrokeWidth(2f);
                        mPaint.setStyle(Paint.Style.STROKE);
                        mPaint.setColor(Color.BLACK);

                        Rect rect = item.getParents().get(0).getRect();

                        int sx = rect.right;
                        int ex = rect.right + bracketwidth;
                        int sy = rect.top + (rect.height() - 4) / 2;
                        canvas.drawLine(sx, sy, ex, sy, mPaint);

                        //绘制框
                        dy = rect.top;

                        if (pretop != 0 && dy < pretop + itemheight + itemdis)        //调整下位置，防止重叠
                            dy = pretop + itemheight + itemdis;

                        drawKnowItem(canvas, mPaint, item, dx, dy, itemwidth, itemheight);

                    } else {      //多个父类，先绘制括好
                        //先绘制括好
                        drawBracket(canvas, mPaint, item, dx - getBracketWidth());

                        //确定位置
                        dy = calChildItemPos(item);

                        if (pretop != 0 && dy < pretop + itemheight + itemdis)        //调整下位置，防止重叠
                            dy = pretop + itemheight + itemdis;
                        drawKnowItem(canvas, mPaint, item, dx, dy, itemwidth, itemheight);
                    }
                }
                pretop = dy;
            }
        }

        //绘制 你的错误知识点
        //if( !bRootRate ){
        Rect rect = mCognitionItem.getRect();

        dx = rect.left;
        dy = rect.top + rect.height();
        int width = rect.width();

        drawErrorValue(canvas, mPaint, errknow, dx, dy, width, 20);
        //}

    }

    //-----------------------------------------------------------------------------
    private void init() {
        Bitmap bmp_9 = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.arrow_upper);
        mBbracketUpper = new NinePatch(bmp_9, bmp_9.getNinePatchChunk(), null);
        bmp_9 = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.arrow_lower);
        mBbracketLower = new NinePatch(bmp_9, bmp_9.getNinePatchChunk(), null);
        bracketwidth = bmp_9.getWidth();
        mGhostBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_ghost);
    }

    /**
     * 通过父类计算子类的位置
     *
     * @return py 显示的位置
     */
    private int calChildItemPos(CognitionItem cognition) {
        int size = cognition.getParents().size();
        if (size == 1) {
            return cognition.getParents().get(0).getRect().top;
        } else if (size > 1) {
            int mintop = -1, maxtop = -1;
            ArrayList<CognitionItem> list = cognition.getParents();

            for (CognitionItem item : list) {

                if (maxtop == -1 || item.getRect().top > maxtop) {
                    maxtop = item.getRect().top;
                }
                if (mintop == -1 || item.getRect().top < mintop) {
                    mintop = item.getRect().top;
                }
            }

            int dt = (maxtop - mintop) / 2;

            return mintop + dt;
        }
        return 0;
    }

    /*
     * 计算整体宽度
     */
    private int getCalWidth() {

        if (mCognitionArray == null)
            return 0;
        int level = mCognitionArray[0].length;
        if (level == 0) {
            return 0;
        }
        return level * default_itemwidth + (level - 1) * default_bracketwidth;
    }

    /*
     * 计算整体高度
     */
    private int getCalHeight() {

        if (mCognitionArray == null)
            return 0;
        int rows = mCognitionArray.length;
        if (rows == 0) {
            return 0;
        }

        int height = rows * default_itemHeight + (rows - 1) * default_itemvspace;
        if (rows == 1)
            height += default_ErrorTipsHeight;
        return height;
    }

    /*
     *  计算一个对象的宽度  高度
     */
    private int getItemWidth() {
        return default_itemwidth;
    }

    private int getItemHeight() {
        return default_itemHeight;
    }

    /*
     * 括弧的宽度
     */
    private int getBracketWidth() {
        return bracketwidth;
    }

    private int getItemVDistance() {
        return default_itemvspace;
    }

    //--------------------------------------------------------------------
    //结构体定义
    class CognitionItem implements Serializable {

        private static final long serialVersionUID = -5043451566637723359L;

        protected int averageRate;

        protected int cognitionRate;

        protected int gap;

        protected String nodeId;

        protected String nodeName;
        private String knowledgeIds;
        private String predecessor;
        private List<String> pNodeIds;

        private Rect rect;     //绘制的区域

        //分析后分配的 行列信息
        private int row = -1;
        private int col = -1;

        private ArrayList<CognitionItem> parents = new ArrayList<>();
        private ArrayList<CognitionItem> childs = new ArrayList<>();

        public CognitionItem(String id, String name, int my, int ov) {
            nodeId = id;
            nodeName = name;
            cognitionRate = my;
            averageRate = ov;
        }

        public void init() {     //Gson不会给默认变量初始化
            row = -1;
            col = -1;
            parents = new ArrayList<>();
            childs = new ArrayList<>();
            rect = new Rect();
        }

        public ArrayList<CognitionItem> getParents() {
            return parents;
        }

        public void addParent(CognitionItem parent) {
            this.parents.add(parent);
        }

        public ArrayList<CognitionItem> getChilds() {
            return childs;
        }

        public void addChild(CognitionItem child) {
            if (childs.size() < 2) {        //只应许两个子类
                childs.add(child);
            }
        }

        public Rect getRect() {
            return rect;
        }

        public void setRect(Rect rect) {
            this.rect.set(rect);
        }

        public void setRect(int left, int top, int right, int bottom) {
            this.rect.set(left, top, right, bottom);
        }

        public int getRow() {
            return row;
        }

        public void setRow(int row) {
            this.row = row;
        }

        public int getCol() {
            return col;
        }

        public void setCol(int col) {
            this.col = col;
        }

        public void setAverageRate(int averageRate) {
            this.averageRate = averageRate;
        }

        public int getAverageRate() {
            return this.averageRate;
        }

        public void setCognitionRate(int cognitionRate) {
            this.cognitionRate = cognitionRate;
        }

        public int getCognitionRate() {
            return this.cognitionRate;
        }

        public void setGap(int gap) {
            this.gap = gap;
        }

        public int getGap() {
            return this.gap;
        }

        public void setNodeId(String nodeId) {
            this.nodeId = nodeId;
        }

        public String getNodeId() {
            return this.nodeId;
        }

        public void setNodeName(String nodeName) {
            this.nodeName = nodeName;
        }

        public String getNodeName() {
            return this.nodeName;
        }

        public void setPNodeIds(List<String> pNodeIds) {
            this.pNodeIds = pNodeIds;
        }

        public String getKnowledgeIds() {
            return knowledgeIds;
        }

        public void setKnowledgeIds(String knowledgeIds) {
            this.knowledgeIds = knowledgeIds;
        }

        public List<String> getPNodeIds() {
            return this.pNodeIds;
        }

        public String getPredecessor() {
            return predecessor;
        }

        public void setPredecessor(String predecessor) {
            this.predecessor = predecessor;
        }

        @Override
        public String toString() {
            return "CognitionItem{" +
                    "averageRate=" + averageRate +
                    ", cognitionRate=" + cognitionRate +
                    ", gap=" + gap +
                    ", nodeId='" + nodeId + '\'' +
                    ", nodeName='" + nodeName + '\'' +
                    ", knowledgeIds='" + knowledgeIds + '\'' +
                    ", predecessor='" + predecessor + '\'' +
                    ", pNodeIds=" + pNodeIds +
                    ", rect=" + rect +
                    ", row=" + row +
                    ", col=" + col +
                    ", parents=" + parents +
                    ", childs=" + childs +
                    '}';
        }
    }

    //查找层级
    private int findLevel(CognitionItem cognition) {
        if (cognition == null) {
            return 0;
        }
        int level = 1;
        int maxlevel = 0;
        for (CognitionItem item : cognition.getParents()) {
            int l = findLevel(item);
            if (l > maxlevel) {
                maxlevel = l;
            }
        }
        level += maxlevel;
        return level;
    }

    /**
     * 整个Item的数量
     *
     * @param cognition 对象
     * @return 整个数量
     */
    private int getCounts(CognitionItem cognition) {
        int count = 1;
        for (CognitionItem item : cognition.getParents()) {
            count += getCounts(item);
        }
        return count;
    }

    private ArrayList<CognitionItem> getItemsInLevel(CognitionItem cognition, int level) {
        ArrayList<CognitionItem> list = new ArrayList<>();

        if (level == 1) {
            if (!list.contains(cognition))
                list.add(cognition);
        } else if (level > 1) {
            for (CognitionItem item : cognition.getParents()) {
                ArrayList<CognitionItem> ll = getItemsInLevel(item, level - 1);
                for (CognitionItem item1 : ll) {
                    if (!list.contains(item1)) {
                        list.add(item1);
                    }
                }
            }
        }
        return list;
    }

    private void parseData() {

        int level = findLevel(mCognitionItem);            //层级
        int count = getCounts(mCognitionItem);             //总个数

        //AppLog.d("ffff level = " + level );
        //AppLog.d("ffff count = " + count );

        if (level == 0 || level > 3) {                      //只支持到第三级
            mCognitionArray = null;
        } else if (level == 1) {
            mCognitionArray = new CognitionItem[1][1];
            setItemCowCol(mCognitionArray, mCognitionItem, 0, 0);
        } else if (level == 2) {
            //分配
            mCognitionArray = new CognitionItem[count - 1][level];

            //根节点，，也即第一级
            setItemCowCol(mCognitionArray, mCognitionItem, 0, level - 1);

            //第二级 的所有item
            ArrayList<CognitionItem> ll = getItemsInLevel(mCognitionItem, level);
            if (ll.size() < count) {
                for (int i = 0; i < ll.size(); i++) {
                    setItemCowCol(mCognitionArray, ll.get(i), i, 0);
                }
            } else {
                //不会出现的情况
            }
        } else {
            //分配
            mCognitionArray = new CognitionItem[count - 1][level];

            //根节点，，也即第一级
            setItemCowCol(mCognitionArray, mCognitionItem, 0, level - 1);

            //优先第二级，然后决定第三级的位置，以及调整第三级的顺序
            ArrayList<CognitionItem> list = mCognitionItem.getParents();
            int used_row = 0;   //已使用的行
            for (CognitionItem item : list) {
                if (item.getParents().size() == 0) {        //没有父类
                    //放置本类，占用一行
                    setItemCowCol(mCognitionArray, item, used_row, level - 2);
                    used_row++;
                } else if (item.getParents().size() == 1) {  //一个父类
                    //放置本类，和 父类共用一行
                    setItemCowCol(mCognitionArray, item, used_row, level - 2);

                    CognitionItem it = item.getParents().get(0);
                    if (it.getRow() < 0)
                        setItemCowCol(mCognitionArray, it, used_row, level - 3);
                    used_row++;
                } else {                              //多个父类

                    AppLog.d("ffff 222 count = " + count);

                    //放置本类
                    setItemCowCol(mCognitionArray, item, used_row, level - 2);

                    //放置父类，占据多行
                    ArrayList<CognitionItem> parents = item.getParents();
                    sortCognitionParent(parents);
                    for (CognitionItem it : parents) {
                        if (it.getRow() < 0)
                            setItemCowCol(mCognitionArray, it, used_row++, level - 3);
                    }
                }
            }
        }

        //对数组进行处理 去掉没有使用的行
        int rows = 0;
        for (int i = 0; i < mCognitionArray.length; i++) {

            //检查一行是否使用了
            boolean finded = false;
            for (int j = 0; j < mCognitionArray[i].length; j++) {
                if (mCognitionArray[i][j] != null) {
                    finded = true;
                    break;
                }
            }

            if (!finded)
                break;
            rows = i + 1;
        }

        if (rows != mCognitionArray.length) {

            CognitionItem tmparray[][] = new CognitionItem[rows][mCognitionArray[0].length];
            for (int row = 0; row < rows; row++) {
                for (int col = 0; col < tmparray[row].length; col++) {
                    tmparray[row][col] = mCognitionArray[row][col];
                }
            }
            mCognitionArray = tmparray;
        }
    }

    private void setItemCowCol(CognitionItem[][] array, CognitionItem item, int row, int col) {
        array[row][col] = item;
        item.setRow(row);
        item.setCol(col);
    }

    /**
     * 调整父类的顺序，如果有多个子类，并且其中一个已安排位置，则放置尾部，否则，放置顶部
     * 只有一个子类的放置中间
     *
     * @param list 列表
     */
    private void sortCognitionParent(ArrayList<CognitionItem> list) {

        ArrayList<CognitionItem> header = new ArrayList<>();
        ArrayList<CognitionItem> middle = new ArrayList<>();
        ArrayList<CognitionItem> tailer = new ArrayList<>();

        for (CognitionItem item : list) {
            if (item.getChilds().size() == 1) {
                middle.add(item);
            } else {
                //判断所有子类是否已安排位置
                boolean hasPosition = true;
                ArrayList<CognitionItem> childs = item.getChilds();
                for (CognitionItem it : childs) {
                    if (it.getRow() < 0) {
                        hasPosition = false;
                        break;
                    }
                }

                if (hasPosition) {  //都安排了，顶部
                    header.add(item);
                } else {
                    tailer.add(item);
                }
            }
        }
        //清除原来顺序
        list.clear();

        //新顺序
        list.addAll(header);
        list.addAll(middle);
        list.addAll(tailer);
    }
    /*
     *绘制一个知识点数据
     * @param canvas
     * @param item
     * @param x         x坐标
     * @param y         x坐标
     * @param w         绘制区域width
     * @param h         绘制区域height
     */
    //

    private void drawKnowItem(Canvas canvas, Paint paint, CognitionItem item, int x, int y, int w, int h) {

        item.setRect(x, y, x + w, y + h);
        //AppLog.d("---xxxxx yyy left = " + x + ",,top="+y+",,,right="+(x+w)+",,,bottom="+(y+h) );

        int color_bg, color_border, color_font;
        if (item.getCognitionRate() < 0) {
            color_bg = mContext.getResources().getColor(R.color.cognition_no_bg);
            color_border = mContext.getResources().getColor(R.color.cognition_no_border);
            color_font = mContext.getResources().getColor(R.color.cognition_no_know);
        } else if (item.getGap() < 0) {
            color_bg = mContext.getResources().getColor(R.color.cognition_small_bg);
            color_border = mContext.getResources().getColor(R.color.cognition_small_border);
            color_font = mContext.getResources().getColor(R.color.cognition_small_know);
        } else {
            color_bg = mContext.getResources().getColor(R.color.cognition_big_bg);
            color_border = mContext.getResources().getColor(R.color.cognition_big_border);
            color_font = mContext.getResources().getColor(R.color.cognition_big_know);
        }

        //绘制覆盖区域背景颜色
        paint.setColor(color_bg);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(linewidth);
        paint.setAntiAlias(true);

        canvas.drawRoundRect(new RectF(x, y, x + w, y + h), default_rectconner, default_rectconner, paint);

        //先绘制外围矩形
        paint.setColor(color_border);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawRoundRect(new RectF(x, y, x + w, y + h), default_rectconner, default_rectconner, paint);

        //绘制知识点名称
        paint.setColor(color_font);
        String name = item.getNodeName();
        Log.d("itemnodename", "drawKnowItem: " + name + "  nodeid     " + item.getNodeId());
        int sx = x + linewidth;
        int sy = y + linewidth;
        int sw = w - linewidth * 2;
        int sh = h / 2 - linewidth * 2;
        drawItemKnow(canvas, paint, name, sx, sy, sw, sh);

        //绘制分割线
        paint.setStrokeWidth(2f);
        canvas.drawLine(x + default_splitline_distance, y + h / 2, x + w - default_splitline_distance, y + h / 2, paint);

        //绘制值
        paint.setAlpha(128);
        String myval = "我的正确率:" + item.getCognitionRate() + "%";
        String ovval = "";
        if (item.getAverageRate() > item.getCognitionRate()) {
            ovval = "低于平均水平" + (item.getAverageRate() - item.getCognitionRate()) + "%";
        } else if (item.getAverageRate() < item.getCognitionRate()) {
            ovval = "超过平均水平" + (item.getCognitionRate() - item.getAverageRate()) + "%";
        } else {
            ovval = "与平均水平持平";
        }

        if (item.getCognitionRate() < 0) {
            myval = "你还没有学过该知识点";
            ovval = "";
        }
        sx = x + linewidth;
        sy = y + h / 2 + linewidth;
        sw = w - linewidth * 2;
        sh = h / 2 - linewidth * 2;
        drawItemValue(canvas, paint, myval, ovval, sx, sy, sw, sh);

        paint.setAlpha(255);
    }

    /**
     * 绘制知识点名称
     *
     * @param canvas   画布
     * @param paint    画笔
     * @param knowname 知识点
     * @param x        绘制开始x
     * @param y        绘制开始y
     * @param w        绘制最大宽度
     * @param h        绘制最大高度
     * @return 字体大小
     */
    private void drawItemKnow(Canvas canvas, Paint paint, String knowname, int x, int y, int w, int h) {

        if (knowname.length() > MaxFontCount * 2) {
            knowname = knowname.substring(0, MaxFontCount * 2 - 3) + "...";
        }

        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(itemnamefontsize);

        Rect rect = new Rect();
        int size = knowname.length();

        if (size > MaxFontCount) {  //分两行显示

            String know1 = knowname.substring(0, MaxFontCount);
            mPaint.getTextBounds(know1, 0, know1.length(), rect);
            int dx = (w - rect.width()) / 2 + x;
            int dy = h / 2 + y - 2;
            canvas.drawText(know1, dx, dy, paint);

            String know2 = knowname.substring(MaxFontCount);
            mPaint.getTextBounds(know2, 0, know2.length(), rect);
            dx = (w - rect.width()) / 2 + x;
            dy = h / 2 + y + rect.height() + 2;
            canvas.drawText(know2, dx, dy, paint);
        } else {
            mPaint.getTextBounds(knowname, 0, knowname.length(), rect);

            int dx = (w - rect.width()) / 2 + x;
            int dy = (h - rect.height()) / 2 + rect.height() + y;
            canvas.drawText(knowname, dx, dy, paint);
        }
    }

    private void drawItemValue(Canvas canvas, Paint paint, String myvalues, String ovvalues, int x, int y, int w, int h) {

        int offy = 10;
        int offx = 10;

        paint.setStyle(Paint.Style.FILL);

        Rect rect = new Rect();
        paint.setTextSize(itemvaluefontsize);

        //分两行显示
        mPaint.getTextBounds(myvalues, 0, myvalues.length(), rect);
        int dx = /*(w - rect.width()) / 2 +*/ x + offx;
        int dy = h / 2 + y - 3 - offy;
        canvas.drawText(myvalues, dx, dy, paint);

        if (!TextUtils.isEmpty(ovvalues)) {
            mPaint.getTextBounds(ovvalues, 0, ovvalues.length(), rect);
            dx = /*(w - rect.width()) / 2 +*/ x + offx;
            dy = h / 2 + y + rect.height() + 3 - offy;
            canvas.drawText(ovvalues, dx, dy, paint);

            if (ovvalues.contains("低于")) {

                //
                dx = x + w - mGhostBitmap.getWidth() - offx;
                dy = y + h - mGhostBitmap.getHeight() - offy;
                canvas.drawBitmap(mGhostBitmap, dx, dy, paint);
            }
        }
    }

    private float calKnowNameFont(Paint paint, int w, int h) {

        float fontsize = MaxFontSize;

        String knowdemo = "知识点名称测试示例时";
        if (MaxFontCount < knowdemo.length()) {
            knowdemo = knowdemo.substring(0, MaxFontCount);
        }

        Rect rect = new Rect();
        while (true) {
            paint.setTextSize(fontsize);
            mPaint.getTextBounds(knowdemo, 0, knowdemo.length(), rect);

            //仅测试一行显示
            if (rect.width() < w * 4 / 5) {
                break;
            }
            fontsize -= 4;      //减少4
        }
        return fontsize;
    }

    private float calKnowValeFont(Paint paint, int w, int h, float fontsize) {

        String knowvalue = "你还没有学过该知识点";

        Rect rect = new Rect();
        while (true) {
            paint.setTextSize(fontsize);
            mPaint.getTextBounds(knowvalue, 0, knowvalue.length(), rect);

            //仅测试一行显示
            if (rect.width() + mGhostBitmap.getWidth() < w) {
                break;
            }
            fontsize -= 4;      //减少4
        }
        return fontsize;
    }

    private float calErrorFont(Paint paint, int w, int h, float fontsize) {

        Rect rect = new Rect();
        while (true) {
            paint.setTextSize(fontsize);
            mPaint.getTextBounds(errknow, 0, errknow.length(), rect);

            //仅测试一行显示
            if (rect.width() + mGhostBitmap.getWidth() < w * 8 / 10) {
                break;
            }
            fontsize -= 4;      //减少4
        }
        return fontsize;
    }

    /**
     * 绘制括好
     */
    private void drawBracket(Canvas canvas, Paint paint, CognitionItem cognition, int startx) {
        int size = cognition.getParents().size();
        if (size > 1) {
            int mintop = -1, maxtop = -1;
            ArrayList<CognitionItem> list = cognition.getParents();

            for (CognitionItem item : list) {
                if (maxtop == -1 || item.getRect().top > maxtop) {
                    maxtop = item.getRect().top;
                }
                if (mintop == -1 || item.getRect().top < mintop) {
                    mintop = item.getRect().top;
                }
            }

            CognitionItem item = cognition.getParents().get(0);
            Rect rect = item.getRect();

            //开始 结束与 一半的位置
            mintop += rect.height() / 2;
            maxtop += rect.height() / 2;

            //AppLog.d("-----11 mintop = " + mintop + ",,,maxtop = " + maxtop + ",,,,rect.height() = " + rect.height() );

            //绘制上半部分
            mBbracketUpper.draw(canvas, new Rect(startx, mintop, startx + bracketwidth, (mintop + maxtop) / 2), paint);

            //绘制下半部分
            mBbracketLower.draw(canvas, new Rect(startx, (mintop + maxtop) / 2, startx + bracketwidth, maxtop), paint);

        }
    }

    private void dealParentChild(CognitionItem item, List<CognitionItem> list) {

        List<String> nodes = item.getPNodeIds();
        if (nodes == null) {
            return;
        }
        for (String id : nodes) {
            CognitionItem item1 = findNode(list, id);
            if (item1 != null) {
                item.addParent(item1);
                item1.addChild(item);
                dealParentChild(item1, list);
            }
        }
    }

    private CognitionItem findNode(List<CognitionItem> list, String id) {
        if (TextUtils.isEmpty(id)) {
            return null;
        }
        for (CognitionItem item : list) {
            if (id.equals(item.getNodeId()))
                return item;
        }
        return null;
    }

    private void drawErrorValue(Canvas canvas, Paint paint, String errorstr, int x, int y, int w, int offy) {

        int color_font = mContext.getResources().getColor(R.color.knowfont_color);

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(color_font);

        Rect rect = new Rect();
        paint.setTextSize(itemErrorfontsize);

        //分两行显示
        mPaint.getTextBounds(errorstr, 0, errorstr.length(), rect);
        int dx = (w - rect.width()) / 2 + x;
        int dy = y + offy + rect.height();
        canvas.drawText(errorstr, dx, dy, paint);
    }
}