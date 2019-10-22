package com.xxx.utils;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.util.CellRangeAddress;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
* @Description:批量导出的Util
* @Author: hanchao
* @Date: 2017/12/6 0006
*/
public class ExportExcelUtil {
    //显示的导出表的标题
    private String title;
    //导出表的列名
    private String[] rowName ;

    private List<Object[]> dataList = new ArrayList<Object[]>();

    private Map<String,Object> bigTilte;

    HttpServletResponse response;


    //构造方法，传入要导出的数据
    public ExportExcelUtil(String title, String[] rowName, List<Object[]> dataList, Map<String,Object> bigTilte){
        this.dataList = dataList;
        this.rowName = rowName;
        this.title = title;
        this.bigTilte = bigTilte;
    }


    /**
     * 导出Excel
     */
    public static void exportExcel(HttpServletRequest request, HttpServletResponse response, String title, String[] headers, List<Object[]> dataList,Map<String,Object> bigTilte) {
        //使用流将数据导出
        try {
            //防止中文乱码
            String headStr = "attachment; filename=\"" + new String(title.getBytes("UTF-8"), "ISO-8859-1");
            response.setContentType("octets/stream");
            response.setContentType("APPLICATION/OCTET-STREAM");
            response.setHeader("Content-Disposition", headStr);
            OutputStream out = response.getOutputStream();
            ExportExcelUtil ex = new ExportExcelUtil(title, headers, dataList,bigTilte);//没有标题
            if(bigTilte!=null){
                ex.exports(out);
            }else {
                ex.export(out);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * 导出数据
     * */
    public void export(OutputStream out) throws Exception{
        try{
            HSSFWorkbook workbook = new HSSFWorkbook();                     // 创建工作簿对象
            HSSFSheet sheet = workbook.createSheet(title);                  // 创建工作表
           // HSSFPatriarch patriarch = sheet.createDrawingPatriarch();     //插入图片
            // 产生表格标题行
                  //   HSSFRow rowm = sheet.createRow(4);
            //          HSSFCell cellTiltle = rowm.createCell(0);

            //sheet样式定义【getColumnTopStyle()/getStyle()均为自定义方法 - 在下面  - 可扩展】
            HSSFCellStyle columnTopStyle = this.getColumnTopStyle(workbook);//获取列头样式对象
            HSSFCellStyle style = this.getStyle(workbook);                  //单元格样式对象

            //          sheet.addMergedRegion(new CellRangeAddress(0, 1, 0, (rowName.length-1)));//合并单元格
            //          cellTiltle.setCellStyle(columnTopStyle);
            //          cellTiltle.setCellValue(title);

            HSSFRow row = null; //new add
            HSSFCell cell = null;   //new add

            //new add
            int index=0;
            if(this.bigTilte!=null) {
                short num = new Short((String)this.bigTilte.get("num"));

                for (index = 0; index < 4; ++index) {
                    row = sheet.createRow(index);
                    row.setHeight((short) 300);
                    cell = row.createCell(0);
                    cell.setCellType(1);
                    switch (index) {
                        case 0:
                            cell.setCellValue(new HSSFRichTextString((String) this.bigTilte.get("reportType")));
                            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, num));
                            break;
                        case 1:
                            cell.setCellValue(new HSSFRichTextString("品牌:" + (String) this.bigTilte.get("brandName")));
                            sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, num));
                            break;
                        case 2:
                            cell.setCellValue(new HSSFRichTextString("店铺:" + (String) this.bigTilte.get("shops")));
                            sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, num));
                            break;
                        case 3:
                            cell.setCellValue(new HSSFRichTextString("统计时间：" + (String) this.bigTilte.get("beginDate") + "至" + (String) this.bigTilte.get("endDate")));
                            sheet.addMergedRegion(new CellRangeAddress(3, 3, 0, num));
                    }
                }
            }

            // 定义所需列数
            int columnNum = rowName.length;
            //HSSFRow row2 = sheet.createRow(0);                // 在索引2的位置创建行(最顶端的行开始的第二行)
            row = sheet.createRow(4); //new add
            // 将列头设置到sheet的单元格中
            for(int n=0;n<columnNum;n++){
                HSSFCell cell2 = row.createCell(n);               //创建列头对应个数的单元格
                cell2.setCellType(HSSFCell.CELL_TYPE_STRING);             //设置列头单元格的数据类型
                HSSFRichTextString text = new HSSFRichTextString(rowName[n]);
                cell2.setCellValue(text);                                 //设置列头单元格的值
                cell2.setCellStyle(columnTopStyle);//设置列头单元格样式
                //sheet.setColumnWidth(n, Integer.parseInt(headers[n][1]) * 256); //new add 宽度
            }


            //将查询出的数据设置到sheet对应的单元格中
            for(int i=0;i<dataList.size();i++){

                Object[] obj = dataList.get(i);//遍历每个对象
                //HSSFRow row = sheet.createRow(i+1);//创建所需的行数（从第二行开始写数据）
                if(index!=0){
                    row = sheet.createRow(i+index+1);
                }else {
                    row = sheet.createRow(i+1);
                }

                for(int j=0; j<obj.length; j++){
                  //  HSSFCell  cell = null;   //设置单元格的数据类型
                    if(j == 0){
                        cell = row.createCell(j,HSSFCell.CELL_TYPE_NUMERIC);
                        cell.setCellValue(i+1);
                    }else{
                        cell = row.createCell(j,HSSFCell.CELL_TYPE_STRING);
                        if(!"".equals(obj[j]) && obj[j] != null){
                            cell.setCellValue(obj[j].toString());                       //设置单元格的值
                        }
                    }
                    cell.setCellStyle(style);                                   //设置单元格样式
                }
            }
            //让列宽随着导出的列长自动适应
            for (int colNum = 0; colNum < columnNum; colNum++) {
                int columnWidth = sheet.getColumnWidth(colNum) / 256;
                for (int rowNum = 0; rowNum < sheet.getLastRowNum(); rowNum++) {
                    HSSFRow currentRow;
                    //当前行未被使用过
                    if (sheet.getRow(rowNum) == null) {
                        currentRow = sheet.createRow(rowNum);
                    } else {
                        currentRow = sheet.getRow(rowNum);
                    }
//                                     if (currentRow.getCell(colNum) != null) {
//                                         HSSFCell currentCell = currentRow.getCell(colNum);
//                                          if (currentCell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
//                                              int length =     currentCell.getStringCellValue().getBytes().length;
//                                              if (columnWidth < length) {
//                                                  columnWidth = length;
//                                              }
//                                          }
//                                      }
                    if (currentRow.getCell(colNum) != null) {
                        HSSFCell currentCell = currentRow.getCell(colNum);
                        if (currentCell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
                            int length = 0;
                            try {
                                length = currentCell.getStringCellValue().getBytes().length;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            if (columnWidth < length) {
                                columnWidth = length;
                            }
                        }
                    }

                }
                if(colNum == 0){
                    sheet.setColumnWidth(colNum, (columnWidth-2) * 256);
                }else{
                    sheet.setColumnWidth(colNum, (columnWidth+4) * 256);
                }
            }
            if(workbook !=null){
                try{
                    workbook.write(out);
                }catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        finally{
            out.close();
        }
    }

    /*
     * 导出多sheet数据
     * */
    public void exports(OutputStream out) throws Exception{
        try{
            HSSFWorkbook workbook = new HSSFWorkbook();                     // 创建工作簿对象
            HSSFSheet sheet = workbook.createSheet(title);                  // 创建工作表
            // HSSFPatriarch patriarch = sheet.createDrawingPatriarch();     //插入图片
            // 产生表格标题行
            //   HSSFRow rowm = sheet.createRow(4);
            //          HSSFCell cellTiltle = rowm.createCell(0);

            //sheet样式定义【getColumnTopStyle()/getStyle()均为自定义方法 - 在下面  - 可扩展】
            HSSFCellStyle columnTopStyle = this.getColumnTopStyle(workbook);//获取列头样式对象
            HSSFCellStyle style = this.getStyle(workbook);                  //单元格样式对象

            //          sheet.addMergedRegion(new CellRangeAddress(0, 1, 0, (rowName.length-1)));//合并单元格
            //          cellTiltle.setCellStyle(columnTopStyle);
            //          cellTiltle.setCellValue(title);

            HSSFRow row = null; //new add
            HSSFCell cell = null;   //new add

            //new add
            int index=0;
            if(this.bigTilte!=null) {
                short num = new Short((String)this.bigTilte.get("num"));

                for (index = 0; index < 4; ++index) {
                    row = sheet.createRow(index);
                    row.setHeight((short) 300);
                    cell = row.createCell(0);
                    cell.setCellType(1);
                    switch (index) {
                        case 0:
                            cell.setCellValue(new HSSFRichTextString((String) this.bigTilte.get("reportType")));
                            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, num));
                            break;
                        case 1:
                            cell.setCellValue(new HSSFRichTextString("品牌:" + (String) this.bigTilte.get("brandName")));
                            sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, num));
                            break;
                        case 2:
                            cell.setCellValue(new HSSFRichTextString("店铺:" + (String) this.bigTilte.get("shops")));
                            sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, num));
                            break;
                        case 3:
                            cell.setCellValue(new HSSFRichTextString("统计时间：" + (String) this.bigTilte.get("beginDate") + "至" + (String) this.bigTilte.get("endDate")));
                            sheet.addMergedRegion(new CellRangeAddress(3, 3, 0, num));
                    }
                }
            }

            // 定义所需列数
            int columnNum = rowName.length;
            //HSSFRow row2 = sheet.createRow(0);                // 在索引2的位置创建行(最顶端的行开始的第二行)
            row = sheet.createRow(4); //new add
            // 将列头设置到sheet的单元格中
            for(int n=0;n<columnNum;n++){
                HSSFCell cell2 = row.createCell(n);               //创建列头对应个数的单元格
                cell2.setCellType(HSSFCell.CELL_TYPE_STRING);             //设置列头单元格的数据类型
                HSSFRichTextString text = new HSSFRichTextString(rowName[n]);
                cell2.setCellValue(text);                                 //设置列头单元格的值
                cell2.setCellStyle(columnTopStyle);//设置列头单元格样式
                //sheet.setColumnWidth(n, Integer.parseInt(headers[n][1]) * 256); //new add 宽度
            }


            //将查询出的数据设置到sheet对应的单元格中
            for(int i=0;i<dataList.size();i++){

                Object[] obj = dataList.get(i);//遍历每个对象
                //HSSFRow row = sheet.createRow(i+1);//创建所需的行数（从第二行开始写数据）
                if(index!=0){
                    row = sheet.createRow(i+index+1);
                }else {
                    row = sheet.createRow(i+1);
                }

                for(int j=0; j<obj.length; j++){
                    //  HSSFCell  cell = null;   //设置单元格的数据类型
                    if(j == 0){
                        cell = row.createCell(j,HSSFCell.CELL_TYPE_NUMERIC);
                        cell.setCellValue(i+1);
                    }else{
                        cell = row.createCell(j,HSSFCell.CELL_TYPE_STRING);
                        if(!"".equals(obj[j]) && obj[j] != null){
                            cell.setCellValue(obj[j].toString());                       //设置单元格的值
                        }
                    }
                    cell.setCellStyle(style);                                   //设置单元格样式
                }
            }
            //让列宽随着导出的列长自动适应
            for (int colNum = 0; colNum < columnNum; colNum++) {
                int columnWidth = sheet.getColumnWidth(colNum) / 256;
                for (int rowNum = 0; rowNum < sheet.getLastRowNum(); rowNum++) {
                    HSSFRow currentRow;
                    //当前行未被使用过
                    if (sheet.getRow(rowNum) == null) {
                        currentRow = sheet.createRow(rowNum);
                    } else {
                        currentRow = sheet.getRow(rowNum);
                    }
//                                     if (currentRow.getCell(colNum) != null) {
//                                         HSSFCell currentCell = currentRow.getCell(colNum);
//                                          if (currentCell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
//                                              int length =     currentCell.getStringCellValue().getBytes().length;
//                                              if (columnWidth < length) {
//                                                  columnWidth = length;
//                                              }
//                                          }
//                                      }
                    if (currentRow.getCell(colNum) != null) {
                        HSSFCell currentCell = currentRow.getCell(colNum);
                        if (currentCell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
                            int length = 0;
                            try {
                                length = currentCell.getStringCellValue().getBytes().length;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            if (columnWidth < length) {
                                columnWidth = length;
                            }
                        }
                    }

                }
                if(colNum == 0){
                    sheet.setColumnWidth(colNum, (columnWidth-2) * 256);
                }else{
                    sheet.setColumnWidth(colNum, (columnWidth+4) * 256);
                }
            }
            if(workbook !=null){
                try{
                    workbook.write(out);
                }catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        finally{
            out.close();
        }
    }

    /**
     * @Title: exportExcel
     * @Description: 导出Excel的方法
     * @author: disvenk.dai @ 2014-01-09
     * @param workbook
     * @param sheetNum (sheet的位置，0表示第一个表格中的第一个sheet)
     * @param sheetTitle  （sheet的名称）
     * @param headers    （表格的标题）
     * @param result   （表格的数据）
     * @param out  （输出流）
     * @throws Exception
     */
    public void exportExcel(HSSFWorkbook workbook, int sheetNum,
                            String sheetTitle, String[] headers, List<List<String>> result,
                            OutputStream out) throws Exception {
        // 生成一个表格
        HSSFSheet sheet = workbook.createSheet();
        /*workbook.setSheetName(sheetNum, sheetTitle,
                HSSFWorkbook.ENCODING_UTF_16);*/
        workbook.setSheetName(sheetNum, sheetTitle);
        // 设置表格默认列宽度为20个字节
        sheet.setDefaultColumnWidth((short) 20);
        // 生成一个样式
        HSSFCellStyle style = workbook.createCellStyle();
        // 设置这些样式
        style.setFillForegroundColor(HSSFColor.PALE_BLUE.index);
        style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        style.setBorderRight(HSSFCellStyle.BORDER_THIN);
        style.setBorderTop(HSSFCellStyle.BORDER_THIN);
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        // 生成一个字体
        HSSFFont font = workbook.createFont();
        font.setColor(HSSFColor.BLACK.index);
        font.setFontHeightInPoints((short) 12);
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        // 把字体应用到当前的样式
        style.setFont(font);

        // 指定当单元格内容显示不下时自动换行
        style.setWrapText(true);

        // 产生表格标题行
        HSSFRow row = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            HSSFCell cell = row.createCell((short) i);

            cell.setCellStyle(style);
            HSSFRichTextString text = new HSSFRichTextString(headers[i]);
            cell.setCellValue(text.toString());
        }
        // 遍历集合数据，产生数据行
        if (result != null) {
            int index = 1;
            for (List<String> m : result) {
                row = sheet.createRow(index);
                int cellIndex = 0;
                for (String str : m) {
                    HSSFCell cell = row.createCell((short) cellIndex);
                    cell.setCellValue(str.toString());
                    cellIndex++;
                }
                index++;
            }
        }
    }

    /*
     * 列头单元格样式
     */
    public HSSFCellStyle getColumnTopStyle(HSSFWorkbook workbook) {

        // 设置字体
        HSSFFont font = workbook.createFont();
        //设置字体大小
        font.setFontHeightInPoints((short)11);
        //字体加粗
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        //设置字体名字
        //font.setFontName("Courier New");
        font.setFontName("微软雅黑");
        //设置样式;
        HSSFCellStyle style = workbook.createCellStyle();
        //设置底边框;
        style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        //设置底边框颜色;
        style.setBottomBorderColor(HSSFColor.BLACK.index);
        //设置左边框;
        style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        //设置左边框颜色;
        style.setLeftBorderColor(HSSFColor.BLACK.index);
        //设置右边框;
        style.setBorderRight(HSSFCellStyle.BORDER_THIN);
        //设置右边框颜色;
        style.setRightBorderColor(HSSFColor.BLACK.index);
        //设置顶边框;
        style.setBorderTop(HSSFCellStyle.BORDER_THIN);
        //设置顶边框颜色;
        style.setTopBorderColor(HSSFColor.BLACK.index);
        //在样式用应用设置的字体;
        style.setFont(font);
        //设置自动换行;
        style.setWrapText(false);
        //设置水平对齐的样式为居中对齐;
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        //设置垂直对齐的样式为居中对齐;
        style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);

        return style;

    }

    /*
     * 列数据信息单元格样式
     */
    public HSSFCellStyle getStyle(HSSFWorkbook workbook) {
        // 设置字体
        HSSFFont font = workbook.createFont();
        //设置字体大小
        //font.setFontHeightInPoints((short)10);
        //字体加粗
        //font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        //设置字体名字
        //font.setFontName("Courier New");
        //font.setColor(HSSFColor.BLACK.index);
        font.setFontName("微软雅黑");
        //设置样式;
        HSSFCellStyle style = workbook.createCellStyle();

        // 指定当单元格内容显示不下时自动换行
       // style.setWrapText(true);

        //style.setFillForegroundColor(HSSFColor.PALE_BLUE.index);

        //style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        //设置底边框;
        style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        //设置底边框颜色;
        style.setBottomBorderColor(HSSFColor.BLACK.index);
        //设置左边框;
        style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        //设置左边框颜色;
        style.setLeftBorderColor(HSSFColor.BLACK.index);
        //设置右边框;
        style.setBorderRight(HSSFCellStyle.BORDER_THIN);
        //设置右边框颜色;
        style.setRightBorderColor(HSSFColor.BLACK.index);
        //设置顶边框;
        style.setBorderTop(HSSFCellStyle.BORDER_THIN);
        //设置顶边框颜色;
        style.setTopBorderColor(HSSFColor.BLACK.index);
        //在样式用应用设置的字体;
        style.setFont(font);
        //设置自动换行;
        style.setWrapText(false);
        //设置水平对齐的样式为居中对齐;
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        //设置垂直对齐的样式为居中对齐;
        style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);

        return style;

    }
}
