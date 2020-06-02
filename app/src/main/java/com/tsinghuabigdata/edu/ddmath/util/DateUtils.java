/*
 * Copyright (c) 2015. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.tsinghuabigdata.edu.ddmath.util;

import android.widget.DatePicker;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


/**
 * <p>
 * Created by yanshen@tsinghuabigdata.com on 2015/12/21.
 * </p>
 *
 * @author yanshen@tsinghuabigdata.com
 * @version V1.0
 */
public class DateUtils {

    public static final String FORMAT_DATA_TIME     = "yyyy-MM-dd HH:mm:ss";
    public static final String FORMAT_DATA_TIME_MIN = "yyyy-MM-dd HH:mm";
    public static final String FORMAT_DATA          = "yyyy-MM-dd";
    public static final String FORMAT_DATA_MD       = "MM-dd";
    public static final String FORMAT_DATA_PRODUCT  = "yyyy.MM.dd";

    public static String getShareText(long createtime) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm生成");
        return dateFormat.format(createtime);
    }
    /**
     * 第一天
     */
    public static Date frist(int year, int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }

    /**
     * 最后一天
     */
    public static Date last(int year, int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month + 1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        return calendar.getTime();
    }

    /**
     * 一天开始
     */
    public static Date dayFrist(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }

    /**
     * 一天结束
     */
    public static Date dayLast(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        return calendar.getTime();
    }

    /**
     * 相差多少天
     */
    public static int diffDay(Date start, Date end) {
        long milliseconds1 = start.getTime();
        long milliseconds2 = end.getTime();
        long diff = milliseconds2 - milliseconds1;
        long diffDays = diff / (24 * 60 * 60 * 1000);
        return (int) diffDays;
    }

    /**
     * 添加多少年
     */
    public static Date addHowYear(Date now, int amount) {
        return addHow(now, Calendar.YEAR, amount);
    }

    /**
     * 添加多少月
     */
    public static Date addHowMonth(Date now, int amount) {
        return addHow(now, Calendar.MONTH, amount);
    }

    /**
     * 添加多少周
     */
    public static Date addHowWeek(Date now, int amount) {
        return addHow(now, Calendar.WEEK_OF_MONTH, amount);
    }

    /**
     * 添加多少天
     */
    public static Date addHowDay(Date now, int amount) {
        return addHow(now, Calendar.DAY_OF_MONTH, amount);
    }

    /**
     * 添加多少小时
     */
    public static Date addHowHour(Date now, int amount) {
        return addHow(now, Calendar.HOUR_OF_DAY, amount);
    }

    /**
     * 添加多少分钟
     */
    public static Date addHowMinute(Date now, int amount) {
        return addHow(now, Calendar.MINUTE, amount);
    }

    /**
     * 在当前时间添加多少数量
     */
    public static Date addHow(int field, int amount) {
        return addHow(new Date(), field, amount);
    }

    public static Date addHow(Date now, int field, int amount) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(field, amount);
        return calendar.getTime();
    }

    public static boolean isDay(Date edate, Date date) {
        int eDay = edate.getDate();
        int day = date.getDate();
        if (isMonth(edate, date) && eDay == day) {
            return true;
        }
        return false;
    }

    public static boolean isMonth(Date edate, Date date) {
        int eYear = edate.getYear();
        int year = date.getYear();
        int eMonth = edate.getMonth();
        int month = date.getMonth();
        if (eYear == year && eMonth == month) {
            return true;
        }
        return false;
    }

    public static String format(Date date, String format) {
        return new SimpleDateFormat(format,Locale.getDefault()).format(date);
    }

    public static String format(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd",Locale.getDefault()).format(date);
    }

//    public static String formatSecond(Date date) {
//        return new SimpleDateFormat(" yyyy-MM-dd HH:mm:ss").format(date);
//    }

    public static String format(long time) {
        return new SimpleDateFormat(FORMAT_DATA,Locale.getDefault()).format(time);
    }

    public static String format(long time, String farmat) {
        return new SimpleDateFormat(farmat,Locale.getDefault()).format(time);
    }

    /**
     * 日期字符串转换为Date
     */
    public static Date parse(String date, String format) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format,Locale.getDefault());
        try {
            return simpleDateFormat.parse(date);
        } catch (ParseException e) {
            AppLog.i("", e);
            return null;
        }
    }

    /**
     * 日期字符串转换为毫秒
     */
    public static long parseMilliseconds(String date, String format) {
        Date dat = parse(date, format);
        if (dat == null) {
            return 0;
        }
        return dat.getTime();
    }

//    /**
//     * 去掉日期字符串中的时间部分
//     *
//     * @param date
//     * @return
//     */
    //    public static String removeTime(String date) {
    //        if (!TextUtils.isEmpty(date) && date.length() > 10) {
    //            return date.substring(0, 10);
    //        }
    //        return date;
    //    }

    /**
     * 毫秒转换成格式为: x小时xx分钟
     *
     * @param microseconds
     */
    public static String ms2hourmin(long microseconds) {
        long seconds = (long) (microseconds / 1000.0);
        int hours = (int) (seconds / 3600.0);
        int mins = (int) ((seconds % 3600) * 1.0 / 60);
        String ret = "";
        if (hours != 0) {
            ret += hours + "小时";
        }
        if (mins != 0) {
            ret += mins + "分钟";
        } else if (seconds != 0 || microseconds != 0) {
            ret += "1分钟";
        }
        return ret;
    }

    public static String getCurrDateStr(String date) {
        long datetime = parseMilliseconds(date, FORMAT_DATA_TIME);
        return getCurrDateStr(datetime);
    }

    public static String getCurrDateStr(long datetime) {
        long currtime = System.currentTimeMillis();
        long diff = currtime - datetime;

        int hours, mins;
        String before = "前";
        hours = (int) (diff / (60 * 60 * 1000));
        mins = (int) (diff / (60 * 1000));
        String temp;

        //1天内
        if (hours <= 24) {
            if (mins >= 60) {
                temp = "" + hours + "小时" + before;
            } else if (mins == 0) {
                temp = "刚刚";
            } else {
                temp = "" + mins + "分钟" + before;
            }
        } else {
            temp = format(datetime);
        }
        return temp;
    }

    /**
     * 格式化时间（输出类似于 刚刚, 4分钟前, 一小时前, 昨天这样的时间）
     *
     * @param time 需要格式化的时间 如"2014-07-14 19:01:45"
     * @return time为null，或者时间格式不匹配，输出空字符""
     */
    public static String formatDisplayTime(String time) {
        String display = "";
        int tMin = 60 * 1000;
        int tHour = 60 * tMin;
        int tDay = 24 * tHour;

        if (time != null) {
            try {
                Date tDate = new SimpleDateFormat(FORMAT_DATA_TIME,Locale.getDefault()).parse(time);
                Date today = new Date();
                SimpleDateFormat thisYearDf = new SimpleDateFormat("yyyy",Locale.getDefault());
                SimpleDateFormat todayDf = new SimpleDateFormat("yyyy-MM-dd",Locale.getDefault());
                Date thisYear = new Date(thisYearDf.parse(thisYearDf.format(today)).getTime());
                Date yesterday = new Date(todayDf.parse(todayDf.format(today)).getTime());
                Date beforeYes = new Date(yesterday.getTime() - tDay);
                if (tDate != null) {
                    SimpleDateFormat halfDf = new SimpleDateFormat("yyyy-MM-dd",Locale.getDefault());
                    long dTime = today.getTime() - tDate.getTime();
                    if (tDate.before(thisYear)) {
                        display = new SimpleDateFormat("yyyy-MM-dd",Locale.getDefault()).format(tDate);
                    } else {

                        if (dTime < tMin) {
                            display = "刚刚";
                        } else if (dTime < tHour) {
                            display = (int) Math.ceil(dTime / tMin) + "分钟前";
                        } else if (dTime < tDay && tDate.after(yesterday)) {
                            display = (int) Math.ceil(dTime / tHour) + "小时前";
                        } else if (tDate.after(beforeYes) && tDate.before(yesterday)) {
                            display = "昨天";
                        } else {
                            display = halfDf.format(tDate);
                        }
                    }
                }
            } catch (Exception e) {
                AppLog.i("", e);
            }
        }

        return display;
    }

    /**
     * 毫秒转日期
     */
    public static String ms2datestr(long time, String format) {
        DateFormat formatter = new SimpleDateFormat(format,Locale.getDefault());
        return formatter.format(time);
    }

    /**
     * 毫秒转带分钟的日期格式字串
     *
     * @param time
     * @return
     */
    public static String ms2datestrAsMin(long time) {
        return ms2datestr(time, FORMAT_DATA_TIME_MIN);
    }

    /**
     * 最近一周前 (7天)
     *
     * @return
     */
    public static String getBeforeWeek() {
        return getBeforeWeek(1);
    }

    public static String getBeforeWeek(int week) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd",Locale.getDefault());
        Date dateNow = new Date();
        Calendar cl = Calendar.getInstance();
        cl.setTime(dateNow);
        //		cl.add(Calendar.DAY_OF_YEAR, -1);	//一天
        cl.add(Calendar.WEEK_OF_YEAR, -1 * week);    //一周
        //		cl.add(Calendar.MONTH, -1);			//一个月
        return sdf.format(cl.getTime()) + " 00:00:00";
    }

    /**
     * 最近几月前
     */
    public static String getBeforeMonth(int moth) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd",Locale.getDefault());
        Date dateNow = new Date();
        Calendar cl = Calendar.getInstance();
        cl.setTime(dateNow);
        cl.add(Calendar.MONTH, -1 * moth);    //moth个月
        return sdf.format(cl.getTime()) + " 00:00:00";
    }

    public static String getEndTime(int indexPeriod) {
        String startTime;
        switch (indexPeriod) {
            case 0:
                startTime = getBeforeWeek();
                break;
            case 1:
                startTime = getBeforeMonth(1);
                break;
            case 2:
                startTime = getBeforeMonth(2);
                break;
            default:
                startTime = "1970-01-01 00:00:00";
                break;
        }
        return startTime;
    }

    /**
     * 获取首页时间格式
     */
    public static String getHomeTime(long second) {
        //        SimpleDateFormat sdf = new SimpleDateFormat("mm分ss秒");
        //        return sdf.format(millis);
        String time;
        if (second < 60) {
            time = second + "秒";
        } else {
            long m = second / 60;
            long s = second % 60;
            time = m + "分" + s + "秒";
        }
        return time;
    }

    /**
     * 根据日期获得星期
     *
     * @param date
     * @return
     */
    public static String getWeekOfDate(Date date) {
        String[] weekDaysName = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};
        //String[] weekDaysCode = { "0", "1", "2", "3", "4", "5", "6" };
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int intWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        return weekDaysName[intWeek];
    }

    /**
     * 获得今天的格式 2017-02-18
     */
    public static String getToday() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd",Locale.getDefault());
        return format.format(new Date());
    }

    /**
     * 获得时间的格式 19:36
     *
     * @param applyTime
     */
    public static String getTutorTime(String applyTime) {
        //        2017/03/16 16:06:45
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss",Locale.getDefault());
        Long lo;
        try {
            lo = sdf.parse(applyTime).getTime();
        } catch (Exception e) {
            AppLog.i("", e);
            return null;
        }
        SimpleDateFormat sf = new SimpleDateFormat("HH:mm",Locale.getDefault());
        return sf.format(lo);

        /*String  opentime="2014-05-29 21:30:00";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//小写的mm表示的是分钟

        Long lo = sdf.parse(opentime).getTime();
        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
        sf.format(lo);*/
    }


    public static String timeCalculate(int recLen) {
        String m;
        String s;
        int minute = recLen / 60;
        if (minute < 10) {
            m = "0" + minute;
        } else {
            m = String.valueOf(minute);
        }
        int second = recLen % 60;
        if (second < 10) {
            s = "0" + second;
        } else {
            s = String.valueOf(second);
        }
        return m + ":" + s;
    }

    public static String getMinute(long applyTime) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm",Locale.getDefault());
        return format.format(applyTime);
    }

    public static String getDay(long time) {
        SimpleDateFormat format = new SimpleDateFormat("M月d日",Locale.getDefault());
        return format.format(time);
    }

    public static String getSingleDay(long time) {
        SimpleDateFormat format = new SimpleDateFormat("d日",Locale.getDefault());
        return format.format(time);
    }

    /**
     * 判断是否为今天(效率比较高)
     *
     * @param time
     * @return true今天 false不是
     * @throws ParseException
     */
    public static boolean IsToday(long time) {

        Calendar pre = Calendar.getInstance();
        Date predate = new Date(System.currentTimeMillis());
        pre.setTime(predate);
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(time));
        if (cal.get(Calendar.YEAR) == (pre.get(Calendar.YEAR))) {
            int diffDay = cal.get(Calendar.DAY_OF_YEAR)
                    - pre.get(Calendar.DAY_OF_YEAR);

            if (diffDay == 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否为今天(效率比较高)
     *
     * @param datePicker sdd
     * @return true今天 false不是
     * @throws ParseException
     */
    public static boolean IsToday(DatePicker datePicker) {
        Calendar pre = Calendar.getInstance();
        Date predate = new Date(System.currentTimeMillis());
        pre.setTime(predate);
        if (datePicker.getYear() == (pre.get(Calendar.YEAR)) && datePicker.getMonth() == (pre.get(Calendar.MONTH))
                && datePicker.getDayOfMonth() == (pre.get(Calendar.DAY_OF_MONTH))) {
            return true;
        }
        return false;
    }

//
//    public static SimpleDateFormat getDateFormat() {
//        if (null == DateLocal.get()) {
//            DateLocal.set(new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA));
//        }
//        return DateLocal.get();
//    }

//    //获得当天0点时间
//    public static long getTimesmorning() {
//        Calendar cal = Calendar.getInstance();
//        cal.set(Calendar.HOUR_OF_DAY, 0);
//        cal.set(Calendar.SECOND, 0);
//        cal.set(Calendar.MINUTE, 0);
//        cal.set(Calendar.MILLISECOND, 0);
//        return cal.getTimeInMillis();
//    }

    //获得当天0点时间
    public static long getYesterdayLast() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis() - 1;
    }

    //获得昨天时间
    public static long getYesterday() {
        Calendar cal = Calendar.getInstance();
        int day = cal.get(Calendar.DATE);
        cal.set(Calendar.DATE, day - 1);
        return cal.getTimeInMillis();
    }

    //获得昨天时间
    public static long getRecentDay(int beforeday) {
        Calendar cal = Calendar.getInstance();
        int day = cal.get(Calendar.DATE);
        cal.set(Calendar.DATE, day - beforeday);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    //获得最近一周的时间
    public static long getTimesRecentWeek() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.add(Calendar.DAY_OF_YEAR, -7);
        return cal.getTimeInMillis();
    }

    //获得最近两周的时间
    public static long getTimesTwoWeek() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.add(Calendar.DAY_OF_YEAR, -14);
        return cal.getTimeInMillis();
    }

    //获得最近一月的时间
    public static long getTimesRecentMonth() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.add(Calendar.MONTH, -1);
        return cal.getTimeInMillis();
    }

    //获得最近两月的时间
    public static long getTimesTwoMonth() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.add(Calendar.MONTH, -2);
        return cal.getTimeInMillis();
    }

    //获得最近六月的时间
    public static long getTimesSixMonth() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.add(Calendar.MONTH, -6);
        return cal.getTimeInMillis();
    }

    //获得自定义的开始时间——用于错题本
    public static long getmCustomTime(DatePicker datePicker) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, datePicker.getYear());
        cal.set(Calendar.MONTH, datePicker.getMonth());
        cal.set(Calendar.DAY_OF_MONTH, datePicker.getDayOfMonth());
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    //获得自定义的结束时间——用于错题本
    public static long getmCustomEndTime(DatePicker datePicker) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, datePicker.getYear());
        cal.set(Calendar.MONTH, datePicker.getMonth());
        cal.set(Calendar.DAY_OF_MONTH, datePicker.getDayOfMonth());
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.add(Calendar.DAY_OF_MONTH, 1);
        cal.add(Calendar.MILLISECOND, -1);
        return cal.getTimeInMillis();
    }

    /**
     *计算距未来某个时间相隔天数
     */
    public static int calculateNumberOfDays(long time){

        long now = System.currentTimeMillis();
        if(time > now){
            long diffTime = time-now;
            return  1 + (int) (diffTime/(3600*1000*24));
        }else{
            return -1;
        }
    }

    private static ThreadLocal<SimpleDateFormat> DateLocal = new ThreadLocal<>();

    public static boolean compare(DatePicker start, DatePicker end) {
        //        if (start.getYear() > end.getYear() || start.getMonth() > end.getMonth() || start.getMonth() > end.getMonth())
        Calendar cal1 = Calendar.getInstance();
        cal1.set(Calendar.DAY_OF_MONTH, start.getDayOfMonth());
        cal1.set(Calendar.MONTH, start.getMonth());
        cal1.set(Calendar.YEAR, start.getYear());
        Calendar cal2 = Calendar.getInstance();
        cal2.set(Calendar.DAY_OF_MONTH, end.getDayOfMonth());
        cal2.set(Calendar.MONTH, end.getMonth());
        cal2.set(Calendar.YEAR, end.getYear());
        return cal1.getTimeInMillis() <= cal2.getTimeInMillis();
    }

    public static String getEndDate( long date ){
        SimpleDateFormat format = new SimpleDateFormat("MM月dd日 HH:mm前提交",Locale.getDefault());
        return format.format(date);
    }
}
