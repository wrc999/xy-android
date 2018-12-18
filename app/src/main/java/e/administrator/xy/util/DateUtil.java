package e.administrator.xy.util;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2018/8/13.
 */

public class DateUtil {

    public static String getCurrentTime()throws Exception{
        Date date=new Date();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);
    }
    public static String getCurrentDate()throws Exception{
        Date date=new Date();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);
    }
    public static String getSelectTime(Date date)throws Exception{
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);
    }
    public static String getSelectDotTime(Date date)throws Exception{
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy.MM.dd");
        return sdf.format(date);
    }
    public static String getMinute(Date date)throws Exception{
        SimpleDateFormat sdf=new SimpleDateFormat("mm");
        return sdf.format(date);
    }
    public static String getSelectDateTime(Date date)throws Exception{
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);
    }

    public static Boolean judgeTime(String date)throws Exception{
        Date dates = new Date();
        String time = DateUtil.getSelectTime(dates);
        int nowYear = Integer.parseInt(time.substring(0,4));
        int nowMonth = Integer.parseInt(time.substring(5,7));
        int nowDay = Integer.parseInt(time.substring(8,10));
//        String[] str = date.split("\\.");
        String[] str = date.split("-");
        int year = Integer.parseInt(str[0]);
        int month = Integer.parseInt(str[1]);
        int day = Integer.parseInt(str[2]);
        if (nowYear<year){return true;}
        if (nowYear==year && nowMonth<month){return true;}
        if (nowYear==year && nowMonth==month && nowDay<day){return true;}
        return false;
    }
}
