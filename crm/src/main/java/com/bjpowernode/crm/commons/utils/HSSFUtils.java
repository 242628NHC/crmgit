package com.bjpowernode.crm.commons.utils;

import org.apache.poi.hssf.usermodel.HSSFCell;

/**
 * 关于excel文件的工具类
 */
public class HSSFUtils {
    /**
     * 从指定的HSSFCell对象中获取列的值,返回的值全部是字符串
     * @param cell HSSFCell对象
     * @return
     */
    public static String getCellValueForStr(HSSFCell cell){
        String ret="";
        //获取列的数据
        if (cell.getCellType()==HSSFCell.CELL_TYPE_STRING){
            ret=cell.getStringCellValue()+"";
        }else if (cell.getCellType()==HSSFCell.CELL_TYPE_NUMERIC){
            ret=cell.getNumericCellValue()+"";
        }else if (cell.getCellType()==HSSFCell.CELL_TYPE_BOOLEAN){
            ret=cell.getBooleanCellValue()+"";
        }else if (cell.getCellType()==HSSFCell.CELL_TYPE_FORMULA){
            ret=cell.getCellFormula();
        }else if (cell.getCellType()==HSSFCell.CELL_TYPE_BLANK){
            ret="";
        }
        return ret;
    }
}
