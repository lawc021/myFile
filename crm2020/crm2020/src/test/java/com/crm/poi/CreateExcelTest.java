/**
 * @项目名：crm-project
 * @创建人： Administrator
 * @创建时间： 2020-05-28
 * @公司： www.bjpowernode.com
 * @描述：
 */
package com.crm.poi;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.HorizontalAlignment;

import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * 测试使用apache-poi生成excel文件
 */
public class CreateExcelTest {
    public static void main(String[] args) throws Exception{
        //1.创建HSSFWorkbook对象，对应一个excel文件
        HSSFWorkbook wb=new HSSFWorkbook();
        //2.使用wb创建HSSFSheet对象，对应一个页
        HSSFSheet sheet=wb.createSheet("学生列表");
        //3.使用sheet创建HSSFRow对象，对应一行
        HSSFRow row=sheet.createRow(0);//rownum：行的编号，从0开始，0表示第一行，1表示第二行，....
        //4.使用 row创建三个HSSFCell对象，对应三列
        HSSFCell cell=row.createCell(0);//column：列的编号,从0开始，0表示第一列，1表示第二列，....
        cell.setCellValue("学号");
        cell=row.createCell(1);
        cell.setCellValue("姓名");
        cell=row.createCell(2);
        cell.setCellValue("年龄");

        //创建HSSFCellStyle对象，对应样式
        HSSFCellStyle style=wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);

        //5.使用sheet创建10个HSSFRow对象，对应10行
        for(int i=1;i<=10;i++){
            row=sheet.createRow(i);

            cell=row.createCell(0);//column：列的编号,从0开始，0表示第一列，1表示第二列，....
            cell.setCellStyle(style);
            cell.setCellValue(100+i);
            cell=row.createCell(1);
            cell.setCellValue("NAME"+i);
            cell=row.createCell(2);
            cell.setCellStyle(style);
            cell.setCellValue(20+i);
        }

        //使用wb生成excel文件
        OutputStream os=new FileOutputStream("d:\\student.xls");//文件可以自动创建，目录必须手动创建。
        wb.write(os);
        os.close();
        wb.close();
        System.out.println("==============create excel ok===========");
    }
}
