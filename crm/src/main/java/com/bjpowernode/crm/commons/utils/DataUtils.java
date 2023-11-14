package com.bjpowernode.crm.commons.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DataUtils {
    /**
     * 对指定的date对象进行格式话:yyyy-MM-dd HH:mm:ss
     * @param date
     * @return
     */
    public static String formateDateTime(Date date){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dataStr=sdf.format(date);
        return dataStr;
    }
    /**
     * 对指定的date对象进行格式话:yyyy-MM-dd
     * @param date
     * @return
     */
    public static String formateDate(Date date){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        String dataStr=sdf.format(date);
        return dataStr;
    }
    /**
     * 对指定的date对象进行格式话:HH:mm:ss
     * @param date
     * @return
     */
    public static String formateTime(Date date){
        SimpleDateFormat sdf=new SimpleDateFormat("HH:mm:ss");
        String dataStr=sdf.format(date);
        return dataStr;
    }
}
