/**
 * @项目名：crm-project
 * @创建人： Administrator
 * @创建时间： 2020-05-29
 * @公司： www.bjpowernode.com
 * @描述：
 */
package com.crm.poi;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.FileInputStream;
import java.io.InputStream;

/**
 * 使用apache-poi解析excel文件
 */
public class ParseExcelTest {
    public static void main(String[] args) throws  Exception{
        //根据目标文件创建HSSFWorkbook对象，封装了文件中的所有数据
        InputStream is=new FileInputStream("d:\\student.xls");
        HSSFWorkbook wb=new HSSFWorkbook(is);
        //根据wb获取HSSFSheet对象，对应一页的数据
        HSSFSheet sheet=wb.getSheetAt(0);
        //根据sheet获取HSSFRow对象，对应一行的数据
        HSSFRow row=null;
        HSSFCell cell=null;
        for(int i=0;i<=sheet.getLastRowNum();i++){//sheet.getLastRowNum()：获取的是最后一行的编号
            row=sheet.getRow(i);

            //根据row获取HSSFCell对象，对应一列的数据
            for(int j=0;j<row.getLastCellNum();j++){//row.getLastCellNum()：获取的是最后一列的编号+1
                cell=row.getCell(j);
                System.out.print(getCellValue(cell)+" ");
            }

            //一行打完，打印一个换行
            System.out.println();
        }

    }


    public static String getCellValue(HSSFCell cell){
        String ret="";
        /*if(cell.getCellType()==HSSFCell.CELL_TYPE_STRING){
            ret=cell.getStringCellValue();
        }else if(cell.getCellType()==HSSFCell.CELL_TYPE_BOOLEAN){
            ret=cell.getBooleanCellValue()+"";
        }else if(cell.getCellType()==HSSFCell.CELL_TYPE_NUMERIC){
            ret=cell.getNumericCellValue()+"";
        }else if(cell.getCellType()==HSSFCell.CELL_TYPE_FORMULA){
            ret=cell.getCellFormula();
        }else{
            ret="";
        }*/

        switch (cell.getCellType()){
            case HSSFCell.CELL_TYPE_STRING:
                ret=cell.getStringCellValue();
                break;
            case HSSFCell.CELL_TYPE_BOOLEAN:
                ret=cell.getBooleanCellValue()+"";
                break;
            case HSSFCell.CELL_TYPE_NUMERIC:
                ret=cell.getNumericCellValue()+"";
                break;
            case HSSFCell.CELL_TYPE_FORMULA:
                ret=cell.getCellFormula();
                break;
             default:
                    ret="";
        }

        return ret;
    }
}
